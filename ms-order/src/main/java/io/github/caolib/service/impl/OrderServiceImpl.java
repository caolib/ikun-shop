package io.github.caolib.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.caolib.OrderStatus;
import io.github.caolib.client.CartClient;
import io.github.caolib.client.CommodityClient;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.CommodityDTO;
import io.github.caolib.domain.dto.OrderDetailDTO;
import io.github.caolib.domain.dto.OrderFormDTO;
import io.github.caolib.domain.po.Order;
import io.github.caolib.domain.po.OrderDetail;
import io.github.caolib.domain.vo.OrderVO2;
import io.github.caolib.enums.Q;
import io.github.caolib.enums.Time;
import io.github.caolib.exception.BadRequestException;
import io.github.caolib.mapper.OrderMapper;
import io.github.caolib.service.IOrderDetailService;
import io.github.caolib.service.IOrderService;
import io.github.caolib.utils.BeanUtils;
import io.github.caolib.utils.CollUtils;
import io.github.caolib.utils.UserContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    private final CommodityClient commodityClient;
    private final CartClient cartClient;
    private final IOrderDetailService detailService;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @GlobalTransactional
    public Long createOrder(OrderFormDTO orderFormDTO) {
        // 订单
        Order order = new Order();
        // 查询商品
        List<OrderDetailDTO> detailDTOS = orderFormDTO.getDetails();
        // 获取商品id和数量的映射Map
        Map<Long, Integer> itemNumMap = detailDTOS.stream().collect(Collectors.toMap(OrderDetailDTO::getItemId, OrderDetailDTO::getNum));
        Set<Long> itemIds = itemNumMap.keySet();
        // 查询商品
        List<CommodityDTO> items = commodityClient.queryCommodityByIds(itemIds);
        if (items == null || items.size() < itemIds.size()) {
            throw new BadRequestException("商品不存在");
        }
        // 计算商品总价 单价x数量
        int total = 0;
        for (CommodityDTO item : items) {
            total += item.getPrice() * itemNumMap.get(item.getId());
        }
        order.setTotalFee(total);
        // 设置订单其它属性
        order.setPaymentType(orderFormDTO.getPaymentType());
        order.setUserId(UserContext.getUserId());
        order.setStatus(1);
        // 将订单写入order表中
        save(order);

        // 将订单详情写入order_detail表中
        List<OrderDetail> details = buildDetails(order.getId(), items, itemNumMap);
        detailService.saveBatch(details);

        // RPC -> 扣减库存
        R<Void> r = commodityClient.deductStock(detailDTOS);
        if (r.getCode() != 200) {
            log.error(r.toString());
            throw new BadRequestException("扣减库存失败");
        }
        // RPC -> 清理购物车商品
        cartClient.deleteCartItemByIds(itemIds);

        // MQ -> 发送延迟[订单状态检测]消息

        return order.getId();
    }

    /**
     * 标记订单为支付成功
     *
     * @param orderId 订单id
     */
    @Override
    public void markOrderPaySuccess(Long orderId) {
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.SUCCESS.getCode());
        order.setPayTime(LocalDateTime.now());
        updateById(order);
    }

    /**
     * 获取用户所有订单详情
     */
    @Override
    public R<List<OrderDetail>> getUserOrderDetails() {
        Long userId = UserContext.getUserId();

        // 获取用户所有订单id
        List<Long> orders = lambdaQuery()
                .eq(Order::getUserId, userId)
                .list()
                .stream()
                .map(Order::getId)
                .toList();

        if (CollUtils.isEmpty(orders)) {
            return R.ok(CollUtils.emptyList());
        }

        // 获取订单详情
        List<OrderDetail> details = detailService.lambdaQuery()
                .in(OrderDetail::getOrderId, orders)
                .list();

        return R.ok(details);
    }

    /**
     * 获取用户所有订单
     */
    @Override
    public R<List<OrderVO2>> getUserOrders(Integer status) {
        // 获取用户id
        Long userId = UserContext.getUserId();

        // 获取用户所有订单
        List<Order> orders = lambdaQuery()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime)
                .list();

        // 检查订单是否超时
        List<Long> outDateOrderIds = new ArrayList<>(); //超时订单id
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.NON_PAYMENT.getCode()) { // 如果订单未支付
                // 如果订单超时
                if (order.getCreateTime().plusSeconds(Time.TIMEOUT).isBefore(LocalDateTime.now())) {
                    outDateOrderIds.add(order.getId());
                    order.setStatus(OrderStatus.CLOSED.getCode()); // 修改订单状态为超时关闭
                }
            }
        }
        // MQ --> 发送[修改订单为超时状态]消息
        if (!outDateOrderIds.isEmpty()) {
            try {
                rabbitTemplate.convertAndSend(Q.PAY_EXCHANGE, Q.PAY_CLOSE_KEY, outDateOrderIds);
                log.debug("<发送关闭订单消息 --> MQ orderIds:{}>", outDateOrderIds);
            } catch (Exception e) {
                log.error("<发送关闭订单消息失败 --> MQ orderIds:{}>", outDateOrderIds, e);
            }
        }
        // 转换为VO
        List<OrderVO2> orderVOS = BeanUtils.copyList(orders, OrderVO2.class);

        // 获取所有订单id
        List<Long> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());

        // 批量查询订单详情
        List<OrderDetail> allDetails = detailService.lambdaQuery().in(OrderDetail::getOrderId, orderIds).list();

        // 将订单详情按订单id分组
        Map<Long, List<OrderDetail>> detailsMap = allDetails.stream().collect(Collectors.groupingBy(OrderDetail::getOrderId));

        // 设置订单详情
        for (OrderVO2 orderVO : orderVOS) {
            orderVO.setOrderDetails(detailsMap.get(orderVO.getId()));
        }

        return R.ok(orderVOS);
    }

    @Override
    public R<Void> deleteOrders(List<Long> orderIds) {
        // 获取用户ID
        Long userId = UserContext.getUserId();

        // 删除订单
        lambdaUpdate().eq(Order::getUserId, userId)
                .in(Order::getId, orderIds)
                .remove();

        return R.ok();
    }

    @Override
    public void markOrdersOutDate(List<Long> outDateOrderIds) {
        // 修改订单状态为超时关闭
        lambdaUpdate().in(Order::getId, outDateOrderIds)
                .set(Order::getStatus, OrderStatus.CLOSED.getCode())
                .update();
    }

    /**
     * 构建订单详情信息
     *
     * @param orderId 订单id
     * @param items   商品信息
     * @param numMap  商品数量映射
     */
    private List<OrderDetail> buildDetails(Long orderId, List<CommodityDTO> items, Map<Long, Integer> numMap) {
        List<OrderDetail> details = new ArrayList<>(items.size());
        for (CommodityDTO item : items) {
            OrderDetail detail = new OrderDetail();
            detail.setName(item.getName());
            detail.setSpec(item.getSpec());
            detail.setPrice(item.getPrice());
            detail.setNum(numMap.get(item.getId()));
            detail.setItemId(item.getId());
            detail.setImage(item.getImage());
            detail.setOrderId(orderId);
            details.add(detail);
        }
        return details;
    }
}

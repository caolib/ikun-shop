package io.github.caolib.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.caolib.client.CartClient;
import io.github.caolib.client.CommodityClient;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.CommodityDTO;
import io.github.caolib.domain.dto.OrderDetailDTO;
import io.github.caolib.domain.dto.OrderFormDTO;
import io.github.caolib.domain.po.Order;
import io.github.caolib.domain.po.OrderDetail;
import io.github.caolib.domain.vo.OrderVO;
import io.github.caolib.exception.BadRequestException;
import io.github.caolib.mapper.OrderMapper;
import io.github.caolib.service.IOrderDetailService;
import io.github.caolib.service.IOrderService;
import io.github.caolib.utils.UserContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    private final CommodityClient commodityClient;
    private final IOrderDetailService detailService;
    private final CartClient cartClient;

    @Override
    @GlobalTransactional
    public Long createOrder(OrderFormDTO orderFormDTO) {
        // 订单数据
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
        // 将Order写入数据库order表中
        save(order);

        // 保存订单详情到order_detail表中
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

        return order.getId();
    }

    @Override
    public void markOrderPaySuccess(Long orderId) {
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(2);
        order.setPayTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    public R<List<OrderVO>> getUserOrders() {
        Long userId = UserContext.getUserId();



        return null;
    }

    /**
     * 构建订单详情信息
     * @param orderId 订单id
     * @param items 商品信息
     * @param numMap 商品数量映射
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

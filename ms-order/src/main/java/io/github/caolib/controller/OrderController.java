package io.github.caolib.controller;

import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.OrderFormDTO;
import io.github.caolib.domain.vo.OrderVO;
import io.github.caolib.domain.vo.OrderVO2;
import io.github.caolib.service.IOrderService;
import io.github.caolib.utils.BeanUtils;
import io.github.caolib.utils.CollUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    /**
     * 获取用户订单 缓存慎用,可以考虑在MQ消息删除对应缓存
     *
     * @param status 订单状态
     */
    @GetMapping
    public R<List<OrderVO2>> getUserOrders(Integer status) {
        return orderService.getUserOrders(status);
    }

    /**
     * 根据id查询订单
     *
     * @param id 订单id
     * @return 订单信息
     */
    @GetMapping("/{id}")
    public OrderVO queryOrderById(@PathVariable String id) {
        return BeanUtils.copyBean(orderService.getById(id), OrderVO.class);
    }

    /**
     * 创建订单
     *
     * @param orderFormDTO 订单表单
     * @return 订单id
     */
    @PostMapping
    public Long createOrder(@RequestBody OrderFormDTO orderFormDTO) {
        log.debug("创建订单：{}", orderFormDTO);
        return orderService.createOrder(orderFormDTO);
    }

    /**
     * 标记订单已支付
     *
     * @param orderId 订单id
     */
    @PutMapping("/{orderId}")
    public void markOrderPaySuccess(@PathVariable("orderId") Long orderId) {
        orderService.markOrderPaySuccess(orderId);
    }


    /**
     * 批量删除订单
     */
    @DeleteMapping
    public R<Void> deleteBatch(@RequestBody List<String> orderIds) {
        // 转换为Long
        List<Long> ids = CollUtils.convertToLong(orderIds);

        log.debug("删除订单：{}", orderIds);

        return orderService.deleteOrders(ids);
    }


}
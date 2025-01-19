package io.github.caolib.controller;

import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.OrderFormDTO;
import io.github.caolib.domain.vo.OrderVO;
import io.github.caolib.service.IOrderService;
import io.github.caolib.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单管理接口
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    @GetMapping
    public R<List<OrderVO>> getUserOrders(){
        return orderService.getUserOrders();
    }

    /**
     * 根据id查询订单
     *
     * @param orderId 订单id
     * @return 订单信息
     */
    @GetMapping("{id}")
    public OrderVO queryOrderById(@PathVariable("id") Long orderId) {
        return BeanUtils.copyBean(orderService.getById(orderId), OrderVO.class);
    }

    /**
     * 创建订单
     *
     * @param orderFormDTO 订单表单
     * @return 订单id
     */
    @PostMapping
    public Long createOrder(@RequestBody OrderFormDTO orderFormDTO) {
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
}
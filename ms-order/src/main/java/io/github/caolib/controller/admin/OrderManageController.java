package io.github.caolib.controller.admin;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.caolib.domain.po.Order;
import io.github.caolib.domain.query.OrderQuery;
import io.github.caolib.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单管理
 */
@Slf4j
@RestController
@RequestMapping("/orders/manage")
@RequiredArgsConstructor
public class OrderManageController {
    private final IOrderService orderService;

    /**
     * 分页查询所有订单
     */
    @GetMapping
    public Page<Order> getOrderPage(OrderQuery query) {
        return orderService.getOrderPage(query);
    }
}

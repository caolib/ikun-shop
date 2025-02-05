package io.github.caolib.controller.admin;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.caolib.domain.R;
import io.github.caolib.domain.po.Order;
import io.github.caolib.domain.query.OrderQuery;
import io.github.caolib.domain.vo.OrderDetailVO;
import io.github.caolib.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 删除订单
     *
     * @param id 订单id
     */
    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }


    /**
     * 获取订单详情
     *
     * @param id 订单id
     */
    @GetMapping("/{id}")
    public R<OrderDetailVO> getOrderDetail(@PathVariable Long id) {
        return R.ok(orderService.getOrderDetail(id));
    }
}

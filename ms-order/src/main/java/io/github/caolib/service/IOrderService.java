package io.github.caolib.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.OrderFormDTO;
import io.github.caolib.domain.po.Order;
import io.github.caolib.domain.query.OrderQuery;
import io.github.caolib.domain.vo.OrderDetailVO;
import io.github.caolib.domain.vo.OrderVO2;

import java.util.List;

public interface IOrderService extends IService<Order> {

    Long createOrder(OrderFormDTO orderFormDTO);

    void markOrderPaySuccess(Long orderId);

    R<List<OrderVO2>> getUserOrders();

    R<Void> deleteOrders(List<Long> orderIds);

    void markOrderTimeout(Long orderId);

    Page<Order> getOrderPage(OrderQuery query);

    void deleteOrder(Long id);

    OrderDetailVO getOrderDetail(Long id);
}

package io.github.caolib.service;


import com.baomidou.mybatisplus.extension.service.IService;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.OrderFormDTO;
import io.github.caolib.domain.po.Order;
import io.github.caolib.domain.vo.OrderVO;

import java.util.List;

public interface IOrderService extends IService<Order> {

    Long createOrder(OrderFormDTO orderFormDTO);

    void markOrderPaySuccess(Long orderId);

    R<List<OrderVO>> getUserOrders();
}

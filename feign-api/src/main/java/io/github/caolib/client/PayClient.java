package io.github.caolib.client;

import io.github.caolib.domain.vo.PayOrderVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "pay", path = "/pays")
public interface PayClient {
    /**
     * 根据业务订单id查询支付单id
     *
     * @param orderId 业务订单id
     */
    @GetMapping("/{orderId}")
    PayOrderVO getPayOrderByOrderId(@PathVariable Long orderId);


    /**
     * 取消支付单
     *
     * @param orderId 订单id
     */
    @PutMapping("/{orderId}")
    void cancelPayOrder(@PathVariable Long orderId);
}

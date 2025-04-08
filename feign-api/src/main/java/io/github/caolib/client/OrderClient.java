package io.github.caolib.client;


import io.github.caolib.domain.vo.PayDetailVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "order", path = "/orders")
public interface OrderClient {
    @PutMapping("/{orderId}")
    void markOrderPaySuccess(@PathVariable("orderId") Long orderId);

    @PostMapping("/getDetails")
    List<PayDetailVO> getPayDetail(@RequestBody List<Long> orderIds);
}

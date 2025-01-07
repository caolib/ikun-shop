package io.github.caolib.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

@FeignClient(name = "cart", path = "/carts")
public interface CartClient {
    @DeleteMapping
    void deleteCartItemByIds(@RequestParam("ids") Collection<Long> ids);

}

package io.github.caolib.client;


import io.github.caolib.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

@FeignClient(name = "cart", path = "/carts")
public interface CartClient {
    @DeleteMapping
    void deleteCartItemByIds(@RequestParam("ids") Collection<Long> ids);

    @DeleteMapping("/cancel/{userId}")
    R<Void> deleteCartByUserId(@PathVariable Long userId);

}

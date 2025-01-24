package io.github.caolib.client;

import io.github.caolib.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user", path = "/users")
public interface UserClient {
    @PutMapping("/money/deduct")
    R<String> deductMoney(@RequestParam("pw") String pw, @RequestParam("amount") Integer amount);
}

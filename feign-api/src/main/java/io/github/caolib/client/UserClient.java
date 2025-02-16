package io.github.caolib.client;

import io.github.caolib.domain.R;
import io.github.caolib.domain.vo.UserInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user", path = "/users")
public interface UserClient {
    /**
     * 扣减余额
     *
     * @param pw     密码
     * @param amount 金额
     */
    @PutMapping("/money/deduct")
    R<String> deductMoney(@RequestParam String pw, @RequestParam Integer amount, @RequestParam Long userId);

    /**
     * 根据id获取用户名
     *
     * @param id 用户id
     */
    @GetMapping("/{id}")
    UserInfoVO getUserInfoById(@PathVariable Long id);
}

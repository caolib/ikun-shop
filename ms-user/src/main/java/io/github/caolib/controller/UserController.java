package io.github.caolib.controller;

import io.github.caolib.domain.dto.LoginFormDTO;
import io.github.caolib.domain.vo.UserLoginVO;
import io.github.caolib.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户相关接口
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    /**
     * 用户登录接口
     * @param loginFormDTO 登录表单
     * @return 用户登录信息
     */
    @PostMapping("login")
    public UserLoginVO login(@RequestBody @Validated LoginFormDTO loginFormDTO){
        return userService.login(loginFormDTO);
    }
    /**
     * 扣减余额
     * @param pw 支付密码
     * @param amount 支付金额
     */
    @PutMapping("/money/deduct")
    public void deductMoney(@RequestParam("pw") String pw, @RequestParam("amount") Integer amount){
        userService.deductMoney(pw, amount);
    }
}
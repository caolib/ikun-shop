package io.github.caolib.controller;

import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.LoginFormDTO;
import io.github.caolib.domain.dto.PwdFormDTO;
import io.github.caolib.domain.dto.RegisterFormDTO;
import io.github.caolib.domain.vo.UserInfoVO;
import io.github.caolib.domain.vo.UserLoginVO;
import io.github.caolib.service.IUserService;
import io.github.caolib.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    /**
     * 用户登录接口
     *
     * @param loginFormDTO 登录表单
     * @return 用户登录信息
     */
    @PostMapping("/login")
    public UserLoginVO login(@RequestBody LoginFormDTO loginFormDTO) {
        return userService.login(loginFormDTO);
    }

    @GetMapping("/logout")
    public R<Void> logout(){
        userService.logout();
        return R.ok();
    }

    /**
     * 扣减余额
     *
     * @param pw     支付密码
     * @param amount 支付金额
     */
    @PutMapping("/money/deduct")
    R<String> deductMoney(@RequestParam String pw, @RequestParam Integer amount, @RequestParam Long userId) {
        return userService.deductBalance(pw, amount, userId);
    }

    // 用户注册
    @PostMapping("/register")
    public R<Void> register(@RequestBody @Validated RegisterFormDTO registerFormDTO) {
        log.debug("注册: {}", registerFormDTO);

        return userService.register(registerFormDTO);
    }

    /**
     * 获取用户信息
     */
    @GetMapping
    public R<UserInfoVO> getUserInfo() {
        Long userId = UserContext.getUserId();

        return userService.getUserInfo(userId);
    }

    /**
     * 根据id获取用户名
     *
     * @param id 用户id
     */
    @GetMapping("/{id}")
    UserInfoVO getUserInfoById(@PathVariable Long id) {
        return userService.getUserInfoById(id);
    }

    /**
     * 修改密码
     */
    @PutMapping("/pwd")
    public R<Void> changePassword(@RequestBody PwdFormDTO pwdFormDTO) {
        log.debug("修改密码: {}", pwdFormDTO);

        return userService.changePassword(UserContext.getUserId(), pwdFormDTO);
    }

    /**
     * 注销账户
     */
    @DeleteMapping
    public R<Void> cancelAccount() {
        return userService.cancelAccount(UserContext.getUserId());
    }

}
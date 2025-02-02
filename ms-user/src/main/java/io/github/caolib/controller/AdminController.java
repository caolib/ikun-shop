package io.github.caolib.controller;


import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.LoginFormDTO;
import io.github.caolib.domain.dto.RegisterFormDTO;
import io.github.caolib.domain.vo.UserLoginVO;
import io.github.caolib.service.IAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/users/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IAdminService adminService;

    /**
     * 管理员登录接口
     *
     * @param loginForm 登录表单
     * @return 管理员登录信息
     */
    @PostMapping("/login")
    public UserLoginVO login(@RequestBody LoginFormDTO loginForm) {
        return adminService.login(loginForm);
    }


    /**
     * 管理员注册
     * @param registerFormDTO 注册表单
     */
    @PostMapping("/register")
    public R<Void> register(@RequestBody @Validated RegisterFormDTO registerFormDTO) {
        log.debug("注册: {}", registerFormDTO);

        return adminService.register(registerFormDTO);
    }

}

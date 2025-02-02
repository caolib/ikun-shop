package io.github.caolib.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.LoginFormDTO;
import io.github.caolib.domain.dto.RegisterFormDTO;
import io.github.caolib.domain.po.AdminUser;
import io.github.caolib.domain.vo.UserLoginVO;
import io.github.caolib.enums.Auth;
import io.github.caolib.enums.Code;
import io.github.caolib.enums.UserStatus;
import io.github.caolib.exception.BadRequestException;
import io.github.caolib.exception.BizIllegalException;
import io.github.caolib.mapper.AdminMapper;
import io.github.caolib.service.IAdminService;
import io.github.caolib.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl extends ServiceImpl<AdminMapper, AdminUser> implements IAdminService {
    private final JwtTool jwtTool;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserLoginVO login(LoginFormDTO loginDTO) {
        // 查询用户
        AdminUser admin = lambdaQuery().eq(AdminUser::getUsername, loginDTO.getUsername()).one();
        // 校验用户是否存在
        if (admin == null) throw new BadRequestException(Code.USER_NOT_EXIST);
        // 校验用户信息
        jwtTool.checkUser(loginDTO, Auth.ADMIN, admin.getStatus(), admin.getPassword());
        // 返回用户信息
        return jwtTool.setReturnUser(admin, Auth.ADMIN, "");
    }

    /**
     * 管理员注册
     *
     * @param registerFormDTO 注册表单
     */
    @Override
    public R<Void> register(RegisterFormDTO registerFormDTO) {
        String username = registerFormDTO.getUsername();
        String password = registerFormDTO.getPassword();
        String phone = registerFormDTO.getPhone();

        // 校验用户名是否存在
        lambdaQuery().eq(AdminUser::getUsername, username).oneOpt().ifPresent(user -> {
            throw new BizIllegalException(Code.USERNAME_ALREADY_EXIST); // 用户名已存在
        });

        // 电话号码是否存在
        lambdaQuery().eq(AdminUser::getPhone, phone).oneOpt().ifPresent(user -> {
            throw new BizIllegalException(Code.PHONE_ALREADY_EXIST); // 电话号码已存在
        });

        // 保存用户信息
        LocalDateTime now = LocalDateTime.now();

        AdminUser admin = AdminUser.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .phone(phone)
                .createTime(now)
                .updateTime(now)
                .status(UserStatus.NORMAL)
                .build();

        save(admin);

        return R.ok();
    }

}

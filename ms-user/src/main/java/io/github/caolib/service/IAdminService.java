package io.github.caolib.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.LoginFormDTO;
import io.github.caolib.domain.dto.RegisterFormDTO;
import io.github.caolib.domain.po.AdminUser;
import io.github.caolib.domain.vo.UserLoginVO;

public interface IAdminService extends IService<AdminUser> {
    UserLoginVO login(LoginFormDTO loginForm);

    R<Void> register(RegisterFormDTO registerFormDTO);
}

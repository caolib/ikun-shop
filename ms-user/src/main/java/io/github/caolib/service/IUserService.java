package io.github.caolib.service;


import com.baomidou.mybatisplus.extension.service.IService;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.LoginFormDTO;
import io.github.caolib.domain.dto.PwdFormDTO;
import io.github.caolib.domain.dto.RegisterFormDTO;
import io.github.caolib.domain.po.User;
import io.github.caolib.domain.vo.UserInfoVO;
import io.github.caolib.domain.vo.UserLoginVO;

public interface IUserService extends IService<User> {

    UserLoginVO login(LoginFormDTO loginFormDTO);

    R<String> deductMoney(String pw, Integer totalFee);

    R<UserInfoVO> getUserInfo();

    R<Void> register(RegisterFormDTO registerFormDTO);

    R<Void> changePassword(PwdFormDTO pwdFormDTO);

    R<Void> cancelAccount();

}

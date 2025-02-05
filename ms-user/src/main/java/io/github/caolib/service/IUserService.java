package io.github.caolib.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.LoginFormDTO;
import io.github.caolib.domain.dto.PwdFormDTO;
import io.github.caolib.domain.dto.RegisterFormDTO;
import io.github.caolib.domain.dto.UserDTO;
import io.github.caolib.domain.po.User;
import io.github.caolib.domain.query.UserQuery;
import io.github.caolib.domain.vo.UserInfoVO;
import io.github.caolib.domain.vo.UserLoginVO;

public interface IUserService extends IService<User> {

    UserLoginVO login(LoginFormDTO loginFormDTO);

    R<String> deductBalance(String pw, Integer totalFee, Long userId);

    R<UserInfoVO> getUserInfo(Long userId);

    R<Void> register(RegisterFormDTO registerFormDTO);

    R<Void> changePassword(Long userId, PwdFormDTO pwdFormDTO);

    R<Void> cancelAccount(Long userId);

    R<Page<User>> getUsers(UserQuery query);

    R<Void> freezeUser(Long id);

    R<Void> recoverUser(Long id);

    UserInfoVO getUserInfoById(Long id);

    void updateUser(UserDTO user);
}

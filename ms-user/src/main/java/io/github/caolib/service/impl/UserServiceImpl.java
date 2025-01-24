package io.github.caolib.service.impl;


import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.caolib.config.UserProperties;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.LoginFormDTO;
import io.github.caolib.domain.dto.RegisterFormDTO;
import io.github.caolib.domain.po.User;
import io.github.caolib.domain.po.UserOAuth;
import io.github.caolib.domain.vo.UserInfoVO;
import io.github.caolib.domain.vo.UserLoginVO;
import io.github.caolib.enums.Code;
import io.github.caolib.enums.UserStatus;
import io.github.caolib.exception.BadRequestException;
import io.github.caolib.exception.BizIllegalException;
import io.github.caolib.exception.ForbiddenException;
import io.github.caolib.mapper.OAuthMapper;
import io.github.caolib.mapper.UserMapper;
import io.github.caolib.service.IUserService;
import io.github.caolib.utils.BeanUtils;
import io.github.caolib.utils.JwtTool;
import io.github.caolib.utils.PhoneUtil;
import io.github.caolib.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    private final OAuthMapper oAuthMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTool jwtTool;
    private final UserProperties userProperties;

    @Override
    public UserLoginVO login(LoginFormDTO loginDTO) {
        // 数据校验
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        // 根据用户名或手机号查询
        User user = lambdaQuery().eq(User::getUsername, username).one();
        Assert.notNull(user, "用户不存在"); // 判断用户是否存在
        // 校验账号状态
        if (user.getStatus() == UserStatus.FROZEN) {
            throw new ForbiddenException(Code.USER_IS_FROZEN);
        }
        // 校验密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException(Code.USERNAME_OR_PASSWORD_ERROR);
        }
        // 返回用户信息
        return jwtTool.setReturnUser(user, "");
    }

    @Override
    public R<String> deductMoney(String pw, Integer totalFee) {
        Long userId = UserContext.getUserId();
        // 校验密码
        User user = getById(userId);
        if (user == null || !passwordEncoder.matches(pw, user.getPassword())) {
            Code error = Code.USERNAME_OR_PASSWORD_ERROR;
            log.error(error.getMessage());
            throw new BizIllegalException(error);
        }
        // 查询账户余额
        Long money = userMapper.getUserMoney(userId);
        if (money < totalFee) {
            throw new BizIllegalException(Code.INSUFFICIENT_BALANCE);
        }
        // 扣减余额
        baseMapper.updateMoney(userId, totalFee);

        return R.ok("扣减成功");
    }


    @Override
    public R<UserInfoVO> getUserInfo() {
        Long userId = UserContext.getUserId();
        // 查询用户信息
        User user = getById(userId);
        // 判断用户是否存在
        if (user == null) {
            return R.error(Code.USER_NOT_EXIST);
        }
        // 查询用户授权信息
        UserOAuth oauthUser = oAuthMapper.getByUserId(userId);
        //log.debug("oauthUser:{}", oauthUser);
        // 封装返回数据
        UserInfoVO userInfoVO = BeanUtils.copyBean(user, UserInfoVO.class);
        //log.debug("userInfoVO:{}", userInfoVO);
        if (oauthUser != null) {
            userInfoVO.setOauth_name(oauthUser.getUsername());
            userInfoVO.setAvatar_url(oauthUser.getAvatarUrl());
            userInfoVO.setProvider(oauthUser.getProvider());
        }

        // 电话号码脱敏,只显示前三位和后四位,中间用*代替
        String maskPhone = PhoneUtil.maskPhone(userInfoVO.getPhone());
        log.debug("maskPhone:{}", maskPhone);
        userInfoVO.setPhone(maskPhone);

        return R.ok(userInfoVO);
    }

    @Override
    public R<Void> register(RegisterFormDTO registerFormDTO) {
        String username = registerFormDTO.getUsername();
        String password = registerFormDTO.getPassword();
        String phone = registerFormDTO.getPhone();

        // 校验用户名是否存在
        int count =  userMapper.getUserByUsername(username);
        if (count > 0) {
            throw new BizIllegalException(Code.USERNAME_ALREADY_EXIST); // 用户名已存在
        }
        // 电话号码是否存在
        count = userMapper.getUserByPhone(phone);
        if (count > 0) {
            throw new BizIllegalException(Code.PHONE_ALREADY_EXIST); // 电话号码已存在
        }

        // 保存用户信息
        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .phone(phone)
                .createTime(now)
                .updateTime(now)
                .balance(userProperties.getInitBalance())
                .build();

        save(user);

        return R.ok();
    }
}

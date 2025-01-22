package io.github.caolib.service.impl;


import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.caolib.domain.dto.LoginFormDTO;
import io.github.caolib.domain.po.User;
import io.github.caolib.domain.vo.UserLoginVO;
import io.github.caolib.enums.UserStatus;
import io.github.caolib.exception.BadRequestException;
import io.github.caolib.exception.BizIllegalException;
import io.github.caolib.exception.ForbiddenException;
import io.github.caolib.mapper.UserMapper;
import io.github.caolib.service.IUserService;
import io.github.caolib.utils.JwtTool;
import io.github.caolib.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    private final PasswordEncoder passwordEncoder;
    private final JwtTool jwtTool;

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
            throw new ForbiddenException("账号冻结");
        }
        // 校验密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("用户名或密码错误");
        }
        // 返回用户信息
        return jwtTool.setReturnUser(user, "");
    }

    @Override
    public void deductMoney(String pw, Integer totalFee) {
        log.debug("开始扣款");
        // 校验密码
        User user = getById(UserContext.getUserId());
        if (user == null || !passwordEncoder.matches(pw, user.getPassword())) {
            throw new BizIllegalException("用户密码错误");
        }
        // 尝试扣款
        try {
            baseMapper.updateMoney(UserContext.getUserId(), totalFee);
        } catch (Exception e) {
            throw new RuntimeException("扣款失败，可能是余额不足！", e);
        }
        log.debug("扣款成功");
    }
}

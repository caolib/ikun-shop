package io.github.caolib.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.caolib.client.CartClient;
import io.github.caolib.config.UserProperties;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.LoginFormDTO;
import io.github.caolib.domain.dto.PwdFormDTO;
import io.github.caolib.domain.dto.RegisterFormDTO;
import io.github.caolib.domain.dto.UserDTO;
import io.github.caolib.domain.po.Address;
import io.github.caolib.domain.po.User;
import io.github.caolib.domain.po.UserOAuth;
import io.github.caolib.domain.query.UserQuery;
import io.github.caolib.domain.vo.UserInfoVO;
import io.github.caolib.domain.vo.UserLoginVO;
import io.github.caolib.enums.*;
import io.github.caolib.exception.BadRequestException;
import io.github.caolib.exception.BizIllegalException;
import io.github.caolib.mapper.AddressMapper;
import io.github.caolib.mapper.OAuthMapper;
import io.github.caolib.mapper.UserMapper;
import io.github.caolib.service.IUserService;
import io.github.caolib.utils.BeanUtils;
import io.github.caolib.utils.JwtTool;
import io.github.caolib.utils.PhoneUtil;
import io.github.caolib.utils.TimeUtil;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private final RabbitTemplate rabbitTemplate;
    private final CartClient cartClient;
    private final AddressMapper addressMapper;

    @Override
    public UserLoginVO login(LoginFormDTO loginDTO) {
        // 查询用户
        User user = lambdaQuery().eq(User::getUsername, loginDTO.getUsername()).one();
        // 校验用户是否存在
        if (user == null) throw new BadRequestException(Code.USER_NOT_EXIST);
        // 校验用户信息
        jwtTool.checkUser(loginDTO, Auth.USER, user.getStatus(), user.getPassword());
        // 返回用户信息
        return jwtTool.setReturnUser(user, Auth.USER, "");
    }

    /**
     * 扣减用户余额
     *
     * @param pw       支付密码
     * @param totalFee 支付金额
     * @param userId   用户id
     */
    @Override
    @CacheEvict(value = Cache.USER_INFO, key = "#userId")
    public R<String> deductBalance(String pw, Integer totalFee, Long userId) {
        // 校验密码
        User user = getById(userId);
        checkPwd(user, pw);

        // 查询账户余额
        Long money = userMapper.getUserMoney(userId);
        if (money < totalFee) {
            throw new BizIllegalException(Code.INSUFFICIENT_BALANCE);
        }
        // 扣减余额
        baseMapper.updateMoney(userId, totalFee);

        return R.ok("扣减成功");
    }


    /**
     * 获取用户信息
     *
     * @param userId 用户id
     */
    @Override
    @Cacheable(value = Cache.USER_INFO, key = "#userId")
    public R<UserInfoVO> getUserInfo(Long userId) {
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
        //log.debug("maskPhone:{}", maskPhone);
        userInfoVO.setPhone(maskPhone);

        return R.ok(userInfoVO);
    }

    /**
     * 用户注册
     *
     * @param registerFormDTO 注册表单
     */
    @Override
    public R<Void> register(RegisterFormDTO registerFormDTO) {
        String username = registerFormDTO.getUsername();
        String password = registerFormDTO.getPassword();
        String phone = registerFormDTO.getPhone();

        // 校验用户名是否存在
        int count = userMapper.getUserByUsername(username);
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

    /**
     * 修改密码
     *
     * @param userId     用户id
     * @param pwdFormDTO 密码表单
     */
    @Override
    @CacheEvict(value = Cache.USER_INFO, key = "#userId")
    public R<Void> changePassword(Long userId, PwdFormDTO pwdFormDTO) {
        // 校验密码
        User user = getById(userId);
        checkPwd(user, pwdFormDTO.getOldPwd());

        // 更新密码
        userMapper.updatePwd(userId, passwordEncoder.encode(pwdFormDTO.getNewPwd()));

        return R.ok();
    }


    /**
     * 注销账户
     */
    @Override
    @GlobalTransactional
    @CacheEvict(value = Cache.USER_INFO, key = "#userId")
    public R<Void> cancelAccount(Long userId) {
        // 删除用户
        removeById(userId);

        // 删除用户地址信息
        addressMapper.delete(new LambdaQueryWrapper<Address>().eq(Address::getUserId, userId));

        // 删除用户授权信息
        oAuthMapper.deleteByUserId(userId);

        // MQ --> 删除用户支付单信息
        try {
            rabbitTemplate.convertAndSend(Q.PAY_EXCHANGE, Q.PAY_DELETE_KEY, userId);
            log.debug("<删除用户支付单信息 --> MQ，userId:{}>", userId);
        } catch (Exception e) {
            log.error(E.MQ_UPDATE_ORDER_STATUS_FAILED, userId);
        }

        // RPC --> 删除用户购物车所有信息
        R<Void> res = cartClient.deleteCartByUserId(userId);
        if (res.getCode() != 200) {
            throw new BizIllegalException(E.RPC_DELETE_CART_FAILED);
        }

        return R.ok();
    }


    /**
     * 分页查询用户信息
     *
     * @param query 查询条件
     */
    @Override
    public R<Page<User>> getUsers(UserQuery query) {
        Long id = query.getId();
        String username = query.getUsername();
        String phone = query.getPhone();
        Integer status = query.getStatus() == null ? null : query.getStatus();
        // 分页查询
        Page<User> page = lambdaQuery()
                .eq(id != null, User::getId, id)
                .like(StrUtil.isNotBlank(username), User::getUsername, username)
                .eq(StrUtil.isNotBlank(phone), User::getPhone, phone)
                .eq(status != null, User::getStatus, UserStatus.of(status))
                .page(query.toPage());

        // 删除密码信息
        page.getRecords().forEach(user -> user.setPassword(null));

        return R.ok(page);
    }

    @Override
    public R<Void> freezeUser(Long id) {
        // 查询用户
        User user = getById(id);
        // 校验用户是否存在
        if (user == null) throw new BadRequestException(Code.USER_NOT_EXIST);
        // 校验用户状态
        if (user.getStatus() == UserStatus.FROZEN) throw new BizIllegalException(Code.USER_ALREADY_FROZEN);

        // 冻结用户账号
        user.setStatus(UserStatus.FROZEN);
        updateById(user);

        return R.ok();
    }

    @Override
    public R<Void> recoverUser(Long id) {
        // 查询用户
        User user = getById(id);
        // 校验用户是否存在
        if (user == null) throw new BadRequestException(Code.USER_NOT_EXIST);
        // 校验用户状态
        if (user.getStatus() == UserStatus.NORMAL) throw new BizIllegalException(Code.USER_ALREADY_NORMAL);

        // 解冻用户账号
        user.setStatus(UserStatus.NORMAL);
        updateById(user);

        return R.ok();
    }

    /**
     * 根据id获取用户信息
     *
     * @param id 用户id
     */
    @Override
    public UserInfoVO getUserInfoById(Long id) {
        User user = getById(id);
        if (user == null) throw new BizIllegalException(Code.USER_NOT_EXIST);

        return BeanUtils.copyBean(user, UserInfoVO.class);
    }

    @Override
    public void updateUser(UserDTO user) {
        // 设置更新时间
        user.setUpdateTime(TimeUtil.now());

        userMapper.updateUser(user);
    }


    /**
     * 校验密码
     */
    public void checkPwd(User user, String password) {
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            Code error = Code.USERNAME_OR_PASSWORD_ERROR;
            log.error(error.getMessage());
            throw new BizIllegalException(error);
        }
    }
}

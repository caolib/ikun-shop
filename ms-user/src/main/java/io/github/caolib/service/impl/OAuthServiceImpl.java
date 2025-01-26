package io.github.caolib.service.impl;

import io.github.caolib.config.UserProperties;
import io.github.caolib.domain.R;
import io.github.caolib.domain.po.GitHubUser;
import io.github.caolib.domain.po.TokenResponse;
import io.github.caolib.domain.po.User;
import io.github.caolib.domain.po.UserOAuth;
import io.github.caolib.domain.vo.UserLoginVO;
import io.github.caolib.enums.GH;
import io.github.caolib.exception.GitHubLoginException;
import io.github.caolib.mapper.OAuthMapper;
import io.github.caolib.mapper.UserMapper;
import io.github.caolib.service.OAuthService;
import io.github.caolib.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuthServiceImpl implements OAuthService {
    private final OAuthMapper OAuthMapper;
    private final UserMapper userMapper;
    private final JwtTool jwtTool;
    private final UserProperties userProperties;

    /**
     * 获取请求体
     *
     * @param code 授权码
     * @return 请求体
     */
    @NotNull
    private static HttpEntity<MultiValueMap<String, String>> getMultiValueMapHttpEntity(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");

        // 设置请求体
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", GH.CLIENT_ID);
        body.add("client_secret", GH.CLIENT_SECRET);
        body.add("code", code);
        body.add("redirect_uri", GH.CALL_BACK_URL); // 确保和 GitHub 应用中的回调 URL 一致

        return new HttpEntity<>(body, headers);
    }

    /**
     * 获取访问令牌
     *
     * @param code 授权码
     * @return 访问令牌
     */
    @Override
    public String getAccessToken(String code) {
        // 设置代理
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 7890));
        factory.setProxy(proxy);
        RestTemplate restTemplate = new RestTemplate(factory);

        // 设置请求头
        HttpEntity<MultiValueMap<String, String>> requestEntity = getMultiValueMapHttpEntity(code);
        try {
            ResponseEntity<TokenResponse> responseEntity = restTemplate.exchange(
                    GH.TOKEN_URL,
                    HttpMethod.POST,
                    requestEntity,
                    TokenResponse.class
            );
            // 返回访问令牌
            TokenResponse accessTokenResponse = responseEntity.getBody();
            if (accessTokenResponse != null) {
                return accessTokenResponse.getAccessToken();
            } else {
                throw new RuntimeException("获取访问令牌失败");
            }
        } catch (ResourceAccessException e) {
            if (Objects.requireNonNull(e.getMessage()).contains("github.com/login/oauth/access_token")) {
                throw new GitHubLoginException("连接超时,请检查网络！", 401);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    @Transactional
    public R<UserLoginVO> login(String code) {
        String accessToken = getAccessToken(code);
        log.debug("access_token: {}", accessToken);
        GitHubUser gitHubUser = getGitHubUser(accessToken);
        log.debug("GitHub 用户信息: {}", gitHubUser);

        String username = gitHubUser.getLogin();        // 用户名
        String nickname = gitHubUser.getName();         // 昵称
        String email = gitHubUser.getEmail();           // 邮箱
        Long oauthId = gitHubUser.getId();              // github 用户id
        String avatarUrl = gitHubUser.getAvatarUrl();   // 头像地址

        // 从用户授权表查找用户是否存在
        UserOAuth userOAuth = OAuthMapper.selectByOAuthId(String.valueOf(oauthId));
        User user;
        // 不存在就创建用户，并创建用户授权信息
        if (userOAuth == null) {
            log.debug("用户不存在，开始创建用户...");
            // 创建用户
            user = User.builder().username(nickname)
                    .balance(userProperties.getInitBalance())
                    .password(userProperties.getInitPassword())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now()).build();
            // 插入用户表
            userMapper.insert(user);

            log.debug("用户创建成功，开始创建用户授权信息...");

            // 获取用户id
            Long userId = user.getId();
            // 创建用户授权信息
            UserOAuth oauthUser = UserOAuth.builder().accessToken(accessToken)
                    .userId(userId)
                    .oauthId(String.valueOf(oauthId))
                    .username(username)
                    .avatarUrl(avatarUrl)
                    .email(email)
                    .build();

            // 插入用户授权表
            OAuthMapper.addOauthUser(oauthUser);
            log.debug("用户授权信息创建成功");
        } else {
            user = userMapper.selectById(userOAuth.getUserId());
            // 更新访问令牌
            OAuthMapper.updateAccessToken(oauthId, accessToken);
        }
        // 返回用户信息
        UserLoginVO vo = jwtTool.setReturnUser(user, avatarUrl);
        return R.ok(vo);
    }

    /**
     * 获取 GitHub 用户信息
     *
     * @param accessToken 访问令牌
     * @return GitHub 用户信息
     */
    public GitHubUser getGitHubUser(String accessToken) {
        log.debug("开始获取 GitHub 用户信息...");
        RestTemplate restTemplate = new RestTemplate();
        // 设置请求头，携带 access_token
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 发送请求
        try {
            ResponseEntity<GitHubUser> responseEntity = restTemplate.exchange(
                    GH.GITHUB_USER_API_URL,
                    org.springframework.http.HttpMethod.GET,
                    entity,
                    GitHubUser.class
            );
            log.debug("{}", responseEntity.getBody());
            return responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            throw new GitHubLoginException("获取GitHub用户信息失败", 401);
        }
    }
}

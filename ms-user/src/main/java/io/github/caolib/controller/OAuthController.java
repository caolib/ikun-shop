package io.github.caolib.controller;


import io.github.caolib.domain.R;
import io.github.caolib.domain.vo.UserLoginVO;
import io.github.caolib.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 第三方登录
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/oauth")
@RestController
public class OAuthController {
    private final OAuthService OAuthService;

    @GetMapping("/github")
    public R<UserLoginVO> handleGitHubCallback(@RequestParam String code) {
        log.debug("github授权码: {}", code);

        return OAuthService.login(code);
    }

}

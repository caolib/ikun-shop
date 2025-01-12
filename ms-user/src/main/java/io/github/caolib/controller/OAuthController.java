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

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/oauth")
@RestController
public class OAuthController {
    private final OAuthService OAuthService;

    @GetMapping("/success")
    public R<UserLoginVO> handleGitHubCallback(@RequestParam String code, @RequestParam(required = false) String state) {
        log.debug("code: {} state: {}", code, state);

        return OAuthService.login(code);
    }

}

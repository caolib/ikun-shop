package io.github.caolib.service;

import io.github.caolib.domain.R;
import io.github.caolib.domain.vo.UserLoginVO;

public interface OAuthService {
    String getAccessToken(String code);

    R<UserLoginVO> login(String code);
}

package io.github.caolib.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * GitHub OAuth 配置
 */
@Data
@ConfigurationProperties(prefix = "github")
public class GithubProperties {
    public static final String TOKEN_URL = "https://github.com/login/oauth/access_token";
    public static final String GITHUB_USER_API_URL = "https://api.github.com/user";
    public String clientId;
    public String clientSecret;
    public String callBackUrl;
}

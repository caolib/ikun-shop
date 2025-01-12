package io.github.caolib.domain.po;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class TokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("scope")
    private String scope;
}

package io.github.caolib.mapper;

import io.github.caolib.domain.po.UserOAuth;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.mybatis.spring.annotation.MapperScan;

@MapperScan
public interface OAuthMapper {

    @Select("select * from user_oauth where oauth_id = #{oAuthId}")
    UserOAuth selectByOAuthId(String oAuthId);

    void insert(UserOAuth oauthUser);

    @Update("update user_oauth set access_token = #{accessToken} where oauth_id = #{oauthId}")
    void updateAccessToken(Long oauthId, String accessToken);
}

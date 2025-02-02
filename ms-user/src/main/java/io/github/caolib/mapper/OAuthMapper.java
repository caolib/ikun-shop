package io.github.caolib.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.caolib.domain.po.UserOAuth;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OAuthMapper extends BaseMapper<UserOAuth> {

    @Select("SELECT * FROM user_oauth WHERE oauth_id = #{oAuthId}")
    UserOAuth selectByOAuthId(String oAuthId);

    void addOauthUser(UserOAuth oauthUser);

    @Update("UPDATE user_oauth SET access_token = #{accessToken} WHERE oauth_id = #{oauthId}")
    void updateAccessToken(Long oauthId, String accessToken);

    @Select("SELECT * FROM user_oauth WHERE user_id = #{userId};")
    UserOAuth getByUserId(Long userId);

    @Delete("DELETE FROM user_oauth WHERE user_id = #{userId}")
    void deleteByUserId(Long userId);
}

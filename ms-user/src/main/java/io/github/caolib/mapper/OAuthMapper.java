package io.github.caolib.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.caolib.domain.po.UserOAuth;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OAuthMapper extends BaseMapper<UserOAuth> {

    @Select("select * from user_oauth where oauth_id = #{oAuthId}")
    UserOAuth selectByOAuthId(String oAuthId);

    void addOauthUser(UserOAuth oauthUser);

    @Update("update user_oauth set access_token = #{accessToken} where oauth_id = #{oauthId}")
    void updateAccessToken(Long oauthId, String accessToken);

    @Select("SELECT * FROM user_oauth WHERE user_id = #{userId};")
    UserOAuth getByUserId(Long userId);

    @Delete("delete from user_oauth where user_id = #{userId}")
    void deleteByUserId(Long userId);
}

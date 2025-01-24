package io.github.caolib.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.caolib.domain.po.User;
import org.apache.ibatis.annotations.*;


@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Update("update user set balance = balance - ${totalFee} where id = #{userId}")
    void updateMoney(@Param("userId") Long userId, @Param("totalFee") Integer totalFee);

    @Insert("INSERT INTO user (username, create_time,update_time) VALUES (#{username}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertUser(User user);

    @Select("SELECT balance FROM user WHERE id = #{userId}")
    Long getUserMoney(Long userId);


    @Select("SELECT COUNT(*) FROM user WHERE username = #{username}")
    int getUserByUsername(String username);

    @Select("SELECT COUNT(*) FROM user WHERE phone = #{phone}")
    int getUserByPhone(String phone);
}

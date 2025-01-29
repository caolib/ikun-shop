package io.github.caolib.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.caolib.domain.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Update("UPDATE user SET balance = balance - ${totalFee} WHERE id = #{userId}")
    void updateMoney(@Param("userId") Long userId, @Param("totalFee") Integer totalFee);

    @Select("SELECT balance FROM user WHERE id = #{userId}")
    Long getUserMoney(Long userId);

    @Select("SELECT COUNT(*) FROM user WHERE username = #{username}")
    int getUserByUsername(String username);

    @Select("SELECT COUNT(*) FROM user WHERE phone = #{phone}")
    int getUserByPhone(String phone);

    @Update("UPDATE user SET password = #{pwd} WHERE id = #{userId}")
    void updatePwd(Long userId, String pwd);
}

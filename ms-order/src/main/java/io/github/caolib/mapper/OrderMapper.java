package io.github.caolib.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.caolib.domain.po.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Update("UPDATE `order` SET status = #{code} WHERE id = #{orderId}")
    void markOrderTimeout(Long orderId, int code);
}

package io.github.caolib.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.caolib.domain.po.PayOrder;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


public interface PayOrderMapper extends BaseMapper<PayOrder> {

    @Select("SELECT pay_order_no FROM pay_order WHERE biz_order_no = #{bizOrderId}")
    Long getPayOrderId(Long bizOrderId);

    @Update("UPDATE pay_order SET is_delete = TRUE WHERE biz_user_id = #{userId}")
    void updatePayStatusByUserId(Long userId);
}

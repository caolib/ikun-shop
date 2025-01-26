package io.github.caolib.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.caolib.domain.po.PayOrder;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


public interface PayOrderMapper extends BaseMapper<PayOrder> {

    @Select("select pay_order_no from pay_order where biz_order_no = #{bizOrderId}")
    Long getPayOrderId(Long bizOrderId);

    @Update("update pay_order set is_delete = true where biz_user_id = #{userId}")
    void updatePayStatusByUserId(Long userId);
}

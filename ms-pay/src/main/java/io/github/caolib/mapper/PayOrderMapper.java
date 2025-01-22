package io.github.caolib.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.caolib.domain.po.PayOrder;
import org.apache.ibatis.annotations.Select;


public interface PayOrderMapper extends BaseMapper<PayOrder> {

    @Select("select pay_order_no from pay_order where biz_order_no = #{bizOrderId}")
    Long getPayOrderId(Long bizOrderId);
}

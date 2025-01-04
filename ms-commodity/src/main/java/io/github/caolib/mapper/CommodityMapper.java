package io.github.caolib.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.github.caolib.domain.dto.OrderDetailDTO;
import io.github.caolib.domain.po.Commodity;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author 虎哥
 * @since 2023-05-05
 */
public interface CommodityMapper extends BaseMapper<Commodity> {

    @Update("UPDATE commodity SET stock = stock - #{num} WHERE id = #{itemId}")
    void updateStock(OrderDetailDTO orderDetail);
}

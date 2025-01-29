package io.github.caolib.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.caolib.domain.dto.OrderDetailDTO;
import io.github.caolib.domain.po.Commodity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CommodityMapper extends BaseMapper<Commodity> {

    @Update("UPDATE commodity SET stock = stock - #{num} WHERE id = #{itemId}")
    void updateStock(OrderDetailDTO orderDetail);

    void updateStockBatch(@Param("items") List<OrderDetailDTO> items);

    @Update("UPDATE commodity SET stock = stock + #{num} WHERE id = #{itemId}")
    void recover(Long itemId, Integer num);
}

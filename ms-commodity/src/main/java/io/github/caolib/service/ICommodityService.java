package io.github.caolib.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.caolib.domain.PageDTO;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.CommodityDTO;
import io.github.caolib.domain.dto.OrderDetailDTO;
import io.github.caolib.domain.po.Commodity;
import io.github.caolib.domain.query.SearchQuery;

import java.util.Collection;
import java.util.List;

public interface ICommodityService extends IService<Commodity> {

    R<Void> deductStock(List<OrderDetailDTO> items);

    List<CommodityDTO> getItemByIds(Collection<Long> ids);

    PageDTO<CommodityDTO> pageQuery(SearchQuery query);

    void releaseStock(List<OrderDetailDTO> dtos);

    void updateCommodity(CommodityDTO commodityDTO);


    //PageDTO<CommodityDTO> queryItemByPage(PageQuery query);
}

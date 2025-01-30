package io.github.caolib.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.caolib.domain.PageDTO;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.CommodityDTO;
import io.github.caolib.domain.dto.OrderDetailDTO;
import io.github.caolib.domain.po.Commodity;
import io.github.caolib.domain.query.SearchQuery;
import io.github.caolib.enums.Cache;
import io.github.caolib.exception.BizIllegalException;
import io.github.caolib.mapper.CommodityMapper;
import io.github.caolib.service.ICommodityService;
import io.github.caolib.utils.BeanUtils;
import io.github.caolib.utils.CollUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommodityServiceImpl extends ServiceImpl<CommodityMapper, Commodity> implements ICommodityService {

    private final CommodityMapper commodityMapper;

    @Override
    @Cacheable(value = Cache.COMMODITY_LIST, key = "#q")
    public PageDTO<CommodityDTO> pageQuery(SearchQuery q) {
        String key = q.getKey();
        String brand = q.getBrand();
        String category = q.getCategory();
        Integer minPrice = q.getMinPrice();
        Integer maxPrice = q.getMaxPrice();
        // 分页查询
        Page<Commodity> result = lambdaQuery()
                .like(StrUtil.isNotBlank(key), Commodity::getName, key)
                .eq(StrUtil.isNotBlank(brand), Commodity::getBrand, brand)
                .eq(StrUtil.isNotBlank(category), Commodity::getCategory, category)
                .eq(Commodity::getStatus, 1)
                .ge(minPrice != null, Commodity::getPrice, minPrice)
                .le(maxPrice != null, Commodity::getPrice, maxPrice)
                .page(q.toMpPage(q.getSortBy(), q.getIsAsc()));
        // 封装并返回
        return PageDTO.of(result, CommodityDTO.class);
    }


    /**
     * 扣减库存
     *
     * @param items 商品列表
     */
    @Override
    @Transactional
    @CacheEvict(value = {Cache.COMMODITY_LIST, Cache.COMMODITY_IDS}, allEntries = true)
    public R<Void> deductStock(List<OrderDetailDTO> items) {
        // TODO 待测试
        items.forEach(item -> {
            Commodity commodity = getById(item.getItemId()); // 查询商品
            if (commodity == null || commodity.getStock() < item.getNum()) {  // 库存不足
                throw new BizIllegalException("库存不足！");
            }
            commodityMapper.updateStock(item);
        });
        return R.ok();
    }

    /**
     * 根据id批量查询商品
     *
     * @param ids 商品id集合
     */
    @Override
    @Cacheable(value = Cache.COMMODITY_IDS, key = "#ids")
    public List<CommodityDTO> getItemByIds(Collection<Long> ids) {
        if (CollUtils.isEmpty(ids))
            return List.of();
        return BeanUtils.copyList(listByIds(ids), CommodityDTO.class);
    }


    /**
     * 恢复库存
     *
     * @param dtos 商品列表
     */
    @Override
    @CacheEvict(value = {Cache.COMMODITY_LIST, Cache.COMMODITY_IDS}, allEntries = true)
    public void releaseStock(List<OrderDetailDTO> dtos) {
        dtos.forEach(dto -> commodityMapper.recover(dto.getItemId(), dto.getNum()));
    }


}

package io.github.caolib.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.caolib.domain.PageDTO;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.CommodityDTO;
import io.github.caolib.domain.dto.OrderDetailDTO;
import io.github.caolib.domain.po.Commodity;
import io.github.caolib.domain.query.CommodityPageQuery;
import io.github.caolib.exception.BizIllegalException;
import io.github.caolib.mapper.CommodityMapper;
import io.github.caolib.service.ICommodityService;
import io.github.caolib.utils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class CommodityServiceImpl extends ServiceImpl<CommodityMapper, Commodity> implements ICommodityService {

    @Override
    @Transactional
    public R<Void> deductStock(List<OrderDetailDTO> items) {
        // TODO 不使用字符串拼接
        String sqlStatement = "io.github.caolib.mapper.CommodityMapper.updateStock";
        boolean r;
        try {
            r = executeBatch(items, (sqlSession, entity) -> sqlSession.update(sqlStatement, entity));
        } catch (Exception e) {
            throw new BizIllegalException("更新库存异常，可能是库存不足!", e);
        }
        if (!r) {
            throw new BizIllegalException("库存不足！");
        }
        return R.ok();
    }

    @Override
    public List<CommodityDTO> queryItemByIds(Collection<Long> ids) {
        return BeanUtils.copyList(listByIds(ids), CommodityDTO.class);
    }

    @Override
    public PageDTO<CommodityDTO> pageQuery(CommodityPageQuery query) {
        // 分页查询
         Page<Commodity> result = lambdaQuery()
                .like(StrUtil.isNotBlank(query.getKey()), Commodity::getName, query.getKey())
                .eq(StrUtil.isNotBlank(query.getBrand()), Commodity::getBrand, query.getBrand())
                .eq(StrUtil.isNotBlank(query.getCategory()), Commodity::getCategory, query.getCategory())
                .eq(Commodity::getStatus, 1)
                .ge(query.getMinPrice() != null, Commodity::getPrice, query.getMinPrice())
                .le(query.getMaxPrice() != null, Commodity::getPrice, query.getMaxPrice())
                .page(query.toMpPage("update_time", false));
        // 封装并返回
        return PageDTO.of(result, CommodityDTO.class);
    }


}

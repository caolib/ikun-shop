package io.github.caolib.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.caolib.domain.PageDTO;
import io.github.caolib.domain.dto.CommodityDTO;
import io.github.caolib.domain.po.Commodity;
import io.github.caolib.domain.query.CommodityPageQuery;
import io.github.caolib.service.ICommodityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "搜索相关接口")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final ICommodityService itemService;

    @ApiOperation("搜索商品")
    @GetMapping("/list")
    public PageDTO<CommodityDTO> search(CommodityPageQuery query) {
        // 分页查询
        Page<Commodity> result = itemService.lambdaQuery()
                .like(StrUtil.isNotBlank(query.getKey()), Commodity::getName, query.getKey())
                .eq(StrUtil.isNotBlank(query.getBrand()), Commodity::getBrand, query.getBrand())
                .eq(StrUtil.isNotBlank(query.getCategory()), Commodity::getCategory, query.getCategory())
                .eq(Commodity::getStatus, 1)
                .between(query.getMaxPrice() != null, Commodity::getPrice, query.getMinPrice(), query.getMaxPrice())
                .page(query.toMpPage("update_time", false));
        // 封装并返回
        return PageDTO.of(result, CommodityDTO.class);
    }
}

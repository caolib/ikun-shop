package io.github.caolib.controller;

import io.github.caolib.domain.PageDTO;
import io.github.caolib.domain.dto.CommodityDTO;
import io.github.caolib.domain.query.CommodityPageQuery;
import io.github.caolib.service.ICommodityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 搜索相关接口
 */
@Slf4j
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final ICommodityService commodityService;

    /**
     * 搜索商品
     *
     * @param query 商品分页查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public PageDTO<CommodityDTO> search(CommodityPageQuery query) {
        //log.debug("搜索条件: {}", query);
        return commodityService.pageQuery(query);
    }
}
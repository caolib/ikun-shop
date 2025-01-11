package io.github.caolib.domain.query;

import io.github.caolib.domain.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品分页查询条件
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommodityPageQuery extends PageQuery {
    /**
     * 搜索关键字
     */
    private String key;
    /**
     * 商品分类
     */
    private String category;
    /**
     * 商品品牌
     */
    private String brand;
    /**
     * 价格最小值
     */
    private Integer minPrice;
    /**
     * 价格最大值
     */
    private Integer maxPrice;
}
package io.github.caolib.domain.query;

import io.github.caolib.domain.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 商品分页查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommodityPageQuery extends PageQuery implements Serializable {
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
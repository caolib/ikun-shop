package io.github.caolib.domain.dto;

import lombok.Data;

/**
 * 商品数据传输对象
 */
@Data
public class CommodityDTO {
    /**
     * 商品ID
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 价格（分）
     */
    private Integer price;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 类目名称
     */
    private String category;

    /**
     * 品牌名称
     */
    private String brand;

    /**
     * 规格
     */
    private String spec;

    /**
     * 销量
     */
    private Integer sold;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 是否是推广广告，true/false
     */
    private Boolean isAD;

    /**
     * 商品状态 1-正常，2-下架，3-删除
     */
    private Integer status;
}
package io.github.caolib.domain.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serial;
import java.io.Serializable;

/**
 * 商品实体
 */
@Data
public class CommodityDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 商品id
     */
    private Long id;
    /**
     * SKU名称
     */
    private String name;
    /**
     * 价格（分）
     */
    @Min(0)
    private Integer price;
    /**
     * 库存数量
     */
    @Min(0)
    private Integer stock;
    /**
     * 商品图片
     */
    private String image;
    /**
     * 分类
     */
    private String category;
    /**
     * 品牌
     */
    private String brand;
    /**
     * 规格
     */
    private String spec;
    /**
     * 销量
     */
    @Min(0)
    private Integer sold;
    /**
     * 评论数
     */
    @Min(0)
    private Integer commentCount;
    /**
     * 商品状态 1-正常，2-下架，3-删除
     */
    private Integer status;
}
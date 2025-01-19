package io.github.caolib.domain.dto;


import lombok.Data;

/**
 * 新增购物车商品表单实体
 */
@Data
public class CartFormDTO {
    /**
     * 商品id
     */
    private Long itemId;
    /**
     * 商品名
     */
    private String name;
    /**
     * 商品规格，颜色、尺寸等，使用json格式存储
     */
    private String spec;
    /**
     * 价格,单位：分
     */
    private Integer price;
    /**
     * 商品图片
     */
    private String image;
}
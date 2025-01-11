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
     * 商品标题
     */
    private String name;
    /**
     * 商品动态属性键值集
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
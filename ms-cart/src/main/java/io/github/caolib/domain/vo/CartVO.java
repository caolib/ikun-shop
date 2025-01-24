package io.github.caolib.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单详情表
 */
@Data
public class CartVO {
    /**
     * 购物车记录id
     */
    private Long id;
    /**
     * sku商品id
     */
    private Long itemId;
    /**
     * 购买数量
     */
    private Integer num;
    /**
     * 商品标题
     */
    private String name;
    /**
     * 商品规格
     */
    private String spec;
    /**
     * 价格,单位：元
     */
    private Integer price;
    /**
     * 商品最新价格
     */
    private Integer newPrice;
    /**
     * 商品最新状态
     */
    private Integer status = 1;
    /**
     * 商品最新库存
     */
    private Integer stock = 10;
    /**
     * 商品图片
     */
    private String image;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
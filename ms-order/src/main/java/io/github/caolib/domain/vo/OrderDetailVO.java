package io.github.caolib.domain.vo;

import io.github.caolib.domain.po.OrderDetail;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderDetailVO {
    // 订单id
    private Long id;
    // 用户名
    private String username;
    // 总金额，单位为分
    private Integer totalFee;
    // 商品列表
    private List<OrderDetail> orderDetails;
    // 状态
    private String status;
    // 创建时间
    private LocalDateTime createTime;
    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 发货时间
     */
    private LocalDateTime consignTime;

    /**
     * 交易完成时间
     */
    private LocalDateTime endTime;

    /**
     * 交易关闭时间
     */
    private LocalDateTime closeTime;

    /**
     * 评价时间
     */
    private LocalDateTime commentTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

package io.github.caolib.domain.vo;

import io.github.caolib.domain.po.OrderDetail;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO2 {
    // 订单id
    private Long id;
    // 总金额，单位为分
    private Integer totalFee;
    // 商品列表
    private List<OrderDetail> orderDetails;
    // 状态
    private String status;
    // 创建时间
    private LocalDateTime createTime;
}

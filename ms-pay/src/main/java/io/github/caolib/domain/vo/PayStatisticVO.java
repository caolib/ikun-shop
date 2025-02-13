package io.github.caolib.domain.vo;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 支付统计VO
 */
@Data
@Builder
public class PayStatisticVO {
    private Long totalFee; // 总金额
    private Long totalOrder; // 总订单数
    private LocalDateTime time; // 时间
}

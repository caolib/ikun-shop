package io.github.caolib.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

@Data
@Accessors(chain = true)
public class PayDetailResVO {
    // 日期
    private LocalDate date;
    // 商品销售详情列表
    private List<PayDetailVO> payDetailList;
}

package io.github.caolib.controller.admin;

import io.github.caolib.domain.R;
import io.github.caolib.domain.vo.PayDetailResVO;
import io.github.caolib.domain.vo.PayStatisticVO;
import io.github.caolib.service.IPayOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 支付管理
 */
@RestController
@RequestMapping("/pays/manage")
@RequiredArgsConstructor
public class PayManageController {
    private final IPayOrderService payOrderService;


    /**
     * 近一个星期的支付统计
     */
    @GetMapping
    public R<List<PayStatisticVO>> weekStatistic(@RequestParam int days) {
        return payOrderService.dayStatistic(days);
    }

    /**
     * 一段时间内的商品销量详情
     */
    @GetMapping("/pay-detail")
    public R<List<PayDetailResVO>> payDetail(String durationStart, String durationEnd) {
        // 将字符串转换为 LocalDateTime
        LocalDate start = LocalDate.parse(durationStart);
        LocalDate end = LocalDate.parse(durationEnd);

        return payOrderService.payDetail(start, end);
    }

}

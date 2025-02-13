package io.github.caolib.controller.admin;

import io.github.caolib.domain.R;
import io.github.caolib.domain.vo.PayStatisticVO;
import io.github.caolib.service.IPayOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public R<List<PayStatisticVO>> weekStatistic() {
        return payOrderService.weekStatistic();
    }

}

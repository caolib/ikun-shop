package io.github.caolib.service;


import com.baomidou.mybatisplus.extension.service.IService;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.PayFormDTO;
import io.github.caolib.domain.dto.PayOrderFormDTO;
import io.github.caolib.domain.po.PayOrder;
import io.github.caolib.domain.vo.PayDetailResVO;
import io.github.caolib.domain.vo.PayOrderVO;
import io.github.caolib.domain.vo.PayStatisticVO;

import java.time.LocalDate;
import java.util.List;

public interface IPayOrderService extends IService<PayOrder> {
    R<PayOrder> createPayOrder(PayFormDTO applyDTO);

    void payOrderByBalance(PayOrderFormDTO payOrderFormDTO);

    PayOrderVO getPayOrderId(Long bizOrderId);

    void deleteByUserId(Long userId);

    List<PayOrderVO> getUserPayOrders(Long userId);

    void cancelPayOrder(Long payOrderId);

    R<List<PayStatisticVO>> dayStatistic(int days);

    R<List<PayDetailResVO>> payDetail(LocalDate durationStart, LocalDate durationEnd);
}

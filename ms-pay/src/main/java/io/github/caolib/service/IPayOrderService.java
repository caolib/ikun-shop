package io.github.caolib.service;


import com.baomidou.mybatisplus.extension.service.IService;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.PayApplyDTO;
import io.github.caolib.domain.dto.PayOrderFormDTO;
import io.github.caolib.domain.po.PayOrder;
import io.github.caolib.domain.vo.PayOrderVO;

import java.util.List;

public interface IPayOrderService extends IService<PayOrder> {

    String applyPayOrder(PayApplyDTO applyDTO);

    R<PayOrder> createPayOrder(PayApplyDTO applyDTO);

    void payOrderByBalance(PayOrderFormDTO payOrderFormDTO);

    PayOrderVO getPayOrderId(Long bizOrderId);

    void deleteByUserId(Long userId);

    List<PayOrderVO> getUserPayOrders();

    void cancelPayOrder(Long payOrderId);
}

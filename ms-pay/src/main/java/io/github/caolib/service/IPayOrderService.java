package io.github.caolib.service;


import com.baomidou.mybatisplus.extension.service.IService;
import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.PayApplyDTO;
import io.github.caolib.domain.dto.PayOrderFormDTO;
import io.github.caolib.domain.po.PayOrder;

public interface IPayOrderService extends IService<PayOrder> {

    String applyPayOrder(PayApplyDTO applyDTO);

    R<PayOrder> createPayOrder(PayApplyDTO applyDTO);

    void tryPayOrderByBalance(PayOrderFormDTO payOrderFormDTO);

    R<String> getPayOrderId(Long bizOrderId);
}

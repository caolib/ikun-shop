package io.github.caolib.controller;

import io.github.caolib.domain.R;
import io.github.caolib.domain.dto.PayApplyDTO;
import io.github.caolib.domain.dto.PayOrderFormDTO;
import io.github.caolib.domain.po.PayOrder;
import io.github.caolib.domain.vo.PayOrderVO;
import io.github.caolib.enums.PayType;
import io.github.caolib.exception.BizIllegalException;
import io.github.caolib.service.IPayOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 支付相关接口
 */
@RestController
@RequestMapping("pays")
@RequiredArgsConstructor
public class PayController {

    private final IPayOrderService payOrderService;

    /**
     * 查询用户支付单
     */
    @GetMapping
    public List<PayOrderVO> queryPayOrders() {
        return payOrderService.getUserPayOrders();
    }

    /**
     * 生成支付单
     *
     * @param applyDTO 支付申请数据传输对象
     * @return 返回支付单记录号
     */
    @PostMapping
    public R<PayOrder> applyPayOrder(@RequestBody PayApplyDTO applyDTO) {
        if (!PayType.BALANCE.equalsValue(applyDTO.getPayType())) {
            throw new BizIllegalException("目前只支持余额支付");
        }
        return payOrderService.createPayOrder(applyDTO);
    }

    /**
     * 使用用户余额支付
     *
     * @param id  支付单记录id
     * @param dto 支付订单表单数据传输对象
     */
    @PostMapping("/{id}")
    public R<Void> tryPayOrderByBalance(@PathVariable Long id, @RequestBody PayOrderFormDTO dto) {
        dto.setId(id);
        payOrderService.payOrderByBalance(dto);
        return R.ok();
    }

    /**
     * 根据业务订单id查询支付单id
     * @param bizOrderId 业务订单id
     * @return 支付单id
     */
    @GetMapping("/{bizOrderId}")
    public R<String> getPayOrderIdByBizOrderId(@PathVariable Long bizOrderId) {
        return payOrderService.getPayOrderId(bizOrderId);
    }
}
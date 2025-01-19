package io.github.caolib.controller;

import io.github.caolib.domain.dto.PayApplyDTO;
import io.github.caolib.domain.dto.PayOrderFormDTO;
import io.github.caolib.domain.vo.PayOrderVO;
import io.github.caolib.enums.PayType;
import io.github.caolib.exception.BizIllegalException;
import io.github.caolib.service.IPayOrderService;
import io.github.caolib.utils.BeanUtils;
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
     * 查询支付单
     * @return 支付单列表
     */
    @GetMapping
    public List<PayOrderVO> queryPayOrders(){
        return BeanUtils.copyList(payOrderService.list(), PayOrderVO.class);
    }

    /**
     * 生成支付单
     * @param applyDTO 支付申请数据传输对象
     * @return 支付单号
     */
    @PostMapping
    public String applyPayOrder(@RequestBody PayApplyDTO applyDTO){
        if(!PayType.BALANCE.equalsValue(applyDTO.getPayType())){
            // 目前只支持余额支付
            throw new BizIllegalException("抱歉，目前只支持余额支付");
        }
        return payOrderService.applyPayOrder(applyDTO);
    }

    /**
     * 使用用户余额支付
     * @param id 支付单id
     * @param payOrderFormDTO 支付订单表单数据传输对象
     */
    @PostMapping("{id}")
    public void tryPayOrderByBalance(@PathVariable("id") Long id, @RequestBody PayOrderFormDTO payOrderFormDTO){
        payOrderFormDTO.setId(id);
        payOrderService.tryPayOrderByBalance(payOrderFormDTO);
    }
}
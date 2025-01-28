package io.github.caolib.enums;

public class Q {
    // 交换机
    public static final String PAY_EXCHANGE = "pay.direct"; // 支付交换机
    public static final String PAY_DELAY_EXCHANGE = "pay.delay.direct"; // 支付交换机

    // 队列
    public static final String PAY_SUCCESS_Q = "order.pay.success.queue"; // 订单支付成功
    public static final String PAY_ORDER_DELETE_Q = "order.pay.delete.queue"; // 删除订单
    public static final String PAY_DELAY_Q = "order.pay.delay.queue"; // 订单延迟队列


    // 路由键
    public static final String PAY_SUCCESS_KEY = "pay.success";
    public static final String PAY_DELETE_KEY = "pay.delete";
    public static final String PAY_DELAY_KEY = "pay.delay";

}

package com.seepine.pay.entity.payjs;

import lombok.Data;

/**
 * @author seepine
 */
@Data
public class PayJsEntity {
    /**
     * 1:请求成功，0:请求失败
     */
    int return_code;
    /**
     * PAYJS 平台订单号
     */
    String payjs_order_id;
    /**
     * 用户生成的订单号原样返回
     */
    String out_trade_no;
    /**
     * 金额。单位：分
     */
    int total_fee;
}

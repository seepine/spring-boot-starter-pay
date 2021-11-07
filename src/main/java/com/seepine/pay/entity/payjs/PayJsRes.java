package com.seepine.pay.entity.payjs;

import lombok.Data;

/**
 * @author seepine
 */
@Data
public class PayJsRes extends PayJsEntity {
    /**
     * 返回消息
     */
    String return_msg;
    /**
     * 二维码内容(有效期2小时)
     */
    String code_url;
    /**
     * 二维码图片地址
     */
    String qrcode;
    /**
     * 数据签名 详见签名算法
     */
    String sign;
    /**
     * 0：未支付，1：支付成功
     */
    Integer status;
    /**
     * check:微信显示订单号
     */
    String transaction_id;
    /**
     * check:PAYJS 平台商户号
     */
    String mchid;
    /**
     * check:用户 OPENID
     */
    String openid;
    /**
     * check:订单支付时间
     */
    String paid_time;
    /**
     * check:用户自定义数据
     */
    String attach;
}

package com.seepine.pay.entity.payjs;

import lombok.Data;

/**
 * @author seepine
 */
@Data
public class PayJsReq extends PayJsEntity {
    /**
     * 微信用户手机显示订单号
     */
    private String transaction_id;
    /**
     * 支付成功时间
     */
    private String time_end;
    /**
     * 用户OPENID标示，本参数没有实际意义，旨在方便用户端区分不同用户
     */
    private String openid;
    /**
     * 用户自定义数据
     */
    private String attach;
    /**
     * 商户号
     */
    private String mchid;
    /**
     * 支付类型。微信订单不返回该字段；支付宝订单返回：alipay
     */
    private String type;
    /**
     * 数据签名
     */
    private String sign;
}

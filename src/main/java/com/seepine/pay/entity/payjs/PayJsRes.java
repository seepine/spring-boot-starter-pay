package com.seepine.pay.entity.payjs;

import lombok.Data;

/**
 * @author seepine
 */
@Data
public class PayJsRes extends PayJsEntity{
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
}

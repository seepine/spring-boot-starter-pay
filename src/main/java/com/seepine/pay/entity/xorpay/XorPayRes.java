package com.seepine.pay.entity.xorpay;

import lombok.Data;

@Data
public class XorPayRes {
  String status;
  String aoid;
  int expire_in;
  String qr;
  /** 平台自身id */
  String order_id;
  /** 价格 */
  Double pay_price;
  // detail
  /** 渠道流水号 */
  String transaction_id;
  /** 用户付款方式 */
  String bank_type;
  /** 消费者 */
  String buyer;
}

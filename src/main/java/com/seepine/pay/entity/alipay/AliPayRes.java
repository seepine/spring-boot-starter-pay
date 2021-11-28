package com.seepine.pay.entity.alipay;

import lombok.Data;

import java.time.LocalDateTime;

/** @author seepine */
@Data
public class AliPayRes {
  LocalDateTime notify_time;
  String notify_type;
  String notify_id;
  String sign_type;
  String sign;
  String trade_no;
  String app_id;
  String out_trade_no;
  String buyer_id;
  /** WAIT_BUYER_PAY,TRADE_CLOSED,TRADE_SUCCESS,TRADE_FINISHED */
  String trade_status;

  Double total_amount;
  Double receipt_amount;
  Double invoice_amount;
  Double buyer_pay_amount;
  String subject;
  LocalDateTime gmt_create;
  LocalDateTime gmt_payment;

  /** 是否验签通过 */
  Boolean signVerified;
  /** 寻找到的渠道号 */
  Integer channel;
}

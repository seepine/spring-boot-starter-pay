package com.seepine.pay.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum XorPayType {
  NATIVE("native"),
  JSAPI("jsapi"),
  WECHAT_BARCODE("wechat_barcode"),
  ALIPAY_BARCODE("alipay_barcode");
  public final String value;
}

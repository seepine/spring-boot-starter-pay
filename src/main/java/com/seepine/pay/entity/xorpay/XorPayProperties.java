package com.seepine.pay.entity.xorpay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** @author seepine */
@Data
@ConfigurationProperties(prefix = "xor-pay")
public class XorPayProperties {
  /** 商户id */
  String[] aid;
  /** 密钥 */
  String[] secret;
  /** 异步通知地址 */
  String[] notifyUrl;
}

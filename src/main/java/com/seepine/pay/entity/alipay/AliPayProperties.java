package com.seepine.pay.entity.alipay;

import lombok.Data;

/** @author seepine */
@Data
public class AliPayProperties {
  /** 网关地址 */
  String serverUrl;
  /** 开放平台的应用appId */
  String appId;
  /** 应用私钥，由example.com_私钥.txt所得 */
  String appPrivateKeyPath;
  /** 阿里公共证书路径，alipayCertPublicKey_RSA2.crt */
  String alipayPublicCertPath;
  /** 阿里公共证书，alipayCertPublicKey_RSA2.crt中的内容 */
  String alipayPublicCert;
  /** 阿里根证书路径，alipayRootCert.crt */
  String alipayRootCertPath;
  /** 应用公共证书，appCertPublicKey_2021002196659839.crt */
  String appCertPublicKeyPath;
  /** 回调地址 */
  String notifyUrl;

  String charset = "utf-8";
  String format = "json";
  String signType = "RSA2";
}

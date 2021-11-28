package com.seepine.pay.entity.alipay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 对接步骤 1.支付宝开放平台开发助手生成crs文件 - example.com.csr - example.com_公钥.txt - example.com_私钥.txt
 * 2.上传example.com.csr至开放平台获得 - alipayCertPublicKey_RSA2.crt - alipayRootCert.crt -
 * appCertPublicKey_2021002196659839.crt
 *
 * @author seepine
 */
@Data
@ConfigurationProperties(prefix = "ali-pay")
public class AliPayAutoProperties {
    /** 网关地址 */
    String[] serverUrl;
    /** 开放平台的应用appId */
    String[] appId;
    /** 应用私钥，由example.com_私钥.txt所得 */
    String[] appPrivateKeyPath;
    /** 阿里公共证书路径，alipayCertPublicKey_RSA2.crt */
    String[] alipayPublicCertPath;
    /** 阿里根证书路径，alipayRootCert.crt */
    String[] alipayRootCertPath;
    /** 应用公共证书，appCertPublicKey_2021002196659839.crt */
    String[] appCertPublicKeyPath;
    /** 回调地址 */
    String[] notifyUrl;

    String charset = "utf-8";
    String format = "json";
    String signType = "RSA2";
}

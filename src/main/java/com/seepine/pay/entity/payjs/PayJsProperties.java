package com.seepine.pay.entity.payjs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author seepine
 */
@Data
@ConfigurationProperties(prefix = "pay-js")
public class PayJsProperties {
    /**
     * 商户id
     */
    String[] mChId;
    /**
     * 密钥
     */
    String[] secret;
    /**
     * 异步通知地址
     */
    String[] notifyUrl;
}

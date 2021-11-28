package com.seepine.pay.config;

import com.seepine.pay.entity.alipay.AliPayAutoProperties;
import com.seepine.pay.entity.alipay.AliPayProperties;
import com.seepine.pay.entity.payjs.PayJsProperties;
import com.seepine.pay.service.AliPayTemplate;
import com.seepine.pay.service.PayJsTemplate;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** @author seepine */
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({PayJsProperties.class, AliPayAutoProperties.class})
public class PayAutoConfigurer {
  private final PayJsProperties payJsProperties;
  private final AliPayAutoProperties aliPayAutoProperties;

  @Bean
  @ConditionalOnMissingBean(PayJsProperties.class)
  public PayJsTemplate payJsTemplate() {
    return new PayJsTemplate(payJsProperties);
  }

  @Bean
  @ConditionalOnMissingBean(AliPayAutoProperties.class)
  public AliPayTemplate aliPayTemplate() {
    return new AliPayTemplate(aliPayAutoProperties);
  }
}

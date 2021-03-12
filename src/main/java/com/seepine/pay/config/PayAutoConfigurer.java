package com.seepine.pay.config;

import com.seepine.pay.entity.payjs.PayJsProperties;
import com.seepine.pay.service.PayJsTemplate;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author seepine
 */
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({PayJsProperties.class})
public class PayAutoConfigurer {
    private final PayJsProperties payJsProperties;

    @Bean
    @ConditionalOnMissingBean(PayJsProperties.class)
    public PayJsTemplate payJsTemplate() {
        return new PayJsTemplate(payJsProperties);
    }
}

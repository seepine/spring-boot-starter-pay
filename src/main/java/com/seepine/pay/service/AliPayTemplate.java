package com.seepine.pay.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.internal.util.file.IOUtils;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.seepine.http.util.StrUtil;
import com.seepine.pay.entity.alipay.AliPayAutoProperties;
import com.seepine.pay.entity.alipay.AliPayProperties;
import com.seepine.pay.entity.alipay.AliPayRes;
import com.seepine.pay.exception.PayException;
import com.seepine.pay.util.SignUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/** @author seepine */
@Slf4j
@RequiredArgsConstructor
public class AliPayTemplate implements InitializingBean {
  private final AliPayAutoProperties aliPayAutoProperties;

  private AlipayClient[] alipayClients;
  private AliPayProperties[] aliPayProperties;

  private AlipayClient alipayClient(AliPayProperties aliPayProperties) throws AlipayApiException {

    PropertyMapper propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();

    CertAlipayRequest certAlipayRequest = new CertAlipayRequest();
    propertyMapper.from(aliPayProperties.getServerUrl()).to(certAlipayRequest::setServerUrl);
    propertyMapper.from(aliPayProperties.getAppId()).to(certAlipayRequest::setAppId);
    propertyMapper
        .from(aliPayProperties.getAppPrivateKeyPath())
        .as(this::loadFile)
        .to(certAlipayRequest::setPrivateKey);

    propertyMapper.from(aliPayProperties::getFormat).to(certAlipayRequest::setFormat);
    propertyMapper.from(aliPayProperties::getCharset).to(certAlipayRequest::setCharset);
    propertyMapper.from(aliPayProperties::getSignType).to(certAlipayRequest::setSignType);

    propertyMapper
        .from(aliPayProperties.getAppCertPublicKeyPath())
        .as(this::loadFile)
        .to(certAlipayRequest::setCertContent);
    propertyMapper
        .from(aliPayProperties.getAlipayPublicCertPath())
        .as(this::loadFile)
        .to(certAlipayRequest::setAlipayPublicCertContent);
    propertyMapper
        .from(aliPayProperties.getAlipayRootCertPath())
        .as(this::loadFile)
        .to(certAlipayRequest::setRootCertContent);
    return new DefaultAlipayClient(certAlipayRequest);
  }

  private String loadFile(String classPath) {
    ClassPathResource resource = new ClassPathResource(classPath);
    try (InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream())) {
      return IOUtils.toString(inputStreamReader);
    } catch (IOException e) {
      log.error("ali pay app private key is required ,{}", e.getMessage());
      throw new PayException("ali pay app private key is required");
    }
  }

  private AliPayProperties aliPayProperties(int index, AliPayAutoProperties aliPayAutoProperties)
      throws AlipayApiException {
    AliPayProperties properties = new AliPayProperties();
    properties.setAppId(aliPayAutoProperties.getAppId()[index]);
    properties.setAlipayPublicCertPath(aliPayAutoProperties.getAlipayPublicCertPath()[index]);
    properties.setAlipayPublicCert(
        SignUtil.getAlipayPublicKey(properties.getAlipayPublicCertPath()));
    properties.setAlipayRootCertPath(aliPayAutoProperties.getAlipayRootCertPath()[index]);
    properties.setAppPrivateKeyPath(aliPayAutoProperties.getAppPrivateKeyPath()[index]);
    properties.setServerUrl(aliPayAutoProperties.getServerUrl()[index]);
    properties.setAppCertPublicKeyPath(aliPayAutoProperties.getAppCertPublicKeyPath()[index]);
    properties.setNotifyUrl(aliPayAutoProperties.getNotifyUrl()[index]);
    return properties;
  }

  @Override
  public void afterPropertiesSet() throws AlipayApiException {
    log.info("afterPropertiesSet AliPayTemplate");
    log.info("finish properties : {}", aliPayAutoProperties.toString());
    if (aliPayAutoProperties.getAppId() == null) {
      return;
    }
    alipayClients = new AlipayClient[aliPayAutoProperties.getAppId().length];
    aliPayProperties = new AliPayProperties[aliPayAutoProperties.getAppId().length];
    for (int i = 0; i < aliPayAutoProperties.getAppId().length; i++) {
      aliPayProperties[i] = aliPayProperties(i, aliPayAutoProperties);
      alipayClients[i] = alipayClient(aliPayProperties[i]);
    }
  }

  public Channel channel() {
    return channel(0);
  }

  public Channel channel(int channel) {
    return new Channel(alipayClients[channel], aliPayProperties[0]);
  }

  public static class Channel {
    private final AliPayProperties properties;
    private final AlipayClient alipayClient;

    private Channel(AlipayClient alipayClient, AliPayProperties properties) {
      this.alipayClient = alipayClient;
      this.properties = properties;
    }

    /**
     * 直接获取alipayClient实例
     *
     * @return AlipayClient
     */
    public AlipayClient alipayClient() {
      return this.alipayClient;
    }

    /**
     * 当面付预下单
     *
     * @param subject 商品名称
     * @param outTradeNo 商户订单号
     * @param amount 金额(元)
     * @return 二维码内容，需要将内容生成一张二维码图片
     */
    public String tradePreCreate(String subject, String outTradeNo, Double amount) {
      return tradePreCreate(subject, outTradeNo, amount, null);
    }

    /**
     * 当面付预下单
     *
     * @param subject 商品名称
     * @param outTradeNo 商户订单号
     * @param amount 金额(元)
     * @param timeoutExpress 过期时间，如8m，表示8分钟过期，为null则默认两小时
     * @return 二维码内容，需要将内容生成一张二维码图片
     */
    public String tradePreCreate(
        String subject, String outTradeNo, Double amount, String timeoutExpress) {
      AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
      request.setNotifyUrl(properties.getNotifyUrl());
      JSONObject bodyReq = new JSONObject();
      bodyReq.put("out_trade_no", outTradeNo);
      bodyReq.put("total_amount", String.format("%.2f", amount));
      bodyReq.put("subject", subject);
      if (timeoutExpress != null) {
        bodyReq.put("timeout_express", timeoutExpress);
      }
      request.setBizContent(bodyReq.toString());
      AlipayTradePrecreateResponse execute;
      try {
        execute = alipayClient.certificateExecute(request);
      } catch (AlipayApiException e) {
        e.printStackTrace();
        throw new PayException(e.getMessage());
      }
      if (!execute.isSuccess()) {
        throw new PayException(execute.getBody());
      }
      String qrCode = execute.getQrCode();
      if (StrUtil.isBlank(qrCode)) {
        throw new PayException(execute.getBody());
      }
      return qrCode;
    }

    /**
     * 支付宝异步通知验签和解析
     *
     * @param requestParams requestParams
     * @return AliPayRes
     */
    public AliPayRes notify(Map<String, String[]> requestParams) {
      Map<String, String> parameters = new HashMap<>(requestParams.size());
      for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
        String key = entry.getKey();
        String[] values = entry.getValue();
        String valueStr = "";
        for (int i = 0; i < values.length; i++) {
          valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
        }
        parameters.put(key, valueStr);
      }
      log.debug("parameters is [parameters={}]", parameters);
      boolean signVerified;
      try {
        signVerified =
            AlipaySignature.rsaCheckV1(
                parameters,
                properties.getAlipayPublicCert(),
                properties.getCharset(),
                properties.getSignType());
        AliPayRes res = JSON.parseObject(JSON.toJSONString(parameters), AliPayRes.class);
        if (!properties.getAppId().equals(res.getApp_id())) {
          log.error("appId不符合，本地{}，回调传来的{}", properties.getAppId(), res.getApp_id());
          throw new PayException("appId不符合");
        }
        res.setSignVerified(signVerified);
        return res;
      } catch (AlipayApiException e) {
        throw new PayException(e.getMessage());
      }
    }
  }
}

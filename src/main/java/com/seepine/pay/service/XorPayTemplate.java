package com.seepine.pay.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seepine.http.util.StrUtil;
import com.seepine.pay.entity.xorpay.XorPayProperties;
import com.seepine.pay.entity.xorpay.XorPayRes;
import com.seepine.pay.enums.XorPayType;
import com.seepine.pay.exception.PayException;
import com.seepine.pay.util.SignUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** @author seepine */
@Slf4j
@RequiredArgsConstructor
public class XorPayTemplate implements InitializingBean {
  private final XorPayProperties xorPayProperties;

  @Override
  public void afterPropertiesSet() {
    log.info("afterPropertiesSet XorPayTemplate");
    log.info("finish properties : {}", xorPayProperties.toString());
  }

  public Integer getChannel(String mChId) {
    for (int i = 0; i < xorPayProperties.getAid().length; i++) {
      if (xorPayProperties.getAid()[i].equals(mChId)) {
        return i;
      }
    }
    return null;
  }

  public Channel channel() {
    return Channel.build(0, xorPayProperties);
  }

  public Channel channel(int channel) {
    return Channel.build(channel, xorPayProperties);
  }

  public static class Channel {
    private final int channel;
    private final XorPayProperties xorPayProperties;

    private Channel(int channel, XorPayProperties xorPayProperties) {
      this.channel = channel;
      this.xorPayProperties = xorPayProperties;
    }

    public static Channel build(int channel, XorPayProperties xorPayProperties) {
      return new Channel(channel, xorPayProperties);
    }

    /**
     * native下单
     *
     * @param name 标题
     * @param outTradeNo 订单号（已方自己生成）
     * @param amount 金额（单位元）
     * @return PayJsRes
     * @throws IOException IOException
     * @throws PayException PayException
     */
    public XorPayRes pay(
        String name, String outTradeNo, XorPayType payType, int expire, Double amount)
        throws IOException {
      return pay(
          name, outTradeNo, payType, expire, amount, xorPayProperties.getNotifyUrl()[channel]);
    }

    /**
     * native下单
     *
     * @param name 标题
     * @param outTradeNo 订单号（已方自己生成）
     * @param amount 金额（单位元）
     * @param notifyUrl 异步通知回调地址
     * @return PayJsRes
     * @throws IOException IOException
     */
    public XorPayRes pay(
        String name,
        String outTradeNo,
        XorPayType payType,
        int expire,
        Double amount,
        String notifyUrl)
        throws IOException {
      Map<String, Object> payData = new HashMap<>(7);
      payData.put("name", name);
      payData.put("pay_type", payType.value);
      payData.put("price", String.format("%.2f", amount));
      payData.put("order_id", outTradeNo);
      payData.put("notify_url", notifyUrl);
      payData.put("expire", String.valueOf(expire));
      payData.put(
          "sign",
          SignUtil.md5(
              name
                  + payType.value
                  + payData.get("price")
                  + outTradeNo
                  + notifyUrl
                  + xorPayProperties.getSecret()[channel]));
      HttpRequest req =
          cn.hutool.http.HttpUtil.createPost(
                  "https://xorpay.com/api/cashier/" + xorPayProperties.getAid()[channel])
              .form(payData);
      HttpResponse res = req.execute();
      if (res.getStatus() == 302) {
        XorPayRes payJsRes = new XorPayRes();
        if (StrUtil.isNotBlank(res.header("Location"))) {
          payJsRes.setStatus("ok");
          payJsRes.setQr("https://xorpay.com" + res.header("Location"));
          return payJsRes;
        }
        payJsRes.setStatus("fail");
        return payJsRes;
      }
      String result = res.body();
      JSONObject obj = JSON.parseObject(result);
      XorPayRes payJsRes = obj.toJavaObject(XorPayRes.class);
      if (obj.getString("info") != null) {
        payJsRes.setQr(obj.getJSONObject("info").getString("qr"));
      }
      return payJsRes;
    }

    public XorPayRes notify(Map<String, String[]> requestParams) {
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
      try {
        // aoid + order_id + pay_price + pay_time + app secret
        String sign =
            SignUtil.md5(
                parameters.get("aoid")
                    + parameters.get("order_id")
                    + parameters.get("pay_price")
                    + parameters.get("pay_time")
                    + xorPayProperties.getSecret()[channel]);
        if (!sign.equals(parameters.get("sign"))) {
          log.error("sign fail,get:{},gen:{}", parameters.get("sign"), sign);
          log.debug("parameters is [parameters={}]", parameters);
          throw new PayException("sign fail");
        }
        XorPayRes res = new XorPayRes();
        res.setOrder_id(parameters.get("order_id"));
        res.setAoid(parameters.get("aoid"));
        res.setPay_price(Double.valueOf(parameters.get("pay_price")));
        JSONObject obj = JSON.parseObject(parameters.get("detail"));
        if (obj != null) {
          res.setTransaction_id(obj.getString("transaction_id"));
          res.setBank_type(obj.getString("bank_type"));
          res.setBuyer(obj.getString("buyer"));
        }
        return res;
      } catch (IOException e) {
        throw new PayException(e.getMessage());
      }
    }
  }
}

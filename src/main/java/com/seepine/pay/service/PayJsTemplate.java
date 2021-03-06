package com.seepine.pay.service;

import com.alibaba.fastjson.JSON;
import com.seepine.http.util.HttpUtil;
import com.seepine.pay.entity.payjs.PayJsProperties;
import com.seepine.pay.entity.payjs.PayJsReq;
import com.seepine.pay.entity.payjs.PayJsRes;
import com.seepine.pay.exception.PayException;
import com.seepine.pay.util.SignUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author seepine
 */
@Slf4j
@RequiredArgsConstructor
public class PayJsTemplate implements InitializingBean {
    private final PayJsProperties payJsProperties;

    @Override
    public void afterPropertiesSet() {
        log.info("afterPropertiesSet PayJsTemplate");
        log.info("finish properties : {}", payJsProperties.toString());
    }

    public Integer getChannel(String mChId) {
        for (int i = 0; i < payJsProperties.getMChId().length; i++) {
            if (payJsProperties.getMChId()[i].equals(mChId)) {
                return i;
            }
        }
        return null;
    }

    public Channel channel() {
        return Channel.build(0, payJsProperties);
    }

    public Channel channel(int channel) {
        return Channel.build(channel, payJsProperties);
    }


    public PayJsRes pay(String subject, String outTradeNo, Double amount) throws IOException, PayException {
        return channel().pay(subject, outTradeNo, amount);
    }

    public PayJsRes pay(String subject, String outTradeNo, Double amount, String notifyUrl) throws IOException, PayException {
        return channel().pay(subject, outTradeNo, amount, notifyUrl);
    }

    public boolean checkSign(PayJsReq payJsReq) {
        return channel().checkSign(payJsReq);
    }

    public PayJsRes close(String payJsOrderId) throws IOException, PayException {
        return channel().close(payJsOrderId);
    }


    public static class Channel {
        private final int channel;
        private final PayJsProperties payJsProperties;

        private Channel(int channel, PayJsProperties payJsProperties) {
            this.channel = channel;
            this.payJsProperties = payJsProperties;
        }

        public static Channel build(int channel, PayJsProperties payJsProperties) {
            return new Channel(channel, payJsProperties);
        }

        /**
         * native??????
         *
         * @param subject    ??????
         * @param outTradeNo ?????????????????????????????????
         * @param amount     ?????????????????????
         * @return PayJsRes
         * @throws IOException  IOException
         * @throws PayException PayException
         */
        public PayJsRes payCashier(String subject, String outTradeNo, Double amount) throws IOException, PayException {
            return payCashier(subject, outTradeNo, amount, payJsProperties.getNotifyUrl()[channel]);
        }

        /**
         * native??????
         *
         * @param subject    ??????
         * @param outTradeNo ?????????????????????????????????
         * @param amount     ?????????????????????
         * @param notifyUrl  ????????????????????????
         * @return PayJsRes
         * @throws IOException  IOException
         * @throws PayException PayException
         */
        public PayJsRes payCashier(String subject, String outTradeNo, Double amount, String notifyUrl) throws IOException, PayException {
      Map<String, String> payData = new HashMap<>(8);
            payData.put("mchid", payJsProperties.getMChId()[channel]);
            payData.put("total_fee", String.valueOf((int) (amount * 100)));
            payData.put("out_trade_no", outTradeNo);
            payData.put("body", subject);
            payData.put("notify_url", notifyUrl);
            payData.put("auto", notifyUrl);
            payData.put("hide", notifyUrl);
            payData.put("sign", SignUtil.sign(payData, payJsProperties.getSecret()[channel]));

            String result = HttpUtil.post("https://payjs.cn/api/cashier", JSON.toJSONString(payData));
            PayJsRes payJsRes = JSON.parseObject(result, PayJsRes.class);
            if (payJsRes.getReturn_code() == 1) {
                return payJsRes;
            }
            throw new PayException(payJsRes.getReturn_msg(), payJsRes);
        }

        /**
         * native??????
         *
         * @param subject    ??????
         * @param outTradeNo ?????????????????????????????????
         * @param amount     ?????????????????????
         * @return PayJsRes
         * @throws IOException  IOException
         * @throws PayException PayException
         */
        public PayJsRes pay(String subject, String outTradeNo, Double amount) throws IOException, PayException {
            return pay(subject, outTradeNo, amount, payJsProperties.getNotifyUrl()[channel]);
        }

        /**
         * native??????
         *
         * @param subject    ??????
         * @param outTradeNo ?????????????????????????????????
         * @param amount     ?????????????????????
         * @param notifyUrl  ????????????????????????
         * @return PayJsRes
         * @throws IOException  IOException
         * @throws PayException PayException
         */
        public PayJsRes pay(String subject, String outTradeNo, Double amount, String notifyUrl) throws IOException, PayException {
            Map<String, String> payData = new HashMap<>(6);
            payData.put("mchid", payJsProperties.getMChId()[channel]);
            payData.put("total_fee", String.valueOf((int) (amount * 100)));
            payData.put("out_trade_no", outTradeNo);
            payData.put("body", subject);
            payData.put("notify_url", notifyUrl);
            payData.put("sign", SignUtil.sign(payData, payJsProperties.getSecret()[channel]));

            String result = HttpUtil.post("https://payjs.cn/api/native", JSON.toJSONString(payData));
            PayJsRes payJsRes = JSON.parseObject(result, PayJsRes.class);
            if (payJsRes.getReturn_code() == 1) {
                return payJsRes;
            }
            throw new PayException(payJsRes.getReturn_msg(), payJsRes);
        }

        public PayJsRes close(String payJsOrderId) throws IOException, PayException {
            Map<String, String> payData = new HashMap<>(2);
            payData.put("payjs_order_id", payJsOrderId);
            payData.put("sign", SignUtil.sign(payData, payJsProperties.getSecret()[channel]));

            String result = HttpUtil.post("https://payjs.cn/api/close", JSON.toJSONString(payData));
            PayJsRes payJsRes = JSON.parseObject(result, PayJsRes.class);
            if (payJsRes.getReturn_code() == 1) {
                return payJsRes;
            }
            throw new PayException(payJsRes.getReturn_msg(), payJsRes);
        }

        public PayJsRes check(String payJsOrderId) throws IOException, PayException {
            Map<String, String> payData = new HashMap<>(2);
            payData.put("payjs_order_id", payJsOrderId);
            payData.put("sign", SignUtil.sign(payData, payJsProperties.getSecret()[channel]));

            String result = HttpUtil.post("https://payjs.cn/api/check", JSON.toJSONString(payData));
            PayJsRes payJsRes = JSON.parseObject(result, PayJsRes.class);
            if (payJsRes.getReturn_code() == 1) {
                if (!payJsRes.getMchid().equals(payJsProperties.getMChId()[channel])) {
                    throw new PayException("??????id?????????", payJsRes);
                }
                return payJsRes;
            }
            throw new PayException(payJsRes.getReturn_msg(), payJsRes);
        }

        /**
         * ??????????????????
         *
         * @param payJsReq payJsReq
         * @return true/false
         */
        public boolean checkSign(PayJsReq payJsReq) {
            Map<String, String> notifyData = new HashMap<>(10);
            notifyData.put("return_code", String.valueOf(payJsReq.getReturn_code()));
            notifyData.put("total_fee", String.valueOf(payJsReq.getTotal_fee()));
            notifyData.put("out_trade_no", payJsReq.getOut_trade_no());
            notifyData.put("payjs_order_id", payJsReq.getPayjs_order_id());
            notifyData.put("transaction_id", payJsReq.getTransaction_id());
            notifyData.put("time_end", payJsReq.getTime_end());
            notifyData.put("openid", payJsReq.getOpenid());
            notifyData.put("mchid", payJsReq.getMchid());
            if (payJsReq.getAttach() != null) {
                notifyData.put("attach", payJsReq.getAttach());
            }
            if (payJsReq.getType() != null) {
                notifyData.put("type", payJsReq.getType());
            }
            String sign = SignUtil.sign(notifyData, payJsProperties.getSecret()[channel]);
            return sign.equals(payJsReq.getSign());
        }
    }
}

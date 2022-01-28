package com.seepine.pay.util;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayConstants;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.internal.util.codec.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/** @author seepine */
@Slf4j
public class SignUtil {

  public static String sign(Map<String, String> params, String secret) {
    String sign = "";
    StringBuilder sb = new StringBuilder();
    Set<String> keySet = params.keySet();
    TreeSet<String> sortSet = new TreeSet<>(keySet);
    for (String key : sortSet) {
      String value = params.get(key);
      sb.append(key).append("=").append(value).append("&");
    }
    sb.append("key=").append(secret);
    byte[] md5Digest;
    try {
      md5Digest = getMd5Digest(sb.toString());
      sign = byte2hex(md5Digest);
    } catch (IOException e) {
      log.error("生成签名错误,{}" + e.getMessage());
    }
    return sign;
  }

  private static String byte2hex(byte[] bytes) {
    StringBuilder sign = new StringBuilder();
    for (byte aByte : bytes) {
      String hex = Integer.toHexString(aByte & 0xFF);
      if (hex.length() == 1) {
        sign.append("0");
      }
      sign.append(hex.toUpperCase());
    }
    return sign.toString();
  }

  private static byte[] getMd5Digest(String data) throws IOException {
    byte[] bytes;
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      bytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
    } catch (GeneralSecurityException gse) {
      throw new IOException(gse);
    }
    return bytes;
  }

  public static String md5(String str) throws IOException {
    byte[] byteArray = getMd5Digest(str);
    StringBuilder md5StrBuff = new StringBuilder();
    for (byte b : byteArray) {
      if (Integer.toHexString(0xFF & b).length() == 1)
        md5StrBuff.append("0").append(Integer.toHexString(0xFF & b));
      else md5StrBuff.append(Integer.toHexString(0xFF & b));
    }
    return md5StrBuff.toString();
  }

  public static boolean rsaCertCheckV2(Map<String, String> params, String alipayCert)
      throws AlipayApiException {
    String sign = params.get("sign");
    String content = AlipaySignature.getSignCheckContentV1(params);
    return AlipaySignature.rsaCheck(
        content, sign, alipayCert, AlipayConstants.CHARSET_UTF8, AlipayConstants.SIGN_TYPE_RSA2);
  }

  public static String getAlipayPublicKey(String alipayPublicCertPath) throws AlipayApiException {
    try {
      ClassPathResource resource = new ClassPathResource(alipayPublicCertPath);
      CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
      X509Certificate cert = (X509Certificate) cf.generateCertificate(resource.getInputStream());
      PublicKey publicKey = cert.getPublicKey();
      return Base64.encodeBase64String(publicKey.getEncoded());
    } catch (NoSuchProviderException | CertificateException | IOException e) {
      throw new AlipayApiException(e);
    }
  }
}

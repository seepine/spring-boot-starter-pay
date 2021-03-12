package com.seepine.pay.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author seepine
 */
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
}

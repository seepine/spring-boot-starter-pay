package com.seepine.pay.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpUtils {
  /**
   * @param url ����ĵ�ַ
   * @param params ��װ�Ĳ���
   * @return
   */
  public static String post(String url, Map<String, String> params) {
    URL u = null;
    HttpURLConnection con = null;
    // �����������
    StringBuffer sb = new StringBuffer();
    if (params != null) {
      for (Map.Entry<String, String> e : params.entrySet()) {
        sb.append(e.getKey());
        sb.append("=");
        sb.append(e.getValue());
        sb.append("&");
      }
      sb.substring(0, sb.length() - 1);
    }
    System.out.println("send_url:" + url);
    System.out.println("send_data:" + sb.toString());
    // ���Է�������
    try {
      u = new URL(url);
      con = (HttpURLConnection) u.openConnection();
      //// POST ֻ��Ϊ��д���ϸ����ƣ�post�᲻ʶ��
      con.setRequestMethod("POST");
      con.setDoOutput(true);
      con.setDoInput(true);
      con.setUseCaches(false);
      con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      OutputStreamWriter osw =
          new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8);
      osw.write(sb.toString());
      osw.flush();
      osw.close();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (con != null) {
        con.disconnect();
      }
    }

    // ��ȡ��������
    StringBuffer buffer = new StringBuffer();
    try {
      // һ��Ҫ�з���ֵ�������޷��������͸�server�ˡ�
      BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
      String temp;
      while ((temp = br.readLine()) != null) {
        buffer.append(temp);
        buffer.append("\n");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return buffer.toString();
  }

  public static String get(String url) throws Exception {
    URL urlobj = new URL(url);
    URLConnection conn = urlobj.openConnection();
    InputStream in = conn.getInputStream();
    BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
    StringBuffer sb = new StringBuffer();
    String line = "";
    while ((line = br.readLine()) != null) {
      sb.append(line).append("\r\n");
    }
    in.close();
    return sb.toString();
  }
}

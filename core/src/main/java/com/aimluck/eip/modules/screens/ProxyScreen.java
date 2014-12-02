/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
 * http://www.aipo.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aimluck.eip.modules.screens;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALURLConnectionUtils;

/**
 * 各ポートレットでの添付ファイルを表示させるクラスです。 <br />
 * 
 */
public class ProxyScreen extends ALVelocityScreen {
  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {
    JetspeedUser loginuser = (JetspeedUser) rundata.getUser();
    if (loginuser == null || !loginuser.hasLoggedIn()) {
      return;
    }

    String urlString = ALEipUtils.getParameter(rundata, context, "url");
    if (urlString == null || urlString.equals("")) {
      return;
    }

    // normalize()でエラーが起きないようにurlStringから改行コードを排除する処理
    urlString = urlString.replaceAll("\\n", "");

    URI uri = new URI(urlString).normalize();
    String path =
      uri.getPath().replaceAll("\\.\\./", "").replaceAll("\\./", "");
    URL url =
      new URL(String.format(
        "%s://%s%s",
        uri.getScheme(),
        uri.getAuthority(),
        path));

    URLConnection urlCon = url.openConnection();
    urlCon.addRequestProperty("User-Agent", ALURLConnectionUtils.USER_AGENT);
    urlCon.addRequestProperty(
      "Accept-Encoding",
      ALURLConnectionUtils.ACCEPT_ENCODING);
    try {
      InputStream in = urlCon.getInputStream();
      ServletOutputStream out = null;
      HttpServletResponse response = rundata.getResponse();
      out = response.getOutputStream();

      try {
        byte[] buf = new byte[1024];
        int len = 0;

        while ((len = in.read(buf)) > 0) {
          out.write(buf, 0, len);
        }

        out.flush();
      } finally {
        out.close();
        in.close();
      }
    } catch (SSLHandshakeException ex) {
      urlCon = getHttpsConnection(url.toString());
      InputStream in = urlCon.getInputStream();
      ServletOutputStream out = null;
      HttpServletResponse response = rundata.getResponse();
      out = response.getOutputStream();

      try {
        byte[] buf = new byte[1024];
        int len = 0;

        while ((len = in.read(buf)) > 0) {
          out.write(buf, 0, len);
        }

        out.flush();
      } finally {
        out.close();
        in.close();
      }
    }

  }

  private static HttpURLConnection getHttpsConnection(String url)
      throws Exception {

    HttpURLConnection urlconn = null;

    URL connectURL = new URL(url);

    // https接続の場合
    if ("https".equals(connectURL.getProtocol())) {

      // 証明書情報　全て空を返す
      TrustManager[] tm = { new X509TrustManager() {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
          return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        }
      } };
      SSLContext sslcontext = SSLContext.getInstance("SSL");
      sslcontext.init(null, tm, null);
      // ホスト名の検証ルール　何が来てもtrueを返す
      HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
          return true;
        }
      });

      urlconn = (HttpsURLConnection) connectURL.openConnection();

      ((HttpsURLConnection) urlconn).setSSLSocketFactory(sslcontext
        .getSocketFactory());

      // http接続の場合
    } else {

      urlconn = (HttpURLConnection) connectURL.openConnection();

    }

    // http,https共通

    urlconn.setRequestMethod("GET");
    // 接続
    urlconn.connect();

    return urlconn;
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return null;
  }

}

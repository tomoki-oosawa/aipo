/*
 * Aipo is a groupware program developed by TOWN, Inc.
 * Copyright (C) 2004-2015 TOWN, Inc.
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
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALURLConnectionUtils;

/**
 * 各ポートレットでの添付ファイルを表示させるクラスです。 <br />
 * 
 */
public class ProxyScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ProxyScreen.class.getName());

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

    URLConnection urlCon = ALURLConnectionUtils.openUrlConnection(url);
    urlCon.addRequestProperty("User-Agent", ALURLConnectionUtils.USER_AGENT);
    urlCon.addRequestProperty(
      "Accept-Encoding",
      ALURLConnectionUtils.ACCEPT_ENCODING);
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

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return null;
  }

}

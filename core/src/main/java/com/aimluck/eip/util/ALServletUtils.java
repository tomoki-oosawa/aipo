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
package com.aimluck.eip.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.http.HttpServletRequestLocator;
import com.aimluck.eip.http.ServletContextLocator;
import com.aimluck.eip.services.config.ALConfigHandler.Property;
import com.aimluck.eip.services.config.ALConfigService;

public class ALServletUtils {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALServletUtils.class.getName());

  public static String getRequestBaseUrl() {
    HttpServletRequest request = HttpServletRequestLocator.get();
    String scheme = request.getScheme();
    int port = request.getServerPort();
    String serverName = request.getServerName();
    StringBuilder b = new StringBuilder(scheme);
    b.append("://").append(serverName);
    if (!("http".equals(scheme) && port == 80)
      && !("https".equals(scheme) && port == 443)) {
      b.append(":").append(port);
    }

    return b.toString();

  }

  public static String getAccessUrl(String host, int port, boolean isGlobal) {

    String loginUrl = ALConfigService.get(Property.EXTERNAL_LOGIN_URL);

    if (loginUrl != null && loginUrl.length() > 0) {
      return loginUrl;
    }

    if (host == null || "".equals(host)) {
      return "";
    }

    String scheme =
      isGlobal
        ? ALConfigService.get(Property.ACCESS_GLOBAL_URL_PROTOCOL)
        : ALConfigService.get(Property.ACCESS_LOCAL_URL_PROTOCOL);
    StringBuilder b = new StringBuilder(scheme);
    b.append("://").append(host);
    if (!("http".equals(scheme) && port == 80)
      && !("https".equals(scheme) && port == 443)) {
      b.append(":").append(port);
    }
    String contextPath = ServletContextLocator.get().getContextPath();
    if (contextPath.equals("/")) {
      contextPath = "";
    }
    b.append(contextPath);
    b.append("/");
    return b.toString();

  }
}

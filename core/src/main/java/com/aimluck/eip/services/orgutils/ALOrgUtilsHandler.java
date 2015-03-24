/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2015 Aimluck,Inc.
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
package com.aimluck.eip.services.orgutils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.context.Context;

import com.aimluck.eip.http.HttpServletRequestLocator;
import com.aimluck.eip.http.ServletContextLocator;
import com.aimluck.eip.services.config.ALConfigHandler.Property;
import com.aimluck.eip.services.config.ALConfigService;
import com.aimluck.eip.services.social.ALContainerConfigService;
import com.aimluck.eip.services.social.ALSocialApplicationHandler;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALServletUtils;

/**
 *
 *
 */
public abstract class ALOrgUtilsHandler {

  public abstract String getTheme(String orgId);

  public abstract String getAlias(String orgId);

  public abstract String getAliasjp(String orgId);

  public abstract String getCopyright(String orgId);

  public abstract String getAliasCopyright(String orgId);

  public abstract String getCopyrightShort(String orgId);

  public abstract String getVersion(String orgId);

  public Map<String, String> getParameters(String orgId) {
    Map<String, String> hash = new HashMap<String, String>();

    hash.put("theme", getTheme(orgId));
    hash.put("alias", getAlias(orgId));
    hash.put("aliasjp", getAliasjp(orgId));
    hash.put("copyright", getCopyright(orgId));
    hash.put("copyright_short", getCopyrightShort(orgId));
    hash.put("alias_copyright", getAliasCopyright(orgId));
    hash.put("version", getVersion(orgId));
    String url = getExternalResourcesUrl(orgId);
    hash.put("external_resources_url", url);
    hash.put("unlockeddomain_url", getUnlockedDomainBaseUrl(orgId));
    hash.put("context_path", getContextPath(orgId));
    hash.put("isXDomain", String.valueOf(url.startsWith("http")));

    HttpServletRequest request = HttpServletRequestLocator.get();
    String useragent = request.getHeader("User-Agent").trim();
    hash.put("client", ALEipUtils.getClient(useragent));
    hash.put("clientVer", ALEipUtils.getClientVersion(useragent));

    return hash;
  }

  /**
   * JavaScript,CSS,画像を外部サーバーから取得する
   * 
   * @param rundata
   * @return
   */
  public String getExternalResourcesUrl(String orgId) {

    StringBuffer url = new StringBuffer();

    String external_resources_url = "";
    try {
      external_resources_url =
        ALConfigService.get(Property.EXTERNAL_RESOURCES_URL);

    } catch (IllegalStateException ignore) {
      // ignore
    }
    if (external_resources_url.isEmpty()) {
      ServletContext servletContext = ServletContextLocator.get();
      String contextPath = servletContext.getContextPath();
      if ("/".equals(contextPath)) {
        contextPath = "";
      }
      url.append(contextPath);
    } else {
      url.append(external_resources_url);
    }

    return url.toString();
  }

  public String getXDomainBasePath(String orgId) {
    String url = getExternalResourcesUrl(orgId);
    if (url.startsWith("http")) {
      return url;
    } else {
      return new StringBuilder(ALServletUtils.getRequestBaseUrl())
        .append(url)
        .toString();
    }
  }

  public String getUnlockedDomainBaseUrl(String orgId) {

    String unlockedDomain = "";
    try {
      unlockedDomain =
        ALContainerConfigService
          .get(ALSocialApplicationHandler.Property.UNLOCKED_DOMAIN);

    } catch (IllegalStateException ignore) {
      // ignore
    } catch (Exception ignore) {
      // ignore
    }

    if (unlockedDomain.isEmpty()) {
      return "";
    }

    HttpServletRequest request = HttpServletRequestLocator.get();
    String scheme = request.getScheme();
    StringBuffer url = new StringBuffer(scheme);
    url.append("://");
    url.append(unlockedDomain);

    return url.toString();
  }

  public String getContextPath(String orgId) {
    String contextPath = ServletContextLocator.get().getContextPath();
    if ("/".equals(contextPath)) {
      contextPath = "";
    }
    return contextPath;
  }

  public void assignCommonContext(Context context, String orgId) {
    Map<String, String> map = getParameters(orgId);
    Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
    while (iterator.hasNext()) {
      Entry<String, String> next = iterator.next();
      context.put(next.getKey(), next.getValue());
    }
  }
}

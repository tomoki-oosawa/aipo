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

package com.aimluck.eip.services.orgutils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;

import org.apache.jetspeed.services.daemonfactory.DaemonFactoryService;
import org.apache.turbine.services.TurbineServices;

import com.aimluck.eip.services.config.ALConfigHandler.Property;
import com.aimluck.eip.services.config.ALConfigService;
import com.aimluck.eip.services.daemonfactory.AipoDaemonFactoryService;

/**
 *
 *
 */
public abstract class ALOrgUtilsHandler {

  public abstract File getDocumentPath(String rootPath, String orgId,
      String categoryKey);

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
    hash.put("external_resources_url", getExternalResourcesUrl(orgId));

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

    String external_resources_url =
      ALConfigService.get(Property.EXTERNAL_RESOURCES_URL);

    if (external_resources_url.isEmpty()) {
      AipoDaemonFactoryService aipoDaemonService =
        (AipoDaemonFactoryService) TurbineServices.getInstance().getService(
          DaemonFactoryService.SERVICE_NAME);
      ServletConfig servlet_config = aipoDaemonService.getServletConfig();
      String servlet_name = servlet_config.getServletName();
      url.append("/").append(servlet_name);
    } else {
      url.append(external_resources_url);
    }

    return url.toString();
  }
}

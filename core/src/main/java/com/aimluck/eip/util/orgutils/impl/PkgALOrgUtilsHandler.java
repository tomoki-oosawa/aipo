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

package com.aimluck.eip.util.orgutils.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;

import org.apache.jetspeed.services.daemonfactory.DaemonFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.services.TurbineServices;

import com.aimluck.eip.services.config.ALConfigFactoryService;
import com.aimluck.eip.services.config.ALConfigHandler;
import com.aimluck.eip.services.config.ALConfigHandler.Property;
import com.aimluck.eip.services.daemonfactory.AipoDaemonFactoryService;
import com.aimluck.eip.util.orgutils.ALOrgUtilsHandler;

/**
 *
 *
 */
public class PkgALOrgUtilsHandler extends ALOrgUtilsHandler {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(PkgALOrgUtilsHandler.class.getName());

  @Override
  public File getDocumentPath(String rootPath, String org_name,
      String categoryKey) {

    File rootDir = new File(rootPath);

    // If it is still missing, try to create it
    if (!rootDir.exists()) {
      try {
        rootDir.mkdirs();
      } catch (Exception e) {
        logger.error("[ALSoftOrgUtilsHandler] Can't create directory...:"
          + rootPath);
        return rootDir;
      }
    }

    if (org_name == null) {
      return rootDir;
    }

    File base = null;

    // パスを作成
    base =
      new File(rootDir.getAbsolutePath()
        + File.separator
        + org_name
        + File.separator
        + categoryKey);

    return base;
  }

  @Override
  public String getThemeName(String org_name) {
    return JetspeedResources.getString("aipo.theme");
  }

  @Override
  public Map<String, String> getParameters(String org_name) {
    Map<String, String> hash = new HashMap<String, String>();

    hash.put("theme", JetspeedResources.getString("aipo.theme"));
    hash.put("alias", JetspeedResources.getString("aipo.alias"));
    hash.put("aliasjp", JetspeedResources.getString("aipo.aliasjp"));
    hash.put("copyright", JetspeedResources.getString("aipo.copyright"));
    hash.put("copyright_short", JetspeedResources
      .getString("aipo.copyright_short"));
    hash.put("alias_copyright", JetspeedResources
      .getString("aipo.aliascopyright"));
    hash.put("version", JetspeedResources.getString("aipo.version", ""));
    hash.put("dislink", getDistributionServer());

    return hash;
  }

  /**
   * JavaScript,CSS,画像を外部サーバーから取得する
   * 
   * @param rundata
   * @return
   */
  public static String getDistributionServer() {

    StringBuffer url = new StringBuffer();

    ALConfigHandler configHandler =
      ALConfigFactoryService.getInstance().getConfigHandler();

    String protocol = configHandler.get(Property.EXTERNAL_RESOURCES_URL);

    if (protocol.isEmpty()) {
      // get configuration parameters from Jetspeed Resources
      AipoDaemonFactoryService aipoDaemonService =
        (AipoDaemonFactoryService) TurbineServices.getInstance().getService(
          DaemonFactoryService.SERVICE_NAME);
      ServletConfig servlet_config = aipoDaemonService.getServletConfig();
      String servlet_name = servlet_config.getServletName();
      url.append("/").append(servlet_name);
    } else {
      url.append(protocol);
    }

    return url.toString();
  }
}

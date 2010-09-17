/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2010 Aimluck,Inc.
 * http://aipostyle.com/
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

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;

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
  public HashMap<String, String> getParameters(String org_name) {
    HashMap<String, String> hash = new HashMap<String, String>();

    hash.put("theme", JetspeedResources.getString("aipo.theme"));
    hash.put("alias", JetspeedResources.getString("aipo.alias"));
    hash.put("aliasjp", JetspeedResources.getString("aipo.aliasjp"));
    hash.put("copyright", JetspeedResources.getString("aipo.copyright"));
    hash.put("copyright_short", JetspeedResources
      .getString("aipo.copyright_short"));
    return hash;
  }

}

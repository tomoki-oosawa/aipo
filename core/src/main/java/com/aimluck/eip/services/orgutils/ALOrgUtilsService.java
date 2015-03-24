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

import java.util.Map;

import org.apache.velocity.context.Context;

import com.aimluck.eip.orm.Database;

/**
 *
 */
public class ALOrgUtilsService {

  private ALOrgUtilsService() {

  }

  public static ALOrgUtilsHandler getService() {
    return ALOrgUtilsFactoryService.getInstance().getOrgUtilsHandler();
  }

  public static String getTheme() {
    return getService().getTheme(Database.getDomainName());
  }

  public static String getAlias() {
    return getService().getAlias(Database.getDomainName());
  }

  public static String getAliasjp() {
    return getService().getAliasjp(Database.getDomainName());
  }

  public static String getCopyright() {
    return getService().getCopyright(Database.getDomainName());
  }

  public static String getAliasCopyright() {
    return getService().getAliasCopyright(Database.getDomainName());
  }

  public static String getCopyrightShort() {
    return getService().getCopyrightShort(Database.getDomainName());
  }

  public static String getVersion() {
    return getService().getVersion(Database.getDomainName());
  }

  public static Map<String, String> getParameters() {
    return getService().getParameters(Database.getDomainName());
  }

  public static String getExternalResourcesUrl() {
    return getService().getExternalResourcesUrl(Database.getDomainName());
  }

  public static void assignCommonContext(Context context) {
    getService().assignCommonContext(context, Database.getDomainName());
  }
}

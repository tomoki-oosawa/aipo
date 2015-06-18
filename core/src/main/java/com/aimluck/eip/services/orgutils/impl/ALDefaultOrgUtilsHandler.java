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
package com.aimluck.eip.services.orgutils.impl;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;

import com.aimluck.eip.services.orgutils.ALOrgUtilsHandler;

/**
 *
 *
 */
public class ALDefaultOrgUtilsHandler extends ALOrgUtilsHandler {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALDefaultOrgUtilsHandler.class.getName());

  @Override
  public String getAlias(String orgId) {
    return JetspeedResources.getString("aipo.alias");
  }

  @Override
  public String getAliasjp(String orgId) {
    return JetspeedResources.getString("aipo.aliasjp");
  }

  @Override
  public String getCopyright(String orgId) {
    return JetspeedResources.getString("aipo.copyright");
  }

  @Override
  public String getAliasCopyright(String orgId) {
    return JetspeedResources.getString("aipo.aliascopyright");
  }

  @Override
  public String getCopyrightShort(String orgId) {
    return JetspeedResources.getString("aipo.copyright_short");
  }

  @Override
  public String getTheme(String orgId) {
    return JetspeedResources.getString("aipo.theme");
  }

  @Override
  public String getVersion(String orgId) {
    return JetspeedResources.getString("aipo.version", "");
  }

}

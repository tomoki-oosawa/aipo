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
package com.aimluck.eip.services.config;

import com.aimluck.eip.services.config.ALConfigHandler.Property;

/**
 * 
 */
public class ALConfigService {

  private ALConfigService() {

  }

  public static ALConfigHandler getService() {
    return ALConfigFactoryService.getInstance().getConfigHandler();
  }

  public static String get(Property property) {
    return getService().get(property);
  }

  public static void put(Property property, String value) {
    getService().put(property, value);
  }

  public static String get(String property, String defaultValue) {
    return getService().get(property, defaultValue);
  }

  public static void put(String property, String value) {
    getService().put(property, value);
  }
}

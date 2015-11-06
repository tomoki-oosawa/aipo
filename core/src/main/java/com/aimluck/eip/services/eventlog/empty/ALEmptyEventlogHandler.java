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
package com.aimluck.eip.services.eventlog.empty;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.services.eventlog.ALEventlogHandler;

/**
 * イベントログを管理するクラスです。 <br />
 * 
 */
public class ALEmptyEventlogHandler extends ALEventlogHandler {

  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALEmptyEventlogHandler.class.getName());

  @Override
  public void log(int entity_id, int portlet_type, String note) {

  }

  @Override
  public void log(int entity_id, int portlet_type, String note, String mode) {

  }

  @Override
  public void logLogin(int userid) {

  }

  @Override
  public void logLogout(int userid) {

  }

  @Override
  public void logXlsScreen(int userid, String Note, int _p_type) {

  }

  /**
   * @param mode
   * @return
   */
  @Override
  public int getEventTypeValue(String mode) {
    return 0;
  }

  /**
   * @param eventType
   * @return
   */
  @Override
  public String getEventAliasName(int eventType) {
    return null;
  }

  /**
   * @param portletType
   * @return
   */
  @Override
  public String getPortletAliasName(int portletType) {
    return null;
  }
}

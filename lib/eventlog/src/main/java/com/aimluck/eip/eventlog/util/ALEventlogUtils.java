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
package com.aimluck.eip.eventlog.util;

import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;

/**
 *
 */
public class ALEventlogUtils {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALEventlogUtils.class.getName());

  /**
   * mode を DB に保存するための数値に変換します。
   * 
   * @param mode
   * @return
   */
  public static int getEventTypeValue(String mode) {
    return ALEventlogFactoryService
      .getInstance()
      .getEventlogHandler()
      .getEventTypeValue(mode);
  }

  /**
   * イベントのエイリアス名を取得します。
   * 
   * @param eventType
   * @return
   */
  public static String getEventAliasName(int eventType) {
    return ALEventlogFactoryService
      .getInstance()
      .getEventlogHandler()
      .getEventAliasName(eventType);
  }

  /**
   * ポートレットのエイリアス名を取得します。
   * 
   * @param eventType
   * @return
   */
  public static String getPortletAliasName(int portletType) {
    return ALEventlogFactoryService
      .getInstance()
      .getEventlogHandler()
      .getPortletAliasName(portletType);
  }

  /**
   * ポートレットIDからそのポートレットのPSMLのparentの文字列を取得する
   * 
   * @param rundata
   * @param portletEntryId
   * @return
   */
  public static String getPortletName(RunData rundata, String portletEntryId) {
    try {
      Portlets portlets =
        ((JetspeedRunData) rundata).getProfile().getDocument().getPortlets();
      if (portlets == null) {
        return null;
      }

      Portlets[] portletList = portlets.getPortletsArray();
      if (portletList == null) {
        return null;
      }

      int length = portletList.length;
      for (int i = 0; i < length; i++) {
        Entry[] entries = portletList[i].getEntriesArray();
        if (entries == null || entries.length <= 0) {
          continue;
        }

        int ent_length = entries.length;
        for (int j = 0; j < ent_length; j++) {
          if (entries[j].getId().equals(portletEntryId)) {
            return entries[j].getParent();
          }
        }
      }
    } catch (Exception ex) {
      logger.error("ALEventlogUtils.getPortletName", ex);
      return null;
    }
    return null;
  }
}

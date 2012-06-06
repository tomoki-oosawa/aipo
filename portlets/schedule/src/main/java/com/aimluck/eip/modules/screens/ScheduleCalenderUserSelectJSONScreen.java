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

package com.aimluck.eip.modules.screens;

import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Parameter;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.psml.PsmlParameter;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * カレンダーを処理するクラスです。<br />
 * UserFacilityLiteJSONScreenの選択されたグループをセッションに保持する。
 */
public class ScheduleCalenderUserSelectJSONScreen extends
    UserFacilityLiteJSONScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleCalenderUserSelectJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {

    savePsmlParameters(rundata, context);

    String groupname = rundata.getParameters().getString("groupname");

    if ("pickup".equals(groupname)) {
      return null;// ユーザーセレクト時
    } else {
      return super.getJSONString(rundata, context);
    }
  }

  // psmlに選択されたグループを保存
  private boolean savePsmlParameters(RunData rundata, Context context) {
    try {
      String portletEntryId =
        rundata.getParameters().getString("js_peid", null);
      String groupname = rundata.getParameters().getString("groupname");
      if (groupname == "" || groupname == null) {// nullで送信するとpsmlが破壊される
        return false;
      }

      String KEY_CGRP = "p8h-cgrp";

      Profile profile = ((JetspeedRunData) rundata).getProfile();
      Portlets portlets = profile.getDocument().getPortlets();
      if (portlets == null) {
        return false;
      }

      Portlets[] portletList = portlets.getPortletsArray();
      if (portletList == null) {
        return false;
      }

      PsmlParameter param = null;
      int length = portletList.length;
      for (int i = 0; i < length; i++) {
        Entry[] entries = portletList[i].getEntriesArray();
        if (entries == null || entries.length <= 0) {
          continue;
        }

        int ent_length = entries.length;
        for (int j = 0; j < ent_length; j++) {
          if (entries[j].getId().equals(portletEntryId)) {
            boolean hasParam = false;
            Parameter params[] = entries[j].getParameter();
            int param_len = params.length;
            for (int k = 0; k < param_len; k++) {
              if (params[k].getName().equals(KEY_CGRP)) {
                hasParam = true;
                params[k].setValue(groupname);
                entries[j].setParameter(k, params[k]);
              }
            }
            if (!hasParam) {
              param = new PsmlParameter();
              param.setName(KEY_CGRP);
              param.setValue(groupname);
              entries[j].addParameter(param);
            }
            break;
          }
        }
      }
      profile.store();

    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }
}

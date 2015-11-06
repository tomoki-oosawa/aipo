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
package com.aimluck.eip.modules.screens;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.schedule.util.ScheduleAclUtils;
import com.aimluck.eip.user.beans.UserLiteBean;
import com.aimluck.eip.user.util.UserUtils;
import com.aimluck.eip.util.ALEipUtils;

public class ScheduleUserLiteJSONScreen extends ALJSONScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleUserLiteJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    String result = "";
    JSONArray json;

    try {

      String mode = rundata.getParameters().getString("mode");
      if ("group".equals(mode)) {
        String groupname = rundata.getParameters().getString("groupname");

        // ログインユーザをリストに含める場合、true
        boolean include_loginuser =
          rundata.getParameters().getBoolean("inc_luser", true);

        json =
          JSONArray.fromObject(ScheduleAclUtils.getAclAcceptUserFilter(
            UserUtils.getUserLiteBeansFromGroup(
              rundata,
              groupname,
              include_loginuser),
            ALEipUtils.getUserId(rundata),
            2));

      } else if ("group_loginname".equals(mode)) {
        String groupname = rundata.getParameters().getString("groupname");
        List<UserLiteBean> users =
          ScheduleAclUtils.getAclAcceptUserFilter(UserUtils
            .getUserLiteBeansFromGroup(rundata, groupname, false), ALEipUtils
            .getUserId(rundata), 2);
        List<UserLiteBean> outputs = new ArrayList<UserLiteBean>();
        for (UserLiteBean datarow : users) {
          UserLiteBean user = datarow;
          UserLiteBean out = new UserLiteBean();
          out.initField();
          out.setUserId(Integer.valueOf(user.getUserId()));
          out.setName(user.getName());
          out.setAliasName("(" + user.getAliasName() + ")", user.getName());
          outputs.add(out);
        }
        json = JSONArray.fromObject(outputs);
      } else {
        json = new JSONArray();
      }
      result = json.toString();
    } catch (Exception e) {
      logger.error("[ScheduleUserLiteJSONScreen]", e);
    }

    return result;
  }
}
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

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.schedule.util.ScheduleAclUtils;
import com.aimluck.eip.userfacility.beans.UserFacilityLiteBean;
import com.aimluck.eip.userfacility.util.UserFacilityUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class ScheduleUserFacilityLiteJSONScreen extends ALJSONScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleUserFacilityLiteJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    String result = "";
    JSONArray json;

    try {

      int userid = ALEipUtils.getUserId(rundata);
      String mode = rundata.getParameters().getString("mode");
      if ("group".equals(mode)) {
        String[] groupname =
          rundata.getParameters().getString("groupname").split(";");
        List<UserFacilityLiteBean> list = null;
        List<UserFacilityLiteBean> users =
          new ArrayList<UserFacilityLiteBean>();
        List<UserFacilityLiteBean> facilities =
          new ArrayList<UserFacilityLiteBean>();
        if ("f".equals(groupname[0])) {
          list =
            UserFacilityUtils.getFacilityLiteBeansFromGroup(rundata, Integer
              .valueOf(groupname[1]));

        } else {
          list =
            UserFacilityUtils.getUserFacilityLiteBeansFromGroup(
              rundata,
              groupname[0]);
        }
        for (UserFacilityLiteBean bean : list) {
          if ("U".equals(bean.getUserFacilityType())) {
            users.add(bean);
          } else {
            facilities.add(bean);
          }
        }
        List<UserFacilityLiteBean> results1 =
          ScheduleAclUtils.getAclAcceptUserFilter(users, userid, 1);
        List<UserFacilityLiteBean> results2 =
          ScheduleAclUtils.getAclAcceptFacilityFilter(facilities, userid, 1);

        results1.addAll(results2);

        json = JSONArray.fromObject(results1);
      } else {
        json = new JSONArray();
      }
      result = json.toString();
    } catch (Exception e) {
      logger.error("[UserLiteJSONScreen]", e);
    }

    return result;
  }
}

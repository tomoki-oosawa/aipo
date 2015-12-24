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

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.userfacility.beans.UserFacilityLiteBean;
import com.aimluck.eip.userfacility.util.UserFacilityUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * カレンダーを処理するクラスです。<br />
 * UserFacilityLiteJSONScreenの選択されたグループをセッションに保持する。
 */
public class ScheduleCalendarUserSelectJSONScreen extends
    UserFacilityLiteJSONScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleCalendarUserSelectJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {

    ALEipUtils.setPsmlParameters(rundata, context, "p8h-cgrp", rundata
      .getParameters()
      .getString("groupname"));

    String groupname = rundata.getParameters().getString("groupname");
    String has_acl_other = ScheduleUtils.hasAuthOther(rundata);
    if ("pickup".equals(groupname)) {
      VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
      List<UserFacilityLiteBean> memberList =
        new ArrayList<UserFacilityLiteBean>();
      String pickedMember =
        portlet.getPortletConfig().getInitParameter("p6a-uids");
      if (pickedMember == null || "".equals(pickedMember)) {
        UserFacilityLiteBean login_user =
          UserFacilityUtils.getUserFacilityLiteBean(rundata);
        memberList.add(login_user);
      } else {
        String pickedMembers[] = pickedMember.split(",");
        List<UserFacilityLiteBean> ulist;
        if ("F".equals(has_acl_other)) {
          ulist = ScheduleUtils.getALEipFacility(pickedMembers, rundata);
          if (pickedMember.contains(String.valueOf(ALEipUtils
            .getUserId(rundata)))) {
            UserFacilityLiteBean login_user =
              UserFacilityUtils.getUserFacilityLiteBean(rundata);
            ulist.add(login_user);
          }
        } else {
          ulist = ScheduleUtils.getALEipUserFacility(pickedMembers, rundata);
        }
        if (ulist == null || ulist.size() == 0) {
          UserFacilityLiteBean login_user =
            UserFacilityUtils.getUserFacilityLiteBean(rundata);
          memberList.add(login_user);
        } else {
          memberList.addAll(ulist);
        }
      }
      JSONArray json = JSONArray.fromObject(memberList);
      return json.toString();
    } else {
      return super.getJSONString(rundata, context);
    }
  }
}

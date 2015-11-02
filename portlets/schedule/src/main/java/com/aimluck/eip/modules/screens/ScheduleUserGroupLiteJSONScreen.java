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

import net.sf.json.JSONArray;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.user.util.UserUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * グループ情報をJSONデータとして出力するクラスです。 <br />
 * 
 */
public class ScheduleUserGroupLiteJSONScreen extends UserGroupLiteJSONScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleUserGroupLiteJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    String result = "";
    JSONArray json;

    try {

      String mode = rundata.getParameters().getString("mode");
      if ("mygroup".equals(mode)) {
        json =
          JSONArray.fromObject(UserUtils.getUserGroupLiteBeans(
            rundata,
            true,
            false,
            false));
      } else if ("post".equals(mode)) {
        json =
          JSONArray.fromObject(UserUtils.getUserGroupLiteBeans(
            rundata,
            false,
            true,
            false));
      } else if ("mygroup_and_post".equals(mode)) {
        json =
          JSONArray.fromObject(UserUtils.getUserGroupLiteBeans(
            rundata,
            true,
            true,
            false));
      } else {

        boolean hasAclviewOther;
        int userid = ALEipUtils.getUserId(rundata);
        // アクセス権限
        ALAccessControlFactoryService aclservice =
          (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
            .getInstance())
            .getService(ALAccessControlFactoryService.SERVICE_NAME);
        ALAccessControlHandler aclhandler =
          aclservice.getAccessControlHandler();

        hasAclviewOther =
          aclhandler.hasAuthority(
            userid,
            ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_OTHER,
            ALAccessControlConstants.VALUE_ACL_LIST);

        if (hasAclviewOther) {
          json =
            JSONArray.fromObject(UserUtils.getUserGroupLiteBeans(
              rundata,
              true,
              true,
              true));
        } else {
          json =
            JSONArray.fromObject(UserUtils.getUserGroupLiteBeans(
              rundata,
              false,
              false,
              true));
        }
      }

      result = json.toString();
    } catch (Exception e) {
      logger.error("[ScheduleUserGroupLiteJSONScreen]", e);
    }

    return result;
  }
}

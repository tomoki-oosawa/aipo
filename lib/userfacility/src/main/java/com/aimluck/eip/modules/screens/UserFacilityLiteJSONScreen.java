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
package com.aimluck.eip.modules.screens;

import net.sf.json.JSONArray;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.userfacility.util.UserFacilityUtils;

/**
 * 設備情報をJSONデータとして出力するクラスです。 <br />
 * 
 */
public class UserFacilityLiteJSONScreen extends ALJSONScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(UserFacilityLiteJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    String result = "";
    JSONArray json;

    try {

      String mode = rundata.getParameters().getString("mode");
      if ("group".equals(mode)) {
        String[] groupname =
          rundata.getParameters().getString("groupname").split(";");
        if ("f".equals(groupname[0])) {
          if ("all".equals(groupname[1])) {
            json =
              JSONArray.fromObject(UserFacilityUtils
                .getAllFacilityLiteBeans(rundata));
          } else {
            json =
              JSONArray.fromObject(UserFacilityUtils
                .getFacilityLiteBeansFromGroup(rundata, Integer
                  .valueOf(groupname[1])));
          }
        } else {
          if ("allgroups".equals(groupname[0])) {
            json =
              JSONArray.fromObject(UserFacilityUtils
                .getAllFacilityLiteBeans(rundata));
          } else {
            json =
              JSONArray.fromObject(UserFacilityUtils
                .getUserFacilityLiteBeansFromGroup(rundata, groupname[0]));
          }
        }
      } else {
        json = new JSONArray();
      }
      result = json.toString();
    } catch (Exception e) {
      logger.error("UserFacilityLiteJSONScreen.getJSONString", e);
    }

    return result;
  }
}

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

import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.userfacility.beans.UserAllLiteBean.Type;
import com.aimluck.eip.userfacility.util.UserAllUtils;

/**
 *
 */
public class UserAllLiteJSONScreen extends ALJSONScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(UserAllLiteJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    String result = "";
    JSONArray json;

    try {

      Type filterType = null;
      try {
        filterType =
          Type.valueOf(rundata.getParameters().getString("filterType"));
      } catch (Throwable ignore) {
        // ignore
      }

      int filterId = 0;
      try {
        filterId = rundata.getParameters().getInt("filterId");
      } catch (Throwable ignore) {
        // ignore
      }

      String mode = rundata.getParameters().getString("mode");
      if ("group".equals(mode) || "grouponly".equals(mode)) {
        String groupName = rundata.getParameters().getString("groupname");

        TurbineGroup turbineGroup =
          Database.query(TurbineGroup.class).where(
            Operations.eq("groupName", groupName)).fetchSingle();

        if (turbineGroup != null) {
          filterType = Type.ug;
          filterId = Integer.valueOf(turbineGroup.getId());
        }
      }

      json =
        JSONArray.fromObject(UserAllUtils.getUserAllLiteBeans(
          rundata,
          filterId,
          filterType,
          "grouponly".equals(mode)));

      result = json.toString();
    } catch (Exception e) {
      logger.error("UserAllLiteJSONScreen.getJSONString", e);
    }

    return result;
  }
}

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
import net.sf.json.JSONObject;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALActivity;
import com.aimluck.eip.common.ALActivityCount;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.services.social.ALActivityService;
import com.aimluck.eip.services.social.model.ALActivityGetRequest;
import com.aimluck.eip.util.ALEipUtils;

public class CheckActivityJSONScreen extends ALJSONScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CheckActivityJSONScreen.class.getName());

  @Override
  protected String getPrefix() {
    return "";
  }

  @Override
  protected String getSuffix() {
    return "";
  }

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    String result = "";
    JSONObject json;
    JSONArray jsonArray;

    try {
      String loginName = ALEipUtils.getALEipUser(rundata).getName().getValue();
      String isRead = null;
      Integer isReadCount = null;
      boolean all = false;
      try {
        isRead = rundata.getParameters().getString("isRead");
        if ("*".equals(isRead)) {
          all = true;
        } else {
          isReadCount = Integer.valueOf(isRead);
        }
      } catch (Throwable t) {

      }
      Long max = null;
      try {
        String maxStr = rundata.getParameters().getString("max");
        if (maxStr != null && !"".equals(maxStr)) {
          max = Long.valueOf(maxStr);
        }
      } catch (Throwable t) {

      }
      if (all) {
        ALActivityService.setAllRead(loginName);
      } else if (isReadCount != null && isReadCount.intValue() > 0) {
        ALActivityService.setRead(isReadCount, loginName);
      }
      ALActivityCount count =
        ALActivityService.count(new ALActivityGetRequest().withTargetLoginName(
          loginName).withRead(0).withPriority(1f));
      json = new JSONObject();
      json.put("unreadCount", count.getCount());
      json.put("max", count.getMax());

      jsonArray = new JSONArray();
      if (max != null
        && count.getCount() > 0
        && count.getMax() > max.longValue()) {
        ResultList<ALActivity> list =
          ALActivityService.getList(new ALActivityGetRequest()
            .withTargetLoginName(loginName)
            .withRead(0)
            .withPriority(1f)
            .withMax(max.longValue()));
        for (ALActivity activity : list) {
          JSONObject activityJson = new JSONObject();
          activityJson.put("displayName", activity.getDisplayName().getValue());
          activityJson.put("icon", activity.getIcon() == null ? "" : activity
            .getIcon()
            .getValue());
          activityJson.put("text", activity.getTitle().getValue());
          activityJson.put("appId", activity.getAppId().getValue());
          jsonArray.add(activityJson);
        }
      }
      json.put("activities", jsonArray);

      result = json.toString();
    } catch (Exception e) {
      logger.error("CheckActivityJSONScreen.getJSONString", e);
    }

    return result;
  }
}

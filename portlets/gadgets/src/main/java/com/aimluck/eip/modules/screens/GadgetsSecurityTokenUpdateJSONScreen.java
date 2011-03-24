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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.social.gadgets.ALGadgetContext;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 
 */
public class GadgetsSecurityTokenUpdateJSONScreen extends ALJSONScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GadgetsSecurityTokenUpdateJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    JSONArray result = new JSONArray();

    try {
      String payload = getPayload(rundata);
      JSONArray jsonArray = JSONArray.fromObject(payload);

      if (jsonArray != null) {

        ALEipUser user = ALEipUtils.getALEipUser(rundata);
        String orgId = Database.getDomainName();
        String viewer =
          new StringBuilder(orgId)
            .append(":")
            .append(user.getName().getValue())
            .toString();

        Object[] array = jsonArray.toArray();
        for (Object obj : array) {
          JSONObject jsonObject = JSONObject.fromObject(obj);
          Long mid = jsonObject.getLong("id");
          String appId = jsonObject.getString("appId");
          String portletId = jsonObject.getString("portletId");
          String specUrl = jsonObject.getString("specUrl");
          String activeUrl = jsonObject.getString("activeUrl");

          ALGadgetContext gadgetContext =
            new ALGadgetContext(rundata, viewer, appId, specUrl, mid, activeUrl);

          JSONObject resultObj = new JSONObject();
          resultObj.put("id", mid);
          resultObj.put("appId", appId);
          resultObj.put("portletId", portletId);
          resultObj.put("specUrl", specUrl);
          resultObj.put("secureToken", gadgetContext.getSecureToken());
          resultObj.put("activeUrl", activeUrl);
          result.add(resultObj);
        }

      }
    } catch (Exception e) {
      logger.error("[GadgetsSecurityTokenUpdateJSONScreen]", e);
    }

    return result.toString();
  }

  @Override
  protected String getPrefix() {
    return "";
  }

  @Override
  protected String getSuffix() {
    return "";
  }
}

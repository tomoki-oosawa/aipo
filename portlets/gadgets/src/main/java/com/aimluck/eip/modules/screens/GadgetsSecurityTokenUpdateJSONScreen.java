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

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.social.ALApplicationService;
import com.aimluck.eip.services.social.gadgets.ALGadgetContext;
import com.aimluck.eip.services.social.gadgets.ALGadgetSpec;
import com.aimluck.eip.util.ALEipUtils;
import com.google.inject.internal.Lists;

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
      String view = rundata.getParameters().getString("view");
      if (!"home".equals(view)
        && !"canvas".equals(view)
        && !"popup".equals(view)
        && !"default".equals(view)) {
        view = "home";
      }
      String update = rundata.getParameters().getString("update");
      boolean isUpdate = "1".equals(update);
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
        List<String> urls = Lists.newArrayList();
        for (Object obj : array) {
          JSONObject jsonObject = JSONObject.fromObject(obj);
          String specUrl = jsonObject.getString("specUrl");
          urls.add(specUrl);
        }
        Map<String, ALGadgetSpec> metaData =
          ALApplicationService.getMetaData(urls, view, false, false);
        for (Object obj : array) {
          JSONObject jsonObject = JSONObject.fromObject(obj);
          Long mid = jsonObject.getLong("id");
          String appId = jsonObject.getString("appId");
          String portletId = jsonObject.getString("portletId");
          String specUrl = jsonObject.getString("specUrl");
          String activeUrl = jsonObject.getString("activeUrl");

          ALGadgetSpec spec = metaData.get(specUrl);

          ALGadgetContext gadgetContext = null;
          if (isUpdate) {
            gadgetContext =
              new ALGadgetContext(
                rundata,
                viewer,
                appId,
                specUrl,
                mid,
                activeUrl);
          }
          JSONObject resultObj = new JSONObject();
          resultObj.put("id", mid);
          resultObj.put("appId", appId);
          resultObj.put("portletId", portletId);
          resultObj.put("specUrl", specUrl);
          resultObj.put("secureToken", isUpdate ? gadgetContext
            .getSecureToken() : null);
          resultObj.put("activeUrl", activeUrl);
          resultObj.put("height", spec == null ? 200 : spec.getHeight());
          resultObj.put("scrolling", spec == null ? false : spec.isScrolling());
          resultObj.put("views", spec == null ? null : spec.get("views"));
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

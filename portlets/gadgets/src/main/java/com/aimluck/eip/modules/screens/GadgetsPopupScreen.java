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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.jetspeed.portal.Portlet;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALApplication;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.gadgets.util.GadgetsUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.social.ALApplicationService;
import com.aimluck.eip.services.social.ALContainerConfigService;
import com.aimluck.eip.services.social.ALSocialApplicationHandler.Property;
import com.aimluck.eip.services.social.gadgets.ALGadgetContext;
import com.aimluck.eip.services.social.model.ALApplicationGetRequest;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 *
 */
public class GadgetsPopupScreen extends ALVelocityScreen {

  /**
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {
    context.put("l10n", ALLocalizationUtils.createLocalization(rundata));

    String appId = rundata.getParameters().getString("aid");
    ALApplication app =
      ALApplicationService.get(new ALApplicationGetRequest().withAppId(appId));
    if (app == null) {
      context.put("isActive", false);
      context.put("title", "");
      String template = "portlets/html/gadgets-popup.vm";
      setTemplate(rundata, context, template);
      return;
    }
    ALStringField title = app.getTitle();
    String url = app.getUrl().getValue();
    boolean isActive = app.getStatus() == 1;

    ALEipUser user = ALEipUtils.getALEipUser(rundata);
    String orgId = Database.getDomainName();
    String viewer =
      new StringBuilder(orgId)
        .append(":")
        .append(user.getName().getValue())
        .toString();

    Long moduleId = rundata.getParameters().getLong("mid");
    Map<String, Object> maps = new HashMap<String, Object>();
    if (moduleId != null) {
      Portlet portlet =
        ALEipUtils.getPortlet(rundata, String.valueOf(moduleId));
      if (portlet != null) {
        @SuppressWarnings("unchecked")
        Iterator<String> names =
          portlet.getPortletConfig().getInitParameterNames();
        while (names.hasNext()) {
          String next = names.next();
          if (next != null && next.startsWith("pref-")) {
            String value = portlet.getPortletConfig().getInitParameter(next);
            String key = next.substring(5);
            Map<String, String> maps2 = new HashMap<String, String>();
            maps2.put("value", value);
            maps.put(key, maps2);
          }
        }
      } else {
        moduleId = 0l;
      }
    } else {
      moduleId = 0l;
    }

    ALGadgetContext gadgetContext =
      new ALGadgetContext(rundata, viewer, appId, url, moduleId);

    context.put("gadgetContext", gadgetContext);
    context.put("isActive", isActive);

    String id =
      moduleId == 0 ? String.valueOf(System.nanoTime()) : String
        .valueOf(moduleId);
    context.put("portletId", id);
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("portletId", id);
    jsonObject.put("appId", appId);
    jsonObject.put("specUrl", gadgetContext.getAppUrl());
    jsonObject.put("secureToken", gadgetContext.getSecureToken());
    jsonObject.put("serverBase", gadgetContext.getServerBase());
    jsonObject.put("width", "100%");
    jsonObject.put("rpcRelay", "files/container/rpc_relay.html");
    jsonObject.put("userPrefs", JSONObject.fromObject(maps));

    String externalId = rundata.getParameters().getString("eid");
    if (externalId != null && externalId.length() > 0) {
      JSONObject externalJson = new JSONObject();
      externalJson.put("externalId", externalId);
      jsonObject.put("viewParams", externalJson);
    }
    context.put("assignData", jsonObject.toString());
    context.put("title", title);
    context.put("nocache", "true".equals(ALContainerConfigService
      .get(Property.CACHE_GADGET_XML)) ? "0" : "1");

    String template = "portlets/html/gadgets-popup.vm";
    setTemplate(rundata, context, template);

  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return GadgetsUtils.GADGETS_PORTLET_NAME;
  }

}

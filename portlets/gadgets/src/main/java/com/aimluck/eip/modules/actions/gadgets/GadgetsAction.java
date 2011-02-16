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

package com.aimluck.eip.modules.actions.gadgets;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.social.ALApplicationService;
import com.aimluck.eip.services.social.gadgets.ALGadgetContext;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 *
 */
public class GadgetsAction extends ALBaseAction {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GadgetsAction.class.getName());

  /**
   * 
   * @param portlet
   * @param context
   * @param rundata
   * @throws java.lang.Exception
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {
    buildCommonContext(portlet, context, rundata, false);
    context.put("view", "home");
  }

  /**
   * 
   * @param portlet
   * @param context
   * @param rundata
   */
  @Override
  protected void buildMaximizedContext(VelocityPortlet portlet,
      Context context, RunData rundata) {
    buildCommonContext(portlet, context, rundata, true);
  }

  protected void buildCommonContext(VelocityPortlet portlet, Context context,
      RunData rundata, boolean isMaximized) {

    String appId = rundata.getParameters().getString("aid");
    if (appId == null || appId.length() == 0) {
      appId = portlet.getPortletConfig().getInitParameter("aid");
    }
    String url = portlet.getPortletConfig().getInitParameter("url");
    boolean isActive = true;
    if (url == null || url == "") {
      url = portlet.getPortletConfig().getInitParameter("p1a-url");
      appId = url;
    } else {
      isActive = checkApplicationAvailability(appId);
    }

    ALEipUser user = ALEipUtils.getALEipUser(rundata);
    String orgId = Database.getDomainName();
    String viewer =
      new StringBuilder(orgId)
        .append(":")
        .append(user.getName().getValue())
        .toString();

    ALGadgetContext gadgetContext = new ALGadgetContext(rundata, viewer, url);

    context.put("gadgetContext", gadgetContext);
    context.put("isActive", isActive);

    @SuppressWarnings("unchecked")
    Iterator<String> names = portlet.getPortletConfig().getInitParameterNames();
    Map<String, Object> maps = new HashMap<String, Object>();
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
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("portletId", portlet.getID());
    jsonObject.put("specUrl", gadgetContext.getAppUrl());
    jsonObject.put("secureToken", gadgetContext.getSecureToken());
    jsonObject.put("serverBase", gadgetContext.getServerBase());
    jsonObject.put("height", "200");
    jsonObject.put("width", "100%");
    jsonObject.put("rpcRelay", "files/container/rpc_relay.html");
    jsonObject.put("userPrefs", JSONObject.fromObject(maps));
    context.put("assignData", jsonObject.toString());
    setTemplate(rundata, isMaximized ? "gadgets-list" : "gadgets");
  }

  protected boolean checkApplicationAvailability(String appId) {
    return ALApplicationService.checkAvailability(appId);
  }
}

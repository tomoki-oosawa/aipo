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

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALApplication;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.social.ALApplicationService;
import com.aimluck.eip.services.social.gadgets.ALGadgetContext;
import com.aimluck.eip.services.social.model.ALApplicationGetRequest;
import com.aimluck.eip.util.ALEipUtils;

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

    String appId = rundata.getParameters().getString("aid");
    ALApplication app =
      ALApplicationService.get(new ALApplicationGetRequest().withAppId(appId));
    if (app == null) {
      throw new ALPageNotFoundException();
    }
    String url = app.getUrl().getValue();
    boolean isActive = app.getStatus() == 1;

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

    Map<String, Object> maps = new HashMap<String, Object>();

    String id = String.valueOf(System.nanoTime());
    context.put("portletId", id);
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("portletId", id);
    jsonObject.put("specUrl", gadgetContext.getAppUrl());
    jsonObject.put("secureToken", gadgetContext.getSecureToken());
    jsonObject.put("serverBase", gadgetContext.getServerBase());
    jsonObject.put("width", "100%");
    jsonObject.put("rpcRelay", "files/container/rpc_relay.html");
    jsonObject.put("userPrefs", JSONObject.fromObject(maps));
    context.put("assignData", jsonObject.toString());

    String template = "portlets/html/ja/gadgets-popup.vm";
    setTemplate(rundata, context, template);
  }

}

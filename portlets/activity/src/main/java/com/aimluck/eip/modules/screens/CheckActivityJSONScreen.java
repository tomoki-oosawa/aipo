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

import net.sf.json.JSONObject;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

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

    try {
      String loginName = ALEipUtils.getALEipUser(rundata).getName().getValue();
      Integer isRead = null;
      try {
        isRead = rundata.getParameters().getInteger("isRead");
      } catch (Throwable t) {

      }
      if (isRead != null & isRead.intValue() > 0) {
        ALActivityService.setRead(isRead, loginName);
      }
      int count =
        ALActivityService.count(new ALActivityGetRequest().withTargetLoginName(
          loginName).withRead(0));
      json = new JSONObject();
      json.put("unreadCount", count);

      result = json.toString();
    } catch (Exception e) {
      logger.error("[CheckActivityJSONScreen]", e);
    }

    return result;
  }
}

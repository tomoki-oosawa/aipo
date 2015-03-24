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

import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.util.template.BaseJetspeedLink;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.services.config.ALConfigHandler.Property;
import com.aimluck.eip.services.config.ALConfigService;

/**
 * 通信中にタイムアウトした場合の処理クラスです。 <br />
 * 
 */
public class ALVelocityTimeoutScreen extends ALVelocityScreen implements
    ALErrorScreen {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALVelocityTimeoutScreen.class.getName());

  @Override
  protected void doOutput(RunData rundata, Context context) {
    String layout_template = "screens/html/AjaxTimeout.vm";
    String externalLoginUrl = ALConfigService.get(Property.EXTERNAL_LOGIN_URL);
    if ("".equals(externalLoginUrl)) {
      BaseJetspeedLink jslink = (BaseJetspeedLink) context.get("jslink");
      Portlet portlet = (Portlet) context.get("portlet");
      if (jslink == null || portlet == null) {
        context.put("redirectUrl", "./");
      } else {
        context.put("redirectUrl", jslink
          .getPortletById(portlet.getID())
          .toString());
      }
    } else {
      context.put("redirectUrl", externalLoginUrl);
    }
    setTemplate(rundata, context, layout_template);
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return null;
  }
}

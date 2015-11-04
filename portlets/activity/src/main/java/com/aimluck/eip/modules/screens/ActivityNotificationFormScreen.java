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

import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.activity.ActivityNotificationFormData;
import com.aimluck.eip.activity.util.ActivityUtils;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class ActivityNotificationFormScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ActivityNotificationFormScreen.class.getName());

  /**
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {
    String portletId = ActivityUtils.getGlobalPortletId(rundata);
    Portlet portlet = ALEipUtils.getPortlet(rundata, portletId);
    String desktopNotificationParam =
      portlet.getPortletConfig().getInitParameter("desktopNotification");
    context.put("desktopNotification", "T".equals(desktopNotificationParam));
    String support = rundata.getParameters().getString("s");
    context.put("notifySupport", "1".equals(support));
    String permission = rundata.getParameters().getString("p");
    context.put("notifyPermission", "0".equals(permission)
      || "granted".equals(permission));
    context.put("notifyPermissionDefault", "1".equals(permission)
      || "default".equals(permission));
    String recommend = rundata.getParameters().getString("r");
    context.put("recommend", "1".equals(recommend));
    ALEipUtils.setupContext(portletId, rundata, context);
    ALEipUtils.setTemp(rundata, context, ALEipConstants.ENTITY_ID, "1");
    ActivityNotificationFormData formData = new ActivityNotificationFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);
    String layout_template = "portlets/html/ajax-activity-notification-form.vm";
    setTemplate(rundata, context, layout_template);
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return ActivityUtils.ACTIVITY_PORTLET_NAME;
  }

}

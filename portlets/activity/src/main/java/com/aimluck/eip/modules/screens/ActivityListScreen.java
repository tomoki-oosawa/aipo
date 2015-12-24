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

import com.aimluck.eip.activity.ActivitySelectData;
import com.aimluck.eip.activity.util.ActivityUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class ActivityListScreen extends ALVelocityScreen {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ActivityListScreen.class.getName());

  /**
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    try {
      String portletId = ActivityUtils.getGlobalPortletId(rundata);
      context.put("portletId", ActivityUtils.getGlobalPortletId(rundata));
      Portlet portlet = ALEipUtils.getPortlet(rundata, portletId);
      String desktopNotificationParam =
        portlet.getPortletConfig().getInitParameter("desktopNotification");
      context.put("desktopNotification", "T".equals(desktopNotificationParam));
      String support = rundata.getParameters().getString("s");
      context.put("notifySupport", "1".equals(support));
      String permission = rundata.getParameters().getString("p");
      context.put("notifyPermission", "0".equals(permission)
        || "granted".equals(permission));

      ActivitySelectData listData = new ActivitySelectData();
      listData.initField();
      listData.setRowsNum(5);
      listData.doViewList(this, rundata, context);
      String layout_template = "portlets/html/ajax-activity-list.vm";
      setTemplate(rundata, context, layout_template);
    } catch (Exception ex) {
      logger.error("ActivityListScreen.doOutput", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return ActivityUtils.ACTIVITY_PORTLET_NAME;
  }
}

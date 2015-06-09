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

import net.sf.json.JSONObject;

import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.portal.PortletInstance;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.persistence.PersistenceManager;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.activity.util.ActivityUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class ActivityNotifyEnableJSONScreen extends ALJSONScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ActivityNotifyEnableJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    String result = "";
    JSONObject json;

    try {

      String notify = rundata.getParameters().getString("enable");
      Boolean notifyValue = null;
      if ("T".equals(notify)) {
        notifyValue = true;
      } else if ("F".equals(notify)) {
        notifyValue = false;
      }
      String portletId = ActivityUtils.getGlobalPortletId(rundata);
      String desktopNotificationParam = null;
      if (portletId != null) {
        Portlet p = ALEipUtils.getPortlet(rundata, portletId);
        Profile profile = ((JetspeedRunData) rundata).getProfile();

        PortletInstance instance = PersistenceManager.getInstance(p, rundata);

        if (notifyValue != null) {
          // save all the changes
          try {
            instance.setAttribute("desktopNotification", notifyValue
              ? "T"
              : "F");
            profile.setDocument(instance.getDocument());
            profile.store();
            p.init();
            org.apache.jetspeed.util.PortletSessionState
              .setPortletConfigChanged(p, rundata);
          } catch (PortletException e) {
            logger.error("Customizer failed to reinitialize the portlet "
              + p.getName(), e);
          } catch (Exception e) {
            logger.error("Unable to save profile ", e);
          }
        }

        desktopNotificationParam =
          p.getPortletConfig().getInitParameter("desktopNotification");
      }
      json = new JSONObject();
      json.put("enable", notifyValue != null
        ? notifyValue.booleanValue()
        : (desktopNotificationParam == null ? true : "T"
          .equals(desktopNotificationParam)));
      result = json.toString();
    } catch (Exception e) {
      logger.error("ActivityNotifyEnableJSONScreen.getJSONString", e);
    }

    return result;
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

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

package com.aimluck.eip.activity;

import java.util.List;

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

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.activity.util.ActivityUtils;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class ActivityNotificationFormData extends ALAbstractFormData {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ActivityNotificationFormData.class.getName());

  private ALStringField desktopNotification;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
  }

  /**
     *
     */
  @Override
  public void initField() {
    desktopNotification = new ALStringField();
  }

  /**
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected void setValidator() throws ALPageNotFoundException,
      ALDBErrorException {
  }

  /**
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean validate(List<String> msgList)
      throws ALPageNotFoundException, ALDBErrorException {
    return true;
  }

  /**
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {

    String portletId = ActivityUtils.getGlobalPortletId(rundata);
    Portlet portlet = ALEipUtils.getPortlet(rundata, portletId);
    String desktopNotificationParam =
      portlet.getPortletConfig().getInitParameter("desktopNotification");
    if ("F".equals(desktopNotificationParam)
      || "T".equals(desktopNotificationParam)) {
      desktopNotification.setValue(desktopNotificationParam);
    } else {
      desktopNotification.setValue("T");
    }
    return true;
  }

  /**
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {

    return true;
  }

  /**
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    String portletId = ActivityUtils.getGlobalPortletId(rundata);
    String desktopNotificationParam = null;
    if (portletId != null) {
      Portlet p = ALEipUtils.getPortlet(rundata, portletId);
      Profile profile = ((JetspeedRunData) rundata).getProfile();
      PortletInstance instance = PersistenceManager.getInstance(p, rundata);
      desktopNotificationParam = desktopNotification.getValue();
      if ("F".equals(desktopNotificationParam)
        || "T".equals(desktopNotificationParam)) {
        // save all the changes
        try {
          instance
            .setAttribute("desktopNotification", desktopNotificationParam);
          profile.setDocument(instance.getDocument());
          profile.store();
          p.init();
          org.apache.jetspeed.util.PortletSessionState.setPortletConfigChanged(
            p,
            rundata);
        } catch (PortletException e) {
          logger.error("Customizer failed to reinitialize the portlet "
            + p.getName(), e);
          return false;
        } catch (Exception e) {
          logger.error("Unable to save profile ", e);
          return false;
        }
      }
    }
    return true;
  }

  /**
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  /**
   * @return desktopNotification
   */
  public ALStringField getDesktopNotification() {
    return desktopNotification;
  }

  /**
   * @param desktopNotification
   *          セットする desktopNotification
   */
  public void setDesktopNotification(String desktopNotification) {
    this.desktopNotification.setValue(desktopNotification);
  }

}

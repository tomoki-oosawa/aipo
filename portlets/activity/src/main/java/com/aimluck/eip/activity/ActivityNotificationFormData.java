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
package com.aimluck.eip.activity;

import java.util.ArrayList;
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

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.activity.util.ActivityUtils;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.reminder.ALReminderHandler.ReminderCategory;
import com.aimluck.eip.services.reminder.ALReminderHandler.ReminderNotifyType;
import com.aimluck.eip.services.reminder.ALReminderService;
import com.aimluck.eip.services.reminder.model.ALReminderDefaultItem;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 *
 */
public class ActivityNotificationFormData extends ALAbstractFormData {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ActivityNotificationFormData.class.getName());

  private ALStringField desktopNotification;

  /** <code>public_flag</code> 通知フラグ */
  private ALStringField reminder_flag;

  private ALStringField notify_type_mail;

  private ALStringField notify_type_message;

  private ALNumberField notify_timing;

  private String orgId;

  private ALEipUser loginUser;

  private ALReminderDefaultItem defaultItem;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
    orgId = Database.getDomainName();
    loginUser = ALEipUtils.getALEipUser(rundata);
    if (ALReminderService.isEnabled()) {
      defaultItem =
        ALReminderService.getDefault(orgId, loginUser
          .getUserId()
          .getValueAsString(), ReminderCategory.SCHEDULE);
    }
  }

  /**
     *
     */
  @Override
  public void initField() {
    desktopNotification = new ALStringField();

    reminder_flag = new ALStringField();
    reminder_flag.setTrim(true);
    reminder_flag.setValue("F");

    notify_type_mail = new ALStringField();

    notify_type_message = new ALStringField();

    notify_timing = new ALNumberField();
    notify_timing.setValue(0);
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

    if (ALReminderService.isEnabled()) {
      if (reminder_flag.getValue().equals("T")
        && ((notify_type_mail.getValue() == null || !notify_type_mail
          .getValue()
          .equals("TRUE")) && (notify_type_message.getValue() == null || !notify_type_message
          .getValue()
          .equals("TRUE")))) {
        msgList.add(ALLocalizationUtils
          .getl10n("SCHEDULE_MESSAGE_SELECT_REMINDER_ON"));
      }
      if (reminder_flag.getValue().equals("T")
        && !ActivityUtils.notifyTimingList.contains(notify_timing
          .getValueWithInt())) {
        msgList.add(ALLocalizationUtils
          .getl10n("SCHEDULE_MESSAGE_SELECT_REMINDER_ONTIME"));
      }
    }
    return (msgList.size() == 0);
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
    if (ALReminderService.isEnabled()) {
      if (defaultItem != null) {
        if (defaultItem.isEnabled()) {
          reminder_flag.setValue("T");
        }
        List<ReminderNotifyType> list = defaultItem.getNotifyType();
        if (list != null && list.size() > 0) {
          if (list.contains(ReminderNotifyType.MAIL)) {
            notify_type_mail.setValue("TRUE");
          }
          if (list.contains(ReminderNotifyType.MESSAGE)) {
            notify_type_message.setValue("TRUE");
          }
        }
        notify_timing.setValue(Long.valueOf(defaultItem.getNotifyTiming()));
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

    if (ALReminderService.isEnabled()) {
      try {
        if (reminder_flag.getValue() != null) {
          if (defaultItem == null) {
            defaultItem = new ALReminderDefaultItem();
          }
          defaultItem.setOrgId(orgId);
          defaultItem.setUserId(loginUser.getUserId().getValueAsString());
          defaultItem.setCategory(ReminderCategory.SCHEDULE);
          if (reminder_flag.getValue().equals("T")) {
            defaultItem.setEnabled(true);
            ArrayList<ReminderNotifyType> list =
              new ArrayList<ReminderNotifyType>();
            if (notify_type_mail.getValue() != null
              && notify_type_mail.getValue().equals("TRUE")) {
              list.add(ReminderNotifyType.MAIL);
            }
            if (notify_type_message.getValue() != null
              && notify_type_message.getValue().equals("TRUE")) {
              list.add(ReminderNotifyType.MESSAGE);
            }
            defaultItem.setNotifyType(list);
            defaultItem.setNotifyTiming(notify_timing.getValueWithInt());
            ALReminderService.updateDefault(defaultItem);
          } else {
            defaultItem.setEnabled(false);
            ALReminderService.updateDefault(defaultItem);
          }
        }
      } catch (Exception e) {
        logger.error("Unable to save ReminderService ", e);
        return false;
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

  public ALStringField getReminderFlag() {
    return reminder_flag;
  }

  public ALStringField getNotifyTypeMail() {
    return notify_type_mail;
  }

  public ALStringField getNotifyTypeMessage() {
    return notify_type_message;
  }

  public ALNumberField getNotifyTiming() {
    return notify_timing;
  }

  public boolean isReminderEnabled() {
    return ALReminderService.isEnabled();
  }
}

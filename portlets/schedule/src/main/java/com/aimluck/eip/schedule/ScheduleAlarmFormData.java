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
package com.aimluck.eip.schedule;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.services.reminder.ALReminderHandler.ReminderCategory;
import com.aimluck.eip.services.reminder.ALReminderHandler.ReminderNotifyType;
import com.aimluck.eip.services.reminder.ALReminderService;
import com.aimluck.eip.services.reminder.model.ALReminderItem;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * スケジュールのフォームデータを管理するクラスです。
 *
 */
public class ScheduleAlarmFormData extends ALAbstractFormData {

  /** <code>logger</code> logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleAlarmFormData.class.getName());

  /** <code>public_flag</code> 通知フラグ */
  private ALStringField reminder_flag;

  private ALStringField notify_type_mail;

  private ALStringField notify_type_message;

  private ALNumberField notify_timing;

  /** <code>login_user</code> ログインユーザー */
  private ALEipUser loginUser;

  private String orgId;

  private ScheduleDetailResultData rd;

  /** <code>end_date</code> 終了日時 */
  private ALDateTimeField view_date;

  private ScheduleDetailOnedaySelectData ondaySelectData = null;

  /**
   *
   * @param action
   * @param rundata
   * @param context
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    // スーパークラスのメソッドを呼び出す。
    super.init(action, rundata, context);

    orgId = Database.getDomainName();
    loginUser = ALEipUtils.getALEipUser(rundata);
    rd = new ScheduleDetailResultData();

    view_date = new ALDateTimeField("yyyy-MM-dd");
    if (ALEipUtils.isMatch(rundata, context)) {
      if (rundata.getParameters().containsKey("view_date")) {
        String tmpViewDate = rundata.getParameters().getString("view_date");
        view_date.setValue(tmpViewDate);
        if (!view_date.validate(new ArrayList<String>())) {
          logger.debug("[ScheduleSelectData] Parameter cannot validate");
          ALEipUtils.redirectPageNotFound(rundata);
          return;
        }
      }
    }
    ondaySelectData = new ScheduleDetailOnedaySelectData();
    ondaySelectData.initField();
    ondaySelectData.doSelectList(action, rundata, context);

  }

  /*
   *
   */
  @Override
  public void initField() {
    reminder_flag = new ALStringField();
    reminder_flag.setTrim(true);
    reminder_flag.setValue("F");

    notify_type_mail = new ALStringField();

    notify_type_message = new ALStringField();

    notify_timing = new ALNumberField();
  }

  /**
   *
   * @param msgList
   * @return
   * @throws ALDBErrorException
   * @throws ALPageNotFoundException
   */
  @Override
  protected boolean validate(List<String> msgList) throws ALDBErrorException,
      ALPageNotFoundException {
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
        && !ScheduleUtils.notifyTimingList.contains(notify_timing
          .getValueWithInt())) {
        msgList.add(ALLocalizationUtils
          .getl10n("SCHEDULE_MESSAGE_SELECT_REMINDER_ONTIME"));

      }
    }

    return (msgList.size() == 0);
  }

  /**
   *
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
    try {
      // オブジェクトモデルを取得
      EipTSchedule schedule =
        ScheduleUtils.getEipTScheduleDetail(
          rundata,
          context,
          ScheduleUtils.SCHEDULEMAP_TYPE_USER);
      if (schedule == null) {
        return false;
      }
      // 自分に関係のある予定なのかどうか判断する
      List<ALEipUser> memberList = ScheduleUtils.getEffectiveUsers(schedule);
      boolean isMember = false;
      for (ALEipUser member : memberList) {
        if (member.getUserId().getValue() == loginUser.getUserId().getValue()) {
          isMember = true;
          break;
        }
      }
      if (!isMember) {
        return false;
      }
      rd =
        ScheduleUtils.getResultDataDetail(
          schedule,
          view_date,
          loginUser.getUserId().getValueWithInt(),
          loginUser.getUserId().getValueWithInt(),
          ScheduleUtils.SCHEDULEMAP_TYPE_USER,
          false,
          ondaySelectData);
      if (ALReminderService.isEnabled()) {
        ALReminderItem item =
          ALReminderService.getJob(orgId, loginUser
            .getUserId()
            .getValueAsString(), ReminderCategory.SCHEDULE, schedule
            .getScheduleId()
            .intValue());
        if (item != null) {
          reminder_flag.setValue("T");
          List<ReminderNotifyType> list = item.getNotifyType();
          if (list != null && list.size() > 0) {
            if (list.contains(ReminderNotifyType.MAIL)) {
              notify_type_mail.setValue("TRUE");
            }
            if (list.contains(ReminderNotifyType.MESSAGE)) {
              notify_type_message.setValue("TRUE");
            }
          }
          notify_timing.setValue(Long.valueOf(item.getNotifyTiming()));
        } else {
          reminder_flag.setValue("F");
          notify_type_mail.setValue("FALSE");
          notify_type_message.setValue("FALSE");
          notify_timing.setValue(0L);
        }
      }

    } catch (Exception e) {
      logger.error("[ScheduleAlarmFormData]", e);
      throw new ALDBErrorException();

    }
    return true;
  }

  /**
   *
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

    if (ALReminderService.isEnabled()) {
      // オブジェクトモデルを取得
      EipTSchedule schedule =
        ScheduleUtils.getEipTSchedule(rundata, context, false);
      if (schedule == null) {
        return false;
      }
      // 自分に関係のある予定なのかどうか判断する
      List<ALEipUser> memberList = ScheduleUtils.getEffectiveUsers(schedule);
      boolean isMember = false;
      for (ALEipUser member : memberList) {
        if (member.getUserId().getValue() == loginUser.getUserId().getValue()) {
          isMember = true;
          break;
        }
      }
      if (!isMember) {
        return false;
      }
      if ("T".equals(reminder_flag.getValue())) {
        boolean isMail = false;
        boolean isMessage = false;
        if ("TRUE".equals(notify_type_mail.getValue())) {
          isMail = true;
        }
        if ("TRUE".equals(notify_type_message.getValue())) {
          isMessage = true;
        }
        ScheduleUtils.setupReminderJob(
          Database.getDomainName(),
          loginUser.getUserId().toString(),
          schedule,
          notify_timing.getValueWithInt(),
          isMail,
          isMessage);
      } else {
        ALReminderItem item = new ALReminderItem();
        item.setOrgId(Database.getDomainName());
        item.setUserId(loginUser.getUserId().toString());
        item.setItemId(schedule.getScheduleId().intValue());
        item.setCategory(ReminderCategory.SCHEDULE);
        ALReminderService.removeJob(item);
      }
    }
    return true;
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
    return false;
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

  public boolean isReminderEnabled() {
    return ALReminderService.isEnabled();
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

  public ScheduleDetailResultData getDetail() {
    return rd;
  }

  /**
   *
   * @return
   */
  public ALDateTimeField getViewDate() {
    return view_date;
  }
}

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

package com.aimluck.eip.schedule;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.account.EipMUserHoliday;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.schedule.util.ScheduleHolidayUtils;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.timeline.util.TimelineUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * ワークフローカテゴリのフォームデータを管理するクラスです。 <BR>
 * 
 */
public class ScheduleHolidayFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleHolidayFormData.class.getName());

  /** 休日名 */
  private ALStringField holiday_title;

  /** 日付 */
  private ALDateTimeField holiday_date;

  /** 作成者名 */
  private ALNumberField create_user_id;

  /** 更新者名 */
  private ALNumberField update_user_id;

  /** 休日一覧 */
  private List<ScheduleHolidayResultData> holidayList;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * 
   * 
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
  }

  /**
   *
   *
   */
  @Override
  public void initField() {
    // 休日名
    holiday_title = new ALStringField();
    holiday_title.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_COMPANY_HOLIDAY"));
    holiday_title.setTrim(true);

    holiday_date = new ALDateTimeField("yyyy-MM-dd");
    holiday_date.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_HOLIDAY_DATE"));
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
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean res = super.setFormData(rundata, context, msgList);
    if (res) {
      try {
        if (holiday_date.toString().equals("")) {
          Calendar cal = Calendar.getInstance();
          cal.set(Calendar.MINUTE, (int) Math
            .floor(cal.get(Calendar.MINUTE) / 5.0) * 5); // 5分刻みに調整
          holiday_date.setValue(cal.getTime());
        } else {
          holiday_date.setValue(holiday_date.getValue());
        }
      } catch (Exception ex) {
        logger.error("report", ex);
      }
    }
    return res;
  }

  /**
   * ワークフローカテゴリの各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    holiday_title.setNotNull(true);
    holiday_date.setNotNull(true);
  }

  /**
   * ワークフローカテゴリのフォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {
    holiday_title.validate(msgList);
    holiday_date.validate(msgList);
    return (msgList.size() == 0);
  }

  /**
   * 休日一覧を取得します。 <BR>
   * 
   * @return
   */
  public List<ScheduleHolidayResultData> getHolidayList() {
    return holidayList;
  }

  /**
   * 
   * @param rundata
   * @param context
   */
  public void loadHolidayList(RunData rundata, Context context) {
    holidayList = ScheduleHolidayUtils.loadHolidayList(rundata, context);
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
    try {
      EipMUserHoliday holiday =
        ScheduleHolidayUtils.getEipMUserHoliday(rundata, context);
      if (holiday == null) {
        return false;
      }
      // タイトル
      holiday_title.setValue(holiday.getHolidayTitle());
      // 日付
      holiday_date.setValue(holiday.getHolidayDate());
      // 作成者
      create_user_id.setValue(holiday.getCreateUserId());
      // 更新者
      update_user_id.setValue(holiday.getUpdateUserId());
    } catch (Exception ex) {
      logger.error("holiday", ex);
      return false;
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
    try {
      // 新規オブジェクトモデル
      EipMUserHoliday holiday = Database.create(EipMUserHoliday.class);

      // 作成者ID
      int ownerid = ALEipUtils.getUserId(rundata);

      // タイトル
      holiday.setHolidayTitle(holiday_title.getValue());

      String tmpDate =
        rundata.getParameters().getString("holiday_date_year")
          + "-"
          + rundata.getParameters().getString("holiday_date_month")
          + "-"
          + rundata.getParameters().getString("holiday_date_day");
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      // 日付
      holiday.setHolidayDate(sdf.parse(tmpDate));
      // 作成者
      holiday.setCreateUserId(ownerid);
      // 更新者
      holiday.setUpdateUserId(ownerid);

      // ユーザーID
      holiday.setTurbineUser(ALEipUtils.getTurbineUser(ALEipUtils
        .getUserId(rundata)));

      Database.commit();
    } catch (Throwable t) {
      Database.rollback();
      logger.error("[ScheduleHolidayFormData]", t);
      return false;
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
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    try {
      // オブジェクトモデルを取得
      EipMUserHoliday holiday =
        ScheduleHolidayUtils.getEipMUserHoliday(rundata, context);
      if (holiday == null) {
        return false;
      }
      // 作成者ID
      int ownerid = ALEipUtils.getUserId(rundata);
      // タイトル
      holiday.setHolidayTitle(holiday_title.getValue());
      // 日付
      holiday.setHolidayDate(holiday_date.getValue());
      // 作成者
      holiday.setCreateUserId(holiday.getCreateUserId());
      // 更新者
      holiday.setUpdateUserId(ownerid);

      Database.commit();
    } catch (Throwable t) {
      Database.rollback();
      logger.error("[ScheduleHolidayFormData]", t);
      return false;
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
    try {
      // オブジェクトモデルを取得
      EipMUserHoliday holiday =
        ScheduleHolidayUtils.getEipMUserHoliday(rundata, context);
      if (holiday == null) {
        return false;
      }

      // entityIdの取得
      int entityId = holiday.getUserHolidayId();
      // タイトルの取得
      String holidayName = holiday.getHolidayTitle();

      // UserHolidayを削除
      Database.delete(holiday);
      Database.commit();

      TimelineUtils.deleteTimelineActivity(rundata, context, "holiday", holiday
        .getUserHolidayId()
        .toString());

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        entityId,
        ALEventlogConstants.PORTLET_TYPE_SCHEDULE,
        holidayName);

    } catch (Throwable t) {
      Database.rollback();
      logger.error("[ScheduleHolidayFormData]", t);
      return false;
    }
    return true;
  }

  /**
   * 休日名を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getHolidayTitle() {
    return holiday_title;
  }

  /**
   * 日付を取得します。<BR>
   * 
   * @return
   */
  public ALDateTimeField getHolidayDate() {
    return holiday_date;
  }

  /**
   * 作成者を取得します。<BR>
   * 
   * @return
   */
  public ALNumberField getCreateUserId() {
    return create_user_id;
  }

  /**
   * 更新者を取得します。<BR>
   * 
   * @return
   */
  public ALNumberField getUpdateUserId() {
    return update_user_id;
  }
}

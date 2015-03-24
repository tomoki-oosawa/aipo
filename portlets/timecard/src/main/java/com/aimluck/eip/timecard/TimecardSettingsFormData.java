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
package com.aimluck.eip.timecard;

import java.util.Calendar;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.eip.cayenne.om.portlet.EipTTimecardSettings;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.timecard.util.TimecardUtils;

/**
 * タイムカード集計のフォームデータを管理するためのクラスです。 
 * 
 */

public class TimecardSettingsFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(TimecardSettingsFormData.class.getName());

  /** 開始時刻 */
  private ALNumberField start_time_hour;

  private ALNumberField start_time_minute;

  /** 終了時刻 */
  private ALNumberField end_time_hour;

  private ALNumberField end_time_minute;

  private ALNumberField worktime_in;

  private ALNumberField resttime_in;

  private ALNumberField worktime_out;

  private ALNumberField resttime_out;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
  }

  /**
   *
   */
  public void initField() {

    start_time_hour = new ALNumberField();
    start_time_minute = new ALNumberField();
    end_time_hour = new ALNumberField();
    end_time_minute = new ALNumberField();

    worktime_in = new ALNumberField();
    worktime_in.setFieldName("勤務時間内の勤務時間");
    worktime_in.setNotNull(true);
    worktime_in.limitMinValue(0);

    resttime_in = new ALNumberField();
    resttime_in.setFieldName("勤務時間内の休憩時間");
    resttime_in.setNotNull(true);
    resttime_in.limitValue(0, 360);

    worktime_out = new ALNumberField();
    worktime_out.setFieldName("勤務時間外の勤務時間");
    worktime_out.setNotNull(true);
    worktime_out.limitMinValue(0);

    resttime_out = new ALNumberField();
    resttime_out.setFieldName("勤務時間外の休憩時間");
    resttime_out.setNotNull(true);
    resttime_out.limitValue(0, 360);
  }

  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    try {
      EipTTimecardSettings record =
        TimecardUtils.getEipTTimecardSettings(rundata, context);
      if (record == null) {
        return false;
      }

      start_time_hour = new ALNumberField(record.getStartHour());
      start_time_minute = new ALNumberField(record.getStartMinute());
      end_time_hour = new ALNumberField(record.getEndHour());
      end_time_minute = new ALNumberField(record.getEndMinute());

      worktime_in.setValue(record.getWorktimeIn());
      resttime_in.setValue(record.getResttimeIn());
      worktime_out.setValue(record.getWorktimeOut());
      resttime_out.setValue(record.getResttimeOut());
    } catch (Exception ex) {
      logger.error("timecard", ex);
      return false;
    }
    return true;
  }

  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    try {
      EipTTimecardSettings record =
        TimecardUtils.getEipTTimecardSettings(rundata, context);
      if (record == null) {
        return false;
      }

      record.setStartHour((int) start_time_hour.getValue());
      record.setStartMinute((int) start_time_minute.getValue());
      record.setEndHour((int) end_time_hour.getValue());
      record.setEndMinute((int) end_time_minute.getValue());
      record.setWorktimeIn((int) worktime_in.getValue());
      record.setResttimeIn((int) resttime_in.getValue());
      record.setWorktimeOut((int) worktime_out.getValue());
      record.setResttimeOut((int) resttime_out.getValue());

      // 更新日
      record.setUpdateDate(Calendar.getInstance().getTime());

      Database.commit();
    } catch (Exception ex) {
      Database.rollback();
      logger.error("timecard", ex);
      return false;
    }
    return true;
  }

  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  @Override
  protected void setValidator() throws ALPageNotFoundException,
      ALDBErrorException {
    start_time_hour.limitValue(0, 23);
    start_time_minute.limitValue(0, 59);
    end_time_hour.limitValue(0, 23);
    end_time_minute.limitValue(0, 59);

    worktime_in.limitValue(0, 480);
    worktime_out.limitValue(0, 480);
    resttime_in.limitValue(0, 480);
    resttime_out.limitValue(0, 480);
  }

  @Override
  protected boolean validate(List<String> msgList)
      throws ALPageNotFoundException, ALDBErrorException {

    worktime_in.validate(msgList);
    resttime_in.validate(msgList);
    worktime_out.validate(msgList);
    resttime_out.validate(msgList);

    return (msgList.size() == 0);
  }

  /**
   * 詳細データを取得する抽象メソッドです。
   * 
   * @param rundata
   * @param context
   * @return
   */
  protected Object selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return super.setFormData(rundata, context, msgList);

  }

  /**
   * 
   * @return
   */
  public ALNumberField getStartTimeHour() {
    return this.start_time_hour;
  }

  /**
   * 
   * @return
   */
  public ALNumberField getStartTimeMinute() {
    return this.start_time_minute;
  }

  /**
   * 
   * @return
   */
  public ALNumberField getEndTimeHour() {
    return this.end_time_hour;
  }

  /**
   * 
   * @return
   */
  public ALNumberField getEndTimeMinute() {
    return this.end_time_minute;
  }

  /**
   * 
   * @return
   */
  public ALNumberField getWorktimeIn() {
    return this.worktime_in;
  }

  /**
   * 
   * @return
   */
  public ALNumberField getWorktimeOut() {
    return this.worktime_out;
  }

  /**
   * 
   * @return
   */
  public ALNumberField getResttimeIn() {
    return this.resttime_in;
  }

  /**
   * 
   * @return
   */
  public ALNumberField getResttimeOut() {
    return this.resttime_out;
  }

}

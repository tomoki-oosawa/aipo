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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.common.ALData;

/**
 * タイムカードのResultDataです。
 * 
 */
public class TimecardResultData implements ALData {

  /** 現在時刻 */
  private ALStringField now_time;

  /** 出勤フラグ */
  private ALStringField work_flag;

  /** 勤務時刻 */
  private ALDateTimeField work_date;

  /** 修正フラグ */
  private ALStringField refix_flag;

  /** 一覧の日付 */
  private ALStringField list_date;

  /** 理由 */
  private ALStringField reason;

  /** タイムカードのID */
  private ALStringField timecard_id;

  /** ある一日の合計勤務時間 */
  private ALNumberField sum_work_date;

  /** 勤務開始時間 */
  private ALDateTimeField start_work_date;

  /**
   *
   */
  public void initField() {
    work_flag = new ALStringField();
    now_time = new ALStringField();
    work_date = new ALDateTimeField();
    refix_flag = new ALStringField();
    list_date = new ALStringField();
    timecard_id = new ALStringField();
    reason = new ALStringField();
    sum_work_date = new ALNumberField();
    start_work_date = new ALDateTimeField("yyyy-MM-dd");

  }

  /**
   * @return
   */
  public void setWorkFlag(String flag) {
    work_flag.setValue(flag);
  }

  /**
   * @param i
   */
  public void setNowTime(String time) {
    now_time.setValue(time);
  }

  /**
   * @param i
   */
  public void setWorkDate(Date date) {
    work_date.setValue(date);
    list_date.setValue(ALDateUtil.format(date, "MM月dd日"));
  }

  /**
   * @param i
   */
  public void setRefixFlag(Date create_date, Date update_date) {
    if (equalsToDate(create_date, update_date, true)) {
      refix_flag.setValue("0");
    } else {
      refix_flag.setValue("1");
    }
  }

  /**
   * @return
   */
  public void setTimecardId(long i) {
    timecard_id.setValue(Long.valueOf(i).toString());
  }

  /**
   * @param i
   */
  public void setReason(String str_reason) {
    reason.setValue(str_reason);
  }

  /**
   * @param i
   */
  public void setStartWorkDate(Date date_start_work_date) {
    start_work_date.setValue(date_start_work_date);
  }

  /**
   * 
   * @return
   */
  public void addSumWorkDate() {
    long count = sum_work_date.getValue();
    sum_work_date.setValue(count + 1);
  }

  /**
   * @return
   */
  public ALNumberField getSumWorkDate() {
    return sum_work_date;
  }

  /**
   * @return
   */
  public ALDateTimeField getStartWorkDate() {
    return start_work_date;
  }

  /**
   * @return
   */
  public ALStringField getWorkFlag() {
    return work_flag;
  }

  /**
   *
   */
  public ALStringField getNowTime() {
    return now_time;
  }

  /**
   *
   */
  public ALDateTimeField getWorkDate() {
    return work_date;
  }

  public String getWorkDateStr() {
    SimpleDateFormat sdf = new SimpleDateFormat("HH時mm分");
    return sdf.format(work_date.getValue());
  }

  /**
   *
   */
  public ALStringField getRefixFlag() {
    return refix_flag;
  }

  /**
   *
   */
  public ALStringField getListDate() {
    return list_date;
  }

  /**
   *
   */
  public ALStringField getTimecardId() {
    return timecard_id;
  }

  /**
   *
   */
  public ALStringField getReason() {
    return reason;
  }

  /**
   * 指定した2つの日付を比較する．
   * 
   * @param date1
   * @param date2
   * @param checkTime
   *          時間まで比較する場合，true．
   * @return 等しい場合，true．
   */
  private boolean equalsToDate(Date date1, Date date2, boolean checkTime) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date1);
    int date1Year = cal.get(Calendar.YEAR);
    int date1Month = cal.get(Calendar.MONTH) + 1;
    int date1Day = cal.get(Calendar.DATE);
    int date1Hour = cal.get(Calendar.HOUR);
    int date1Minute = cal.get(Calendar.MINUTE);
    int date1Second = cal.get(Calendar.SECOND);
    cal.setTime(date2);
    int date2Year = cal.get(Calendar.YEAR);
    int date2Month = cal.get(Calendar.MONTH) + 1;
    int date2Day = cal.get(Calendar.DATE);
    int date2Hour = cal.get(Calendar.HOUR);
    int date2Minute = cal.get(Calendar.MINUTE);
    int date2Second = cal.get(Calendar.SECOND);
    if (checkTime) {
      if (date1Year == date2Year
        && date1Month == date2Month
        && date1Day == date2Day
        && date1Hour == date2Hour
        && date1Minute == date2Minute
        && date1Second == date2Second) {
        return true;
      }
    } else {
      if (date1Year == date2Year
        && date1Month == date2Month
        && date1Day == date2Day) {
        return true;
      }
    }
    return false;
  }
}

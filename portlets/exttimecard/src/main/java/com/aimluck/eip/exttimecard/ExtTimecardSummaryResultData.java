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
package com.aimluck.eip.exttimecard;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aimluck.commons.field.ALDateField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.eip.cayenne.om.portlet.EipTTimecardSettings;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.exttimecard.util.ExtTimecardUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALCommonUtils;

/**
 * 一日分のタイムカード(出勤・退勤の履歴)を保持する。<br>
 * 一日ごとの勤務時間・残業時間などを計算し、その結果を保持する。
 *
 *
 */
public class ExtTimecardSummaryResultData implements ALData {

  private ALDateField date = null;

  private List<ExtTimecardResultData> list = null;

  /** オーナ ID */
  private ALNumberField owner_id;

  /** 就業日数 */
  private ALNumberField work_day = null;

  /** 就業時間 */
  private ALNumberField work_hour = null;

  /** 残業日数 */
  private ALNumberField overtime_day = null;

  /** 残業時間 */
  private ALNumberField overtime_hour = null;

  /** 休出日数 */
  private ALNumberField off_day = null;

  /** 休出時間 */
  private ALNumberField off_hour = null;

  /** 遅刻日数 */
  private ALNumberField late_coming_day = null;

  /** 早退日数 */
  private ALNumberField early_leaving_day = null;

  /** 欠勤日数 */
  private ALNumberField absent_day = null;

  /** 有休日数 */
  private ALNumberField paid_holiday = null;

  /** 代休日数 */
  private ALNumberField compensatory_holiday = null;

  /** その他 */
  private ALNumberField other_day = null;

  /** 未入力 */
  private ALNumberField no_input = null;

  private String user_name = null;

  private String systemName = null;

  /**
   *
   *
   */
  @Override
  public void initField() {
    work_day = new ALNumberField(0);
    work_hour = new ALNumberField(0);

    overtime_day = new ALNumberField(0);
    overtime_hour = new ALNumberField(0);

    off_day = new ALNumberField(0);
    off_hour = new ALNumberField(0);

    late_coming_day = new ALNumberField(0);
    early_leaving_day = new ALNumberField(0);
    absent_day = new ALNumberField(0);
    paid_holiday = new ALNumberField(0);
    compensatory_holiday = new ALNumberField(0);
    other_day = new ALNumberField(0);
    no_input = new ALNumberField(0);

    date = new ALDateField();
    date.setValue(new Date());
    list = new ArrayList<ExtTimecardResultData>();

    owner_id = new ALNumberField();

  }

  /**
   * 出勤日数、および時間を設定します。
   *
   * @param days
   * @param hour
   */
  public void setWorkDayHour(int days, float hour) {
    work_day.setValue(days);
    work_hour.setValue(String.valueOf(hour));
  }

  /**
   * 残業日数、および時間を設定します。
   *
   * @param days
   * @param hour
   */
  public void setOvertimeDayHour(int days, float hour) {
    overtime_day.setValue(days);
    overtime_hour.setValue(String.valueOf(hour));
  }

  /**
   * 休出日数、および時間を設定します。
   *
   * @param days
   * @param hour
   */
  public void setOffDayHour(int days, float hour) {
    off_day.setValue(days);
    off_hour.setValue(String.valueOf(hour));
  }

  /**
   * 遅刻日数を設定します。
   *
   * @param days
   */
  public void setLateComingDay(int days) {
    late_coming_day.setValue(days);
  }

  /**
   * 早退日数を設定します。
   *
   * @param days
   */
  public void setEarlyLeavingDay(int days) {
    early_leaving_day.setValue(days);
  }

  /**
   * 欠勤日数を設定します。
   *
   * @param days
   */
  public void setAbsentDay(int days) {
    absent_day.setValue(days);
  }

  /**
   * 有休日数を設定します。
   *
   * @param days
   */
  public void setPaidHoliday(int days) {
    paid_holiday.setValue(days);
  }

  /**
   * 代休日数を設定します。
   *
   * @param days
   */
  public void setCompensatoryHoliday(int days) {
    compensatory_holiday.setValue(days);
  }

  /**
   * その他日数を設定します。
   *
   * @param days
   */
  public void setOtherDay(int days) {
    other_day.setValue(days);
  }

  /**
   * 未入力日数を設定します。
   *
   * @param days
   */
  public void setNoInput(int days) {
    no_input.setValue(days);
  }

  /**
   * 就業日数を取得します。
   *
   * @return
   */
  public ALNumberField getWorkDay() {
    return work_day;
  }

  /**
   * 就業時間を取得します。
   *
   * @return
   */
  public ALNumberField getWorkHour() {
    return work_hour;
  }

  /**
   * 残業日数を取得します。
   *
   * @return
   */
  public ALNumberField getOvertimeDay() {
    return overtime_day;
  }

  /**
   * 残業時間を取得します。
   *
   * @return
   */
  public ALNumberField getOvertimeHour() {
    return overtime_hour;
  }

  /**
   * 休出日数を取得します。
   *
   * @return
   */
  public ALNumberField getOffDay() {
    return off_day;
  }

  /**
   * 休出時間を取得します。
   *
   * @return
   */
  public ALNumberField getOffHour() {
    return off_hour;
  }

  /**
   * 遅刻日数を取得します。
   *
   * @return
   */
  public ALNumberField getLateComingDay() {
    return late_coming_day;
  }

  /**
   * 早退日数を取得します。
   *
   * @return
   */
  public ALNumberField getEarlyLeavingDay() {
    return early_leaving_day;
  }

  /**
   * 欠勤日数を取得します。
   *
   * @return
   */
  public ALNumberField getAbsentDay() {
    return absent_day;
  }

  /**
   * 有休日数を取得します。
   *
   * @return
   */
  public ALNumberField getPaidHoliday() {
    return paid_holiday;
  }

  /**
   * 代休日数を取得します。
   *
   * @return
   */
  public ALNumberField getCompensatoryHoliday() {
    return compensatory_holiday;
  }

  /**
   * その他日数を取得します。
   *
   * @return
   */
  public ALNumberField getOtherDay() {
    return other_day;
  }

  /**
   * 未入力数を取得します。
   *
   * @return
   */
  public ALNumberField getNoInput() {
    return no_input;
  }

  // --------------------------------------------------------------------------------------
  /**
   *
   * @param date
   */
  public void setDate(Date date) {
    this.date.setValue(date);
  }

  /**
   * @return date
   */
  public ALDateField getDate() {
    return date;
  }

  /**
   *
   * @param minute
   * @return
   */
  @SuppressWarnings("unused")
  private String minuteToHour(long minute) {
    BigDecimal decimal = new BigDecimal(minute / 60.0);
    DecimalFormat dformat = new DecimalFormat("##.#");
    String str =
      dformat.format(decimal.setScale(1, BigDecimal.ROUND_FLOOR).doubleValue());
    return str;
  }

  /**
   *
   * @return
   */
  public String getDateStr() {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日(EE)");
      return sdf.format(date.getValue().getDate());
    } catch (Exception e) {
      return "";
    }
  }

  /**
   *
   * @return
   */
  public List<ExtTimecardResultData> getList() {
    return list;
  }

  public void setUserName(String user_name) {
    this.user_name = user_name;
  }

  public String getUserName() {
    return user_name;
  }

  public String getUserNameHtml() {
    return ALCommonUtils.replaceToAutoCR(user_name);
  }

  public void setSystemName(String systemName) {
    this.systemName = systemName;
  }

  public String getSystemName() {
    return systemName;
  }

  public String getSystemNameHtml() {
    return ALCommonUtils.replaceToAutoCR(systemName);
  }

  /**
   *
   * @return
   */
  public List<ExtTimecardResultData> getViewList() {
    ArrayList<ExtTimecardResultData> viewlist =
      new ArrayList<ExtTimecardResultData>();

    ExtTimecardResultData rd = null;

    int size = list.size();
    for (int i = 0; i < size; i++) {
      rd = list.get(i);
      if (!ExtTimecardUtils.WORK_FLG_DUMMY.equals(rd.getWorkFlag().getValue())) {
        viewlist.add(rd);
      }
    }
    return viewlist;
  }

  /**
   *
   * @param rd
   */
  public void addExtTimecardResultData(ExtTimecardResultData rd) {
    list.add(rd);
  }

  /**
   * @param i
   */
  public void setOwnerId(long i) {
    owner_id.setValue(i);
  }

  /**
   * @return
   */
  public ALNumberField getOwnerId() {
    return owner_id;
  }

  /**
   * ExtTimecardResultDataオブジェクトのインスタンスを作る
   *
   * @param date
   * @param workflag
   * @return
   */
  @SuppressWarnings("unused")
  private ExtTimecardResultData createExtTimecardResultData(Date date,
      String workflag) {
    ExtTimecardResultData rd = new ExtTimecardResultData();
    rd.initField();
    rd.setWorkDate(date);
    rd.setWorkFlag(workflag);
    return rd;
  }

  /**
   * 勤務時間設定をDBから取得する
   *
   * @return
   */
  @SuppressWarnings("unused")
  private EipTTimecardSettings loadEipTTimecardSettings() {

    SelectQuery<EipTTimecardSettings> query =
      Database.query(EipTTimecardSettings.class);
    List<EipTTimecardSettings> aList = query.fetchList();
    if (aList != null && aList.size() > 0) {
      EipTTimecardSettings record = aList.get(0);
      return record;
    } else {
      return null;
    }
  }

}

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
package com.aimluck.eip.schedule.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.aimluck.commons.field.ALCellDateField;
import com.aimluck.commons.field.ALCellDateTimeField;
import com.aimluck.commons.field.ALCellNumberField;
import com.aimluck.commons.field.ALCellStringField;
import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.facilities.FacilityResultData;
import com.aimluck.eip.schedule.AbstractCellScheduleFormData;
import com.aimluck.eip.schedule.util.CellScheduleUtils;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 携帯版スケジュールのフォームデータを管理するクラスです。
 *
 */
public class CellScheduleFormBean implements ALData {

  /** <code>start_date</code> 開始日時 */
  private ALCellDateTimeField start_date;

  /** <code>end_date</code> 終了日時 */
  private ALCellDateTimeField end_date;

  /** <code>end_date</code> 指定日時 */
  private ALCellDateTimeField view_date;

  /** <code>repeat_type</code> 繰り返し種別 */
  private ALCellStringField repeat_type;

  /** <code>week_0</code> 繰り返し曜日 */
  private ALCellStringField week_0;

  /** <code>week_1</code> 繰り返し曜日 */
  private ALCellStringField week_1;

  /** <code>week_2</code> 繰り返し曜日 */
  private ALCellStringField week_2;

  /** <code>week_3</code> 繰り返し曜日 */
  private ALCellStringField week_3;

  /** <code>week_4</code> 繰り返し曜日 */
  private ALCellStringField week_4;

  /** <code>week_5</code> 繰り返し曜日 */
  private ALCellStringField week_5;

  /** <code>week_6</code> 繰り返し曜日 */
  private ALCellStringField week_6;

  /** <code>week_6</code> 繰り返し週 */
  private ALCellStringField repeat_week;

  /** <code>limit_flag</code> 期限ありなし */
  private ALCellStringField limit_flag;

  /** <code>limit_date</code> 繰り返し期限（開始日） */
  private ALCellDateField limit_start_date;

  /** <code>limit_date</code> 繰り返し期限（終了日） */
  private ALCellDateField limit_end_date;

  /** <code>month_day</code> 繰り返す日 */
  private ALCellNumberField month_day;

  /** <code>month_day</code> 毎年繰り返す月 */
  private ALCellNumberField year_month;

  /** <code>month_day</code> 毎年繰り返す日 */
  private ALCellNumberField year_day;

  /** <code>edit_schedule_flag</code> 繰り返しスケジュールの編集フラグ */
  private ALCellNumberField edit_repeat_flag;

  /** <code>memberList</code> メンバーリスト */
  private List<ALEipUser> memberList;

  /** <code>memberList</code> 設備メンバーリスト */
  private List<FacilityResultData> facilityMemberList;

  /** <code>edit_control_flag</code> 共有メンバーによる編集／削除権限フラグ */
  private ALCellStringField edit_flag;

  /** <code>enable_change_tmpreserve</code> 仮スケジュールに戻すかどうか */
  private ALCellStringField change_tmpreserve_flag;

  /** <code>currentYear</code> 現在の年 */
  private int current_year;

  @Override
  public void initField() {
    initField(null, null, null);
  }

  public void initField(String tmpStart, String tmpEnd, String tmpView) {
    Calendar cal = Calendar.getInstance();
    int min = cal.get(Calendar.MINUTE);
    if (min <= 15) {
      cal.set(Calendar.MINUTE, 15);
    } else if (min <= 30) {
      cal.set(Calendar.MINUTE, 30);
    } else if (min <= 45) {
      cal.set(Calendar.MINUTE, 45);
    } else {
      cal.set(Calendar.MINUTE, 60);
    }
    Date now = cal.getTime();

    // 開始日時
    start_date = new ALCellDateTimeField("yyyy-MM-dd-HH-mm");
    if (tmpStart == null || tmpStart.equals("")) {
      start_date.setValue(now);
    } else {
      start_date.setValue(tmpStart);
    }
    start_date.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_START_DATE"));

    // 終了日時
    end_date = new ALCellDateTimeField("yyyy-MM-dd-HH-mm");
    if (tmpEnd == null || tmpEnd.equals("")) {
      if (tmpStart == null || tmpStart.equals("")) {
        end_date.setValue(now);
      } else {
        end_date.setValue(tmpStart);
      }
    } else {
      end_date.setValue(tmpEnd);
    }
    end_date.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_END_DATE"));

    // 指定日時
    view_date = new ALCellDateTimeField("yyyy-MM-dd");
    if (tmpView == null || tmpView.equals("")) {
      view_date.setValue(now);
    } else {
      view_date.setValue(tmpView);
    }
    view_date.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_SELECT_DATE"));

    // 繰り返しタイプ
    repeat_type = new ALCellStringField();
    repeat_type.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_REPEAT_TYPE"));
    repeat_type.setValue("D");
    repeat_type.setTrim(true);

    // 日
    week_0 = new ALCellStringField();
    week_0.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_SUNDAY"));
    week_0.setTrim(true);

    // 月
    week_1 = new ALCellStringField();
    week_1.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_MANDAY"));
    week_1.setTrim(true);

    // 火
    week_2 = new ALCellStringField();
    week_2.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_TUSEDAY"));
    week_2.setTrim(true);

    // 水
    week_3 = new ALCellStringField();
    week_3.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_WEDNESDAY"));
    week_3.setTrim(true);

    // 木
    week_4 = new ALCellStringField();
    week_4.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_THURSDAY"));
    week_4.setTrim(true);

    // 金
    week_5 = new ALCellStringField();
    week_5.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_FRIDAY"));
    week_5.setTrim(true);

    // 土
    week_6 = new ALCellStringField();
    week_6.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_SATURDAY"));
    week_6.setTrim(true);

    // 繰り返し週
    repeat_week = new ALCellStringField();
    repeat_week.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_REPEAT_WEEK"));
    repeat_week.setTrim(true);

    // 繰り返し日（選択されたときのみ Validate する）
    month_day = new ALCellNumberField();
    month_day.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_EVERY_MONTH_DAY"));
    month_day.limitValue(1, 32);

    // 毎年繰り返し月（選択されたときのみ Validate する）
    year_month = new ALCellNumberField();
    year_month.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_EVERY_YEAR_MONTH"));
    year_month.limitValue(1, 12);

    // 毎年繰り返し日（選択されたときのみ Validate する）
    year_day = new ALCellNumberField();
    year_day.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_EVERY_YEAR_DAY"));
    year_day.limitValue(1, 32);

    // 繰り返しフラグ
    limit_flag = new ALCellStringField();
    limit_flag.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_REPEAT"));
    limit_flag.setValue("OFF");
    limit_flag.setTrim(true);

    // 繰り返し期限
    limit_start_date = new ALCellDateField();
    limit_start_date.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_REPEAT_LIMIT"));
    limit_start_date.setValue(start_date.getValue());
    limit_end_date = new ALCellDateField();
    limit_end_date.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_REPEAT_LIMIT"));
    ALCellDateTimeField tmp_date = new ALCellDateTimeField("yyyy-MM-dd-HH-mm");
    if (tmpEnd == null || tmpEnd.equals("")) {
      if (tmpStart == null || tmpStart.equals("")) {
        tmp_date.setValue(now);
        limit_end_date.setValue(tmp_date.getValue());
      } else {
        tmp_date.setValue(tmpStart);
        limit_end_date.setValue(tmp_date.getValue());
      }
    } else {
      tmp_date.setValue(tmpEnd);
      limit_end_date.setValue(tmp_date.getValue());
    }

    // 繰り返しスケジュールの編集フラグ
    edit_repeat_flag = new ALCellNumberField();
    edit_repeat_flag.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_REPEAT_EDIT_AREA"));
    edit_repeat_flag
      .setValue(AbstractCellScheduleFormData.FLAG_EDIT_REPEAT_DEF);

    // メンバーリスト
    memberList = new ArrayList<ALEipUser>();

    // 設備リスト
    facilityMemberList = new ArrayList<FacilityResultData>();

    // 共有メンバーによる編集／削除権限フラグ
    edit_flag = new ALCellStringField();
    edit_flag.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_EDIT_DELETE_PERMISSION"));
    edit_flag.setTrim(true);
    edit_flag.setValue("T");

    // 仮スケジュールに戻すかどうか
    change_tmpreserve_flag = new ALCellStringField();
    change_tmpreserve_flag.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_TEMP_SCHEDULE"));
    change_tmpreserve_flag.setTrim(true);
    change_tmpreserve_flag.setValue("F");

    // 現在の年（年を選択するリストボックスに利用）
    current_year = Calendar.getInstance().get(Calendar.YEAR);
  }

  public void setResultData(EipTSchedule record) {
    // DN -> 毎日 (A = N -> 期限なし A = L -> 期限あり)
    // WnnnnnnnN W01111110 -> 毎週(月～金用)
    // MnnN M25 -> 毎月25日
    // YnnnnN Y0101N -> 毎年1月1日
    // S -> 期間での指定
    String ptn = record.getRepeatPattern();
    int count = 0;

    // 毎日
    boolean is_repeat = true;
    @SuppressWarnings("unused")
    boolean is_span = false;

    if (ptn.charAt(0) == 'D') {
      repeat_type.setValue("D");
      count = 1;
    } else if (ptn.charAt(0) == 'W' && ptn.length() == 9) {
      repeat_type.setValue("W");
      week_0.setValue(ptn.charAt(1) != '0' ? "TRUE" : null);
      week_1.setValue(ptn.charAt(2) != '0' ? "TRUE" : null);
      week_2.setValue(ptn.charAt(3) != '0' ? "TRUE" : null);
      week_3.setValue(ptn.charAt(4) != '0' ? "TRUE" : null);
      week_4.setValue(ptn.charAt(5) != '0' ? "TRUE" : null);
      week_5.setValue(ptn.charAt(6) != '0' ? "TRUE" : null);
      week_6.setValue(ptn.charAt(7) != '0' ? "TRUE" : null);
      count = 8;
      // 第何週
    } else if (ptn.charAt(0) == 'W' && ptn.length() == 10) {
      repeat_type.setValue("W");
      week_0.setValue(ptn.charAt(1) != '0' ? "TRUE" : null);
      week_1.setValue(ptn.charAt(2) != '0' ? "TRUE" : null);
      week_2.setValue(ptn.charAt(3) != '0' ? "TRUE" : null);
      week_3.setValue(ptn.charAt(4) != '0' ? "TRUE" : null);
      week_4.setValue(ptn.charAt(5) != '0' ? "TRUE" : null);
      week_5.setValue(ptn.charAt(6) != '0' ? "TRUE" : null);
      week_6.setValue(ptn.charAt(7) != '0' ? "TRUE" : null);
      repeat_week.setValue("0");
      switch (ptn.charAt(8)) {
        case '1':
          repeat_week.setValue("1");
          break;
        case '2':
          repeat_week.setValue("2");
          break;
        case '3':
          repeat_week.setValue("3");
          break;
        case '4':
          repeat_week.setValue("4");
          break;
        case '5':
          repeat_week.setValue("5");
          break;
        default:
          break;
      }
      count = 9;
    } else if (ptn.charAt(0) == 'M') {
      repeat_type.setValue("M");
      if (ptn.substring(1, 3).equals("XX")) {
        month_day.setValue(32);
      } else {
        month_day.setValue(Integer.parseInt(ptn.substring(1, 3)));
      }
      count = 3;
    } else if (ptn.charAt(0) == 'Y') {
      repeat_type.setValue("Y");
      year_month.setValue(Integer.parseInt(ptn.substring(1, 3)));
      year_day.setValue(Integer.parseInt(ptn.substring(3, 5)));
      count = 5;
    } else if (ptn.charAt(0) == 'S') {
      is_span = true;
      is_repeat = false;
    } else {
      is_repeat = false;
    }

    if (is_repeat) {
      // 開始日時
      Calendar tmpViewCal = Calendar.getInstance();
      tmpViewCal.setTime(view_date.getValue());
      Calendar tmpStartCal = Calendar.getInstance();
      tmpStartCal.setTime(record.getStartDate());
      tmpViewCal.set(Calendar.HOUR_OF_DAY, tmpStartCal
        .get(Calendar.HOUR_OF_DAY));
      tmpViewCal.set(Calendar.MINUTE, tmpStartCal.get(Calendar.MINUTE));
      start_date.setValue(tmpViewCal.getTime());
      // 終了日時
      Calendar tmpStopCal = Calendar.getInstance();
      tmpStopCal.setTime(record.getEndDate());
      tmpViewCal
        .set(Calendar.HOUR_OF_DAY, tmpStopCal.get(Calendar.HOUR_OF_DAY));
      tmpViewCal.set(Calendar.MINUTE, tmpStopCal.get(Calendar.MINUTE));
      end_date.setValue(tmpViewCal.getTime());

      if (ptn.charAt(count) == 'N') {
        limit_start_date.setValue(view_date.getValue());
        limit_end_date.setValue(view_date.getValue());
        limit_flag.setValue("OFF");
      } else {
        limit_flag.setValue("ON");
        limit_start_date.setValue(record.getStartDate());
        limit_end_date.setValue(record.getEndDate());
      }

      // 繰り返しスケジュールの編集フラグ
      edit_repeat_flag
        .setValue(AbstractCellScheduleFormData.FLAG_EDIT_REPEAT_ALL);
    }

    // 開始日時
    start_date.setValue(record.getStartDate());

    // 終了日時
    end_date.setValue(record.getEndDate());

    // 期限開始日
    limit_start_date.setValue(record.getStartDate());

    // 期限終了日
    limit_end_date.setValue(record.getEndDate());
  }

  public boolean validateDelegate(List<String> msgList, ALEipUser loginUser,
      String entityId, String scheduleType) throws ALDBErrorException,
      ALPageNotFoundException {
    boolean is_repeat = false;
    boolean is_span = false;

    if (CellScheduleUtils.SCHEDULE_TYPE_REPEAT.equals(scheduleType)) {
      is_repeat = true;
    } else if (CellScheduleUtils.SCHEDULE_TYPE_SPAN.equals(scheduleType)) {
      is_span = true;
    }

    if ("".equals(week_0.getValue())) {
      week_0.setValue(null);
    }
    if ("".equals(week_1.getValue())) {
      week_1.setValue(null);
    }
    if ("".equals(week_2.getValue())) {
      week_2.setValue(null);
    }
    if ("".equals(week_3.getValue())) {
      week_3.setValue(null);
    }
    if ("".equals(week_4.getValue())) {
      week_4.setValue(null);
    }
    if ("".equals(week_5.getValue())) {
      week_5.setValue(null);
    }
    if ("".equals(week_6.getValue())) {
      week_6.setValue(null);
    }

    return ScheduleUtils.validateDelegate(
      getStartDate(),
      getEndDate(),
      getRepeatType(),
      is_repeat,
      is_span,
      getWeek0(),
      getWeek1(),
      getWeek2(),
      getWeek3(),
      getWeek4(),
      getWeek5(),
      getWeek6(),
      getRepeatWeek(),
      getLimitFlag(),
      getLimitStartDate(),
      getLimitEndDate(),
      getMonthDay(),
      getYearMonth(),
      getYearDay(),
      loginUser,
      entityId,
      msgList,
      true);
  }

  /**
   * 開始日時を取得します。
   *
   * @return
   */
  public ALCellDateTimeField getStartDate() {
    return start_date;
  }

  public String getStartDateDate() {
    return ScheduleUtils.translateDate(start_date.getValue(), "yyyyMMdd");
  }

  public String getStartDateTime() {
    return ScheduleUtils.translateDate(start_date.getValue(), "HHmm");
  }

  public String getStartDateDate2() {
    try {
      Calendar cal = Calendar.getInstance();
      cal.setTime(start_date.getValue());
      return ScheduleUtils.translateDate(start_date.getValue(), "yyyy/MM/dd")
        + ALDateUtil.getDayOfWeek(cal);
    } catch (Exception e) {
      return "";
    }
  }

  public String getStartDateTime2() {
    try {
      return ScheduleUtils.translateDate(start_date.getValue(), "HH:mm");
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * 終了日時を取得します。
   *
   * @return
   */
  public ALCellDateTimeField getEndDate() {
    return end_date;
  }

  public String getEndDateDate() {
    return ScheduleUtils.translateDate(end_date.getValue(), "yyyyMMdd");
  }

  public String getEndDateTime() {
    return ScheduleUtils.translateDate(end_date.getValue(), "HHmm");
  }

  public String getEndDateDate2() {
    try {
      Calendar cal = Calendar.getInstance();
      cal.setTime(end_date.getValue());
      return ScheduleUtils.translateDate(end_date.getValue(), "yyyy/MM/dd")
        + ALDateUtil.getDayOfWeek(cal);
    } catch (Exception e) {
      return "";
    }
  }

  public String getEndDateTime2() {
    try {
      return ScheduleUtils.translateDate(end_date.getValue(), "HH:mm");
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * 指定日時を取得します。
   *
   * @return
   */
  public ALCellDateTimeField getViewDate() {
    return view_date;
  }

  /**
   * 繰り返しタイプを取得します。
   *
   * @return
   */
  public ALCellStringField getRepeatType() {
    return repeat_type;
  }

  /**
   * 繰り返し曜日を取得します。
   *
   * @return
   */
  public ALCellStringField getWeek0() {
    return week_0;
  }

  /**
   * 繰り返し曜日を取得します。
   *
   * @return
   */
  public ALCellStringField getWeek1() {
    return week_1;
  }

  /**
   * 繰り返し曜日を取得します。
   *
   * @return
   */
  public ALCellStringField getWeek2() {
    return week_2;
  }

  /**
   * 繰り返し曜日を取得します。
   *
   * @return
   */
  public ALCellStringField getWeek3() {
    return week_3;
  }

  /**
   * 繰り返し曜日を取得します。
   *
   * @return
   */
  public ALCellStringField getWeek4() {
    return week_4;
  }

  /**
   * 繰り返し曜日を取得します。
   *
   * @return
   */
  public ALCellStringField getWeek5() {
    return week_5;
  }

  /**
   * 繰り返し曜日を取得します。
   *
   * @return
   */
  public ALCellStringField getWeek6() {
    return week_6;
  }

  /**
   * 繰り返し週を取得します。
   *
   * @return
   */
  public ALCellStringField getRepeatWeek() {
    return repeat_week;
  }

  /**
   * 期限フラグを取得します。
   *
   * @return
   */
  public ALCellStringField getLimitFlag() {
    return limit_flag;
  }

  /**
   * 期限開始日を取得します。
   *
   * @return
   */
  public ALCellDateField getLimitStartDate() {
    return limit_start_date;
  }

  public String getLimitStartDateDate() {
    Date date = null;
    try {
      date = limit_start_date.getValue().getDate();
    } catch (Exception e) {
    }
    if (date == null) {
      return "";
    }
    return ScheduleUtils.translateDate(date, "yyyyMMdd");
    // return ""+(date.getYear()+1900)+date.getMonth()+date.getDate();
  }

  public String getLimitStartDateDate2() {
    try {
      return ScheduleUtils.translateDate(
        limit_start_date.getValue().getDate(),
        "yyyy/MM/dd");
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * 期限終了日を取得します。
   *
   * @return
   */
  public ALCellDateField getLimitEndDate() {
    return limit_end_date;
  }

  public String getLimitEndDateDate() {
    Date date = null;
    try {
      date = limit_end_date.getValue().getDate();
    } catch (Exception e) {
    }
    if (date == null) {
      return "";
    }
    return ScheduleUtils.translateDate(date, "yyyyMMdd");
  }

  public String getLimitEndDateDate2() {
    try {
      return ScheduleUtils.translateDate(
        limit_end_date.getValue().getDate(),
        "yyyy/MM/dd");
    } catch (Exception e) {
      return "";
    }
  }

  public ALCellNumberField getEditRepeatFlag() {
    return edit_repeat_flag;
  }

  /**
   * 毎月繰り返す日を取得します。
   *
   * @return
   */
  public ALCellNumberField getMonthDay() {
    return month_day;
  }

  /**
   * 毎年繰り返す日を取得します。
   *
   * @return
   */
  public ALCellNumberField getYearMonth() {
    return year_month;
  }

  /**
   * 毎年繰り返す日を取得します。
   *
   * @return
   */
  public ALCellNumberField getYearDay() {
    return year_day;
  }

  /**
   * グループメンバーを取得します。
   *
   * @return
   */
  public List<ALEipUser> getMemberList() {
    return memberList;
  }

  /**
   * 設備一覧を取得します。
   *
   * @return
   */
  public List<FacilityResultData> getFacilityMemberList() {
    return facilityMemberList;
  }

  /**
   * 共有メンバーによる編集／削除権限フラグを取得します。
   *
   * @return
   */
  public ALCellStringField getEditFlag() {
    return edit_flag;
  }

  /**
   * 仮スケジュールに戻すかどうかを取得します。
   *
   * @return
   */
  public ALCellStringField getChangeTmpreserveFlag() {
    return change_tmpreserve_flag;
  }

  /**
   * 現在の年を取得します。
   *
   * @return
   */
  public int getCurrentYear() {
    return current_year;
  }
}

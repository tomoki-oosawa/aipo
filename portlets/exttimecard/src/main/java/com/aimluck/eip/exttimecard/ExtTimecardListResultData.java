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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.commons.field.ALDateField;
import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.cayenne.om.portlet.EipTExtTimecard;
import com.aimluck.eip.cayenne.om.portlet.EipTExtTimecardSystem;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipHolidaysManager;
import com.aimluck.eip.exttimecard.util.ExtTimecardUtils;

/**
 * タイムカードのResultDataです。 <BR>
 * 
 */
public class ExtTimecardListResultData implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ExtTimecardListResultData.class.getName());

  private ALDateField date = null;

  private ExtTimecardResultData rd;

  private List<ExtTimecardResultData> list = null;

  /** タイムカードの設定 */
  private EipTExtTimecardSystem timecard_system;

  /** 計算済み就業時間 */
  private float calculated_work_hour = NO_DATA;

  /** 計算済み全込み就業時間 */
  private float calculated_total_work_hour = NO_DATA;

  /** 計算済み残業時間 */
  private float calculated_overtime_hour = NO_DATA;

  /** 時間データがないことを示す数値 */
  public static final float NO_DATA = -1f;

  /** 特定の日付より前から後かを示す変数 */
  private int beforeafter;

  /**
   *
   *
   */
  @Override
  public void initField() {
    date = new ALDateField();
    date.setValue(new Date());
    list = new ArrayList<ExtTimecardResultData>();
    beforeafter = 0;
  }

  /**
   * 日付を設定します。
   * 
   * @param resultdata
   */
  public void setDate(Date date) {
    this.date.setValue(date);
  }

  /**
   * 現在設定されている日付を取得します。
   * 
   * @return
   */
  public String getDate() {
    return date.getValue().toString();
  }

  /**
   * タイムカードのResultDataを設定します。
   * 
   * @param resultdata
   */
  public void setRd(ExtTimecardResultData resultdata) {
    rd = resultdata;
  }

  /**
   * タイムカードのResultDataを取得します。
   * 
   * @param
   */
  public ExtTimecardResultData getRd() {
    return rd;
  }

  /**
   * ResultDataがあるかどうか
   * 
   * @param
   */
  public boolean getIsResultData() {
    return (rd != null);
  }

  /**
   * 日付が今日より前であるかどうか
   * 
   * @param
   */
  public int getIsBeforeOrAfterToday() {
    try {
      Date today = new Date();
      Date now = date.getValue().getDate();
      if (ExtTimecardUtils.sameDay(now, today)) {
        return 0;
      }
      return now.compareTo(today);
    } catch (Exception e) {
      return 0;
    }
  }

  /**
   * 日付が今日より前であるかどうか、日付変更時間を考慮する
   */
  public int getIsBeforeOrafterTodayAlter() {

    try {

      int change_time = timecard_system.getChangeHour().intValue();
      Date today = new Date();
      long time =
        today.getTime()
          - Long.valueOf(Integer.toString(change_time * 60 * 60 * 1000));
      today.setTime(time);

      Date now = date.getValue().getDate();

      logger.debug(today.toString());
      logger.error(now.toString());

      if (ExtTimecardUtils.sameDay(now, today)) {
        return 0;
      }
      return now.compareTo(today);
    } catch (RuntimeException e) {
      // RuntimeException
      return 0;
    } catch (Exception e) {
      return 0;
    }

  }

  /**
   * 曜日が土曜日であるか、日曜日であるか、休日であるか取得
   * 
   * @return int 0 ... 平日 1 ... 土曜日 2 ... 日曜日 3 ... 休日
   */
  public int getIsSaturdayOrSundayOrHoliday() {
    try {
      Date now = date.getValue().getDate();
      ALEipHolidaysManager holidaysManager = ALEipHolidaysManager.getInstance();
      if (holidaysManager.isHoliday(now) != null) {
        return 3;
      }
      Calendar cal = Calendar.getInstance();
      cal.setTime(now);
      if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
        return 1;
      }
      if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
        return 2;
      }
      return 0;
    } catch (Exception e) {
      return -1;
    }
  }

  /**
   * 出勤時間が空かどうか
   * 
   * @return
   */
  public boolean getIsNotNullClockInTime() {
    if (rd == null) {
      return false;
    }
    if (rd.getIsNullClockInTime()) {
      return false;
    }
    return true;
  }

  /**
   * 退勤時間が空かどうか
   * 
   * @return
   */
  public boolean getIsNotNullClockOutTime() {
    if (rd == null) {
      return false;
    }
    if (rd.getIsNullClockOutTime()) {
      return false;
    }
    return true;
  }

  /**
   * 外出／復帰の回数を得ます。
   * 
   * @return
   */
  public int getOutgoingComebackTimes() {
    if (rd == null) {
      return 1;
    }
    return Math.max(Math.max(rd.getAllOutgoingTime().size(), rd
      .getAllComebackTime()
      .size()), 1);
  }

  /**
   * 現在残業中かどうか調べます。
   * 
   * @return boolean
   */
  public boolean getIsClockOverTime() {
    if (getIsNotNullClockInTime()) {
      int end_hour = timecard_system.getEndHour(), end_minute =
        timecard_system.getEndMinute();

      Calendar cal = Calendar.getInstance();

      int now_hour = cal.get(Calendar.HOUR_OF_DAY);
      int now_minute = cal.get(Calendar.MINUTE);

      if (now_hour < end_hour) {
        return false;
      } else if (now_hour == end_hour) {
        if (now_minute < end_minute) {
          return false;
        } else {
          return true;
        }
      } else {
        return true;
      }
    }
    return false;
  }

  /**
   * 外出／復帰の配列を得ます。
   * 
   * @return
   */
  public List<Map<String, String>> getOutgoingComeback() {
    List<Map<String, String>> result_list =
      new ArrayList<Map<String, String>>();

    Map<String, String> dummymap = new HashMap<String, String>();
    dummymap.put("outgoing", "");
    dummymap.put("comeback", "");
    if (rd == null) {
      result_list.add(dummymap);
    } else {
      List<ALDateTimeField> out = rd.getAllOutgoingTime();
      List<ALDateTimeField> come = rd.getAllComebackTime();
      // ALDateTimeField out[] = rd.getAllOutgoingTime();
      // ALDateTimeField come[] = rd.getAllComebackTime();
      for (int i = 0; i < EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY; i++) {
        boolean isnullout = out.get(i).isNullHour();
        boolean isnullcome = come.get(i).isNullHour();
        if (isnullout && isnullcome) {
          break;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("outgoing", isnullout ? "" : out.get(i).getTime());
        map.put("comeback", isnullcome ? "" : come.get(i).getTime());
        result_list.add(map);
      }
      if (result_list.size() == 0) {
        result_list.add(dummymap);
      }
    }
    return result_list;
  }

  /**
   * 外出／復帰の配列を得ます。
   * 
   * @return
   */
  public List<Map<String, String>> getOutgoingComeback_xls() {
    List<Map<String, String>> result_list =
      new ArrayList<Map<String, String>>();

    Map<String, String> dummymap = new HashMap<String, String>();
    dummymap.put("outgoing", "");
    dummymap.put("comeback", "");
    if (rd == null) {
      result_list.add(dummymap);
    } else {
      List<ALDateTimeField> out = rd.getAllOutgoingTime();
      List<ALDateTimeField> come = rd.getAllComebackTime();
      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
      for (int i = 0; i < EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY; i++) {
        boolean isnullout = out.get(i).isNullHour();
        boolean isnullcome = come.get(i).isNullHour();
        if (isnullout && isnullcome) {
          // break;
        }
        String out_str = "", come_str = "";
        try {
          if (!isnullout) {
            out_str = sdf.format(out.get(i).getValue());
          }
          if (!isnullcome) {
            come_str = sdf.format(come.get(i).getValue());
          }
        } catch (Exception e) {
          out_str = "";
          come_str = "";
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("outgoing", out_str);
        map.put("comeback", come_str);
        result_list.add(map);
      }
      if (result_list.size() == 0) {
        result_list.add(dummymap);
      }
    }
    return result_list;
  }

  @SuppressWarnings("unused")
  private String changeMinute(String minute) {
    if (Integer.parseInt(minute) >= 0 && Integer.parseInt(minute) <= 9) {
      return minute = "0" + minute;
    }
    return minute;
  }

  /**
   * 就業時間を計算します。
   * 
   * @return float
   */
  public float getWorkHour() {
    if (!getIsNotNullClockInTime() || !getIsNotNullClockOutTime()) {
      return NO_DATA;
    } else if (calculated_work_hour != NO_DATA) {
      return calculated_work_hour;
    } else {
      float time = 0f;
      float in = getInworkHour();// 残業以外
      if (getIsSaturdayOrSundayOrHoliday() == 0) {
        if (in != NO_DATA) {
          time += in;
        }
        // 残業を就業に含める
        // if (over != NO_DATA) {
        // time += over;
        // }
      }
      calculated_work_hour = time;
      return time;
    }
  }

  /**
   * すべてを含めた就業時間を計算します。
   * 
   * @return float
   */
  public float getTotalWorkHour() {
    if (!getIsNotNullClockInTime() || !getIsNotNullClockOutTime()) {
      return NO_DATA;
    } else if (calculated_total_work_hour != NO_DATA) {
      return calculated_total_work_hour;
    } else {
      float time = 0f;
      float in = getInworkHour();// 残業以外
      float over = getOvertimeHour();// 残業
      if (in != NO_DATA) {
        time += in;
      }
      if (over != NO_DATA) {
        time += over;
      }
      calculated_total_work_hour = time;
      return time;
    }
  }

  /**
   * 残業以外の時間を計算します。
   * 
   * @return float
   */
  public float getInworkHour() {
    if (!getIsNotNullClockInTime() || !getIsNotNullClockOutTime()) {
      return -1f;
    } else {
      if (getIsSaturdayOrSundayOrHoliday() != 0) {// 祝日なので残業以外は無し
        return 0f;
      }

      float time = 0f;
      time +=
        (rd.getClockOutTime().getValue().getTime() - rd
          .getClockInTime()
          .getValue()
          .getTime())
          / (1000.0 * 60.0 * 60.0);

      // 就業時間だけなので、残業を引く
      float ovetTime = 0f;
      Date start_date = getStartDate(), end_date = getEndDate();
      long start_time = start_date.getTime(), end_time = end_date.getTime();
      /** 早出残業 */
      if (rd.getClockInTime().getValue().getTime() < start_time) {
        ovetTime += start_time - rd.getClockInTime().getValue().getTime();
        if (rd.getClockOutTime().getValue().getTime() < start_time) {
          ovetTime -= start_time - rd.getClockOutTime().getValue().getTime();
        }
      }
      /** 残業 */
      if (end_time < rd.getClockOutTime().getValue().getTime()) {
        ovetTime += rd.getClockOutTime().getValue().getTime() - end_time;
        if (end_time < rd.getClockInTime().getValue().getTime()) {
          ovetTime -= rd.getClockInTime().getValue().getTime() - end_time;
        }
      }
      ovetTime /= (1000.0 * 60.0 * 60.0);
      time -= ovetTime;

      /** 外出時間を就業時間に含めない場合 */
      if (timecard_system.getOutgoingAddFlag().equals("F")) {
        float outgoing_time = getOutgoingTime(getStartDate(), getEndDate());
        if (outgoing_time != NO_DATA) {
          time -= outgoing_time;
        } else {
          return NO_DATA;
        }
      }

      /** 就業時間の中で決まった時間の休憩を取らせます。 */
      /** 決まった時間ごとの休憩時間を取らせます。 */
      float worktimein = (timecard_system.getWorktimeIn() / 60f);
      float resttimein = (timecard_system.getResttimeIn() / 60f);
      if (worktimein == 0F) {
        return time;
      }
      int resttimes = (int) (time / worktimein);
      return time - resttimes * resttimein;
    }
  }

  /**
   * 残業時間を計算します。？
   * 
   * @return float
   */
  public float getOutworkHour() {
    if (!getIsNotNullClockInTime() || !getIsNotNullClockOutTime()) {
      return -1f;
    } else {
      float time = 0f;
      if (getIsSaturdayOrSundayOrHoliday() == 0) {
        long start_time, end_time;
        int start_hour = timecard_system.getStartHour(), start_minute =
          timecard_system.getStartMinute();
        int end_hour = timecard_system.getEndHour(), end_minute =
          timecard_system.getEndMinute();

        Calendar cal = Calendar.getInstance();

        try {
          cal.setTime(date.getValue().getDate());
        } catch (Exception e) {
        }

        cal.set(Calendar.HOUR_OF_DAY, start_hour);
        cal.set(Calendar.MINUTE, start_minute);
        start_time = cal.getTime().getTime();

        cal.set(Calendar.HOUR_OF_DAY, end_hour);
        cal.set(Calendar.MINUTE, end_minute);
        end_time = cal.getTime().getTime();

        if (start_hour >= 0 && start_hour < timecard_system.getChangeHour()) {
          start_time += 1000 * 60 * 60 * 24;
          end_time += 1000 * 60 * 60 * 24;
        } else if (end_hour >= 0 && end_hour < timecard_system.getChangeHour()) {
          end_time += 1000 * 60 * 60 * 24;
        }

        /** 早出残業 */
        if (rd.getClockInTime().getValue().getTime() < start_time) {
          time += start_time - rd.getClockInTime().getValue().getTime();
        }

        /** 残業 */
        if (end_time < rd.getClockOutTime().getValue().getTime()) {
          time += rd.getClockOutTime().getValue().getTime() - end_time;
        }

        time /= (1000.0 * 60.0 * 60.0);
      }
      return time;
    }
  }

  /**
   * 休出時間を計算します。
   * 
   * @return float
   */
  public float getOffWorkHour() {
    if (getIsSaturdayOrSundayOrHoliday() != 0) {
      return getTotalWorkHour();
    }
    return -1f;
  }

  /**
   * 休憩時間を差し引いた就業時間を計算します。
   * 
   * @return float
   */
  public float getWorkHourWithoutRestHour() {
    return getWorkHourWithoutRestHour(false);
  }

  public float getWorkHourWithoutRestHour(boolean round) {
    float time = NO_DATA;
    if (calculated_work_hour != NO_DATA) {
      time = calculated_work_hour;
    } else {
      time = getWorkHour();
    }
    if (round) {
      time = ExtTimecardUtils.roundHour(time);
    }
    return time;
  }

  /**
   * 残業時間を計算します。
   * 
   * @return float
   */
  public float getOvertimeHour() {
    if (!getIsNotNullClockInTime() || !getIsNotNullClockOutTime()) {
      return NO_DATA;
    } else if (calculated_overtime_hour != NO_DATA) {
      return calculated_overtime_hour;
    } else {
      float time = 0f;
      Date start_date = getStartDate(), end_date = getEndDate(), change_date =
        getChangeDate(), nextchange_date = getNextChangeDate();
      if (getIsSaturdayOrSundayOrHoliday() == 0) {
        long start_time = start_date.getTime(), end_time = end_date.getTime();
        /** 早出残業 */
        if (rd.getClockInTime().getValue().getTime() < start_time) {
          time += start_time - rd.getClockInTime().getValue().getTime();
          if (rd.getClockOutTime().getValue().getTime() < start_time) {
            time -= start_time - rd.getClockOutTime().getValue().getTime();
          }
        }

        /** 残業 */
        if (end_time < rd.getClockOutTime().getValue().getTime()) {
          time += rd.getClockOutTime().getValue().getTime() - end_time;
          if (end_time < rd.getClockInTime().getValue().getTime()) {
            time -= rd.getClockInTime().getValue().getTime() - end_time;
          }
        }
        time /= (1000.0 * 60.0 * 60.0);

        /** 外出時間を残業時間に含めない場合 */
        if (timecard_system.getOutgoingAddFlag().equals("F")) {
          float outgoing_time;
          outgoing_time = getOutgoingTime(change_date, start_date);
          if (outgoing_time != NO_DATA) {
            time -= outgoing_time;
          }
          outgoing_time = getOutgoingTime(end_date, nextchange_date);
          if (outgoing_time != NO_DATA) {
            time -= outgoing_time;
          }
        }
      } else {// 祝日
        time +=
          (rd.getClockOutTime().getValue().getTime() - rd
            .getClockInTime()
            .getValue()
            .getTime())
            / (1000.0 * 60.0 * 60.0);
        float outgoing_time;
        outgoing_time = getOutgoingTime(change_date, nextchange_date);
        if (outgoing_time != NO_DATA) {
          time -= outgoing_time;
        }
      }

      /** 就業時間の中で決まった時間の休憩を取らせます。 */
      /** 決まった時間ごとの休憩時間を取らせます。 */
      float worktimeout = (timecard_system.getWorktimeOut() / 60f);
      float resttimeout = (timecard_system.getResttimeOut() / 60f);
      if (worktimeout == 0F) {// 0の時は休憩なし？
        return time;
      }
      int resttimes = (int) (time / worktimeout);

      calculated_overtime_hour = time - resttimes * resttimeout;
      return time - resttimes * resttimeout;
    }
  }

  /**
   * 休憩時間を差し引いた残業時間を計算します。
   * 
   * @return float
   */
  public float getOvertimeHourWithoutRestHour() {
    return getOvertimeHourWithoutRestHour(false);
  }

  public float getOvertimeHourWithoutRestHour(boolean round) {
    float time = NO_DATA;

    if (calculated_overtime_hour > NO_DATA) {
      time = calculated_overtime_hour;
    } else {
      time = getOvertimeHour();
    }
    if (round) {
      time = ExtTimecardUtils.roundHour(time);
    }
    if (getIsSaturdayOrSundayOrHoliday() != 0) {
      if (time != NO_DATA) {
        time = 0f;
      }
    }
    return time;
  }

  /**
   * 休出時間を計算します。
   * 
   * @return float
   */
  public float getOffHour() {
    return getOffHour(false);
  }

  public float getOffHour(boolean round) {
    if (getIsSaturdayOrSundayOrHoliday() != 0) {
      float time = NO_DATA;
      if (calculated_total_work_hour != NO_DATA) {
        time = calculated_total_work_hour;
      } else {
        time = getTotalWorkHour();
      }
      if (round) {
        time = ExtTimecardUtils.roundHour(time);
      }
      return time;
    }
    return NO_DATA;
  }

  /**
   * 休憩時間を計算します。
   * 
   * @return float
   */
  public float getRestHour() {
    float time = 0f, work, overtime;
    if (getWorkHour() != NO_DATA && getOvertimeHour() != NO_DATA) {
      work = getWorkHour() - getOvertimeHour();
      overtime = getOvertimeHour();
      int rest_work_num = 0, rest_overtime_num = 0;
      int rest_work_extra = 0, rest_overtime_extra = 0;
      if (work > 0) {
        rest_work_num =
          (int) (work * 60 / (timecard_system.getWorktimeIn() + timecard_system
            .getResttimeIn()));
        rest_work_extra =
          (int) (work
            * 60
            - rest_work_num
            * (timecard_system.getWorktimeIn() + timecard_system
              .getResttimeIn()) - timecard_system.getWorktimeIn());
        if (rest_work_extra < 0) {
          rest_work_extra = 0;
        }
      }
      if (overtime > 0) {
        rest_overtime_num =
          (int) (overtime * 60 / (timecard_system.getWorktimeOut() + timecard_system
            .getResttimeOut()));
        rest_overtime_extra =
          (int) (overtime
            * 60
            - rest_overtime_num
            * (timecard_system.getWorktimeOut() + timecard_system
              .getResttimeOut()) - timecard_system.getWorktimeOut());
        if (rest_overtime_extra < 0) {
          rest_overtime_extra = 0;
        }
      }
      time =
        rest_work_num
          * timecard_system.getResttimeIn()
          + rest_overtime_num
          * timecard_system.getResttimeOut()
          + rest_work_extra
          + rest_overtime_extra;
      time /= 60;
      return time;
    } else {
      return NO_DATA;
    }
  }

  /**
   * その日遅刻したかどうか
   * 
   * @return boolean
   */
  public boolean isLateComing() {
    if (!getIsNotNullClockInTime() || !getIsNotNullClockOutTime()) {
      return false;
    }
    Date start_date = getStartDate();
    if (rd.getClockInTime().getValue().after(start_date)) {
      return true;
    }
    return false;
  }

  /**
   * その日早退したかどうか
   * 
   * @return boolean
   */
  public boolean isEarlyLeaving() {
    if (!getIsNotNullClockInTime() || !getIsNotNullClockOutTime()) {
      return false;
    }
    Date end_date = getEndDate();
    if (rd.getClockOutTime().getValue().before(end_date)) {
      return true;
    }
    return false;
  }

  /**
   * その日欠勤したかどうか
   * 
   * @return boolean
   */
  public boolean isAbsent() {
    return false;
  }

  /**
   *
   *
   */
  public String getHourToString(float time) {
    if (time == NO_DATA) {
      return "";
    }
    return time + "h";
  }

  /**
   * 日付を取得します。
   * 
   * @return
   */
  public String getDateStr() {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("M月d日(EE)");
      return sdf.format(date.getValue().getDate());
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * 日付を取得します。
   * 
   * @return
   */
  public String getDateStr(String str) {
    try {
      if (str == null || "".equals(str)) {
        str = "yyyy年MM月dd日(EE)";
      }
      SimpleDateFormat sdf = new SimpleDateFormat(str);
      return sdf.format(date.getValue().getDate());
    } catch (RuntimeException e) {
      // RuntimeException
      return "";
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * タイムカードの設定を取得します。
   * 
   * @return
   */
  public EipTExtTimecardSystem getTimecardSystem() {
    return timecard_system;
  }

  /**
   * タイムカードの設定を読み込みます。
   * 
   * @return
   */
  public void setTimecardSystem(EipTExtTimecardSystem system) {
    timecard_system = system;
  }

  public Date getClockInDate() {
    return rd.getClockInTime().getValue();
  }

  public List<ExtTimecardResultData> getList() {
    return list;
  }

  public List<ExtTimecardResultData> getViewList() {
    List<ExtTimecardResultData> viewlist =
      new ArrayList<ExtTimecardResultData>();

    // ExtTimecardResultData rd = null;
    //
    // int size = list.size();
    // for (int i = 0; i < size; i++) {
    // rd = (ExtTimecardResultData) list.get(i);
    //
    // if (!ExtTimecardUtils.WORK_FLG_DUMMY.equals(rd.getWorkFlag().getValue()))
    // {
    // viewlist.add(rd);
    // }
    // }

    return viewlist;
  }

  public void addExtTimecardResultData(ExtTimecardResultData rd) {
    list.add(rd);
  }

  public String getSummayTimes() {
    int size = list.size();

    if (size < 1) {
      return "0";
    }
    return "0";
  }

  /**
   * 始業時間を取得します。
   * 
   * @return
   */
  private Date getStartDate() {
    int start_hour = timecard_system.getStartHour(), start_minute =
      timecard_system.getStartMinute();
    Calendar cal = Calendar.getInstance();
    try {
      cal.setTime(rd.getPunchDate().getValue());
    } catch (Exception e) {
    }
    cal.set(Calendar.HOUR_OF_DAY, start_hour);
    cal.set(Calendar.MINUTE, start_minute);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    if (start_hour >= 0 && start_hour < timecard_system.getChangeHour()) {
      cal.add(Calendar.DAY_OF_MONTH, 1);
    }
    return cal.getTime();
  }

  /**
   * 就業時間を取得します。
   * 
   * @return
   */
  private Date getEndDate() {
    int end_hour = timecard_system.getEndHour(), end_minute =
      timecard_system.getEndMinute();
    Calendar cal = Calendar.getInstance();
    try {
      cal.setTime(rd.getPunchDate().getValue());
    } catch (Exception e) {
    }
    cal.set(Calendar.HOUR_OF_DAY, end_hour);
    cal.set(Calendar.MINUTE, end_minute);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    if (end_hour >= 0 && end_hour < timecard_system.getChangeHour()) {
      cal.add(Calendar.DAY_OF_MONTH, 1);
    }
    return cal.getTime();
  }

  private Date getChangeDate() {
    int change_hour = timecard_system.getChangeHour();
    Calendar cal = Calendar.getInstance();
    try {
      cal.setTime(rd.getPunchDate().getValue());
    } catch (Exception e) {
    }
    cal.set(Calendar.HOUR_OF_DAY, change_hour);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    return cal.getTime();
  }

  private Date getNextChangeDate() {
    int change_hour = timecard_system.getChangeHour();
    Calendar cal = Calendar.getInstance();
    try {
      cal.setTime(rd.getPunchDate().getValue());
    } catch (Exception e) {
    }
    cal.add(Calendar.DATE, 1);
    cal.set(Calendar.HOUR_OF_DAY, change_hour);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    return cal.getTime();
  }

  /**
   * 特定の時間中に含まれる外出時間を計算します。
   * 
   * @param from_date
   * @param to_date
   * @return
   */
  private float getOutgoingTime(Date from_date, Date to_date) {
    long outgoing_time = 0;
    int comeback_num = 0, outgoing_num = 0, from_num = -1, to_num = -1;
    if (from_date.getTime() > to_date.getTime()) {
      return 0.0f;
    }
    for (ALDateTimeField field : rd.getAllComebackTime()) {
      if (field.isNullHour()) {
        break;
      }
      comeback_num++;
      /** 勤務時間より前だった場合は無視。はじめて勤務時間より後になった番号を記録する */
      if (field.getValue().getTime() < from_date.getTime()) {
        continue;
      } else if (from_num == -1) {
        from_num = comeback_num;
      }
      /** 勤務時間より後だった場合は番号を記録する */
      if (field.getValue().getTime() > to_date.getTime()) {
        to_num = comeback_num;
        outgoing_time += to_date.getTime();
        continue;
      }
      outgoing_time += field.getValue().getTime();
    }

    /** 外出時間がすべて勤務時間より前だった場合は、外出時間は0とする */
    if (from_num != -1) {
      for (ALDateTimeField field : rd.getAllOutgoingTime()) {
        if (field.isNullHour()) {
          break;
        }
        outgoing_num++;
        if (outgoing_num < from_num) {
          continue;
        }
        if (outgoing_num == from_num) {
          if (field.getValue().getTime() < from_date.getTime()) {
            outgoing_time -= from_date.getTime();
            continue;
          }
        }
        if (field.getValue().getTime() > to_date.getTime()) {
          outgoing_time -= to_date.getTime();
          continue;
        }
        outgoing_time -= field.getValue().getTime();
        if (outgoing_num == to_num) {
          break;
        }
      }
      if (comeback_num == outgoing_num) {
        return (float) outgoing_time / (1000 * 60 * 60);
      } else {
        /** 外出／復帰が対応しないため計算できない */
        return NO_DATA;
      }
    }
    return 0.0f;
  }

  public void setBeforeAfter() {
    try {

      if (timecard_system != null) {
        int change_time = timecard_system.getChangeHour().intValue();
        Date today = new Date();
        long time =
          today.getTime()
            - Long.valueOf(Integer.toString(change_time * 60 * 60 * 1000));
        today.setTime(time);

        Date now = date.getValue().getDate();

        if (ExtTimecardUtils.sameDay(now, today)) {
          beforeafter = 0;
          return;
        }
        beforeafter = now.compareTo(today);
      } else {
        beforeafter = 0;
      }
    } catch (Exception e) {
      beforeafter = 0;
      logger.error("error", e);
    }
  }

  public int getBeforeAfter() {
    return beforeafter;
  }
}

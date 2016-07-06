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
package com.aimluck.eip.exttimecard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.cayenne.om.portlet.EipTExtTimecard;
import com.aimluck.eip.cayenne.om.portlet.EipTExtTimecardSystem;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.exttimecard.util.ExtTimecardUtils;

/**
 * タイムカードのResultDataです。 <BR>
 *
 */
public class ExtTimecardResultData implements ALData {

  /** 現在時刻 */
  private ALStringField now_time;

  /** タイムカードの設定 */
  private EipTExtTimecardSystem timecard_system;

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

  public boolean reason_flg;

  /** 備考 */
  private ALStringField remarks;

  public boolean remarks_flg;

  /** タイムカードのID */
  private ALStringField timecard_id;

  /** ある一日の合計勤務時間 */
  private ALNumberField sum_work_date;

  /** */
  private ALDateTimeField punch_date;

  /** 出勤時間 */
  private ALDateTimeField clock_in_time;

  /** 退勤時間 */
  private ALDateTimeField clock_out_time;

  /** 外出時間 */
  private List<ALDateTimeField> outgoing_time;

  /** 復帰時間 */
  private List<ALDateTimeField> comeback_time;

  /** 種類 */
  private ALStringField type;

  private boolean isTypeP;

  private boolean isTypeA;

  private boolean isTypeH;

  private boolean isTypeC;

  private boolean isTypeE;

  private boolean isCurrentMonth;

  /**
   *
   *
   */
  @Override
  public void initField() {
    work_flag = new ALStringField();
    now_time = new ALStringField();
    work_date = new ALDateTimeField();
    refix_flag = new ALStringField();
    list_date = new ALStringField();
    timecard_id = new ALStringField();
    reason = new ALStringField();
    sum_work_date = new ALNumberField();
    remarks = new ALStringField();
    punch_date = new ALDateTimeField();
    clock_in_time = new ALDateTimeField();
    clock_out_time = new ALDateTimeField();
    reason_flg = false;
    remarks_flg = false;
    isTypeP = false;
    isTypeA = false;
    isTypeH = false;
    isTypeC = false;
    isTypeE = false;
    outgoing_time = new ArrayList<ALDateTimeField>();
    comeback_time = new ArrayList<ALDateTimeField>();

    type = new ALStringField();
    /*
     * outgoing_time = new
     * ALDateTimeField[EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY]; comeback_time
     * = new ALDateTimeField[EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY]; for
     * (int i = 0 ; i < EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY ; i++) {
     * outgoing_time[i] = new ALDateTimeField(); comeback_time[i] = new
     * ALDateTimeField(); }
     */
  }

  /**
   * 出勤時間がNULLかどうか調べます。
   *
   * @return boolean
   */
  public boolean getIsNullClockInTime() {
    if (clock_in_time.isNullHour()) {
      return true;
    }
    return false;
  }

  /**
   * 退勤時間がNULLかどうか調べます。
   *
   * @return boolean
   */
  public boolean getIsNullClockOutTime() {
    if (clock_out_time.isNullHour()) {
      return true;
    }
    return false;
  }

  /**
   * 現在外出中かどうか調べます。
   *
   * @return boolean
   */
  public boolean getIsOutgoing() {
    int length = outgoing_time.size();
    for (int i = 0; i < length; i++) {
      if (!outgoing_time.get(i).isNullHour()
        && comeback_time.get(i).isNullHour()) {
        return true;
      }
    }
    return false;
  }

  /**
   * 現在残業中かどうか調べます。
   *
   * @return boolean
   */
  public boolean getIsOverTime() {
    if (!getIsNullClockInTime()) {
      if (!ExtTimecardUtils.isNewRule()) {
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
      } else {
        // 法定外残業
        Calendar cal = Calendar.getInstance();
        float time = 0f;
        time +=
          (cal.getTime().getTime() - clock_in_time.getValue().getTime())
            / (1000.0 * 60.0 * 60.0);

        /** 外出時間を就業時間に含めない場合 */
        if ("F".equals(timecard_system.getOutgoingAddFlag())) {
          int length = outgoing_time.size();
          for (int i = 0; i < length; i++) {
            if (!outgoing_time.get(i).isNullHour()
              && !comeback_time.get(i).isNullHour()) {
              time -=
                (comeback_time.get(i).getValue().getTime() - outgoing_time.get(
                  i).getValue().getTime())
                  / (1000.0 * 60.0 * 60.0);
            }
          }
        }

        /** 就業時間の中で決まった時間の休憩を取らせます。 */
        /** 決まった時間ごとの休憩時間を取らせます。 */
        float worktimein = (timecard_system.getWorktimeIn() / 60f);
        float resttimein = (timecard_system.getResttimeIn() / 60f);
        if (worktimein != 0F) {
          int resttimes = (int) (time / worktimein);
          time -= resttimes * resttimein;
        }
        float overTime =
          ExtTimecardUtils.getOvertimeMinuteByDay(timecard_system
            .getOvertimeType()) / 60f;
        if (time >= overTime) {
          return true;
        } else {
          return false;
        }

      }
    }
    return false;
  }

  public void setClockInTime(Date date) {
    clock_in_time.setValue(date);
  }

  public void setClockOutTime(Date date) {
    clock_out_time.setValue(date);
  }

  public void setOutgoingTime(Date date, int n) {
    ALDateTimeField datetime = new ALDateTimeField();
    datetime.setValue(date);
    outgoing_time.add(datetime);
    // outgoing_time.get(n-1).setValue(date);
  }

  public void setComebackTime(Date date, int n) {
    ALDateTimeField datetime = new ALDateTimeField();
    datetime.setValue(date);
    comeback_time.add(datetime);
    // comeback_time.get(n-1).setValue(date);
  }

  public void setPunchDate(Date date) {
    punch_date.setValue(date);
  }

  /**
   * @param i
   */
  public void setNowTime(String time) {
    now_time.setValue(time);
  }

  public void setType(String str) {
    type.setValue(str);
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
  public void setRemarks(String str_remarks) {
    remarks.setValue(str_remarks);
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

  public ALDateTimeField getClockInTime() {
    return clock_in_time;
  }

  public String getClockInTime(String format) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(format);
      return sdf.format(clock_in_time.getValue());
    } catch (Exception e) {
      return "";
    }
  }

  public String getClockInTimeHourMinuteText() {
    return clock_in_time.getTime().toString();
  }

  public ALDateTimeField getPunchDate() {
    return punch_date;
  }

  public ALDateTimeField getClockOutTime() {
    return clock_out_time;
  }

  public String getClockOutTime(String format) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(format);
      return sdf.format(clock_out_time.getValue());
    } catch (Exception e) {
      return "";
    }
  }

  public String getClockOutTimeHourMinuteText() {
    return clock_out_time.getTime().toString();
  }

  public ALStringField getType() {
    return type;
  }

  /**
   * 当日中最新の外出時間を得ます。
   *
   * @return
   */
  public ALDateTimeField getOutgoingTime() {
    for (int i = 0; i < EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY; i++) {
      if (outgoing_time.get(i).isNullHour()) {
        if (i == 0) {
          return null;
        }
        return outgoing_time.get(i - 1);
      }
    }
    return outgoing_time.get(EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY - 1);
  }

  /**
   * 当日中最近の復帰時間を得ます。
   *
   * @return
   */
  public ALDateTimeField getComebackTime() {
    for (int i = 0; i < EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY; i++) {
      if (!comeback_time.get(i).isNullHour()) {
        if (i == 0) {
          return null;
        }
        return comeback_time.get(i - 1);
      }
    }
    return comeback_time.get(EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY - 1);
  }

  /**
   * 当日全ての外出時間を得ます。
   *
   * @return
   */
  public List<ALDateTimeField> getAllOutgoingTime() {
    return outgoing_time;
  }

  /**
   * 当日全ての復帰時間を得ます。
   *
   * @return
   */
  public List<ALDateTimeField> getAllComebackTime() {
    return comeback_time;
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
  *
  */
  public ALStringField getRemarks() {
    return remarks;
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
   * @return
   */
  public ALStringField getWorkFlag() {
    return work_flag;
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
  public void setWorkDate(Date date) {
    work_date.setValue(date);
    list_date.setValue(ALDateUtil.format(date, "MM月dd日"));
  }

  public boolean getRemarksFlg() {
    return remarks_flg;
  }

  public boolean getReasonFlg() {
    return reason_flg;
  }

  public boolean getIsTypeP() {
    return isTypeP;
  }

  public boolean getIsTypeA() {
    return isTypeA;
  }

  public boolean getIsTypeH() {
    return isTypeH;
  }

  public boolean getIsTypeC() {
    return isTypeC;
  }

  public boolean getIsTypeE() {
    return isTypeE;
  }

  public void setRemarksFlg(boolean b) {
    remarks_flg = b;
  }

  public void setReasonFlg(boolean b) {
    reason_flg = b;
  }

  public void setIsTypeP(boolean b) {
    isTypeP = b;
  }

  public void setIsTypeA(boolean b) {
    isTypeA = b;
  }

  public void setIsTypeH(boolean b) {
    isTypeH = b;
  }

  public void setIsTypeC(boolean b) {
    isTypeC = b;
  }

  public void setIsTypeE(boolean b) {
    isTypeE = b;
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

  /**
   * @return isCurrentMonth
   */
  public boolean isCurrentMonth() {
    return isCurrentMonth;
  }

  /**
   * @param isCurrentMonth
   *          セットする isCurrentMonth
   */
  public void setCurrentMonth(boolean isCurrentMonth) {
    this.isCurrentMonth = isCurrentMonth;
  }

}

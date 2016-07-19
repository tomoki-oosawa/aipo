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
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipHolidaysManager;
import com.aimluck.eip.common.ALHoliday;
import com.aimluck.eip.http.HttpServletRequestLocator;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.services.config.ALConfigHandler;
import com.aimluck.eip.services.config.ALConfigService;

/**
 * スケジュールコンテナです。
 *
 */
public class ScheduleDayContainer implements ALData {

  /** <code>today</code> 日付 */
  private ALDateTimeField today;

  /** <code>scheduleList</code> スケジュールリスト */
  private List<ScheduleResultData> scheduleList;

  /** <code>spanRd</code> 期間スケジュール */
  private ScheduleResultData spanRd;

  /** <code>is_hasspan</code> 期間スケジュールがあるかどうか */
  private boolean is_hasspan = false;

  /** <code>holiday</code> 祝日情報 */
  private ALHoliday holiday;

  /*
   *
   */
  @Override
  public void initField() {
    // 日付
    today = new ALDateTimeField("yyyy-MM-dd-HH-mm");
    // スケジュールリスト
    scheduleList = new ArrayList<ScheduleResultData>();
    spanRd = null;
  }

  /**
   * スケジュールリストを取得します。
   *
   * @return
   */
  public List<ScheduleResultData> getScheduleList() {
    return scheduleList;
  }

  /**
   * 日付を設定します。
   *
   * @param date
   */
  public void setDate(Date date) {
    today.setValue(date);

    // 祝日かどうかを検証する．
    ALEipHolidaysManager holidaysManager = ALEipHolidaysManager.getInstance();
    holiday = holidaysManager.isHoliday(date);
  }

  /**
   * 日付を取得します。
   *
   * @return
   */
  public ALDateTimeField getDate() {
    return today;
  }

  /**
   *
   * @return ScheduleWeeklySelectDataのtodayのフォーマットに合わせたString
   */
  public String getToday() {
    return today.toString("yyyy-MM-dd");

  }

  /**
   * 期間スケジュールがあるかどうかを設定します。
   *
   * @param bool
   */
  public void setHasspan(boolean bool) {
    is_hasspan = bool;
  }

  /**
   * 期間スケジュールがNULLかどうか。
   *
   * @return
   */
  public boolean isSpanNull() {
    return spanRd == null;
  }

  /**
   * 期間スケジュールがあるかどうか。
   *
   * @return
   */
  public boolean isHasspan() {
    return is_hasspan;
  }

  /**
   * スケジュールを追加します。
   *
   * @param rd
   */
  public void addResultData(ScheduleResultData rd) {
    int size = scheduleList.size();
    boolean canAdd = true;
    boolean repeat_del = false;
    for (int i = 0; i < size; i++) {
      repeat_del = false;
      ScheduleResultData rd2 = scheduleList.get(i);
      if (rd.isRepeat()
        && rd2.isDummy()
        && rd.getScheduleId().getValue() == rd2.getParentId().getValue()
        && ScheduleUtils.equalsToDate(
          rd.getStartDate().getValue(),
          rd2.getStartDate().getValue(),
          false)) {
        // [繰り返しスケジュール] 親の ID を検索
        canAdd = false;
        break;
      }
      if (rd2.isRepeat()
        && rd.isDummy()
        && rd2.getScheduleId().getValue() == rd.getParentId().getValue()
        && ScheduleUtils.equalsToDate(
          rd.getStartDate().getValue(),
          rd2.getStartDate().getValue(),
          false)) {
        // [繰り返しスケジュール] 親の ID を検索
        scheduleList.remove(rd2);
        canAdd = true;
        repeat_del = true;
      }

      if (!repeat_del) {
        // 繰り返しスケジュールの変更／削除が無い場合

        if (!rd.isDummy() && !rd2.isDummy()) {
          // ダミースケジュールではないときに
          // 重複スケジュールを検出する。
          // 時間が重なっている場合重複スケジュールとする。
          if ((rd.getStartDate().getValue().before(
            rd2.getStartDate().getValue())
            && rd2.getStartDate().getValue().before(rd.getEndDate().getValue()))
            || (rd2.getStartDate().getValue().before(
              rd.getStartDate().getValue())
              && rd.getStartDate().getValue().before(
                rd2.getEndDate().getValue()))
            || (rd.getStartDate().getValue().before(rd2.getEndDate().getValue())
              && rd2.getEndDate().getValue().before(rd.getEndDate().getValue()))
            || (rd2.getStartDate().getValue().before(rd.getEndDate().getValue())
              && rd.getEndDate().getValue().before(rd2.getEndDate().getValue()))
            || (rd.getEndDate().getValue().equals(rd2.getEndDate().getValue())
              && rd.getStartDate().getValue().equals(
                rd2.getStartDate().getValue()))) {
            rd2.setDuplicate(true);
            rd.setDuplicate(true);
          }
        }
      }
    }
    if (canAdd) {
      scheduleList.add(rd);
    }
  }

  /**
   * 期間スケジュールを追加します。
   *
   * @param rd
   */
  public void setSpanResultData(ScheduleResultData rd) {
    spanRd = rd;
  }

  /**
   * 期間スケジュールを取得します。
   *
   * @return
   */
  public ScheduleResultData getSpanResultData() {
    return spanRd;
  }

  public static String getHolidayOfWeek() {
    HttpServletRequest request = HttpServletRequestLocator.get();
    String cacheHoliday = null;
    if (request != null) {
      try {
        cacheHoliday =
          (String) request.getAttribute(
            ALConfigHandler.Property.HOLIDAY_OF_WEEK.toString());
      } catch (Throwable ignore) {

      }
    }
    if (cacheHoliday == null) {
      cacheHoliday =
        ALConfigService.get(ALConfigHandler.Property.HOLIDAY_OF_WEEK);
      if (request != null) {
        request.setAttribute(
          ALConfigHandler.Property.HOLIDAY_OF_WEEK.toString(),
          cacheHoliday);
      }
    }
    return cacheHoliday;
  }

  /**
   * 祝日かどうかを検証する． 祝日の場合，true．
   *
   * @return
   */
  public boolean isHoliday() {

    return (holiday == null) ? false : true;

  }

  /**
   * 祝日を休日にするかどうかを検証する. 休日にする場合 ture
   */
  public boolean isDayOffHoliday() {
    String cacheHoliday = getHolidayOfWeek();
    return (cacheHoliday.charAt(8) == '0') ? false : true;
  }

  /**
   * 祝日情報を取得する．
   *
   * @return
   */
  public ALHoliday getHoliday() {
    return holiday;
  }

  /**
   * 休日かどうかを判定する 休日の場合 ture
   */
  public boolean isSetHoliday(int DayOfWeek) {
    // TODO
    String cacheHoliday = getHolidayOfWeek();
    /*
     * boolean holiday = cacheHoliday.charAt(8) != '0'; if (holiday) {
     * ALEipHolidaysManager holidaysManager =
     * ALEipHolidaysManager.getInstance(); if (holidaysManager.isHoliday(date)
     * != null) { return true; } }
     */

    return cacheHoliday.charAt(DayOfWeek) != '0';

  }

}

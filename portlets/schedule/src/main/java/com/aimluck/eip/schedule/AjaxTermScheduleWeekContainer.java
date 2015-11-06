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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.aimluck.eip.common.ALData;
import com.aimluck.eip.schedule.util.ScheduleUtils;

/**
 * カレンダー用のスケジュールのコンテナです。
 * 
 */
public class AjaxTermScheduleWeekContainer implements ALData {

  /** <code>dayList</code> JOB リスト */
  private List<AjaxTermScheduleDayContainer> dayList;

  /*
   * 
   */
  public void initField() {
    dayList = new ArrayList<AjaxTermScheduleDayContainer>();
  }

  /**
   * 表示開始する日付を設定します。
   * 
   * @param cal
   */
  public void setViewStartDate(Calendar cal) {
    for (int i = 1; i <= 7; i++) {
      // 日付を1日ずつずらす
      AjaxTermScheduleDayContainer con = new AjaxTermScheduleDayContainer();
      con.initField();
      con.setDate(cal.getTime());
      dayList.add(con);
      cal.add(Calendar.DATE, 1);
    }
  }

  /**
   * Term をコンテナに格納します。
   * 
   * @param rd
   */
  public void addTermResultData(int index, AjaxScheduleResultData rd) {
    int span = rd.getRowspan();
    AjaxTermScheduleDayContainer con = dayList.get(index);
    con.setHasTerm(true);
    con.setTermResultData(rd);
    for (int i = 1; i < span; i++) {
      con = dayList.get(index + i);
      con.setHasTerm(true);
    }
  }

  public boolean canAddTerm(AjaxScheduleResultData rd) {
    boolean canAdd = true;
    Date startDate = rd.getStartDate().getValue();
    Date endDate = rd.getEndDate().getValue();

    AjaxTermScheduleDayContainer con;
    int size = dayList.size();
    for (int i = 0; i < size; i++) {
      con = dayList.get(i);
      Date conDate = con.getDate().getValue();
      boolean isEmptyStartDate =
        ScheduleUtils.equalsToDate(
          ScheduleUtils.getEmptyDate(),
          startDate,
          false);
      boolean isEmptyEndDate =
        ScheduleUtils
          .equalsToDate(ScheduleUtils.getEmptyDate(), endDate, false);
      if (isEmptyStartDate && isEmptyEndDate) {
        // 開始日と入稿日が未設定の場合
        if (con.isHasTerm()) {
          canAdd = false;
        }
      } else {
        if (isEmptyStartDate) {
          // 開始日が未設定の場合
          if (ScheduleUtils.equalsToDate(endDate, conDate, false)
            || conDate.before(endDate)) {
            if (con.isHasTerm()) {
              canAdd = false;
            }
          }
        } else if (isEmptyEndDate) {
          // 入稿日が未設定の場合
          if (ScheduleUtils.equalsToDate(startDate, conDate, false)
            || conDate.after(startDate)) {
            if (con.isHasTerm()) {
              canAdd = false;
            }
          }
        } else {
          // 開始日と入稿日が設定されている場合
          if ((ScheduleUtils.equalsToDate(startDate, conDate, false) || conDate
            .after(startDate))
            && (ScheduleUtils.equalsToDate(endDate, conDate, false) || conDate
              .before(endDate))) {
            if (con.isHasTerm()) {
              canAdd = false;
            }
          }
        }
      }
    }
    return canAdd;
  }

  /**
   * JOB リストを取得します。
   * 
   * @return
   * 
   * @uml.property name="dayList"
   */
  public List<AjaxTermScheduleDayContainer> getDayList() {
    return dayList;
  }

  /**
   * 表示すべき期間スケジュールが存在するかどうかを返します
   * 
   * @return
   */
  public boolean hasVisibleTerm() {
    for (AjaxTermScheduleDayContainer dayContainer : dayList) {
      if (dayContainer.isHasTerm()) {
        AjaxScheduleResultData rd = dayContainer.getTermResultData();
        if (rd != null
          && (!rd.isPrivate() || rd.isLoginuser() || rd.isMember())) {
          return true;
        }
      }
    }
    return false;
  }
}

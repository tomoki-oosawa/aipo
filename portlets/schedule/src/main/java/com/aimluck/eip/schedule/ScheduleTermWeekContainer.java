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
package com.aimluck.eip.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.aimluck.eip.common.ALData;
import com.aimluck.eip.schedule.util.ScheduleUtils;

/**
 * 期間スケジュールのコンテナです。
 * 
 */
public class ScheduleTermWeekContainer implements ALData {

  /** <code>dayList</code> １日分のリスト */
  private List<ScheduleTermDayContainer> dayList;

  /*
   * 
   */
  public void initField() {
    dayList = new ArrayList<ScheduleTermDayContainer>();
  }

  /**
   * 表示開始する日付を設定します。
   * 
   * @param cal
   */
  public void setViewStartDate(Calendar cal) {
    for (int i = 1; i <= 7; i++) {
      // 日付を1日ずつずらす
      ScheduleTermDayContainer con = new ScheduleTermDayContainer();
      con.initField();
      con.setDate(cal.getTime());
      dayList.add(con);
      cal.add(Calendar.DATE, 1);
    }
  }

  /**
   * 期間スケジュールをコンテナに格納します。
   * 
   * @param rd
   */
  public void addTermResultData(int index, ScheduleResultData rd) {
    int span = rd.getRowspan();
    ScheduleTermDayContainer con = dayList.get(index);
    con.setHasTerm(true);
    con.setTermResultData(rd);
    for (int i = 1; i < span; i++) {
      con = dayList.get(index + i);
      con.setHasTerm(true);
    }
  }

  public boolean canAddTerm(ScheduleResultData rd) {
    boolean canAdd = true;
    Date startDate = rd.getStartDate().getValue();
    Date endDate = rd.getEndDate().getValue();

    ScheduleTermDayContainer con;
    int size = dayList.size();
    for (int i = 0; i < size; i++) {
      con = dayList.get(i);
      Date conDate = con.getDate().getValue();

      if ((ScheduleUtils.equalsToDate(startDate, conDate, false) || conDate
        .after(startDate))
        && (ScheduleUtils.equalsToDate(endDate, conDate, false) || conDate
          .before(endDate))) {
        if (con.isHasTerm()) {
          canAdd = false;
        }
      }
    }
    return canAdd;
  }

  /**
   * １日分のリストを取得します。
   * 
   * @return
   * 
   * @uml.property name="dayList"
   */
  public List<ScheduleTermDayContainer> getDayList() {
    return dayList;
  }

}

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aimluck.eip.common.ALData;
import com.aimluck.eip.exttimecard.util.ExtTimecardUtils;

/**
 *
 */
public class ExtTimecardListResultDataContainer implements ALData {

  private List<Map<Integer, ExtTimecardListResultData>> list;

  // 起点となる週の初めの曜日
  private Date queryStartDate;

  public ExtTimecardListResultDataContainer(Date startDate) {
    this.queryStartDate = startDate;
  }

  @Override
  public void initField() {
    list = new ArrayList<Map<Integer, ExtTimecardListResultData>>() {

      private static final long serialVersionUID = 7614354348253756254L;

      {
        this.add(new HashMap<Integer, ExtTimecardListResultData>());
        this.add(new HashMap<Integer, ExtTimecardListResultData>());
        this.add(new HashMap<Integer, ExtTimecardListResultData>());
        this.add(new HashMap<Integer, ExtTimecardListResultData>());
        this.add(new HashMap<Integer, ExtTimecardListResultData>());
        this.add(new HashMap<Integer, ExtTimecardListResultData>());
        this.add(new HashMap<Integer, ExtTimecardListResultData>());
      }
    };
  }

  public void add(ExtTimecardListResultData rd) {
    if (list == null) {
      initField();
    }
    int weekOfMonth = getWeekOfMonth(rd.getRd().getPunchDate().getValue());
    Map<Integer, ExtTimecardListResultData> map = list.get(weekOfMonth);
    if (map != null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(rd.getRd().getPunchDate().getValue());
      map.put(cal.get(Calendar.DAY_OF_WEEK), rd);
    }
  }

  /**
   * 法定休日扱いとするかどうか
   *
   * @param rd
   * @return
   */
  public boolean isStatutoryOffDay(ExtTimecardListResultData rd) {
    int weekOfMonth = getWeekOfMonth(rd.getRd().getPunchDate().getValue());
    Map<Integer, ExtTimecardListResultData> map = list.get(weekOfMonth);
    List<Integer> offdayDayOfWeek =
      ExtTimecardUtils.getOffdayDayOfWeek(rd.getTimecardSystem());
    boolean allWork = true;
    int last = -1;
    for (int dayOfWeek : offdayDayOfWeek) {
      last = dayOfWeek;
      ExtTimecardListResultData data = map.get(dayOfWeek);
      if (data == null
        || data.getOffHour() == ExtTimecardListResultData.NO_DATA) {
        allWork = false;
      }
    }
    if (allWork) {
      // 休日をすべて出勤した場合、後の曜日を法定休日とする。
      Calendar cal = Calendar.getInstance();
      cal.setTime(rd.getRd().getPunchDate().getValue());
      return last == cal.get(Calendar.DAY_OF_WEEK);
    } else {
      return false;
    }
  }

  /**
   * @return startDate
   */
  public Date getStartDate() {
    return queryStartDate;
  }

  /**
   * @param startDate
   *          セットする startDate
   */
  public void setStartDate(Date startDate) {
    this.queryStartDate = startDate;
  }

  protected int getWeekOfMonth(Date date) {
    int differenceDays = differenceDays(queryStartDate, date);
    if (differenceDays < 0) {
      differenceDays = -differenceDays;
    }
    int result = differenceDays / 7;
    return result;
  }

  protected int differenceDays(Date date1, Date date2) {
    long datetime1 = date1.getTime();
    long datetime2 = date2.getTime();
    long one_date_time = 1000 * 60 * 60 * 24;
    long diffDays = (datetime1 - datetime2) / one_date_time;
    return (int) diffDays;
  }

}

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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.aimluck.commons.field.ALDateField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.timecard.util.TimecardUtils;

/**
 * タイムカードのResultDataです。
 * 
 */
public class TimecardListResultData implements ALData {

  private ALDateField date = null;

  private List<TimecardResultData> list = null;

  /**
   *
   */
  @Override
  public void initField() {
    date = new ALDateField();
    date.setValue(new Date());
    list = new ArrayList<TimecardResultData>();
  }

  public void setDate(Date date) {
    this.date.setValue(date);
  }

  public ALDateField getDate() {
    return date;
  }

  public String getDateStr() {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日(EE)");
      return sdf.format(date.getValue().getDate());
    } catch (Exception e) {
      return "";
    }
  }

  public List<TimecardResultData> getList() {
    return list;
  }

  public List<TimecardResultData> getViewList() {
    List<TimecardResultData> viewlist = new ArrayList<TimecardResultData>();

    TimecardResultData rd = null;

    int size = list.size();
    for (int i = 0; i < size; i++) {
      rd = list.get(i);
      if (!TimecardUtils.WORK_FLG_DUMMY.equals(rd.getWorkFlag().getValue())) {
        viewlist.add(rd);
      }
    }
    return viewlist;
  }

  public void addTimecardResultData(TimecardResultData rd) {
    list.add(rd);
  }

  public String getSummayTimes() {
    double millisecond = 0;
    Date workOnDate = null;
    Date workOffDate = null;
    TimecardResultData rd = null;
    int size = list.size();

    if (size < 1) {
      return "0";
    }

    if (size == 1) {
      rd = list.get(size - 1);
      if (TimecardUtils.WORK_FLG_ON.equals(rd.getWorkFlag().getValue())) {
        workOnDate = rd.getWorkDate().getValue();
        Calendar cal = Calendar.getInstance();
        millisecond += cal.getTime().getTime() - workOnDate.getTime();
      } else {
        workOffDate = rd.getWorkDate().getValue();
        // 退勤から始まる場合
        Calendar cal = Calendar.getInstance();
        cal.setTime(workOffDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        millisecond += workOffDate.getTime() - cal.getTime().getTime();
      }
    } else {
      for (int i = 0; i < size; i++) {
        rd = list.get(i);
        if (TimecardUtils.WORK_FLG_ON.equals(rd.getWorkFlag().getValue())) {
          workOnDate = rd.getWorkDate().getValue();
        } else {
          workOffDate = rd.getWorkDate().getValue();
          if (workOnDate == null) {
            // 前日の最後が『出勤』の場合
            Calendar cal = Calendar.getInstance();
            cal.setTime(workOffDate);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            millisecond += workOffDate.getTime() - cal.getTime().getTime();
          } else {
            millisecond += workOffDate.getTime() - workOnDate.getTime();
          }
        }
      }
    }

    BigDecimal decimal = new BigDecimal(millisecond / 1000 / 60 / 60);
    DecimalFormat dformat = new DecimalFormat("##.#");
    return dformat.format(decimal
      .setScale(1, BigDecimal.ROUND_FLOOR)
      .doubleValue());
  }
}

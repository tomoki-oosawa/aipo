/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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
import java.util.List;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.common.ALData;

/**
 * １日スケジュールのコンテナです。
 * 
 */
public class ScheduleOnedayContainer implements ALData {

  /** <code>list</code> スケジュールリスト */
  private List<ScheduleOnedayResultData> list;

  /** <code>dList</code> 重複スケジュールリスト */
  private List<ScheduleOnedayResultData> dList;

  /** <code>rd</code> 期間スケジュール */
  private ScheduleOnedayResultData rd;

  /** <code>rows</code> rows */
  private int rows[];

  /** <code>count</code> count */
  private int count;

  /** <code>rowIndex</code> rowIndex */
  private int rowIndex;

  /*
   * 
   */
  public void initField() {
    // スケジュールリスト
    list = new ArrayList<ScheduleOnedayResultData>();
    // 重複スケジュールリスト
    dList = new ArrayList<ScheduleOnedayResultData>();
  }

  /**
   * 開始時間、終了時間を設定します。
   * 
   * @param startHour
   * @param endHour
   */
  public void initHour(int startHour, int endHour) {
    rows = new int[(endHour - startHour) * 4 + 1];
    int size = rows.length;
    for (int i = 0; i < size; i++) {
      rows[i] = 1;
    }
  }

  /**
   * スケジュールを追加します。
   * 
   * @param rd
   * @param startHour
   * @param endHour
   * @param viewDate
   */
  public void addResultData(ScheduleOnedayResultData rd, int startHour,
      int endHour, ALDateTimeField viewDate) {
    ScheduleOnedayResultData rd2 = new ScheduleOnedayResultData();
    rd2.setFormat("yyyy-MM-dd-HH-mm");
    rd2.initField();
    // Oneday
    boolean dup = false;
    int sta = startHour * 4;
    int eta = endHour * 4;
    int st =
      Integer.parseInt(rd.getStartDate().getHour())
        * 4
        + Integer.parseInt(rd.getStartDate().getMinute())
        / 15;
    int ed =
      Integer.parseInt(rd.getEndDate().getHour())
        * 4
        + Integer.parseInt(rd.getEndDate().getMinute())
        / 15;
    if (!(rd.getStartDate().getDay().equals(rd.getEndDate().getDay()))
      && rd.getEndDate().getHour().equals("0")) {
      ed = 4 * 24;
    }
    if ((ed - sta > 0 && eta - st > 0) || (ed - sta == 0 && st == ed)) {
      if (sta > st) {
        st = sta;
      }
      if (eta < ed) {
        ed = eta;
      }

      int tmpRowIndex = rowIndex;
      rd.setStartRow(st - sta);
      rd.setEndRow(ed - sta);
      if ((ed - st == 0) && (st - sta + tmpRowIndex - count >= 0)) {
        rd.setIndex(rows[st - sta]);
        if (rows[st - sta] > 1) {
          rd.setDuplicate(true);
          list.get(list.size() - 1).setDuplicate(true);
        }
        rows[st - sta]++;
        rowIndex++;
        ed++;
      }
      sta -= tmpRowIndex;
      // eta -= tmpRowIndex;
      if (st - sta - count > 0) {
        // Rowspan は Velocity で設定される。
        // rd2.setRowspan(st - sta - count);
        Calendar cal = Calendar.getInstance();
        cal.setTime(viewDate.getValue());
        cal.add(Calendar.HOUR, startHour);
        int hour = (count - tmpRowIndex) / 4;
        int min = ((count - tmpRowIndex) % 4) * 15;
        cal.add(Calendar.HOUR, hour);
        cal.add(Calendar.MINUTE, min);
        rd2.setStartDate(cal.getTime());
        hour = (st - sta - count) / 4;
        min = ((st - sta - count) % 4) * 15;
        cal.add(Calendar.HOUR, hour);
        cal.add(Calendar.MINUTE, min);
        rd2.setEndDate(cal.getTime());
        rd2.setStartRow(list.size() == 0 ? 0 : list
          .get(list.size() - 1)
          .getEndRow());
        rd2.setEndRow(rd.getStartRow());
        list.add(rd2);
      } else if (st - sta - count != 0) {
        dList.add(rd);
        rd.setDuplicate(true);
        dup = true;
        list.get(list.size() - 1).setDuplicate(true);
      } else {

      }
      if (!dup) {
        list.add(rd);
        count = ed - sta;
      }

    }
  }

  /**
   * 後処理を行います。
   * 
   * @param startHour
   * @param endHour
   * @param viewDate
   */
  public void last(int startHour, int endHour, ALDateTimeField viewDate) {
    int index = (endHour - startHour) * 4 + rowIndex;
    if (index > count) {
      ScheduleOnedayResultData rd = new ScheduleOnedayResultData();
      rd.setFormat("yyyy-MM-dd-HH-mm");
      rd.initField();
      rd.setRowspan(index - count);
      Calendar cal = Calendar.getInstance();
      cal.setTime(viewDate.getValue());
      cal.add(Calendar.HOUR, startHour);
      int hour = (count - rowIndex) / 4;
      int min = ((count - rowIndex) % 4) * 15;
      cal.add(Calendar.HOUR, hour);
      cal.add(Calendar.MINUTE, min);
      rd.setStartDate(cal.getTime());
      hour = (index - count) / 4;
      min = ((index - count) % 4) * 15;
      cal.add(Calendar.HOUR, hour);
      cal.add(Calendar.MINUTE, min);
      rd.setEndDate(cal.getTime());
      rd.setStartRow(list.size() == 0 ? 0 : list
        .get(list.size() - 1)
        .getEndRow());
      rd.setEndRow(rows.length - 1);
      list.add(rd);
    }
  }

  /**
   * 期間スケジュールを設定します。
   * 
   * @param rd
   */
  public void setSpanResultData(ScheduleOnedayResultData rd) {
    this.rd = rd;
  }

  /**
   * rowsを取得します。
   * 
   * @return
   */
  public int[] getRows() {
    return rows;
  }

  /**
   * 期間スケジュールを取得します。
   * 
   * @return
   */
  public ScheduleOnedayResultData getSpanResultData() {
    return rd;
  }

  /**
   * スケジュールリストを取得します。
   * 
   * @return
   */
  public List<ScheduleOnedayResultData> getSchedule() {
    return list;
  }

  /**
   * 期間スケジュールリストを取得します。
   * 
   * @return
   */
  public List<ScheduleOnedayResultData> getDuplicateSchedule() {
    return dList;
  }

  /**
   * 重複スケジュールがあるかどうか
   * 
   * @return
   */
  public boolean isDuplicate() {
    return dList.size() != 0;
  }

}

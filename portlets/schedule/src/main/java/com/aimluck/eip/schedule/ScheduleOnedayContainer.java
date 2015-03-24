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

  /** <code>dList</code> 重複スケジュールリスト */
  private List<ScheduleOnedayResultData> dupList;

  /** <code>rowIndex</code> rowIndex */

  private int dRowCount;

  /** <code>rd</code> 期間スケジュール */
  private ScheduleOnedayResultData rd;

  /** <code>rows</code> rows */
  private int rows[];

  /** <code>count</code> count */
  private int count;

  /** <code>count</code> count */
  private int dcount;

  /** <code>rowIndex</code> rowIndex */
  private int rowIndex;

  /*
   *
   */
  @Override
  public void initField() {
    // スケジュールリスト
    list = new ArrayList<ScheduleOnedayResultData>();
    // 重複スケジュールリスト
    dList = new ArrayList<ScheduleOnedayResultData>();
    // 重複スケジュールリスト
    dupList = new ArrayList<ScheduleOnedayResultData>();
  }

  /**
   * 開始時間、終了時間を設定します。
   * 
   * @param startHour
   * @param endHour
   */
  public void initHour(int startHour, int endHour) {
    rows = new int[(endHour - startHour) * 12 + 1];
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
    int sta = startHour * 12;
    int eta = endHour * 12;
    int st =
      Integer.parseInt(rd.getStartDate().getHour())
        * 12
        + Integer.parseInt(rd.getStartDate().getMinute())
        / 5;
    int ed =
      Integer.parseInt(rd.getEndDate().getHour())
        * 12
        + Integer.parseInt(rd.getEndDate().getMinute())
        / 5;
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
      if (st - sta - count > 0) {
        // Rowspan は Velocity で設定される。
        // rd2.setRowspan(st - sta - count);
        Calendar cal = Calendar.getInstance();
        cal.setTime(viewDate.getValue());
        cal.add(Calendar.HOUR, startHour);
        int hour = (count - tmpRowIndex) / 12;
        int min = ((count - tmpRowIndex) % 12) * 5;
        cal.add(Calendar.HOUR, hour);
        cal.add(Calendar.MINUTE, min);
        rd2.setStartDate(cal.getTime());
        hour = (st - sta - count) / 12;
        min = ((st - sta - count) % 12) * 5;
        cal.add(Calendar.HOUR, hour);
        cal.add(Calendar.MINUTE, min);
        rd2.setEndDate(cal.getTime());
        rd2.setStartRow(list.size() == 0 ? 0 : list
          .get(list.size() - 1)
          .getEndRow());
        rd2.setEndRow(rd.getStartRow());
        list.add(rd2);
      } else if (st - sta - count != 0) {
        rd.setDuplicate(true);
        dup = true;
        list.get(list.size() - 1).setDuplicate(true);
        dupList.add(rd);
        // 重複スケジュールの並べ替え
        dRowCount = dupList.size();
        for (int i = 0; i < dupList.size() - 1; i++) {
          for (int j = i + 1; j < dupList.size(); j++) {
            if ((dupList.get(i).getEndDateTime() <= dupList
              .get(j)
              .getStartDateTime())) {
              dupList.add(i + 1, dupList.get(j));
              dupList.remove(j + 1);
              dRowCount = dRowCount - 1;
              break;
            } else if ((dupList.get(i).getEndRow() <= dupList
              .get(j)
              .getStartRow())) {
              dRowCount = dRowCount - 1;
            }
          }
        }
        interrupDList(rd, dupList, startHour, endHour, viewDate);
      }

      if (!dup) {
        list.add(rd);
        count = ed - sta;
      } else {
        rd.setdRowCount(dRowCount);
      }
    }
  }

  /**
   * @param rd3
   * @param dupList2
   * @param i
   * @param viewDate
   * @param endHour
   * @param startHour
   */
  private void interrupDList(ScheduleOnedayResultData rd3,
      List<ScheduleOnedayResultData> dupList2, int startHour, int endHour,
      ALDateTimeField viewDate) {
    dList.clear();
    dcount = 0;

    int i = 0;
    int tmpRowIndex = 0;
    int dRowIndex = 0;
    // 重複スケジュールでのrows加算
    int sta = startHour * 12;
    int eta = endHour * 12;
    int st =
      Integer.parseInt(rd3.getStartDate().getHour())
        * 12
        + Integer.parseInt(rd3.getStartDate().getMinute())
        / 5;
    int ed =
      Integer.parseInt(rd3.getEndDate().getHour())
        * 12
        + Integer.parseInt(rd3.getEndDate().getMinute())
        / 5;
    if (!(rd3.getStartDate().getDay().equals(rd3.getEndDate().getDay()))
      && rd3.getEndDate().getHour().equals("0")) {
      ed = 12 * 24;
    }
    if ((ed - sta > 0 && eta - st > 0) || (ed - sta == 0 && st == ed)) {
      if (sta > st) {
        st = sta;
      }
      if (eta < ed) {
        ed = eta;
      }

      tmpRowIndex = rowIndex;
      if ((ed - st == 0) && (st - sta + tmpRowIndex - dcount >= 0)) {
        rd3.setIndex(rows[st - sta]);
        if (rows[st - sta] > 1) {
          rd3.setDuplicate(true);
        }
        rows[st - sta]++;
      }
    }

    do {
      ScheduleOnedayResultData rd2 = new ScheduleOnedayResultData();
      rd2.setFormat("yyyy-MM-dd-HH-mm");
      rd2.initField();
      // Oneday
      sta = startHour * 12;
      eta = endHour * 12;
      st =
        Integer.parseInt(dupList2.get(i).getStartDate().getHour())
          * 12
          + Integer.parseInt(dupList2.get(i).getStartDate().getMinute())
          / 5;
      ed =
        Integer.parseInt(dupList2.get(i).getEndDate().getHour())
          * 12
          + Integer.parseInt(dupList2.get(i).getEndDate().getMinute())
          / 5;
      if (!(dupList2.get(i).getStartDate().getDay().equals(dupList2
        .get(i)
        .getEndDate()
        .getDay()))
        && dupList2.get(i).getEndDate().getHour().equals("0")) {
        ed = 12 * 24;
      }
      if ((ed - sta > 0 && eta - st > 0) || (ed - sta == 0 && st == ed)) {
        if (sta > st) {
          st = sta;
        }
        if (eta < ed) {
          ed = eta;
        }

        tmpRowIndex = dRowIndex;
        // tmpRowIndex = rows[st - sta];
        dupList2.get(i).setStartRow(st - sta);
        dupList2.get(i).setEndRow(ed - sta);
        if ((ed - st == 0) && (st - sta + tmpRowIndex - dcount >= 0)) {
          dRowIndex++;
          ed++;
        }
        sta -= tmpRowIndex;
        if (i > 0) {
          if (st - sta - dcount > 0) {
            // Rowspan は Velocity で設定される。
            // rd2.setRowspan(st - sta - count);
            Calendar cal = Calendar.getInstance();
            cal.setTime(viewDate.getValue());
            cal.add(Calendar.HOUR, startHour);
            int hour = (dcount - tmpRowIndex) / 12;
            int min = ((dcount - tmpRowIndex) % 12) * 5;
            cal.add(Calendar.HOUR, hour);
            cal.add(Calendar.MINUTE, min);
            rd2.setStartDate(cal.getTime());
            hour = (st - sta - dcount) / 12;
            min = ((st - sta - dcount) % 12) * 5;
            cal.add(Calendar.HOUR, hour);
            cal.add(Calendar.MINUTE, min);
            rd2.setEndDate(cal.getTime());
            rd2.setStartRow(dList.size() == 0 ? 0 : dList
              .get(dList.size() - 1)
              .getEndRow());
            rd2.setEndRow(dupList2.get(i).getStartRow());
            dList.add(rd2);
          } else if (st - sta - dcount == 0) {

          } else {
            int index = (endHour - startHour) * 12 + dRowIndex;
            if (index > dcount) {
              ScheduleOnedayResultData rd = new ScheduleOnedayResultData();
              rd.setFormat("yyyy-MM-dd-HH-mm");
              rd.initField();
              rd.setRowspan(index - dcount);
              Calendar cal = Calendar.getInstance();
              cal.setTime(viewDate.getValue());
              cal.add(Calendar.HOUR, startHour);
              int hour = (dcount - dRowIndex) / 12;
              int min = ((dcount - dRowIndex) % 12) * 5;
              cal.add(Calendar.HOUR, hour);
              cal.add(Calendar.MINUTE, min);
              rd.setStartDate(cal.getTime());
              hour = (index - dcount) / 12;
              min = ((index - dcount) % 12) * 5;
              cal.add(Calendar.HOUR, hour);
              cal.add(Calendar.MINUTE, min);
              rd.setEndDate(cal.getTime());
              rd.setStartRow(dList.size() == 0 ? 0 : dList
                .get(dList.size() - 1)
                .getEndRow());
              rd.setEndRow(rows.length - 1);
              dList.add(rd);
            }
            dcount = 0;
            dRowIndex = 0;
            tmpRowIndex = dRowIndex;
            sta = startHour * 12;
            if (st - sta - dcount > 0) {
              Calendar cal = Calendar.getInstance();
              cal.setTime(viewDate.getValue());
              cal.add(Calendar.HOUR, startHour);
              int hour = (dcount - tmpRowIndex) / 12;
              int min = ((dcount - tmpRowIndex) % 12) * 5;
              cal.add(Calendar.HOUR, hour);
              cal.add(Calendar.MINUTE, min);
              rd2.setStartDate(cal.getTime());
              hour = (st - sta - dcount) / 12;
              min = ((st - sta - dcount) % 12) * 5;
              cal.add(Calendar.HOUR, hour);
              cal.add(Calendar.MINUTE, min);
              rd2.setEndDate(cal.getTime());
              rd2.setStartRow(0);
              rd2.setEndRow(dupList2.get(i).getStartRow());
              dList.add(rd2);
            }
          }
        } else {
          if (st - sta - dcount > 0) {
            // Rowspan は Velocity で設定される。
            // rd2.setRowspan(st - sta - count);
            Calendar cal = Calendar.getInstance();
            cal.setTime(viewDate.getValue());
            cal.add(Calendar.HOUR, startHour);
            int hour = (dcount - tmpRowIndex) / 12;
            int min = ((dcount - tmpRowIndex) % 12) * 5;
            cal.add(Calendar.HOUR, hour);
            cal.add(Calendar.MINUTE, min);
            rd2.setStartDate(cal.getTime());
            hour = (st - sta - dcount) / 12;
            min = ((st - sta - dcount) % 12) * 5;
            cal.add(Calendar.HOUR, hour);
            cal.add(Calendar.MINUTE, min);
            rd2.setEndDate(cal.getTime());
            rd2.setStartRow(dList.size() == 0 ? 0 : dList
              .get(dList.size() - 1)
              .getEndRow());
            rd2.setEndRow(dupList2.get(i).getStartRow());
            dList.add(rd2);
          } else if (st - sta - dcount != 0) {
          }
        }
        dcount = ed - sta;
        dList.add(dupList2.get(i));
      }
      i++;
    } while (i < dupList2.size());

  }

  /**
   * 後処理を行います。
   * 
   * @param startHour
   * @param endHour
   * @param viewDate
   */
  public void last(int startHour, int endHour, ALDateTimeField viewDate) {
    int index = (endHour - startHour) * 12 + rowIndex;
    if (index > count) {
      ScheduleOnedayResultData rd = new ScheduleOnedayResultData();
      rd.setFormat("yyyy-MM-dd-HH-mm");
      rd.initField();
      rd.setRowspan(index - count);
      Calendar cal = Calendar.getInstance();
      cal.setTime(viewDate.getValue());
      cal.add(Calendar.HOUR, startHour);
      int hour = (count - rowIndex) / 12;
      int min = ((count - rowIndex) % 12) * 5;
      cal.add(Calendar.HOUR, hour);
      cal.add(Calendar.MINUTE, min);
      rd.setStartDate(cal.getTime());
      hour = (index - count) / 12;
      min = ((index - count) % 12) * 5;
      cal.add(Calendar.HOUR, hour);
      cal.add(Calendar.MINUTE, min);
      rd.setEndDate(cal.getTime());
      rd.setStartRow(list.size() == 0 ? 0 : list
        .get(list.size() - 1)
        .getEndRow());
      rd.setEndRow(rows.length - 1);
      list.add(rd);
    }

    if (index > dcount + rowIndex) {
      ScheduleOnedayResultData rd = new ScheduleOnedayResultData();
      rd.setFormat("yyyy-MM-dd-HH-mm");
      rd.initField();
      rd.setRowspan(index - dcount);
      Calendar cal = Calendar.getInstance();
      cal.setTime(viewDate.getValue());
      cal.add(Calendar.HOUR, startHour);
      int hour = (dcount - rowIndex) / 12;
      int min = ((dcount - rowIndex) % 12) * 5;
      cal.add(Calendar.HOUR, hour);
      cal.add(Calendar.MINUTE, min);
      rd.setStartDate(cal.getTime());
      hour = (index - dcount) / 12;
      min = ((index - dcount) % 12) * 5;
      cal.add(Calendar.HOUR, hour);
      cal.add(Calendar.MINUTE, min);
      rd.setEndDate(cal.getTime());
      rd.setStartRow(dList.size() == 0 ? 0 : dList
        .get(dList.size() - 1)
        .getEndRow());
      rd.setEndRow(rows.length - 1);
      dList.add(rd);
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
   * 重複スケジュールリストを取得します。
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

  /**
   * @return
   */
  public int getDuplicateScheduleRowCount() {
    return dRowCount;
  }

}

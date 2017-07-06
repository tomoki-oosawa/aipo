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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.schedule.util.ScheduleUtils;

/**
 * 週間スケジュールのコンテナです。
 * 
 */
public class ScheduleListContainer implements ALData {

  public static final int SCHEDULE_LIST_DATE_LIMIT = 7;

  private ALDateTimeField viewStartDate;

  private List<ScheduleResultData> scheduleList;

  private boolean isSort = false;

  public void setViewStartDate(Calendar cal) {
    viewStartDate.setValue(cal.getTime());
  }

  @Override
  public void initField() {
    viewStartDate = new ALDateTimeField("yyyy-MM-dd-HH-mm");
    // スケジュールリスト
    scheduleList = new ArrayList<ScheduleResultData>();
  }

  public boolean addResultData(ScheduleSearchResultData rd) {
    isSort = false;
    Calendar startDate = Calendar.getInstance();
    startDate.setTime(viewStartDate.getValue());
    Calendar endDate = Calendar.getInstance();
    endDate.setTime(startDate.getTime());
    endDate.add(Calendar.DATE, SCHEDULE_LIST_DATE_LIMIT);
    while (startDate.before(endDate)) {
      ALDateTimeField field = new ALDateTimeField("yyyy-MM-dd-HH-mm");
      ScheduleSearchResultData addRd = new ScheduleSearchResultData();
      field.setValue(startDate.getTime());
      if (!rd.getPattern().equals("N")) {
        // 繰り返しスケジュール
        boolean isClone = false;
        boolean isSpan = false;
        if (rd.getPattern().equals("S")
          && !field.getValue().before(rd.getStartDate().getValue())
          && !field.getValue().after(rd.getEndDate().getValue())) {
          isClone = true;
          isSpan = true;
        }
        if (!rd.getPattern().equals("S")
          && ScheduleUtils.isView(field, rd.getPattern(), rd
            .getStartDate()
            .getValue(), rd.getEndDate().getValue())) {
          isClone = true;
        }
        if (isClone) {
          Calendar temp = Calendar.getInstance();
          temp.setTime(field.getValue());
          temp
            .set(Calendar.HOUR, Integer.parseInt(rd.getStartDate().getHour()));
          temp.set(Calendar.MINUTE, Integer.parseInt(rd
            .getStartDate()
            .getMinute()));
          temp.set(Calendar.SECOND, 0);
          temp.set(Calendar.MILLISECOND, 0);
          Calendar temp2 = Calendar.getInstance();
          temp2.setTime(field.getValue());
          temp2.set(Calendar.HOUR, Integer.parseInt(rd.getEndDate().getHour()));
          temp2.set(Calendar.MINUTE, Integer.parseInt(rd
            .getEndDate()
            .getMinute()));
          temp2.set(Calendar.SECOND, 0);
          temp2.set(Calendar.MILLISECOND, 0);
          addRd.initField();
          addRd.setScheduleId((int) rd.getScheduleId().getValue());
          addRd.setParentId((int) rd.getParentId().getValue());
          addRd.setName(rd.getName().getValue());
          // 開始日を設定し直す
          addRd.setStartDate(temp.getTime());
          // 終了日を設定し直す
          addRd.setEndDate(temp2.getTime());
          addRd.setTmpreserve(rd.isTmpreserve());
          addRd.setPublic(rd.isPublic());
          addRd.setHidden(rd.isHidden());
          addRd.setDummy(rd.isDummy());
          addRd.setLoginuser(rd.isLoginuser());
          addRd.setOwner(rd.isOwner());
          addRd.setMember(rd.isMember());
          addRd.setType(rd.getType());
          SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
          if (!sdf.format(rd.getStartDate().getValue()).equals(
            sdf.format(rd.getEndDate().getValue()))) {
            addRd.setTerm(true);
          }
          // 繰り返しはON
          addRd.setRepeat(!isSpan);
          addRd.setPattern(rd.getPattern());
          addRd.setCreateUser(rd.getCreateUser());
          addResultDataInternal(addRd);
        }
      } else {
        addResultDataInternal(rd);
        return true;
      }
      startDate.add(Calendar.DATE, 1);
    }
    return false;
  }

  protected void addResultDataInternal(ScheduleResultData rd) {
    int size = scheduleList.size();
    boolean canAdd = true;
    boolean repeat_del = false;
    for (int i = 0; i < size; i++) {
      repeat_del = false;
      ScheduleResultData rd2 = scheduleList.get(i);
      if (rd.isRepeat()
        && rd2.isDummy()
        && rd.getScheduleId().getValue() == rd2.getParentId().getValue()
        && ScheduleUtils.equalsToDate(rd.getStartDate().getValue(), rd2
          .getStartDate()
          .getValue(), false)) {
        // [繰り返しスケジュール] 親の ID を検索
        canAdd = false;
        break;
      }
      if (rd2.isRepeat()
        && rd.isDummy()
        && rd2.getScheduleId().getValue() == rd.getParentId().getValue()
        && ScheduleUtils.equalsToDate(rd.getStartDate().getValue(), rd2
          .getStartDate()
          .getValue(), false)) {
        // [繰り返しスケジュール] 親の ID を検索
        scheduleList.remove(rd2);
        canAdd = true;
        repeat_del = true;
      }

      if (!repeat_del) {
        // 繰り返しスケジュールの変更／削除が無い場合

        if (!rd.isDummy()
          && !rd2.isDummy()
          && !rd.getPattern().equals("S")
          && !rd2.getPattern().equals("S")) {
          // 終日スケジュール・期間スケジュールではなく、
          // ダミースケジュールでもないときに
          // 重複スケジュールを検出する。
          // 時間が重なっている場合重複スケジュールとする。
          if ((rd.getStartDate().getValue().before(
            rd2.getStartDate().getValue()) && rd2
            .getStartDate()
            .getValue()
            .before(rd.getEndDate().getValue()))
            || (rd2.getStartDate().getValue().before(
              rd.getStartDate().getValue()) && rd
              .getStartDate()
              .getValue()
              .before(rd2.getEndDate().getValue()))
            || (rd
              .getStartDate()
              .getValue()
              .before(rd2.getEndDate().getValue()) && rd2
              .getEndDate()
              .getValue()
              .before(rd.getEndDate().getValue()))
            || (rd2
              .getStartDate()
              .getValue()
              .before(rd.getEndDate().getValue()) && rd
              .getEndDate()
              .getValue()
              .before(rd2.getEndDate().getValue()))
            || (rd.getEndDate().getValue().equals(rd2.getEndDate().getValue()) && rd
              .getStartDate()
              .getValue()
              .equals(rd2.getStartDate().getValue()))) {
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

  public List<ScheduleResultData> getScheduleList() {
    if (!isSort) {
      // ソート
      Collections.sort(scheduleList, new Comparator<ScheduleResultData>() {

        @Override
        public int compare(ScheduleResultData a, ScheduleResultData b) {
          Calendar cal = Calendar.getInstance();
          Calendar cal2 = Calendar.getInstance();

          cal.setTime(a.getStartDate().getValue());
          cal2.setTime(b.getStartDate().getValue());
          if ((cal.getTime()).compareTo(cal2.getTime()) != 0) {
            return (cal.getTime()).compareTo(cal2.getTime());
          } else {
            if (!a.getPattern().equals("S") && !b.getPattern().equals("S")) {
              cal.setTime(a.getEndDate().getValue());
              cal2.setTime(b.getEndDate().getValue());

              return (cal.getTime()).compareTo(cal2.getTime());
            } else if (a.getPattern().equals("S") && b.getPattern().equals("S")) {
              if (a.isTerm()) {
                return -1;
              } else if (b.isTerm()) {
                return 1;
              } else {
                return 0;
              }
            } else {
              if (a.getPattern().equals("S")) {
                return -1;
              } else {
                return 1;
              }
            }
          }
        }
      });
      isSort = true;
    }
    List<ScheduleResultData> results = new ArrayList<ScheduleResultData>();
    for (ScheduleResultData rd : scheduleList) {
      if (!rd.isDummy()) {
        results.add(rd);
      }
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    String date = "";
    for (ScheduleResultData rd : scheduleList) {
      if (!rd.isDummy()
        && !date.equals(sdf.format(rd.getStartDate().getValue()))) {
        date = sdf.format(rd.getStartDate().getValue());
        rd.setDayStart(true);
      } else {
        rd.setDayStart(false);
      }
    }
    return results;
  }
}

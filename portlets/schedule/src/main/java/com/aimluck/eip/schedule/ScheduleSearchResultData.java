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

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALEipHolidaysManager;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * スケジュールの検索データを管理するクラスです。
 *
 */
public class ScheduleSearchResultData extends ScheduleResultData {

  private ALEipUser createUser;

  private ALDateTimeField DateDay;

  private ALStringField note;

  @Override
  public void initField() {
    super.initField();
    note = new ALStringField();
  }

  public String getDateFull() {
    SimpleDateFormat sdf =
      new SimpleDateFormat(ALLocalizationUtils
        .getl10n("SCHEDULE_SIMPLE_DATE_FORMAT"));
    StringBuilder b = new StringBuilder();
    if ("S".equals(getPattern())) {
      b.append(sdf.format(getStartDate().getValue()));
    } else {
      b.append(sdf.format(getStartDate().getValue())).append(" ").append(
        getDate2());
    }

    return b.toString();
  }

  public String getDateFullOnlyDate() {
    SimpleDateFormat sdf =
      new SimpleDateFormat(ALLocalizationUtils
        .getl10n("SCHEDULE_SIMPLE_DATE_FORMAT"));
    StringBuilder b = new StringBuilder();
    b.append(sdf.format(getStartDate().getValue()));
    return b.toString();
  }

  public String getDateShort() {
    SimpleDateFormat sdf =
      new SimpleDateFormat(ALLocalizationUtils
        .getl10n("SCHEDULE_SIMPLE_DATE_FORMAT_SHORT"));
    StringBuilder b = new StringBuilder();
    b.append(sdf.format(getStartDate().getValue()));
    return b.toString();
  }

  public String getPatternTime() {
    StringBuilder b = new StringBuilder();
    if ("S".equals(getPattern())) {
      if (isTerm()) {
        b.append(ALLocalizationUtils.getl10n("SCHEDULE_TERM"));
      } else {
        b.append(ALLocalizationUtils.getl10n("SCHEDULE_ALL_DAY"));
      }
    } else {
      b.append(getDate());
    }
    return b.toString();
  }

  public ALDateTimeField getDateDay() {
    DateDay = new ALDateTimeField("yyyy-MM-dd");
    DateDay.setValue(getViewDate());
    return DateDay;

  }

  /**
   * 時刻まではチェックしない。
   *
   * @return 年・月・日が等しいならtrue
   */
  public boolean isStartDayEqualsEndDay() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    String startDate = sdf.format(getStartDate().getValue());
    String endDate = sdf.format(getEndDate().getValue());
    return startDate.equals(endDate);
  }

  public boolean isStartDayEqualsToday() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    String startDate = sdf.format(getStartDate().getValue());
    String toDay = sdf.format(new java.util.Date());
    return startDate.equals(toDay);
  }

  public boolean isStartDateHoliday() {
    return ALEipHolidaysManager.getInstance().isHoliday(
      getStartDate().getValue()) != null;
  }

  public String getDateDetail() {
    SimpleDateFormat sdf =
      new SimpleDateFormat(ALLocalizationUtils
        .getl10n("SCHEDULE_SIMPLE_DATE_FORMAT"));
    StringBuilder b = new StringBuilder();
    if ("S".equals(getPattern())) {
      b.append(sdf.format(getStartDate().getValue()));
      if (!isStartDayEqualsEndDay()) {
        b.append("&nbsp;");
        b.append("-");
        b.append("&nbsp;");
        b.append(sdf.format(getEndDate().getValue()));
      }
    } else {
      b.append(sdf.format(getStartDate().getValue())).append(" ").append(
        getDate());
    }
    return b.toString();
  }

  public String getViewDate() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    sdf.format(getStartDate().getValue());
    return sdf.format(getStartDate().getValue());
  }

  /**
   * @return createUser
   */
  public ALEipUser getCreateUser() {
    return createUser;
  }

  /**
   * @param createUser
   *          セットする createUser
   */
  public void setCreateUser(ALEipUser createUser) {
    this.createUser = createUser;
  }

  /**
   * @return note
   */
  public ALStringField getNote() {
    return note;
  }

  /**
   * @param note
   *          セットする note
   */
  public void setNote(String note) {
    this.note.setValue(note);
  }

  public ALStringField getNoteShort() {
    ALStringField field = new ALStringField();

    String str = note.getValue().replaceAll("\r", "").replaceAll("\n", " ");
    field.setValue(ALCommonUtils.compressString(str, 30));
    return field;
  }
}

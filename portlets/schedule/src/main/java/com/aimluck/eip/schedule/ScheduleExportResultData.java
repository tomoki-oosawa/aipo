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
import java.util.List;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALEipHolidaysManager;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.facilities.FacilityResultData;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * スケジュールのCSVエクスポートデータを管理するクラスです。
 *
 * ScheduleDetailResultDataのデータにScheduleSearchResultDataのデータおよび参加者、設備のリストを追加
 *
 */
public class ScheduleExportResultData extends ScheduleDetailResultData {

  /** <code>members</code> 共有メンバー */
  private final List<ALEipUser> members = new ArrayList<ALEipUser>();

  /** <code>facilities</code> 共有設備 */
  private final List<FacilityResultData> facilities =
    new ArrayList<FacilityResultData>();

  /** 区切り文字 */
  public static final String DISP_SEPARATOR = "、";

  @Override
  public void initField() {
    super.initField();
  }

  /**
   * @return members
   */
  public List<ALEipUser> getMembers() {
    return members;
  }

  public String getMemberNameExport() {
    StringBuilder b = new StringBuilder();
    int i = 0;
    for (ALEipUser user : members) {
      b.append(user.getAliasName().getValue());
      i++;
      if (i < members.size()) {
        b.append(DISP_SEPARATOR);
      }
    }
    return b.toString();
  }

  public void addMember(ALEipUser user) {
    members.add(0, user);
  }

  public void addAllMember(List<ALEipUser> users) {
    members.addAll(users);
  }

  /**
   * @return facilities
   */
  public List<FacilityResultData> getFacilities() {
    return facilities;
  }

  public String getFacilityNameExport() {
    StringBuilder b = new StringBuilder();
    int i = 0;
    for (FacilityResultData rd : facilities) {
      b.append(rd.getFacilityName().getValue());
      i++;
      if (i < facilities.size()) {
        b.append(DISP_SEPARATOR);
      }
    }
    return b.toString();
  }

  public void addFacility(FacilityResultData facility) {
    facilities.add(0, facility);
  }

  public void addAllFacility(List<FacilityResultData> rds) {
    facilities.addAll(rds);
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

  public String getEndDateExport() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    sdf.format(getEndDate().getValue());
    return sdf.format(getEndDate().getValue());
  }

  public ALStringField getNoteExport() {
    ALStringField field = new ALStringField();

    String str = getNoteStr().replaceAll("\r", "").replaceAll("\n", " ");
    field.setValue(str);
    return field;
  }
}

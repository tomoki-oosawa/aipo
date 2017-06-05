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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.aimluck.commons.field.ALNumberField;
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

  /** <code>user_id</code> ユーザー・設備 ID */
  private ALNumberField user_id;

  /** <code>members</code> 共有メンバー */
  private List<ALEipUser> members;

  /** <code>facilities</code> 共有設備 */
  private List<FacilityResultData> facilities;

  /** 区切り文字 */
  public static final String DISP_SEPARATOR = ",";

  @Override
  public void initField() {
    super.initField();
    user_id = new ALNumberField();
    members = new ArrayList<ALEipUser>();
    facilities = new ArrayList<FacilityResultData>();
  }

  public void addAllMember(List<ALEipUser> users) {
    // ソートした上で挿入する
    Collections.sort(users, new Comparator<ALEipUser>() {
      @Override
      public int compare(ALEipUser users1, ALEipUser users2) {
        return users1.getUserId().getValueWithInt()
          - users2.getUserId().getValueWithInt();
      }
    });
    members.addAll(users);
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
      // ダブルクオーテーションを削除
      String str = user.getAliasName().getValue().replaceAll("\"", "");
      b.append(str);
      i++;
      if (i < members.size()) {
        b.append(DISP_SEPARATOR);
      }
    }
    return b.toString();
  }

  public void addAllFacility(List<FacilityResultData> rds) {
    Collections.sort(rds, new Comparator<FacilityResultData>() {
      @Override
      public int compare(FacilityResultData rds1, FacilityResultData rds2) {
        return rds1.getFacilityId().getValueWithInt()
          - rds2.getFacilityId().getValueWithInt();
      }
    });
    facilities.addAll(rds);
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
      // ダブルクオーテーションを削除
      String str = rd.getFacilityName().getValue().replaceAll("\"", "");
      b.append(str);
      i++;
      if (i < facilities.size()) {
        b.append(DISP_SEPARATOR);
      }
    }
    return b.toString();
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
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    sdf.format(getStartDate().getValue());
    return sdf.format(getStartDate().getValue());
  }

  public String getEndDateExport() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    sdf.format(getEndDate().getValue());
    return sdf.format(getEndDate().getValue());
  }

  public ALStringField getNoteExport() {
    ALStringField field = new ALStringField();

    // ダブルクオーテーションを削除
    String str = getNoteStr().replaceAll("\"", "");
    field.setValue(str);
    return field;
  }

  public ALStringField getPlaceExport() {
    ALStringField field = new ALStringField();

    // ダブルクオーテーションを削除
    String str = getPlace().getValue().replaceAll("\"", "");
    field.setValue(str);
    return field;
  }

  public ALStringField getNameExport() {
    ALStringField field = new ALStringField();

    // ダブルクオーテーションを削除
    String str = getName().getValue().replaceAll("\"", "");
    field.setValue(str);
    return field;
  }

  // 公開度
  public ALStringField getPublicExport() {
    ALStringField field = new ALStringField();

    // ダブルクオーテーションを削除
    String str = getDegree().getValue().replaceAll("\"", "");
    field.setValue(str);
    return field;
  }

  // 繰り返し
  public ALStringField getRepeatExport() {
    ALStringField field = new ALStringField();

    // ダブルクオーテーションを削除
    String str = getRepeatText().getValue().replaceAll("\"", "");
    field.setValue(str);
    return field;
  }

  // 重複

  /**
   * @return user_id
   */
  public ALNumberField getUserId() {
    return user_id;
  }

  /**
   * @param user_id
   *          セットする user_id
   */
  public void setUserId(int userId) {
    user_id.setValue(userId);
  }
}

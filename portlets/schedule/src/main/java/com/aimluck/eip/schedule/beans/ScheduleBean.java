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
package com.aimluck.eip.schedule.beans;

import java.util.Date;
import java.util.List;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.schedule.AjaxScheduleResultData;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * スケジュールの検索データを管理するクラスです。
 * 
 */
public class ScheduleBean implements ALData, Cloneable {

  /** <code>schedule_id</code> スケジュールID */
  private ALNumberField schedule_id;

  /** <code>parent_id</code> 親スケジュール ID */
  private ALNumberField parent_id;

  /** <code>parent_id</code> スケジュールオーナー名 */
  private ALStringField owner_name;

  /** <code>parent_id</code> スケジュールオーナーID */
  private ALStringField owner_id;

  /** <code>start_date</code> 開始時間 */
  private ALDateTimeField start_date;

  /** <code>end_date</code> 終了時間 */
  private ALDateTimeField end_date;

  /** <code>name</code> タイトル */
  private ALStringField name;

  /** <code>date</code> 場所 */
  private ALStringField place;

  /** <code>ptn</code> 繰り返しパターン */
  private String ptn;

  /** <code>format</code> フォーマット */
  private String format = "HH:mm";

  /** <code>is_owner</code> オーナーかどうか */
  private boolean is_owner;

  /** <code>is_tmpreserve</code> 仮スケジュールかどうか */
  private boolean is_tmpreserve;

  /** <code>is_duplicate</code> 重複スケジュールかどうか */
  private boolean is_duplicate;

  /** <code>is_public</code> 公開スケジュールかどうか */
  private boolean is_public;

  /** <code>is_hidden</code> 表示するかどうか */
  private boolean is_hidden;

  /** <code>is_loginuser</code> ログインユーザー */
  private boolean is_loginuser;

  /** <code>is_member</code> 共有メンバー */
  private boolean is_member;

  /** <code>is_confirm</code> 確認済みかどうか */
  private boolean is_confirm;

  /** <code>is_repeat</code> 繰り返しかどうか */
  private boolean is_repeat;

  /** <code>is_dummy</code> ダミーかどうか */
  private boolean is_dummy;

  /** <code>is_editable</code> 共有メンバーによる編集／削除フラグ */
  private boolean is_editable;

  /** <code>type</code> 設備かどうか */
  private ALStringField type;

  /** <code>rowspan</code> rowspan */
  private int rowspan;

  /** <code>colspanReal</code> colspanReal */
  private int colspanReal;

  /** <code>index</code> index */
  private int index;

  /** <code>indexReal</code> colspanReal */
  private int indexReal;

  private List<String> tmpList;

  private ALNumberField userCount;

  private ALNumberField facilityCount;

  /*
   *
   */
  @Override
  public void initField() {
    name = new ALStringField();
    type = new ALStringField();
    place = new ALStringField();
    schedule_id = new ALNumberField();
    parent_id = new ALNumberField();
    owner_name = new ALStringField();
    owner_id = new ALStringField();
    start_date = new ALDateTimeField(format);
    end_date = new ALDateTimeField(format);
    userCount = new ALNumberField();
    facilityCount = new ALNumberField();
    is_owner = true;
    is_tmpreserve = false;
    is_duplicate = false;
    is_public = true;
    is_hidden = false;
    is_loginuser = true;
    is_member = true;
    is_confirm = false;
    is_repeat = false;
    is_dummy = false;
    tmpList = null;
  }

  public void setResultData(AjaxScheduleResultData rd) {
    this.name = rd.getName();
    this.schedule_id = rd.getScheduleId();
    this.parent_id = rd.getParentId();
    this.place = rd.getPlace();

    try {
      if (ScheduleUtils.SCHEDULEMAP_TYPE_FACILITY.equals(rd.getType())) {
        this.owner_name.setValue("");
        this.owner_id.setValue("f" + Integer.toString(rd.getUserId()));
      } else {
        ALEipUser user = ALEipUtils.getALEipUser(rd.getUserId());
        if (user == null) {
          this.owner_name.setValue("");
          this.owner_id.setValue("");
        } else {
          this.owner_name.setValue(user.getName().toString());
          this.owner_id.setValue(user.getUserId().toString());
        }
      }
    } catch (Exception e) {
      //
    }
    this.start_date = rd.getStartDate();
    this.end_date = rd.getEndDate();
    this.is_owner = rd.isOwner();
    this.is_tmpreserve = rd.isTmpreserve();
    this.is_public = rd.isPublic();
    this.is_duplicate = rd.isDuplicate();
    this.is_hidden = rd.isHidden();
    this.is_loginuser = rd.isLoginuser();
    this.is_member = rd.isMember();
    this.is_confirm = rd.isConfirm();
    this.is_repeat = rd.isRepeat();
    this.is_dummy = rd.isDummy();
    this.ptn = rd.getPattern();
    // this.format = rd.getFormat();
    this.is_editable = rd.isEditable();
    this.rowspan = rd.getRowspan();
    this.type.setValue(rd.getType());
    this.tmpList = rd.getMemberList();
    this.userCount.setValue(rd.getUserCount());
    this.facilityCount.setValue(rd.getFacilityCount());
  }

  public List<String> getMemberList() {
    return tmpList;
  }

  public void setMemberList(List<String> list) {
    tmpList = list;
  }

  public String getStartDateHour() {
    return start_date.toStringHour();
  }

  public String getStartDateMinute() {
    return start_date.toStringMinute();
  }

  public String getEndDateHour() {
    return end_date.toStringHour();
  }

  public String getEndDateMinute() {
    return end_date.toStringMinute();
  }

  /**
   * 終了時間を取得します。
   * 
   * @return
   */
  public String getEndDate() {
    return end_date.toString();
  }

  /**
   * 開始時間を取得します。
   * 
   * @return
   */
  public String getStartDate() {
    return start_date.toString();
  }

  /**
   * 終了時間を設定します。
   * 
   * @param date
   */
  public void setEndDate(Date date) {
    end_date.setValue(date);
  }

  /**
   * 開始時間を設定します。
   * 
   * @param date
   */
  public void setStartDate(Date date) {
    start_date.setValue(date);
  }

  /**
   * タイトルを取得します。
   * 
   * @return
   */
  public String getName() {
    return name.toString();
  }

  /**
   * 場所を取得します。
   * 
   * @return
   */
  public String getPlace() {
    return place.toString();
  }

  /**
   * 詳細を表示するかどうか
   * 
   * @return
   */
  public boolean isShowDetail() {
    return (is_loginuser || is_public || is_owner || is_member);
  }

  /**
   * タイトルを設定します。
   * 
   * @param string
   */
  public void setName(String string) {
    name.setValue(string);
  }

  /**
   * フォーマットを設定します。
   * 
   * @param string
   */
  public void setFormat(String string) {
    format = string;
  }

  /**
   * スケジュールIDを取得します。
   * 
   * @return
   */
  public String getScheduleId() {
    return schedule_id.toString();
  }

  /**
   * スケジュールIDを設定します。
   * 
   * @param number
   */
  public void setScheduleId(int number) {
    schedule_id.setValue(number);
  }

  /**
   * 親スケジュール ID を取得します。
   * 
   * @return
   */
  public String getParentId() {
    return parent_id.toString();
  }

  /**
   * 親スケジュール ID を設定します。
   * 
   * @param number
   */
  public void setParentId(int number) {
    parent_id.setValue(number);
  }

  /**
   * オーナーかどうかを設定します。
   * 
   * @param bool
   */
  public void setOwner(boolean bool) {
    is_owner = bool;
  }

  /**
   * オーナーかどうか
   * 
   * @return
   */
  public boolean isOwner() {
    return is_owner;
  }

  /**
   * 仮スケジュールかどうか
   * 
   * @return
   */
  public boolean isTmpreserve() {
    return is_tmpreserve;
  }

  /**
   * 公開するかどうか
   * 
   * @return
   */
  public boolean isPublic() {
    return is_public;
  }

  /**
   * 仮スケジュールかどうかを設定します。
   * 
   * @param bool
   */
  public void setTmpreserve(boolean bool) {
    is_tmpreserve = bool;
  }

  /**
   * 確認済みスケジュールかどうか
   * 
   * @return
   */
  public boolean isConfirm() {
    return is_confirm;
  }

  /**
   * 確認済みスケジュールかどうかを設定します。
   * 
   * @param bool
   */
  public void setConfirm(boolean bool) {
    is_confirm = bool;
  }

  /**
   * 重複スケジュールかどうか
   * 
   * @return
   */
  public boolean isDuplicate() {
    return is_duplicate;
  }

  /**
   * 重複スケジュールかどうかを設定島します。
   * 
   * @param bool
   */
  public void setDuplicate(boolean bool) {
    is_duplicate = bool;
  }

  /**
   * 公開するかどうかを設定します。
   * 
   * @param bool
   */
  public void setPublic(boolean bool) {
    is_public = bool;
  }

  /**
   * 表示するかどうか
   * 
   * @return
   */
  public boolean isHidden() {
    return is_hidden;
  }

  /**
   * 表示するかどうかを設定します。
   * 
   * @param bool
   */
  public void setHidden(boolean bool) {
    is_hidden = bool;
  }

  /**
   * 非公開かどうか
   * 
   */
  public boolean isPrivate() {
    return !is_public && !is_hidden;
  }

  /**
   * ログインユーザーかどうか
   * 
   * @return
   */
  public boolean isLoginuser() {
    return is_loginuser;
  }

  /**
   * ログインユーザーかどうかを設定します。
   * 
   * @param b
   */
  public void setLoginuser(boolean b) {
    is_loginuser = b;
  }

  /**
   * 共有メンバーかどうか
   * 
   * @return
   */
  public boolean isMember() {
    return is_member;
  }

  /**
   * 共有メンバーかどうかを設定します。
   * 
   * @param b
   */
  public void setMember(boolean b) {
    is_member = b;
  }

  /**
   * 繰り返しかどうか
   * 
   * @return
   */
  public boolean isRepeat() {
    return is_repeat;
  }

  /**
   * 繰り返しかどうかを設定します。
   * 
   * @param bool
   */
  public void setRepeat(boolean bool) {
    is_repeat = bool;
  }

  /**
   * ダミーかどうか
   * 
   * @return
   */
  public boolean isDummy() {
    return is_dummy;
  }

  /**
   * ダミーかどうかを設定します。
   * 
   * @param bool
   */
  public void setDummy(boolean bool) {
    is_dummy = bool;
  }

  /**
   * 
   * @return
   */
  @Override
  public Object clone() {
    try {
      return (super.clone());
    } catch (CloneNotSupportedException e) {
      throw (new InternalError(e.getMessage()));
    }
  }

  /**
   * rowspanを設定します。
   * 
   * @param number
   */
  public void setRowspan(int number) {
    rowspan = number;
  }

  /**
   * rowspanを取得します。
   * 
   * @return
   */
  public int getRowspan() {
    return rowspan;
  }

  /**
   * 繰り返しパターンを取得します。
   * 
   * @return
   */
  public String getPattern() {
    return ptn;
  }

  /**
   * 繰り返しパターンを設定します。
   * 
   * @param string
   */
  public void setPattern(String string) {
    ptn = string;
  }

  /**
   * 共有メンバーによる編集／削除フラグを取得する．
   * 
   * @return
   */
  public boolean isEditable() {
    return is_editable;
  }

  /**
   * 共有メンバーによる編集／削除フラグを設定する．
   * 
   * @param string
   */
  public void setEditFlag(boolean bool) {
    is_editable = bool;
  }

  /**
   * 日付を取得します。
   * 
   * @return
   */
  public String getDate() {
    if (start_date.getValue().equals(end_date.getValue())) {
      return start_date.toString();
    } else if ((start_date.getYear().equals(end_date.getYear())
      && start_date.getMonth().equals(end_date.getMonth()) && start_date
      .getDay()
      .equals(end_date.getDay()))
      || is_repeat) {
      return new StringBuffer()
        .append(start_date.toString())
        .append("&nbsp;")
        .append('-')
        .append("&nbsp;")
        .append(end_date.toString())
        .toString();
    } else {
      return new StringBuffer()
        .append(start_date.toString())
        .append("&nbsp;")
        .append('-')
        .append("&nbsp;")
        .append("24:00")
        .toString();
    }
  }

  /**
   * 日付を取得します。
   * 
   * @return
   */
  public String getDate2() {
    if (start_date.getValue().equals(end_date.getValue())) {
      return start_date.toString();
    } else if ((start_date.getYear().equals(end_date.getYear())
      && start_date.getMonth().equals(end_date.getMonth()) && start_date
      .getDay()
      .equals(end_date.getDay()))
      || is_repeat) {
      return new StringBuffer()
        .append(start_date.toString())
        .append('-')
        .append(end_date.toString())
        .toString();
    } else {
      return new StringBuffer()
        .append(start_date.toString())
        .append('-')
        .append("24:00")
        .toString();
    }
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public int getColspanReal() {
    return colspanReal;
  }

  public void setColspanReal(int colspanReal) {
    this.colspanReal = colspanReal;
  }

  public void setIndexReal(int index) {
    this.indexReal = index;
  }

  public int getIndexReal() {
    return this.indexReal;
  }

  public void setOwnerId(String str) {
    this.owner_id.setValue(str);
  }

  public String getOwnerId() {
    return this.owner_id.toString();
  }

  public void setOwnerName(String str) {
    this.owner_name.setValue(str);
  }

  public String getOwnerName() {
    return this.owner_name.toString();
  }

  public void setType(String str) {
    this.type.setValue(str);
  }

  public String getType() {
    return this.type.toString();
  }

  /**
   * @return userCount
   */
  public int getUserCount() {
    return (int) userCount.getValue();
  }

  /**
   * @param userCount
   *          セットする userCount
   */
  public void setUserCount(int userCount) {
    this.userCount.setValue(userCount);
  }

  /**
   * @return facilityCount
   */
  public int getFacilityCount() {
    return (int) facilityCount.getValue();
  }

  /**
   * @param facilityCount
   *          セットする facilityCount
   */
  public void setFacilityCount(int facilityCount) {
    this.facilityCount.setValue(facilityCount);
  }

}

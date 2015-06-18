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
package com.aimluck.eip.manhour;

import java.util.Calendar;
import java.util.Date;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * プロジェクト管理のResultDataです。 <br />
 */
public class ManHourResultData implements ALData {

  /** category * */

  /** category_id */
  private ALNumberField category_id;

  /** ユーザー名 */
  private ALStringField user;

  private ALNumberField user_id;

  /** カテゴリ名 */
  private ALStringField category_name;

  /** スケジュール * */

  /** <code>schedule_id</code> スケジュールID */
  private ALNumberField schedule_id;

  /** <code>parent_id</code> 親スケジュール ID */
  private ALNumberField parent_id;

  /** <code>start_date</code> 開始時間 */
  private ALDateTimeField start_date;

  /** <code>end_date</code> 終了時間 */
  private ALDateTimeField end_date;

  /** <code>name</code> 予定 */
  private ALStringField name;

  /** <code>date</code> 日付 */
  // private ALStringField date;

  /** <code>ptn</code> 繰り返しパターン */
  private String ptn;

  /** <code>format</code> フォーマット */
  private String format1 = "yyyy年M月d日 H:mm";

  private String format2 = "H:mm";

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

  /** <code>rowspan</code> rowspan */
  private int rowspan;

  /**
   *
   *
   */
  @Override
  public void initField() {
    user = new ALStringField();
    // カテゴリ
    category_id = new ALNumberField();
    user_id = new ALNumberField();
    category_name = new ALStringField();
    // スケジュール
    name = new ALStringField();
    schedule_id = new ALNumberField();
    parent_id = new ALNumberField();
    start_date = new ALDateTimeField(format1);
    end_date = new ALDateTimeField(format2);
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
  }

  /**
   * @return
   */
  public ALStringField getCategoryName() {
    return category_name;
  }

  /**
   * 終了時間を取得します。
   * 
   * @return
   */
  public ALDateTimeField getEndDate() {
    return end_date;
  }

  /**
   * 開始時間を取得します。
   * 
   * @return
   */
  public ALDateTimeField getStartDate() {
    return start_date;
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
   * 予定を取得します。
   * 
   * @return
   */
  public ALStringField getName() {
    return name;
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
   * 予定を設定します。
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
    format1 = string;
    format2 = string;
  }

  /**
   * スケジュールIDを取得します。
   * 
   * @return
   */
  public ALNumberField getScheduleId() {
    return schedule_id;
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
  public ALNumberField getParentId() {
    return parent_id;
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

  public int getManHourMin() {
    int start_hour = Integer.parseInt(start_date.getHour());
    int end_hour = Integer.parseInt(end_date.getHour());
    int start_min = Integer.parseInt(start_date.getMinute());
    int end_min = Integer.parseInt(end_date.getMinute());
    return end_hour * 60 + end_min - start_hour * 60 - start_min;
  }

  public double getManHour() {
    return (getManHourMin() * 100 / 60.0) / 100.0;
  }

  public ALNumberField getCategoryId() {
    return category_id;
  }

  public void setCategoryId(long number) {
    category_id.setValue(number);
  }

  public ALStringField getUser() {
    return user;
  }

  public void setUser(String string) {
    user.setValue(string);
  }

  public void setCategoryName(String string) {
    category_name.setValue(string);
  }

  public boolean isPast() {
    Calendar cal = Calendar.getInstance();
    return cal.getTime().after(end_date.getValue());
  }

  public ALNumberField getUserId() {
    return user_id;
  }

  public void setUserId(long number) {
    user_id.setValue(number);
  }
}

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.fileupload.beans.FileuploadBean;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * スケジュールの検索データを管理するクラスです。
 *
 */
public class ScheduleResultData implements ALData, Cloneable {

  /** <code>schedule_id</code> スケジュールID */
  private ALNumberField schedule_id;

  /** <code>parent_id</code> 親スケジュール ID */
  private ALNumberField parent_id;

  /** <code>type</code> 設備かどうか */
  private ALStringField type;

  /** 添付ファイルリスト */
  private List<FileuploadBean> attachmentFileList = null;

  /** <code>start_date</code> 開始時間 */
  private ALDateTimeField start_date;

  private int start_date_time;

  /** <code>end_date</code> 終了時間 */
  private ALDateTimeField end_date;

  private int end_date_time;

  /** <code>name</code> タイトル */
  private ALStringField name;

  /** 内容 */
  private ALStringField description;

  /** 場所 */
  private ALStringField place;

  /** <code>ptn</code> 繰り返しパターン */
  private String ptn;

  /** <code>format</code> フォーマット */
  private String format = "H:mm";

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

  /** <code>is_daystart</code> その日の最初のスケージュルかどうか */
  private boolean is_daystart;

  /** <code>is_term</code> 期間スケジュールかどうか */
  private boolean is_term;

  /** <code>rowspan</code> rowspan */
  private int rowspan;

  /** <code>common_category_name</code> 共有カテゴリ名 */
  private ALStringField common_category_name;

  /** <code>is_last_started</code> 最終開始済かどうか */
  private boolean is_last_started;

  /*
   *
   */
  @Override
  public void initField() {
    name = new ALStringField();
    type = new ALStringField();
    place = new ALStringField();
    description = new ALStringField();
    schedule_id = new ALNumberField();
    parent_id = new ALNumberField();
    start_date = new ALDateTimeField(format);
    end_date = new ALDateTimeField(format);
    common_category_name = new ALStringField();
    attachmentFileList = new ArrayList<FileuploadBean>();
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
    is_daystart = false;
    is_term = false;
    is_last_started = false;
  }

  /**
   * 終了時間を取得します。
   *
   * @return
   */
  public ALDateTimeField getEndDate() {
    return end_date;
  }

  public int getEndDateTime() {
    return end_date_time;
  }

  /**
   * 開始時間を取得します。
   *
   * @return
   */
  public ALDateTimeField getStartDate() {
    return start_date;
  }

  public int getStartDateTime() {
    return start_date_time;
  }

  /**
   * 終了時間を設定します。
   *
   * @param date
   */
  public void setEndDate(Date date) {
    int hh, mm;
    end_date.setValue(date);
    hh = Integer.parseInt(end_date.getHour());
    mm = Integer.parseInt(end_date.getMinute());
    end_date_time = hh * 60 + mm;
  }

  /**
   * 開始時間を設定します。
   *
   * @param date
   */
  public void setStartDate(Date date) {
    int hh, mm;
    start_date.setValue(date);
    hh = Integer.parseInt(start_date.getHour());
    mm = Integer.parseInt(start_date.getMinute());
    start_date_time = hh * 60 + mm;
  }

  /**
   * タイトルを取得します。
   *
   * @return
   */
  public ALStringField getName() {
    return name;
  }

  public String getWbrName() {
    return ALCommonUtils.replaceToAutoCR(name.toString());
  }

  /**
   * 内容を取得します。
   *
   * @return
   */
  public ALStringField getDescription() {
    return description;
  }

  /**
   * 場所を取得します。
   *
   * @return
   */
  public ALStringField getPlace() {
    return place;
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
   * 内容を設定します。
   *
   * @param string
   */
  public void setDescription(String string) {
    description.setValue(string);
  }

  /**
   * 場所を設定します。
   *
   * @param string
   */
  public void setPlace(String string) {
    place.setValue(string);
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
   * ファイルリストを取得します。
   *
   * @return
   */
  public List<FileuploadBean> getAttachmentFileList() {
    return attachmentFileList;
  }

  /**
   * ファイルリストを設定します。
   *
   * @param list
   */
  public void setAttachmentFiles(List<FileuploadBean> list) {
    attachmentFileList = list;
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
   * 重複スケジュールかどうかを設定します。
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
    return !is_public && is_hidden;
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
        .append(" ")
        .append('-')
        .append(" ")
        .append(end_date.toString())
        .toString();
    } else {
      return new StringBuffer()
        .append(start_date.toString())
        .append(" ")
        .append('-')
        .append(" ")
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
    if (start_date.getHour().equals(end_date.getHour())
      && start_date.getMinute().equals(end_date.getMinute())) {
      return start_date.toString();
    } else if ((start_date.getYear().equals(end_date.getYear())
      && start_date.getMonth().equals(end_date.getMonth()) && start_date
      .getDay()
      .equals(end_date.getDay()))
      || is_repeat) {
      return new StringBuffer()
        .append(start_date.toString())
        .append(" - ")
        .append(end_date.toString())
        .toString();
    } else {
      return new StringBuffer()
        .append(start_date.toString())
        .append(" - ")
        .append("24:00")
        .toString();
    }
  }

  /**
   *
   *
   * @return
   */
  public ALStringField getCommonCategoryName() {
    return common_category_name;
  }

  /**
   *
   * @param string
   */
  public void setCommonCategoryName(String string) {
    common_category_name.setValue(string);
  }

  public void setType(String str) {
    this.type.setValue(str);
  }

  public String getType() {
    return this.type.toString();
  }

  public String getSpanDateText() {
    return ALLocalizationUtils.getl10nFormat("SCHEDULE_UNTIL_SPAN", start_date
      .getYear(), start_date.getMonth(), start_date.getDay(), start_date
      .getDayOfWeek(), end_date.getYear(), end_date.getMonth(), end_date
      .getDay(), end_date.getDayOfWeek());
  }

  public void setDayStart(boolean b) {
    is_daystart = b;
  }

  public boolean isDayStart() {
    return is_daystart;
  }

  public void setTerm(boolean b) {
    is_term = b;
  }

  public boolean isTerm() {
    return is_term;
  }

  /**
   * スケジュールが開始されたか
   *
   * ・繰り返しで期間指定がある場合には最後のスケジュールが開始されたか判定する<br />
   *
   * @return
   */
  public boolean isLastStarted() {
    return is_last_started;
  }

  /**
   *
   * @param bool
   */
  public void setLastStarted(boolean bool) {
    is_last_started = bool;
  }

}

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
package com.aimluck.eip.todo;

import java.util.Date;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ToDoのResultDataです。 <BR>
 *
 */
public class ToDoResultData implements ALData {

  /** Todo ID */
  private ALNumberField todo_id;

  /** カテゴリID */
  private ALNumberField category_id;

  /** 状態 */
  private ALNumberField state;

  /** 優先度 */
  private ALNumberField priority;

  /** 担当者ID */
  private ALNumberField user_id;

  /** 担当者 */
  private ALStringField user_name;

  /** 作成者(更新者)ID */
  private ALNumberField create_user_id;

  /** 作成者 */
  private ALStringField create_user_name;

  /** タイトル */
  private ALStringField todo_name;

  /** カテゴリ名 */
  private ALStringField category_name;

  /** 開始日 */
  private ALStringField start_date;

  /** 締切日 */
  private ALStringField end_date;

  /** メモ */
  private ALStringField note;

  /** 公開/非公開フラグ */
  private boolean is_public;

  /** スケジュール表示フラグ */
  private boolean addon_schedule_flg;

  /** 優先度画像名 */
  private ALStringField priority_image;

  /** 優先度（文字列） */
  private ALStringField priority_string;

  /** 状態画像名 */
  private ALStringField state_image;

  /** 状態（文字列） */
  private ALStringField state_string;

  /** 作成日 */
  private ALStringField create_date;

  /** 更新日 */
  private ALDateTimeField update_date;

  /** ログインユーザーID */
  private ALNumberField login_user_id;

  /**
   * 期限状態（期限前/期限当日/期限後）． <br>
   * クラス ToDoUtils の変数 LIMIT_STATE_BEFORE，LIMIT_STATE_TODAY，LIMIT_STATE_AFTER
   * を参照．
   */
  private ALNumberField limit_state;

  private boolean is_self_todo;

  private boolean hasAclListTodoOther;

  private boolean hasAclDetailTodoOther;

  private boolean hasAclEditTodoOther;

  private boolean hasAclDeleteTodoOther;

  /**
   *
   *
   */
  @Override
  public void initField() {
    todo_id = new ALNumberField();
    category_id = new ALNumberField();
    state = new ALNumberField();
    priority = new ALNumberField();
    user_id = new ALNumberField();
    user_name = new ALStringField();
    todo_name = new ALStringField();
    category_name = new ALStringField();
    note = new ALStringField();
    note.setTrim(false);
    start_date = new ALStringField();
    end_date = new ALStringField();
    priority_image = new ALStringField();
    priority_string = new ALStringField();
    state_image = new ALStringField();
    state_string = new ALStringField();
    create_date = new ALStringField();
    update_date = new ALDateTimeField();
    login_user_id = new ALNumberField();
    limit_state = new ALNumberField();
    create_user_id = new ALNumberField();
    create_user_name = new ALStringField();
    is_public = true;
    addon_schedule_flg = true;
    is_self_todo = false;
  }

  /**
   * @return
   */
  public ALNumberField getCategoryId() {
    return category_id;
  }

  /**
   * @return
   */
  public String getCategoryName() {
    return category_name.toString();
  }

  public String getCategoryNameHtml() {
    return ALCommonUtils.replaceToAutoCR(category_name.toString());
  }

  /**
   * @return
   */
  public ALStringField getEndDate() {
    return end_date;
  }

  /**
   * @return
   */
  public ALNumberField getPriority() {
    return priority;
  }

  /**
   * @return
   */
  public ALNumberField getState() {
    return state;
  }

  /**
   * @return
   */
  public ALNumberField getTodoId() {
    return todo_id;
  }

  /**
   * @return
   */
  public String getTodoName() {
    return ALCommonUtils.replaceToAutoCR(todo_name.toString());
  }

  /**
   * @return
   */
  public ALStringField getTodoNameString() {
    return todo_name;
  }

  /**
   * @param i
   */
  public void setCategoryId(long i) {
    category_id.setValue(i);
  }

  /**
   * @param string
   */
  public void setCategoryName(String string) {
    category_name.setValue(string);
  }

  /**
   * @param string
   */
  public void setEndDate(String string) {
    end_date.setValue(string);
  }

  /**
   * @param i
   */
  public void setPriority(int i) {
    priority.setValue(i);
  }

  /**
   * @param i
   */
  public void setState(int i) {
    state.setValue(i);
  }

  /**
   * @param i
   */
  public void setTodoId(long i) {
    todo_id.setValue(i);
  }

  /**
   * @param string
   */
  public void setTodoName(String string) {
    todo_name.setValue(string);
  }

  /**
   * @return
   */
  public String getNote() {
    return ALEipUtils.getMessageList(note.getValue());
  }

  /**
   * @return
   */
  public ALStringField getStartDate() {
    return start_date;
  }

  /**
   * 公開/非公開フラグ．
   *
   * @return
   */
  public boolean isPublic() {
    return is_public;
  }

  /**
   * @param string
   */
  public void setNote(String string) {
    note.setValue(string);
  }

  /**
   * @return
   */
  public void setPublicFlag(boolean bool) {
    is_public = bool;
  }

  /**
   * @param string
   */
  public void setStartDate(String string) {
    start_date.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getPriorityImage() {
    return priority_image;
  }

  /**
   * @return
   */
  public ALStringField getPriorityString() {
    return priority_string;
  }

  /**
   * @param string
   */
  public void setPriorityImage(String string) {
    priority_image.setValue(string);
  }

  /**
   * @param string
   */
  public void setPriorityString(String string) {
    priority_string.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getStateString() {
    return state_string;
  }

  /**
   * @param string
   */
  public void setStateString(String string) {
    state_string.setValue(string);
  }

  /**
   * @param string
   */
  public void setStateImage(String string) {
    state_image.setValue(string);
  }

  /**
   *
   * @return
   */
  public ALStringField getStateImage() {
    return state_image;
  }

  /**
   * @return
   */
  public ALStringField getCreateDate() {
    return create_date;
  }

  /**
   * @return
   */
  public ALDateTimeField getUpdateDate() {
    return ALEipUtils.getFormattedTime(update_date);
  }

  /**
   * @param string
   */
  public void setCreateDate(String string) {
    create_date.setValue(string);
  }

  /**
   * @param string
   */
  public void setUpdateDate(Date date) {
    if (date == null) {
      return;
    }
    this.update_date.setValue(date);
  }

  /**
   *
   * @return
   */
  public ALNumberField getLimitState() {
    return limit_state;
  }

  /**
   *
   * @param value
   */
  public void setLimitState(int value) {
    limit_state.setValue(value);
  }

  public void setAddonScheduleFlg(boolean bool) {
    addon_schedule_flg = bool;
  }

  public boolean addonScheduleFlg() {
    return addon_schedule_flg;
  }

  public void setUserId(long i) {
    user_id.setValue(i);
  }

  public ALNumberField getUserId() {
    return user_id;
  }

  public void setUserName(String user_name) {
    this.user_name.setValue(user_name);
  }

  public ALStringField getUserName() {
    return user_name;
  }

  public String getUserNameHtml() {
    return ALCommonUtils.replaceToAutoCR(user_name.toString());
  }

  public void setCreateUserId(long i) {
    create_user_id.setValue(i);
  }

  public ALNumberField getCreateUserId() {
    return create_user_id;
  }

  public void setCreateUserName(String create_user_name) {
    this.create_user_name.setValue(create_user_name);
  }

  public ALStringField getCreateUserName() {
    return create_user_name;
  }

  public void setLoginUserId(int i) {
    login_user_id.setValue(i);
  }

  public ALNumberField getLoginUserId() {
    return login_user_id;
  }

  public boolean isSelfTodo() {
    return is_self_todo;
  }

  public void setIsSelfTodo(boolean is_self_todo) {
    this.is_self_todo = is_self_todo;
  }

  /**
   * hasAclListTodoOtherを取得します。
   *
   * @return hasAclListTodoOther
   */
  public boolean hasAclListTodoOther() {
    return hasAclListTodoOther;
  }

  /**
   * hasAclListTodoOtherを設定します。
   *
   * @param hasAclListTodoOther
   *          hasAclListTodoOther
   */
  public void setAclListTodoOther(boolean hasAclListTodoOther) {
    this.hasAclListTodoOther = hasAclListTodoOther;
  }

  /**
   * hasAclDetailTodoOtherを取得します。
   *
   * @return hasAclDetailTodoOther
   */
  public boolean hasAclDetailTodoOther() {
    return hasAclDetailTodoOther;
  }

  /**
   * hasAclDetailTodoOtherを設定します。
   *
   * @param hasAclDetailTodoOther
   *          hasAclDetailTodoOther
   */
  public void setAclDetailTodoOther(boolean hasAclDetailTodoOther) {
    this.hasAclDetailTodoOther = hasAclDetailTodoOther;
  }

  /**
   * hasAclEditTodoOtherを取得します。
   *
   * @return hasAclEditTodoOther
   */
  public boolean hasAclEditTodoOther() {
    return hasAclEditTodoOther;
  }

  /**
   * hasAclEditTodoOtherを設定します。
   *
   * @param hasAclEditTodoOther
   *          hasAclEditTodoOther
   */
  public void setAclEditTodoOther(boolean hasAclEditTodoOther) {
    this.hasAclEditTodoOther = hasAclEditTodoOther;
  }

  /**
   * hasAclDeleteTodoOtherを取得します。
   *
   * @return hasAclDeleteTodoOther
   */
  public boolean hasAclDeleteTodoOther() {
    return hasAclDeleteTodoOther;
  }

  /**
   * hasAclDeleteTodoOtherを設定します。
   *
   * @param hasAclDeleteTodoOther
   *          hasAclDeleteTodoOther
   */
  public void setAclDeleteTodoOther(boolean hasAclDeleteTodoOther) {
    this.hasAclDeleteTodoOther = hasAclDeleteTodoOther;
  }
}

/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.todo;

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

  /** Todo名 */
  private ALStringField todo_name;

  /** カテゴリ名 */
  private ALStringField category_name;

  /** 開始日 */
  private ALStringField start_date;

  /** 締め切り日 */
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

  /** 登録日 */
  private ALStringField create_date;

  /** 更新日 */
  private ALStringField update_date;

  /**
   * 期限状態（期限前/期限当日/期限後）． <br>
   * クラス ToDoUtils の変数 LIMIT_STATE_BEFORE，LIMIT_STATE_TODAY，LIMIT_STATE_AFTER
   * を参照．
   */
  private ALNumberField limit_state;

  /**
   * 
   * 
   */
  public void initField() {
    todo_id = new ALNumberField();
    category_id = new ALNumberField();
    state = new ALNumberField();
    priority = new ALNumberField();
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
    update_date = new ALStringField();
    limit_state = new ALNumberField();

    is_public = true;
    addon_schedule_flg = true;
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
  public ALStringField getUpdateDate() {
    return update_date;
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
  public void setUpdateDate(String string) {
    update_date.setValue(string);
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
}

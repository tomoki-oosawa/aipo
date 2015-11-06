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

import java.util.Date;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * スケジュールに表示されるTodo の ResultData <BR>
 * 
 */
public class ScheduleToDoResultData implements ALData, Cloneable {

  /**
   * ToDo ID
   */
  private ALNumberField todo_id;

  /**
   * ToDo 名
   */
  private ALStringField todo_name;

  /**
   * 所有者 ID
   */
  private ALNumberField user_id;

  /**
   * <code>start_date</code> 開始時間
   */
  private ALDateTimeField start_date;

  /**
   * <code>end_date</code> 終了時間
   */
  private ALDateTimeField end_date;

  /** 公開/非公開フラグ */
  private boolean is_public;

  /** スケジュール表示フラグ */
  private boolean addon_schedule_flg;

  /**
   * ToDo へのリンク
   */
  private ALStringField todo_url;

  /** <code>rowspan</code> rowspan */
  private int rowspan;

  /** <code>format</code> フォーマット */
  private static final String format = "H:mm";

  /**
   *
   *
   */
  @Override
  public void initField() {
    todo_id = new ALNumberField();
    todo_name = new ALStringField();
    user_id = new ALNumberField();
    start_date = new ALDateTimeField(format);
    end_date = new ALDateTimeField(format);
    todo_url = new ALStringField();

    is_public = true;
    addon_schedule_flg = true;
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
  public ALStringField getTodoName() {
    return todo_name;
  }

  public ALNumberField getUserId() {
    return user_id;
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

  public ALStringField getTodoUrl() {
    return todo_url;
  }

  /**
   * 公開/非公開フラグ．
   * 
   * @return
   */
  public boolean isPublic() {
    return is_public;
  }

  public boolean addonScheduleFlg() {
    return addon_schedule_flg;
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

  public void setUserId(long i) {
    user_id.setValue(i);
  }

  public void setTodoUrl(String string) {
    todo_url.setValue(string);
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
   * rowspanを設定します。
   * 
   * @param number
   * 
   * @uml.property name="rowspan"
   */
  public void setRowspan(int number) {
    rowspan = number;
  }

  /**
   * rowspanを取得します。
   * 
   * @return
   * 
   * @uml.property name="rowspan"
   */
  public int getRowspan() {
    return rowspan;
  }

  /**
   * @return
   */
  public void setPublicFlag(boolean bool) {
    is_public = bool;
  }

  public void setAddonScheduleFlg(boolean bool) {
    addon_schedule_flg = bool;
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
}

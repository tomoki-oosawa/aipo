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
package com.aimluck.eip.eventlog;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * イベントログのResultDataです。 <BR>
 * 
 */
public class EventlogResultData implements ALData {

  /** Eventlog ID */
  private ALNumberField eventlog_id;

  /** 名前 */
  private ALStringField user_full_name;

  /** イベント発生日 */
  private ALStringField event_date;

  /** イベント名 */
  private ALStringField event_name;

  /** イベント発生機能名 */
  private ALStringField portlet_name;

  /** エンティティID */
  private ALNumberField entity_id;

  /** 接続IPアドレス */
  private ALStringField ip_addr;

  /** メモ */
  private ALStringField note;

  /** ログに残したデータ名 */
  private ALStringField data_name;

  /** データ名を表示するかどうか/ */
  private boolean is_data_name;

  /**
   *
   *
   */
  @Override
  public void initField() {
    eventlog_id = new ALNumberField();
    user_full_name = new ALStringField();
    event_date = new ALStringField();
    event_name = new ALStringField();
    portlet_name = new ALStringField();
    entity_id = new ALNumberField();
    ip_addr = new ALStringField();
    note = new ALStringField();
    data_name = new ALStringField();
    is_data_name = false;
  }

  /**
   * @return
   */
  public ALNumberField getEventlogId() {
    return eventlog_id;
  }

  /**
   * @return
   */
  public ALStringField getUserFullName() {
    return user_full_name;
  }

  /**
   * @return
   */
  public ALStringField getEventDate() {
    return event_date;
  }

  /**
   * @return
   */
  public ALStringField getEventName() {
    return event_name;
  }

  /**
   * @return
   */
  public ALStringField getPortletName() {
    return portlet_name;
  }

  /**
   * @return
   */
  public ALNumberField getEntityId() {
    return entity_id;
  }

  /**
   * @return
   */
  public ALStringField getIpAddr() {
    return ip_addr;
  }

  /**
   * @return
   */
  public ALStringField getNote() {
    return note;
  }

  /**
   * @return
   */
  public ALStringField getDataName() {
    return data_name;
  }

  /**
   * @return
   */
  public boolean isDataNameFlag() {
    return is_data_name;
  }

  /**
   * @param i
   */
  public void setEventlogId(long i) {
    eventlog_id.setValue(i);
  }

  /**
   * @param i
   */
  public void setUserFullName(String string) {
    user_full_name.setValue(string);
  }

  /**
   * @param string
   */
  public void setEventDate(String string) {
    event_date.setValue(string);
  }

  /**
   * @param string
   */
  public void setEventName(String string) {
    event_name.setValue(string);
  }

  /**
   * @param string
   */
  public void setPortletName(String string) {
    portlet_name.setValue(string);
  }

  /**
   * @param i
   */
  public void setEntityId(long i) {
    entity_id.setValue(i);
  }

  /**
   * @param string
   */
  public void setIpAddr(String string) {
    ip_addr.setValue(string);
  }

  /**
   * @param string
   */
  public void setNote(String string) {
    note.setValue(string);
  }

  /**
   * @param string
   */
  public void setDataName(String string) {
    data_name.setValue(string);
  }

  /**
   * @return
   */
  public void setDataNameFlag(boolean bool) {
    is_data_name = bool;
  }
}

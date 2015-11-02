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
package com.aimluck.eip.webmail;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * 管理者メール通知設定のResultDataです。 <br />
 */
public class WebMailAdminSettingsResultData implements ALData {

  /** メール送信時のメッセージ種別(スケジュール) */
  private ALNumberField msg_type_schedule;

  /** メール送信時のメッセージ種別(伝言メモ) */
  private ALNumberField msg_type_note;

  /** メール送信時のメッセージ種別(ブログ) */
  private ALNumberField msg_type_blog;

  /** メール送信時のメッセージ種別(ワークフロー) */
  private ALNumberField msg_type_workflow;

  /** メール通知時間 */
  private ALStringField msg_inform_time_hour;

  private ALStringField msg_inform_time_minute;

  public void initField() {
    msg_type_schedule = new ALNumberField();
    msg_type_note = new ALNumberField();
    msg_type_blog = new ALNumberField();
    msg_type_workflow = new ALNumberField();
    msg_inform_time_hour = new ALStringField();
    msg_inform_time_minute = new ALStringField();
  }

  /**
   * メール送信時のメッセージ種別(スケジュール)
   * 
   * @return
   */
  public ALNumberField getMsgTypeSchedule() {
    return msg_type_schedule;
  }

  /**
   * メール送信時のメッセージ種別(伝言メモ)
   * 
   * @return
   */
  public ALNumberField getMsgTypeNote() {
    return msg_type_note;
  }

  /**
   * メール送信時のメッセージ種別(ブログ)
   * 
   * @return
   */
  public ALNumberField getMsgTypeBlog() {
    return msg_type_blog;
  }

  /**
   * メール送信時のメッセージ種別(ワークフロー)
   * 
   * @return
   */
  public ALNumberField getMsgTypeWorkflow() {
    return msg_type_workflow;
  }

  /**
   * メール送信時間
   * 
   * @return
   */
  public ALStringField getMsgNotifyTimeHour() {
    return msg_inform_time_hour;
  }

  public ALStringField getMsgNotifyTimeMinute() {
    return msg_inform_time_minute;
  }

  /**
   * メール送信時のメッセージ種別(スケジュール)
   * 
   */

  public void setMsgTypeSchedule(int i) {
    msg_type_schedule.setValue(i);
  }

  /**
   * メール送信時のメッセージ種別(伝言メモ)
   * 
   */

  public void setMsgTypeNote(int i) {
    msg_type_note.setValue(i);
  }

  /**
   * メール送信時のメッセージ種別(ブログ)
   * 
   */
  public void setMsgTypeBlog(int i) {
    msg_type_blog.setValue(i);
  }

  /**
   * メール送信時のメッセージ種別(ワークフロー)
   * 
   */
  public void setMsgTypeWorkflow(int i) {
    msg_type_workflow.setValue(i);
  }

  /**
   * メール送信時間
   * 
   * @return
   */
  public void setMsgNotifyTimeHour(String str) {
    msg_inform_time_hour.setValue(str);
  }

  public void setMsgNotifyTimeMinute(String str) {
    msg_inform_time_minute.setValue(str);
  }
}

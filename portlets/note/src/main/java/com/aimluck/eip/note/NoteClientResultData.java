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
package com.aimluck.eip.note;

import java.util.Date;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.note.util.NoteUtils;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 伝言メモ依頼者のResultDataです。
 */
public class NoteClientResultData {

  /** 伝言メモ ID */
  private ALNumberField note_id;

  /** 送信元ユーザ ID（アカウント ID） */
  private ALStringField src_user_id;

  /** 送信元ユーザ名 */
  private ALStringField src_user_fullname;

  /** 依頼者名 */
  private ALStringField client_name;

  /** 依頼者所属 */
  private ALStringField company_name;

  /** 用件 */
  private ALStringField subject;

  /** 新着／未読／既読フラグ */
  private ALStringField note_stat = null;

  /** 受付日時 */
  private ALDateTimeField accept_date;

  /** 新着／未読／既読の画像へのファイルパス */
  private String note_stat_image_path = null;

  /** 新着／未読／既読の画像に対する説明文 */
  private String note_stat_image_description = null;

  /**
   *
   */
  public void initField() {
    note_id = new ALNumberField();
    client_name = new ALStringField();
    src_user_id = new ALStringField();
    src_user_fullname = new ALStringField();
    company_name = new ALStringField();
    subject = new ALStringField();
    note_stat = new ALStringField();
    accept_date = new ALDateTimeField(NoteUtils.DATE_TIME_FORMAT);
  }

  /**
   * @return
   */
  public ALDateTimeField getAcceptDate() {
    return ALEipUtils.getFormattedTime(accept_date);
  }

  /**
   * @return
   */
  public String getClientName() {
    return ALCommonUtils.replaceToAutoCR(client_name.toString());
  }

  /**
   * @return
   */
  public ALNumberField getNoteId() {
    return note_id;
  }

  /**
   * @return
   */
  public String getSubject() {
    return ALCommonUtils.replaceToAutoCR(subject.toString());
  }

  /**
   * @return
   */
  public String getClientNameSubjectText() {
    return ALLocalizationUtils.getl10nFormat(
      "NOTE_CLIENT_NAME_SUBJECT_TEXT",
      ALCommonUtils.replaceToAutoCR(client_name.toString()),
      ALCommonUtils.replaceToAutoCR(subject.toString()));
  }

  /**
   * @param field
   */
  public void setClientName(String field) {
    client_name.setValue(field);
  }

  /**
   * @param field
   */
  public void setNoteId(long id) {
    note_id.setValue(id);
  }

  /**
   * @param field
   */
  public void setSubject(String field) {
    subject.setValue(field);
  }

  /**
   * 
   * @return
   */
  public ALStringField getNoteStat() {
    return note_stat;
  }

  /**
   * 
   * @param value
   */
  public void setNoteStat(String value) {
    note_stat.setValue(value);
  }

  /**
   * 新着／未読／既読の画像ファイルへのパスを返す．
   * 
   * @return
   */
  public void setNoteStatImage(String noteStatImagePath) {
    this.note_stat_image_path = noteStatImagePath;
  }

  /**
   * 新着／未読／既読の画像ファイルへのパスを返す．
   * 
   * @return
   */
  public String getNoteStatImage() {
    return note_stat_image_path;
  }

  /**
   * 
   * @param noteStatImageDescription
   */
  public void setNoteStatImageDescription(String noteStatImageDescription) {
    this.note_stat_image_description = noteStatImageDescription;
  }

  /**
   * 
   * @return
   */
  public String getNoteStatImageDescription() {
    return note_stat_image_description;
  }

  /**
   * @param field
   */
  public void setCompanyName(String field) {
    company_name.setValue(field);
  }

  /**
   * @return
   */
  public String getCompanyName() {
    return ALCommonUtils.replaceToAutoCR(company_name.toString());
  }

  /**
   * @param field
   */
  public void setAcceptDate(Date date) {
    if (date == null) {
      return;
    }
    accept_date.setValue(date);
  }

  public ALDateTimeField getAcceptDateTime() {
    return ALEipUtils.getFormattedTime(accept_date);
  }

  /**
   * @param field
   */
  public void setSrcUserId(String field) {
    src_user_id.setValue(field);
  }

  /**
   * @return
   */
  public ALStringField getSrcUserId() {
    return src_user_id;
  }

  /**
   * 
   * @param field
   */
  public void setSrcUserFullName(String field) {
    src_user_fullname.setValue(field);
  }

  /**
   * 
   * @return
   */
  public ALStringField getSrcUserFullName() {
    return src_user_fullname;
  }

}

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

import java.util.Date;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.util.ALCommonUtils;

/**
 * WebメールのResultDataです。 <br />
 */
public class WebMailIndexRowResultData {

  /** 未読／既読 */
  private boolean is_read = false;

  /** 未読／既読の画像へのファイルパス */
  private String read_image_path = null;

  /** 未読／既読の画像に対する説明文 */
  private String read_image_description = null;

  /** 添付ファイルの有無 */
  private boolean has_attachments = false;

  /** 添付有りの画像へのファイルパス */
  private String withfiles_image_path = null;

  /** 添付有りの画像に対する説明文 */
  private String withfiles_image_description = null;

  /** mailId */
  private ALNumberField mail_id = null;

  /** 件名 */
  private ALStringField subject = null;

  /** 差出人 or 受取人 */
  private ALStringField person = null;

  /** 日付 */
  private ALDateTimeField date = null;

  /** 日付 */
  private ALDateTimeField dateYear = null;

  /** 日付 */
  private ALDateTimeField dateDateYear = null;

  /** 日付時刻 */
  private ALDateTimeField date_time = null;

  /** ファイル容量（KB） */
  private ALStringField file_volume = null;

  /** メールの本体の保存ファイル */
  private ALStringField file_name = null;

  public void initField() {
    mail_id = new ALNumberField();
    subject = new ALStringField();
    person = new ALStringField();
    date = new ALDateTimeField("M月d日");
    date_time = new ALDateTimeField("H:mm");
    dateYear = new ALDateTimeField("yyyy年");
    dateDateYear = new ALDateTimeField("yyyy年M月d日");
    file_volume = new ALStringField();
    file_name = new ALStringField();
  }

  /**
   * 日付を返す．
   * 
   * @return
   */
  public ALDateTimeField getDate() {
    ALDateTimeField today = new ALDateTimeField("M月d日");
    ALDateTimeField thisYear = new ALDateTimeField("yyyy年");
    today.setValue(new Date());
    thisYear.setValue(new Date());
    if (date.toString().equals(today.toString())
      && dateYear.toString().equals(thisYear.toString())) {
      return date_time;
    } else if (!date.toString().equals(today.toString())
      && dateYear.toString().equals(thisYear.toString())) {
      return date;
    } else {
      return dateDateYear;
    }
  }

  /**
   * メール（添付ファイルを含む）の容量（KB）を返す．
   * 
   * @return
   */
  public ALStringField getFileVolume() {
    return file_volume;
  }

  /**
   * 差出人 or 受取人のメールアドレスを返す．
   * 
   * @return
   */
  public String getPerson() {
    return ALCommonUtils.replaceToAutoCR(person.toString());
  }

  /**
   * 既読／未読を返す．
   * 
   * @return
   */
  public boolean isRead() {
    return is_read;
  }

  /**
   * 添付ファイルの有無を返す．
   * 
   * @return
   */
  public boolean hasAttachments() {
    return has_attachments;
  }

  /**
   * 件名を返す．
   * 
   * @return
   */
  public String getSubject() {
    return ALCommonUtils.replaceToAutoCR(subject.toString());
  }

  /**
   * メールが保存されているファイル名を返す．
   * 
   * @return
   */
  public ALStringField getFileName() {
    return file_name;
  }

  /**
   * @param date
   */
  public void setDate(Date nDate) {
    if (nDate == null) {
      return;
    }
    this.dateYear.setValue(nDate);
    this.dateDateYear.setValue(nDate);
    this.date.setValue(nDate);
    this.date_time.setValue(nDate);
  }

  public void setDate(String dateStr) {
    this.date.setValue(dateStr);
  }

  /**
   * @param string
   */
  public void setFileName(String string) {
    file_name.setValue(string);
  }

  /**
   * @param string
   */
  public void setFileVolume(String string) {
    file_volume.setValue(string);
  }

  /**
   * @param string
   */
  public void setPerson(String string) {
    person.setValue(string);
  }

  /**
   * @param b
   */
  public void setRead(boolean b) {
    is_read = b;
  }

  public void hasAttachments(boolean b) {
    has_attachments = b;
  }

  /**
   * 既読／未読の画像ファイルへのパスを返す．
   * 
   * @return
   */
  public void setReadImage(String readImagePath) {
    this.read_image_path = readImagePath;
  }

  /**
   * 既読／未読の画像ファイルへのパスを返す．
   * 
   * @return
   */
  public String getReadImage() {
    return read_image_path;
  }

  public void setWithFilesImage(String withfilesImagePath) {
    this.withfiles_image_path = withfilesImagePath;
  }

  public String getWithFilesImage() {
    return withfiles_image_path;
  }

  public void setReadImageDescription(String readImageDescription) {
    this.read_image_description = readImageDescription;
  }

  public String getReadImageDescription() {
    return read_image_description;
  }

  public void setWithFilesImageDescription(String withfileImageDescription) {
    this.withfiles_image_description = withfileImageDescription;
  }

  public String getWithFilesImageDescription() {
    return withfiles_image_description;
  }

  /**
   * @param field
   */
  public void setSubject(String string) {
    subject.setValue(string);
  }

  /**
   * @return
   */
  public ALNumberField getMailId() {
    return mail_id;
  }

  /**
   *
   */
  public void setMailId(String mailId) {
    mail_id.setValue(mailId);
  }

}

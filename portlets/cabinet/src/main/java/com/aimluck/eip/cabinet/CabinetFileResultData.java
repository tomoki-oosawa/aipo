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
package com.aimluck.eip.cabinet;

import java.util.Date;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 共有フォルダのファイルのResultDataです。 <BR>
 *
 */
public class CabinetFileResultData implements ALData {

  /** ファイル ID */
  private ALNumberField file_id;

  /** ファイルタイトル */
  private ALStringField file_title;

  /** ファイル名 */
  private ALStringField file_name;

  /** ファイルサイズ */
  private ALNumberField file_size;

  /** ダウンロード数 */
  private ALNumberField counter;

  /** ファイル位置 */
  private String position;

  /** メモ */
  private ALStringField note;

  /** 登録者 ID */
  private ALNumberField create_user_id;

  /** 登録者名 */
  private ALStringField create_user;

  /** 更新者 ID */
  private ALNumberField update_user_id;

  /** 更新者名 */
  private ALStringField update_user;

  /** 作成日 */
  private ALStringField create_date;

  /** 更新日 */
  private ALDateTimeField update_date;

  /** フォルダ ID */
  private ALNumberField folder_id;

  /** フォルダ名 */
  private ALStringField folder_name;

  /** 編集可能か */
  private boolean is_editable;

  /**
   *
   *
   */
  @Override
  public void initField() {
    file_id = new ALNumberField();
    file_title = new ALStringField();
    file_name = new ALStringField();
    file_size = new ALNumberField();
    counter = new ALNumberField();
    note = new ALStringField();
    note.setTrim(false);
    create_user_id = new ALNumberField();
    create_user = new ALStringField();
    update_user_id = new ALNumberField();
    update_user = new ALStringField();
    create_date = new ALStringField();
    update_date = new ALDateTimeField();
    folder_id = new ALNumberField();
    folder_name = new ALStringField();
  }

  /**
   * @return
   */
  public ALNumberField getFileId() {
    return file_id;
  }

  /**
   * @return
   */
  public ALStringField getFileTitle() {
    return file_title;
  }

  public String getFileTitleHtml() {
    return ALCommonUtils.replaceToAutoCR(file_title.toString());
  }

  /**
   * @return
   */
  public ALStringField getFileName() {
    return file_name;
  }

  /**
   * @return
   */
  public String getWbrFileName() {
    return ALCommonUtils.replaceToAutoCR(file_name.getValue());
  }

  public String getURLEncodedFileName() {
    return file_name.getURLEncodedValue();
  }

  /**
   * @return
   */
  public ALNumberField getFileSize() {
    return file_size;
  }

  /**
   * @return
   */
  public ALNumberField getCounter() {
    return counter;
  }

  /**
   * @param i
   */
  public void setFileId(long i) {
    file_id.setValue(i);
  }

  /**
   * @param string
   */
  public void setFileTitle(String string) {
    file_title.setValue(string);
  }

  /**
   * @param string
   */
  public void setFileName(String string) {
    file_name.setValue(string);
  }

  /**
   * @param i
   */
  public void setFileSize(long i) {
    file_size.setValue(((i - 1) / 1024) + 1);
  }

  /**
   * @param i
   */
  public void setCounter(int i) {
    counter.setValue(i);
  }

  /**
   * @return
   */
  public String getNote() {
    return ALEipUtils.getMessageList(note.getValue());
  }

  public String getNoteHtml() {
    return ALCommonUtils.replaceToAutoCR(ALEipUtils.getMessageList(note
      .getValue()));
  }

  /**
   * @param string
   */
  public void setNote(String string) {
    note.setValue(string);
  }

  public ALStringField getCreateUser() {
    return create_user;
  }

  public void setCreateUser(String str) {
    create_user.setValue(str);
  }

  public ALStringField getUpdateUser() {
    return update_user;
  }

  public String getUpdateUserHtml() {
    return ALCommonUtils.replaceToAutoCR(update_user.toString());
  }

  public void setUpdateUser(String str) {
    update_user.setValue(str);
  }

  /**
   * 登録者IDのの取得
   *
   * @return
   */
  public ALNumberField getCreateUserId() {
    return create_user_id;
  }

  /**
   * 登録者IDの設定
   *
   * @param i
   */
  public void setCreateUserId(long i) {
    create_user_id.setValue(i);
  }

  /**
   * 更新者IDのの取得
   *
   * @return
   */
  public ALNumberField getUpdateUserId() {
    return update_user_id;
  }

  /**
   * 更新者IDの設定
   *
   * @param i
   */
  public void setUpdateUserId(long i) {
    update_user_id.setValue(i);
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

  public String getPosition() {
    return position;
  }

  public String getPositionHtml() {
    return ALCommonUtils.replaceToAutoCR(position.toString());
  }

  public void setPosition(String str) {
    position = str;
  }

  public ALNumberField getFolderId() {
    return folder_id;
  }

  public void setFolderId(int id) {
    folder_id.setValue(id);
  }

  public ALStringField getFolderName() {
    return folder_name;
  }

  public String getFileNameHtml() {
    return ALCommonUtils.replaceToAutoCR(file_name.toString());
  }

  public String getFolderNameHtml() {
    return ALCommonUtils.replaceToAutoCR(folder_name.toString());
  }

  public void setFolderName(String string) {
    folder_name.setValue(string);
  }

  public void setisEditable(boolean isEditable) {
    this.is_editable = isEditable;
  }

  public boolean isEditable() {
    return is_editable;
  }

}

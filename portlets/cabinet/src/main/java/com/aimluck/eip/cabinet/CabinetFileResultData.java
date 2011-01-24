/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

  /** ファイル位置 */
  private String position;

  /** メモ */
  private ALStringField note;

  /** 登録者名 */
  private ALStringField create_user;

  /** 更新者名 */
  private ALStringField update_user;

  /** 登録日 */
  private ALStringField create_date;

  /** 更新日 */
  private ALStringField update_date;

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
  public void initField() {
    file_id = new ALNumberField();
    file_title = new ALStringField();
    file_name = new ALStringField();
    file_size = new ALNumberField();
    note = new ALStringField();
    note.setTrim(false);
    create_user = new ALStringField();
    update_user = new ALStringField();
    create_date = new ALStringField();
    update_date = new ALStringField();
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

  /**
   * @return
   */
  public ALNumberField getFileSize() {
    return file_size;
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
    file_size.setValue(i);
  }

  /**
   * @return
   */
  public String getNote() {
    return ALEipUtils.getMessageList(note.getValue());
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

  public void setUpdateUser(String str) {
    update_user.setValue(str);
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

  public String getPosition() {
    return position;
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

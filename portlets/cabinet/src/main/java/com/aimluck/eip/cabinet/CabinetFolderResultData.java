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
 * 共有フォルダのフォルダのResultDataです。 <BR>
 * 
 */
public class CabinetFolderResultData implements ALData {

  /** Folder ID */
  private ALNumberField folder_id;

  /** フォルダ名 */
  private ALStringField folder_name;

  /** フォルダ位置 */
  private String position;

  /** メモ */
  private ALStringField note;

  /**
   * 登録者名
   */
  private ALStringField create_user;

  /**
   * 更新者名
   */
  private ALStringField update_user;

  /** 閲覧/返信フラグ */
  private ALNumberField access_flag;

  /** 登録日 */
  private ALStringField create_date;

  /** 更新日 */
  private ALStringField update_date;

  /** 編集・削除を許可するか */
  private boolean can_update = true;

  /**
   *
   *
   */
  @Override
  public void initField() {
    folder_id = new ALNumberField();
    folder_name = new ALStringField();
    note = new ALStringField();
    note.setTrim(false);
    create_user = new ALStringField();
    update_user = new ALStringField();
    access_flag = new ALNumberField();
    create_date = new ALStringField();
    update_date = new ALStringField();
  }

  /**
   * @return
   */
  public ALNumberField getFolderId() {
    return folder_id;
  }

  /**
   * @return
   */
  public ALStringField getFolderName() {
    return folder_name;
  }

  public String getFolderNameHtml() {
    return ALCommonUtils.replaceToAutoCR(folder_name.toString());
  }

  /**
   * @param i
   */
  public void setFolderId(long i) {
    folder_id.setValue(i);
  }

  /**
   * @param string
   */
  public void setFolderName(String string) {
    folder_name.setValue(string);
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

  /**
   * @return
   */
  public String getNote() {
    return ALEipUtils.getMessageList(note.getValue());
  }

  public String getNoteHtml() {
    return ALCommonUtils.replaceToAutoCR(ALEipUtils.getMessageList(
      note.getValue()).toString());
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

  public boolean canUpdate() {
    return can_update;
  }

  public void setCanUpdate(boolean bool) {
    can_update = bool;
  }

  public ALNumberField getAccessFlag() {
    return access_flag;
  }

  public void setAccessFlag(int access_flag) {
    this.access_flag.setValue(access_flag);
  }

}

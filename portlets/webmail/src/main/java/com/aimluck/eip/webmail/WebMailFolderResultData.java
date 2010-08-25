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
package com.aimluck.eip.webmail;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * ウェブメールのフォルダのResultDataです。 <BR>
 * 
 */
public class WebMailFolderResultData implements ALData {

  /** Folder ID */
  private ALNumberField folder_id;

  /** フォルダ名 */
  private ALStringField folder_name;

  /** 登録日 */
  private ALStringField create_date;

  /** 更新日 */
  private ALStringField update_date;

  /** 編集・削除を許可するか */
  private boolean can_update = true;

  /**
   * 
   * @see com.aimluck.eip.common.ALData#initField()
   */
  public void initField() {
    folder_id = new ALNumberField();
    folder_name = new ALStringField();
    create_date = new ALStringField();
    update_date = new ALStringField();
  }

  /**
   * フォルダIDを返します。
   * 
   * @return
   */
  public ALNumberField getFolderId() {
    return folder_id;
  }

  /**
   * フォルダ名を返します。
   * 
   * @return
   */
  public ALStringField getFolderName() {
    return folder_name;
  }

  /**
   * フォルダ作成日を返します。
   * 
   * @return
   */
  public ALStringField getCreateDate() {
    return create_date;
  }

  /**
   * フォルダ更新日を返します。
   * 
   * @return
   */
  public ALStringField getUpdateDate() {
    return update_date;
  }

  /**
   * フォルダの更新可否を返します。
   * 
   * @return
   */
  public boolean getCanUpdate() {
    return can_update;
  }

  /**
   * フォルダIDをセットします。
   * 
   * @param i
   */
  public void setFolderId(long i) {
    folder_id.setValue(i);
  }

  /**
   * フォルダIDをセットします。
   * 
   * @param string
   */
  public void setFolderName(String string) {
    folder_name.setValue(string);
  }

  /**
   * フォルダ作成日をセットします。
   * 
   * @param string
   */
  public void setCreateDate(String string) {
    create_date.setValue(string);
  }

  /**
   * フォルダ更新日をセットします。
   * 
   * @param string
   */
  public void setUpdateDate(String string) {
    update_date.setValue(string);
  }

  /**
   * フォルダの更新可否をセットします。
   * 
   * @param bool
   */
  public void setCanUpdate(boolean bool) {
    can_update = bool;
  }
}

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
package com.aimluck.eip.webmail;

import com.aimluck.commons.field.ALDateField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTMailFolder;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALCommonUtils;

/**
 * ウェブメールのフォルダのResultDataです。 <BR>
 * 
 */
public class WebMailFolderResultData implements ALData {

  /** Folder ID */
  private Integer folder_id;

  /** フォルダ名 */
  private ALStringField folder_name;

  /** 作成日 */
  private ALDateField create_date;

  /** 更新日 */
  private ALDateField update_date;

  /** 編集・削除を許可するか */
  private boolean can_update = true;

  public WebMailFolderResultData() {

  }

  public WebMailFolderResultData(EipTMailFolder folder) {
    initField();
    this.folder_id = folder.getFolderId();
    this.folder_name.setValue(folder.getFolderName());
    this.create_date.setValue(folder.getCreateDate());
    this.update_date.setValue(folder.getUpdateDate());
  }

  /**
   *
   *
   */
  @Override
  public void initField() {
    folder_id = 0;
    folder_name = new ALStringField();
    create_date = new ALDateField();
    update_date = new ALDateField();
  }

  /**
   * フォルダIDを返します。
   * 
   * @return
   */
  public Integer getFolderId() {
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

  public String getWbrFolderName() {
    return ALCommonUtils.replaceToAutoCR(getFolderName().toString());
  }

  /**
   * フォルダ作成日を返します。
   * 
   * @return
   */
  public ALDateField getCreateDate() {
    return create_date;
  }

  /**
   * フォルダ更新日を返します。
   * 
   * @return
   */
  public ALDateField getUpdateDate() {
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
  public void setFolderId(Integer i) {
    folder_id = i;
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

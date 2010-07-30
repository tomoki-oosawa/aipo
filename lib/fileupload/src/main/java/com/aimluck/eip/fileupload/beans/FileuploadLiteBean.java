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
package com.aimluck.eip.fileupload.beans;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * アップロードファイルのBeanです。 <br />
 * 
 */
public class FileuploadLiteBean implements ALData, Cloneable {

  /** フォルダ名 */
  private ALStringField folder_name;

  /** ファイルID */
  private ALNumberField file_id;

  /** ファイル名 */
  private ALStringField file_name;

  /** 新規にアップロードされたファイルかどうか */
  private boolean is_new_file = true;

  /**
   * 
   * @see com.aimluck.eip.common.ALData#initField()
   */
  public void initField() {
    folder_name = new ALStringField();
    file_id = new ALNumberField();
    file_name = new ALStringField();
  }

  /**
   * 
   * @param string
   */
  public void setFolderName(String string) {
    folder_name.setValue(string);
  }

  /**
   * 
   * @param i
   */
  public void setFileId(int i) {
    file_id.setValue(i);
  }

  /**
   * 
   * @param string
   */
  public void setFileName(String string) {
    file_name.setValue(string);
  }

  /**
   * 
   * @param bool
   */
  public void setFlagNewFile(boolean bool) {
    is_new_file = bool;
  }

  /**
   * 
   * @return
   */
  public String getFolderName() {
    return folder_name.getValue();
  }

  /**
   * 
   * @return
   */
  public int getFileId() {
    return (int) file_id.getValue();
  }

  /**
   * 
   * @return
   */
  public String getFileName() {
    return file_name.getValue();
  }

  /**
   * 
   * @return
   */
  public ALStringField getFileNameField() {
    return file_name;
  }

  /**
   * 
   * @return
   */
  public boolean isNewFile() {
    return is_new_file;
  }

}

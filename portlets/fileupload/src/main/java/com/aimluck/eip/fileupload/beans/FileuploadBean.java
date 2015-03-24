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
package com.aimluck.eip.fileupload.beans;

import com.aimluck.commons.field.ALStringField;

/**
 * アップロードファイルの詳細な情報を含むBeanです。 <br />
 * 
 */
public class FileuploadBean extends FileuploadLiteBean {

  /** フォルダパス */
  private ALStringField folderPath = null;

  /** ファイルの種別 */
  private ALStringField contentType = null;

  /** 画像かどうか */
  private boolean isImage = false;

  /**
   * コンストラクタ
   */
  public FileuploadBean() {
    initField();

    folderPath = new ALStringField();
    folderPath.setFieldName("フォルダパス");
    folderPath.setTrim(true);

    contentType = new ALStringField();
    contentType.setFieldName("コンテントタイプ");
    contentType.setTrim(true);
  }

  public ALStringField getFolderPath() {
    return folderPath;
  }

  public void setFolderPath(String path) {
    folderPath.setValue(path);
  }

  public ALStringField getContentType() {
    return contentType;
  }

  public void setContentType(String type) {
    contentType.setValue(type);
  }

  public void setIsImage(boolean bool) {
    isImage = bool;
  }

  public boolean isImage() {
    return isImage;
  }

}

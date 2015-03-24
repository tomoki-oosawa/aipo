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
package com.aimluck.eip.fileupload;

/**
 * ファイルアップロードのフォームデータを管理するクラスです。 <br />
 * 
 */
public class FileuploadViewResultData {
  private int ownerId;

  private int entryId;

  private int fileId;

  private String fileName;

  private String RawScreen;

  /**
   * @param ownerId
   *          セットする ownerId
   */
  public void setOwnerId(int ownerId) {
    this.ownerId = ownerId;
  }

  /**
   * @return ownerId
   */
  public int getOwnerId() {
    return ownerId;
  }

  /**
   * @param entryId
   *          セットする entryId
   */
  public void setEntryId(int entryId) {
    this.entryId = entryId;
  }

  /**
   * @return entryId
   */
  public int getEntryId() {
    return entryId;
  }

  /**
   * @param fileId
   *          セットする fileId
   */
  public void setFileId(int fileId) {
    this.fileId = fileId;
  }

  /**
   * @return fileId
   */
  public int getFileId() {
    return fileId;
  }

  /**
   * @param rawScreen
   *          セットする rawScreen
   */
  public void setRawScreen(String rawScreen) {
    RawScreen = rawScreen;
  }

  /**
   * @return rawScreen
   */
  public String getRawScreen() {
    return RawScreen;
  }

  /**
   * @param fileName
   *          セットする fileName
   */
  public void setFileName(String fileName) {
    this.fileName = fileName != null ? fileName : "null";
  }

  /**
   * @return fileName
   */
  public String getFileName() {
    return fileName;
  }

}

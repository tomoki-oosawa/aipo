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
package com.aimluck.eip.blog;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * ブログエントリーの添付ファイルのResultDataです。 <BR>
 * 
 */
public class BlogFileResultData implements ALData {
  /** ファイル ID */
  private ALNumberField file_id;

  /** Owner ID */
  private ALNumberField owner_id;

  /** エントリー ID */
  private ALNumberField entry_id;

  /** エントリータイトル */
  private ALStringField entry_title;

  /**
   * 
   * 
   */
  public void initField() {
    file_id = new ALNumberField();
    owner_id = new ALNumberField();
    entry_id = new ALNumberField();
    entry_title = new ALStringField();
  }

  /**
   * @return
   */
  public ALNumberField getFileId() {
    return file_id;
  }

  /**
   * @param i
   */
  public void setFileId(long i) {
    file_id.setValue(i);
  }

  /**
   * @return
   */
  public ALNumberField getOwnerId() {
    return owner_id;
  }

  /**
   * @param i
   */
  public void setOwnerId(long i) {
    owner_id.setValue(i);
  }

  /**
   * @return
   */
  public ALNumberField getEntryId() {
    return entry_id;
  }

  /**
   * @param i
   */
  public void setEntryId(long i) {
    entry_id.setValue(i);
  }

  /**
   * @return
   */
  public ALStringField getEntryTitle() {
    return entry_title;
  }

  /**
   * @param i
   */
  public void setEntryTitle(String string) {
    entry_title.setValue(string);
  }
}

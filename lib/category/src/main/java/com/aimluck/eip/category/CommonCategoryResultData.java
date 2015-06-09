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
package com.aimluck.eip.category;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * 共有カテゴリのResultDataです。 <br />
 * 
 */
public class CommonCategoryResultData implements ALData {

  /** カテゴリID */
  private ALNumberField category_id;

  /** カテゴリ名 */
  private ALStringField name;

  /** メモ */
  private ALStringField note;

  /** 作成者 */
  private ALNumberField create_user_id;

  /** 登録日 */
  private ALStringField create_date;

  /** 更新日 */
  private ALStringField update_date;

  /**
   *
   */
  public void initField() {
    category_id = new ALNumberField();
    name = new ALStringField();
    note = new ALStringField();
    create_user_id = new ALNumberField();
    create_date = new ALStringField();
    update_date = new ALStringField();
  }

  /**
   * @return
   */
  public ALStringField getName() {
    return name;
  }

  /**
   * @return
   */
  public ALStringField getNote() {
    return note;
  }

  /**
   * @return
   */
  public ALNumberField getCreateUserId() {
    return create_user_id;
  }

  /**
   * @param string
   */
  public void setName(String string) {
    name.setValue(string);
  }

  /**
   * @param string
   */
  public void setNote(String string) {
    note.setValue(string);
  }

  /**
   * @param string
   */
  public void setCreateUserId(long id) {
    create_user_id.setValue(id);
  }

  /**
   * @return
   */
  public ALNumberField getCommonCategoryId() {
    return category_id;
  }

  /**
   * @param i
   */
  public void setCommonCategoryId(long i) {
    category_id.setValue(i);
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

}

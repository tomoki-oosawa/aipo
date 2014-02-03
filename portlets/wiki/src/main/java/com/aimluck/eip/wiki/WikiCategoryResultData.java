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

package com.aimluck.eip.wiki;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 掲示板トピックのResultData <BR>
 * 
 */
public class WikiCategoryResultData implements ALData {

  /** カテゴリID */
  private ALNumberField category_id;

  /** カテゴリ名 */
  private ALStringField category_name;

  /** オーナ ID */
  private ALNumberField owner_id;

  /** 所有者名 */
  private ALStringField owner_name;

  /** ユーザー名 */
  private ALStringField user_name;

  /** 更新ユーザー名 */
  private ALStringField update_user_name;

  /** メモ */
  private ALStringField note;

  /** 公開/非公開フラグ */
  private boolean is_public;

  /** 閲覧/返信フラグ */
  private ALNumberField access_flag;

  /** 登録日 */
  private ALStringField create_date;

  /** 更新日 */
  private ALStringField update_date;

  /**
   *
   *
   */
  @Override
  public void initField() {
    category_id = new ALNumberField();
    category_name = new ALStringField();
    owner_id = new ALNumberField();
    owner_name = new ALStringField();
    note = new ALStringField();
    create_date = new ALStringField();
    update_date = new ALStringField();
    user_name = new ALStringField();
    access_flag = new ALNumberField();
    is_public = true;
    update_user_name = new ALStringField();
  }

  /**
   * @return
   */
  public String getCategoryName() {
    return category_name.toString();
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
  public void setCategoryName(String string) {
    category_name.setValue(string);
  }

  /**
   * @param string
   */
  public void setNote(String string) {
    note.setValue(string);
  }

  /**
   * @return
   */
  public ALNumberField getCategoryId() {
    return category_id;
  }

  /**
   * @param i
   */
  public void setCategoryId(long i) {
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

  public ALNumberField getOwnerId() {
    return owner_id;
  }

  public void setOwnerId(long i) {
    owner_id.setValue(i);
  }

  /**
   * 公開/非公開フラグ．
   * 
   * @return
   */
  public boolean isPublic() {
    return is_public;
  }

  /**
   * @return
   */
  public void setPublicFlag(boolean bool) {
    is_public = bool;
  }

  public void setOwnerName(String string) {
    owner_name.setValue(string);
  }

  public ALStringField getOwnerName() {
    return owner_name;
  }

  public void setAccessFlag(int value) {
    access_flag.setValue(value);
  }

  public ALNumberField getAccessFlag() {
    return access_flag;
  }

  /**
   * @param value
   */
  public void setUserName(String user_name) {
    this.user_name.setValue(user_name);
  }

  public ALStringField getUserName() {
    return user_name;
  }

  /**
   * @param value
   */
  public void setUpdateUserName(String update_user_name) {
    this.update_user_name.setValue(update_user_name);
  }

  public ALStringField getUpdateUserName() {
    return update_user_name;
  }

}

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
package com.aimluck.eip.msgboard;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 掲示板カテゴリ の ResultData <BR>
 *
 */
public class MsgboardCategoryResultData implements ALData {

  /** カテゴリID */
  private ALNumberField category_id;

  /** カテゴリ名 */
  private ALStringField category_name;

  /** オーナ ID */
  private ALNumberField owner_id;

  /** 所有者名 */
  private ALStringField owner_name;

  /** メモ */
  private ALStringField note;

  /** 公開/非公開フラグ */
  private boolean is_public;

  /** 閲覧/返信フラグ */
  private ALNumberField access_flag;

  /** 作成日 */
  private ALStringField create_date;

  /** 更新日 */
  private ALStringField update_date;

  /** ログインユーザーID */
  private ALNumberField login_user_id;

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
    login_user_id = new ALNumberField();
    note = new ALStringField();
    create_date = new ALStringField();
    update_date = new ALStringField();
    access_flag = new ALNumberField();
    is_public = true;
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

  public void setLoginUserId(int i) {
    login_user_id.setValue(i);
  }

  public ALNumberField getLoginUserId() {
    return login_user_id;
  }

}

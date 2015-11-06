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
package com.aimluck.eip.todo;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALCommonUtils;

/**
 * ToDoカテゴリのResultDataです。<BR>
 *
 */
public class ToDoCategoryResultData implements ALData {

  /** カテゴリID */
  private ALNumberField category_id;

  /** カテゴリ名 */
  private ALStringField category_name;

  /** ユーザー名 */
  private ALStringField user_name;

  /** ユーザー名id */
  private ALNumberField user_id;

  /** 更新ユーザー名 */
  private ALStringField update_user_name;

  /** 更新ユーザー名id */
  private ALNumberField update_user_id;

  /** メモ */
  private ALStringField note;

  /** 作成日 */
  private ALStringField create_date;

  /** 更新日 */
  private ALStringField update_date;

  private boolean hasAclEditCategoryOther;

  private boolean hasAclDeleteCategoryOther;

  private boolean is_self_category;

  /**
   *
   *
   */
  @Override
  public void initField() {
    category_id = new ALNumberField();
    category_name = new ALStringField();
    note = new ALStringField();
    create_date = new ALStringField();
    update_date = new ALStringField();
    user_name = new ALStringField();
    user_id = new ALNumberField();
    update_user_name = new ALStringField();
    update_user_id = new ALNumberField();
    is_self_category = false;
  }

  /**
   * @return
   */
  public String getCategoryName() {
    return category_name.toString();
  }

  public String getCategoryNameHtml() {
    return ALCommonUtils.replaceToAutoCR(category_name.toString());
  }

  /**
   * @return
   */
  public String getNote() {
    return ALCommonUtils.replaceToAutoCR(note.toString());
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

  public void setUserName(String user_name) {
    this.user_name.setValue(user_name);
  }

  public ALStringField getUserName() {
    return user_name;
  }

  public void setUpdateUserName(String update_user_name) {
    this.update_user_name.setValue(update_user_name);
  }

  public ALStringField getUpdateUserName() {
    return update_user_name;
  }

  public void setHasAclEditCategoryOther(boolean hasAclEditCategoryOther) {
    this.hasAclEditCategoryOther = hasAclEditCategoryOther;
  }

  public boolean hasAclEditCategoryOther() {
    return hasAclEditCategoryOther;
  }

  public void setHasAclDeleteCategoryOther(boolean hasAclDeleteCategoryOther) {
    this.hasAclDeleteCategoryOther = hasAclDeleteCategoryOther;
  }

  public boolean hasAclDeleteCategoryOther() {
    return hasAclDeleteCategoryOther;
  }

  public void setIsSelfCategory(boolean is_self_category) {
    this.is_self_category = is_self_category;
  }

  public boolean getIsSelfCategory() {
    return is_self_category;
  }

  /**
   * @return update_user_id
   */
  public ALNumberField getUpdateUserId() {
    return update_user_id;
  }

  /**
   * @param update_user_id
   *          セットする update_user_id
   */
  public void setUpdateUserId(ALNumberField update_user_id) {
    this.update_user_id = update_user_id;
  }

  /**
   * @return user_id
   */
  public ALNumberField getUserId() {
    return user_id;
  }

  /**
   * @param user_id
   *          セットする user_id
   */
  public void setUserId(ALNumberField user_id) {
    this.user_id = user_id;
  }

}

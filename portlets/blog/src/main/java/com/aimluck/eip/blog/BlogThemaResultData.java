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

import java.util.ArrayList;
import java.util.List;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ブログテーマのResultDataです。 <BR>
 *
 */
public class BlogThemaResultData implements ALData {

  /** テーマ ID */
  private ALNumberField thema_id;

  /** テーマ名 */
  private ALStringField thema_name;

  /** メモ */
  private ALStringField description;

  /** 作成ユーザーID */
  private ALNumberField create_user_id;

  /** 作成ユーザー名 */
  private ALStringField create_user_name;

  /** 更新ユーザーID */
  private ALNumberField update_user_id;

  /** 更新ユーザー名 */
  private ALStringField update_user_name;

  /** 作成日 */
  private ALStringField create_date;

  /** 更新日 */
  private ALStringField update_date;

  /** テーマに含まれる記事数 */
  private ALNumberField entry_num;

  /** テーマに含まれる記事リスト */
  private List<BlogEntryResultData> entryList;

  /**
   *
   *
   */
  @Override
  public void initField() {
    thema_id = new ALNumberField();
    thema_name = new ALStringField();
    description = new ALStringField();
    create_user_id = new ALNumberField();
    create_user_name = new ALStringField();
    update_user_id = new ALNumberField();
    update_user_name = new ALStringField();
    create_date = new ALStringField();
    update_date = new ALStringField();
    entry_num = new ALNumberField();

    entryList = new ArrayList<BlogEntryResultData>();
  }

  /**
   * @return
   */
  public ALStringField getThemaName() {
    return thema_name;
  }

  public String getThemaNameHtml() {
    return ALCommonUtils.replaceToAutoCR(thema_name.toString());
  }

  /**
   * @return
   */
  public String getDescription() {
    return ALEipUtils.getMessageList(description.getValue());
  }

  /**
   * @param string
   */
  public void setThemaName(String string) {
    thema_name.setValue(string);
  }

  /**
   * @param string
   */
  public void setDescription(String string) {
    description.setValue(string);
  }

  /**
   * @return
   */
  public ALNumberField getThemaId() {
    return thema_id;
  }

  /**
   * @param i
   */
  public void setThemaId(long i) {
    thema_id.setValue(i);
  }

  /**
   * @return
   */
  public ALNumberField getCreateUserId() {
    return create_user_id;
  }

  /**
   * @param i
   */
  public void setCreateUserId(long i) {
    create_user_id.setValue(i);
  }

  /**
   * @return
   */
  public ALStringField getCreateUserName() {
    return create_user_name;
  }

  /**
   * @param i
   */
  public void setCreateUserName(String str) {
    create_user_name.setValue(str);
  }

  /**
   * @return
   */
  public ALNumberField getUpdateUserId() {
    return update_user_id;
  }

  /**
   * @param i
   */
  public void setUpdateUserId(long i) {
    update_user_id.setValue(i);
  }

  /**
   * @return
   */
  public ALStringField getUpdateUserName() {
    return update_user_name;
  }

  /**
   * @param i
   */
  public void setUpdateUserName(String str) {
    update_user_name.setValue(str);
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

  public ALNumberField getEntryNum() {
    return entry_num;
  }

  public void setEntryNum(int i) {
    entry_num.setValue(i);
  }

  public void setEntryList(List<BlogEntryResultData> list) {
    entryList.addAll(list);
  }

  public List<BlogEntryResultData> getEntryList() {
    return entryList;
  }
}

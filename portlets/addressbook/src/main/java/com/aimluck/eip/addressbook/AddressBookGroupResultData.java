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
package com.aimluck.eip.addressbook;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * アドレス帳グループのリザルトデータです。
 */
public class AddressBookGroupResultData implements ALData {

  /** グループID */
  private ALNumberField group_id;

  /** グループ名 */
  private ALStringField group_name;

  /** オーナID */
  private ALNumberField owner_id;

  /** 公開フラグ */
  private ALStringField public_flag;

  /** 作成日 */
  private ALStringField create_date;

  /** 更新日 */
  private ALStringField update_date;

  /**
   *
   */
  @Override
  public void initField() {
    group_id = new ALNumberField();
    group_name = new ALStringField();
    owner_id = new ALNumberField();
    public_flag = new ALStringField();
    create_date = new ALStringField();
    update_date = new ALStringField();
  }

  /**
   * @param i
   */
  public void setGroupId(long i) {
    group_id.setValue(i);
  }

  /**
   * @return
   */
  public ALNumberField getGroupId() {
    return group_id;
  }

  /**
   * @param string
   */
  public void setGroupName(String string) {
    group_name.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getGroupName() {
    return group_name;
  }

  public void setOwnerId(long i) {
    owner_id.setValue(i);
  }

  public ALNumberField getOwnerId() {
    return owner_id;
  }

  /**
   * @param string
   */
  public void setPublicFlag(String string) {
    public_flag.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getPublicFlag() {
    return public_flag;
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

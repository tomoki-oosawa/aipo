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
package com.aimluck.eip.user.beans;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * ユーザーのBeanです。 <br />
 * 
 */
public class UserEmailLiteBean implements ALData {

  /** ID */
  private ALNumberField user_id;

  /** 名前 */
  private ALStringField name;

  /** 名前（アプリケーション） */
  private ALStringField alias_name;

  /** Email */
  private ALStringField email;

  /**
   *
   */
  @Override
  public void initField() {
    user_id = new ALNumberField();
    name = new ALStringField();
    alias_name = new ALStringField();
    email = new ALStringField();
  }

  /**
   * 
   * @param string
   */
  public void setName(String string) {
    name.setValue(string);
  }

  /**
   * 
   * @param firstName
   * @param lastName
   */
  public void setAliasName(String firstName, String lastName) {
    alias_name.setValue(new StringBuffer().append(lastName).append(" ").append(
      firstName).toString());
  }

  /**
   * 
   * @param string
   */
  public void setEmail(String string) {
    email.setValue(string);
  }

  /**
   * 
   * @return
   */
  public String getName() {
    return name.getValue();
  }

  /**
   * 
   * @return
   */
  public String getAliasName() {
    return alias_name.getValue();
  }

  /**
   * 
   * @return
   */
  public String getEmail() {
    return email.getValue();
  }

  /**
   * @return
   */
  public String getUserId() {
    return user_id.toString();
  }

  /**
   * @param field
   */
  public void setUserId(int number) {
    user_id.setValue(number);
  }
}

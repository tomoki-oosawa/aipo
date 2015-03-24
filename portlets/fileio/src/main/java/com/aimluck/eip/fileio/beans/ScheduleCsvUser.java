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
package com.aimluck.eip.fileio.beans;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;

/**
 * ユーザー情報を表すクラスです。 <br />
 * 
 */
public class ScheduleCsvUser {

  /** ID */
  private ALNumberField user_id;

  /** 名前 */
  private ALStringField name;

  /** 名前（アプリケーション） */
  private ALStringField alias_name;

  private ALStringField firstName;

  private ALStringField lastName;

  public ScheduleCsvUser() {
    this.initField();
  }

  public void initField() {
    user_id = new ALNumberField();
    name = new ALStringField();
    alias_name = new ALStringField();
    firstName = new ALStringField();
    lastName = new ALStringField();

    name.setTrim(true);
    firstName.setTrim(true);
    lastName.setTrim(true);

    name.limitMaxLength(16);
    firstName.limitMaxLength(20);
    lastName.limitMaxLength(20);
  }

  public ALStringField getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName.setValue(firstName);
  }

  public ALStringField getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName.setValue(lastName);
  }

  public void parseFullName(String rawName) {
    /** full space to half space */
    rawName = rawName.replace("　", " ");
    String value[] = rawName.trim().split(" ");

    if (value.length > 0) {
      setLastName(value[0].trim());
    }

    if (value.length > 1) {
      setFirstName(value[1].trim());
    }

    setAliasName(firstName.getValue(), lastName.getValue());
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
   * @return
   */
  public ALStringField getName() {
    return name;
  }

  /**
   * 
   * @return
   */
  public ALStringField getAliasName() {
    return alias_name;
  }

  /**
   * @return
   */
  public ALNumberField getUserId() {
    return user_id;
  }

  /**
   * @param field
   */
  public void setUserId(int number) {
    user_id.setValue(number);
  }

  public void complement() throws Exception {
    if (name.getValue() == null) {
      if (lastName.getValue() == null || firstName.getValue() == null) {
        return;
      }
      complementLoginName();
    } else {
      complementUserName();
    }
  }

  public void complementUserName() throws Exception {
    TurbineUser user =
      Database.query(TurbineUser.class).where(
        Operations.eq(TurbineUser.LOGIN_NAME_PROPERTY, name.getValue())).where(
        Operations.eq(TurbineUser.DISABLED_PROPERTY, "F")).fetchSingle();

    if (user == null) {
      user =
        Database.query(TurbineUser.class).where(
          Operations.eq(TurbineUser.EMAIL_PROPERTY, name.getValue())).where(
          Operations.eq(TurbineUser.DISABLED_PROPERTY, "F")).fetchSingle();
      if (user == null) {
        name.setValue("");// error化
        alias_name.setValue("---");
        throw new Exception();
      } else {
        this.name.limitMaxLength(50);
      }
    }

    user_id.setValue(user.getUserId());
    firstName.setValue(user.getFirstName());
    lastName.setValue(user.getLastName());
    setAliasName(firstName.getValue(), lastName.getValue());
  }

  public void complementLoginName() throws Exception {

    TurbineUser user =
      Database.query(TurbineUser.class).where(
        Operations.eq(TurbineUser.FIRST_NAME_PROPERTY, firstName.getValue()),
        Operations.and(Operations.eq(TurbineUser.LAST_NAME_PROPERTY, lastName
          .getValue()))).fetchSingle();

    if (user == null) {

      throw new Exception();
    }

    user_id.setValue(user.getUserId());
    name.setValue("");
  }
}

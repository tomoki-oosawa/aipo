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
package com.aimluck.eip.webmail;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.webmail.util.WebMailUtils;

/**
 * WebメールアカウントのResultDataです。 <br />
 */
public class WebMailAccountResultData implements ALData {

  /** The value for the accountId field */
  private ALNumberField accountId;

  /** The value for the userId field */
  private ALNumberField userId;

  private ALStringField mailaddress;

  /** The value for the accountName field */
  private ALStringField accountName;

  /** 未読のメール数 */
  private ALNumberField countUnRead;

  /** 最終更新日 */
  private ALDateTimeField finalAccessDate;

  private ALStringField account_type;

  /**
   *
   *
   */
  @Override
  public void initField() {
    accountId = new ALNumberField();
    userId = new ALNumberField();
    mailaddress = new ALStringField();
    accountName = new ALStringField();
    countUnRead = new ALNumberField();
    finalAccessDate = new ALDateTimeField(WebMailUtils.DATE_TIME_FORMAT);
    account_type = new ALStringField();
  }

  /**
   * @return
   */
  public ALNumberField getAccountId() {
    return accountId;
  }

  public ALStringField getMailAddress() {
    return mailaddress;
  }

  /**
   * @return
   */
  public ALStringField getAccountName() {
    return accountName;
  }

  /**
   * @return
   */
  public ALNumberField getUserId() {
    return userId;
  }

  public void setMailAddress(String string) {
    mailaddress.setValue(string);
  }

  /**
   * @param i
   */
  public void setAccountId(int i) {
    accountId.setValue(i);
  }

  /**
   * @param string
   */
  public void setAccountName(String string) {
    accountName.setValue(string);
  }

  /**
   * @param i
   */
  public void setUserId(int i) {
    userId.setValue(i);
  }

  /**
   * @return
   */
  public int getCountUnRead() {
    return (int) countUnRead.getValue();
  }

  /**
   * @param i
   */
  public void setCountUnRead(int i) {
    countUnRead.setValue(i);
  }

  /**
   * @return
   */
  public ALDateTimeField getFinalAccessDate() {
    return finalAccessDate;
  }

  /**
   * @param string
   */
  public void setFinalAccessDate(String string) {
    finalAccessDate.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getAccountType() {
    return account_type;
  }

  /**
   * @return
   */
  public void setAccountType(String string) {
    account_type.setValue(string);
  }

}

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
package com.aimluck.eip.webmail.beans;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * WebメールアカウントのBeanです。 <br />
 */
public class WebmailAccountLiteBean implements ALData, Cloneable {

  /** The value for the accountId field */
  private ALNumberField accountId;

  /** The value for the accountName field */
  private ALStringField accountName;

  /**
   *
   *
   */
  public void initField() {
    accountId = new ALNumberField();
    accountName = new ALStringField();
  }

  /**
   * @return
   */
  public ALNumberField getAccountId() {
    return accountId;
  }

  /**
   * @return
   */
  public ALStringField getAccountName() {
    return accountName;
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

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

}

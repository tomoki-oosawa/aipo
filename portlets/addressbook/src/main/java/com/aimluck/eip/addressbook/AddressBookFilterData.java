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
 * アドレス帳のフィルタ用データクラスです。
 */
public class AddressBookFilterData implements ALData {

  private ALNumberField groupId;

  private ALNumberField addressId;

  private ALStringField fullName;

  public void initField() {
    groupId = new ALNumberField();
    addressId = new ALNumberField();
    fullName = new ALStringField();
  }

  /**
   * @return
   */
  public ALNumberField getAddressId() {
    return addressId;
  }

  /**
   * @return
   */
  public ALStringField getFullName() {
    return fullName;
  }

  /**
   * @param field
   */
  public void setAddressId(int i) {
    addressId.setValue(i);
  }

  /**
   * @param field
   */
  public void setFullName(String lname, String fname) {
    fullName.setValue(new StringBuffer()
      .append(lname)
      .append(" ")
      .append(fname)
      .toString());
  }

  /**
   * @return
   */
  public ALNumberField getGroupId() {
    return groupId;
  }

  /**
   * @param field
   */
  public void setGroupId(int i) {
    groupId.setValue(i);
  }

}

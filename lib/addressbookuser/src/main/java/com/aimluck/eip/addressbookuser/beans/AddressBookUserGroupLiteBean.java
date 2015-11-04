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
package com.aimluck.eip.addressbookuser.beans;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * AddressBookUserGroupのBeanです。 <br />
 * 
 */
public class AddressBookUserGroupLiteBean implements ALData {

  /** ID */
  private ALNumberField group_id;

  /** 名前 */
  private ALStringField name;

  /**
   *
   */
  @Override
  public void initField() {
    group_id = new ALNumberField();
    name = new ALStringField();
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
   * @return
   */
  public String getName() {
    return name.getValue();
  }

  /**
   * @return
   */
  public String getGroupId() {
    return group_id.toString();
  }

  /**
   * @param field
   */
  public void setGroupId(int i) {
    group_id.setValue(i);
  }
}

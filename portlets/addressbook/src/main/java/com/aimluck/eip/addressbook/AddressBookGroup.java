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

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * アドレス帳グループ
 */
public class AddressBookGroup implements ALData {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AddressBookGroup.class.getName());

  // グループID
  private ALNumberField group_id;

  // グループ名
  private ALStringField group_name;

  /**
   * 初期化処理。
   */
  public void initField() {
    group_id = new ALNumberField();
    group_name = new ALStringField();
  }

  /**
   * @return
   */
  public ALNumberField getGroup_id() {
    return group_id;
  }

  /**
   * @return
   */
  public ALStringField getGroup_name() {
    return group_name;
  }

  /**
   * @param field
   */
  public void setGroupId(int i) {
    group_id.setValue(i);
  }

  /**
   * @param field
   */
  public void setGroupName(String str) {
    group_name.setValue(str);
  }

}

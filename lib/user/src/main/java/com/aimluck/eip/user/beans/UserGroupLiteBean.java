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
package com.aimluck.eip.user.beans;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * ユーザーのBeanです。 <br />
 * 
 */
public class UserGroupLiteBean implements ALData {

  /** ID */
  private ALStringField group_id;

  /** 名前 */
  private ALStringField name;

  /**
   *
   */
  @Override
  public void initField() {
    group_id = new ALStringField();
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

  public ALStringField getAliasName() {
    return name;
  }

  /**
   * @return
   */
  public String getGroupId() {
    return group_id.getValue();
  }

  /**
   * @param field
   */
  public void setGroupId(String string) {
    group_id.setValue(string);
  }
}

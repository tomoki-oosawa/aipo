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
package com.aimluck.eip.mygroup;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * マイグループのResultDataです。 <br />
 */
public class MyGroupResultData implements ALData {

  /** グループ名 */
  private ALStringField group_name;

  /** グループ名（アプリケーション） */
  private ALStringField group_alias_name;

  /**
   * 
   * 
   */
  public void initField() {
    group_name = new ALStringField();
    group_alias_name = new ALStringField();
  }

  /**
   * @return
   */
  public ALStringField getGroupAliasName() {
    return group_alias_name;
  }

  /**
   * @return
   */
  public ALStringField getGroupName() {
    return group_name;
  }

  /**
   * @param field
   */
  public void setGroupAliasName(String field) {
    group_alias_name.setValue(field);
  }

  /**
   * @param field
   */
  public void setGroupName(String field) {
    group_name.setValue(field);
  }

}

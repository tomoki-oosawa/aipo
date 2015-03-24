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
package com.aimluck.eip.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;

/**
 * グループを表すクラスです。 <br />
 * 
 */
public class ALEipGroup implements ALData, Serializable {

  /** FacilityGroup ID */
  private ALNumberField facility_group_id;

  /**
   *
   */
  private static final long serialVersionUID = -2097251761867253015L;

  /** グループ名 */
  private ALStringField name;

  /** グループ名（アプリケーション） */
  private ALStringField alias_name;

  public ALEipGroup() {
  }

  /**
   *
   */
  @Override
  public void initField() {
    name = new ALStringField();
    alias_name = new ALStringField();
  }

  /**
   * 
   * @param in
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream in) throws IOException,
      ClassNotFoundException {
    initField();
    in.defaultReadObject();
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
   * @param string
   */
  public void setAliasName(String string) {
    alias_name.setValue(string);
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
}

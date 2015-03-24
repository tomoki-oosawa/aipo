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
package com.aimluck.eip.userfacility.beans;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 *
 */
public class UserAllLiteBean implements ALData {

  private ALStringField key;

  private ALNumberField id;

  private ALStringField name;

  private Type type;

  public static enum Type {

    u,

    ug,

    f,

    fg

  }

  /**
   *
   */
  @Override
  public void initField() {
    key = new ALStringField();
    id = new ALNumberField();
    name = new ALStringField();
    type = Type.u;
  }

  /**
   * @return id
   */
  public String getKey() {
    return key.getValue();
  }

  /**
   * @return id
   */
  public int getId() {
    return (int) id.getValue();
  }

  /**
   * @param id
   *          セットする id
   */
  public void setId(int id) {
    this.id.setValue(id);
    this.key.setValue(this.type.toString() + this.id.getValue());
  }

  /**
   * @return name
   */
  public String getName() {
    if (Type.ug.equals(type) || Type.fg.equals(type)) {
      name.setValue("[" + name.getValue() + "]");
    }
    return name.getValue();
  }

  /**
   * @param name
   *          セットする name
   */
  public void setName(String name) {
    this.name.setValue(name);
  }

  /**
   * @return type
   */
  public String getType() {
    return type.toString();
  }

  /**
   * @param type
   *          セットする type
   */
  public void setType(Type type) {
    this.type = type;
    this.key.setValue(this.type.toString() + this.id.getValue());
  }

}

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
package com.aimluck.eip.schedule;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 *
 */
public class ScheduleAdminAclUserGroupResultData implements ALData {

  private ALNumberField id;

  private ALStringField name;

  private Type type;

  private boolean updated;

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
    id = new ALNumberField();
    name = new ALStringField();
    type = Type.u;
    updated = false;
  }

  /**
   * @return name
   */
  public ALStringField getName() {
    if (Type.ug.equals(type) || Type.fg.equals(type)) {
      name.setValue("[" + name.getValue() + "]");
    }
    return name;
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
  public Type getType() {
    return type;
  }

  /**
   * @param type
   *          セットする type
   */
  public void setType(Type type) {
    this.type = type;
  }

  /**
   * @return id
   */
  public ALNumberField getId() {
    return id;
  }

  /**
   * @param id
   *          セットする id
   */
  public void setId(int id) {
    this.id.setValue(id);
  }

  public String getKey() {
    return type.toString() + this.id.getValueAsString();
  }

  /**
   * @return updated
   */
  public boolean isUpdated() {
    return updated;
  }

  /**
   * @param updated
   *          セットする updated
   */
  public void setUpdated(boolean updated) {
    this.updated = updated;
  }

}

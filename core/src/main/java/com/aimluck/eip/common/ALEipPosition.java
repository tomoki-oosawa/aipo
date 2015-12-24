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
package com.aimluck.eip.common;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;

/**
 * 役職を表すクラスです。 <br />
 * 
 */
public class ALEipPosition implements ALData {

  /** 役職ID */
  private ALNumberField position_id;

  /** 役職名 */
  private ALStringField position_name;

  /**
   *
   */
  public void initField() {
    position_id = new ALNumberField();
    position_name = new ALStringField();
  }

  /**
   * @return
   */
  public ALNumberField getPositionId() {
    return position_id;
  }

  /**
   * @return
   */
  public ALStringField getPositionName() {
    return position_name;
  }

  /**
   * @param id
   */
  public void setPositionId(int id) {
    position_id.setValue(id);
  }

  /**
   * @param string
   */
  public void setPositionName(String string) {
    position_name.setValue(string);
  }

}

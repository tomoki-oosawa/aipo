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
package com.aimluck.eip.whatsnew.beans;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * 新着情報のパラメーターのBeanです。 <BR>
 * 
 */
public class WhatsNewParamsBean implements ALData {

  /** key */
  private ALStringField key;

  /** value */
  private ALStringField value;

  /**
   * 
   * 
   */
  public void initField() {
    key = new ALStringField();
    value = new ALStringField();
  }

  /**
   * 
   * @param string
   */
  public void setKey(String string) {
    key.setValue(string);
  }

  /**
   * 
   * @return
   */
  public String getKey() {
    return key.getValue();
  }

  /**
   * 
   * @param string
   */
  public void setValue(String string) {
    value.setValue(string);
  }

  /**
   * 
   * @return
   */
  public String getValue() {
    return value.getValue();
  }

}

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
package com.aimluck.eip.accessctl.bean;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 *
 *
 */
public class AccessControlPortletBean implements ALData {

  /** ポートレット種別 */
  private ALNumberField portlet_type;

  /** ポートレット名 */
  private ALStringField portlet_name;

  /**
   *
   */
  public void initField() {
    portlet_type = new ALNumberField();
    portlet_name = new ALStringField();
  }

  /**
   * @return
   */
  public int getPortletType() {
    return (int) portlet_type.getValue();
  }

  /**
   * @return
   */
  public String getPortletName() {
    return portlet_name.getValue();
  }

  /**
   * @param i
   */
  public void setPortletType(long i) {
    portlet_type.setValue(i);
  }

  /**
   * @param string
   */
  public void setPortletName(String string) {
    portlet_name.setValue(string);
  }

}

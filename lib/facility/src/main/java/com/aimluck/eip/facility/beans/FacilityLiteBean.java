/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.facility.beans;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * 施設のBeanです。 <br />
 * 
 */
public class FacilityLiteBean implements ALData, Cloneable {

  /** Facility ID */
  private ALNumberField facility_id;

  /** 施設名 */
  private ALStringField facility_name;

  /**
   * 
   * @see com.aimluck.eip.common.ALData#initField()
   */
  public void initField() {
    facility_id = new ALNumberField();
    facility_name = new ALStringField();
  }

  /**
   * @return
   */
  public String getFacilityId() {
    return facility_id.toString();
  }

  /**
   * @return
   */
  public String getFacilityName() {
    return facility_name.getValue();
  }

  /**
   * @param i
   */
  public void setFacilityId(long i) {
    facility_id.setValue(i);
  }

  /**
   * @param string
   */
  public void setFacilityName(String string) {
    facility_name.setValue(string);
  }

}

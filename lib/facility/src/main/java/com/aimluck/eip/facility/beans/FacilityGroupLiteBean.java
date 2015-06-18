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
package com.aimluck.eip.facility.beans;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * 設備グループのBeanです。 <br />
 * 
 */
public class FacilityGroupLiteBean implements ALData {

  /** FacilityGroup ID */
  private ALNumberField facility_group_id;

  /** 設備グループ名 */
  private ALStringField facility_group_name;

  /**
   *
   */
  @Override
  public void initField() {
    facility_group_id = new ALNumberField();
    facility_group_name = new ALStringField();
  }

  /**
   * @return
   */
  public String getFacilityGroupId() {
    return facility_group_id.toString();
  }

  /**
   * @return
   */
  public String getFacilityGroupName() {
    return facility_group_name.getValue();
  }

  /**
   * @param i
   */
  public void setFacilityGroupId(long i) {
    facility_group_id.setValue(i);
  }

  /**
   * @param string
   */
  public void setFacilityGroupName(String string) {
    facility_group_name.setValue(string);
  }

}

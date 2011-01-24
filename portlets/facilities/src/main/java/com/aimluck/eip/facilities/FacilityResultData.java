/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

package com.aimluck.eip.facilities;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 施設のResultDataです。 <BR>
 * 
 */
public class FacilityResultData implements ALData {

  /** Facility ID */
  private ALNumberField facility_id;

  /** 施設名 */
  private ALStringField facility_name;

  /** メモ */
  private ALStringField note;

  /** 登録日 */
  private ALStringField create_date;

  /** 更新日 */
  private ALStringField update_date;

  /**
   * 
   * 
   */
  public void initField() {
    facility_id = new ALNumberField();
    facility_name = new ALStringField();
    note = new ALStringField();
    note.setTrim(false);
    create_date = new ALStringField();
    update_date = new ALStringField();
  }

  /**
   * @return
   */
  public ALNumberField getFacilityId() {
    return facility_id;
  }

  /**
   * @return
   */
  public ALStringField getFacilityName() {
    return facility_name;
  }

  /**
   * @return
   */
  public String getStringFacilityName() {
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

  /**
   * @return
   */
  public String getNote() {
    return ALEipUtils.getMessageList(note.getValue());
  }

  /**
   * @param string
   */
  public void setNote(String string) {
    note.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getCreateDate() {
    return create_date;
  }

  /**
   * @return
   */
  public ALStringField getUpdateDate() {
    return update_date;
  }

  /**
   * @param string
   */
  public void setCreateDate(String string) {
    create_date.setValue(string);
  }

  /**
   * @param string
   */
  public void setUpdateDate(String string) {
    update_date.setValue(string);
  }

}

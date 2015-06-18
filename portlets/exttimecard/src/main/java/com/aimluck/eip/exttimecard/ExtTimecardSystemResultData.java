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
package com.aimluck.eip.exttimecard;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * タイムカード管理の勤務形態一覧の情報を保持する。
 * 
 * 
 */
public class ExtTimecardSystemResultData implements ALData {

  private ALNumberField system_id;

  private ALStringField system_name;

  /**
   *
   *
   */
  @Override
  public void initField() {
    system_id = new ALNumberField();
    system_name = new ALStringField();
  }

  public Boolean isSystemIdNormal() {
    return (system_id.getValue() == 1);
  }

  public ALNumberField getSystemId() {
    return system_id;
  }

  public ALStringField getSystemName() {
    return system_name;
  }

  public void setSystemId(int i) {
    system_id.setValue(i);
  }

  public void setSystemName(String str) {
    system_name.setValue(str);
  }

}

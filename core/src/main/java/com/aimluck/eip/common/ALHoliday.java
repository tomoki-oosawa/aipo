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

import com.aimluck.commons.field.ALDateField;
import com.aimluck.commons.field.ALStringField;

/**
 * 祝日を表すクラスです。 <br />
 * 
 */
public class ALHoliday {

  /** 祝日の名前 */
  private ALStringField name = null;

  /** 祝日の日付 */
  private ALDateField day = null;

  public ALHoliday(String name, String day) {
    this.name = new ALStringField();
    this.name.setTrim(true);
    this.name.setValue(name);

    this.day = new ALDateField();
    this.day.setValue(day);
  }

  public ALDateField getDay() {
    return day;
  }

  public ALStringField getName() {
    return name;
  }

}

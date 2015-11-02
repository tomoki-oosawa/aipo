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
package com.aimluck.eip.category.beans;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * 共有カテゴリのBeanです。 <br />
 * 
 */
public class CommonCategoryLiteBean implements ALData {

  /** カテゴリ ID */
  private ALNumberField category_id;

  /** カテゴリ名 */
  private ALStringField category_name;

  /**
   *
   */
  @Override
  public void initField() {
    category_id = new ALNumberField();
    category_name = new ALStringField();
  }

  /**
   * @return
   */
  public String getCategoryId() {
    return category_id.toString();
  }

  /**
   * @return
   */
  public String getCategoryName() {
    return category_name.toString();
  }

  /**
   * @param i
   */
  public void setCategoryId(long number) {
    category_id.setValue(number);
  }

  /**
   * @param string
   */
  public void setCategoryName(String string) {
    category_name.setValue(string);
  }
}

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
package com.aimluck.eip.workflow;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * ワークフローカテゴリのResultDataです。<BR>
 * 
 */
public class WorkflowCategoryResultData implements ALData {

  /** カテゴリID */
  protected ALNumberField category_id;

  /** カテゴリ名 */
  protected ALStringField category_name;

  /** カテゴリテンプレート */
  protected ALStringField ordertemplate;

  /**
   * 
   * 
   */
  public void initField() {
    category_id = new ALNumberField();
    category_name = new ALStringField();
    ordertemplate = new ALStringField();
  }

  /**
   * @return
   */
  public ALStringField getCategoryName() {
    return category_name;
  }

  /**
   * @param string
   */
  public void setCategoryName(String string) {
    category_name.setValue(string);
  }

  /**
   * @return
   */
  public ALNumberField getCategoryId() {
    return category_id;
  }

  /**
   * @param i
   */
  public void setCategoryId(long i) {
    category_id.setValue(i);
  }

  /**
   * @return
   */
  public ALStringField getOrderTemplate() {
    return ordertemplate;
  }

  /**
   * @param string
   */
  public void setOrderTemplate(String string) {
    ordertemplate.setValue(string);
  }

}

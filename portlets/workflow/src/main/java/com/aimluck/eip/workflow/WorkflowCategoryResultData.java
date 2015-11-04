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

  /** 申請経路 */
  protected ALStringField hasRouteName;

  /** 申請経路名 */
  protected ALStringField routeName;

  /**
   *
   *
   */
  @Override
  public void initField() {
    category_id = new ALNumberField();
    category_name = new ALStringField();
    ordertemplate = new ALStringField();
    hasRouteName = new ALStringField();
    routeName = new ALStringField();
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

  /**
   * @param string
   */
  public void setRouteName(String string) {
    routeName.setValue(string);
  }

  /**
   * @param string
   */
  public void setRoute(String string) {
    hasRouteName.setValue(string);
  }

  /**
   * @param
   */
  public ALStringField getRouteName() {
    return routeName;
  }

  public boolean getHasRouteName() {

    boolean bool;

    if (routeName.getValue() == null || "".equals(routeName.getValue())) {
      bool = false;
    } else {
      bool = true;
    }

    return bool;
  }

}

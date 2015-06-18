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
package com.aimluck.eip.workflow;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * ワークフローのResultDataです。 <BR>
 * 
 */
public class WorkflowOldRequestResultData implements ALData {

  /** Request ID */
  protected ALNumberField request_id;

  /** Request名 */
  protected ALStringField request_name;

  /** カテゴリ名 */
  protected ALStringField category_name;

  /** 更新日 */
  private ALStringField update_date;

  /**
   * 
   * 
   */
  public void initField() {
    request_id = new ALNumberField();
    request_name = new ALStringField();
    category_name = new ALStringField();
    update_date = new ALStringField();
  }

  /**
   * @return
   */
  public ALNumberField getRequestId() {
    return request_id;
  }

  /**
   * @return
   */
  public ALStringField getRequestName() {
    return request_name;
  }

  /**
   * @return
   */
  public String getCategoryName() {
    return category_name.getValue();
  }

  /**
   * @return
   */
  public ALStringField getUpdateDate() {
    return update_date;
  }

  /**
   * @param i
   */
  public void setRequestId(long i) {
    request_id.setValue(i);
  }

  /**
   * @param string
   */
  public void setRequestName(String string) {
    request_name.setValue(string);
  }

  /**
   * @param i
   */
  public void setCategoryName(String string) {
    category_name.setValue(string);
  }

  /**
   * @param string
   */
  public void setUpdateDate(String string) {
    update_date.setValue(string);
  }

}

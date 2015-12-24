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

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 *
 *
 */
public class AccessControlAclBean implements ALData {

  /** ACL ID */
  private ALStringField acl_id;

  /** ACL 名 */
  private ALStringField acl_name;

  /** 選択フラグ */
  private ALStringField checked;

  /**
   *
   */
  public void initField() {
    acl_id = new ALStringField();
    acl_name = new ALStringField();
    checked = new ALStringField();
  }

  /**
   * @return
   */
  public String getAclId() {
    return acl_id.getValue();
  }

  /**
   * @return
   */
  public String getAclName() {
    return acl_name.getValue();
  }

  /**
   * @return
   */
  public String getChecked() {
    return checked.getValue();
  }

  /**
   * @param i
   */
  public void setAclId(String string) {
    acl_id.setValue(string);
  }

  /**
   * @param string
   */
  public void setAclName(String string) {
    acl_name.setValue(string);
  }

  /**
   * @param string
   */
  public void setChecked(String string) {
    checked.setValue(string);
  }
}

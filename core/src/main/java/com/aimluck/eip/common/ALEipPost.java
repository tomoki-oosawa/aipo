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

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;

/**
 * 部署情報を表すクラスです。 <br />
 * 
 */
public class ALEipPost implements ALData {

  /** 部署ID */
  private ALNumberField post_id;

  /** 部署名 */
  private ALStringField post_name;

  /** グループ名 */
  private ALStringField group_name;

  /**
   *
   */
  public void initField() {
    post_id = new ALNumberField();
    post_name = new ALStringField();
    group_name = new ALStringField();
  }

  /**
   * @return
   */
  public ALNumberField getPostId() {
    return post_id;
  }

  /**
   * @return
   */
  public ALStringField getPostName() {
    return post_name;
  }

  /**
   * @param id
   */
  public void setPostId(int id) {
    post_id.setValue(id);
  }

  /**
   * @param string
   */
  public void setPostName(String string) {
    post_name.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getGroupName() {
    return group_name;
  }

  /**
   * @param string
   */
  public void setGroupName(String string) {
    group_name.setValue(string);
  }

}

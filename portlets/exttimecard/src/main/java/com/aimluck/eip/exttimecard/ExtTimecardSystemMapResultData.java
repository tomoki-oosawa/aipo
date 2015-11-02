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
package com.aimluck.eip.exttimecard;

import java.util.ArrayList;
import java.util.List;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.user.beans.UserGroupLiteBean;

/**
 * タイムカード集計の設定情報を保持する。
 * 
 * 
 */
public class ExtTimecardSystemMapResultData implements ALData {

  private ALNumberField system_map_id;

  private ALNumberField user_id;

  private ALStringField login_name;

  private ALNumberField system_id;

  private ALStringField name;

  private ALStringField system_name;

  private List<UserGroupLiteBean> post_name_list;

  /**
   * 
   * 
   */
  public void initField() {
    system_map_id = new ALNumberField();
    user_id = new ALNumberField();
    system_id = new ALNumberField();
    name = new ALStringField();
    login_name = new ALStringField();
    system_name = new ALStringField();
    post_name_list = new ArrayList<UserGroupLiteBean>();
  }

  public ALNumberField getSystemMapId() {
    return system_map_id;
  }

  public ALNumberField getUserId() {
    return user_id;
  }

  public ALNumberField getSystemId() {
    return system_id;
  }

  public ALStringField getName() {
    return name;
  }

  public ALStringField getSystemName() {
    return system_name;
  }

  public List<UserGroupLiteBean> getPostNameList() {
    return post_name_list;
  }

  public void setSystemMapId(int i) {
    system_map_id.setValue(i);
  }

  public void setUserId(int i) {
    user_id.setValue(i);
  }

  public void setSystemId(int i) {
    system_id.setValue(i);
  }

  public void setName(String str) {
    name.setValue(str);
  }

  public void setSystemName(String str) {
    system_name.setValue(str);
  }

  public void setPostNameList(List<UserGroupLiteBean> list) {
    post_name_list.addAll(list);
  }

  public ALStringField getLoginName() {
    return login_name;
  }

  public void setLoginName(String loginName) {
    login_name.setValue(loginName);
  }
}

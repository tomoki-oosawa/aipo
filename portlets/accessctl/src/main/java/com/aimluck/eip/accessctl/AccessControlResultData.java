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
package com.aimluck.eip.accessctl;

import java.util.ArrayList;
import java.util.List;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アクセスコントロールのResultDataです。 <BR>
 * 
 */
public class AccessControlResultData implements ALData {

  /** アクセスコントロールのロールID */
  private ALNumberField acl_role_id;

  /** アクセスコントロールのロール名 */
  private ALStringField acl_role_name;

  private ALStringField feature_name;

  /** アクセスコントロールのメモ */
  private ALStringField note;

  private boolean hasAclList;

  private boolean hasAclDetail;

  private boolean hasAclInsert;

  private boolean hasAclUpdate;

  private boolean hasAclDelete;

  private boolean hasAclExport;

  /** ユーザー名一覧 */
  private List<String> unamelist;

  /** 作成日 */
  private ALStringField create_date;

  /** 更新日 */
  private ALStringField update_date;

  /**
   *
   *
   */
  @Override
  public void initField() {
    acl_role_id = new ALNumberField();
    acl_role_name = new ALStringField();
    feature_name = new ALStringField();
    note = new ALStringField();
    unamelist = new ArrayList<String>();
    create_date = new ALStringField();
    update_date = new ALStringField();
  }

  /**
   * @return
   */
  public ALNumberField getAclRoleId() {
    return acl_role_id;
  }

  /**
   * @return
   */
  public String getAclRoleName() {
    return ALCommonUtils.replaceToAutoCR(acl_role_name.toString());
  }

  /**
   * @return
   */
  public ALStringField getFeatureName() {
    return feature_name;
  }

  /**
   * @return
   */
  public String getNote() {
    return ALEipUtils.getMessageList(note.getValue());
  }

  /**
   * @return
   */
  public boolean hasAclList() {
    return hasAclList;
  }

  /**
   * @return
   */
  public boolean hasAclDetail() {
    return hasAclDetail;
  }

  /**
   * @return
   */
  public boolean hasAclInsert() {
    return hasAclInsert;
  }

  /**
   * @return
   */
  public boolean hasAclUpdate() {
    return hasAclUpdate;
  }

  /**
   * @return
   */
  public boolean hasAclDelete() {
    return hasAclDelete;
  }

  /**
   * @return
   */
  public boolean hasAclExport() {
    return hasAclExport;
  }

  public List<String> getUserNameList() {
    return unamelist;
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
   * @param i
   */
  public void setAclRoleId(long i) {
    acl_role_id.setValue(i);
  }

  /**
   * @param string
   */
  public void setAclRoleName(String string) {
    acl_role_name.setValue(string);
  }

  /**
   * @param string
   */
  public void setFeatureName(String string) {
    feature_name.setValue(string);
  }

  /**
   * @param string
   */
  public void setNote(String string) {
    note.setValue(string);
  }

  /**
   * @param string
   */
  public void setAclList(boolean bool) {
    hasAclList = bool;
  }

  /**
   * @param string
   */
  public void setAclDetail(boolean bool) {
    hasAclDetail = bool;
  }

  /**
   * @param string
   */
  public void setAclInsert(boolean bool) {
    hasAclInsert = bool;
  }

  /**
   * @param string
   */
  public void setAclUpdate(boolean bool) {
    hasAclUpdate = bool;
  }

  /**
   * @param string
   */
  public void setAclDelete(boolean bool) {
    hasAclDelete = bool;
  }

  /**
   * @param string
   */
  public void setAclExport(boolean bool) {
    hasAclExport = bool;
  }

  public void addUserNameList(List<String> list) {
    unamelist.addAll(list);
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

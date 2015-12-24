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
package com.aimluck.eip.cabinet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 共有フォルダのフォルダ階層の情報（一行）を表すクラス．
 *
 */
public class FolderInfo implements Cloneable {

  private ArrayList<FolderInfo> colist = null;

  /** フォルダ階層のインデックス番号 */
  private int index = -1;

  /** フォルダ名 */
  private ALStringField folder_name = null;

  /** フォルダ ID */
  private int folder_id = -1;

  /** 親フォルダ ID */
  private int parent_folder_id = -1;

  private boolean visible = false;

  /** 権限的に可視であるか */
  private boolean authorized_visible = false;

  private boolean opened = false;

  /** 編集・削除を許可するか */
  private boolean can_update = true;

  /** 更新者の名前 */
  private ALEipUser update_name = null;

  /** 更新日 */
  private Date update_date;

  /** 更新者ID */
  private ALNumberField update_user_id = null;

  /**
   * コンストラクタ
   *
   * @param index
   * @param isRead
   * @param subject
   * @param from
   * @param date
   * @param fileVolume
   * @param fileName
   */
  public FolderInfo() {
    folder_name = new ALStringField();
    update_name = new ALEipUser();
    colist = new ArrayList<FolderInfo>();
    update_date = new Date();
    update_user_id = new ALNumberField();
  }

  public ALEipUser getUpdateName() {
    return update_name;
  }

  public ALNumberField getUpdateUserId() {
    return update_user_id;
  }

  /**
   * @return
   */
  public ALDateTimeField getUpdateDate() {
    return ALEipUtils.getFormattedTime(update_date);
  }

  /**
   * @param field
   */
  public void setUpdateDate(Date date) {
    if (date == null) {
      return;
    }
    update_date = date;
  }

  /**
   * @param update_name
   */
  public void setUpdateName(ALEipUser user) {
    update_name = user;
  }

  public void setUpdateUserId(long i) {
    update_user_id.setValue(i);
  }

  public void setList(List<FolderInfo> list) {
    colist.addAll(list);
  }

  public List<FolderInfo> getList() {
    return colist;
  }

  public int getHierarchyIndex() {
    return index;
  }

  public void setHierarchyIndex(int index) {
    this.index = index;
  }

  public void setHierarchyIndex(String str) {
    if (str == null || "".equals(str)) {
      index = -1;
    }
    try {
      index = Integer.parseInt(str);
    } catch (Exception e) {
      index = -1;
    }
  }

  public String getFolderName() {
    return folder_name.toString();
  }

  public boolean visible() {
    return visible;
  }

  public void setFolderName(String fname) {
    folder_name.setValue(fname);
  }

  public int getFolderId() {
    return folder_id;
  }

  public void setFolderId(int id) {
    folder_id = id;
  }

  public void setFolderId(String id) {
    if (id == null || "".equals(id)) {
      folder_id = -1;
    }
    try {
      folder_id = Integer.parseInt(id);
    } catch (Exception e) {
      folder_id = -1;
    }
  }

  public int getParentFolderId() {
    return parent_folder_id;
  }

  public void setParentFolderId(int id) {
    parent_folder_id = id;
  }

  public void setParentFolderId(String id) {
    if (id == null || "".equals(id)) {
      parent_folder_id = -1;
    }
    try {
      parent_folder_id = Integer.parseInt(id);
    } catch (Exception e) {
      parent_folder_id = -1;
    }
  }

  public void setVisible(boolean bool) {
    visible = bool;
  }

  public boolean isOpened() {
    return opened;
  }

  public void setOpened(boolean bool) {
    opened = bool;
  }

  public boolean canUpdate() {
    return can_update;
  }

  public void setCanUpdate(boolean bool) {
    can_update = bool;
  }

  @Override
  public Object clone() {
    try {
      return (super.clone());
    } catch (CloneNotSupportedException e) {
      throw (new InternalError(e.getMessage()));
    }
  }

  public boolean isAuthorizedVisible() {
    return authorized_visible;
  }

  public void setAuthorizedVisible(boolean authorized_visible) {
    this.authorized_visible = authorized_visible;
  }

}

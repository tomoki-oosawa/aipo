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
package com.aimluck.eip.modules.parameters;

import java.util.Iterator;
import java.util.List;

import org.apache.turbine.util.RunData;

import com.aimluck.eip.cabinet.FolderInfo;
import com.aimluck.eip.cabinet.util.CabinetUtils;

/**
 */
public class CabinetFolderListBox extends ListBox {

  public static final String INITIAL_VALUE = "initialvalue";

  // private String DEF_INITIAL_VALUE = "";

  /**
   * 共有フォルダの設定値を処理するクラスです。 <br />
   * 
   * @param data
   */
  @Override
  protected void init(RunData data) {
    // カテゴリ一覧を取得
    List<FolderInfo> folder_hierarchy_list = CabinetUtils.getFolderList();

    int length = folder_hierarchy_list.size();
    String[] folderKeys = new String[length];
    String[] folderValues = new String[length];

    int count = 0;

    // カテゴリの登録
    FolderInfo folderinfo = null;
    Iterator<FolderInfo> iter = folder_hierarchy_list.iterator();
    while (iter.hasNext()) {
      folderinfo = iter.next();

      StringBuffer nbsps = new StringBuffer();
      int len = folderinfo.getHierarchyIndex();
      for (int i = 0; i < len; i++) {
        nbsps.append("&nbsp;&nbsp;&nbsp;");
      }

      folderKeys[count] = "" + folderinfo.getFolderId();
      folderValues[count] = nbsps.toString() + folderinfo.getFolderName();
      count++;
    }

    this.layout = (String) this.getParm(LAYOUT, LAYOUT_COMBO);
    this.items = folderKeys;
    this.values = folderValues;
    this.size = Integer.toString(length);
    this.multiple =
      Boolean
        .valueOf((String) this.getParm(MULTIPLE_CHOICE, "false"))
        .booleanValue();

  }
}

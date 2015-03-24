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
package com.aimluck.eip.modules.parameters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.turbine.util.RunData;

import com.aimluck.eip.cabinet.FolderInfo;
import com.aimluck.eip.cabinet.util.CabinetUtils;

/**
 */
public class CabinetFolderListBox extends ListBox {

  public static final String INITIAL_VALUE = "initialvalue";

  public static final String DEF_INITIAL_VALUE = "すべてのフォルダ";

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

    List<String> folderKeys = new ArrayList<String>();
    List<String> folderValues = new ArrayList<String>();

    // ヘッダ
    folderKeys.add("0");
    folderValues.add((String) this.getParm(INITIAL_VALUE, DEF_INITIAL_VALUE));

    int count = 1;

    // カテゴリの登録
    FolderInfo folderinfo = null;
    Iterator<FolderInfo> iter = folder_hierarchy_list.iterator();
    while (iter.hasNext()) {
      folderinfo = iter.next();

      if (!CabinetUtils.isAccessibleFolder(folderinfo.getFolderId(), data)) {
        continue;
      }

      StringBuffer nbsps = new StringBuffer();
      int len = folderinfo.getHierarchyIndex();
      for (int i = 0; i < len; i++) {
        nbsps.append("&nbsp;&nbsp;&nbsp;");
      }

      folderKeys.add("" + folderinfo.getFolderId());
      folderValues.add(nbsps.toString() + folderinfo.getFolderName());
      count++;
    }

    this.layout = (String) this.getParm(LAYOUT, LAYOUT_COMBO);
    this.items = folderKeys.toArray(new String[count]);
    this.values = folderValues.toArray(new String[count]);
    this.size = Integer.toString(count);
    this.multiple =
      Boolean
        .valueOf((String) this.getParm(MULTIPLE_CHOICE, "false"))
        .booleanValue();

  }
}

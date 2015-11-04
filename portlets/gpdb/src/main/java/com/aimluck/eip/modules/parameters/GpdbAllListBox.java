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
package com.aimluck.eip.modules.parameters;

import java.util.Iterator;
import java.util.List;

import org.apache.turbine.util.RunData;

import com.aimluck.eip.gpdb.GpdbResultData;
import com.aimluck.eip.gpdb.util.GpdbUtils;

/**
 * Webデータベースの設定値を処理するクラスです。
 */
public class GpdbAllListBox extends ListBox {

  public static final String INITIAL_VALUE = "initialvalue";

  private static final String DEF_INITIAL_VALUE = "";

  /**
   * Initialize options
   * 
   * @param data
   */
  @Override
  protected void init(RunData data) {
    // 選択項目一覧を取得
    List<GpdbResultData> gpdbAllList = GpdbUtils.getGpdbAllList();
    int length = 1 + gpdbAllList.size();
    String[] categoryKeys = new String[length];
    String[] categoryValues = new String[length];

    categoryKeys[0] = "";
    categoryValues[0] = (String) this.getParm(INITIAL_VALUE, DEF_INITIAL_VALUE);
    int count = 1;

    // 選択項目の登録
    GpdbResultData gpdbData = null;
    Iterator<GpdbResultData> iter = gpdbAllList.iterator();
    while (iter.hasNext()) {
      gpdbData = iter.next();
      categoryKeys[count] = gpdbData.getGpdbId().toString();
      categoryValues[count] = gpdbData.getGpdbName().toString();
      count++;
    }

    this.layout = (String) this.getParm(LAYOUT, LAYOUT_COMBO);
    this.items = categoryKeys;
    this.values = categoryValues;
    this.size = Integer.toString(length);
    this.multiple =
      Boolean
        .valueOf((String) this.getParm(MULTIPLE_CHOICE, "false"))
        .booleanValue();

  }
}

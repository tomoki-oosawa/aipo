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
package com.aimluck.eip.userlist.utils;

import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.util.ALEipUtils;

public class UserListUtils {

  public static final String USERLIST_PORTLET_NAME = "UserList";

  private static final String SEARCH_WORD = "sword";

  private static String prevFilter = null;

  /**
   * 検索クエリ用のキーワードを取得します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static String getKeyword(RunData rundata, Context context) {
    String keyword = null;
    String keywordParm = rundata.getParameters().getString(SEARCH_WORD);
    keyword = ALEipUtils.getTemp(rundata, context, SEARCH_WORD);
    if (keywordParm == null && (keyword == null)) {
      ALEipUtils.setTemp(rundata, context, SEARCH_WORD, "");
      keyword = "";
    } else if (keywordParm != null) {
      keywordParm = keywordParm.trim();
      ALEipUtils.setTemp(rundata, context, SEARCH_WORD, keywordParm);
      keyword = keywordParm;
    }

    // 部署フィルタが変更されていれば、検索キーワードを削除する
    String currentFilter = rundata.getParameters().get("filter");
    if (currentFilter == null) {
      if (prevFilter != null) {
        ALEipUtils.setTemp(rundata, context, SEARCH_WORD, "");
        keyword = "";
      }
    } else {
      if (prevFilter == null || currentFilter.equals(prevFilter)) {
        ALEipUtils.setTemp(rundata, context, SEARCH_WORD, "");
        keyword = "";
      }
    }

    return keyword;
  }
}

/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

package com.aimluck.eip.wiki.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.portlet.EipTWikiCategory;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.wiki.WikiCategoryResultData;

/**
 * Wikiのユーティリティクラスです。 <BR>
 * 
 */
public class WikiCategoryUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WikiCategoryUtils.class.getName());

  public static EipTWikiCategory getWikiCategory(Integer pk) {
    return Database.get(EipTWikiCategory.class, pk);
  }

  public static List<WikiCategoryResultData> loadCategoryList(RunData rundata) {
    // カテゴリ一覧
    List<WikiCategoryResultData> categoryList =
      new ArrayList<WikiCategoryResultData>();
    try {
      SelectQuery<EipTWikiCategory> query =
        Database.query(EipTWikiCategory.class);
      query.orderAscending(EipTWikiCategory.CATEGORY_NAME_PROPERTY);

      WikiCategoryResultData otherRd = null;

      List<EipTWikiCategory> result = query.fetchList();
      for (EipTWikiCategory category : result) {
        WikiCategoryResultData rd = new WikiCategoryResultData();
        rd.initField();
        rd.setCategoryId(category.getCategoryId().longValue());
        rd.setCategoryName(category.getCategoryName());
        if (category.getCategoryId().longValue() == 1) {
          // カテゴリ「未分類」は最後に追加するため，ここではリストに追加しない．
          otherRd = rd;
        } else {
          categoryList.add(rd);
        }
      }
      if (otherRd != null) {
        categoryList.add(otherRd);
      }
    } catch (Exception e) {
      logger.error("WikiUtils.loadCategoryList", e);
    }
    return categoryList;
  }
}

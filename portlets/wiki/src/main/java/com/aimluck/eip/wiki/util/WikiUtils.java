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

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTWiki;
import com.aimluck.eip.cayenne.om.portlet.EipTWikiCategory;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.wiki.WikiCategoryResultData;

/**
 * Wikiのユーティリティクラスです。 <BR>
 * 
 */
public class WikiUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WikiUtils.class.getName());

  public static final String WIKI_PORTLET_NAME = "Wiki";

  public static EipTWiki getEipTWiki(RunData rundata, Context context,
      boolean isJoin) throws ALPageNotFoundException, ALDBErrorException {
    String id = ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (id == null || Integer.valueOf(id) == null) {
        // wikiID が空の場合
        logger.debug("[Wiki] Empty ID...");
        throw new ALPageNotFoundException();
      }

      int userid = ALEipUtils.getUserId(rundata);

      SelectQuery<EipTWiki> query = Database.query(EipTWiki.class);
      Expression exp1 =
        ExpressionFactory.matchDbExp(EipTWiki.WIKI_ID_PK_COLUMN, Integer
          .valueOf(id));
      query.setQualifier(exp1);
      query.distinct(true);

      List<EipTWiki> wikis = query.fetchList();
      if (wikis == null || wikis.size() == 0) {
        // 指定した トピック ID のレコードが見つからない場合
        logger.debug("[Wiki] Not found ID...");
        throw new ALPageNotFoundException();
      }

      EipTWiki wiki = wikis.get(0);

      return wiki;
    } catch (ALPageNotFoundException pageNotFound) {
      // logger.error("[MsgboardUtils]", pageNotFound);
      throw pageNotFound;
    } catch (Exception ex) {
      logger.error("[WikiUtils]", ex);
      throw new ALDBErrorException();
    }
  }

  public static List<WikiCategoryResultData> loadCategoryList(RunData rundata) {
    // カテゴリ一覧
    List<WikiCategoryResultData> categoryList =
      new ArrayList<WikiCategoryResultData>();
    try {
      SelectQuery<EipTWikiCategory> query =
        Database.query(EipTWikiCategory.class);

      query.distinct(true);

      WikiCategoryResultData otherRd = null;

      List<EipTWikiCategory> aList = query.fetchList();
      int size = aList.size();
      for (int i = 0; i < size; i++) {
        EipTWikiCategory record = aList.get(i);
        WikiCategoryResultData rd = new WikiCategoryResultData();
        rd.initField();
        rd.setCategoryId(record.getCategoryId().longValue());
        rd.setCategoryName(record.getCategoryName());
        if (record.getCategoryId().longValue() == 1) {
          // カテゴリ「その他」は最後に追加するため，ここではリストに追加しない．
          otherRd = rd;
        } else {
          categoryList.add(rd);
        }
      }
      if (otherRd != null) {
        categoryList.add(otherRd);
      }
    } catch (Exception ex) {
      logger.error("msgboard", ex);
      return null;
    }
    return categoryList;
  }

}

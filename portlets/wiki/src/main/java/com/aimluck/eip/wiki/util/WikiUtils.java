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

import com.aimluck.eip.cayenne.om.portlet.EipTWikiCategory;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
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

  /**
   * プルダウン用のカテゴリーリストを返します
   * 
   * @param rundata
   * @return
   */
  public static ArrayList<WikiCategoryResultData> getCategoryList(
      RunData rundata) {
    ArrayList<WikiCategoryResultData> categoryList =
      new ArrayList<WikiCategoryResultData>();

    WikiCategoryResultData rd;

    try {
      // カテゴリ一覧
      List<EipTWikiCategory> categoryList2 =
        Database.query(EipTWikiCategory.class).orderAscending(
          EipTWikiCategory.CATEGORY_NAME_PROPERTY).fetchList();

      StringBuffer title;
      ALEipUser user;
      for (EipTWikiCategory record : categoryList2) {
        user = ALEipUtils.getALEipUser(record.getUserId());
        // exclude 「その他」
        if (user != null) {
          rd = new WikiCategoryResultData();
          rd.initField();
          rd.setCategoryId(record.getCategoryId().longValue());
          title = new StringBuffer(record.getCategoryName());
          // title.append(" （");
          // title.append(user.getAliasName());
          // title.append("）");
          rd.setCategoryName(title.toString());
          categoryList.add(rd);
        }
      }
    } catch (Exception ex) {
      logger.error("wiki", ex);
    }

    // その他追加
    EipTWikiCategory unCategorized =
      Database.query(EipTWikiCategory.class).where(
        Operations.eq(EipTWikiCategory.TURBINE_USER_PROPERTY, 0)).fetchSingle();
    rd = new WikiCategoryResultData();
    rd.initField();
    rd.setCategoryId(unCategorized.getCategoryId());
    rd.setCategoryName(unCategorized.getCategoryName());
    categoryList.add(rd);

    return categoryList;
  }

  /**
   * Wikiカテゴリ オブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipTWikiCategory getEipTWikiCategory(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    String categoryid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);

    if (categoryid == null || Integer.valueOf(categoryid) == null) {
      // カテゴリIDが空の場合
      logger.debug("[Wiki] Empty ID...");
      throw new ALPageNotFoundException();
    }

    try {
      Expression exp1 =
        ExpressionFactory.matchDbExp(
          EipTWikiCategory.CATEGORY_ID_PK_COLUMN,
          categoryid);

      List<EipTWikiCategory> categoryList =
        Database.query(EipTWikiCategory.class, exp1).fetchList();

      if (categoryList == null || categoryList.size() == 0) {
        // 指定したカテゴリIDのレコードが見つからない場合
        logger.debug("[Wiki] Not found ID...");
        throw new ALPageNotFoundException();
      }

      return categoryList.get(0);
    } catch (Exception ex) {
      logger.error("wiki", ex);
      throw new ALDBErrorException();
    }
  }

  /**
   * Wikiカテゴリ オブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipTWikiCategory getEipTWikiCategory(Long category_id) {
    try {
      return Database.get(EipTWikiCategory.class, category_id);
    } catch (Exception ex) {
      logger.error("wiki", ex);
      return null;
    }
  }
}

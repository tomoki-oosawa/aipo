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

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.commons.lang.StringUtils;
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

/**
 * Wikiのユーティリティクラスです。 <BR>
 * 
 */
public class WikiUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WikiUtils.class.getName());

  /** グループによる表示切り替え用変数の識別子 */
  public static final String TARGET_GROUP_NAME = "target_group_name";

  /** ユーザによる表示切り替え用変数の識別子 */
  public static final String TARGET_USER_ID = "target_user_id";

  /** 検索キーワード変数の識別子 */
  public static final String TARGET_KEYWORD = "keyword";

  /** パラメータリセットの識別子 */
  private static final String RESET_KEYWORD_FLAG = "reset_keyword_params";

  /** パラメータリセットの識別子 */
  private static final String RESET_TARGET_FLAG = "reset_target_params";

  /** パラメータリセットの識別子 */
  private static final String RESET_FLAG = "reset_params";

  /** 期限状態（期限前） */
  public static final int LIMIT_STATE_BEFORE = -1;

  /** 期限状態（期限当日） */
  public static final int LIMIT_STATE_TODAY = 0;

  /** 期限状態（期限後） */
  public static final int LIMIT_STATE_AFTER = 1;

  public static final String WIKI_PORTLET_NAME = "Wiki";

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

  /**
   * 表示切り替えで指定したグループ ID を取得する．
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static String getTargetGroupName(RunData rundata, Context context) {
    String target_group_name = null;
    String idParam = rundata.getParameters().getString(TARGET_GROUP_NAME);
    target_group_name = ALEipUtils.getTemp(rundata, context, TARGET_GROUP_NAME);
    if (idParam == null && target_group_name == null) {
      ALEipUtils.setTemp(rundata, context, TARGET_GROUP_NAME, "all");
      target_group_name = "all";
    } else if (idParam != null) {
      ALEipUtils.setTemp(rundata, context, TARGET_GROUP_NAME, idParam);
      target_group_name = idParam;
    }
    return target_group_name;
  }

  /**
   * 表示切り替えで指定したユーザ ID を取得する．
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static String getTargetUserId(RunData rundata, Context context) {
    String target_user_id = null;
    String idParam = rundata.getParameters().getString(TARGET_USER_ID);
    target_user_id = ALEipUtils.getTemp(rundata, context, TARGET_USER_ID);

    if (idParam == null && (target_user_id == null)) {
      ALEipUtils.setTemp(rundata, context, TARGET_USER_ID, "all");
      target_user_id = "all";
    } else if (idParam != null) {
      ALEipUtils.setTemp(rundata, context, TARGET_USER_ID, idParam);
      target_user_id = idParam;
    }
    return target_user_id;
  }

  public static EipTWiki getEipTWiki(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    String id = ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (StringUtils.isEmpty(id)) {
        throw new ALPageNotFoundException();
      }
      return Database.get(EipTWiki.class, Integer.valueOf(id));
    } catch (ALPageNotFoundException pageNotFound) {
      throw pageNotFound;
    } catch (Exception e) {
      logger.error("[WikiUtils]", e);
      throw new ALDBErrorException();
    }
  }

  public static boolean isTitleDuplicate(String title) {
    return isTitleDuplicate(title, null);
  }

  public static boolean isTitleDuplicate(String title, Integer pk) {
    SelectQuery<EipTWiki> query = Database.query(EipTWiki.class);
    Expression titleExp =
      ExpressionFactory.matchExp(EipTWiki.WIKI_NAME_PROPERTY, title);
    query.andQualifier(titleExp);
    if (pk != null) {
      Expression pkExp =
        ExpressionFactory.noMatchDbExp(EipTWiki.WIKI_ID_PK_COLUMN, pk);
      query.andQualifier(pkExp);
    }
    int result = query.getCount();
    return result != 0;
  }

}

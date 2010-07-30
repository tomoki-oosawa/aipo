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
package com.aimluck.eip.msgboard;

import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardCategory;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 掲示板カテゴリの複数削除を行うためのクラスです。 <BR>
 *
 */
public class MsgboardCategoryMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(MsgboardCategoryMultiDelete.class.getName());

  /**
   *
   * @param rundata
   * @param context
   * @param values
   * @param msgList
   * @return
   * @see com.aimluck.eip.common.ALAbstractCheckList#action(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList,
   *      java.util.ArrayList)
   */
  protected boolean action(RunData rundata, Context context,
      List<String> values, List<String> msgList) {
    try {
      // カテゴリを削除
      // DBテーブルのカスケード設定で，
      // トピックおよび添付ファイルも自動的に削除される．
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      SelectQuery query = getSelectQuery(rundata, values);
      List<?> list = dataContext.performQuery(query);
      if (list == null || list.size() == 0) {
        // トピックリストが空の場合
        logger.debug("[MsgboardMultiDelete] Empty TopicIDs...");
        return false;
      }

      int size = list.size();
      for (int i = 0; i < size; i++) {
        EipTMsgboardCategory category = (EipTMsgboardCategory) list.get(i);
        dataContext.deleteObject(category);

        // イベントログに保存
        ALEventlogFactoryService
            .getInstance()
            .getEventlogHandler()
            .log(category.getCategoryId(),
                ALEventlogConstants.PORTLET_TYPE_MSGBOARD_CATEGORY,
                category.getCategoryName());
      }
      dataContext.commitChanges();

      // 一覧表示画面のフィルタに設定されているカテゴリのセッション情報を削除
      String filtername = MsgboardTopicSelectData.class.getName()
          + ALEipConstants.LIST_FILTER;
      ALEipUtils.removeTemp(rundata, context, filtername);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery getSelectQuery(RunData rundata, List<String> values) {
    SelectQuery query = new SelectQuery(EipTMsgboardCategory.class);
    Expression exp = ExpressionFactory.inDbExp(
        EipTMsgboardCategory.CATEGORY_ID_PK_COLUMN, values);
    query.setQualifier(exp);
    return query;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限を返します。
   *
   * @return
   */
  protected int getDefineAclType() {
    return ALAccessControlConstants.VALUE_ACL_DELETE;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   *
   * @return
   */
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_MSGBOARD_CATEGORY;
  }
}

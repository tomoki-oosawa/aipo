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
package com.aimluck.eip.todo;

import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTTodoCategory;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALDataContext;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ToDoカテゴリの複数削除を行うためのクラスです。 <BR>
 *
 */
public class ToDoCategoryMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(ToDoCategoryMultiDelete.class.getName());

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
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();

      SelectQuery<EipTTodoCategory> query = new SelectQuery<EipTTodoCategory>(EipTTodoCategory.class);
      Expression exp1 = ExpressionFactory.matchDbExp(
          TurbineUser.USER_ID_PK_COLUMN,
          Integer.valueOf(ALEipUtils.getUserId(rundata)));
      query.setQualifier(exp1);
      Expression exp2 = ExpressionFactory.inDbExp(
          EipTTodoCategory.CATEGORY_ID_PK_COLUMN, values);
      query.andQualifier(exp2);

      List<EipTTodoCategory> categorylist = ALDataContext.performQuery(query);
      if (categorylist == null || categorylist.size() == 0)
        return false;

      int categorylistsize = categorylist.size();

      // カテゴリを削除
      for (int i = 0; i < categorylistsize; i++) {
        EipTTodoCategory category = categorylist.get(i);

        // entityIdを取得
        Integer entityId = category.getCategoryId();
        // カテゴリ名を取得
        String categoryName = category.getCategoryName();

        // Todoカテゴリを削除
        dataContext.deleteObject(category);
        dataContext.commitChanges();

        // ログに保存
        ALEventlogFactoryService
            .getInstance()
            .getEventlogHandler()
            .log(entityId, ALEventlogConstants.PORTLET_TYPE_TODO_CATEGORY,
                categoryName);
      }
      // 一覧表示画面のフィルタに設定されているカテゴリのセッション情報を削除
      String filtername = ToDoSelectData.class.getName()
          + ALEipConstants.LIST_FILTER;
      ALEipUtils.removeTemp(rundata, context, filtername);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
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
    return ALAccessControlConstants.POERTLET_FEATURE_TODO_CATEGORY_SELF;
  }
}

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
package com.aimluck.eip.todo;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTTodoCategory;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ToDoカテゴリの複数削除を行うためのクラスです。 <BR>
 *
 */
public class ToDoCategoryMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ToDoCategoryMultiDelete.class.getName());

  private String aclPortletFeature;

  /**
   * @param rundata
   * @param context
   * @param defineAclType
   * @return
   * @throws ALPermissionException
   */
  @Override
  protected boolean doCheckAclPermission(RunData rundata, Context context,
      int defineAclType) throws ALPermissionException {
    List<Integer> values = new ArrayList<Integer>();
    Object[] objs = rundata.getParameters().getKeys();
    int length = objs.length;
    for (int i = 0; i < length; i++) {
      if (objs[i].toString().startsWith("check")) {
        String str = rundata.getParameters().getString(objs[i].toString());
        values.add(Integer.valueOf(str));
      }
    }

    Expression exp1 =
      ExpressionFactory.noMatchExp(EipTTodoCategory.USER_ID_PROPERTY, Integer
        .valueOf(ALEipUtils.getUserId(rundata)));
    Expression exp2 =
      ExpressionFactory.inDbExp(EipTTodoCategory.CATEGORY_ID_PK_COLUMN, values);
    if (Database
      .query(EipTTodoCategory.class)
      .andQualifier(exp1.andExp(exp2))
      .getCount() > 0) {
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_TODO_CATEGORY_OTHER;
    } else {
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_TODO_CATEGORY_SELF;
    }
    return super.doCheckAclPermission(rundata, context, defineAclType);
  }

  /**
   *
   * @param rundata
   * @param context
   * @param values
   * @param msgList
   * @return
   */
  @Override
  protected boolean action(RunData rundata, Context context,
      List<String> values, List<String> msgList) {
    try {
      Expression exp1 =
        ExpressionFactory.inDbExp(
          EipTTodoCategory.CATEGORY_ID_PK_COLUMN,
          values);

      List<EipTTodoCategory> categoryList =
        Database.query(EipTTodoCategory.class, exp1).fetchList();
      if (categoryList == null || categoryList.size() == 0) {
        return false;
      }

      for (EipTTodoCategory category : categoryList) {
        if (category.getEipTTodoArray().size() > 0) {
          msgList.add("1つ以上のToDoを含むカテゴリを削除することはできません。");
          return false;
        }
      }

      // カテゴリを削除
      for (EipTTodoCategory category : categoryList) {

        // entityIdを取得
        Integer entityId = category.getCategoryId();
        // カテゴリ名を取得
        String categoryName = category.getCategoryName();

        // Todoカテゴリを削除
        Database.delete(category);
        Database.commit();

        // ログに保存
        ALEventlogFactoryService.getInstance().getEventlogHandler().log(
          entityId,
          ALEventlogConstants.PORTLET_TYPE_TODO_CATEGORY,
          categoryName);
      }

      // 一覧表示画面のフィルタに設定されているカテゴリのセッション情報を削除
      String filtername =
        ToDoSelectData.class.getSimpleName() + ALEipConstants.LIST_FILTER;
      ALEipUtils.removeTemp(rundata, context, filtername);

    } catch (Throwable t) {
      Database.rollback();
      logger.error("[ToDoCategoryMultiDelete]", t);
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
  @Override
  protected int getDefineAclType() {
    return ALAccessControlConstants.VALUE_ACL_DELETE;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   *
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return aclPortletFeature;
  }
}

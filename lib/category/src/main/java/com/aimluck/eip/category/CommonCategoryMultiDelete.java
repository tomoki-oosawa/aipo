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
package com.aimluck.eip.category;

import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.category.util.CommonCategoryUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTCommonCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 複数の共有カテゴリを削除するクラスです。 <br />
 * 
 */
public class CommonCategoryMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CommonCategoryMultiDelete.class.getName());

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
  @Override
  protected boolean action(RunData rundata, Context context,
      List<String> values, List<String> msgList) {
    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
        .getDataContext();

      // アクセス権限
      int loginuserid = ALEipUtils.getUserId(rundata);

      ALAccessControlFactoryService aclservice = (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
      ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

      boolean hasAuthorityOtherDelete = aclhandler
        .hasAuthority(
          loginuserid,
          ALAccessControlConstants.POERTLET_FEATURE_MANHOUR_COMMON_CATEGORY_OTHER,
          ALAccessControlConstants.VALUE_ACL_DELETE);

      // 共有カテゴリリストを取得
      SelectQuery query = new SelectQuery(EipTCommonCategory.class);
      if (!hasAuthorityOtherDelete) {
        Expression exp1 = ExpressionFactory.matchExp(
          EipTCommonCategory.CREATE_USER_ID_PROPERTY,
          Integer.valueOf(loginuserid));
        query.setQualifier(exp1);
      }
      Expression exp2 = ExpressionFactory.inDbExp(
        EipTCommonCategory.COMMON_CATEGORY_ID_PK_COLUMN, values);
      query.andQualifier(exp2);

      List<?> categorylist = dataContext.performQuery(query);
      if (categorylist == null || categorylist.size() == 0) {
        return false;
      }

      // 共有カテゴリ「未分類」のオブジェクトを取得
      EipTCommonCategory tmpCategory = CommonCategoryUtils
        .getEipTCommonCategory(dataContext, Long.valueOf(1));

      // 共有カテゴリ内の ScheduleMap は「未分類」にカテゴリ変更する
      EipTScheduleMap record = null;
      EipTCommonCategory category = null;
      int size = categorylist.size();
      for (int i = 0; i < size; i++) {
        category = (EipTCommonCategory) categorylist.get(i);
        List<?> result = ALEipUtils.getObjectModels(dataContext,
          EipTScheduleMap.class, EipTScheduleMap.COMMON_CATEGORY_ID_PROPERTY,
          category.getCommonCategoryId(), false);
        if (result != null && result.size() > 0) {
          int size2 = result.size();
          for (int j = 0; j < size2; j++) {
            record = (EipTScheduleMap) result.get(j);
            record.setEipTCommonCategory(tmpCategory);
          }
        }
      }

      // カテゴリを削除
      dataContext.deleteObjects(categorylist);
      dataContext.commitChanges();

      // 一覧表示画面のフィルタに設定されているカテゴリのセッション情報を削除
      String filtername = CommonCategorySelectData.class.getName()
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
    return ALAccessControlConstants.POERTLET_FEATURE_MANHOUR_COMMON_CATEGORY;
  }
}

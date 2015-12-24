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
package com.aimluck.eip.category;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.category.util.CommonCategoryUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTCommonCategory;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
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
   */
  @Override
  protected boolean action(RunData rundata, Context context,
      List<String> values, List<String> msgList) {
    try {
      // アクセス権限
      int loginuserid = ALEipUtils.getUserId(rundata);

      ALAccessControlFactoryService aclservice =
        (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
          .getInstance())
          .getService(ALAccessControlFactoryService.SERVICE_NAME);
      ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

      boolean hasAuthorityOtherDelete =
        aclhandler
          .hasAuthority(
            loginuserid,
            ALAccessControlConstants.POERTLET_FEATURE_MANHOUR_COMMON_CATEGORY_OTHER,
            ALAccessControlConstants.VALUE_ACL_DELETE);

      // 共有カテゴリリストを取得
      SelectQuery<EipTCommonCategory> query =
        Database.query(EipTCommonCategory.class);
      if (!hasAuthorityOtherDelete) {
        Expression exp1 =
          ExpressionFactory.matchExp(
            EipTCommonCategory.CREATE_USER_ID_PROPERTY,
            Integer.valueOf(loginuserid));
        query.andQualifier(exp1);
      }
      Expression exp2 =
        ExpressionFactory.inDbExp(
          EipTCommonCategory.COMMON_CATEGORY_ID_PK_COLUMN,
          values);
      query.andQualifier(exp2);

      List<EipTCommonCategory> commoncategory_list = query.fetchList();
      if (commoncategory_list == null || commoncategory_list.size() == 0) {
        return false;
      }

      // 共有カテゴリ内の ScheduleMap は「その他」にカテゴリ変更する
      for (EipTCommonCategory record : commoncategory_list) {
        CommonCategoryUtils.setDefaultCommonCategoryToSchedule(record);
      }

      // カテゴリを削除
      Database.deleteAll(commoncategory_list);
      Database.commit();

      rundata.getParameters().add(
        ALEipConstants.MODE,
        ALEipConstants.MODE_MULTI_DELETE);

      for (EipTCommonCategory record : commoncategory_list) {
        // イベントログに保存
        ALEventlogFactoryService.getInstance().getEventlogHandler().log(
          record.getCommonCategoryId(),
          ALEventlogConstants.PORTLET_TYPE_COMMON_CATEGORY,
          record.getName());
      }

      // 一覧表示画面のフィルタに設定されているカテゴリのセッション情報を削除
      String filtername =
        CommonCategorySelectData.class.getName() + ALEipConstants.LIST_FILTER;
      ALEipUtils.removeTemp(rundata, context, filtername);
    } catch (Throwable t) {
      Database.rollback();
      logger.error("CommonCategoryMultiDelete.action", t);
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

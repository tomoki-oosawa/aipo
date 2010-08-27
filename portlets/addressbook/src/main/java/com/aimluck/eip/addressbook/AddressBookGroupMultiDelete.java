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
package com.aimluck.eip.addressbook;

import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipMAddressGroup;
import com.aimluck.eip.cayenne.om.portlet.EipTAddressbookGroupMap;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アドレスグループの複数削除を行うためのクラスです。
 * 
 */
public class AddressBookGroupMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AddressBookGroupMultiDelete.class.getName());

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
      // address-groupテーブルのデータを削除
      DataContext dataContext =
        DatabaseOrmService.getInstance().getDataContext();
      SelectQuery query = new SelectQuery(EipMAddressGroup.class);
      Expression exp1 =
        ExpressionFactory.matchExp(EipMAddressGroup.OWNER_ID_PROPERTY, Integer
          .valueOf(ALEipUtils.getUserId(rundata)));
      query.setQualifier(exp1);
      Expression exp2 =
        ExpressionFactory.inDbExp(EipMAddressGroup.GROUP_ID_PK_COLUMN, values);
      query.andQualifier(exp2);

      @SuppressWarnings("unchecked")
      List<EipMAddressGroup> groups = dataContext.performQuery(query);

      int grouplistsize = groups.size();

      // 会社情報を削除
      for (int i = 0; i < grouplistsize; i++) {
        EipMAddressGroup group = groups.get(i);

        // entityIdを取得
        Integer entityId = group.getGroupId();
        // グループ名を取得
        String groupName = group.getGroupName();

        // グループ情報を削除
        dataContext.deleteObject(group);
        dataContext.commitChanges();

        // ログに保存
        ALEventlogFactoryService.getInstance().getEventlogHandler().log(
          entityId,
          ALEventlogConstants.PORTLET_TYPE_ADDRESSBOOK_GROUP,
          groupName);
      }

      // Address group Mapテーブルデータの削除
      SelectQuery mapquery = new SelectQuery(EipTAddressbookGroupMap.class);
      Expression mapexp =
        ExpressionFactory.matchDbExp(
          EipMAddressGroup.GROUP_ID_PK_COLUMN,
          Integer.valueOf(ALEipUtils.getUserId(rundata)));
      mapquery.setQualifier(mapexp);

      @SuppressWarnings("unchecked")
      List<EipTAddressbookGroupMap> maps = dataContext.performQuery(mapquery);
      dataContext.deleteObjects(maps);

      dataContext.commitChanges();

      // 検索画面用フィルタにて設定されているグループフィルタをセッションから削除する。
      String filtername =
        AddressBookFilterdSelectData.class.getName()
          + ALEipConstants.LIST_FILTER;
      ALEipUtils.removeTemp(rundata, context, filtername);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * アクセス権限チェック用メソッド。 アクセス権限を返します。
   * 
   * @return
   */
  @Override
  protected int getDefineAclType() {
    return ALAccessControlConstants.VALUE_ACL_DELETE;
  }

  /**
   * アクセス権限チェック用メソッド。 アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_ADDRESSBOOK_COMPANY_GROUP;
  }
}

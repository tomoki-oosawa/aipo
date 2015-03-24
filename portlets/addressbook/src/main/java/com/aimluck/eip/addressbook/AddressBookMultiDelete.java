/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2015 Aimluck,Inc.
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
package com.aimluck.eip.addressbook;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipMAddressbook;
import com.aimluck.eip.cayenne.om.portlet.EipTAddressbookGroupMap;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;

/**
 * アドレス帳の複数データ削除クラスです。
 * 
 */
public class AddressBookMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AddressBookMultiDelete.class.getName());

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
      if (values.size() == 0) {
        return false;
      }
      SelectQuery<EipMAddressbook> query =
        Database.query(EipMAddressbook.class);
      Expression exp =
        ExpressionFactory.inDbExp(EipMAddressbook.ADDRESS_ID_PK_COLUMN, values);
      query.setQualifier(exp);

      List<EipMAddressbook> addresses = query.fetchList();
      for (EipMAddressbook record : addresses) {
        Integer entityId = record.getAddressId();
        StringBuilder name = new StringBuilder();
        name.append(record.getFirstName());
        name.append(" ");
        name.append(record.getLastName());
        Database.delete(record);
        // mapから削除
        SelectQuery<EipTAddressbookGroupMap> query2 =
          Database.query(EipTAddressbookGroupMap.class);
        Expression exp2 =
          ExpressionFactory.matchExp(
            EipTAddressbookGroupMap.ADDRESS_ID_PROPERTY,
            Integer.valueOf(entityId));
        query2.setQualifier(exp2);

        List<EipTAddressbookGroupMap> maps = query2.fetchList();
        Database.deleteAll(maps);
        // ログに保存
        ALEventlogFactoryService.getInstance().getEventlogHandler().log(
          entityId,
          ALEventlogConstants.PORTLET_TYPE_ADDRESSBOOK_COMPANY,
          name.toString());
      }

      Database.commit();
    } catch (Exception ex) {
      Database.rollback();
      logger.error("AddressBookMultiDelete.action", ex);
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
    return ALAccessControlConstants.POERTLET_FEATURE_ADDRESSBOOK_ADDRESS_OUTSIDE;
  }
}

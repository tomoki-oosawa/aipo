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

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.addressbook.util.AddressBookUtils;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbook;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbookCompany;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;

/**
 * アドレス帳会社情報の複数データ削除クラスです。
 *
 */
public class AddressBookCompanyMultiDelete extends ALAbstractCheckList {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(AddressBookCompanyMultiDelete.class.getName());

  /**
   * @see com.aimluck.eip.common.ALAbstractCheckList#action(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList,
   *      java.util.ArrayList)
   */
  protected boolean action(RunData rundata, Context context,
      List<String> values, List<String> msgList) {
    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();

      // アドレス情報の中で削除対象会社に所属しているものの会社IDを（未分類）のものとする
      int empty_id = AddressBookUtils
          .getDummyEipMAddressbookCompany(rundata, context).getCompanyId()
          .intValue();

      SelectQuery addrquery = new SelectQuery(EipMAddressbook.class);
      Expression addrexp = ExpressionFactory.inDbExp(
          EipMAddressbook.EIP_MADDRESSBOOK_COMPANY_PROPERTY + "."
              + EipMAddressbookCompany.COMPANY_ID_PK_COLUMN, values);
      addrquery.setQualifier(addrexp);

      @SuppressWarnings("unchecked")
      List<EipMAddressbook> addresses = dataContext.performQuery(addrquery);

      if (addresses != null && addresses.size() > 0) {
        EipMAddressbook addressbook = null;

        EipMAddressbookCompany company = (EipMAddressbookCompany) DataObjectUtils
            .objectForPK(dataContext, EipMAddressbookCompany.class,
                Integer.valueOf(empty_id));

        int addrsize = addresses.size();
        for (int i = 0; i < addrsize; i++) {
          addressbook = (EipMAddressbook) addresses.get(i);
          addressbook.setEipMAddressbookCompany(company);
        }
      }

      // address-groupテーブルのデータを削除
      SelectQuery query = new SelectQuery(EipMAddressbookCompany.class);
      Expression exp = ExpressionFactory.inDbExp(
          EipMAddressbookCompany.COMPANY_ID_PK_COLUMN, values);
      query.setQualifier(exp);

      @SuppressWarnings("unchecked")
      List<EipMAddressbookCompany> groups = dataContext.performQuery(query);

      int grouplistsize = groups.size();

      // 会社情報を削除
      for (int i = 0; i < grouplistsize; i++) {
        EipMAddressbookCompany group = groups.get(i);

        // entityIdを取得
        Integer entityId = group.getCompanyId();
        // 会社名を取得
        String groupName = group.getCompanyName();

        // 会社情報を削除
        dataContext.deleteObject(group);

        // ログに保存
        ALEventlogFactoryService
            .getInstance()
            .getEventlogHandler()
            .log(entityId,
                ALEventlogConstants.PORTLET_TYPE_ADDRESSBOOK_COMPANY, groupName);
      }

      dataContext.commitChanges();
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
    return ALAccessControlConstants.POERTLET_FEATURE_ADDRESSBOOK_COMPANY;
  }
}

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

import com.aimluck.eip.addressbook.util.AddressBookUtils;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbook;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbookCompany;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
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

      // アドレス情報の中で削除対象会社に所属しているものの会社IDを（その他）のものとする
      int empty_id =
        AddressBookUtils
          .getDummyEipMAddressbookCompany(rundata, context)
          .getCompanyId()
          .intValue();

      SelectQuery<EipMAddressbook> addrquery =
        Database.query(EipMAddressbook.class);
      Expression addrexp =
        ExpressionFactory.inDbExp(
          EipMAddressbook.EIP_MADDRESSBOOK_COMPANY_PROPERTY
            + "."
            + EipMAddressbookCompany.COMPANY_ID_PK_COLUMN,
          values);
      addrquery.setQualifier(addrexp);

      List<EipMAddressbook> addresses = addrquery.fetchList();

      if (addresses != null && addresses.size() > 0) {
        EipMAddressbook addressbook = null;

        EipMAddressbookCompany company =
          Database.get(EipMAddressbookCompany.class, Integer.valueOf(empty_id));

        int addrsize = addresses.size();
        for (int i = 0; i < addrsize; i++) {
          addressbook = addresses.get(i);
          addressbook.setEipMAddressbookCompany(company);
        }
      }

      // address-groupテーブルのデータを削除
      SelectQuery<EipMAddressbookCompany> query =
        Database.query(EipMAddressbookCompany.class);
      Expression exp =
        ExpressionFactory.inDbExp(
          EipMAddressbookCompany.COMPANY_ID_PK_COLUMN,
          values);
      query.setQualifier(exp);

      List<EipMAddressbookCompany> groups = query.fetchList();

      int grouplistsize = groups.size();

      // 会社情報を削除
      for (int i = 0; i < grouplistsize; i++) {
        EipMAddressbookCompany group = groups.get(i);

        // entityIdを取得
        Integer entityId = group.getCompanyId();
        // 会社名を取得
        String groupName = group.getCompanyName();

        // 会社情報を削除
        Database.delete(group);

        // ログに保存
        ALEventlogFactoryService.getInstance().getEventlogHandler().log(
          entityId,
          ALEventlogConstants.PORTLET_TYPE_ADDRESSBOOK_COMPANY,
          groupName);
      }

      Database.commit();
    } catch (Exception ex) {
      Database.rollback();
      logger.error("AddressBookCompanyMultiDelete.action", ex);
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
    return ALAccessControlConstants.POERTLET_FEATURE_ADDRESSBOOK_COMPANY;
  }
}

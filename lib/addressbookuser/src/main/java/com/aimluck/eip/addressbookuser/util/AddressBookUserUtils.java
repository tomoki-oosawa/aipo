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
package com.aimluck.eip.addressbookuser.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.addressbookuser.beans.AddressBookUserEmailLiteBean;
import com.aimluck.eip.addressbookuser.beans.AddressBookUserGroupLiteBean;
import com.aimluck.eip.addressbookuser.beans.AddressBookUserLiteBean;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressGroup;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbook;
import com.aimluck.eip.cayenne.om.portlet.EipTAddressbookGroupMap;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ユーティリティクラスです。 <br />
 * 
 */
public class AddressBookUserUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AddressBookUserUtils.class.getName());

  /**
   * 
   * @param rundata
   * @return
   */
  public static List<AddressBookUserLiteBean> getAddressBookUserLiteBeansFromGroup(
      String groupid, int loginuserid) {
    List<AddressBookUserLiteBean> list =
      new ArrayList<AddressBookUserLiteBean>();

    try {
      SelectQuery<EipMAddressbook> query =
        getSelectQueryForAddressBook(groupid, loginuserid);

      List<EipMAddressbook> addressbook_list = query.fetchList();

      for (EipMAddressbook record : addressbook_list) {
        AddressBookUserLiteBean address = new AddressBookUserLiteBean();
        address.initField();
        address.setAddressId(record.getAddressId());
        address.setFullName(record.getLastName(), record.getFirstName());
        list.add(address);
      }
    } catch (Exception e) {
      logger.error(
        "AddressBookUserUtils.getAddressBookUserLiteBeansFromGroup",
        e);
    }

    return list;
  }

  /**
   * 
   * @param rundata
   * @return
   */
  public static List<AddressBookUserGroupLiteBean> getAddressBookUserGroupLiteBeans(
      RunData rundata) {
    List<AddressBookUserGroupLiteBean> list =
      new ArrayList<AddressBookUserGroupLiteBean>();

    try {
      // 自分がオーナのグループを取得
      SelectQuery<EipMAddressGroup> query =
        Database.query(EipMAddressGroup.class);

      Expression exp =
        ExpressionFactory.matchExp(EipMAddressGroup.OWNER_ID_PROPERTY, Integer
          .valueOf(ALEipUtils.getUserId(rundata)));
      query.setQualifier(exp).orderAscending(
        EipMAddressGroup.GROUP_NAME_PROPERTY);

      List<EipMAddressGroup> addressgroup_list = query.fetchList();
      for (EipMAddressGroup record : addressgroup_list) {
        AddressBookUserGroupLiteBean bean = new AddressBookUserGroupLiteBean();
        bean.initField();
        bean.setGroupId(record.getGroupId());
        bean.setName(record.getGroupName());
        list.add(bean);
      }
    } catch (Exception e) {
      logger.error("AddressBookUserUtils.getAddressBookUserGroupLiteBeans", e);
    }
    return list;
  }

  /**
   * 
   * @param rundata
   * @return
   */
  public static List<AddressBookUserEmailLiteBean> getAddressBookUserEmailLiteBeansFromGroup(
      String groupid, int loginuserid) {
    List<AddressBookUserEmailLiteBean> list =
      new ArrayList<AddressBookUserEmailLiteBean>();

    try {
      SelectQuery<EipMAddressbook> query =
        getSelectQueryForAddressBook(groupid, loginuserid);

      List<EipMAddressbook> addressbook_list = query.fetchList();

      for (EipMAddressbook record : addressbook_list) {
        AddressBookUserEmailLiteBean bean = new AddressBookUserEmailLiteBean();
        bean.initField();
        bean.setAddressId(record.getAddressId());
        bean.setFullName(record.getLastName(), record.getFirstName());
        bean.setEmail(record.getEmail());
        list.add(bean);
      }
    } catch (Exception e) {
      logger.error(
        "AddressBookUserUtils.getAddressBookUserEmailLiteBeansFromGroup",
        e);
    }

    return list;
  }

  private static SelectQuery<EipMAddressbook> getSelectQueryForAddressBook(
      String groupid, int loginuserid) {
    SelectQuery<EipMAddressbook> query = Database.query(EipMAddressbook.class);

    Expression exp21 =
      ExpressionFactory.matchExp(EipMAddressbook.PUBLIC_FLAG_PROPERTY, "T");
    Expression exp22 =
      ExpressionFactory
        .matchExp(EipMAddressbook.OWNER_ID_PROPERTY, loginuserid);
    Expression exp23 =
      ExpressionFactory.matchExp(EipMAddressbook.PUBLIC_FLAG_PROPERTY, "F");
    query.setQualifier(exp21.orExp(exp22.andExp(exp23)));

    if (groupid != null && !"".equals(groupid) && !"all".equals(groupid)) {
      Expression exp31 =
        ExpressionFactory.matchDbExp(
          EipMAddressbook.EIP_TADDRESSBOOK_GROUP_MAP_PROPERTY
            + "."
            + EipTAddressbookGroupMap.EIP_TADDRESS_GROUP_PROPERTY
            + "."
            + EipMAddressGroup.GROUP_ID_PK_COLUMN,
          groupid);
      query.andQualifier(exp31);
    }

    return query;
  }
}

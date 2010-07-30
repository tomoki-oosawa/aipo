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
package com.aimluck.eip.addressbookuser.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.addressbookuser.beans.AddressBookUserEmailLiteBean;
import com.aimluck.eip.addressbookuser.beans.AddressBookUserGroupLiteBean;
import com.aimluck.eip.addressbookuser.beans.AddressBookUserLiteBean;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressGroup;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbook;
import com.aimluck.eip.cayenne.om.portlet.EipTAddressbookGroupMap;
import com.aimluck.eip.orm.DatabaseOrmService;
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
  public static List<AddressBookUserLiteBean> getAddressBookUserLiteBeansFromGroup(String groupid,
      int loginuserid) {
    List<AddressBookUserLiteBean> list = new ArrayList<AddressBookUserLiteBean>();
    DataContext dataContext = DatabaseOrmService.getInstance().getDataContext();

    try {
      SelectQuery query = new SelectQuery(EipMAddressbook.class);
      query.addCustomDbAttribute(EipMAddressbook.ADDRESS_ID_PK_COLUMN);
      query.addCustomDbAttribute(EipMAddressbook.LAST_NAME_COLUMN);
      query.addCustomDbAttribute(EipMAddressbook.FIRST_NAME_COLUMN);

      Expression exp21 = ExpressionFactory.matchExp(
          EipMAddressbook.PUBLIC_FLAG_PROPERTY, "T");
      Expression exp22 = ExpressionFactory.matchExp(
          EipMAddressbook.OWNER_ID_PROPERTY, loginuserid);
      Expression exp23 = ExpressionFactory.matchExp(
          EipMAddressbook.PUBLIC_FLAG_PROPERTY, "F");
      query.setQualifier(exp21.orExp(exp22.andExp(exp23)));

      if (groupid != null && !"".equals(groupid) && !"all".equals(groupid)) {

        Expression exp31 = ExpressionFactory.matchDbExp(
            EipMAddressbook.EIP_TADDRESSBOOK_GROUP_MAP_PROPERTY + "."
                + EipTAddressbookGroupMap.EIP_TADDRESS_GROUP_PROPERTY + "."
                + EipMAddressGroup.GROUP_ID_PK_COLUMN, groupid);
        query.andQualifier(exp31);
      }

      List<?> alist = dataContext.performQuery(query);

      DataRow dataRow;
      AddressBookUserLiteBean address = null;
      int size = alist.size();
      for (int i = 0; i < size; i++) {
        dataRow = (DataRow) alist.get(i);
        address = new AddressBookUserLiteBean();
        address.initField();
        address.setAddressId(((Integer) ALEipUtils.getObjFromDataRow(dataRow,
            EipMAddressbook.ADDRESS_ID_PK_COLUMN)).intValue());
        address.setFullName((String) ALEipUtils.getObjFromDataRow(dataRow,
            EipMAddressbook.LAST_NAME_COLUMN), (String) ALEipUtils
            .getObjFromDataRow(dataRow, EipMAddressbook.FIRST_NAME_COLUMN));
        list.add(address);
      }

    } catch (Exception e) {
      logger.error("[AddressBookUserUtils]", e);
    }

    return list;
  }

  /**
   *
   * @param rundata
   * @return
   */
  public static List<AddressBookUserGroupLiteBean> getAddressBookUserGroupLiteBeans(RunData rundata) {
    List<AddressBookUserGroupLiteBean> list = new ArrayList<AddressBookUserGroupLiteBean>();

    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();

      // 自分がオーナのグループを取得
      SelectQuery query = new SelectQuery(EipMAddressGroup.class);
      query.addCustomDbAttribute(EipMAddressGroup.GROUP_ID_PK_COLUMN);
      query.addCustomDbAttribute(EipMAddressGroup.GROUP_NAME_COLUMN);

      Expression exp = ExpressionFactory.matchExp(
          EipMAddressGroup.OWNER_ID_PROPERTY, Integer.valueOf(ALEipUtils
              .getUserId(rundata)));
      query.setQualifier(exp);
      query.addOrdering(EipMAddressGroup.GROUP_NAME_PROPERTY, Ordering.ASC);
      List<?> glist = dataContext.performQuery(query);

      // Mapへ値をセット
      DataRow dataRow;
      AddressBookUserGroupLiteBean bean = null;
      int size = glist.size();
      for (int i = 0; i < size; i++) {
        dataRow = (DataRow) glist.get(i);
        bean = new AddressBookUserGroupLiteBean();
        bean.initField();
        bean.setGroupId(((Integer) ALEipUtils.getObjFromDataRow(dataRow,
            EipMAddressGroup.GROUP_ID_PK_COLUMN)).intValue());
        bean.setName((String) ALEipUtils.getObjFromDataRow(dataRow,
            EipMAddressGroup.GROUP_NAME_COLUMN));
        list.add(bean);
      }

    } catch (Exception e) {
      logger.error("[AddressBookUserUtils]", e);
    }
    return list;
  }

  /**
   *
   * @param rundata
   * @return
   */
  public static List<AddressBookUserEmailLiteBean> getAddressBookUserEmailLiteBeansFromGroup(String groupid,
      int loginuserid) {
    List<AddressBookUserEmailLiteBean> list = new ArrayList<AddressBookUserEmailLiteBean>();
    DataContext dataContext = DatabaseOrmService.getInstance().getDataContext();

    try {
      SelectQuery query = new SelectQuery(EipMAddressbook.class);
      query.addCustomDbAttribute(EipMAddressbook.ADDRESS_ID_PK_COLUMN);
      query.addCustomDbAttribute(EipMAddressbook.LAST_NAME_COLUMN);
      query.addCustomDbAttribute(EipMAddressbook.FIRST_NAME_COLUMN);
      query.addCustomDbAttribute(EipMAddressbook.EMAIL_COLUMN);

      Expression exp21 = ExpressionFactory.matchExp(
          EipMAddressbook.PUBLIC_FLAG_PROPERTY, "T");
      Expression exp22 = ExpressionFactory.matchExp(
          EipMAddressbook.OWNER_ID_PROPERTY, loginuserid);
      Expression exp23 = ExpressionFactory.matchExp(
          EipMAddressbook.PUBLIC_FLAG_PROPERTY, "F");
      query.setQualifier(exp21.orExp(exp22.andExp(exp23)));

      if (groupid != null && !"".equals(groupid) && !"all".equals(groupid)) {

        Expression exp31 = ExpressionFactory.matchDbExp(
            EipMAddressbook.EIP_TADDRESSBOOK_GROUP_MAP_PROPERTY + "."
                + EipTAddressbookGroupMap.EIP_TADDRESS_GROUP_PROPERTY + "."
                + EipMAddressGroup.GROUP_ID_PK_COLUMN, groupid);
        query.andQualifier(exp31);
      }

      List<?> alist = dataContext.performQuery(query);

      DataRow dataRow;
      AddressBookUserEmailLiteBean address = null;
      int size = alist.size();
      for (int i = 0; i < size; i++) {
        dataRow = (DataRow) alist.get(i);
        address = new AddressBookUserEmailLiteBean();
        address.initField();
        address.setAddressId(((Integer) ALEipUtils.getObjFromDataRow(dataRow,
            EipMAddressbook.ADDRESS_ID_PK_COLUMN)).intValue());
        address.setFullName((String) ALEipUtils.getObjFromDataRow(dataRow,
            EipMAddressbook.LAST_NAME_COLUMN), (String) ALEipUtils
            .getObjFromDataRow(dataRow, EipMAddressbook.FIRST_NAME_COLUMN));
        address.setEmail((String) ALEipUtils.getObjFromDataRow(dataRow,
            EipMAddressbook.EMAIL_COLUMN));
        list.add(address);
      }

    } catch (Exception e) {
      logger.error("[AddressBookUserUtils]", e);
    }

    return list;
  }
}

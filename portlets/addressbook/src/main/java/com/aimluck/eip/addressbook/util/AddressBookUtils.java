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
package com.aimluck.eip.addressbook.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.addressbook.AddressBookGroup;
import com.aimluck.eip.addressbook.AddressBookGroupResultData;
import com.aimluck.eip.addressbook.AddressBookResultData;
import com.aimluck.eip.addressbookuser.beans.AddressBookUserGroupLiteBean;
import com.aimluck.eip.cayenne.om.account.EipMPost;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressGroup;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbook;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbookCompany;
import com.aimluck.eip.cayenne.om.portlet.EipTAddressbookGroupMap;
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * Addressbookのユーティリティクラスです。
 * 
 */
public class AddressBookUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AddressBookUtils.class.getName());

  /** アドレスブックファイルを一時保管するディレクトリの指定 */
  public static final String FOLDER_TMP_FOR_ADDRESSBOOK_FILES =
    JetspeedResources.getString("aipo.tmp.addressbook.directory", "");

  public static final String EMPTY_COMPANY_NAME = "";

  public static final String ADDRESSBOOK_PORTLET_NAME = "AddressBook";

  public static EipMAddressGroup getEipMAddressGroup(RunData rundata,
      Context context) {
    String groupid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (groupid == null || Integer.valueOf(groupid) == null) {
        // グループIDが空の場合
        logger.debug("[AddressBook] Group ID...");
        return null;
      }
      SelectQuery<EipMAddressGroup> query =
        Database.query(EipMAddressGroup.class);
      Expression exp1 =
        ExpressionFactory.matchDbExp(
          EipMAddressGroup.GROUP_ID_PK_COLUMN,
          Integer.valueOf(groupid));
      query.setQualifier(exp1);
      Expression exp2 =
        ExpressionFactory.matchExp(EipMAddressGroup.OWNER_ID_PROPERTY, Integer
          .valueOf(ALEipUtils.getUserId(rundata)));
      query.andQualifier(exp2);

      List<EipMAddressGroup> groups = query.fetchList();
      if (groups == null || groups.size() == 0) {
        // 指定したカテゴリIDのレコードが見つからない場合
        logger.debug("[AddressBook] Not found Group ID...");
        return null;
      }
      return groups.get(0);
    } catch (Exception ex) {
      logger.error("AddressBookUtils.getEipMAddressGroup", ex);
      return null;
    }
  }

  /**
   * 指定した取引先情報の取得
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipMAddressbookCompany getEipMAddressbookCompany(
      RunData rundata, Context context) {
    // セッションから指定された 取引先ID を取得
    String companyid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (companyid == null || Integer.valueOf(companyid) == null) {
        logger.debug("[AddressBook] Company ID...");
        return null;
      }

      SelectQuery<EipMAddressbookCompany> query =
        Database.query(EipMAddressbookCompany.class);
      Expression exp =
        ExpressionFactory.matchDbExp(
          EipMAddressbookCompany.COMPANY_ID_PK_COLUMN,
          Integer.valueOf(companyid));
      query.setQualifier(exp);

      List<EipMAddressbookCompany> companys = query.fetchList();
      if (companys == null || companys.size() == 0) {
        logger.debug("[AddressBook] Not found Company ID...");
        return null;
      }
      return companys.get(0);
    } catch (Exception ex) {
      logger.error("AddressBookUtils.getEipMAddressbookCompany", ex);
      return null;
    }
  }

  /**
   * @param id
   * @return
   */
  public static List<String> getMyGroupNames(RunData rundata, int addressid,
      int userid) {
    try {
      SelectQuery<EipTAddressbookGroupMap> query =
        Database.query(EipTAddressbookGroupMap.class);
      Expression exp1 =
        ExpressionFactory.matchExp(
          EipTAddressbookGroupMap.ADDRESS_ID_PROPERTY,
          Integer.valueOf(addressid));
      query.setQualifier(exp1);
      Expression exp2 =
        ExpressionFactory.matchExp(
          EipTAddressbookGroupMap.EIP_TADDRESS_GROUP_PROPERTY
            + "."
            + EipMAddressGroup.OWNER_ID_PROPERTY,
          Integer.valueOf(userid));
      query.andQualifier(exp2);

      List<EipTAddressbookGroupMap> groups = query.fetchList();

      List<String> aList = new ArrayList<String>();
      int size = groups.size();
      for (int i = 0; i < size; i++) {
        aList.add(groups.get(i).getEipTAddressGroup().getGroupName());
      }
      return aList;
    } catch (Exception ex) {
      logger.error("AddressBookUtils.getMyGroupNames", ex);
    }
    return null;
  }

  public static String getMyGroupNamesAsString(RunData rundata, int addressid,
      int userid) {
    List<String> aList = getMyGroupNames(rundata, addressid, userid);
    int group_size = aList.size();
    StringBuffer groupNames = new StringBuffer();
    for (int i = 0; i < group_size; i++) {
      groupNames.append(aList.get(i)).append("、");
    }
    if (groupNames.length() == 0) {
      return "";
    } else {
      return groupNames.substring(0, groupNames.length() - 1);
    }
  }

  /**
   * セッションで指定されたアドレスIDを持つアドレス情報を取得する。
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipMAddressbook getEipMAddressbook(RunData rundata,
      Context context) {
    // セッションから指定された アドレスID を取得
    String addressid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (addressid == null || Integer.valueOf(addressid) == null) {
        logger.debug("[AddressBook] Address ID...");
        return null;
      }

      SelectQuery<EipMAddressbook> query =
        Database.query(EipMAddressbook.class);
      Expression exp11 =
        ExpressionFactory.matchDbExp(
          EipMAddressbook.ADDRESS_ID_PK_COLUMN,
          Integer.valueOf(addressid));
      query.setQualifier(exp11);

      Expression exp21 =
        ExpressionFactory.matchExp(EipMAddressbook.PUBLIC_FLAG_PROPERTY, "T");
      Expression exp22 =
        ExpressionFactory.matchExp(
          EipMAddressbook.OWNER_ID_PROPERTY,
          ALEipUtils.getUserId(rundata));
      Expression exp23 =
        ExpressionFactory.matchExp(EipMAddressbook.PUBLIC_FLAG_PROPERTY, "F");
      query.andQualifier(exp21.orExp(exp22.andExp(exp23)));

      List<EipMAddressbook> addresses = query.fetchList();

      if (addresses == null || addresses.size() == 0) {
        logger.debug("[AddressBook] Not found Address ID...");
        return null;
      }
      return addresses.get(0);
    } catch (Exception ex) {
      logger.error("AddressBookUtils.getEipMAddressbook", ex);
      return null;
    }
  }

  /**
   * その他の会社情報を取得する。
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipMAddressbookCompany getDummyEipMAddressbookCompany(
      RunData rundata, Context context) {
    // default company definition : company_id value is integer 1
    return Database.get(EipMAddressbookCompany.class, Integer.valueOf(1));
  }

  /**
   * 自分がオーナのグループを取得しID/Objectのマップを返却する。
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static Map<Integer, AddressBookGroup> getGroupMap(RunData rundata,
      Context context) {
    try {
      Map<Integer, AddressBookGroup> groupMap =
        new LinkedHashMap<Integer, AddressBookGroup>();

      SelectQuery<EipMAddressGroup> query =
        Database.query(EipMAddressGroup.class);
      Expression exp =
        ExpressionFactory.matchExp(EipMAddressGroup.OWNER_ID_PROPERTY, Integer
          .valueOf(ALEipUtils.getUserId(rundata)));
      query.setQualifier(exp);

      List<EipMAddressGroup> list = query.fetchList();
      int size = list.size();
      for (int i = 0; i < size; i++) {
        EipMAddressGroup record = list.get(i);
        AddressBookGroup group = new AddressBookGroup();
        group.initField();
        group.setGroupId(record.getGroupId().intValue());
        group.setGroupName(record.getGroupName());
        groupMap.put(record.getGroupId(), group);
      }
      return groupMap;
    } catch (Exception ex) {
      logger.error("AddressBookUtils.getGroupMap", ex);
      return null;
    }
  }

  public static List<EipMAddressGroup> getGroups(int uid) {
    logger.debug("AddressbookUtils getGroups in");
    try {
      SelectQuery<EipMAddressGroup> query =
        Database.query(EipMAddressGroup.class);
      Expression exp =
        ExpressionFactory.matchExp(EipMAddressGroup.OWNER_ID_PROPERTY, Integer
          .valueOf(uid));
      query.setQualifier(exp);

      List<EipMAddressGroup> groups = query.fetchList();

      if (groups == null || groups.size() == 0) {
        logger.debug("[AddressBook] Not found group ID...");
        return null;
      }
      logger.debug("AddressbookUtils getGroups out");
      return groups;
    } catch (Exception ex) {
      logger.error("AddressBookUtils.getGroups", ex);
      return null;
    }
  }

  public static List<ALStringField> getGroupMember(String gid) {
    List<ALStringField> resultList = new ArrayList<ALStringField>();

    StringBuffer statement = new StringBuffer();
    statement.append("SELECT ");
    statement.append(" B.last_name, B.first_name, ");
    statement.append(" C.company_name ");
    statement.append("FROM eip_t_addressbook_group_map as A ");
    statement.append("LEFT JOIN eip_m_addressbook as B ");
    statement.append(" on A.ADDRESS_ID = B.ADDRESS_ID ");
    statement.append("LEFT JOIN eip_m_addressbook_company AS C ");
    statement.append(" on B.COMPANY_ID = C.COMPANY_ID ");
    statement.append("WHERE A.GROUP_ID = #bind($gid) ");
    statement
      .append("ORDER BY C.company_name_kana, C.company_name, B.last_name_kana");
    String query = statement.toString();

    try {
      List<DataRow> list =
        Database.sql(EipMAddressbook.class, query).param(
          "gid",
          Integer.parseInt(gid)).fetchListAsDataRow();

      int recordNum = list.size();
      DataRow dataRow;
      for (int i = 0; i < recordNum; i++) {
        dataRow = list.get(i);
        StringBuffer strBuf = new StringBuffer();
        strBuf
          .append(
            Database.getFromDataRow(dataRow, EipMAddressbook.LAST_NAME_COLUMN))
          .append(
            Database.getFromDataRow(dataRow, EipMAddressbook.FIRST_NAME_COLUMN));
        String companyName =
          (String) Database.getFromDataRow(
            dataRow,
            EipMAddressbookCompany.COMPANY_NAME_COLUMN);
        if (companyName != null && companyName.trim().length() > 0) {
          strBuf.append(" (").append(companyName).append(")");
        }
        ALStringField st = new ALStringField(strBuf.toString());
        resultList.add(st);
      }
    } catch (Exception ex) {
      logger.error("AddressbookUtils.getGroupMember", ex);
    }
    return resultList;
  }

  /**
   * ユーザーの所属する部署の一覧を取得します。
   * 
   * @param uid
   *          ユーザーID
   * @return 所属する部署リスト
   */
  public static List<AddressBookUserGroupLiteBean> getPostBeanList(int uid) {
    SelectQuery<TurbineUserGroupRole> query =
      Database.query(TurbineUserGroupRole.class);
    Expression exp1 =
      ExpressionFactory.matchExp(
        TurbineUserGroupRole.TURBINE_USER_PROPERTY,
        Integer.valueOf(uid));
    Expression exp2 =
      ExpressionFactory.greaterExp(
        TurbineUserGroupRole.TURBINE_GROUP_PROPERTY,
        Integer.valueOf(3));
    Expression exp3 =
      ExpressionFactory.matchExp(TurbineUserGroupRole.TURBINE_GROUP_PROPERTY
        + "."
        + TurbineGroup.OWNER_ID_PROPERTY, Integer.valueOf(1));
    query.setQualifier(exp1);
    query.andQualifier(exp2);
    query.andQualifier(exp3);

    List<TurbineUserGroupRole> list = query.fetchList();

    if (list == null || list.size() < 0) {
      return null;
    }

    List<AddressBookUserGroupLiteBean> resultList =
      new ArrayList<AddressBookUserGroupLiteBean>();

    TurbineUserGroupRole ugr = null;
    TurbineGroup group = null;
    AddressBookUserGroupLiteBean bean = null;
    int size = list.size();
    for (int i = 0; i < size; i++) {
      ugr = list.get(i);
      group = ugr.getTurbineGroup();
      EipMPost post = group.getEipMPost();
      bean = new AddressBookUserGroupLiteBean();
      bean.initField();
      bean.setGroupId(post.getPostId());
      bean.setName(post.getPostName());
      resultList.add(bean);
    }

    return resultList;
  }

  /**
   * 現在表示させているタブが「社外」であるかどうか調べます。
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static boolean isSyagai(RunData rundata, Context context) {
    String tabParam = rundata.getParameters().getString("tab");
    String currentTab = ALEipUtils.getTemp(rundata, context, "tab");
    if (tabParam == null) {
      if (currentTab == null
        || currentTab.trim().length() == 0
        || "syagai".equals(currentTab)) {
        return true;
      }
      return false;
    } else {
      if ("syagai".equals(tabParam)) {
        return true;
      }
      return false;
    }
  }

  /**
   * TurbineUserクラスをもとに作った社内アドレスのResultDataを返します。
   * 
   * @param record
   * @param strLength
   *          表示文字数
   * @return
   */
  public static AddressBookResultData getCorpResultData(TurbineUser record,
      int strLength) {
    AddressBookResultData rd = new AddressBookResultData();

    rd.initField();
    rd.setAddressId(record.getUserId().intValue());
    rd.setName(ALCommonUtils.compressString(new StringBuffer()
      .append(record.getLastName())
      .append(" ")
      .append(record.getFirstName())
      .toString(), strLength));
    if (record.getCompanyId().intValue() > 0) {
      rd.setCompanyName(ALCommonUtils.compressString(ALEipUtils
        .getCompanyName(record.getCompanyId().intValue()), strLength));
    }

    rd.setPostList(compressString(AddressBookUtils.getPostBeanList(record
      .getUserId()
      .intValue()), strLength));

    if (record.getPositionId().intValue() > 0) {
      rd.setPositionName(ALCommonUtils.compressString(ALEipUtils
        .getPositionName(record.getPositionId()), strLength));
    }
    rd.setEmail(ALCommonUtils.compressString(record.getEmail(), strLength));
    rd.setTelephone(record.getOutTelephone());
    rd.setCellularPhone(record.getCellularPhone());
    rd.setCellularMail(record.getCellularMail());
    rd.setInTelephone(record.getInTelephone());

    return rd;
  }

  public static List<AddressBookUserGroupLiteBean> compressString(
      List<AddressBookUserGroupLiteBean> list, int length) {
    for (AddressBookUserGroupLiteBean bean : list) {
      bean.setName(ALCommonUtils.compressString(bean.getName(), length));
    }
    return list;
  }

  public static Expression excludeDefaultCompanyCriteria() {
    Expression exp =
      ExpressionFactory.noMatchDbExp(
        EipMAddressbookCompany.COMPANY_ID_PK_COLUMN,
        Integer.valueOf(1));
    return exp;
  }

  /**
   * 自分がオーナーのグループを取得します
   * 
   * @param rundata
   * @return
   */
  public static List<AddressBookGroupResultData> getMyGroups(RunData rundata) {
    List<AddressBookGroupResultData> res =
      new ArrayList<AddressBookGroupResultData>();
    try {
      // 自分がオーナのグループ指定
      SelectQuery<EipMAddressGroup> query =
        Database.query(EipMAddressGroup.class);
      Expression exp =
        ExpressionFactory.matchExp(EipMAddressGroup.OWNER_ID_PROPERTY, Integer
          .valueOf(ALEipUtils.getUserId(rundata)));
      query.orderAscending(EipMAddressGroup.GROUP_NAME_PROPERTY);
      query.setQualifier(exp);
      List<EipMAddressGroup> aList = query.fetchList();
      for (EipMAddressGroup record : aList) {
        AddressBookGroupResultData rd = new AddressBookGroupResultData();
        rd.initField();
        rd.setGroupId(record.getGroupId().longValue());
        rd.setGroupName(record.getGroupName());
        res.add(rd);
      }
    } catch (Exception ex) {
      logger.error("AddressBookUtils.getMyGroups", ex);
    }
    return res;
  }

}

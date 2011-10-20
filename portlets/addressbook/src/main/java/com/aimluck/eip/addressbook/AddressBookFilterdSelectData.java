/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.addressbook.util.AddressBookUtils;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressGroup;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbook;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbookCompany;
import com.aimluck.eip.cayenne.om.portlet.EipTAddressbookGroupMap;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アドレス帳ワード検索用データクラスです。(社外アドレス検索用)
 * 
 */
public class AddressBookFilterdSelectData extends
    AbstractAddressBookFilterdSelectData<EipMAddressbook, EipMAddressbook> {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AddressBookFilterdSelectData.class.getName());

  /** フィルタに利用するグループリスト */
  private List<AddressBookGroupResultData> groupList;

  /**
   * 初期化処理を行います。
   * 
   * @param action
   * @param rundata
   * @param context
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "name_kana");
    }

    super.init(action, rundata, context);
  }

  /**
   * アドレス情報の一覧を、グループ・一覧・社員単位で表示する。
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<EipMAddressbook> selectList(RunData rundata,
      Context context) {

    try {
      SelectQuery<EipMAddressbook> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);
      return query.getResultList();
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 社外アドレスタブ選択時のアドレス帳の詳細情報を表示します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected EipMAddressbook selectDetail(RunData rundata, Context context) {
    try {
      return AddressBookUtils.getEipMAddressbook(rundata, context);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 
   * @param record
   * @return
   */
  @Override
  protected Object getResultData(EipMAddressbook record) {
    try {

      AddressBookResultData rd = new AddressBookResultData();
      rd.initField();
      rd.setAddressId(record.getAddressId().intValue());
      rd.setName(ALCommonUtils.compressString(new StringBuffer()
        .append(record.getLastName())
        .append(" ")
        .append(record.getFirstName())
        .toString(), getStrLength()));
      rd.setNameKana(ALCommonUtils.compressString(new StringBuffer()
        .append(record.getLastNameKana())
        .append(' ')
        .append(record.getFirstNameKana())
        .toString(), getStrLength()));

      EipMAddressbookCompany company = record.getEipMAddressbookCompany();

      if (!AddressBookUtils.EMPTY_COMPANY_NAME.equals(company.getCompanyName())) {
        // 「未分類」の会社情報ではない場合
        rd.setCompanyName(ALCommonUtils.compressString(
          company.getCompanyName(),
          getStrLength()));
        rd.setCompanyId(company.getCompanyId().toString());
        rd.setPostName(ALCommonUtils.compressString(
          company.getPostName(),
          getStrLength()));
      }
      rd.setPositionName(ALCommonUtils.compressString(
        record.getPositionName(),
        getStrLength()));
      rd.setEmail(record.getEmail());
      rd.setTelephone(record.getTelephone());
      rd.setCellularPhone(record.getCellularPhone());
      rd.setCellularMail(record.getCellularMail());
      rd.setPublicFlag(record.getPublicFlag());

      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 詳細情報の返却データ取得。
   * 
   * @param record
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipMAddressbook record) {
    try {

      AddressBookResultData rd = new AddressBookResultData();
      rd.initField();

      // 登録ユーザ名の設定
      ALEipUser createdUser =
        ALEipUtils.getALEipUser(record.getCreateUserId().intValue());
      String createdUserName = createdUser.getAliasName().getValue();
      rd.setCreatedUser(createdUserName);

      // 更新ユーザ名の設定
      String updatedUserName;
      if (record.getCreateUserId().equals(record.getUpdateUserId())) {
        updatedUserName = createdUserName;
      } else {
        ALEipUser updatedUser =
          ALEipUtils.getALEipUser(record.getUpdateUserId().intValue());
        updatedUserName = updatedUser.getAliasName().getValue();
      }
      rd.setUpdatedUser(updatedUserName);

      // アドレスID の設定
      int addressId = record.getAddressId().intValue();
      rd.setAddressId(addressId);
      rd.setName(new StringBuffer()
        .append(record.getLastName())
        .append(' ')
        .append(record.getFirstName())
        .toString());
      rd.setNameKana(new StringBuffer()
        .append(record.getLastNameKana())
        .append(' ')
        .append(record.getFirstNameKana())
        .toString());
      rd.setEmail(record.getEmail());
      rd.setTelephone(record.getTelephone());
      rd.setCellularPhone(record.getCellularPhone());
      rd.setCellularMail(record.getCellularMail());
      rd.setPositionName(record.getPositionName());
      rd.setPublicFlag(record.getPublicFlag());

      EipMAddressbookCompany company = record.getEipMAddressbookCompany();
      if (!AddressBookUtils.EMPTY_COMPANY_NAME.equals(company.getCompanyName())) {
        // 「未分類」の会社情報ではない場合、会社情報を設定する
        rd.setCompanyName(company.getCompanyName());
        rd.setCompanyNameKana(company.getCompanyNameKana());
        rd.setPostName(company.getPostName());
        rd.setZipcode(company.getZipcode());
        rd.setCompanyAddress(company.getAddress());
        rd.setCompanyTelephone(company.getTelephone());
        rd.setCompanyFaxNumber(company.getFaxNumber());
        rd.setCompanyUrl(company.getUrl());
      }

      rd.setCreateDate(ALDateUtil.format(record.getCreateDate(), "yyyy年M月d日"));
      rd.setUpdateDate(ALDateUtil.format(record.getUpdateDate(), "yyyy年M月d日"));
      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 
   * @return
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("group", EipMAddressbook.EIP_TADDRESSBOOK_GROUP_MAP_PROPERTY
      + "."
      + EipTAddressbookGroupMap.EIP_TADDRESS_GROUP_PROPERTY
      + "."
      + EipMAddressGroup.GROUP_ID_PK_COLUMN);
    map.putValue("name_kana", EipMAddressbook.LAST_NAME_KANA_PROPERTY);
    map.putValue(
      "company_name_kana",
      EipMAddressbook.EIP_MADDRESSBOOK_COMPANY_PROPERTY
        + "."
        + EipMAddressbookCompany.COMPANY_NAME_KANA_PROPERTY);
    return map;
  }

  /**
   * 検索条件を設定した SelectQuery を返します。
   * 
   * @param query
   * @param rundata
   * @param context
   * @return
   */
  protected SelectQuery<EipMAddressbook> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipMAddressbook> query = Database.query(EipMAddressbook.class);

    Expression exp21 =
      ExpressionFactory.matchExp(EipMAddressbook.PUBLIC_FLAG_PROPERTY, "T");
    Expression exp22 =
      ExpressionFactory.matchExp(EipMAddressbook.OWNER_ID_PROPERTY, ALEipUtils
        .getUserId(rundata));
    Expression exp23 =
      ExpressionFactory.matchExp(EipMAddressbook.PUBLIC_FLAG_PROPERTY, "F");
    query.setQualifier(exp21.orExp(exp22.andExp(exp23)));

    return getSelectQueryForIndex(query, rundata, context);
  }

  /**
   * インデックス検索のためのカラムを返します。
   * 
   * @return
   */
  @Override
  protected String getColumnForIndex() {
    return EipMAddressbook.LAST_NAME_KANA_PROPERTY;
  }

  public List<AddressBookGroupResultData> getGroupList() {
    return groupList;
  }

  /**
   * 
   * @param rundata
   * @param context
   */
  public void loadGroups(RunData rundata, Context context) {
    groupList = new ArrayList<AddressBookGroupResultData>();
    try {
      SelectQuery<EipMAddressGroup> query =
        Database.query(EipMAddressGroup.class);
      Expression exp =
        ExpressionFactory.matchExp(EipMAddressGroup.OWNER_ID_PROPERTY, Integer
          .valueOf(ALEipUtils.getUserId(rundata)));
      query.setQualifier(exp);
      query.orderAscending(EipMAddressGroup.GROUP_NAME_PROPERTY);

      List<EipMAddressGroup> aList = query.fetchList();

      int size = aList.size();
      for (int i = 0; i < size; i++) {
        EipMAddressGroup record = aList.get(i);
        AddressBookGroupResultData rd = new AddressBookGroupResultData();
        rd.initField();
        rd.setGroupId(record.getGroupId().intValue());
        rd.setGroupName(record.getGroupName());
        groupList.add(rd);
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
    }
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

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

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
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
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALDataContext;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アドレス帳ワード検索用データクラスです。(社外アドレス検索用)
 *
 */
public class AddressBookFilterdSelectData extends
    ALAbstractSelectData<EipMAddressbook> {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(AddressBookFilterdSelectData.class.getName());

  /** 現在選択されているタブ */
  private String currentTab;

  /** フィルタに利用するグループリスト */
  private List<AddressBookGroupResultData> groupList;

  /** 現在選択されているインデックス */
  private String index;

  /**
   * 初期化します。
   *
   * @see com.aimluck.eip.common.ALAbstractSelectData#init(com.aimluck.eip.modules.actions.common.ALAction,
   *      org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
   */
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "name_kana");
    }

    String tabParam = rundata.getParameters().getString("tab");
    currentTab = ALEipUtils.getTemp(rundata, context, "tab");
    if (tabParam == null && currentTab == null) {
      ALEipUtils.setTemp(rundata, context, "tab", "syagai");
      currentTab = "syagai";
    } else if (tabParam != null) {
      ALEipUtils.setTemp(rundata, context, "tab", tabParam);
      currentTab = tabParam;
    }

    // 検索ワードがセッションに保存されている場合は，セッションから削除する．
    /*
     * String searchWord = ALEipUtils .getTemp(rundata, context,
     * "AddressBooksword"); if (searchWord != null && !searchWord.equals("")) {
     * ALEipUtils.removeTemp(rundata, context, "AddressBooksword"); }
     */

    super.init(action, rundata, context);
  }

  /**
   * アドレス情報の一覧を、グループ・一覧・社員単位で表示する。
   *
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectList(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected List<EipMAddressbook> selectList(RunData rundata, Context context) {

    try {
      SelectQuery query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);
      List<EipMAddressbook> list = ALDataContext.performQuery(
          EipMAddressbook.class, query);
      return buildPaginatedList(list);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 社外アドレスタブ選択時のアドレス帳の詳細情報を表示します。
   *
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectDetail(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected EipMAddressbook selectDetail(RunData rundata, Context context) {
    try {
      return AddressBookUtils.getEipMAddressbook(rundata, context);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultData(java.lang.Object)
   */
  protected Object getResultData(EipMAddressbook record) {
    try {

      AddressBookResultData rd = new AddressBookResultData();
      rd.initField();
      rd.setAddressId(record.getAddressId().intValue());
      rd.setName(new StringBuffer().append(record.getLastName()).append(" ")
          .append(record.getFirstName()).toString());
      rd.setNameKana(new StringBuffer().append(record.getLastNameKana())
          .append(' ').append(record.getFirstNameKana()).toString());

      EipMAddressbookCompany company = record.getEipMAddressbookCompany();

      if (!AddressBookUtils.EMPTY_COMPANY_NAME.equals(company.getCompanyName())
      /* && company.getCreateUserId().intValue() != 1 */) {
        // 「未分類」の会社情報ではない場合

        rd.setCompanyName(ALCommonUtils.compressString(
            company.getCompanyName(), getStrLength()));
        rd.setCompanyId(company.getCompanyId().toString());
        rd.setPostName(ALCommonUtils.compressString(company.getPostName(),
            getStrLength()));
      }
      rd.setPositionName(ALCommonUtils.compressString(record.getPositionName(),
          getStrLength()));
      rd.setEmail(record.getEmail());
      rd.setTelephone(record.getTelephone());
      rd.setCellularPhone(record.getCellularPhone());
      rd.setCellularMail(record.getCellularMail());
      rd.setPublicFlag(record.getPublicFlag());
      rd.setIndex(index);

      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 詳細情報の返却データ取得。
   *
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultDataDetail(java.lang.Object)
   */
  protected Object getResultDataDetail(EipMAddressbook record) {
    try {

      AddressBookResultData rd = new AddressBookResultData();
      rd.initField();

      // 登録ユーザ名の設定
      ALEipUser createdUser = ALEipUtils.getALEipUser(record.getCreateUserId()
          .intValue());
      String createdUserName = createdUser.getAliasName().getValue();
      rd.setCreatedUser(createdUserName);

      // 更新ユーザ名の設定
      String updatedUserName;
      if (record.getCreateUserId().equals(record.getUpdateUserId())) {
        updatedUserName = createdUserName;
      } else {
        ALEipUser updatedUser = ALEipUtils.getALEipUser(record
            .getUpdateUserId().intValue());
        updatedUserName = updatedUser.getAliasName().getValue();
      }
      rd.setUpdatedUser(updatedUserName);

      // アドレスID の設定
      int addressId = record.getAddressId().intValue();
      rd.setAddressId(addressId);
      rd.setName(new StringBuffer().append(record.getLastName()).append(' ')
          .append(record.getFirstName()).toString());
      rd.setNameKana(new StringBuffer().append(record.getLastNameKana())
          .append(' ').append(record.getFirstNameKana()).toString());
      rd.setEmail(record.getEmail());
      rd.setTelephone(record.getTelephone());
      rd.setCellularPhone(record.getCellularPhone());
      rd.setCellularMail(record.getCellularMail());
      rd.setPositionName(record.getPositionName());
      rd.setPublicFlag(record.getPublicFlag());

      EipMAddressbookCompany company = record.getEipMAddressbookCompany();
      if (!AddressBookUtils.EMPTY_COMPANY_NAME.equals(company.getCompanyName())
      /* && company.getCreateUserId().intValue() != 1 */) {
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
   * @see com.aimluck.eip.common.ALAbstractSelectData#getColumnMap()
   */
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("group", EipMAddressbook.EIP_TADDRESSBOOK_GROUP_MAP_PROPERTY
        + "." + EipTAddressbookGroupMap.EIP_TADDRESS_GROUP_PROPERTY + "."
        + EipMAddressGroup.GROUP_ID_PK_COLUMN);
    map.putValue("name_kana", EipMAddressbook.LAST_NAME_KANA_PROPERTY);
    map.putValue("company_name_kana",
        EipMAddressbook.EIP_MADDRESSBOOK_COMPANY_PROPERTY + "."
            + EipMAddressbookCompany.COMPANY_NAME_KANA_PROPERTY);
    return map;
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery getSelectQuery(RunData rundata, Context context) {
    SelectQuery query = new SelectQuery(EipMAddressbook.class);

    Expression exp21 = ExpressionFactory.matchExp(
        EipMAddressbook.PUBLIC_FLAG_PROPERTY, "T");
    Expression exp22 = ExpressionFactory.matchExp(
        EipMAddressbook.OWNER_ID_PROPERTY, ALEipUtils.getUserId(rundata));
    Expression exp23 = ExpressionFactory.matchExp(
        EipMAddressbook.PUBLIC_FLAG_PROPERTY, "F");
    query.setQualifier(exp21.orExp(exp22.andExp(exp23)));

    // インデックス指定時の条件文作成
    String index_session = ALEipUtils.getTemp(rundata, context, LIST_INDEX_STR);
    String index_rundata = rundata.getParameters().getString("idx");
    if (index_rundata != null) {
      if ("-1".equals(index_rundata) || "".equals(index_rundata)) {
        ALEipUtils.setTemp(rundata, context, LIST_INDEX_STR, "-1");
        context.put("idx", "-1");
      } else {
        index = index_rundata;
        ALEipUtils.setTemp(rundata, context, LIST_INDEX_STR, index);
        buildSelectQueryForAddressbookIndex(query,
            EipMAddressbook.LAST_NAME_KANA_PROPERTY, Integer.parseInt(index));
        context.put("idx", index);
      }
    } else if (index_session != null) {
      buildSelectQueryForAddressbookIndex(query,
          EipMAddressbook.LAST_NAME_KANA_PROPERTY,
          Integer.parseInt(index_session));
      context.put("idx", index);
    }

    /*
     * // インデックス指定時の条件文作成 if (rundata.getParameters().getString("idx") != null)
     * { // 索引の保存 index = rundata.getParameters().getString("idx");
     *
     * buildSelectQueryForAddressbookIndex(query,
     * EipMAddressbook.LAST_NAME_KANA_PROPERTY, Integer.parseInt(index)); }
     */

    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * 現在選択されているタブを取得します。
   *
   * @return
   */
  public String getCurrentTab() {
    return currentTab;
  }

  public List<AddressBookGroupResultData> getGroupList() {
    return groupList;
  }

  /**
   * 現在選択されているインデックスを取得します。
   *
   * @return
   */
  public String getIndex() {
    return index;
  }

  /**
   *
   * @param rundata
   * @param context
   */
  public void loadGroups(RunData rundata, Context context) {
    groupList = new ArrayList<AddressBookGroupResultData>();
    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      SelectQuery query = new SelectQuery(EipMAddressGroup.class);
      Expression exp = ExpressionFactory.matchExp(
          EipMAddressGroup.OWNER_ID_PROPERTY,
          Integer.valueOf(ALEipUtils.getUserId(rundata)));
      query.setQualifier(exp);
      query.addOrdering(EipMAddressGroup.GROUP_NAME_PROPERTY, Ordering.ASC);

      @SuppressWarnings("unchecked")
      List<EipMAddressGroup> aList = dataContext.performQuery(query);

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

  // ***************************************************************************
  // privateメソッド
  // ***************************************************************************

  /**
   * インデックス検索のためのユニコードマッピングによる条件文の追加。
   *
   * @param crt
   * @param idx
   */
  private void buildSelectQueryForAddressbookIndex(SelectQuery query,
      String lastNameKana, int idx) {

    // インデックスによる検索
    switch (idx) {
    // ア行
    case 1:
      Expression exp01 = ExpressionFactory.greaterOrEqualExp(lastNameKana, "ア");
      Expression exp02 = ExpressionFactory.lessExp(lastNameKana, "カ");
      query.andQualifier(exp01.andExp(exp02));
      break;
    // カ行
    case 6:
      Expression exp11 = ExpressionFactory.greaterOrEqualExp(lastNameKana, "カ");
      Expression exp12 = ExpressionFactory.lessExp(lastNameKana, "サ");
      query.andQualifier(exp11.andExp(exp12));
      break;
    // サ行
    case 11:
      Expression exp21 = ExpressionFactory.greaterOrEqualExp(lastNameKana, "サ");
      Expression exp22 = ExpressionFactory.lessExp(lastNameKana, "タ");
      query.andQualifier(exp21.andExp(exp22));
      break;
    // タ行
    case 16:
      Expression exp31 = ExpressionFactory.greaterOrEqualExp(lastNameKana, "タ");
      Expression exp32 = ExpressionFactory.lessExp(lastNameKana, "ナ");
      query.andQualifier(exp31.andExp(exp32));
      break;
    // ナ行
    case 21:
      Expression exp41 = ExpressionFactory.greaterOrEqualExp(lastNameKana, "ナ");
      Expression exp42 = ExpressionFactory.lessExp(lastNameKana, "ハ");
      query.andQualifier(exp41.andExp(exp42));
      break;
    // ハ行
    case 26:
      Expression exp51 = ExpressionFactory.greaterOrEqualExp(lastNameKana, "ハ");
      Expression exp52 = ExpressionFactory.lessExp(lastNameKana, "マ");
      query.andQualifier(exp51.andExp(exp52));
      break;
    // マ行
    case 31:
      Expression exp61 = ExpressionFactory.greaterOrEqualExp(lastNameKana, "マ");
      Expression exp62 = ExpressionFactory.lessExp(lastNameKana, "ヤ");
      query.andQualifier(exp61.andExp(exp62));
      break;
    // ヤ行
    case 36:
      Expression exp71 = ExpressionFactory.greaterOrEqualExp(lastNameKana, "ヤ");
      Expression exp72 = ExpressionFactory.lessExp(lastNameKana, "ラ");
      query.andQualifier(exp71.andExp(exp72));
      break;
    // ラ行
    case 41:
      Expression exp81 = ExpressionFactory.greaterOrEqualExp(lastNameKana, "ラ");
      Expression exp82 = ExpressionFactory.lessExp(lastNameKana, "ワ");
      query.andQualifier(exp81.andExp(exp82));
      break;
    // ワ行
    case 46:
      Expression exp91 = ExpressionFactory.greaterOrEqualExp(lastNameKana, "ワ");
      Expression exp92 = ExpressionFactory.lessOrEqualExp(lastNameKana, "ヴ");
      query.andQualifier(exp91.andExp(exp92));
      break;
    // 英数(上記以外)
    case 52:
      Expression exp100 = ExpressionFactory.lessExp(lastNameKana, "ア");
      Expression exp101 = ExpressionFactory
          .greaterOrEqualExp(lastNameKana, "ヴ");
      query.andQualifier(exp100.orExp(exp101));
      break;
    }
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   *
   * @return
   */
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_ADDRESSBOOK_ADDRESS_OUTSIDE;
  }
}

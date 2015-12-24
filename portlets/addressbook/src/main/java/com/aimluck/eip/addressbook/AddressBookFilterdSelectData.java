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
import com.aimluck.commons.utils.ALStringUtil;
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
      logger.error("AddressBookFilterdSelectData.selectList", ex);
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
      logger.error("AddressBookFilterdSelectData.selectDetail", ex);
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
        // 「その他」の会社情報ではない場合
        rd.setCompanyName(ALCommonUtils.compressString(
          company.getCompanyName(),
          getStrLength()));
        rd.setCompanyId(company.getCompanyId().toString());
        rd.setPostName(ALCommonUtils.compressString(
          company.getPostName(),
          getStrLength()));
        rd.setCompanyNameKana(ALCommonUtils.compressString(company
          .getCompanyNameKana(), getStrLength()));
        rd.setPostName(ALCommonUtils.compressString(
          company.getPostName(),
          getStrLength()));
        rd.setZipcode(ALCommonUtils.compressString(
          company.getZipcode(),
          getStrLength()));
        rd.setCompanyAddress(ALCommonUtils.compressString(
          company.getAddress(),
          getStrLength()));
        rd.setCompanyTelephone(ALCommonUtils.compressString(company
          .getTelephone(), getStrLength()));
        rd.setCompanyFaxNumber(ALCommonUtils.compressString(company
          .getFaxNumber(), getStrLength()));
        rd.setCompanyUrl(ALCommonUtils.compressString(
          company.getUrl(),
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
      rd.setNote(record.getNote());
      return rd;
    } catch (Exception ex) {
      logger.error("AddressBookFilterdSelectData.getResultData", ex);
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
      rd.setNote(record.getNote());
      rd.setPublicFlag(record.getPublicFlag());

      EipMAddressbookCompany company = record.getEipMAddressbookCompany();
      if (!AddressBookUtils.EMPTY_COMPANY_NAME.equals(company.getCompanyName())) {
        // 「その他」の会社情報ではない場合、会社情報を設定する
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
      rd.setCreatedUserId(record.getCreateUserId());
      rd.setUpdatedUserId(record.getUpdateUserId());
      return rd;
    } catch (Exception ex) {
      logger.error("AddressBookFilterdSelectData.getResultDataDetail", ex);
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
    SelectQuery<EipMAddressbook> query;
    String word = searchWord.toString();

    String transWord =
      ALStringUtil.convertHiragana2Katakana(ALStringUtil
        .convertH2ZKana(searchWord.toString()));
    transWord = transWord.replace("　", " "); // 全角スペースを半角スペースに変換する
    String[] transWords = transWord.split(" ");

    query = Database.query(EipMAddressbook.class);

    for (int i = 0; i < transWords.length; i++) {
      Expression exp01 =
        ExpressionFactory.matchExp(EipMAddressbook.PUBLIC_FLAG_PROPERTY, "T");
      Expression exp02 =
        ExpressionFactory.matchExp(
          EipMAddressbook.OWNER_ID_PROPERTY,
          ALEipUtils.getUserId(rundata));
      Expression exp03 =
        ExpressionFactory.matchExp(EipMAddressbook.PUBLIC_FLAG_PROPERTY, "F");
      query.setQualifier(exp01.orExp(exp02.andExp(exp03)));

      Expression exp11 =
        ExpressionFactory.likeExp(EipMAddressbook.FIRST_NAME_PROPERTY, "%"
          + word
          + "%");
      Expression exp12 =
        ExpressionFactory.likeExp(EipMAddressbook.LAST_NAME_PROPERTY, "%"
          + word
          + "%");
      Expression exp13 =
        ExpressionFactory.likeExp(EipMAddressbook.FIRST_NAME_KANA_PROPERTY, "%"
          + word
          + "%");
      Expression exp14 =
        ExpressionFactory.likeExp(EipMAddressbook.LAST_NAME_KANA_PROPERTY, "%"
          + word
          + "%");
      Expression exp15 =
        ExpressionFactory.likeExp(EipMAddressbook.EMAIL_PROPERTY, "%"
          + word
          + "%");
      Expression exp16 =
        ExpressionFactory.likeExp(EipMAddressbook.TELEPHONE_PROPERTY, "%"
          + word
          + "%");
      Expression exp17 =
        ExpressionFactory.likeExp(EipMAddressbook.CELLULAR_PHONE_PROPERTY, "%"
          + word
          + "%");
      Expression exp18 =
        ExpressionFactory.likeExp(EipMAddressbook.CELLULAR_MAIL_PROPERTY, "%"
          + word
          + "%");
      Expression exp19 =
        ExpressionFactory.likeExp(EipMAddressbook.NOTE_PROPERTY, "%"
          + word
          + "%");

      Expression exp21 =
        ExpressionFactory.likeExp(
          EipMAddressbook.EIP_MADDRESSBOOK_COMPANY_PROPERTY
            + "."
            + EipMAddressbookCompany.COMPANY_NAME_PROPERTY,
          "%" + word + "%");
      Expression exp22 =
        ExpressionFactory.likeExp(
          EipMAddressbook.EIP_MADDRESSBOOK_COMPANY_PROPERTY
            + "."
            + EipMAddressbookCompany.COMPANY_NAME_KANA_PROPERTY,
          "%" + word + "%");
      Expression exp23 =
        ExpressionFactory.likeExp(
          EipMAddressbook.EIP_MADDRESSBOOK_COMPANY_PROPERTY
            + "."
            + EipMAddressbookCompany.TELEPHONE_PROPERTY,
          "%" + word + "%");

      Expression exp31 =
        ExpressionFactory.likeExp(EipMAddressbook.FIRST_NAME_PROPERTY, "%"
          + transWords[i]
          + "%");
      Expression exp32 =
        ExpressionFactory.likeExp(EipMAddressbook.LAST_NAME_PROPERTY, "%"
          + transWords[i]
          + "%");
      Expression exp33 =
        ExpressionFactory.likeExp(EipMAddressbook.FIRST_NAME_KANA_PROPERTY, "%"
          + transWords[i]
          + "%");
      Expression exp34 =
        ExpressionFactory.likeExp(EipMAddressbook.LAST_NAME_KANA_PROPERTY, "%"
          + transWords[i]
          + "%");
      Expression exp35 =
        ExpressionFactory.likeExp(
          EipMAddressbook.EIP_MADDRESSBOOK_COMPANY_PROPERTY
            + "."
            + EipMAddressbookCompany.COMPANY_NAME_PROPERTY,
          "%" + transWords[i] + "%");
      Expression exp36 =
        ExpressionFactory.likeExp(
          EipMAddressbook.EIP_MADDRESSBOOK_COMPANY_PROPERTY
            + "."
            + EipMAddressbookCompany.COMPANY_NAME_KANA_PROPERTY,
          "%" + transWords[i] + "%");
      Expression exp37 =
        ExpressionFactory.likeExp(EipMAddressbook.NOTE_PROPERTY, "%"
          + transWords[i]
          + "%");

      Expression exp41 =
        ExpressionFactory.matchExp(EipMAddressbook.PUBLIC_FLAG_PROPERTY, "T");
      Expression exp42 =
        ExpressionFactory.matchExp(
          EipMAddressbook.OWNER_ID_PROPERTY,
          ALEipUtils.getUserId(rundata));
      Expression exp43 =
        ExpressionFactory.matchExp(EipMAddressbook.PUBLIC_FLAG_PROPERTY, "F");
      if (word != null && !"".equals(word)) {
        query.andQualifier((exp11.orExp(exp12).orExp(exp13).orExp(exp14).orExp(
          exp15).orExp(exp16).orExp(exp17).orExp(exp18).orExp(exp19).orExp(
          exp21).orExp(exp22).orExp(exp23).orExp(exp31).orExp(exp32).orExp(
          exp33).orExp(exp34).orExp(exp35).orExp(exp36).orExp(exp37))
          .andExp(exp41.orExp(exp42.andExp(exp43))));
      }
    }

    // query.setQualifier(exp41.orExp(exp42.andExp(exp43)));

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
      logger.error("AddressBookFilterdSelectData.loadGroups", ex);
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

  /**
   * 現在ページを設定します。
   *
   * @param page
   */
  public void setCurrentPage(int page) {
    current_page = page;
  }
}

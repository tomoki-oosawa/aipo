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

import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.addressbook.util.AddressBookUtils;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressGroup;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbook;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbookCompany;
import com.aimluck.eip.cayenne.om.portlet.EipTAddressbookGroupMap;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アドレス帳での検索BOX用データです。(社外アドレス検索用)
 * 
 */
public class AddressBookWordSelectData extends
    AbstractAddressBookWordSelectData<EipMAddressbook, EipMAddressbook> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AddressBookWordSelectData.class.getName());

  /** フィルタに利用するグループリスト */
  private List<AddressBookGroupResultData> groupList;

  /** マイグループリスト */
  private final List<ALEipGroup> myGroupList = null;

  /**
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
    super.init(action, rundata, context);
  }

  /**
   * 自分がオーナーのアドレスを取得
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<EipMAddressbook> selectList(RunData rundata,
      Context context) {
    ResultList<EipMAddressbook> list;

    try {
      SelectQuery<EipMAddressbook> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);
      list = query.getResultList();
    } catch (Exception ex) {
      logger.error("AddressBookWordSelectData.selectList", ex);
      return null;
    }
    return list;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected EipMAddressbook selectDetail(RunData rundata, Context context) {
    return null;
  }

  /**
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipMAddressbook obj) {
    try {
      AddressBookResultData rd = new AddressBookResultData();

      EipMAddressbook record = obj;
      rd.initField();
      rd.setAddressId(record.getAddressId().intValue());
      rd.setName(ALCommonUtils.compressString(new StringBuffer()
        .append(record.getLastName())
        .append(" ")
        .append(record.getFirstName())
        .toString(), getStrLength()));
      rd.setNameKana(ALCommonUtils.compressString(new StringBuffer()
        .append(record.getLastNameKana())
        .append(" ")
        .append(record.getFirstNameKana())
        .toString(), getStrLength()));

      EipMAddressbookCompany company = record.getEipMAddressbookCompany();
      // TODO: 「その他」の会社情報ではない場合
      if (!AddressBookUtils.EMPTY_COMPANY_NAME.equals(company.getCompanyName())) {
        rd.setCompanyId(company.getCompanyId().toString());
        rd.setCompanyName(ALCommonUtils.compressString(
          company.getCompanyName(),
          getStrLength()));
        rd.setPostName(ALCommonUtils.compressString(
          company.getPostName(),
          getStrLength()));
      }

      rd.setPositionName(ALCommonUtils.compressString(
        record.getPositionName(),
        getStrLength()));
      rd.setEmail(ALCommonUtils.compressString(
        record.getEmail(),
        getStrLength()));
      rd.setTelephone(record.getTelephone());
      rd.setCellularPhone(record.getCellularPhone());
      rd.setCellularMail(record.getCellularMail());
      rd.setPublicFlag(record.getPublicFlag());

      return rd;
    } catch (Exception ex) {
      logger.error("AddressBookWordSelectData.getResultData", ex);
      return null;
    }
  }

  /**
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipMAddressbook obj) {
    return null;
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
   * 
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipMAddressbook> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipMAddressbook> query = null;
    String word = searchWord.getValue();
    String transWord =
      ALStringUtil.convertHiragana2Katakana(ALStringUtil
        .convertH2ZKana(searchWord.getValue()));
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

      if (word != null && !"".equals(word)) {
        query.andQualifier(exp11.orExp(exp12).orExp(exp13).orExp(exp14).orExp(
          exp15).orExp(exp16).orExp(exp17).orExp(exp18).orExp(exp19).orExp(
          exp21).orExp(exp22).orExp(exp23).orExp(exp31).orExp(exp32).orExp(
          exp33).orExp(exp34).orExp(exp35).orExp(exp36).orExp(exp37));
      }
    }

    return query;
  }

  /**
   * 
   * @param rundata
   * @param context
   */
  @Override
  public void loadGroups(RunData rundata, Context context) {
    groupList = AddressBookUtils.getMyGroups(rundata);
  }

  /**
   * グループリストを取得します。
   * 
   * @return
   */
  @Override
  public List<AddressBookGroupResultData> getGroupList() {
    return groupList;
  }

  /**
   * マイグループリストを取得します。
   * 
   * @return
   */
  @Override
  public List<ALEipGroup> getMyGroupList() {
    return myGroupList;
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
   * @return
   */
  @Override
  public String getTemplateFilePath() {
    return "portlets/html/ajax-addressbook-list.vm";
  }
}

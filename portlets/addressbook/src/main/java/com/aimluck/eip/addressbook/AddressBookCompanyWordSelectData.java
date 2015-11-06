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

import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.addressbook.util.AddressBookUtils;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbookCompany;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アドレス帳での検索BOX用データです。
 * 
 */
public class AddressBookCompanyWordSelectData extends
    ALAbstractSelectData<EipMAddressbookCompany, EipMAddressbookCompany> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AddressBookCompanyWordSelectData.class.getName());

  /** 検索ワード */
  private ALStringField searchWord;

  private List<AddressBookGroupResultData> groupList;

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

    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "company_name_kana");
    }
    groupList = AddressBookUtils.getMyGroups(rundata);

    super.init(action, rundata, context);
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<EipMAddressbookCompany> selectList(RunData rundata,
      Context context) {

    // ページャからきた場合に検索ワードをセッションへ格納する
    if (!rundata.getParameters().containsKey(ALEipConstants.LIST_START)
      && !rundata.getParameters().containsKey(ALEipConstants.LIST_SORT)) {
      ALEipUtils.setTemp(rundata, context, "AddressBooksCompanyword", rundata
        .getParameters()
        .getString("sword"));
    }

    // 検索ワードの設定
    searchWord = new ALStringField();
    searchWord.setTrim(true);
    // セッションから値を取得する。
    // 検索ワード未指定時は空文字が入力される
    searchWord.setValue(ALEipUtils.getTemp(
      rundata,
      context,
      "AddressBooksCompanyword"));

    try {

      SelectQuery<EipMAddressbookCompany> query =
        getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      return query.getResultList();
    } catch (Exception ex) {
      logger.error("AddressBookCompanyWordSelectData.selectList", ex);
      return null;
    }
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected EipMAddressbookCompany selectDetail(RunData rundata, Context context) {
    return null;
  }

  /**
   * 
   * @param record
   * @return
   */
  @Override
  protected Object getResultData(EipMAddressbookCompany record) {
    try {
      AddressBookCompanyResultData rd = new AddressBookCompanyResultData();
      rd.initField();
      rd.setCompanyId(record.getCompanyId().intValue());
      rd.setCompanyName(ALCommonUtils.compressString(
        record.getCompanyName(),
        getStrLength()));
      rd.setCompanyNameKana(record.getCompanyNameKana());
      rd.setPostName(ALCommonUtils.compressString(
        record.getPostName(),
        getStrLength()));
      rd.setZipcode(record.getZipcode());
      rd.setAddress(ALCommonUtils.compressString(
        record.getAddress(),
        getStrLength()));
      rd.setTelephone(record.getTelephone());
      rd.setFaxNumber(record.getFaxNumber());
      rd.setUrl(record.getUrl());
      return rd;
    } catch (Exception ex) {
      logger.error("AddressBookCompanyWordSelectData.selectDetail", ex);
      return null;
    }
  }

  /**
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipMAddressbookCompany obj) {
    return null;
  }

  /**
   * 
   * @return
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue(
      "company_name_kana",
      EipMAddressbookCompany.COMPANY_NAME_KANA_PROPERTY);
    return map;
  }

  /**
   * 検索条件を設定した SelectQuery を返します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipMAddressbookCompany> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipMAddressbookCompany> query =
      Database.query(EipMAddressbookCompany.class);

    // exclude default company
    query.setQualifier(AddressBookUtils.excludeDefaultCompanyCriteria());

    String word = searchWord.getValue();
    String transWord =
      ALStringUtil.convertHiragana2Katakana(ALStringUtil
        .convertH2ZKana(searchWord.getValue()));

    Expression exp11 =
      ExpressionFactory.likeExp(
        EipMAddressbookCompany.COMPANY_NAME_PROPERTY,
        "%" + word + "%");
    Expression exp12 =
      ExpressionFactory.likeExp(
        EipMAddressbookCompany.COMPANY_NAME_KANA_PROPERTY,
        "%" + word + "%");
    Expression exp13 =
      ExpressionFactory.likeExp(EipMAddressbookCompany.POST_NAME_PROPERTY, "%"
        + word
        + "%");
    Expression exp14 =
      ExpressionFactory.likeExp(EipMAddressbookCompany.ZIPCODE_PROPERTY, "%"
        + word
        + "%");
    Expression exp15 =
      ExpressionFactory.likeExp(EipMAddressbookCompany.ADDRESS_PROPERTY, "%"
        + word
        + "%");
    Expression exp16 =
      ExpressionFactory.likeExp(EipMAddressbookCompany.TELEPHONE_PROPERTY, "%"
        + word
        + "%");
    Expression exp17 =
      ExpressionFactory.likeExp(EipMAddressbookCompany.FAX_NUMBER_PROPERTY, "%"
        + word
        + "%");
    Expression exp18 =
      ExpressionFactory.likeExp(EipMAddressbookCompany.URL_PROPERTY, "%"
        + word
        + "%");

    Expression exp21 =
      ExpressionFactory.likeExp(
        EipMAddressbookCompany.COMPANY_NAME_PROPERTY,
        "%" + transWord + "%");
    Expression exp22 =
      ExpressionFactory.likeExp(
        EipMAddressbookCompany.COMPANY_NAME_KANA_PROPERTY,
        "%" + transWord + "%");
    Expression exp23 =
      ExpressionFactory.likeExp(EipMAddressbookCompany.POST_NAME_PROPERTY, "%"
        + transWord
        + "%");
    Expression exp24 =
      ExpressionFactory.likeExp(EipMAddressbookCompany.ZIPCODE_PROPERTY, "%"
        + transWord
        + "%");
    Expression exp25 =
      ExpressionFactory.likeExp(EipMAddressbookCompany.ADDRESS_PROPERTY, "%"
        + transWord
        + "%");
    Expression exp26 =
      ExpressionFactory.likeExp(EipMAddressbookCompany.TELEPHONE_PROPERTY, "%"
        + transWord
        + "%");
    Expression exp27 =
      ExpressionFactory.likeExp(EipMAddressbookCompany.FAX_NUMBER_PROPERTY, "%"
        + transWord
        + "%");
    Expression exp28 =
      ExpressionFactory.likeExp(EipMAddressbookCompany.URL_PROPERTY, "%"
        + transWord
        + "%");

    query.andQualifier(exp11
      .orExp(exp12)
      .orExp(exp13)
      .orExp(exp14)
      .orExp(exp15)
      .orExp(exp16)
      .orExp(exp17)
      .orExp(exp18)
      .orExp(exp21)
      .orExp(exp22)
      .orExp(exp23)
      .orExp(exp24)
      .orExp(exp25)
      .orExp(exp26)
      .orExp(exp27)
      .orExp(exp28));

    return query;
  }

  /**
   * 検索ワードを取得します。
   * 
   * @return
   */
  public ALStringField getSearchWord() {
    return searchWord;
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

  public List<AddressBookGroupResultData> getGroupList() {
    return groupList;
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

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

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#init(com.aimluck.eip.modules.actions.common.ALAction,
   *      org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "company_name_kana");
    }

    super.init(action, rundata, context);
  }

  /**
   * 
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectList(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  @Override
  protected List<EipMAddressbookCompany> selectList(RunData rundata,
      Context context) {

    // ページャからきた場合に検索ワードをセッションへ格納する
    if (!rundata.getParameters().containsKey(ALEipConstants.LIST_START)
      && !rundata.getParameters().containsKey(ALEipConstants.LIST_SORT)) {
      ALEipUtils.setTemp(rundata, context, "AddressBooksCompanyword", rundata
        .getParameters().getString("sword"));
    }

    // 検索ワードの設定
    searchWord = new ALStringField();
    searchWord.setTrim(true);
    // セッションから値を取得する。
    // 検索ワード未指定時は空文字が入力される
    searchWord.setValue(ALEipUtils.getTemp(rundata, context,
      "AddressBooksCompanyword"));

    try {

      SelectQuery<EipMAddressbookCompany> query = getSelectQuery(rundata,
        context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      List<EipMAddressbookCompany> clist = query.perform();
      return buildPaginatedList(clist);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 未使用。
   * 
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectDetail(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  @Override
  protected EipMAddressbookCompany selectDetail(RunData rundata, Context context) {
    return null;
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultData(java.lang.Object)
   */
  @Override
  protected Object getResultData(EipMAddressbookCompany record) {
    try {
      AddressBookCompanyResultData rd = new AddressBookCompanyResultData();
      rd.initField();
      rd.setCompanyId(record.getCompanyId().intValue());
      rd.setCompanyName(ALCommonUtils.compressString(record.getCompanyName(),
        getStrLength()));
      rd.setCompanyNameKana(record.getCompanyNameKana());
      rd.setPostName(ALCommonUtils.compressString(record.getPostName(),
        getStrLength()));
      rd.setZipcode(record.getZipcode());
      rd.setAddress(ALCommonUtils.compressString(record.getAddress(),
        getStrLength()));
      rd.setTelephone(record.getTelephone());
      rd.setFaxNumber(record.getFaxNumber());
      rd.setUrl(record.getUrl());
      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 未使用。
   * 
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultDataDetail(java.lang.Object)
   */
  @Override
  protected Object getResultDataDetail(EipMAddressbookCompany obj) {
    return null;
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getColumnMap()
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("company_name_kana",
      EipMAddressbookCompany.COMPANY_NAME_KANA_PROPERTY);
    return map;
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipMAddressbookCompany> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipMAddressbookCompany> query = new SelectQuery<EipMAddressbookCompany>(
      EipMAddressbookCompany.class);

    //
    // Expression exp01 = ExpressionFactory.noMatchExp(
    // EipMAddressbookCompany.CREATE_USER_ID_PROPERTY, Integer.valueOf(1));
    Expression exp02 = ExpressionFactory.noMatchExp(
      EipMAddressbookCompany.COMPANY_NAME_PROPERTY,
      AddressBookUtils.EMPTY_COMPANY_NAME);
    query.setQualifier(exp02);

    String word = searchWord.getValue();
    String transWord = ALStringUtil.convertHiragana2Katakana(ALStringUtil
      .convertH2ZKana(searchWord.getValue()));

    Expression exp11 = ExpressionFactory.likeExp(
      EipMAddressbookCompany.COMPANY_NAME_PROPERTY, "%" + word + "%");
    Expression exp12 = ExpressionFactory.likeExp(
      EipMAddressbookCompany.COMPANY_NAME_KANA_PROPERTY, "%" + word + "%");
    Expression exp13 = ExpressionFactory.likeExp(
      EipMAddressbookCompany.POST_NAME_PROPERTY, "%" + word + "%");
    Expression exp14 = ExpressionFactory.likeExp(
      EipMAddressbookCompany.ZIPCODE_PROPERTY, "%" + word + "%");
    Expression exp15 = ExpressionFactory.likeExp(
      EipMAddressbookCompany.ADDRESS_PROPERTY, "%" + word + "%");
    Expression exp16 = ExpressionFactory.likeExp(
      EipMAddressbookCompany.TELEPHONE_PROPERTY, "%" + word + "%");
    Expression exp17 = ExpressionFactory.likeExp(
      EipMAddressbookCompany.FAX_NUMBER_PROPERTY, "%" + word + "%");
    Expression exp18 = ExpressionFactory.likeExp(
      EipMAddressbookCompany.URL_PROPERTY, "%" + word + "%");

    Expression exp21 = ExpressionFactory.likeExp(
      EipMAddressbookCompany.COMPANY_NAME_PROPERTY, "%" + transWord + "%");
    Expression exp22 = ExpressionFactory.likeExp(
      EipMAddressbookCompany.COMPANY_NAME_KANA_PROPERTY, "%" + transWord + "%");
    Expression exp23 = ExpressionFactory.likeExp(
      EipMAddressbookCompany.POST_NAME_PROPERTY, "%" + transWord + "%");
    Expression exp24 = ExpressionFactory.likeExp(
      EipMAddressbookCompany.ZIPCODE_PROPERTY, "%" + transWord + "%");
    Expression exp25 = ExpressionFactory.likeExp(
      EipMAddressbookCompany.ADDRESS_PROPERTY, "%" + transWord + "%");
    Expression exp26 = ExpressionFactory.likeExp(
      EipMAddressbookCompany.TELEPHONE_PROPERTY, "%" + transWord + "%");
    Expression exp27 = ExpressionFactory.likeExp(
      EipMAddressbookCompany.FAX_NUMBER_PROPERTY, "%" + transWord + "%");
    Expression exp28 = ExpressionFactory.likeExp(
      EipMAddressbookCompany.URL_PROPERTY, "%" + transWord + "%");

    query.andQualifier(exp11.orExp(exp12).orExp(exp13).orExp(exp14)
      .orExp(exp15).orExp(exp16).orExp(exp17).orExp(exp18).orExp(exp21).orExp(
        exp22).orExp(exp23).orExp(exp24).orExp(exp25).orExp(exp26).orExp(exp27)
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
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_ADDRESSBOOK_COMPANY;
  }
}

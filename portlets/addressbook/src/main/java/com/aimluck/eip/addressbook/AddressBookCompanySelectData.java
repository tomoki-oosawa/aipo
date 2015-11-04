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
 * アドレス帳会社情報の検索用データクラスです。
 * 
 */
public class AddressBookCompanySelectData extends
    ALAbstractSelectData<EipMAddressbookCompany, EipMAddressbookCompany> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AddressBookCompanySelectData.class.getName());

  /** 現在選択されているインデックス */
  private String index;

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
    try {
      SelectQuery<EipMAddressbookCompany> query =
        getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      return query.getResultList();
    } catch (Exception ex) {
      logger.error("AddressBookCompanySelectData.selectList", ex);
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
    try {
      EipMAddressbookCompany record;

      String companyId =
        ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
      if (companyId == null || Integer.valueOf(companyId) == null) {
        return null;
      }

      AddressBookCompanyResultData rd = new AddressBookCompanyResultData();
      rd.initField();

      record =
        Database.get(EipMAddressbookCompany.class, Integer.valueOf(companyId));
      return record;
    } catch (Exception ex) {
      logger.error("AddressBookCompanySelectData.selectDetail", ex);
      return null;
    }
  }

  /**
   * 
   * @param obj
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
      logger.error("AddressBookCompanySelectData.getResultData", ex);
      return null;
    }
  }

  /**
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipMAddressbookCompany record) {
    try {
      AddressBookCompanyResultData rd = new AddressBookCompanyResultData();
      rd.initField();

      rd.setCompanyId(record.getCompanyId().intValue());
      rd.setCompanyName(record.getCompanyName());
      rd.setCompanyNameKana(record.getCompanyNameKana());
      rd.setPostName(record.getPostName());
      rd.setZipcode(record.getZipcode());
      rd.setAddress(record.getAddress());
      rd.setTelephone(record.getTelephone());
      rd.setFaxNumber(record.getFaxNumber());
      rd.setUrl(record.getUrl());

      return rd;
    } catch (Exception ex) {
      logger.error("AddressBookCompanySelectData.getResultDataDetail", ex);
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

    query.setQualifier(AddressBookUtils.excludeDefaultCompanyCriteria());

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
        buildSelectQueryForAddressbookIndex(
          query,
          EipMAddressbookCompany.COMPANY_NAME_KANA_PROPERTY,
          Integer.parseInt(index));
        context.put("idx", index);
      }
    } else if (index_session != null) {
      buildSelectQueryForAddressbookIndex(
        query,
        EipMAddressbookCompany.COMPANY_NAME_KANA_PROPERTY,
        Integer.parseInt(index_session));
      context.put("idx", index);
    } else {
      context.put("idx", "-1");
    }
    return buildSelectQueryForFilter(query, rundata, context);
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
   * インデックス検索のためのユニコードマッピングによる条件文の追加。
   * 
   * @param crt
   * @param idx
   */
  private void buildSelectQueryForAddressbookIndex(
      SelectQuery<EipMAddressbookCompany> query, String companyNameKana, int idx) {
    // インデックスによる検索
    switch (idx) {
    // ア行
      case 1:
        Expression exp01 =
          ExpressionFactory.greaterOrEqualExp(companyNameKana, "ア");
        Expression exp02 = ExpressionFactory.lessExp(companyNameKana, "カ");
        query.andQualifier(exp01.andExp(exp02));
        break;
      // カ行
      case 6:
        Expression exp11 =
          ExpressionFactory.greaterOrEqualExp(companyNameKana, "カ");
        Expression exp12 = ExpressionFactory.lessExp(companyNameKana, "サ");
        query.andQualifier(exp11.andExp(exp12));
        break;
      // サ行
      case 11:
        Expression exp21 =
          ExpressionFactory.greaterOrEqualExp(companyNameKana, "サ");
        Expression exp22 = ExpressionFactory.lessExp(companyNameKana, "タ");
        query.andQualifier(exp21.andExp(exp22));
        break;
      // タ行
      case 16:
        Expression exp31 =
          ExpressionFactory.greaterOrEqualExp(companyNameKana, "タ");
        Expression exp32 = ExpressionFactory.lessExp(companyNameKana, "ナ");
        query.andQualifier(exp31.andExp(exp32));
        break;
      // ナ行
      case 21:
        Expression exp41 =
          ExpressionFactory.greaterOrEqualExp(companyNameKana, "ナ");
        Expression exp42 = ExpressionFactory.lessExp(companyNameKana, "ハ");
        query.andQualifier(exp41.andExp(exp42));
        break;
      // ハ行
      case 26:
        Expression exp51 =
          ExpressionFactory.greaterOrEqualExp(companyNameKana, "ハ");
        Expression exp52 = ExpressionFactory.lessExp(companyNameKana, "マ");
        query.andQualifier(exp51.andExp(exp52));
        break;
      // マ行
      case 31:
        Expression exp61 =
          ExpressionFactory.greaterOrEqualExp(companyNameKana, "マ");
        Expression exp62 = ExpressionFactory.lessExp(companyNameKana, "ヤ");
        query.andQualifier(exp61.andExp(exp62));
        break;
      // ヤ行
      case 36:
        Expression exp71 =
          ExpressionFactory.greaterOrEqualExp(companyNameKana, "ヤ");
        Expression exp72 = ExpressionFactory.lessExp(companyNameKana, "ラ");
        query.andQualifier(exp71.andExp(exp72));
        break;
      // ラ行
      case 41:
        Expression exp81 =
          ExpressionFactory.greaterOrEqualExp(companyNameKana, "ラ");
        Expression exp82 = ExpressionFactory.lessExp(companyNameKana, "ワ");
        query.andQualifier(exp81.andExp(exp82));
        break;
      // ワ行
      case 46:
        Expression exp91 =
          ExpressionFactory.greaterOrEqualExp(companyNameKana, "ワ");
        Expression exp92 =
          ExpressionFactory.lessOrEqualExp(companyNameKana, "ヴ");
        query.andQualifier(exp91.andExp(exp92));
        break;
      // 英数(上記以外)
      case 52:
        Expression exp100 = ExpressionFactory.lessExp(companyNameKana, "ア");
        Expression exp101 =
          ExpressionFactory.greaterOrEqualExp(companyNameKana, "ヴ");
        query.andQualifier(exp100.orExp(exp101));
        break;
      // default
      default:
        Expression exp111 = ExpressionFactory.lessExp(companyNameKana, "");
        Expression exp112 =
          ExpressionFactory.greaterOrEqualExp(companyNameKana, "");
        query.andQualifier(exp111.orExp(exp112));
    }
  }

  public List<AddressBookGroupResultData> getGroupList() {
    return groupList;
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

  /**
   * 現在ページを設定します。
   * 
   * @param page
   */
  public void setCurrentPage(int page) {
    current_page = page;
  }
}

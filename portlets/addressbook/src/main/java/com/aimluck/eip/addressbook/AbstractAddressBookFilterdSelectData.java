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

import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbook;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アドレス帳ワード検索用の基底データクラスです。
 * 
 */
public abstract class AbstractAddressBookFilterdSelectData<M1, M2> extends
    ALAbstractSelectData<M1, M2> {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AbstractAddressBookFilterdSelectData.class.getName());

  /** 「全て」を意味する検索用インデックス */
  private static final String INDEX_STR_ALL = "-1";

  /** 現在選択されているタブ */
  private String currentTab;

  /** 現在選択されているインデックス */
  private String index;

  /** 検索ワード */
  protected ALStringField searchWord;

  private boolean hasAuthorityList;

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

    // ページャからきた場合に検索ワードをセッションへ格納する
    if (!rundata.getParameters().containsKey(ALEipConstants.LIST_START)
      && !rundata.getParameters().containsKey(ALEipConstants.LIST_SORT)) {
      ALEipUtils.setTemp(rundata, context, "AddressBooksword", rundata
        .getParameters()
        .getString("sword"));
    }

    // 検索ワードの設定
    searchWord = new ALStringField();
    searchWord.setTrim(true);
    searchWord.setValue(ALEipUtils
      .getTemp(rundata, context, "AddressBooksword"));

    // 現在選択されているタブをセッションとパラメータから読み込みます。
    String tabParam = rundata.getParameters().getString("tab");
    currentTab = ALEipUtils.getTemp(rundata, context, "tab");
    if (tabParam == null && currentTab == null) {
      ALEipUtils.setTemp(rundata, context, "tab", "syagai");
      currentTab = "syagai";
    } else if (tabParam != null) {
      ALEipUtils.setTemp(rundata, context, "tab", tabParam);
      currentTab = tabParam;
    }

    hasAuthorityList =
      checkHasAuthority(rundata, ALAccessControlConstants.VALUE_ACL_LIST);

    // 検索用インデックスをセッションとパラメータからを読み込みます。
    String index_session = ALEipUtils.getTemp(rundata, context, LIST_INDEX_STR);
    String index_rundata = rundata.getParameters().getString("idx");
    if (index_rundata != null) {
      if (INDEX_STR_ALL.equals(index_rundata) || "".equals(index_rundata)) {
        ALEipUtils.setTemp(rundata, context, LIST_INDEX_STR, INDEX_STR_ALL);
        index = INDEX_STR_ALL;
      } else {
        index = index_rundata;
        ALEipUtils.setTemp(rundata, context, LIST_INDEX_STR, index);
      }
    } else if (index_session != null) {
      index = index_session;
    }

    super.init(action, rundata, context);
  }

  /**
   * 検索条件を設定した SelectQuery を返します。
   * 
   * @param query
   * @param rundata
   * @param context
   * @return
   */
  protected SelectQuery<M1> getSelectQueryForIndex(SelectQuery<M1> query,
      RunData rundata, Context context) {

    // インデックス指定時の条件文作成
    if (index != null && !INDEX_STR_ALL.equals(index)) {
      buildSelectQueryForAddressbookIndex(query, getColumnForIndex(), Integer
        .parseInt(index));
    }

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

  /**
   * 検索ワードを取得します。
   * 
   * @return
   */
  public ALStringField getSearchWord() {
    return searchWord;
  }

  /**
   * アクセス権限があるかどうか取得します。
   * 
   * @return
   */
  public boolean getHasAuthorityList() {
    return hasAuthorityList;
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
   * インデックス検索のためのカラムを返します。
   * 
   * @return
   */
  abstract protected String getColumnForIndex();

  /**
   * 現在選択されているタブに合わせてアクセス権限をチェックします。
   * 
   * @param rundata
   * @param type
   * @return
   */
  public boolean checkHasAuthority(RunData rundata, int type) {
    String feature =
      ALAccessControlConstants.POERTLET_FEATURE_ADDRESSBOOK_ADDRESS_INSIDE;
    if ("syagai".equals(currentTab)) {
      feature =
        ALAccessControlConstants.POERTLET_FEATURE_ADDRESSBOOK_ADDRESS_OUTSIDE;
    }

    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

    return aclhandler
      .hasAuthority(ALEipUtils.getUserId(rundata), feature, type);
  }

  /**
   * インデックス検索のためのユニコードマッピングによる条件文の追加。
   * 
   * @param crt
   * @param idx
   */
  private void buildSelectQueryForAddressbookIndex(SelectQuery<M1> query,
      String lastNameKana, int idx) {

    // インデックスによる検索
    switch (idx) {
    // ア行
      case 1:
        Expression exp01 =
          ExpressionFactory.greaterOrEqualExp(lastNameKana, "ア");
        Expression exp02 = ExpressionFactory.lessExp(lastNameKana, "カ");
        query.andQualifier(exp01.andExp(exp02));
        break;
      // カ行
      case 6:
        Expression exp11 =
          ExpressionFactory.greaterOrEqualExp(lastNameKana, "カ");
        Expression exp12 = ExpressionFactory.lessExp(lastNameKana, "サ");
        query.andQualifier(exp11.andExp(exp12));
        break;
      // サ行
      case 11:
        Expression exp21 =
          ExpressionFactory.greaterOrEqualExp(lastNameKana, "サ");
        Expression exp22 = ExpressionFactory.lessExp(lastNameKana, "タ");
        query.andQualifier(exp21.andExp(exp22));
        break;
      // タ行
      case 16:
        Expression exp31 =
          ExpressionFactory.greaterOrEqualExp(lastNameKana, "タ");
        Expression exp32 = ExpressionFactory.lessExp(lastNameKana, "ナ");
        query.andQualifier(exp31.andExp(exp32));
        break;
      // ナ行
      case 21:
        Expression exp41 =
          ExpressionFactory.greaterOrEqualExp(lastNameKana, "ナ");
        Expression exp42 = ExpressionFactory.lessExp(lastNameKana, "ハ");
        query.andQualifier(exp41.andExp(exp42));
        break;
      // ハ行
      case 26:
        Expression exp51 =
          ExpressionFactory.greaterOrEqualExp(lastNameKana, "ハ");
        Expression exp52 = ExpressionFactory.lessExp(lastNameKana, "マ");
        query.andQualifier(exp51.andExp(exp52));
        break;
      // マ行
      case 31:
        Expression exp61 =
          ExpressionFactory.greaterOrEqualExp(lastNameKana, "マ");
        Expression exp62 = ExpressionFactory.lessExp(lastNameKana, "ヤ");
        query.andQualifier(exp61.andExp(exp62));
        break;
      // ヤ行
      case 36:
        Expression exp71 =
          ExpressionFactory.greaterOrEqualExp(lastNameKana, "ヤ");
        Expression exp72 = ExpressionFactory.lessExp(lastNameKana, "ラ");
        query.andQualifier(exp71.andExp(exp72));
        break;
      // ラ行
      case 41:
        Expression exp81 =
          ExpressionFactory.greaterOrEqualExp(lastNameKana, "ラ");
        Expression exp82 = ExpressionFactory.lessExp(lastNameKana, "ワ");
        query.andQualifier(exp81.andExp(exp82));
        break;
      // ワ行
      case 46:
        Expression exp91 =
          ExpressionFactory.greaterOrEqualExp(lastNameKana, "ワ");
        Expression exp92 = ExpressionFactory.lessOrEqualExp(lastNameKana, "ヴ");
        query.andQualifier(exp91.andExp(exp92));
        break;
      // 英数(上記以外)
      case 52:
        Expression exp100 = ExpressionFactory.lessExp(lastNameKana, "ア");
        Expression exp101 =
          ExpressionFactory.greaterOrEqualExp(lastNameKana, "ヴ");
        query.andQualifier(exp100.orExp(exp101));
        break;
      // default
      default:
        Expression exp111 = ExpressionFactory.lessExp(lastNameKana, "");
        Expression exp112 =
          ExpressionFactory.greaterOrEqualExp(lastNameKana, "");
        query.andQualifier(exp111.orExp(exp112));
    }
  }

  @Override
  protected SelectQuery<M1> buildSelectQueryForListViewSort(
      SelectQuery<M1> query, RunData rundata, Context context) {
    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    String sort_type = ALEipUtils.getTemp(rundata, context, LIST_SORT_TYPE_STR);
    String crt_key = null;

    Attributes map = getColumnMap();
    if (sort == null) {
      return query;
    }
    crt_key = map.getValue(sort);
    if (crt_key == null) {
      return query;
    }
    if (sort_type != null
      && ALEipConstants.LIST_SORT_TYPE_DESC.equals(sort_type)) {
      query.orderDesending(crt_key);
      if (sort.equals("name_kana")) {
        query.orderDesending(EipMAddressbook.FIRST_NAME_KANA_PROPERTY);
      }
    } else {
      query.orderAscending(crt_key);
      if (sort.equals("name_kana")) {
        query.orderAscending(EipMAddressbook.FIRST_NAME_KANA_PROPERTY);
      }
      sort_type = ALEipConstants.LIST_SORT_TYPE_ASC;
    }
    current_sort = sort;
    current_sort_type = sort_type;
    return query;
  }
}

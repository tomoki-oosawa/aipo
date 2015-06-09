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
package com.aimluck.eip.addressbook;

import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

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
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アドレス帳での検索BOX用データです。(社内アドレス検索用)
 * 
 */
public abstract class AbstractAddressBookWordSelectData<M1, M2> extends
    ALAbstractSelectData<M1, M2> {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AddressBookWordSelectData.class.getName());

  /** 検索ワード */
  protected ALStringField searchWord;

  /** 現在選択されているタブ */
  protected String currentTab;

  private boolean hasAuthorityList;

  public static AbstractAddressBookWordSelectData<?, ?> createAddressBookWordSelectData(
      RunData rundata, Context context) {
    return new AddressBookWordSelectData();
  }

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
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "name_kana");
    }

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

    // 社内名簿廃止(2011/12/08)
    currentTab = "syagai";

    hasAuthorityList =
      checkHasAuthority(rundata, ALAccessControlConstants.VALUE_ACL_LIST);

    super.init(action, rundata, context);
  }

  /**
   * 
   * @param rundata
   * @param context
   */
  public abstract void loadGroups(RunData rundata, Context context);

  /**
   * PC用の検索結果画面のテンプレートのパスを得ます。
   * 
   * @return
   */
  public abstract String getTemplateFilePath();

  /**
   * 現在選択されているタブを取得します。
   * 
   * @return
   */
  public String getCurrentTab() {
    return currentTab;
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
   * 検索ワードを取得します。
   * 
   * @return
   */
  public ALStringField getSearchWord() {
    return searchWord;
  }

  /**
   * グループリストを取得します。
   * 
   * @return
   */
  public abstract List<AddressBookGroupResultData> getGroupList();

  /**
   * 
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  /**
   * 
   * @return
   */
  public abstract List<ALEipGroup> getMyGroupList();

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

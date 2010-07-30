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
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.addressbook.util.AddressBookUtils;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressGroup;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbook;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbookCompany;
import com.aimluck.eip.cayenne.om.portlet.EipTAddressbookGroupMap;
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アドレス帳での検索BOX用データです。
 *
 */
public class AddressBookWordSelectData extends ALAbstractSelectData {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(AddressBookWordSelectData.class.getName());

  /** 検索ワード */
  private ALStringField searchWord;

  /** 現在選択されているタブ */
  private String currentTab;

  /** フィルタに利用するグループリスト */
  private List<AddressBookGroupResultData> groupList;

  /** マイグループリスト */
  private List<ALEipGroup> myGroupList = null;

  private DataContext dataContext;

  /** アクセス権限の機能名 */
  private String aclPortletFeature = null;

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#init(com.aimluck.eip.modules.actions.common.ALAction,
   *      org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
   */
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "name_kana");
    }

    dataContext = DatabaseOrmService.getInstance().getDataContext();

    // ページャからきた場合に検索ワードをセッションへ格納する
    if (!rundata.getParameters().containsKey(ALEipConstants.LIST_START)
        && !rundata.getParameters().containsKey(ALEipConstants.LIST_SORT)) {
      ALEipUtils.setTemp(rundata, context, "AddressBooksword", rundata
          .getParameters().getString("sword"));
    }

    // 検索ワードの設定
    searchWord = new ALStringField();
    searchWord.setTrim(true);
    // searchWord.setValue(rundata.getParameters().getString("sword"));
    // セッションから値を取得する。
    // 検索ワード未指定時は空文字が入力される
    searchWord.setValue(ALEipUtils
        .getTemp(rundata, context, "AddressBooksword"));

    String tabParam = rundata.getParameters().getString("tab");
    currentTab = ALEipUtils.getTemp(rundata, context, "tab");
    if (tabParam == null && currentTab == null) {
      ALEipUtils.setTemp(rundata, context, "tab", "syagai");
      currentTab = "syagai";
    } else if (tabParam != null) {
      ALEipUtils.setTemp(rundata, context, "tab", tabParam);
      currentTab = tabParam;
    }

    // アクセス権
    if ("syagai".equals(currentTab)) {
      aclPortletFeature = ALAccessControlConstants.POERTLET_FEATURE_ADDRESSBOOK_ADDRESS_OUTSIDE;
    } else {
      aclPortletFeature = ALAccessControlConstants.POERTLET_FEATURE_ADDRESSBOOK_ADDRESS_INSIDE;
    }

    super.init(action, rundata, context);
  }

  /**
   * 自分がオーナーのアドレスを取得
   *
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectList(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected List<?> selectList(RunData rundata, Context context) {
    List<?> list;

    try {

      if ("syagai".equals(currentTab)) {
        // 社外アドレス検索時
        SelectQuery query = getSelectQuery(rundata, context);
        buildSelectQueryForListView(query);
        buildSelectQueryForListViewSort(query, rundata, context);
        list = dataContext.performQuery(query);
      } else if ("corp".equals(currentTab)) {
        // 社内アドレス検索時
        SelectQuery query = getSelectQuery(rundata, context);
        buildSelectQueryForListView(query);
        buildSelectQueryForListViewSort(query, rundata, context);
        list = dataContext.performQuery(query);
      } else {
        logger.info("unknown_addressTab_selected");
        return new ArrayList<Object>();
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
    return buildPaginatedList(list);
  }

  /**
   * 未使用。
   *
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectDetail(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected Object selectDetail(RunData rundata, Context context) {
    return null;
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultData(java.lang.Object)
   */
  protected Object getResultData(Object obj) {
    try {
      AddressBookResultData rd = new AddressBookResultData();

      if ("syagai".equals(currentTab)) {
        EipMAddressbook record = (EipMAddressbook) obj;
        rd.initField();
        rd.setAddressId(record.getAddressId().intValue());
        rd.setName(new StringBuffer().append(record.getLastName()).append(" ")
            .append(record.getFirstName()).toString());
        rd.setNameKana(new StringBuffer().append(record.getLastNameKana())
            .append(' ').append(record.getFirstNameKana()).toString());

        EipMAddressbookCompany company = record.getEipMAddressbookCompany();
        if (!AddressBookUtils.EMPTY_COMPANY_NAME.equals(company
            .getCompanyName())
        /* && company.getCreateUserId().intValue() != 1 */) {
          // 「未分類」の会社情報ではない場合
          rd.setCompanyId(company.getCompanyId().toString());
          rd.setCompanyName(ALCommonUtils.compressString(
              company.getCompanyName(), getStrLength()));
          rd.setPostName(ALCommonUtils.compressString(company.getPostName(),
              getStrLength()));
        }

        rd.setPositionName(ALCommonUtils.compressString(
            record.getPositionName(), getStrLength()));
        rd.setEmail(ALCommonUtils.compressString(record.getEmail(),
            getStrLength()));
        rd.setTelephone(record.getTelephone());
        rd.setCellularPhone(record.getCellularPhone());
        rd.setCellularMail(record.getCellularMail());
        rd.setPublicFlag(record.getPublicFlag());
      } else {
        // 社員用
        TurbineUser record = (TurbineUser) obj;
        rd.initField();
        rd.setAddressId(record.getUserId().intValue());
        rd.setName(new StringBuffer().append(record.getLastName()).append(" ")
            .append(record.getFirstName()).toString());
        if (record.getCompanyId().intValue() > 0) {
          rd.setCompanyName(ALCommonUtils.compressString(
              ALEipUtils.getCompanyName(record.getCompanyId().intValue()),
              getStrLength()));
        }

        rd.setPostList(AddressBookUtils.getPostBeanList(record.getUserId()
            .intValue()));

        if (record.getPositionId().intValue() > 0) {
          rd.setPositionName(ALCommonUtils.compressString(
              ALEipUtils.getPositionName(record.getPositionId()),
              getStrLength()));
        }
        rd.setEmail(ALCommonUtils.compressString(record.getEmail(),
            getStrLength()));
        rd.setTelephone(record.getOutTelephone());
        rd.setCellularPhone(record.getCellularPhone());
        rd.setCellularMail(record.getCellularMail());
      }

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
  protected Object getResultDataDetail(Object obj) {
    return null;
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getColumnMap()
   */
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();

    String currentTab = getCurrentTab();
    if (currentTab == null || currentTab.trim().length() == 0
        || "syagai".equals(currentTab)) {
      map.putValue("group", EipMAddressbook.EIP_TADDRESSBOOK_GROUP_MAP_PROPERTY
          + "." + EipTAddressbookGroupMap.EIP_TADDRESS_GROUP_PROPERTY + "."
          + EipMAddressGroup.GROUP_ID_PK_COLUMN);
      map.putValue("name_kana", EipMAddressbook.LAST_NAME_KANA_PROPERTY);
      map.putValue("company_name_kana",
          EipMAddressbook.EIP_MADDRESSBOOK_COMPANY_PROPERTY + "."
              + EipMAddressbookCompany.COMPANY_NAME_KANA_PROPERTY);
    } else {
      map.putValue("corp_group", TurbineUser.TURBINE_USER_GROUP_ROLE_PROPERTY
          + "." + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY + "."
          + TurbineGroup.GROUP_NAME_COLUMN);
      map.putValue("name_kana", TurbineUser.LAST_NAME_KANA_PROPERTY);
    }

    return map;
  }

  /**
   *
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery getSelectQuery(RunData rundata, Context context) {
    SelectQuery query = null;
    String word = searchWord.getValue();
    String transWord = ALStringUtil.convertHiragana2Katakana(ALStringUtil
        .convertH2ZKana(searchWord.getValue()));

    if ("syagai".equals(currentTab)) {
      query = new SelectQuery(EipMAddressbook.class);

      Expression exp01 = ExpressionFactory.matchExp(
          EipMAddressbook.PUBLIC_FLAG_PROPERTY, "T");
      Expression exp02 = ExpressionFactory.matchExp(
          EipMAddressbook.OWNER_ID_PROPERTY, ALEipUtils.getUserId(rundata));
      Expression exp03 = ExpressionFactory.matchExp(
          EipMAddressbook.PUBLIC_FLAG_PROPERTY, "F");
      query.setQualifier(exp01.orExp(exp02.andExp(exp03)));

      Expression exp11 = ExpressionFactory.likeExp(
          EipMAddressbook.FIRST_NAME_PROPERTY, "%" + word + "%");
      Expression exp12 = ExpressionFactory.likeExp(
          EipMAddressbook.LAST_NAME_PROPERTY, "%" + word + "%");
      Expression exp13 = ExpressionFactory.likeExp(
          EipMAddressbook.FIRST_NAME_KANA_PROPERTY, "%" + word + "%");
      Expression exp14 = ExpressionFactory.likeExp(
          EipMAddressbook.LAST_NAME_KANA_PROPERTY, "%" + word + "%");
      Expression exp15 = ExpressionFactory.likeExp(
          EipMAddressbook.EMAIL_PROPERTY, "%" + word + "%");
      Expression exp16 = ExpressionFactory.likeExp(
          EipMAddressbook.TELEPHONE_PROPERTY, "%" + word + "%");

      Expression exp17 = ExpressionFactory.likeExp(
          EipMAddressbook.EIP_MADDRESSBOOK_COMPANY_PROPERTY + "."
              + EipMAddressbookCompany.COMPANY_NAME_PROPERTY, "%" + word + "%");
      Expression exp18 = ExpressionFactory.likeExp(
          EipMAddressbook.EIP_MADDRESSBOOK_COMPANY_PROPERTY + "."
              + EipMAddressbookCompany.COMPANY_NAME_KANA_PROPERTY, "%" + word
              + "%");
      Expression exp19 = ExpressionFactory.likeExp(
          EipMAddressbook.EIP_MADDRESSBOOK_COMPANY_PROPERTY + "."
              + EipMAddressbookCompany.TELEPHONE_PROPERTY, "%" + word + "%");

      Expression exp31 = ExpressionFactory.likeExp(
          EipMAddressbook.FIRST_NAME_PROPERTY, "%" + transWord + "%");
      Expression exp32 = ExpressionFactory.likeExp(
          EipMAddressbook.LAST_NAME_PROPERTY, "%" + transWord + "%");
      Expression exp33 = ExpressionFactory.likeExp(
          EipMAddressbook.FIRST_NAME_KANA_PROPERTY, "%" + transWord + "%");
      Expression exp34 = ExpressionFactory.likeExp(
          EipMAddressbook.LAST_NAME_KANA_PROPERTY, "%" + transWord + "%");
      Expression exp35 = ExpressionFactory.likeExp(
          EipMAddressbook.EIP_MADDRESSBOOK_COMPANY_PROPERTY + "."
              + EipMAddressbookCompany.COMPANY_NAME_PROPERTY, "%" + transWord
              + "%");
      Expression exp36 = ExpressionFactory.likeExp(
          EipMAddressbook.EIP_MADDRESSBOOK_COMPANY_PROPERTY + "."
              + EipMAddressbookCompany.COMPANY_NAME_KANA_PROPERTY, "%"
              + transWord + "%");

      query.andQualifier(exp11.orExp(exp12).orExp(exp13).orExp(exp14)
          .orExp(exp15).orExp(exp16).orExp(exp17).orExp(exp18).orExp(exp19)
          .orExp(exp31).orExp(exp32).orExp(exp33).orExp(exp34).orExp(exp35)
          .orExp(exp36));

    } else if ("corp".equals(currentTab)) {
      query = new SelectQuery(TurbineUser.class);

      Expression exp01 = ExpressionFactory.matchExp(
          TurbineUser.DISABLED_PROPERTY, "F");
      query.setQualifier(exp01);

      Expression exp02 = ExpressionFactory.noMatchDbExp(
          TurbineUser.USER_ID_PK_COLUMN, Integer.valueOf(1));
      Expression exp03 = ExpressionFactory.noMatchDbExp(
          TurbineUser.USER_ID_PK_COLUMN, Integer.valueOf(2));
      Expression exp04 = ExpressionFactory.noMatchDbExp(
          TurbineUser.USER_ID_PK_COLUMN, Integer.valueOf(3));
      query.andQualifier(exp02.andExp(exp03).andExp(exp04));

      Expression exp11 = ExpressionFactory.likeExp(
          TurbineUser.FIRST_NAME_PROPERTY, "%" + word + "%");
      Expression exp12 = ExpressionFactory.likeExp(
          TurbineUser.LAST_NAME_PROPERTY, "%" + word + "%");
      Expression exp13 = ExpressionFactory.likeExp(
          TurbineUser.FIRST_NAME_KANA_PROPERTY, "%" + word + "%");
      Expression exp14 = ExpressionFactory.likeExp(
          TurbineUser.LAST_NAME_KANA_PROPERTY, "%" + word + "%");
      Expression exp15 = ExpressionFactory.likeExp(TurbineUser.EMAIL_PROPERTY,
          "%" + word + "%");
      Expression exp16 = ExpressionFactory.likeExp(
          TurbineUser.TURBINE_USER_GROUP_ROLE_PROPERTY + "."
              + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY + "."
              + TurbineGroup.GROUP_ALIAS_NAME_PROPERTY, "%" + word + "%");
      /*
       * Expression exp17 = ExpressionFactory.likeExp(
       * TurbineUser.EIP_MPOST_PROPERTY + "." + EipMPost.POST_NAME_PROPERTY, "%"
       * + word + "%");
       */
      Expression exp31 = ExpressionFactory.likeExp(
          TurbineUser.FIRST_NAME_PROPERTY, "%" + transWord + "%");
      Expression exp32 = ExpressionFactory.likeExp(
          TurbineUser.LAST_NAME_PROPERTY, "%" + transWord + "%");
      Expression exp33 = ExpressionFactory.likeExp(
          TurbineUser.FIRST_NAME_KANA_PROPERTY, "%" + transWord + "%");
      Expression exp34 = ExpressionFactory.likeExp(
          TurbineUser.LAST_NAME_KANA_PROPERTY, "%" + transWord + "%");
      Expression exp35 = ExpressionFactory.likeExp(
          TurbineUser.TURBINE_USER_GROUP_ROLE_PROPERTY + "."
              + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY + "."
              + TurbineGroup.GROUP_ALIAS_NAME_PROPERTY, "%" + transWord + "%");
      /*
       * Expression exp36 = ExpressionFactory.likeExp(
       * TurbineUser.EIP_MPOST_PROPERTY + "." + EipMPost.POST_NAME_PROPERTY, "%"
       * + transWord + "%");
       */
      query.andQualifier(exp11.orExp(exp12).orExp(exp13).orExp(exp14)
          .orExp(exp15).orExp(exp16).orExp(exp31).orExp(exp32).orExp(exp33)
          .orExp(exp34).orExp(exp35));
    }

    return query;
  }

  /**
   *
   * @param rundata
   * @param context
   */
  public void loadGroups(RunData rundata, Context context) {
    groupList = new ArrayList<AddressBookGroupResultData>();
    try {
      if ("syagai".equals(currentTab)) {
        // 自分がオーナのグループ指定
        SelectQuery query = new SelectQuery(EipMAddressGroup.class);
        Expression exp = ExpressionFactory.matchExp(
            EipMAddressGroup.OWNER_ID_PROPERTY,
            Integer.valueOf(ALEipUtils.getUserId(rundata)));
        query.setQualifier(exp);

        @SuppressWarnings("unchecked")
        List<EipMAddressGroup> aList = dataContext.performQuery(query);
        int size = aList.size();
        for (int i = 0; i < size; i++) {
          EipMAddressGroup record = aList.get(i);
          AddressBookGroupResultData rd = new AddressBookGroupResultData();
          rd.initField();
          rd.setGroupId(record.getGroupId().longValue());
          rd.setGroupName(record.getGroupName());
          groupList.add(rd);
        }

      } else if ("corp".equals(currentTab)) {
        // マイグループリストの作成
        List<ALEipGroup> myGroups = ALEipUtils.getMyGroups(rundata);

        myGroupList = new ArrayList<ALEipGroup>();
        int length = myGroups.size();
        for (int i = 0; i < length; i++) {
          myGroupList.add(myGroups.get(i));
        }
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
    }
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
   * グループリストを取得します。
   *
   * @return
   */
  public List<AddressBookGroupResultData> getGroupList() {
    return groupList;
  }

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
  public List<ALEipGroup> getMyGroupList() {
    return myGroupList;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   *
   * @return
   */
  public String getAclPortletFeature() {
    return aclPortletFeature;
  }
}

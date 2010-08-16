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
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.addressbook.util.AddressBookUtils;
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アドレス帳ワード検索用データクラスです。(社内アドレス検索用)
 *
 */
public class AddressBookCorpFilterdSelectData extends ALAbstractSelectData {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(AddressBookFilterdSelectData.class.getName());

  /** 現在選択されているタブ */
  private String currentTab;

  /** 現在選択されているインデックス */
  private String index;

  /** 現在選択されている社内用フィルター */
  // private String corpGroup;

  /** マイグループリスト */
  private List<ALEipGroup> myGroupList = null;

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
      ALEipUtils.setTemp(rundata, context, "tab", "corp");
      currentTab = "corp";
    } else if (tabParam != null) {
      ALEipUtils.setTemp(rundata, context, "tab", tabParam);
      currentTab = tabParam;
    }

    super.init(action, rundata, context);
  }

  /**
   *
   * @param rundata
   * @param context
   */
  public void loadMygroupList(RunData rundata, Context context) {
    try {
      // マイグループリストの作成
      List<ALEipGroup> myGroups = ALEipUtils.getMyGroups(rundata);
      myGroupList = new ArrayList<ALEipGroup>();
      int length = myGroups.size();
      for (int i = 0; i < length; i++) {
        myGroupList.add(myGroups.get(i));
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
    }
  }

  /**
   * アドレス情報の一覧を、グループ・一覧・社員単位で表示する。
   *
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectList(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected List<?> selectList(RunData rundata, Context context) {
    DataContext dataContext = DatabaseOrmService.getInstance().getDataContext();

    try {
      SelectQuery query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      @SuppressWarnings("unchecked")
      List<TurbineUser> list = dataContext.performQuery(query);
      return buildPaginatedList(list);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * アドレス帳の詳細情報を表示します。
   *
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectDetail(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected Object selectDetail(RunData rundata, Context context) {
    try {
      // 指定された ユーザIDを取得
      String userId = ALEipUtils.getTemp(rundata, context,
          ALEipConstants.ENTITY_ID);
      if (userId == null || Integer.valueOf(userId) == null)
        return null;

      return ALEipUtils.getBaseUser(Integer.valueOf(userId).intValue());
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultData(java.lang.Object)
   */
  protected Object getResultData(Object obj) {
    try {
      TurbineUser record = (TurbineUser) obj;

      AddressBookResultData rd = new AddressBookResultData();
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
            ALEipUtils.getPositionName(record.getPositionId()), getStrLength()));
      }

      rd.setEmail(record.getEmail());
      rd.setTelephone(record.getOutTelephone());
      rd.setInTelephone(record.getInTelephone());
      rd.setCellularPhone(record.getCellularPhone());
      rd.setCellularMail(record.getCellularMail());

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
  protected Object getResultDataDetail(Object obj) {
    try {
      ALBaseUser record = (ALBaseUser) obj;
      AddressBookResultData rd = new AddressBookResultData();
      rd.initField();
      // アドレスID の設定
      int userId = Integer.valueOf(record.getUserId()).intValue();
      rd.setAddressId(userId);
      rd.setName(new StringBuffer().append(record.getLastName()).append(' ')
          .append(record.getFirstName()).toString());
      rd.setNameKana(new StringBuffer().append(record.getLastNameKana())
          .append(' ').append(record.getFirstNameKana()).toString());
      rd.setEmail(record.getEmail());
      rd.setTelephone(record.getOutTelephone());
      rd.setInTelephone(record.getInTelephone());
      rd.setCellularPhone(record.getCellularPhone());
      rd.setCellularMail(record.getCellularMail());
      rd.setPostList(AddressBookUtils.getPostBeanList(userId));
      if (record.getPositionId() > 0) {
        rd.setPositionName(ALCommonUtils.compressString(
            ALEipUtils.getPositionName(record.getPositionId()), getStrLength()));
      }

      rd.setCreateDate(ALDateUtil.format(record.getCreated(), "yyyy年M月d日"));
      rd.setUpdateDate(ALDateUtil.format(record.getModified(), "yyyy年M月d日"));
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
    map.putValue("corp_group", TurbineUser.TURBINE_USER_GROUP_ROLE_PROPERTY
        + "." + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY + "."
        + TurbineGroup.GROUP_NAME_COLUMN);
    map.putValue("name_kana", TurbineUser.LAST_NAME_KANA_PROPERTY);
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
    SelectQuery query = new SelectQuery(TurbineUser.class);

    Expression exp11 = ExpressionFactory.matchExp(
        TurbineUser.DISABLED_PROPERTY, "F");
    query.setQualifier(exp11);
    Expression exp21 = ExpressionFactory.noMatchDbExp(
        TurbineUser.USER_ID_PK_COLUMN, Integer.valueOf(1));
    Expression exp22 = ExpressionFactory.noMatchDbExp(
        TurbineUser.USER_ID_PK_COLUMN, Integer.valueOf(2));
    Expression exp23 = ExpressionFactory.noMatchDbExp(
        TurbineUser.USER_ID_PK_COLUMN, Integer.valueOf(3));
    Expression exp24 = ExpressionFactory.noMatchDbExp(
        TurbineUser.USER_ID_PK_COLUMN, Integer.valueOf(4));
    query.andQualifier(exp21.andExp(exp22).andExp(exp23).andExp(exp24));

    // インデックス指定時の条件文作成
    if (rundata.getParameters().getString("idx") != null) {
      // 索引の保存
      index = rundata.getParameters().getString("idx");
      buildSelectQueryForAddressbookUserIndex(query,
          TurbineUser.LAST_NAME_KANA_PROPERTY,
          Integer.parseInt(rundata.getParameters().getString("idx")));
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
  private void buildSelectQueryForAddressbookUserIndex(SelectQuery query,
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
    return ALAccessControlConstants.POERTLET_FEATURE_ADDRESSBOOK_ADDRESS_INSIDE;
  }
}

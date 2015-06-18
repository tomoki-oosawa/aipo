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

import java.util.ArrayList;
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
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アドレス帳での検索BOX用データです。(社内アドレス検索用)
 * 
 */
public class AddressBookCorpWordSelectData extends
    AbstractAddressBookWordSelectData<TurbineUser, TurbineUser> {

  /** フィルタに利用するグループリスト */
  private List<AddressBookGroupResultData> groupList;

  /** マイグループリスト */
  private List<ALEipGroup> myGroupList = null;

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AddressBookWordSelectData.class.getName());

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
  protected ResultList<TurbineUser> selectList(RunData rundata, Context context) {
    ResultList<TurbineUser> list;
    try {
      SelectQuery<TurbineUser> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);
      list = query.getResultList();
    } catch (Exception ex) {
      logger.error("AddressBookCorpWordSelectData.selectList", ex);
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
  protected TurbineUser selectDetail(RunData rundata, Context context) {
    return null;
  }

  /**
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(TurbineUser obj) {
    try {
      return AddressBookUtils.getCorpResultData(obj, getStrLength());
    } catch (Exception ex) {
      logger.error("AddressBookCorpWordSelectData.getResultData", ex);
      return null;
    }
  }

  /**
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(TurbineUser obj) {
    return null;
  }

  /**
   * 
   * @return
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();

    map.putValue("corp_group", TurbineUser.TURBINE_USER_GROUP_ROLE_PROPERTY
      + "."
      + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY
      + "."
      + TurbineGroup.GROUP_NAME_COLUMN);
    map.putValue("name_kana", TurbineUser.LAST_NAME_KANA_PROPERTY);

    return map;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<TurbineUser> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<TurbineUser> query = null;
    String word = searchWord.getValue();
    String transWord =
      ALStringUtil.convertHiragana2Katakana(ALStringUtil
        .convertH2ZKana(searchWord.getValue()));

    query = Database.query(TurbineUser.class);

    Expression exp_exclude_my_group =
      ExpressionFactory.matchExp(TurbineUser.TURBINE_USER_GROUP_ROLE_PROPERTY
        + "."
        + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY
        + "."
        + TurbineGroup.OWNER_ID_PROPERTY, 1);

    Expression exp01 =
      ExpressionFactory.matchExp(TurbineUser.DISABLED_PROPERTY, "F");
    query.setQualifier(exp01);

    Expression exp02 =
      ExpressionFactory.noMatchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
        .valueOf(1));
    Expression exp03 =
      ExpressionFactory.noMatchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
        .valueOf(2));
    Expression exp04 =
      ExpressionFactory.noMatchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
        .valueOf(3));
    query.andQualifier(exp02.andExp(exp03).andExp(exp04));

    Expression exp11 =
      ExpressionFactory.likeExp(TurbineUser.FIRST_NAME_PROPERTY, "%"
        + word
        + "%");
    Expression exp12 =
      ExpressionFactory.likeExp(TurbineUser.LAST_NAME_PROPERTY, "%"
        + word
        + "%");
    Expression exp13 =
      ExpressionFactory.likeExp(TurbineUser.FIRST_NAME_KANA_PROPERTY, "%"
        + word
        + "%");
    Expression exp14 =
      ExpressionFactory.likeExp(TurbineUser.LAST_NAME_KANA_PROPERTY, "%"
        + word
        + "%");
    Expression exp15 =
      ExpressionFactory.likeExp(TurbineUser.EMAIL_PROPERTY, "%" + word + "%");
    Expression exp16 =
      ExpressionFactory.likeExp(TurbineUser.TURBINE_USER_GROUP_ROLE_PROPERTY
        + "."
        + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY
        + "."
        + TurbineGroup.GROUP_ALIAS_NAME_PROPERTY, "%" + word + "%");
    exp16 = exp16.andExp(exp_exclude_my_group);
    Expression exp21 =
      ExpressionFactory.likeExp(TurbineUser.OUT_TELEPHONE_PROPERTY, "%"
        + word
        + "%");
    Expression exp22 =
      ExpressionFactory.likeExp(TurbineUser.IN_TELEPHONE_PROPERTY, "%"
        + word
        + "%");
    Expression exp23 =
      ExpressionFactory.likeExp(TurbineUser.CELLULAR_PHONE_PROPERTY, "%"
        + word
        + "%");

    Expression exp31 =
      ExpressionFactory.likeExp(TurbineUser.FIRST_NAME_PROPERTY, "%"
        + transWord
        + "%");
    Expression exp32 =
      ExpressionFactory.likeExp(TurbineUser.LAST_NAME_PROPERTY, "%"
        + transWord
        + "%");
    Expression exp33 =
      ExpressionFactory.likeExp(TurbineUser.FIRST_NAME_KANA_PROPERTY, "%"
        + transWord
        + "%");
    Expression exp34 =
      ExpressionFactory.likeExp(TurbineUser.LAST_NAME_KANA_PROPERTY, "%"
        + transWord
        + "%");
    Expression exp35 =
      ExpressionFactory.likeExp(TurbineUser.TURBINE_USER_GROUP_ROLE_PROPERTY
        + "."
        + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY
        + "."
        + TurbineGroup.GROUP_ALIAS_NAME_PROPERTY, "%" + transWord + "%");
    exp35 = exp35.andExp(exp_exclude_my_group);
    if (word != null && !"".equals(word)) {
      query.andQualifier(exp11
        .orExp(exp12)
        .orExp(exp13)
        .orExp(exp14)
        .orExp(exp15)
        .orExp(exp16)
        .orExp(exp21)
        .orExp(exp22)
        .orExp(exp23)
        .orExp(exp31)
        .orExp(exp32)
        .orExp(exp33)
        .orExp(exp34)
        .orExp(exp35));
    }
    query.distinct();
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
    try {
      // マイグループリストの作成
      List<ALEipGroup> myGroups = ALEipUtils.getMyGroups(rundata);

      myGroupList = new ArrayList<ALEipGroup>();
      int length = myGroups.size();
      for (int i = 0; i < length; i++) {
        myGroupList.add(myGroups.get(i));
      }
    } catch (Exception ex) {
      logger.error("AddressBookCorpWordSelectData.loadGroups", ex);
    }
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
    return ALAccessControlConstants.POERTLET_FEATURE_ADDRESSBOOK_ADDRESS_INSIDE;
  }

  /**
   * @return
   */
  @Override
  public String getTemplateFilePath() {
    return "portlets/html/ajax-addressbook-corplist.vm";
  }
}

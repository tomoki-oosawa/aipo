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
import java.util.Map;
import java.util.jar.Attributes;

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
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アドレス帳ワード検索用データクラスです。(社内アドレス検索用)
 * 
 */
public class AddressBookCorpFilterdSelectData extends
    AbstractAddressBookFilterdSelectData<TurbineUser, ALBaseUser> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AddressBookFilterdSelectData.class.getName());

  /** マイグループリスト */
  private List<ALEipGroup> myGroupList = null;

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
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "name_kana");
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
      groupList = AddressBookUtils.getMyGroups(rundata);
    } catch (Exception ex) {
      logger.error("AddressBookCorpFilterdSelectData.loadMygroupList", ex);
    }
  }

  /**
   * アドレス情報の一覧を、グループ・一覧・社員単位で表示する。
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<TurbineUser> selectList(RunData rundata, Context context) {

    try {
      SelectQuery<TurbineUser> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      return query.getResultList();
    } catch (Exception ex) {
      logger.error("AddressBookCorpFilterdSelectData.selectList", ex);
      return null;
    }
  }

  /**
   * アドレス帳の詳細情報を表示します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ALBaseUser selectDetail(RunData rundata, Context context) {
    try {
      // 指定された ユーザIDを取得
      String userId =
        ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
      if (userId == null || Integer.valueOf(userId) == null) {
        return null;
      }

      return ALEipUtils.getBaseUser(Integer.valueOf(userId).intValue());
    } catch (Exception ex) {
      logger.error("AddressBookCorpFilterdSelectData.selectDetail", ex);
      return null;
    }
  }

  /**
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(TurbineUser record) {
    try {
      return AddressBookUtils.getCorpResultData(record, getStrLength());
    } catch (Exception ex) {
      logger.error("AddressBookCorpFilterdSelectData.getResultData", ex);
      return null;
    }
  }

  /**
   * 詳細情報の返却データ取得。
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(ALBaseUser record) {
    try {
      AddressBookResultData rd = new AddressBookResultData();
      rd.initField();
      // アドレスID の設定
      int userId = Integer.valueOf(record.getUserId()).intValue();
      rd.setAddressId(userId);
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
      rd.setTelephone(record.getOutTelephone());
      rd.setInTelephone(record.getInTelephone());
      rd.setCellularPhone(record.getCellularPhone());
      rd.setCellularMail(record.getCellularMail());
      rd.setPostList(AddressBookUtils.getPostBeanList(userId));
      if (record.getPositionId() > 0) {
        rd.setPositionName(ALCommonUtils.compressString(ALEipUtils
          .getPositionName(record.getPositionId()), getStrLength()));
      }
      rd.setCreateDate(ALDateUtil.format(record.getCreated(), "yyyy年M月d日"));
      rd.setUpdateDate(ALDateUtil.format(record.getModified(), "yyyy年M月d日"));
      return rd;
    } catch (Exception ex) {
      logger.error("AddressBookCorpFilterdSelectData.getResultDataDetail", ex);
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
    map.putValue("corp_group", TurbineUser.TURBINE_USER_GROUP_ROLE_PROPERTY
      + "."
      + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY
      + "."
      + TurbineGroup.GROUP_NAME_COLUMN);
    map.putValue("name_kana", TurbineUser.LAST_NAME_KANA_PROPERTY);
    return map;
  }

  /**
   * 検索条件を設定した SelectQuery を返します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<TurbineUser> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);

    Expression exp11 =
      ExpressionFactory.matchExp(TurbineUser.DISABLED_PROPERTY, "F");
    query.setQualifier(exp11);
    Expression exp21 =
      ExpressionFactory.noMatchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
        .valueOf(1));
    Expression exp22 =
      ExpressionFactory.noMatchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
        .valueOf(2));
    Expression exp23 =
      ExpressionFactory.noMatchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
        .valueOf(3));
    query.andQualifier(exp21.andExp(exp22).andExp(exp23));

    return getSelectQueryForIndex(query, rundata, context);
  }

  /**
   * インデックス検索のためのカラムを返します。
   * 
   * @return
   */
  @Override
  protected String getColumnForIndex() {
    return TurbineUser.LAST_NAME_KANA_PROPERTY;
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
   * アクセス権限チェック用メソッド。 アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_ADDRESSBOOK_ADDRESS_INSIDE;
  }

  public List<AddressBookGroupResultData> getGroupList() {
    return groupList;
  }
}

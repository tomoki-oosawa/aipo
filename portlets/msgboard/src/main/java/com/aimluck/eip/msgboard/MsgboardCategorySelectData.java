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
package com.aimluck.eip.msgboard;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardCategoryMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.msgboard.util.MsgboardUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 掲示板カテゴリ検索データを管理するクラスです。 <BR>
 *
 */
public class MsgboardCategorySelectData extends
    ALAbstractSelectData<EipTMsgboardCategoryMap, EipTMsgboardCategory>
    implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MsgboardCategorySelectData.class.getName());

  /** カテゴリ一覧 */
  private List<MsgboardCategoryResultData> categoryList;

  /** カテゴリの総数 */
  private int categorySum;

  /** ログインユーザ ID */
  private int uid;

  /** <code>members</code> 共有メンバー */
  private List<ALEipUser> members;

  /** 他人のカテゴリ詳細表示権限 */
  private boolean authority_detail;

  /** 他人のカテゴリ編集権限 */
  private boolean authority_edit;

  /** 他人のカテゴリ削除権限 */
  private boolean authority_delete;

  /**
   *
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "category_name");
      logger.debug("[MsgboardCategorySelectData] Init Parameter. : "
        + "category_name");
    }

    uid = ALEipUtils.getUserId(rundata);

    authority_detail =
      MsgboardUtils.checkPermission(
        rundata,
        context,
        ALAccessControlConstants.VALUE_ACL_DETAIL,
        ALAccessControlConstants.POERTLET_FEATURE_MSGBOARD_CATEGORY_OTHER);

    authority_edit =
      MsgboardUtils.checkPermission(
        rundata,
        context,
        ALAccessControlConstants.VALUE_ACL_UPDATE,
        ALAccessControlConstants.POERTLET_FEATURE_MSGBOARD_CATEGORY_OTHER);

    authority_delete =
      MsgboardUtils.checkPermission(
        rundata,
        context,
        ALAccessControlConstants.VALUE_ACL_DELETE,
        ALAccessControlConstants.POERTLET_FEATURE_MSGBOARD_CATEGORY_OTHER);

    super.init(action, rundata, context);
  }

  /**
   *
   * @param rundata
   * @param context
   */
  public void loadCategoryList(RunData rundata) {
    // カテゴリ一覧
    categoryList = MsgboardUtils.loadCategoryList(rundata);
  }

  /**
   * 一覧データを取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<EipTMsgboardCategoryMap> selectList(RunData rundata,
      Context context) {
    try {

      SelectQuery<EipTMsgboardCategoryMap> query =
        getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      ResultList<EipTMsgboardCategoryMap> list = query.getResultList();
      // 件数をセットする．
      categorySum = list.getTotalCount();
      return list;
    } catch (Exception ex) {
      logger.error("msgboard", ex);
      return null;
    }
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipTMsgboardCategoryMap> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipTMsgboardCategoryMap> query =
      Database.query(EipTMsgboardCategoryMap.class);

    Expression exp1 =
      ExpressionFactory.noMatchDbExp(
        EipTMsgboardCategoryMap.EIP_TMSGBOARD_CATEGORY_PROPERTY
          + "."
          + EipTMsgboardCategory.TURBINE_USER_PROPERTY
          + "."
          + TurbineUser.USER_ID_PK_COLUMN,
        Integer.valueOf(0));
    query.setQualifier(exp1);

    // アクセス制御

    int loginUserId = ALEipUtils.getUserId(rundata);

    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();
    boolean hasAclviewOther =
      aclhandler.hasAuthority(
        loginUserId,
        ALAccessControlConstants.POERTLET_FEATURE_MSGBOARD_CATEGORY_OTHER,
        ALAccessControlConstants.VALUE_ACL_LIST);

    Expression exp01 =
      ExpressionFactory.matchExp(
        EipTMsgboardCategoryMap.EIP_TMSGBOARD_CATEGORY_PROPERTY
          + "."
          + EipTMsgboardCategory.PUBLIC_FLAG_PROPERTY,
        MsgboardUtils.PUBLIC_FLG_VALUE_PUBLIC);
    Expression exp02 =
      ExpressionFactory.matchExp(
        EipTMsgboardCategoryMap.STATUS_PROPERTY,
        MsgboardUtils.STAT_VALUE_OWNER);
    Expression exp03 =
      ExpressionFactory.matchExp(
        EipTMsgboardCategoryMap.STATUS_PROPERTY,
        MsgboardUtils.STAT_VALUE_ALL);
    Expression exp11 =
      ExpressionFactory.matchExp(
        EipTMsgboardCategoryMap.EIP_TMSGBOARD_CATEGORY_PROPERTY
          + "."
          + EipTMsgboardCategory.PUBLIC_FLAG_PROPERTY,
        MsgboardUtils.PUBLIC_FLG_VALUE_NONPUBLIC);
    Expression exp12 =
      ExpressionFactory.matchExp(
        EipTMsgboardCategoryMap.USER_ID_PROPERTY,
        Integer.valueOf(ALEipUtils.getUserId(rundata)));

    if (!hasAclviewOther) {
      query.andQualifier((exp01.andExp(exp02.orExp(exp03))).orExp(exp11
        .andExp(exp12)));
    } else {
      query.andQualifier((exp01.andExp(exp02.orExp(exp03))).orExp(exp11
        .andExp(exp02.orExp(exp03))));
    }
    query.distinct(true);

    return query;
  }

  /**
   * 詳細データを取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected EipTMsgboardCategory selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    // オブジェクトモデルを取得
    return MsgboardUtils.getEipTMsgboardCategory(rundata, context, false);
  }

  /**
   * ResultDataを取得します。（一覧データ） <BR>
   *
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipTMsgboardCategoryMap record)
      throws ALPageNotFoundException, ALDBErrorException {
    MsgboardCategoryResultData rd = new MsgboardCategoryResultData();
    rd.initField();
    try {
      EipTMsgboardCategory category = record.getEipTMsgboardCategory();

      rd.setCategoryId(category.getCategoryId().intValue());

      rd.setCategoryName(ALCommonUtils.compressString(category
        .getCategoryName(), getStrLength()));

      rd.setNote(category.getNote());
      rd.setOwnerId(category.getTurbineUser().getUserId().longValue());
      // 公開/非公開を設定する．
      rd.setPublicFlag((MsgboardUtils.PUBLIC_FLG_VALUE_PUBLIC).equals(category
        .getPublicFlag()));
      rd.setOwnerName(ALEipUtils.getUserFullName(category
        .getTurbineUser()
        .getUserId()
        .intValue()));
      rd.setLoginUserId(uid);
    } catch (Exception e) {
      logger.error("[MsgboardCategorySelectData]", e);
      throw new ALDBErrorException();
    }
    return rd;
  }

  /**
   * ResultDataを取得します。（詳細データ） <BR>
   *
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTMsgboardCategory record)
      throws ALPageNotFoundException, ALDBErrorException {
    MsgboardCategoryResultData rd = new MsgboardCategoryResultData();
    rd.initField();
    try {
      String loginUserStatus = null;
      // 公開区分
      boolean public_flag =
        (MsgboardUtils.PUBLIC_FLG_VALUE_PUBLIC).equals(record.getPublicFlag());

      SelectQuery<EipTMsgboardCategoryMap> mapquery =
        Database.query(EipTMsgboardCategoryMap.class);
      Expression mapexp =
        ExpressionFactory.matchDbExp(
          EipTMsgboardCategory.CATEGORY_ID_PK_COLUMN,
          record.getCategoryId());
      mapquery.setQualifier(mapexp);

      List<EipTMsgboardCategoryMap> list = mapquery.fetchList();

      List<Integer> users = new ArrayList<Integer>();
      int size = list.size();
      if (size == 1) {
        EipTMsgboardCategoryMap map = list.get(0);
        users.add(map.getUserId());
        loginUserStatus = map.getStatus();
      } else {
        for (int i = 0; i < size; i++) {
          EipTMsgboardCategoryMap map = list.get(i);
          users.add(map.getUserId());
          if (uid == map.getUserId().intValue()) {
            loginUserStatus = map.getStatus();
          }
        }
      }

      SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
      Expression exp =
        ExpressionFactory.inDbExp(TurbineUser.USER_ID_PK_COLUMN, users);
      Expression nonDisabledexp =
        ExpressionFactory.noMatchExp(TurbineUser.DISABLED_PROPERTY, "T");
      query.setQualifier(exp.andExp(nonDisabledexp));

      members = ALEipUtils.getUsersFromSelectQuery(query);

      rd.setCategoryId(record.getCategoryId().intValue());
      rd.setCategoryName(record.getCategoryName());
      rd.setNote(record.getNote());
      // 公開/非公開を設定する．
      // rd.setPublicFlag("T".equals(record.getPublicFlag()));
      rd.setOwnerId(record.getTurbineUser().getUserId().intValue());
      rd.setOwnerName(ALEipUtils.getUserFullName(record
        .getTurbineUser()
        .getUserId()
        .intValue()));
      rd.setCreateDate(ALDateUtil.format(record.getCreateDate(), "yyyy年M月d日"));
      rd.setUpdateDate(ALDateUtil.format(record.getUpdateDate(), "yyyy年M月d日"));
      rd.setLoginUserId(uid);

      if (public_flag) {
        if ((MsgboardUtils.STAT_VALUE_ALL).equals(loginUserStatus)) {
          rd.setAccessFlag(MsgboardUtils.ACCESS_PUBLIC_ALL);
        } else {
          rd.setAccessFlag(MsgboardUtils.ACCESS_PUBLIC_MEMBER);
        }
      } else {
        if ((MsgboardUtils.STAT_VALUE_ALL).equals(loginUserStatus)) {
          rd.setAccessFlag(MsgboardUtils.ACCESS_SEACRET_SELF);
        } else {
          rd.setAccessFlag(MsgboardUtils.ACCESS_SEACRET_MEMBER);
        }
      }
    } catch (Exception e) {
      logger.error("[MsgboardCategorySelectData]", e);
      throw new ALDBErrorException();
    }
    return rd;
  }

  @Override
  public boolean doViewDetail(ALAction action, RunData rundata, Context context) {
    try {
      init(action, rundata, context);
      doCheckAclPermission(
        rundata,
        context,
        ALAccessControlConstants.VALUE_ACL_DETAIL);
      action.setMode(ALEipConstants.MODE_DETAIL);
      EipTMsgboardCategory obj = selectDetail(rundata, context);
      if (obj != null) {
        data = getResultDataDetail(obj);

        // 公開区分
        boolean public_flag =
          (MsgboardUtils.PUBLIC_FLG_VALUE_PUBLIC).equals(obj.getPublicFlag());

        boolean isMember = false;

        // このカテゴリを共有しているメンバーを取得
        SelectQuery<EipTMsgboardCategoryMap> mapquery =
          Database.query(EipTMsgboardCategoryMap.class);
        Expression mapexp =
          ExpressionFactory.matchDbExp(
            EipTMsgboardCategory.CATEGORY_ID_PK_COLUMN,
            obj.getCategoryId());
        mapquery.setQualifier(mapexp);

        List<EipTMsgboardCategoryMap> list = mapquery.fetchList();

        List<Integer> users = new ArrayList<Integer>();
        int size = list.size();
        for (int i = 0; i < size; i++) {
          EipTMsgboardCategoryMap map = list.get(i);
          users.add(map.getUserId());
          if (uid == map.getUserId().intValue()) {
            isMember = true;
          }
        }

        // 自分がメンバーではない非公開のカテゴリで、他人のカテゴリの詳細表示権限がない場合はエラー
        if (!public_flag && !isMember && !authority_detail) {
          throw new ALPermissionException();
        }
      }

      action.setResultData(this);
      action.putData(rundata, context);
      return (data != null);
    } catch (ALPermissionException e) {
      ALEipUtils.redirectPermissionError(rundata);
      return false;
    } catch (ALPageNotFoundException e) {
      ALEipUtils.redirectPageNotFound(rundata);
      return false;
    } catch (ALDBErrorException e) {
      ALEipUtils.redirectDBError(rundata);
      return false;
    }
  }

  /**
   *
   * @return
   */
  public List<MsgboardCategoryResultData> getCategoryList() {
    return categoryList;
  }

  /**
   * @return
   *
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue(
      "category_name",
      EipTMsgboardCategoryMap.EIP_TMSGBOARD_CATEGORY_PROPERTY
        + "."
        + EipTMsgboardCategory.CATEGORY_NAME_PROPERTY);
    map.putValue(
      "create_user",
      EipTMsgboardCategoryMap.EIP_TMSGBOARD_CATEGORY_PROPERTY
        + "."
        + EipTMsgboardCategory.TURBINE_USER_PROPERTY
        + "."
        + TurbineUser.LAST_NAME_KANA_PROPERTY);
    return map;
  }

  public int getCategorySum() {
    return categorySum;
  }

  public int getUserId() {
    return uid;
  }

  /**
   *
   * @param id
   * @return
   */
  public boolean isMatch(int id1, long id2) {
    return id1 == (int) id2;
  }

  public boolean getAuthorityEdit() {
    return authority_edit;
  }

  public boolean getAuthorityDelete() {
    return authority_delete;
  }

  /**
   * 共有メンバーを取得します。
   *
   * @return
   */
  public List<ALEipUser> getMemberList() {
    return members;
  }

  /**
   * アクセス権限チェック用メソッド。 アクセス権限の機能名を返します。
   *
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_MSGBOARD_CATEGORY;
  }

}

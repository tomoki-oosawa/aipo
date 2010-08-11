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
package com.aimluck.eip.category;

import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.category.util.CommonCategoryUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTCommonCategory;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALDataContext;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 共有カテゴリ検索データを管理するクラスです。 <br />
 *
 */
public class CommonCategorySelectData extends
    ALAbstractSelectData<EipTCommonCategory> implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(CommonCategorySelectData.class.getName());

  /** ログインユーザ ID */
  private int uid;

  /** 他人の共有カテゴリ編集権限 */
  private boolean authority_edit;

  /** 他人の共有カテゴリ削除権限 */
  private boolean authority_delete;

  /** 他人の共有カテゴリ一覧表示権限 */
  private boolean authority_list;

  /** 他人の共有カテゴリ詳細表示権限 */
  private boolean authority_detail;

  /**
   *
   * @param action
   * @param rundata
   * @param context
   * @see com.aimluck.eip.common.ALAbstractSelectData#init(com.aimluck.eip.modules.actions.common.ALAction,
   *      org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
   */
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    uid = ALEipUtils.getUserId(rundata);

    authority_list = CommonCategoryUtils
        .CheckPermission(
            rundata,
            context,
            ALAccessControlConstants.VALUE_ACL_LIST,
            ALAccessControlConstants.POERTLET_FEATURE_MANHOUR_COMMON_CATEGORY_OTHER);

    authority_detail = CommonCategoryUtils
        .CheckPermission(
            rundata,
            context,
            ALAccessControlConstants.VALUE_ACL_DETAIL,
            ALAccessControlConstants.POERTLET_FEATURE_MANHOUR_COMMON_CATEGORY_OTHER);

    authority_edit = CommonCategoryUtils
        .CheckPermission(
            rundata,
            context,
            ALAccessControlConstants.VALUE_ACL_UPDATE,
            ALAccessControlConstants.POERTLET_FEATURE_MANHOUR_COMMON_CATEGORY_OTHER);

    authority_delete = CommonCategoryUtils
        .CheckPermission(
            rundata,
            context,
            ALAccessControlConstants.VALUE_ACL_DELETE,
            ALAccessControlConstants.POERTLET_FEATURE_MANHOUR_COMMON_CATEGORY_OTHER);

    super.init(action, rundata, context);
  }

  /**
   * 一覧データを取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectList(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected List<EipTCommonCategory> selectList(RunData rundata, Context context) {
    try {

      SelectQuery query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      List<EipTCommonCategory> list = ALDataContext.performQuery(
          EipTCommonCategory.class, query);
      return buildPaginatedList(list);
    } catch (Exception ex) {
      logger.error("Exception", ex);
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
  private SelectQuery getSelectQuery(RunData rundata, Context context) {
    SelectQuery query = new SelectQuery(EipTCommonCategory.class);

    Expression exp = ExpressionFactory.noMatchDbExp(
        EipTCommonCategory.COMMON_CATEGORY_ID_PK_COLUMN, Integer.valueOf(1));
    query.setQualifier(exp);

    if (!authority_list) {
      Expression exp2 = ExpressionFactory.matchExp(
          EipTCommonCategory.CREATE_USER_ID_PROPERTY, Integer.valueOf(uid));
      query.andQualifier(exp2);
    }

    return query;
  }

  /**
   * 詳細データを取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectDetail(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected EipTCommonCategory selectDetail(RunData rundata, Context context) {
    // オブジェクトモデルを取得
    return CommonCategoryUtils.getEipTCommonCategory(rundata, context);
  }

  /**
   * ResultDataを取得します。（一覧データ） <BR>
   *
   * @param obj
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getListData(java.lang.Object)
   */
  protected Object getResultData(EipTCommonCategory obj) {
    EipTCommonCategory record = (EipTCommonCategory) obj;
    CommonCategoryResultData rd = new CommonCategoryResultData();
    rd.initField();
    rd.setCommonCategoryId(record.getCommonCategoryId().longValue());
    rd.setName(ALCommonUtils.compressString(record.getName(), getStrLength()));
    rd.setCreateUserId(record.getCreateUserId().longValue());
    return rd;
  }

  /**
   * ResultDataを取得します。（詳細データ） <BR>
   *
   * @param obj
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultDataDetail(java.lang.Object)
   */
  protected Object getResultDataDetail(EipTCommonCategory obj) {
    EipTCommonCategory record = (EipTCommonCategory) obj;
    CommonCategoryResultData rd = new CommonCategoryResultData();
    rd.initField();
    rd.setCommonCategoryId(record.getCommonCategoryId().longValue());
    rd.setName(record.getName());
    rd.setNote(record.getNote());
    rd.setCreateUserId(record.getCreateUserId().intValue());
    rd.setCreateDate(ALDateUtil.format(record.getCreateDate(), "yyyy年M月d日"));
    rd.setUpdateDate(ALDateUtil.format(record.getUpdateDate(), "yyyy年M月d日"));
    return rd;
  }

  /**
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getColumnMap()
   */
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("name", EipTCommonCategory.NAME_PROPERTY);
    return map;
  }

  /**
   * ログインユーザID
   *
   * @return
   */
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

  public boolean getAuthorityList() {
    return authority_list;
  }

  public boolean getAuthorityDetail() {
    return authority_detail;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   *
   * @return
   */
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_MANHOUR_COMMON_CATEGORY;
  }
}

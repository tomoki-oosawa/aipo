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
package com.aimluck.eip.blog;

import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.blog.util.BlogUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogThema;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ブログテーマ検索データを管理するクラスです。 <BR>
 *
 */
public class BlogThemaSelectData extends
    ALAbstractSelectData<EipTBlogThema, EipTBlogThema> implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(BlogThemaSelectData.class.getName());

  /** テーマの総数 */
  private int themaSum;

  private List<BlogThemaResultData> themaList;

  private int uid;

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
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "thema_name");
      logger
        .debug("[BlogCategorySelectData] Init Parameter. : " + "thema_name");
    }

    themaList = BlogUtils.getThemaList(rundata, context);
    uid = ALEipUtils.getUserId(rundata);

    super.init(action, rundata, context);
  }

  /**
   * 一覧データを取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<EipTBlogThema> selectList(RunData rundata,
      Context context) {
    try {
      SelectQuery<EipTBlogThema> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      ResultList<EipTBlogThema> list = query.getResultList();
      // 件数をセットする．
      themaSum = list.getTotalCount();
      return list;
    } catch (Exception ex) {
      logger.error("blog", ex);
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
  private SelectQuery<EipTBlogThema> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipTBlogThema> query = Database.query(EipTBlogThema.class);

    Expression exp =
      ExpressionFactory.noMatchDbExp(EipTBlogThema.THEMA_ID_PK_COLUMN, Integer
        .valueOf(1));
    query.setQualifier(exp);

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
  protected EipTBlogThema selectDetail(RunData rundata, Context context) {
    // オブジェクトモデルを取得
    return BlogUtils.getEipTBlogThema(rundata, context);
  }

  /**
   * ResultDataを取得します。（一覧データ） <BR>
   *
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipTBlogThema record) {
    BlogThemaResultData rd = new BlogThemaResultData();
    rd.initField();
    rd.setThemaId(record.getThemaId().longValue());
    rd.setThemaName(ALCommonUtils.compressString(
      record.getThemaName(),
      getStrLength()));
    rd.setDescription(record.getDescription());
    return rd;
  }

  /**
   * ResultDataを取得します。（詳細データ） <BR>
   *
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTBlogThema record) {
    BlogThemaResultData rd = new BlogThemaResultData();
    rd.initField();
    rd.setThemaId(record.getThemaId().longValue());
    rd.setThemaName(record.getThemaName());
    rd.setDescription(record.getDescription());
    if ((int) rd.getThemaId().getValue() != 1) {
      rd.setCreateUserId(record.getCreateUserId().longValue());
      rd.setCreateUserName(BlogUtils.getUserFullName(record
        .getCreateUserId()
        .intValue()));
    }
    rd.setUpdateUserId(record.getUpdateUserId().longValue());
    rd.setUpdateUserName(BlogUtils.getUserFullName(record
      .getUpdateUserId()
      .intValue()));
    if ((int) rd.getThemaId().getValue() != 1) {
      rd.setCreateDate(ALDateUtil.format(record.getCreateDate(), "yyyy年M月d日"));
    }
    rd.setUpdateDate(ALDateUtil.format(record.getUpdateDate(), "yyyy年M月d日"));
    return rd;
  }

  /**
   * @return
   *
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("thema_name", EipTBlogThema.THEMA_NAME_PROPERTY);
    return map;
  }

  public int getThemaSum() {
    return themaSum;
  }

  public List<BlogThemaResultData> getThemaList() {
    return themaList;
  }

  public int getLoginUid() {
    return uid;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   *
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_BLOG_THEME;
  }
}

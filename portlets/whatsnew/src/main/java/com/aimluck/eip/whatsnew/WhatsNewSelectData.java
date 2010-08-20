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
package com.aimluck.eip.whatsnew;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTWhatsNew;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.whatsnew.util.WhatsNewUtils;

/**
 * 新着情報の検索データを管理するクラスです。 <BR>
 * 
 */

public class WhatsNewSelectData extends
    ALAbstractSelectData<WhatsNewContainer, WhatsNewContainer> implements
    ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WhatsNewSelectData.class.getName());

  /** ログインユーザーID */
  private int uid;

  /** 保持期間 */
  private int viewSpan;

  /** 保持件数 */
  private int viewNum;

  /**
   *
   */
  @Override
  public void initField() {
    viewSpan = 0;
    viewNum = 100;
    super.initField();
  }

  /** 親レコード(parentId!=0)のIDリスト */
  public List<Integer> parentIds;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * @see com.aimluck.eip.common.ALAbstractSelectData#init(com.aimluck.eip.modules.actions.common.ALAction,
   *      org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    uid = ALEipUtils.getUserId(rundata);
    parentIds = new ArrayList<Integer>();
    DataContext dataContext = DatabaseOrmService.getInstance().getDataContext();
    SelectQuery query = new SelectQuery(EipTWhatsNew.class);

    /** 既読判定の指定 */
    Expression exp1 =
      ExpressionFactory.matchExp(EipTWhatsNew.PARENT_ID_PROPERTY, Integer
        .valueOf(0));
    query.setQualifier(exp1.notExp());

    /** 自分の既読の指定 */
    Expression exp2 =
      ExpressionFactory.matchExp(EipTWhatsNew.USER_ID_PROPERTY, Integer
        .valueOf(uid));

    query.andQualifier(exp2);
    @SuppressWarnings("unchecked")
    List<EipTWhatsNew> readflags = dataContext.performQuery(query);
    for (int i = 0; i < readflags.size(); i++) {
      parentIds.add(readflags.get(i).getParentId());
    }
    super.init(action, rundata, context);
  }

  /**
   * 一覧データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   * @see com.aimluck.eip.common.ALAbstractListData#selectData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  @Override
  public ResultList<WhatsNewContainer> selectList(RunData rundata,
      Context context) {
    try {
      /** 31日以上たった新着情報を削除する */
      WhatsNewUtils.removeMonthOverWhatsNew();

      DataContext dataContext =
        DatabaseOrmService.getInstance().getDataContext();

      List<WhatsNewContainer> list = new ArrayList<WhatsNewContainer>();
      list.add(getContainerPublic(
        rundata,
        context,
        dataContext,
        WhatsNewUtils.WHATS_NEW_TYPE_BLOG_ENTRY));
      list.add(getContainer(
        rundata,
        context,
        dataContext,
        WhatsNewUtils.WHATS_NEW_TYPE_BLOG_COMMENT));
      list.add(getContainerBoth(
        rundata,
        context,
        dataContext,
        WhatsNewUtils.WHATS_NEW_TYPE_MSGBOARD_TOPIC));
      list.add(getContainer(
        rundata,
        context,
        dataContext,
        WhatsNewUtils.WHATS_NEW_TYPE_SCHEDULE));
      list.add(getContainer(
        rundata,
        context,
        dataContext,
        WhatsNewUtils.WHATS_NEW_TYPE_WORKFLOW_REQUEST));
      list.add(getContainer(
        rundata,
        context,
        dataContext,
        WhatsNewUtils.WHATS_NEW_TYPE_NOTE));

      return new ResultList<WhatsNewContainer>(list);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }

  }

  private WhatsNewContainer getContainer(RunData rundata, Context context,
      DataContext dataContext, int type) {
    WhatsNewContainer con = new WhatsNewContainer();
    SelectQuery query = new SelectQuery(EipTWhatsNew.class);
    Expression exp =
      ExpressionFactory.matchExp(EipTWhatsNew.USER_ID_PROPERTY, Integer
        .valueOf(uid));
    query.setQualifier(exp);
    Expression exp2 =
      ExpressionFactory.matchExp(EipTWhatsNew.PORTLET_TYPE_PROPERTY, Integer
        .valueOf(type));
    query.andQualifier(exp2);
    Expression exp3 =
      ExpressionFactory.matchExp(EipTWhatsNew.PARENT_ID_PROPERTY, Integer
        .valueOf("-1"));
    query.andQualifier(exp3);

    /** 表示期限の条件を追加する */
    query = addSpanCriteria(query);

    query.addOrdering(EipTWhatsNew.UPDATE_DATE_PROPERTY, false);
    @SuppressWarnings("unchecked")
    List<EipTWhatsNew> temp = dataContext.performQuery(query);
    con.setList(temp);
    con.setType(type);

    return con;
  }

  private WhatsNewContainer getContainerPublic(RunData rundata,
      Context context, DataContext dataContext, int type) {
    WhatsNewContainer con = new WhatsNewContainer();
    SelectQuery query = new SelectQuery(EipTWhatsNew.class);

    /** blogのtypeを指定 */
    Expression exp1 =
      ExpressionFactory.matchExp(EipTWhatsNew.PORTLET_TYPE_PROPERTY, type);
    query.setQualifier(exp1);

    /** 既読済みのレコード外し */
    // if (parentIds != null && parentIds.size() > 0) {
    // Expression exp2 = ExpressionFactory.inDbExp(
    // EipTWhatsNew.WHATSNEW_ID_PK_COLUMN, parentIds);
    // query.andQualifier(exp2.notExp());
    // }

    /** 記事（parent_id = 0）の指定 */
    Expression exp3 =
      ExpressionFactory.matchExp(EipTWhatsNew.PARENT_ID_PROPERTY, Integer
        .valueOf(0));
    query.andQualifier(exp3);

    /** 表示期限の条件を追加する */
    query = addSpanCriteria(query);

    /** 表示件数の条件を追加する */
    query = addNumberCriteria(query);

    query.addOrdering(EipTWhatsNew.UPDATE_DATE_PROPERTY, false);
    @SuppressWarnings("unchecked")
    List<EipTWhatsNew> result = dataContext.performQuery(query);

    /** 既読物を抜く */
    List<EipTWhatsNew> filterd_result = new ArrayList<EipTWhatsNew>();
    int size = result.size();
    if (null != result && size > 0) {

      for (int i = 0; i < size; i++) {
        int id = result.get(i).getWhatsNewId().intValue();
        if (parentIds.indexOf(id) == -1) {
          filterd_result.add(result.get(i));
        }
      }
    }

    con.setList(filterd_result);
    con.setType(type);

    return con;
  }

  private WhatsNewContainer getContainerBoth(RunData rundata, Context context,
      DataContext dataContext, int type) {
    WhatsNewContainer con = new WhatsNewContainer();
    SelectQuery query = new SelectQuery(EipTWhatsNew.class);
    Expression exp1 =
      ExpressionFactory.matchExp(EipTWhatsNew.PORTLET_TYPE_PROPERTY, type);
    query.setQualifier(exp1);
    if (parentIds != null && parentIds.size() > 0) {
      Expression exp2 =
        ExpressionFactory
          .inDbExp(EipTWhatsNew.WHATSNEW_ID_PK_COLUMN, parentIds);
      query.andQualifier(exp2.notExp());
    }
    Expression exp3 =
      ExpressionFactory.matchExp(EipTWhatsNew.PARENT_ID_PROPERTY, Integer
        .valueOf(0));
    query.andQualifier(exp3);
    query.addOrdering(EipTWhatsNew.UPDATE_DATE_PROPERTY, false);

    @SuppressWarnings("unchecked")
    List<EipTWhatsNew> temp = dataContext.performQuery(query);

    query = new SelectQuery(EipTWhatsNew.class);
    Expression exp =
      ExpressionFactory.matchExp(EipTWhatsNew.USER_ID_PROPERTY, Integer
        .valueOf(uid));
    query.setQualifier(exp);
    Expression exp4 =
      ExpressionFactory.matchExp(EipTWhatsNew.PORTLET_TYPE_PROPERTY, Integer
        .valueOf(type));
    query.andQualifier(exp4);
    Expression exp5 =
      ExpressionFactory.matchExp(EipTWhatsNew.PARENT_ID_PROPERTY, Integer
        .valueOf("-1"));
    query.andQualifier(exp5);
    query.addOrdering(EipTWhatsNew.UPDATE_DATE_PROPERTY, false);
    @SuppressWarnings("unchecked")
    List<EipTWhatsNew> performQuery = dataContext.performQuery(query);
    temp.addAll(performQuery);

    con.setList(temp);
    con.setType(type);

    return con;
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   * 
   * @param obj
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getListData(java.lang.Object)
   */

  @Override
  protected Object getResultData(WhatsNewContainer record) {

    WhatsNewResultData rd =
      WhatsNewUtils.setupWhatsNewResultData(record, uid, viewNum, viewSpan);

    return rd;
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
  @Override
  public WhatsNewContainer selectDetail(RunData rundata, Context context) {
    return null;
  }

  /**
   * ResultData に値を格納して返します。（詳細データ） <BR>
   * 
   * @param obj
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultDataDetail(java.lang.Object)
   */
  @Override
  protected Object getResultDataDetail(WhatsNewContainer obj) {
    return null;
  }

  /**
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getColumnMap()
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    // map.putValue("whatsnew_name", EipTWhatsNew.TODO_NAME_PROPERTY);
    // map.putValue("state", EipTWhatsNew.STATE_PROPERTY);
    // map.putValue("priority", EipTWhatsNew.PRIORITY_PROPERTY);
    // map.putValue("end_date", EipTWhatsNew.END_DATE_PROPERTY);
    // map.putValue("category_name", EipTWhatsNew.EIP_TTODO_CATEGORY_PROPERTY +
    // "."
    // + EipTWhatsNewCategory.CATEGORY_NAME_PROPERTY);
    // map.putValue("category", EipTWhatsNewCategory.CATEGORY_ID_PK_COLUMN
    // .toLowerCase());
    return map;
  }

  /**
   * 
   * @param i
   */
  public void setViewSpan(int i) {
    viewSpan = i;
  }

  /**
   * 
   * @param i
   */
  public void setViewNum(int i) {
    viewNum = i;
  }

  /**
   * @return SelectQuery
   * 
   */
  private SelectQuery addSpanCriteria(SelectQuery query) {

    if (viewSpan > 0) {
      Calendar cal = Calendar.getInstance();
      if (viewSpan == 31) {// 一ヶ月指定の場合は別処理
        cal.add(Calendar.MONTH, -1);
        /** 日付けを１にセットする */
        cal.set(Calendar.DAY_OF_MONTH, 1);
      } else {
        cal.add(Calendar.DAY_OF_MONTH, -1 * viewSpan);
      }

      /** 時分秒を０にセットする */
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);

      Expression exp =
        ExpressionFactory.greaterOrEqualExp(
          EipTWhatsNew.UPDATE_DATE_PROPERTY,
          cal.getTime());
      query.andQualifier(exp);
    }

    return query;
  }

  /**
   * 
   * @param query
   * @return SelectQuery
   */
  private SelectQuery addNumberCriteria(SelectQuery query) {

    if (viewNum > 0) {
      query.setFetchLimit(viewNum);
    }
    return query;
  }
}

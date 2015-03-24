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
package com.aimluck.eip.whatsnew;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
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
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
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
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    uid = ALEipUtils.getUserId(rundata);
    parentIds = new ArrayList<Integer>();
    SelectQuery<EipTWhatsNew> query = Database.query(EipTWhatsNew.class);

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
    List<EipTWhatsNew> readflags = query.fetchList();
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
   */
  @Override
  public ResultList<WhatsNewContainer> selectList(RunData rundata,
      Context context) {
    try {
      /** 31日以上たった新着情報を削除する */
      WhatsNewUtils.removeMonthOverWhatsNew();

      List<WhatsNewContainer> list = new ArrayList<WhatsNewContainer>();
      list.add(getContainerPublic(
        rundata,
        context,
        WhatsNewUtils.WHATS_NEW_TYPE_BLOG_ENTRY));
      list.add(getContainer(
        rundata,
        context,
        WhatsNewUtils.WHATS_NEW_TYPE_BLOG_COMMENT));
      list.add(getContainerBoth(
        rundata,
        context,
        WhatsNewUtils.WHATS_NEW_TYPE_MSGBOARD_TOPIC));
      list.add(getContainer(
        rundata,
        context,
        WhatsNewUtils.WHATS_NEW_TYPE_SCHEDULE));
      list.add(getContainer(
        rundata,
        context,
        WhatsNewUtils.WHATS_NEW_TYPE_WORKFLOW_REQUEST));
      list
        .add(getContainer(rundata, context, WhatsNewUtils.WHATS_NEW_TYPE_NOTE));

      return new ResultList<WhatsNewContainer>(list);
    } catch (Exception ex) {
      logger.error("whatsnew", ex);
      return null;
    }

  }

  private WhatsNewContainer getContainer(RunData rundata, Context context,
      int type) {
    WhatsNewContainer con = new WhatsNewContainer();
    SelectQuery<EipTWhatsNew> query = Database.query(EipTWhatsNew.class);
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

    query.orderDesending(EipTWhatsNew.UPDATE_DATE_PROPERTY);
    List<EipTWhatsNew> temp = query.fetchList();
    con.setList(temp);
    con.setType(type);

    return con;
  }

  private WhatsNewContainer getContainerPublic(RunData rundata,
      Context context, int type) {
    WhatsNewContainer con = new WhatsNewContainer();
    SelectQuery<EipTWhatsNew> query = Database.query(EipTWhatsNew.class);

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

    query.orderDesending(EipTWhatsNew.UPDATE_DATE_PROPERTY);
    List<EipTWhatsNew> result = query.fetchList();
    /** 既読物を抜く */
    List<EipTWhatsNew> filterd_result = new ArrayList<EipTWhatsNew>();
    if (null != result) {
      int size = result.size();
      if (size > 0) {
        for (int i = 0; i < size; i++) {
          int id = result.get(i).getWhatsNewId().intValue();
          if (parentIds.indexOf(id) == -1) {
            filterd_result.add(result.get(i));
          }
        }
      }
    }

    con.setList(filterd_result);
    con.setType(type);

    return con;
  }

  private WhatsNewContainer getContainerBoth(RunData rundata, Context context,
      int type) {
    WhatsNewContainer con = new WhatsNewContainer();
    SelectQuery<EipTWhatsNew> query = Database.query(EipTWhatsNew.class);
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
    query.orderDesending(EipTWhatsNew.UPDATE_DATE_PROPERTY);

    List<EipTWhatsNew> temp = query.fetchList();

    query = Database.query(EipTWhatsNew.class);
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
    query.orderDesending(EipTWhatsNew.UPDATE_DATE_PROPERTY);
    List<EipTWhatsNew> performQuery = query.fetchList();
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
   */
  @Override
  protected Object getResultDataDetail(WhatsNewContainer obj) {
    return null;
  }

  /**
   * @return
   * 
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
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
  private SelectQuery<EipTWhatsNew> addSpanCriteria(
      SelectQuery<EipTWhatsNew> query) {

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
  private SelectQuery<EipTWhatsNew> addNumberCriteria(
      SelectQuery<EipTWhatsNew> query) {

    if (viewNum > 0) {
      query.limit(viewNum);
    }
    return query;
  }
}

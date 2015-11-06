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
package com.aimluck.eip.timeline;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTTimelineLike;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タイムライントピックの検索データを管理するクラスです。 <BR>
 * 
 */
public class TimelineLikeSelectData extends
    ALAbstractSelectData<EipTTimelineLike, EipTTimelineLike> implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(TimelineSelectData.class.getName());

  private final List<Integer> users = new ArrayList<Integer>();

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "update_date");
    ALEipUtils.setTemp(
      rundata,
      context,
      LIST_SORT_TYPE_STR,
      ALEipConstants.LIST_SORT_TYPE_DESC);
  }

  /**
   * 一覧データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  public ResultList<EipTTimelineLike> selectList(RunData rundata,
      Context context) {
    try {
      SelectQuery<EipTTimelineLike> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);
      // 表示するカラムのみデータベースから取得する．
      ResultList<EipTTimelineLike> list = query.getResultList();
      return list;
    } catch (Exception ex) {
      logger.error("timeline", ex);
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

  private SelectQuery<EipTTimelineLike> getSelectQuery(RunData rundata,
      Context context) {

    SelectQuery<EipTTimelineLike> query =
      Database.query(EipTTimelineLike.class);

    String timelineId =
      rundata.getParameters().getString(EipTTimelineLike.TIMELINE_ID_PROPERTY);
    Expression exp1 =
      ExpressionFactory.matchExp(EipTTimelineLike.TIMELINE_ID_PROPERTY, Integer
        .valueOf(timelineId));
    query.setQualifier(exp1);
    query.distinct(true);

    return query;
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipTTimelineLike record) {
    try {
      TimelineLikeResultData rd = new TimelineLikeResultData();
      rd.initField();
      rd.setTimelineLikeId(record.getTimelineLikeId().longValue());
      rd.setTimelineId(record.getTimelineId().longValue());
      rd.setUserId(record.getOwnerId().longValue());
      rd.setCreateDate(record.getCreateDate());

      if (!users.contains(record.getOwnerId())) {
        users.add(record.getOwnerId());
      }

      return rd;
    } catch (Exception ex) {
      logger.error("timeline", ex);
      return null;
    }
  }

  /**
   * ResultData に値を格納して返します。（詳細データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTTimelineLike record)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  @Override
  public EipTTimelineLike selectDetail(RunData rundata, Context context) {
    ALEipUtils.redirectPageNotFound(rundata);
    return null;
  }

  /**
   * @return
   * 
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("create_date", EipTTimelineLike.CREATE_DATE_PROPERTY);
    return map;
  }

  /**
   * 
   * @param id
   * @return
   */
  public boolean isMatch(int id1, long id2) {
    return id1 == (int) id2;
  }

  public List<TimelineLikeResultData> getLikeList(Integer timelineId) {
    List<TimelineLikeResultData> likeList =
      new ArrayList<TimelineLikeResultData>();
    SelectQuery<EipTTimelineLike> query =
      getSelectQueryForLike(timelineId.toString());
    List<EipTTimelineLike> aList = query.fetchList();
    if (aList != null) {
      for (EipTTimelineLike like : aList) {
        likeList.add((TimelineLikeResultData) getResultData(like));
      }
    }
    loadAggregateUsers();
    return likeList;
  }

  private static SelectQuery<EipTTimelineLike> getSelectQueryForLike(
      String topicid) {
    SelectQuery<EipTTimelineLike> query =
      Database.query(EipTTimelineLike.class);
    Expression exp =
      ExpressionFactory.matchExp(EipTTimelineLike.TIMELINE_ID_PROPERTY, Integer
        .valueOf(topicid));
    query.setQualifier(exp);
    query.orderDesending(EipTTimelineLike.CREATE_DATE_PROPERTY);
    return query;
  }

  @Override
  public boolean doViewList(ALAction action, RunData rundata, Context context) {
    boolean result = super.doViewList(action, rundata, context);
    loadAggregateUsers();
    return result;
  }

  protected void loadAggregateUsers() {
    ALEipManager.getInstance().getUsers(users);
  }

}

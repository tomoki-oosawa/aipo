/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

import com.aimluck.eip.cayenne.om.portlet.EipTTimelineUrl;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
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
public class TimelineUrlSelectData extends
    ALAbstractSelectData<EipTTimelineUrl, EipTTimelineUrl> implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(TimelineSelectData.class.getName());

  /** ログインユーザ ID */
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
    super.init(action, rundata, context);

    uid = ALEipUtils.getUserId(rundata);

  }

  /**
   * 一覧データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  public ResultList<EipTTimelineUrl> selectList(RunData rundata, Context context) {
    try {
      SelectQuery<EipTTimelineUrl> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);
      // 表示するカラムのみデータベースから取得する．
      ResultList<EipTTimelineUrl> list = query.getResultList();
      return list;
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

  private SelectQuery<EipTTimelineUrl> getSelectQuery(RunData rundata,
      Context context) {

    SelectQuery<EipTTimelineUrl> query = Database.query(EipTTimelineUrl.class);

    String timelineId =
      rundata.getParameters().getString(EipTTimelineUrl.TIMELINE_ID_PROPERTY);
    Expression exp1 =
      ExpressionFactory.matchExp(EipTTimelineUrl.TIMELINE_ID_PROPERTY, Integer
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
  @SuppressWarnings("unchecked")
  @Override
  protected Object getResultData(EipTTimelineUrl record) {
    try {
      TimelineUrlResultData rd = new TimelineUrlResultData();
      rd.initField();
      rd.setTimelineUrlId(record.getTimelineUrlId().longValue());
      rd.setTimelineId(record.getTimelineId().longValue());
      rd.setThumbnail(record.getThumbnail());
      rd.setTitle(record.getTitle());
      rd.setUrl(record.getUrl());
      rd.setBody(record.getBody());
      boolean flag = false;
      if (record.getThumbnail() != null) {
        flag = true;
      }
      rd.setThumbnailFlag(flag);
      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
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
  protected Object getResultDataDetail(EipTTimelineUrl record)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  @Override
  public EipTTimelineUrl selectDetail(RunData rundata, Context context) {
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

  public List<TimelineUrlResultData> getUrlList(Integer timelineId) {
    List<TimelineUrlResultData> urlList =
      new ArrayList<TimelineUrlResultData>();
    SelectQuery<EipTTimelineUrl> query =
      getSelectQueryForUrl(timelineId.toString());
    List<EipTTimelineUrl> aList = query.fetchList();
    if (aList != null) {
      for (EipTTimelineUrl url : aList) {
        urlList.add((TimelineUrlResultData) getResultData(url));
      }
    }
    return urlList;
  }

  private static SelectQuery<EipTTimelineUrl> getSelectQueryForUrl(
      String topicid) {
    SelectQuery<EipTTimelineUrl> query = Database.query(EipTTimelineUrl.class);
    Expression exp =
      ExpressionFactory.matchExp(EipTTimelineUrl.TIMELINE_ID_PROPERTY, Integer
        .valueOf(topicid));
    query.setQualifier(exp);
    query.distinct(true);
    return query;
  }

}

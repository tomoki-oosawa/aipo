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
package com.aimluck.eip.exttimecard;

import java.util.jar.Attributes;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTExtTimecardSystem;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.exttimecard.util.ExtTimecardUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タイムカード集計の検索データを管理するためのクラスです。 <br />
 * 
 */
public class ExtTimecardSystemSelectData extends
    ALAbstractSelectData<EipTExtTimecardSystem, EipTExtTimecardSystem> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ExtTimecardSystemSelectData.class.getName());

  /** システムの総数 */
  private int systemSum;

  /** <code>viewtype</code> 表示タイプ */
  protected String viewtype = "system";

  private String portletName;

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<EipTExtTimecardSystem> selectList(RunData rundata,
      Context context) {
    try {

      SelectQuery<EipTExtTimecardSystem> query =
        getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      ResultList<EipTExtTimecardSystem> list = query.getResultList();
      // 件数をセットする．
      systemSum = list.getTotalCount();
      return list;
    } catch (Exception ex) {
      logger.error("exttimecard", ex);
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
  private SelectQuery<EipTExtTimecardSystem> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipTExtTimecardSystem> query =
      Database.query(EipTExtTimecardSystem.class);

    return query;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected EipTExtTimecardSystem selectDetail(RunData rundata, Context context) {
    // ポートレット名を取得します。
    VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
    setPortletName(portlet.getPortletConfig().getName());

    return ExtTimecardUtils.getEipTExtTimecardSystem(rundata, context);
  }

  /**
   *
   */
  @Override
  protected Object getResultData(EipTExtTimecardSystem record) {
    try {
      ExtTimecardSystemDetailResultData rd =
        new ExtTimecardSystemDetailResultData();
      rd.initField();
      rd.setSystemId(record.getSystemId().intValue());
      rd.setUserId(record.getUserId().intValue());
      rd.setSystemName(record.getSystemName());
      rd.setStartHour(record.getStartHour().intValue());
      rd.setStartMinute(record.getStartMinute());
      rd.setEndHour(record.getEndHour().intValue());
      rd.setEndMinute(record.getEndMinute());
      rd.setStartDay(record.getStartDay().intValue());
      rd.setWorkTimeIn(record.getWorktimeIn());
      rd.setRestTimeIn(record.getResttimeIn());
      rd.setWorkTimeOut(record.getWorktimeOut());
      rd.setRestTimeOut(record.getResttimeOut());
      rd.setChangeHour(record.getChangeHour());
      rd.setOutgoingAddFlag(record.getOutgoingAddFlag());

      return rd;
    } catch (Exception ex) {
      logger.error("exttimecard", ex);
      return null;
    }
  }

  /**
   * 
   * @param record
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTExtTimecardSystem record) {
    try {
      ExtTimecardSystemDetailResultData rd =
        new ExtTimecardSystemDetailResultData();
      rd.initField();
      rd.setSystemId(record.getSystemId().intValue());
      rd.setUserId(record.getUserId().intValue());
      rd.setSystemName(record.getSystemName());
      rd.setStartHour(record.getStartHour());
      rd.setStartMinute(record.getStartMinute());
      rd.setEndHour(record.getEndHour());
      rd.setEndMinute(record.getEndMinute());
      rd.setStartDay(record.getStartDay());
      rd.setWorkTimeIn(record.getWorktimeIn());
      rd.setWorkTimeOut(record.getWorktimeOut());
      rd.setRestTimeIn(record.getResttimeIn());
      rd.setRestTimeOut(record.getResttimeOut());
      rd.setChangeHour(record.getChangeHour());
      rd.setOutgoingAddFlag(record.getOutgoingAddFlag());
      rd.setCreateDate(record.getCreateDate().toString());
      rd.setUpdateDate(record.getUpdateDate().toString());

      return rd;
    } catch (Exception ex) {
      logger.error("exttimecard", ex);
      return null;
    }
  }

  /**
   * @return
   * 
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("create_date", EipTExtTimecardSystem.CREATE_DATE_PROPERTY);
    map.putValue("system_name", EipTExtTimecardSystem.SYSTEM_NAME_PROPERTY);
    return map;
  }

  public int getSystemSum() {
    return systemSum;
  }

  /**
   * 表示タイプを取得します。
   * 
   * @return
   */
  public String getViewtype() {
    return viewtype;
  }

  /**
   * ポートレット名を取得します。
   * 
   * @return portletName
   */
  public String getPortletName() {
    return portletName;
  }

  /**
   * @param portletName
   * 
   */
  public void setPortletName(String portletName) {
    this.portletName = portletName;
  }
}

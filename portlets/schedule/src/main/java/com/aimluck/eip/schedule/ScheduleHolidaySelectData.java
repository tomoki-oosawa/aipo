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

package com.aimluck.eip.schedule;

import java.util.List;
import java.util.jar.Attributes;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.account.EipMUserHoliday;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.schedule.util.ScheduleHolidayUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 設備検索データを管理するクラスです。 <BR>
 * 
 */
public class ScheduleHolidaySelectData extends
    ALAbstractSelectData<EipMUserHoliday, EipMUserHoliday> implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleHolidaySelectData.class.getName());

  /** 休日の総数 */
  private int holidaySum;

  /** <code>viewtype</code> 表示タイプ */
  protected String viewtype;

  /** フィルターなしの全設備リスト */
  private List<ScheduleHolidayResultData> holidayList;

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
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, ALEipUtils
        .getPortlet(rundata, context)
        .getPortletConfig()
        .getInitParameter("p2a-sort"));
    }
    super.init(action, rundata, context);
    viewtype = "holiday";
  }

  /**
   * 一覧データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  public ResultList<EipMUserHoliday> selectList(RunData rundata, Context context) {
    try {

      SelectQuery<EipMUserHoliday> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      ResultList<EipMUserHoliday> list = query.getResultList();
      // 休日の総数をセットする．
      holidaySum = list.getTotalCount();

      return list;
    } catch (Exception ex) {
      logger.error("schedule_holiday", ex);
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
  private SelectQuery<EipMUserHoliday> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipMUserHoliday> query = Database.query(EipMUserHoliday.class);
    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipMUserHoliday record) {
    try {
      ScheduleHolidayResultData rd = new ScheduleHolidayResultData();
      rd.initField();
      rd.setHolidayId(record.getUserHolidayId().longValue());
      rd.setHolidayTitle(record.getHolidayTitle());
      rd.setHolidayDate(record.getHolidayDate());
      return rd;
    } catch (Exception ex) {
      logger.error("facilities", ex);
      return null;
    }
  }

  /**
   * 詳細データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  public EipMUserHoliday selectDetail(RunData rundata, Context context) {
    return ScheduleHolidayUtils.getEipMUserHoliday(rundata, context);
  }

  /**
   * ResultData に値を格納して返します。（詳細データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipMUserHoliday record) {
    try {
      ScheduleHolidayResultData rd = new ScheduleHolidayResultData();
      rd.initField();
      rd.setHolidayTitle(record.getHolidayTitle());
      rd.setHolidayId(record.getUserHolidayId().longValue());
      rd.setCreateUserID(record.getCreateUserId());
      rd.setUpdateUserID(record.getUpdateUserId());
      return rd;
    } catch (Exception ex) {
      logger.error("holidays", ex);
      return null;
    }
  }

  /**
   * 設備の総数を返す． <BR>
   * 
   * @return
   */
  public int getHolidaySum() {
    return holidaySum;
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
   * @return
   * 
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("holiday_title", EipMUserHoliday.HOLIDAY_TITLE_PROPERTY);
    map.putValue("holiday_date", EipMUserHoliday.HOLIDAY_DATE_PROPERTY);
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

  public void loadHolidayList(RunData rundata, Context context) {
    holidayList = ScheduleHolidayUtils.loadHolidayList(rundata, context);
  }

  public List<ScheduleHolidayResultData> getHolidayList() {
    return holidayList;
  }
}

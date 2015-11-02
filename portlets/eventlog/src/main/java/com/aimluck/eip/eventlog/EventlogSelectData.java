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
package com.aimluck.eip.eventlog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.cayenne.om.portlet.EipTEventlog;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.eventlog.util.ALEventlogUtils;
import com.aimluck.eip.eventlog.util.EventlogUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * イベントログ検索データを管理するクラスです。 <BR>
 * 
 */
public class EventlogSelectData extends
    ALAbstractSelectData<EipTEventlog, EipTEventlog> implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(EventlogSelectData.class.getName());

  /** イベントログの書き出し可能最大数 */
  private static final int MAX_SIZE = 50000;

  /** イベントログの総数 */
  private int eventlogSum;

  private ALDateTimeField start_date, end_date;

  private int start_date_max_day, end_date_max_day;

  private int view_date_max_year;

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

    String sort_type = ALEipUtils.getTemp(rundata, context, LIST_SORT_TYPE_STR);
    if (sort_type == null || "".equals(sort_type)) {
      ALEipUtils.setTemp(
        rundata,
        context,
        LIST_SORT_TYPE_STR,
        ALEipConstants.LIST_SORT_TYPE_DESC);
    }
    Calendar cal = Calendar.getInstance();
    view_date_max_year = cal.get(Calendar.YEAR);

    start_date = new ALDateTimeField();
    cal = EventlogUtils.getViewCalendar(true, rundata, context);
    start_date.setValue(cal.getTime());
    start_date_max_day = cal.getActualMaximum(Calendar.DATE);

    end_date = new ALDateTimeField();
    cal = EventlogUtils.getViewCalendar(false, rundata, context);
    end_date.setValue(cal.getTime());
    end_date_max_day = cal.getActualMaximum(Calendar.DATE);

    super.init(action, rundata, context);
  }

  public ALDateTimeField getStartDate() {
    return start_date;
  }

  public int getMaxSize() {
    return MAX_SIZE;
  }

  public ALDateTimeField getEndDate() {
    return end_date;
  }

  public int getStartDateMaxDay() {
    return start_date_max_day;
  }

  public int getEndDateMaxDay() {
    return end_date_max_day;
  }

  public int getViewDateMaxYear() {
    return view_date_max_year;
  }

  /**
   * 一覧データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  public ResultList<EipTEventlog> selectList(RunData rundata, Context context) {
    try {

      SelectQuery<EipTEventlog> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);
      buildSelectQueryForDate(query, rundata, context);

      ResultList<EipTEventlog> list = query.getResultList();
      // イベントログの総数をセットする．
      eventlogSum = list.getTotalCount();

      return list;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

  /**
   * @param query
   * @param rundata
   * @param context
   */
  private void buildSelectQueryForDate(SelectQuery<EipTEventlog> query,
      RunData rundata, Context context) {
    Expression exp1 =
      ExpressionFactory.greaterOrEqualExp(
        EipTEventlog.EVENT_DATE_PROPERTY,
        start_date.getValue());
    Calendar cal = Calendar.getInstance();
    cal.setTime(end_date.getValue());
    cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
    Expression exp2 =
      ExpressionFactory
        .lessExp(EipTEventlog.EVENT_DATE_PROPERTY, cal.getTime());
    query.andQualifier(exp1.andExp(exp2));
  }

  public boolean isOverSize() {
    return getCount() > MAX_SIZE;
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipTEventlog> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipTEventlog> query = Database.query(EipTEventlog.class);
    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipTEventlog record) {
    try {
      DateFormat df = new SimpleDateFormat("yyyy年M月d日(EE)HH:mm:ss");

      EventlogResultData rd = new EventlogResultData();
      rd.initField();
      rd.setEventlogId(record.getEventlogId().longValue());

      TurbineUser user = record.getTurbineUser();

      rd.setUserFullName(user == null ? "" : new StringBuffer().append(
        user.getLastName()).append(" ").append(user.getFirstName()).toString());

      if (user != null
        && user.getLastName().trim().equals("")
        && user.getFirstName().trim().equals("")) {
        rd.setUserFullName(record.getTurbineUser().getEmail());
      }

      rd.setEventDate(df.format(record.getUpdateDate()));
      rd.setPortletName(ALEventlogUtils.getPortletAliasName(record
        .getPortletType()));
      rd.setEntityId(record.getEntityId().longValue());
      rd.setIpAddr(record.getIpAddr());
      rd.setEventName(ALEventlogUtils.getEventAliasName(record.getEventType()));
      rd.setNote(record.getNote());
      return rd;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
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
  public EipTEventlog selectDetail(RunData rundata, Context context) {
    return EventlogUtils.getEipTEventlog(rundata, context);
  }

  /**
   * ResultData に値を格納して返します。（詳細データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTEventlog record) {
    try {
      DateFormat df = new SimpleDateFormat("yyyy年MM月dd日(EE)HH:mm:ss");

      EventlogResultData rd = new EventlogResultData();
      rd.initField();
      rd.setEventlogId(record.getEventlogId().longValue());

      String userFullName =
        ALEipUtils.getUserFullName(record
          .getTurbineUser()
          .getUserId()
          .intValue());

      if (userFullName != null && !userFullName.trim().equals("")) {
        rd.setUserFullName(userFullName);
      }

      if (userFullName == null || userFullName.trim().equals("")) {
        rd.setUserFullName(record.getTurbineUser().getEmail());
      }

      rd.setEventDate(df.format(record.getUpdateDate()));
      rd.setPortletName(ALEventlogUtils.getPortletAliasName(record
        .getPortletType()));
      rd.setEntityId(record.getEntityId().longValue());
      rd.setIpAddr(record.getIpAddr());
      rd.setEventName(ALEventlogUtils.getEventAliasName(record.getEventType()));
      rd.setNote(record.getNote());
      // 各ポートレットのデータ名を取得
      String dataName =
        EventlogUtils.getPortletDataName(record.getPortletType(), record
          .getEntityId());
      if (dataName != null && !"".equals(dataName)) {
        rd.setDataName(dataName);
        rd.setDataNameFlag(true);
      } else {
        // データが削除された可能性あり-DBのNOTEカラムから取得する
        String tmp_dataName = record.getNote();
        if (tmp_dataName != null && !"".equals(tmp_dataName)) {
          rd.setDataName(tmp_dataName);
          rd.setDataNameFlag(true);
        }
      }
      return rd;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
      return null;
    }
  }

  /**
   * イベントログの総数を返す． <BR>
   * 
   * @return
   */
  public int getEventlogSum() {
    return eventlogSum;
  }

  /**
   * @return
   * 
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("event_date", EipTEventlog.EVENT_DATE_PROPERTY);
    map.putValue("user_name", EipTEventlog.TURBINE_USER_PROPERTY
      + "."
      + TurbineUser.LAST_NAME_KANA_PROPERTY);
    map.putValue("portlet_id", EipTEventlog.PORTLET_TYPE_PROPERTY);
    map.putValue("event_type", EipTEventlog.EVENT_TYPE_PROPERTY);
    map.putValue("ip_addr", EipTEventlog.IP_ADDR_PROPERTY);
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

}

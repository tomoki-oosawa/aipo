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

package com.aimluck.eip.holiday;

import java.util.Calendar;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.cayenne.om.portlet.EipMHoliday;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.holiday.util.HolidayUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALUserContextLocator;

/**
 * Holiday検索データを管理するクラスです。 <BR>
 * 
 */
public class HolidaySelectData extends
    ALAbstractSelectData<EipMHoliday, EipMHoliday> implements ALData {

  public static final String VIEWED_HOLIDAY_YEAR_KEY =
    "com.aimluck.eip.holiday.viewedHolidayYear";

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(HolidaySelectData.class.getName());

  /** Holiday の総数 */
  private int holidaySum;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * @throws AfLPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, ALEipUtils
        .getPortlet(rundata, context)
        .getPortletConfig()
        .getInitParameter("p1b-sort"));
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
  public ResultList<EipMHoliday> selectList(RunData rundata, Context context) {
    try {
      SelectQuery<EipMHoliday> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      ResultList<EipMHoliday> resultList = query.getResultList();
      setPageParam(resultList.getTotalCount());
      holidaySum = resultList.getTotalCount();
      return resultList;
    } catch (Exception ex) {
      logger.error("Exception", ex);
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
  public EipMHoliday selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException {

    try {
      EipMHoliday holiday = HolidayUtils.getEipMHoliday(rundata, context, true);
      return holiday;
    } catch (ALPageNotFoundException pageNotFound) {
      throw pageNotFound;
    }
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipMHoliday> getSelectQuery(RunData rundata,
      Context context) {
    int viewedYear = getViewedHolidayYear();
    SelectQuery<EipMHoliday> query = Database.query(EipMHoliday.class);
    Expression exp1 =
      ExpressionFactory.greaterOrEqualExp(
        EipMHoliday.HOLIDAY_DATE_PROPERTY,
        HolidayUtils.getJan1st(viewedYear));
    Expression exp2 =
      ExpressionFactory.lessOrEqualExp(
        EipMHoliday.HOLIDAY_DATE_PROPERTY,
        HolidayUtils.getDec31st(viewedYear));
    query.setQualifier(exp1.andExp(exp2));
    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipMHoliday record) {
    try {

      HolidayResultData rd = new HolidayResultData();
      rd.initField();
      rd.setHolidayId(record.getHolidayId().intValue());
      if (!HolidayUtils.isEmptyDate(record.getHolidayDate())) {
        rd.setHolidayDate(ALDateUtil.format(
          record.getHolidayDate(),
          "yyyy年M月d日"));
      }
      rd.setHolidayName(ALCommonUtils.compressString(
        record.getHolidayName(),
        getStrLength()));
      rd.setDefaultFlag(ALCommonUtils.compressString(
        record.getDefaultFlag(),
        getStrLength()));
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
  protected Object getResultDataDetail(EipMHoliday record) {
    try {

      HolidayResultData rd = new HolidayResultData();
      rd.initField();
      rd.setHolidayId(record.getHolidayId().intValue());
      if (!HolidayUtils.isEmptyDate(record.getHolidayDate())) {
        rd.setHolidayDate(ALDateUtil.format(
          record.getHolidayDate(),
          "yyyy年M月d日"));
      }
      rd.setHolidayName(ALCommonUtils.compressString(
        record.getHolidayName(),
        getStrLength()));
      rd.setDefaultFlag(ALCommonUtils.compressString(
        record.getDefaultFlag(),
        getStrLength()));
      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * Holiday の総数を返す． <BR>
   * 
   * @return
   */
  public int getHolidaySum() {
    return holidaySum;
  }

  /**
   * 表示年を初期化する。
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static int initViewedHolidayYear(RunData rundata, Context context) {
    int year = Calendar.getInstance().get(Calendar.YEAR);
    ALEipUtils.setTemp(rundata, context, VIEWED_HOLIDAY_YEAR_KEY, String
      .valueOf(year));
    return year;
  }

  /**
   * 表示年を取得する
   * 
   * @return
   */
  public static int getViewedHolidayYear() {
    int viewedYear = -1;
    RunData rundata = ALUserContextLocator.getRunData();
    Context context =
      org.apache.turbine.services.velocity.TurbineVelocity.getContext(rundata);

    String temp = ALEipUtils.getTemp(rundata, context, VIEWED_HOLIDAY_YEAR_KEY);
    if (temp == null) {
      viewedYear = initViewedHolidayYear(rundata, context);
    } else {
      viewedYear = Integer.parseInt(temp);
    }
    return viewedYear;
  }

  /**
   * 表示年を次の年にする。
   * 
   * @return
   */
  public static void incrementViewedHolidayYear() {
    RunData rundata = ALUserContextLocator.getRunData();
    Context context =
      org.apache.turbine.services.velocity.TurbineVelocity.getContext(rundata);
    int viewedYear = getViewedHolidayYear();
    ALEipUtils.setTemp(rundata, context, VIEWED_HOLIDAY_YEAR_KEY, String
      .valueOf(++viewedYear));
  }

  /**
   * 表示年を前の年にする。<br>
   * （ただし、2004年以前には遡らない）
   * 
   * @return
   */
  public static void decrementViewedHolidayYear() {
    RunData rundata = ALUserContextLocator.getRunData();
    Context context =
      org.apache.turbine.services.velocity.TurbineVelocity.getContext(rundata);
    int viewedYear = getViewedHolidayYear();
    if (viewedYear > HolidayUtils.YEAR_START) {
      ALEipUtils.setTemp(rundata, context, VIEWED_HOLIDAY_YEAR_KEY, String
        .valueOf(--viewedYear));
    }
  }

  /**
   * @return
   * 
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("holiday_id", EipMHoliday.HOLIDAY_ID_PK_COLUMN);
    map.putValue("holiday_date", EipMHoliday.HOLIDAY_DATE_PROPERTY);
    map.putValue("holiday_name", EipMHoliday.HOLIDAY_NAME_PROPERTY);
    map.putValue("default_flag", EipMHoliday.DEFAULT_FLAG_PROPERTY);
    return map;
  }

}

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
package com.aimluck.eip.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.jar.Attributes;

import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.cayenne.om.portlet.VEipTScheduleList;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.util.ALEipUtils;

/**
 * Widgetsで表示するカレンダーのクラスです。
 * 
 */
public class AjaxScheduleMonthlySelectData extends
    ALAbstractSelectData<VEipTScheduleList, VEipTScheduleList> {

  /** <code>viewMonth</code> 今日 */
  private ALDateTimeField today;

  /** <code>viewMonth</code> 現在の月 */
  private ALDateTimeField viewMonth;

  /** <code>monthCon</code> 月間スケジュールコンテナ */
  private ScheduleMonthContainer monthCon;

  /** <code>prevMonth</code> 前の月 */
  private ALDateTimeField prevMonth;

  /** <code>nextMonth</code> 次の月 */
  private ALDateTimeField nextMonth;

  /** <code>nextMonth</code> 表示されている日 */
  private ALDateTimeField viewStart;

  /**
   * 現在の月を取得します。
   * 
   * @return
   */
  public ALDateTimeField getMonthlyCalendarToday() {
    return today;
  }

  /**
   * 現在の月を取得します。
   * 
   * @return
   */
  public ALDateTimeField getMonthlyCalendarViewMonth() {
    return viewMonth;
  }

  /**
   * 月間スケジュールコンテナを取得します。
   * 
   * @return
   */
  public ScheduleMonthContainer getMonthlyCalendarContainer() {
    return monthCon;
  }

  /**
   * 前の月を取得します。
   * 
   * @return
   */
  public ALDateTimeField getMonthlyCalendarPrevMonth() {
    return prevMonth;
  }

  /**
   * 次の月を取得します。
   * 
   * @return
   */
  public ALDateTimeField getMonthlyCalendarNextMonth() {
    return nextMonth;
  }

  /**
   * 現在の月を取得します。
   * 
   * @return
   */
  public void setMonthlyCalendarViewMonth(String year, String month) {
    if (month.length() == 1) {
      month = "0" + month;
    }
    viewMonth.setValue(year + "-" + month);
  }

  /**
   * Widgetsで表示する用のカレンダーデータをセットします。
   * 
   * @param rundata
   * @param context
   * @throws ALPageNotFoundException
   */
  public void setMonthlyCalendar(RunData rundata, Context context)
      throws ALPageNotFoundException {

    // 自ポートレットからのリクエストであれば、パラメータを展開しセッションに保存する。
    if (ALEipUtils.isMatch(rundata, context)) {
      // スケジュールの表示開始日時
      // e.g. 2004-3-14
      if (rundata.getParameters().containsKey("monthly_calendar_month")) {
        ALEipUtils.setTemp(rundata, context, "monthly_calendar_month", rundata
          .getParameters()
          .getString("monthly_calendar_month"));
      }
    }

    // 現在の月
    if ("".equals(viewMonth.toString())) {
      String tmpViewMonth =
        ALEipUtils.getTemp(rundata, context, "monthly_calendar_month");
      if (tmpViewMonth == null || tmpViewMonth.equals("")) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        viewMonth.setValue(cal.getTime());
      } else {
        viewMonth.setValue(tmpViewMonth);
        if (!viewMonth.validate(new ArrayList<String>())) {
          ALEipUtils.removeTemp(rundata, context, "monthly_calendar_month");
          throw new ALPageNotFoundException();
        }
      }
    } else {
      ALEipUtils.setTemp(rundata, context, "monthly_calendar_month", viewMonth
        .toString());
    }

    // 今日
    Calendar cal = Calendar.getInstance();
    today.setValue(cal.getTime());

    // 表示開始日時
    Calendar tmpCal = Calendar.getInstance();
    cal.setTime(viewMonth.getValue());
    tmpCal.setTime(viewMonth.getValue());
    int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
    cal.add(Calendar.DATE, -dayofweek + 1);

    // 月間スケジュールコンテナの初期化
    try {
      monthCon = new ScheduleMonthContainer();
      monthCon.initField();
      monthCon.setViewMonth(cal, tmpCal);
    } catch (Exception e) {
      // logger.error("schedule", e);
    }

    // 次の月、前の月
    Calendar cal2 = Calendar.getInstance();
    cal2.setTime(viewMonth.getValue());
    cal2.add(Calendar.MONTH, 1);
    nextMonth.setValue(cal2.getTime());
    cal2.add(Calendar.MONTH, -2);
    prevMonth.setValue(cal2.getTime());
  }

  /**
   *
   */
  @Override
  public void initField() {
    super.initField();
    // 前の月
    prevMonth = new ALDateTimeField("yyyy-MM");
    // 次の月
    nextMonth = new ALDateTimeField("yyyy-MM");
    // 現在の月
    viewMonth = new ALDateTimeField("yyyy-MM");
    viewMonth.setNotNull(true);
    // 今日
    today = new ALDateTimeField("yyyy-MM-dd");
    // 表示開始日時
    viewStart = new ALDateTimeField("yyyy-MM-dd");
    viewStart.setNotNull(true);
  }

  /**
   * @param action
   * @param rundata
   * @param context
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    setMonthlyCalendar(rundata, context);

    String tmpViewStart = ALEipUtils.getTemp(rundata, context, "view_start");
    if (tmpViewStart == null || "".equals(tmpViewStart)) {
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      viewStart.setValue(cal.getTime());
    } else {
      viewStart.setValue(tmpViewStart);
    }
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected ResultList<VEipTScheduleList> selectList(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected VEipTScheduleList selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   * @param obj
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultData(VEipTScheduleList obj)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   * @param obj
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultDataDetail(VEipTScheduleList obj)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   * @return
   */
  @Override
  protected Attributes getColumnMap() {
    return null;
  }

  /**
   * @return viewDate
   */
  public ALDateTimeField getViewDate() {
    return viewStart;
  }

}

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
package com.aimluck.eip.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALCellStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 週間スケジュールの検索結果を管理するクラスです。
 * 
 */
public class CellScheduleWeekSelectByMemberData extends
    CellScheduleWeekSelectData {

  /** <code>logger</code> logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleWeeklySelectData.class.getName());

  private DataContext dataContext;

  private ALCellStringField user;

  /** <code>login_user</code> 表示対象ユーザー */
  private ALEipUser targerUser;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
    dataContext = DatabaseOrmService.getInstance().getDataContext();

    user = new ALCellStringField();
    user.setNotNull(true);

    // 表示対象ユーザー取得
    String tmpTargetUser = rundata.getParameters().getString("selectedmember");
    if (tmpTargetUser == null || tmpTargetUser.equals("")) {
      targerUser = ALEipUtils.getALEipUser(rundata);
    } else {
      targerUser = ALEipUtils.getALEipUser(Integer.parseInt(tmpTargetUser));
      // setTemplate(rundata, "schedule-menu-select-member");
    }
  }

  @Override
  public void initField() {

  }

  @Override
  protected ResultList selectList(RunData rundata, Context context) {
    ArrayList scheduleMapList = new ArrayList();
    Calendar cal = Calendar.getInstance();
    cal.setTime(getStartDate().getValue());
    // int userid = ALEipUtils.getUserId(rundata);
    int userid = (int) targerUser.getUserId().getValue();

    dataContext = DatabaseOrmService.getInstance().getDataContext();

    // 通常、期間スケジュール、または日単位繰り返し
    for (int k = 0; k < 7; k++) {
      SelectQuery query = new SelectQuery(EipTScheduleMap.class);
      Expression exp =
        ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, Integer
          .valueOf(userid));
      Expression exp0 =
        ExpressionFactory.noMatchExp(EipTScheduleMap.STATUS_PROPERTY, "D");
      Expression exp00 =
        ExpressionFactory.noMatchExp(EipTScheduleMap.STATUS_PROPERTY, "R");

      Expression exp11 =
        ExpressionFactory.greaterOrEqualExp(
          EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
            + "."
            + EipTSchedule.END_DATE_PROPERTY,
          cal.getTime());
      cal.add(Calendar.DAY_OF_MONTH, 1);
      Expression exp12 =
        ExpressionFactory.lessExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
          + "."
          + EipTSchedule.START_DATE_PROPERTY, cal.getTime());
      Expression exp13 =
        ExpressionFactory.matchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
          + "."
          + EipTSchedule.REPEAT_PATTERN_PROPERTY, "N");
      Expression exp14 =
        ExpressionFactory.matchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
          + "."
          + EipTSchedule.REPEAT_PATTERN_PROPERTY, "S");
      Expression exp10 = exp11.andExp(exp12.andExp(exp13.orExp(exp14)));

      Expression exp21 =
        ExpressionFactory.matchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
          + "."
          + EipTSchedule.REPEAT_PATTERN_PROPERTY, "DN");
      Expression exp22 =
        ExpressionFactory.matchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
          + "."
          + EipTSchedule.REPEAT_PATTERN_PROPERTY, "DL");
      Expression exp20 = exp21.orExp(exp22.andExp(exp11).andExp(exp12));

      query.setQualifier((exp10.orExp(exp20)).andExp(exp).andExp(exp0).andExp(
        exp00));

      /*
       * List orders = new ArrayList(); orders.add(new
       * Ordering(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY + "." +
       * EipTSchedule.START_DATE_PROPERTY, true)); orders.add(new
       * Ordering(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY + "." +
       * EipTSchedule.END_DATE_PROPERTY, true)); query.addOrderings(orders);
       */

      List list = dataContext.performQuery(query);
      scheduleMapList.add(list);
    }

    // 週間、または毎月の場合
    SelectQuery query = new SelectQuery(EipTScheduleMap.class);
    Expression exp =
      ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, Integer
        .valueOf(userid));
    Expression exp2 =
      ExpressionFactory.noMatchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, "N");
    Expression exp3 =
      ExpressionFactory.noMatchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, "S");

    query.setQualifier(exp);
    query.andQualifier(exp2);
    query.andQualifier(exp3);
    List list = dataContext.performQuery(query);

    EipTSchedule schedule = null;
    for (int k = 0; k < list.size(); k++) {
      schedule = ((EipTScheduleMap) list.get(k)).getEipTSchedule();
      String pattern = schedule.getRepeatPattern();
      // 週間
      if (pattern.startsWith("W")) {
        for (int l = 0; l < 7; l++) {
          if (pattern.charAt(l + 1) == '1') {
            int index = (l - cal.get(Calendar.DAY_OF_WEEK) + 7 + 1) % 7;
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(getStartDate().getValue());
            cal2.add(Calendar.DAY_OF_MONTH, index);
            if (pattern.endsWith("L")) {
              if (schedule.getEndDate().compareTo(cal2.getTime()) >= 0) {
                cal2.add(Calendar.DAY_OF_MONTH, 1);
                if (schedule.getStartDate().compareTo(cal2.getTime()) < 0) {
                  List list2 = (List) scheduleMapList.get(index);
                  list2.add(list.get(k));
                  scheduleMapList.set(index, list2);
                }
              }
            } else {
              List list2 = (List) scheduleMapList.get(index);
              list2.add(list.get(k));
              scheduleMapList.set(index, list2);
            }
          }
        }
        // 毎月
      } else if (pattern.startsWith("M")) {
        int day = Integer.parseInt(pattern.substring(1, pattern.length() - 1));
        Calendar cal2 = Calendar.getInstance();
        Calendar cal_event = Calendar.getInstance();
        cal2.setTime(getStartDate().getValue());
        cal2.set(Calendar.DAY_OF_MONTH, day);
        cal_event.setTime(getStartDate().getValue());

        int index = (day - cal_event.get(Calendar.DAY_OF_MONTH));
        if (index < 0) {
          index += cal_event.getActualMaximum(Calendar.DAY_OF_MONTH);
          cal2.add(Calendar.MONTH, 1);
        }
        if (index >= 0
          && index <= 6
          && cal_event.getActualMaximum(Calendar.DAY_OF_MONTH) >= day) {
          if (pattern.endsWith("L")) {
            if (schedule.getEndDate().compareTo(cal2.getTime()) >= 0) {
              cal2.add(Calendar.DAY_OF_MONTH, 1);
              if (schedule.getStartDate().compareTo(cal2.getTime()) < 0) {
                List list2 = (List) scheduleMapList.get(index);
                list2.add(list.get(k));
                scheduleMapList.set(index, list2);
              }
            }
          } else {
            List list2 = (List) scheduleMapList.get(index);
            list2.add(list.get(k));
            scheduleMapList.set(index, list2);
          }
        }
      }
    }

    // ダミースケジュールの処理

    SelectQuery queryD = new SelectQuery(EipTScheduleMap.class);
    Expression expD =
      ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, Integer
        .valueOf(userid));
    Expression expD2 =
      ExpressionFactory.matchExp(EipTScheduleMap.STATUS_PROPERTY, "D");
    queryD.setQualifier(expD);
    queryD.andQualifier(expD2);
    List listD = dataContext.performQuery(queryD);

    for (int k = 0; k < 7; k++) {
      Calendar calD = Calendar.getInstance();
      calD.setTime(getStartDate().getValue());
      calD.add(Calendar.DAY_OF_MONTH, k);

      EipTSchedule scheduleD = null;
      for (int l = 0; l < listD.size(); l++) {
        scheduleD = ((EipTScheduleMap) listD.get(l)).getEipTSchedule();
        if (scheduleD.getEndDate().compareTo(calD.getTime()) >= 0) {
          calD.add(Calendar.DAY_OF_MONTH, 1);
          if (scheduleD.getStartDate().compareTo(calD.getTime()) < 0) {
            List list2 = (List) scheduleMapList.get(k);
            EipTSchedule scheduleM = null;
            for (int m = 0; m < list2.size(); m++) {
              scheduleM = ((EipTScheduleMap) list2.get(m)).getEipTSchedule();
              if (scheduleD.getParentId().intValue() == scheduleM
                .getScheduleId()
                .intValue()) {
                list2.remove(m);
                scheduleMapList.set(k, list2);
                break;
              }
            }
          }
          calD.add(Calendar.DAY_OF_MONTH, -1);
        }
      }
    }

    int size = scheduleMapList.size();
    for (int i = 0; i < size; i++) {
      List slist = (List) scheduleMapList.get(i);

      // ソート
      Collections.sort(slist, new Comparator() {
        public int compare(Object a, Object b) {
          Calendar cal = Calendar.getInstance();
          Calendar cal2 = Calendar.getInstance();
          EipTSchedule p1 = null;
          EipTSchedule p2 = null;
          try {
            p1 = ((EipTScheduleMap) a).getEipTSchedule();
            p2 = ((EipTScheduleMap) b).getEipTSchedule();

          } catch (Exception e) {
            logger.error("Exception", e);
          }
          cal.setTime(p1.getStartDate());
          cal.set(0, 0, 0);
          cal2.setTime(p2.getStartDate());
          cal2.set(0, 0, 0);
          if ((cal.getTime()).compareTo(cal2.getTime()) != 0) {
            return (cal.getTime()).compareTo(cal2.getTime());
          } else {
            cal.setTime(p1.getEndDate());
            cal.set(0, 0, 0);
            cal2.setTime(p2.getEndDate());
            cal2.set(0, 0, 0);

            return (cal.getTime()).compareTo(cal2.getTime());
          }
        }
      });

      scheduleMapList.set(i, slist);
    }

    return new ResultList(scheduleMapList);
  }

  public ALEipUser getTargerUser() {
    return targerUser;
  }

  public void setTargerUser(ALEipUser targerUser) {
    this.targerUser = targerUser;
  }

}

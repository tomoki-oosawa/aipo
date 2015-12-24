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
package com.aimluck.eip.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
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
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 週間スケジュールの検索結果を管理するクラスです。
 *
 */
public class CellScheduleWeekSelectByMemberData extends
    CellScheduleWeekSelectData {

  /** <code>logger</code> logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleWeeklySelectData.class.getName());

  private ALCellStringField user;

  private int currentUserId;

  /** <code>login_user</code> 表示対象ユーザー */
  private ALEipUser targerUser;

  private String aclPortletFeature;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    user = new ALCellStringField();
    user.setNotNull(true);

    // 表示対象ユーザー取得
    String tmpTargetUser = rundata.getParameters().getString("selectedmember");
    if (tmpTargetUser == null || tmpTargetUser.equals("")) {
      targerUser = ALEipUtils.getALEipUser(rundata);
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_SELF;
    } else {
      targerUser = ALEipUtils.getALEipUser(Integer.parseInt(tmpTargetUser));
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_OTHER;
    }

    currentUserId = ALEipUtils.getUserId(rundata);
  }

  @Override
  public void initField() {

  }

  @Override
  protected ResultList<List<EipTScheduleMap>> selectList(RunData rundata,
      Context context) {
    ArrayList<List<EipTScheduleMap>> scheduleMapList =
      new ArrayList<List<EipTScheduleMap>>();
    Calendar cal = Calendar.getInstance();
    cal.setTime(getStartDate().getValue());
    // int userid = ALEipUtils.getUserId(rundata);
    int userid = (int) targerUser.getUserId().getValue();

    Expression expm1 =
      ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, Integer
        .valueOf(userid));
    Expression expm2 =
      ExpressionFactory.noMatchExp(EipTScheduleMap.STATUS_PROPERTY, "D");
    Expression expm3 =
      ExpressionFactory.noMatchExp(EipTScheduleMap.STATUS_PROPERTY, "R");
    Expression expm4 =
      ExpressionFactory.matchExp(
        EipTScheduleMap.TYPE_PROPERTY,
        ScheduleUtils.SCHEDULEMAP_TYPE_USER);

    Expression mapExpression = expm1.andExp(expm2).andExp(expm3).andExp(expm4);

    // 通常、期間スケジュール、または日単位繰り返し
    for (int k = 0; k < 7; k++) {
      SelectQuery<EipTScheduleMap> query =
        Database.query(EipTScheduleMap.class);

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

      query.setQualifier((exp10.orExp(exp20)).andExp(mapExpression));

      List<Ordering> orders = new ArrayList<Ordering>();
      orders.add(new Ordering(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.START_DATE_PROPERTY, true));
      orders.add(new Ordering(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.END_DATE_PROPERTY, true));
      query.getQuery().addOrderings(orders);

      List<EipTScheduleMap> list = query.fetchList();
      scheduleMapList.add(list);
    }

    // 週間、または毎月の場合
    SelectQuery<EipTScheduleMap> query = Database.query(EipTScheduleMap.class);

    Expression exp2 =
      ExpressionFactory.noMatchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, "N");
    Expression exp3 =
      ExpressionFactory.noMatchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, "S");

    query.setQualifier(mapExpression.andExp(exp2).andExp(exp3));
    List<EipTScheduleMap> list = query.fetchList();

    EipTSchedule schedule = null;
    for (int k = 0; k < list.size(); k++) {
      schedule = list.get(k).getEipTSchedule();
      String pattern = schedule.getRepeatPattern();
      // 週間
      if (pattern.startsWith("W")) {
        for (int l = 0; l < 7; l++) {
          if (pattern.charAt(l + 1) == '1') {
            int index = (l - cal.get(Calendar.DAY_OF_WEEK) + 7 + 1) % 7;
            Calendar cal3 = Calendar.getInstance();
            cal3.setTime(cal.getTime());
            cal3.add(Calendar.DAY_OF_MONTH, -(7 - index));
            if (pattern.length() == 9
              || cal3.get(Calendar.DAY_OF_WEEK_IN_MONTH) == Character
                .getNumericValue(pattern.charAt(8))) {
              Calendar cal2 = Calendar.getInstance();
              cal2.setTime(getStartDate().getValue());
              cal2.add(Calendar.DAY_OF_MONTH, index);
              if (pattern.endsWith("L")) {
                if (schedule.getEndDate().compareTo(cal2.getTime()) >= 0) {
                  cal2.add(Calendar.DAY_OF_MONTH, 1);
                  if (schedule.getStartDate().compareTo(cal2.getTime()) < 0) {
                    List<EipTScheduleMap> list2 = scheduleMapList.get(index);
                    list2.add(list.get(k));
                    scheduleMapList.set(index, list2);
                  }
                }
              } else {
                List<EipTScheduleMap> list2 = scheduleMapList.get(index);
                list2.add(list.get(k));
                scheduleMapList.set(index, list2);
              }
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
                List<EipTScheduleMap> list2 = scheduleMapList.get(index);
                list2.add(list.get(k));
                scheduleMapList.set(index, list2);
              }
            }
          } else {
            List<EipTScheduleMap> list2 = scheduleMapList.get(index);
            list2.add(list.get(k));
            scheduleMapList.set(index, list2);
          }
        }
        // 毎年
      } else if (pattern.startsWith("Y")) {
        int day = Integer.parseInt(pattern.substring(3, 5));
        int month = Integer.parseInt(pattern.substring(1, 3));
        Calendar cal2 = Calendar.getInstance();
        Calendar cal_event = Calendar.getInstance();
        cal2.setTime(getStartDate().getValue());
        cal2.set(Calendar.DAY_OF_MONTH, day);
        // JANUARY = 0 から始まるので月の設定は-1した値を使う
        cal2.set(Calendar.MONTH, month - 1);
        cal_event.setTime(getStartDate().getValue());

        int index = (day - cal_event.get(Calendar.DAY_OF_MONTH));
        if (index < 0) {
          index += cal_event.getActualMaximum(Calendar.DAY_OF_MONTH);
          cal_event.add(Calendar.MONTH, 1);
        }
        if (index >= 0
          && index <= 6
          && cal_event.getActualMaximum(Calendar.DAY_OF_MONTH) >= day
          && (cal_event.get(Calendar.MONTH) + 1) == month) {
          if (pattern.endsWith("L")) {
            if (schedule.getEndDate().compareTo(cal2.getTime()) >= 0) {
              cal2.add(Calendar.DAY_OF_MONTH, 1);
              if (schedule.getStartDate().compareTo(cal2.getTime()) < 0) {
                List<EipTScheduleMap> list2 = scheduleMapList.get(index);
                list2.add(list.get(k));
                scheduleMapList.set(index, list2);
              }
            }
          } else {
            List<EipTScheduleMap> list2 = scheduleMapList.get(index);
            list2.add(list.get(k));
            scheduleMapList.set(index, list2);
          }
        }
      }
    }

    // ダミースケジュールの処理

    SelectQuery<EipTScheduleMap> queryD = Database.query(EipTScheduleMap.class);

    Expression expD2 =
      ExpressionFactory.matchExp(EipTScheduleMap.STATUS_PROPERTY, "D");

    queryD.setQualifier(expm1.andExp(expm4).andExp(expD2));

    List<EipTScheduleMap> listD = queryD.fetchList();

    for (int k = 0; k < 7; k++) {
      Calendar calD = Calendar.getInstance();
      calD.setTime(getStartDate().getValue());
      calD.add(Calendar.DAY_OF_MONTH, k);

      EipTSchedule scheduleD = null;
      for (int l = 0; l < listD.size(); l++) {
        scheduleD = listD.get(l).getEipTSchedule();
        if (scheduleD.getEndDate().compareTo(calD.getTime()) >= 0) {
          calD.add(Calendar.DAY_OF_MONTH, 1);
          if (scheduleD.getStartDate().compareTo(calD.getTime()) < 0) {
            List<EipTScheduleMap> list2 = scheduleMapList.get(k);
            EipTSchedule scheduleM = null;
            for (int m = 0; m < list2.size(); m++) {
              scheduleM = list2.get(m).getEipTSchedule();
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
      List<EipTScheduleMap> slist = scheduleMapList.get(i);

      // ソート
      Collections.sort(slist, new Comparator<EipTScheduleMap>() {
        @Override
        public int compare(EipTScheduleMap a, EipTScheduleMap b) {
          Calendar cal = Calendar.getInstance();
          Calendar cal2 = Calendar.getInstance();
          EipTSchedule p1 = null;
          EipTSchedule p2 = null;
          try {
            p1 = (a).getEipTSchedule();
            p2 = (b).getEipTSchedule();
          } catch (Exception e) {
            logger.error("schedule", e);
          }

          // 期間スケジュールを先頭に表示
          if (p1.getRepeatPattern().equals("S")) {
            if (!p2.getRepeatPattern().equals("S")) {
              return -1;
            }
          } else {
            if (p2.getRepeatPattern().equals("S")) {
              return 1;
            }
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

    return new ResultList<List<EipTScheduleMap>>(scheduleMapList);
  }

  @Override
  protected Object getResultData(List<EipTScheduleMap> scheduleDayList)
      throws ALPageNotFoundException, ALDBErrorException {

    ArrayList<CellScheduleResultData> resultList =
      new ArrayList<CellScheduleResultData>();
    // ArrayList scheduleDayList = new ArrayList();
    int s = scheduleDayList.size();
    EipTScheduleMap map = null;

    for (int k = 0; k < s; k++) {
      CellScheduleResultData rd = new CellScheduleResultData();
      rd.initField();
      map = scheduleDayList.get(k);

      // is_member check (this schedule members have current user)
      SelectQuery<EipTScheduleMap> mapquery =
        Database.query(EipTScheduleMap.class);
      Expression expm1 =
        ExpressionFactory.matchExp(EipTScheduleMap.SCHEDULE_ID_PROPERTY, map
          .getScheduleId());
      Expression expm2 =
        ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, Integer
          .valueOf(currentUserId));
      Expression expm3 =
        ExpressionFactory.matchExp(
          EipTScheduleMap.TYPE_PROPERTY,
          ScheduleUtils.SCHEDULEMAP_TYPE_USER);
      Expression expm4 =
        ExpressionFactory.noMatchExp(EipTScheduleMap.STATUS_PROPERTY, "R");

      mapquery.setQualifier(expm1.andExp(expm2).andExp(expm3).andExp(expm4));

      int count = mapquery.getCount();
      boolean is_member = (count > 0) ? true : false;

      rd.setScheduleId(map.getScheduleId());
      rd.setName(map.getEipTSchedule().getName());
      rd.setStartDate(map.getEipTSchedule().getStartDate());
      rd.setEndDate(map.getEipTSchedule().getEndDate());
      rd.setPublic(map.getEipTSchedule().getPublicFlag().equals("O"));
      rd.setRepeat(map.getEipTSchedule().getRepeatPattern().equals("S"));
      rd.setPattern(map.getEipTSchedule().getRepeatPattern());
      rd.setMember(is_member);
      // // 期間スケジュールの場合
      if (rd.getPattern().equals("S")) {
        rd.setSpan(true);
      }

      boolean publicable = map.getEipTSchedule().getPublicFlag().equals("O");
      if (!publicable && !is_member) {
        rd.setName(ALLocalizationUtils.getl10n("SCHEDULE_CLOSE_PUBLIC_WORD"));
      }
      boolean hidden = map.getEipTSchedule().getPublicFlag().equals("P");
      if (!hidden || is_member) {
        resultList.add(rd);
      }
    }
    return resultList;
  }

  public ALEipUser getTargerUser() {
    return targerUser;
  }

  public void setTargerUser(ALEipUser targerUser) {
    this.targerUser = targerUser;
  }

  @Override
  public String getAclPortletFeature() {
    return aclPortletFeature;
  }

  public String getAliasNameText() {
    return ALLocalizationUtils.getl10nFormat(
      "SCHEDULE_ONES_SCHEDULE",
      targerUser.getAliasName().toString());
  }
}

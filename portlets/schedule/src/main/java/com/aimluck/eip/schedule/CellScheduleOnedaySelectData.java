/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2010 Aimluck,Inc.
 * http://aipostyle.com/
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALCellDateTimeField;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * スケジュール1日表示の検索結果を管理するクラスです。
 * 
 */
public class CellScheduleOnedaySelectData extends ScheduleOnedaySelectData {

  /** <code>logger</code> logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CellScheduleOnedaySelectData.class.getName());

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected ResultList<EipTScheduleMap> selectList(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    try {
      List<EipTScheduleMap> resultBaseList =
        getSelectQuery(rundata, context).fetchList();

      List<EipTScheduleMap> resultList =
        ScheduleUtils.sortByDummySchedule(resultBaseList);

      List<EipTScheduleMap> list = new ArrayList<EipTScheduleMap>();
      int resultSize = resultList.size();
      int DummySize = 0;
      boolean flg = false;
      boolean canAdd = true;
      for (int i = 0; i < resultSize; i++) {
        EipTScheduleMap record = resultList.get(i);
        EipTSchedule schedule = (record.getEipTSchedule());
        canAdd = true;

        if (!record.getStatus().equals("D")) {
          if (!flg) {
            DummySize = i;
            flg = true;
          }
          for (int j = 0; j < DummySize; j++) {
            EipTScheduleMap record2 = resultList.get(j);
            EipTSchedule schedule2 = (record2.getEipTSchedule());
            if (!schedule.getRepeatPattern().equals("N")
              && schedule.getScheduleId().equals(schedule2.getParentId())) {
              canAdd = false;
              break;
            }
          }
        } else {
          canAdd = false;
        }

        /*
         * for (int j = 0; j < size; j++) { EipTScheduleMap record2 =
         * (EipTScheduleMap) list.get(j); EipTSchedule schedule2 =
         * (EipTSchedule) (record2.getEipTSchedule()); if
         * (!schedule.getRepeatPattern().equals("N") &&
         * "D".equals(record2.getStatus()) && schedule.getScheduleId() ==
         * schedule2.getParentId()) { canAdd = false; break; }
         * 
         * if (!schedule2.getRepeatPattern().equals("N") &&
         * "D".equals(record.getStatus()) && schedule2.getScheduleId() ==
         * schedule.getParentId()) { // [繰り返しスケジュール] 親の ID を検索 if
         * (!delList.contains(record2)) { delList.add(record2); } canAdd = true;
         * } }
         * 
         * delSize = delList.size(); for (int k = 0; k < delSize; k++) {
         * list.remove(delList.get(k)); }
         */

        if (canAdd) {
          list.add(record);
        }
      }

      // ダミーを削除する．
      /*
       * delList.clear(); size = list.size(); for (int i = 0; i < size; i++) {
       * EipTScheduleMap record = (EipTScheduleMap) list.get(i); if
       * ("D".equals(record.getStatus())) { delList.add(record); } } delSize =
       * delList.size(); for (int i = 0; i < delSize; i++) {
       * list.remove(delList.get(i)); }
       */

      // ソート
      Collections.sort(list, new Comparator<EipTScheduleMap>() {

        public int compare(EipTScheduleMap a, EipTScheduleMap b) {
          Calendar cal = Calendar.getInstance();
          Calendar cal2 = Calendar.getInstance();
          EipTSchedule p1 = null;
          EipTSchedule p2 = null;
          try {
            p1 = (a).getEipTSchedule();
            p2 = (b).getEipTSchedule();

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

      int listsize = list.size();
      setPageParam(listsize);
      int start = getStart();
      int rowsNum = this.getRowsNum();
      int count = -1;
      ArrayList<EipTScheduleMap> reslist = new ArrayList<EipTScheduleMap>();
      for (int i = 0; i < rowsNum; i++) {
        count = i + start;
        if (count >= listsize) {
          break;
        }
        reslist.add(list.get(count));
      }

      if (viewToDo == 1) {
        // ToDo の読み込み
        loadToDo(rundata, context);
      }

      return new ResultList<EipTScheduleMap>(reslist);
    } catch (Exception e) {
      logger.error("[ScheduleOnedaySelectData]", e);
      throw new ALDBErrorException();
    }
  }

  /**
   * 検索条件を設定した SelectQuery を返します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected SelectQuery<EipTScheduleMap> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipTScheduleMap> query = Database.query(EipTScheduleMap.class);

    Expression exp1 =
      ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, Integer
        .valueOf(ALEipUtils.getUserId(rundata)));
    query.setQualifier(exp1);
    Expression exp2 =
      ExpressionFactory.matchExp(
        EipTScheduleMap.TYPE_PROPERTY,
        ScheduleUtils.SCHEDULEMAP_TYPE_USER);
    query.andQualifier(exp2);

    // 終了日時
    Expression exp11 =
      ExpressionFactory.greaterOrEqualExp(
        EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
          + "."
          + EipTSchedule.END_DATE_PROPERTY,
        getViewDate().getValue());

    // 日付を1日ずつずらす
    Calendar cal = Calendar.getInstance();
    cal.setTime(getViewDate().getValue());
    cal.add(Calendar.DATE, 1);
    ALCellDateTimeField field = new ALCellDateTimeField();
    field.setValue(cal.getTime());
    // 開始日時
    // LESS_EQUALからLESS_THANへ修正、期間スケジュールFIXのため(Haruo Kaneko)
    Expression exp12 =
      ExpressionFactory.lessExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.START_DATE_PROPERTY, field.getValue());

    // 通常スケジュール
    Expression exp13 =
      ExpressionFactory.matchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, "N");
    // 期間スケジュール
    Expression exp14 =
      ExpressionFactory.matchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, "S");

    // 繰り返しスケジュール（週間）
    Calendar date = Calendar.getInstance();
    date.setTime(getViewDate().getValue());
    int weekindex = date.get(Calendar.DAY_OF_WEEK);
    String token = null;
    StringBuffer sb = new StringBuffer();
    sb.append("W");
    for (int i = 0; i < 7; i++) {
      if (i == weekindex) {
        token = "1";
      } else {
        token = "_";
      }
      sb.append(token);
    }

    Expression exp21 =
      ExpressionFactory.likeExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, (sb.toString() + "L"));
    Expression exp22 =
      ExpressionFactory.likeExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, (sb.toString() + "N"));

    // 繰り返しスケジュール（日）
    Expression exp23 =
      ExpressionFactory.matchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, "DN");
    Expression exp31 =
      ExpressionFactory.matchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, "DL");

    // 繰り返しスケジュール（月）
    SimpleDateFormat sdf = new SimpleDateFormat("dd");
    sdf.setTimeZone(TimeZone.getDefault());
    String dayStr = sdf.format(date.getTime());

    Expression exp24 =
      ExpressionFactory.likeExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, ("M" + dayStr + "L"));
    Expression exp25 =
      ExpressionFactory.likeExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, ("M" + dayStr + "N"));

    query.andQualifier((exp11.andExp(exp12).andExp(((exp13).orExp(exp14))
      .orExp(exp21)
      .orExp(exp31)
      .orExp(exp24))).orExp(exp22.orExp(exp23).orExp(exp25)));

    // 開始日時でソート
    List<Ordering> orders = new ArrayList<Ordering>();
    orders.add(new Ordering(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
      + "."
      + EipTSchedule.START_DATE_PROPERTY, true));
    orders.add(new Ordering(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
      + "."
      + EipTSchedule.END_DATE_PROPERTY, true));
    query.getQuery().addOrderings(orders);

    return query;
  }

  /**
   * 
   * @param record
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultData(EipTScheduleMap record)
      throws ALPageNotFoundException, ALDBErrorException {
    CellScheduleResultData rd = new CellScheduleResultData();
    CellScheduleResultData rd2 = new CellScheduleResultData();
    rd.initField();
    rd2.setFormat("yyyy-MM-dd-HH-mm");
    rd2.initField();
    try {
      EipTSchedule schedule = record.getEipTSchedule();
      if ("R".equals(record.getStatus())) {
        return rd;
      }
      if (!ScheduleUtils.isView(
        getViewDate(),
        schedule.getRepeatPattern(),
        schedule.getStartDate(),
        schedule.getEndDate())) {
        return rd;
      }
      // ID
      rd.setScheduleId(schedule.getScheduleId().intValue());
      // 親スケジュール ID
      rd.setParentId(schedule.getParentId().intValue());
      // 予定
      rd.setName(schedule.getName());
      // 開始時間
      rd.setStartDate(schedule.getStartDate());
      // 終了時間
      rd.setEndDate(schedule.getEndDate());
      // 仮スケジュールかどうか
      rd.setTmpreserve("T".equals(record.getStatus()));
      // 公開するかどうか
      rd.setPublic("O".equals(schedule.getPublicFlag()));
      // 表示するかどうか
      rd.setHidden("P".equals(schedule.getPublicFlag()));
      // ダミーか
      // rd.setDummy("D".equals(record.getStatus()));
      // 繰り返しパターン
      rd.setPattern(schedule.getRepeatPattern());

      // // 期間スケジュールの場合
      if (rd.getPattern().equals("S")) {
        // spanResultData = rd;
        rd.setSpan(true);
        return rd;
      }

      // 繰り返しスケジュールの場合
      if (!rd.getPattern().equals("N")) {

        if (!ScheduleUtils.isView(getViewDate(), rd.getPattern(), rd
          .getStartDate()
          .getValue(), rd.getEndDate().getValue())) {
          return rd;
        }
        rd.setRepeat(true);
      }

    } catch (Exception e) {
      logger.error("Exception", e);

      return null;
    }
    return rd;
  }

  @Override
  protected String getPortletURItoTodo(RunData rundata, long entityid,
      String schedulePortletId) {
    return ScheduleUtils.getPortletURItoTodoDetailPaneForCell(
      rundata,
      "Cell_ToDo",
      entityid,
      schedulePortletId);
  }
}

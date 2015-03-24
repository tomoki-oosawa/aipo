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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.cayenne.om.portlet.VEipTScheduleList;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.query.ResultList;
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
  protected ResultList<VEipTScheduleList> selectList(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    try {
      List<VEipTScheduleList> resultBaseList =
        getScheduleList(rundata, context);

      List<VEipTScheduleList> resultList =
        ScheduleUtils.sortByDummySchedule(resultBaseList);

      List<VEipTScheduleList> list = new ArrayList<VEipTScheduleList>();
      int resultSize = resultList.size();
      int DummySize = 0;
      boolean flg = false;
      boolean canAdd = true;
      for (int i = 0; i < resultSize; i++) {
        VEipTScheduleList record = resultList.get(i);
        canAdd = true;

        if (!record.getStatus().equals("D")) {
          if (!flg) {
            DummySize = i;
            flg = true;
          }
          for (int j = 0; j < DummySize; j++) {
            VEipTScheduleList record2 = resultList.get(j);
            if (!record.getRepeatPattern().equals("N")
              && record.getScheduleId().equals(record2.getParentId())) {
              canAdd = false;
              break;
            }
          }
        } else {
          canAdd = false;
        }
        if (canAdd) {
          list.add(record);
        }
      }

      // ソート
      Collections.sort(list, new Comparator<VEipTScheduleList>() {

        @Override
        public int compare(VEipTScheduleList a, VEipTScheduleList b) {
          Calendar cal = Calendar.getInstance();
          Calendar cal2 = Calendar.getInstance();

          // 期間スケジュールを先頭に表示
          if (a.getRepeatPattern().equals("S")) {
            if (!b.getRepeatPattern().equals("S")) {
              return -1;
            }
          } else {
            if (b.getRepeatPattern().equals("S")) {
              return 1;
            }
          }

          cal.setTime(a.getStartDate());
          cal.set(0, 0, 0);
          cal2.setTime(b.getStartDate());
          cal2.set(0, 0, 0);
          if ((cal.getTime()).compareTo(cal2.getTime()) != 0) {
            return (cal.getTime()).compareTo(cal2.getTime());
          } else {
            cal.setTime(a.getEndDate());
            cal.set(0, 0, 0);
            cal2.setTime(b.getEndDate());
            cal2.set(0, 0, 0);

            return (cal.getTime()).compareTo(cal2.getTime());
          }
        }
      });

      if (viewToDo == 1) {
        // ToDo の読み込み
        loadToDo(rundata, context);
      }

      return new ResultList<VEipTScheduleList>(list);
    } catch (Exception e) {
      logger.error("[CellScheduleOnedaySelectData]", e);
      throw new ALDBErrorException();
    }
  }

  protected List<VEipTScheduleList> getScheduleList(RunData rundata,
      Context context) {

    Calendar cal = Calendar.getInstance();
    cal.setTime(getViewDate().getValue());
    cal.add(Calendar.DATE, 1);
    cal.add(Calendar.MILLISECOND, -1);
    ALDateTimeField field = new ALDateTimeField();
    field.setValue(cal.getTime());

    Integer userid = Integer.valueOf(ALEipUtils.getUserId(rundata));

    return ScheduleUtils.getScheduleList(
      userid,
      getViewDate().getValue(),
      field.getValue(),
      Arrays.asList(userid),
      null);
  }

  /**
   * 
   * @param record
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultData(VEipTScheduleList record)
      throws ALPageNotFoundException, ALDBErrorException {
    CellScheduleResultData rd = new CellScheduleResultData();
    CellScheduleResultData rd2 = new CellScheduleResultData();
    rd.initField();
    rd2.setFormat("yyyy-MM-dd-HH-mm");
    rd2.initField();
    try {
      if ("R".equals(record.getStatus())) {
        return null;
      }
      if (!ScheduleUtils.isView(
        getViewDate(),
        record.getRepeatPattern(),
        record.getStartDate(),
        record.getEndDate())) {
        return null;
      }
      // ID
      rd.setScheduleId(record.getScheduleId().intValue());
      // 親スケジュール ID
      rd.setParentId(record.getParentId().intValue());
      // タイトル
      rd.setName(record.getName());
      // 開始時間
      rd.setStartDate(record.getStartDate());
      // 終了時間
      rd.setEndDate(record.getEndDate());
      // 仮スケジュールかどうか
      rd.setTmpreserve("T".equals(record.getStatus()));
      // 公開するかどうか
      rd.setPublic("O".equals(record.getPublicFlag()));
      // 表示するかどうか
      rd.setHidden("P".equals(record.getPublicFlag()));
      // ダミーか
      // rd.setDummy("D".equals(record.getStatus()));
      // 繰り返しパターン
      rd.setPattern(record.getRepeatPattern());

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
      logger.error("[CellScheduleOnedaySelectData]", e);

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

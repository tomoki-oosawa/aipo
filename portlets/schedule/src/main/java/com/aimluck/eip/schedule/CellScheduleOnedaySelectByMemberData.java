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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALCellDateTimeField;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.cayenne.om.portlet.EipTTodo;
import com.aimluck.eip.cayenne.om.portlet.auto._EipTSchedule;
import com.aimluck.eip.cayenne.om.portlet.auto._EipTScheduleMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.todo.util.ToDoUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * スケジュール1日表示の検索結果を管理するクラスです。
 * 
 */
public class CellScheduleOnedaySelectByMemberData extends CellScheduleOnedaySelectData {

  /** <code>logger</code> logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(CellScheduleOnedaySelectByMemberData.class.getName());

  /** <code>login_user</code> 表示対象ユーザー */
  private ALEipUser targerUser;
  
  private DataContext dataContext;
  
  /** <code>todoList</code> ToDo リスト */
  private List todoList;
  
  /** ポートレット ID */
  private String portletId;
  
  /** ログインユーザID */
  private int userid;
  
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
    dataContext = DatabaseOrmService.getInstance().getDataContext();
    
    userid = ALEipUtils.getUserId(rundata);
    
    // 表示対象ユーザー取得
    String s = rundata.getParameters().getString("selectedmember");
    if (s != null) {
        targerUser = ALEipUtils.getALEipUser(Integer.parseInt(s));
    } else {
        //ToDo詳細画面に遷移後に"前画面に戻る"で戻ってきた場合
        s = ALEipUtils.getTemp(rundata, context, "target_otheruser_id");
        targerUser = ALEipUtils.getALEipUser(Integer.parseInt(s));
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
  protected SelectQuery getSelectQuery(RunData rundata, Context context) {
    SelectQuery query = new SelectQuery(EipTScheduleMap.class);

    Expression exp1 = ExpressionFactory.matchExp(
        _EipTScheduleMap.USER_ID_PROPERTY, targerUser.getUserId().getValue());
    query.setQualifier(exp1);
    Expression exp2 = ExpressionFactory.matchExp(_EipTScheduleMap.TYPE_PROPERTY,
        ScheduleUtils.SCHEDULEMAP_TYPE_USER);
    query.andQualifier(exp2);

    // 終了日時
    Expression exp11 = ExpressionFactory.greaterOrEqualExp(
        _EipTScheduleMap.EIP_TSCHEDULE_PROPERTY + "."
            + _EipTSchedule.END_DATE_PROPERTY, getViewDate().getValue());

    // 日付を1日ずつずらす
    Calendar cal = Calendar.getInstance();
    cal.setTime(getViewDate().getValue());
    cal.add(Calendar.DATE, 1);
    ALCellDateTimeField field = new ALCellDateTimeField();
    field.setValue(cal.getTime());
    // 開始日時
    // LESS_EQUALからLESS_THANへ修正、期間スケジュールFIXのため(Haruo Kaneko)
    Expression exp12 = ExpressionFactory.lessExp(
        _EipTScheduleMap.EIP_TSCHEDULE_PROPERTY + "."
            + _EipTSchedule.START_DATE_PROPERTY, field.getValue());

    // 通常スケジュール
    Expression exp13 = ExpressionFactory.matchExp(
        _EipTScheduleMap.EIP_TSCHEDULE_PROPERTY + "."
            + _EipTSchedule.REPEAT_PATTERN_PROPERTY, "N");
    // 期間スケジュール
    Expression exp14 = ExpressionFactory.matchExp(
        _EipTScheduleMap.EIP_TSCHEDULE_PROPERTY + "."
            + _EipTSchedule.REPEAT_PATTERN_PROPERTY, "S");

    // 繰り返しスケジュール（週間）
    Date date = getViewDate().getValue();
    int weekindex = date.getDay();
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

    Expression exp21 = ExpressionFactory.likeExp(
        _EipTScheduleMap.EIP_TSCHEDULE_PROPERTY + "."
            + _EipTSchedule.REPEAT_PATTERN_PROPERTY, (sb.toString() + "L"));
    Expression exp22 = ExpressionFactory.likeExp(
        _EipTScheduleMap.EIP_TSCHEDULE_PROPERTY + "."
            + _EipTSchedule.REPEAT_PATTERN_PROPERTY, (sb.toString() + "N"));

    // 繰り返しスケジュール（日）
    Expression exp23 = ExpressionFactory.matchExp(
        _EipTScheduleMap.EIP_TSCHEDULE_PROPERTY + "."
            + _EipTSchedule.REPEAT_PATTERN_PROPERTY, "DN");
    Expression exp31 = ExpressionFactory.matchExp(
        _EipTScheduleMap.EIP_TSCHEDULE_PROPERTY + "."
            + _EipTSchedule.REPEAT_PATTERN_PROPERTY, "DL");

    // 繰り返しスケジュール（月）
    SimpleDateFormat sdf = new SimpleDateFormat("dd");
    sdf.setTimeZone(TimeZone.getDefault());
    String dayStr = sdf.format(date);

    Expression exp24 = ExpressionFactory.likeExp(
        _EipTScheduleMap.EIP_TSCHEDULE_PROPERTY + "."
            + _EipTSchedule.REPEAT_PATTERN_PROPERTY, ("M" + dayStr + "L"));
    Expression exp25 = ExpressionFactory.likeExp(
        _EipTScheduleMap.EIP_TSCHEDULE_PROPERTY + "."
            + _EipTSchedule.REPEAT_PATTERN_PROPERTY, ("M" + dayStr + "N"));

    query.andQualifier((exp11.andExp(exp12).andExp(((exp13).orExp(exp14))
        .orExp(exp21).orExp(exp31).orExp(exp24))).orExp(exp22.orExp(exp23)
        .orExp(exp25)));

    // 開始日時でソート
    List orders = new ArrayList();
    orders.add(new Ordering(_EipTScheduleMap.EIP_TSCHEDULE_PROPERTY + "."
        + _EipTSchedule.START_DATE_PROPERTY, true));
    orders.add(new Ordering(_EipTScheduleMap.EIP_TSCHEDULE_PROPERTY + "."
        + _EipTSchedule.END_DATE_PROPERTY, true));
    query.addOrderings(orders);

    return query;
  }
  
  public void loadToDo(RunData rundata, Context context) {
    todoList = new ArrayList();
    try {
      SelectQuery query = getSelectQueryForTodo(rundata, context);
      List todos = dataContext.performQuery(query);

      int todosize = todos.size();
      for (int i = 0; i < todosize; i++) {
        EipTTodo record = (EipTTodo) todos.get(i);
        ScheduleToDoResultData rd = new ScheduleToDoResultData();
        rd.initField();

        // ポートレット ToDo のへのリンクを取得する．
        String todo_url = getPortletURItoTodo(rundata, record.getTodoId()
            .longValue(), portletId);

        rd.setTodoId(record.getTodoId().intValue());
        rd.setTodoName(record.getTodoName());
        rd.setUserId(record.getTurbineUser().getUserId().intValue());
        rd.setStartDate(record.getStartDate());
        rd.setEndDate(record.getEndDate());
        rd.setTodoUrl(todo_url);
        // 公開/非公開を設定する．
        rd.setPublicFlag("T".equals(record.getPublicFlag()));
        todoList.add(rd);
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return;
    }
  }

  private SelectQuery getSelectQueryForTodo(RunData rundata, Context context) {
    //Integer uid = Integer.valueOf(ALEipUtils.getUserId(rundata));
    Integer uid = (int)targerUser.getUserId().getValue();
    SelectQuery query = new SelectQuery(EipTTodo.class);

    Expression exp1 = ExpressionFactory.noMatchExp(EipTTodo.STATE_PROPERTY,
        Short.valueOf((short) 100));
    query.setQualifier(exp1);
    Expression exp2 = ExpressionFactory.matchExp(
        EipTTodo.ADDON_SCHEDULE_FLG_PROPERTY, "T");
    query.andQualifier(exp2);
    Expression exp3 = ExpressionFactory.matchDbExp(
        TurbineUser.USER_ID_PK_COLUMN, uid);
    query.andQualifier(exp3);

    // 終了日時
    Expression exp11 = ExpressionFactory.greaterOrEqualExp(
        EipTTodo.END_DATE_PROPERTY, getViewDate().getValue());
    // 開始日時
    Expression exp12 = ExpressionFactory.lessOrEqualExp(
        EipTTodo.START_DATE_PROPERTY, getViewDate().getValue());

    // 開始日時のみ指定されている ToDo を検索
    Expression exp21 = ExpressionFactory.lessOrEqualExp(
        EipTTodo.START_DATE_PROPERTY, getViewDate().getValue());
    Expression exp22 = ExpressionFactory.matchExp(EipTTodo.END_DATE_PROPERTY,
        ToDoUtils.getEmptyDate());

    // 終了日時のみ指定されている ToDo を検索
    Expression exp31 = ExpressionFactory.greaterOrEqualExp(
        EipTTodo.END_DATE_PROPERTY, getViewDate().getValue());
    Expression exp32 = ExpressionFactory.matchExp(EipTTodo.START_DATE_PROPERTY,
        ToDoUtils.getEmptyDate());

    query.andQualifier((exp11.andExp(exp12)).orExp(exp21.andExp(exp22)).orExp(
        exp31.andExp(exp32)));
    return query;
  }
  
  /*
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultData(java.lang.Object)
   */
  protected Object getResultData(Object obj) throws ALPageNotFoundException,
      ALDBErrorException {
    CellScheduleResultData rd = new CellScheduleResultData();
    CellScheduleResultData rd2 = new CellScheduleResultData();
    rd.initField();
    rd2.setFormat("yyyy-MM-dd-HH-mm");
    rd2.initField();
    try {
      EipTScheduleMap record = (EipTScheduleMap) obj;
      EipTSchedule schedule = record.getEipTSchedule();
      if ("R".equals(record.getStatus()))
      	//参加メンバーが削除したレコード
        return rd;
      if (!ScheduleUtils.isView(getViewDate(), schedule.getRepeatPattern(),
          schedule.getStartDate(), schedule.getEndDate())) {
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
      
      // is_memberのチェック
      SelectQuery mapquery = new SelectQuery(EipTScheduleMap.class);
      Expression mapexp1 = ExpressionFactory.matchExp(
          EipTScheduleMap.SCHEDULE_ID_PROPERTY, record.getScheduleId());
      mapquery.setQualifier(mapexp1);

      Expression mapexp2 = ExpressionFactory.matchExp(
          EipTScheduleMap.USER_ID_PROPERTY, Integer.valueOf(userid));
      mapquery.andQualifier(mapexp2);
      
      Expression mapexp3 = ExpressionFactory.noMatchExp(
          EipTScheduleMap.STATUS_PROPERTY, "R");
      mapquery.andQualifier(mapexp3);

      List schedulemaps = dataContext.performQuery(mapquery);
      
      boolean is_member = (schedulemaps != null && schedulemaps.size() > 0) ? true
          : false;
      rd.setMember(is_member);
      
      //if( schedulemaps!=null ) {
      //	System.out.println("名前=" + rd.getName() + " レコード数=" + schedulemaps.size());
      //}
      
      //非公開／完全に隠す　に対応
      boolean publicable = schedule.getPublicFlag().equals("O");
      if (!publicable && !is_member) {
        rd.setName("非公開");
      }
      boolean hidden = schedule.getPublicFlag().equals("P");
      if ( hidden && !is_member) {
      	//「完全に隠す」でメンバーでない場合
      	return null;
      }

      // // 期間スケジュールの場合
      if (rd.getPattern().equals("S")) {
        // spanResultData = rd;
        rd.setSpan(true);
        return rd;
      }

      // 繰り返しスケジュールの場合
      if (!rd.getPattern().equals("N")) {

        if (!ScheduleUtils.isView(getViewDate(), rd.getPattern(), rd
            .getStartDate().getValue(), rd.getEndDate().getValue())) {
          return rd;
        }
        rd.setRepeat(true);
      }

    } catch (Exception e) {
      // TODO: エラー処理
      logger.error("Exception", e);

      return null;
    }
    return rd;
  }
  
  public ALEipUser getTargerUser() {
    return targerUser;
  }

  public void setTargerUser(ALEipUser targerUser) {
      this.targerUser = targerUser;
  }

  public List getToDoResultDataList() {
    return todoList;
  }

  @Override
  public void setPortletId(String id) {
      portletId = id;
  }
}

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
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.cayenne.om.portlet.EipTTodo;
import com.aimluck.eip.cayenne.om.portlet.VEipTScheduleList;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.todo.util.ToDoUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * スケジュール1日表示の検索結果を管理するクラスです。
 * 
 */
public class CellScheduleOnedaySelectByMemberData extends
    CellScheduleOnedaySelectData {

  /** <code>logger</code> logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CellScheduleOnedaySelectByMemberData.class.getName());

  /** <code>login_user</code> 表示対象ユーザー */
  private ALEipUser targerUser;

  /** <code>todoList</code> ToDo リスト */
  private List<ScheduleToDoResultData> todoList;

  /** ポートレット ID */
  private String portletId;

  /** ログインユーザID */
  private int userid;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    userid = ALEipUtils.getUserId(rundata);

    // 表示対象ユーザー取得
    String s = rundata.getParameters().getString("selectedmember");
    if (s != null) {
      targerUser = ALEipUtils.getALEipUser(Integer.parseInt(s));
    } else {
      // ToDo詳細画面に遷移後に"前画面に戻る"で戻ってきた場合
      s = ALEipUtils.getTemp(rundata, context, "target_otheruser_id");
      targerUser = ALEipUtils.getALEipUser(Integer.parseInt(s));
    }
  }

  @Override
  protected List<VEipTScheduleList> getScheduleList(RunData rundata,
      Context context) {

    Calendar cal = Calendar.getInstance();
    cal.setTime(getViewDate().getValue());
    cal.add(Calendar.DATE, 1);
    cal.add(Calendar.MILLISECOND, -1);
    ALDateTimeField field = new ALDateTimeField();
    field.setValue(cal.getTime());

    return ScheduleUtils.getScheduleList(
      userid,
      getViewDate().getValue(),
      field.getValue(),
      Arrays.asList((int) targerUser.getUserId().getValue()),
      null);
  }

  @Override
  public void loadToDo(RunData rundata, Context context) {
    todoList = new ArrayList<ScheduleToDoResultData>();
    try {
      SelectQuery<EipTTodo> query = getSelectQueryForTodo(rundata, context);
      List<EipTTodo> todos = query.fetchList();

      int todosize = todos.size();
      for (int i = 0; i < todosize; i++) {
        EipTTodo record = todos.get(i);
        ScheduleToDoResultData rd = new ScheduleToDoResultData();
        rd.initField();

        // ポートレット ToDo のへのリンクを取得する．
        String todo_url =
          getPortletURItoTodo(
            rundata,
            record.getTodoId().longValue(),
            portletId);

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
      logger.error("schedule", ex);
      return;
    }
  }

  private SelectQuery<EipTTodo> getSelectQueryForTodo(RunData rundata,
      Context context) {
    // Integer uid = Integer.valueOf(ALEipUtils.getUserId(rundata));
    Integer uid = (int) targerUser.getUserId().getValue();
    SelectQuery<EipTTodo> query = Database.query(EipTTodo.class);

    Expression exp1 =
      ExpressionFactory.noMatchExp(EipTTodo.STATE_PROPERTY, Short
        .valueOf((short) 100));
    query.setQualifier(exp1);
    Expression exp2 =
      ExpressionFactory.matchExp(EipTTodo.ADDON_SCHEDULE_FLG_PROPERTY, "T");
    query.andQualifier(exp2);
    Expression exp3 =
      ExpressionFactory.matchDbExp(TurbineUser.USER_ID_PK_COLUMN, uid);
    query.andQualifier(exp3);

    // 終了日時
    Expression exp11 =
      ExpressionFactory.greaterOrEqualExp(
        EipTTodo.END_DATE_PROPERTY,
        getViewDate().getValue());
    // 開始日時
    Expression exp12 =
      ExpressionFactory.lessOrEqualExp(
        EipTTodo.START_DATE_PROPERTY,
        getViewDate().getValue());

    // 開始日時のみ指定されている ToDo を検索
    Expression exp21 =
      ExpressionFactory.lessOrEqualExp(
        EipTTodo.START_DATE_PROPERTY,
        getViewDate().getValue());
    Expression exp22 =
      ExpressionFactory.matchExp(EipTTodo.END_DATE_PROPERTY, ToDoUtils
        .getEmptyDate());

    // 終了日時のみ指定されている ToDo を検索
    Expression exp31 =
      ExpressionFactory.greaterOrEqualExp(
        EipTTodo.END_DATE_PROPERTY,
        getViewDate().getValue());
    Expression exp32 =
      ExpressionFactory.matchExp(EipTTodo.START_DATE_PROPERTY, ToDoUtils
        .getEmptyDate());

    query.andQualifier((exp11.andExp(exp12)).orExp(exp21.andExp(exp22)).orExp(
      exp31.andExp(exp32)));
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
  protected Object getResultData(VEipTScheduleList record)
      throws ALPageNotFoundException, ALDBErrorException {
    CellScheduleResultData rd = new CellScheduleResultData();
    CellScheduleResultData rd2 = new CellScheduleResultData();
    rd.initField();
    rd2.setFormat("yyyy-MM-dd-HH-mm");
    rd2.initField();
    try {
      if ("R".equals(record.getStatus())) {
        // 参加ユーザーが削除したレコード
        return rd;
      }
      if (!ScheduleUtils.isView(
        getViewDate(),
        record.getRepeatPattern(),
        record.getStartDate(),
        record.getEndDate())) {
        return rd;
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

      boolean is_member = record.isMember();

      // 非公開／完全に隠す　に対応
      boolean publicable = record.getPublicFlag().equals("O");
      if (!publicable && !is_member) {
        rd.setName(ALLocalizationUtils.getl10n("SCHEDULE_CLOSE_PUBLIC_WORD"));
      }
      boolean hidden = record.getPublicFlag().equals("P");
      if (hidden && !is_member) {
        // 「完全に隠す」でメンバーでない場合
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
          .getStartDate()
          .getValue(), rd.getEndDate().getValue())) {
          return rd;
        }
        rd.setRepeat(true);
      }

    } catch (Exception e) {
      logger.error("schedule", e);

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

  @Override
  public List<ScheduleToDoResultData> getToDoResultDataList() {
    return todoList;
  }

  @Override
  public void setPortletId(String id) {
    portletId = id;
  }

  public String getAliasNameText() {
    return ALLocalizationUtils.getl10nFormat("SCHEDULE_GO_SCHEDULE", targerUser
      .getAliasName()
      .toString());
  }

  public String getAliasNameOnedayListText() {
    return ALLocalizationUtils.getl10nFormat(
      "SCHEDULE_ONES_ONEDAY_LIST",
      targerUser.getAliasName().toString());
  }
}

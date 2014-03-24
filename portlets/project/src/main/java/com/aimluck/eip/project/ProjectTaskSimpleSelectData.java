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
 *
 * Project Management Portlet was developed by Advance,Inc.
 * http://www.a-dvance.co.jp/
 */

package com.aimluck.eip.project;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.cayenne.DataRow;
import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.cayenne.om.portlet.EipTProjectTask;
import com.aimluck.eip.cayenne.om.portlet.EipTProjectTaskFile;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipHolidaysManager;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SQLTemplate;
import com.aimluck.eip.project.util.ProjectFile;
import com.aimluck.eip.project.util.ProjectUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タスクの検索データを管理するクラスです。 <BR>
 * 
 */
public class ProjectTaskSimpleSelectData extends
    ALAbstractSelectData<EipTProjectTask, EipTProjectTask> implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ProjectTaskSimpleSelectData.class.getName());

  /** タスクの総数 */
  private int taskCount;

  /** 全プロジェクトの一覧 */
  private List<ProjectResultData> allProject;

  /** 選択されているプロジェクトID */
  private Integer selectedProjectId;

  /** ファイル操作オブジェクト */
  private final ProjectFile<EipTProjectTaskFile, EipTProjectTask> pfile =
    new ProjectFile<EipTProjectTaskFile, EipTProjectTask>(
      EipTProjectTaskFile.class);

  /** プロジェクトメンバーリスト */
  private List<ALEipUser> projectMembers;

  /** 基準日From（検索用） */
  private ALDateTimeField base_date_from;

  /** 基準日To（検索用） */
  private ALDateTimeField base_date_to;

  /** キーワード（検索用） */
  private String target_keyword;

  /** 担当者（検索用） */
  private String target_user_id;

  /** 分類（検索用） */
  private String target_tracker;

  /** 優先度（検索用） */
  private String target_priority;

  /** ステータス（検索用） */
  private String target_status;

  /** 進捗率FROM（検索用） */
  private String target_progress_rate_from;

  /** 進捗率TO（検索用） */
  private String target_progress_rate_to;

  /** 進捗遅れフラグ（検索用） */
  private String target_delay;

  /** イナズマ線表示フラグ（検索用） */
  private String progress_line_checked;

  /** 選択最大年 */
  private int viewDateMaxYear;

  /** インデント表示フラグ */
  private boolean indentFlg = true;

  private Calendar calFrom;

  private Calendar calTo;

  /**
   * 初期設定
   * 
   * @param action
   *          ALAction
   * @param rundata
   *          RunData
   * @param context
   *          Context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    // 検索条件・ソートリセット（プロジェクト選択時）
    String resetCondition = rundata.getParameters().get("reset_condition");
    if (resetCondition != null && resetCondition.length() > 0) {
      ALEipUtils.setTemp(rundata, context, "target_keyword", "");
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "");
      ALEipUtils.setTemp(rundata, context, LIST_SORT_TYPE_STR, "");
    }

    allProject = ProjectUtils.getAllProject(); // 全プロジェクト
    setProject(rundata, context);

    super.init(action, rundata, context);

    // 年
    Calendar cal = Calendar.getInstance();
    viewDateMaxYear = cal.get(Calendar.YEAR) + 10;

    // メンバー
    projectMembers = ProjectUtils.getProjectMembers(selectedProjectId);

    // 基準日(From,To)
    calFrom = ProjectUtils.getViewCalendar(rundata, context, "base_date_from");
    calTo = ProjectUtils.getViewCalendar(rundata, context, "base_date_to");
    calFrom.set(Calendar.DAY_OF_MONTH, 1);
    calTo.set(Calendar.DAY_OF_MONTH, calTo.getActualMaximum(Calendar.DATE));
    base_date_from = new ALDateTimeField();
    base_date_to = new ALDateTimeField();
    base_date_from.setValue(calFrom.getTime());
    base_date_to.setValue(calTo.getTime());
    // イナズマ線表示
    progress_line_checked =
      ProjectUtils.getParameter(rundata, context, "progress_line_checked");
  }

  /**
   * 一覧データを取得します。 <BR>
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @return ResultList
   */
  @Override
  protected ResultList<EipTProjectTask> selectList(RunData rundata,
      Context context) {

    if (null == selectedProjectId) {
      return null;
    }

    // キーワード
    target_keyword =
      ProjectUtils.getParameter(rundata, context, "target_keyword");
    // 担当者
    target_user_id =
      ProjectUtils.getParameter(rundata, context, "target_user_id");
    // 分類
    target_tracker =
      ProjectUtils.getParameter(rundata, context, "target_tracker");
    // 優先度
    target_priority =
      ProjectUtils.getParameter(rundata, context, "target_priority");
    // ステータス
    target_status =
      ProjectUtils.getParameter(rundata, context, "target_status");
    // 進捗率FROM
    target_progress_rate_from =
      ProjectUtils.getParameter(rundata, context, "target_progress_rate_from");
    // 進捗率TO
    target_progress_rate_to =
      ProjectUtils.getParameter(rundata, context, "target_progress_rate_to");
    // 進捗遅れ
    target_delay = ProjectUtils.getParameter(rundata, context, "target_delay");

    /** for sql injection */
    {
      if (isRejectParameter(target_user_id)) {
        target_user_id = "0";
      }

      if (isRejectParameter(target_tracker)) {
        target_tracker = "0";
      }

      if (isRejectParameter(target_priority)) {
        target_priority = "0";
      }

      if (isRejectParameter(target_status)) {
        target_status = "0";
      }

      if (isRejectParameter(target_progress_rate_from)) {
        target_progress_rate_from = "0";
      }

      if (isRejectParameter(target_progress_rate_to)) {
        target_progress_rate_to = "0";
      }
    }

    // -------------------------
    // 共通SELECT句
    // -------------------------
    StringBuilder sl = new StringBuilder();
    sl.append(", task.task_id"); // タスクID
    sl.append(", task.task_name"); // タスク名
    sl.append(", task.parent_task_id");// 親タスクID
    sl.append(", task.project_id");// プロジェクトID
    sl.append(", task.tracker");// 分類
    sl.append(", task.explanation"); // 説明
    sl.append(", task.status"); // ステータス
    sl.append(", task.priority"); // 優先度
    sl.append(", task.start_plan_date"); // 開始予定日
    sl.append(", task.end_plan_date"); // 完了予定日
    sl.append(", task.start_date"); // 開始実績日
    sl.append(", task.end_date"); // 完了実績日
    sl.append(", task.plan_workload"); // 計画工数
    sl.append(", task.progress_rate"); // 進捗率
    sl.append(", task.update_date"); // 更新日

    if (target_delay != null && target_delay.equals(ProjectUtils.FLG_ON)) {
      /**
       * CURRENT_DATE in SQL depend on Database locale, so unify the time in
       * java
       */
      sl.append(", CASE");
      sl
        .append("    WHEN task.start_plan_date IS NULL OR task.end_plan_date IS NULL");
      sl.append("      THEN 0");
      sl
        .append("    WHEN ")
        .append(ProjectUtils.getCurrentDateWithCast())
        .append(" < task.end_plan_date");
      sl.append("      THEN");
      sl.append("        CASE WHEN ").append(
        ProjectUtils.getCurrentDateWithCast()).append(
        " - task.start_plan_date + 1 < 0");
      sl.append("          THEN 0");
      sl
        .append("          ELSE ")
        .append(ProjectUtils.getCurrentDateWithCast())
        .append(" - task.start_plan_date + 1");
      sl.append("        END");
      sl.append("      ELSE");
      sl.append("        task.end_plan_date - task.start_plan_date + 1");
      sl.append("  END AS lapsed_days"); // 基準日までのタスク経過日数
      sl
        .append(", CASE WHEN task.end_plan_date - task.start_plan_date + 1 < 0");
      sl.append("    THEN 0");
      sl.append("    ELSE task.end_plan_date - task.start_plan_date + 1");
      sl.append("  END AS task_days"); // タスク日数
    }

    StringBuilder sb = new StringBuilder();
    SQLTemplate<EipTProjectTask> sqltemp = null;
    SQLTemplate<EipTProjectTask> sqlCountTemp = null;

    if (Database.isJdbcMySQL()) {
      String tempTableName = "tree" + String.valueOf(new Date().getTime());
      String result = getMySQLFetchQuery(rundata, context, sl.toString());
      String countResult = getMySQLCountQuery(rundata, context, sl.toString());
      sqltemp =
        Database.sql(EipTProjectTask.class, result.replaceAll(
          "tree",
          tempTableName));
      sqlCountTemp =
        Database.sql(EipTProjectTask.class, countResult.replaceAll(
          "tree",
          tempTableName));
    } else {

      sqltemp =
        Database.sql(EipTProjectTask.class, getPostgresFetchQuery(
          rundata,
          context,
          sl.toString()));
      sqlCountTemp =
        Database.sql(EipTProjectTask.class, getPostgresCountQuery(
          rundata,
          context,
          sl.toString()));

      sqltemp.param("project_id", selectedProjectId);
      sqlCountTemp.param("project_id", selectedProjectId);

      // 分類
      if (target_keyword != null && target_keyword.trim().length() > 0) {
        sqltemp.param("target_keyword", "%" + target_keyword + "%");
        sqlCountTemp.param("target_keyword", "%" + target_keyword + "%");
      }
      // 担当者
      if (target_user_id != null && !target_user_id.equals("all")) {
        sqltemp.param("target_user_id", Integer.valueOf(target_user_id));
        sqlCountTemp.param("target_user_id", Integer.valueOf(target_user_id));
      }
      // 分類
      if (target_tracker != null && !target_tracker.equals("all")) {
        sqltemp.param("target_tracker", target_tracker);
        sqlCountTemp.param("target_tracker", target_tracker);
      }
      // 優先度
      if (target_priority != null && !target_priority.equals("all")) {
        sqltemp.param("target_priority", target_priority);
        sqlCountTemp.param("target_priority", target_priority);
      }
      // ステータス
      if (target_status != null && !target_status.equals("all")) {
        sqltemp.param("target_status", target_status);
        sqlCountTemp.param("target_status", target_status);
      }
      // 進捗率FROM
      if (target_progress_rate_from != null
        && !target_progress_rate_from.equals("0")) {
        sqltemp.param("target_progress_rate_from", Integer
          .valueOf(target_progress_rate_from));
        sqlCountTemp.param("target_progress_rate_from", Integer
          .valueOf(target_progress_rate_from));
      }
      // 進捗率TO
      if (target_progress_rate_to != null
        && !target_progress_rate_to.equals("100")) {
        sqltemp.param("target_progress_rate_to", Integer
          .valueOf(target_progress_rate_to));
        sqlCountTemp.param("target_progress_rate_to", Integer
          .valueOf(target_progress_rate_to));
      }
    }

    ResultList<EipTProjectTask> list = new ResultList<EipTProjectTask>();

    // 取得値を設定する
    List<DataRow> result = sqltemp.fetchListAsDataRow();
    for (int i = 0; i < result.size(); i++) {

      DataRow row = result.get(i);

      Object parentTaskId = row.get("parent_task_id");
      Object explanation = row.get("explanation");
      Object startPlanDate = row.get("start_plan_date");
      Object endPlanDate = row.get("end_plan_date");
      Object startDate = row.get("start_date");
      Object endDate = row.get("end_date");
      Object updateDate = row.get("update_date");

      EipTProjectTask task = new EipTProjectTask();
      // タスクID
      task.setTaskId(row.get("task_id").toString());
      // タスク名
      task.setTaskName(row.get("task_name").toString());
      // 親タスクID
      if (parentTaskId != null) {
        task.setParentTaskId(Integer.valueOf(parentTaskId.toString()));
      }
      // プロジェクトID
      task.setProjectId(Integer.valueOf(row.get("project_id").toString()));
      // 分類
      task.setTracker(row.get("tracker").toString());
      // 説明
      if (explanation != null) {
        task.setExplanation(explanation.toString());
      }
      // ステータス
      task.setStatus(row.get("status").toString());
      // 優先度
      task.setPriority(row.get("priority").toString());
      // 開始予定日
      if (startPlanDate == null) {
        task.setStartPlanDate(ProjectUtils.getEmptyDate());
      } else {
        task.setStartPlanDate((Date) startPlanDate);
      }
      // 完了予定日
      if (endPlanDate == null) {
        task.setEndPlanDate(ProjectUtils.getEmptyDate());
      } else {
        task.setEndPlanDate((Date) endPlanDate);
      }
      // 開始実績日
      if (startDate == null) {
        task.setStartDate(ProjectUtils.getEmptyDate());
      } else {
        task.setStartDate((Date) startDate);
      }
      // 完了日
      if (endDate == null) {
        task.setEndDate(ProjectUtils.getEmptyDate());
      } else {
        task.setEndDate((Date) endDate);
      }
      // 計画工数
      task.setPlanWorkload(new BigDecimal(row.get("plan_workload").toString()));
      // 進捗率
      task
        .setProgressRate(Integer.valueOf(row.get("progress_rate").toString()));
      // 更新日
      task.setUpdateDate((Date) updateDate);
      // タスク名表示インデント
      if (Database.isJdbcMySQL()) {
        String indent = row.get("indent").toString();
        String[] split = indent.split(",");
        if (split.length > 0) {
          task.setIndent(split.length - 1);
        } else {
          task.setIndent(0);
        }
      } else {
        task.setIndent(Integer.valueOf(row.get("indent").toString()));
      }

      list.add(task);
    }

    // 全体の件数
    int count = 0;
    List<DataRow> countResult = sqlCountTemp.fetchListAsDataRow();
    for (DataRow row : countResult) {
      Long tmp = (Long) row.get("count");
      count = tmp != null ? tmp.intValue() : 0;
    }

    // 件数をセットする．
    setPageParam(count);
    taskCount = count;

    // インデント表示フラグ
    if (Database.isJdbcMySQL()) {
      if (!getMySQLWhereList().isEmpty()) {
        indentFlg = false;
      }
    } else {
      if (!getPostgresWhereList().isEmpty()) {
        indentFlg = false;
      }
    }

    return list;
  }

  private String getMySQLFetchQuery(RunData rundata, Context context, String sl) {

    StringBuilder sb = new StringBuilder();

    sb.append("CALL WITH_EMULATOR(");
    sb.append("  \"tree\",");
    sb.append("  \"SELECT ");
    sb
      .append("    CONVERT(task.order_no, CHAR(255)) AS path, CONVERT(0, CHAR(255)) AS indent, CONVERT(LPAD(task.order_no,10,'0'), CHAR(255)) AS lpad_path");
    sb.append(sl);
    sb
      .append("    FROM eip_t_project_task AS task WHERE task.project_id = ")
      .append(selectedProjectId)
      .append(" AND task.parent_task_id IS NULL\",");
    sb.append("  \"SELECT ");
    sb
      .append("  concat(tree.path, ',', task.order_no) AS path, concat(path, ',', 1) AS indent, concat(tree.lpad_path, ',', LPAD(task.order_no,10,'0')) AS lpad_path");
    sb.append(sl);
    sb
      .append("    FROM eip_t_project_task AS task JOIN tree ON tree.task_id = task.parent_task_id\",");
    sb
      .append("  \"SELECT task_id, task_name, parent_task_id, project_id, tracker, explanation, status, priority, start_plan_date, end_plan_date, start_date, end_date, plan_workload, progress_rate, update_date, indent FROM tree");

    List<String> whereList = getMySQLWhereList();

    // WHERE句セット
    for (int i = 0; i < whereList.size(); i++) {
      if (i == 0) {
        sb.append(" WHERE ");
      } else {
        sb.append(" AND ");
      }
      sb.append(whereList.get(i));
    }
    sb.append(getOrderBy(rundata, context));
    sb.append(" LIMIT ").append(getRowsNum()).append(" OFFSET ").append(
      current_page - 1);

    sb.append("\",");
    sb.append("  \"0\",");
    sb.append("  \"\"");
    sb.append(");");

    return sb.toString();
  }

  private String getMySQLCountQuery(RunData rundata, Context context, String sl) {
    StringBuilder sb = new StringBuilder();

    sb.append("CALL WITH_EMULATOR(");
    sb.append("  \"tree\",");
    sb.append("  \"SELECT ");
    sb
      .append("    CONVERT(task.order_no, CHAR(255)) AS path, CONVERT(0, CHAR(255)) AS indent, CONVERT(LPAD(task.order_no,10,'0'), CHAR(255)) AS lpad_path");
    sb.append(sl);
    sb
      .append("    FROM eip_t_project_task AS task WHERE task.project_id = ")
      .append(selectedProjectId)
      .append(" AND task.parent_task_id IS NULL\",");
    sb.append("  \"SELECT ");
    sb
      .append("  concat(tree.path, ',', task.order_no) AS path, concat(path, ',', 1) AS indent, concat(tree.lpad_path, ',', LPAD(task.order_no,10,'0')) AS lpad_path");
    sb.append(sl);
    sb
      .append("    FROM eip_t_project_task AS task JOIN tree ON tree.task_id = task.parent_task_id\",");
    sb.append("  \"SELECT Count(task_id) AS count FROM tree");

    List<String> whereList = getMySQLWhereList();

    // WHERE句セット
    for (int i = 0; i < whereList.size(); i++) {
      if (i == 0) {
        sb.append(" WHERE ");
      } else {
        sb.append(" AND ");
      }
      sb.append(whereList.get(i));
    }
    sb.append(getOrderBy(rundata, context));

    sb.append("\",");
    sb.append("  \"0\",");
    sb.append("  \"\"");
    sb.append(");");

    return sb.toString();
  }

  private String getPostgresFetchQuery(RunData rundata, Context context,
      String sl) {

    StringBuilder sb = new StringBuilder();
    sb.append("WITH RECURSIVE tree(path) AS (");
    sb.append("  SELECT");
    sb.append("        ARRAY[task.order_no] AS path");
    sb.append("      , 0 AS indent");
    sb.append(sl);
    sb.append("    FROM");
    sb.append("      eip_t_project_task AS task");
    sb.append("    WHERE task.project_id = #bind($project_id)");
    sb.append("      AND task.parent_task_id IS NULL");
    sb.append("  UNION ALL ");
    sb.append("  SELECT");
    sb.append("        tree.path || ARRAY[task.order_no] AS path");
    sb.append("      , array_upper(path, 1) AS indent");
    sb.append(sl);
    sb.append("    FROM");
    sb.append("      eip_t_project_task AS task");
    sb.append("        JOIN tree ");
    sb.append("          ON tree.task_id = task.parent_task_id");
    sb.append(") ");
    sb.append("SELECT");
    sb.append("        #result('task_id')"); // タスクID
    sb.append("      , #result('task_name')"); // タスク名
    sb.append("      , #result('parent_task_id')");// 親タスクID
    sb.append("      , #result('project_id')");// プロジェクトID
    sb.append("      , #result('tracker')");// 分類
    sb.append("      , #result('explanation')"); // 説明
    sb.append("      , #result('status')"); // ステータス
    sb.append("      , #result('priority')"); // 優先度
    sb.append("      , #result('start_plan_date')"); // 開始予定日
    sb.append("      , #result('end_plan_date')"); // 完了予定日
    sb.append("      , #result('start_date')"); // 開始実績日
    sb.append("      , #result('end_date')"); // 完了実績日
    sb.append("      , #result('plan_workload')"); // 計画工数
    sb.append("      , #result('progress_rate' 'java.math.BigDecimal')"); // 進捗率
    sb.append("      , #result('update_date')"); // 更新日
    sb.append("      , #result('indent')"); // インデント
    sb.append("  FROM");
    sb.append("    tree ");

    List<String> whereList = getPostgresWhereList();
    // WHERE句セット
    for (int i = 0; i < whereList.size(); i++) {
      if (i == 0) {
        sb.append(" WHERE ");
      } else {
        sb.append(" AND ");
      }
      sb.append(whereList.get(i));
    }
    sb.append(getOrderBy(rundata, context));
    sb.append(" LIMIT ").append(getRowsNum()).append(" OFFSET ").append(
      current_page - 1);
    return sb.toString();
  }

  private String getPostgresCountQuery(RunData rundata, Context context,
      String sl) {
    StringBuilder sb = new StringBuilder();
    sb.append("WITH RECURSIVE tree(path) AS (");
    sb.append("  SELECT");
    sb.append("        ARRAY[task.order_no] AS path");
    sb.append("      , 0 AS indent");
    sb.append(sl);
    sb.append("    FROM");
    sb.append("      eip_t_project_task AS task");
    sb.append("    WHERE task.project_id = #bind($project_id)");
    sb.append("      AND task.parent_task_id IS NULL");
    sb.append("  UNION ALL ");
    sb.append("  SELECT");
    sb.append("        tree.path || ARRAY[task.order_no] AS path");
    sb.append("      , array_upper(path, 1) AS indent");
    sb.append(sl);
    sb.append("    FROM");
    sb.append("      eip_t_project_task AS task");
    sb.append("        JOIN tree ");
    sb.append("          ON tree.task_id = task.parent_task_id");
    sb.append(") ");
    sb.append("SELECT");
    sb.append("        COUNT(task_id) AS count");
    sb.append("  FROM");
    sb.append("    tree ");

    List<String> whereList = getPostgresWhereList();
    // WHERE句セット
    for (int i = 0; i < whereList.size(); i++) {
      if (i == 0) {
        sb.append(" WHERE ");
      } else {
        sb.append(" AND ");
      }
      sb.append(whereList.get(i));
    }
    sb.append(getOrderBy(rundata, context));

    return sb.toString();
  }

  private List<String> getMySQLWhereList() {
    List<String> whereList = new ArrayList<String>();
    // キーワード
    if (target_keyword != null && target_keyword.trim().length() > 0) {
      whereList.add(" tree.task_name LIKE "
        + ProjectUtils.getLikeEnclosed(ProjectUtils
          .getEscapedStringForMysql(target_keyword)));
    }
    // 担当者
    if (target_user_id != null && !target_user_id.equals("all")) {
      StringBuilder where = new StringBuilder();
      where.append(" EXISTS(");
      where.append("   SELECT 0");
      where.append("     FROM eip_t_project_task_member AS member");
      where.append("    WHERE member.task_id = tree.task_id");
      where.append("      AND member.user_id = ").append(target_user_id);
      where.append(" )");
      whereList.add(String.valueOf(where));
    }
    // 分類
    if (target_tracker != null && !target_tracker.equals("all")) {
      whereList.add(" tree.tracker = " + target_tracker);
    }
    // 優先度
    if (target_priority != null && !target_priority.equals("all")) {
      whereList.add(" tree.priority = " + target_priority);
    }
    // ステータス
    if (target_status != null && !target_status.equals("all")) {
      whereList.add(" tree.status = " + target_status);
    }
    // 進捗率FROM
    if (target_progress_rate_from != null
      && !target_progress_rate_from.equals("0")) {
      whereList.add(" tree.progress_rate >= " + target_progress_rate_from);
    }
    // 進捗率TO
    if (target_progress_rate_to != null
      && !target_progress_rate_to.equals("100")) {
      whereList.add(" tree.progress_rate <= " + target_progress_rate_to);
    }
    // 進捗遅れ
    if (target_delay != null && target_delay.equals(ProjectUtils.FLG_ON)) {
      whereList
        .add(" (tree.task_days <> 0 AND tree.lapsed_days * 100 / tree.task_days > tree.progress_rate)");
    }

    return whereList;
  }

  private List<String> getPostgresWhereList() {
    List<String> whereList = new ArrayList<String>();
    // キーワード
    if (target_keyword != null && target_keyword.trim().length() > 0) {
      whereList.add(" tree.task_name LIKE #bind($target_keyword)");
    }
    // 担当者
    if (target_user_id != null && !target_user_id.equals("all")) {
      StringBuilder where = new StringBuilder();
      where.append(" EXISTS(");
      where.append("   SELECT 0");
      where.append("     FROM eip_t_project_task_member AS member");
      where.append("    WHERE member.task_id = tree.task_id");
      where.append("      AND member.user_id = #bind($target_user_id)");
      where.append(" )");
      whereList.add(String.valueOf(where));
    }
    // 分類
    if (target_tracker != null && !target_tracker.equals("all")) {
      whereList.add(" tree.tracker = #bind($target_tracker)");
    }
    // 優先度
    if (target_priority != null && !target_priority.equals("all")) {
      whereList.add(" tree.priority = #bind($target_priority)");
    }
    // ステータス
    if (target_status != null && !target_status.equals("all")) {
      whereList.add(" tree.status = #bind($target_status)");
    }
    // 進捗率FROM
    if (target_progress_rate_from != null
      && !target_progress_rate_from.equals("0")) {
      whereList.add(" tree.progress_rate >= #bind($target_progress_rate_from)");
    }
    // 進捗率TO
    if (target_progress_rate_to != null
      && !target_progress_rate_to.equals("100")) {
      whereList.add(" tree.progress_rate <= #bind($target_progress_rate_to)");
    }
    // 進捗遅れ
    if (target_delay != null && target_delay.equals(ProjectUtils.FLG_ON)) {
      whereList
        .add(" (tree.task_days <> 0 AND tree.lapsed_days * 100 / tree.task_days > tree.progress_rate)");
    }
    return whereList;
  }

  /**
   * ソート句を構築します。
   * 
   * @param crt
   * @return
   */
  protected String getOrderBy(RunData rundata, Context context) {

    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    String sort_type = ALEipUtils.getTemp(rundata, context, LIST_SORT_TYPE_STR);
    String crt_key = null;

    Attributes map = getColumnMap();
    if (sort != null && sort.length() > 0) {
      crt_key = map.getValue(sort);
    }

    current_sort = sort;
    current_sort_type = sort_type;

    if (crt_key != null && crt_key.length() > 0) {

      // インデントを非表示
      indentFlg = false;

      if (sort_type != null
        && ALEipConstants.LIST_SORT_TYPE_DESC.equals(sort_type)) {
        return "  ORDER BY " + crt_key + " DESC";
      } else {
        return "  ORDER BY " + crt_key;
      }

    } else {
      if (Database.isJdbcMySQL()) {
        return "  ORDER BY lpad_path";
      } else {
        return "  ORDER BY path";
      }
    }
  }

  /**
   * 詳細データを取得します。 <BR>
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @return 汎用データベースオブジェクトモデル
   */
  @Override
  protected EipTProjectTask selectDetail(RunData rundata, Context context) {
    // オブジェクトモデルを取得
    return ProjectUtils.getEipTProjectTask(rundata, context);
  }

  /**
   * ResultDataを取得します。（一覧データ） <BR>
   * 
   * @param record
   *          レコード
   * @return ResultData
   */
  @Override
  protected Object getResultData(EipTProjectTask record) {
    ProjectTaskResultData data = ProjectUtils.getProjectTaskResultData(record);
    Integer taskId = (int) data.getTaskId().getValue();

    // 子タスク件数
    int cntChild = ProjectUtils.getCountChildrenTask(taskId);

    // 子タスクが2件以上あればtrue
    // 並び替えボタン表示用
    data.setHasChildren(cntChild >= 2);

    // 経過タスク日数
    int lapsedDays =
      ProjectUtils.getLapsedDays(ProjectUtils.toString(record
        .getStartPlanDate()), ProjectUtils.toString(Calendar
        .getInstance()
        .getTime()));
    // 予定タスク日数
    int taskDays =
      ProjectUtils.getLapsedDays(ProjectUtils.toString(record
        .getStartPlanDate()), ProjectUtils.toString(record.getEndPlanDate()));
    data.setPlanTerm(taskDays);

    if (lapsedDays > taskDays) {
      // 経過タスク日数≦タスク日数にする
      lapsedDays = taskDays;
    }

    // 予定進捗率
    data
      .setPlanProgressRate(ProjectUtils.getPlanWorkload(lapsedDays, taskDays));

    // 実績工数
    List<ProjectTaskMemberResultData> memberList = data.getMemberList();
    BigDecimal workload = BigDecimal.valueOf(0);
    workload.setScale(1);
    for (int i = 0; i < memberList.size(); i++) {
      ProjectTaskMemberResultData member = memberList.get(i);
      workload = workload.add(member.getWorkload());
    }
    data.setWorkload(workload);

    // 完了予測工数
    BigDecimal forecastWorkload = BigDecimal.valueOf(0);
    if (data.getProgressRate().getValue() != 0) {
      forecastWorkload =
        workload.multiply(BigDecimal.valueOf(100)).divide(
          BigDecimal.valueOf(data.getProgressRate().getValue()),
          2,
          BigDecimal.ROUND_HALF_UP);
    }
    data.setForecastWorkload(forecastWorkload);

    // インデント表示フラグ
    data.setIndentFlg(indentFlg);

    return data;
  }

  /**
   * ResultDataを取得します。（詳細データ） <BR>
   * 
   * @param record
   *          レコード
   * @return ResultData
   */
  @Override
  protected Object getResultDataDetail(EipTProjectTask record) {
    ProjectTaskResultData data = ProjectUtils.getProjectTaskResultData(record);
    int taskId = (int) data.getTaskId().getValue();
    // ファイルリスト
    List<EipTProjectTaskFile> list =
      pfile
        .getSelectQueryForFiles(EipTProjectTask.TASK_ID_PK_COLUMN, taskId)
        .fetchList();
    data.setAttachmentFiles(pfile.getFileList(list));
    // コメントリスト
    data.setCommentList(ProjectUtils.getProjectTaskCommentList("" + taskId));
    // パンくずリスト
    data.setTopicPath(ProjectUtils.getTaskTopicPath(taskId));
    return data;
  }

  /**
   * 項目情報を取得する
   * 
   * @return 項目情報
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("task_name", "task_name");
    map.putValue("progress_rate", "progress_rate");
    return map;
  }

  /**
   * プロジェクトの総数を取得する
   * 
   * @return プロジェクト総数
   */
  public int getTaskCount() {
    return taskCount;
  }

  /**
   * 全プロジェクトのリストを返す
   * 
   * @return 全プロジェクトのリスト
   */
  public List<ProjectResultData> getAllProject() {
    return allProject;
  }

  /**
   * 選択されているプロジェクトIDを取得する
   * 
   * @return プロジェクトID
   */
  public Integer getSelectedProjectId() {
    return selectedProjectId;
  }

  /**
   * プロジェクトメンバーリストを取得します。
   * 
   * @return プロジェクトメンバーリスト
   */
  public List<ALEipUser> getProjectMembers() {
    return projectMembers;
  }

  /**
   * 検索したキーワードを取得します。
   * 
   * @return 検索キーワード
   */
  public String getTargetKeyword() {
    return target_keyword;
  }

  /**
   * 検索した担当者を取得します。
   * 
   * @return 検索担当者
   */
  public String getTargetUserId() {
    return target_user_id;
  }

  /**
   * 検索した分類を取得します。
   * 
   * @return 検索分類
   */
  public String getTargetTracker() {
    return target_tracker;
  }

  /**
   * 検索した優先度を取得します。
   * 
   * @return 検索優先度
   */
  public String getTargetPriority() {
    return target_priority;
  }

  /**
   * 検索したステータスを取得します。
   * 
   * @return 検索ステータス
   */
  public String getTargetStatus() {
    return target_status;
  }

  /**
   * 検索した進捗率FROMを取得します。
   * 
   * @return 進捗率FROM
   */
  public String getTargetProgressRateFrom() {
    return target_progress_rate_from;
  }

  /**
   * 検索した進捗率TOを取得します。
   * 
   * @return 進捗率TO
   */
  public String getTargetProgressRateTo() {
    return target_progress_rate_to;
  }

  /**
   * 検索した進捗遅れフラグを取得します。
   * 
   * @return 進捗遅れフラグ
   */
  public String getTargetDelay() {
    return target_delay;
  }

  /**
   * イナズマ線表示フラグを取得します。
   * 
   * @return イナズマ線表示フラグ
   */
  public String getProgressLineChecked() {
    return progress_line_checked;
  }

  /**
   * 日付の年最大値を取得する。
   * 
   * @return 年最大値
   */
  public int getViewDateMaxYear() {
    return viewDateMaxYear;
  }

  /**
   * 基準日Fromを取得する。
   * 
   * @return 基準日From
   */
  public ALDateTimeField getBaseDateFrom() {
    return base_date_from;
  }

  /**
   * 基準日Toを取得する。
   * 
   * @return 基準日To
   */
  public ALDateTimeField getBaseDateTo() {
    return base_date_to;
  }

  /**
   * 基準日Fromの年を取得する。
   * 
   * @return 基準日Fromの年
   */
  public int getBaseDateFromYear() {
    return calFrom.get(Calendar.YEAR);
  }

  /**
   * 基準日Toの年を取得する。
   * 
   * @return 基準日Toの年
   */
  public int getBaseDateToYear() {
    return calTo.get(Calendar.YEAR);
  }

  /**
   * 加算日数を加えた日を取得する。
   * 
   * @return 加算後の日
   */
  public int getDay(int days) {
    Calendar cal = (Calendar) calFrom.clone();
    cal.add(Calendar.DAY_OF_MONTH, days - 1);
    return cal.get(Calendar.DAY_OF_MONTH);
  }

  /**
   * 加算日数を加えた日がシステム日付かチェックする。
   * 
   * @return TRUE:システム日付である
   */
  public boolean isToday(int days) {
    Calendar today = Calendar.getInstance();
    Calendar cal = (Calendar) calFrom.clone();
    cal.add(Calendar.DAY_OF_MONTH, days - 1);

    return today.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
      && today.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
      && today.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH);
  }

  /**
   * 表示年月の日数を取得する。
   * 
   * @return 日数
   */
  public int getDays() {
    return ProjectUtils.getLapsedDays(calFrom, calTo);
  }

  /**
   * 表示年月の日数を取得する。
   * 
   * @return 日数
   */
  public int getDays(Date dateTo) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(dateTo);
    return ProjectUtils.getLapsedDays(calFrom, cal);
  }

  /**
   * 年を取得する。
   * 
   * @return 週番号
   */
  public int getYear(int days) {
    Calendar cal = (Calendar) calFrom.clone();
    cal.add(Calendar.DAY_OF_MONTH, days - 1);
    return cal.get(Calendar.YEAR);
  }

  /**
   * 月を取得する。
   * 
   * @return 月
   */
  public int getMonth(int days) {
    Calendar cal = (Calendar) calFrom.clone();
    cal.add(Calendar.DAY_OF_MONTH, days - 1);
    return cal.get(Calendar.MONTH);
  }

  /**
   * 週を取得する。
   * 
   * @return 週番号
   */
  public int getWeek(int days) {
    Calendar cal = (Calendar) calFrom.clone();
    cal.add(Calendar.DAY_OF_MONTH, days - 1);
    return cal.get(Calendar.WEEK_OF_YEAR);
  }

  /**
   * 分類リストを返します。
   * 
   * @return 分類リスト
   */
  public Map<String, String> getTrackerMap() {
    return ProjectUtils.getKubunMap("tracker");
  }

  /**
   * ステータスリストを返します。
   * 
   * @return ステータスリスト
   */
  public Map<String, String> getStatusMap() {
    return ProjectUtils.getKubunMap("status");
  }

  /**
   * 優先度リストを返します。
   * 
   * @return 優先度リスト
   */
  public Map<String, String> getPriorityMap() {
    return ProjectUtils.getKubunMap("priority");
  }

  /**
   * 現在時刻の long 値を返します。
   * 
   * @return 現在時刻の long 値
   */
  public long getNowTime() {
    return Calendar.getInstance().getTimeInMillis();
  }

  /**
   * 加算日数を加えた日付が休日かをチェックする。
   * 
   * @return TRUE:加算後の日が休日
   */

  public boolean isHoliday(int days) {
    Calendar cal = (Calendar) calFrom.clone();
    cal.add(Calendar.DAY_OF_MONTH, days - 1);

    // 祝日かどうかを検証する．
    ALEipHolidaysManager holidaysManager = ALEipHolidaysManager.getInstance();
    return holidaysManager.isHoliday(cal.getTime()) != null;
  }

  /**
   * 加算日数を加えた日付が土曜日かをチェックする。
   * 
   * @return TRUE:加算後の日が土曜
   */

  public boolean isSaturday(int days) {
    Calendar cal = (Calendar) calFrom.clone();
    cal.add(Calendar.DAY_OF_MONTH, days - 1);
    return Calendar.SATURDAY == cal.get(Calendar.DAY_OF_WEEK);
  }

  /**
   * 加算日数を加えた日付が土曜日かをチェックする。
   * 
   * @return TRUE:加算後の日が日曜
   */

  public boolean isSunday(int days) {
    Calendar cal = (Calendar) calFrom.clone();
    cal.add(Calendar.DAY_OF_MONTH, days - 1);
    return Calendar.SUNDAY == cal.get(Calendar.DAY_OF_WEEK);
  }

  /**
   * ビュータイプを取得する
   * 
   * @return ビュータイプ
   */
  public String getViewtype() {
    return "project";
  }

  private boolean isRejectParameter(String numeric) {
    return StringUtils.isNotEmpty(numeric)
      && !"all".equals(numeric)
      && !StringUtils.isNumeric(numeric);
  }

  public void setFiltersFromPSML(VelocityPortlet portlet, Context context,
      RunData rundata) {
    ALEipUtils.setTemp(rundata, context, LIST_FILTER_STR, portlet
      .getPortletConfig()
      .getInitParameter("p12f-filters"));

    ALEipUtils.setTemp(rundata, context, LIST_FILTER_TYPE_STR, portlet
      .getPortletConfig()
      .getInitParameter("p12g-filtertypes"));
  }

  public boolean isProjectEmpty() {
    return allProject.isEmpty();
  }

  public boolean isTaskEmpty() {
    return null == getList() || getList().isEmpty();
  }

  public void setProject(RunData rundata, Context context) {
    // validate projectId and reset projectId

    String filter = rundata.getParameters().getString("filter", "");
    String filterType = rundata.getParameters().getString("filtertype", "");
    String sesFilter = ALEipUtils.getTemp(rundata, context, LIST_FILTER_STR);
    String sesFilterType =
      ALEipUtils.getTemp(rundata, context, LIST_FILTER_TYPE_STR);

    sesFilter = sesFilter == null ? "" : sesFilter;
    sesFilterType = sesFilterType == null ? "" : sesFilterType;

    if (filterType.isEmpty()) {
      filter = sesFilter;
      filterType = sesFilterType;
    }

    if (StringUtils.isEmpty(filter) || StringUtils.isEmpty(filterType)) {
      ProjectResultData data = allProject.get(0);
      if (null != data) {
        selectedProjectId = data.getProjectId().getValueWithInt();
      }
      return;
    }

    boolean existProject = false;
    for (ProjectResultData data : allProject) {
      if (filter.equals(data.getProjectId().toString())) {
        existProject = true;
        break;
      }
    }

    if (!existProject) {
      ProjectResultData data = allProject.get(0);
      if (null != data) {
        selectedProjectId = data.getProjectId().getValueWithInt();
      } else {
        selectedProjectId = null;
      }
    } else {
      selectedProjectId = Integer.valueOf(filter);
    }
  }

}

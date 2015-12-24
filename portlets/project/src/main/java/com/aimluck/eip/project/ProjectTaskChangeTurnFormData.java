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
package com.aimluck.eip.project;

import static com.aimluck.eip.util.ALLocalizationUtils.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTProjectTask;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.project.util.ProjectUtils;

/**
 *
 */
public class ProjectTaskChangeTurnFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ProjectTaskChangeTurnFormData.class.getName());

  /** タスクのリスト　ソート後 */
  private ALStringField positions;

  /** タスクID配列 */
  private String[] taskIds = null;

  /** タスクのリスト */
  private List<ProjectTaskResultData> taskList = null;

  /** タスクオブジェクトのリスト */
  private List<EipTProjectTask> rawProjectTaskList = null;

  /** プロジェクトID */
  private String projectId = null;

  /** 親タスクID */
  private String parentTaskId = null;

  /**
   * 初期化します。
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
    super.init(action, rundata, context);

    taskList = new ArrayList<ProjectTaskResultData>();
    rawProjectTaskList = new ArrayList<EipTProjectTask>();

    // プロジェクトID
    projectId = rundata.getParameters().getString("projectid");

    // 親タスクID
    parentTaskId = rundata.getParameters().getString("parent_task_id");
  }

  /**
   * 各フィールドを初期化します。
   * 
   * 
   */
  @Override
  public void initField() {
    // 項目定義のリスト
    positions = new ALStringField();
    positions.setFieldName(getl10n("PROJECT_TASK_LIST"));
    positions.setTrim(true);
  }

  /**
   * データに値を設定します。
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean res = true;
    try {
      res = super.setFormData(rundata, context, msgList);
      if (res) {
        if (positions.getValue() == null || positions.getValue().equals("")) {// 初期

          SelectQuery<EipTProjectTask> query =
            Database.query(EipTProjectTask.class);
          query.andQualifier(ExpressionFactory.matchExp(
            EipTProjectTask.PARENT_TASK_ID_PROPERTY,
            parentTaskId));
          query.andQualifier(ExpressionFactory.matchExp(
            EipTProjectTask.PROJECT_ID_PROPERTY,
            projectId));
          query.orderAscending(EipTProjectTask.ORDER_NO_PROPERTY);
          taskList = ProjectUtils.getProjectTaskResultList(query.fetchList());

        } else {// データ送信時
          StringTokenizer st = new StringTokenizer(positions.getValue(), ",");
          taskIds = new String[st.countTokens()];
          int count = 0;
          while (st.hasMoreTokens()) {
            taskIds[count] = st.nextToken();
            count++;
          }
          SelectQuery<EipTProjectTask> query =
            Database.query(EipTProjectTask.class);
          List<EipTProjectTask> list = query.fetchList();

          for (int i = 0; i < taskIds.length; i++) {
            EipTProjectTask task =
              getEipTProjectTaskFromTaskId(list, taskIds[i]);
            taskList.add(ProjectUtils.getProjectTaskResultData(task));
            rawProjectTaskList.add(task);
          }
        }
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return res;
  }

  /**
   * 各フィールドに対する制約条件を設定します。
   */
  @Override
  protected void setValidator() {
  }

  /**
   * フォームに入力されたデータの妥当性検証を行います。
   * 
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean validate(List<String> msgList) {
    return msgList.isEmpty();
  }

  /**
   * 『項目定義』を読み込みます。
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      return true;
    } catch (Exception e) {
      logger.error("Exception", e);
      return false;
    }
  }

  /**
   * 『項目定義』を追加します。
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 『タスク』を更新します。
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    boolean res = true;
    try {
      int newPosition = 1;
      for (EipTProjectTask task : rawProjectTaskList) {
        task.setOrderNo(newPosition);
        newPosition++;
      }
      Database.commit();
    } catch (Exception e) {
      Database.rollback();
      logger.error("Exception", e);
      res = false;
    }
    return res;
  }

  /**
   * 『タスク』を削除します。
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 指定したchar型文字が記号であるかを判断します。
   * 
   * @param ch
   *          文字
   * @return TRUE 成功 FALSE 失敗
   */
  protected boolean isSymbol(char ch) {
    byte[] chars;

    try {
      chars = (Character.valueOf(ch).toString()).getBytes("shift_jis");
    } catch (UnsupportedEncodingException ex) {
      return false;
    }

    if (chars == null
      || chars.length == 2
      || Character.isDigit(ch)
      || Character.isLetter(ch)) {
      return false;
    } else {
      return true;
    }

  }

  /**
   * 指定したタスクIDのオブジェクトを取得する．
   * 
   * @param taskList
   *          タスクリスト
   * @param taskId
   *          タスクID
   * @return タスクオブジェクト
   */
  private EipTProjectTask getEipTProjectTaskFromTaskId(
      List<EipTProjectTask> taskList, String taskId) {
    for (int i = 0; i < taskList.size(); i++) {
      EipTProjectTask task = taskList.get(i);
      if (task.getTaskId().toString().equals(taskId)) {
        return task;
      }
    }
    return null;
  }

  /**
   * タスク情報のリストを取得する．
   * 
   * @return タスク情報のリスト
   */
  public List<ProjectTaskResultData> getProjectTaskList() {
    return taskList;
  }

  /**
   * 親タスクIDを返す
   * 
   * @return 親タスクID
   */
  public String getProjectId() {
    return projectId;
  }

  /**
   * 親タスクIDを返す
   * 
   * @return 親タスクID
   */
  public String getParentTaskId() {
    return parentTaskId;
  }
}

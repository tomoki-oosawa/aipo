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

package com.aimluck.eip.project.util;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.DataRow;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.cayenne.om.portlet.EipTProject;
import com.aimluck.eip.cayenne.om.portlet.EipTProjectTask;
import com.aimluck.eip.cayenne.om.portlet.EipTProjectTaskMember;
import com.aimluck.eip.orm.Database;

/**
 * プロジェクト管理のユーティリティクラスです。
 * 
 */
public class ProjectFormUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ProjectFormUtils.class.getName());

  public static void updateParentTaskDelegate(Integer taskId) {
    updateParentTask(taskId);
    Database.commit();
  }

  /**
   * 子タスクの値から親タスクの値を再計算し更新する。
   * 
   * @param taskId
   *          タスクID
   * @return FALSE:失敗
   */
  private static boolean updateParentTask(Integer taskId) {

    if (taskId == null) {
      return false;
    }

    // オブジェクトモデルを取得
    EipTProjectTask task = ProjectUtils.getEipTProjectTask("" + taskId);
    if (task == null) {
      return false;
    }
    // 進捗情報取得
    List<DataRow> datarow = ProjectUtils.getChildTask(task.getTaskId());

    // 進捗情報設定
    if (datarow != null) {

      DataRow row = datarow.get(0);
      Object cnt = row.get("cnt");

      if (cnt != null && Integer.valueOf(cnt.toString()) > 0) {
        // 計算対象子タスクがある場合

        // 実績進捗率
        task.setProgressRate(Integer.valueOf(row.get("result_per").toString()));
        // 計画工数
        task
          .setPlanWorkload(new BigDecimal(row.get("plan_workload").toString()));
        // 開始予定日
        Object startPlanDate = row.get("start_plan_date");
        if (startPlanDate == null) {
          task.setStartPlanDate(ProjectUtils.getEmptyDate());
        } else {
          task.setStartPlanDate((Date) startPlanDate);
        }
        // 完了予定日
        Object endPlanDate = row.get("end_plan_date");
        if (endPlanDate == null) {
          task.setEndPlanDate(ProjectUtils.getEmptyDate());
        } else {
          task.setEndPlanDate((Date) endPlanDate);
        }
        // 開始実績日
        Object startDate = row.get("start_date");
        if (startDate == null) {
          task.setStartDate(ProjectUtils.getEmptyDate());
        } else {
          task.setStartDate((Date) startDate);
        }
        // 完了実績日
        Object endDate = row.get("end_date");
        if (endDate == null) {
          task.setEndDate(ProjectUtils.getEmptyDate());
        } else {
          task.setEndDate((Date) endDate);
        }

      } else {
        task.setStartPlanDate(ProjectUtils.getEmptyDate());
        task.setEndPlanDate(ProjectUtils.getEmptyDate());
        task.setStartDate(ProjectUtils.getEmptyDate());
        task.setEndDate(ProjectUtils.getEmptyDate());
        task.setPlanWorkload(BigDecimal.valueOf(0));
        task.setProgressRate(0);
      }
    }

    // 担当者情報を削除
    ProjectUtils.removeProjectTaskMember(task);

    // 担当者情報取得
    List<DataRow> memberRow = ProjectUtils.getChildTaskMember(taskId);

    // 担当者設定
    if (datarow != null) {
      for (int i = 0; i < memberRow.size(); i++) {
        DataRow member = memberRow.get(i);
        EipTProjectTaskMember data =
          Database.create(EipTProjectTaskMember.class);
        data.setEipTProjectTask(task);
        data.setUserId(Integer.valueOf(member.get("user_id").toString()));
        data.setWorkload(new BigDecimal(member.get("workload").toString()));
      }
    }

    Database.commit();
    // 親タスクを更新する
    return updateParentTask(task.getParentTaskId());
  }

  /**
   * プロジェクトを更新する
   * 
   * @param projectId
   *          プロジェクトID
   * @return TRUE:更新実行 FALSE:更新しない
   */
  public static void updateProject(Integer projectId, Integer loginUserId) {

    // オブジェクトモデルを取得
    EipTProject project = ProjectUtils.getEipTProject(projectId);
    if (project == null) {
      return;
    }

    if (ProjectUtils.FLG_OFF.equals(project.getProgressFlg())) {
      // 自動計算しない場合
      return;
    }

    project.setProgressRate(ProjectUtils.getProjectProgressRate(projectId));
    project.setUpdateUserId(loginUserId);
    project.setUpdateDate(Calendar.getInstance().getTime());

    Database.commit();
  }
}

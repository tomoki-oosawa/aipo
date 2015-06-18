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
package com.aimluck.eip.modules.actions.project;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.exp.ExpressionFactory;

import com.aimluck.eip.cayenne.om.portlet.EipTProject;
import com.aimluck.eip.cayenne.om.portlet.EipTProjectMember;
import com.aimluck.eip.cayenne.om.portlet.EipTProjectTask;
import com.aimluck.eip.cayenne.om.portlet.EipTProjectTaskMember;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.modules.actions.project.util.ProjectTestUtil;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.project.util.ProjectUtils;
import com.aimluck.eip.util.ALEipUtils;

public class CreateProjectData {

  final static int MASTER_USER_ID = 23;

  final static Map<Integer, List<Integer>> map =
    new HashMap<Integer, List<Integer>>();

  final static int PROJECT_NUM = 5;

  final static int PARENT_NUM = 100;

  /**
   * ユーザー数1000 プロジェクト数5 タスク数 1000(親100, 子300, 孫600) メンバーは等分配
   * 
   * @param args
   */
  public static void main(String[] args) {
    ProjectTestInitializer.initialize();
    memberInitialize();

    int projectNum = Database.query(EipTProject.class).getCount();
    if (projectNum < PROJECT_NUM) {
      for (int i = projectNum + 1; i <= PROJECT_NUM; i++) {
        createProject(i);
      }
    }

    List<EipTProject> projects =
      Database.query(EipTProject.class).limit(PROJECT_NUM).fetchList();

    for (int i = 0; i < projects.size(); i++) {
      EipTProject project = projects.get(i);
      createTask(i + 1, project);
    }

    System.out.println("CreateProjectData Success");
  }

  private static void memberInitialize() {
    List<ALEipUser> users = ALEipUtils.getUsers("LoginUser");
    if (users.isEmpty()) {
      return;
    }
    List<Integer> ilist = new ArrayList<Integer>();
    for (ALEipUser user : users) {
      ilist.add(user.getUserId().getValueWithInt());
    }
    List<List<Integer>> result =
      ProjectTestUtil
        .devide(ilist, (int) Math.ceil(ilist.size() / PROJECT_NUM));
    int i = 1;
    for (List<Integer> _ilist : result) {
      if (i >= 6) {
        List<Integer> _list = map.get(i % PROJECT_NUM);
        _list.addAll(_ilist);
      } else {

        if (!_ilist.contains(Integer.valueOf(MASTER_USER_ID))) {
          _ilist.add(MASTER_USER_ID);
        }
        map.put(i++, _ilist);
      }

    }
  }

  private static void createProject(int num) {
    String projectName = "プロジェクト" + String.valueOf(num);
    String desc = "プロジェクトの説明" + String.valueOf(num);
    String progressFlg = "f";
    int formProgressRate = 0;

    // ログインユーザーの取得
    TurbineUser tuser = Database.get(TurbineUser.class, MASTER_USER_ID);

    // 新規オブジェクトモデル
    EipTProject project = Database.create(EipTProject.class);
    // プロジェクト名
    project.setProjectName(projectName);
    // 説明
    project.setExplanation(desc);
    // 管理者ID
    project.setAdminUserId(MASTER_USER_ID);
    // 進捗率自動計算フラグ
    project.setProgressFlg(progressFlg);
    // 作成者
    project.setTurbineUser(tuser);
    // 更新者
    project.setUpdateUserId(tuser.getUserId());
    // 作成日
    project.setCreateDate(Calendar.getInstance().getTime());
    // 更新日
    project.setUpdateDate(Calendar.getInstance().getTime());

    // 進捗率
    int progressRate = formProgressRate;
    if (ProjectUtils.FLG_ON.equals(progressFlg)) {
      // 自動計算する場合、プロジェクト作成したてなので進捗0%
      progressRate = 0;
    }
    project.setProgressRate(progressRate);

    // プロジェクトを登録
    Database.commit();

    // -----------------------
    // メンバーの登録
    // -----------------------

    for (Integer id : map.get(num)) {
      EipTProjectMember projectMember =
        Database.create(EipTProjectMember.class);
      projectMember.setEipTProject(project);
      projectMember.setUserId(id);
    }

    // メンバーを登録
    Database.commit();

  }

  public static void createTask(int num, EipTProject project) {
    for (int i = 0; i < PARENT_NUM; i++) {
      int parentId = createParent(num, project);

      // int child1 = createChild(projectId, parentId);
      // int child2 = createChild(projectId, parentId);
      // int child3 = createChild(projectId, parentId);
      //
      // createGrandchild(projectId, child1);
      // createGrandchild(projectId, child1);
      //
      // createGrandchild(projectId, child2);
      // createGrandchild(projectId, child2);
      //
      // createGrandchild(projectId, child3);
      // createGrandchild(projectId, child3);
    }
  }

  public static int createParent(int num, EipTProject project) {

    Integer parentTaskId = null;

    // ログインユーザーの取得
    TurbineUser tuser = Database.get(TurbineUser.class, MASTER_USER_ID);

    // 新規オブジェクトモデル
    EipTProjectTask task = Database.create(EipTProjectTask.class);

    // 登録・更新共通の項目の設定
    setInsertUpdate(task, project);

    // 作成者
    task.setTurbineUser(tuser);

    // 作成日
    task.setCreateDate(Calendar.getInstance().getTime());

    // if (parentTaskId != null) {
    // // 親タスクID
    // task.setParentTaskId(Integer.valueOf(parentTaskId));
    // }

    // ソートNoを取得
    int orderNo = 1;
    SelectQuery<EipTProjectTask> query = Database.query(EipTProjectTask.class);
    query.andQualifier(ExpressionFactory.matchExp(
      EipTProjectTask.PARENT_TASK_ID_PROPERTY,
      parentTaskId));
    query.orderDesending(EipTProjectTask.ORDER_NO_PROPERTY);
    EipTProjectTask parentTask = query.fetchSingle();
    if (parentTask != null) {
      orderNo = parentTask.getOrderNo() + 1;
    }
    task.setOrderNo(orderNo);

    List<Integer> member = new ArrayList<Integer>(map.get(num));
    Collections.shuffle(member);

    // 担当者情報
    for (int i = 0; i < Math.ceil(member.size() / PARENT_NUM); i++) {
      EipTProjectTaskMember data = new EipTProjectTaskMember();
      data.setEipTProjectTask(task);
      data.setUserId(member.get(i));
      data.setWorkload(new BigDecimal(1.00));
    }

    // 添付ファイルを登録する。
    // pfile.insertAttachmentFiles(fileuploadList, folderName, uid, task,
    // msgList);

    // タスクを登録
    Database.commit();

    // プロジェクト情報を更新
    if (updateProject(project.getProjectId())) {
      Database.commit();
    }

    return task.getTaskId();
  }

  public static int createChild(int projectId, int parentId) {
    return 0;
  }

  public static void createGrandchild(int projectId, int childId) {

  }

  private static boolean setInsertUpdate(EipTProjectTask task,
      EipTProject project) {
    List<Integer> trackers = Arrays.asList(1, 2, 3);
    Collections.shuffle(trackers);
    String taskName = "タスク" + new Date().getTime();
    String desc = "タスク説明" + new Date().getTime();
    List<Integer> status = Arrays.asList(1, 2, 3, 4, 5, 6);
    Collections.shuffle(status);
    List<Integer> priority = Arrays.asList(1, 2, 3);
    Collections.shuffle(priority);

    // プロジェクトID
    task.setEipTProject(project);
    // 分類
    task.setTracker(String.valueOf(trackers.get(0)));
    // タスク名
    task.setTaskName(taskName);
    // 説明
    task.setExplanation(desc);
    // ステータス
    task.setStatus(String.valueOf(status.get(0)));
    // 優先度
    task.setPriority(String.valueOf(priority.get(0)));

    if (true) {
      // 子タスクがない場合のみ更新する

      // 開始予定日
      task.setStartPlanDate(new Date());
      // 完了予定日
      task.setEndPlanDate(new Date());
      // 開始実績日
      task.setStartDate(ProjectUtils.getEmptyDate());
      // 完了実績日
      task.setEndDate(ProjectUtils.getEmptyDate());
      // 計画工数
      task.setPlanWorkload(new BigDecimal(4.0));
      // 進捗率
      task.setProgressRate(0);
    }
    // 更新者
    task.setUpdateUserId(MASTER_USER_ID);
    // 更新日
    task.setUpdateDate(Calendar.getInstance().getTime());

    return true;
  }

  private static boolean updateProject(Integer projectId) {

    // オブジェクトモデルを取得
    EipTProject project = ProjectUtils.getEipTProject(projectId);
    if (project == null) {
      return false;
    }

    if (ProjectUtils.FLG_OFF.equals(project.getProgressFlg())) {
      // 自動計算しない場合
      return false;
    }

    project.setProgressRate(ProjectUtils.getProjectProgressRate(projectId));
    project.setUpdateUserId(MASTER_USER_ID);
    project.setUpdateDate(Calendar.getInstance().getTime());

    return true;
  }

}

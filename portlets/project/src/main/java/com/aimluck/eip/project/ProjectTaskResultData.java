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
package com.aimluck.eip.project;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.fileupload.beans.FileuploadBean;
import com.aimluck.eip.project.util.ProjectUtils;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タスクのResultDataです。 <BR>
 *
 */
public class ProjectTaskResultData implements ALData {

  /** タスク ID */
  protected ALNumberField task_id;

  /** 親タスク ID */
  protected ALNumberField parent_task_id;

  /** 親タスク名 */
  protected ALStringField parent_task_name;

  /** プロジェクト ID */
  protected ALNumberField project_id;

  /** 分類 */
  protected ALStringField tracker;

  /** タスク名 */
  protected ALStringField task_name;

  /** 説明 */
  protected ALStringField explanation;

  /** ステータス */
  protected ALStringField status;

  /** 優先度 */
  protected ALStringField priority;

  /** 開始予定日 */
  protected ALDateTimeField start_plan_date;

  /** 完了予定日 */
  protected ALDateTimeField end_plan_date;

  /** 予定期間 */
  protected ALNumberField plan_term;

  /** 開始実績日 */
  protected ALDateTimeField start_date;

  /** 完了実績日 */
  protected ALDateTimeField end_date;

  /** 計画工数 */
  protected BigDecimal plan_workload;

  /** 工数実績 */
  protected BigDecimal workload;

  /** 完了予測工数 */
  protected BigDecimal forecast_workload;

  /** 予定進捗率 */
  protected ALNumberField plan_progress_rate;

  /** 進捗率 */
  protected ALNumberField progress_rate;

  /** 担当者リスト */
  private List<ProjectTaskMemberResultData> memberList = null;

  /** ログインユーザーID */
  private ALNumberField login_user_id;

  /** 作成日 */
  protected ALDateTimeField create_date;

  /** 更新日 */
  protected ALDateTimeField update_date;

  /** 添付ファイルリスト */
  private List<FileuploadBean> attachmentFileList = null;

  /** コメントリスト */
  private List<ProjectTaskCommentResultData> commentList = null;

  /** 実績表示フラグ */
  private boolean resultDisplayFlg;

  /** 子タスク保持フラグ */
  private boolean hasChildren;

  /** フォーム用の子タスク保持フラグ */
  private boolean hasChildrenForForm;

  /** インデント表示フラグ */
  private boolean indentFlg;

  /** タスク名表示インデント */
  private ALNumberField indent;

  /** パンくずリスト */
  private ALStringField topicPath;

  /** 小数値項目フォーマット */
  private final DecimalFormat df = new DecimalFormat("#.0##");

  private boolean editable;

  /**
   * 初期設定を行います
   */
  @Override
  public void initField() {
    task_id = new ALNumberField();
    parent_task_id = new ALNumberField();
    parent_task_name = new ALStringField();
    project_id = new ALNumberField();
    tracker = new ALStringField();
    task_name = new ALStringField();
    explanation = new ALStringField();
    status = new ALStringField();
    priority = new ALStringField();
    start_plan_date = new ALDateTimeField();
    end_plan_date = new ALDateTimeField();
    plan_term = new ALNumberField();
    start_date = new ALDateTimeField();
    end_date = new ALDateTimeField();
    plan_workload = new BigDecimal(0);
    workload = new BigDecimal(0);
    forecast_workload = new BigDecimal(0);
    plan_progress_rate = new ALNumberField();
    progress_rate = new ALNumberField();
    memberList = new ArrayList<ProjectTaskMemberResultData>();
    login_user_id = new ALNumberField();
    create_date = new ALDateTimeField();
    update_date = new ALDateTimeField();
    attachmentFileList = new ArrayList<FileuploadBean>();
    commentList = new ArrayList<ProjectTaskCommentResultData>();
    indent = new ALNumberField();
    topicPath = new ALStringField();
    resultDisplayFlg = true;
    hasChildren = false;
    indentFlg = false;
    hasChildrenForForm = false;
    editable = false;
  }

  /**
   * タスクIDを取得する
   *
   * @return タスクID
   */
  public ALNumberField getTaskId() {
    return task_id;
  }

  /**
   * タスクIDを設定する
   *
   * @param i
   *          タスクトID
   */
  public void setTaskId(long i) {
    task_id.setValue(i);
  }

  /**
   * 親タスクIDを取得する
   *
   * @return 親タスクID
   */
  public ALNumberField getParentTaskId() {
    return parent_task_id;
  }

  /**
   * 親タスクIDを設定する
   *
   * @param i
   *          親タスクID
   */
  public void setParentTaskId(long i) {
    parent_task_id.setValue(i);
  }

  /**
   * 親タスク名を取得する
   *
   * @return 親タスク名
   */
  public String getParentTaskName() {
    return ALCommonUtils.replaceToAutoCR(parent_task_name.toString());
  }

  /**
   * 親タスク名を設定する
   *
   * @param string
   *          親タスク名
   */
  public void setParentTaskName(String string) {
    parent_task_name.setValue(string);
  }

  /**
   * プロジェクトIDを取得する
   *
   * @return プロジェクトID
   */
  public ALNumberField getProjectId() {
    return project_id;
  }

  /**
   * プロジェクトIDを設定する
   *
   * @param i
   *          プロジェクトID
   */
  public void setProjectId(long i) {
    project_id.setValue(i);
  }

  /**
   * 分類を取得する
   *
   * @return 分類
   */
  public String getTracker() {
    return ALCommonUtils.replaceToAutoCR(tracker.toString());
  }

  /**
   * 分類を設定する
   *
   * @param string
   *          分類
   */
  public void setTracker(String string) {
    tracker.setValue(string);
  }

  /**
   * タスク名を取得する
   *
   * @return タスク名
   */
  public ALStringField getTaskName() {
    return task_name;
  }

  public String getTaskNameHtml() {
    return ALCommonUtils.replaceToAutoCR(task_name.toString());
  }

  /**
   * タスク名を設定する
   *
   * @param string
   *          タスク名
   */
  public void setTaskName(String string) {
    task_name.setValue(string);
  }

  /**
   * 説明を取得する
   *
   * @return 説明
   */
  public String getExplanation() {
    return ALEipUtils.getMessageList(explanation.getValue());
  }

  /**
   * 説明を設定する
   *
   * @param string
   *          説明
   */
  public void setExplanation(String string) {
    explanation.setValue(string);
  }

  /**
   * ステータスを取得する
   *
   * @return ステータス
   */
  public String getStatus() {
    return ALCommonUtils.replaceToAutoCR(status.toString());
  }

  /**
   * ステータスを設定する
   *
   * @param string
   *          ステータス
   */
  public void setStatus(String string) {
    status.setValue(string);
  }

  /**
   * 優先度を取得する
   *
   * @return 優先度
   */
  public String getPriority() {
    return ALCommonUtils.replaceToAutoCR(priority.toString());
  }

  /**
   * 優先度を設定する
   *
   * @param string
   *          優先度
   */
  public void setPriority(String string) {
    priority.setValue(string);
  }

  /**
   * 開始予定日を取得する
   *
   * @return 開始予定日
   */
  public ALDateTimeField getStartPlanDate() {
    return start_plan_date;
  }

  /**
   * 開始予定日を設定する
   *
   * @param dt
   *          開始予定日
   */
  public void setStartPlanDate(Date dt) {
    start_plan_date.setValue(dt);
  }

  /**
   * 完了予定日を取得する
   *
   * @return 完了予定日
   */
  public ALDateTimeField getEndPlanDate() {
    return end_plan_date;
  }

  /**
   * 完了予定日をフォーマットしていで取得する
   *
   * @return 完了予定日
   */
  public String getEndPlanDateWithFormat(String format) {
    return end_plan_date.toString(format);
  }

  /**
   * 完了予定日を設定する
   *
   * @param dt
   *          完了予定日
   */
  public void setEndPlanDate(Date dt) {
    end_plan_date.setValue(dt);
  }

  /**
   * 予定期間を取得する
   *
   * @return 予定期間
   */
  public ALNumberField getPlanTerm() {
    return plan_term;
  }

  /**
   * 予定期間を設定する
   *
   * @param i
   *          予定期間
   */
  public void setPlanTerm(long i) {
    plan_term.setValue(i);
  }

  /**
   * 開始実績日を取得する
   *
   * @return 開始実績日
   */
  public ALDateTimeField getStartDate() {
    return start_date;
  }

  /**
   * 開始実績日を設定する
   *
   * @param dt
   *          開始実績日
   */
  public void setStartDate(Date dt) {
    start_date.setValue(dt);
  }

  /**
   * 完了実績日を取得する
   *
   * @return 完了実績日
   */
  public ALDateTimeField getEndDate() {
    return end_date;
  }

  /**
   * 完了実績日を設定する
   *
   * @param dt
   *          完了実績日
   */
  public void setEndDate(Date dt) {
    end_date.setValue(dt);
  }

  /**
   * 計画工数を取得する
   *
   * @return 計画工数
   */
  public BigDecimal getPlanWorkload() {
    return plan_workload;
  }

  /**
   * 計画工数を設定する
   *
   * @param i
   *          計画工数
   */
  public void setPlanWorkload(BigDecimal i) {
    plan_workload = i;
  }

  /**
   * 計画工数を取得する（表示用）
   *
   * @return 計画工数
   */
  public String getPlanWorkloadFormat() {
    return df.format(plan_workload);
  }

  /**
   * 実績工数を取得する
   *
   * @return 実績工数
   */
  public BigDecimal getWorkload() {
    return workload;
  }

  /**
   * 実績工数を設定する
   *
   * @param i
   *          実績工数
   */
  public void setWorkload(BigDecimal i) {
    workload = i;
  }

  /**
   * 実績工数を取得する（表示用）
   *
   * @return 実績工数
   */
  public String getWorkloadFormat() {
    return df.format(workload);
  }

  /**
   * 完了予測工数を取得する
   *
   * @return 完了予測工数
   */
  public BigDecimal getForecastWorkload() {
    return forecast_workload;
  }

  /**
   * 完了予測工数を設定する
   *
   * @param i
   *          完了予測工数
   */
  public void setForecastWorkload(BigDecimal i) {
    forecast_workload = i;
  }

  /**
   * 完了予測工数を取得する（表示用）
   *
   * @return 完了予測工数
   */
  public String getForecastWorkloadFormat() {
    return df.format(forecast_workload);
  }

  /**
   * 予定進捗率を取得する
   *
   * @return 予定進捗率
   */
  public ALNumberField getPlanProgressRate() {
    return plan_progress_rate;
  }

  /**
   * 予定進捗率を設定する
   *
   * @param i
   *          予定進捗率
   */
  public void setPlanProgressRate(long i) {
    plan_progress_rate.setValue(i);
  }

  /**
   * 進捗率を取得する
   *
   * @return 進捗率
   */
  public ALNumberField getProgressRate() {
    return progress_rate;
  }

  /**
   * 進捗率を設定する
   *
   * @param i
   *          進捗率
   */
  public void setProgressRate(long i) {
    progress_rate.setValue(i);
  }

  /**
   * 作成日を取得する
   *
   * @return 作成日
   */
  public ALDateTimeField getCreateDate() {
    return create_date;
  }

  /**
   * 作成日を設定する
   *
   * @param dt
   *          作成日
   */
  public void setCreateDate(Date dt) {
    create_date.setValue(dt);
  }

  /**
   * 更新日を取得する
   *
   * @return 更新日
   */
  public ALDateTimeField getUpdateDate() {
    return ALEipUtils.getFormattedTime(update_date);
  }

  /**
   * 更新日を設定する
   *
   * @param dt
   *          更新日
   */
  public void setUpdateDate(Date dt) {
    update_date.setValue(dt);
  }

  /**
   * 担当者リストを取得します。
   *
   * @return 担当者リスト
   */
  public List<ProjectTaskMemberResultData> getMemberList() {
    return memberList;
  }

  /**
   * 担当者リストを設定します。
   *
   * @param memberList
   *          担当者リスト
   */
  public void setMemberList(List<ProjectTaskMemberResultData> memberList) {
    this.memberList = memberList;
  }

  /**
   * 担当者リスト文字列を取得します。
   *
   * @return 担当者リスト
   */
  public String getMemberListString() {
    if (memberList == null) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < memberList.size(); i++) {
      ProjectTaskMemberResultData member = memberList.get(i);
      if (member == null) {
        continue;
      }

      if (i > 0) {
        sb.append("、");
      }
      sb.append(member.getUserName());
    }
    return String.valueOf(sb);
  }

  /**
   * ログインユーザーIDを取得します。
   *
   * @return ログインユーザーID
   */
  public ALNumberField getLoginUserId() {
    return login_user_id;
  }

  /**
   * ログインユーザーIDを設定する
   *
   * @param ログインユーザーID
   */
  public void setLoginUserId(long i) {
    login_user_id.setValue(i);
  }

  /**
   * 工数合計を取得します。
   *
   * @return 担当者リスト
   */
  public BigDecimal getWorkloadTotal() {
    BigDecimal w = BigDecimal.valueOf(0);
    if (memberList == null) {
      return w;
    }

    for (int i = 0; i < memberList.size(); i++) {
      ProjectTaskMemberResultData member = memberList.get(i);
      if (member == null) {
        continue;
      }
      w = w.add(member.getWorkload());
    }
    return w;
  }

  /**
   * 添付ファイルリストを取得します。
   *
   * @return list 添付ファイルリスト
   */
  public List<FileuploadBean> getAttachmentFileList() {
    return attachmentFileList;
  }

  /**
   * 添付ファイルリストを設定します。
   *
   * @param list
   *          添付ファイルリスト
   */
  public void setAttachmentFiles(List<FileuploadBean> list) {
    attachmentFileList = list;
  }

  /**
   * コメントリストを取得します。
   *
   * @return list コメントリスト
   */
  public List<ProjectTaskCommentResultData> getCommentList() {
    return commentList;
  }

  /**
   * コメントリストを設定します。
   *
   * @param list
   *          添付ファイルリスト
   */
  public void setCommentList(List<ProjectTaskCommentResultData> list) {
    commentList = list;
  }

  /**
   * 実績表示フラグを取得します。
   *
   * @return 実績表示フラグ
   */
  public boolean getResultDisplayFlg() {
    return resultDisplayFlg;
  }

  /**
   * 実績表示フラグを設定します。
   *
   * @param b
   *          実績表示フラグ
   */
  public void setResultDisplayFlg(boolean b) {
    resultDisplayFlg = b;
  }

  /**
   * 子タスク保持フラグを取得します。
   *
   * @return 子タスク保持フラグ
   */
  public boolean getHasChildren() {
    return hasChildren;
  }

  /**
   * 子タスク保持フラグを設定します。
   *
   * @param b
   *          子タスク保持フラグ
   */
  public void setHasChildren(boolean b) {
    hasChildren = b;
  }

  /**
   * 一覧条件絞込みフラグを取得します。
   *
   * @return 一覧条件絞込みフラグ
   */
  public boolean getConditionFlg() {
    return indentFlg;
  }

  /**
   * インデント表示フラグを設定します。
   *
   * @param b
   *          インデント表示フラグ
   */
  public void setIndentFlg(boolean b) {
    indentFlg = b;
  }

  /**
   * タスク名表示インデントを取得する
   *
   * @return インデント
   */
  public ALNumberField getIndent() {
    return indent;
  }

  /**
   * タスク名表示インデントを設定する
   *
   * @param i
   *          インデント
   */
  public void setIndent(long i) {
    indent.setValue(i);
  }

  /**
   * タスク名表示インデント文字列を取得する
   *
   * @return インデント
   */
  public String getIndentString() {
    if (!indentFlg) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < indent.getValue(); i++) {
      sb.append("&nbsp;&nbsp;");
    }
    if (sb.length() > 0) {
      sb.append("└&nbsp;");
    }
    return String.valueOf(sb);
  }

  /**
   * 進捗遅延であるかをチェックする
   *
   * @return TRUE:工数オーバー
   */
  public boolean isDelay() {
    if ("".equals(getPlanProgressRate().toString())
      || "".equals(getProgressRate().toString())) {
      return false;
    }
    return (getPlanProgressRate().getValue() > getProgressRate().getValue());
  }

  /**
   * 工数オーバーであるかをチェックする
   *
   * @return TRUE:工数オーバー
   */
  public boolean isWorkloadOver() {
    if ("".equals(getStartPlanDate().toString())
      || "".equals(getEndPlanDate().toString())) {
      return false;
    }
    return (getPlanWorkload().compareTo(getForecastWorkload()) < 0);
  }

  /**
   * パンくずリストを設定する
   *
   * @param i
   *          パンくずリスト
   */
  public void setTopicPath(String i) {
    topicPath.setValue(i);
  }

  /**
   * パンくずリストを取得する
   *
   * @return パンくずリスト
   */
  public ALStringField getTopicPath() {
    return topicPath;
  }

  /**
   * 未来開始予定であるかを取得する
   *
   * @return TRUE:未来開始予定タスクである
   */
  public boolean isNoStart() {
    Calendar today = Calendar.getInstance();

    Calendar cal = Calendar.getInstance();
    cal.setTime(start_plan_date.getValue());

    return today.get(Calendar.YEAR) < cal.get(Calendar.YEAR)
      || (today.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && today
        .get(Calendar.MONTH) < cal.get(Calendar.MONTH))
      || (today.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
        && today.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && today
        .get(Calendar.DAY_OF_MONTH) < cal.get(Calendar.DAY_OF_MONTH));
  }

  /**
   * 完了済み予定であるかを取得する
   *
   * @return TRUE:完了済み予定
   */
  public boolean isPastFinish() {
    Calendar today = Calendar.getInstance();

    Calendar cal = Calendar.getInstance();
    cal.setTime(end_plan_date.getValue());

    return today.get(Calendar.YEAR) > cal.get(Calendar.YEAR)
      || (today.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && today
        .get(Calendar.MONTH) > cal.get(Calendar.MONTH))
      || (today.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
        && today.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && today
        .get(Calendar.DAY_OF_MONTH) > cal.get(Calendar.DAY_OF_MONTH));
  }

  /**
   * 完了済であるかを取得する
   *
   * @return TRUE:ステータスが新規、進行中、フィードバックのもの以外を完了済とする
   */
  public boolean isFinishStatus() {
    for (String _status : ProjectUtils.completeStatus) {
      if (status.getValue().equals(_status)) {
        return true;
      }
    }
    return false;
  }

  // ---------------------------------------------------
  // 区分値取得
  // ---------------------------------------------------
  /**
   * 分類区分値を取得する
   *
   * @param kubun
   *          区分値
   * @return 区分値文字列
   */
  public String getTrackerString() {
    return ProjectUtils.getKubunValueString("tracker", tracker.getValue());
  }

  /**
   * ステータス区分値を取得する
   *
   * @param kubun
   *          区分値
   * @return 区分値文字列
   */
  public String getStatusString() {
    return ProjectUtils.getKubunValueString("status", status.getValue());
  }

  /**
   * 優先度区分値を取得する
   *
   * @param kubun
   *          区分値
   * @return 区分値文字列
   */
  public String getPriorityString() {
    return ProjectUtils.getKubunValueString("priority", priority.getValue());
  }

  /**
   * @return hasChildrenForForm
   */
  public boolean isHasChildrenForForm() {
    return hasChildrenForForm;
  }

  /**
   * @param hasChildrenForForm
   *          セットする hasChildrenForForm
   */
  public void setHasChildrenForForm(boolean hasChildrenForForm) {
    this.hasChildrenForForm = hasChildrenForForm;
  }

  public boolean isEditable() {
    return editable;
  }

  public void setEditable(boolean editable) {
    this.editable = editable;
  }

}

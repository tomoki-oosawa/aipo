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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.fileupload.beans.FileuploadBean;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * プロジェクトのResultDataです。 <BR>
 *
 */
public class ProjectResultData implements ALData {

  /** プロジェクト ID */
  protected ALNumberField project_id;

  /** プロジェクト名 */
  protected ALStringField project_name;

  /** 説明 */
  protected ALStringField explanation;

  /** 管理者 ID */
  protected ALNumberField admin_user_id;

  /** 管理者名 */
  private ALStringField admin_user_name;

  /** 進捗率入力フラグ */
  protected ALStringField progress_flg;

  /** 進捗率 */
  protected ALNumberField progress_rate;

  /** 予定進捗率 */
  protected ALNumberField plan_progress_rate;

  /** 社内参加者 */
  private List<ALEipUser> memberList = null;

  /** 添付ファイルリスト */
  private List<FileuploadBean> attachmentFileList = null;

  /** 作成者ID */
  private ALNumberField create_user_id;

  /** 作成者 */
  protected ALStringField create_user_name;

  /** 更新者ID */
  private ALNumberField update_user_id;

  /** 更新者 */
  protected ALStringField update_user_name;

  /** ログインユーザーID */
  private ALNumberField login_user_id;

  /** 作成日 */
  protected ALDateTimeField create_date;

  /** 更新日 */
  protected ALDateTimeField update_date;

  /** タスク更新日 */
  protected ALDateTimeField task_update_date;

  /** 計画工数 */
  protected BigDecimal plan_workload;

  /** 工数実績 */
  protected BigDecimal workload;

  /** 完了予測工数 */
  protected BigDecimal forecast_workload;

  /** 残タスク数 **/
  protected int remainder_task;

  /** 小数値項目フォーマット */
  private final DecimalFormat df = new DecimalFormat("#.##");

  /**
   * 初期設定を行います
   */
  @Override
  public void initField() {
    project_id = new ALNumberField();
    project_name = new ALStringField();
    explanation = new ALStringField();
    admin_user_id = new ALNumberField();
    admin_user_name = new ALStringField();
    progress_flg = new ALStringField();
    progress_rate = new ALNumberField();
    plan_progress_rate = new ALNumberField();
    memberList = new ArrayList<ALEipUser>();
    create_user_id = new ALNumberField();
    create_user_name = new ALStringField();
    update_user_id = new ALNumberField();
    update_user_name = new ALStringField();
    login_user_id = new ALNumberField();
    create_date = new ALDateTimeField("M月d日");
    update_date = new ALDateTimeField("M月d日");
    task_update_date = new ALDateTimeField();
    plan_workload = new BigDecimal(0);
    workload = new BigDecimal(0);
    remainder_task = 0;
    forecast_workload = new BigDecimal(0);
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
   * プロジェクト名を取得する
   *
   * @return プロジェクト名
   */
  public ALStringField getProjectName() {
    return project_name;
  }

  public String getProjectNameHtml() {
    return ALCommonUtils.replaceToAutoCR(project_name.toString());
  }

  /**
   * プロジェクト名を設定する
   *
   * @param string
   *          汎用データベース名
   */
  public void setProjectName(String string) {
    project_name.setValue(string);
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
   * 管理者IDを取得する
   *
   * @return 管理者ID
   */
  public ALNumberField getAdminUserId() {
    return admin_user_id;
  }

  /**
   * 管理者IDを設定する
   *
   * @param i
   *          管理者ID
   */
  public void setAdminUserId(long i) {
    admin_user_id.setValue(i);
  }

  /**
   * 進捗率入力フラグを取得する
   *
   * @return 進捗率入力フラグ
   */
  public ALStringField getProgressFlg() {
    return progress_flg;
  }

  /**
   * 進捗率入力フラグを設定する
   *
   * @param string
   *          進捗率入力フラグ
   */
  public void setProgressFlg(String string) {
    progress_flg.setValue(string);
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
   * 作成者IDを取得する
   *
   * @return create_user_id
   */
  public ALNumberField getCreateUserId() {
    return create_user_id;
  }

  /**
   * 作成者IDを設定する
   *
   * @param create_user_id
   */
  public void setCreateUserId(long i) {
    create_user_id.setValue(i);
  }

  /**
   * @return create_user_name
   */
  public ALStringField getCreateUserName() {
    return create_user_name;
  }

  /**
   * @param create_user_name
   *          セットする create_user_name
   */
  public void setCreateUserName(String string) {
    create_user_name.setValue(string);
  }

  /**
   * 更新者IDを取得する
   *
   * @return update_user_id
   */
  public ALNumberField getUpdateUserId() {
    return update_user_id;
  }

  /**
   * 更新者IDを設定する
   *
   * @param update_user_id
   */
  public void setUpdateUserId(long i) {
    update_user_id.setValue(i);
  }

  /**
   * @return update_user_name
   */
  public ALStringField getUpdateUserName() {
    return update_user_name;
  }

  /**
   * @param update_user_name
   *          セットする update_user_name
   */
  public void setUpdateUserName(String string) {
    update_user_name.setValue(string);
  }

  /**
   * ログインユーザーIDを取得する
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
    return update_date;
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
   * タスク更新日を取得する
   *
   * @return 更新日
   */
  public ALDateTimeField getTaskUpdateDate() {
    return ALEipUtils.getFormattedTime(task_update_date);
  }

  /**
   * タスク更新日を設定する
   *
   * @param dt
   *          更新日
   */
  public void setTaskUpdateDate(Date dt) {
    task_update_date.setValue(dt);
  }

  /**
   * 社内参加者を取得します。
   *
   * @return 社内参加者
   */
  public List<ALEipUser> getMemberList() {
    return memberList;
  }

  /**
   * 社内参加者を設定します。
   *
   * @param memberList
   *          社内参加者
   */
  public void setMemberList(List<ALEipUser> memberList) {
    this.memberList = memberList;
  }

  /**
   * アップロードファイルリストを取得します。
   *
   * @return アップロードファイルリスト
   */
  public List<FileuploadBean> getAttachmentFileList() {
    return attachmentFileList;
  }

  /**
   * アップロードファイルリストを設定します。
   *
   * @param attachmentFileList
   *          アップロードファイルリスト
   */
  public void setAttachmentFileList(List<FileuploadBean> attachmentFileList) {
    this.attachmentFileList = attachmentFileList;
  }

  /**
   * 管理者名を取得します。
   *
   * @return 管理者名
   */
  public ALStringField getAdminUserName() {
    return admin_user_name;
  }

  /**
   * 管理者名を設定します。
   *
   * @param string
   *          管理者名
   */
  public void setAdminUserName(String string) {
    admin_user_name.setValue(string);
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
   * 残タスク数を設定する
   *
   * @param i
   *          残タスク数
   */
  public void setReminderTask(int i) {
    remainder_task = i;
  }

  /**
   * 残タスク数を取得する（表示用）
   *
   * @return 残タスク数
   */
  public int getReminderTask() {
    return remainder_task;
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
    return (getPlanWorkload().compareTo(getForecastWorkload()) < 0);
  }

}

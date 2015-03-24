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
package com.aimluck.eip.workflow;

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
import com.aimluck.eip.util.ALLocalizationUtils;
import com.aimluck.eip.workflow.util.WorkflowUtils;

/**
 * ワークフローのResultDataです。 <BR>
 *
 */
public class WorkflowResultData implements ALData {

  /** Request ID */
  protected ALNumberField request_id;

  /** Request名 */
  protected ALStringField request_name;

  /** カテゴリID */
  protected ALNumberField category_id;

  /** カテゴリ名 */
  protected ALStringField category_name;

  /** 重要度 */
  protected ALNumberField priority;

  /** 重要度画像名 */
  protected ALStringField priority_image;

  /** 重要度（文字列） */
  protected ALStringField priority_string;

  /** 進捗（文字列） */
  protected ALStringField state_string;

  /** 状態 */
  protected ALStringField progress;

  /** 金額 */
  protected ALNumberField price;

  /** 作成日 */
  protected ALStringField create_date;

  /** 作成日 */
  protected ALDateTimeField createYear;

  /** 作成日 */
  protected ALDateTimeField createDateYear;

  /** 作成日 */
  protected ALDateTimeField createDate;

  /** 作成日 */
  protected ALDateTimeField createDateTime;

  /** 最終閲覧者 */
  protected ALEipUser last_update_user;

  /** 申請者 */
  protected ALEipUser client_user;

  /** 添付ファイルリスト */
  private List<FileuploadBean> attachmentFileList = null;

  /** 更新日時 */
  private ALDateTimeField updateDate;

  /** 更新日時 */
  private ALDateTimeField updateDateTime;

  /** あなた宛のお知らせのID */
  protected ALNumberField activityId;

  /**
   *
   *
   */
  @Override
  public void initField() {
    request_id = new ALNumberField();
    request_name = new ALStringField();
    category_id = new ALNumberField();
    category_name = new ALStringField();
    priority = new ALNumberField();
    priority_image = new ALStringField();
    priority_string = new ALStringField();
    state_string = new ALStringField();
    progress = new ALStringField();
    price = new ALNumberField();
    create_date = new ALStringField();

    last_update_user = new ALEipUser();
    client_user = new ALEipUser();
    attachmentFileList = new ArrayList<FileuploadBean>();

    updateDate =
      new ALDateTimeField(ALLocalizationUtils.getl10n("WORKFLOW_MONTH_DAY"));
    updateDateTime = new ALDateTimeField("H:mm");

    createYear =
      new ALDateTimeField(ALLocalizationUtils.getl10n("WORKFLOW_YEAR"));
    createDateYear =
      new ALDateTimeField(ALLocalizationUtils
        .getl10n("WORKFLOW_YEAR_MONTH_DAY"));
    createDate =
      new ALDateTimeField(ALLocalizationUtils.getl10n("WORKFLOW_MONTH_DAY"));
    createDateTime = new ALDateTimeField("H:mm");

    activityId = new ALNumberField();
  }

  /**
   * @return
   */
  public ALNumberField getRequestId() {
    return request_id;
  }

  /**
   * @return
   */
  public String getRequestName() {
    return ALCommonUtils.replaceToAutoCR(request_name.toString());
  }

  /**
   * @return
   */
  public ALNumberField getCategoryId() {
    return category_id;
  }

  /**
   * @return
   */
  public String getCategoryName() {
    return ALCommonUtils.replaceToAutoCR(category_name.toString());
  }

  /**
   * @return
   */
  public ALStringField getStateString() {
    return state_string;
  }

  /**
   * @return
   */
  public ALStringField getPriorityString() {
    return priority_string;
  }

  /**
   * @return
   */
  public ALStringField getPriorityImage() {
    return priority_image;
  }

  /**
   * @return
   */
  public ALStringField getProgress() {
    return progress;
  }

  /**
   * @return
   */
  public ALNumberField getPrice() {
    return price;
  }

  public String getPriceStr() {
    return WorkflowUtils.translateMoneyStr(price.toString());
  }

  /**
   * @param i
   */
  public void setRequestId(long i) {
    request_id.setValue(i);
  }

  /**
   * @param string
   */
  public void setRequestName(String string) {
    request_name.setValue(string);
  }

  /**
   * @param i
   */
  public void setCategoryId(long i) {
    category_id.setValue(i);
  }

  /**
   * @param i
   */
  public void setCategoryName(String string) {
    category_name.setValue(string);
  }

  /**
   * @param i
   */
  public void setPriority(int i) {
    priority.setValue(i);
  }

  /**
   * @param string
   */
  public void setPriorityImage(String string) {
    priority_image.setValue(string);
  }

  /**
   * @param string
   */
  public void setPriorityString(String string) {
    priority_string.setValue(string);
  }

  /**
   * @param string
   */
  public void setStateString(String string) {
    state_string.setValue(string);
  }

  /**
   * @param string
   */
  public void setProgress(String string) {
    progress.setValue(string);
  }

  /**
   * @param string
   */
  public void setPrice(long i) {
    price.setValue(i);
  }

  /**
   * @return
   */
  public ALStringField getCreateDate() {
    return create_date;
  }

  /**
   * @param string
   */
  public void setCreateDate(String string) {
    create_date.setValue(string);
  }

  /**
   * @return list
   */
  public List<FileuploadBean> getAttachmentFileList() {
    return attachmentFileList;
  }

  /**
   * @param list
   */
  public void setAttachmentFiles(List<FileuploadBean> list) {
    attachmentFileList = list;
  }

  /**
   * @return
   */
  public ALNumberField getActivityId() {
    return activityId;
  }

  /**
   * @param string
   */
  public void setActivityId(int string) {
    activityId.setValue(string);
  }

  /**
   * @param field
   */
  public void setUpdateDateTime(Date date) {
    if (date == null) {
      return;
    }
    this.updateDate.setValue(date);
    this.updateDateTime.setValue(date);
  }

  public ALDateTimeField getUpdateDateTime() {
    ALDateTimeField today =
      new ALDateTimeField(ALLocalizationUtils.getl10n("WORKFLOW_MONTH_DAY"));
    today.setValue(new Date());
    if (updateDate.toString().equals(today.toString())) {
      return updateDateTime;
    } else {
      return updateDate;
    }
  }

  public void setCreateDateTime(Date date) {
    if (date == null) {
      return;
    }
    this.createYear.setValue(date);
    this.createDateYear.setValue(date);
    this.createDate.setValue(date);
    this.createDateTime.setValue(date);
  }

  public ALDateTimeField getCreateDateTime() {
    ALDateTimeField today =
      new ALDateTimeField(ALLocalizationUtils.getl10n("WORKFLOW_MONTH_DAY"));
    ALDateTimeField thisYear =
      new ALDateTimeField(ALLocalizationUtils.getl10n("WORKFLOW_YEAR"));
    today.setValue(new Date());
    thisYear.setValue(new Date());
    if (createDate.toString().equals(today.toString())
      && createYear.toString().equals(thisYear.toString())) {
      return createDateTime;
    } else if (!createDate.toString().equals(today.toString())
      && createYear.toString().equals(thisYear.toString())) {
      return createDate;
    } else {
      return createDateYear;
    }
  }

  /**
   * @return last_update_user
   */
  public ALEipUser getLastUpdateUser() {
    return last_update_user;
  }

  /**
   * @param last_update_user
   *          セットする last_update_user
   */
  public void setLastUpdateUser(ALEipUser last_update_user) {
    this.last_update_user = last_update_user;
  }

  /**
   * @return client_user
   */
  public ALEipUser getClientUser() {
    return client_user;
  }

  /**
   * @param client_user
   *          セットする client_user
   */
  public void setClientUser(ALEipUser client_user) {
    this.client_user = client_user;
  }

}

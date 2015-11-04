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
package com.aimluck.eip.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.fileupload.beans.FileuploadBean;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 報告書のResultDataです。 <BR>
 *
 */
public class ReportResultData implements ALData {

  /** Report ID */
  protected ALNumberField report_id;

  /** 報告書名 */
  protected ALStringField report_name;

  /** 親 報告書 ID */
  private ALNumberField parent_id;

  /** 開始時間 */
  private ALDateTimeField start_date;

  /** 開始時間 */
  private ALDateTimeField end_date;

  /** 作成日 */
  private ALDateTimeField create_date;

  /** 作成日 */
  protected ALDateTimeField createDate;

  /** 添付ファイルリスト */
  private List<FileuploadBean> attachmentFileList = null;

  /** 更新日時 */
  private ALDateTimeField updateDate;

  /** 登録者Id */
  protected ALNumberField client_id;

  /** 登録者名 */
  protected ALStringField client_name;

  /** 社内参加者 */
  private List<ALEipUser> memberList = null;

  /** 通知先 */
  private List<ALEipUser> mapList = null;

  private boolean is_self_report;

  /** <code>statusList</code> メンバーの状態 */
  private Map<Integer, String> statusList;

  /** 送信した報告書数 */
  private ALNumberField sent_report;

  /** 既読報告書数 */
  private ALNumberField read_report;

  /**
   *
   *
   */
  @Override
  public void initField() {
    report_id = new ALNumberField();
    report_name = new ALStringField();
    start_date = new ALDateTimeField();
    end_date = new ALDateTimeField();
    create_date = new ALDateTimeField();
    parent_id = new ALNumberField();

    attachmentFileList = new ArrayList<FileuploadBean>();

    updateDate = new ALDateTimeField();
    createDate = new ALDateTimeField();
    client_name = new ALStringField();
    client_id = new ALNumberField();

    memberList = new ArrayList<ALEipUser>();
    mapList = new ArrayList<ALEipUser>();
    is_self_report = false;
    statusList = new HashMap<Integer, String>();
    sent_report = new ALNumberField();
    read_report = new ALNumberField();
  }

  /**
   * @return
   */
  public ALNumberField getReportId() {
    return report_id;
  }

  /**
   * @return
   */
  public String getReportName() {
    return ALCommonUtils.replaceToAutoCR(report_name.toString());
  }

  /**
   * @param i
   */
  public void setReportId(long i) {
    report_id.setValue(i);
  }

  /**
   * @param string
   */
  public void setReportName(String string) {
    report_name.setValue(string);
  }

  /**
   * @param i
   */
  public void setParentId(long i) {
    parent_id.setValue(i);
  }

  /**
   * @param i
   */
  public ALNumberField getParentId() {
    return parent_id;
  }

  /**
   * @return
   */
  public ALDateTimeField getCreateDate() {
    return ALEipUtils.getFormattedTime(create_date);
  }

  /**
   * @return
   */
  public ALStringField getClientName() {
    return client_name;
  }

  /**
   * @param string
   */
  public void setClientName(String string) {
    client_name.setValue(string);
  }

  /**
   * @return
   */
  public ALNumberField getClientId() {
    return client_id;
  }

  /**
   * @param i
   */
  public void setClientId(long i) {
    client_id.setValue(i);
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
   * @return list
   */
  public List<ALEipUser> getMemberList() {
    return memberList;
  }

  /**
   * @param list
   */
  public void setMemberList(List<ALEipUser> list) {
    memberList = list;
  }

  /**
   * @return list
   */
  public List<ALEipUser> getMapList() {
    return mapList;
  }

  /**
   * @param list
   */
  public void setMapList(List<ALEipUser> list) {
    mapList = list;
  }

  public ALDateTimeField getUpdateDateTime() {
    return ALEipUtils.getFormattedTime(updateDate);
  }

  public void setCreateDate(Date date) {
    if (date == null) {
      return;
    }
    this.createDate.setValue(date);
  }

  public ALDateTimeField getCreateDateTime() {
    return ALEipUtils.getFormattedTime(createDate);
  }

  public boolean isSelfReport() {
    return is_self_report;
  }

  public void setIsSelfReport(boolean is_self_report) {
    this.is_self_report = is_self_report;
  }

  /**
   *
   * @param date
   */
  public void setStartDate(Date date) {
    start_date.setValue(date);
  }

  /**
   *
   * @return
   */
  public ALDateTimeField getStartDate() {
    return start_date;
  }

  /**
   *
   * @param date
   */
  public void setEndDate(Date date) {
    end_date.setValue(date);
  }

  /**
   *
   * @return
   */
  public ALDateTimeField getEndDate() {
    return end_date;
  }

  /**
   * @param list
   */
  public void setStatusList(HashMap<Integer, String> map) {
    statusList = map;
  }

  public String getStatus(long id) {
    return statusList.get(Integer.valueOf((int) id));
  }

  public ALNumberField getSentReport() {
    return sent_report;
  }

  public void setSentReport(long value) {
    sent_report.setValue(value);
  }

  public ALNumberField getReadReport() {
    return read_report;
  }

  public void setReadReport(long value) {
    read_report.setValue(value);
  }
}

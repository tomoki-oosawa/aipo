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
import java.util.List;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ワークフローのResultDataです。 <BR>
 * 
 */
public class WorkflowDetailResultData extends WorkflowResultData {

  /** Parent ID */
  private ALNumberField parent_id;

  /** ユーザーID */
  private ALNumberField user_id;

  /** メモ */
  private ALStringField note;

  /** 更新日 */
  private ALStringField update_date;

  /** 決裁情報 */
  private List<WorkflowDecisionRecordData> drlist;

  /** 差し戻し先一覧 */
  private List<WorkflowDecisionRecordData> remandlist;

  /** 申請者に差し戻せるかどうか */
  private boolean can_remand_applicant;

  /** 過去の申請内容のリンク一覧 */
  private List<WorkflowOldRequestResultData> oldrequestLinks;

  /** 申請経路名 */
  private ALStringField route_name;

  /**
   *
   *
   */
  @Override
  public void initField() {
    super.initField();
    parent_id = new ALNumberField();
    user_id = new ALNumberField();
    note = new ALStringField();
    note.setTrim(false);
    update_date = new ALStringField();
    drlist = new ArrayList<WorkflowDecisionRecordData>();
    remandlist = new ArrayList<WorkflowDecisionRecordData>();
    oldrequestLinks = new ArrayList<WorkflowOldRequestResultData>();
    route_name = new ALStringField();
  }

  /**
   * @return
   */
  public ALNumberField getParentId() {
    return parent_id;
  }

  /**
   * @return
   */
  public ALNumberField getUserId() {
    return user_id;
  }

  /**
   * @return
   */
  public String getNote() {
    return ALEipUtils.getMessageList(note.getValue());
  }

  public List<WorkflowDecisionRecordData> getDecisionRecords() {
    return drlist;
  }

  public List<WorkflowDecisionRecordData> getRemandingRecords() {
    return remandlist;
  }

  public List<WorkflowOldRequestResultData> getOldRequestLinks() {
    return oldrequestLinks;
  }

  public boolean getCanRemandApplicant() {
    return can_remand_applicant;
  }

  /**
   * @param string
   */
  public ALStringField getRouteName() {
    return route_name;
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
  public void setUserId(long i) {
    user_id.setValue(i);
  }

  /**
   * @param string
   */
  public void setNote(String string) {
    note.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getUpdateDate() {
    return update_date;
  }

  /**
   * @param string
   */
  public void setUpdateDate(String string) {
    update_date.setValue(string);
  }

  public void setDecisionRecords(List<WorkflowDecisionRecordData> list) {
    drlist.addAll(list);
  }

  public void setRemandingRecords(List<WorkflowDecisionRecordData> list) {
    remandlist.addAll(list);
  }

  public void setCanRemandApplicant(boolean b) {
    can_remand_applicant = b;
  }

  public void setOldRequestLinks(List<WorkflowOldRequestResultData> links) {
    oldrequestLinks.addAll(links);
  }

  /**
   * @param string
   */
  public void setRouteName(String string) {
    route_name.setValue(string);
  }

  public boolean getHasRootName() {
    boolean bool;

    if (route_name.getValue() == null || "".equals(route_name.getValue())) {
      bool = false;
    } else {
      bool = true;
    }

    return bool;
  }

}

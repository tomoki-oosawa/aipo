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

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ワークフローのResultDataです。 <BR>
 * 
 */
public class WorkflowDecisionRecordData implements ALData {

  /** ユーザーID */
  private ALNumberField user_id;

  /** ユーザー名 */
  private ALStringField user_alias_name;

  /** 決裁状況 */
  private ALStringField status;

  /** 決裁状況（文字列） */
  private ALStringField status_string;

  /** 順番 */
  private ALNumberField order;

  /** コメント */
  private ALStringField note;

  /** 更新日 */
  private ALStringField update_date;

  /**
   * 
   * 
   */
  public void initField() {
    user_id = new ALNumberField();
    user_alias_name = new ALStringField();
    status = new ALStringField();
    status_string = new ALStringField();
    order = new ALNumberField();
    note = new ALStringField();
    note.setTrim(false);
    update_date = new ALStringField();
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
  public ALStringField getUserAliasName() {
    return user_alias_name;
  }

  /**
   * @return
   */
  public ALStringField getStatus() {
    return status;
  }

  /**
   * @return
   */
  public ALStringField getStatusString() {
    return status_string;
  }

  /**
   * @return
   */
  public ALNumberField getOrder() {
    return order;
  }

  /**
   * @return
   */
  public String getNote() {
    return ALEipUtils.getMessageList(note.getValue());
  }

  /**
   * @return
   */
  public String getNoteR() {
    if (note.getValue() == null) {
      return "";
    }
    return note.getValue();
  }

  /**
   * @return
   */
  public ALStringField getUpdateDate() {
    return update_date;
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
  public void setUserAliasName(String string) {
    user_alias_name.setValue(string);
  }

  /**
   * @param string
   */
  public void setStatus(String string) {
    status.setValue(string);
  }

  /**
   * @param string
   */
  public void setStatusString(String string) {
    status_string.setValue(string);
  }

  /**
   * @param string
   */
  public void setOrder(int i) {
    order.setValue(i);
  }

  /**
   * @param string
   */
  public void setNote(String string) {
    note.setValue(string);
  }

  /**
   * @param string
   */
  public void setUpdateDate(String string) {
    update_date.setValue(string);
  }

}

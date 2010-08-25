/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.schedule;

import java.util.List;

import com.aimluck.commons.field.ALNumberField;

/**
 * カレンダー用スケジュールのResultDataです。 <br />
 * 
 */
public class AjaxScheduleResultData extends ScheduleResultData {

  private List<String> memberlist;

  /** <code>parent_id</code> スケジュールowner ID */
  private ALNumberField user_id;

  @Override
  public void initField() {
    user_id = new ALNumberField();
    super.initField();
  }

  public void setUserId(int index) {
    this.user_id.setValue(index);
  }

  public List<String> getMemberList() {
    return memberlist;
  }

  public int getUserId() {
    return (int) this.user_id.getValue();
  }

  public void setMemberList(List<String> list) {
    memberlist = list;
  }

}

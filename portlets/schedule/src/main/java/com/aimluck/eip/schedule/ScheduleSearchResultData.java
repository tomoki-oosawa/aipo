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
 */

package com.aimluck.eip.schedule;

import java.text.SimpleDateFormat;

import com.aimluck.eip.common.ALEipUser;

/**
 * スケジュールの検索データを管理するクラスです。
 * 
 */
public class ScheduleSearchResultData extends ScheduleResultData {

  private ALEipUser createUser;

  @Override
  public void initField() {
    super.initField();
  }

  public String getDateFull() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy'年'MM'月'dd'日（'E'）'");
    StringBuilder b = new StringBuilder();
    if ("S".equals(getPattern())) {
      b.append(sdf.format(getStartDate().getValue()));
    } else {
      b.append(sdf.format(getStartDate().getValue())).append(" ").append(
        getDate2());
    }
    return b.toString();
  }

  public String getDateDetail() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy'年'MM'月'dd'日（'E'）'");
    StringBuilder b = new StringBuilder();
    if ("S".equals(getPattern())) {
      String startDate = sdf.format(getStartDate().getValue());
      b.append(startDate);
      String endDate = sdf.format(getEndDate().getValue());
      if (!startDate.equals(endDate)) {
        b.append("～");
        b.append(sdf.format(getEndDate().getValue()));
      }
    } else {
      b.append(sdf.format(getStartDate().getValue())).append(" ").append(
        getDate2());
    }
    return b.toString();
  }

  public String getViewDate() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    sdf.format(getStartDate().getValue());
    return sdf.format(getStartDate().getValue());
  }

  /**
   * @return createUser
   */
  public ALEipUser getCreateUser() {
    return createUser;
  }

  /**
   * @param createUser
   *          セットする createUser
   */
  public void setCreateUser(ALEipUser createUser) {
    this.createUser = createUser;
  }
}

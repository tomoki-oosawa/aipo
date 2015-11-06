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
package com.aimluck.eip.schedule;

/**
 * スケジュールの検索データを管理するクラスです。
 * 
 */
public class CellScheduleResultData extends ScheduleResultData {

  /** <code>is_span</code> 期間スケジュールかどうか */
  private boolean is_span;

  /**
   * 日付を取得します。
   * 
   * @return
   */
  public String getDateForCell() {
    if (getStartDate().getValue().equals(getEndDate().getValue())) {
      return getStartDate().toString();
    } else if ((getStartDate().getYear().equals(getEndDate().getYear())
      && getStartDate().getMonth().equals(getEndDate().getMonth()) && getStartDate()
      .getDay()
      .equals(getEndDate().getDay()))
      || isRepeat()) {
      return new StringBuffer()
        .append(getStartDate().toString())
        .append('-')
        .append(getEndDate().toString())
        .toString();
    } else {
      return new StringBuffer()
        .append(getStartDate().toString())
        .append('-')
        .append("24:00")
        .toString();
    }
  }

  public void setSpan(boolean is_span) {
    this.is_span = is_span;
  }

  public boolean isSpan() {
    return is_span;
  }
}

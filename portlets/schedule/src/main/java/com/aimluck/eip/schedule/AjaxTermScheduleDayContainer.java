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
package com.aimluck.eip.schedule;

import java.util.Date;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.common.ALData;

/**
 * カレンダー用スケジュールコンテナです。
 * 
 */
public class AjaxTermScheduleDayContainer implements ALData {

  /**
   * <code>today</code> 日付
   */
  private ALDateTimeField today;

  /**
   * <code>spanRd</code> Term
   */
  private AjaxScheduleResultData termRd;

  private boolean is_hasterm = false;

  /*
   * 
   */
  public void initField() {
    // 日付
    today = new ALDateTimeField("yyyy-MM-dd-HH-mm");
    // JOB
    termRd = null;
  }

  /**
   * 日付を取得します。
   * 
   * @return
   */
  public ALDateTimeField getDate() {
    return today;
  }

  /**
   * 日付を設定します。
   * 
   * @param date
   */
  public void setDate(Date date) {
    today.setValue(date);
  }

  /**
   * 期間スケジュールを追加します。
   * 
   * @param rd
   */
  public void setTermResultData(AjaxScheduleResultData rd) {
    termRd = rd;
  }

  /**
   * 期間スケジュールを取得します。
   * 
   * @return
   */
  public AjaxScheduleResultData getTermResultData() {
    return termRd;
  }

  public void setHasTerm(boolean bool) {
    is_hasterm = bool;
  }

  public boolean isHasTerm() {
    return is_hasterm;
  }

  /**
   * JobがNULLかどうか。
   * 
   * @return
   */
  public boolean isTermNull() {
    return termRd == null;
  }
}

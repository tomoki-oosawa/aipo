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

/**
 * スケジュールの検索結果を管理するクラスです。
 * 
 */
public class ScheduleOnedayResultData extends ScheduleResultData {

  /** <code>startRow</code> 開始 */
  private int startRow;

  /** <code>endRow</code> 終了 */
  private int endRow;

  /** <code>index</code> インデックス */
  private int index;

  private int dRowCount;

  /**
   * 終了地点を取得します。
   * 
   * @return endRow
   */
  public int getEndRow() {
    return endRow;
  }

  /**
   * 終了地点を設定します。
   * 
   * @param endRow
   */
  public void setEndRow(int endRow) {
    this.endRow = endRow;
  }

  /**
   * 開始地点を取得します。
   * 
   * @return startRow
   */
  public int getStartRow() {
    return startRow;
  }

  /**
   * 開始地点を設定します。
   * 
   * @param startRow
   */
  public void setStartRow(int startRow) {
    this.startRow = startRow;
  }

  /**
   * インデックスを取得します。
   * 
   * @return index
   */
  public int getIndex() {
    return index;
  }

  /**
   * インデックスを設定します。
   * 
   * @param index
   */
  public void setIndex(int index) {
    this.index = index;
  }

  /**
   * @param dRowCount
   */
  public void setdRowCount(int dRowCount) {
    this.dRowCount = dRowCount;
  }

  /**
   * @return dRowCount
   */
  public int getdRowCount() {
    return dRowCount;
  }
}

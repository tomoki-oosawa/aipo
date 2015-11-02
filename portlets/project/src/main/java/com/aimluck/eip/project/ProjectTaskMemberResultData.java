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

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * タスク担当者のResultDataです。 <BR>
 * 
 */
public class ProjectTaskMemberResultData implements ALData {

  /** ユーザーID */
  protected ALNumberField user_id;

  /** ユーザー名 */
  protected ALStringField user_name;

  /** 工数 */
  protected BigDecimal workload;

  /** 小数値項目フォーマット */
  private final DecimalFormat df = new DecimalFormat("#.0##");

  /**
   * 初期設定を行います
   */
  @Override
  public void initField() {
    user_id = new ALNumberField();
    user_name = new ALStringField();
    workload = new BigDecimal(0);
  }

  /**
   * ユーザーIDを取得する
   * 
   * @return ユーザーID
   */
  public ALNumberField getUserId() {
    return user_id;
  }

  /**
   * ユーザーIDを設定する
   * 
   * @param i
   *          ユーザーID
   */
  public void setUserId(long i) {
    user_id.setValue(i);
  }

  /**
   * ユーザー名を取得する
   * 
   * @return ユーザー名
   */
  public String getUserName() {
    return user_name.getValue();
  }

  /**
   * ユーザー名を設定する
   * 
   * @param string
   *          ユーザー名
   */
  public void setUserName(String string) {
    user_name.setValue(string);
  }

  /**
   * 工数を取得する
   * 
   * @return 工数
   */
  public BigDecimal getWorkload() {
    return new BigDecimal(df.format(workload));
  }

  /**
   * 工数を設定する
   * 
   * @param i
   *          工数
   */
  public void setWorkload(BigDecimal i) {
    workload = i;
  }
}

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
package com.aimluck.commons.field;

import java.io.Serializable;

/**
 * 入力フィールドを表す抽象クラスです。 <br />
 * 
 */
public abstract class ALAbstractField implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = -6402614177274276807L;

  /** 入力文字の文字コード判別用に利用する文字コード */
  protected static final String ENCORDE_CONFIRM_CHARTYPE = "shift_jis";

  /** Nullフラグ */
  protected boolean notNull = false;

  /** 入力フィールド名 */
  protected String fieldName = null;

  /**
   * コンストラクタ
   * 
   */
  public ALAbstractField() {
  }

  /**
   * notNull(必須入力)フラグを設定します。
   * 
   * @param bool
   *          notNullフラグ
   */
  public void setNotNull(boolean bool) {
    notNull = bool;
  }

  /**
   * notNull(必須入力)フラグを取得します。
   * 
   * @return
   */
  public boolean isNotNull() {
    return notNull;
  }

  /**
   * 入力フィールド名を設定します。
   * 
   * @param str
   */
  public void setFieldName(String str) {
    fieldName = str;
  }

  /**
   * 入力フィールド名を取得します。
   * 
   * @return
   */
  public String getFieldName() {
    return fieldName;
  }

  /**
   * 入力フィールドに値を設定します。
   * 
   * @param str
   */
  public abstract void setValue(String str);

}

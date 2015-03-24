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
package com.aimluck.commons.field;

/**
 * 入力フィールド値が指定どおりではない場合にスローされます。 <br />
 * 
 */
public class ALIllegalDateException extends Exception {

  /**
   *
   */
  private static final long serialVersionUID = -1521066221069185346L;

  /**
   * 詳細メッセージを指定しないで ALIllegalDateException を構築します。
   * 
   */
  public ALIllegalDateException() {
    super();
  }

  /**
   * 指定された詳細メッセージを持つ ALIllegalDateException を構築します。
   * 
   * @param msg
   */
  public ALIllegalDateException(String msg) {
    super(msg);
  }
}

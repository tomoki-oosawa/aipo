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
package com.aimluck.commons.utils;

import java.io.Serializable;
import java.util.Comparator;

/**
 * 半角文字の比較用クラスです。 <br />
 * 
 */
public abstract class ALAbstractComparator<T> implements Comparator<T>,
    Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   * コンストラクタ
   * 
   */
  public ALAbstractComparator() {
  }

  /**
   * 指定されたオブジェクトを比較します。
   * 
   */
  @Override
  public int compare(Object obj1, Object obj2) {
    return compare(getCharArray(obj1), getCharArray(obj2));
  }

  /**
   * 指定されたオブジェクトを比較します。
   * 
   * @param chars1
   * @param chars2
   * @return
   */
  public final int compare(char[] chars1, char[] chars2) {
    int max;
    int ret = 0;
    int char1Len = chars1.length;
    int char2Len = chars2.length;

    if (char1Len < char2Len) {
      max = char1Len;
    } else {
      max = char2Len;
    }
    for (int i = 0; i < max; i++) {
      if ((ret = chars1[i] - chars2[i]) != 0) {
        return ret;
      }
    }
    return char1Len - char2Len;
  }

  /**
   * char配列を取得します。
   * 
   * @param obj
   * @return
   */
  protected abstract char[] getCharArray(Object obj);

}

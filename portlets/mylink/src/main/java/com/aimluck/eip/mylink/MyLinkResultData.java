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
package com.aimluck.eip.mylink;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * Myリンクの ResultData です。<BR>
 * 
 */
public class MyLinkResultData implements ALData {

  /** リンクタイトル */
  private ALStringField title;

  /** リンクURL */
  private ALStringField link;

  /**
   * フィールドを初期化します。<BR>
   * 
   * 
   */
  public void initField() {
    title = new ALStringField();
    link = new ALStringField();
  }

  /**
   * URLを取得します。<BR>
   * 
   * @return
   */
  public ALStringField getLink() {
    return link;
  }

  /**
   * タイトルを取得します。<BR>
   * 
   * @return
   */
  public ALStringField getTitle() {
    return title;
  }

  /**
   * URLを設定します。<BR>
   * 
   * @param string
   */
  public void setLink(String string) {
    link.setValue(string);
  }

  /**
   * タイトルを設定します。<BR>
   * 
   * @param string
   */
  public void setTitle(String string) {
    title.setValue(string);
  }

}

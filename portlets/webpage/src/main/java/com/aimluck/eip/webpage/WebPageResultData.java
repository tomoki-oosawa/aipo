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
package com.aimluck.eip.webpage;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * WebPageの ResultData です。<BR>
 * 
 */
public class WebPageResultData implements ALData {

  /** WebPageタイトル */
  private ALStringField title;

  /** WebPageURL */
  private ALStringField link;

  /** WebPage縦幅（通常時） */
  private ALNumberField normalheight;

  /** WebPage縦幅（最大化時） */
  private ALNumberField maximizedheight;

  /** WebPage表示フラグ */
  private boolean webpageflag;

  /**
   * フィールドを初期化します。<BR>
   * 
   * 
   */
  public void initField() {
    title = new ALStringField();
    link = new ALStringField();
    normalheight = new ALNumberField();
    maximizedheight = new ALNumberField();
    webpageflag = true;
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
   * 縦幅を取得します。 <BR>
   */
  public ALNumberField getNormalHeight() {
    return normalheight;
  }

  /**
   * 縦幅を取得します。 <BR>
   */
  public ALNumberField getMaximizedHeight() {
    return maximizedheight;
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

  /**
   * 標準表示時の縦長を設定します。<BR>
   */
  public void setNormalHeight(int i) {
    if (i <= 0) {
      normalheight.setValue(300);
    } else if (i > 3000) {
      normalheight.setValue(3000);
    } else {
      normalheight.setValue(i);
    }
  }

  /**
   * 最大化時の縦長を設定します。<BR>
   */
  public void setMaximizedHeight(int i) {
    if (i <= 0) {
      maximizedheight.setValue(300);
    } else if (i > 3000) {
      maximizedheight.setValue(3000);
    } else {
      maximizedheight.setValue(i);
    }
  }

  /**
   * 表示の有無のフラグを設定します。<BR>
   */
  public void setWebPageFlag(boolean bool) {
    webpageflag = bool;
  }

  /**
   * 表示の有無のフラグを取得します。<BR>
   */
  public boolean getWebPageFlag() {
    return webpageflag;
  }
}

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

package com.aimluck.eip.common;

import javax.servlet.http.HttpServletRequest;

import com.aimluck.eip.http.HttpServletRequestLocator;

/**
 * 会社情報、部署情報、役職情報をメモリ上に保持するクラスです。 <br />
 * 
 */
public class ALBlogManager extends ALEipManager {

  /** Singleton */
  private static ALBlogManager manager = new ALBlogManager();

  /** BlogResultDataキー */
  private static String BLOG_USER_LIST_KEY =
    "com.aimluck.eip.blog.BlogEntryLatestSelectData.userDataList";

  /**
   * 
   * @return
   */
  public static ALBlogManager getInstance() {
    return manager;
  }

  public Object getUserDataList() {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      // requestから取得
      Object obj = request.getAttribute(BLOG_USER_LIST_KEY);
      if (obj != null) {
        return obj;
      }
    }
    return null;
  }

  public void setUserDataList(Object obj) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      request.setAttribute(BLOG_USER_LIST_KEY, obj);
    }
  }

}

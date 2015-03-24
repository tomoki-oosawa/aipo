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
package com.aimluck.eip.blog;

import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipUser;

/**
 * ブログ画面のユーザー情報のResultDataです。 <br />
 */
public class BlogUserResultData extends ALEipUser implements ALData {

  /** 1日以内に記事投稿をしたかのフラグ */
  private boolean newly_create_entry;

  /**
   *
   *
   */
  @Override
  public void initField() {
    super.initField();
    newly_create_entry = false;
  }

  /**
   * 
   * @param bool
   */
  public void setNewlyCreateEntry(boolean bool) {
    newly_create_entry = bool;
  }

  public boolean newlyCreateEntry() {
    return newly_create_entry;
  }

}

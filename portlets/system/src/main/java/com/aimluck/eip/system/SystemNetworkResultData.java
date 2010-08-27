/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.system;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 *
 */
public class SystemNetworkResultData implements ALData {

  /** ローカル URL */
  private ALStringField local_url;

  /** グローバル URL */
  private ALStringField global_url;

  /**
   *
   */
  public void initField() {
    local_url = new ALStringField();
    global_url = new ALStringField();
  }

  /**
   * 
   * @param str
   */
  public void setLocalUrl(String str) {
    local_url.setValue(str);
  }

  /**
   * 
   * @return
   */
  public ALStringField getLocalUrl() {
    return local_url;
  }

  /**
   * 
   * @param str
   */
  public void setGlobalUrl(String str) {
    global_url.setValue(str);
  }

  /**
   * 
   * @return
   */
  public ALStringField getGlobalUrl() {
    return global_url;
  }

}

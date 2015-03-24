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
package com.aimluck.eip.system;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALCommonUtils;

/**
 *
 */
public class SystemNetworkResultData implements ALData {

  public static final String EXIST = "exist";

  /** ローカル URL */
  private ALStringField local_url;

  /** グローバル URL */
  private ALStringField global_url;

  private String sample = EXIST;

  /**
   *
   */
  @Override
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
   * @return
   */
  public String getWbrLocalUrl() {

    return ALCommonUtils.replaceToAutoCR(getLocalUrl().toString());

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

  /**
   * 
   * @return
   */
  public String getWbrGlobalUrl() {

    return ALCommonUtils.replaceToAutoCR(getGlobalUrl().toString());

  }

  /**
   * @return sample
   */
  public String getSample() {
    return sample;
  }

  /**
   * @param value
   */
  public void setSample(String value) {
    sample = value;
  }

}

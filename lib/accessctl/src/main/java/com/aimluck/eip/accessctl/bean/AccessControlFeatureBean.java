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
package com.aimluck.eip.accessctl.bean;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 *
 *
 */
public class AccessControlFeatureBean implements ALData {

  /** Feature ID */
  private ALNumberField feature_id;

  /** 機能名 */
  private ALStringField feature_name;

  /** 機能エイリアス名 */
  private ALStringField feature_alias_name;

  /**
   *
   */
  public void initField() {
    feature_id = new ALNumberField();
    feature_name = new ALStringField();
    feature_alias_name = new ALStringField();
  }

  /**
   * @return
   */
  public int getFeatureId() {
    return (int) feature_id.getValue();
  }

  /**
   * @return
   */
  public String getFeatureName() {
    return feature_name.getValue();
  }

  /**
   * @return
   */
  public String getFeatureAliasName() {
    return feature_alias_name.getValue();
  }

  /**
   * @param i
   */
  public void setFeatureId(long i) {
    feature_id.setValue(i);
  }

  /**
   * @param string
   */
  public void setFeatureName(String string) {
    feature_name.setValue(string);
  }

  /**
   * @param string
   */
  public void setFeatureAliasName(String string) {
    feature_alias_name.setValue(string);
  }
}

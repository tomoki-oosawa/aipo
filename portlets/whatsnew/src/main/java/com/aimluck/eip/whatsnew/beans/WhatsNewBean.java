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
package com.aimluck.eip.whatsnew.beans;

import java.util.ArrayList;
import java.util.List;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * 新着情報のBeanです。 <BR>
 * 
 */
public class WhatsNewBean implements ALData, Comparable<WhatsNewBean> {

  /** ID */
  private ALNumberField entity_id;

  /** 名前 */
  private ALStringField name;

  /** 名前 */
  private ALStringField owner_name;

  /** 作成日 */
  private ALDateTimeField update_date;

  /** パラメータ */
  private List<WhatsNewParamsBean> paramMap;

  /** ポートレット名 */
  private ALStringField portlet_name;

  /** Javascript関数 */

  private ALStringField js_function_name;

  /**
   *
   *
   */
  @Override
  public void initField() {
    entity_id = new ALNumberField();
    name = new ALStringField();
    owner_name = new ALStringField();
    update_date = new ALDateTimeField();
    paramMap = new ArrayList<WhatsNewParamsBean>();

    /** ポートレット名 */
    portlet_name = new ALStringField();

    /** Javascript関数 */
    js_function_name = new ALStringField();

  }

  /**
   * 
   * @param key
   * @param value
   */
  public void addParamMap(String k, String v) {
    WhatsNewParamsBean mbean = new WhatsNewParamsBean();
    mbean.initField();
    mbean.setKey(k);
    mbean.setValue(v);
    paramMap.add(mbean);
  }

  /**
   * 
   * @return
   */

  public List<WhatsNewParamsBean> getParamMap() {
    return paramMap;
  }

  /**
   * 
   * @param string
   */
  public void setName(String string) {
    name.setValue(string);
  }

  /**
   * 
   * @return
   */
  public String getName() {
    return name.toString();
  }

  /**
   * 
   * @param string
   */
  public void setOwnerName(String string) {
    owner_name.setValue(string);
  }

  /**
   * 
   * @return
   */
  public ALStringField getOwnerName() {
    return owner_name;
  }

  /**
   * 
   * @param string
   */
  public void setEntityId(int i) {
    entity_id.setValue(i);
  }

  /**
   * 
   * @return
   */
  public ALNumberField getEntityId() {
    return entity_id;
  }

  /**
   * @return
   */
  public ALDateTimeField getUpdateDate() {
    return update_date;
  }

  /**
   * @param string
   */
  public void setUpdateDate(String string) {
    update_date.setValue(string);
  }

  /**
   * @param string
   */
  public void setPortletName(String string) {
    portlet_name.setValue(string);
  }

  /**
   * @param string
   */
  public ALStringField getPortletName() {
    return portlet_name;
  }

  /**
   * @param string
   */
  public String getJsFunctionName() {
    return (js_function_name.getValue());
  }

  /**
   * @param string
   */
  public void setJsFunctionName(String string) {
    js_function_name.setValue(string);
  }

  @Override
  public int compareTo(WhatsNewBean obj) {
    // 更新日時で比較します
    return obj.getUpdateDate().getValue().compareTo(
      this.getUpdateDate().getValue());
  }

  /**
   * 自動生成
   * 
   * @return
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
      prime
        * result
        + ((update_date == null) ? 0 : update_date.getValue().hashCode());
    return result;
  }

  /**
   * 自動生成。更新日時で比較します。
   * 
   * @param obj
   * @return
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    WhatsNewBean other = (WhatsNewBean) obj;
    if (update_date == null) {
      if (other.update_date != null) {
        return false;
      }
    } else if (!update_date.getValue().equals(other.update_date.getValue())) {
      return false;
    }
    return true;
  }

}

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
package com.aimluck.eip.whatsnew;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.whatsnew.beans.WhatsNewBean;

/**
 * 新着情報のResultDataです。 <BR>
 * 
 */
public class WhatsNewResultData implements ALData {

  /** ポートレット ID */
  private ALNumberField portlet_type;

  /** ポートレット名 */
  private ALStringField portlet_name;

  /** 作成日 */
  private ALDateTimeField create_date;

  /** 更新日 */
  private ALDateTimeField update_date;

  /** 新着アイテム（ポートレット毎のリスト） */
  private List<WhatsNewBean> beans;

  /** 親ID */
  private ALNumberField parent_id;

  /**
   *
   *
   */
  @Override
  public void initField() {

    /** Type */
    portlet_type = new ALNumberField();

    /** ポートレット名 */
    portlet_name = new ALStringField();

    /** 作成日 */
    create_date = new ALDateTimeField();

    /** 更新日 */
    update_date = new ALDateTimeField();

    /** タイトルと作成日 */
    beans = new ArrayList<WhatsNewBean>();

    /** 親ID */
    parent_id = new ALNumberField();

  }

  /**
   * @return
   */
  public ALDateTimeField getCreateDate() {
    return create_date;
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
  public void setCreateDate(Date date) {
    create_date.setValue(date);
  }

  /**
   * @param string
   */
  public void setUpdateDate(Date date) {
    update_date.setValue(date);
  }

  /**
   * @param i
   */
  public void setType(long i) {
    portlet_type.setValue(i);
  }

  /**
   * @param i
   */
  public void setParentId(long i) {
    parent_id.setValue(i);
  }

  /**
   * @param
   */
  public long getParentId() {
    return parent_id.getValue();
  }

  /**
   * @param string
   */
  public List<WhatsNewBean> getBeans() {
    return beans;
  }

  /**
   * @param string
   */
  public void setBean(WhatsNewBean b) {
    beans.add(b);
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

}

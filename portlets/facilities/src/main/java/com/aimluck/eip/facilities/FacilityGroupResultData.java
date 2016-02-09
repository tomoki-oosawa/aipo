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
package com.aimluck.eip.facilities;

import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.facilities.util.FacilitiesUtils;

/**
 * 設備グループのResultDataです。 <BR>
 *
 */
public class FacilityGroupResultData implements ALData {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FacilityGroupResultData.class.getName());

  /** FacilityGroup ID */
  private ALNumberField group_id;

  /** 名前 */
  private ALStringField group_name;

  /** ソート */
  private ALNumberField sort;

  /**
   *
   *
   */
  @Override
  public void initField() {
    group_id = new ALNumberField();
    group_name = new ALStringField();
    sort = new ALNumberField();
  }

  /**
   * @return
   */
  public ALNumberField getGroupId() {
    return group_id;
  }

  /**
   * @return
   */
  public ALStringField getGroupName() {
    return group_name;
  }

  /**
   * @return
   */
  public String getStringGroupName() {
    return group_name.getValue();
  }

  /**
   * @param i
   */
  public void setGroupId(long i) {
    group_id.setValue(i);
  }

  /**
   * @param string
   */
  public void setGroupName(String string) {
    group_name.setValue(string);
  }

  /**
   * @return
   */
  public ALNumberField getSort() {
    return sort;
  }

  /**
   * @param int
   */
  public void setSort(int num) {
    sort.setValue(num);
  }

  /**
   * あるグループに所属する設備のリストを取得します
   *
   * @param postid
   * @return
   */
  public List<FacilityResultData> getFacilityListByGroupId(String groupid) {
    List<EipMFacility> result =
      FacilitiesUtils.getFacilityListByGroupId(Integer.parseInt(groupid));
    return FacilitiesUtils.getFacilityResultList(result);
  }

}

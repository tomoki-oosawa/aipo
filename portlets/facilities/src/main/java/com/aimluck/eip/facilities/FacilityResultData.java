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
package com.aimluck.eip.facilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipMFacilityGroup;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.facilities.util.FacilitiesUtils;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 設備のResultDataです。 <BR>
 * 
 */
public class FacilityResultData implements ALData {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FacilityResultData.class.getName());

  /** Facility ID */
  private ALNumberField facility_id;

  /** ユーザーID */
  private ALNumberField user_id;

  /** 設備名 */
  private ALStringField facility_name;

  /** メモ */
  private ALStringField note;

  /** 作成日 */
  private ALDateTimeField create_date;

  /** 更新日 */
  private ALDateTimeField update_date;

  /** 設備グループリスト */
  private List<FacilityGroupResultData> facilityGroupList;

  /**
   *
   *
   */
  @Override
  public void initField() {
    facility_id = new ALNumberField();
    user_id = new ALNumberField();
    facility_name = new ALStringField();
    note = new ALStringField();
    note.setTrim(false);
    create_date = new ALDateTimeField();
    update_date = new ALDateTimeField();
    facilityGroupList = new ArrayList<FacilityGroupResultData>();
  }

  /**
   * @return
   */
  public ALNumberField getFacilityId() {
    return facility_id;
  }

  /**
   * @return
   */
  public ALNumberField getUserId() {
    return user_id;
  }

  /**
   * @return
   */
  public ALStringField getFacilityName() {
    return facility_name;
  }

  public String getFacilityNameHtml() {
    return ALCommonUtils.replaceToAutoCR(facility_name.toString());
  }

  /**
   * @return
   */
  public String getStringFacilityName() {
    return facility_name.getValue();
  }

  /**
   * @param i
   */
  public void setFacilityId(long i) {
    facility_id.setValue(i);
  }

  public void setUserId(long value) {
    user_id.setValue(value);
  }

  /**
   * @param string
   */
  public void setFacilityName(String string) {
    facility_name.setValue(string);
  }

  /**
   * @return
   */
  public String getNote() {
    return ALEipUtils.getMessageList(note.getValue());
  }

  /**
   * @param string
   */
  public void setNote(String string) {
    note.setValue(string);
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
   * ある設備が所属する設備グループのリストを取得します
   * 
   * @param postid
   * @return
   */
  public List<FacilityGroupResultData> getFacilityGroupListByFacilityId(
      String facilityid) {
    List<EipMFacilityGroup> _facilityGroupList =
      FacilitiesUtils.getFacilityGroupListByFacilityId(facilityid);

    if (_facilityGroupList == null) {
      return facilityGroupList;
    }

    for (EipMFacilityGroup group : _facilityGroupList) {
      FacilityGroupResultData data = new FacilityGroupResultData();
      data.initField();
      data.setGroupId(group.getGroupId());
      data.setGroupName(group.getGroupName());
      facilityGroupList.add(data);
    }

    return facilityGroupList;
  }

  public List<FacilityGroupResultData> getFacilityGroupList() {
    return facilityGroupList;
  }
}

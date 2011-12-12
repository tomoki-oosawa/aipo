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

package com.aimluck.eip.facilities;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipMFacilityGroup;
import com.aimluck.eip.cayenne.om.portlet.EipMFacilityGroupMap;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 設備のResultDataです。 <BR>
 * 
 */
public class FacilityResultData implements ALData {

  /** logger */
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

  /** 登録日 */
  private ALStringField create_date;

  /** 更新日 */
  private ALStringField update_date;

  /** 設備グループリスト */
  private List<EipMFacilityGroup> facility_group_list;

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
    create_date = new ALStringField();
    update_date = new ALStringField();
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
  public ALStringField getCreateDate() {
    return create_date;
  }

  /**
   * @return
   */
  public ALStringField getUpdateDate() {
    return update_date;
  }

  /**
   * @param string
   */
  public void setCreateDate(String string) {
    create_date.setValue(string);
  }

  /**
   * @param string
   */
  public void setUpdateDate(String string) {
    update_date.setValue(string);
  }

  /**
   * ある設備が所属する設備グループのリストを取得します
   * 
   * @param postid
   * @return
   */
  public List<EipMFacilityGroup> getFacilityGroupListByFacilityId(
      String facilityid) {
    try {
      SelectQuery<EipMFacilityGroupMap> query =

      Database.query(EipMFacilityGroupMap.class);
      query.where(Operations.eq(
        EipMFacilityGroupMap.FACILITY_ID_PROPERTY,
        Integer.valueOf(facilityid)));
      List<EipMFacilityGroupMap> maps = query.fetchList();
      List<Integer> faclityGroupIdList = new ArrayList<Integer>();
      for (EipMFacilityGroupMap map : maps) {
        faclityGroupIdList.add(map.getGroupId());
      }
      if (faclityGroupIdList.size() > 0) {
        SelectQuery<EipMFacilityGroup> fquery =
          Database.query(EipMFacilityGroup.class);
        Expression exp =
          ExpressionFactory.inDbExp(
            EipMFacilityGroup.GROUP_ID_PK_COLUMN,
            faclityGroupIdList);
        fquery.setQualifier(exp);
        return facility_group_list = fquery.fetchList();
      } else {
        return null;
      }
    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return null;
    }
  }

  public List<EipMFacilityGroup> getFacilityGroupList() {
    return facility_group_list;
  }
}

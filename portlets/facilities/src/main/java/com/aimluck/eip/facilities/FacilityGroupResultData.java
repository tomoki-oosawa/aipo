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
import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.cayenne.om.portlet.EipMFacilityGroupMap;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.orm.query.SelectQuery;

/**
 * 施設グループのResultDataです。 <BR>
 * 
 */
public class FacilityGroupResultData implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FacilityGroupResultData.class.getName());

  /** FacilityGroup ID */
  private ALNumberField group_id;

  /** 名前 */
  private ALStringField group_name;

  /**
   *
   *
   */
  @Override
  public void initField() {
    group_id = new ALNumberField();
    group_name = new ALStringField();
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
   * あるグループに所属する施設のリストを取得します
   * 
   * @param postid
   * @return
   */
  public List<EipMFacility> getFacilityListByGroupId(String groupid) {
    try {
      SelectQuery<EipMFacilityGroupMap> query =

      Database.query(EipMFacilityGroupMap.class);
      query.where(Operations.eq(EipMFacilityGroupMap.GROUP_ID_PROPERTY, Integer
        .valueOf(groupid)));
      List<EipMFacilityGroupMap> maps = query.fetchList();
      List<Integer> faclityIdList = new ArrayList<Integer>();
      for (EipMFacilityGroupMap map : maps) {
        faclityIdList.add(map.getFacilityId());
      }
      SelectQuery<EipMFacility> fquery = Database.query(EipMFacility.class);
      Expression exp =
        ExpressionFactory.inDbExp(
          EipMFacility.FACILITY_ID_PK_COLUMN,
          faclityIdList);
      fquery.setQualifier(exp);
      fquery.orderAscending(EipMFacility.SORT_PROPERTY);
      return fquery.fetchList();
    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return null;
    }
  }
}

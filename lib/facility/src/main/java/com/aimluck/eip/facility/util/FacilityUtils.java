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
package com.aimluck.eip.facility.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.cayenne.om.portlet.EipMFacilityGroup;
import com.aimluck.eip.cayenne.om.portlet.EipMFacilityGroupMap;
import com.aimluck.eip.facility.beans.FacilityGroupLiteBean;
import com.aimluck.eip.facility.beans.FacilityLiteBean;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;

/**
 * 設備のユーティリティクラスです。 <br />
 *
 */
public class FacilityUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FacilityUtils.class.getName());

  public static List<FacilityLiteBean> getFacilityLiteBeans(RunData rundata) {
    List<FacilityLiteBean> facilityAllList = new ArrayList<FacilityLiteBean>();

    try {
      SelectQuery<EipMFacility> query = Database.query(EipMFacility.class);
      query.orderAscending(EipMFacility.SORT_PROPERTY);
      List<EipMFacility> facility_list =
        query.orderAscending(EipMFacility.FACILITY_NAME_PROPERTY).fetchList();

      for (EipMFacility record : facility_list) {
        FacilityLiteBean bean = new FacilityLiteBean();
        bean.initField();
        bean.setFacilityId(record.getFacilityId().longValue());
        bean.setFacilityName(record.getFacilityName());
        facilityAllList.add(bean);
      }
    } catch (Exception ex) {
      logger.error("FacilityUtils.getFacilityLiteBeans", ex);
    }
    return facilityAllList;
  }

  public static List<FacilityLiteBean> getFacilityFromGroupId(RunData rundata,
      int groupid) {
    List<FacilityLiteBean> facilityAllList = new ArrayList<FacilityLiteBean>();
    // facilitygroupmap探索
    SelectQuery<EipMFacilityGroupMap> mapquery =
      Database.query(EipMFacilityGroupMap.class);
    Expression mapexp =
      ExpressionFactory.matchExp(
        EipMFacilityGroupMap.GROUP_ID_PROPERTY,
        groupid);
    mapquery.setQualifier(mapexp);
    List<EipMFacilityGroupMap> FacilityMaps = mapquery.fetchList();
    List<Integer> facilityIds = new ArrayList<Integer>();
    for (EipMFacilityGroupMap map : FacilityMaps) {
      facilityIds.add(map.getEipMFacilityFacilityId().getFacilityId());
    }

    List<EipMFacility> facility_list;

    if (facilityIds.isEmpty()) {
      // for empty
      facility_list = new ArrayList<EipMFacility>(0);
    } else {
      SelectQuery<EipMFacility> fquery = Database.query(EipMFacility.class);
      Expression fexp =
        ExpressionFactory.inDbExp(
          EipMFacility.FACILITY_ID_PK_COLUMN,
          facilityIds);
      fquery.setQualifier(fexp);
      fquery.orderAscending(EipMFacility.SORT_PROPERTY);
      facility_list = fquery.fetchList();
    }

    for (EipMFacility record : facility_list) {
      FacilityLiteBean bean = new FacilityLiteBean();
      bean.initField();
      bean.setFacilityId(record.getFacilityId().longValue());
      bean.setFacilityName(record.getFacilityName());
      facilityAllList.add(bean);
    }
    return facilityAllList;
  }

  public static List<FacilityLiteBean> getFacilityFromGroupName(
      RunData rundata, String groupname) {
    /** SQLを構築してデータベース検索 */
    // グループ名から行取得
    SelectQuery<EipMFacilityGroup> query =
      Database.query(EipMFacilityGroup.class);
    Expression exp =
      ExpressionFactory.matchExp(
        EipMFacilityGroup.GROUP_NAME_PROPERTY,
        groupname);
    query.setQualifier(exp);
    EipMFacilityGroup group = query.fetchSingle();
    return getFacilityFromGroupId(rundata, group.getGroupId());
  }

  public static List<FacilityGroupLiteBean> getFacilityGroupLiteBeans() {
    List<FacilityGroupLiteBean> facilityGroupAllList =
      new ArrayList<FacilityGroupLiteBean>();
    try {
      SelectQuery<EipMFacilityGroup> query =
        Database.query(EipMFacilityGroup.class);

      List<EipMFacilityGroup> facility_list =
        query.orderAscending(EipMFacilityGroup.SORT_PROPERTY).fetchList();

      for (EipMFacilityGroup record : facility_list) {
        FacilityGroupLiteBean bean = new FacilityGroupLiteBean();
        bean.initField();
        bean.setFacilityGroupId(record.getGroupId().longValue());
        bean.setFacilityGroupName(record.getGroupName());
        facilityGroupAllList.add(bean);
      }
    } catch (Exception ex) {
      logger.error("FacilityUtils.getFacilityFromGroupName", ex);
    }
    return facilityGroupAllList;
  }
}

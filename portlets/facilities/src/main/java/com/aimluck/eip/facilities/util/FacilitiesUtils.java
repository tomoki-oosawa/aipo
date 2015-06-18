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
package com.aimluck.eip.facilities.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.cayenne.om.portlet.EipMFacilityGroup;
import com.aimluck.eip.cayenne.om.portlet.EipMFacilityGroupMap;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.facilities.FacilityGroupResultData;
import com.aimluck.eip.facilities.FacilityResultData;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 設備のユーティリティクラスです。 <BR>
 * 
 */
public class FacilitiesUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FacilitiesUtils.class.getName());

  public static final String FACILITIES_PORTLET_NAME = "Facilities";

  /**
   * 設備オブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipMFacility getEipMFacility(RunData rundata, Context context) {
    String facilityid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (facilityid == null || Integer.valueOf(facilityid) == null) {
        // Facilities IDが空の場合
        logger.debug("[Facility] Empty ID...");
        return null;
      }

      EipMFacility facility = Database.get(EipMFacility.class, facilityid);

      return facility;
    } catch (Exception ex) {
      logger.error("facilities", ex);
      return null;
    }
  }

  /**
   * 設備グループオブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipMFacilityGroup getEipMFacilityGroup(RunData rundata,
      Context context) {
    String faclitygroupid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (faclitygroupid == null || Integer.valueOf(faclitygroupid) == null) {
        // Facilities IDが空の場合
        logger.debug("[Facility] Empty ID...");
        return null;
      }
      Expression exp =
        ExpressionFactory.matchDbExp(
          EipMFacilityGroup.GROUP_ID_PK_COLUMN,
          faclitygroupid);
      List<EipMFacilityGroup> facilities =
        Database.query(EipMFacilityGroup.class, exp).fetchList();
      if (facilities == null || facilities.size() == 0) {
        // 指定したFacilities IDのレコードが見つからない場合
        logger.debug("[Facilities] Not found ID...");
        return null;
      }
      return facilities.get(0);
    } catch (Exception ex) {
      logger.error("facilities", ex);
      return null;
    }
  }

  /**
   * 
   * @param rundata
   * @param context
   */
  public static List<FacilityResultData> getFacilityAllList() {
    List<FacilityResultData> facilityAllList =
      new ArrayList<FacilityResultData>();

    try {
      List<EipMFacility> aList =
        Database.query(EipMFacility.class).orderAscending(
          EipMFacility.SORT_PROPERTY).fetchList();

      for (EipMFacility record : aList) {
        FacilityResultData rd = new FacilityResultData();
        rd.initField();
        rd.setFacilityId(record.getFacilityId().longValue());
        rd.setFacilityName(record.getFacilityName());
        facilityAllList.add(rd);
      }
    } catch (Exception ex) {
      logger.error("facilities", ex);
    }
    return facilityAllList;
  }

  public static List<FacilityResultData> getFacilityList(String groupname) {
    List<FacilityResultData> list = new ArrayList<FacilityResultData>();

    StringBuffer statement = new StringBuffer();
    statement.append("SELECT DISTINCT ");
    statement.append("  B.FACILITY_ID, B.FACILITY_NAME, B.SORT ");
    statement.append("FROM eip_facility_group as A ");
    statement.append("LEFT JOIN eip_m_facility as B ");
    statement.append("  on A.FACILITY_ID = B.FACILITY_ID ");
    statement.append("LEFT JOIN turbine_group as C ");
    statement.append("  on A.GROUP_ID = C.GROUP_ID ");
    statement.append("WHERE C.GROUP_NAME = #bind($groupname) ");
    statement.append("ORDER BY B.SORT");

    String query = statement.toString();

    try {
      List<EipMFacility> list2 =
        Database
          .sql(EipMFacility.class, query)
          .param("groupname", groupname)
          .fetchList();

      FacilityResultData frd;
      // ユーザデータを作成し、返却リストへ格納
      for (EipMFacility record : list2) {
        frd = new FacilityResultData();
        frd.initField();
        frd.setFacilityId(record.getFacilityId());
        frd.setFacilityName(record.getFacilityName());
        list.add(frd);
      }
    } catch (Exception ex) {
      logger.error("[Exception]", ex);
    }

    return list;
  }

  public static List<FacilityResultData> getFacilityGroupList(Integer groupid) {
    List<FacilityResultData> list = new ArrayList<FacilityResultData>();

    StringBuffer statement = new StringBuffer();
    statement.append("SELECT DISTINCT ");
    statement.append("  B.FACILITY_ID, B.FACILITY_NAME, B.SORT ");
    statement.append("FROM eip_m_facility_group_map as A ");
    statement.append("LEFT JOIN eip_m_facility as B ");
    statement.append("  on A.FACILITY_ID = B.FACILITY_ID ");
    statement.append("LEFT JOIN eip_m_facility_group as C ");
    statement.append("  on A.GROUP_ID = C.GROUP_ID ");
    statement.append("WHERE C.GROUP_ID = #bind($groupid) ");
    statement.append("ORDER BY B.SORT");

    String query = statement.toString();

    try {
      List<EipMFacility> list2 =
        Database
          .sql(EipMFacility.class, query)
          .param("groupid", groupid)
          .fetchList();

      FacilityResultData frd;
      // ユーザデータを作成し、返却リストへ格納
      for (EipMFacility record : list2) {
        frd = new FacilityResultData();
        frd.initField();
        frd.setFacilityId(record.getFacilityId());
        frd.setFacilityName(record.getFacilityName());
        list.add(frd);
      }
    } catch (Exception ex) {
      logger.error("[Exception]", ex);
    }

    return list;
  }

  public static List<FacilityResultData> getFacilityListByGroupId(String groupId) {
    List<FacilityResultData> list = new ArrayList<FacilityResultData>();

    // SQLの作成
    StringBuffer statement = new StringBuffer();
    statement.append("SELECT B.FACILITY_ID, B.FACILITY_NAME, B.SORT ");
    statement.append("FROM eip_m_facility_group_map as A ");
    statement
      .append("LEFT JOIN eip_m_facility as B on A.FACILITY_ID = B.FACILITY_ID ");
    statement.append("WHERE A.GROUP_ID = #bind($groupId) ");
    statement.append("ORDER BY B.SORT");

    String query = statement.toString();

    try {
      List<EipMFacility> list2 =
        Database.sql(EipMFacility.class, query).param(
          "groupId",
          Integer.parseInt(groupId)).fetchList();

      FacilityResultData frd;
      // ユーザデータを作成し、返却リストへ格納
      for (EipMFacility record : list2) {
        frd = new FacilityResultData();
        frd.initField();
        frd.setFacilityId(record.getFacilityId());
        frd.setFacilityName(record.getFacilityName());
        list.add(frd);
      }
    } catch (Exception ex) {
      logger.error("[Exception]", ex);
    }

    return list;

  }

  public static List<Integer> getFacilityIds(String groupname) {
    List<Integer> list = new ArrayList<Integer>();

    // SQLの作成
    StringBuffer statement = new StringBuffer();
    statement.append("SELECT DISTINCT ");
    statement.append("  B.FACILITY_ID ");
    statement.append("FROM eip_facility_group as A ");
    statement.append("LEFT JOIN eip_m_facility as B ");
    statement.append("  on A.FACILITY_ID = B.FACILITY_ID ");
    statement.append("LEFT JOIN turbine_group as C ");
    statement.append("  on A.GROUP_ID = C.GROUP_ID ");
    statement.append("WHERE C.GROUP_NAME = #bind($groupname)");
    String query = statement.toString();

    try {
      List<EipMFacility> list2 =
        Database
          .sql(EipMFacility.class, query)
          .param("groupname", groupname)
          .fetchList();

      // ユーザデータを作成し、返却リストへ格納
      for (EipMFacility record : list2) {
        list.add(record.getFacilityId());
      }
    } catch (Exception ex) {
      logger.error("[Exception]", ex);
    }

    return list;
  }

  public static List<Integer> getFacilityGroupIds(Integer groupid) {
    List<Integer> list = new ArrayList<Integer>();

    // SQLの作成
    StringBuffer statement = new StringBuffer();

    statement.append("SELECT A.group_id, C.facility_id, C.facility_name ");
    statement.append("FROM eip_m_facility_group AS A ");
    statement.append("INNER JOIN eip_m_facility_group_map as B ");
    statement.append("  ON A.group_id = B.group_id ");
    statement.append("INNER JOIN eip_m_facility as C ");
    statement.append("  ON C.facility_id = B.facility_id ");
    statement.append("WHERE A.group_id = #bind($groupid) ");
    String query = statement.toString();

    try {
      List<EipMFacility> list2 =
        Database
          .sql(EipMFacility.class, query)
          .param("groupid", groupid)
          .fetchList();

      // ユーザデータを作成し、返却リストへ格納
      for (EipMFacility record : list2) {
        list.add(record.getFacilityId());
      }
    } catch (Exception ex) {
      logger.error("[Exception]", ex);
    }

    return list;
  }

  public static List<FacilityResultData> getFacilitiesFromSelectQuery(
      SelectQuery<EipMFacility> query) {
    List<FacilityResultData> list = new ArrayList<FacilityResultData>();
    try {
      List<EipMFacility> aList =
        query.orderAscending(EipMFacility.SORT_PROPERTY).fetchList();

      for (EipMFacility record : aList) {
        FacilityResultData rd = new FacilityResultData();
        rd.initField();
        rd.setFacilityName(record.getFacilityName());
        rd.setFacilityId(record.getFacilityId().longValue());
        rd.setCreateDate(record.getCreateDate());
        rd.setUpdateDate(record.getUpdateDate());
        list.add(rd);
      }
    } catch (Exception ex) {
      logger.error("[Exception]", ex);
    }
    return list;
  }

  /**
   * 第一引数のリストに，第二引数で指定したユーザ ID が含まれているかを検証する．
   * 
   * @param memberIdList
   * @param memberId
   * @return
   */
  public static boolean isContains(List<FacilityResultData> facilityrList,
      FacilityResultData rd) {
    int size = facilityrList.size();
    long fid = rd.getFacilityId().getValue();
    FacilityResultData facility = null;
    for (int i = 0; i < size; i++) {
      facility = facilityrList.get(i);
      if (facility.getFacilityId().getValue() == fid) {
        return true;
      }
    }
    return false;
  }

  public static List<EipMFacilityGroup> getFacilityGroupListByFacilityId(
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
        return fquery.fetchList();
      } else {
        return null;
      }
    } catch (Exception ex) {
      Database.rollback();
      logger.error("facilities", ex);
      return null;
    }
  }

  public static List<FacilityGroupResultData> getFacilityGroupAllList() {
    List<FacilityGroupResultData> facilityAllList =
      new ArrayList<FacilityGroupResultData>();

    try {
      List<EipMFacilityGroup> result =
        Database.query(EipMFacilityGroup.class).orderAscending(
          EipMFacilityGroup.GROUP_NAME_PROPERTY).fetchList();

      for (EipMFacilityGroup group : result) {
        FacilityGroupResultData data = new FacilityGroupResultData();
        data.initField();
        data.setGroupId(group.getGroupId());
        data.setGroupName(group.getGroupName());
        facilityAllList.add(data);
      }
    } catch (Exception ex) {
      logger.error("facilities", ex);
    }

    return facilityAllList;
  }

  public static List<EipMFacility> getFacilityListByGroupId(int groupid) {
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
      if (faclityIdList.size() > 0) {
        SelectQuery<EipMFacility> fquery = Database.query(EipMFacility.class);
        Expression exp =
          ExpressionFactory.inDbExp(
            EipMFacility.FACILITY_ID_PK_COLUMN,
            faclityIdList);
        fquery.setQualifier(exp);
        fquery.orderAscending(EipMFacility.SORT_PROPERTY);
        return fquery.fetchList();
      } else {
        List<EipMFacility> list = new ArrayList<EipMFacility>();
        return list;
      }
    } catch (Exception ex) {
      Database.rollback();
      logger.error("facilities", ex);
      List<EipMFacility> list = new ArrayList<EipMFacility>();
      return list;
    }
  }

  public static List<FacilityResultData> getFacilityResultList(
      List<EipMFacility> result) {
    List<FacilityResultData> list = new ArrayList<FacilityResultData>();
    for (EipMFacility model : result) {
      list.add(getFacilityResultData(model));
    }
    return list;
  }

  public static FacilityResultData getFacilityResultData(EipMFacility model) {
    FacilityResultData data = new FacilityResultData();
    data.initField();
    data.setFacilityId(model.getFacilityId());
    data.setFacilityName(model.getFacilityName());
    data.setNote(model.getNote());
    data.setUpdateDate(model.getUpdateDate());
    data.setCreateDate(model.getCreateDate());
    data.setUserId(model.getUserId());
    return data;
  }
}

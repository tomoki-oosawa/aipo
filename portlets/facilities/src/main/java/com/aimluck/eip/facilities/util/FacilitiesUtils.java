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

package com.aimluck.eip.facilities.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.cayenne.om.portlet.EipMFacilityGroup;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.facilities.FacilityResultData;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 施設のユーティリティクラスです。 <BR>
 * 
 */
public class FacilitiesUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FacilitiesUtils.class.getName());

  /**
   * 施設オブジェクトモデルを取得します。 <BR>
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

      Expression exp =
        ExpressionFactory.matchDbExp(
          EipMFacility.FACILITY_ID_PK_COLUMN,
          facilityid);
      List<EipMFacility> facilities =
        Database.query(EipMFacility.class, exp).fetchList();
      if (facilities == null || facilities.size() == 0) {
        // 指定したFacilities IDのレコードが見つからない場合
        logger.debug("[Facilities] Not found ID...");
        return null;
      }
      return facilities.get(0);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 施設グループオブジェクトモデルを取得します。 <BR>
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
      logger.error("Exception", ex);
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
          EipMFacility.FACILITY_NAME_PROPERTY).fetchList();

      for (EipMFacility record : aList) {
        FacilityResultData rd = new FacilityResultData();
        rd.initField();
        rd.setFacilityId(record.getFacilityId().longValue());
        rd.setFacilityName(record.getFacilityName());
        facilityAllList.add(rd);
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
    }
    return facilityAllList;
  }

  public static List<FacilityResultData> getFacilityList(String groupname) {
    List<FacilityResultData> list = new ArrayList<FacilityResultData>();

    // SQLの作成
    StringBuffer statement = new StringBuffer();
    statement.append("SELECT DISTINCT ");
    statement.append("  B.FACILITY_ID, B.FACILITY_NAME ");
    statement.append("FROM eip_facility_group as A ");
    statement.append("LEFT JOIN eip_m_facility as B ");
    statement.append("  on A.FACILITY_ID = B.FACILITY_ID ");
    statement.append("LEFT JOIN turbine_group as C ");
    statement.append("  on A.GROUP_ID = C.GROUP_ID ");
    statement.append("WHERE C.GROUP_NAME = #bind($groupname) ");
    statement.append("ORDER BY B.FACILITY_NAME");
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

  public static List<FacilityResultData> getFacilitiesFromSelectQuery(
      SelectQuery<EipMFacility> query) {
    List<FacilityResultData> list = new ArrayList<FacilityResultData>();
    try {
      List<EipMFacility> aList = query.fetchList();

      for (EipMFacility record : aList) {
        FacilityResultData rd = new FacilityResultData();
        rd.initField();
        rd.setFacilityName(record.getFacilityName());
        rd.setFacilityId(record.getFacilityId().longValue());
        list.add(rd);
      }
    } catch (Exception ex) {
      logger.error("[Exception]", ex);
    }
    Collections.sort(list, new Comparator<FacilityResultData>() {
      @Override
      public int compare(FacilityResultData str1, FacilityResultData str2) {
        return str1.getStringFacilityName().compareTo(
          str2.getStringFacilityName());
      }
    });
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
}

/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.facilities.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.facilities.FacilityResultData;
import com.aimluck.eip.orm.DatabaseOrmService;
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
    String facilityid = ALEipUtils.getTemp(rundata, context,
        ALEipConstants.ENTITY_ID);
    try {
      if (facilityid == null || Integer.valueOf(facilityid) == null) {
        // Facilities IDが空の場合
        logger.debug("[Facility] Empty ID...");
        return null;
      }

      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      Expression exp = ExpressionFactory.matchDbExp(
          EipMFacility.FACILITY_ID_PK_COLUMN, facilityid);
      SelectQuery query = new SelectQuery(EipMFacility.class, exp);
      List<?> facilities = dataContext.performQuery(query);
      if (facilities == null || facilities.size() == 0) {
        // 指定したFacilities IDのレコードが見つからない場合
        logger.debug("[Facilities] Not found ID...");
        return null;
      }
      return ((EipMFacility) facilities.get(0));
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
    List<FacilityResultData> facilityAllList = new ArrayList<FacilityResultData>();

    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      SelectQuery query = new SelectQuery(EipMFacility.class);
      @SuppressWarnings("unchecked")
      List<EipMFacility> aList = dataContext.performQuery(query);

      int size = aList.size();
      for (int i = 0; i < size; i++) {
        EipMFacility record = aList.get(i);
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
    statement.append("FROM EIP_FACILITY_GROUP as A ");
    statement.append("LEFT JOIN EIP_M_FACILITY as B ");
    statement.append("  on A.FACILITY_ID = B.FACILITY_ID ");
    statement.append("LEFT JOIN TURBINE_GROUP as C ");
    statement.append("  on A.GROUP_ID = C.GROUP_ID ");
    statement.append("WHERE C.GROUP_NAME = '").append(groupname).append("'");
    statement.append("order by B.FACILITY_NAME");
    String query = statement.toString();

    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      @SuppressWarnings("deprecation")
      SQLTemplate rawSelect = new SQLTemplate(EipMFacility.class, query, true);
      rawSelect.setFetchingDataRows(true);
      @SuppressWarnings("unchecked")
      List<DataRow> flist = dataContext.performQuery(rawSelect);
      int recNum = flist.size();

      FacilityResultData frd;
      DataRow dataRow;
      // ユーザデータを作成し、返却リストへ格納
      for (int j = 0; j < recNum; j++) {
        dataRow = flist.get(j);
        frd = new FacilityResultData();
        frd.initField();
        frd.setFacilityId(((Integer) ALEipUtils.getObjFromDataRow(dataRow,
            EipMFacility.FACILITY_ID_PK_COLUMN)).intValue());
        frd.setFacilityName((String) ALEipUtils.getObjFromDataRow(dataRow,
            EipMFacility.FACILITY_NAME_COLUMN));
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
    statement.append("FROM EIP_FACILITY_GROUP as A ");
    statement.append("LEFT JOIN EIP_M_FACILITY as B ");
    statement.append("  on A.FACILITY_ID = B.FACILITY_ID ");
    statement.append("LEFT JOIN TURBINE_GROUP as C ");
    statement.append("  on A.GROUP_ID = C.GROUP_ID ");
    statement.append("WHERE C.GROUP_NAME = '").append(groupname).append("'");
    String query = statement.toString();

    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      @SuppressWarnings("deprecation")
      SQLTemplate rawSelect = new SQLTemplate(EipMFacility.class, query, true);
      rawSelect.setFetchingDataRows(true);
      @SuppressWarnings("unchecked")
      List<DataRow> flist = dataContext.performQuery(rawSelect);
      int recNum = flist.size();

      DataRow dataRow;
      // ユーザデータを作成し、返却リストへ格納
      for (int j = 0; j < recNum; j++) {
        dataRow = flist.get(j);
        list.add((Integer) ALEipUtils.getObjFromDataRow(dataRow,
            EipMFacility.FACILITY_ID_PK_COLUMN));
      }
    } catch (Exception ex) {
      logger.error("[Exception]", ex);
    }

    return list;
  }

  public static List<FacilityResultData> getFacilitiesFromSelectQuery(
      SelectQuery query) {
    List<FacilityResultData> list = new ArrayList<FacilityResultData>();
    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      List<?> aList = dataContext.performQuery(query);

      int size = aList.size();

      for (int i = 0; i < size; i++) {
        EipMFacility record = (EipMFacility) aList.get(i);
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

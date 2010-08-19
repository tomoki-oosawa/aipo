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
package com.aimluck.eip.facilities;

import java.util.List;
import java.util.jar.Attributes;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.facilities.util.FacilitiesUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 施設検索データを管理するクラスです。 <BR>
 * 
 */
public class FacilitySelectData extends
    ALAbstractSelectData<EipMFacility, EipMFacility> implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FacilitySelectData.class.getName());

  /** 施設の総数 */
  private int facilitySum;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * @see com.aimluck.eip.common.ALAbstractSelectData#init(com.aimluck.eip.modules.actions.common.ALAction,
   *      org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, ALEipUtils
        .getPortlet(rundata, context).getPortletConfig().getInitParameter(
          "p2a-sort"));
    }

    super.init(action, rundata, context);
  }

  /**
   * 一覧データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   * @see com.aimluck.eip.common.ALAbstractListData#selectData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  @Override
  public List<EipMFacility> selectList(RunData rundata, Context context) {
    try {

      SelectQuery<EipMFacility> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      List<EipMFacility> list = query.fetchList();
      // 施設の総数をセットする．
      facilitySum = list.size();

      return buildPaginatedList(list);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipMFacility> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipMFacility> query = new SelectQuery<EipMFacility>(
      EipMFacility.class);
    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   * 
   * @param obj
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getListData(java.lang.Object)
   */
  @Override
  protected Object getResultData(EipMFacility record) {
    try {
      FacilityResultData rd = new FacilityResultData();
      rd.initField();
      rd.setFacilityId(record.getFacilityId().longValue());
      rd.setFacilityName(record.getFacilityName());
      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 詳細データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectDetail(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  @Override
  public EipMFacility selectDetail(RunData rundata, Context context) {
    return FacilitiesUtils.getEipMFacility(rundata, context);
  }

  /**
   * ResultData に値を格納して返します。（詳細データ） <BR>
   * 
   * @param obj
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultDataDetail(java.lang.Object)
   */
  @Override
  protected Object getResultDataDetail(EipMFacility record) {
    try {
      FacilityResultData rd = new FacilityResultData();
      rd.initField();
      rd.setFacilityName(record.getFacilityName());
      rd.setFacilityId(record.getFacilityId().longValue());
      rd.setNote(record.getNote());
      rd.setCreateDate(ALDateUtil.format(record.getCreateDate(), "yyyy年M月d日"));
      rd.setUpdateDate(ALDateUtil.format(record.getUpdateDate(), "yyyy年M月d日"));
      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 施設の総数を返す． <BR>
   * 
   * @return
   */
  public int getFacilitySum() {
    return facilitySum;
  }

  /**
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getColumnMap()
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("facility_name", EipMFacility.FACILITY_NAME_PROPERTY);
    return map;
  }

  /**
   * 
   * @param id
   * @return
   */
  public boolean isMatch(int id1, long id2) {
    return id1 == (int) id2;
  }

}

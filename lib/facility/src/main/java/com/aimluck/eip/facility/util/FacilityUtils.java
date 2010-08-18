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
package com.aimluck.eip.facility.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.facility.beans.FacilityLiteBean;
import com.aimluck.eip.orm.DatabaseOrmService;

/**
 * 施設のユーティリティクラスです。 <br />
 * 
 */
public class FacilityUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FacilityUtils.class.getName());

  public static List<FacilityLiteBean> getFacilityLiteBeans() {
    List<FacilityLiteBean> facilityAllList = new ArrayList<FacilityLiteBean>();

    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
        .getDataContext();
      SelectQuery query = new SelectQuery(EipMFacility.class);
      query.addOrdering(EipMFacility.FACILITY_NAME_PROPERTY, true);
      List<?> aList = dataContext.performQuery(query);

      int size = aList.size();
      for (int i = 0; i < size; i++) {
        EipMFacility record = (EipMFacility) aList.get(i);
        FacilityLiteBean bean = new FacilityLiteBean();
        bean.initField();
        bean.setFacilityId(record.getFacilityId().longValue());
        bean.setFacilityName(record.getFacilityName());
        facilityAllList.add(bean);
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
    }
    return facilityAllList;
  }
}

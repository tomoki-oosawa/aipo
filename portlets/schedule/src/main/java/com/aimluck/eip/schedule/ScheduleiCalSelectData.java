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
package com.aimluck.eip.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.VEipTScheduleList;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class ScheduleiCalSelectData extends ScheduleSearchSelectData {

  /** <code>logger</code> logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleiCalSelectData.class.getName());

  private final Map<Integer, List<ScheduleSearchResultData>> dummyMaps =
    new HashMap<Integer, List<ScheduleSearchResultData>>();

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

  }

  @Override
  protected ResultList<VEipTScheduleList> getScheduleList(RunData rundata,
      Context context) {

    List<Integer> tmpUsers = new ArrayList<Integer>();
    tmpUsers.add(ALEipUtils.getUserId(rundata));
    List<Integer> tmpFacilities = new ArrayList<Integer>();

    // 前後3ヶ月の予定を取得
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MONTH, -3);
    Date startDate = cal.getTime();
    cal.add(Calendar.MONTH, 6);
    Date endDate = cal.getTime();

    return (ResultList<VEipTScheduleList>) ScheduleUtils.getScheduleList(
      ALEipUtils.getUserId(rundata),
      startDate,
      endDate,
      tmpUsers,
      tmpFacilities,
      true);
  }

  @Override
  protected Object getResultData(VEipTScheduleList record)
      throws ALPageNotFoundException, ALDBErrorException {
    ScheduleSearchResultData result =
      (ScheduleSearchResultData) super.getResultData(record);

    if ("D".equals(record.getStatus())) {
      if (dummyMaps.containsKey(record.getParentId())) {
        List<ScheduleSearchResultData> list =
          dummyMaps.get(record.getParentId());
        list.add(result);
        dummyMaps.put(record.getParentId(), list);
        return null;
      } else {
        List<ScheduleSearchResultData> list =
          new ArrayList<ScheduleSearchResultData>();
        list.add(result);
        dummyMaps.put(record.getParentId(), list);
        return null;
      }
    } else {
      return result;
    }
  }

  public Map<Integer, List<ScheduleSearchResultData>> getDummyMaps() {
    return dummyMaps;
  }
}

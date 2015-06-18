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
package com.aimluck.eip.timecard;

import java.util.jar.Attributes;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTTimecardSettings;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.timecard.util.TimecardUtils;

/**
 * タイムカード集計の検索データを管理するためのクラスです。
 * 
 */
public class TimecardSettingsSelectData extends
    ALAbstractSelectData<EipTTimecardSettings, EipTTimecardSettings> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(TimecardSettingsSelectData.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<EipTTimecardSettings> selectList(RunData rundata,
      Context context) {
    return null;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected EipTTimecardSettings selectDetail(RunData rundata, Context context) {
    return TimecardUtils.getEipTTimecardSettings(rundata, context);
  }

  /**
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipTTimecardSettings obj) {
    return null;
  }

  /**
   * 
   * @param record
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTTimecardSettings record) {
    try {
      TimecardSettingsResultData rd = new TimecardSettingsResultData();
      rd.initField();
      rd.setTimecardSettingsId(record.getTimecardSettingsId().intValue());
      rd.setStartTime(record.getStartHour()
        + "時"
        + record.getStartMinute()
        + "分");
      rd.setEndTime(record.getEndHour() + "時" + record.getEndMinute() + "分");
      rd.setWorktimeIn(record.getWorktimeIn().intValue());
      rd.setWorktimeOut(record.getWorktimeOut().intValue());
      rd.setResttimeIn(record.getResttimeIn().intValue());
      rd.setResttimeOut(record.getResttimeOut().intValue());

      return rd;
    } catch (Exception ex) {
      logger.error("timecard", ex);
      return null;
    }
  }

  /**
   *
   */
  @Override
  protected Attributes getColumnMap() {
    return null;
  }

}

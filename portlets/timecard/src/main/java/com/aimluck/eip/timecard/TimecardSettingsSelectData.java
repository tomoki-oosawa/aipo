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
package com.aimluck.eip.timecard;

import java.util.List;
import java.util.jar.Attributes;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTTimecardSettings;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.timecard.util.TimecardUtils;

/**
 * タイムカード集計の検索データを管理するためのクラスです。 <br />
 * 
 */
public class TimecardSettingsSelectData extends
    ALAbstractSelectData<EipTTimecardSettings, EipTTimecardSettings> {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(TimecardSettingsSelectData.class.getName());

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectList(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  @Override
  protected List<EipTTimecardSettings> selectList(RunData rundata,
      Context context) {
    return null;
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectDetail(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  @Override
  protected EipTTimecardSettings selectDetail(RunData rundata, Context context) {
    return TimecardUtils.getEipTTimecardSettings(rundata, context);
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultData(java.lang.Object)
   */
  @Override
  protected Object getResultData(EipTTimecardSettings obj) {
    return null;
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultDataDetail(java.lang.Object)
   */
  @Override
  protected Object getResultDataDetail(EipTTimecardSettings record) {
    try {
      TimecardSettingsResultData rd = new TimecardSettingsResultData();
      rd.initField();
      rd.setTimecardSettingsId(record.getTimecardSettingsId().intValue());
      rd.setStartTime(record.getStartHour() + "時" + record.getStartMinute()
        + "分");
      rd.setEndTime(record.getEndHour() + "時" + record.getEndMinute() + "分");
      rd.setWorktimeIn(record.getWorktimeIn().intValue());
      rd.setWorktimeOut(record.getWorktimeOut().intValue());
      rd.setResttimeIn(record.getResttimeIn().intValue());
      rd.setResttimeOut(record.getResttimeOut().intValue());

      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getColumnMap()
   */
  @Override
  protected Attributes getColumnMap() {
    return null;
  }

}

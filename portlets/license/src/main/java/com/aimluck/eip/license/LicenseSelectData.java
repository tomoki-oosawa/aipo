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
package com.aimluck.eip.license;

import java.util.jar.Attributes;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.account.AipoLicense;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.license.util.LicenseUtils;
import com.aimluck.eip.orm.query.ResultList;

/**
 */
public class LicenseSelectData extends
    ALAbstractSelectData<AipoLicense, AipoLicense> {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(LicenseSelectData.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<AipoLicense> selectList(RunData rundata, Context context) {
    return null;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected AipoLicense selectDetail(RunData rundata, Context context) {
    return LicenseUtils.getAipoLicense(rundata, context);
  }

  /**
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(AipoLicense obj) {
    return null;
  }

  /**
   * 
   * @param license
   * @return
   */
  @Override
  protected Object getResultDataDetail(AipoLicense license) {

    return license.getLicense();
  }

  /**
   * 
   * @return
   */
  @Override
  protected Attributes getColumnMap() {
    return null;
  }

}

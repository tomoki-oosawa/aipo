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
package com.aimluck.eip.system;

import java.util.jar.Attributes;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.orm.query.ResultList;

/**
 *
 */
public class SystemVersionSelectData extends
    ALAbstractSelectData<String, String> {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SystemVersionSelectData.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<String> selectList(RunData rundata, Context context) {
    return null;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected String selectDetail(RunData rundata, Context context) {
    return JetspeedResources.getString("aipo.version", "");
  }

  /**
   * 
   * @param license
   * @return
   */
  @Override
  protected Object getResultData(String license) {
    return null;
  }

  /**
   * 
   * @param license
   * @return
   */
  @Override
  protected Object getResultDataDetail(String license) {
    return license;
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

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
package com.aimluck.eip.mygroup.util;

import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.util.ALEipUtils;

/**
 * マイグループのユーティリティクラスです。 <BR>
 * 
 */
public class MyGroupUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MyGroupUtils.class.getName());

  public static final String MYGROUP_PORTLET_NAME = "MyGroup";

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static TurbineGroup getGroup(RunData rundata, Context context) {

    String id = ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);

    try {
      if (id == null) {
        logger.debug("Empty ID...");
        return null;
      }

      return (TurbineGroup) JetspeedSecurity.getGroup(id);
    } catch (Exception ex) {
      logger.error("mygroup", ex);
      return null;
    }
  }

}

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
package com.aimluck.eip.modules.actions;

import org.apache.jetspeed.modules.actions.JetspeedAccessController;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.common.ALConstants;
import com.aimluck.eip.util.ALSessionUtils;

/**
 * Calls the profiler to load the requested PSML resource based on request
 * params Its necessary to load the profile from this action, not the
 * SessionValidator in order to get the cached ACL list from logon
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: JetspeedAccessController.java,v 1.10 2004/02/23 02:59:06 jford
 *          Exp $
 */

public class ALAccessController extends JetspeedAccessController {

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALAccessController.class.getName());

  @Override
  public void doPerform(RunData data) throws Exception {

    if (ALSessionUtils.isImageRequest(data)) {
      return;
    }

    super.doPerform(data);

    if (checkDBError(data)) {
      // JetspeedAccessController set to ShowError.vm, reset to DBError.vm
      data.setScreenTemplate(ALConstants.DB_ERROR_TEMPLATE);
    }
  }

  private boolean checkDBError(RunData data) {
    String message = data.getMessage();
    if (null != message && message.indexOf(ALConstants.DB_ERROR_PROFILE) != -1) {
      return true;
    }
    return false;
  }

}
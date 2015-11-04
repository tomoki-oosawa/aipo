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
package com.aimluck.eip.modules.actions.controls;

// Turbine stuff
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.modules.Action;
import org.apache.turbine.util.RunData;

/**
 * Change the state of a portlet to maximized. This setting is not persistent.
 * Since the maximized state affects the whole screen, this action redirects the
 * user to a new template and store the state to go to when clicking on restore
 * 
 * @author <a href="mailto:re_carrasco@bco011.sonda.cl">Roberto Carrasco </a>
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta </a>
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer </a>
 */
public class Maximize extends Action {

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(Maximize.class.getName());

  /**
   * @param rundata
   *          The RunData object for the current request
   */
  @Override
  public void doPerform(RunData rundata) throws Exception {
    // Only logged in users can maximize
    if (rundata.getUser() == null) {
      return;
    }
    // Get jsp_peid parmameter. If it does not exist, then do nothing
    String peid = rundata.getParameters().getString("js_peid");
    if (peid == null) {
      // // error redirect
      // rundata.setRedirectURI("/aipo/portal");
      return;
    }

    // Get the Portlet using the PSML document and the PEID
    JetspeedRunData jdata = (JetspeedRunData) rundata;

    if (jdata.getProfile() == null) {
      logger.warn("Failed to get Profile entry for User ("
        + rundata.getUser().getName()
        + ")");
      return;
    }

    // Get the Portlet using the PSML document and the PEID
    Entry entry = jdata.getProfile().getDocument().getEntryById(peid);
    if (entry == null) {
      logger.warn("Failed to get PEID ("
        + peid
        + ") entry for User ("
        + rundata.getUser().getName()
        + ")");

      // error redirect
      jdata.getUser().removeTemp("js_peid");
      rundata.setRedirectURI("/aipo/portal");

      return;
    }

    // record that this portlet is now maximized
    jdata.getUser().setTemp("js_peid", peid);

    jdata.setCustomized(null);

  }
}

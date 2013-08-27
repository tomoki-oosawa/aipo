/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

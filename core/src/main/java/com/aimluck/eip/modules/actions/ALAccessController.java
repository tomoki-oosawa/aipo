/*
 * Copyright 2000-2004 The Apache Software Foundation.
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

package com.aimluck.eip.modules.actions;

import org.apache.jetspeed.modules.actions.JetspeedAccessController;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

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
  }

}
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
package com.aimluck.eip.modules.parameters;

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

// Java stuff
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.jetspeed.modules.actions.portlets.PsmlManagerAction;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.QueryLocator;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.util.PortletUtils;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * Returns list box control populated with pmsl pages for current user for
 * current media type.
 * 
 * @author <a href="morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: MyPagesListBox.java,v 1.5 2004/02/23 03:01:20 jford Exp $
 */

public class MyPagesListBox extends VelocityParameterPresentationStyle {

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MyPagesListBox.class.getName());

  /**
   * Put custom objects in the velocity context
   * 
   * @param data
   * @param name
   * @param value
   * @param parms
   * @param context
   */
  @SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
  @Override
  public void buildContext(RunData rundata, String name, String value,
      Map parms, Context context) {
    Map entries = null;

    try {
      entries = (Map) rundata.getUser().getTemp(PsmlManagerAction.CACHED_PSML);
      if (entries == null) {
        // Initialize the query locator
        QueryLocator ql = new QueryLocator(QueryLocator.QUERY_USER);

        JetspeedRunData jdata = (JetspeedRunData) rundata;
        ql.setUser(jdata.getJetspeedUser());
        // ql.setMediaType(jdata.getCapability().getPreferredMediaType());
        entries = new Hashtable();
        Iterator i = Profiler.query(ql);
        while (i.hasNext()) {
          Profile profile = (Profile) i.next();

          String mediaType = profile.getMediaType();
          if (mediaType != null
            && mediaType.equals(jdata.getCapability().getPreferredMediaType())) {
            if (PortletUtils.canAccessProfile(rundata, profile)) {
              if (logger.isDebugEnabled()) {
                logger.debug("MyPagesListBox: refreshing user profile list: "
                  + profile.getPath());
              }
              String title = profile.getName();
              if (profile.getRootSet() != null
                && profile.getRootSet().getTitle() != null) {
                title = profile.getRootSet().getTitle();
              }
              entries.put(profile, title);
            }
          }
        }

        rundata.getUser().setTemp(PsmlManagerAction.CACHED_PSML, entries);
      }

      context.put("pages", entries);
    } catch (Exception e) {
      logger.error("MyPagesListBox.buildContext", e);
    }

  }

}
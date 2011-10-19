/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

package com.aimluck.eip.activity.util;

import java.util.Iterator;

import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.util.RunData;

/**
 *
 */
public class ActivityUtils {

  public static final String ACTIVITY_PORTLET_NAME = "Activity";

  public static String getGlobalPortletId(RunData rundata) {

    Portlets portlets =
      ((JetspeedRunData) rundata).getProfile().getDocument().getPortlets();
    if (portlets == null) {
      return null;
    }

    @SuppressWarnings("unchecked")
    Iterator<Entry> iterator = portlets.getEntriesIterator();
    while (iterator.hasNext()) {
      Entry next = iterator.next();
      if ("Activity".equals(next.getParent())) {
        return next.getId();
      }
    }
    return null;
  }
}

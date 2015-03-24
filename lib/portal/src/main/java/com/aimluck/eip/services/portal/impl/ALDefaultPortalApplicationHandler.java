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
package com.aimluck.eip.services.portal.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.cayenne.om.account.EipMInactiveApplication;
import com.aimluck.eip.http.HttpServletRequestLocator;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.portal.ALPortalApplicationHandler;

/**
 *
 */
public class ALDefaultPortalApplicationHandler extends
    ALPortalApplicationHandler {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALDefaultPortalApplicationHandler.class.getName());

  private static ALPortalApplicationHandler instance;

  public static final String KEY_INACTIVE_APPLICATIONS =
    "com.aipo.portal.inactive.applications";

  public static ALPortalApplicationHandler getInstance() {
    if (instance == null) {
      instance = new ALDefaultPortalApplicationHandler();
    }
    return instance;
  }

  /**
   * @param portletName
   * @return
   */
  @Override
  public boolean isActive(String portletName) {
    List<String> list = getInactiveApplicationList();
    return !list.contains(portletName);
  }

  protected List<String> getInactiveApplicationList() {
    HttpServletRequest request = HttpServletRequestLocator.get();
    List<String> result = null;
    if (request != null) {
      try {
        Object attr = request.getAttribute(KEY_INACTIVE_APPLICATIONS);
        if (attr != null) {
          result = explode((String) attr, ",");
        }
      } catch (Throwable t) {
        logger.warn("[ALDefaultPortalApplicationHandler]", t);
      }
    }
    if (result == null) {
      result = getInactiveApplicationListByQuery();
    }
    return result;
  }

  protected List<String> getInactiveApplicationListByQuery() {
    List<EipMInactiveApplication> list =
      Database.query(EipMInactiveApplication.class).fetchList();

    List<String> result = new ArrayList<String>();
    for (EipMInactiveApplication model : list) {
      result.add(model.getName());
    }

    // Create Cache
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      try {
        String value = implode(result, ",");
        request.setAttribute(KEY_INACTIVE_APPLICATIONS, value);
      } catch (Throwable t) {
        logger.warn("[ALDefaultPortalApplicationHandler]", t);
      }
    }
    return result;
  }

  protected List<String> explode(String s, String separator) {
    StringTokenizer st = new StringTokenizer(s, separator);
    List<String> v = new ArrayList<String>();
    for (; st.hasMoreTokens();) {
      v.add(st.nextToken());
    }
    return v;
  }

  protected String implode(List<String> array, String separator) {
    StringBuilder out = new StringBuilder();
    boolean first = true;
    for (String v : array) {
      if (first) {
        first = false;
      } else {
        out.append(separator);
      }
      out.append(v);
    }
    return out.toString();
  }

}

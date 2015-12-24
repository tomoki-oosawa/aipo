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
package com.aimluck.eip.workflow;

import java.util.StringTokenizer;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALLocalizationUtils;
import com.aimluck.eip.workflow.util.WorkflowUtils;

/**
 * ワークフロー申請経路のResultDataです。<BR>
 * 
 */
public class WorkflowRouteResultData implements ALData {

  /** 申請経路ID */
  protected ALNumberField route_id;

  /** 申請経路名 */
  protected ALStringField route_name;

  /** 申請経路 */
  protected ALStringField route;

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WorkflowUtils.class.getName());

  /**
   *
   *
   */
  @Override
  public void initField() {
    route_id = new ALNumberField();
    route_name = new ALStringField();
    route = new ALStringField();
  }

  /**
   * @return
   */
  public ALStringField getRouteName() {
    return route_name;
  }

  /**
   * @param string
   */
  public void setRouteName(String string) {
    route_name.setValue(string);
  }

  /**
   * @return
   */
  public ALNumberField getRouteId() {
    return route_id;
  }

  /**
   * @param i
   */
  public void setRouteId(long i) {
    route_id.setValue(i);
  }

  /**
   * @return
   */
  public ALStringField getRoute() {
    return route;
  }

  /**
   * @param string
   */
  public void setRoute(String string) {
    route.setValue(string);
  }

  public String getRouteV() {
    try {
      StringBuffer routeun = new StringBuffer();
      String username, next_token;

      StringTokenizer st = new StringTokenizer(route.getValue(), ",");
      int routenumber = 1;
      while (st.hasMoreTokens()) {
        next_token = st.nextToken();
        if (!next_token.equals("")) {
          username = ALStringUtil.sanitizing(WorkflowUtils.getName(next_token));
          routeun.append(routenumber);
          routeun.append(". ");
          routeun.append(username);
          routeun.append("<br />");
          routenumber++;
        }
      }

      return routeun.toString();
    } catch (Exception ex) {
      logger.error("[WorkflowRouteResultData]", ex);
      return null;
    }
  }

  public String getRouteH() {
    try {
      StringBuffer routeun = new StringBuffer();
      String username, next_token;

      StringTokenizer st = new StringTokenizer(route.getValue(), ",");
      while (st.hasMoreTokens()) {
        next_token = st.nextToken();
        if (!next_token.equals("")) {
          username = ALStringUtil.sanitizing(WorkflowUtils.getName(next_token));
          routeun.append(username);
          routeun.append(" -> ");
        }
      }
      routeun.append(ALLocalizationUtils.getl10n("WORKFLOW_COMPLETION"));

      return routeun.toString();
    } catch (Exception ex) {
      logger.error("[WorkflowRouteResultData]", ex);
      return null;
    }
  }
}

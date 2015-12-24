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
package com.aimluck.eip.modules.actions.externalsearch;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 検索窓のアクションクラスです。 
 * 
 */
public class ExternalSearchAction extends ALBaseAction {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ExternalSearchAction.class.getName());

  /**
   * 通常表示の際の処理を記述します。 
   * 
   * @param portlet
   * @param context
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {
    doExternalsearch_list(rundata, context);
  }

  /**
   * 最大化表示の際の処理を記述します。 
   * 
   * @param portlet
   * @param context
   * @param rundata
   */
  @Override
  protected void buildMaximizedContext(VelocityPortlet portlet,
      Context context, RunData rundata) {
    doViewPage(rundata, context, "externalsearch-list");
  }

  /**
   * 検索窓を表示します。 
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doExternalsearch_list(RunData rundata, Context context)
      throws Exception {
    doViewPage(rundata, context, "externalsearch");
  }

  private void doViewPage(RunData rundata, Context context, String vm_template) {
    String def_searchengine =
      ALEipUtils
        .getPortlet(rundata, context)
        .getPortletConfig()
        .getInitParameter("p1a-selects", "");
    if (!"".equals(def_searchengine)) {
      context.put("def_searchengine", def_searchengine);
    }
    setTemplate(rundata, vm_template);
  }

}

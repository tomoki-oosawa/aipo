/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.modules.actions.webpage;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.webpage.WebPageSelectData;

/**
 * WebPageのアクションクラスです。<BR>
 *
 */
public class WebPageAction extends ALBaseAction {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(WebPageAction.class.getName());

  /**
   * 通常表示の際の処理を記述します。<BR>
   *
   * @param portlet
   * @param context
   * @param rundata
   * @throws java.lang.Exception
   * @see org.apache.jetspeed.modules.actions.portlets.VelocityPortletAction#buildNormalContext(org.apache.jetspeed.portal.portlets.VelocityPortlet,
   *      org.apache.velocity.context.Context, org.apache.turbine.util.RunData)
   */
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {
    WebPageSelectData listData = new WebPageSelectData();
    listData.doViewDetail(this, rundata, context);
    setTemplate(rundata, "webpage");
  }

  /**
   *
   * @param portlet
   * @param context
   * @param rundata
   * @see org.apache.jetspeed.modules.actions.portlets.VelocityPortletAction#buildMaximizedContext(org.apache.jetspeed.portal.portlets.VelocityPortlet,
   *      org.apache.velocity.context.Context, org.apache.turbine.util.RunData)
   */
  protected void buildMaximizedContext(VelocityPortlet portlet,
      Context context, RunData rundata) {
    WebPageSelectData listData = new WebPageSelectData();
    listData.doViewDetail(this, rundata, context);
    setTemplate(rundata, "webpage-list");
  }

}

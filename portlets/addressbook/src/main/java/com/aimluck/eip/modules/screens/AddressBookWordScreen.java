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
package com.aimluck.eip.modules.screens;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.addressbook.AddressBookWordSelectData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アドレス帳検索ボックスを処理するクラスです。 <br />
 * 
 */
public class AddressBookWordScreen extends ALVelocityScreen {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(AddressBookWordScreen.class.getName());

  private String mode = null;

  private String currentTab = null;

  /**
   * @see org.apache.turbine.modules.screens.RawScreen#doOutput(org.apache.turbine.util.RunData)
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
    mode = rundata.getParameters().getString(ALEipConstants.MODE);
    try {
      if ("ajaxsearch".equals(mode)) {
        AddressBookWordSelectData listData = new AddressBookWordSelectData();
        listData.setRowsNum(Integer.parseInt(ALEipUtils.getPortlet(rundata,
            context).getPortletConfig().getInitParameter("p1a-rows")));
        listData.setStrLength(Integer.parseInt(ALEipUtils.getPortlet(rundata,
            context).getPortletConfig().getInitParameter("p3a-strlen")));
        listData.doViewList(this, rundata, context);
        listData.loadGroups(rundata, context);
        // 現在のタブによって処理を分岐
        currentTab = ALEipUtils.getTemp(rundata, context, "tab");
        if (currentTab == null || currentTab.trim().length() == 0
            || "syagai".equals(currentTab)) {
          setTemplate(rundata, context, getLayoutTemplate());
        } else {
          setTemplate(rundata, context, getLayoutTemplate());
        }
      } else {

        // mode指定がないとき
        AddressBookWordSelectData listData = new AddressBookWordSelectData();
        listData.setRowsNum(Integer.parseInt(portlet.getPortletConfig()
            .getInitParameter("p1a-rows")));
        listData.setStrLength(Integer.parseInt(ALEipUtils.getPortlet(rundata,
            context).getPortletConfig().getInitParameter("p3a-strlen")));
        listData.doViewList(this, rundata, context);

        String layout_template = getLayoutTemplate();
        setTemplate(rundata, context, layout_template);
      }

    } catch (Exception ex) {
      logger.error("[AddressBookWordScreen] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  protected String getLayoutTemplate() {
    String layout_template = null;
    // if ("ajaxsearch".equals(mode)) {
    if (currentTab == null || currentTab.trim().length() == 0
        || "syagai".equals(currentTab)) {
      layout_template = "portlets/html/ja/ajax-addressbook-list.vm";
    } else {
      layout_template = "portlets/html/ja/ajax-addressbook-corplist.vm";
    }
    // } else {
    // if (currentTab == null || currentTab.trim().length() == 0
    // || "syagai".equals(currentTab)) {
    // layout_template = "portlets/html/ja/ajax-addressbook-list.vm";
    // } else {
    // layout_template = "portlets/html/ja/ajax-addressbook-corplist.vm";
    // }
    // }

    return layout_template;
  }
}

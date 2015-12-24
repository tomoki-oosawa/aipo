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
package com.aimluck.eip.modules.screens;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.addressbook.AbstractAddressBookWordSelectData;
import com.aimluck.eip.addressbook.AddressBookFilterdSelectData;
import com.aimluck.eip.addressbook.util.AddressBookUtils;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アドレス帳検索ボックスを処理するクラスです。
 * 
 */
public class AddressBookWordScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AddressBookWordScreen.class.getName());

  private String mode = null;

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
    mode = rundata.getParameters().getString(ALEipConstants.MODE);
    try {
      if ("ajaxsearch".equals(mode)) {
        AbstractAddressBookWordSelectData<?, ?> listData =
          AbstractAddressBookWordSelectData.createAddressBookWordSelectData(
            rundata,
            context);

        listData.setRowsNum(Integer.parseInt(ALEipUtils.getPortlet(
          rundata,
          context).getPortletConfig().getInitParameter("p1a-rows")));
        listData.setStrLength(Integer.parseInt(ALEipUtils.getPortlet(
          rundata,
          context).getPortletConfig().getInitParameter("p3a-strlen")));
        listData.doViewList(this, rundata, context);
        listData.loadGroups(rundata, context);

        setTemplate(rundata, context, listData.getTemplateFilePath());
      } else {
        AddressBookFilterdSelectData listData =
          new AddressBookFilterdSelectData();
        listData.setRowsNum(Integer.parseInt(portlet
          .getPortletConfig()
          .getInitParameter("p1b-rows")));
        listData.setStrLength(Integer.parseInt(ALEipUtils.getPortlet(
          rundata,
          context).getPortletConfig().getInitParameter("p3a-strlen")));
        listData.doViewList(this, rundata, context);
        listData.loadGroups(rundata, context);

        setTemplate(
          rundata,
          context,
          "portlets/html/ajax-addressbook-list.vm");
      }
    } catch (Exception ex) {
      logger.error("AddressBookWordScreen.doOutput", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return AddressBookUtils.ADDRESSBOOK_PORTLET_NAME;
  }
}

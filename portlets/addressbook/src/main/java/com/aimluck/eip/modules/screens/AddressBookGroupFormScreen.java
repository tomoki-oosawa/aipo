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
package com.aimluck.eip.modules.screens;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.addressbook.AddressBookGroupFormData;
import com.aimluck.eip.addressbook.util.AddressBookUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アドレス帳グループを処理するクラスです。
 */
public class AddressBookGroupFormScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AddressBookGroupFormScreen.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    try {
      doAddressBook_form(rundata, context);
    } catch (Exception ex) {
      logger.error("AddressBookGroupFormScreen.doOutput", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  protected void doAddressBook_form(RunData rundata, Context context) {
    AddressBookGroupFormData formData = new AddressBookGroupFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);

    formData.loadAddresses(rundata, context);

    String layout_template = "portlets/html/ajax-addressbook-group-form.vm";
    setTemplate(rundata, context, layout_template);
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return AddressBookUtils.ADDRESSBOOK_PORTLET_NAME;
  }
}

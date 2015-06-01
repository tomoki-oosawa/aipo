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

import com.aimluck.eip.account.util.AccountUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ユーザーアカウントの詳細画面を処理するクラスです。 <br />
 * 
 */
public class AccountUserDetailMailSizeScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountUserDetailMailSizeScreen.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {
    try {

      //
      // AccountUserSelectData detailData = new AccountUserSelectData();
      // detailData.initField();
      // detailData.doViewDetail(this, rundata, context);
      //
      // String entityid =
      // ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
      // context.put(ALEipConstants.ENTITY_ID, entityid);

      Integer user_id =
        Integer.valueOf(rundata.getParameters().get("user_id").toString());
      context.put("mailSize", AccountUtils.getSizeStr(AccountUtils
        .getStorageSizeOfUserMail(user_id)));

      String layout_template =
        "portlets/html/ajax-account-user-detail-mailsize.vm";

      setTemplate(rundata, context, layout_template);
    } catch (Exception ex) {
      logger.error("AccountUserDetailMailSizeScreen.doOutput", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return AccountUtils.ACCOUNT_PORTLET_NAME;
  }

}

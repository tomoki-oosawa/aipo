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

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.webmail.WebMailSelectData;
import com.aimluck.eip.webmail.util.WebMailUtils;

/**
 * Webメールの一覧を処理するクラスです。 <br />
 * 
 */
public class WebMailListScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WebMailListScreen.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {
    String mode = rundata.getParameters().getString("mode");
    try {

      if ("recieve".equals(mode)) {
        // メニューからメール受信をクリックしてきた場合
        // メール受信開始状態にする
        ALEipUtils.setTemp(rundata, context, "start_recieve", "1");
        WebMailUtils.receiveMailsThread(rundata, context);
      }
      // if ("stoprecieving".equals(mode)) {
      // // メニューからメール受信をクリックしてきた場合
      // WebMailUtils.stopReceiveMailThread(Integer.parseInt(accountId));
      // }

      VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);

      // 受信フォルダもしくは送信フォルダに保存されているメールの一覧を表示する．
      WebMailSelectData listData = new WebMailSelectData();
      listData.initField();
      listData.loadMailAccountList(rundata, context);
      listData.setRowsNum(Integer.parseInt(portlet
        .getPortletConfig()
        .getInitParameter("p1b-rows")));
      listData.setStrLength(0);
      listData.doViewList(this, rundata, context);

      setTemplate(rundata, context, "portlets/html/ajax-webmail-list.vm");
    } catch (Exception ex) {
      logger.error("[WebMailListScreen] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return WebMailUtils.WEBMAIL_PORTLET_NAME;
  }

}

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
package com.aimluck.eip.modules.actions.webmail;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.webmail.WebMailAccountSelectData;
import com.aimluck.eip.webmail.WebMailFilterSelectData;
import com.aimluck.eip.webmail.WebMailSelectData;
import com.aimluck.eip.webmail.util.WebMailUtils;

/**
 * Webメールの取り扱いに関するアクションクラスです。 <br />
 */
public class WebMailAction extends ALBaseAction {
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(WebMailAction.class.getName());

  /**
   * 
   * @param portlet
   * @param context
   * @param rundata
   * @throws Exception
   * @see org.apache.jetspeed.modules.actions.portlets.VelocityPortletAction#buildNormalContext(org.apache.jetspeed.portal.portlets.VelocityPortlet,
   *      org.apache.velocity.context.Context, org.apache.turbine.util.RunData)
   */
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {

    // セッション情報をクリアする
    clearWebMailSession(rundata, context);

    ALEipUtils.setTemp(rundata, context, ALEipConstants.ENTITY_ID, ALEipUtils
        .getPortlet(rundata, context).getPortletConfig().getInitParameter(
            "p3a-accounts").trim());

    WebMailSelectData listData = new WebMailSelectData();
    listData.initField();
    listData.loadMailAccountList(rundata, context);
    listData.setRowsNum(Integer.parseInt(portlet.getPortletConfig()
        .getInitParameter("p1a-rows")));
    listData.setStrLength(Integer.parseInt(portlet.getPortletConfig()
        .getInitParameter("p4a-strlen")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "webmail");
  }

  /**
   * 
   * @param portlet
   * @param context
   * @param rundata
   */
  protected void buildMaximizedContext(VelocityPortlet portlet,
      Context context, RunData rundata) {
    // MODEを取得
    String mode = rundata.getParameters().getString(ALEipConstants.MODE);

    try {
      if (ALEipConstants.MODE_LIST.equals(mode)) {
        doWebmail_list(rundata, context);
      }

      if (getMode() == null) {
        doWebmail_list(rundata, context);
      }
    } catch (Exception e) {
      logger.error("Exception", e);
    }
  }

  /**
   * 受信フォルダもしくは送信フォルダに保存されているメールの一覧を表示する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWebmail_list(RunData rundata, Context context) throws Exception {
    VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);

    WebMailSelectData listData = new WebMailSelectData();
    listData.initField();
    listData.loadMailAccountList(rundata, context);
    listData.setRowsNum(Integer.parseInt(portlet.getPortletConfig()
        .getInitParameter("p1b-rows")));
    listData.setStrLength(Integer.parseInt(portlet.getPortletConfig()
        .getInitParameter("p4a-strlen")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "webmail-list");
  }

  /**
   * POP3 サーバからメールを受信し，受信トレイを表示する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWebmail_receive_mails(RunData rundata, Context context)
      throws Exception {
    // メールを受信する．
    WebMailUtils.receiveMailsThread(rundata, context);

    // 受信トレイにフォーカスを移す．
    ALEipUtils.setTemp(rundata, context, "tab", WebMailUtils.TAB_RECEIVE);
    doWebmail_list(rundata, context);
  }

  /**
   * アカウントの一覧を表示する．
   * 
   * @param context
   * @param rundata
   * @param isMaximized
   * @param rowCount
   * @throws Exception
   */
  public void doWebmail_account_list(RunData rundata, Context context)
      throws Exception {
    VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);

    WebMailAccountSelectData listData = new WebMailAccountSelectData();
    listData.initField();
    listData.setRowsNum(Integer.parseInt(portlet.getPortletConfig()
        .getInitParameter("p1c-rows")));
    listData.setStrLength(Integer.parseInt(portlet.getPortletConfig()
        .getInitParameter("p4a-strlen")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "webmail-account-list");
  }

  /**
   * フィルタ一覧を表示する．
   *
   * @param context
   * @param rundata
   * @param isMaximized
   * @param rowCount
   * @throws Exception
   */
  public void doWebmail_filter_list(RunData rundata, Context context)
      throws Exception {
    VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);

    WebMailFilterSelectData listData = new WebMailFilterSelectData();
    listData.initField();
    listData.loadMailAccountList(rundata, context);
    listData.setRowsNum(Integer.parseInt(portlet.getPortletConfig()
        .getInitParameter("p1c-rows")));
    listData.setStrLength(Integer.parseInt(portlet.getPortletConfig()
        .getInitParameter("p4a-strlen")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "webmail-filter-list");
  }

  private void clearWebMailSession(RunData rundata, Context context) {
    JetspeedRunData jdata = (JetspeedRunData) rundata;
    VelocityPortlet portlet = ((VelocityPortlet) context.get("portlet"));

    jdata.getUser().removeTemp("tr_pop3mail_recieve");
    jdata.getUser().removeTemp(
        new StringBuffer().append(portlet.getID()).append(
            "com.aimluck.eip.webmail.WebMailAccountSelectDatasort").toString());
  }
}

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
package com.aimluck.eip.modules.actions.webmail;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.webmail.CellWebMailAccountSelectData;
import com.aimluck.eip.webmail.WebMailFormData;
import com.aimluck.eip.webmail.WebMailSelectData;
import com.aimluck.eip.webmail.util.WebMailUtils;

/**
 * Webメールのアクションクラスです。
 * 
 */
public class CellWebMailAction extends WebMailAction {

  /** <code>logger</code> logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CellWebMailAction.class.getName());

  /**
   * 
   * @param portlet
   * @param context
   * @param rundata
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) {

  }

  /**
   * 
   * @param portlet
   * @param context
   * @param rundata
   */
  @Override
  protected void buildMaximizedContext(VelocityPortlet portlet,
      Context context, RunData rundata) {
    // MODEを取得
    String mode = rundata.getParameters().getString(ALEipConstants.MODE);
    try {
      if (ALEipConstants.MODE_FORM.equals(mode)) {
        // doWebmail_form(rundata, context);
      } else if (ALEipConstants.MODE_DETAIL.equals(mode)) {
        // doSchedule_detail(rundata, context);
      } else if (ALEipConstants.MODE_LIST.equals(mode)) {
        doWebmail_account_list(rundata, context);
      }
      if (getMode() == null) {
        doWebmail_menu(rundata, context);
      }
    } catch (Exception ex) {
      logger.error("[ScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * メールを作成するページを表示する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWebmail_create_mail(RunData rundata, Context context)
      throws Exception {
    ALEipUtils.removeTemp(rundata, context, ALEipConstants.ENTITY_ID);

    // ACCOUNT_ID の取得
    String accountId =
      rundata.getParameters().getString(WebMailUtils.ACCOUNT_ID);
    if (accountId != null) {
      // ACCOUNT_ID をセッションに登録する．
      ALEipUtils.setTemp(rundata, context, WebMailUtils.ACCOUNT_ID, accountId);
    }

    // showMailForm(rundata, context, WebMailFormData.TYPE_NEW_MAIL);

    showMailForm(rundata, context);
  }

  /**
   * 指定したメール種別に応じて，メールを作成するページを表示する．
   * 
   * @param rundata
   * @param context
   * @param mailType
   */
  // private void showMailForm(RunData rundata, Context context, int mailType) {
  private void showMailForm(RunData rundata, Context context) {
    WebMailFormData formData = new WebMailFormData();
    // formData.initField(mailType);
    formData.initField();
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "webmail-form");
  }

  /**
   * 指定したフォルダにあるメールの内容を表示する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWebmail_show_mail(RunData rundata, Context context)
      throws Exception {
    WebMailSelectData detailData = new WebMailSelectData();
    detailData.initField();
    detailData.loadMailAccountList(rundata, context);
    boolean result = detailData.doViewDetail(this, rundata, context);
    String mailIndex =
      rundata.getParameters().getString(ALEipConstants.ENTITY_ID);
    context.put(WebMailUtils.ACCOUNT_ID, ALEipUtils.getTemp(
      rundata,
      context,
      WebMailUtils.ACCOUNT_ID));
    context.put("currentTab", ALEipUtils.getTemp(rundata, context, "tab"));
    context.put(ALEipConstants.ENTITY_ID, mailIndex);
    context.put("hasError", !result);
    setTemplate(rundata, "webmail-detail");
  }

  /**
   * 受信したメールを表示する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWebmail_show_received_mails(RunData rundata, Context context)
      throws Exception {
    // ACCOUNT_ID のセット
    setAccountIdToSession(rundata, context);

    ALEipUtils.setTemp(rundata, context, "tab", WebMailUtils.TAB_RECEIVE);
    doWebmail_list(rundata, context);
  }

  /**
   * 送信したメールを表示する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWebmail_show_sent_mails(RunData rundata, Context context)
      throws Exception {
    // ACCOUNT_ID のセット
    setAccountIdToSession(rundata, context);

    ALEipUtils.setTemp(rundata, context, "tab", WebMailUtils.TAB_SENT);
    doWebmail_list(rundata, context);
  }

  /**
   * POP3 サーバからメールを受信し，受信トレイを表示する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  public void doWebmail_receive_mails(RunData rundata, Context context)
      throws Exception {
    setAccountIdToSession(rundata, context);

    // super.doWebmail_receive_mails(rundata, context);

    // メールを受信する．
    // if (WebMailUtils.isNewMessage(rundata, context)) {
    ALEipUtils.setTemp(rundata, context, "start_recieve", "1");
    WebMailUtils.receiveMailsThread(rundata, context);
    // }
    doWebmail_show_received_mails(rundata, context);
  }

  /**
   * リクエストに含まれる AccountID をセッションにセットする．
   * 
   * @param rundata
   * @param context
   */
  private void setAccountIdToSession(RunData rundata, Context context) {
    // ACCOUNT_ID の取得
    String accountId =
      rundata.getParameters().getString(WebMailUtils.ACCOUNT_ID);
    if (accountId != null) {
      // ACCOUNT_ID をセッションに登録する．
      ALEipUtils.setTemp(rundata, context, WebMailUtils.ACCOUNT_ID, accountId);
    }
  }

  /**
   * スケジュールのメニューを表示する．
   * 
   * @param rundata
   * @param context
   */
  public void doWebmail_menu(RunData rundata, Context context) {
    // ACCOUNT_ID をセッションから削除する．
    ALEipUtils.removeTemp(rundata, context, WebMailUtils.ACCOUNT_ID);
    ALEipUtils.setTemp(rundata, context, "WebMail_Normal", "true");

    // // 新着メールの最終確認日をセッションに追加する．
    // if (confirmNewmail) {
    // String lastTime = WebMailUtils.getNowTime();
    // ALEipUtils.setTemp(rundata, context, LAST_TIME, lastTime);
    // }
    // context.put(LAST_TIME, ALEipUtils.getTemp(rundata, context, LAST_TIME));

    int rowsNum =
      Integer.parseInt(ALEipUtils
        .getPortlet(rundata, context)
        .getPortletConfig()
        .getInitParameter("p1b-rows"));

    CellWebMailAccountSelectData listData = new CellWebMailAccountSelectData();
    listData.initField();
    listData.setRowsNum(rowsNum);
    listData.doViewList(this, rundata, context);

    setTemplate(rundata, "webmail-menu");
  }

  /**
   * メール一覧ページを表示させる.
   * 
   * @param rundata
   * @param context
   */
  public void doWebmail_pageview(RunData rundata, Context context)
      throws Exception {
    // 受信フォルダもしくは送信フォルダに保存されているメールの一覧を表示する．
    WebMailSelectData listData = new WebMailSelectData();
    listData.initField();
    listData.loadMailAccountList(rundata, context);

    // PSMLからパラメータをロードする
    // 最大表示件数（通常時）
    VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
    listData.setRowsNum(Integer.parseInt(portlet
      .getPortletConfig()
      .getInitParameter("p1b-rows")));
    listData.setStrLength(0);

    listData.doViewList(this, rundata, context);

    setTemplate(rundata, "webmail-list");
  }

  public void doWebmail_send_mail(RunData rundata, Context context)
      throws Exception {
    WebMailFormData formData = new WebMailFormData();
    formData.initField();
    if (formData.doInsert(this, rundata, context)) {
      doWebmail_list(rundata, context);
    } else {
      setTemplate(rundata, "webmail-form");
    }
  }

}

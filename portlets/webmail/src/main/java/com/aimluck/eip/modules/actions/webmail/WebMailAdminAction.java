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

import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.webmail.WebMailAccountFormData;
import com.aimluck.eip.webmail.WebMailAccountSelectData;
import com.aimluck.eip.webmail.WebMailAdminSettingsFormData;
import com.aimluck.eip.webmail.WebMailAdminSettingsSelectData;

/**
 * 管理者用メールアカウントの取り扱いに関するアクションクラスです。 <br />
 */
public class WebMailAdminAction extends ALBaseAction {

  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WebMailAdminAction.class.getName());

  /**
   * 
   * @param portlet
   * @param context
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {
    if (getMode() == null) {
      doWebmail_mailaccount_detail(rundata, context);
    }
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
  }

  /**
   * メールアカウントのフォームを表示する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWebmail_mailaccount_form(RunData rundata, Context context)
      throws Exception {
    WebMailAccountFormData formData = new WebMailAccountFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "webmail-account-form-admin");
  }

  /**
   * メールアカウントを追加するフォームを表示する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWebmail_mailaccount_insert(RunData rundata, Context context)
      throws Exception {
    WebMailAccountFormData formData = new WebMailAccountFormData();
    rundata.getRequest().setAttribute(
      "account_name",
      ALOrgUtilsService.getAlias() + "システムメールアカウント");
    formData.initField();
    if (formData.doInsert(this, rundata, context)) {
      // データ登録が成功したとき
      doWebmail_mailaccount_detail(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(
      // jsLink
      // .getPortletById(ALEipUtils.getPortlet(rundata, context).getID())
      // .addQueryData("eventSubmit_doTodo_list", "1")
      // .toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    } else {
      setTemplate(rundata, "webmail-account-form-admin");
    }
  }

  /**
   * メールアカウントを更新するフォームを表示する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWebmail_mailaccount_update(RunData rundata, Context context)
      throws Exception {
    WebMailAccountFormData formData = new WebMailAccountFormData();
    formData.initField();
    if (formData.doUpdate(this, rundata, context)) {
      // データ更新が成功したとき
      doWebmail_mailaccount_detail(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doTodo_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    } else {
      setTemplate(rundata, "webmail-account-form-admin");
    }
  }

  /**
   * メールアカウントを削除する． <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWebmail_mailaccount_delete(RunData rundata, Context context)
      throws Exception {
    WebMailAccountFormData formData = new WebMailAccountFormData();
    formData.initField();
    if (formData.doDelete(this, rundata, context)) {
      // データ削除が成功したとき
      doWebmail_mailaccount_detail(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doTodo_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    }
  }

  /**
   * メールアカウントの詳細を表示する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWebmail_mailaccount_detail(RunData rundata, Context context)
      throws Exception {
    WebMailAccountSelectData detailData = new WebMailAccountSelectData();
    detailData.initField();
    detailData.doViewDetail(this, rundata, context);
    setTemplate(rundata, "webmail-account-detail-admin");
  }

  /**
   * メールアカウントのフォームを表示する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWebmail_mailaccount_form_option(RunData rundata, Context context)
      throws Exception {
    WebMailAdminSettingsFormData formData = new WebMailAdminSettingsFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "webmail-account-form-admin-option");
  }

  /**
   * メールアカウントの詳細を表示する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWebmail_mailaccount_detail_option(RunData rundata,
      Context context) throws Exception {
    WebMailAdminSettingsSelectData detailData =
      new WebMailAdminSettingsSelectData();
    detailData.initField();
    detailData.doViewDetail(this, rundata, context);
    setTemplate(rundata, "webmail-account-detail-admin-option");
  }

  /**
   * メールアカウントを更新するフォームを表示する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWebmail_mailaccount_update_option(RunData rundata,
      Context context) throws Exception {
    WebMailAdminSettingsFormData formData = new WebMailAdminSettingsFormData();
    formData.initField();
    if (formData.doUpdate(this, rundata, context)) {
      // データ更新が成功したとき
      doWebmail_mailaccount_detail_option(rundata, context);
    } else {
      setTemplate(rundata, "webmail-account-form-admin-option");
    }
  }
}

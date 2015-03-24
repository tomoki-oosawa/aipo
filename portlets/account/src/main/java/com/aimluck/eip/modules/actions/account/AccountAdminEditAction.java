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
package com.aimluck.eip.modules.actions.account;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.account.AccountPasswdFormData;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 管理者情報編集用アクションクラスです。
 */
public class AccountAdminEditAction extends ALBaseAction {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountAdminEditAction.class.getName());

  /**
   * @param portlet
   * @param context
   * @param rundata
   * @throws java.lang.Exception
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {
    if (getMode() == null) {
      doAccount_adminpasswd_form(rundata, context);
    }
  }

  /**
   * 管理者パスワード変更用のフォームを表示します。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_adminpasswd_form(RunData rundata, Context context)
      throws Exception {
    AccountPasswdFormData formData = new AccountPasswdFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "account-adminpasswd-form");
  }

  /**
   * 管理者パスワードの変更を行います。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_adminpasswd_update(RunData rundata, Context context)
      throws Exception {
    AccountPasswdFormData formData = new AccountPasswdFormData();
    formData.initField();
    if (formData.doUpdate(this, rundata, context)) {
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doAccount_return_adminpasswd_form",
        "1").toString()
        + "&success=true");
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      JetspeedLinkFactory.putInstance(jsLink);
      jsLink = null;
      setTemplate(rundata, "account-adminpasswd-form");
    } else {
      setTemplate(rundata, "account-adminpasswd-form");
    }
  }

  /**
   * 管理者パスワードの変更に成功した場合の画面遷移時に呼び出されるメソッドです。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_return_adminpasswd_form(RunData rundata, Context context)
      throws Exception {
    context.put("success", rundata.getParameters().getString("success"));
    AccountPasswdFormData formData = new AccountPasswdFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "account-adminpasswd-form");
  }
}

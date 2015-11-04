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
package com.aimluck.eip.modules.actions.account;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.account.AccountCompanyFormData;
import com.aimluck.eip.account.AccountPositionFormData;
import com.aimluck.eip.account.AccountPositionMultiDelete;
import com.aimluck.eip.account.AccountPositionSelectData;
import com.aimluck.eip.account.AccountPostFormData;
import com.aimluck.eip.account.AccountPostMultiDelete;
import com.aimluck.eip.account.AccountPostSelectData;
import com.aimluck.eip.modules.actions.common.ALSecureBaseAction;
import com.aimluck.eip.util.ALEipUtils;

/**
 * システム管理画面にて、会社情報を管理するアクションクラスです。
 * 
 */
public class AccountCompanyAction extends ALSecureBaseAction {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountCompanyAction.class.getName());

  /**
   * @param portlet
   * @param context
   * @param rundata
   * @throws java.lang.Exception
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {

    String chg_adminpasswd =
      JetspeedResources.getString("aipo.chg_adminpasswd", "false");

    if (ALEipUtils.getUserId(rundata) != 1) {
      // 管理者権限を持ったユーザーでは、管理者パスワードの変更メニューを表示しない。
      chg_adminpasswd = "false";
    }

    context.put("chg_adminpasswd", chg_adminpasswd);

    if (getTemplate(context).equals("account-position-list")) {
      doAccount_position_list(rundata, context);
    } else {
      doAccount_post_list(rundata, context);
    }

  }

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_company_form(RunData rundata, Context context)
      throws Exception {
    AccountCompanyFormData formData = new AccountCompanyFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "account-company-form");
  }

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_company_update(RunData rundata, Context context)
      throws Exception {

    AccountCompanyFormData formData = new AccountCompanyFormData();
    formData.initField();
    if (formData.doUpdate(this, rundata, context)) {
      // doAccount_list(rundata, context);
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doAccount_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      jsLink = null;
    } else {
      setTemplate(rundata, "account-company-form");
    }
  }

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_post_form(RunData rundata, Context context)
      throws Exception {
    AccountPostFormData formData = new AccountPostFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "account-post-form");
  }

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_post_insert(RunData rundata, Context context)
      throws Exception {
    AccountPostFormData formData = new AccountPostFormData();
    formData.initField();
    if (formData.doInsert(this, rundata, context)) {
      // doAccount_post_list(rundata, context);
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doAccount_post_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      jsLink = null;
    } else {
      setTemplate(rundata, "account-post-form");
    }
  }

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_post_delete(RunData rundata, Context context)
      throws Exception {
    AccountPostFormData formData = new AccountPostFormData();
    formData.initField();
    if (formData.doDelete(this, rundata, context)) {
      // doAccount_post_list(rundata, context);
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doAccount_post_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      jsLink = null;
    }
  }

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_post_update(RunData rundata, Context context)
      throws Exception {
    AccountPostFormData formData = new AccountPostFormData();
    formData.initField();
    if (formData.doUpdate(this, rundata, context)) {
      // doAccount_post_list(rundata, context);
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doAccount_post_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      jsLink = null;

    } else {
      setTemplate(rundata, "account-post-form");
    }
  }

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_post_list(RunData rundata, Context context)
      throws Exception {
    AccountPostSelectData listData = new AccountPostSelectData();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "account-post-list");
  }

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_post_detail(RunData rundata, Context context)
      throws Exception {
    AccountPostSelectData detailData = new AccountPostSelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      setTemplate(rundata, "account-post-detail");
    } else {
      doAccount_post_list(rundata, context);
    }
  }

  /**
   * 部署を削除します（複数） <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_post_multi_delete(RunData rundata, Context context)
      throws Exception {
    AccountPostMultiDelete delete = new AccountPostMultiDelete();
    delete.doMultiAction(this, rundata, context);
    JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
    rundata.setRedirectURI(jsLink.getPortletById(
      ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      "eventSubmit_doAccount_post_list",
      "1").toString());
    rundata.getResponse().sendRedirect(rundata.getRedirectURI());
    jsLink = null;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_position_form(RunData rundata, Context context)
      throws Exception {
    AccountPositionFormData formData = new AccountPositionFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "account-position-form");
  }

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_position_insert(RunData rundata, Context context)
      throws Exception {
    AccountPositionFormData formData = new AccountPositionFormData();
    formData.initField();
    if (formData.doInsert(this, rundata, context)) {
      // doAccount_position_list(rundata, context);
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doAccount_position_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      jsLink = null;
    } else {
      setTemplate(rundata, "account-position-form");
    }
  }

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_position_delete(RunData rundata, Context context)
      throws Exception {
    AccountPositionFormData formData = new AccountPositionFormData();
    formData.initField();
    if (formData.doDelete(this, rundata, context)) {
      // doAccount_position_list(rundata, context);
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doAccount_position_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      jsLink = null;
    }
  }

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_position_update(RunData rundata, Context context)
      throws Exception {
    AccountPositionFormData formData = new AccountPositionFormData();
    formData.initField();
    if (formData.doUpdate(this, rundata, context)) {
      // doAccount_position_list(rundata, context);
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doAccount_position_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      jsLink = null;
    } else {
      setTemplate(rundata, "account-position-form");
    }
  }

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_position_list(RunData rundata, Context context)
      throws Exception {
    AccountPositionSelectData listData = new AccountPositionSelectData();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1b-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "account-position-list");
  }

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_position_detail(RunData rundata, Context context)
      throws Exception {
    AccountPositionSelectData detailData = new AccountPositionSelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      setTemplate(rundata, "account-position-detail");
    } else {
      doAccount_position_list(rundata, context);
    }
  }

  /**
   * 役職を削除します（複数） <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_position_multi_delete(RunData rundata, Context context)
      throws Exception {
    AccountPositionMultiDelete delete = new AccountPositionMultiDelete();
    delete.doMultiAction(this, rundata, context);
    JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
    rundata.setRedirectURI(jsLink.getPortletById(
      ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      "eventSubmit_doAccount_position_list",
      "1").toString());
    rundata.getResponse().sendRedirect(rundata.getRedirectURI());
    jsLink = null;
  }

}

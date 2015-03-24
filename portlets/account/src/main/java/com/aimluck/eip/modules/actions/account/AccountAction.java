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

import com.aimluck.eip.account.AccountChangeTurnFormData;
import com.aimluck.eip.account.AccountUserFormData;
import com.aimluck.eip.account.AccountUserMultiDelete;
import com.aimluck.eip.account.AccountUserSelectData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.modules.actions.common.ALSecureBaseAction;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ユーザーアカウントの取り扱いに関するアクションクラスです。
 * 
 */
public class AccountAction extends ALSecureBaseAction {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountAction.class.getName());

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
      doAccount_list(rundata, context);
    }
  }

  /**
   * 登録画面用のフォームを表示します。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_form(RunData rundata, Context context) throws Exception {
    AccountUserFormData formData = new AccountUserFormData();
    formData.initField();
    if (formData.doViewForm(this, rundata, context)) {
      setTemplate(rundata, "account-form");
    } else {
      // setTemplate(rundata, "account");
      doAccount_list(rundata, context);
    }
  }

  /**
   * アカウントの登録を行います。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_insert(RunData rundata, Context context)
      throws Exception {
    AccountUserFormData formData = new AccountUserFormData();
    formData.initField();
    if (formData.doInsert(this, rundata, context)) {
      // データの登録に成功したとき
      // doAccount_list(rundata, context);
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doAccount_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      JetspeedLinkFactory.putInstance(jsLink);
      jsLink = null;
    } else {
      setTemplate(rundata, "account-form");
    }
  }

  /**
   * アカウントの削除を行います。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_delete(RunData rundata, Context context)
      throws Exception {
    AccountUserFormData formData = new AccountUserFormData();
    formData.initField();
    if (formData.doDelete(this, rundata, context)) {
      // データの削除に成功したとき
      // doAccount_list(rundata, context);
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doAccount_list",
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
  public void doAccount_update(RunData rundata, Context context)
      throws Exception {
    AccountUserFormData formData = new AccountUserFormData();

    formData.initField();
    if (formData.doUpdate(this, rundata, context)) {
      // データの更新に成功したとき
      doAccount_list(rundata, context);
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doAccount_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      JetspeedLinkFactory.putInstance(jsLink);
      jsLink = null;
    } else {
      setTemplate(rundata, "account-form");
    }
  }

  /**
   * アカウント一覧を表示します。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_list(RunData rundata, Context context) throws Exception {
    AccountUserSelectData listData = new AccountUserSelectData();
    // 会社/部署/役職テーブルデータをマップへロード
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.doViewList(this, rundata, context);
  }

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_detail(RunData rundata, Context context)
      throws Exception {
    AccountUserSelectData detailData = new AccountUserSelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      setTemplate(rundata, "account-detail");
    } else {
      doAccount_list(rundata, context);
    }
  }

  /**
   * ユーザーアカウントを削除します（複数） <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_multi_delete(RunData rundata, Context context)
      throws Exception {
    AccountUserMultiDelete delete = new AccountUserMultiDelete();
    delete.doMultiAction(this, rundata, context);
    JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
    rundata.setRedirectURI(jsLink.getPortletById(
      ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      "eventSubmit_doAccount_list",
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
  public void doAccount_change_turn_form(RunData rundata, Context context)
      throws Exception {
    // ユーザ情報の詳細画面や編集画面からの遷移時に，
    // セッションに残る ENTITY_ID を削除する．
    ALEipUtils.removeTemp(rundata, context, ALEipConstants.ENTITY_ID);

    AccountChangeTurnFormData formData = new AccountChangeTurnFormData();
    formData.initField();

    if (formData.doViewForm(this, rundata, context)) {
      setTemplate(rundata, "account-change-turn");
    } else {
      doAccount_list(rundata, context);
    }
  }

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_change_turn_update(RunData rundata, Context context)
      throws Exception {
    AccountChangeTurnFormData formData = new AccountChangeTurnFormData();
    formData.initField();

    if (formData.doUpdate(this, rundata, context)) {
      // データの更新に成功したとき
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doAccount_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      JetspeedLinkFactory.putInstance(jsLink);
      jsLink = null;
    } else {
      setTemplate(rundata, "account-change-turn");
    }
  }

}

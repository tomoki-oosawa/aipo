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
package com.aimluck.eip.modules.actions.page;

// Jetspeed imports
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.page.PageFormData;
import com.aimluck.eip.page.PageMultiDelete;
import com.aimluck.eip.page.PageSelectData;
import com.aimluck.eip.page.util.PageUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ページ設定の取り扱いに関するアクションクラスです。 
 * org.apache.jetspeed.modules.actions.portlets.CustomizeSetAction から処理を移管した．
 * 
 */
public class PageAction extends ALBaseAction {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(PageAction.class.getName());

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
      doPage_list(rundata, context);
    }
  }

  /**
   * ページの一覧を表示する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doPage_list(RunData rundata, Context context) throws Exception {
    PageSelectData listData = new PageSelectData();
    listData.initField();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "page");
  }

  /**
   * ページを登録するフォームを表示する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doPage_form(RunData rundata, Context context) throws Exception {
    PageFormData formData = new PageFormData();
    formData.initField();
    if (formData.doViewForm(this, rundata, context)) {
      setTemplate(rundata, "page-form");
    } else {
      doPage_list(rundata, context);
    }
  }

  /**
   * ページを登録する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doPage_insert(RunData rundata, Context context) throws Exception {
    String portletId = rundata.getParameters().getString("js_peid");
    Portlet portlet = PageUtils.getPortlet(rundata, portletId);
    context.put("portlet", portlet);

    PageFormData formData = new PageFormData();
    formData.initField();
    if (formData.doInsert(this, rundata, context)) {
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doPage_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      jsLink = null;
    } else {
      setTemplate(rundata, "page-form");
    }
  }

  /**
   * ページを更新する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doPage_update(RunData rundata, Context context) throws Exception {
    String portletId = rundata.getParameters().getString("js_peid");
    Portlet portlet = PageUtils.getPortlet(rundata, portletId);
    context.put("portlet", portlet);

    PageFormData formData = new PageFormData();
    formData.initField();
    if (formData.doUpdate(this, rundata, context)) {
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doPage_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      jsLink = null;
    } else {
      setTemplate(rundata, "page-form");
    }
  }

  /**
   * ページの詳細を表示する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doPage_detail(RunData rundata, Context context) throws Exception {
    PageSelectData detailData = new PageSelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      setTemplate(rundata, "page-detail");
    } else {
      doPage_list(rundata, context);
    }
  }

  /**
   * ページを削除する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doPage_delete(RunData rundata, Context context) throws Exception {
    String portletId = rundata.getParameters().getString("js_peid");
    Portlet portlet = PageUtils.getPortlet(rundata, portletId);
    context.put("portlet", portlet);

    PageFormData formData = new PageFormData();
    formData.initField();
    if (formData.doDelete(this, rundata, context)) {
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doPage_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      jsLink = null;
    } else {
      setTemplate(rundata, "page-detail");
    }
  }

  public void doPage_multi_delete(RunData rundata, Context context)
      throws Exception {
    String portletId = rundata.getParameters().getString("js_peid");
    Portlet portlet = PageUtils.getPortlet(rundata, portletId);
    context.put("portlet", portlet);

    this.setMode(ALEipConstants.MODE_DELETE);
    PageMultiDelete multiDelete = new PageMultiDelete();
    multiDelete.doMultiAction(this, rundata, context);
    JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
    rundata.setRedirectURI(jsLink.getPortletById(
      ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      "eventSubmit_doPage_list",
      "1").toString());
    rundata.getResponse().sendRedirect(rundata.getRedirectURI());
    jsLink = null;
  }

}

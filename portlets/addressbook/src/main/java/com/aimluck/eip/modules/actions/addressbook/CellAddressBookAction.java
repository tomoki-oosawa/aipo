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
package com.aimluck.eip.modules.actions.addressbook;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.addressbook.AbstractAddressBookWordSelectData;
import com.aimluck.eip.addressbook.AddressBookCompanySelectData;
import com.aimluck.eip.addressbook.AddressBookCompanyWordSelectData;
import com.aimluck.eip.addressbook.AddressBookCorpFilterdSelectData;
import com.aimluck.eip.addressbook.AddressBookFilterdSelectData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アドレス帳のアクションクラスです。
 * 
 */
public class CellAddressBookAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CellAddressBookAction.class.getName());

  private static final String MODE_SEARCH = "search";

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
  }

  /**
   * 最大化表示の際の処理を記述します。
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
      } else if (ALEipConstants.MODE_DETAIL.equals(mode)) {
        doAddressbook_detail(rundata, context);
      } else if ("search".equals(mode)) {
      } else if (ALEipConstants.MODE_LIST.equals(mode)) {
        String currentTab = ALEipUtils.getTemp(rundata, context, "tab");
        if (currentTab == null
          || currentTab.trim().length() == 0
          || "syagai".equals(currentTab)) {
          doAddressbook_list(rundata, context);
        } else {
          doAddressbook_corp_list(rundata, context);
        }
      }

      if (getMode() == null || ALEipConstants.MODE_LIST.equals(mode)) {
        doAddressbook_menu(rundata, context);
      }
    } catch (Exception ex) {
      logger.error("CellAddressBookAction.buildMaximizedContext", ex);
    }
  }

  /**
   * アドレス帳のメニューを表示する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_menu(RunData rundata, Context context)
      throws Exception {
    putData(rundata, context);
    setTemplate(rundata, "addressbook-menu");
  }

  /**
   * アドレス情報の表示を行います。(社外アドレス、一覧表示)
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_list(RunData rundata, Context context) {
    ALEipUtils.setTemp(rundata, context, "tab", "syagai");
    AddressBookFilterdSelectData listData = new AddressBookFilterdSelectData();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.doViewList(this, rundata, context);
    listData.loadGroups(rundata, context);
    setTemplate(rundata, "addressbook-list");
  }

  /**
   * アドレス情報の表示を行います。（社内アドレス、一覧表示）
   * 
   * @param rundata
   * @param context
   */
  public void doAddressbook_corp_list(RunData rundata, Context context) {
    ALEipUtils.setTemp(rundata, context, "tab", "corp");
    AddressBookCorpFilterdSelectData listData =
      new AddressBookCorpFilterdSelectData();
    listData.loadMygroupList(rundata, context);
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "addressbook-corplist");
  }

  public void doAddressbook_detail(RunData rundata, Context context)
      throws Exception {
    AddressBookFilterdSelectData detailData =
      new AddressBookFilterdSelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      setTemplate(rundata, "addressbook-detail");
    } else {
      doAddressbook_list(rundata, context);
    }
  }

  public void doAddressbook_corp_detail(RunData rundata, Context context)
      throws Exception {
    AddressBookCorpFilterdSelectData detailData =
      new AddressBookCorpFilterdSelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      setTemplate(rundata, "addressbook-corpdetail");
    } else {
      doAddressbook_corp_list(rundata, context);
    }
  }

  public void doAddressbook_company_list(RunData rundata, Context context)
      throws Exception {
    AddressBookCompanySelectData listData = new AddressBookCompanySelectData();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "addressbook-company-list");
  }

  /**
   * 会社情報の詳細を表示します。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_company_detail(RunData rundata, Context context)
      throws Exception {
    AddressBookCompanySelectData detailData =
      new AddressBookCompanySelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      setTemplate(rundata, "addressbook-company-detail");
    } else {
      doAddressbook_company_list(rundata, context);
    }
  }

  public void doAddressbook_addr_search(RunData rundata, Context context)
      throws Exception {
    this.setMode(MODE_SEARCH);

    String address_type =
      ALEipUtils
        .getPortlet(rundata, context)
        .getPortletConfig()
        .getInitParameter("p2a-type");
    context.put("address_type", address_type);
    putData(rundata, context);
    setTemplate(rundata, "addressbook-addr-search");
  }

  /**
   * 検索ワードによる検索処理を行います。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_addr_search_list(RunData rundata, Context context)
      throws Exception {
    context.put("isSerchRes", Boolean.TRUE);

    AbstractAddressBookWordSelectData<?, ?> listData =
      AbstractAddressBookWordSelectData.createAddressBookWordSelectData(
        rundata,
        context);

    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.doViewList(this, rundata, context);
    listData.loadGroups(rundata, context);

    if (listData.getCurrentTab().equals("syagai")) {
      setTemplate(rundata, "addressbook-list");
    } else {
      setTemplate(rundata, "addressbook-corplist");
    }
  }

  public void doAddressbook_company_search(RunData rundata, Context context)
      throws Exception {
    this.setMode(MODE_SEARCH);
    putData(rundata, context);
    setTemplate(rundata, "addressbook-company-search");
  }

  /**
   * 会社情報を検索ワードで検索する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_company_search_list(RunData rundata, Context context)
      throws Exception {
    AddressBookCompanyWordSelectData listData =
      new AddressBookCompanyWordSelectData();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.doViewList(this, rundata, context);

    context.put("isSerchRes", Boolean.TRUE);

    setTemplate(rundata, "addressbook-company-list.vm");
  }
}

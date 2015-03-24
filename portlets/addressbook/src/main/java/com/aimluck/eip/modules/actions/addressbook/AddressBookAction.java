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

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.addressbook.AbstractAddressBookWordSelectData;
import com.aimluck.eip.addressbook.AddressBookCompanyFormData;
import com.aimluck.eip.addressbook.AddressBookCompanyMultiDelete;
import com.aimluck.eip.addressbook.AddressBookCompanySelectData;
import com.aimluck.eip.addressbook.AddressBookCompanyWordSelectData;
import com.aimluck.eip.addressbook.AddressBookCorpFilterdSelectData;
import com.aimluck.eip.addressbook.AddressBookFilterdSelectData;
import com.aimluck.eip.addressbook.AddressBookFormData;
import com.aimluck.eip.addressbook.AddressBookGroupFormData;
import com.aimluck.eip.addressbook.AddressBookGroupMultiDelete;
import com.aimluck.eip.addressbook.AddressBookGroupSelectData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アドレス帳のアクションクラスです。
 * 
 */
public class AddressBookAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AddressBookAction.class.getName());

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
    // セッションのクリア
    clearAddressbookSession(rundata, context);
    setTemplate(rundata, "addressbook");
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
    // 検索ワードの取得
    String searchWord =
      ALEipUtils.getTemp(rundata, context, "AddressBooksword");
    try {
      if (ALEipConstants.MODE_FORM.equals(mode)) {
        doAddressbook_form(rundata, context);
      } else if (ALEipConstants.MODE_DETAIL.equals(mode)) {
        doAddressbook_detail(rundata, context);
      } else if ("search".equals(mode)) {
        doAddressbook_search_list(rundata, context);
      }

      if (getMode() == null || ALEipConstants.MODE_LIST.equals(mode)) {
        if (searchWord != null && !searchWord.equals("")) {
          // セッションの検索ワードを取り除く
          // ALEipUtils.removeTemp(rundata, context, "AddressBooksword");
          ALEipUtils.setTemp(rundata, context, "AddressBooksword", "");
        }
        // String currentTab = ALEipUtils.getTemp(rundata, context, "tab");
        // if (currentTab == null || currentTab.trim().length() == 0
        // || "syagai".equals(currentTab)) {
        doAddressbook_list(rundata, context);
        // } else {
        // doAddressbook_corp_list(rundata, context);
        // }
      }
    } catch (Exception ex) {
      logger.error("AddressBookAction.buildMaximizedContext", ex);
    }
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
      .getInitParameter("p1b-rows")));
    listData.setStrLength(Integer.parseInt(ALEipUtils.getPortlet(
      rundata,
      context).getPortletConfig().getInitParameter("p3a-strlen")));
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
    listData.setStrLength(Integer.parseInt(ALEipUtils.getPortlet(
      rundata,
      context).getPortletConfig().getInitParameter("p3a-strlen")));
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

  /**
   * アドレス帳へ登録するためのフォームを表示します。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_form(RunData rundata, Context context)
      throws Exception {
    AddressBookFormData formData = new AddressBookFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);

    formData.loadGroupList(rundata, context);
    formData.loadCompanyList(rundata, context);
    formData.loadGroups(rundata, context);

    setTemplate(rundata, "addressbook-form");
  }

  /**
   * アドレス帳への新規登録処理
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_insert(RunData rundata, Context context)
      throws Exception {
    AddressBookFormData formData = new AddressBookFormData();

    formData.initField();
    formData.loadGroupList(rundata, context);
    formData.loadCompanyList(rundata, context);

    if (formData.doInsert(this, rundata, context)) {
      // データinsertに成功したとき
      doAddressbook_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doAddressbook_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    } else {
      setTemplate(rundata, "addressbook-form");
    }
  }

  public void doAddressbook_update(RunData rundata, Context context)
      throws Exception {
    AddressBookFormData formData = new AddressBookFormData();

    formData.initField();
    if (formData.doUpdate(this, rundata, context)) {
      // データ更新に成功したとき
      doAddressbook_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doAddressbook_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    } else {
      formData.loadGroupList(rundata, context);
      formData.loadCompanyList(rundata, context);
      setTemplate(rundata, "addressbook-form");
    }
  }

  public void doAddressbook_delete(RunData rundata, Context context)
      throws Exception {
    AddressBookFormData formData = new AddressBookFormData();
    formData.initField();
    if (formData.doDelete(this, rundata, context)) {
      // データ削除に成功したとき
      doAddressbook_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doAddressbook_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    }
  }

  public void doAddressbook_group_list(RunData rundata, Context context)
      throws Exception {
    AddressBookGroupSelectData listData = new AddressBookGroupSelectData();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.setStrLength(Integer.parseInt(ALEipUtils.getPortlet(
      rundata,
      context).getPortletConfig().getInitParameter("p3a-strlen")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "addressbook-group-list");
  }

  /**
   * アドレスグループの詳細表示を行います。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_group_detail(RunData rundata, Context context)
      throws Exception {
    AddressBookGroupSelectData detailData = new AddressBookGroupSelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      setTemplate(rundata, "addressbook-group-detail");
    } else {
      doAddressbook_group_list(rundata, context);
    }
  }

  public void doAddressbook_group_form(RunData rundata, Context context)
      throws Exception {
    AddressBookGroupFormData formData = new AddressBookGroupFormData();
    formData.initField();

    formData.doViewForm(this, rundata, context);
    formData.loadFilter(rundata, context);
    formData.loadAddresses(rundata, context);

    setTemplate(rundata, "addressbook-group-form");
  }

  /**
   * アドレスグループの追加。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_group_insert(RunData rundata, Context context)
      throws Exception {
    AddressBookGroupFormData formData = new AddressBookGroupFormData();
    formData.initField();
    formData.loadFilter(rundata, context);
    formData.loadAddresses(rundata, context);

    if (formData.doInsert(this, rundata, context)) {
      // データ登録に成功したとき
      doAddressbook_group_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doAddressbook_group_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    } else {
      setTemplate(rundata, "addressbook-group-form");
    }
  }

  /**
   * アドレス帳に登録してある社外グループ情報を削除する。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_group_delete(RunData rundata, Context context)
      throws Exception {
    AddressBookGroupFormData formData = new AddressBookGroupFormData();
    formData.initField();
    if (formData.doDelete(this, rundata, context)) {
      doAddressbook_group_list(rundata, context);
    }
  }

  /**
   * アドレスグループの修正。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_group_update(RunData rundata, Context context)
      throws Exception {
    AddressBookGroupFormData formData = new AddressBookGroupFormData();
    formData.initField();
    if (formData.doUpdate(this, rundata, context)) {
      doAddressbook_group_list(rundata, context);
    } else {
      formData.loadFilter(rundata, context);
      setTemplate(rundata, "addressbook-group-form");
    }
  }

  public void doAddressbook_group_multi_delete(RunData rundata, Context context)
      throws Exception {
    AddressBookGroupMultiDelete delete = new AddressBookGroupMultiDelete();
    delete.doMultiAction(this, rundata, context);
    doAddressbook_group_list(rundata, context);
  }

  public void doAddressbook_company_list(RunData rundata, Context context)
      throws Exception {
    AddressBookCompanySelectData listData = new AddressBookCompanySelectData();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.setStrLength(Integer.parseInt(ALEipUtils.getPortlet(
      rundata,
      context).getPortletConfig().getInitParameter("p3a-strlen")));
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

  public void doAddressbook_company_form(RunData rundata, Context context)
      throws Exception {
    AddressBookCompanyFormData formData = new AddressBookCompanyFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "addressbook-company-form");
  }

  public void doAddressbook_company_insert(RunData rundata, Context context)
      throws Exception {
    AddressBookCompanyFormData formData = new AddressBookCompanyFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);
    if (formData.doInsert(this, rundata, context)) {
      doAddressbook_company_list(rundata, context);
    } else {
      setTemplate(rundata, "addressbook-company-form");
    }
  }

  /**
   * アドレス帳に登録してある会社情報を削除する。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_company_delete(RunData rundata, Context context)
      throws Exception {
    AddressBookCompanyFormData formData = new AddressBookCompanyFormData();
    formData.initField();
    if (formData.doDelete(this, rundata, context)) {
      doAddressbook_company_list(rundata, context);
    }
  }

  /**
   * アドレス帳登録済みの会社情報を修正する。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_company_update(RunData rundata, Context context)
      throws Exception {
    AddressBookCompanyFormData formData = new AddressBookCompanyFormData();
    formData.initField();
    if (formData.doUpdate(this, rundata, context)) {
      doAddressbook_company_list(rundata, context);
    } else {
      setTemplate(rundata, "addressbook-company-form");
    }
  }

  /**
   * アドレス帳登録済みの会社を削除する。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_company_multi_delete(RunData rundata,
      Context context) throws Exception {
    AddressBookCompanyMultiDelete delete = new AddressBookCompanyMultiDelete();
    delete.doMultiAction(this, rundata, context);
    doAddressbook_company_list(rundata, context);
  }

  /**
   * 検索ワードによる検索処理を行います。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_search_list(RunData rundata, Context context)
      throws Exception {
    AbstractAddressBookWordSelectData<?, ?> listData =
      AbstractAddressBookWordSelectData.createAddressBookWordSelectData(
        rundata,
        context);

    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.setStrLength(Integer.parseInt(ALEipUtils.getPortlet(
      rundata,
      context).getPortletConfig().getInitParameter("p3a-strlen")));
    listData.doViewList(this, rundata, context);
    listData.loadGroups(rundata, context);

    setTemplate(rundata, listData.getTemplateFilePath());

    // setTemplate(rundata, "addressbook-list");
    // setTemplate(rundata, "addressbook-corplist");
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
    listData.setStrLength(Integer.parseInt(ALEipUtils.getPortlet(
      rundata,
      context).getPortletConfig().getInitParameter("p3a-strlen")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "addressbook-company-list.vm");
  }

  private void clearAddressbookSession(RunData rundata, Context context) {
    List<String> list = new ArrayList<String>();
    list.add("tab");
    list.add("entityid");
    list.add("AddressBooksCompanyword");
    list.add("com.aimluck.eip.addressbook.AddressBookFilterdSelectDatasort");
    list.add("com.aimluck.eip.addressbook.AddressBookFilterdSelectDatafilter");
    list
      .add("com.aimluck.eip.addressbook.AddressBookFilterdSelectDatafiltertype");
    list
      .add("com.aimluck.eip.addressbook.AddressBookCorpFilterdSelectDatasort");
    list.add("com.aimluck.eip.addressbook.AddressBookCompanySelectDatasort");
    list.add("com.aimluck.eip.addressbook.AddressBookGroupSelectDatasort");
    list.add("AddressBooksword");
    list.add("com.aimluck.eip.addressbook.AddressBookWordSelectDatasort");
    list
      .add("com.aimluck.eip.addressbook.AddressBookCompanyWordSelectDatasort");
    ALEipUtils.removeTemp(rundata, context, list);
  }
}

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
package com.aimluck.eip.modules.actions.cabinet;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cabinet.CabinetSelectData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 共有フォルダの取り扱いに関するアクションクラスです。 <br />
 * 
 */
public class CabinetAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(CabinetAction.class.getName());

  /**
   * 通常表示の際の処理を記述します。 <BR>
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
    // セッション情報のクリア
    clearCabinetSession(rundata, context);

    CabinetSelectData listData = new CabinetSelectData();
    listData.setIsNormalContext(true);
    listData.initField();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
        .getPortlet(rundata, context).getPortletConfig().getInitParameter(
            "p1a-rows")));
    if (listData.doViewList(this, rundata, context)) {
      setTemplate(rundata, "cabinet");
    }
  }

  /**
   * 最大化表示の際の処理を記述する． <BR>
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
      // if (ALEipConstants.MODE_FORM.equals(mode)) {
      // } else if (ALEipConstants.MODE_DETAIL.equals(mode)) {
      // doCabinet_file_detail(rundata, context);
      // } else if (ALEipConstants.MODE_LIST.equals(mode)) {
      // doCabinet_list(rundata, context);
      // } else if ("folder_detail".equals(mode)) {
      // doCabinet_folder_detail(rundata, context);
      // }

      if (ALEipConstants.MODE_LIST.equals(mode)) {
        doCabinet_list(rundata, context);
      }

      if (getMode() == null) {
        doCabinet_list(rundata, context);
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
    }

  }

  /**
   * キャビネットの一覧を表示する． <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doCabinet_list(RunData rundata, Context context) throws Exception {
    CabinetSelectData listData = new CabinetSelectData();
    listData.initField();
    // PSMLからパラメータをロードする
    // 最大表示件数（最大化時）
    listData.setRowsNum(Integer.parseInt(ALEipUtils
        .getPortlet(rundata, context).getPortletConfig().getInitParameter(
            "p1b-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "cabinet-list");
  }

  private void clearCabinetSession(RunData rundata, Context context) {
    List list = new ArrayList();
    list.add("entityid");
    list.add("folder_id");
    list.add("CabinetFileWord");
    list.add("com.aimluck.eip.cabinet.CabinetFolderSelectDatasort");
    list.add("com.aimluck.eip.cabinet.CabinetFileWordSelectDatasort");
    ALEipUtils.removeTemp(rundata, context, list);
  }

  // /**
  // * フォルダを登録する. <BR>
  // *
  // * @param rundata
  // * @param context
  // * @throws Exception
  // */
  // public void doCabinet_folder_insert(RunData rundata, Context context)
  // throws Exception {
  // CabinetFolderFormData formData = new CabinetFolderFormData();
  // formData.initField();
  // if (formData.doInsert(this, rundata, context)) {
  // // データ登録が成功したとき
  // FolderInfo info = formData.getSelectedFolderInfo();
  // ALEipUtils.setTemp(rundata, context, CabinetUtils.KEY_FOLDER_ID, ""
  // + info.getFolderId());
  // doCabinet_list(rundata, context);
  // // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
  // // rundata.setRedirectURI(jsLink.getPortletById(
  // // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
  // // "eventSubmit_doCabinet_list", "1").addQueryData("folder_id",
  // // info.getFolderId()).toString());
  // // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
  // // jsLink = null;
  // } else {
  // setTemplate(rundata, "cabinet-folder-form");
  // }
  // }

  // /**
  // * フォルダを更新する． <BR>
  // *
  // * @param rundata
  // * @param context
  // * @throws Exception
  // */
  // public void doCabinet_folder_update(RunData rundata, Context context)
  // throws Exception {
  // CabinetFolderFormData formData = new CabinetFolderFormData();
  // formData.initField();
  // if (formData.doUpdate(this, rundata, context)) {
  // // データ更新が成功したとき
  // doCabinet_list(rundata, context);
  // // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
  // // rundata.setRedirectURI(jsLink.getPortletById(
  // // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
  // // "eventSubmit_doCabinet_list", "1").toString());
  // // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
  // // jsLink = null;
  // } else {
  // setTemplate(rundata, "cabinet-folder-form");
  // }
  // }

  // /**
  // * フォルダの詳細を表示する． <BR>
  // *
  // * @param rundata
  // * @param context
  // * @throws Exception
  // */
  // public void doCabinet_folder_detail(RunData rundata, Context context)
  // throws Exception {
  // CabinetFolderSelectData detailData = new CabinetFolderSelectData();
  // detailData.initField();
  // if (detailData.doViewDetail(this, rundata, context)) {
  // setTemplate(rundata, "cabinet-folder-detail");
  // } else {
  // doCabinet_list(rundata, context);
  // }
  // }

  // /**
  // * フォルダを削除する． <BR>
  // *
  // * @param rundata
  // * @param context
  // * @throws Exception
  // */
  // public void doCabinet_folder_delete(RunData rundata, Context context)
  // throws Exception {
  // CabinetFolderFormData formData = new CabinetFolderFormData();
  // formData.initField();
  // if (formData.doDelete(this, rundata, context)) {
  // // データ更新が成功したとき
  // FolderInfo info = formData.getSelectedFolderInfo();
  // ALEipUtils.setTemp(rundata, context, CabinetUtils.KEY_FOLDER_ID, ""
  // + info.getFolderId());
  // doCabinet_list(rundata, context);
  // // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
  // // rundata.setRedirectURI(jsLink.getPortletById(
  // // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
  // // "eventSubmit_doCabinet_list", "1").addQueryData("folder_id",
  // // info.getFolderId()).toString());
  // // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
  // // jsLink = null;
  // } else {
  // setTemplate(rundata, "cabinet-folder-detail");
  // }
  // }

  // /**
  // * ファイルの登録フォームを表示する. <BR>
  // *
  // * @param rundata
  // * @param context
  // * @throws Exception
  // */
  // public void doCabinet_file_form(RunData rundata, Context context)
  // throws Exception {
  // CabinetFileFormData formData = new CabinetFileFormData();
  // formData.initField();
  // formData.doViewForm(this, rundata, context);
  // setTemplate(rundata, "cabinet-file-form");
  // }

  // /**
  // * ファイルを登録する. <BR>
  // *
  // * @param rundata
  // * @param context
  // * @throws Exception
  // */
  // public void doCabinet_file_insert(RunData rundata, Context context)
  // throws Exception {
  // CabinetFileFormData formData = new CabinetFileFormData();
  // formData.initField();
  // if (formData.doInsert(this, rundata, context)) {
  // // データ登録が成功したとき
  // FolderInfo info = formData.getSelectedFolderInfo();
  // ALEipUtils.setTemp(rundata, context, CabinetUtils.KEY_FOLDER_ID, ""
  // + info.getFolderId());
  // doCabinet_list(rundata, context);
  // // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
  // // rundata.setRedirectURI(jsLink.getPortletById(
  // // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
  // // "eventSubmit_doCabinet_list", "1").addQueryData("folder_id",
  // // info.getFolderId()).toString());
  // // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
  // // jsLink = null;
  // } else {
  // setTemplate(rundata, "cabinet-file-form");
  // }
  // }

  // /**
  // * ファイルを更新する． <BR>
  // *
  // * @param rundata
  // * @param context
  // * @throws Exception
  // */
  // public void doCabinet_file_update(RunData rundata, Context context)
  // throws Exception {
  // CabinetFileFormData formData = new CabinetFileFormData();
  // formData.initField();
  // if (formData.doUpdate(this, rundata, context)) {
  // // データ更新が成功したとき
  // doCabinet_list(rundata, context);
  // // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
  // // rundata.setRedirectURI(jsLink.getPortletById(
  // // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
  // // "eventSubmit_doCabinet_list", "1").toString());
  // // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
  // // jsLink = null;
  // } else {
  // setTemplate(rundata, "cabinet-file-form");
  // }
  // }

  // /**
  // * ファイルの詳細を表示する． <BR>
  // *
  // * @param rundata
  // * @param context
  // * @throws Exception
  // */
  // public void doCabinet_file_detail(RunData rundata, Context context)
  // throws Exception {
  // CabinetSelectData detailData = new CabinetSelectData();
  // detailData.initField();
  // if (detailData.doViewDetail(this, rundata, context)) {
  // setTemplate(rundata, "cabinet-file-detail");
  // } else {
  // doCabinet_list(rundata, context);
  // }
  // }
  //
  // /**
  // * ファイルを削除する． <BR>
  // *
  // * @param rundata
  // * @param context
  // * @throws Exception
  // */
  // public void doCabinet_file_delete(RunData rundata, Context context)
  // throws Exception {
  // CabinetFileFormData formData = new CabinetFileFormData();
  // formData.initField();
  // if (formData.doDelete(this, rundata, context)) {
  // // データ更新が成功したとき
  // FolderInfo info = formData.getSelectedFolderInfo();
  // ALEipUtils.setTemp(rundata, context, CabinetUtils.KEY_FOLDER_ID, ""
  // + info.getFolderId());
  // doCabinet_list(rundata, context);
  // // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
  // // rundata.setRedirectURI(jsLink.getPortletById(
  // // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
  // // "eventSubmit_doCabinet_list", "1").addQueryData("folder_id",
  // // info.getFolderId()).toString());
  // // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
  // // jsLink = null;
  // } else {
  // setTemplate(rundata, "cabinet-file-detail");
  // }
  // }

  // /**
  // * 会社情報を検索ワードで検索する．
  // *
  // * @param rundata
  // * @param context
  // * @throws Exception
  // */
  // public void doCabinet_file_search_list(RunData rundata, Context context)
  // throws Exception {
  // CabinetFileWordSelectData listData = new CabinetFileWordSelectData();
  // // PSMLからパラメータをロードする
  // // 最大表示件数（最大化時）
  // listData.setRowsNum(Integer.parseInt(ALEipUtils
  // .getPortlet(rundata, context).getPortletConfig().getInitParameter(
  // "p1b-rows")));
  // listData.doViewList(this, rundata, context);
  // setTemplate(rundata, "cabinet-file-search-list");
  // }

}

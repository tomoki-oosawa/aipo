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
package com.aimluck.eip.modules.actions.mygroup;

import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.mygroup.MyGroupFormData;
import com.aimluck.eip.mygroup.MyGroupSelectData;
import com.aimluck.eip.util.ALEipUtils;

/**
 * マイグループのアクションクラスです。 <BR>
 * 
 */
public class MyGroupAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MyGroupAction.class.getName());

  /**
   * 通常表示の際の処理を記述します。 <BR>
   * 
   * @param portlet
   * @param context
   * @param rundata
   * @throws java.lang.Exception
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {
    if (getMode() == null) {
      doMygroup_list(rundata, context);
    }
  }

  /**
   * 最大化表示の際の処理を記述します。 <BR>
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
      setupMaximized(rundata, context);
      if (ALEipConstants.MODE_FORM.equals(mode)) {
        doMygroup_form(rundata, context);
      } else if (ALEipConstants.MODE_DETAIL.equals(mode)) {
        doMygroup_detail(rundata, context);
      } else if (ALEipConstants.MODE_LIST.equals(mode)) {
        doMygroup_list(rundata, context);
      }

      if (getMode() == null) {
        doMygroup_list(rundata, context);
      }
    } catch (Exception ex) {
      logger.error("mygroup", ex);
    }
  }

  private void setupMaximized(RunData rundata, Context context)
      throws Exception {

    context.put("isMaximized", Boolean.TRUE);

    context.put("PURL_AccountEdit", getPortletURIinPersonalConfigPane(
      rundata,
      "AccountEdit"));
    context.put("PURL_WebMailAccountEdit", getPortletURIinPersonalConfigPane(
      rundata,
      "WebMailAccountEdit"));
    context.put("PURL_MyGroup", getPortletURIinPersonalConfigPane(
      rundata,
      "MyGroup"));
    context
      .put("PURL_Page", getPortletURIinPersonalConfigPane(rundata, "Page"));
    context.put("PURL_Cellular", getPortletURIinPersonalConfigPane(
      rundata,
      "Cellular"));
  }

  /**
   * マイグループ登録のフォームを表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doMygroup_form(RunData rundata, Context context) throws Exception {
    MyGroupFormData formData = new MyGroupFormData();
    formData.initField();
    formData.loadFacilityAllList(rundata, context);
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "mygroup-form");

  }

  /**
   * マイグループを登録します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doMygroup_insert(RunData rundata, Context context)
      throws Exception {
    try {
      MyGroupFormData formData = new MyGroupFormData();
      formData.initField();
      formData.loadFacilityAllList(rundata, context);
      if (formData.doInsert(this, rundata, context)) {
        doMygroup_list(rundata, context);
        // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
        // rundata.setRedirectURI(jsLink.getPortletById(
        // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        // "eventSubmit_doMygroup_list", "1").toString());
        // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
        // jsLink = null;
      } else {
        setTemplate(rundata, "mygroup-form");
      }
    } catch (Exception e) {
      logger.error("[MyGroupAction]", e);
    }
  }

  /**
   * マイグループを更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doMygroup_update(RunData rundata, Context context)
      throws Exception {
    MyGroupFormData formData = new MyGroupFormData();
    formData.initField();
    formData.loadFacilityAllList(rundata, context);
    if (formData.doUpdate(this, rundata, context)) {
      doMygroup_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doMygroup_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    } else {
      setTemplate(rundata, "mygroup-form");
    }
  }

  /**
   * マイグループを削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doMygroup_delete(RunData rundata, Context context)
      throws Exception {
    MyGroupFormData formData = new MyGroupFormData();
    formData.initField();
    if (formData.doDelete(this, rundata, context)) {
      doMygroup_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doMygroup_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    } else {
      setTemplate(rundata, "mygroup-detail");
    }
  }

  /**
   * マイグループを削除します。（複数） <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doMygroup_multi_delete(RunData rundata, Context context)
      throws Exception {
  }

  /**
   * マイグループを一覧表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doMygroup_list(RunData rundata, Context context) throws Exception {
    MyGroupSelectData listData = new MyGroupSelectData();
    listData.initField();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "mygroup");
  }

  /**
   * マイグループを詳細表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doMygroup_detail(RunData rundata, Context context)
      throws Exception {
    MyGroupSelectData detailData = new MyGroupSelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      setTemplate(rundata, "mygroup-detail");
    } else {
      doMygroup_list(rundata, context);
    }
  }

  /**
   * 指定したエントリー名を持つ個人設定ページに含まれるポートレットへの URI を取得する．
   * 
   * @param rundata
   * @param portletEntryName
   *          PSML ファイルに記述されているタグ entry の要素 parent
   * @return
   */
  private String getPortletURIinPersonalConfigPane(RunData rundata,
      String portletEntryName) {
    try {
      Portlets portlets =
        ((JetspeedRunData) rundata).getProfile().getDocument().getPortlets();
      if (portlets == null) {
        return null;
      }

      Portlets[] portletList = portlets.getPortletsArray();
      if (portletList == null) {
        return null;
      }

      int length = portletList.length;
      for (int i = 0; i < length; i++) {
        Entry[] entries = portletList[i].getEntriesArray();
        if (entries == null || entries.length <= 0) {
          continue;
        }

        int ent_length = entries.length;
        for (int j = 0; j < ent_length; j++) {
          if (entries[j].getParent().equals(portletEntryName)) {
            JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);

            DynamicURI duri =
              jsLink.getLink(
                JetspeedLink.CURRENT,
                null,
                null,
                JetspeedLink.CURRENT,
                null);
            duri =
              duri.addPathInfo(
                JetspeedResources.PATH_PANEID_KEY,
                entries[j].getId()).addQueryData(
                JetspeedResources.PATH_ACTION_KEY,
                "controls.Restore");
            return duri.toString();
          }
        }
      }
    } catch (Exception ex) {
      logger.error("mygroup", ex);
      return null;
    }
    return null;
  }
}

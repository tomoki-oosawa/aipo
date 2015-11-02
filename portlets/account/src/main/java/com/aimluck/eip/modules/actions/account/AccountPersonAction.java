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

import com.aimluck.eip.account.AccountEditFormData;
import com.aimluck.eip.account.AccountEditSelectData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.modules.actions.common.ALBaseAction;

/**
 * 個人設定・ユーザー情報用アクションクラスです。
 * 
 */
public class AccountPersonAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountPersonAction.class.getName());

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
      doAccountperson_detail(rundata, context);
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
      // setupMaximized(rundata, context);

      if (ALEipConstants.MODE_FORM.equals(mode)) {
        doAccountperson_form(rundata, context);
      } else if (ALEipConstants.MODE_DETAIL.equals(mode)) {
        doAccountperson_detail(rundata, context);
      }

      if (getMode() == null) {
        doAccountperson_detail(rundata, context);
      }
    } catch (Exception ex) {
      logger.error("AccountPersonAction.buildMaximizedContext", ex);
    }
  }

  @SuppressWarnings("unused")
  private void setupMaximized(RunData rundata, Context context)
      throws Exception {
    // Maximize maxmize = new Maximize();
    // maxmize.doPerform(rundata);

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
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccountedit_update(RunData rundata, Context context)
      throws Exception {
    AccountEditFormData formData = new AccountEditFormData();
    formData.initField();
    setTemplate(rundata, "account-person-form");
    if (formData.doUpdate(this, rundata, context)) {
      doAccountperson_detail(rundata, context);
    } else {
      formData.loadEmail(rundata);
      setTemplate(rundata, "account-person-form");
    }
  }

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccountperson_detail(RunData rundata, Context context)
      throws Exception {
    AccountEditSelectData detailData = new AccountEditSelectData();
    detailData.initField();
    detailData.doViewDetail(this, rundata, context);
    setTemplate(rundata, "account-person-detail");
  }

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccountperson_form(RunData rundata, Context context)
      throws Exception {
    AccountEditFormData formData = new AccountEditFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "account-person-form");
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
      logger.error("AccountPersonAction.getPortletURIinPersonalConfigPane", ex);
      return null;
    }
    return null;
  }
}

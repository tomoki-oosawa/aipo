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
package com.aimluck.eip.gadgets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.profile.Parameter;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.psmlmanager.db.DBUtils;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.exolab.castor.mapping.Mapping;

import com.aimluck.eip.cayenne.om.account.JetspeedUserProfile;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.gadgets.util.PsmlDBUtils;
import com.aimluck.eip.gadgets.util.PsmlUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 『テンプレート更新』のフォームデータを管理するクラス．
 *
 */
public class AdvancedGagetsPsmlFormData extends GagetsPsmlFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AdvancedGagetsPsmlFormData.class.getName());

  private static final String UPDATE_SUCCESS = "success";

  private static final String UPDATE_ERROR = "error";

  private static final String DEFAULT_VIEW_STATUS = "default_psml_status";

  // castor mapping
  public static final String DEFAULT_MAPPING =
    "${webappRoot}/WEB-INF/conf/psml-mapping.xml";

  /**
   * データを更新します。
   *
   * @param action
   * @param rundata
   * @param context
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  public boolean doUpdate(ALAction action, RunData rundata, Context context) {
    try {
      if (!doCheckSecurity(rundata, context)) {
        return false;
      }

      init(action, rundata, context);

      doCheckAclPermission(
        rundata,
        context,
        ALAccessControlConstants.VALUE_ACL_UPDATE);

      action.setMode(ALEipConstants.MODE_UPDATE);
      rundata.getParameters().add(
        ALEipConstants.MODE,
        ALEipConstants.MODE_UPDATE);
      List<String> msgList = new ArrayList<String>();
      setValidator();

      boolean res = false;
      if (isOverQuota()) {
        msgList.add(ALLocalizationUtils.getl10n("GADGETS_ALERT_DISC_CAPACITY"));
      } else {
        res =
          (setFormData(rundata, context, msgList) && validate(msgList) && updateFormData(
            rundata,
            context,
            msgList));
      }

      if (!res) {
        action.setMode(ALEipConstants.MODE_EDIT_FORM);
        mode = action.getMode();
      }
      action.setResultData(this);
      if (!msgList.isEmpty()) {
        action.addErrorMessages(msgList);
      }
      action.putData(rundata, context);

      return res;
    } catch (ALPermissionException e) {
      ALEipUtils.redirectPermissionError(rundata);
      return false;
    } catch (ALPageNotFoundException e) {
      ALEipUtils.redirectPageNotFound(rundata);
      return false;
    } catch (ALDBErrorException e) {
      ALEipUtils.redirectDBError(rundata);
      return false;
    }
  }

  /**
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    boolean success;
    if ("timeline".equals(rundata.getParameters().getString("mode"))
      || "schedule".equals(rundata.getParameters().getString("mode"))) {
      success = updateTemplateFormData(rundata, context, msgList);
    } else if (rundata.getParameters().getString("mode") == null
      || "".equals(rundata.getParameters().getString("mode"))) {
      success = false;
      msgList.add(ALLocalizationUtils.getl10n("GADGETS_ALERT_SELECT_SETTING"));
    } else {
      success = super.updateFormData(rundata, context, msgList);
    }
    return success;
  }

  protected boolean updateTemplateFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      String psml;
      Mapping mapping = PsmlUtils.getMapping(rundata);
      File psmlFile;
      if ("timeline".equals(rundata.getParameters().getString("mode"))) {
        psmlFile = PsmlUtils.getTemplateHtmlDefaultTimelinePsmlFile(rundata);
      } else if ("schedule".equals(rundata.getParameters().getString("mode"))) {
        psmlFile = PsmlUtils.getTemplateHtmlDefaultSchedulePsmlFile(rundata);
      } else {
        psmlFile = PsmlUtils.getTemplateHtmlDefaultPsmlFile(rundata);
      }
      PSMLDocument doc = PsmlUtils.loadDocument(psmlFile, mapping);
      byte[] psmlByte =
        DBUtils.portletsToBytes(doc.getPortlets(), PsmlUtils
          .getMapping(rundata));

      psml = new String(psmlByte);
      psml = PsmlUtils.parsePsmlForAllUser(psml);
      psml = PsmlUtils.PSMLEncode(psml);

      PsmlDBUtils.checkAndFixInconsistency(PsmlUtils.TEMPLATE_NAME);
      JetspeedUserProfile profileTemplate =
        PsmlDBUtils.getTemplateHtmlProfile();
      profileTemplate.setProfile(psml.getBytes());

      Portlets portlets = DBUtils.bytesToPortlets(psml.getBytes(), mapping);

      List<JetspeedUserProfile> profiles = PsmlDBUtils.getAllUserHtmlProfile();
      for (JetspeedUserProfile profile : profiles) {
        String userName = profile.getUserName();
        PsmlDBUtils.checkAndFixInconsistency(profile.getUserName());
        ALEipUser alEipUser = ALEipUtils.getALEipUser(userName);
        if (alEipUser == null) {
          continue;
        }
        long userId = alEipUser.getUserId().getValue();
        boolean isAdmin = ALEipUtils.isAdmin((int) userId);
        org.apache.jetspeed.util.PortletUtils.regenerateIds(portlets);
        Portlets myportlets = (Portlets) portlets.clone();

        Portlets[] portletList = myportlets.getPortletsArray();

        int length = portletList.length;

        for (int i = 0; i < length; i++) {
          Entry[] entries = portletList[i].getEntriesArray();
          if (entries == null || entries.length <= 0) {
            continue;
          }

          int ent_length = entries.length;
          for (int j = 0; j < ent_length; j++) {
            if (entries[j].getParent().equals("Schedule")
              || entries[j].getParent().equals("AjaxScheduleWeekly")) {
              Parameter scheduleParameter = entries[j].getParameter("p6a-uids");
              if (scheduleParameter != null) {
                scheduleParameter.setValue("");
              }
            }

            if (entries[j].getParent().equals("WebMail")) {
              Parameter webmailParameter =
                entries[j].getParameter("p3a-accounts");
              if (webmailParameter != null) {
                webmailParameter.setValue("");
              }
            }

          }
        }
        profile.setProfile(DBUtils.portletsToBytes(myportlets, mapping));
        if (isAdmin == true) {
          ALEipUtils.addAdminPage(userName);
        }
      }

      Database.commit();
      ALEipUtils.setTemp(rundata, context, DEFAULT_VIEW_STATUS, UPDATE_SUCCESS);

    } catch (Exception ex) {
      Database.rollback();
      logger.error("gadgets", ex);
      ALEipUtils.setTemp(rundata, context, DEFAULT_VIEW_STATUS, UPDATE_ERROR);
      return false;
    }
    return true;
  }

}

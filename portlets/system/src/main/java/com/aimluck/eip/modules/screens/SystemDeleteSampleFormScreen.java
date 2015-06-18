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
package com.aimluck.eip.modules.screens;

import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Parameter;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileException;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.system.util.SystemUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * サンプルデータを処理するクラスです。 <br />
 * 
 */
public class SystemDeleteSampleFormScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SystemDeleteSampleFormScreen.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    try {
      doSystem_delete_sample_form(rundata, context);
    } catch (Exception ex) {
      logger.error("[SystemDeleteSampleFormScreen] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * 
   * @param rundata
   * @param context
   */
  protected void doSystem_delete_sample_form(RunData rundata, Context context) {
    try {
      // For security
      context.put(ALEipConstants.SECURE_ID, rundata.getUser().getTemp(
        ALEipConstants.SECURE_ID));

      context.put("alias", ALOrgUtilsService.getAlias());
      context.put("desa", getFlag(rundata));

      context.put("l10n", ALLocalizationUtils.createLocalization(rundata));

      // Set layout
      String layout_template =
        "portlets/html/ajax-system-form-delete-sample.vm";
      setTemplate(rundata, context, layout_template);

    } catch (Exception ex) {
      logger.error("[SystemDeleteSampleFormScreen] Exception.", ex);
      ALEipUtils.redirectPageNotFound(rundata);
    }
  }

  /**
   * サンプルデータの削除済みフラグを取得します。
   * 
   * @param rundata
   * @return flag 削除済みの場合:"1"
   * @throws ProfileException
   */
  private String getFlag(RunData rundata) throws ProfileException {
    String portletEntryId =
      rundata.getParameters().getString("portlet_id", null);

    String FLAG = "desa";

    Profile profile = ((JetspeedRunData) rundata).getProfile();
    Portlets portlets = profile.getDocument().getPortlets();

    Portlets[] portletList = portlets.getPortletsArray();

    Parameter params[] = null;

    Entry[] entries = portletList[0].getEntriesArray();
    Entry entry = null;
    int ent_length = entries.length;
    for (int j = 0; j < ent_length; j++) {
      entry = entries[j];
      if (entry.getId().equals(portletEntryId)) {
        params = entry.getParameter();
        int param_len = params.length;
        for (int k = 0; k < param_len; k++) {
          if (params[k].getName().equals(FLAG)) {
            return params[k].getValue();
          }
        }
        break;
      }
    }

    return "0";
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return SystemUtils.SYSTEM_PORTLET_NAME;
  }
}

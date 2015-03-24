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
package com.aimluck.eip.modules.actions.deletesample;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.deletesample.DeleteSampleFormData;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;

/**
 * サンプルデータ削除のアクションクラスです。 <BR>
 * 
 */
public class DeleteSampleAction extends ALBaseAction {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(DeleteSampleAction.class.getName());

  /**
   * 通常表示の際の処理を記述します。 <BR>
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
      setTemplate(rundata, "deletesample");
    }
    if ("1".equals(portlet.getPortletConfig().getInitParameter("desa"))) {
      setTemplate(rundata, "deletesample-delete");
    }
    // For security
    context.put(ALEipConstants.SECURE_ID, rundata.getUser().getTemp(
      ALEipConstants.SECURE_ID));

    context.put("alias", ALOrgUtilsService.getAlias());
  }

  /**
   * DeleteSample登録のフォームを表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doDeletesample_form(RunData rundata, Context context)
      throws Exception {
    DeleteSampleFormData formData = new DeleteSampleFormData();
    formData.initField();
    if (formData.doUpdate(this, rundata, context)) {
      context.put("flag", "success");
    } else {
      context.put("flag", "fail");
    }
    setTemplate(rundata, "deletesample-result");
  }
}

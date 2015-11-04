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
package com.aimluck.eip.modules.screens;

import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.fileupload.FileuploadFormData;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ファイルのアップロードを処理するクラスです。 <br />
 * 
 */
public class FileuploadadvFormScreen extends FileuploadFormScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileuploadadvFormScreen.class.getName());

  /**
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @throws Exception
   *           例外
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    String mode = rundata.getParameters().getString("mode", "");
    try {
      if (mode.contains(ALEipConstants.MODE_FORM)) {
        doFileupload_form(rundata, context, mode);
      } else if (mode.contains(ALEipConstants.MODE_UPDATE)) {
        doFileupload_update(rundata, context, mode);
      }
    } catch (Exception ex) {
      logger.error("[FileuploadadvFormScreen] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * 添付ファイルの入力フォームを開く．
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @throws Exception
   *           例外
   */
  @Override
  protected void doFileupload_form(RunData rundata, Context context, String mode)
      throws Exception {
    context.put("data", rundata);
    context.put("js_peid", rundata.getParameters().getString("js_peid", ""));
    context.put("msize", rundata.getParameters().getString(
      FileuploadUtils.KEY_MAX_SIZE,
      "0"));
    context.put("nsize", rundata.getParameters().getString(
      FileuploadUtils.KEY_NOW_SIZE,
      "0"));
    context.put("keyid", rundata.getParameters().getString("keyid", ""));

    HttpServletResponse response = rundata.getResponse();
    response
      .setContentType("text/html; " + ALEipConstants.DEF_CONTENT_ENCODING);

    FileuploadFormData formData = new FileuploadFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);

    String layout_template = "layouts/html/fileuploadadv-mini.vm";

    setTemplate(rundata, context, layout_template);
  }

  /**
   * 添付ファイルのアップロードを受け付ける．
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @throws Exception
   *           例外
   */
  @Override
  protected void doFileupload_update(RunData rundata, Context context,
      String mode) throws Exception {

    context.put("data", rundata);
    context.put("js_peid", rundata.getParameters().getString("js_peid", ""));
    context.put("msize", rundata.getParameters().getString(
      FileuploadUtils.KEY_MAX_SIZE,
      "0"));
    context.put("nsize", rundata.getParameters().getString(
      FileuploadUtils.KEY_NOW_SIZE,
      "0"));
    context.put("keyid", rundata.getParameters().getString("keyid", ""));

    HttpServletResponse response = rundata.getResponse();
    response
      .setContentType("text/html; " + ALEipConstants.DEF_CONTENT_ENCODING);

    FileuploadFormData formData = new FileuploadFormData();
    formData.initField();
    if (formData.doUpdate(this, rundata, context)) {
      context.put("receiveFile", "true");
    }
    String layout_template = "layouts/html/fileuploadadv-mini.vm";

    setTemplate(rundata, context, layout_template);

  }

  /**
   * ポートレット名を取得する
   * 
   * @return ポートレット名
   */
  @Override
  protected String getPortletName() {
    return null;
  }

}

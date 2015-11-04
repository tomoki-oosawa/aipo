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

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.fileupload.FileuploadViewResultData;

/**
 * 各ポートレットでの添付ファイルを表示させるクラスです。 <br />
 * 
 */
public class FileuploadViewScreen extends ALVelocityScreen {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileuploadViewScreen.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    FileuploadViewResultData data = new FileuploadViewResultData();

    data.setOwnerId(rundata.getParameters().getInt("ownerid"));
    data.setEntryId(rundata.getParameters().getInt("entryid"));
    data.setFileId(rundata.getParameters().getInt("attachmentindex"));
    data.setRawScreen(rundata.getParameters().getString("screen"));
    data.setFileName(rundata.getParameters().getString("f"));

    context.put("jslink", JetspeedLinkFactory.getInstance(rundata));

    setResultData(data);

    putData(rundata, context);

    String layout_template = "portlets/html/fileupload-image.vm";
    setTemplate(rundata, context, layout_template);
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return null;
  }

}

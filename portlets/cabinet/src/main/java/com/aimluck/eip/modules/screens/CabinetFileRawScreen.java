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
package com.aimluck.eip.modules.screens;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.access.DataContext;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cabinet.util.CabinetUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFile;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.orm.DatabaseOrmService;

/**
 * 共有フォルダのファイルの一覧を処理するクラスです。 <br />
 * 
 */
public class CabinetFileRawScreen extends FileuploadRawScreen {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(CabinetFileRawScreen.class.getName());

  /**
   * @see org.apache.turbine.modules.screens.RawScreen#getContentType(org.apache.turbine.util.RunData)
   */
  protected String getContentType(RunData rundata) {
    return "application/octet-stream";
  }

  /**
   * @see org.apache.turbine.modules.screens.RawScreen#doOutput(org.apache.turbine.util.RunData)
   */
  protected void doOutput(RunData rundata) throws Exception {

    try {
      int fileindex = rundata.getParameters().getInt(ALEipConstants.ENTITY_ID);

      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      EipTCabinetFile cabinetfile = (EipTCabinetFile) DataObjectUtils
          .objectForPK(dataContext, EipTCabinetFile.class, Integer
              .valueOf(fileindex));

      super.setFilePath(CabinetUtils.getSaveDirPath(DatabaseOrmService
          .getInstance().getOrgId(rundata))
          + cabinetfile.getFilePath());
      super.setFileName(cabinetfile.getFileName());
      super.doOutput(rundata);

    } catch (Exception e) {
      logger.error("[ERROR]" + e);
    }

  }
}

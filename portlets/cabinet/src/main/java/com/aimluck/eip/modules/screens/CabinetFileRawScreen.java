/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cabinet.util.CabinetUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFile;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFolder;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFolderMap;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.eventlog.action.ALActionEventlogConstants;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 共有フォルダのファイルの一覧を処理するクラスです。 <br />
 * 
 */
public class CabinetFileRawScreen extends FileuploadRawScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CabinetFileRawScreen.class.getName());

  private int userid;

  private EipTCabinetFolder cabinetfolder;

  /**
   * 
   * @param rundata
   * @return
   */
  @Override
  protected String getContentType(RunData rundata) {
    return "application/octet-stream";
  }

  /**
   * 
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata) throws Exception {

    try {
      doCheckAclPermission(
        rundata,
        ALAccessControlConstants.POERTLET_FEATURE_CABINET_FILE,
        ALAccessControlConstants.VALUE_ACL_LIST);
    } catch (ALPermissionException e) {
      throw new Exception();
    }
    try {
      int fileindex = rundata.getParameters().getInt(ALEipConstants.ENTITY_ID);

      EipTCabinetFile cabinetfile =
        Database.get(EipTCabinetFile.class, Integer.valueOf(fileindex));

      doFileCheckView(rundata, cabinetfile);

      super.setFilePath(CabinetUtils.getSaveDirPath(Database.getDomainName())
        + cabinetfile.getFilePath());
      super.setFileName(cabinetfile.getFileName());
      super.doOutput(rundata);

      cabinetfile.setCounter(cabinetfile.getCounter() + 1);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        cabinetfile.getFileId(),
        ALEventlogConstants.PORTLET_TYPE_CABINET_FILE,
        cabinetfile.getFileTitle() + "  (" + cabinetfile.getFileName() + ")",
        ALActionEventlogConstants.EVENT_MODE_DOWNLOAD);

    } catch (ALPermissionException e) {
      throw new Exception();
    } catch (Exception e) {
      logger.error("[ERROR]" + e);
    }

  }

  private boolean doFileCheckView(RunData rundata, EipTCabinetFile cabinetfile)
      throws ALPermissionException {
    userid = ALEipUtils.getUserId(rundata);
    cabinetfolder = cabinetfile.getEipTCabinetFolder();

    switch (Integer.parseInt(cabinetfolder.getPublicFlag())) {
      case 0:
        return true;
      case 1:
        return true;
      case 2:
        if (checkFolderMap()) {
          return true;
        } else {
          throw new ALPermissionException();
        }
      case 3:
        if (checkFolderMap()) {
          return true;
        } else {
          throw new ALPermissionException();
        }
      default:
        throw new ALPermissionException();
    }
  }

  private boolean checkFolderMap() {
    @SuppressWarnings("unchecked")
    List<EipTCabinetFolderMap> folderMap =
      cabinetfolder.getEipTCabinetFolderMap();

    for (EipTCabinetFolderMap map : folderMap) {
      if (map.getUserId().intValue() == userid) {
        return true;
      }
    }

    return false;
  }
}

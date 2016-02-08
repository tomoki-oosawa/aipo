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
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cabinet.util.CabinetUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFile;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFolder;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.eventlog.action.ALActionEventlogConstants;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;

/**
 * 共有フォルダのファイルの一覧を処理するクラスです。 <br />
 *
 */
public class CabinetFileRawScreen extends FileuploadRawScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CabinetFileRawScreen.class.getName());

  private EipTCabinetFolder cabinetfolder;

  @Override
  protected void init(RunData rundata) throws Exception {
    int fileindex = rundata.getParameters().getInt(ALEipConstants.ENTITY_ID);
    EipTCabinetFile file =
      Database.get(EipTCabinetFile.class, Integer.valueOf(fileindex));
    if (null == file) {
      throw new ALPageNotFoundException();
    }
    doFileCheckView(rundata, file);

    setFilePath(CabinetUtils.getSaveDirPath(Database.getDomainName())
      + file.getFilePath());
    setFileName(file.getFileName());
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

      if (cabinetfile != null) {

        super.doOutput(rundata);

        cabinetfile.setCounter(cabinetfile.getCounter() + 1);
        Database.commit();

        // イベントログに保存
        ALEventlogFactoryService.getInstance().getEventlogHandler().log(
          cabinetfile.getFileId(),
          ALEventlogConstants.PORTLET_TYPE_CABINET_FILE,
          cabinetfile.getFileTitle() + "  (" + cabinetfile.getFileName() + ")",
          ALActionEventlogConstants.EVENT_MODE_DOWNLOAD);
      }

    } catch (ALPermissionException e) {
      throw new Exception();
    } catch (Exception e) {
      Database.rollback();
      logger.error("CabinetFileRawScreen.doOutput", e);
    }

  }

  private boolean doFileCheckView(RunData rundata, EipTCabinetFile cabinetfile)
      throws ALPermissionException {
    cabinetfolder = cabinetfile.getEipTCabinetFolder();

    if (CabinetUtils.isAccessibleFolder(cabinetfolder.getFolderId(), rundata)) {
      return true;
    } else {
      throw new ALPermissionException();
    }
  }
}

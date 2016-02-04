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

import com.aimluck.eip.cayenne.om.portlet.EipTProjectTask;
import com.aimluck.eip.cayenne.om.portlet.EipTProjectTaskFile;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.project.util.ProjectFile;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;

/**
 * タスクの添付ファイルの一覧を処理するクラスです。
 */
public class ProjectTaskFileRawScreen extends FileuploadRawScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ProjectTaskFileRawScreen.class.getName());

  /** ファイル操作オブジェクト */
  private final ProjectFile<EipTProjectTaskFile, EipTProjectTask> pfile =
    new ProjectFile<EipTProjectTaskFile, EipTProjectTask>(
      EipTProjectTaskFile.class);

  @Override
  protected void init(RunData rundata) throws Exception {
    EipTProjectTaskFile projectTaskfile =
      pfile.getEipTFile(rundata, EipTProjectTaskFile.FILE_ID_PK_COLUMN);

    setFilePath(ProjectFile.getSaveDirPath(projectTaskfile
      .getOwnerId()
      .intValue())
      + projectTaskfile.getFilePath());
    setFileName(projectTaskfile.getFileName());
  }

  /**
   *
   * @param rundata
   *          RunData
   * @throws Exception
   *           例外
   */
  @Override
  protected void doOutput(RunData rundata) throws Exception {
    try {
      doCheckAclPermission(
        rundata,
        ALAccessControlConstants.POERTLET_FEATURE_MSGBOARD_TOPIC_OTHER,
        ALAccessControlConstants.VALUE_ACL_LIST);
    } catch (ALPermissionException e) {
      try {
        doCheckAclPermission(
          rundata,
          ALAccessControlConstants.POERTLET_FEATURE_MSGBOARD_TOPIC,
          ALAccessControlConstants.VALUE_ACL_LIST);
      } catch (ALPermissionException ex) {
        throw new Exception();
      }
    }
    try {
      EipTProjectTaskFile projectTaskfile =
        pfile.getEipTFile(rundata, EipTProjectTaskFile.FILE_ID_PK_COLUMN);

      super.setFilePath(ProjectFile.getSaveDirPath(projectTaskfile
        .getOwnerId()
        .intValue())
        + projectTaskfile.getFilePath());
      super.setFileName(projectTaskfile.getFileName());
      super.doOutput(rundata);
    } catch (ALPermissionException e) {
      throw new Exception();
    } catch (Exception e) {
      logger.error("[ERROR]", e);
    }
  }
}

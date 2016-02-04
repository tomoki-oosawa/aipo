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

import com.aimluck.eip.cayenne.om.portlet.EipTReportFile;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.report.util.ReportUtils;

/**
 * 報告書の添付ファイルの一覧を処理するクラスです。 <br />
 */
public class ReportFileRawScreen extends FileuploadRawScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ReportFileRawScreen.class.getName());

  @Override
  protected void init(RunData rundata) throws Exception {
    EipTReportFile reportfile = ReportUtils.getEipTReportFile(rundata);

    setFilePath(ReportUtils.getSaveDirPath(Database.getDomainName(), reportfile
      .getOwnerId()
      .intValue())
      + reportfile.getFilePath());
    setFileName(reportfile.getFileName());
  }

  /**
   *
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata) throws Exception {
    try {
      EipTReportFile reportfile = ReportUtils.getEipTReportFile(rundata);

      super.setFilePath(ReportUtils.getSaveDirPath(
        Database.getDomainName(),
        reportfile.getOwnerId().intValue())
        + reportfile.getFilePath());
      super.setFileName(reportfile.getFileName());
      super.doOutput(rundata);
    } catch (Exception e) {
      logger.error("ReportFileRawScreen.doOutput", e);
    }
  }
}

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

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardFile;
import com.aimluck.eip.msgboard.util.MsgboardUtils;

/**
 * 掲示板トピックの添付ファイルのサムネイルを処理するクラスです。
 */
public class MsgboardTopicFileThumbnailScreen extends FileuploadThumbnailScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MsgboardTopicFileThumbnailScreen.class.getName());

  /**
   * 
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata) throws Exception {
    try {
      EipTMsgboardFile msgboardfile =
        MsgboardUtils.getEipTMsgboardFile(rundata);

      super.setFile(msgboardfile.getFileThumbnail());
      super.setFileName(msgboardfile.getFileName());
      super.doOutput(rundata);
    } catch (Exception e) {
      logger.error("MsgboardTopicFileThumbnailScreen.doOutput", e);
    }
  }
}

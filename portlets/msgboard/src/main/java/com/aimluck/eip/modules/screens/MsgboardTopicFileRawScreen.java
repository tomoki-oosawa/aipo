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

import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardCategoryMap;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardFile;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardTopic;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.msgboard.util.MsgboardUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 掲示板トピックの添付ファイルの一覧を処理するクラスです。
 */
public class MsgboardTopicFileRawScreen extends FileuploadRawScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MsgboardTopicFileRawScreen.class.getName());

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
      EipTMsgboardFile msgboardfile =
        MsgboardUtils.getEipTMsgboardFile(rundata);

      doFileCheckView(rundata, msgboardfile);

      super.setFilePath(MsgboardUtils.getSaveDirPath(
        Database.getDomainName(),
        msgboardfile.getOwnerId().intValue())
        + msgboardfile.getFilePath());
      super.setFileName(msgboardfile.getFileName());
      super.doOutput(rundata);
    } catch (ALPermissionException e) {
      throw new Exception();
    } catch (Exception e) {
      logger.error("MsgboardTopicFileRawScreen.doOutput", e);
    }
  }

  private boolean doFileCheckView(RunData rundata, EipTMsgboardFile msgboardfile)
      throws ALPermissionException {
    int userid = ALEipUtils.getUserId(rundata);
    EipTMsgboardTopic msgboardtopic = msgboardfile.getEipTMsgboardTopic();
    EipTMsgboardCategory msgboardcategory =
      msgboardtopic.getEipTMsgboardCategory();

    if ("T".equals(msgboardcategory.getPublicFlag())) {
      return true;
    } else {
      @SuppressWarnings("unchecked")
      List<EipTMsgboardCategoryMap> categoryMap =
        msgboardcategory.getEipTMsgboardCategoryMaps();
      for (EipTMsgboardCategoryMap map : categoryMap) {
        if (map.getUserId().intValue() == userid) {
          return true;
        }
      }
      throw new ALPermissionException();
    }

  }
}

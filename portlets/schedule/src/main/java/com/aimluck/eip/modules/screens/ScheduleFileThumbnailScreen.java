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

import java.io.FileNotFoundException;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleFile;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;

/**
 * スケジュールの添付ファイルのサムネイルを処理するクラスです。
 */
public class ScheduleFileThumbnailScreen extends FileuploadThumbnailScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleFileThumbnailScreen.class.getName());

  /**
   *
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata) throws Exception {
    try {
      EipTScheduleFile schedulefile =
        ScheduleUtils.getEipTScheduleFile(rundata);

      {// アクセス権限の確認
       // 他人の非公開の予定にはアクセス出来ない
        EipTSchedule record = schedulefile.getEipTSchedule();
        if (!ScheduleUtils.hasAuthorityForScheduleDetail(
          rundata,
          null,
          record,
          ScheduleUtils.SCHEDULEMAP_TYPE_USER)) {
          throw new FileNotFoundException();
        }

        // スケジュールポートレットへのアクセス権限の確認
        String aclPortletFeature = "";
        rundata.getParameters().remove(ALEipConstants.ENTITY_ID);
        rundata.getParameters().add(
          ALEipConstants.ENTITY_ID,
          record.getScheduleId().toString());
        if (!ScheduleUtils.hasRelation(rundata)) {
          aclPortletFeature =
            ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_OTHER;
        } else {
          aclPortletFeature =
            ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_SELF;
        }
        doCheckAclPermission(
          rundata,
          aclPortletFeature,
          ALAccessControlConstants.VALUE_ACL_DETAIL);
      }

      EipTSchedule record = schedulefile.getEipTSchedule();
      if (!ScheduleUtils.hasAuthorityForScheduleDetail(
        rundata,
        null,
        record,
        ScheduleUtils.SCHEDULEMAP_TYPE_USER)) {
        throw new FileNotFoundException();
      }

      super.setFile(schedulefile.getFileThumbnail());
      super.setFileName(schedulefile.getFileName());
      super.doOutput(rundata);
    } catch (Exception e) {
      logger.error("ScheduleFileThumbnailScreen.doOutput", e);
    }
  }
}

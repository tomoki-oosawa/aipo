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
package com.aimluck.eip.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;

import com.aimluck.eip.cayenne.om.portlet.EipTTimeline;
import com.aimluck.eip.cayenne.om.portlet.EipTTimelineFile;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.storage.ALStorageService;

/**
 * タイムラインへの自動投稿を処理するクラスです。
 */
public class ALTimelineUtils {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALTimelineUtils.class.getName());

  public static boolean hasTimelinePost() {
    return Database.query(EipTTimeline.class).getCount() > 0;
  }

  public static void postTimeline(JetspeedRunData data, int uid) {
    Date now = new Date();
    EipTTimeline timeline = Database.create(EipTTimeline.class);
    timeline.setParentId(0);
    timeline.setOwnerId(uid);
    timeline.setExternalId(null);
    timeline.setNote(ALLocalizationUtils.getl10n("TIMELINE_GUIDE_MESSAGE"));
    timeline.setTimelineType("T");
    timeline.setParams(null);
    timeline.setPinned("F");
    timeline.setCreateDate(now);
    timeline.setUpdateDate(now);

    Database.commit();

    try {
      uploadTimelineImage(
        data.getServletContext(),
        uid,
        0,
        ALLocalizationUtils.getl10n("TIMELINE_GUIDE_IMAGE_1"),
        "/images/first/guide01.png",
        "/images/first/guide01s.jpg",
        timeline);

      uploadTimelineImage(
        data.getServletContext(),
        uid,
        1,
        ALLocalizationUtils.getl10n("TIMELINE_GUIDE_IMAGE_2"),
        "/images/first/guide02.png",
        "/images/first/guide02s.jpg",
        timeline);

      uploadTimelineImage(
        data.getServletContext(),
        uid,
        2,
        ALLocalizationUtils.getl10n("TIMELINE_GUIDE_IMAGE_3"),
        "/images/first/guide03.png",
        "/images/first/guide03s.jpg",
        timeline);
    } catch (FileNotFoundException e) {
      logger.error("ALTimelineUtils", e);
    } catch (IOException e) {
      logger.error("ALTimelineUtils", e);
    }
  }

  private static void uploadTimelineImage(ServletContext servletContext,
      int uid, int index, String title, String filePath, String sFilePath,
      EipTTimeline timeline) throws FileNotFoundException, IOException {
    Date now = new Date();
    String filename = index + "_" + String.valueOf(System.nanoTime());
    File tmpFile = new File(servletContext.getRealPath(sFilePath));
    byte[] imageInBytes = IOUtils.toByteArray(new FileInputStream(tmpFile));

    EipTTimelineFile file = Database.create(EipTTimelineFile.class);
    file.setOwnerId(uid);
    file.setFileName(title);
    file.setFilePath(getRelativePath(filename));
    file.setFileThumbnail(imageInBytes);
    file.setEipTTimeline(timeline);
    file.setCreateDate(now);
    file.setUpdateDate(now);

    Database.commit();

    tmpFile = new File(servletContext.getRealPath(filePath));
    imageInBytes = IOUtils.toByteArray(new FileInputStream(tmpFile));

    ALStorageService.createNewFile(
      new ByteArrayInputStream(imageInBytes),
      JetspeedResources.getString("aipo.filedir", "")
        + ALStorageService.separator()
        + Database.getDomainName()
        + ALStorageService.separator()
        + JetspeedResources.getString("aipo.timeline.categorykey", "")
        + ALStorageService.separator()
        + uid
        + ALStorageService.separator()
        + filename);
  }

  private static String getRelativePath(String fileName) {
    return new StringBuffer().append("/").append(fileName).toString();
  }
}

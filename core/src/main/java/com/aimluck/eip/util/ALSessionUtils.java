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

package com.aimluck.eip.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

/**
 * Aimluck Session のユーティリティクラスです。 <br />
 * 
 */
public class ALSessionUtils {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALSessionUtils.class.getName());

  private static List<String> imageScreenList;

  private static List<String> jsonScreenList;

  public static List<String> getImageScreenList() {
    if (imageScreenList == null) {
      imageScreenList = new ArrayList<String>();
      imageScreenList.add("FileuploadFacePhotoScreen");
      imageScreenList.add("TimelineFileThumbnailScreen");
      imageScreenList.add("TimelineUrlThumbnailScreen");
      imageScreenList.add("BlogFileThumbnailScreen");
      imageScreenList.add("MsgboardTopicFileThumbnailScreen");
    }
    return imageScreenList;
  }

  public static List<String> getJsonScreenList() {
    if (jsonScreenList == null) {
      jsonScreenList = new ArrayList<String>();
    }
    return jsonScreenList;
  }

  public static boolean isImageRequest(RunData data) {
    String template = data.getParameters().getString("template");
    if (template == null) {
      return false;
    }
    if (getImageScreenList().contains(template)) {
      data.getRequest().setAttribute(
        "com.aimluck.eip.util.ALSessionUtils.isImageRequest",
        true);
      return true;
    }
    return false;
  }

  public static boolean isJsonScreen(RunData data) {
    String template = data.getParameters().getString("template");
    if (template == null) {
      return false;
    }

    if (template.endsWith("JSONScreen")) {
      return true;
    }

    // if (getJsonScreenList().contains(template)) {
    // return true;
    // }
    return false;
  }

}

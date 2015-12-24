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

import java.text.MessageFormat;
import java.util.Locale;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.services.customlocalization.ALLocalizationTool;

public class ALLocalizationUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALLocalizationUtils.class.getName());

  public static ALLocalizationTool createLocalization(RunData rundata) {
    ALLocalizationTool tool = ALEipManager.getInstance().getLocalizationTool();
    if (tool == null) {
      tool = new ALLocalizationTool();
      tool.init(rundata);
      ALEipManager.getInstance().setLocalizationTool(tool);
      Locale.setDefault(tool.getLocale());
    }
    return tool;
  }

  public static String getl10n(String key) {
    ALLocalizationTool tool = ALEipManager.getInstance().getLocalizationTool();
    if (tool == null) {
      tool = new ALLocalizationTool();
      RunData rundata = ALSessionUtils.getRundata();
      if (rundata != null) {
        tool.init(rundata);
      }
      ALEipManager.getInstance().setLocalizationTool(tool);
    }
    return tool.get(key);
  }

  public static String getl10nFormat(String key, Object... values) {
    return MessageFormat.format(getl10n(key), values);
  }
}
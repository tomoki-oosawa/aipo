package com.aimluck.eip.util;

import java.text.MessageFormat;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.localization.LocalizationTool;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.common.ALEipManager;

public class ALLocalizationUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALLocalizationUtils.class.getName());

  public static LocalizationTool createLocalization(RunData rundata) {
    LocalizationTool tool = ALEipManager.getInstance().getLocalizationTool();
    if (tool == null) {
      tool = new LocalizationTool();
      tool.init(rundata);
      ALEipManager.getInstance().setLocalizationTool(tool);
    }
    return tool;
  }

  public static String getl10n(String key) {
    LocalizationTool tool = ALEipManager.getInstance().getLocalizationTool();
    if (tool == null) {
      tool = new LocalizationTool();
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
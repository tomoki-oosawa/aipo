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
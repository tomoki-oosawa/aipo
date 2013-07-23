package com.aimluck.eip.services.customlocalization;

import java.util.Locale;
import java.util.MissingResourceException;

import org.apache.jetspeed.services.customlocalization.CustomLocalization;
import org.apache.jetspeed.services.customlocalization.CustomLocalizationTool;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 *
 */
public class ALLocalizationTool extends CustomLocalizationTool {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALLocalizationTool.class.getName());

  @Override
  public String get(String key) {
    try {
      String s =
        CustomLocalization.getString(getBundleName(null), getLocale(), key);
      return s;
    } catch (MissingResourceException ignore) {
      try {
        String s =
          CustomLocalization.getString(
            getBundleName(null),
            Locale.JAPANESE,
            key);
        return s;
      } catch (MissingResourceException noKey) {
        logger.error("Exception", noKey);
      }
    }
    return null;
  }
}

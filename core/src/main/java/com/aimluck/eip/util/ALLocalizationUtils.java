package com.aimluck.eip.util;

import java.text.MessageFormat;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.localization.LocalizationTool;
import org.apache.turbine.util.RunData;

public class ALLocalizationUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALLocalizationUtils.class.getName());

  private static LocalizationTool locale;

  public static LocalizationTool createLocalization(RunData rundata) {
    LocalizationTool lt = new LocalizationTool();
    lt.init(rundata);
    locale = lt;
    return locale;
  }

  public static String getl10n(String key) {
    if (locale == null) {//
      logger.info("言語が設定される前に実行されました。");
      locale = new LocalizationTool();// 言語設定なし
    }
    return locale.get(key);
  }

  public static String getl10nFormat(String key, Object... values) {
    return MessageFormat.format(getl10n(key), values);
  }
}
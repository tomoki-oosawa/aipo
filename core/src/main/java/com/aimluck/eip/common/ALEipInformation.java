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
package com.aimluck.eip.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;

public class ALEipInformation {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALEipInformation.class.getName());

  /** デフォルトエンコーディングを表わすシステムプロパティのキー */
  protected static final String KEY_ENCODING = "content.defaultencoding";

  protected static final String DATE_KEY = "aipo.information.date";

  protected static final String TEXT_KEY = "aipo.information.text";

  protected static final String INFORMATION_CONFIG_CONCATE = "=";

  public static final String INFORMATION_COOKIE_PREFIX = "aui_info_disp_";

  /** お知らせ */
  public static final String INFORMATION_TITLE = JetspeedResources
    .getString("aipo.alias");

  private static ALEipInformation information = new ALEipInformation();

  /** お知らせが書かれたファイルへのパス */
  private final String FILE_INFORMATION_DEFAULT = (JetspeedResources.getString(
    "aipo.home",
    "").equals("")) ? "" : JetspeedResources.getString("aipo.home", "")
    + File.separator
    + "conf"
    + File.separator
    + "information.properties";

  private String informationText = null;

  private String informationDate = null;

  private String informationCookie = null;

  /**
   * コンストラクタ
   */
  private ALEipInformation() {
    informationText = "";
    informationDate = "";
    informationCookie = "";

    loadInformations();
  }

  /**
   * クラス ALEipInformation のインスタンスを取得する．
   * 
   * @return クラス ALEipInformation のインスタンス
   */
  public static ALEipInformation getInstance() {
    return information;
  }

  /**
   * お知らせ情報をテキストファイルから読み込む． <br>
   */
  private void loadInformations() {
    File defaultFile = new File(FILE_INFORMATION_DEFAULT);

    informationText = "";
    informationDate = "";
    informationCookie = "";

    BufferedReader reader = null;

    try {
      if (defaultFile.exists()) {
        reader =
          new BufferedReader(new InputStreamReader(new FileInputStream(
            defaultFile), ALEipConstants.DEF_CONTENT_ENCODING));
        loadInformation(reader);
      }
    } catch (Exception ex) {
      logger.error("ALEipInformation.loadInformations", ex);
      return;
    }
  }

  /**
   * お知らせ情報をテキストファイルから読み込む．
   * 
   * @param reader
   * @param list
   * @throws Exception
   */
  private void loadInformation(BufferedReader reader) throws Exception {
    if (reader == null) {
      return;
    }

    String line = null;
    while ((line = reader.readLine()) != null) {
      if (line.startsWith("#")) {
        continue;
      }
      if (line.startsWith(DATE_KEY)) {
        String dayStr =
          line.replaceAll(DATE_KEY + INFORMATION_CONFIG_CONCATE, "");
        if (dayStr != null && !"".equals(dayStr)) {
          informationDate = dayStr;
          informationCookie = INFORMATION_COOKIE_PREFIX + dayStr;
        }
      }
      if (line.startsWith(TEXT_KEY)) {
        String dayStr =
          line.replaceAll(TEXT_KEY + INFORMATION_CONFIG_CONCATE, "");
        if (dayStr != null && !"".equals(dayStr)) {
          informationText = dayStr;
        }
      }
    }
  }

  public String getInformationText() {
    return informationText;
  }

  public String getInformationDate() {
    return informationDate;
  }

  public String getInformationCookie() {
    return informationCookie;
  }

}

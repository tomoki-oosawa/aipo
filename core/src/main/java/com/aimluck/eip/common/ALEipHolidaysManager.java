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
package com.aimluck.eip.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;

/**
 * 祝日を保持するシングルトンクラスです。 <br />
 * この実装は同期化されない点に注意してください。
 * 
 */
public class ALEipHolidaysManager {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALEipHolidaysManager.class.getName());

  /** デフォルトエンコーディングを表わすシステムプロパティのキー */
  protected static final String KEY_ENCODING = "content.defaultencoding";

  private static ALEipHolidaysManager manager = new ALEipHolidaysManager();

  /** デフォルトの祝日が書かれたファイルへのパス */
  private final String FILE_HOLI_DAYS_DEFAULT = (JetspeedResources.getString(
    "aipo.home",
    "").equals("")) ? "" : JetspeedResources.getString("aipo.home", "")
    + File.separator
    + "conf"
    + File.separator
    + "holidays_default.properties";

  /** ユーザ定義の祝日が書かれたファイルへのパス */
  private final String FILE_HOLI_DAYS_USER = (JetspeedResources.getString(
    "aipo.home",
    "").equals("")) ? "" : JetspeedResources.getString("aipo.home", "")
    + File.separator
    + "conf"
    + File.separator
    + "holidays_user.properties";

  /** デフォルトの祝日一覧 */
  private Map<String, ALHoliday> defaultHolidays = null;

  /** ユーザ定義の祝日一覧 */
  private Map<String, ALHoliday> userHolidays = null;

  /**
   * コンストラクタ
   */
  private ALEipHolidaysManager() {
    defaultHolidays = new HashMap<String, ALHoliday>();
    userHolidays = new HashMap<String, ALHoliday>();

    loadHolidays();
  }

  /**
   * クラス ALEipHolidaysManager のインスタンスを取得する．
   * 
   * @return クラス ALEipHolidaysManager のインスタンス
   */
  public static ALEipHolidaysManager getInstance() {
    return manager;
  }

  /**
   * 指定した日付が祝日であるかを検証する．
   * 
   * @param date
   *          検証する日付
   * @return 指定した日付の祝日情報．指定した日付が祝日ではない場合は，null．
   */
  public ALHoliday isHoliday(Date date) {
    if (date == null) {
      return null;
    }

    ALHoliday holiDay = null;

    holiDay = isHoliday(userHolidays, date);
    if (holiDay == null) {
      holiDay = isHoliday(defaultHolidays, date);
    }
    return holiDay;
  }

  /**
   * 指定した日付が祝日であるかを検証する．
   * 
   * @param list
   * @param year
   * @param month
   * @param day
   * @return
   */
  private ALHoliday isHoliday(Map<String, ALHoliday> list, Date date) {
    String key = new SimpleDateFormat("yyyy-MM-dd").format(date);
    return list.get(key);
  }

  /**
   * 祝日情報をテキストファイルから読み込む． <br>
   * 祝日情報のフォーマット：祝日名,祝日日時 <br>
   * 例）文化の日,2004-11-03
   */
  private void loadHolidays() {
    File defaultFile = new File(FILE_HOLI_DAYS_DEFAULT);
    File userFile = new File(FILE_HOLI_DAYS_USER);

    defaultHolidays.clear();
    userHolidays.clear();

    BufferedReader reader = null;

    try {
      if (defaultFile.exists()) {
        // デフォルトの祝日を読み込む．
        reader =
          new BufferedReader(new InputStreamReader(new FileInputStream(
            defaultFile), ALEipConstants.DEF_CONTENT_ENCODING));
        loadHoliday(reader, defaultHolidays);
      }

      if (userFile.exists()) {
        // ユーザ定義の祝日を読み込む．
        reader =
          new BufferedReader(new InputStreamReader(
            new FileInputStream(userFile),
            ALEipConstants.DEF_CONTENT_ENCODING));
        loadHoliday(reader, userHolidays);
      }

      /*-
      Comparator<ALHoliday> comp = getHolidaysComparator();
      if (comp != null) {
        Collections.sort(defaultHolidays, comp);
        Collections.sort(userHolidays, comp);
      }
       */
    } catch (Exception ex) {
      logger.error("ALEipHolidaysManager.loadHolidays", ex);
      return;
    }
  }

  /**
   * 祝日情報をテキストファイルから読み込む．
   * 
   * @param reader
   * @param list
   * @throws Exception
   */
  private void loadHoliday(BufferedReader reader, Map<String, ALHoliday> list)
      throws Exception {
    if (reader == null) {
      return;
    }

    List<String> dummyList = new ArrayList<String>();
    StringTokenizer st = null;
    ALHoliday holiDay = null;
    String line = null;
    while ((line = reader.readLine()) != null) {
      if (line.startsWith("#")) {
        continue;
      }
      st = new StringTokenizer(line, ",");
      if (st.countTokens() == 2) {
        String nameStr = st.nextToken();
        String dayStr = st.nextToken();
        holiDay = new ALHoliday(nameStr, dayStr);
        if (holiDay.getDay().validate(dummyList)) {
          String key =
            new SimpleDateFormat("yyyy-MM-dd").format(holiDay
              .getDay()
              .getValue()
              .getDate());
          list.put(key, holiDay);
        }
      }
    }
  }

  /**
   * 祝日情報を日付で昇順に並び替える比較関数を取得する．
   * 
   * @return 祝日情報を日付で昇順に並び替える比較関数
   */
  protected Comparator<ALHoliday> getHolidaysComparator() {
    Comparator<ALHoliday> com = new Comparator<ALHoliday>() {
      @Override
      public int compare(ALHoliday obj0, ALHoliday obj1) {
        int ret = 0;
        try {
          Date day0 = obj0.getDay().getValue().getDate();
          Date day1 = obj1.getDay().getValue().getDate();

          // 日付の昇順
          if ((ret = day0.compareTo(day1)) == 0) {
            // 日付が同じ場合，祝日名の昇順
            String name0 = (obj0).getName().getValue();
            String name1 = (obj1).getName().getValue();
            ret = name0.compareTo(name1);
          }
        } catch (RuntimeException ex) {
          // RuntimeException
          logger.error("ALEipHolidaysManager.getHolidaysComparator", ex);
          return -1;
        } catch (Exception ex) {
          logger.error("ALEipHolidaysManager.getHolidaysComparator", ex);
          return -1;
        }
        return ret;
      }
    };
    return com;
  }

}

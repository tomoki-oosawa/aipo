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
package com.aimluck.eip.modules.screens;

import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 *
 */
public class FileIOScheduleCsvFileScreen extends ALCSVScreen {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileIOScheduleCsvFileScreen.class.getName());

  /**
   * 
   * @param rundata
   * @return
   */
  @Override
  protected String getContentType(RunData rundata) {
    return "application/octet-stream";
  }

  /**
   * 
   * @param rundata
   * @return
   * @throws Exception
   */
  @Override
  protected String getCSVString(RunData rundata) throws Exception {
    String LINE_SEPARATOR = System.getProperty("line.separator");
    try {
      StringBuffer sb = new StringBuffer();
      sb.append("開始日,開始時刻,終了日,終了時刻,場所,予定,内容,名前,ユーザー名");
      sb.append(LINE_SEPARATOR);
      sb
        .append("\"2011/07/09\",\"16:30\",\"2011/07/09\",\"17:00\",\"会議室\",\"営業会議\",\"営業戦略と一週間の予定を確認\",\"山田 太郎\",\"yamada\"");
      sb.append(LINE_SEPARATOR);
      sb
        .append("\"2011/07/06\",\"14:20\",\"2011/07/06\",\"15:00\",\"会議室\",\"総務会議\",\"一週間の反省と来週の予定を確認\",\"鈴木 太郎\",\"suzuki1\"");
      sb.append(LINE_SEPARATOR);
      sb
        .append("\"2011/06/29\",\"08:30\",\"2011/06/29\",\"09:30\",\"会議室\",\"部長会議\",\"一ヶ月間の反省と今後の予定を確認\",\"鈴木 花子\",\"suzuki0\"");
      sb.append(LINE_SEPARATOR);
      sb
        .append("\"2011/11/01\",\"\",\"2011/11/30\",\"\",\"\",\"開発期間\",\"製品の開発期間\",\"山田 太郎, 鈴木 太郎\",\"yamada, suzuki0\"");
      sb.append(LINE_SEPARATOR);

      return sb.toString();
    } catch (Exception e) {
      logger.error("FileIOScheduleCsvFileScreen.getCSVString", e);
      return null;
    }
  }

  /**
   * 
   * @param rundata
   * @return
   * @throws Exception
   */
  protected String getCSVStringDB(RunData rundata) throws Exception {
    String LINE_SEPARATOR = System.getProperty("line.separator");
    try {
      StringBuffer sb = new StringBuffer();
      sb.append("開始日,開始時刻,終了日,終了時刻,場所,予定,内容,名前");
      sb.append(LINE_SEPARATOR);
      SelectQuery<EipTSchedule> query = Database.query(EipTSchedule.class);
      List<EipTSchedule> list = query.fetchList();
      EipTSchedule rec;
      ALDateTimeField time = new ALDateTimeField();
      for (int i = 0; i < list.size(); i++) {
        rec = list.get(i);
        if (!rec.getRepeatPattern().equals("N")) {
          continue;
        }
        time.setValue(rec.getStartDate());
        sb.append(
          "\""
            + time.getYear()
            + "/"
            + time.getMonth()
            + "/"
            + time.getDay()
            + "\"").append(",");
        sb
          .append("\"" + time.getHour() + ":" + time.getMinute() + "\"")
          .append(",");
        time.setValue(rec.getEndDate());
        sb.append(
          "\""
            + time.getYear()
            + "/"
            + time.getMonth()
            + "/"
            + time.getDay()
            + "\"").append(",");
        sb
          .append("\"" + time.getHour() + ":" + time.getMinute() + "\"")
          .append(",");
        sb.append("\"" + makeOutputItem(rec.getPlace()) + "\"").append(",");
        sb.append("\"" + makeOutputItem(rec.getName()) + "\"").append(",");
        sb.append("\"" + makeOutputItem(rec.getNote()) + "\"").append(",");
        try {
          ALEipUser user = ALEipUtils.getALEipUser(rec.getCreateUserId());
          sb.append("\"" + user.getAliasName() + "\"");
        } catch (Exception e) {
          sb.append(LINE_SEPARATOR);
          continue;
        }
        sb.append(LINE_SEPARATOR);
      }
      return sb.toString();
    } catch (Exception e) {
      logger.error("FileIOScheduleCsvFileScreen.getCSVStringDB", e);
      return null;
    }
  }

  @Override
  protected String getFileName() {
    return ALOrgUtilsService.getAlias() + "_schedules.csv";
  }
}

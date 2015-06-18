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

import com.aimluck.eip.cayenne.om.account.EipMPost;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;

/**
 *
 *
 */
public class FileIOAccountPostCsvFileScreen extends ALCSVScreen {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileIOAccountPostCsvFileScreen.class.getName());

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
   */
  @Override
  protected String getCSVString(RunData rundata) throws Exception {
    String LINE_SEPARATOR = System.getProperty("line.separator");
    try {
      StringBuffer sb = new StringBuffer();
      sb.append("部署名,郵便番号,住所,電話番号,内線番号,Fax番号");
      sb.append(LINE_SEPARATOR);
      sb.append("業務部,111-1111,東京都○○,99-99-99,9999,99-99-99");
      sb.append(LINE_SEPARATOR);
      sb.append("営業部,111-1111,,99-99-99,9999,99-99-99");
      sb.append(LINE_SEPARATOR);

      return sb.toString();
    } catch (Exception e) {
      logger.error("FileIOAccountPostCsvFileScreen.getCSVString", e);
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
      sb.append("部署名,郵便番号,住所,電話番号,内線番号,Fax番号");
      sb.append(LINE_SEPARATOR);
      EipMPost rec0;
      SelectQuery<EipMPost> query0 = Database.query(EipMPost.class);
      List<EipMPost> list = query0.fetchList();
      for (int i = 0; i < list.size(); i++) {
        rec0 = list.get(i);
        sb.append("\"" + makeOutputItem(rec0.getPostName()) + "\"").append(",");
        sb.append("\"" + makeOutputItem(rec0.getZipcode()) + "\"").append(",");
        sb.append("\"" + makeOutputItem(rec0.getAddress()) + "\"").append(",");
        sb.append("\"" + makeOutputItem(rec0.getOutTelephone()) + "\"").append(
          ",");
        sb.append("\"" + makeOutputItem(rec0.getInTelephone()) + "\"").append(
          ",");
        sb.append("\"" + makeOutputItem(rec0.getFaxNumber()) + "\"");
        sb.append(LINE_SEPARATOR);

      }
      return sb.toString();
    } catch (Exception e) {
      logger.error("FileIOAccountPostCsvFileScreen.getCSVStringDB", e);
      return null;
    }
  }

  @Override
  protected String getFileName() {
    return ALOrgUtilsService.getAlias() + "_post.csv";
  }
}

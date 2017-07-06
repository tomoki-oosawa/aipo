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
package com.aimluck.eip.modules.screens;

import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.portlet.EipMAddressbookCompany;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;

/**
 *
 *
 */
public class FileIOAddressBookCompanyCsvFileScreen extends ALCSVScreen {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileIOAddressBookCompanyCsvFileScreen.class.getName());

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
      sb.append("会社名,部課,フリガナ（会社名）,郵便番号,住所,会社電話番号,会社Fax,URL");
      sb.append(LINE_SEPARATOR);

      sb
        .append("やまだ商事,営業部,ヤマダショウジ,111-1111,東京都○○,99-9999-9999,99-9999-9999,http://");
      sb.append(LINE_SEPARATOR);
      sb
        .append("イトウ水産,業務部,イトウスイサン,222-2222,東京都○○,99-9999-9998,99-9999-9998,http://");
      sb.append(LINE_SEPARATOR);
      sb.append("たなか建設,,タナカケンセツ,,,99-9999-9999,99-9999-9999,");
      sb.append(LINE_SEPARATOR);

      return sb.toString();
    } catch (Exception e) {
      logger.error("FileIOAddressBookCompanyCsvFileScreen.getCSVstring", e);
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
      sb.append("会社名,部課,フリガナ（会社名）,郵便番号,住所,会社電話番号,会社Fax,URL");
      sb.append(LINE_SEPARATOR);
      EipMAddressbookCompany rec0;
      SelectQuery<EipMAddressbookCompany> query0 =
        Database.query(EipMAddressbookCompany.class);
      List<EipMAddressbookCompany> list = query0.fetchList();
      for (int i = 0; i < list.size(); i++) {
        rec0 = list.get(i);
        sb.append("\"" + makeOutputItem(rec0.getCompanyName()) + "\"").append(
          ",");
        sb.append("\"" + makeOutputItem(rec0.getPostName()) + "\"").append(",");
        sb
          .append("\"" + makeOutputItem(rec0.getCompanyNameKana()) + "\"")
          .append(",");
        sb.append("\"" + makeOutputItem(rec0.getZipcode()) + "\"").append(",");
        sb.append("\"" + makeOutputItem(rec0.getAddress()) + "\"").append(",");
        sb
          .append("\"" + makeOutputItem(rec0.getTelephone()) + "\"")
          .append(",");
        sb
          .append("\"" + makeOutputItem(rec0.getFaxNumber()) + "\"")
          .append(",");
        sb.append("\"" + makeOutputItem(rec0.getUrl()) + "\"");
        sb.append(LINE_SEPARATOR);

      }
      return sb.toString();
    } catch (Exception e) {
      logger.error("FileIOAddressBookCompanyCsvFileScreen.getCSVString", e);
      return null;
    }
  }

  @Override
  protected String getFileName() {
    return ALOrgUtilsService.getAlias() + "_addressbook_company.csv";
  }
}

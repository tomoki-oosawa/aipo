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

import com.aimluck.eip.cayenne.om.portlet.EipMAddressbook;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbookCompany;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;

/**
 *
 */
public class FileIOAddressBookCsvFileScreen extends ALCSVScreen {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileIOAddressBookCsvFileScreen.class.getName());

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

      sb.append("名前（姓）,名前（名）,フリガナ（姓）,フリガナ（名）");
      sb.append(",役職,電話番号,携帯電話番号,E-mail,E-mail（携帯電話）");
      sb.append(",会社名,部課,フリガナ（会社名）,郵便番号,住所,会社電話番号,会社Fax,URL,備考");
      sb.append(LINE_SEPARATOR);
      sb.append("山田,太郎,ヤマダ,タロウ");
      sb.append(",営業部長,99-99-91,999-9999-9999,a@a.com,a@a.ne.jp");
      sb
        .append(",いとう貿易,営業部,イトウボウエキ,111-1111,東京都○○,99-99-99,99-99-99,http://,1990年より勤務");
      sb.append(LINE_SEPARATOR);
      sb.append("鈴木,花子,スズキ,ハナコ");
      sb.append(",業務部長,99-99-92,999-9999-9999,b@b.com,b@bne.jp");
      sb.append(",すずき製作所,業務部,スズキセイサクショ,,,,,,");
      sb.append(LINE_SEPARATOR);
      sb.append("鈴木,太郎,スズキ,タロウ");
      sb.append(",,99-99-91,999-9999-9999,c@c.com,c@c.ne.jp");
      sb.append(",,,,,,,,,,");
      sb.append(LINE_SEPARATOR);

      return sb.toString();
    } catch (Exception e) {
      logger.error("FileIOAddressBookCsvFileScreen.getCSVString", e);
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
      sb
        .append("名前（姓）,名前（名）,フリガナ（姓）,フリガナ（名）,役職,電話番号,携帯電話番号,E-mail,E-mail（携帯電話）,会社名,部課,フリガナ（会社名）,郵便番号,住所,会社電話番号,会社Fax,URL");
      sb.append(LINE_SEPARATOR);
      SelectQuery<EipMAddressbook> query0 =
        Database.query(EipMAddressbook.class);
      List<EipMAddressbook> list = query0.fetchList();
      EipMAddressbook rec0;
      EipMAddressbookCompany rec1;
      // SelectQuery query1 = Database.query(EipMAddressbookCompany.class);
      for (int i = 0; i < list.size(); i++) {
        rec0 = list.get(i);
        sb.append(rec0.getLastName()).append(",");
        sb.append(rec0.getFirstName()).append(",");
        sb.append(rec0.getLastNameKana()).append(",");
        sb.append(rec0.getFirstNameKana()).append(",");
        sb.append(rec0.getPositionName()).append(",");
        sb.append(rec0.getTelephone()).append(",");
        sb.append(rec0.getCellularPhone()).append(",");
        sb.append(rec0.getEmail()).append(",");
        sb.append(rec0.getCellularMail()).append(",");

        try {
          rec1 = rec0.getEipMAddressbookCompany();
        } catch (Exception e) {
          sb.append(",,,,,,,");
          sb.append(LINE_SEPARATOR);
          logger.error("[FileIOAddressBookCsvFileScreen]", e);
          continue;
        }
        if (rec1 != null) {
          sb
            .append("\"" + makeOutputItem(rec1.getCompanyName()) + "\"")
            .append(",");
          sb.append("\"" + makeOutputItem(rec1.getPostName()) + "\"").append(
            ",");
          sb
            .append("\"" + makeOutputItem(rec1.getCompanyNameKana()) + "\"")
            .append(",");
          sb
            .append("\"" + makeOutputItem(rec1.getZipcode()) + "\"")
            .append(",");
          sb
            .append("\"" + makeOutputItem(rec1.getAddress()) + "\"")
            .append(",");
          sb.append("\"" + makeOutputItem(rec1.getTelephone()) + "\"").append(
            ",");
          sb.append("\"" + makeOutputItem(rec1.getFaxNumber()) + "\"").append(
            ",");
          sb.append("\"" + makeOutputItem(rec1.getUrl()) + "\"");
          sb.append(LINE_SEPARATOR);
        } else {
          sb.append(",,,,,,,");
          sb.append(LINE_SEPARATOR);
        }
      }
      return sb.toString();
    } catch (Exception e) {
      logger.error("FileIOAddressBookCsvFileScreen.getCSVStringDB", e);
      return null;
    }
  }

  @Override
  protected String getFileName() {
    return ALOrgUtilsService.getAlias() + "_addressbook.csv";
  }
}

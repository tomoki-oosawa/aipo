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

import java.util.ListIterator;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.account.AccountPostResultData;
import com.aimluck.eip.cayenne.om.account.EipMPost;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 *
 */
public class AccountPostCsvExportScreen extends ALCSVScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountPostCsvExportScreen.class.getName());

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
   * ResultData に値を格納して返します。（一覧データ） <BR>
   *
   * @param obj
   * @return
   */
  protected AccountPostResultData getPostResultData(EipMPost record) {
    try {
      AccountPostResultData rd = new AccountPostResultData();
      rd.initField();
      rd.setPostName(record.getPostName());
      rd.setZipcode(record.getZipcode());
      rd.setAddress(record.getAddress());
      rd.setOutTelephone(record.getOutTelephone());
      rd.setInTelephone(record.getInTelephone());
      rd.setFaxNumber(record.getFaxNumber());
      return rd;
    } catch (Exception ex) {
      logger.error("AccountPostCsvExportScreen.getResultData", ex);
      return null;
    }
  }

  /**
  *
  */
  @Override
  protected String getCSVString(RunData rundata) throws Exception {
    if (ALEipUtils.isAdmin(rundata)) {
      SelectQuery<EipMPost> query = getSelectQuery(rundata);
      ResultList<EipMPost> list = query.getResultList();
      String LINE_SEPARATOR = System.getProperty("line.separator");
      try {
        AccountPostResultData data;
        StringBuffer sb =
          new StringBuffer("\"部署名\",\"郵便番号\",\"住所\","
            + "\"電話番号（外線）\",\"電話番号（内線）\",\"FAX\"");
        for (ListIterator<EipMPost> iterator = list.listIterator(list.size()); iterator
          .hasPrevious();) {
          sb.append(LINE_SEPARATOR);
          data = getPostResultData(iterator.previous());
          sb.append("\"");
          sb.append(data.getPostName());
          sb.append("\",\"");
          sb.append(data.getZipcode());
          sb.append("\",\"");
          sb.append(data.getAddress());
          sb.append("\",\"");
          sb.append(data.getOutTelephone());
          sb.append("\",\"");
          sb.append(data.getInTelephone());
          sb.append("\",\"");
          sb.append(data.getFaxNumber());
          sb.append("\"");
        }
        return sb.toString();
      } catch (Exception e) {
        logger.error("AccountPostCsvExportScreen.getCSVString", e);
        return null;
      }
    } else {
      throw new ALPermissionException();
    }
  }

  /**
   *
   * @param rundata
   * @return
   */
  private SelectQuery<EipMPost> getSelectQuery(RunData rundata) {
    SelectQuery<EipMPost> query = Database.query(EipMPost.class);
    query.orderDesending(EipMPost.SORT_PROPERTY);
    return query;
  }

  @Override
  protected String getFileName() {
    return "Aipo_post.csv";
  }

}
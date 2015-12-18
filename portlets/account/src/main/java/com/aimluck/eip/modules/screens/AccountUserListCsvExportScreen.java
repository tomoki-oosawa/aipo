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

import com.aimluck.eip.account.AccountResultData;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class AccountUserListCsvExportScreen extends ALCSVScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountUserListCsvExportScreen.class.getName());

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
  protected AccountResultData getResultData(TurbineUser record) {
    try {

      AccountResultData rd = new AccountResultData();
      rd.initField();

      rd.setName(new StringBuffer()
        .append(record.getLastName())
        .append(" ")
        .append(record.getFirstName())
        .toString());
      rd.setUserName(record.getLoginName());
      rd.setUserId(record.getUserId().intValue());
      rd.setDisabled(record.getDisabled());
      rd.setPositionName(ALEipUtils.getPositionName(record
        .getPositionId()
        .intValue()));
      rd.setEmail(record.getEmail());

      return rd;
    } catch (Exception ex) {
      logger.error("AccountUserSelectData.getResultData", ex);
      return null;
    }
  }

  /**
  *
  */
  @Override
  protected String getCSVString(RunData rundata) throws Exception {
    if (ALEipUtils.isAdmin(rundata)) {
      SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);

      ResultList<TurbineUser> list = query.getResultList();
      String LINE_SEPARATOR = System.getProperty("line.separator");
      try {
        StringBuffer sb =
          new StringBuffer("\"名前\",\"ユーザ名\",\"ID\",\"操作\",\"役職\",\"メアド\"");
        AccountResultData data;
        for (ListIterator<TurbineUser> iterator =
          list.listIterator(list.size()); iterator.hasPrevious();) {
          sb.append(LINE_SEPARATOR);
          data = getResultData(iterator.previous());
          sb.append("\"");
          sb.append(data.getName());
          sb.append("\",\"");
          sb.append(data.getUserName());
          sb.append("\",\"");
          sb.append(data.getUserId());
          sb.append("\",\"");
          sb.append(data.getDisabled());
          sb.append("\",\"");
          sb.append(data.getPositionName());
          sb.append("\",\"");
          sb.append(data.getEmail());
          sb.append("\"");

        }
        return sb.toString();
      } catch (Exception e) {
        logger.error("AccountUserListCsvExportScreen.getCSVString", e);
        return null;
      }
    } else {
      throw new ALPermissionException();
    }
  }

  @Override
  protected String getFileName() {
    return "userlist.csv";
  }

}
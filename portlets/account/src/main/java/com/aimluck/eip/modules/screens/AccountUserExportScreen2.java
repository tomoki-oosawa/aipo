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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
public class AccountUserExportScreen2 extends ALCSVScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountUserExportScreen2.class.getName());

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
   * AccountResultData に値を格納して返します。（一覧データ） <BR>
   *
   * @param obj
   * @return
   */
  protected AccountResultData getResultData(TurbineUser record) {
    try {
      DateFormat df = new SimpleDateFormat("yyyy年MM月dd日(EE)HH:mm:ss");

      AccountResultData ard = new AccountResultData();
      ard.initField();
      TurbineUser user = record.getTurbineUser();

      ard.setUserName(user.getLoginName());
      ard.setName(new StringBuffer()
        .append(user.getLastName())
        .append(" ")
        .append(user.getFirstName())
        .toString());
      ard.setNameKana(new StringBuffer().append(user.getLastNameKana()).append(
        " ").append(user.getFirstNameKana()).toString());
      ard.setEmail(user.getEmail());
      ard.setOutTelephone(user.getOutTelephone());
      ard.setInTelephone(user.getInTelephone());
      ard.setCellularPhone(user.getCellularPhone());
      ard.setCellularMail(user.getCellularMail());

      ard.setPostNameList(ALEipUtils.getPostNameList(user.getUpdatedUserId()));
      ard.setPositionName(ALEipUtils.getPositionName(user.getPositionId()));

      return ard;

    } catch (Exception ex) {
      logger.error("TurbineUser", ex);
      return null;
    }
  }

  /**
   *
   */
  @Override
  protected String getCSVString(RunData rundata) throws Exception {
    if (ALEipUtils.isAdmin(rundata)) { // ここ
      SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);

      ResultList<TurbineUser> list = query.getResultList();
      String LINE_SEPARATOR = System.getProperty("line.separator");
      try {

        AccountResultData ard = new AccountResultData(); // ４月８日追加

        StringBuffer sb =
          new StringBuffer(
            "\"ログイン名\",\"名前\",\"名前（フリガナ）\",\"メールアドレス\",\"番号（外線）\",\"番号（内線）\",\"電話番号（携帯）\",\"携帯メールアドレス\",\"部署\",\"役職\"");
        AccountResultData data;
        for (ListIterator<TurbineUser> iterator =
          list.listIterator(list.size()); iterator.hasPrevious();) {
          sb.append(LINE_SEPARATOR);
          data = getResultData(iterator.previous());
          sb.append("\"");
          sb.append(ard.getUserName());
          sb.append("\",\"");
          sb.append(ard.getName());
          sb.append("\",\"");
          sb.append(ard.getNameKana());
          sb.append("\",\"");
          sb.append(ard.getEmail());
          sb.append("\",\"");
          sb.append(ard.getOutTelephone());
          sb.append("\",\"");
          sb.append(ard.getInTelephone());
          sb.append("\"");
          sb.append(ard.getCellularPhone());
          sb.append("\",\"");
          sb.append(ard.getCellularMail());
          sb.append("\"");
          sb.append(ard.getPositionName());
          sb.append("\"");

        }
        return sb.toString();
      } catch (Exception e) {
        logger.error("AccountUserExportScreen2.getCSVString", e);
        return null;
      }
    } else {
      throw new ALPermissionException();
    }
  }

  @Override
  protected String getFileName() {
    return "accountuser.export";
  }

}

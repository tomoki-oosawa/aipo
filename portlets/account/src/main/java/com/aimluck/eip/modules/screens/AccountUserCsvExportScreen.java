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

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.account.AccountResultData;
import com.aimluck.eip.account.util.AccountUtils;
import com.aimluck.eip.cayenne.om.account.EipMUserPosition;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 *
 */
public class AccountUserCsvExportScreen extends ALCSVScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountUserCsvExportScreen.class.getName());

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
      Integer id = Integer.valueOf(record.getUserId());
      AccountResultData rd = new AccountResultData();
      rd.initField();
      rd.setUserId(Integer.valueOf(record.getUserId()).intValue());
      rd.setUserName(record.getLoginName());
      rd.setName(new StringBuffer()
        .append(record.getLastName())
        .append(" ")
        .append(record.getFirstName())
        .toString());
      rd.setFirstName(record.getFirstName());
      rd.setLastName(record.getLastName());
      rd.setNameKana(new StringBuffer()
        .append(record.getLastNameKana())
        .append(" ")
        .append(record.getFirstNameKana())
        .toString());
      rd.setFirstNameKana(record.getFirstNameKana());
      rd.setLastNameKana(record.getLastNameKana());
      rd.setEmail(record.getEmail());
      rd.setOutTelephone(record.getOutTelephone());
      rd.setInTelephone(record.getInTelephone());
      rd.setCellularPhone(record.getCellularPhone());
      rd.setCellularMail(record.getCellularMail());
      rd.setPostNameList(ALEipUtils.getPostNameList(id.intValue()));
      rd.setPositionName(ALEipUtils.getPositionName(record.getPositionId()));
      rd.setDisabled(record.getDisabled());
      rd.setIsAdmin(ALEipUtils.isAdmin(Integer.valueOf(record.getUserId())));
      rd.setPhotoModified(record.getPhotoModified().getTime());
      rd.setCode(record.getCode());

      return rd;
    } catch (Exception ex) {
      logger.error("AccountUserCsvExportScreen.getResultData", ex);
      return null;
    }
  }

  /**
  *
  */
  @Override
  protected String getCSVString(RunData rundata) throws Exception {
    if (ALEipUtils.isAdmin(rundata)) {
      SelectQuery<TurbineUser> query = getSelectQuery(rundata);

      ResultList<TurbineUser> list = query.getResultList();
      String LINE_SEPARATOR = System.getProperty("line.separator");
      try {
        AccountResultData data;
        StringBuffer sb =
          new StringBuffer("\"ユーザー名\",\"パスワード\",\"名前（姓）\",\"名前（名）\","
            + "\"名前（姓・フリガナ）\",\"名前（名・フリガナ）\",\"メールアドレス\","
            + "\"電話番号（外線）\",\"電話番号（内線）\",\"電話番号（携帯）\","
            + "\"携帯メールアドレス\",\"部署名\",\"役職\",\"社員コード\"");
        for (ListIterator<TurbineUser> iterator =
          list.listIterator(list.size()); iterator.hasPrevious();) {
          sb.append(LINE_SEPARATOR);
          data = getResultData(iterator.previous());
          sb.append("\"");
          sb.append(data.getUserName());
          sb.append("\",\"");
          sb.append("*");// Password
          sb.append("\",\"");
          sb.append(data.getLastName());
          sb.append("\",\"");
          sb.append(data.getFirstName());
          sb.append("\",\"");
          sb.append(data.getLastNameKana());
          sb.append("\",\"");
          sb.append(data.getFirstNameKana());
          sb.append("\",\"");
          sb.append(data.getEmail());
          sb.append("\",\"");
          sb.append(data.getOutTelephone());
          sb.append("\",\"");
          sb.append(data.getInTelephone());
          sb.append("\",\"");
          sb.append(data.getCellularPhone());
          sb.append("\",\"");
          sb.append(data.getCellularMail());
          sb.append("\",\"");
          int i = 0, size = data.getPostNameList().size();
          while (i < size - 1) {
            sb.append(data.getPostNameList().get(i));
            sb.append("/");
            ++i;
          }
          if (i == size - 1) {
            sb.append(data.getPostNameList().get(i));
          }
          sb.append("\",\"");
          sb.append(data.getPositionName());
          sb.append("\",\"");
          sb.append(data.getCode());
          sb.append("\"");
        }
        return sb.toString();
      } catch (Exception e) {
        logger.error("AccountUserCsvExportScreen.getCSVString", e);
        return null;
      }
    } else {
      throw new ALPermissionException();
    }
  }

  /**
   * @param rundata
   * @return
   */
  private SelectQuery<TurbineUser> getSelectQuery(RunData rundata) {

    ALStringField target_keyword = new ALStringField();
    target_keyword.setValue(rundata.getParameters().get("target_keyword"));
    String filter_post = rundata.getParameters().get("current_post");
    String filter_role = rundata.getParameters().get("current_role");

    SelectQuery<TurbineUser> query =
      AccountUtils.getSelectQuery(
        target_keyword.getValue(),
        filter_post,
        filter_role);

    query.orderDesending(TurbineUser.EIP_MUSER_POSITION_PROPERTY
      + "."
      + EipMUserPosition.POSITION_PROPERTY);

    return query;
  }

  @Override
  protected String getFileName() {
    return "Aipo_users.csv";
  }

}
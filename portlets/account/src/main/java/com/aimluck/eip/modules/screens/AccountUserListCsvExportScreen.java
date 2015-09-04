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

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.account.AccountResultData;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.eventlog.util.ALEventlogUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
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
      // DateFormat df = new SimpleDateFormat("yyyy年MM月dd日(EE)HH:mm:ss");

      AccountResultData rd = new AccountResultData();
      rd.initField();
      rd.setUserName(record.getFirstName());

      TurbineUser user = record.getTurbineUser();

      rd.setUserFullName(user == null ? "" : new StringBuffer().append(
        user.getLastName()).append(" ").append(user.getFirstName()).toString());

      rd.setPortletName(ALEventlogUtils.getPortletAliasName(record
        .getPortletType()));
      rd.setEntityId(record.getEntityId().longValue());
      rd.setIpAddr(record.getIpAddr());
      rd.setEventName(ALEventlogUtils.getEventAliasName(record.getEventType()));
      rd.setNote(record.getNote());
      return rd;
    } catch (Exception ex) {
      logger.error("eventlog", ex);
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
      /*
       * Date startDay = DateFormat.getDateInstance(DateFormat.DEFAULT,
       * Locale.JAPAN).parse( rundata.getParameters().get("start_day")); Date
       * endDay = DateFormat.getDateInstance(DateFormat.DEFAULT,
       * Locale.JAPAN).parse( rundata.getParameters().get("end_day")); Calendar
       * cal = Calendar.getInstance(); cal.setTime(endDay);
       * cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1); endDay =
       * cal.getTime(); Expression exp1 = ExpressionFactory.greaterOrEqualExp(
       * TurbineUser.EVENT_DATE_PROPERTY, startDay); Expression exp2 =
       * ExpressionFactory.lessExp(TurbineUser.EVENT_DATE_PROPERTY, endDay);
       * query.andQualifier(exp1.andExp(exp2)); ResultList<TurbineUser> list =
       * query.getResultList();
       */
      String LINE_SEPARATOR = System.getProperty("line.separator");
      try {
        StringBuffer sb = new StringBuffer("\"ユーザー名\",\"名前\",\"部署名\",\"役職名\"");
        AccountResultData data;
        /*
         * for (ListIterator<TurbineUser> iterator =
         * list.listIterator(list.size()); iterator.hasPrevious();) {
         * sb.append(LINE_SEPARATOR); data = getResultData(iterator.previous());
         * sb.append("\""); sb.append(data.getEventDate()); sb.append("\",\"");
         * sb.append(data.getUserFullName()); sb.append("\",\"");
         * sb.append(data.getPortletName()); sb.append("\",\"");
         * sb.append(data.getEventName()); sb.append("\",\"");
         * sb.append(data.getIpAddr()); sb.append("\",\"");
         * sb.append(data.getNote()); sb.append("\""); }
         */
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
    return "Aipo_users.csv";
  }

}
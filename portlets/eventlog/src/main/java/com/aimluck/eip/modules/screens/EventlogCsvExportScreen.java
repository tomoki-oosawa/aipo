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
import java.util.Calendar;
import java.util.Date;
import java.util.ListIterator;
import java.util.Locale;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.portlet.EipTEventlog;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.eventlog.EventlogResultData;
import com.aimluck.eip.eventlog.util.ALEventlogUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 *
 */
public class EventlogCsvExportScreen extends ALCSVScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(EventlogCsvExportScreen.class.getName());

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
  protected EventlogResultData getResultData(EipTEventlog record) {
    try {
      DateFormat df = new SimpleDateFormat("yyyy年MM月dd日(EE)HH:mm:ss");

      EventlogResultData rd = new EventlogResultData();
      rd.initField();
      rd.setEventlogId(record.getEventlogId().longValue());

      TurbineUser user = record.getTurbineUser();

      rd.setUserFullName(user == null ? "" : new StringBuffer().append(
        user.getLastName()).append(" ").append(user.getFirstName()).toString());

      rd.setEventDate(df.format(record.getUpdateDate()));
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
      SelectQuery<EipTEventlog> query = Database.query(EipTEventlog.class);

      Date startDay =
        DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.JAPAN).parse(
          rundata.getParameters().get("start_day"));
      Date endDay =
        DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.JAPAN).parse(
          rundata.getParameters().get("end_day"));
      Calendar cal = Calendar.getInstance();
      cal.setTime(endDay);
      cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
      endDay = cal.getTime();
      Expression exp1 =
        ExpressionFactory.greaterOrEqualExp(
          EipTEventlog.EVENT_DATE_PROPERTY,
          startDay);
      Expression exp2 =
        ExpressionFactory.lessExp(EipTEventlog.EVENT_DATE_PROPERTY, endDay);
      query.andQualifier(exp1.andExp(exp2));
      ResultList<EipTEventlog> list = query.getResultList();
      String LINE_SEPARATOR = System.getProperty("line.separator");
      try {
        StringBuffer sb =
          new StringBuffer("\"日時\",\"名前\",\"機能名\",\"操作\",\"接続IP\",\"件名\"");
        EventlogResultData data;
        for (ListIterator<EipTEventlog> iterator =
          list.listIterator(list.size()); iterator.hasPrevious();) {
          sb.append(LINE_SEPARATOR);
          data = getResultData(iterator.previous());
          sb.append("\"");
          sb.append(data.getEventDate());
          sb.append("\",\"");
          sb.append(data.getUserFullName());
          sb.append("\",\"");
          sb.append(data.getPortletName());
          sb.append("\",\"");
          sb.append(data.getEventName());
          sb.append("\",\"");
          sb.append(data.getIpAddr());
          sb.append("\",\"");
          sb.append(data.getNote());
          sb.append("\"");
        }
        return sb.toString();
      } catch (Exception e) {
        logger.error("EventlogCsvExportScreen.getCSVString", e);
        return null;
      }
    } else {
      throw new ALPermissionException();
    }
  }

  @Override
  protected String getFileName() {
    return "eventlog.csv";
  }

}
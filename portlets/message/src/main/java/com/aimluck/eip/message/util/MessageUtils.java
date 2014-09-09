/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2014 Aimluck,Inc.
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

package com.aimluck.eip.message.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.DataRow;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.util.template.BaseJetspeedLink;
import org.apache.jetspeed.util.template.ContentTemplateLink;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTMessageRoom;
import com.aimluck.eip.message.MessageMockPortlet;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SQLTemplate;

/**
 *
 */
public class MessageUtils {

  public static final String MESSAGE_PORTLET_NAME = "Message";

  public static void setupContext(RunData rundata, Context context) {
    Portlet portlet = new MessageMockPortlet();
    context.put("portlet", portlet);
    context.put("jslink", new BaseJetspeedLink(rundata));
    context.put("clink", new ContentTemplateLink(rundata));
  }

  public static ResultList<EipTMessageRoom> getRoomList(int userId) {
    return getRoomList(userId, null, -1, -1);
  }

  protected static ResultList<EipTMessageRoom> getRoomList(int userId,
      String keyword, int page, int limit) {
    StringBuilder select = new StringBuilder();

    boolean isMySQL = Database.isJdbcMySQL();
    boolean isSearch = (keyword != null && keyword.length() > 0);

    select.append("select");
    select.append(" t2.room_id, ");
    select.append(" t2.name, ");
    select.append(" t4.last_name, ");
    select.append(" t4.first_name, ");
    select.append(" t2.auto_name, ");
    select.append(" t2.room_type, ");

    select.append(" t2.last_message, ");
    select.append(" last_update_date, ");
    select
      .append(" (select count(*) from eip_t_message_read t3 where t3.room_id = t2.room_id and t3.user_id = #bind($user_id) and t3.is_read ='F') as unread ");

    StringBuilder count = new StringBuilder();
    count.append("select count(t2.room_id) AS c ");

    StringBuilder body = new StringBuilder();
    body
      .append("  from eip_t_message_room_member t1, eip_t_message_room t2, turbine_user t4 where t1.user_id = #bind($user_id) and t1.room_id = t2.room_id and t1.target_user_id = t4.user_id ");
    if (isSearch) {
      if (isMySQL) {
        body
          .append(" and ((t2.room_type='G' and t2.name like #bind($keyword)) or (t2.room_type='O' and CONCAT(t4.last_name,t4.first_name) like #bind($keyword))) ");
      } else {
        body
          .append(" and ((t2.room_type='G' and t2.name like #bind($keyword)) or (t2.room_type='O' and (t4.last_name || t4.first_name) like #bind($keyword))) ");
      }
    }

    StringBuilder last = new StringBuilder();

    last.append(" order by t2.last_update_date desc ");

    SQLTemplate<EipTMessageRoom> countQuery =
      Database
        .sql(EipTMessageRoom.class, count.toString() + body.toString())
        .param("user_id", Integer.valueOf(userId));
    if (isSearch) {
      countQuery.param("keyword", "%" + keyword + "%");
    }

    int countValue = 0;
    if (page > 0 && limit > 0) {
      List<DataRow> fetchCount = countQuery.fetchListAsDataRow();

      for (DataRow row : fetchCount) {
        countValue = ((Long) row.get("c")).intValue();
      }

      int offset = 0;
      if (limit > 0) {
        int num = ((int) (Math.ceil(countValue / (double) limit)));
        if ((num > 0) && (num < page)) {
          page = num;
        }
        offset = limit * (page - 1);
      } else {
        page = 1;
      }

      last.append(" LIMIT ");
      last.append(limit);
      last.append(" OFFSET ");
      last.append(offset);
    }

    SQLTemplate<EipTMessageRoom> query =
      Database.sql(
        EipTMessageRoom.class,
        select.toString() + body.toString() + last.toString()).param(
        "user_id",
        Integer.valueOf(userId));
    if (isSearch) {
      query.param("keyword", "%" + keyword + "%");
    }

    List<DataRow> fetchList = query.fetchListAsDataRow();

    List<EipTMessageRoom> list = new ArrayList<EipTMessageRoom>();
    for (DataRow row : fetchList) {
      Long unread = (Long) row.get("unread");
      String lastName = (String) row.get("last_name");
      String firstName = (String) row.get("first_name");
      EipTMessageRoom object =
        Database.objectFromRowData(row, EipTMessageRoom.class);
      object.setUnreadCount(unread.intValue());
      object.setFirstName(firstName);
      object.setLastName(lastName);
      list.add(object);
    }

    if (page > 0 && limit > 0) {
      return new ResultList<EipTMessageRoom>(list, page, limit, countValue);
    } else {
      return new ResultList<EipTMessageRoom>(list, -1, -1, list.size());
    }
  }
}

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
package com.aimluck.eip.message.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.util.template.ContentTemplateLink;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.cayenne.om.portlet.EipTMessage;
import com.aimluck.eip.cayenne.om.portlet.EipTMessageFile;
import com.aimluck.eip.cayenne.om.portlet.EipTMessageRead;
import com.aimluck.eip.cayenne.om.portlet.EipTMessageRoom;
import com.aimluck.eip.cayenne.om.portlet.EipTMessageRoomMember;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.fileupload.beans.FileuploadBean;
import com.aimluck.eip.fileupload.beans.FileuploadLiteBean;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
import com.aimluck.eip.message.MessageListSelectData;
import com.aimluck.eip.message.MessageMockPortlet;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SQLTemplate;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.services.push.ALPushService;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALJetspeedLink;

/**
 *
 */
public class MessageUtils {

  public static final String MESSAGE_PORTLET_NAME = "Message";

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MessageUtils.class.getName());

  public static final String FOLDER_FILEDIR_MESSAGE = JetspeedResources
    .getString("aipo.filedir", "");

  public static final String CATEGORY_KEY = JetspeedResources.getString(
    "aipo.message.categorykey",
    "");

  public static final String FILE_ENCODING = JetspeedResources.getString(
    "content.defaultencoding",
    "UTF-8");

  public static void setupContext(RunData rundata, Context context) {
    Portlet portlet = new MessageMockPortlet();
    context.put("portlet", portlet);
    context.put("jslink", new ALJetspeedLink(rundata));
    context.put("clink", new ContentTemplateLink(rundata));
  }

  public static void setupContext(RunData rundata, Context context,
      String portletId) {
    Portlet portlet = new MessageMockPortlet(portletId);
    context.put("portlet", portlet);
    context.put("jslink", new ALJetspeedLink(rundata));
    context.put("clink", new ContentTemplateLink(rundata));
  }

  public static EipTMessage getMessage(int messageId) {
    return Database.get(EipTMessage.class, messageId);
  }

  public static EipTMessageRoom getRoom(int roomId) {
    return Database.get(EipTMessageRoom.class, roomId);
  }

  public static EipTMessageRoom getRoom(int userId, int targetUserId) {
    EipTMessageRoomMember model =
      Database.query(EipTMessageRoomMember.class).where(
        Operations.eq(EipTMessageRoomMember.USER_ID_PROPERTY, userId)).where(
        Operations.eq(
          EipTMessageRoomMember.TARGET_USER_ID_PROPERTY,
          targetUserId)).fetchSingle();
    if (model != null) {
      return model.getEipTMessageRoom();
    } else {
      return null;
    }
  }

  public static EipTMessageRoomMember getRoomMember(int roomId, int userId) {
    EipTMessageRoomMember model =
      Database
        .query(EipTMessageRoomMember.class)
        .where(Operations.eq(EipTMessageRoomMember.USER_ID_PROPERTY, userId))
        .where(
          Operations.eq(
            EipTMessageRoomMember.EIP_TMESSAGE_ROOM_PROPERTY,
            roomId))
        .fetchSingle();
    return model;
  }

  public static EipTMessageRoom getRoom(RunData rundata, Context context)
      throws ALPageNotFoundException {
    Integer roomId = null;
    try {
      try {
        roomId = rundata.getParameters().getInteger(ALEipConstants.ENTITY_ID);
      } catch (Throwable ignore) {
        //
      }
      if (roomId == null) {
        throw new ALPageNotFoundException();
      }
      return Database.get(EipTMessageRoom.class, roomId);
    } catch (ALPageNotFoundException e) {
      throw e;
    } catch (Throwable t) {
      logger.error("MessageUtils.getRoom", t);
      return null;
    }
  }

  public static boolean isJoinRoom(int roomId, int userId) {
    EipTMessageRoom room = getRoom(roomId);
    if (room == null) {
      return false;
    }
    return isJoinRoom(room, userId);
  }

  public static boolean isJoinRoom(EipTMessageRoom room, int userId) {
    @SuppressWarnings("unchecked")
    List<EipTMessageRoomMember> list = room.getEipTMessageRoomMember();
    for (EipTMessageRoomMember member : list) {
      if (member.getUserId().intValue() == userId) {
        return true;
      }
    }
    return false;
  }

  public static boolean isJoinRoom(EipTMessageFile file, int userId) {
    return isJoinRoom(file.getRoomId(), userId);
  }

  public static boolean isJoinRoom(EipTMessage message, int userId) {
    EipTMessageRoom room = message.getEipTMessageRoom();
    if (room == null) {
      return false;
    }
    return isJoinRoom(room, userId);
  }

  public static boolean hasAuthorityRoom(EipTMessageRoom room, int userId) {
    @SuppressWarnings("unchecked")
    List<EipTMessageRoomMember> list = room.getEipTMessageRoomMember();
    for (EipTMessageRoomMember member : list) {
      if (member.getUserId().intValue() == userId) {
        return "A".equals(member.getAuthority());
      }
    }
    return false;
  }

  public static boolean isDesktopNotification(EipTMessageRoom room, int userId) {
    EipTMessageRoomMember model = getRoomMember(room.getRoomId(), userId);
    if (model != null) {
      return "A".equals(model.getDesktopNotification());
    }
    return false;
  }

  public static boolean isMobileNotification(EipTMessageRoom room, int userId) {
    EipTMessageRoomMember model = getRoomMember(room.getRoomId(), userId);
    if (model != null) {
      return "A".equals(model.getMobileNotification());
    }
    return false;
  }

  public static ResultList<EipTMessage> getMessageList(int roomId, int cursor,
      int limit, boolean isLatest) {
    StringBuilder select = new StringBuilder();

    select.append("select");
    select.append(" t1.message_id, ");
    select.append(" t1.room_id,  ");
    select.append(" t1.user_id, ");
    select.append(" t1.message, ");
    select.append(" t1.create_date, ");
    select.append(" t1.member_count, ");
    select.append(" t2.last_name, ");
    select.append(" t2.first_name, ");
    select.append(" t2.has_photo, ");
    select.append(" t2.photo_modified, ");

    select
      .append(" (select count(*) from eip_t_message_read t3 where t3.message_id = t1.message_id and t3.room_id = t1.room_id and t3.is_read = 'F') as unread ");

    StringBuilder count = new StringBuilder();
    count.append("select count(t1.message_id) AS c ");

    StringBuilder body = new StringBuilder();
    body
      .append("  from eip_t_message t1, turbine_user t2 where t1.user_id = t2.user_id and t1.room_id = #bind($room_id) ");
    if (cursor > 0) {
      if (isLatest) {
        body.append(" and t1.message_id > #bind($cursor) ");
      } else {
        body.append(" and t1.message_id < #bind($cursor) ");
      }
    }
    StringBuilder last = new StringBuilder();

    last.append(" order by t1.create_date desc ");

    if (limit > 0) {
      last.append(" limit ");
      last.append(limit);
    }

    SQLTemplate<EipTMessage> query =
      Database.sql(
        EipTMessage.class,
        select.toString() + body.toString() + last.toString()).param(
        "room_id",
        Integer.valueOf(roomId));
    if (cursor > 0) {
      query.param("cursor", cursor);
    }

    List<DataRow> fetchList = query.fetchListAsDataRow();

    List<EipTMessage> list = new ArrayList<EipTMessage>();
    for (DataRow row : fetchList) {
      Long unread = (Long) row.get("unread");
      String lastName = (String) row.get("last_name");
      String firstName = (String) row.get("first_name");
      String hasPhoto = (String) row.get("has_photo");
      Date photoModified = (Date) row.get("photo_modified");

      EipTMessage object = Database.objectFromRowData(row, EipTMessage.class);
      object.setUnreadCount(unread.intValue());
      object.setFirstName(firstName);
      object.setLastName(lastName);
      object.setHasPhoto(hasPhoto);
      if (photoModified != null) {
        object.setPhotoModified(photoModified.getTime());
      }
      list.add(object);
    }

    return new ResultList<EipTMessage>(list, -1, -1, list.size());
  }

  public static ResultList<EipTMessage> getMessageJumpList(Integer roomId,
      int cursor) {
    List<Integer> roomList = new ArrayList<Integer>();
    roomList.add(roomId);
    ResultList<EipTMessage> resultListTop =
      getMessageList(
        roomList,
        null,
        cursor,
        MessageListSelectData.MESSAGE_LIMIT / 2,
        true,
        true,
        true);
    ResultList<EipTMessage> resultListBottom =
      getMessageList(
        roomList,
        null,
        cursor,
        MessageListSelectData.MESSAGE_LIMIT / 2,
        false,
        false,
        false);
    resultListTop.addAll(resultListBottom);
    return new ResultList<EipTMessage>(resultListTop, -1, -1, resultListTop
      .size());
  }

  public static ResultList<EipTMessage> getMessageList(List<Integer> roomList,
      String keyword, int cursor, int limit, boolean isLatest) {
    return getMessageList(
      roomList,
      keyword,
      cursor,
      limit,
      isLatest,
      false,
      false);
  }

  public static ResultList<EipTMessage> getMessageList(List<Integer> roomList,
      String keyword, int cursor, int limit, boolean isLatest,
      boolean isReverse, boolean isEquals) {
    StringBuilder select = new StringBuilder();

    boolean isSearch = (keyword != null && keyword.length() > 0);

    select.append("select");
    select.append(" t1.message_id, ");
    select.append(" t1.room_id,  ");
    select.append(" t1.user_id, ");
    select.append(" t1.message, ");
    select.append(" t1.create_date, ");
    select.append(" t1.member_count, ");
    select.append(" t2.last_name, ");
    select.append(" t2.first_name, ");
    select.append(" t2.has_photo, ");
    select.append(" t2.photo_modified, ");

    select
      .append(" (select count(*) from eip_t_message_read t3 where t3.message_id = t1.message_id and t3.room_id = t1.room_id and t3.is_read = 'F') as unread ");

    StringBuilder count = new StringBuilder();
    count.append("select count(t1.message_id) AS c ");

    StringBuilder body = new StringBuilder();
    body
      .append("  from eip_t_message t1, turbine_user t2 where t1.user_id = t2.user_id and t1.room_id IN(");
    boolean isFirst = true;
    for (Integer roomId : roomList) {
      if (!isFirst) {
        body.append(",");
      }
      isFirst = false;
      body.append(roomId);
    }
    body.append(") ");
    if (cursor > 0) {
      if (isLatest) {
        if (isEquals) {
          body.append(" and t1.message_id >= #bind($cursor) ");
        } else {
          body.append(" and t1.message_id > #bind($cursor) ");
        }
      } else {
        if (isEquals) {
          body.append(" and t1.message_id <= #bind($cursor) ");
        } else {
          body.append(" and t1.message_id < #bind($cursor) ");
        }
      }
    }
    if (isSearch) {
      body.append(" and t1.message like #bind($keyword) ");
    }

    StringBuilder last = new StringBuilder();

    if (isReverse) {
      last.append(" order by t1.create_date asc ");
    } else {
      last.append(" order by t1.create_date desc ");
    }

    if (limit > 0) {
      last.append(" limit ");
      last.append(limit);
    }

    SQLTemplate<EipTMessage> query =
      Database.sql(EipTMessage.class, select.toString()
        + body.toString()
        + last.toString());
    if (cursor > 0) {
      query.param("cursor", cursor);
    }
    if (isSearch) {
      query.param("keyword", "%" + keyword + "%");
    }

    List<DataRow> fetchList = query.fetchListAsDataRow();

    List<EipTMessage> list = new ArrayList<EipTMessage>();
    for (DataRow row : fetchList) {
      Long unread = (Long) row.get("unread");
      String lastName = (String) row.get("last_name");
      String firstName = (String) row.get("first_name");
      String hasPhoto = (String) row.get("has_photo");
      Date photoModified = (Date) row.get("photo_modified");
      Integer roomId = (Integer) row.get("room_id");

      EipTMessage object = Database.objectFromRowData(row, EipTMessage.class);
      object.setUnreadCount(unread.intValue());
      object.setFirstName(firstName);
      object.setLastName(lastName);
      object.setHasPhoto(hasPhoto);
      if (photoModified != null) {
        object.setPhotoModified(photoModified.getTime());
      }
      object.setRoomId(roomId);
      list.add(object);
    }

    if (isReverse) {
      Collections.reverse(list);
    }

    return new ResultList<EipTMessage>(list, -1, -1, list.size());
  }

  public static EipTMessage getLastMessage(int roomId) {
    List<Integer> tmpRoomIdList = new ArrayList<Integer>();
    tmpRoomIdList.add(roomId);
    ResultList<EipTMessage> lastMessage =
      MessageUtils.getMessageList(tmpRoomIdList, null, 0, 1, true, false, true);

    if (lastMessage.size() == 0) {
      return null;
    }
    return lastMessage.get(0);

  }

  public static ResultList<EipTMessage> getLast2Messages(int roomId) {
    List<Integer> tmpRoomIdList = new ArrayList<Integer>();
    tmpRoomIdList.add(roomId);
    ResultList<EipTMessage> lastMessage =
      MessageUtils.getMessageList(tmpRoomIdList, null, 0, 2, true, false, true);

    return lastMessage;

  }

  public static List<Integer> getRoomIds(int userId) {
    StringBuilder select = new StringBuilder();

    select.append("select");
    select.append(" t2.room_id ");

    StringBuilder count = new StringBuilder();
    count.append("select count(t2.room_id) AS c ");

    StringBuilder body = new StringBuilder();
    body
      .append("  from eip_t_message_room_member t1, eip_t_message_room t2, turbine_user t4 where t1.user_id = #bind($user_id) and t1.room_id = t2.room_id and t1.target_user_id = t4.user_id ");

    StringBuilder last = new StringBuilder();

    SQLTemplate<EipTMessageRoom> query =
      Database.sql(
        EipTMessageRoom.class,
        select.toString() + body.toString() + last.toString()).param(
        "user_id",
        Integer.valueOf(userId));
    List<DataRow> fetchList = query.fetchListAsDataRow();

    List<Integer> list = new ArrayList<Integer>(fetchList.size());
    for (DataRow row : fetchList) {
      Integer roomId = (Integer) row.get("room_id");
      list.add(roomId);
    }

    return list;
  }

  public static ResultList<EipTMessageRoom> getRoomList(int userId,
      String keyword) {
    return getRoomList(userId, keyword, -1, -1);
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
    select.append(" t2.has_photo, ");
    select.append(" t2.photo_modified, ");
    select.append(" t4.user_id, ");
    select.append(" t4.last_name, ");
    select.append(" t4.first_name, ");
    select.append(" t4.has_photo as user_has_photo, ");
    select.append(" t4.photo_modified as user_photo_modified, ");
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
          .append(" and ((t2.room_type='G' and t2.name like #bind($keyword)) or (t2.room_type='O' and CONCAT( (case when t4.login_name='admin' then #bind($alias) else t4.last_name end) ,t4.first_name) like #bind($keyword))) ");
      } else {
        body
          .append(" and ((t2.room_type='G' and t2.name like #bind($keyword)) or (t2.room_type='O' and ( (case when t4.login_name='admin' then #bind($alias) else t4.last_name end) || t4.first_name) like #bind($keyword))) ");
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
      countQuery.param("alias", ALOrgUtilsService.getAlias());
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
      query.param("alias", ALOrgUtilsService.getAlias());
    }

    List<DataRow> fetchList = query.fetchListAsDataRow();

    List<EipTMessageRoom> list = new ArrayList<EipTMessageRoom>();
    for (DataRow row : fetchList) {
      Long unread = (Long) row.get("unread");
      Integer tUserId = (Integer) row.get("user_id");
      String lastName = (String) row.get("last_name");
      String firstName = (String) row.get("first_name");
      String hasPhoto = (String) row.get("user_has_photo");
      Date photoModified = (Date) row.get("user_photo_modified");

      EipTMessageRoom object =
        Database.objectFromRowData(row, EipTMessageRoom.class);
      object.setUnreadCount(unread.intValue());
      object.setUserId(tUserId);
      object.setFirstName(firstName);
      object.setLastName(lastName);
      object.setUserHasPhoto(hasPhoto);
      if (photoModified != null) {
        object.setUserPhotoModified(photoModified.getTime());
      }
      list.add(object);
    }

    if (page > 0 && limit > 0) {
      return new ResultList<EipTMessageRoom>(list, page, limit, countValue);
    } else {
      return new ResultList<EipTMessageRoom>(list, -1, -1, list.size());
    }
  }

  public static ResultList<TurbineUser> getUserList(String groupName) {
    return getUserList(groupName, null, -1, -1);
  }

  public static ResultList<TurbineUser> getUserList(String groupName,
      String keyword) {
    return getUserList(groupName, keyword, -1, -1);
  }

  public static ResultList<TurbineUser> getUserList(String groupName,
      String keyword, int page, int limit) {

    StringBuilder select = new StringBuilder();

    boolean isMySQL = Database.isJdbcMySQL();
    boolean isSearch = (keyword != null && keyword.length() > 0);

    String keywordKana = "";

    select
      .append("select distinct t2.user_id, t2.login_name, t2.last_name, t2.first_name, t2.last_name_kana, t2.first_name_kana, t2.has_photo, t2.photo_modified, (t2.last_name_kana = '') ");

    StringBuilder count = new StringBuilder();
    count.append("select count(distinct t2.user_id) AS c ");

    StringBuilder body = new StringBuilder();
    body
      .append(" from turbine_user_group_role t1, turbine_user t2, turbine_group t3 where t1.user_id = t2.user_id and t1.group_id = t3.group_id and t2.user_id > 3 and t2.disabled = 'F' and t3.group_name = #bind($group_name)");
    if (isSearch) {
      keywordKana = ALStringUtil.convertHiragana2Katakana(keyword);
      if (isMySQL) {
        body
          .append(" and ( (CONCAT(t2.last_name,t2.first_name) like #bind($keyword)) or (CONCAT(t2.last_name_kana,t2.first_name_kana) like #bind($keywordKana)) ) ");
      } else {
        body
          .append(" and ( ((t2.last_name || t2.first_name)    like #bind($keyword)) or ((t2.last_name_kana || t2.first_name_kana)    like #bind($keywordKana)) ) ");
      }
    }

    StringBuilder last = new StringBuilder();

    last
      .append(" order by (t2.last_name_kana = ''), t2.last_name_kana, t2.first_name_kana ");

    SQLTemplate<TurbineUser> countQuery =
      Database
        .sql(TurbineUser.class, count.toString() + body.toString())
        .param("group_name", groupName);
    if (isSearch) {
      countQuery.param("keyword", "%" + keyword + "%");
      countQuery.param("keywordKana", "%" + keywordKana + "%");
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

      last.append(" limit ");
      last.append(limit);
      last.append(" offset ");
      last.append(offset);
    }

    SQLTemplate<TurbineUser> query =
      Database.sql(
        TurbineUser.class,
        select.toString() + body.toString() + last.toString()).param(
        "group_name",
        groupName);
    if (isSearch) {
      query.param("keyword", "%" + keyword + "%");
      query.param("keywordKana", "%" + keywordKana + "%");
    }

    List<TurbineUser> list = query.fetchList();

    if (page > 0 && limit > 0) {
      return new ResultList<TurbineUser>(list, page, limit, countValue);
    } else {
      return new ResultList<TurbineUser>(list, -1, -1, list.size());
    }
  }

  public static ResultList<TurbineUser> getReadUserList(int messageId) {
    StringBuilder sql = new StringBuilder();
    sql
      .append("select t1.user_id, t1.last_name, t1.first_name, t1.has_photo, t1.photo_modified from turbine_user t1, eip_t_message_read t2 where t1.user_id = t2.user_id and t2.message_id = #bind($message_id) and t2.is_read = 'T';");

    SQLTemplate<TurbineUser> query =
      Database.sql(TurbineUser.class, sql.toString()).param(
        "message_id",
        messageId);

    List<TurbineUser> list = query.fetchList();
    return new ResultList<TurbineUser>(list, -1, -1, list.size());
  }

  /**
   * 未読のものがあったら既読にする
   */
  public static void read(EipTMessageRoom room, int userId, int lastMessageId) {
    int countValue = 0;
    try {
      SQLTemplate<EipTMessageRead> countQuery =
        Database
          .sql(
            EipTMessageRead.class,
            "select count(*) as c from eip_t_message_read where room_id = #bind($room_id) and user_id = #bind($user_id) and is_read = 'F' and message_id <= #bind($message_id)")
          .param("room_id", Integer.valueOf(room.getRoomId()))
          .param("user_id", Integer.valueOf(userId))
          .param("message_id", Integer.valueOf(lastMessageId));

      List<DataRow> fetchCount = countQuery.fetchListAsDataRow();

      for (DataRow row : fetchCount) {
        countValue = ((Long) row.get("c")).intValue();
      }
    } catch (Throwable ignore) {
      // ignore
      logger.error("MessageUtils.read", ignore);
    }

    if (countValue > 0) {
      try {
        String sql =
          "update eip_t_message_read set is_read = 'T' where room_id = #bind($room_id) and user_id = #bind($user_id) and is_read = 'F' and message_id <= #bind($message_id)";
        Database.sql(EipTMessageRead.class, sql).param(
          "room_id",
          Integer.valueOf(room.getRoomId())).param(
          "user_id",
          Integer.valueOf(userId)).param(
          "message_id",
          Integer.valueOf(lastMessageId)).execute();

        List<String> recipients = new ArrayList<String>();
        @SuppressWarnings("unchecked")
        List<EipTMessageRoomMember> members = room.getEipTMessageRoomMember();
        for (EipTMessageRoomMember member : members) {
          if (member.getUserId().intValue() != userId) {
            recipients.add(member.getLoginName());
          }
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("roomId", String.valueOf(room.getRoomId()));

        ALPushService.pushAsync("messagev2_read", params, recipients);
      } catch (Throwable ignore) {
        // ignore
        logger.error("MessageUtils.read", ignore);
      }
    }
  }

  public static List<EipTMessageFile> getEipTMessageFilesByRoom(int roomId) {
    return Database.query(EipTMessageFile.class).where(
      Operations.eq(EipTMessageFile.ROOM_ID_PROPERTY, roomId)).fetchList();
  }

  public static List<EipTMessageFile> getEipTMessageFilesByOwner(int userId) {
    return Database.query(EipTMessageFile.class).where(
      Operations.eq(EipTMessageFile.OWNER_ID_PROPERTY, userId)).fetchList();
  }

  public static List<EipTMessageFile> getEipTMessageFilesByMessage(int messageId) {
    return Database
      .query(EipTMessageFile.class)
      .where(Operations.eq(EipTMessageFile.MESSAGE_ID_PROPERTY, messageId))
      .fetchList();
  }

  public static EipTMessageFile getEipTMessageFile(RunData rundata)
      throws ALPageNotFoundException, ALDBErrorException {
    try {
      int attachmentIndex =
        rundata.getParameters().getInt("attachmentIndex", -1);
      if (attachmentIndex < 0) {
        logger.debug("[MessageUtils] Empty ID...");
        throw new ALPageNotFoundException();

      }

      SelectQuery<EipTMessageFile> query =
        Database.query(EipTMessageFile.class);
      Expression exp =
        ExpressionFactory.matchDbExp(EipTMessageFile.FILE_ID_PK_COLUMN, Integer
          .valueOf(attachmentIndex));
      query.andQualifier(exp);

      List<EipTMessageFile> files = query.fetchList();
      if (files == null || files.size() == 0) {
        logger.debug("[MessageUtils] Not found ID...");
        throw new ALPageNotFoundException();
      }
      return files.get(0);
    } catch (Exception ex) {
      logger.error("[MessageUtils]", ex);
      throw new ALDBErrorException();
    }
  }

  public static String getRelativePath(String fileName) {
    return new StringBuffer().append("/").append(fileName).toString();
  }

  public static String getSaveDirPath(String orgId, int uid) {
    return ALStorageService.getDocumentPath(
      FOLDER_FILEDIR_MESSAGE,
      CATEGORY_KEY + ALStorageService.separator() + uid);
  }

  public static List<FileuploadLiteBean> getFileuploadList(RunData rundata) {
    String[] fileids =
      rundata
        .getParameters()
        .getStrings(FileuploadUtils.KEY_FILEUPLOAD_ID_LIST);
    if (fileids == null) {
      return null;
    }

    List<String> hadfileids = new ArrayList<String>();
    List<String> newfileids = new ArrayList<String>();

    for (int j = 0; j < fileids.length; j++) {
      if (fileids[j].trim().startsWith("s")) {
        hadfileids.add(fileids[j].trim().substring(1));
      } else {
        newfileids.add(fileids[j].trim());
      }
    }

    List<FileuploadLiteBean> fileNameList = new ArrayList<FileuploadLiteBean>();
    FileuploadLiteBean filebean = null;
    int fileid = 0;

    if (newfileids.size() > 0) {
      String folderName =
        rundata.getParameters().getString(
          FileuploadUtils.KEY_FILEUPLOAD_FODLER_NAME);
      if (folderName == null || folderName.equals("")) {
        return null;
      }

      int length = newfileids.size();
      for (int i = 0; i < length; i++) {
        if (newfileids.get(i) == null || newfileids.get(i).equals("")) {
          continue;
        }

        try {
          fileid = Integer.parseInt(newfileids.get(i));
        } catch (Exception e) {
          continue;
        }

        if (fileid == 0) {
          filebean = new FileuploadLiteBean();
          filebean.initField();
          filebean.setFolderName("photo");
          filebean.setFileName("");
          fileNameList.add(filebean);
        } else {
          BufferedReader reader = null;
          try {
            reader =
              new BufferedReader(new InputStreamReader(ALStorageService
                .getTmpFile(ALEipUtils.getUserId(rundata), folderName, fileid
                  + FileuploadUtils.EXT_FILENAME), FILE_ENCODING));
            String line = reader.readLine();
            if (line == null || line.length() <= 0) {
              continue;
            }

            filebean = new FileuploadLiteBean();
            filebean.initField();
            filebean.setFolderName(fileids[i]);
            filebean.setFileId(fileid);
            filebean.setFileName(line);
            fileNameList.add(filebean);
          } catch (Exception e) {
            logger.error("message", e);
          } finally {
            try {
              reader.close();
            } catch (Exception e) {
              logger.error("message", e);
            }
          }
        }

      }
    }

    if (hadfileids.size() > 0) {
      ArrayList<Integer> hadfileidsValue = new ArrayList<Integer>();
      for (int k = 0; k < hadfileids.size(); k++) {
        try {
          fileid = Integer.parseInt(hadfileids.get(k));
          hadfileidsValue.add(fileid);
        } catch (Exception e) {
          continue;
        }
      }

      try {
        SelectQuery<EipTMessageFile> reqquery =
          Database.query(EipTMessageFile.class);
        Expression reqexp1 =
          ExpressionFactory.inDbExp(
            EipTMessageFile.FILE_ID_PK_COLUMN,
            hadfileidsValue);
        reqquery.setQualifier(reqexp1);
        List<EipTMessageFile> requests = reqquery.fetchList();
        int requestssize = requests.size();
        for (int i = 0; i < requestssize; i++) {
          EipTMessageFile file = requests.get(i);
          filebean = new FileuploadBean();
          filebean.initField();
          filebean.setFileId(file.getFileId());
          filebean.setFileName(file.getFileName());
          filebean.setFlagNewFile(false);
          fileNameList.add(filebean);
        }
      } catch (Exception ex) {
        logger.error("[MessageUtils] Exception.", ex);
      }
    }
    return fileNameList;
  }

  public static Map<Integer, Long> getReadCountList(int roomId, int userId,
      int minMessageId, int maxMessageId) {
    SQLTemplate<EipTMessageRead> query =
      Database
        .sql(
          EipTMessageRead.class,
          "select message_id, is_read, count(*) as c from eip_t_message_read where room_id = #bind($room_id) and user_id <> #bind($user_id) and message_id >= #bind($min_message_id) and message_id <= #bind($max_message_id) group by message_id, is_read order by message_id desc")
        .param("room_id", Integer.valueOf(roomId))
        .param("user_id", Integer.valueOf(userId))
        .param("min_message_id", Integer.valueOf(minMessageId))
        .param("max_message_id", Integer.valueOf(maxMessageId));

    List<DataRow> fetchList = query.fetchListAsDataRow();

    Map<Integer, MessageReadEntry> maps =
      new HashMap<Integer, MessageReadEntry>();
    Map<Integer, Long> result = new HashMap<Integer, Long>();
    for (DataRow row : fetchList) {
      Long count = (Long) row.get("c");
      Integer messageId = (Integer) row.get("message_id");
      String isRead = (String) row.get("is_read");
      MessageReadEntry read = maps.get(messageId);
      if (read == null) {
        read = new MessageReadEntry();
      }
      if ("T".equals(isRead)) {
        read.setRead(count);
      } else {
        read.setUnread(count);
      }
      maps.put(messageId, read);
    }
    Iterator<Entry<Integer, MessageReadEntry>> iterator =
      maps.entrySet().iterator();
    while (iterator.hasNext()) {
      Entry<Integer, MessageReadEntry> next = iterator.next();
      MessageReadEntry value = next.getValue();
      if (value.getUnread() == null) {
        result.put(next.getKey(), Long.valueOf(-1));
      } else {
        result.put(next.getKey(), (value.getRead() == null
          ? Long.valueOf(0)
          : value.getRead()));
      }
    }
    return result;
  }

  public static class MessageReadEntry {
    private Long read;

    private Long unread;

    public Long getRead() {
      return read;
    }

    public void setRead(Long read) {
      this.read = read;
    }

    public Long getUnread() {
      return unread;
    }

    public void setUnread(Long unread) {
      this.unread = unread;
    }
  }
}

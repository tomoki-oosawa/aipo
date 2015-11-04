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
package com.aimluck.eip.note;

import java.util.jar.Attributes;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTNote;
import com.aimluck.eip.cayenne.om.portlet.EipTNoteMap;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.note.util.NoteUtils;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 伝言メモ依頼者検索データを管理するためのクラスです。
 */
public class NoteClientSelectData extends
    ALAbstractSelectData<EipTNoteMap, EipTNoteMap> implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(NoteClientSelectData.class.getName());

  /** ポートレットにアクセスしているユーザ ID */
  private String userId;

  /** 新着数 */
  private int newNoteAllSum = 0;

  /** 受信未読数 */
  private int unreadReceivedNotesAllSum = 0;

  /**
   *
   * @param action
   * @param rundata
   * @param context
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, ALEipUtils
        .getPortlet(rundata, context)
        .getPortletConfig()
        .getInitParameter("p2a-sort"));
      logger.debug("Init Parameter (Note) : "
        + ALEipUtils
          .getPortlet(rundata, context)
          .getPortletConfig()
          .getInitParameter("p2a-sort"));
    }

    super.init(action, rundata, context);
  }

  /**
   *
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<EipTNoteMap> selectList(RunData rundata, Context context) {

    try {
      userId = Integer.toString(ALEipUtils.getUserId(rundata));
      newNoteAllSum = NoteUtils.getNewReceivedNoteAllSum(rundata, userId);
      unreadReceivedNotesAllSum =
        NoteUtils.getUnreadReceivedNotesAllSum(rundata, userId);

      SelectQuery<EipTNoteMap> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      return query.getResultList();
    } catch (Exception ex) {
      logger.error("note", ex);
      return null;
    }
  }

  /**
   * ソート用の <code>SelectQuery</code> を構築します。
   *
   * @param crt
   * @return
   */
  @Override
  protected SelectQuery<EipTNoteMap> buildSelectQueryForListViewSort(
      SelectQuery<EipTNoteMap> query, RunData rundata, Context context) {
    String sort = "create_date";
    String sort_type = ALEipConstants.LIST_SORT_TYPE_DESC;
    String crt_key = null;

    Attributes map = getColumnMap();

    crt_key = map.getValue(sort);
    if (crt_key == null) {
      return query;
    }
    query.orderDesending(crt_key);
    current_sort = sort;
    current_sort_type = sort_type;
    return query;

  }

  /**
   *
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected EipTNoteMap selectDetail(RunData rundata, Context context) {
    return null;
  }

  /**
   *
   * @param map
   * @return
   */
  @Override
  protected Object getResultData(EipTNoteMap map) {
    try {
      EipTNote record = map.getEipTNote();

      NoteClientResultData rd = new NoteClientResultData();
      rd.initField();
      rd.setNoteId(record.getNoteId().intValue());
      rd.setClientName(ALCommonUtils.compressString(
        record.getClientName(),
        getStrLength()));
      rd.setCompanyName(ALCommonUtils.compressString(
        record.getCompanyName(),
        getStrLength()));
      rd.setNoteStat(map.getNoteStat());
      rd.setAcceptDate(record.getAcceptDate());

      String subject = "";
      if (record.getSubjectType().equals("0")) {
        subject =
          ALCommonUtils.compressString(
            record.getCustomSubject(),
            getStrLength());
      } else if (record.getSubjectType().equals("1")) {
        subject = ALLocalizationUtils.getl10n("NOTE_CALL_AGAIN");
      } else if (record.getSubjectType().equals("2")) {
        subject = ALLocalizationUtils.getl10n("NOTE_CALL_BACK");
      } else if (record.getSubjectType().equals("3")) {
        subject = ALLocalizationUtils.getl10n("NOTE_TELL_ME");
      } else if (record.getSubjectType().equals("4")) {
        subject = ALLocalizationUtils.getl10n("NOTE_TAKE_A_MESSAGE");
      }

      rd.setSubject(subject);

      if (NoteUtils.NOTE_STAT_NEW.equals(map.getNoteStat())) {
        rd.setNoteStat(NoteUtils.NOTE_STAT_NEW);
        rd.setNoteStatImage("images/note/note_new_message.gif");
        rd.setNoteStatImageDescription(ALLocalizationUtils
          .getl10n("NOTE_NEW_MESSAGE"));
      } else if (NoteUtils.NOTE_STAT_UNREAD.equals(map.getNoteStat())) {
        rd.setNoteStat(NoteUtils.NOTE_STAT_UNREAD);
        rd.setNoteStatImage("images/note/note_unread_message.gif");
        rd.setNoteStatImageDescription(ALLocalizationUtils
          .getl10n("NOTE_UNREAD_MESSAGE"));
      } else {
        rd.setNoteStat(NoteUtils.NOTE_STAT_READ);
        rd.setNoteStatImage("images/note/note_read_message.gif");
        rd.setNoteStatImageDescription(ALLocalizationUtils
          .getl10n("NOTE_READ_MESSAGE"));
      }

      ALEipUser user =
        ALEipUtils
          .getALEipUser(Integer.valueOf(record.getOwnerId()).intValue());
      rd.setSrcUserId(record.getOwnerId());
      rd.setSrcUserFullName(user.getAliasName().getValue());

      return rd;
    } catch (RuntimeException e) {
      logger.error("note", e);
      return null;
    } catch (Exception ex) {
      logger.error("note", ex);
      return null;
    }
  }

  /**
   *
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTNoteMap obj) {
    return null;
  }

  /**
   *
   * @return
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("client_name", EipTNoteMap.EIP_TNOTE_PROPERTY
      + "."
      + EipTNote.CLIENT_NAME_PROPERTY);
    map.putValue("subject_type", EipTNoteMap.EIP_TNOTE_PROPERTY
      + "."
      + EipTNote.SUBJECT_TYPE_PROPERTY);
    map.putValue("create_date", EipTNoteMap.EIP_TNOTE_PROPERTY
      + "."
      + EipTNote.CREATE_DATE_PROPERTY);
    map.putValue("accept_date", EipTNoteMap.EIP_TNOTE_PROPERTY
      + "."
      + EipTNote.ACCEPT_DATE_PROPERTY);
    map.putValue("note_stat", EipTNoteMap.NOTE_STAT_PROPERTY);
    return map;
  }

  /**
   * 検索条件を設定した Criteria を返す．
   *
   * @param rundata
   * @return
   */
  private SelectQuery<EipTNoteMap> getSelectQuery(RunData rundata,
      Context context) {
    return NoteUtils.getSelectQueryNoteList(rundata, context);
  }

  /**
   *
   * @return
   */
  public String getUserId() {
    return userId;
  }

  /**
   *
   * @param userId
   * @return
   */
  public String getUserName(String userId) {
    return NoteUtils.getUserName(userId);
  }

  /**
   *
   * @param userId
   * @return
   */
  public String getUserFullName(String userId) {
    try {
      ALEipUser user =
        ALEipUtils.getALEipUser(Integer.valueOf(userId).intValue());
      return user.getAliasName().getValue();
    } catch (Exception e) {
      return "";
    }
  }

  /**
   *
   * @param userName
   * @return
   */
  public String getUserId(String userName) {
    return NoteUtils.getUserId(userName);
  }

  /**
   *
   * @return
   */
  public int getNewNoteAllSum() {
    return newNoteAllSum;
  }

  /**
   *
   * @return
   */
  public int getUnreadReceivedNotesAllSum() {
    return unreadReceivedNotesAllSum;
  }

}

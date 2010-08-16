/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.note;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import com.aimluck.eip.orm.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTNote;
import com.aimluck.eip.cayenne.om.portlet.EipTNoteMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.note.util.NoteUtils;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 伝言メモの検索データを管理するためのクラスです。 <br />
 */
public class NoteSelectData extends ALAbstractSelectData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(NoteSelectData.class.getName());

  /** 現在選択しているタブ */
  private String currentTab;

  /** ポートレットにアクセスしているユーザ ID */
  private String userId;

  /** 表示対象の部署名 */
  private String target_group_name;

  /** 表示対象のユーザ ID */
  private String target_user_id;

  private List<ALEipGroup> myGroupList = null;

  /** 受信／送信した未読メモ */
  private int unreadNotesAllSum = 0;

  /** 新着数 */
  private int newNoteAllSum = 0;

  /** <code>statusList</code> メンバーの状態 */
  private Map<Integer, String> statusList;

  /** <code>members</code> 送信先メンバー */
  private List<ALEipUser> members;

  /** <code>mailAccountURI</code> ポートレット WebMailAccountEdit のへのリンク */
  private String mailAccountURI;

  /** <code>userAccountURI</code> ポートレット AccountEdit のへのリンク */
  private String userAccountURI;

  private DataContext dataContext;

  /**
   *
   * @param action
   * @param rundata
   * @param context
   * @see com.aimluck.eip.common.ALAbstractSelectData#init(com.aimluck.eip.modules.actions.common.ALAction,
   *      org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
   */
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    setCurrentTab(rundata, context);

    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      String sortkey = null;
      if ("received_notes".equals(getCurrentTab())) {
        sortkey = "p2a-sort";
      } else {
        sortkey = "p2b-sort";
      }
      sort = ALEipUtils.getPortlet(rundata, context).getPortletConfig()
          .getInitParameter(sortkey);
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, sort);
      logger.debug("Init Parameter (Note) : "
          + ALEipUtils.getPortlet(rundata, context).getPortletConfig()
              .getInitParameter(sortkey));
    } else {
      if ("received_notes".equals(getCurrentTab())) {
        if ("create_date".equals(sort)) {
          sort = "note_stat";
          ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, sort);
        }
      } else {
        if ("accept_date".equals(sort) || "note_stat".equals(sort)) {
          sort = "create_date";
          ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, sort);
          ALEipUtils.setTemp(rundata, context, LIST_SORT_TYPE_STR,
              ALEipConstants.LIST_SORT_TYPE_DESC);
        }
      }
    }

    userId = Integer.toString(ALEipUtils.getUserId(rundata));
    statusList = new HashMap<Integer, String>();

    // ポートレット WebMailAccountEdit のへのリンクを取得する．
    mailAccountURI = NoteUtils.getPortletURIinPersonalConfigPane(rundata,
        "WebMailAccountEdit");

    // ポートレット AccountEdit のへのリンクを取得する．
    userAccountURI = NoteUtils.getPortletURIinPersonalConfigPane(rundata,
        "AccountEdit");

    dataContext = DatabaseOrmService.getInstance().getDataContext();

    super.init(action, rundata, context);
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectList(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected List<?> selectList(RunData rundata, Context context) {

    try {
      target_group_name = NoteUtils.getTargetGroupName(rundata, context);
      target_user_id = NoteUtils.getTargetUserId(rundata, context);

      List<ALEipGroup> myGroups = ALEipUtils.getMyGroups(rundata);
      myGroupList = new ArrayList<ALEipGroup>();
      int length = myGroups.size();
      for (int i = 0; i < length; i++) {
        myGroupList.add(myGroups.get(i));
      }

      SelectQuery<EipTNoteMap> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      List<?> list = dataContext.performQuery(query);
      return buildPaginatedList(list);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectDetail(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected Object selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException {
    userId = Integer.toString(ALEipUtils.getUserId(rundata));
    setCurrentTab(rundata, context);

    if ("received_notes".equals(currentTab)) {
      // 受信履歴の未読数をセットする．
      unreadNotesAllSum = NoteUtils.getUnreadReceivedNotesAllSum(rundata,
          userId);
      // 受信履歴の新着数をセットする．
      newNoteAllSum = NoteUtils.getNewReceivedNoteAllSum(rundata, userId);
    } else {
      // 送信履歴の未読数をセットする．
      // unreadNotesAllSum = NoteUtils.getUnreadSentNotesAllSum(userId);
      // 送信履歴の新着数をセットする．
      // newNoteAllSum = NoteUtils.getNewSentNoteAllSum(userId);
    }

    EipTNote note = NoteUtils.getEipTNoteDetail(rundata, context,
        getSelectQueryForDetail(rundata, context));

    if (note == null) {
      logger.debug("[NoteSelectData] This page cannot be loaded.");
      throw new ALPageNotFoundException();
    }

    return note;
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultData(java.lang.Object)
   */
  protected Object getResultData(Object obj) {
    try {
      EipTNoteMap map = (EipTNoteMap) obj;
      EipTNote record = map.getEipTNote();

      String destUserNames = getDestUserNamesLimit(record);

      NoteResultData rd = new NoteResultData();
      rd.initField();
      rd.setNoteId(record.getNoteId().longValue());
      rd.setSrcUserId(record.getOwnerId());
      rd.setDestUserId(map.getUserId());

      ALEipUser user = ALEipUtils.getALEipUser(Integer.valueOf(
          record.getOwnerId()).intValue());
      rd.setSrcUserFullName(user.getAliasName().getValue());
      rd.setDestUserFullName(destUserNames);
      rd.setClientName(ALCommonUtils.compressString(record.getClientName(),
          getStrLength()));
      rd.setCompanyName(ALCommonUtils.compressString(record.getCompanyName(),
          getStrLength()));
      rd.setTelephone(record.getTelephone());
      rd.setEmailAddress(record.getEmailAddress());
      if (record.getAddDestType().equals("1")) {
        rd.setAddDestTypePc("1");
      } else if (record.getAddDestType().equals("2")) {
        rd.setAddDestTypeCellphone("1");
      } else if (record.getAddDestType().equals("3")) {
        rd.setAddDestTypePc("1");
        rd.setAddDestTypeCellphone("1");
      }
      rd.setSubjectType(record.getSubjectType());
      if ("0".equals(record.getSubjectType())) {
        rd.setCustomSubject(ALCommonUtils.compressString(
            record.getCustomSubject(), getStrLength()));
      }

      rd.setMessage(record.getMessage());
      rd.setAcceptDate(record.getAcceptDate());
      rd.setConfirmDate(map.getConfirmDate());
      rd.setCreateDate(record.getCreateDate());
      rd.setUpdateDate(record.getUpdateDate());

      if (NoteUtils.NOTE_STAT_NEW.equals(map.getNoteStat())) {
        rd.setNoteStat(NoteUtils.NOTE_STAT_NEW);
        rd.setNoteStatImage("images/note/note_new_message.gif");
        rd.setNoteStatImageDescription("新着");
        // 新着数をカウントアップする．
        // newNoteAllSum++;
      } else if (NoteUtils.NOTE_STAT_UNREAD.equals(map.getNoteStat())) {
        rd.setNoteStat(NoteUtils.NOTE_STAT_UNREAD);
        rd.setNoteStatImage("images/note/note_unread_message.gif");
        rd.setNoteStatImageDescription("未読");
        // 受信履歴の未読数をカウントアップする．
        unreadNotesAllSum++;
      } else if (NoteUtils.NOTE_STAT_READ.equals(map.getNoteStat())) {
        rd.setNoteStat(NoteUtils.NOTE_STAT_READ);
        rd.setNoteStatImage("images/note/note_read_message.gif");
        rd.setNoteStatImageDescription("既読");
      } else {
        rd.setNoteStat(NoteUtils.NOTE_STAT_DELETED);
        rd.setNoteStatImage("images/note/note_deleted_message.gif");
        rd.setNoteStatImageDescription("削除済み");
      }

      if (record.getMessage() == null || record.getMessage().equals("")) {
        rd.setHasMemo(false);
      } else {
        rd.setHasMemo(true);
      }

      // 伝言メモを登録
      if (map.getUserId().equals(userId)
          && (!record.getOwnerId().equals(userId))
          && map.getNoteStat().equals(NoteUtils.NOTE_STAT_NEW)) {
        // 未読フラグ
        map.setNoteStat(NoteUtils.NOTE_STAT_UNREAD);
      }
      dataContext.commitChanges();

      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultDataDetail(java.lang.Object)
   */
  protected Object getResultDataDetail(Object obj) {
    if (obj == null)
      return null;
    try {
      Date nowDate = Calendar.getInstance().getTime();

      EipTNoteMap map = null;
      EipTNote record = (EipTNote) obj;

      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      SelectQuery<EipTNoteMap> mapquery = new SelectQuery<EipTNoteMap>(EipTNoteMap.class);
      Expression mapexp = ExpressionFactory.matchExp(
          EipTNoteMap.NOTE_ID_PROPERTY, record.getNoteId());
      mapquery.setQualifier(mapexp);
      List<?> list = dataContext.performQuery(mapquery);

      List<Integer> users = new ArrayList<Integer>();
      int size = list.size();
      for (int i = 0; i < size; i++) {
        EipTNoteMap notemap = (EipTNoteMap) list.get(i);
        if (userId.equals(notemap.getUserId())) {
          map = notemap;
        }

        if ("T".equals(notemap.getDelFlg())) {
          statusList.put(Integer.valueOf(notemap.getUserId()),
              NoteUtils.NOTE_STAT_DELETED);
        } else {
          statusList.put(Integer.valueOf(notemap.getUserId()),
              notemap.getNoteStat());
        }
        users.add(Integer.valueOf(notemap.getUserId()));
      }

      SelectQuery<TurbineUser> query = new SelectQuery<TurbineUser>(TurbineUser.class);
      Expression exp = ExpressionFactory.inDbExp(TurbineUser.USER_ID_PK_COLUMN,
          users);
      query.setQualifier(exp);

      members = ALEipUtils.getUsersFromSelectQuery(query);

      String destUserNames = "";

      NoteResultData rd = new NoteResultData();
      rd.initField();
      rd.setNoteId(record.getNoteId().longValue());
      rd.setSrcUserId(record.getOwnerId());
      rd.setDestUserId(map.getUserId());

      ALEipUser user = ALEipUtils.getALEipUser(Integer.valueOf(
          record.getOwnerId()).intValue());
      rd.setSrcUserFullName(user.getAliasName().getValue());
      rd.setDestUserFullName(destUserNames);
      rd.setClientName(record.getClientName());
      rd.setCompanyName(record.getCompanyName());
      rd.setTelephone(record.getTelephone());
      rd.setEmailAddress(record.getEmailAddress());
      if (record.getAddDestType().equals("1")) {
        rd.setAddDestTypePc("1");
      } else if (record.getAddDestType().equals("2")) {
        rd.setAddDestTypeCellphone("1");
      } else if (record.getAddDestType().equals("3")) {
        rd.setAddDestTypePc("1");
        rd.setAddDestTypeCellphone("1");
      }
      rd.setSubjectType(record.getSubjectType());
      if ("0".equals(record.getSubjectType())) {
        rd.setCustomSubject(record.getCustomSubject());
      }
      rd.setMessage(record.getMessage());
      rd.setAcceptDate(record.getAcceptDate());
      rd.setCreateDate(record.getCreateDate());

      // 伝言メモの受信者の確認日時と未読／既読を登録する
      if (map.getUserId().equals(userId)
          && (!record.getOwnerId().equals(userId))) {
        if (map.getConfirmDate() == null) {
          // 確認日時
          map.setConfirmDate(nowDate);
          rd.setConfirmDate(nowDate);
        } else {
          rd.setConfirmDate(map.getConfirmDate());
        }

        if (map.getNoteStat().equals(NoteUtils.NOTE_STAT_READ)) {
          rd.setNoteStat(map.getNoteStat());
        } else {
          // 既読に変更．
          map.setNoteStat(NoteUtils.NOTE_STAT_READ);
          rd.setNoteStat(NoteUtils.NOTE_STAT_READ);
        }

        record.setUpdateDate(nowDate);
        rd.setUpdateDate(nowDate);

        // 伝言メモを登録
        dataContext.commitChanges();
      } else {
        rd.setConfirmDate(map.getConfirmDate());
        rd.setNoteStat(map.getNoteStat());
        rd.setUpdateDate(record.getUpdateDate());
      }
      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getColumnMap()
   */
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("client_name", EipTNoteMap.EIP_TNOTE_PROPERTY + "."
        + EipTNote.CLIENT_NAME_PROPERTY);
    map.putValue("company_name", EipTNoteMap.EIP_TNOTE_PROPERTY + "."
        + EipTNote.COMPANY_NAME_PROPERTY);
    map.putValue("subject_type", EipTNoteMap.EIP_TNOTE_PROPERTY + "."
        + EipTNote.SUBJECT_TYPE_PROPERTY);
    map.putValue("create_date", EipTNoteMap.EIP_TNOTE_PROPERTY + "."
        + EipTNote.CREATE_DATE_PROPERTY);
    map.putValue("confirm_date", EipTNoteMap.CONFIRM_DATE_PROPERTY);
    map.putValue("accept_date", EipTNoteMap.EIP_TNOTE_PROPERTY + "."
        + EipTNote.ACCEPT_DATE_PROPERTY);
    // map.putValue("src_user", TurbineUserConstants.LAST_NAME_KANA);
    // map.putValue("dest_user", TurbineUserConstants.LAST_NAME_KANA);
    map.putValue("note_stat", EipTNoteMap.NOTE_STAT_PROPERTY);
    return map;
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipTNoteMap> getSelectQuery(RunData rundata, Context context) {
    SelectQuery<EipTNoteMap> query = new SelectQuery<EipTNoteMap>(EipTNoteMap.class);

    Expression exp1 = ExpressionFactory.matchExp(EipTNoteMap.USER_ID_PROPERTY,
        Integer.valueOf(userId));
    query.setQualifier(exp1);
    Expression exp2 = ExpressionFactory.matchExp(EipTNoteMap.DEL_FLG_PROPERTY,
        "F");
    query.andQualifier(exp2);

    if ("received_notes".equals(getCurrentTab())) {
      Expression exp3 = ExpressionFactory.noMatchExp(
          EipTNoteMap.EIP_TNOTE_PROPERTY + "." + EipTNote.OWNER_ID_PROPERTY,
          Integer.valueOf(userId));
      query.andQualifier(exp3);
    } else {
      Expression exp3 = ExpressionFactory.matchExp(
          EipTNoteMap.EIP_TNOTE_PROPERTY + "." + EipTNote.OWNER_ID_PROPERTY,
          Integer.valueOf(userId));
      query.andQualifier(exp3);
    }

    return query;
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipTNote> getSelectQueryForDetail(RunData rundata, Context context) {
    SelectQuery<EipTNote> query = new SelectQuery<EipTNote>(EipTNote.class);
    return query;
  }

  private String getDestUserNamesLimit(EipTNote note) throws ALDBErrorException {
    StringBuffer destUserNames = new StringBuffer();
    List<?> mapList = note.getEipTNoteMaps();
    if (mapList == null || mapList.size() == 0) {
      logger.error("[NoteSelectData] DatabaseException");
      throw new ALDBErrorException();
    }
    int mapListSize = mapList.size();
    for (int i = 0; i < mapListSize; i++) {
      EipTNoteMap tmpmap = (EipTNoteMap) mapList.get(i);
      if (tmpmap.getUserId().equals(userId)) {
        mapList.remove(i);
        break;
      }
    }

    mapListSize = mapList.size();
    if (mapListSize >= 2) {
      EipTNoteMap tmpmap = (EipTNoteMap) mapList.get(0);
      ALEipUser user = ALEipUtils.getALEipUser(Integer.valueOf(
          tmpmap.getUserId()).intValue());
      destUserNames.append(user.getAliasName());
      destUserNames.append("、・・・");
    } else {
      EipTNoteMap tmpmap = (EipTNoteMap) mapList.get(0);
      ALEipUser user = ALEipUtils.getALEipUser(Integer.valueOf(
          tmpmap.getUserId()).intValue());
      destUserNames.append(user.getAliasName());
    }
    return destUserNames.toString();
  }

  private void setCurrentTab(RunData rundata, Context context) {
    String tabParam = rundata.getParameters().getString("tab");
    currentTab = ALEipUtils.getTemp(rundata, context, "tab");
    if (tabParam == null && currentTab == null) {
      ALEipUtils.setTemp(rundata, context, "tab", "received_notes");
      currentTab = "received_notes";
    } else if (tabParam != null) {
      ALEipUtils.setTemp(rundata, context, "tab", tabParam);
      currentTab = tabParam;
    }
  }

  /**
   * 現在選択されているタブを取得します。 <BR>
   *
   * @return
   */
  public String getCurrentTab() {
    return currentTab;
  }

  public String getUserId() {
    return userId;
  }

  public String getTargetGroupName() {
    return target_group_name;
  }

  public String getTargetUserId() {
    return target_user_id;
  }

  public String getUserName(String userId) {
    return NoteUtils.getUserName(userId);
  }

  public String getUserFullName(String userId) {
    try {
      ALEipUser user = ALEipUtils.getALEipUser(Integer.valueOf(userId)
          .intValue());

      return user.getAliasName().getValue();
    } catch (Exception e) {
      return "";
    }
  }

  public String getUserId(String userName) {
    return NoteUtils.getUserId(userName);
  }

  /**
   *
   * @param groupname
   * @return
   */
  public List<ALEipUser> getUsers(String groupname) {
    return ALEipUtils.getUsers(groupname);
  }

  public int getNewNoteAllSum() {
    return newNoteAllSum;
  }

  public int getUnreadNotesAllSum() {
    return unreadNotesAllSum;
  }

  /**
   *
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  /**
   *
   * @return
   */
  public List<ALEipGroup> getMyGroupList() {
    return myGroupList;
  }

  /**
   * 状態を取得する．
   *
   * @param id
   * @return
   */
  public String getStatus(long id) {
    return statusList.get(Integer.valueOf((int) id));
  }

  /**
   * 送信先メンバーを取得します。
   *
   * @return
   */
  public List<ALEipUser> getMemberList() {
    return members;
  }

  public String getMailAccountURI() {
    return mailAccountURI;
  }

  public String getUserAccountURI() {
    return userAccountURI;
  }

}

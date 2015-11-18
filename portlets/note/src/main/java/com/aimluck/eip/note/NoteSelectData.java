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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTNote;
import com.aimluck.eip.cayenne.om.portlet.EipTNoteMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.social.Activity;
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
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SQLTemplate;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.social.ALActivityService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 伝言メモの検索データを管理するためのクラスです。
 */
public class NoteSelectData extends ALAbstractSelectData<EipTNoteMap, EipTNote> {

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
  private List<ALEipUser> members = null;

  /** <code>mailAccountURI</code> ポートレット WebMailAccountEdit のへのリンク */
  private String mailAccountURI;

  /** <code>userAccountURI</code> ポートレット AccountEdit のへのリンク */
  private String userAccountURI;

  private ALStringField target_keyword;

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

    setCurrentTab(rundata, context);
    if (NoteUtils.hasResetFlag(rundata, context)) {
      ALEipUtils.removeTemp(rundata, context, LIST_SORT_STR);
      ALEipUtils.removeTemp(rundata, context, LIST_SORT_TYPE_STR);
    }
    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);

    if (sort == null || sort.equals("")) {
      if ("received_notes".equals(getCurrentTab())) {
        sort = "accept_date";
      } else {
        sort = "create_date";
      }
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, sort);
    } else {
      if ("received_notes".equals(getCurrentTab())) {
        // 受信一覧
        if ("create_date".equals(sort)) {
          // 受信一覧に無いソートが指定されている場合、デフォルトを読み込む
          ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "accept_date");
        }
      } else {
        // 送信一覧
        if ("accept_date".equals(sort)
          || "note_stat".equals(sort)
          || "create_date".equals(sort)) {
          // 送信一覧に無いソートが指定されている場合、デフォルトを読み込む
          ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "create_date");
        }
      }
    }

    userId = Integer.toString(ALEipUtils.getUserId(rundata));
    statusList = new HashMap<Integer, String>();

    // ポートレット WebMailAccountEdit のへのリンクを取得する．
    mailAccountURI =
      NoteUtils
        .getPortletURIinPersonalConfigPane(rundata, "WebMailAccountEdit");

    // ポートレット AccountEdit のへのリンクを取得する．
    userAccountURI =
      NoteUtils.getPortletURIinPersonalConfigPane(rundata, "AccountEdit");

    target_keyword = new ALStringField();

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
      if (NoteUtils.hasResetFlag(rundata, context)) {
        NoteUtils.resetFilter(rundata, context, this.getClass().getName());
        target_keyword.setValue("");
      } else {
        target_keyword.setValue(NoteUtils.getTargetKeyword(rundata, context));
      }

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
    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    String sort_type = ALEipUtils.getTemp(rundata, context, LIST_SORT_TYPE_STR);
    String crt_key = null;

    Attributes map = getColumnMap();
    if (sort == null) {
      return query;
    }
    crt_key = map.getValue(sort);
    if (crt_key == null) {
      return query;
    }
    if (sort_type != null
      && ALEipConstants.LIST_SORT_TYPE_ASC.equals(sort_type)) {
      query.orderAscending(crt_key);
    } else {
      query.orderDesending(crt_key);
      sort_type = ALEipConstants.LIST_SORT_TYPE_DESC;
    }
    current_sort = sort;
    current_sort_type = sort_type;
    return query;
  }

  /**
   *
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   */
  @Override
  protected EipTNote selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException {
    userId = Integer.toString(ALEipUtils.getUserId(rundata));
    setCurrentTab(rundata, context);

    if ("received_notes".equals(currentTab)) {
      // 受信履歴の未読数をセットする．
      unreadNotesAllSum =
        NoteUtils.getUnreadReceivedNotesAllSum(rundata, userId);
      // 受信履歴の新着数をセットする．
      newNoteAllSum = NoteUtils.getNewReceivedNoteAllSum(rundata, userId);
    } else {
      // 送信履歴の未読数をセットする．
      // unreadNotesAllSum = NoteUtils.getUnreadSentNotesAllSum(userId);
      // 送信履歴の新着数をセットする．
      // newNoteAllSum = NoteUtils.getNewSentNoteAllSum(userId);
    }

    EipTNote note =
      NoteUtils.getEipTNoteDetail(rundata, context, getSelectQueryForDetail(
        rundata,
        context));

    if (note == null) {
      logger.debug("[NoteSelectData] This page cannot be loaded.");
      throw new ALPageNotFoundException();
    }

    return note;
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

      String destUserNames = getDestUserNamesLimit(record);

      NoteResultData rd = new NoteResultData();
      rd.initField();
      rd.setNoteId(record.getNoteId().longValue());
      rd.setSrcUserId(record.getOwnerId());
      rd.setDestUserId(map.getUserId());

      ALEipUser user =
        ALEipUtils
          .getALEipUser(Integer.valueOf(record.getOwnerId()).intValue());
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
      rd.setConfirmDate(map.getConfirmDate());
      rd.setCreateDate(record.getCreateDate());
      rd.setUpdateDate(record.getUpdateDate());

      Expression mapexp =
        ExpressionFactory.matchExp(EipTNoteMap.NOTE_ID_PROPERTY, record
          .getNoteId());
      List<EipTNoteMap> list =
        Database.query(EipTNoteMap.class, mapexp).fetchList();
      // メッセージを既読した人数
      Integer readNotes = 0;
      for (EipTNoteMap notemap : list) {
        if (notemap.getNoteStat().equals(NoteUtils.NOTE_STAT_READ)) {
          readNotes++;
        }
      }
      rd.setSentNote(list.size() - 1);
      rd.setReadNote(readNotes.longValue());

      List<Integer> users = new ArrayList<Integer>();
      for (EipTNoteMap notemap : list) {
        if (userId.equals(notemap.getUserId())) {
          map = notemap;
        }
        if ("T".equals(notemap.getDelFlg())) {
          statusList.put(
            Integer.valueOf(notemap.getUserId()),
            NoteUtils.NOTE_STAT_DELETED);
        } else {
          statusList.put(Integer.valueOf(notemap.getUserId()), notemap
            .getNoteStat());
        }
        users.add(Integer.valueOf(notemap.getUserId()));
      }
      SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
      Expression exp =
        ExpressionFactory.inDbExp(TurbineUser.USER_ID_PK_COLUMN, users);
      query.setQualifier(exp);
      members = ALEipUtils.getUsersFromSelectQuery(query);
      rd.setDestUsers(members);

      if (NoteUtils.NOTE_STAT_NEW.equals(map.getNoteStat())) {
        rd.setNoteStat(NoteUtils.NOTE_STAT_NEW);
        rd.setNoteStatImage("images/note/note_new_message.gif");
        rd.setNoteStatImageDescription(ALLocalizationUtils
          .getl10n("NOTE_NEW_MESSAGE"));
        // 新着数をカウントアップする．
        // newNoteAllSum++;
      } else if (NoteUtils.NOTE_STAT_UNREAD.equals(map.getNoteStat())) {
        rd.setNoteStat(NoteUtils.NOTE_STAT_UNREAD);
        rd.setNoteStatImage("images/note/note_unread_message.gif");
        rd.setNoteStatImageDescription(ALLocalizationUtils
          .getl10n("NOTE_UNREAD_MESSAGE"));
        // 受信履歴の未読数をカウントアップする．
        unreadNotesAllSum++;
      } else if (NoteUtils.NOTE_STAT_READ.equals(map.getNoteStat())) {
        rd.setNoteStat(NoteUtils.NOTE_STAT_READ);
        rd.setNoteStatImage("images/note/note_read_message.gif");
        rd.setNoteStatImageDescription(ALLocalizationUtils
          .getl10n("NOTE_READ_MESSAGE"));
      } else {
        rd.setNoteStat(NoteUtils.NOTE_STAT_DELETED);
        rd.setNoteStatImage("images/note/note_deleted_message.gif");
        rd.setNoteStatImageDescription(ALLocalizationUtils
          .getl10n("NOTE_DELETED_MESSAGE"));
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
      Database.commit();

      return rd;
    } catch (Throwable t) {
      Database.rollback();
      logger.error("[NoteSelectData]", t);
      return null;
    }
  }

  /**
   *
   * @param record
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTNote record) {
    if (record == null) {
      return null;
    }
    try {
      Date nowDate = Calendar.getInstance().getTime();

      EipTNoteMap map = null;
      Expression mapexp =
        ExpressionFactory.matchExp(EipTNoteMap.NOTE_ID_PROPERTY, record
          .getNoteId());
      List<EipTNoteMap> list =
        Database.query(EipTNoteMap.class, mapexp).fetchList();

      List<Integer> users = new ArrayList<Integer>();
      for (EipTNoteMap notemap : list) {
        if (userId.equals(notemap.getUserId())) {
          map = notemap;
        }

        if ("T".equals(notemap.getDelFlg())) {
          statusList.put(
            Integer.valueOf(notemap.getUserId()),
            NoteUtils.NOTE_STAT_DELETED);
        } else {
          statusList.put(Integer.valueOf(notemap.getUserId()), notemap
            .getNoteStat());
        }
        users.add(Integer.valueOf(notemap.getUserId()));
      }

      SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
      Expression exp =
        ExpressionFactory.inDbExp(TurbineUser.USER_ID_PK_COLUMN, users);
      query.setQualifier(exp);

      members = ALEipUtils.getUsersFromSelectQuery(query);

      String destUserNames = "";

      NoteResultData rd = new NoteResultData();
      rd.initField();
      rd.setNoteId(record.getNoteId().longValue());
      rd.setSrcUserId(record.getOwnerId());
      rd.setDestUserId(map.getUserId());

      ALEipUser user =
        ALEipUtils
          .getALEipUser(Integer.valueOf(record.getOwnerId()).intValue());
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

          Expression exp2 =
            ExpressionFactory.matchExp(Activity.EXTERNAL_ID_PROPERTY, rd
              .getNoteId()
              .toString());
          Expression exp3 =
            ExpressionFactory.matchExp(Activity.APP_ID_PROPERTY, "Note");
          Expression exp4 = exp2.andExp(exp3);
          List<Activity> list2 =
            Database.query(Activity.class, exp4).fetchList();

          for (Activity activity : list2) {
            ALActivityService.setRead(activity.getId(), ALEipUtils
              .getTurbineUser(Integer.valueOf(map.getUserId()))
              .getLoginName());

          }
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
        Database.commit();
      } else {
        rd.setConfirmDate(map.getConfirmDate());
        rd.setNoteStat(map.getNoteStat());
        rd.setUpdateDate(record.getUpdateDate());
      }
      return rd;
    } catch (RuntimeException ex) {
      Database.rollback();
      logger.error("note", ex);
      return null;
    } catch (Exception ex) {
      Database.rollback();
      logger.error("note", ex);
      return null;
    }
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
    map.putValue("company_name", EipTNoteMap.EIP_TNOTE_PROPERTY
      + "."
      + EipTNote.COMPANY_NAME_PROPERTY);
    map.putValue("subject_type", EipTNoteMap.EIP_TNOTE_PROPERTY
      + "."
      + EipTNote.SUBJECT_TYPE_PROPERTY);
    map.putValue("create_date", EipTNoteMap.EIP_TNOTE_PROPERTY
      + "."
      + EipTNote.CREATE_DATE_PROPERTY);
    map.putValue("confirm_date", EipTNoteMap.CONFIRM_DATE_PROPERTY);
    map.putValue("accept_date", EipTNoteMap.EIP_TNOTE_PROPERTY
      + "."
      + EipTNote.ACCEPT_DATE_PROPERTY);
    // map.putValue("src_user", TurbineUserConstants.LAST_NAME_KANA);
    // map.putValue("dest_user", TurbineUserConstants.LAST_NAME_KANA);
    map.putValue("note_stat", EipTNoteMap.NOTE_STAT_PROPERTY);
    return map;
  }

  /**
   * 検索条件を設定した SelectQuery を返します。
   *
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipTNoteMap> getSelectQuery(RunData rundata,
      Context context) {

    if ((target_keyword != null) && (!target_keyword.getValue().equals(""))) {
      ALEipUtils.setTemp(rundata, context, LIST_SEARCH_STR, target_keyword
        .getValue());
    } else {
      ALEipUtils.removeTemp(rundata, context, LIST_SEARCH_STR);
    }

    SelectQuery<EipTNoteMap> query = Database.query(EipTNoteMap.class);

    Expression exp1 =
      ExpressionFactory.matchExp(EipTNoteMap.USER_ID_PROPERTY, Integer
        .valueOf(userId));
    query.setQualifier(exp1);
    Expression exp2 =
      ExpressionFactory.matchExp(EipTNoteMap.DEL_FLG_PROPERTY, "F");
    query.andQualifier(exp2);

    if ("received_notes".equals(getCurrentTab())) {
      Expression exp3 =
        ExpressionFactory.noMatchExp(EipTNoteMap.EIP_TNOTE_PROPERTY
          + "."
          + EipTNote.OWNER_ID_PROPERTY, Integer.valueOf(userId));
      query.andQualifier(exp3);
    } else {
      Expression exp3 =
        ExpressionFactory.matchExp(EipTNoteMap.EIP_TNOTE_PROPERTY
          + "."
          + EipTNote.OWNER_ID_PROPERTY, Integer.valueOf(userId));
      query.andQualifier(exp3);
    }

    return buildSelectQueryForFilter(query, rundata, context);
  }

  @Override
  protected SelectQuery<EipTNoteMap> buildSelectQueryForFilter(
      SelectQuery<EipTNoteMap> query, RunData rundata, Context context) {

    String search = ALEipUtils.getTemp(rundata, context, LIST_SEARCH_STR);
    if (search != null && !search.equals("")) {
      current_search = search;
      Expression ex1 =
        ExpressionFactory.likeExp(EipTNote.CLIENT_NAME_PROPERTY, "%"
          + search
          + "%");
      Expression ex2 =
        ExpressionFactory.likeExp(EipTNote.COMPANY_NAME_PROPERTY, "%"
          + search
          + "%");
      Expression ex3 =
        ExpressionFactory.likeExp(EipTNote.EMAIL_ADDRESS_PROPERTY, "%"
          + search
          + "%");
      Expression ex4 =
        ExpressionFactory.likeExp(EipTNote.TELEPHONE_PROPERTY, "%"
          + search
          + "%");
      Expression ex5 =
        ExpressionFactory.likeExp(EipTNote.CUSTOM_SUBJECT_PROPERTY, "%"
          + search
          + "%");
      Expression ex6 =
        ExpressionFactory
          .likeExp(EipTNote.MESSAGE_PROPERTY, "%" + search + "%");

      StringBuilder body = new StringBuilder();
      body.append("SELECT eip_t_note_map.note_id");
      body.append(" FROM eip_t_note_map");
      body.append(" INNER JOIN eip_t_note");
      body.append(" ON eip_t_note_map.note_id = eip_t_note.note_id");
      body.append(" INNER JOIN turbine_user");
      body.append(" ON ").append(
        Database.castToIntRawColumn("eip_t_note_map.user_id")).append(
        " = turbine_user.user_id");
      body.append(" WHERE turbine_user.first_name LIKE #bind($search)");
      body.append(" OR turbine_user.last_name like #bind($search)");
      body.append(" OR turbine_user.first_name_kana LIKE #bind($search)");
      body.append(" OR turbine_user.last_name_kana LIKE #bind($search);");

      SQLTemplate<EipTNoteMap> Query =
        Database.sql(EipTNoteMap.class, body.toString()).param(
          "search",
          "%" + search + "%");
      List<DataRow> fetch = Query.fetchListAsDataRow();
      List<Integer> resultid = new ArrayList<Integer>();
      for (DataRow row : fetch) {
        resultid.add(((Integer) row.get("note_id")).intValue());
      }

      SelectQuery<EipTNote> q = Database.query(EipTNote.class);
      q.andQualifier((ex6
        .orExp(ex5.orExp(ex4.orExp(ex3.orExp(ex2.orExp(ex1)))))));
      List<EipTNote> queryList = q.fetchList();
      for (EipTNote item : queryList) {
        if (item.getNoteId() != 0 && !resultid.contains(item.getNoteId())) {
          resultid.add(item.getNoteId());
        } else if (!resultid.contains(item.getNoteId())) {
          resultid.add(item.getNoteId());
        }
      }
      if (resultid.size() == 0) {
        // 検索結果がないことを示すために-1を代入
        resultid.add(-1);
      }
      Expression ex =
        ExpressionFactory.inDbExp(EipTNote.NOTE_ID_PK_COLUMN, resultid);
      query.andQualifier(ex);
    }
    return query;
  }

  /**
   * 検索条件を設定した SelectQuery を返します。
   *
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipTNote> getSelectQueryForDetail(RunData rundata,
      Context context) {
    return Database.query(EipTNote.class);
  }

  /**
   * 一覧表示します。
   *
   * @param action
   * @param rundata
   * @param context
   * @return TRUE 成功 FASLE 失敗
   */

  private String getDestUserNamesLimit(EipTNote note) throws ALDBErrorException {
    StringBuffer destUserNames = new StringBuffer();
    @SuppressWarnings("unchecked")
    List<EipTNoteMap> mapList = note.getEipTNoteMaps();
    if (mapList == null || mapList.size() == 0) {
      logger.error("[NoteSelectData] DatabaseException");
      throw new ALDBErrorException();
    }
    int mapListSize = mapList.size();
    for (int i = 0; i < mapListSize; i++) {
      EipTNoteMap tmpmap = mapList.get(i);
      if (tmpmap.getUserId().equals(userId)) {
        mapList.remove(i);
        break;
      }
    }

    // mapListSize = mapList.size();
    if (mapList.size() > 0) {
      for (EipTNoteMap tmpmap : mapList) {
        ALEipUser user =
          ALEipUtils.getALEipUser(Integer
            .valueOf(tmpmap.getUserId())
            .intValue());
        destUserNames.append(tmpmap == mapList.get(0)
          ? user.getAliasName()
          : "," + user.getAliasName());
      }
    }

    // if (mapListSize >= 2) {
    // EipTNoteMap tmpmap = mapList.get(0);
    // ALEipUser user =
    // ALEipUtils.getALEipUser(Integer.valueOf(tmpmap.getUserId()).intValue());
    // destUserNames.append(user.getAliasName());
    // destUserNames.append("、・・・");
    // } else {
    // EipTNoteMap tmpmap = mapList.get(0);
    // ALEipUser user =
    // ALEipUtils.getALEipUser(Integer.valueOf(tmpmap.getUserId()).intValue());
    // destUserNames.append(user.getAliasName());
    // }
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
   * 現在選択されているタブを取得します。
   *
   * @return
   */
  public String getCurrentTab() {
    return currentTab;
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
   * @return
   */
  public String getTargetGroupName() {
    return target_group_name;
  }

  /**
   *
   * @return
   */
  public String getTargetUserId() {
    return target_user_id;
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
    } catch (RuntimeException e) {
      throw e;
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
   * @param groupname
   * @return
   */
  public List<ALEipUser> getUsers(String groupname) {
    return ALEipUtils.getUsers(groupname);
  }

  public int getNewNoteAllSum() {
    return newNoteAllSum;
  }

  /**
   *
   * @return
   */
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

  /**
   *
   * @return
   */
  public String getMailAccountURI() {
    return mailAccountURI;
  }

  /**
   *
   * @return
   */
  public String getUserAccountURI() {
    return userAccountURI;
  }

  public ALStringField getTargetKeyword() {
    return target_keyword;
  }
}

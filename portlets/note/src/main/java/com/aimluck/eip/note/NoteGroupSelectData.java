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
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.account.EipMUserPosition;
import com.aimluck.eip.cayenne.om.portlet.EipTNoteMap;
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.note.util.NoteUtils;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 伝言メモの送信先に指定できるグループの検索データを管理するためのクラスです。 <br />
 */
public class NoteGroupSelectData extends ALAbstractSelectData<TurbineUser>
    implements ALData {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(NoteGroupSelectData.class.getName());

  /** 現在選択しているタブ */
  private String currentTab;

  private String userId = null;

  private String userAliasName = null;

  private List<ALEipGroup> myGroupList = null;

  private int unreadReceivedNotesAllSum = 0;

  /** 新着数 */
  private int newNoteAllSum = 0;

  /**
   * 初期化処理を行います。 <BR>
   *
   * @param action
   * @param rundata
   * @param context
   */
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    ALEipUtils.removeTemp(rundata, context, NoteUtils.TARGET_USER_ID);

    super.init(action, rundata, context);

    // グループの初期値を取得する
    try {
      String filter = ALEipUtils.getTemp(rundata, context, LIST_FILTER_STR);
      if (filter == null || filter.equals("")) {
        VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
        String groupName = portlet.getPortletConfig().getInitParameter(
            "p3b-group");
        if (groupName != null) {
          ALEipUtils.setTemp(rundata, context, LIST_FILTER_STR, groupName);
          ALEipUtils.setTemp(rundata, context, LIST_FILTER_TYPE_STR, "group");
        }
      }
    } catch (Exception ex) {
      logger.debug("Exception", ex);
    }
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectList(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected List<TurbineUser> selectList(RunData rundata, Context context) {
    setCurrentTab(rundata, context);
    try {
      userId = Integer.toString(ALEipUtils.getUserId(rundata));
      userAliasName = ALEipUtils.getALEipUser(rundata).getAliasName()
          .toString();
      NoteUtils.getTargetGroupName(rundata, context);

      List<ALEipGroup> myGroups = ALEipUtils.getMyGroups(rundata);
      myGroupList = new ArrayList<ALEipGroup>();
      int length = myGroups.size();
      for (int i = 0; i < length; i++) {
        myGroupList.add(myGroups.get(i));
      }

      // 受信履歴の未読数と新着数をカウントアップする．

      List<EipTNoteMap> list = NoteUtils.getSelectQueryNoteList(rundata,
          context).perform();
      // List list = orm_notemap.doSelect();
      if (list != null && list.size() > 0) {
        String stat = null;
        int size = list.size();
        for (int i = 0; i < size; i++) {
          EipTNoteMap map = list.get(i);
          stat = map.getNoteStat();
          if (NoteUtils.NOTE_STAT_NEW.equals(stat)) {
            // 新着数をカウントアップする．
            newNoteAllSum++;
          } else if (NoteUtils.NOTE_STAT_UNREAD.equals(stat)) {
            // 受信履歴の未読数をカウントアップする．
            unreadReceivedNotesAllSum++;
          }
        }
      }

      String filter = ALEipUtils.getTemp(rundata, context, LIST_FILTER_STR);
      String filter_type = ALEipUtils.getTemp(rundata, context,
          LIST_FILTER_TYPE_STR);
      if (filter == null || filter_type == null || filter.equals("")) {
        return new ArrayList<TurbineUser>();
      } else {
        SelectQuery<TurbineUser> query = getSelectQuery(rundata, context);
        buildSelectQueryForListView(query);
        buildSelectQueryForListViewSort(query, rundata, context);

        List<TurbineUser> ulist = query.perform();
        return buildPaginatedList(ulist);
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectDetail(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected TurbineUser selectDetail(RunData rundata, Context context) {
    setCurrentTab(rundata, context);
    return null;
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultData(java.lang.Object)
   */
  protected Object getResultData(TurbineUser user) {
    try {
      NoteGroupResultData rd = new NoteGroupResultData();
      rd.initField();
      rd.setUserId(user.getUserId().intValue());
      rd.setUserName(user.getLastName() + " " + user.getFirstName());
      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultDataDetail(java.lang.Object)
   */
  protected Object getResultDataDetail(TurbineUser obj) {
    return null;
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getColumnMap()
   */
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("src_user", TurbineUser.LAST_NAME_KANA_PROPERTY);
    map.putValue("group", TurbineUser.TURBINE_USER_GROUP_ROLE_PROPERTY + "."
        + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY + "."
        + TurbineGroup.GROUP_NAME_PROPERTY);
    map.putValue("userposition", TurbineUser.EIP_MUSER_POSITION_PROPERTY + "."
        + EipMUserPosition.POSITION_PROPERTY); // ユーザの順番
    return map;
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<TurbineUser> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<TurbineUser> query = new SelectQuery<TurbineUser>(
        TurbineUser.class);

    Expression exp11 = ExpressionFactory.noMatchDbExp(
        TurbineUser.USER_ID_PK_COLUMN, Integer.valueOf(1));
    Expression exp12 = ExpressionFactory.noMatchDbExp(
        TurbineUser.USER_ID_PK_COLUMN, Integer.valueOf(2));
    Expression exp13 = ExpressionFactory.noMatchDbExp(
        TurbineUser.USER_ID_PK_COLUMN, Integer.valueOf(3));
    query.setQualifier(exp11.andExp(exp12).andExp(exp13));

    Expression exp2 = ExpressionFactory.matchExp(TurbineUser.DISABLED_PROPERTY,
        "F");
    query.andQualifier(exp2);
    Expression exp3 = ExpressionFactory.noMatchDbExp(
        TurbineUser.USER_ID_PK_COLUMN, Integer.valueOf(userId));
    query.andQualifier(exp3);

    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * フィルタ用の <code>SelectQuery</code> を構築します。
   *
   * @param crt
   * @param rundata
   * @param context
   * @return
   */
  protected SelectQuery<TurbineUser> buildSelectQueryForFilter(
      SelectQuery<TurbineUser> query, RunData rundata, Context context) {
    String filter = ALEipUtils.getTemp(rundata, context, LIST_FILTER_STR);
    String filter_type = ALEipUtils.getTemp(rundata, context,
        LIST_FILTER_TYPE_STR);
    String crt_key = null;
    Attributes map = getColumnMap();
    if (filter == null || filter_type == null || filter.equals(""))
      return query;
    crt_key = map.getValue(filter_type);
    if (crt_key == null)
      return query;

    Expression exp = ExpressionFactory.matchExp(crt_key, filter);
    query.andQualifier(exp);
    current_filter = filter;
    current_filter_type = filter_type;
    return query;
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

  public String getUserAliasName() {
    return userAliasName;
  }

  public int getNewNoteAllSum() {
    return newNoteAllSum;
  }

  public int getUnreadReceivedNotesAllSum() {
    return unreadReceivedNotesAllSum;
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

}

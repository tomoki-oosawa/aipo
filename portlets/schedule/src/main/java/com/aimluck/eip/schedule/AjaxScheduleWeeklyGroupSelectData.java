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
package com.aimluck.eip.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Parameter;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.psml.PsmlParameter;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.cayenne.om.portlet.EipTTodo;
import com.aimluck.eip.cayenne.om.portlet.VEipTScheduleList;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.facilities.FacilityResultData;
import com.aimluck.eip.facilities.util.FacilitiesUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.services.portal.ALPortalApplicationService;
import com.aimluck.eip.todo.util.ToDoUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * カレンダー用週間スケジュールの検索結果を管理するクラスです。
 *
 */
public class AjaxScheduleWeeklyGroupSelectData extends
    AjaxScheduleMonthlySelectData {

  /** <code>logger</code> logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AjaxScheduleWeeklyGroupSelectData.class.getName());

  /** <code>prevDate</code> 前の日 */
  private ALDateTimeField prevDate;

  /** <code>nextDate</code> 次の日 */
  private ALDateTimeField nextDate;

  /** <code>prevWeek</code> 前の週 */
  private ALDateTimeField prevWeek;

  /** <code>nextWeek</code> 次の週 */
  private ALDateTimeField nextWeek;

  /** <code>today</code> 今日 */
  private ALDateTimeField today;

  /** <code>prevMonth</code> 前の月 */
  private ALDateTimeField prevMonth;

  /** <code>nextMonth</code> 次の月 */
  private ALDateTimeField nextMonth;

  /** <code>viewStart</code> 表示開始日時 */
  private ALDateTimeField viewStart;

  /** <code>viewEnd</code> 表示終了日時 */
  private ALDateTimeField viewEnd;

  /** <code>viewEndCrt</code> 表示終了日時 (Criteria) */
  private ALDateTimeField viewEndCrt;

  /** <code>weekCon</code> 週間スケジュールコンテナ */
  private AjaxScheduleWeekContainer weekCon;

  /** <code>viewtype</code> 表示タイプ */
  protected String viewtype;

  /** <code>tmpCal</code> テンポラリ日付 */
  protected Calendar tmpCal;

  /** <code>weekTodoConList</code> ToDo リスト（週間スケジュール用） */
  private List<ScheduleToDoWeekContainer> weekTodoConList;

  /** <code>weekTermConList</code> 期間スケジュール リスト（週間スケジュール用） */
  private List<AjaxTermScheduleWeekContainer> weekTermConList;

  /** <code>viewJob</code> ToDo 表示設定 */
  protected int viewTodo;

  /** <code>memberList</code> メンバーリスト */
  private List<Integer> memberList;

  /** <code>facilityList</code> メンバーリスト */
  private List<Integer> facilityList;

  /** ポートレット ID */
  private String portletId;

  /** ログインユーザID */
  private int userid;

  /** 共有スケジュールを全員分表示するかどうか */
  private boolean show_all;

  /** <code>doneList</code> 入力済み期間スケジュールリスト */
  private List<Integer> doneTermList;

  private Integer uid;

  private String acl_feat;

  private String has_acl_other;

  /**
   *
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    // 展開されるパラメータは以下の通りです。
    // ・viewStart 形式：yyyy-MM-dd
    uid = Integer.valueOf(ALEipUtils.getUserId(rundata));
    // 表示タイプの設定
    viewtype = "weekly";
    // POST/GET から yyyy-MM-dd の形式で受け渡される。
    // 前の日
    prevDate = new ALDateTimeField("yyyy-MM-dd");
    // 次の日
    nextDate = new ALDateTimeField("yyyy-MM-dd");
    // 前の週
    prevWeek = new ALDateTimeField("yyyy-MM-dd");
    // 次の週
    nextWeek = new ALDateTimeField("yyyy-MM-dd");
    // 前の月
    prevMonth = new ALDateTimeField("yyyy-MM-dd");
    // 次の月
    nextMonth = new ALDateTimeField("yyyy-MM-dd");
    // 表示開始日時
    viewStart = new ALDateTimeField("yyyy-MM-dd");
    viewStart.setNotNull(true);
    // 表示終了日時
    viewEnd = new ALDateTimeField("yyyy-MM-dd");
    // 表示終了日時 (Criteria)
    viewEndCrt = new ALDateTimeField("yyyy-MM-dd");
    // 今日
    today = new ALDateTimeField("yyyy-MM-dd");
    Calendar to = Calendar.getInstance();
    to.set(Calendar.HOUR_OF_DAY, 0);
    to.set(Calendar.MINUTE, 0);
    today.setValue(to.getTime());

    // 自ポートレットからのリクエストであれば、パラメータを展開しセッションに保存する。
    if (ALEipUtils.isMatch(rundata, context)) {
      // スケジュールの表示開始日時
      // e.g. 2004-3-14
      if (rundata.getParameters().containsKey("view_start")) {
        ALEipUtils.setTemp(rundata, context, "view_start", rundata
          .getParameters()
          .getString("view_start"));
      }
    }

    // 表示開始日時
    String tmpViewStart = ALEipUtils.getTemp(rundata, context, "view_start");
    if (tmpViewStart == null || tmpViewStart.equals("")) {
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      viewStart.setValue(cal.getTime());
    } else {
      viewStart.setValue(tmpViewStart);
      if (!viewStart.validate(new ArrayList<String>())) {
        ALEipUtils.removeTemp(rundata, context, "view_start");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        viewStart.setValue(cal.getTime());
      }
    }
    // MonthlyCalendarに表示する月を登録
    this.setMonthlyCalendarViewMonth(viewStart.getYear(), viewStart.getMonth());

    Calendar cal2 = Calendar.getInstance();
    cal2.setTime(viewStart.getValue());
    cal2.add(Calendar.DATE, 1);
    nextDate.setValue(cal2.getTime());
    cal2.add(Calendar.DATE, 6);
    nextWeek.setValue(cal2.getTime());
    cal2.add(Calendar.DATE, -8);
    prevDate.setValue(cal2.getTime());
    cal2.add(Calendar.DATE, -6);
    prevWeek.setValue(cal2.getTime());
    cal2.add(Calendar.DATE, 7);
    // このときの日付を捕捉
    tmpCal = Calendar.getInstance();
    tmpCal.setTime(cal2.getTime());
    // 週間スケジュールコンテナの初期化
    try {
      weekCon = new AjaxScheduleWeekContainer();
      weekCon.initField();
      weekCon.setViewStartDate(cal2);
    } catch (Exception e) {
      logger.error("schedule", e);
    }
    // 表示終了日時
    viewEndCrt.setValue(cal2.getTime());
    cal2.add(Calendar.DATE, -1);
    viewEnd.setValue(cal2.getTime());

    Calendar cal3 = Calendar.getInstance();
    cal3.setTime(viewStart.getValue());
    cal3.add(Calendar.MONTH, -1);
    prevMonth.setValue(cal3.getTime());
    cal3.add(Calendar.MONTH, 2);
    nextMonth.setValue(cal3.getTime());

    ALEipUtils.setTemp(rundata, context, "tmpStart", viewStart.toString()
      + "-00-00");
    ALEipUtils.setTemp(rundata, context, "tmpEnd", viewStart.toString()
      + "-00-00");

    weekTodoConList = new ArrayList<ScheduleToDoWeekContainer>();
    weekTermConList = new ArrayList<AjaxTermScheduleWeekContainer>();

    if (action != null) {
      // ToDo 表示設定
      viewTodo =
        !ALPortalApplicationService.isActive(ToDoUtils.TODO_PORTLET_NAME)
          ? 0
          : Integer.parseInt(ALEipUtils
            .getPortlet(rundata, context)
            .getPortletConfig()
            .getInitParameter("p5a-view"));
    }

    userid = ALEipUtils.getUserId(rundata);

    String tmpstr = rundata.getParameters().getString("s_all");
    show_all = "t".equals(tmpstr);
    doneTermList = new ArrayList<Integer>();

    acl_feat = ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_SELF;
    has_acl_other = ScheduleUtils.hasAuthOther(rundata);

    boolean ex_user = initMemberList(rundata);
    boolean ex_facility = initFacilityList(rundata);

    if (!(ex_user || ex_facility)) {
      memberList = new ArrayList<Integer>();
      memberList.add(uid);
    }

    // スーパークラスのメソッドを呼び出す。
    super.init(action, rundata, context);

  }

  private boolean initMemberList(RunData rundata) {
    memberList = null;
    String str[] = rundata.getParameters().getStrings("m_id");
    String s_item;

    List<Integer> u_list = new ArrayList<Integer>();
    int len = 0;
    if (str == null || str.length == 0) {
      return false;
    }
    len = str.length;
    for (int i = 0; i < len; i++) {
      s_item = str[i];
      if (!s_item.startsWith("f")) {
        u_list.add(Integer.parseInt(s_item));
      }
    }

    if (u_list.size() == 0) {
      return false;
    }

    List<ALEipUser> temp_list = new ArrayList<ALEipUser>();
    memberList = new ArrayList<Integer>();

    SelectQuery<TurbineUser> member_query = Database.query(TurbineUser.class);
    Expression exp =
      ExpressionFactory.inDbExp(TurbineUser.USER_ID_PK_COLUMN, u_list);
    member_query.setQualifier(exp);
    member_query.toString();
    temp_list.addAll(ALEipUtils.getUsersFromSelectQuery(member_query));
    for (ALEipUser eipuser : temp_list) {
      if (!("T".equals(has_acl_other))) {
        if (uid != eipuser.getUserId().getValue()) {
          /**
           * 自分以外のメンバーがいる場合は、他人のスケジュールを見る権限があるかをチェックする
           */
          acl_feat = ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_OTHER;
        }
      }
      memberList.add((int) eipuser.getUserId().getValue());
    }

    if (memberList.size() == 0 || memberList == null) {
      return false;
    }

    return true;
  }

  private boolean initFacilityList(RunData rundata) {
    facilityList = null;
    String str[] = rundata.getParameters().getStrings("m_id");
    String s_item;

    List<Integer> f_list = new ArrayList<Integer>();

    int len = 0;
    if (str == null || str.length == 0) {
      return false;
    }
    len = str.length;

    for (int i = 0; i < len; i++) {
      s_item = str[i];
      if (s_item.startsWith("f")) {
        f_list.add(Integer.parseInt(s_item.substring(1)));
      }
    }

    if (f_list.size() == 0) {
      return false;
    }
    List<FacilityResultData> temp_list = new ArrayList<FacilityResultData>();
    facilityList = new ArrayList<Integer>();

    SelectQuery<EipMFacility> facility_query =
      Database.query(EipMFacility.class);
    Expression exp =
      ExpressionFactory.inDbExp(EipMFacility.FACILITY_ID_PK_COLUMN, f_list);
    facility_query.setQualifier(exp);
    temp_list.addAll(FacilitiesUtils
      .getFacilitiesFromSelectQuery(facility_query));
    for (FacilityResultData facility : temp_list) {
      facilityList.add((int) facility.getFacilityId().getValue());
    }

    if (facilityList.size() == 0 || facilityList == null) {
      return false;
    } else {
      /**
       * 設備が入っている場合は、他人のスケジュールを見る権限があるかをチェックする
       */
      // acl_feat = ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_OTHER;
    }

    return true;
  }

  /**
   *
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected ResultList<VEipTScheduleList> selectList(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    try {

      if (rundata.getParameters().containsKey("pickup")) {
        savePsmlParameters(rundata, context);
      }

      List<VEipTScheduleList> list =
        ScheduleUtils.getScheduleList(userid, viewStart.getValue(), viewEndCrt
          .getValue(), memberList, facilityList);

      if (viewTodo == 1) {
        // ToDo の読み込み
        loadTodo(rundata, context);
      }

      if (show_all) {
        return new ResultList<VEipTScheduleList>(ScheduleUtils
          .sortByDummySchedule(list));
      }

      return new ResultList<VEipTScheduleList>(sortLoginUserSchedule(list));
      // return ScheduleUtils.sortByDummySchedule(list);
    } catch (Exception e) {
      logger.error("[AjaxScheduleWeeklyGroupSelectData] TorqueException", e);
      throw new ALDBErrorException();

    }
  }

  /**
   * ログインユーザーのスケジュールが上にくるようにソートする．
   *
   * @param list
   * @return
   */
  private List<VEipTScheduleList> sortLoginUserSchedule(
      List<VEipTScheduleList> list) {
    // 重複スケジュールの表示調節のために，
    // ダミースケジュールをリストの始めに寄せる．

    List<VEipTScheduleList> dummyList = new ArrayList<VEipTScheduleList>();
    List<VEipTScheduleList> normalList = new ArrayList<VEipTScheduleList>();
    List<VEipTScheduleList> loginUserList = new ArrayList<VEipTScheduleList>();
    List<VEipTScheduleList> ownerList = new ArrayList<VEipTScheduleList>();
    VEipTScheduleList map = null;
    int size = list.size();
    for (int i = 0; i < size; i++) {
      map = list.get(i);
      if ("D".equals(map.getStatus())) {
        dummyList.add(map);
      } else if (userid == map.getUserId().intValue()) {
        loginUserList.add(map);
      } else if (map.getOwnerId().intValue() == map.getUserId().intValue()) {
        ownerList.add(map);
      } else {
        normalList.add(map);
      }
    }

    list.clear();
    list.addAll(dummyList);
    list.addAll(loginUserList);
    list.addAll(ownerList);
    list.addAll(normalList);
    return list;
  }

  // psmlにユーザーを保存
  private boolean savePsmlParameters(RunData rundata, Context context) {
    try {
      String portletEntryId =
        rundata.getParameters().getString("js_peid", null);
      if (portletEntryId == null || "".equals(portletEntryId)) {
        return false;
      }

      String KEY_UIDS = "p6a-uids";
      String KEY_SCHK = "p7d-schk";

      StringBuffer uids = new StringBuffer();
      String str[] = rundata.getParameters().getStrings("m_id");

      // 誰も選択されなかった場合はログインユーザーをかえす
      if (str == null || str.length == 0) {
        str = new String[] { Integer.toString(ALEipUtils.getUserId(rundata)) };
      }

      int len = str.length - 1;
      for (int i = 0; i < len; i++) {
        uids.append(str[i]).append(",");
      }
      uids.append(str[len]);

      String schk = rundata.getParameters().getString("s_all");
      if (!("t".equals(schk))) {
        schk = "f";
      }

      Profile profile = ((JetspeedRunData) rundata).getProfile();
      Portlets portlets = profile.getDocument().getPortlets();
      if (portlets == null) {
        return false;
      }

      Portlets[] portletList = portlets.getPortletsArray();
      if (portletList == null) {
        return false;
      }

      PsmlParameter param = null;
      int length = portletList.length;
      for (int i = 0; i < length; i++) {
        Entry[] entries = portletList[i].getEntriesArray();
        if (entries == null || entries.length <= 0) {
          continue;
        }

        int ent_length = entries.length;
        for (int j = 0; j < ent_length; j++) {
          if (entries[j].getId().equals(portletEntryId)) {
            boolean hasParam = false;
            boolean hasParam2 = false;
            Parameter params[] = entries[j].getParameter();
            int param_len = params.length;
            for (int k = 0; k < param_len; k++) {
              if (params[k].getName().equals(KEY_UIDS)) {
                params[k].setValue(uids.toString());
                entries[j].setParameter(k, params[k]);
                hasParam = true;
              } else if (params[k].getName().equals(KEY_SCHK)) {
                params[k].setValue(schk);
                entries[j].setParameter(k, params[k]);
                hasParam2 = true;
              }
            }

            if (!hasParam) {
              param = new PsmlParameter();
              param.setName(KEY_UIDS);
              param.setValue(uids.toString());
              entries[j].addParameter(param);
            }

            if (!hasParam2) {
              param = new PsmlParameter();
              param.setName(KEY_SCHK);
              param.setValue(schk);
              entries[j].addParameter(param);
            }

            break;
          }
        }
      }

      profile.store();

    } catch (Exception ex) {
      logger.error("schedule", ex);
      return false;
    }
    return true;
  }

  /**
   *
   * @param record
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultData(VEipTScheduleList record)
      throws ALPageNotFoundException, ALDBErrorException {
    AjaxScheduleResultData rd = new AjaxScheduleResultData();
    rd.initField();
    try {
      // スケジュールが棄却されている場合は表示しない
      if ("R".equals(record.getStatus())) {
        return rd;
      }

      boolean is_member = record.isMember();
      // アクセス権限
      ALAccessControlFactoryService aclservice =
        (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
          .getInstance())
          .getService(ALAccessControlFactoryService.SERVICE_NAME);
      ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

      boolean hasAclviewOther =
        aclhandler.hasAuthority(
          userid,
          ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_OTHER,
          ALAccessControlConstants.VALUE_ACL_LIST);

      if (!hasAclviewOther && !is_member) {// 閲覧権限がなく、グループでもない
        return rd;
      }

      // ID
      rd.setScheduleId(record.getScheduleId().intValue());
      // 親スケジュール ID
      rd.setParentId(record.getParentId().intValue());
      // オーナーID
      rd.setUserId(record.getUserId());
      // 名前
      rd.setName(record.getName());
      // 場所
      rd.setPlace(record.getPlace());
      // 開始日時
      rd.setStartDate(record.getStartDate());
      // 終了日時
      rd.setEndDate(record.getEndDate());
      // 仮スケジュールかどうか
      rd.setTmpreserve("T".equals(record.getStatus()));
      // 公開するかどうか
      rd.setPublic("O".equals(record.getPublicFlag()));
      // 非表示にするかどうか
      rd.setHidden("P".equals(record.getPublicFlag()));
      // ダミーか
      rd.setDummy("D".equals(record.getStatus()));
      // ログインユーザかどうか
      rd.setLoginuser(record.getUserId().intValue() == userid);
      // オーナーかどうか
      rd.setOwner(record.getOwnerId().intValue() == userid);
      // 設備かどうか
      rd.setType(record.getType());
      // 共有メンバーかどうか
      rd.setMember(record.isMember());
      // 繰り返しパターン
      rd.setPattern(record.getRepeatPattern());
      // 共有メンバーによる編集／削除フラグ
      rd.setEditFlag("T".equals(record.getEditFlag()));

      rd.setUserCount(record.getUserCount());
      rd.setFacilityCount(record.getFacilityCount());

      // 期間スケジュールの場合
      if (rd.getPattern().equals("S")) {
        int stime;
        if (ScheduleUtils.equalsToDate(ScheduleUtils.getEmptyDate(), rd
          .getStartDate()
          .getValue(), false)) {
          stime = 0;
        } else {
          stime =
            -(int) ((viewStart.getValue().getTime() - rd
              .getStartDate()
              .getValue()
              .getTime()) / 86400000);
        }
        int etime =
          -(int) ((viewStart.getValue().getTime() - rd
            .getEndDate()
            .getValue()
            .getTime()) / 86400000);
        if (stime < 0) {
          stime = 0;
        }
        int count = stime;
        int col = etime - stime + 1;
        // 行をはみ出す場合
        if (count + col > 7) {
          col = 7 - count;
        }

        // rowspan を設定
        rd.setRowspan(col);
        if (col > 0) {
          // 期間スケジュール を格納
          int schedule_id = (int) rd.getScheduleId().getValue();
          if (!(doneTermList.contains(schedule_id))) {
            ScheduleUtils.addTerm(
              weekTermConList,
              viewStart.getValue(),
              count,
              rd);
            if (!show_all && !rd.isDummy()) {
              doneTermList.add(schedule_id);
            }
          }
        }
        return rd;
      }

      weekCon.addResultData(rd, show_all);

    } catch (Exception e) {
      logger.error("schedule", e);

      return null;
    }
    return rd;
  }

  /**
   *
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected VEipTScheduleList selectDetail(RunData rundata, Context context) {
    return null;
  }

  /**
   *
   * @param record
   * @return
   */
  @Override
  protected Object getResultDataDetail(VEipTScheduleList record) {
    return null;
  }

  /*
   *
   */
  @Override
  protected Attributes getColumnMap() {
    return null;
  }

  public void loadTodo(RunData rundata, Context context) {
    try {
      SelectQuery<EipTTodo> query = getSelectQueryForTodo(rundata, context);
      List<EipTTodo> todos = query.fetchList();

      int todossize = todos.size();
      for (int i = 0; i < todossize; i++) {
        EipTTodo record = todos.get(i);
        ScheduleToDoResultData rd = new ScheduleToDoResultData();
        rd.initField();

        // ポートレット ToDoPublic のへのリンクを取得する．
        String todo_url =
          ScheduleUtils.getPortletURItoTodoDetailPane(rundata, "ToDo", record
            .getTodoId()
            .longValue(), portletId);
        rd.setTodoId(record.getTodoId().intValue());
        rd.setTodoName(record.getTodoName());
        rd.setUserId(record.getTurbineUser().getUserId().intValue());
        rd.setStartDate(record.getStartDate());
        rd.setEndDate(record.getEndDate());
        rd.setTodoUrl(todo_url);
        // 公開/非公開を設定する．
        rd.setPublicFlag("T".equals(record.getPublicFlag()));

        int stime;
        if (ScheduleUtils.equalsToDate(ToDoUtils.getEmptyDate(), rd
          .getStartDate()
          .getValue(), false)) {
          stime = 0;
        } else {
          stime =
            -(int) ((viewStart.getValue().getTime() - rd
              .getStartDate()
              .getValue()
              .getTime()) / 86400000);
        }
        int etime =
          -(int) ((viewStart.getValue().getTime() - rd
            .getEndDate()
            .getValue()
            .getTime()) / 86400000);
        if (stime < 0) {
          stime = 0;
        }
        int count = stime;
        int col = etime - stime + 1;
        // 行をはみ出す場合
        if (count + col > 7) {
          col = 7 - count;
        }

        // rowspan を設定
        rd.setRowspan(col);
        if (col > 0) {
          // ToDo を格納
          ScheduleUtils.addToDo(
            weekTodoConList,
            viewStart.getValue(),
            count,
            rd);
        }
      }
    } catch (Exception ex) {
      logger.error("schedule", ex);
      return;
    }
  }

  private SelectQuery<EipTTodo> getSelectQueryForTodo(RunData rundata,
      Context context) {

    SelectQuery<EipTTodo> query = Database.query(EipTTodo.class);

    Expression exp1 =
      ExpressionFactory.noMatchExp(EipTTodo.STATE_PROPERTY, Short
        .valueOf((short) 100));
    query.setQualifier(exp1);
    Expression exp2 =
      ExpressionFactory.matchExp(EipTTodo.ADDON_SCHEDULE_FLG_PROPERTY, "T");
    query.andQualifier(exp2);
    Expression exp3 =
      ExpressionFactory.matchDbExp(TurbineUser.USER_ID_PK_COLUMN, uid);
    query.andQualifier(exp3);

    // 終了日時
    Expression exp11 =
      ExpressionFactory.greaterOrEqualExp(EipTTodo.END_DATE_PROPERTY, viewStart
        .getValue());
    // 開始日時
    Expression exp12 =
      ExpressionFactory.lessOrEqualExp(EipTTodo.START_DATE_PROPERTY, viewEndCrt
        .getValue());

    // 開始日時のみ指定されている ToDo を検索
    Expression exp21 =
      ExpressionFactory.lessOrEqualExp(EipTTodo.START_DATE_PROPERTY, viewEndCrt
        .getValue());
    Expression exp22 =
      ExpressionFactory.matchExp(EipTTodo.END_DATE_PROPERTY, ToDoUtils
        .getEmptyDate());

    // 終了日時のみ指定されている ToDo を検索
    Expression exp31 =
      ExpressionFactory.greaterOrEqualExp(EipTTodo.END_DATE_PROPERTY, viewStart
        .getValue());
    Expression exp32 =
      ExpressionFactory.matchExp(EipTTodo.START_DATE_PROPERTY, ToDoUtils
        .getEmptyDate());

    query.andQualifier((exp11.andExp(exp12)).orExp(exp21.andExp(exp22)).orExp(
      exp31.andExp(exp32)));
    return query;
  }

  /**
   * 表示開始日時を取得します。
   *
   * @return
   */
  public ALDateTimeField getViewStart() {
    return viewStart;
  }

  /**
   * 表示終了日時を取得します。
   *
   * @return
   */
  public ALDateTimeField getViewEnd() {
    return viewEnd;
  }

  /**
   * 表示タイプを取得します。
   *
   * @return
   */
  public String getViewtype() {
    return viewtype;
  }

  /**
   * 表示終了日時 (Criteria) を取得します。
   *
   * @return
   */
  public ALDateTimeField getViewEndCrt() {
    return viewEndCrt;
  }

  /**
   * 前の日を取得します。
   *
   * @return
   */
  public ALDateTimeField getPrevDate() {
    return prevDate;
  }

  /**
   * 前の週を取得します。
   *
   * @return
   */
  public ALDateTimeField getPrevWeek() {
    return prevWeek;
  }

  /**
   * 次の日を取得します。
   *
   * @return
   */
  public ALDateTimeField getNextDate() {
    return nextDate;
  }

  /**
   * 次の週を取得します。
   *
   * @return
   */
  public ALDateTimeField getNextWeek() {
    return nextWeek;
  }

  /**
   * 今日を取得します。
   *
   * @return
   */
  public ALDateTimeField getToday() {
    return today;
  }

  /**
   * 先月を取得する．
   *
   * @return
   */
  public ALDateTimeField getPrevMonth() {
    return prevMonth;
  }

  /**
   * 来月を取得する．
   *
   * @return
   */
  public ALDateTimeField getNextMonth() {
    return nextMonth;
  }

  /**
   * 週間スケジュールコンテナを取得します。
   *
   * @return
   */
  public AjaxScheduleWeekContainer getContainer() {
    return weekCon;
  }

  public List<AjaxTermScheduleWeekContainer> getWeekTermContainerList() {
    return weekTermConList;
  }

  public List<ScheduleToDoWeekContainer> getWeekToDoContainerList() {
    return weekTodoConList;
  }

  public void setPortletId(String id) {
    portletId = id;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   *
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return acl_feat;
  }

}

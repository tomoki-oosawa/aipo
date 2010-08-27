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
package com.aimluck.eip.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Parameter;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.psml.PsmlParameter;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.cayenne.om.portlet.EipTTodo;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractSelectData;
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
import com.aimluck.eip.todo.util.ToDoUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * カレンダー用週間スケジュールの検索結果を管理するクラスです。
 * 
 */
public class AjaxScheduleWeeklyGroupSelectData extends
    ALAbstractSelectData<EipTScheduleMap, EipTScheduleMap> {

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
  private List<Number> memberList;

  /** <code>facilityList</code> メンバーリスト */
  private List<Long> facilityList;

  /** <code>memberList</code> 共有メンバーリスト */
  private List<String> recordMemberList;

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

  // protected String org_id;
  //
  // protected ORMappingEipTTodo orm_todo;

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
        throw new ALPageNotFoundException();
      }
    }
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
      logger.error("Exception", e);
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
        Integer.parseInt(ALEipUtils
          .getPortlet(rundata, context)
          .getPortletConfig()
          .getInitParameter("p5a-view"));
    }

    userid = ALEipUtils.getUserId(rundata);

    // org_id = OrgORMappingMap.getInstance().getOrgId(rundata);
    // orm_todo = TodoOrmUtils.getORMappingEipTTodo(rundata);

    String tmpstr = rundata.getParameters().getString("s_all");
    show_all = "t".equals(tmpstr);
    doneTermList = new ArrayList<Integer>();

    acl_feat = ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_SELF;
    has_acl_other = ScheduleUtils.hasAuthOther(rundata);

    boolean ex_user = initMemberList(rundata);
    boolean ex_facility = initFacilityList(rundata);

    if (!(ex_user || ex_facility)) {
      memberList = new ArrayList<Number>();
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
    memberList = new ArrayList<Number>();

    SelectQuery<TurbineUser> member_query = Database.query(TurbineUser.class);
    Expression exp =
      ExpressionFactory.inDbExp(TurbineUser.USER_ID_PK_COLUMN, u_list);
    member_query.setQualifier(exp);
    member_query.toString();
    temp_list.addAll(ALEipUtils.getUsersFromSelectQuery(member_query));
    int tmpsize = temp_list.size();
    for (int i = 0; i < tmpsize; i++) {
      ALEipUser eipuser = temp_list.get(i);
      if (!("T".equals(has_acl_other))) {
        if (uid != eipuser.getUserId().getValue()) {
          /**
           * 自分以外のメンバーがいる場合は、他人のスケジュールを見る権限があるかをチェックする
           */
          acl_feat = ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_OTHER;
        }
      }
      memberList.add(eipuser.getUserId().getValue());
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
    facilityList = new ArrayList<Long>();

    SelectQuery<EipMFacility> facility_query =
      Database.query(EipMFacility.class);
    Expression exp =
      ExpressionFactory.inDbExp(EipMFacility.FACILITY_ID_PK_COLUMN, f_list);
    facility_query.setQualifier(exp);
    temp_list.addAll(FacilitiesUtils
      .getFacilitiesFromSelectQuery(facility_query));
    int tmpsize = temp_list.size();
    for (int i = 0; i < tmpsize; i++) {
      FacilityResultData facility = temp_list.get(i);
      facilityList.add(facility.getFacilityId().getValue());
    }

    if (facilityList.size() == 0 || facilityList == null) {
      return false;
    } else {
      /**
       * 施設が入っている場合は、他人のスケジュールを見る権限があるかをチェックする
       */
      acl_feat = ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_OTHER;
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
  protected ResultList<EipTScheduleMap> selectList(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    try {

      savePsmlParameters(rundata, context);

      List<EipTScheduleMap> list = new ArrayList<EipTScheduleMap>();
      SelectQuery<EipTScheduleMap> uquery = getSelectQuery(rundata, context);
      if (uquery != null) {
        list.addAll(uquery.fetchList());
      }

      SelectQuery<EipTScheduleMap> fquery =
        getSelectQueryForFacility(rundata, context);
      if (fquery != null) {
        list.addAll(fquery.fetchList());
      }

      if (viewTodo == 1) {
        // ToDo の読み込み
        loadTodo(rundata, context);
      }

      if (show_all) {
        return new ResultList<EipTScheduleMap>(ScheduleUtils
          .sortByDummySchedule(list));
      }

      return new ResultList<EipTScheduleMap>(sortLoginUserSchedule(list));
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
  private List<EipTScheduleMap> sortLoginUserSchedule(List<EipTScheduleMap> list) {
    // 重複スケジュールの表示調節のために，
    // ダミースケジュールをリストの始めに寄せる．

    List<EipTScheduleMap> dummyList = new ArrayList<EipTScheduleMap>();
    List<EipTScheduleMap> normalList = new ArrayList<EipTScheduleMap>();
    List<EipTScheduleMap> loginUserList = new ArrayList<EipTScheduleMap>();
    List<EipTScheduleMap> ownerList = new ArrayList<EipTScheduleMap>();
    EipTScheduleMap map = null;
    int size = list.size();
    for (int i = 0; i < size; i++) {
      map = list.get(i);
      if ("D".equals(map.getStatus())) {
        dummyList.add(map);
      } else if (userid == map.getUserId().intValue()) {
        loginUserList.add(map);
      } else if (map.getEipTSchedule().getOwnerId().intValue() == map
        .getUserId()
        .intValue()) {
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

      // 誰も選択されなかった場合、またはアクセス権がない場合はログインユーザーをかえす
      if (str == null || str.length == 0 || !("T".equals(has_acl_other))) {
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
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 検索条件を設定した SelectQuery を返します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  protected SelectQuery<EipTScheduleMap> getSelectQuery(RunData rundata,
      Context context) {
    if (memberList == null) {
      return null;
    }
    int membersize = memberList.size();

    if (membersize < 1) {
      return null;
    }

    Expression exp20 =
      ExpressionFactory.matchExp(
        EipTScheduleMap.TYPE_PROPERTY,
        ScheduleUtils.SCHEDULEMAP_TYPE_USER);

    SelectQuery<EipTScheduleMap> query = Database.query(EipTScheduleMap.class);

    query.setQualifier(exp20);

    Expression exp21 =
      ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, memberList
        .get(0));
    for (int i = 1; i < membersize; i++) {
      Expression exp1 =
        ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, memberList
          .get(i));
      exp21 = exp21.orExp(exp1);
    }

    query.andQualifier(exp21);

    // 終了日時
    Expression exp11 =
      ExpressionFactory.greaterOrEqualExp(
        EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
          + "."
          + EipTSchedule.END_DATE_PROPERTY,
        viewStart.getValue());
    // 開始日時
    Expression exp12 =
      ExpressionFactory.lessOrEqualExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.START_DATE_PROPERTY, viewEndCrt.getValue());
    // 通常スケジュール
    Expression exp13 =
      ExpressionFactory.noMatchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, "N");
    // 期間スケジュール
    Expression exp14 =
      ExpressionFactory.noMatchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, "S");
    query.andQualifier((exp11.andExp(exp12)).orExp(exp13.andExp(exp14)));
    // 開始日時でソート
    query.orderAscending(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
      + "."
      + EipTSchedule.START_DATE_PROPERTY);

    query.distinct(true);
    return query;
  }

  /**
   * 検索条件を設定した SelectQuery を返します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  protected SelectQuery<EipTScheduleMap> getSelectQueryForFacility(
      RunData rundata, Context context) {
    if (facilityList == null) {
      return null;
    }
    int facilitysize = facilityList.size();

    if (facilitysize < 1) {
      return null;
    }

    Expression exp20 =
      ExpressionFactory.matchExp(
        EipTScheduleMap.TYPE_PROPERTY,
        ScheduleUtils.SCHEDULEMAP_TYPE_FACILITY);

    SelectQuery<EipTScheduleMap> query = Database.query(EipTScheduleMap.class);

    query.setQualifier(exp20);

    Expression exp21 =
      ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, facilityList
        .get(0));
    for (int i = 0; i < facilitysize; i++) {
      Expression exp1 =
        ExpressionFactory.matchExp(
          EipTScheduleMap.USER_ID_PROPERTY,
          facilityList.get(i));
      exp21 = exp21.orExp(exp1);
    }

    query.andQualifier(exp21);

    // 終了日時
    Expression exp11 =
      ExpressionFactory.greaterOrEqualExp(
        EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
          + "."
          + EipTSchedule.END_DATE_PROPERTY,
        viewStart.getValue());
    // 開始日時
    Expression exp12 =
      ExpressionFactory.lessOrEqualExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.START_DATE_PROPERTY, viewEndCrt.getValue());
    // 通常スケジュール
    Expression exp13 =
      ExpressionFactory.noMatchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, "N");
    // 期間スケジュール
    Expression exp14 =
      ExpressionFactory.noMatchExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.REPEAT_PATTERN_PROPERTY, "S");
    query.andQualifier((exp11.andExp(exp12)).orExp(exp13.andExp(exp14)));
    // 開始日時でソート
    query.orderAscending(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
      + "."
      + EipTSchedule.START_DATE_PROPERTY);

    query.distinct(true);
    return query;
  }

  /**
   * 
   * @param record
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultData(EipTScheduleMap record)
      throws ALPageNotFoundException, ALDBErrorException {
    AjaxScheduleResultData rd = new AjaxScheduleResultData();
    rd.initField();
    try {
      EipTSchedule schedule = record.getEipTSchedule();
      // スケジュールが棄却されている場合は表示しない
      if ("R".equals(record.getStatus())) {
        return rd;
      }

      // is_memberのチェック
      SelectQuery<EipTScheduleMap> mapquery =
        Database.query(EipTScheduleMap.class);
      Expression mapexp1 =
        ExpressionFactory.matchExp(
          EipTScheduleMap.SCHEDULE_ID_PROPERTY,
          schedule.getScheduleId());
      mapquery.setQualifier(mapexp1);

      try {
        recordMemberList = new ArrayList<String>();
        List<EipTScheduleMap> tmpList = mapquery.fetchList();
        int tmpSize = tmpList.size();
        EipTScheduleMap tmpMap;
        for (int i = 0; i < tmpSize; i++) {
          tmpMap = tmpList.get(i);
          int m = tmpMap.getUserId().intValue();
          if (!("R".equals(tmpMap.getStatus()))) {
            if ("F".equals(tmpMap.getType())) {
              recordMemberList.add("f" + Integer.toString(m));
            } else {
              recordMemberList.add(Integer.toString(m));
            }
          }
        }
        if (recordMemberList != null && recordMemberList.size() > 0) {
          rd.setMemberList(recordMemberList);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      Expression mapexp2 =
        ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, Integer
          .valueOf(userid));
      mapquery.andQualifier(mapexp2);

      List<EipTScheduleMap> schedulemaps = mapquery.fetchList();
      boolean is_member =
        (schedulemaps != null && schedulemaps.size() > 0) ? true : false;

      // ID
      rd.setScheduleId(schedule.getScheduleId().intValue());
      // 親スケジュール ID
      rd.setParentId(schedule.getParentId().intValue());
      // オーナーID
      rd.setUserId(record.getUserId());
      // 名前
      rd.setName(schedule.getName());
      // 開始日時
      rd.setStartDate(schedule.getStartDate());
      // 終了日時
      rd.setEndDate(schedule.getEndDate());
      // 仮スケジュールかどうか
      rd.setTmpreserve("T".equals(record.getStatus()));
      // 公開するかどうか
      rd.setPublic("O".equals(schedule.getPublicFlag()));
      // 非表示にするかどうか
      rd.setHidden("P".equals(schedule.getPublicFlag()));
      // ダミーか
      rd.setDummy("D".equals(record.getStatus()));
      // ログインユーザかどうか
      rd.setLoginuser(record.getUserId().intValue() == userid);
      // オーナーかどうか
      rd.setOwner(schedule.getOwnerId().intValue() == userid);
      // 施設かどうか
      rd.setType(record.getType());
      // 共有メンバーかどうか
      rd.setMember(is_member);
      // 繰り返しパターン
      rd.setPattern(schedule.getRepeatPattern());
      // 共有メンバーによる編集／削除フラグ
      rd.setEditFlag("T".equals(schedule.getEditFlag()));

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
      logger.error("Exception", e);

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
  protected EipTScheduleMap selectDetail(RunData rundata, Context context) {
    return null;
  }

  /**
   * 
   * @param record
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTScheduleMap record) {
    return null;
  }

  /*
   *
   */
  @Override
  protected Attributes getColumnMap() {
    // このメソッドは利用されません。
    return null;
  }

  @SuppressWarnings("unused")
  private Portlet getPortletURI(RunData rundata, String portletEntryId) {
    try {
      Portlets portlets =
        ((JetspeedRunData) rundata).getProfile().getDocument().getPortlets();
      if (portlets == null) {
        return null;
      }

      Portlets[] portletList = portlets.getPortletsArray();
      if (portletList == null) {
        return null;
      }

      int length = portletList.length;
      for (int i = 0; i < length; i++) {
        Entry[] entries = portletList[i].getEntriesArray();
        if (entries == null || entries.length <= 0) {
          continue;
        }

        int ent_length = entries.length;
        for (int j = 0; j < ent_length; j++) {
          if (entries[j].getId().equals(portletEntryId)) {
            Iterator<?> iter = entries[j].getParameterIterator();
            while (iter.hasNext()) {

            }
          }
        }
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
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
      logger.error("Exception", ex);
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

  // private Criteria getCriteriaForToDo(RunData rundata, Context context) {
  // Integer uid = new Integer(ALEipUtils.getUserId(rundata));
  // Criteria crt = new Criteria();
  // crt.add(EipTTodoConstants.USER_ID, uid, Criteria.EQUAL);
  // crt.add(EipTTodoConstants.STATE, 100, Criteria.NOT_EQUAL);
  // crt.add(EipTTodoConstants.ADDON_SCHEDULE_FLG, "T");
  //
  // // 終了日時
  // Criteria.Criterion c11 = crt.getNewCriterion(EipTTodoConstants.END_DATE,
  // viewStart.getValue(), Criteria.GREATER_EQUAL);
  // // 開始日時
  // Criteria.Criterion c12 = crt.getNewCriterion(EipTTodoConstants.START_DATE,
  // viewEndCrt.getValue(), Criteria.LESS_EQUAL);
  //
  // // 開始日時のみ指定されている JOB を検索
  // Criteria.Criterion c21 = crt.getNewCriterion(EipTTodoConstants.START_DATE,
  // viewEndCrt.getValue(), Criteria.LESS_EQUAL);
  // Criteria.Criterion c22 = crt.getNewCriterion(EipTTodoConstants.END_DATE,
  // ToDoUtils.getEmptyDate(), Criteria.EQUAL);
  //
  // // 終了日時のみ指定されている JOB を検索
  // Criteria.Criterion c31 = crt.getNewCriterion(EipTTodoConstants.END_DATE,
  // viewStart.getValue(), Criteria.GREATER_EQUAL);
  // Criteria.Criterion c32 = crt.getNewCriterion(EipTTodoConstants.START_DATE,
  // ToDoUtils.getEmptyDate(), Criteria.EQUAL);
  //
  // crt.and((c11.and(c12)).or(c21.and(c22)).or(c31.and(c32)));
  // return crt;
  // }

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

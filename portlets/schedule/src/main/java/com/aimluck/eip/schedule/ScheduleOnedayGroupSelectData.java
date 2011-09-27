/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.cayenne.om.portlet.EipTTodo;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.facilities.FacilityResultData;
import com.aimluck.eip.facilities.util.FacilitiesUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.todo.util.ToDoUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * １日スケジュール（グループ）の検索結果を管理するクラスです。
 * 
 */
public class ScheduleOnedayGroupSelectData extends ScheduleOnedaySelectData {

  /** <code>logger</code> logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleOnedayGroupSelectData.class.getName());

  /** <code>termmap</code> 期間スケジュールマップ */
  private Map<Integer, List<ScheduleOnedayResultData>> termmap;

  /** <code>map</code> スケジュールMap */
  private Map<Integer, ScheduleOnedayContainer> map;

  /** <code>members</code> 共有メンバー */
  private List<ALEipUser> members;

  /** <code>groups</code> グループリスト */
  private List<ALEipGroup> groups;

  /** <code>userid</code> ユーザーID */
  private int userid;

  /** <code>rows</code> rows */
  private int rows[];

  /** <code>max</code> max */
  private int max;

  /** <code>is_hasspan</code> 期間スケジュールがあるかどうか */
  private boolean is_hasspan;

  /** <code>myGroupURI</code> ポートレット MyGroup へのリンク */
  private String myGroupURI;

  /** <code>todomap</code> ToDo マップ */
  private Map<Integer, List<ScheduleToDoResultData>> todomap;

  /** ポートレット ID */
  private String portletId;

  /** <code>map</code> スケジュールMap（施設） */
  private Map<Integer, ScheduleOnedayContainer> facilitymap;

  private List<FacilityResultData> facilityList;

  /** <code>hasAuthoritySelfInsert</code> アクセス権限 */
  private boolean hasAuthoritySelfInsert = false;

  /** <code>hasAuthorityFacilityInsert</code> アクセス権限 */
  private boolean hasAuthorityFacilityInsert = false;

  private boolean hasAclviewOther = false;

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
    super.init(action, rundata, context);
    viewtype = "oneday-group";
    try {
      termmap = new LinkedHashMap<Integer, List<ScheduleOnedayResultData>>();
      map = new LinkedHashMap<Integer, ScheduleOnedayContainer>();
      todomap = new LinkedHashMap<Integer, List<ScheduleToDoResultData>>();
      facilitymap = new LinkedHashMap<Integer, ScheduleOnedayContainer>();

      groups = ALEipUtils.getMyGroups(rundata);
      userid = ALEipUtils.getUserId(rundata);
      rows = new int[(endHour - startHour) * 4 + 1];
      int size = rows.length;
      for (int i = 0; i < size; i++) {
        rows[i] = 1;
      }
      String filter = ALEipUtils.getTemp(rundata, context, LIST_FILTER_STR);
      if (filter == null || filter.equals("")) {
        VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
        String groupName =
          portlet.getPortletConfig().getInitParameter("p3a-group");
        if (groupName != null) {
          ALEipUtils.setTemp(rundata, context, LIST_FILTER_STR, groupName);
          ALEipUtils.setTemp(rundata, context, LIST_FILTER_TYPE_STR, "group");
        }
      }

      // ポートレット MyGroup のへのリンクを取得する．
      myGroupURI =
        ScheduleUtils.getPortletURIinPersonalConfigPane(rundata, "MyGroup");

      // アクセス権限
      ALAccessControlFactoryService aclservice =
        (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
          .getInstance())
          .getService(ALAccessControlFactoryService.SERVICE_NAME);
      ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

      hasAclviewOther =
        aclhandler.hasAuthority(
          userid,
          ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_OTHER,
          ALAccessControlConstants.VALUE_ACL_LIST);

      hasAuthoritySelfInsert =
        aclhandler.hasAuthority(
          userid,
          ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_SELF,
          ALAccessControlConstants.VALUE_ACL_INSERT);

      hasAuthorityFacilityInsert =
        aclhandler.hasAuthority(
          userid,
          ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_FACILITY,
          ALAccessControlConstants.VALUE_ACL_INSERT);

      // orm_map = ScheduleOrmUtils.getORMappingEipTScheduleMap(org_id);
    } catch (Exception ex) {
      logger.error("Exception", ex);
    }
  }

  /**
   * 検索条件を設定した SelectQuery を返します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected SelectQuery<EipTScheduleMap> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipTScheduleMap> query = Database.query(EipTScheduleMap.class);

    // 終了日時
    Expression exp11 =
      ExpressionFactory.greaterOrEqualExp(
        EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
          + "."
          + EipTSchedule.END_DATE_PROPERTY,
        getViewDate().getValue());

    // 日付を1日ずつずらす
    Calendar cal = Calendar.getInstance();
    cal.setTime(getViewDate().getValue());
    cal.add(Calendar.DATE, 1);
    ALDateTimeField field = new ALDateTimeField();
    field.setValue(cal.getTime());
    // 開始日時
    // LESS_EQUALからLESS_THANへ修正、期間スケジュールFIXのため(Haruo Kaneko)
    Expression exp12 =
      ExpressionFactory.lessExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.START_DATE_PROPERTY, field.getValue());
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

    query.setQualifier((exp11.andExp(exp12)).orExp(exp13.andExp(exp14)));

    // 開始日時でソート
    List<Ordering> orders = new ArrayList<Ordering>();
    orders.add(new Ordering(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
      + "."
      + EipTSchedule.START_DATE_PROPERTY, true));
    orders.add(new Ordering(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
      + "."
      + EipTSchedule.END_DATE_PROPERTY, true));
    query.getQuery().addOrderings(orders);

    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * 
   * @param query
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected SelectQuery<EipTScheduleMap> buildSelectQueryForFilter(
      SelectQuery<EipTScheduleMap> query, RunData rundata, Context context) {
    String filter = ALEipUtils.getTemp(rundata, context, LIST_FILTER_STR);
    String filter_type =
      ALEipUtils.getTemp(rundata, context, LIST_FILTER_TYPE_STR);
    String crt_key = null;
    Attributes map = getColumnMap();
    if (filter == null
      || filter_type == null
      || filter.equals("")
      || tmpViewDate2 != null
      || !hasAclviewOther) {
      Expression exp1 =
        ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, Integer
          .valueOf(ALEipUtils.getUserId(rundata)));
      query.andQualifier(exp1);

      members = new ArrayList<ALEipUser>();
      members.add(ALEipUtils.getALEipUser(rundata));
      ScheduleOnedayContainer con = new ScheduleOnedayContainer();
      con.initField();
      con.initHour(startHour, endHour);
      Integer uid = Integer.valueOf(ALEipUtils.getUserId(rundata));
      this.termmap.put(uid, new ArrayList<ScheduleOnedayResultData>());
      this.map.put(uid, con);
      this.todomap.put(uid, new ArrayList<ScheduleToDoResultData>());
      return query;
    }
    crt_key = map.getValue(filter_type);
    if (crt_key == null) {
      return query;
    }

    // グループ名からユーザを取得
    List<Integer> ulist = ALEipUtils.getUserIds(filter);

    // グループにユーザが存在しない場合はダミーユーザを設定し、検索します。
    // ダミーユーザーID = -1
    int size = ulist.size();
    if (size == 0) {
      ulist.add(Integer.valueOf(-1));
    } else {
      for (int i = 0; i < size; i++) {
        Integer id = ulist.get(i);
        ScheduleOnedayContainer con = new ScheduleOnedayContainer();
        con.initField();
        con.initHour(startHour, endHour);
        this.termmap.put(id, new ArrayList<ScheduleOnedayResultData>());
        this.map.put(id, con);
        this.todomap.put(id, new ArrayList<ScheduleToDoResultData>());
      }
    }

    // List facilityIds = FacilitiesUtils.getFacilityIds(filter);
    List<Integer> facilityIds = null;

    if ("Facility".equals(filter)) {
      facilityIds = getFacilityIdAllList();
    } else {
      facilityIds = FacilitiesUtils.getFacilityIds(filter);
    }
    int f_size = facilityIds.size();
    if (f_size == 0) {
      facilityIds.add(Integer.valueOf(-1));
    } else {
      for (int i = 0; i < f_size; i++) {
        Integer id = facilityIds.get(i);
        ScheduleOnedayContainer con = new ScheduleOnedayContainer();
        con.initField();
        con.initHour(startHour, endHour);
        this.facilitymap.put(id, con);
      }
    }

    if ("Facility".equals(filter)) {
      Expression exp21 =
        ExpressionFactory.matchExp(
          EipTScheduleMap.TYPE_PROPERTY,
          ScheduleUtils.SCHEDULEMAP_TYPE_FACILITY);
      Expression exp22 = ExpressionFactory.inExp(crt_key, facilityIds);

      query.andQualifier(exp21.andExp(exp22));
    } else {
      Expression exp11 =
        ExpressionFactory.matchExp(
          EipTScheduleMap.TYPE_PROPERTY,
          ScheduleUtils.SCHEDULEMAP_TYPE_USER);
      Expression exp12 = ExpressionFactory.inExp(crt_key, ulist);

      Expression exp21 =
        ExpressionFactory.matchExp(
          EipTScheduleMap.TYPE_PROPERTY,
          ScheduleUtils.SCHEDULEMAP_TYPE_FACILITY);
      Expression exp22 = ExpressionFactory.inExp(crt_key, facilityIds);

      query.andQualifier((exp11.andExp(exp12)).orExp(exp21.andExp(exp22)));
    }

    members = ALEipUtils.getUsers(filter);

    String flag_changeturn =
      ALEipUtils.getTemp(rundata, context, ScheduleUtils.FLAG_CHANGE_TURN_STR);
    if ("0".equals(flag_changeturn)) {
      // ログインユーザの行けジュールを一番上に表示させるため，
      // メンバリストの初めの要素にログインユーザを配置する．
      ALEipUser eipUser = null;
      int memberSize = members.size();
      for (int i = 0; i < memberSize; i++) {
        eipUser = members.get(i);
        if (eipUser.getUserId().getValue() == userid) {
          members.remove(i);
          members.add(0, eipUser);
        }
      }
    }

    facilityList = FacilitiesUtils.getFacilityList(filter);

    current_filter = filter;
    current_filter_type = filter_type;
    return query;
  }

  private List<Integer> getFacilityIdAllList() {
    List<Integer> facilityIdAllList = new ArrayList<Integer>();

    try {
      SelectQuery<EipMFacility> query = Database.query(EipMFacility.class);
      query.select(EipMFacility.FACILITY_ID_PK_COLUMN);
      List<EipMFacility> aList = query.fetchList();

      int size = aList.size();
      for (int i = 0; i < size; i++) {
        EipMFacility record = aList.get(i);
        facilityIdAllList.add(record.getFacilityId());
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
    }
    return facilityIdAllList;
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
    ScheduleOnedayResultData rd = new ScheduleOnedayResultData();
    rd.initField();
    try {
      EipTSchedule schedule = record.getEipTSchedule();
      if ("R".equals(record.getStatus())) {
        return rd;
      }
      if (!ScheduleUtils.isView(
        getViewDate(),
        schedule.getRepeatPattern(),
        schedule.getStartDate(),
        schedule.getEndDate())) {
        return rd;
      }

      SelectQuery<EipTScheduleMap> mapquery =
        Database.query(EipTScheduleMap.class);
      Expression mapexp1 =
        ExpressionFactory.matchExp(
          EipTScheduleMap.SCHEDULE_ID_PROPERTY,
          schedule.getScheduleId());
      mapquery.setQualifier(mapexp1);
      Expression mapexp2 =
        ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, Integer
          .valueOf(userid));
      mapquery.andQualifier(mapexp2);

      List<EipTScheduleMap> schedulemaps = mapquery.fetchList();
      boolean is_member =
        (schedulemaps != null && schedulemaps.size() > 0) ? true : false;

      // Dummy スケジュールではない
      // 完全に隠す
      // 自ユーザー以外
      // 共有メンバーではない
      // オーナーではない
      if ((!"D".equals(record.getStatus()))
        && "P".equals(schedule.getPublicFlag())
        && (userid != record.getUserId().intValue())
        && (userid != schedule.getOwnerId().intValue())
        && !is_member) {
        return rd;
      }
      if ("C".equals(schedule.getPublicFlag())
        && (userid != record.getUserId().intValue())
        && (userid != schedule.getOwnerId().intValue())
        && !is_member) {
        rd.setName("非公開");
        // 仮スケジュールかどうか
        rd.setTmpreserve(false);
      } else {
        rd.setName(schedule.getName());
        // 仮スケジュールかどうか
        rd.setTmpreserve("T".equals(record.getStatus()));
      }
      // ID
      rd.setScheduleId(schedule.getScheduleId().intValue());
      // 親スケジュール ID
      rd.setParentId(schedule.getParentId().intValue());
      // 開始日時
      rd.setStartDate(schedule.getStartDate());
      // 終了日時
      rd.setEndDate(schedule.getEndDate());
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
      // 共有メンバーかどうか
      rd.setMember(is_member);
      // 繰り返しパターン
      rd.setPattern(schedule.getRepeatPattern());

      // ユーザもしくは設備のコンテナを取得する．
      ScheduleOnedayContainer con = null;
      if (ScheduleUtils.SCHEDULEMAP_TYPE_USER.equals(record.getType())) {
        con = map.get(record.getUserId());
      } else {
        // if (ScheduleUtils.SCHEDULEMAP_TYPE_FACILITY.equals(record.getType()))
        // の場合
        con = facilitymap.get(record.getUserId());
      }

      // 期間スケジュールの場合
      if (rd.getPattern().equals("S")) {
        is_hasspan = true;
        List<ScheduleOnedayResultData> terms = termmap.get(record.getUserId());
        if (terms != null) {
          // 期間スケジュールを格納
          terms.add(rd);
        }

        return rd;
      }

      if (!rd.getPattern().equals("N")) {
        // 繰り返しスケジュール
        if (!ScheduleUtils.isView(getViewDate(), rd.getPattern(), rd
          .getStartDate()
          .getValue(), rd.getEndDate().getValue())) {
          return rd;
        }
        rd.setRepeat(true);
      }
      con.addResultData(rd, startHour, endHour, getViewDate());
    } catch (Exception e) {
      logger.error("Exception", e);
      return null;
    }
    return rd;
  }

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * @return
   */
  @Override
  public boolean doViewList(ALAction action, RunData rundata, Context context) {
    boolean res = super.doViewList(action, rundata, context);
    // 後処理
    postDoList();
    return res;
  }

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * @return
   */
  @Override
  public boolean doSelectList(ALAction action, RunData rundata, Context context) {
    boolean res = super.doSelectList(action, rundata, context);
    // 後処理
    postDoList();
    return res;
  }

  /**
   * スケジュールの一日コンテナの各rows値の中で、最大値を取得します。
   * 
   * @param list
   */
  private int[] getMaxRowsFromContainer(Collection<ScheduleOnedayContainer> list) {

    int nowRows[] = new int[rows.length];
    for (ScheduleOnedayContainer container : list) {
      container.last(startHour, endHour, getViewDate());
      if (container.isDuplicate()) {
        is_duplicate = true;
      }

      int size = rows.length;
      int[] tmpRows = container.getRows();
      for (int i = 0; i < size; i++) {
        if (tmpRows[i] > nowRows[i]) {
          nowRows[i] = tmpRows[i];
        }
      }
    }
    return nowRows;
  }

  /**
   * 検索後の処理を行います。
   * 
   */
  private void postDoList() {
    int userRows[] = getMaxRowsFromContainer(map.values());

    int facilityRows[] = getMaxRowsFromContainer(facilitymap.values());

    int size = rows.length;
    for (int i = 0; i < size; i++) {
      rows[i] = Math.max(rows[i], Math.max(userRows[i], facilityRows[i]));
      max += rows[i];
    }
  }

  @Override
  public void loadToDo(RunData rundata, Context context) {
    try {
      SelectQuery<EipTTodo> query = getSelectQueryForTodo(rundata, context);
      List<EipTTodo> todos = query.fetchList();

      int todossize = todos.size();
      for (int i = 0; i < todossize; i++) {
        EipTTodo record = todos.get(i);
        ScheduleToDoResultData rd = new ScheduleToDoResultData();
        rd.initField();

        // ポートレット ToDo のへのリンクを取得する．
        String todo_url = "";
        if (userid == record.getTurbineUser().getUserId().intValue()) {
          todo_url =
            ScheduleUtils.getPortletURItoTodoDetailPane(rundata, "ToDo", record
              .getTodoId()
              .longValue(), portletId);
        } else {
          todo_url =
            ScheduleUtils.getPortletURItoTodoPublicDetailPane(
              rundata,
              "ToDo",
              record.getTodoId().longValue(),
              portletId);
        }
        rd.setTodoId(record.getTodoId().intValue());
        rd.setTodoName(record.getTodoName());
        rd.setUserId(record.getTurbineUser().getUserId().intValue());
        rd.setStartDate(record.getStartDate());
        rd.setEndDate(record.getEndDate());
        rd.setTodoUrl(todo_url);
        // 公開/非公開を設定する．
        rd.setPublicFlag("T".equals(record.getPublicFlag()));

        List<ScheduleToDoResultData> usertodos1 =
          todomap.get(record.getTurbineUser().getUserId());
        if (usertodos1 != null) {
          // ToDo を格納
          usertodos1.add(rd);
        }
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return;
    }
  }

  private SelectQuery<EipTTodo> getSelectQueryForTodo(RunData rundata,
      Context context) {
    Integer uid = Integer.valueOf(userid);

    SelectQuery<EipTTodo> query = Database.query(EipTTodo.class);
    Expression exp1 =
      ExpressionFactory.noMatchExp(EipTTodo.STATE_PROPERTY, Short
        .valueOf((short) 100));
    query.setQualifier(exp1);

    Expression exp01 =
      ExpressionFactory.matchDbExp(TurbineUser.USER_ID_PK_COLUMN, uid);
    Expression exp02 =
      ExpressionFactory.noMatchDbExp(TurbineUser.USER_ID_PK_COLUMN, uid);
    Expression exp03 =
      ExpressionFactory.matchExp(EipTTodo.PUBLIC_FLAG_PROPERTY, "T");
    Expression exp04 =
      ExpressionFactory.matchExp(EipTTodo.ADDON_SCHEDULE_FLG_PROPERTY, "T");
    query.andQualifier(exp01.orExp(exp02.andExp(exp03)).andExp(exp04));

    // 終了日時
    Expression exp11 =
      ExpressionFactory.greaterOrEqualExp(
        EipTTodo.END_DATE_PROPERTY,
        getViewDate().getValue());
    // 開始日時
    Expression exp12 =
      ExpressionFactory.lessOrEqualExp(
        EipTTodo.START_DATE_PROPERTY,
        getViewDate().getValue());

    // 開始日時のみ指定されている ToDo を検索
    Expression exp21 =
      ExpressionFactory.lessOrEqualExp(
        EipTTodo.START_DATE_PROPERTY,
        getViewDate().getValue());
    Expression exp22 =
      ExpressionFactory.matchExp(EipTTodo.END_DATE_PROPERTY, ToDoUtils
        .getEmptyDate());

    // 終了日時のみ指定されている ToDo を検索
    Expression exp31 =
      ExpressionFactory.greaterOrEqualExp(
        EipTTodo.END_DATE_PROPERTY,
        getViewDate().getValue());
    Expression exp32 =
      ExpressionFactory.matchExp(EipTTodo.START_DATE_PROPERTY, ToDoUtils
        .getEmptyDate());

    query.andQualifier((exp11.andExp(exp12)).orExp(exp21.andExp(exp22)).orExp(
      exp31.andExp(exp32)));
    return query;
  }

  /*
   *
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("group", EipTScheduleMap.USER_ID_PROPERTY);
    return map;
  }

  /**
   * 指定した時間のcolspanを取得します。
   * 
   * @param hour
   * @return
   */
  public int getColspan(int hour) {
    return rows[(hour - startHour) * 4]
      + rows[(hour - startHour) * 4 + 1]
      + rows[(hour - startHour) * 4 + 2]
      + rows[(hour - startHour) * 4 + 3];
  }

  /**
   * 指定したスケジュールのcolspanを取得します。
   * 
   * @param hour
   * @return
   */
  public int getScheduleColspan(ScheduleOnedayResultData rd, int[] rows_) {
    int st = rd.getStartRow();
    int ed = rd.getEndRow();
    int span = 0;
    if (st == ed) {
      if (rows_[st] == rd.getIndex()) {
        span = rows[st] - rows_[st] + 1;
      } else {
        span = 1;
      }
    } else {
      for (int i = st; i < ed; i++) {
        span += rows[i];
      }
      span += 1 - rows_[st];
    }
    return span;
  }

  /**
   * 期間スケジュールを取得します。
   * 
   * @param id
   * @return
   */
  public ScheduleOnedayResultData getSpanSchedule(long id) {
    Integer userid = Integer.valueOf((int) id);
    return map.get(userid).getSpanResultData();
  }

  /**
   * 指定したユーザーのスケジュールリストを取得します。
   * 
   * @param id
   * @return
   */
  public List<ScheduleOnedayResultData> getScheduleList(long id) {
    Integer userid = Integer.valueOf((int) id);
    return map.get(userid).getSchedule();
  }

  /**
   * 指定したユーザーのrowsを取得します。
   * 
   * @param id
   * @return
   */
  public int[] getRows(long id) {
    Integer userid = Integer.valueOf((int) id);
    return map.get(userid).getRows();
  }

  /**
   * 指定したユーザーの重複スケジュールリストを取得します。
   * 
   * @param id
   * @return
   */
  public List<ScheduleOnedayResultData> getDuplicateScheduleList(long id) {
    Integer userid = Integer.valueOf((int) id);
    return map.get(userid).getDuplicateSchedule();
  }

  /**
   * 部署マップを取得します。
   * 
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  /**
   * 共有メンバーを取得します。
   * 
   * @return
   */
  public List<ALEipUser> getMemberList() {
    return members;
  }

  /**
   * グループリストを取得します。
   * 
   * @return
   */
  public List<ALEipGroup> getGroupList() {
    return groups;
  }

  /**
   * 指定したユーザーが自ユーザーかどうかを返します。
   * 
   * @param id
   * @return
   */
  public boolean isMatch(long id) {
    return userid == (int) id;
  }

  /**
   * colspanの最大値を返します。
   * 
   * @return
   */
  public int getMax() {
    return max - 1;
  }

  /**
   * 期間スケジュールがあるかどうかを返します。
   * 
   * @return
   */
  public boolean isHasspan() {
    return is_hasspan;
  }

  /**
   * ポートレット MyGroup へのリンクを取得する．
   * 
   * @return
   */
  public String getMyGroupURI() {
    return myGroupURI;
  }

  /**
   * 期間スケジュールリストを取得する.
   * 
   * @param id
   * @return
   */
  public List<ScheduleOnedayResultData> getTermResultDataList(long id) {
    return termmap.get(Integer.valueOf((int) id));
  }

  /**
   * ToDo リストを取得する.
   * 
   * @param id
   * @return
   */
  public List<ScheduleToDoResultData> getToDoResultDataList(long id) {
    return todomap.get(Integer.valueOf((int) id));
  }

  @Override
  public void setPortletId(String id) {
    portletId = id;
  }

  public List<FacilityResultData> getFacilityList() {
    return facilityList;
  }

  /**
   * 指定した施設のスケジュールリストを取得します。
   * 
   * @param id
   * @return
   */
  public List<ScheduleOnedayResultData> getFacilityScheduleList(long id) {
    Integer fid = Integer.valueOf((int) id);
    return facilitymap.get(fid).getSchedule();
  }

  /**
   * 指定した施設のrowsを取得します。
   * 
   * @param id
   * @return
   */
  public int[] getFacilityRows(long id) {
    Integer fid = Integer.valueOf((int) id);
    return facilitymap.get(fid).getRows();
  }

  /**
   * 指定した施設の重複スケジュールリストを取得します。
   * 
   * @param id
   * @return
   */
  public List<ScheduleOnedayResultData> getFacilityDuplicateScheduleList(long id) {
    Integer fid = Integer.valueOf((int) id);
    return facilitymap.get(fid).getDuplicateSchedule();
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_SELF;
  }

  @Override
  public boolean hasAuthoritySelfInsert() {
    return hasAuthoritySelfInsert;
  }

  public boolean hasAuthorityFacilityInsert() {
    return hasAuthorityFacilityInsert;
  }

}

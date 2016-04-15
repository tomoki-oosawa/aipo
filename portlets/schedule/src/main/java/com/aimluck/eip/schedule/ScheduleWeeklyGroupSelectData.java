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
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.cayenne.om.portlet.EipTTodo;
import com.aimluck.eip.cayenne.om.portlet.VEipTScheduleList;
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
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
import com.aimluck.eip.todo.util.ToDoUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 週間スケジュール（グループ）の検索結果を管理するクラスです。
 *
 */
public class ScheduleWeeklyGroupSelectData extends ScheduleWeeklySelectData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleWeeklyGroupSelectData.class.getName());

  /** <code>termmap</code> 期間スケジュールマップ */
  private Map<Integer, List<ScheduleTermWeekContainer>> termmap;

  /** <code>facilitytermmap</code> 設備期間スケジュールマップ */
  private Map<Integer, List<ScheduleTermWeekContainer>> facilitytermmap;

  /** <code>map</code> スケジュールマップ */
  private Map<Integer, ScheduleWeekContainer> map;

  /** <code>members</code> 共有メンバー */
  private List<ALEipUser> members;

  /** <code>groups</code> グループ */
  private List<ALEipGroup> groups;

  /** <code>groups</code> グループリスト */
  private List<ALEipGroup> facilitiyGroups;

  /** <code>userid</code> ログインユーザーID */
  private Integer userid;

  /** <code>myGroupURI</code> ポートレット MyGroup へのリンク */
  private String myGroupURI;

  /** <code>todomap</code> ToDo マップ */
  private Map<Integer, List<ScheduleToDoWeekContainer>> todomap;

  /** ポートレット ID */
  private String portletId;

  /** <code>map</code> スケジュールMap（設備） */
  private Map<Integer, ScheduleWeekContainer> facilitymap;

  private List<FacilityResultData> facilityList;

  /** <code>hasAuthoritySelfInsert</code> アクセス権限 */
  private boolean hasAuthoritySelfInsert = false;

  /** <code>hasAuthorityFacilityInsert</code> アクセス権限 */
  private boolean hasAuthorityFacilityInsert = false;

  private boolean hasAclviewOther = false;

  /** <code>target_group_name</code> グループ名 */
  private TurbineGroup target_group_name;

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
    // スーパークラスのメソッドを呼び出す
    super.init(action, rundata, context);

    if (rundata.getParameters().containsKey(ALEipConstants.LIST_FILTER)) {
      ALEipUtils.setTemp(
        rundata,
        context,
        ScheduleUtils.FILTER_NAMESPACE,
        rundata.getParameters().getString(ALEipConstants.LIST_FILTER));
    }
    if (rundata.getParameters().containsKey(ALEipConstants.LIST_FILTER_TYPE)) {
      ALEipUtils.setTemp(
        rundata,
        context,
        ScheduleUtils.FILTER_NAMESPACE_TYPE,
        rundata.getParameters().getString(ALEipConstants.LIST_FILTER_TYPE));
    }

    // 表示タイプの設定
    viewtype = "weekly-group";
    try {
      termmap = new LinkedHashMap<Integer, List<ScheduleTermWeekContainer>>();
      facilitytermmap =
        new LinkedHashMap<Integer, List<ScheduleTermWeekContainer>>();
      map = new LinkedHashMap<Integer, ScheduleWeekContainer>();
      todomap = new LinkedHashMap<Integer, List<ScheduleToDoWeekContainer>>();
      facilitymap = new LinkedHashMap<Integer, ScheduleWeekContainer>();

      groups = ALEipUtils.getMyGroups(rundata);
      facilitiyGroups = ALEipUtils.getALEipGroups();
      userid = Integer.valueOf(ALEipUtils.getUserId(rundata));
      String filter =
        ALEipUtils.getTemp(rundata, context, ScheduleUtils.FILTER_NAMESPACE);

      target_group_name = getGroup(filter);

      if (filter == null) {
        VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
        String groupName =
          portlet.getPortletConfig().getInitParameter("p3a-group");
        if (groupName != null) {
          ALEipUtils.setTemp(
            rundata,
            context,
            ScheduleUtils.FILTER_NAMESPACE,
            groupName);
          ALEipUtils.setTemp(
            rundata,
            context,
            ScheduleUtils.FILTER_NAMESPACE_TYPE,
            "group");
        }
      }

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

      // ポートレット MyGroup のへのリンクを取得する．
      myGroupURI =
        ScheduleUtils.getPortletURIinPersonalConfigPane(rundata, "MyGroup");
    } catch (Exception ex) {
      logger.error("schedule", ex);
    }
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
      List<VEipTScheduleList> list = getScheduleList(rundata, context);

      if (viewTodo == 1) {
        // ToDO の読み込み
        loadTodo(rundata, context);
      }

      // 時刻でソート
      ScheduleUtils.sortByTime(list);

      return new ResultList<VEipTScheduleList>(ScheduleUtils
        .sortByDummySchedule(list));
    } catch (Exception e) {
      logger.error("[ScheduleWeeklyGroupSelectData]", e);
      throw new ALDBErrorException();

    }
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
    ScheduleResultData rd = new ScheduleResultData();
    rd.initField();
    try {
      // スケジュールが棄却されている場合は表示しない
      if ("R".equals(record.getStatus())) {
        return rd;
      }

      boolean is_member = record.isMember();

      // Dummy スケジュールではない
      // 完全に隠す
      // 自ユーザー以外
      // 共有メンバーではない
      // オーナーではない
      if ((!"D".equals(record.getStatus()))
        && "P".equals(record.getPublicFlag())
        && (userid.intValue() != record.getUserId().intValue())
        && (userid.intValue() != record.getOwnerId().intValue())
        && !is_member) {
        return rd;
      } else if (!hasAclviewOther && !is_member) {// 閲覧権限がなく、グループでもない
        return rd;
      }

      if ("C".equals(record.getPublicFlag())
        && ("F".equals(record.getType()) || ("U".equals(record.getType()) && userid
          .intValue() != record.getUserId().intValue()))
        && (userid.intValue() != record.getOwnerId().intValue())
        && !is_member) {
        // 名前
        rd.setName(ALLocalizationUtils.getl10n("SCHEDULE_CLOSE_PUBLIC_WORD"));
        // 仮スケジュールかどうか
        rd.setTmpreserve(false);
      } else {
        // 名前
        rd.setName(record.getName());
        // 仮スケジュールかどうか
        rd.setTmpreserve("T".equals(record.getStatus()));
      }
      // ID
      rd.setScheduleId(record.getScheduleId().intValue());
      // 親スケジュール ID
      rd.setParentId(record.getParentId().intValue());
      // 開始日時
      rd.setStartDate(record.getStartDate());
      // 終了日時
      rd.setEndDate(record.getEndDate());
      // 公開するかどうか
      rd.setPublic("O".equals(record.getPublicFlag()));
      // 非表示にするかどうか
      rd.setHidden("P".equals(record.getPublicFlag()));
      // ダミーか
      rd.setDummy("D".equals(record.getStatus()));
      // ログインユーザかどうか
      rd.setLoginuser(record.getUserId().intValue() == userid.intValue());
      // オーナーかどうか
      rd.setOwner(record.getOwnerId().intValue() == userid.intValue());
      // 共有メンバーかどうか
      rd.setMember(record.isMember());
      // 繰り返しパターン
      rd.setPattern(record.getRepeatPattern());

      // // 週間スケジュールコンテナを取得
      // ScheduleWeekContainer weekCon = (ScheduleWeekContainer) map
      // .get(new Integer(record.getUserId()));

      // ユーザもしくは設備の週間スケジュールコンテナを取得する．
      ScheduleWeekContainer weekCon = null;
      if (ScheduleUtils.SCHEDULEMAP_TYPE_USER.equals(record.getType())) {
        weekCon = map.get(record.getUserId());
      } else {
        // if (ScheduleUtils.SCHEDULEMAP_TYPE_FACILITY.equals(record.getType()))
        // の場合
        weekCon = facilitymap.get(record.getUserId());
      }

      // 期間スケジュールの場合（「ログインユーザの期間スケジュール」もしくは「完全非公開以外の期間スケジュール」）
      if (rd.getPattern().equals("S")) {
        int stime =
          -(int) ((getViewStart().getValue().getTime() - rd
            .getStartDate()
            .getValue()
            .getTime()) / 86400000);
        int etime =
          -(int) ((getViewStart().getValue().getTime() - rd
            .getEndDate()
            .getValue()
            .getTime()) / 86400000);
        if (stime < 0) {
          stime = 0;
        }
        int count = stime;
        int col = etime - stime + 1;
        if (count + col > 7) {
          col = 7 - count;
        }
        rd.setRowspan(col);
        if (col > 0) {
          if (ScheduleUtils.SCHEDULEMAP_TYPE_USER.equals(record.getType())) {
            List<ScheduleTermWeekContainer> terms =
              termmap.get(record.getUserId());
            if (terms != null) {
              // 期間スケジュールを格納
              ScheduleUtils.addTermSchedule(
                terms,
                getViewStart().getValue(),
                count,
                rd);
            }
          } else {
            List<ScheduleTermWeekContainer> terms =
              facilitytermmap.get(record.getUserId());
            if (terms != null) {
              // 期間スケジュールを格納
              ScheduleUtils.addTermSchedule(
                terms,
                getViewStart().getValue(),
                count,
                rd);
            }
          }
        }
        return rd;
      }
      // 週間種スケジュールコンテナに格納
      weekCon.addResultData(rd);
    } catch (Exception e) {
      logger.error("schedule", e);
      return null;
    }
    return rd;
  }

  /**
   *
   * @param query
   * @param rundata
   * @param context
   * @return
   */
  protected List<VEipTScheduleList> getScheduleList(RunData rundata,
      Context context) {
    String filter =
      ALEipUtils.getTemp(rundata, context, ScheduleUtils.FILTER_NAMESPACE);
    String filter_type =
      ALEipUtils.getTemp(rundata, context, ScheduleUtils.FILTER_NAMESPACE_TYPE);
    if (filter == null || filter_type == null || filter.equals("")) {

      members = new ArrayList<ALEipUser>();
      members.add(ALEipUtils.getALEipUser(rundata));
      Calendar cal = Calendar.getInstance();
      cal.setTime(tmpCal.getTime());
      ScheduleWeekContainer week = new ScheduleWeekContainer();
      week.initField();
      week.setViewStartDate(cal);
      this.termmap.put(userid, new ArrayList<ScheduleTermWeekContainer>());
      this.map.put(userid, week);
      this.todomap.put(userid, new ArrayList<ScheduleToDoWeekContainer>());
      return ScheduleUtils.getScheduleList(
        userid,
        getViewStart().getValue(),
        getViewEndCrt().getValue(),
        Arrays.asList(userid),
        null);
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
        Calendar cal = Calendar.getInstance();
        cal.setTime(tmpCal.getTime());
        ScheduleWeekContainer week = new ScheduleWeekContainer();
        week.initField();
        week.setViewStartDate(cal);
        this.termmap.put(id, new ArrayList<ScheduleTermWeekContainer>());
        this.map.put(id, week);
        this.todomap.put(id, new ArrayList<ScheduleToDoWeekContainer>());
      }
    }

    List<Integer> facilityIds = null;
    String[] filteres = filter.split(";");
    if ("Facility".equals(filter)) {
      facilityIds = getFacilityIdAllList();
    } else if (("group".equals(filter_type)) && !("f".equals(filteres[0]))) {
      facilityIds = FacilitiesUtils.getFacilityIds(filteres[0]);
    } else {
      if ("f".equals(filteres[0])) {
        facilityIds =
          FacilitiesUtils.getFacilityGroupIds(Integer.valueOf(filteres[1]));
      } else {
        if (ScheduleUtils.isNumberValue(filter)) {
          facilityIds =
            FacilitiesUtils.getFacilityGroupIds(Integer.valueOf(filter));
        } else {
          facilityIds = new ArrayList<Integer>();
          String[] split = filter.split(",");
          if (split.length == 2) {
            // URLパラメータにfilterが含まれてしまっていた場合
            // デフォルト値のセットしなおし
            // facilityIds初期化
            VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
            String groupName =
              portlet.getPortletConfig().getInitParameter("p3a-group");
            if (groupName != null) {
              ALEipUtils.setTemp(
                rundata,
                context,
                ScheduleUtils.FILTER_NAMESPACE,
                groupName);
              ALEipUtils.setTemp(
                rundata,
                context,
                ScheduleUtils.FILTER_NAMESPACE_TYPE,
                "group");
            }
          }

        }
      }
    }

    int f_size = facilityIds.size();
    if (f_size == 0) {
      facilityIds.add(Integer.valueOf(-1));
    } else {
      for (int i = 0; i < f_size; i++) {
        Integer id = facilityIds.get(i);
        Calendar cal = Calendar.getInstance();
        cal.setTime(tmpCal.getTime());
        ScheduleWeekContainer week = new ScheduleWeekContainer();
        week.initField();
        week.setViewStartDate(cal);
        this.facilitytermmap
          .put(id, new ArrayList<ScheduleTermWeekContainer>());
        this.facilitymap.put(id, week);
      }
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
        if (eipUser.getUserId().getValue() == userid.intValue()) {
          members.remove(i);
          members.add(0, eipUser);
        }
      }
    }
    if ("Facility".equals(filter)) {
      facilityList = FacilitiesUtils.getFacilityList(filter);
    } else if ("facilitygroup".equals(filter_type) || "f".equals(filteres[0])) {
      if ("f".equals(filteres[0])) {
        facilityList =
          FacilitiesUtils.getFacilityGroupList(Integer.valueOf(filteres[1]));

      } else {

        if (ScheduleUtils.isNumberValue(filter)) {
          facilityList =
            FacilitiesUtils.getFacilityGroupList(Integer.valueOf(filter));
        }

      }
    } else {
      facilityList = FacilitiesUtils.getFacilityList(filter);
    }

    if (!("f".equals(filteres[0]))) {
      current_filter = filter;
    } else {
      current_filter = filteres[1];
    }
    current_filter_type = filter_type;
    return ScheduleUtils.getScheduleList(
      userid,
      getViewStart().getValue(),
      getViewEndCrt().getValue(),
      ulist,
      facilityIds);
  }

  private List<Integer> getFacilityIdAllList() {
    List<Integer> facilityIdAllList = new ArrayList<Integer>();

    try {
      SelectQuery<EipMFacility> query =
        Database.query(EipMFacility.class).select(
          EipMFacility.FACILITY_ID_PK_COLUMN);
      List<EipMFacility> aList = query.fetchList();

      for (EipMFacility record : aList) {
        facilityIdAllList.add(record.getFacilityId());
      }
    } catch (Exception ex) {
      logger.error("schedule", ex);
    }
    return facilityIdAllList;
  }

  /*
   *
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("group", VEipTScheduleList.USER_ID_PROPERTY);
    return map;
  }

  @Override
  public void loadTodo(RunData rundata, Context context) {
    try {
      List<EipTTodo> todos =
        getSelectQueryForTodo(rundata, context).fetchList();

      int todossize = todos.size();
      for (int i = 0; i < todossize; i++) {
        EipTTodo record = todos.get(i);
        ScheduleToDoResultData rd = new ScheduleToDoResultData();
        rd.initField();

        // ポートレット ToDo のへのリンクを取得する．
        String todo_url = "";
        if (userid.intValue() == record.getTurbineUser().getUserId().intValue()) {
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

        int stime;
        if (ScheduleUtils.equalsToDate(ToDoUtils.getEmptyDate(), rd
          .getStartDate()
          .getValue(), false)) {
          stime = 0;
        } else {
          stime =
            -(int) ((getViewStart().getValue().getTime() - rd
              .getStartDate()
              .getValue()
              .getTime()) / 86400000);
        }
        int etime =
          -(int) ((getViewStart().getValue().getTime() - rd
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
          List<ScheduleToDoWeekContainer> usertodos1 =
            todomap.get(record.getTurbineUser().getUserId());
          if (usertodos1 != null) {
            // ToDo を格納
            ScheduleUtils.addToDo(
              usertodos1,
              getViewStart().getValue(),
              count,
              rd);
          }
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

    Expression exp01 =
      ExpressionFactory.matchDbExp(TurbineUser.USER_ID_PK_COLUMN, userid);
    Expression exp02 =
      ExpressionFactory.noMatchDbExp(TurbineUser.USER_ID_PK_COLUMN, userid);
    Expression exp03 =
      ExpressionFactory.matchExp(EipTTodo.PUBLIC_FLAG_PROPERTY, "T");
    Expression exp04 =
      ExpressionFactory.matchExp(EipTTodo.ADDON_SCHEDULE_FLG_PROPERTY, "T");
    query.andQualifier(exp01.orExp(exp02.andExp(exp03)).andExp(exp04));

    // 終了日時
    Expression exp11 =
      ExpressionFactory.greaterOrEqualExp(
        EipTTodo.END_DATE_PROPERTY,
        getViewStart().getValue());
    // 開始日時
    Expression exp12 =
      ExpressionFactory.lessOrEqualExp(
        EipTTodo.START_DATE_PROPERTY,
        getViewEnd().getValue());

    // 開始日時のみ指定されている ToDo を検索
    Expression exp21 =
      ExpressionFactory.lessOrEqualExp(
        EipTTodo.START_DATE_PROPERTY,
        getViewEnd().getValue());
    Expression exp22 =
      ExpressionFactory.matchExp(EipTTodo.END_DATE_PROPERTY, ToDoUtils
        .getEmptyDate());

    // 終了日時のみ指定されている ToDo を検索
    Expression exp31 =
      ExpressionFactory.greaterOrEqualExp(
        EipTTodo.END_DATE_PROPERTY,
        getViewStart().getValue());
    Expression exp32 =
      ExpressionFactory.matchExp(EipTTodo.START_DATE_PROPERTY, ToDoUtils
        .getEmptyDate());

    query.andQualifier((exp11.andExp(exp12)).orExp(exp21.andExp(exp22)).orExp(
      exp31.andExp(exp32)));
    return query;
  }

  /**
   * 週間スケジュールコンテナを取得します。
   *
   * @param id
   * @return
   */
  public ScheduleWeekContainer getContainer(long id) {
    return map.get(Integer.valueOf((int) id));
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
   * 部署マップを取得します。
   *
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
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
   *
   * @param id
   * @return
   */
  public boolean isMatch(long id) {
    return userid.intValue() == (int) id;
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
   * 期間スケジュールコンテナを取得します。
   *
   * @param id
   * @return
   */
  public List<ScheduleTermWeekContainer> getTermContainer(long id) {
    return termmap.get(Integer.valueOf((int) id));
  }

  /**
   * 設備期間スケジュールコンテナを取得します。
   *
   * @param id
   * @return
   */
  public List<ScheduleTermWeekContainer> getFacilityTermContainer(long id) {
    return facilitytermmap.get(Integer.valueOf((int) id));
  }

  /**
   * ToDo コンテナを取得します。
   *
   * @param id
   * @return
   */
  public List<ScheduleToDoWeekContainer> getToDoContainer(long id) {
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
   * 設備の週間スケジュールコンテナを取得します。
   *
   * @param id
   * @return
   */
  public ScheduleWeekContainer getFacilityContainer(long id) {
    return facilitymap.get(Integer.valueOf((int) id));
  }

  /**
   * ログインユーザの ID を取得する．
   *
   * @return
   */
  public long getUserId() {
    return userid.longValue();
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

  /**
   * 施設のグループリストを取得します。
   *
   * @return
   */
  public List<ALEipGroup> getFacilitiyGroupList() {
    return facilitiyGroups;
  }

  @Override
  public boolean hasAuthoritySelfInsert() {
    return hasAuthoritySelfInsert;
  }

  public boolean hasAuthorityFacilityInsert() {
    return hasAuthorityFacilityInsert;
  }

  public TurbineGroup getGroup(String filter) {
    Expression exp1 =
      ExpressionFactory.matchExp(TurbineGroup.GROUP_NAME_PROPERTY, filter);

    SelectQuery<TurbineGroup> query = Database.query(TurbineGroup.class);

    query.setQualifier(exp1);
    List<TurbineGroup> list = query.fetchList();
    if (list.isEmpty()) {
      return null;
    }
    return list.get(0);
  }

  public TurbineGroup getTargetGroupName() {
    return target_group_name;
  }

  @Override
  public String getViewStartFormat() {
    return ALLocalizationUtils.getl10nFormat(
      "SCHEDULE_DATE_FORMAT_NOSPACE",
      getViewStart().getYear(),
      getViewStart().getMonth(),
      getViewStart().getDay());
  }

  @Override
  public String getViewEndFormat() {
    return ALLocalizationUtils.getl10nFormat(
      "SCHEDULE_DATE_FORMAT_NOSPACE",
      getViewEnd().getYear(),
      getViewEnd().getMonth(),
      getViewEnd().getDay());
  }
}

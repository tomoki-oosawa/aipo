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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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

import com.aimluck.commons.field.ALDateTimeField;
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
 * １日スケジュール（グループ）の検索結果を管理するクラスです。
 *
 */
public class ScheduleOnedayGroupSelectData extends ScheduleOnedaySelectData {

  /** <code>logger</code> logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleOnedayGroupSelectData.class.getName());

  /** <code>termmap</code> 期間スケジュールマップ */
  private Map<Integer, List<ScheduleOnedayResultData>> termmap;

  /** <code>facilitytermmap</code> 設備期間スケジュールマップ 9.4 */
  private Map<Integer, List<ScheduleOnedayResultData>> facilitytermmap;

  /** <code>map</code> スケジュールMap */
  private Map<Integer, ScheduleOnedayContainer> map;

  /** <code>members</code> 共有メンバー */
  private List<ALEipUser> members;

  /** <code>groups</code> グループリスト */
  private List<ALEipGroup> groups;

  /** <code>groups</code> グループリスト */
  private List<ALEipGroup> facilitiyGroups;

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

  /** <code>map</code> スケジュールMap（設備） */
  private Map<Integer, ScheduleOnedayContainer> facilitymap;

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

    viewtype = "oneday-group";
    try {
      termmap = new LinkedHashMap<Integer, List<ScheduleOnedayResultData>>();

      facilitytermmap =
        new LinkedHashMap<Integer, List<ScheduleOnedayResultData>>();

      map = new LinkedHashMap<Integer, ScheduleOnedayContainer>();
      todomap = new LinkedHashMap<Integer, List<ScheduleToDoResultData>>();
      facilitymap = new LinkedHashMap<Integer, ScheduleOnedayContainer>();

      groups = ALEipUtils.getMyGroups(rundata);
      facilitiyGroups = ALEipUtils.getALEipGroups();
      userid = ALEipUtils.getUserId(rundata);
      rows = new int[(endHour - startHour) * 12 + 1];
      int size = rows.length;
      for (int i = 0; i < size; i++) {
        rows[i] = 1;
      }
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

    } catch (Exception ex) {
      logger.error("[ScheduleOnedayGroupSelectData]", ex);
    }
  }

  @Override
  protected ResultList<VEipTScheduleList> selectList(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    try {
      List<VEipTScheduleList> resultBaseList =
        getScheduleList(rundata, context);
      List<VEipTScheduleList> resultList =
        ScheduleUtils.sortByDummySchedule(resultBaseList);

      List<VEipTScheduleList> list = new ArrayList<VEipTScheduleList>();
      List<VEipTScheduleList> delList = new ArrayList<VEipTScheduleList>();
      int delSize = 0;
      int resultSize = resultList.size();
      int size = 0;
      boolean canAdd = true;
      for (int i = 0; i < resultSize; i++) {
        VEipTScheduleList record = resultList.get(i);
        delList.clear();
        canAdd = true;
        size = list.size();
        for (int j = 0; j < size; j++) {
          VEipTScheduleList record2 = list.get(j);
          if (!record.getRepeatPattern().equals("N")
            && "D".equals(record2.getStatus())
            && record.getScheduleId().intValue() == record2
              .getParentId()
              .intValue()
            && record.getUserId().intValue() == record2.getUserId().intValue()) {
            canAdd = false;
            break;
          }
          if (!record2.getRepeatPattern().equals("N")
            && "D".equals(record.getStatus())
            && record2.getScheduleId().intValue() == record
              .getParentId()
              .intValue()
            && record2.getUserId().intValue() == record.getUserId().intValue()) {
            // [繰り返しスケジュール] 親の ID を検索
            if (!delList.contains(record2)) {
              delList.add(record2);
            }
            canAdd = true;
          }
        }
        delSize = delList.size();
        for (int k = 0; k < delSize; k++) {
          list.remove(delList.get(k));
        }

        if (canAdd) {
          list.add(record);
        }
      }

      // ダミーを削除する．
      delList.clear();
      size = list.size();
      for (int i = 0; i < size; i++) {
        VEipTScheduleList record = list.get(i);
        if ("D".equals(record.getStatus())) {
          delList.add(record);
        }
      }
      delSize = delList.size();
      for (int i = 0; i < delSize; i++) {
        list.remove(delList.get(i));
      }

      // ソート
      Collections.sort(list, new Comparator<VEipTScheduleList>() {

        @Override
        public int compare(VEipTScheduleList a, VEipTScheduleList b) {
          Calendar cal = Calendar.getInstance();
          Calendar cal2 = Calendar.getInstance();
          cal.setTime(a.getStartDate());
          cal.set(0, 0, 0);
          cal2.setTime(b.getStartDate());
          cal2.set(0, 0, 0);
          if ((cal.getTime()).compareTo(cal2.getTime()) != 0) {
            return (cal.getTime()).compareTo(cal2.getTime());
          } else {
            cal.setTime(a.getEndDate());
            cal.set(0, 0, 0);
            cal2.setTime(b.getEndDate());
            cal2.set(0, 0, 0);

            return (cal.getTime()).compareTo(cal2.getTime());
          }
        }
      });

      if (viewToDo == 1) {
        // ToDo の読み込み
        loadToDo(rundata, context);
      }

      return new ResultList<VEipTScheduleList>(list);
    } catch (Exception e) {
      logger.error("[ScheduleOnedayGroupSelectData]", e);
      throw new ALDBErrorException();
    }
  }

  protected List<VEipTScheduleList> getScheduleList(RunData rundata,
      Context context) {

    Calendar cal = Calendar.getInstance();
    cal.setTime(getViewDate().getValue());
    cal.add(Calendar.DATE, 1);
    cal.add(Calendar.MILLISECOND, -1);
    ALDateTimeField field = new ALDateTimeField();
    field.setValue(cal.getTime());

    String filter =
      ALEipUtils.getTemp(rundata, context, ScheduleUtils.FILTER_NAMESPACE);
    String filter_type =
      ALEipUtils.getTemp(rundata, context, ScheduleUtils.FILTER_NAMESPACE_TYPE);

    if ("all".equals(filter)) {
      filter = filter_type = null;
      ALEipUtils.removeTemp(rundata, context, ScheduleUtils.FILTER_NAMESPACE);
      ALEipUtils.removeTemp(
        rundata,
        context,
        ScheduleUtils.FILTER_NAMESPACE_TYPE);
    }

    if (filter == null
      || filter_type == null
      || filter.equals("")
      || tmpViewDate2 != null) {

      members = new ArrayList<ALEipUser>();
      members.add(ALEipUtils.getALEipUser(rundata));
      ScheduleOnedayContainer con = new ScheduleOnedayContainer();
      con.initField();
      con.initHour(startHour, endHour);
      Integer uid = Integer.valueOf(ALEipUtils.getUserId(rundata));
      this.termmap.put(uid, new ArrayList<ScheduleOnedayResultData>());
      this.map.put(uid, con);
      this.todomap.put(uid, new ArrayList<ScheduleToDoResultData>());
      return ScheduleUtils.getScheduleList(
        userid,
        getViewDate().getValue(),
        field.getValue(),
        Arrays.asList(userid),
        null);
    }

    // グループ名からユーザ（設備）を取得
    List<Integer> ulist = ALEipUtils.getUserIds(filter);
    // グループにユーザが存在しない場合はダミーユーザを設定し、検索します。(0件ヒット)
    // ダミーユーザーID = -1
    int usize = ulist.size();

    if (usize == 0) {
      ulist.add(Integer.valueOf(-1));
    } else {
      for (int i = 0; i < usize; i++) {
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
        ScheduleOnedayContainer con = new ScheduleOnedayContainer();
        con.initField();
        con.initHour(startHour, endHour);
        this.facilitytermmap.put(id, new ArrayList<ScheduleOnedayResultData>());
        this.facilitymap.put(id, con);
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
        if (eipUser.getUserId().getValue() == userid) {
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
      getViewDate().getValue(),
      field.getValue(),
      ulist,
      facilityIds);
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
      logger.error("schedule", ex);
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
  protected Object getResultData(VEipTScheduleList record)
      throws ALPageNotFoundException, ALDBErrorException {
    ScheduleOnedayResultData rd = new ScheduleOnedayResultData();
    rd.initField();
    try {
      if ("R".equals(record.getStatus())) {
        return rd;
      }
      if (!ScheduleUtils.isView(
        getViewDate(),
        record.getRepeatPattern(),
        record.getStartDate(),
        record.getEndDate())) {
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
        && (userid != record.getUserId().intValue())
        && (userid != record.getOwnerId().intValue())
        && !is_member) {
        return rd;
      } else if (!hasAclviewOther && !is_member) {// 閲覧権限がなく、グループでもない
        return rd;
      }
      if ("C".equals(record.getPublicFlag())
        && ("F".equals(record.getType()) || ("U".equals(record.getType()) && userid != record
          .getUserId()
          .intValue()))
        && (userid != record.getOwnerId().intValue())
        && !is_member) {
        rd.setName(ALLocalizationUtils.getl10n("SCHEDULE_CLOSE_PUBLIC_WORD"));
        // 仮スケジュールかどうか
        rd.setTmpreserve(false);
      } else {
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
      rd.setLoginuser(record.getUserId().intValue() == userid);
      // オーナーかどうか
      rd.setOwner(record.getOwnerId().intValue() == userid);
      // 共有メンバーかどうか
      rd.setMember(is_member);
      // 繰り返しパターン
      rd.setPattern(record.getRepeatPattern());

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
        if (ScheduleUtils.SCHEDULEMAP_TYPE_USER.equals(record.getType())) {
          List<ScheduleOnedayResultData> terms =
            termmap.get(record.getUserId());
          if (terms != null) {
            // 期間スケジュールを格納
            terms.add(rd);
          }
        } else {
          List<ScheduleOnedayResultData> terms =
            facilitytermmap.get(record.getUserId());
          if (terms != null) {
            // 期間スケジュールを格納
            terms.add(rd);
          }
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
      logger.error("schedule", e);
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
      logger.error("schedule", ex);
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
    map.putValue("group", VEipTScheduleList.USER_ID_PROPERTY);
    return map;
  }

  /**
   * 指定した時間のcolspanを取得します。
   *
   * @param hour
   * @return
   */
  public int getColspan(int hour) {
    return rows[(hour - startHour) * 12]
      + rows[(hour - startHour) * 12 + 1]
      + rows[(hour - startHour) * 12 + 2]
      + rows[(hour - startHour) * 12 + 3]
      + rows[(hour - startHour) * 12 + 4]
      + rows[(hour - startHour) * 12 + 5]
      + rows[(hour - startHour) * 12 + 6]
      + rows[(hour - startHour) * 12 + 7]
      + rows[(hour - startHour) * 12 + 8]
      + rows[(hour - startHour) * 12 + 9]
      + rows[(hour - startHour) * 12 + 10]
      + rows[(hour - startHour) * 12 + 11];
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

  public List<ScheduleOnedayResultData> getDuplicateSchedule(long id) {
    Integer userid = Integer.valueOf((int) id);
    return map.get(userid).getDuplicateSchedule();
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

  public int getDuplicateScheduleListRowCount(long id) {
    Integer userid = Integer.valueOf((int) id);
    return map.get(userid).getDuplicateScheduleRowCount();
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
   * 施設のグループリストを取得します。
   *
   * @return
   */
  public List<ALEipGroup> getFacilitiyGroupList() {
    return facilitiyGroups;
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
   * 設備期間スケジュールリストを取得する.
   *
   * @param id
   * @return
   */
  public List<ScheduleOnedayResultData> getFacilityTermResultDataList(long id) {
    return facilitytermmap.get(Integer.valueOf((int) id));
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

  @Override
  public String getViewDateText() {
    return ALLocalizationUtils.getl10nFormat(
      "SCHEDULE_DATE_FORMAT_NOSPACE",
      getViewDate().getYear(),
      getViewDate().getMonth(),
      getViewDate().getDay());
  }

  /**
   * 指定した設備のrowsを取得します。
   *
   * @param id
   * @return
   */
  public int[] getFacilityRows(long id) {
    Integer fid = Integer.valueOf((int) id);
    return facilitymap.get(fid).getRows();
  }

  /**
   * 指定した設備の重複スケジュールリストを取得します。
   *
   * @param id
   * @return
   */
  public List<ScheduleOnedayResultData> getFacilityDuplicateScheduleList(long id) {
    Integer fid = Integer.valueOf((int) id);
    return facilitymap.get(fid).getDuplicateSchedule();
  }

  public int getFacilityDuplicateScheduleListRowCount(long id) {
    Integer fid = Integer.valueOf((int) id);
    return facilitymap.get(fid).getDuplicateScheduleRowCount();
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

  private TurbineGroup getGroup(String filter) {
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
}

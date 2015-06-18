/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2015 Aimluck,Inc.
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
package com.aimluck.eip.exttimecard;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.cayenne.om.portlet.EipTExtTimecard;
import com.aimluck.eip.cayenne.om.portlet.EipTExtTimecardSystem;
import com.aimluck.eip.cayenne.om.portlet.EipTExtTimecardSystemMap;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.exttimecard.util.ExtTimecardUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * タイムカード集計の一覧を処理するクラスです。 <br />
 *
 *
 */

public class ExtTimecardSummaryListSelectData extends
    ALAbstractSelectData<EipTExtTimecard, EipTExtTimecard> implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ExtTimecardSummaryListSelectData.class.getName());

  /** <code>target_group_name</code> 表示対象の部署名 */
  private String target_group_name;

  /** <code>target_user_id</code> 表示対象のユーザ ID */
  private String target_user_id;

  /** <code>myGroupList</code> グループリスト（My グループと部署） */
  private List<ALEipGroup> myGroupList = null;

  /** <code>userList</code> 表示切り替え用のユーザリスト */
  private List<ALEipUser> userList = null;

  /** 一覧データ */
  private List<Object> list;

  /** <code>userid</code> ユーザーID */
  private String userid;

  /** <code>TARGET_GROUP_NAME</code> グループによる表示切り替え用変数の識別子 */
  private final String TARGET_GROUP_NAME = "target_group_name";

  /** <code>TARGET_USER_ID</code> ユーザによる表示切り替え用変数の識別子 */
  private final String TARGET_USER_ID = "target_user_id";

  private String nowtime;

  /** ユーザーマップ */
  private Map<Integer, List<ExtTimecardResultData>> usermap;

  /** 日付マップ */
  private Map<Integer, ExtTimecardSummaryResultData> datemap;

  /** アクセス権限の機能名 */
  private String aclPortletFeature = null;

  /** 閲覧権限の有無 */
  private boolean hasAclSummaryOther;

  /** 他のユーザーの更新権限 */
  private boolean hasAclUpdate;

  /** 他のユーザーの追加権限 */
  private boolean hasAclInsert;

  /** 他ユーザーのxlsエクスポート権限 */
  private boolean hasAclXlsExport;

  /** <code>viewMonth</code> 現在の月 */
  private ALDateTimeField viewMonth;

  /** <code>prevMonth</code> 前の月 */
  private ALDateTimeField prevMonth;

  /** <code>nextMonth</code> 次の月 */
  private ALDateTimeField nextMonth;

  /** <code>currentMonth</code> 今月 */
  private ALDateTimeField currentMonth;

  /** <code>today</code> 今日 */
  private ALDateTimeField today;

  /** <code>viewStart</code> 表示開始日時 */
  private ALDateTimeField viewStart;

  /** <code>viewEnd</code> 表示終了日時 */
  private ALDateTimeField viewEnd;

  /** <code>viewEndCrt</code> 表示終了日時 (Criteria) */
  private ALDateTimeField viewEndCrt;

  private final String MODE = "summary";

  /** <code>viewTodo</code> ToDo 表示設定 */
  protected int viewTodo;

  /** 開始日 */
  private int startDay;

  /**
   *
   */
  @Override
  public void initField() {
  }

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

    this.initField();

    // POST/GET から yyyy-MM の形式で受け渡される。
    // 現在の月
    viewMonth = new ALDateTimeField("yyyy-MM");
    viewMonth.setNotNull(true);
    // 前の月
    prevMonth = new ALDateTimeField("yyyy-MM");
    // 次の月
    nextMonth = new ALDateTimeField("yyyy-MM");
    // 今月
    currentMonth = new ALDateTimeField("yyyy-MM");
    // 表示開始日時
    viewStart = new ALDateTimeField("yyyy-MM-dd");
    // 表示終了日時
    viewEnd = new ALDateTimeField("yyyy-MM-dd");
    // 表示終了日時 (Criteria)
    viewEndCrt = new ALDateTimeField("yyyy-MM-dd");

    startDay = 1;

    // 自ポートレットからのリクエストであれば、パラメータを展開しセッションに保存する。
    if (ALEipUtils.isMatch(rundata, context)) {
      // スケジュールの表示開始日時
      // e.g. 2004-3-14
      if (rundata.getParameters().containsKey("view_month")) {
        ALEipUtils.setTemp(rundata, context, "view_month", rundata
          .getParameters()
          .getString("view_month"));
      }
    }

    // ログインユーザの ID を設定する．
    userid = Integer.toString(ALEipUtils.getUserId(rundata));

    // My グループの一覧を取得する．
    List<ALEipGroup> myGroups = ALEipUtils.getMyGroups(rundata);
    myGroupList = new ArrayList<ALEipGroup>();
    int length = myGroups.size();
    for (int i = 0; i < length; i++) {
      myGroupList.add(myGroups.get(i));
    }

    try {
      // スケジュールを表示するユーザ ID をセッションに設定する．
      String userFilter = ALEipUtils.getTemp(rundata, context, TARGET_USER_ID);
      if (userFilter != null && (!userFilter.equals(""))) {
        int paramId = -1;
        try {
          paramId = Integer.parseInt(userFilter);
          if (paramId > 3) {
            ALEipUser user = ALEipUtils.getALEipUser(paramId);
            if (user != null) {
              // 指定したユーザが存在する場合，セッションに保存する．
              ALEipUtils.setTemp(rundata, context, TARGET_USER_ID, userFilter);
            } else {
              ALEipUtils.removeTemp(rundata, context, TARGET_USER_ID);
            }
          }
        } catch (NumberFormatException e) {
        }
      } else {
        ALEipUtils.setTemp(rundata, context, TARGET_USER_ID, userid);
      }
    } catch (Exception ex) {
      logger.error("exttimecard", ex);
    }

    // アクセス権
    if (target_user_id == null
      || "".equals(target_user_id)
      || userid.equals(target_user_id)) {
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_TIMECARD_TIMECARD_SELF;
    } else {
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_TIMECARD_TIMECARD_OTHER;
    }
    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();
    hasAclSummaryOther =
      aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        ALAccessControlConstants.POERTLET_FEATURE_TIMECARD_TIMECARD_OTHER,
        ALAccessControlConstants.VALUE_ACL_LIST);

    // 他のユーザー更新権限
    hasAclUpdate =
      aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        ALAccessControlConstants.POERTLET_FEATURE_TIMECARD_TIMECARD_OTHER,
        ALAccessControlConstants.VALUE_ACL_UPDATE);

    // 他のユーザーの追加権限
    hasAclInsert =
      aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        ALAccessControlConstants.POERTLET_FEATURE_TIMECARD_TIMECARD_OTHER,
        ALAccessControlConstants.VALUE_ACL_INSERT);

    // 他のユーザーの編集権限
    hasAclXlsExport =
      aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        ALAccessControlConstants.POERTLET_FEATURE_TIMECARD_TIMECARD_OTHER,
        ALAccessControlConstants.VALUE_ACL_EXPORT);

    if (!hasAclSummaryOther) {
      // 他ユーザーの閲覧権限がないときには、グループを未選択にする。
      target_group_name = "only";
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_TIMECARD_TIMECARD_SELF;
      hasAclXlsExport =
        aclhandler.hasAuthority(
          ALEipUtils.getUserId(rundata),
          ALAccessControlConstants.POERTLET_FEATURE_TIMECARD_TIMECARD_SELF,
          ALAccessControlConstants.VALUE_ACL_EXPORT);
    }

    datemap = new LinkedHashMap<Integer, ExtTimecardSummaryResultData>();

    usermap = new LinkedHashMap<Integer, List<ExtTimecardResultData>>();

    /** 現在のユーザを取得 */
    if (target_user_id != null && !target_user_id.isEmpty()) {
      /** 勤務形態通常のstartDayを取得 */
      SelectQuery<EipTExtTimecardSystemMap> default_query =
        Database.query(EipTExtTimecardSystemMap.class);
      Expression exp =
        ExpressionFactory.matchExp(
          EipTExtTimecardSystemMap.USER_ID_PROPERTY,
          target_user_id);
      default_query.setQualifier(exp);
      ResultList<EipTExtTimecardSystemMap> map_list =
        default_query.getResultList();
      if (!map_list.isEmpty()) {
        startDay = map_list.get(0).getEipTExtTimecardSystem().getStartDay();
      } else {
        EipTExtTimecardSystem system =
          Database.get(EipTExtTimecardSystem.class, 1);
        if (system != null) {
          try {
            Date now = new Date();
            EipTExtTimecardSystemMap rd = new EipTExtTimecardSystemMap();
            rd.setEipTExtTimecardSystem(system);
            int userid = Integer.parseInt(target_user_id);
            rd.setUserId(userid);
            rd.setCreateDate(now);
            rd.setUpdateDate(now);
            Database.commit();
            startDay = system.getStartDay();
          } catch (Exception ex) {
            Database.rollback();
            logger.error("exttimecard", ex);
          }
        }
      }
    } else if (userid != null && !userid.isEmpty()) {
      /** 勤務形態通常のstartDayを取得 */
      SelectQuery<EipTExtTimecardSystemMap> default_query =
        Database.query(EipTExtTimecardSystemMap.class);
      Expression exp =
        ExpressionFactory.matchExp(
          EipTExtTimecardSystemMap.USER_ID_PROPERTY,
          userid);
      default_query.setQualifier(exp);
      ResultList<EipTExtTimecardSystemMap> map_list =
        default_query.getResultList();
      if (!map_list.isEmpty()) {
        startDay = map_list.get(0).getEipTExtTimecardSystem().getStartDay();
      } else {
        EipTExtTimecardSystem system =
          Database.get(EipTExtTimecardSystem.class, 1);
        if (system != null) {
          try {
            Date now = new Date();
            EipTExtTimecardSystemMap rd = new EipTExtTimecardSystemMap();
            rd.setEipTExtTimecardSystem(system);
            int userid = Integer.parseInt(target_user_id);
            rd.setUserId(userid);
            rd.setCreateDate(now);
            rd.setUpdateDate(now);
            Database.commit();
            startDay = system.getStartDay();
          } catch (Exception ex) {
            Database.rollback();
            logger.error("exttimecard", ex);
          }
        }
      }
    }

    // 今日の日付
    today = new ALDateTimeField("yyyy-MM-dd");
    Calendar to = Calendar.getInstance();
    to.set(Calendar.HOUR_OF_DAY, 0);
    to.set(Calendar.MINUTE, 0);
    today.setValue(to.getTime());
    boolean isBeforeThanStartDay =
      (Integer.parseInt(today.getDay().toString()) < startDay);

    // 現在の月
    String tmpViewMonth = ALEipUtils.getTemp(rundata, context, "view_month");
    if (tmpViewMonth == null || tmpViewMonth.equals("")) {
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.DATE, 1);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      if (isBeforeThanStartDay) {
        cal.add(Calendar.MONTH, -1);
      }
      viewMonth.setValue(cal.getTime());
    } else {
      viewMonth.setValue(tmpViewMonth);
      if (!viewMonth.validate(new ArrayList<String>())) {
        ALEipUtils.removeTemp(rundata, context, "view_month");
        throw new ALPageNotFoundException();
      }
    }

    if (!isBeforeThanStartDay
      && Integer.parseInt(today.getMonth()) == Integer.parseInt(viewMonth
        .getMonth()
        .toString())) {
      currentMonth.setValue(to.getTime());
    } else {
      Calendar tmp_cal = Calendar.getInstance();
      tmp_cal.set(Calendar.DATE, 1);
      tmp_cal.set(Calendar.HOUR_OF_DAY, 0);
      tmp_cal.set(Calendar.MINUTE, 0);
      if (isBeforeThanStartDay) {
        tmp_cal.add(Calendar.MONTH, -1);
      }
      currentMonth.setValue(tmp_cal.getTime());
    }

    // 表示開始日時
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.MONTH, Integer.parseInt(viewMonth.getMonth()) - 1);
    cal.set(Calendar.DATE, startDay);
    Date startDate = cal.getTime();
    viewStart.setValue(startDate);

    // 表示終了日時
    cal.add(Calendar.MONTH, 1);
    cal.add(Calendar.DATE, -1);
    Date endDate = cal.getTime();
    viewEnd.setValue(endDate);
    viewEndCrt.setValue(endDate);

    // 次の月、前の月
    Calendar cal2 = Calendar.getInstance();
    cal2.setTime(viewMonth.getValue());
    cal2.add(Calendar.MONTH, 1);
    nextMonth.setValue(cal2.getTime());
    cal2.add(Calendar.MONTH, -2);
    prevMonth.setValue(cal2.getTime());

    ALEipUtils.setTemp(rundata, context, "tmpStart", viewStart.toString()
      + "-00-00");
    ALEipUtils.setTemp(rundata, context, "tmpEnd", viewStart.toString()
      + "-00-00");

    setupLists(rundata, context);
  }

  /**
   * 一覧表示します。
   *
   * @param action
   * @param rundata
   * @param context
   * @return TRUE 成功 FASLE 失敗
   */
  @Override
  public boolean doViewList(ALAction action, RunData rundata, Context context) {
    try {
      init(action, rundata, context);
      doCheckAclPermission(
        rundata,
        context,
        ALAccessControlConstants.VALUE_ACL_LIST);
      action.setMode(ALEipConstants.MODE_LIST);
      for (int i = 0; i < userList.size(); i++) {
        ALEipUser eipUser = userList.get(i);
        List<EipTExtTimecard> aList =
          selectList(rundata, context, eipUser.getUserId().getValueAsString());
        if (aList != null) {
          list = new ArrayList<Object>();
          Object obj = null;
          int size = aList.size();
          for (int j = 0; j < size; j++) {
            obj = getResultData(aList.get(j));
            if (obj != null) {
              list.add(obj);
            }
          }
        }
      }
      action.setResultData(this);
      action.putData(rundata, context);
      ALEipUtils.removeTemp(rundata, context, ALEipConstants.ENTITY_ID);
      return (list != null);
    } catch (ALPermissionException e) {
      ALEipUtils.redirectPermissionError(rundata);
      return false;
    } catch (ALPageNotFoundException e) {
      ALEipUtils.redirectPageNotFound(rundata);
      return false;
    } catch (ALDBErrorException e) {
      ALEipUtils.redirectDBError(rundata);
      return false;
    }

  }

  /**
   *
   * @param rundata
   * @param context
   * @param target_user_id
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  protected List<EipTExtTimecard> selectList(RunData rundata, Context context,
      String target_user_id) throws ALPageNotFoundException, ALDBErrorException {
    try {
      // 指定グループや指定ユーザをセッションに設定する．
      setupLists(rundata, context);

      if (!"".equals(target_user_id)) {

        SelectQuery<EipTExtTimecard> query =
          getSelectQuery(rundata, context, target_user_id);
        buildSelectQueryForListView(query);
        query.orderAscending(EipTExtTimecard.PUNCH_DATE_PROPERTY);

        return query.getResultList();
      } else {
        return null;
      }
    } catch (Exception ex) {
      logger.error("exttimecard", ex);
      return null;
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
  protected ResultList<EipTExtTimecard> selectList(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    try {
      // 指定グループや指定ユーザをセッションに設定する．
      setupLists(rundata, context);

      if (!"".equals(target_user_id)) {

        SelectQuery<EipTExtTimecard> query =
          getSelectQuery(rundata, context, target_user_id);
        buildSelectQueryForListView(query);
        query.orderAscending(EipTExtTimecard.PUNCH_DATE_PROPERTY);

        return query.getResultList();
      } else {
        return null;
      }
    } catch (Exception ex) {
      logger.error("exttimecard", ex);
      return null;
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
  protected EipTExtTimecard selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   *
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipTExtTimecard record)
      throws ALPageNotFoundException, ALDBErrorException {
    try {
      int user_id = record.getUserId();

      ExtTimecardResultData rd = new ExtTimecardResultData();
      rd.initField();
      rd.setPunchDate(record.getPunchDate());
      rd.setRefixFlag(record.getCreateDate(), record.getUpdateDate());
      rd.setClockInTime(record.getClockInTime());
      rd.setClockOutTime(record.getClockOutTime());
      for (int i = 1; i <= EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY; i++) {
        rd.setOutgoingTime(record.getOutgoingTime(i), i);
        rd.setComebackTime(record.getComebackTime(i), i);
      }
      rd.setType(record.getType());

      List<ExtTimecardResultData> list;
      if (usermap.containsKey(user_id)) {
        list = usermap.get(user_id);
        list.add(rd);
        usermap.put(user_id, list);
      } else {
        list = new ArrayList<ExtTimecardResultData>();
        list.add(rd);
        usermap.put(user_id, list);
      }
    } catch (Exception ex) {
      logger.error("exttimecard", ex);
      return null;
    }
    return null;
  }

  /**
   *
   * @param obj
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultDataDetail(EipTExtTimecard obj)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /*
   * (非 Javadoc)
   */
  @Override
  protected Attributes getColumnMap() {
    return null;
  }

  /**
   * ログイン中のユーザー情報を取得
   *
   */

  private List<ALEipUser> getUserList(int userid) {
    List<ALEipUser> list = new ArrayList<ALEipUser>();
    ALEipUser user = new ALEipUser();
    try {
      user = ALEipUtils.getALEipUser(userid);
    } catch (NumberFormatException e1) {
    } catch (ALDBErrorException e1) {
    }
    list.add(user);
    return list;
  }

  /**
   * 指定グループや指定ユーザをセッションに設定する．
   *
   * @param rundata
   * @param context
   * @throws ALDBErrorException
   */
  private void setupLists(RunData rundata, Context context) {
    target_group_name = getTargetGroupName(rundata, context);
    if (target_group_name != null) {
      if ((!target_group_name.equals(""))
        && (!target_group_name.equals("all"))
        && (!target_group_name.equals("only"))) {
        userList = ALEipUtils.getUsers(target_group_name);
      } else if (target_group_name.equals("all")
        || target_group_name.equals("only")) {
        userList = getUserList(Integer.parseInt(userid));
      } else {
        userList = ALEipUtils.getUsers("LoginUser");
      }
    } else {
      userList = ALEipUtils.getUsers("LoginUser");
    }

    if (userList == null || userList.size() == 0) {
      target_user_id = "";
      ALEipUtils.removeTemp(rundata, context, TARGET_USER_ID);
      return;
    }

    target_user_id = getTargetUserId(rundata, context);
  }

  /**
   * 表示切り替えで指定したグループ ID を取得する．
   *
   * @param rundata
   * @param context
   * @return
   */
  private String getTargetGroupName(RunData rundata, Context context) {
    String target_group_name = null;
    String idParam = null;
    if (ALEipUtils.isMatch(rundata, context)) {
      // 自ポートレットへのリクエストの場合に，グループ名を取得する．
      idParam = rundata.getParameters().getString(TARGET_GROUP_NAME);
    }
    target_group_name = ALEipUtils.getTemp(rundata, context, TARGET_GROUP_NAME);

    if (idParam == null && target_group_name == null) {
      ALEipUtils.setTemp(rundata, context, TARGET_GROUP_NAME, "only");
      target_group_name = "only";
    } else if (idParam != null) {
      ALEipUtils.setTemp(rundata, context, TARGET_GROUP_NAME, idParam);
      target_group_name = idParam;
    }
    return target_group_name;
  }

  /**
   * 表示切り替えで指定したユーザ ID を取得する．
   *
   * @param rundata
   * @param context
   * @return
   */
  private String getTargetUserId(RunData rundata, Context context) {
    String target_user_id = null;
    String idParam = null;
    if (ALEipUtils.isMatch(rundata, context)) {
      // 自ポートレットへのリクエストの場合に，ユーザ ID を取得する．
      idParam = rundata.getParameters().getString(TARGET_USER_ID);
    }
    target_user_id = ALEipUtils.getTemp(rundata, context, TARGET_USER_ID);

    if (idParam == null && (target_user_id == null)) {
      // ログインユーザのスケジュールを表示するため，ログイン ID を設定する．
      ALEipUtils.setTemp(rundata, context, TARGET_USER_ID, userid);
      target_user_id = userid;
    } else if (idParam != null) {
      if (idParam.equals("none")) {
        // グループで表示を切り替えた場合，
        // ログインユーザもしくはユーザリストの一番初めのユーザを
        // 表示するため，ユーザ ID を設定する．
        ALEipUser eipUser = null;
        boolean found = false;
        int length = userList.size();
        for (int i = 0; i < length; i++) {
          eipUser = userList.get(i);
          String eipUserId = eipUser.getUserId().getValueAsString();
          if (userid.equals(eipUserId)) {
            ALEipUtils.setTemp(rundata, context, TARGET_USER_ID, userid);
            target_user_id = userid;
            found = true;
            break;
          }
        }
        if (!found) {
          eipUser = userList.get(0);
          String userId = eipUser.getUserId().getValueAsString();
          ALEipUtils.setTemp(rundata, context, TARGET_USER_ID, userId);
          target_user_id = userId;
        }
      } else {
        // ユーザで表示を切り替えた場合，指定したユーザの ID を設定する．
        ALEipUtils.setTemp(rundata, context, TARGET_USER_ID, idParam);
        target_user_id = idParam;
      }
    }
    return target_user_id;
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipTExtTimecard> getSelectQuery(RunData rundata,
      Context context, String target_user_id) {
    SelectQuery<EipTExtTimecard> query = Database.query(EipTExtTimecard.class);

    /** 勤務形態通常のstartDayを取得 */
    startDay = 1;
    SelectQuery<EipTExtTimecardSystemMap> default_query =
      Database.query(EipTExtTimecardSystemMap.class);
    Expression exp =
      ExpressionFactory.matchExp(
        EipTExtTimecardSystemMap.USER_ID_PROPERTY,
        target_user_id);
    default_query.setQualifier(exp);
    ResultList<EipTExtTimecardSystemMap> map_list =
      default_query.getResultList();
    if (!map_list.isEmpty()) {
      startDay = map_list.get(0).getEipTExtTimecardSystem().getStartDay();
    } else {
      EipTExtTimecardSystem system =
        Database.get(EipTExtTimecardSystem.class, 1);
      if (system != null) {
        try {
          Date now = new Date();
          EipTExtTimecardSystemMap rd = new EipTExtTimecardSystemMap();
          rd.setEipTExtTimecardSystem(system);
          int userid = Integer.parseInt(target_user_id);
          rd.setUserId(userid);
          rd.setCreateDate(now);
          rd.setUpdateDate(now);
          Database.commit();
          startDay = system.getStartDay();
        } catch (Exception ex) {
          Database.rollback();
          logger.error("exttiemcard", ex);
        }
      }
    }

    Expression exp1 =
      ExpressionFactory.matchExp(EipTExtTimecard.USER_ID_PROPERTY, Integer
        .valueOf(target_user_id));
    query.setQualifier(exp1);

    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, Integer.parseInt(viewMonth.getYear()));
    cal.set(Calendar.MONTH, Integer.parseInt(viewMonth.getMonth()) - 1);
    cal.set(Calendar.DAY_OF_MONTH, startDay);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    Date date = cal.getTime();
    Expression exp11 =
      ExpressionFactory.greaterOrEqualExp(
        EipTExtTimecard.PUNCH_DATE_PROPERTY,
        date);

    cal.add(Calendar.MONTH, +1);
    cal.add(Calendar.MILLISECOND, -1);
    Expression exp12 =
      ExpressionFactory.lessOrEqualExp(EipTExtTimecard.PUNCH_DATE_PROPERTY, cal
        .getTime());
    query.andQualifier(exp11.andExp(exp12));

    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * ユーザー毎のタイムカード一覧を取得する。
   *
   * @return
   */
  public List<ExtTimecardSummaryResultData> getUserExtTimecards() {
    List<ExtTimecardSummaryResultData> list =
      new ArrayList<ExtTimecardSummaryResultData>();
    Set<Integer> userset = usermap.keySet();

    /** 以下、ユーザーごとの処理 */
    for (Integer user_id : userset) {
      List<ExtTimecardResultData> userlist = usermap.get(user_id);
      ExtTimecardSummaryResultData summary_rd =
        new ExtTimecardSummaryResultData();
      int work_day = 0, overtime_day = 0, off_day = 0;
      /** 就業、残業、休出日数 */
      float work_hour = 0, overtime_hour = 0, off_hour = 0;
      /** 就業、残業、休出時間 */
      int late_coming_day = 0, early_leaving_day = 0, absent_day = 0;
      /** 遅刻、早退、欠勤 */
      int paid_holiday = 0, compensatory_holiday = 0;
      /** 有休、代休 */
      int other_day = 0, no_input = 0;
      /** その他、未入力 */
      summary_rd.initField();

      /** タイムカード設定を取得 */
      EipTExtTimecardSystem timecard_system =
        ExtTimecardUtils.getEipTExtTimecardSystemByUserId(user_id);

      /**
       * userlistにはユーザー日ごとのタイムカードのResultDataがリストで入っているため、
       * ListResultDataに代入して各日数・時間を計算させる。
       */

      for (Object obj : userlist) {
        ExtTimecardResultData rd = (ExtTimecardResultData) obj;
        ExtTimecardListResultData lrd = new ExtTimecardListResultData();
        lrd.setRd(rd);
        lrd.setTimecardSystem(timecard_system);
        String type = rd.getType().getValue();
        if (type.equals(EipTExtTimecard.TYPE_WORK)) {
          /** 出勤 */
          if (lrd.getWorkHour() != ExtTimecardListResultData.NO_DATA) {
            work_day++;
            work_hour += lrd.getWorkHourWithoutRestHour();
          }
          if (lrd.getOvertimeHour() != ExtTimecardListResultData.NO_DATA) {
            overtime_day++;
            overtime_hour += lrd.getOvertimeHourWithoutRestHour();
          }
          if (lrd.getOffHour() != ExtTimecardListResultData.NO_DATA) {
            off_day++;
            off_hour += lrd.getOffHour();
          }
          if (lrd.isLateComing()) {
            late_coming_day++;
          }
          if (lrd.isEarlyLeaving()) {
            early_leaving_day++;
          }
        } else if (type.equals(EipTExtTimecard.TYPE_ABSENT)) {
          /** 欠勤 */
          absent_day++;
        } else if (type.equals(EipTExtTimecard.TYPE_HOLIDAY)) {
          /** 有休 */
          paid_holiday++;
        } else if (type.equals(EipTExtTimecard.TYPE_COMPENSATORY)) {
          /** 代休 */
          compensatory_holiday++;
        } else if (type.equals(EipTExtTimecard.TYPE_ETC)) {
          /** その他 */
          other_day++;
        }
      }

      /** ユーザーごとの合計をSummaryResultDataに代入する */
      work_hour = ExtTimecardUtils.roundHour(work_hour);
      overtime_hour = ExtTimecardUtils.roundHour(overtime_hour);
      off_hour = ExtTimecardUtils.roundHour(off_hour);

      summary_rd.setWorkDayHour(work_day, work_hour);
      summary_rd.setOvertimeDayHour(overtime_day, overtime_hour);
      summary_rd.setOffDayHour(off_day, off_hour);
      summary_rd.setLateComingDay(late_coming_day);
      summary_rd.setEarlyLeavingDay(early_leaving_day);
      summary_rd.setAbsentDay(absent_day);
      summary_rd.setPaidHoliday(paid_holiday);
      summary_rd.setCompensatoryHoliday(compensatory_holiday);
      summary_rd.setOtherDay(other_day);
      summary_rd.setNoInput(no_input);

      list.add(summary_rd);
    }
    return list;
  }

  /**
   * グループ毎のタイムカード一覧を取得する。
   *
   * @return
   */

  public List<ExtTimecardSummaryResultData> getGroupExtTimecards() {
    List<ExtTimecardSummaryResultData> list =
      new ArrayList<ExtTimecardSummaryResultData>();
    /** 以下、ユーザーごとの処理 */
    for (int i = 0; i < userList.size(); i++) {
      ALEipUser eipUser = userList.get(i);
      int user_id = Integer.parseInt(eipUser.getUserId().getValueAsString());
      List<ExtTimecardResultData> userlist = usermap.get(user_id);
      ExtTimecardSummaryResultData summary_rd =
        new ExtTimecardSummaryResultData();
      int work_day = 0, overtime_day = 0, off_day = 0;
      /** 就業、残業、休出日数 */
      float work_hour = 0, overtime_hour = 0, off_hour = 0;
      /** 就業、残業、休出時間 */
      int late_coming_day = 0, early_leaving_day = 0, absent_day = 0;
      /** 遅刻、早退、欠勤 */
      int paid_holiday = 0, compensatory_holiday = 0;
      /** 有休、代休 */
      int other_day = 0, no_input = 0;
      /** その他、未入力 */
      summary_rd.initField();

      /** タイムカード設定を取得 */
      EipTExtTimecardSystem timecard_system =
        ExtTimecardUtils.getEipTExtTimecardSystemByUserId(user_id);

      /**
       * userlistにはユーザー日ごとのタイムカードのResultDataがリストで入っているため、
       * ListResultDataに代入して各日数・時間を計算させる。
       */
      if (userlist != null) {
        for (ExtTimecardResultData rd : userlist) {
          ExtTimecardListResultData lrd = new ExtTimecardListResultData();
          lrd.initField();
          lrd.setDate(rd.getPunchDate().getValue());
          lrd.setRd(rd);
          lrd.setTimecardSystem(timecard_system);
          String type = rd.getType().getValue();
          if (type.equals(EipTExtTimecard.TYPE_WORK)) {
            /** 出勤 */
            if (lrd.getWorkHour() != ExtTimecardListResultData.NO_DATA) {
              work_day++;
              work_hour += lrd.getWorkHourWithoutRestHour();
            }
            /** 残業 */
            if (lrd.getOvertimeHour() > 0) {
              overtime_day++;
              overtime_hour += lrd.getOvertimeHourWithoutRestHour();
            }
            /** 休出 */
            if (lrd.getOffHour() != ExtTimecardListResultData.NO_DATA) {
              off_day++;
              off_hour += lrd.getOffHour();
            }
            /** 遅刻 */
            if (lrd.isLateComing()) {
              late_coming_day++;
            }
            /** 早退 */
            if (lrd.isEarlyLeaving()) {
              early_leaving_day++;
            }
          } else if (type.equals(EipTExtTimecard.TYPE_ABSENT)) {
            /** 欠勤 */
            absent_day++;
          } else if (type.equals(EipTExtTimecard.TYPE_HOLIDAY)) {
            /** 有休 */
            paid_holiday++;
          } else if (type.equals(EipTExtTimecard.TYPE_COMPENSATORY)) {
            /** 代休 */
            compensatory_holiday++;
          } else if (type.equals(EipTExtTimecard.TYPE_ETC)) {
            /** その他 */
            other_day++;
          }
        }
      } else {
        /** 未入力数の計算のため、長さ0のArrayListをつくる */
        userlist = new ArrayList<ExtTimecardResultData>();
      }

      /** 未入力数を月日数からデータ数を引いて計算する */
      Calendar cal = Calendar.getInstance();
      cal.setTime(getViewMonth().getValue());
      no_input = cal.getActualMaximum(Calendar.DAY_OF_MONTH) - userlist.size();

      /** ユーザーごとの合計をSummaryResultDataに代入する */
      work_hour = ExtTimecardUtils.roundHour(work_hour);
      overtime_hour = ExtTimecardUtils.roundHour(overtime_hour);
      off_hour = ExtTimecardUtils.roundHour(off_hour);

      summary_rd.setUserName(eipUser.getAliasName().getValue());
      summary_rd.setSystemName(timecard_system.getSystemName());
      summary_rd.setWorkDayHour(work_day, work_hour);
      summary_rd.setOvertimeDayHour(overtime_day, overtime_hour);
      summary_rd.setOffDayHour(off_day, off_hour);
      summary_rd.setLateComingDay(late_coming_day);
      summary_rd.setEarlyLeavingDay(early_leaving_day);
      summary_rd.setAbsentDay(absent_day);
      summary_rd.setPaidHoliday(paid_holiday);
      summary_rd.setCompensatoryHoliday(compensatory_holiday);
      summary_rd.setOtherDay(other_day);
      summary_rd.setNoInput(no_input);
      summary_rd.setOwnerId(eipUser.getUserId().getValue());

      list.add(summary_rd);
    }
    return list;
  }

  /**
   * 指定した2つの日付を比較する．
   *
   * @param date1
   * @param date2
   * @param checkTime
   *          時間まで比較する場合，true．
   * @return 等しい場合，0. date1>date2の場合, 1. date1 <date2の場合, 2.
   */
  private boolean sameDay(Date date1, Date date2) {
    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();
    cal1.setTime(date1);
    cal2.setTime(date2);

    int date1Year = cal1.get(Calendar.YEAR);
    int date1Month = cal1.get(Calendar.MONTH) + 1;
    int date1Day = cal1.get(Calendar.DATE);
    int date2Year = cal2.get(Calendar.YEAR);
    int date2Month = cal2.get(Calendar.MONTH) + 1;
    int date2Day = cal2.get(Calendar.DATE);

    if (date1Year == date2Year
      && date1Month == date2Month
      && date1Day == date2Day) {
      return true;
    }
    return false;
  }

  /**
   * userListを設定する．
   *
   */
  public void setuserList(String target_group_name) {
    this.target_group_name = target_group_name;
    if (this.target_group_name != null) {
      if ((!this.target_group_name.equals(""))
        && (!this.target_group_name.equals("all"))
        && (!this.target_group_name.equals("only"))) {
        userList = ALEipUtils.getUsers(target_group_name);
      } else if (this.target_group_name.equals("all")
        || this.target_group_name.equals("only")) {
        userList = getUserList(Integer.parseInt(userid));
      } else {
        userList = ALEipUtils.getUsers("LoginUser");
      }
    } else {
      userList = ALEipUtils.getUsers("LoginUser");
    }
  }

  /**
   * 表示切り替え時に指定するグループ名
   *
   * @return
   */
  public String getTargetGroupName() {
    return target_group_name;
  }

  /**
   * 表示切り替え時に指定するユーザ ID
   *
   * @return
   */
  public String getTargetUserId() {
    return target_user_id;
  }

  /**
   * 指定グループに属するユーザの一覧を取得する．
   *
   * @param groupname
   * @return
   */
  public List<ALEipUser> getUsers() {
    if (hasAclSummaryOther) {
      return userList;
    } else {
      try {
        List<ALEipUser> users = new ArrayList<ALEipUser>();
        users.add(ALEipUtils.getALEipUser(Integer.parseInt(userid)));
        return users;
      } catch (Exception e) {
        return null;
      }
    }
  }

  /**
   * 部署の一覧を取得する．
   *
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    if (hasAclSummaryOther) {
      return ALEipManager.getInstance().getPostMap();
    } else {
      return null;
    }
  }

  /**
   * My グループの一覧を取得する．
   *
   * @return
   */
  public List<ALEipGroup> getMyGroupList() {
    if (hasAclSummaryOther) {
      return myGroupList;
    } else {
      return null;
    }
  }

  /**
   * ログインユーザの ID を取得する．
   *
   * @return
   */
  public String getUserId() {
    return userid;
  }

  /**
   *
   * @param id
   * @return
   */
  public boolean isMatch(int id1, long id2) {
    return id1 == (int) id2;
  }

  /**
   * @return
   */

  public String getNowTime() {
    return nowtime;
  }

  /**
   *
   * @param minute
   * @return
   */
  @SuppressWarnings("unused")
  private String minuteToHour(long minute) {
    BigDecimal decimal = new BigDecimal(minute / 60.0);
    DecimalFormat dformat = new DecimalFormat("##.#");
    String str =
      dformat.format(decimal.setScale(1, BigDecimal.ROUND_FLOOR).doubleValue());
    return str;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   *
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return aclPortletFeature;
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
   * 表示終了日時 (Criteria) を取得します。
   *
   * @return
   */
  public ALDateTimeField getViewEndCrt() {
    return viewEndCrt;
  }

  /**
   * 前の月を取得します。
   *
   * @return
   */
  public ALDateTimeField getPrevMonth() {
    return prevMonth;
  }

  /**
   * 次の月を取得します。
   *
   * @return
   */
  public ALDateTimeField getNextMonth() {
    return nextMonth;
  }

  /**
   * 今月を取得します。
   *
   * @return
   */
  public ALDateTimeField getCurrentMonth() {
    return currentMonth;
  }

  /**
   * 現在の月を取得します。
   *
   * @return
   */
  public ALDateTimeField getViewMonth() {
    return viewMonth;
  }

  public String getViewMonthYearMonthText() {
    return ALLocalizationUtils.getl10nFormat(
      "EXTTIMECARD_YEAR_MONTH_FORMAT",
      viewMonth.getYear().toString(),
      viewMonth.getMonth().toString());
  }

  /**
   * 今日を取得します。
   *
   * @return
   */
  public ALDateTimeField getToday() {
    return today;
  }

  public boolean hasAclUpdate() {
    return hasAclUpdate;
  }

  public boolean hasAclInsert() {
    return hasAclInsert;
  }

  public boolean hasAclXlsExport() {
    return hasAclXlsExport;
  }

  public String getMode() {
    return MODE;
  }

  public String getLoginUserName() {
    try {
      ALEipUser user = ALEipUtils.getALEipUser(Integer.valueOf(userid));
      return user.getAliasName().toString();
    } catch (Exception ignore) {
      return null;
    }
  }

  /**
   * スクリーンの名前を返します。
   *
   * @return
   */
  public String getScreenName() {
    return "ExtTimecardSummarySelectData";
  }

}

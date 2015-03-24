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
package com.aimluck.eip.timecard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
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
import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.cayenne.om.portlet.EipTTimecard;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.timecard.util.TimecardUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タイムカード検索データを管理するクラスです。
 * 
 */
public class TimecardSelectData extends
    ALAbstractSelectData<EipTTimecard, EipTTimecard> implements ALData {

  /** logger */
  private static final JetspeedLogger logger =
    JetspeedLogFactoryService.getLogger(TimecardSelectData.class.getName());

  /** <code>target_group_name</code> 表示対象の部署名 */
  private String target_group_name;

  /** <code>target_user_id</code> 表示対象のユーザ ID */
  private String target_user_id;

  /** <code>myGroupList</code> グループリスト（My グループと部署） */
  private List<ALEipGroup> myGroupList = null;

  /** <code>userList</code> 表示切り替え用のユーザリスト */
  private List<ALEipUser> userList = null;

  /** <code>userid</code> ユーザーID */
  private String userid;

  /** 集計日付 */
  private ALDateTimeField view_date;

  private String nowtime;

  /** 日付マップ */
  private Map<String, TimecardListResultData> datemap;

  /** アクセス権限の機能名 */
  private String aclPortletFeature = null;

  /** 閲覧権限の有無（集計画面） */
  private boolean hasAclSummaryOther;

  /** 閲覧権限の有無（タイムカード外部出力） */
  private boolean hasAclXlsExport;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    Calendar cal = Calendar.getInstance();
    view_date = new ALDateTimeField("yyyy-MM-dd");

    if (rundata.getParameters().containsKey("view_date_year")
      && rundata.getParameters().containsKey("view_date_month")) {

      int tmpViewDate_year =
        Integer.parseInt(rundata.getParameters().getString("view_date_year"));
      int tmpViewDate_month =
        Integer.parseInt(rundata.getParameters().getString("view_date_month"));

      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.YEAR, tmpViewDate_year);
      cal.set(Calendar.MONTH, tmpViewDate_month - 1);
      // cal.set(Calendar.DAY_OF_MONTH, 1);
      // view_date.setValue(cal.getTime());
    } else {
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
    }

    cal.set(Calendar.DAY_OF_MONTH, 1);
    view_date.setValue(cal.getTime());

    // ログインユーザの ID を設定する．
    userid = Integer.toString(ALEipUtils.getUserId(rundata));

    // My グループの一覧を取得する．
    List<ALEipGroup> myGroups = ALEipUtils.getMyGroups(rundata);
    myGroupList = new ArrayList<ALEipGroup>();
    int length = myGroups.size();
    for (int i = 0; i < length; i++) {
      myGroupList.add(myGroups.get(i));
    }

    // 指定グループや指定ユーザをセッションに設定する．
    setupLists(rundata, context);

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
    if (!hasAclSummaryOther) {
      // 他ユーザーの閲覧権限がないときには、ログインユーザーのIDに変更する。
      target_user_id = userid;
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_TIMECARD_TIMECARD_SELF;
    }

    hasAclXlsExport =
      aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        aclPortletFeature,
        ALAccessControlConstants.VALUE_ACL_EXPORT);

    datemap = new LinkedHashMap<String, TimecardListResultData>();

    super.init(action, rundata, context);
  }

  /**
   * 検索条件を設定した SelectQuery を返します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipTTimecard> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipTTimecard> query = Database.query(EipTTimecard.class);

    Expression exp1 =
      ExpressionFactory.matchExp(EipTTimecard.USER_ID_PROPERTY, Integer
        .valueOf(target_user_id));
    query.setQualifier(exp1);

    Calendar cal = Calendar.getInstance();
    Expression exp11 =
      ExpressionFactory.greaterOrEqualExp(
        EipTTimecard.WORK_DATE_PROPERTY,
        view_date.getValue());

    cal.setTime(view_date.getValue());
    cal.add(Calendar.MONTH, +1);

    ALDateTimeField view_date_add_month = new ALDateTimeField("yyyy-MM-dd");
    view_date_add_month.setValue(cal.getTime());

    Expression exp12 =
      ExpressionFactory.lessOrEqualExp(
        EipTTimecard.WORK_DATE_PROPERTY,
        view_date_add_month.getValue());
    query.andQualifier(exp11.andExp(exp12));

    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * 一覧データを取得します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  public ResultList<EipTTimecard> selectList(RunData rundata, Context context) {
    try {

      if (!"".equals(target_user_id)) {

        SelectQuery<EipTTimecard> query = getSelectQuery(rundata, context);
        buildSelectQueryForListView(query);
        query.orderAscending(EipTTimecard.WORK_DATE_PROPERTY);

        return query.getResultList();
      } else {
        return null;
      }
    } catch (Exception ex) {
      logger.error("timecard", ex);
      return null;
    }
  }

  /**
   * ResultData に値を格納して返します。（一覧データ）
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipTTimecard record) {
    try {
      Date date = record.getWorkDate();
      String checkdate = ALDateUtil.format(date, "yyyyMMdd");
      Object value = datemap.get(checkdate);
      if (value == null) {
        TimecardListResultData listrd = new TimecardListResultData();
        listrd.initField();
        listrd.setDate(date);
        datemap.put(checkdate, listrd);
      }
      TimecardListResultData listrd = datemap.get(checkdate);

      TimecardResultData rd = new TimecardResultData();
      rd.initField();
      rd.setWorkFlag(record.getWorkFlag());
      rd.setWorkDate(record.getWorkDate());
      rd.setRefixFlag(record.getCreateDate(), record.getUpdateDate());
      rd.setTimecardId(record.getTimecardId().longValue());
      rd.setReason(record.getReason());
      if (rd.getStartWorkDate() == null) {
        Date workdate = record.getWorkDate();
        workdate.setTime(0);
        rd.setStartWorkDate(workdate);
      }

      listrd.addTimecardResultData(rd);

      return null;
    } catch (Exception ex) {
      logger.error("timecard", ex);
      return null;
    }
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
   * 検索条件を設定した Criteria を返します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipTTimecard> getSelectQueryDetail(RunData rundata,
      Context context) {
    SelectQuery<EipTTimecard> query = Database.query(EipTTimecard.class);
    Expression exp =
      ExpressionFactory.matchExp(EipTTimecard.USER_ID_PROPERTY, Integer
        .valueOf(ALEipUtils.getUserId(rundata)));
    query.setQualifier(exp);

    return query;
  }

  /**
   * 詳細データを取得します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  public EipTTimecard selectDetail(RunData rundata, Context context) {
    try {
      Calendar cal = Calendar.getInstance();
      nowtime =
        cal.get(Calendar.HOUR_OF_DAY) + "時" + cal.get(Calendar.MINUTE) + "分";

      SelectQuery<EipTTimecard> query = getSelectQueryDetail(rundata, context);
      query.orderDesending(EipTTimecard.WORK_DATE_PROPERTY);

      List<EipTTimecard> list = query.fetchList();
      if (list != null && list.size() > 0) {
        return list.get(0);
      } else {
        return null;
      }
    } catch (Exception ex) {
      logger.error("timecard", ex);
      return null;
    }
  }

  /**
   * ResultData に値を格納して返します。（詳細データ）
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTTimecard record) {
    try {
      TimecardResultData rd = new TimecardResultData();
      rd.initField();
      rd.setWorkFlag(record.getWorkFlag());
      rd.setWorkDate(record.getWorkDate());
      return rd;
    } catch (Exception ex) {
      logger.error("timecard", ex);
      return null;
    }
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
    if ((target_group_name != null)
      && (!target_group_name.equals(""))
      && (!target_group_name.equals("all"))) {
      userList = ALEipUtils.getUsers(target_group_name);
    } else {
      userList = ALEipUtils.getUsers("LoginUser");
    }

    if (userList == null || userList.size() == 0) {
      target_user_id = "";
      ALEipUtils.removeTemp(rundata, context, TimecardUtils.TARGET_USER_ID);
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
      idParam =
        rundata.getParameters().getString(TimecardUtils.TARGET_GROUP_NAME);
    }
    target_group_name =
      ALEipUtils.getTemp(rundata, context, TimecardUtils.TARGET_GROUP_NAME);

    if (idParam == null && target_group_name == null) {
      ALEipUtils.setTemp(
        rundata,
        context,
        TimecardUtils.TARGET_GROUP_NAME,
        "all");
      target_group_name = "all";
    } else if (idParam != null) {
      ALEipUtils.setTemp(
        rundata,
        context,
        TimecardUtils.TARGET_GROUP_NAME,
        idParam);
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
      idParam = rundata.getParameters().getString(TimecardUtils.TARGET_USER_ID);
    }
    target_user_id =
      ALEipUtils.getTemp(rundata, context, TimecardUtils.TARGET_USER_ID);

    if (idParam == null && (target_user_id == null)) {
      // ログインユーザのスケジュールを表示するため，ログイン ID を設定する．
      ALEipUtils
        .setTemp(rundata, context, TimecardUtils.TARGET_USER_ID, userid);
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
            ALEipUtils.setTemp(
              rundata,
              context,
              TimecardUtils.TARGET_USER_ID,
              userid);
            target_user_id = userid;
            found = true;
            break;
          }
        }
        if (!found) {
          eipUser = userList.get(0);
          String userId = eipUser.getUserId().getValueAsString();
          ALEipUtils.setTemp(
            rundata,
            context,
            TimecardUtils.TARGET_USER_ID,
            userId);
          target_user_id = userId;
        }
      } else {
        // ユーザで表示を切り替えた場合，指定したユーザの ID を設定する．
        ALEipUtils.setTemp(
          rundata,
          context,
          TimecardUtils.TARGET_USER_ID,
          idParam);
        target_user_id = idParam;
      }
    }
    return target_user_id;
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
   * @return
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    return map;
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
  public ALDateTimeField getViewDate() {
    return view_date;
  }

  public String getNowTime() {
    return nowtime;
  }

  /**
   * 表示する年の最大値を返します。
   * 
   * @return
   */
  public int getEndYear() {
    Calendar calendar = Calendar.getInstance();
    return calendar.get(Calendar.YEAR) + 1;
  }

  public List<String> getDateListKeys() {
    try {
      List<String> list = new ArrayList<String>();
      Set<String> set = datemap.keySet();
      Iterator<String> iter = set.iterator();
      while (iter.hasNext()) {
        list.add(iter.next());
      }

      if (list.size() > 1) {
        for (int i = 0; i < list.size() - 1; i++) {
          TimecardListResultData listrd1 = datemap.get(list.get(i));
          TimecardListResultData listrd2 = datemap.get(list.get(i + 1));
          int listrd1_size = listrd1.getList().size();
          if (listrd1_size > 0) {
            TimecardResultData listrd1_lastrd =
              listrd1.getList().get(listrd1_size - 1);

            TimecardResultData listrd2_firstrd = listrd2.getList().get(0);
            if (TimecardUtils.WORK_FLG_OFF.equals(listrd2_firstrd
              .getWorkFlag()
              .getValue())
              && TimecardUtils.WORK_FLG_ON.equals(listrd1_lastrd
                .getWorkFlag()
                .getValue())
              && !sameDay(
                listrd1_lastrd.getWorkDate().getValue(),
                listrd2_firstrd.getWorkDate().getValue())) {

              Date d = listrd2_firstrd.getWorkDate().getValue();
              Calendar cal = Calendar.getInstance();
              cal.setTime(d);
              cal.set(Calendar.HOUR_OF_DAY, 0);
              cal.set(Calendar.MINUTE, 0);

              TimecardResultData dummyrd = new TimecardResultData();
              dummyrd.initField();
              dummyrd.setWorkFlag(TimecardUtils.WORK_FLG_DUMMY);
              dummyrd.setWorkDate(cal.getTime());

              listrd1.addTimecardResultData(dummyrd);
            }
          }
        }
      }
      return list;
    } catch (Exception e) {
      logger.error(e);
      return null;
    }
  }

  public TimecardListResultData getDateListValue(String date_str) {
    return datemap.get(date_str);
  }

  /**
   * アクセス権限チェック用メソッド。 アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return aclPortletFeature;
  }

  public boolean hasAclXlsExport() {
    return hasAclXlsExport;
  }
}

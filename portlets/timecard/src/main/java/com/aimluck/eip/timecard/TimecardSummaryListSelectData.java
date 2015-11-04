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
package com.aimluck.eip.timecard;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
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
 * タイムカード集計の一覧を処理するクラスです。
 *
 *
 */

public class TimecardSummaryListSelectData extends
    ALAbstractSelectData<EipTTimecard, EipTTimecard> implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(TimecardSummaryListSelectData.class.getName());

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

  /** <code>TARGET_GROUP_NAME</code> グループによる表示切り替え用変数の識別子 */
  private final String TARGET_GROUP_NAME = "target_group_name";

  /** <code>TARGET_USER_ID</code> ユーザによる表示切り替え用変数の識別子 */
  private final String TARGET_USER_ID = "target_user_id";

  /** 集計日付 */
  private ALDateTimeField view_date;

  private String nowtime;

  /** 日付マップ */
  private Map<String, TimecardSummaryResultData> datemap;

  private ALNumberField shugyoNissu;

  private ALNumberField shugyoJikan;

  private ALNumberField shugyoJikannaiNissu;

  private ALNumberField shugyoJikannaiJikan;

  private ALNumberField shugyoJikannai1Nissu;

  private ALNumberField shugyoJikannai1Jikan;

  private ALNumberField shugyoJikannai2Nissu;

  private ALNumberField shugyoJikannai2Jikan;

  private ALNumberField zangyoNissu;

  private ALNumberField zangyoJikan;

  private ALNumberField zangyoJikannaiNissu;

  private ALNumberField zangyoJikannaiJikan;

  private ALNumberField zangyoJikannai1Nissu;

  private ALNumberField zangyoJikannai1Jikan;

  private ALNumberField zangyoJikannai2Nissu;

  private ALNumberField zangyoJikannai2Jikan;

  private ALNumberField chikoku;

  private ALNumberField sotai;

  /** アクセス権限の機能名 */
  private String aclPortletFeature = null;

  /** 閲覧権限の有無 */
  private boolean hasAclSummaryOther;

  /**
   *
   */
  @Override
  public void initField() {

    shugyoNissu = new ALNumberField(0);
    shugyoJikan = new ALNumberField(0);
    shugyoJikannaiNissu = new ALNumberField(0);
    shugyoJikannaiJikan = new ALNumberField(0);
    shugyoJikannai1Nissu = new ALNumberField(0);
    shugyoJikannai1Jikan = new ALNumberField(0);
    shugyoJikannai2Nissu = new ALNumberField(0);
    shugyoJikannai2Jikan = new ALNumberField(0);

    zangyoNissu = new ALNumberField(0);
    zangyoJikan = new ALNumberField(0);
    zangyoJikannaiNissu = new ALNumberField(0);
    zangyoJikannaiJikan = new ALNumberField(0);
    zangyoJikannai1Nissu = new ALNumberField(0);
    zangyoJikannai1Jikan = new ALNumberField(0);
    zangyoJikannai2Nissu = new ALNumberField(0);
    zangyoJikannai2Jikan = new ALNumberField(0);

    chikoku = new ALNumberField(0);
    sotai = new ALNumberField(0);
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

    try {
      // スケジュールを表示するユーザ ID をセッションに設定する．
      String userFilter = ALEipUtils.getTemp(rundata, context, TARGET_USER_ID);
      if (userFilter == null || userFilter.equals("")) {
        VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
        userFilter = portlet.getPortletConfig().getInitParameter("p3a-user");
      }

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
      logger.error("timecard", ex);
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
    if (!hasAclSummaryOther) {
      // 他ユーザーの閲覧権限がないときには、ログインユーザーのIDに変更する。
      target_user_id = userid;
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_TIMECARD_TIMECARD_SELF;
    }

    datemap = new LinkedHashMap<String, TimecardSummaryResultData>();
    setupLists(rundata, context);
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
  protected ResultList<EipTTimecard> selectList(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    try {
      // 指定グループや指定ユーザをセッションに設定する．
      setupLists(rundata, context);

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
   *
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected EipTTimecard selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   *
   * @param record
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultData(EipTTimecard record)
      throws ALPageNotFoundException, ALDBErrorException {
    try {
      Date date = record.getWorkDate();
      String checkdate = ALDateUtil.format(date, "yyyyMMdd");
      Object value = datemap.get(checkdate);
      if (value == null) {
        TimecardSummaryResultData listrd = new TimecardSummaryResultData();
        listrd.initField();
        listrd.setDate(date);
        datemap.put(checkdate, listrd);
      }
      TimecardSummaryResultData listrd = datemap.get(checkdate);

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
   *
   * @param obj
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultDataDetail(EipTTimecard obj)
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
      ALEipUtils.setTemp(rundata, context, TARGET_GROUP_NAME, "all");
      target_group_name = "all";
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
   * @return
   */
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
          TimecardSummaryResultData listrd1 = datemap.get(list.get(i));
          TimecardSummaryResultData listrd2 = datemap.get(list.get(i + 1));
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
      logger.error("[TimecardSummaryListSelectData]", e);
      return null;
    }
  }

  /**
   * @param date_str
   * @return
   */
  public TimecardSummaryResultData getDateListValue(String date_str) {
    return datemap.get(date_str);
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
   *
   *
   */
  public void calc() {
    if (datemap != null) {
      int shugyoNissu_temp = 0;
      int shugyoJikan_temp = 0;
      int shugyoJikannaiNissu_temp = 0;
      int shugyoJikannaiJikan_temp = 0;
      int shugyoJikannai1Nissu_temp = 0;
      int shugyoJikannai1Jikan_temp = 0;
      int shugyoJikannai2Nissu_temp = 0;
      int shugyoJikannai2Jikan_temp = 0;

      int zangyoNissu_temp = 0;
      int zangyoJikan_temp = 0;
      int zangyoJikannaiNissu_temp = 0;
      int zangyoJikannaiJikan_temp = 0;
      int zangyoJikannai1Nissu_temp = 0;
      int zangyoJikannai1Jikan_temp = 0;
      int zangyoJikannai2Nissu_temp = 0;
      int zangyoJikannai2Jikan_temp = 0;

      int chikoku_temp = 0;
      int sotai_temp = 0;

      for (Object element : datemap.values()) {
        TimecardSummaryResultData rd = (TimecardSummaryResultData) element;
        rd.calc();

        //
        if (rd.getShugyo().getValue() > 0) {
          shugyoNissu_temp++;
          shugyoJikan_temp += rd.getShugyo().getValue();
        }
        if (rd.getJikannai().getValue() > 0) {
          shugyoJikannaiNissu_temp++;
          shugyoJikannaiJikan_temp += rd.getJikannai().getValue();
        }
        if (rd.getJikannai1().getValue() > 0) {
          shugyoJikannai1Nissu_temp++;
          shugyoJikannai1Jikan_temp += rd.getJikannai1().getValue();
        }
        if (rd.getJikannai2().getValue() > 0) {
          shugyoJikannai2Nissu_temp++;
          shugyoJikannai2Jikan_temp += rd.getJikannai2().getValue();
        }

        if (rd.getZangyo().getValue() > 0) {
          zangyoNissu_temp++;
          zangyoJikan_temp += rd.getZangyo().getValue();
        }
        if (rd.getZangyo1().getValue() > 0) {
          zangyoJikannai1Nissu_temp++;
          zangyoJikannai1Jikan_temp += rd.getZangyo1().getValue();
        }
        if (rd.getZangyo2().getValue() > 0) {
          zangyoJikannai2Nissu_temp++;
          zangyoJikannai2Jikan_temp += rd.getZangyo2().getValue();
        }

        if ("○".equals(rd.getChikoku().getValue())) {
          chikoku_temp++;
        }
        if ("○".equals(rd.getSotai().getValue())) {
          sotai_temp++;
        }
      }

      shugyoNissu.setValue(shugyoNissu_temp);
      shugyoJikan.setValue(shugyoJikan_temp);
      shugyoJikannaiNissu.setValue(shugyoJikannaiNissu_temp);
      shugyoJikannaiJikan.setValue(shugyoJikannaiJikan_temp);
      shugyoJikannai1Nissu.setValue(shugyoJikannai1Nissu_temp);
      shugyoJikannai1Jikan.setValue(shugyoJikannai1Jikan_temp);
      shugyoJikannai2Nissu.setValue(shugyoJikannai2Nissu_temp);
      shugyoJikannai2Jikan.setValue(shugyoJikannai2Jikan_temp);

      zangyoNissu.setValue(zangyoNissu_temp);
      zangyoJikan.setValue(zangyoJikan_temp);
      zangyoJikannaiNissu.setValue(zangyoJikannaiNissu_temp);
      zangyoJikannaiJikan.setValue(zangyoJikannaiJikan_temp);
      zangyoJikannai1Nissu.setValue(zangyoJikannai1Nissu_temp);
      zangyoJikannai1Jikan.setValue(zangyoJikannai1Jikan_temp);
      zangyoJikannai2Nissu.setValue(zangyoJikannai2Nissu_temp);
      zangyoJikannai2Jikan.setValue(zangyoJikannai2Jikan_temp);

      chikoku.setValue(chikoku_temp);
      sotai.setValue(sotai_temp);
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
  public ALDateTimeField getViewDate() {
    return view_date;
  }

  public String getNowTime() {
    return nowtime;
  }

  public ALNumberField getShugyoNissu() {
    return shugyoNissu;
  }

  public ALNumberField getShugyoJikan() {
    return shugyoJikan;
  }

  public ALStringField getShugyoJikanStr() {
    return new ALStringField(minuteToHour(shugyoJikan.getValue()));
  }

  public ALNumberField getShugyoJikannaiNissu() {
    return shugyoJikannaiNissu;
  }

  public ALNumberField getShugyoJikannaiJikan() {
    return shugyoJikannaiJikan;
  }

  public ALStringField getShugyoJikannaiJikanStr() {
    return new ALStringField(minuteToHour(shugyoJikannaiJikan.getValue()));
  }

  public ALNumberField getShugyoJikannai1Nissu() {
    return shugyoJikannai1Nissu;
  }

  public ALNumberField getShugyoJikannai1Jikan() {
    return shugyoJikannai1Jikan;
  }

  public ALStringField getShugyoJikannai1JikanStr() {
    return new ALStringField(minuteToHour(shugyoJikannai1Jikan.getValue()));
  }

  public ALNumberField getShugyoJikannai2Nissu() {
    return shugyoJikannai2Nissu;
  }

  public ALNumberField getShugyoJikannai2Jikan() {
    return shugyoJikannai2Jikan;
  }

  public ALStringField getShugyoJikannai2JikanStr() {
    return new ALStringField(minuteToHour(shugyoJikannai2Jikan.getValue()));
  }

  public ALNumberField getZangyoNissu() {
    return zangyoNissu;
  }

  public ALNumberField getZangyoJikan() {
    return zangyoJikan;
  }

  public ALStringField getZangyoJikanStr() {
    return new ALStringField(minuteToHour(zangyoJikan.getValue()));
  }

  public ALNumberField getZangyoJikannaiNissu() {
    return zangyoJikannaiNissu;
  }

  public ALNumberField getZangyoJikannaiJikan() {
    return zangyoJikannaiJikan;
  }

  public ALStringField getZangyoJikannaiJikanStr() {
    return new ALStringField(minuteToHour(zangyoJikannaiJikan.getValue()));
  }

  public ALNumberField getZangyoJikannai1Nissu() {
    return zangyoJikannai1Nissu;
  }

  public ALNumberField getZangyoJikannai1Jikan() {
    return zangyoJikannai1Jikan;
  }

  public ALStringField getZangyoJikannai1JikanStr() {
    return new ALStringField(minuteToHour(zangyoJikannai1Jikan.getValue()));
  }

  public ALNumberField getZangyoJikannai2Nissu() {
    return zangyoJikannai2Nissu;
  }

  public ALNumberField getZangyoJikannai2Jikan() {
    return zangyoJikannai2Jikan;
  }

  public ALStringField getZangyoJikannai2JikanStr() {
    return new ALStringField(minuteToHour(zangyoJikannai2Jikan.getValue()));
  }

  public ALNumberField getChikoku() {
    return chikoku;
  }

  public ALNumberField getSotai() {
    return sotai;
  }

  /**
   *
   * @param minute
   * @return
   */
  private String minuteToHour(long minute) {
    BigDecimal decimal = new BigDecimal(minute / 60.0);
    DecimalFormat dformat = new DecimalFormat("##.#");
    String str =
      dformat.format(decimal.setScale(1, BigDecimal.ROUND_FLOOR).doubleValue());
    return str;
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

  /**
   * アクセス権限チェック用メソッド。 アクセス権限の機能名を返します。
   *
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return aclPortletFeature;
  }
}

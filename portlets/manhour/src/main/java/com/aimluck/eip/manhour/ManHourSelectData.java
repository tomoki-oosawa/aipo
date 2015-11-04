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
package com.aimluck.eip.manhour;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.category.beans.CommonCategoryLiteBean;
import com.aimluck.eip.category.util.CommonCategoryUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTCommonCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.manhour.util.ManHourUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.util.ALEipUtils;

/**
 * プロジェクト管理の検索データを管理するためのクラスです。 <br />
 */
public class ManHourSelectData extends
    ALAbstractSelectData<EipTScheduleMap, ManHourResultData> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ManHourSelectData.class.getName());

  private String target_group_name;

  /** 表示対象のユーザ ID */
  private String target_user_id;

  /** グループリスト（My グループと部署） */
  private List<ALEipGroup> myGroupList = null;

  /** ポートレット Schedule への URL */
  private String scheduleUrl;

  /** <code>userid</code> ユーザーID */
  private String userid;

  /** <code>category_id</code> カテゴリID */
  private String category_id;

  /** 共有カテゴリリスト */
  private List<CommonCategoryLiteBean> categoryList = null;

  /** 集計日付 */
  private ALDateTimeField view_date;

  /** スケジュール一覧 */
  private List<ManHourResultData> scheduleList;

  /** 工数合計 */
  private double totalManHourMinPast;

  private double totalManHourMinPlan;

  private boolean is_normal;

  /** アクセス権限の機能名 */
  private String aclPortletFeature = null;

  /** 閲覧権限の有無 */
  private boolean hasAclSummaryOther;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, ALEipUtils
        .getPortlet(rundata, context)
        .getPortletConfig()
        .getInitParameter("p1a-sort"));
    }

    Calendar cal = Calendar.getInstance();
    view_date = new ALDateTimeField("yyyy-MM-dd");

    int view_date_year = ManHourUtils.getViewDateYear(rundata, context);
    int view_date_month = ManHourUtils.getViewDateMonth(rundata, context);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.YEAR, view_date_year);
    cal.set(Calendar.MONTH, view_date_month - 1);
    cal.set(Calendar.DAY_OF_MONTH, 1);
    view_date.setValue(cal.getTime());

    // ログインユーザの ID を設定する．
    userid = Integer.toString(ALEipUtils.getUserId(rundata));

    target_user_id = ALEipUtils.getTemp(rundata, context, "target_user_id");
    if (target_user_id == null || target_user_id.equals("")) {
      ALEipUtils.setTemp(rundata, context, "target_user_id", userid);
    }

    target_group_name =
      ALEipUtils.getParameter(rundata, context, "target_group_name");
    target_user_id =
      ALEipUtils.getParameter(rundata, context, "target_user_id");
    category_id = ALEipUtils.getParameter(rundata, context, "category_id");

    List<ALEipGroup> myGroups = ALEipUtils.getMyGroups(rundata);
    myGroupList = new ArrayList<ALEipGroup>();
    int length = myGroups.size();
    for (int i = 0; i < length; i++) {
      myGroupList.add(myGroups.get(i));
    }

    scheduleList = new ArrayList<ManHourResultData>();

    categoryList = CommonCategoryUtils.getCommonCategoryLiteBeans(rundata);

    // アクセス権
    if (target_user_id.equals(userid)) {
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_MANHOUR_SUMMARY_SELF;
    } else {
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_MANHOUR_SUMMARY_OTHER;
    }
    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();
    hasAclSummaryOther =
      aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        ALAccessControlConstants.POERTLET_FEATURE_MANHOUR_SUMMARY_OTHER,
        ALAccessControlConstants.VALUE_ACL_LIST);
    if (!hasAclSummaryOther) {
      // 他ユーザーの閲覧権限がないときには、ログインユーザーのIDに変更する。
      target_user_id = userid;
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_MANHOUR_SUMMARY_SELF;
    }

    super.init(action, rundata, context);
  }

  /**
   * 
   * @return
   */
  public boolean isNormal() {
    return is_normal;

  }

  /**
   * 
   * @param bool
   */
  public void setNormal(boolean bool) {
    is_normal = bool;
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
      List<EipTScheduleMap> aList = selectList(rundata, context);
      if (aList != null) {
        int size = aList.size();
        for (int i = 0; i < size; i++) {
          getResultData(aList.get(i));
        }
        cleanupDummySchedule(scheduleList);
        scheduleList = getScheduleList();
      }
      action.setResultData(this);
      action.putData(rundata, context);
      ALEipUtils.removeTemp(rundata, context, ALEipConstants.ENTITY_ID);
      return (scheduleList != null);
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
   * ALAbstractSelectData で定義されている getList() をオーバーライドする。<br />
   * 返り値は、ALAbstractSelectData の変数 list ではなく、 <br />
   * このクラスのグローバル変数 scheduleList を返す。
   * 
   */
  @Override
  public List<Object> getList() {
    return Arrays.asList(scheduleList.toArray());
  }

  /**
   *
   */
  private List<ManHourResultData> getScheduleList() {
    ManHourResultData[] obj = new ManHourResultData[scheduleList.size()];
    int size = scheduleList.size();
    for (int i = 0; i < size; i++) {
      obj[i] = scheduleList.get(i);
    }
    Arrays.sort(obj, new Comparator<ManHourResultData>() {
      @Override
      public int compare(ManHourResultData o1, ManHourResultData o2) {
        String sort = getCurrentSort();
        String sort_type = getCurrentSortType();
        int result = 0;
        if ("category".equals(sort)) {
          if ("asc".equals(sort_type)) {
            result =
              (o1).getCategoryName().getValue().compareTo(
                (o2).getCategoryName().getValue());
          } else {
            result =
              (o2).getCategoryName().getValue().compareTo(
                (o1).getCategoryName().getValue());

          }
        } else if ("user_name".equals(sort)) {
          if ("asc".equals(sort_type)) {
            result =
              (o1).getUser().getValue().compareTo((o2).getUser().getValue());
          } else {
            result =
              (o2).getUser().getValue().compareTo((o1).getUser().getValue());

          }
        } else if ("schedule".equals(sort)) {
          if ("asc".equals(sort_type)) {
            result =
              (o1).getName().getValue().compareTo((o2).getName().getValue());
          } else {
            result =
              (o2).getName().getValue().compareTo((o1).getName().getValue());

          }
        } else if ("time".equals(sort)) {
          if ("asc".equals(sort_type)) {
            result =
              (o1).getStartDate().getValue().compareTo(
                (o2).getStartDate().getValue());
          } else {
            result =
              (o2).getStartDate().getValue().compareTo(
                (o1).getStartDate().getValue());

          }
        } else if ("manhour".equals(sort)) {
          if ("asc".equals(sort_type)) {
            result =
              Double.valueOf((o1).getManHourMin()).compareTo(
                Double.valueOf((o2).getManHourMin()));
          } else {
            result =
              Double.valueOf((o2).getManHourMin()).compareTo(
                Double.valueOf((o1).getManHourMin()));

          }
        }
        return result;
      }
    });
    List<ManHourResultData> list = new ArrayList<ManHourResultData>();
    list.addAll(Arrays.asList(obj));
    setPageParam(list.size());

    return buildPaginatedResultData(list);
  }

  /**
   * ページング結果のリストを取得します。
   * 
   * @param records
   *          検索結果
   */
  protected List<ManHourResultData> buildPaginatedResultData(
      List<ManHourResultData> records) {
    List<ManHourResultData> list = new ArrayList<ManHourResultData>();

    setPageParam(records.size());

    int start = getStart();
    int rows_num = getRowsNum();
    int size = records.size();
    int end = (start + rows_num <= size) ? start + rows_num : size;
    for (int i = start; i < end; i++) {
      list.add(records.get(i));
    }

    return list;
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
      SelectQuery<EipTScheduleMap> query = getSelectQuery(rundata, context);
      // buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);
      List<EipTScheduleMap> list = query.fetchList();
      return new ResultList<EipTScheduleMap>(sortByDummySchedule(list));
    } catch (Exception ex) {
      logger.error("manhour", ex);
      return null;
    }
  }

  public static List<EipTScheduleMap> sortByDummySchedule(
      List<EipTScheduleMap> list) {
    // 重複スケジュールの表示調節のために，
    // ダミースケジュールをリストの始めに寄せる．
    List<EipTScheduleMap> dummyList = new ArrayList<EipTScheduleMap>();
    List<EipTScheduleMap> normalList = new ArrayList<EipTScheduleMap>();
    for (EipTScheduleMap scheduleMap : list) {
      if ("D".equals(scheduleMap.getStatus())) {
        dummyList.add(scheduleMap);
      } else {
        normalList.add(scheduleMap);
      }
    }

    List<EipTScheduleMap> newList = new ArrayList<EipTScheduleMap>();
    newList.addAll(dummyList);
    newList.addAll(normalList);
    return newList;
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipTScheduleMap> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipTScheduleMap> query = Database.query(EipTScheduleMap.class);

    // 終了日時
    Expression exp11 =
      ExpressionFactory.greaterOrEqualExp(
        EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
          + "."
          + EipTSchedule.END_DATE_PROPERTY,
        view_date.getValue());

    Calendar cal = Calendar.getInstance();
    cal.setTime(view_date.getValue());
    cal.add(Calendar.MONTH, +1);

    ALDateTimeField view_date_add_month = new ALDateTimeField("yyyy-MM-dd");
    view_date_add_month.setValue(cal.getTime());

    // 開始日時
    Expression exp12 =
      ExpressionFactory.lessOrEqualExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.START_DATE_PROPERTY, view_date_add_month.getValue());

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

    query.setQualifier((exp11.andExp(exp12).andExp(exp14)).orExp(exp13
      .andExp(exp14)));

    Expression exp15 =
      ExpressionFactory.matchExp(EipTScheduleMap.STATUS_PROPERTY, "D");

    // 共有カテゴリ
    if ((category_id != null)
      && (!category_id.equals("") && !is_normal)
      && (!category_id.equals("all"))) {
      Expression exp =
        ExpressionFactory.matchExp(
          EipTScheduleMap.COMMON_CATEGORY_ID_PROPERTY,
          Integer.valueOf(category_id));

      query.andQualifier(exp15.orExp(exp));

    } else {
      Expression exp =
        ExpressionFactory.noMatchExp(
          EipTScheduleMap.COMMON_CATEGORY_ID_PROPERTY,
          Integer.valueOf(1));

      query.andQualifier(exp15.orExp(exp));
    }

    // ユーザー
    if (is_normal) {
      Expression exp1 =
        ExpressionFactory.matchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
          .valueOf(userid));
      query.andQualifier(exp1);
    } else if ((target_user_id != null)
      && (!target_user_id.equals(""))
      && (!target_user_id.equals("all"))) {
      Expression exp1 =
        ExpressionFactory.matchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
          .valueOf(target_user_id));
      query.andQualifier(exp1);
    }

    // グループ
    if ((target_group_name != null)
      && (!target_group_name.equals(""))
      && (!target_group_name.equals("all"))
      && !is_normal) {
      // 選択したグループを指定する．
      Expression exp2 =
        ExpressionFactory.matchExp(
          EipTScheduleMap.TURBINE_USER_GROUP_ROLE_PROPERTY
            + "."
            + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY
            + "."
            + TurbineGroup.GROUP_NAME_PROPERTY,
          target_group_name);
      query.andQualifier(exp2);

    }

    return buildSelectQueryForFilter(query, rundata, context);
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
  protected ManHourResultData selectDetail(RunData rundata, Context context)
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
  protected Object getResultData(EipTScheduleMap record)
      throws ALPageNotFoundException, ALDBErrorException {
    try {

      EipTSchedule schedule = record.getEipTSchedule();
      EipTCommonCategory category = record.getEipTCommonCategory();

      // 登録ユーザ名の設定
      ALEipUser createdUser =
        ALEipUtils.getALEipUser(record.getUserId().intValue());

      String createdUserName = createdUser.getAliasName().toString();

      ManHourResultData rd = new ManHourResultData();
      rd.initField();

      rd.setCategoryId(category.getCommonCategoryId().longValue());
      rd.setCategoryName(category.getName());
      rd.setUser(createdUserName);
      rd.setUserId(record.getUserId().longValue());

      // スケジュール
      // スケジュールが棄却されている場合は表示しない
      if ("R".equals(record.getStatus())) {
        return rd;
      }
      int userid_int = Integer.parseInt(userid);

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

      // boolean is_member = orm_map.count(new Criteria().add(
      // EipTScheduleMapConstants.SCHEDULE_ID, schedule.getScheduleId()).add(
      // EipTScheduleMapConstants.USER_ID, userid)) != 0;

      // Dummy スケジュールではない
      // 完全に隠す
      // 自ユーザー以外
      // 共有メンバーではない
      // オーナーではない
      if ((!"D".equals(record.getStatus()))
        && "P".equals(schedule.getPublicFlag())
        && (userid_int != record.getUserId().intValue())
        && (userid_int != schedule.getOwnerId().intValue())
        && !is_member) {
        return rd;
      }

      if ("C".equals(schedule.getPublicFlag())
        && (userid_int != record.getUserId().intValue())
        && (userid_int != schedule.getOwnerId().intValue())
        && !is_member) {
        // 名前
        rd.setName("非公開");
        // 仮スケジュールかどうか
        rd.setTmpreserve(false);
      } else {
        // 名前
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
      rd.setLoginuser(record.getUserId().intValue() == userid_int);
      // オーナーかどうか
      rd.setOwner(schedule.getOwnerId().intValue() == userid_int);
      // 共有メンバーかどうか
      rd.setMember(is_member);
      // 繰り返しパターン
      rd.setPattern(schedule.getRepeatPattern());

      Calendar cal = Calendar.getInstance();
      cal.setTime(view_date.getValue());

      Calendar cal2 = Calendar.getInstance();
      cal2.setTime(view_date.getValue());
      cal2.add(Calendar.MONTH, +1);
      ALDateTimeField field;
      if (!rd.getPattern().equals("N")) {
        while (cal.before(cal2)) {

          field = new ALDateTimeField("yyyy-MM-dd");
          field.setValue(cal.getTime());
          if (ScheduleUtils.isView(field, rd.getPattern(), rd
            .getStartDate()
            .getValue(), rd.getEndDate().getValue())) {
            Calendar temp = Calendar.getInstance();
            temp.setTime(field.getValue());
            temp.set(Calendar.HOUR, Integer.parseInt(rd
              .getStartDate()
              .getHour()));
            temp.set(Calendar.MINUTE, Integer.parseInt(rd
              .getStartDate()
              .getMinute()));
            temp.set(Calendar.SECOND, 0);
            temp.set(Calendar.MILLISECOND, 0);
            Calendar temp2 = Calendar.getInstance();
            temp2.setTime(field.getValue());
            temp2.set(Calendar.HOUR, Integer
              .parseInt(rd.getEndDate().getHour()));
            temp2.set(Calendar.MINUTE, Integer.parseInt(rd
              .getEndDate()
              .getMinute()));
            temp2.set(Calendar.SECOND, 0);
            temp2.set(Calendar.MILLISECOND, 0);
            ManHourResultData rd3 = new ManHourResultData();

            rd3.initField();
            rd3.setCategoryId(rd.getCategoryId().getValue());
            rd3.setCategoryName(rd.getCategoryName().getValue());
            rd3.setUser(rd.getUser().toString());
            rd3.setUserId(rd.getUserId().getValue());

            rd3.setScheduleId((int) rd.getScheduleId().getValue());
            rd3.setParentId((int) rd.getParentId().getValue());
            rd3.setName(rd.getName().getValue());
            // 開始日を設定し直す
            rd3.setStartDate(temp.getTime());
            // 終了日を設定し直す
            rd3.setEndDate(temp2.getTime());
            rd3.setTmpreserve(rd.isTmpreserve());
            rd3.setPublic(rd.isPublic());
            rd3.setHidden(rd.isHidden());
            rd3.setDummy(rd.isDummy());
            rd3.setLoginuser(rd.isLoginuser());
            rd3.setOwner(rd.isOwner());
            rd3.setMember(rd.isMember());
            // 繰り返しはON
            rd3.setRepeat(true);
            addResultData(rd3);

          }
          cal.add(Calendar.DATE, +1);
        }
      } else {
        addResultData(rd);
      }
      return rd;
    } catch (Exception ex) {
      logger.error("manhour", ex);
      return null;
    }
  }

  private void addResultData(ManHourResultData rd) {
    int size = scheduleList.size();
    int position = 0;
    boolean canAdd = true;
    boolean repeat_del = false;
    boolean pos_bool = false;
    for (int i = 0; i < size; i++) {
      repeat_del = false;
      ManHourResultData rd2 = scheduleList.get(i);
      if (rd.isRepeat()
        && rd2.isDummy()
        && rd.getScheduleId().getValue() == rd2.getParentId().getValue()
        && ScheduleUtils.equalsToDate(rd.getStartDate().getValue(), rd2
          .getStartDate()
          .getValue(), false)) {
        // [繰り返しスケジュール] 親の ID を検索
        canAdd = false;
        break;
      }
      if (rd2.isRepeat()
        && rd.isDummy()
        && rd2.getScheduleId().getValue() == rd.getParentId().getValue()
        && ScheduleUtils.equalsToDate(rd.getStartDate().getValue(), rd2
          .getStartDate()
          .getValue(), false)) {
        // [繰り返しスケジュール] 親の ID を検索
        scheduleList.remove(rd2);
        canAdd = true;
        repeat_del = true;
      }

      // スケジュールを挿入する位置を捕捉する。
      // 開始日時の昇順
      if (!pos_bool
        && rd.getStartDate().getValue().before(rd2.getStartDate().getValue())) {
        position = i;
        pos_bool = true;
      }

      if (!repeat_del) {
        // 繰り返しスケジュールの変更／削除が無い場合

        if (!rd.isDummy() && !rd2.isDummy()) {
          // ダミースケジュールではないときに
          // 重複スケジュールを検出する。
          // 時間が重なっている場合重複スケジュールとする。
          if ((rd.getStartDate().getValue().before(
            rd2.getStartDate().getValue()) && rd2
            .getStartDate()
            .getValue()
            .before(rd.getEndDate().getValue()))
            || (rd2.getStartDate().getValue().before(
              rd.getStartDate().getValue()) && rd
              .getStartDate()
              .getValue()
              .before(rd2.getEndDate().getValue()))
            || (rd
              .getStartDate()
              .getValue()
              .before(rd2.getEndDate().getValue()) && rd2
              .getEndDate()
              .getValue()
              .before(rd.getEndDate().getValue()))
            || (rd2
              .getStartDate()
              .getValue()
              .before(rd.getEndDate().getValue()) && rd
              .getEndDate()
              .getValue()
              .before(rd2.getEndDate().getValue()))
          // || (rd.getEndDate().getValue()
          // .equals(rd2.getEndDate().getValue()) && rd.getStartDate()
          // .getValue().equals(rd2.getStartDate().getValue()))
          ) {
            // rd2.setDuplicate(true);
            rd.setDuplicate(true);
            if (rd.getUserId().getValue() == rd2.getUserId().getValue()) {
              rd2.setDuplicate(true);
            }
          }
        }
      }
    }
    if (canAdd) {
      if (pos_bool) {
        scheduleList.add(position, rd);
        if (rd.isPast()) {
          totalManHourMinPast += rd.getManHourMin();
        } else {
          totalManHourMinPlan += rd.getManHourMin();
        }
      } else {
        scheduleList.add(rd);
        if (rd.isPast()) {
          totalManHourMinPast += rd.getManHourMin();
        } else {
          totalManHourMinPlan += rd.getManHourMin();
        }
      }
    }
  }

  /**
   * ダミースケジュールを一覧から削除する。
   * 
   * @param list
   */
  private void cleanupDummySchedule(List<ManHourResultData> list) {
    if (list == null || list.size() <= 0) {
      return;
    }

    ManHourResultData rd = null;
    List<ManHourResultData> dummyList = new ArrayList<ManHourResultData>();
    int size = list.size();
    for (int i = 0; i < size; i++) {
      rd = list.get(i);
      if (rd.isDummy()) {
        dummyList.add(rd);
      }
    }

    list.removeAll(dummyList);
  }

  /**
   * 
   * @param obj
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultDataDetail(ManHourResultData obj)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  public String getTargetGroupName() {
    return target_group_name;
  }

  public String getTargetUserId() {
    return target_user_id;
  }

  /**
   * 
   * @param groupname
   * @return
   */
  public List<ALEipUser> getUsers() {
    if (hasAclSummaryOther) {
      if ((target_group_name != null)
        && (!target_group_name.equals(""))
        && (!target_group_name.equals("all"))) {
        return ALEipUtils.getUsers(target_group_name);
      } else {
        return ALEipUtils.getUsers("LoginUser");
      }
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
   *
   *
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();

    map.putValue("user_name", EipTScheduleMap.COMMON_CATEGORY_ID_PROPERTY);
    map.putValue("category", EipTScheduleMap.COMMON_CATEGORY_ID_PROPERTY);
    map.putValue("schedule", EipTScheduleMap.COMMON_CATEGORY_ID_PROPERTY);
    map.putValue("time", EipTScheduleMap.COMMON_CATEGORY_ID_PROPERTY);
    map.putValue("manhour", EipTScheduleMap.COMMON_CATEGORY_ID_PROPERTY);
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
   * 
   * @return
   */
  public String getScheduleUrl() {
    return scheduleUrl;
  }

  /**
   * 
   * @return
   */
  public ALDateTimeField getViewDate() {
    return view_date;
  }

  /**
   * 
   * @return
   */
  public String getCategoryId() {
    return category_id;
  }

  public List<CommonCategoryLiteBean> getCategoryList() {
    return categoryList;
  }

  /**
   * 
   * @return
   */
  public double getTotalManHourPast() {
    return ((int) (totalManHourMinPast * 100 / 60)) / 100.0;
  }

  /**
   * 
   * @return
   */
  public double getTotalManHourPlan() {
    return ((int) (totalManHourMinPlan * 100 / 60)) / 100.0;
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
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return aclPortletFeature;
  }
}

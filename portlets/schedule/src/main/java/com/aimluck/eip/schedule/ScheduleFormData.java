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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateContainer;
import com.aimluck.commons.field.ALDateField;
import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALIllegalDateException;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.category.util.CommonCategoryUtils;
import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.cayenne.om.portlet.EipTCommonCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.facilities.FacilityResultData;
import com.aimluck.eip.facilities.util.FacilitiesUtils;
import com.aimluck.eip.mail.util.ALEipUserAddr;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.whatsnew.util.WhatsNewUtils;

/**
 * スケジュールのフォームデータを管理するクラスです。
 * 
 */
public class ScheduleFormData extends ALAbstractFormData {

  /** <code>logger</code> logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleFormData.class.getName());

  /** <code>FLAG_EDIT_REPEAT_DEF</code> デフォルト値（繰り返し編集範囲） */
  private static final int FLAG_EDIT_REPEAT_DEF = -1;

  /** <code>FLAG_EDIT_REPEAT_ALL</code> 個別日程を編集（繰り返し編集範囲） */
  private static final int FLAG_EDIT_REPEAT_ALL = 0;

  /** <code>FLAG_EDIT_SCHEDULE_ONE</code> 全日程を編集（繰り返し編集範囲） */
  private static final int FLAG_EDIT_REPEAT_ONE = 1;

  /** <code>FLAG_DEL_MEMBER_ALL</code> [削除フラグ] すべての共有メンバーからこのスケジュールを削除する */
  public static final int FLAG_DEL_MEMBER_ALL = 0;

  /** <code>FLAG_DEL_MEMBER_ONE</code> [削除フラグ] このスケジュールからログインユーザーだけを削除する */
  public static final int FLAG_DEL_MEMBER_ONE = 1;

  /** <code>FLAG_DEL_RANGE_ALL</code> [削除フラグ] この繰り返しスケジュールを削除する */
  public static final int FLAG_DEL_RANGE_ALL = 0;

  /** <code>FLAG_DEL_RANGE_ONE</code> [削除フラグ] この繰り返しスケジュールの特定スケジュールのみを削除する */
  public static final int FLAG_DEL_RANGE_ONE = 1;

  /** <code>start_date</code> 開始日時 */
  private ALDateTimeField start_date;

  /** <code>end_date</code> 終了日時 */
  private ALDateTimeField end_date;

  /** <code>name</code> 予定 */
  private ALStringField name;

  /** <code>place</code> 場所 */
  private ALStringField place;

  /** <code>note</code> 内容 */
  private ALStringField note;

  /** <code>public_flag</code> 公開/非公開フラグ */
  private ALStringField public_flag;

  /** <code>repeat_type</code> 繰り返し種別 */
  private ALStringField repeat_type;

  /** <code>week_0</code> 繰り返し曜日 */
  private ALStringField week_0;

  /** <code>week_1</code> 繰り返し曜日 */
  private ALStringField week_1;

  /** <code>week_2</code> 繰り返し曜日 */
  private ALStringField week_2;

  /** <code>week_3</code> 繰り返し曜日 */
  private ALStringField week_3;

  /** <code>week_4</code> 繰り返し曜日 */
  private ALStringField week_4;

  /** <code>week_5</code> 繰り返し曜日 */
  private ALStringField week_5;

  /** <code>week_6</code> 繰り返し曜日 */
  private ALStringField week_6;

  /** <code>limit_flag</code> 期限ありなし */
  private ALStringField limit_flag;

  /** <code>limit_date</code> 繰り返し期限（開始日） */
  private ALDateField limit_start_date;

  /** <code>limit_date</code> 繰り返し期限（終了日） */
  private ALDateField limit_end_date;

  /** <code>month_day</code> 繰り返す日 */
  private ALNumberField month_day;

  /** <code>memberList</code> メンバーリスト */
  private ArrayList<ALEipUser> memberList;

  /** <code>currentYear</code> 現在の年 */
  private int currentYear;

  /** <code>is_member</code> スケジュールを共有するかどうか */
  private boolean is_member;

  /** <code>is_repeat</code> 繰り返すかどうか */
  private boolean is_repeat;

  /** <code>is_span</code> 期間指定かどうか */
  private boolean is_span;

  /** <code>login_user</code> ログインユーザー */
  private ALEipUser login_user;

  /** <code>tmpStart</code> 開始日時の初期値 */
  private String tmpStart;

  /** <code>tmpEnd</code> 終了日時の初期値 */
  private String tmpEnd;

  /** <code>selectData</code> 編集するスケジュールの1日の情報 */
  private ScheduleOnedayGroupSelectData selectData;

  /** <code>groups</code> グループ */
  private List<ALEipGroup> groups;

  /** <code>edit_schedule_flag</code> 繰り返しスケジュールの編集フラグ */
  private ALNumberField edit_repeat_flag;

  /** <code>tmpView</code> 表示する日 */
  private String tmpView;

  /** <code>end_date</code> 終了日時 */
  private ALDateTimeField view_date;

  /** <code>edit_control_flag</code> 共有メンバーによる編集／削除権限フラグ */
  private ALStringField edit_flag;

  /** <code>enable_change_tmpreserve</code> 仮スケジュールに戻すかどうか */
  private ALStringField change_tmpreserve_flag;

  /** <code>del_member_flag</code> [削除フラグ] 共有メンバーを削除するフラグ */
  private ALNumberField del_member_flag;

  /** <code>del_range_flag</code> [削除フラグ] 削除範囲のフラグ */
  private ALNumberField del_range_flag;

  /** <code>isOwner</code> 所有者かどうか */
  private boolean is_owner;

  /** <code>is_facility</code> 施設を予約するかどうか */
  private boolean is_facility;

  /** 施設リスト */
  private List facilityList;

  /** 全施設リスト */
  private List<FacilityResultData> facilityAllList;

  /** <code>todo_id</code> ToDo ID */
  private ALNumberField common_category_id;

  private final int msg_type = 0;

  private String org_id;

  private ALEipUser loginUser;

  /** アクセス権限の機能名 */
  private String aclPortletFeature = null;

  private boolean ignore_duplicate_facility;

  /*
   * @see
   * com.aimluck.eip.common.ALAbstractFormData#init(com.aimluck.eip.modules.
   * actions.common.ALAction, org.apache.turbine.util.RunData,
   * org.apache.velocity.context.Context)
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    is_facility = rundata.getParameters().getBoolean("is_facility");
    is_member = rundata.getParameters().getBoolean("is_member");
    is_repeat = rundata.getParameters().getBoolean("is_repeat");
    is_span = rundata.getParameters().getBoolean("is_span");
    ignore_duplicate_facility =
      rundata.getParameters().getBoolean("ignore_duplicate_facility", false);
    login_user = ALEipUtils.getALEipUser(rundata);

    groups = ALEipUtils.getMyGroups(rundata);

    is_owner = true;

    org_id = DatabaseOrmService.getInstance().getOrgId(rundata);
    loginUser = ALEipUtils.getALEipUser(rundata);

    //

    facilityAllList = new ArrayList<FacilityResultData>();
    facilityAllList.addAll(FacilitiesUtils.getFacilityAllList());

    String scheduleId =
      rundata.getParameters().getString(ALEipConstants.ENTITY_ID);
    if (scheduleId == null || scheduleId.equals("new")) {
      String str[] = rundata.getParameters().getStrings("member_to");
      if (str != null && str.length > 0) {
        aclPortletFeature =
          ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_OTHER;
        String user_name = login_user.getName().toString();
        for (int i = 0; i < str.length; i++) {
          if (user_name.equals(str[i])) {
            aclPortletFeature =
              ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_SELF;
            break;
          }
        }
      } else {
        aclPortletFeature =
          ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_SELF;
      }
    } else if (ScheduleUtils.hasRelation(rundata)) {
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_SELF;
    } else {
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_OTHER;
    }

    /*
     */
    // スーパークラスのメソッドを呼び出す。
    super.init(action, rundata, context);
  }

  public void loadParametersViewDate(RunData rundata, Context context) {
    if (ALEipUtils.isMatch(rundata, context)) {
      if (rundata.getParameters().containsKey("view_date")) {
        ALDateTimeField dummy = new ALDateTimeField("yyyy-MM-dd");
        tmpView = rundata.getParameters().getString("view_date");
        ALEipUtils.setTemp(rundata, context, "tmpView", tmpView);
        dummy.setValue(tmpView);
        if (!dummy.validate(new ArrayList<String>())) {
          ALEipUtils.removeTemp(rundata, context, "tmpView");
          logger.debug("[ScheduleFormData] Parameter cannot validate");
          ALEipUtils.redirectPageNotFound(rundata);
          return;
        }
      } else {
        if (tmpView == null || tmpView.equals("")) {
          if (tmpStart != null && !tmpStart.equals("")) {
            tmpView = tmpStart;
            ALEipUtils.setTemp(rundata, context, "tmpView", tmpView);
          }
        }
      }
    }
  }

  /**
   * パラメータを読み込みます。
   * 
   * @param rundata
   * @param context
   */
  public void loadParameters(RunData rundata, Context context) {
    ALDateTimeField dummy = new ALDateTimeField("yyyy-MM-dd-HH-mm");
    dummy.setNotNull(true);
    if (ALEipUtils.isMatch(rundata, context)) {
      if (rundata.getParameters().containsKey("form_start")) {
        tmpStart = rundata.getParameters().getString("form_start");
        ALEipUtils.setTemp(rundata, context, "tmpStart", tmpStart);
        dummy.setValue(tmpStart);
        if (!dummy.validate(new ArrayList<String>())) {
          ALEipUtils.removeTemp(rundata, context, "form_start");
          ALEipUtils.removeTemp(rundata, context, "form_end");
          logger.debug("[ScheduleFormData] Parameter cannot validate");
          ALEipUtils.redirectPageNotFound(rundata);
          return;
        }
      } else {
        dummy.setValue(new Date());
        ALEipUtils.setTemp(rundata, context, "tmpStart", dummy.toString());
      }
      if (rundata.getParameters().containsKey("form_end")) {
        tmpEnd = rundata.getParameters().getString("form_end");
        ALEipUtils.setTemp(rundata, context, "tmpEnd", tmpEnd);
        dummy.setValue(tmpEnd);
        if (!dummy.validate(new ArrayList<String>())) {
          ALEipUtils.removeTemp(rundata, context, "form_start");
          ALEipUtils.removeTemp(rundata, context, "form_end");
          logger.debug("[ScheduleFormData] Parameter cannot validate");
          ALEipUtils.redirectPageNotFound(rundata);
          return;
        }
      } else {
        dummy.setValue(new Date());
        ALEipUtils.setTemp(rundata, context, "tmpEnd", dummy.toString());
      }
    }
    tmpStart = ALEipUtils.getTemp(rundata, context, "tmpStart");
    tmpEnd = ALEipUtils.getTemp(rundata, context, "tmpEnd");

    loadParametersViewDate(rundata, context);
  }

  /*
   * @see com.aimluck.eip.common.ALData#initField()
   */
  public void initField() {
    Date now = new Date();
    Calendar cal = Calendar.getInstance();
    int min = cal.get(Calendar.MINUTE);
    // if (min <= 15) {
    // cal.set(Calendar.MINUTE, 15);
    // } else if (min <= 30) {
    // cal.set(Calendar.MINUTE, 30);
    // } else if (min <= 45) {
    // cal.set(Calendar.MINUTE, 45);
    // } else {
    // cal.set(Calendar.MINUTE, 60);
    // }

    if (min <= 5) {
      cal.set(Calendar.MINUTE, 5);
    } else if (min <= 10) {
      cal.set(Calendar.MINUTE, 10);
    } else if (min <= 15) {
      cal.set(Calendar.MINUTE, 15);
    } else if (min <= 20) {
      cal.set(Calendar.MINUTE, 20);
    } else if (min <= 25) {
      cal.set(Calendar.MINUTE, 25);
    } else if (min <= 30) {
      cal.set(Calendar.MINUTE, 30);
    } else if (min <= 35) {
      cal.set(Calendar.MINUTE, 35);
    } else if (min <= 40) {
      cal.set(Calendar.MINUTE, 40);
    } else if (min <= 45) {
      cal.set(Calendar.MINUTE, 45);
    } else if (min <= 50) {
      cal.set(Calendar.MINUTE, 50);
    } else if (min <= 55) {
      cal.set(Calendar.MINUTE, 55);
    } else {
      cal.set(Calendar.MINUTE, 60);
    }

    // 開始日時
    start_date = new ALDateTimeField("yyyy-MM-dd-HH-mm");
    if (tmpStart == null || tmpStart.equals("")) {
      start_date.setValue(now);
    } else {
      start_date.setValue(tmpStart);
    }
    start_date.setFieldName("開始日時");
    // 終了日時
    end_date = new ALDateTimeField("yyyy-MM-dd-HH-mm");
    if (tmpEnd == null || tmpEnd.equals("")) {
      if (tmpStart == null || tmpStart.equals("")) {
        end_date.setValue(now);
      } else {
        end_date.setValue(tmpStart);
      }
    } else {
      end_date.setValue(tmpEnd);
    }
    end_date.setFieldName("終了日時");
    // 繰り返しタイプ
    repeat_type = new ALStringField();
    repeat_type.setFieldName("繰り返しタイプ");
    repeat_type.setValue("D");
    repeat_type.setTrim(true);
    // 日
    week_0 = new ALStringField();
    week_0.setFieldName("日");
    week_0.setTrim(true);
    // 月
    week_1 = new ALStringField();
    week_1.setFieldName("月");
    week_1.setTrim(true);
    // 火
    week_2 = new ALStringField();
    week_2.setFieldName("火");
    week_2.setTrim(true);
    // 水
    week_3 = new ALStringField();
    week_3.setFieldName("水");
    week_3.setTrim(true);
    // 木
    week_4 = new ALStringField();
    week_4.setFieldName("木");
    week_4.setTrim(true);
    // 金
    week_5 = new ALStringField();
    week_5.setFieldName("金");
    week_5.setTrim(true);
    // 土
    week_6 = new ALStringField();
    week_6.setFieldName("土");
    week_6.setTrim(true);
    // 繰り返し日
    month_day = new ALNumberField();
    month_day.setFieldName("繰り返す月");
    // 繰り返しフラグ
    limit_flag = new ALStringField();
    limit_flag.setFieldName("繰り返し");
    limit_flag.setValue("OFF");
    limit_flag.setTrim(true);
    // 繰り返し期限
    limit_start_date = new ALDateField();
    limit_start_date.setFieldName("繰り返し期限");
    limit_start_date.setValue(start_date.getValue());
    limit_end_date = new ALDateField();
    limit_end_date.setFieldName("繰り返し期限");
    ALDateTimeField tmp_date = new ALDateTimeField("yyyy-MM-dd-HH-mm");
    if (tmpEnd == null || tmpEnd.equals("")) {
      if (tmpStart == null || tmpStart.equals("")) {
        tmp_date.setValue(now);
        limit_end_date.setValue(tmp_date.getValue());
      } else {
        tmp_date.setValue(tmpStart);
        limit_end_date.setValue(tmp_date.getValue());
      }
    } else {
      tmp_date.setValue(tmpEnd);
      limit_end_date.setValue(tmp_date.getValue());
    }
    // 予定
    name = new ALStringField();
    name.setFieldName("予定");
    name.setTrim(true);
    // 場所
    place = new ALStringField();
    place.setFieldName("場所");
    place.setTrim(true);
    // 内容
    note = new ALStringField();
    note.setFieldName("内容");
    note.setTrim(false);
    // 公開区分
    public_flag = new ALStringField();
    public_flag.setFieldName("公開区分");
    public_flag.setTrim(true);
    public_flag.setValue("O");
    // 繰り返しスケジュールの編集フラグ
    edit_repeat_flag = new ALNumberField();
    edit_repeat_flag.setFieldName("繰り返し編集範囲");
    edit_repeat_flag.setValue(FLAG_EDIT_REPEAT_DEF);

    // 指定日時
    view_date = new ALDateTimeField("yyyy-MM-dd");
    if (tmpView == null || tmpView.equals("")) {
      view_date.setValue(now);
    } else {
      view_date.setValue(tmpView);
    }
    view_date.setFieldName("指定日時");

    // 共有メンバーによる編集／削除権限フラグ
    edit_flag = new ALStringField();
    edit_flag.setFieldName("編集／削除権限");
    edit_flag.setTrim(true);
    edit_flag.setValue("T");

    // 仮スケジュールに戻すかどうか
    change_tmpreserve_flag = new ALStringField();
    change_tmpreserve_flag.setFieldName("仮スケジュール");
    change_tmpreserve_flag.setTrim(true);
    change_tmpreserve_flag.setValue("F");

    // メンバーリスト
    memberList = new ArrayList<ALEipUser>();
    // 現在の年（年を選択するリストボックスに利用）
    currentYear = Calendar.getInstance().get(Calendar.YEAR);

    // [削除フラグ] 共有メンバーを削除するフラグ
    del_member_flag = new ALNumberField();
    del_member_flag.setValue(FLAG_DEL_MEMBER_ALL);
    // [削除フラグ] 削除範囲のフラグ
    del_range_flag = new ALNumberField();
    del_range_flag.setValue(FLAG_DEL_RANGE_ALL);

    // 施設リスト
    facilityList = new ArrayList<Object>();

    // 2007.3.28 ToDo連携
    common_category_id = new ALNumberField();
    common_category_id.setFieldName("カテゴリ");
    common_category_id.setValue(1);

    // common_category_id.setNotNull(true);
  }

  /*
   * @see
   * com.aimluck.eip.common.ALAbstractFormData#setFormData(org.apache.turbine
   * .util.RunData, org.apache.velocity.context.Context, java.util.ArrayList)
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean res = super.setFormData(rundata, context, msgList);
    if (res) {
      try {
        String str[] = rundata.getParameters().getStrings("member_to");
        if (str != null && str.length > 0) {
          SelectQuery<TurbineUser> query =
            new SelectQuery<TurbineUser>(TurbineUser.class);
          Expression exp =
            ExpressionFactory.inExp(TurbineUser.LOGIN_NAME_PROPERTY, str);
          query.setQualifier(exp);
          memberList.addAll(ALEipUtils.getUsersFromSelectQuery(query));
        }
        if (memberList.size() == 0) {
          memberList.add(login_user);
        }

        String f_id[] = rundata.getParameters().getStrings("facility_to");
        if (!ScheduleUtils.isZeroLength(f_id)) {
          SelectQuery<EipMFacility> fquery =
            new SelectQuery<EipMFacility>(EipMFacility.class);
          Expression fexp =
            ExpressionFactory.inDbExp(EipMFacility.FACILITY_ID_PK_COLUMN, f_id);
          fquery.setQualifier(fexp);
          List<EipMFacility> f_list = fquery.fetchList();

          int f_size = f_list.size();
          for (int i = 0; i < f_size; i++) {
            EipMFacility f_record = f_list.get(i);
            FacilityResultData rd = new FacilityResultData();
            rd.initField();
            rd.setFacilityId(f_record.getFacilityId().longValue());
            rd.setFacilityName(f_record.getFacilityName());
            facilityList.add(rd);
          }
        }
      } catch (Exception ex) {
        logger.error("Exception", ex);
      }
    }
    return res;
  }

  /*
   * @see com.aimluck.eip.common.ALAbstractFormData#setValidator()
   */
  @Override
  protected void setValidator() {
    // 予定
    name.setNotNull(true);
    name.limitMaxLength(50);
    // 場所
    place.limitMaxLength(50);
    // 内容
    note.limitMaxLength(1000);
  }

  /*
   * @see
   * com.aimluck.eip.common.ALAbstractFormData#validate(java.util.ArrayList)
   */
  @Override
  protected boolean validate(List<String> msgList) throws ALDBErrorException,
      ALPageNotFoundException {
    // 開始日時
    start_date.validate(msgList);
    // 終了日時
    end_date.validate(msgList);

    if (is_repeat) {
      // 開始日時 と 終了日時 の日付を、開始日時 の日付に一致させる
      Calendar tmp_end_date = Calendar.getInstance();
      tmp_end_date.set(Calendar.YEAR, Integer.valueOf(start_date.getYear()));
      tmp_end_date.set(
        Calendar.MONTH,
        Integer.valueOf(start_date.getMonth()) - 1);
      tmp_end_date.set(Calendar.DATE, Integer.valueOf(start_date.getDay()));
      tmp_end_date.set(Calendar.HOUR_OF_DAY, Integer
        .valueOf(end_date.getHour()));
      tmp_end_date.set(Calendar.MINUTE, Integer.valueOf(end_date.getMinute()));
      tmp_end_date.set(Calendar.SECOND, 0);
      end_date.setValue(tmp_end_date.getTime());
    }

    if (is_span) {
      // 開始日時 と 終了日時 の時間を 0時0分0秒 に設定する
      Calendar tmp_start_date = Calendar.getInstance();
      tmp_start_date.setTime(start_date.getValue());
      tmp_start_date.set(Calendar.HOUR_OF_DAY, 0);
      tmp_start_date.set(Calendar.MINUTE, 0);
      tmp_start_date.set(Calendar.SECOND, 0);
      start_date.setValue(tmp_start_date.getTime());

      Calendar tmp_end_date = Calendar.getInstance();
      tmp_end_date.setTime(end_date.getValue());
      tmp_end_date.set(Calendar.HOUR_OF_DAY, 0);
      tmp_end_date.set(Calendar.MINUTE, 0);
      tmp_end_date.set(Calendar.SECOND, 0);
      end_date.setValue(tmp_start_date.getTime());
    }

    // 開始日時＆終了日時
    if (end_date.getValue().before(start_date.getValue())) {
      msgList
        .add("『 <span class='em'>終了日時</span> 』は『 <span class='em'>開始日時</span> 』以降の日付を指定してください。");
    }

    if (is_repeat) {
      try {
        if ("W".equals(repeat_type.getValue())) {
          if (week_0.getValue() == null
            && week_1.getValue() == null
            && week_2.getValue() == null
            && week_3.getValue() == null
            && week_4.getValue() == null
            && week_5.getValue() == null
            && week_6.getValue() == null) {
            msgList.add("『 <span class='em'>毎週</span> 』は曜日をひとつ以上指定してください。");
          } else {
            // 期間を指定しているか．
            if ("ON".equals(limit_flag.toString())) {
              // 指定期間内に指定した曜日が入るか．
              long deltaDay =
                (limit_end_date.getValue().getDate().getTime() - limit_start_date
                  .getValue()
                  .getDate()
                  .getTime()) / 86400000;
              if (deltaDay < 6) {
                // 指定期間内に，月火水木金土日が必ずしも含まれない．
                Calendar limitStartCal = Calendar.getInstance();
                limitStartCal.setTime(limit_start_date.getValue().getDate());
                Calendar limitEndCal = Calendar.getInstance();
                limitEndCal.setTime(limit_end_date.getValue().getDate());
                int limitStartDayOfWeek =
                  limitStartCal.get(Calendar.DAY_OF_WEEK);
                int limitEndDayOfWeek = limitEndCal.get(Calendar.DAY_OF_WEEK);
                boolean hasWeek = true;
                if (week_0.getValue() != null) {
                  hasWeek =
                    hasWeek
                      & includeWeek(
                        Calendar.SUNDAY,
                        limitStartDayOfWeek,
                        limitEndDayOfWeek);
                }
                if (week_1.getValue() != null) {
                  hasWeek =
                    hasWeek
                      & includeWeek(
                        Calendar.MONDAY,
                        limitStartDayOfWeek,
                        limitEndDayOfWeek);
                }
                if (week_2.getValue() != null) {
                  hasWeek =
                    hasWeek
                      & includeWeek(
                        Calendar.TUESDAY,
                        limitStartDayOfWeek,
                        limitEndDayOfWeek);
                }
                if (week_3.getValue() != null) {
                  hasWeek =
                    hasWeek
                      & includeWeek(
                        Calendar.WEDNESDAY,
                        limitStartDayOfWeek,
                        limitEndDayOfWeek);
                }
                if (week_4.getValue() != null) {
                  hasWeek =
                    hasWeek
                      & includeWeek(
                        Calendar.THURSDAY,
                        limitStartDayOfWeek,
                        limitEndDayOfWeek);
                }
                if (week_5.getValue() != null) {
                  hasWeek =
                    hasWeek
                      & includeWeek(
                        Calendar.FRIDAY,
                        limitStartDayOfWeek,
                        limitEndDayOfWeek);
                }
                if (week_6.getValue() != null) {
                  hasWeek =
                    hasWeek
                      & includeWeek(
                        Calendar.SATURDAY,
                        limitStartDayOfWeek,
                        limitEndDayOfWeek);
                }
                if (!hasWeek) {
                  msgList
                    .add("『 <span class='em'>毎週</span> 』で選択する曜日は、『 <span class='em'>期限</span> 』の範囲内で指定してください。");
                }
              }
            }
          }
        } else if ("M".equals(repeat_type.getValue())) {
          if ("ON".equals(limit_flag.toString())) {
            // 毎月の日付指定
            Calendar limitStartCal = Calendar.getInstance();
            limitStartCal.setTime(limit_start_date.getValue().getDate());
            Calendar limitEndCal = Calendar.getInstance();
            limitEndCal.setTime(limit_end_date.getValue().getDate());
            int limitStartMonth = limitStartCal.get(Calendar.MONTH);
            int limitEndMonth = limitEndCal.get(Calendar.MONTH);
            if (limitStartMonth >= limitEndMonth) {
              // 同じ月
              if (limit_start_date.getValue().getDay() > month_day.getValue()
                || limit_end_date.getValue().getDay() < month_day.getValue()) {
                msgList
                  .add("『 <span class='em'>毎月</span> 』は『 <span class='em'>期限</span> 』の範囲内の日付を指定してください。");
              }
            } else {
              if (limit_start_date.getValue().getDay() > month_day.getValue()
                && limit_end_date.getValue().getDay() < month_day.getValue()) {
                msgList
                  .add("『 <span class='em'>毎月</span> 』は『 <span class='em'>期限</span> 』の範囲内の日付を指定してください。");
              }
            }
          }
        }

        if (!ScheduleUtils.equalsToDate(
          limit_start_date.getValue().getDate(),
          limit_end_date.getValue().getDate(),
          false)
          && limit_start_date.getValue().getDate().after(
            limit_end_date.getValue().getDate())) {
          msgList.add("『 <span class='em'>期限</span> 』は今日以降の日付を指定してください。");
        }

      } catch (NumberFormatException nfe) {
        logger
          .error("[ScheduleFormData] NumberFormatException: Limit Date is wrong.");
        throw new ALPageNotFoundException();
      } catch (ALIllegalDateException ad) {
        logger
          .error("[ScheduleFormData] ALIllegalDateException: Limit Date is wrong.");
        throw new ALPageNotFoundException();
      }
    }

    // 予定
    name.validate(msgList);
    // 場所
    place.validate(msgList);
    // 内容
    note.validate(msgList);

    common_category_id.validate(msgList);

    return (msgList.size() == 0);
  }

  /*
   * @see
   * com.aimluck.eip.common.ALAbstractFormData#loadFormData(org.apache.turbine
   * .util.RunData, org.apache.velocity.context.Context, java.util.ArrayList)
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    try {
      // オブジェクトモデルを取得
      EipTSchedule record =
        ScheduleUtils.getEipTSchedule(rundata, context, false);
      if (record == null) {
        return false;
      }

      is_owner =
        (record.getOwnerId().intValue() == login_user.getUserId().getValue())
          ? true
          : false;

      // 予定
      name.setValue(record.getName());
      // 場所
      place.setValue(record.getPlace());
      // 内容
      note.setValue(record.getNote());
      // 公開フラグ
      public_flag.setValue(record.getPublicFlag());
      // 共有メンバーによる編集／削除フラグ
      if ("T".equals(record.getEditFlag())) {
        if (is_owner) {
          edit_flag.setValue(record.getEditFlag());
        } else {
          // スケジュールの登録ユーザがすでにメンバーから抜けているかを検証する．
          int createUserId = record.getOwnerId().intValue();
          boolean inculudeCreateUser = false;
          List<?> scheduleMaps = record.getEipTScheduleMaps();
          for (int i = 0; i < scheduleMaps.size(); i++) {
            EipTScheduleMap map = (EipTScheduleMap) scheduleMaps.get(i);
            if (createUserId == map.getUserId().intValue()
              && !"R".equals(map.getStatus())) {
              inculudeCreateUser = true;
              break;
            }
          }
          if (inculudeCreateUser) {
            edit_flag.setValue("F");
          } else {
            edit_flag.setValue("T");
          }
        }
      } else {
        edit_flag.setValue("F");
      }
      // DN -> 毎日 (A = N -> 期限なし A = L -> 期限あり)
      // WnnnnnnnN W01111110 -> 毎週(月～金用)
      // MnnN M25 -> 毎月25日
      // S -> 期間での指定
      String ptn = record.getRepeatPattern();
      int count = 0;
      is_repeat = true;
      is_span = false;
      // 毎日
      if (ptn.charAt(0) == 'D') {
        repeat_type.setValue("D");
        count = 1;
        // 毎週
      } else if (ptn.charAt(0) == 'W') {
        repeat_type.setValue("W");
        week_0.setValue(ptn.charAt(1) != '0' ? "TRUE" : null);
        week_1.setValue(ptn.charAt(2) != '0' ? "TRUE" : null);
        week_2.setValue(ptn.charAt(3) != '0' ? "TRUE" : null);
        week_3.setValue(ptn.charAt(4) != '0' ? "TRUE" : null);
        week_4.setValue(ptn.charAt(5) != '0' ? "TRUE" : null);
        week_5.setValue(ptn.charAt(6) != '0' ? "TRUE" : null);
        week_6.setValue(ptn.charAt(7) != '0' ? "TRUE" : null);
        count = 8;
        // 毎月
      } else if (ptn.charAt(0) == 'M') {
        repeat_type.setValue("M");
        month_day.setValue(Integer.parseInt(ptn.substring(1, 3)));
        count = 3;
        // 期間
      } else if (ptn.charAt(0) == 'S') {
        is_span = true;
        is_repeat = false;
      } else {
        is_repeat = false;
      }
      if (is_repeat) {
        // 開始日時
        Calendar tmpViewCal = Calendar.getInstance();
        tmpViewCal.setTime(view_date.getValue());
        Calendar tmpStartCal = Calendar.getInstance();
        tmpStartCal.setTime(record.getStartDate());
        tmpViewCal.set(Calendar.HOUR_OF_DAY, tmpStartCal
          .get(Calendar.HOUR_OF_DAY));
        tmpViewCal.set(Calendar.MINUTE, tmpStartCal.get(Calendar.MINUTE));
        start_date.setValue(tmpViewCal.getTime());
        // 終了日時
        Calendar tmpStopCal = Calendar.getInstance();
        tmpStopCal.setTime(record.getEndDate());
        tmpViewCal.set(Calendar.HOUR_OF_DAY, tmpStopCal
          .get(Calendar.HOUR_OF_DAY));
        tmpViewCal.set(Calendar.MINUTE, tmpStopCal.get(Calendar.MINUTE));
        end_date.setValue(tmpViewCal.getTime());

        if (ptn.charAt(count) == 'N') {
          limit_start_date.setValue(view_date.getValue());
          limit_end_date.setValue(view_date.getValue());
          limit_flag.setValue("OFF");
        } else {
          limit_flag.setValue("ON");
          limit_start_date.setValue(record.getStartDate());
          limit_end_date.setValue(record.getEndDate());
        }

        // 繰り返しスケジュールの編集フラグ
        edit_repeat_flag.setValue(FLAG_EDIT_REPEAT_ALL);
      }

      if (is_span) {
        // 開始日時
        start_date.setValue(record.getStartDate());
        // 終了日時
        end_date.setValue(record.getEndDate());

        limit_start_date.setValue(record.getStartDate());
        limit_end_date.setValue(record.getEndDate());
      }

      if (!is_repeat && !is_span) {
        // 開始日時
        start_date.setValue(record.getStartDate());
        // 終了日時
        end_date.setValue(record.getEndDate());

        limit_start_date.setValue(record.getStartDate());
        limit_end_date.setValue(record.getEndDate());
      }

      // このスケジュールを共有しているメンバーを取得
      SelectQuery<EipTScheduleMap> mapquery =
        new SelectQuery<EipTScheduleMap>(EipTScheduleMap.class);
      Expression mapexp =
        ExpressionFactory.matchExp(EipTScheduleMap.SCHEDULE_ID_PROPERTY, record
          .getScheduleId());
      mapquery.setQualifier(mapexp);
      List<EipTScheduleMap> list = mapquery.fetchList();

      List<Integer> users = new ArrayList<Integer>();
      List<Integer> facilityIds = new ArrayList<Integer>();
      int size = list.size();
      for (int i = 0; i < size; i++) {
        EipTScheduleMap map = list.get(i);
        if (ScheduleUtils.SCHEDULEMAP_TYPE_USER.equals(map.getType())) {
          users.add(map.getUserId());
          // if (ALEipUtils.getALEipUser(rundata).getUserId().getValue() == map
          // .getUserId().longValue()) {
          EipTCommonCategory category = map.getEipTCommonCategory();
          if (category == null) {
            common_category_id.setValue(1);
          } else {
            common_category_id.setValue(category
              .getCommonCategoryId()
              .longValue());
          }
          // }
        } else {
          facilityIds.add(map.getUserId());
        }
      }

      if (users.size() > 0) {
        SelectQuery<TurbineUser> query =
          new SelectQuery<TurbineUser>(TurbineUser.class);
        Expression exp =
          ExpressionFactory.inDbExp(TurbineUser.USER_ID_PK_COLUMN, users);
        query.setQualifier(exp);
        memberList.addAll(ALEipUtils.getUsersFromSelectQuery(query));
      } else {
        memberList.add(login_user);
      }

      if (facilityIds.size() > 0) {
        SelectQuery<EipMFacility> fquery =
          new SelectQuery<EipMFacility>(EipMFacility.class);
        Expression fexp =
          ExpressionFactory.inDbExp(
            EipMFacility.FACILITY_ID_PK_COLUMN,
            facilityIds);
        fquery.setQualifier(fexp);
        facilityList.addAll(FacilitiesUtils
          .getFacilitiesFromSelectQuery(fquery));
      }
    } catch (Exception e) {

      // TODO: エラー処理
      logger.error("[ScheduleFormData]", e);
      throw new ALDBErrorException();

    }
    return true;
  }

  /*
   * @see
   * com.aimluck.eip.common.ALAbstractFormData#insertFormData(org.apache.turbine
   * .util.RunData, org.apache.velocity.context.Context, java.util.ArrayList)
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) throws ALDBErrorException {
    EipTSchedule schedule = null;
    try {
      // 施設のアクセスコントロールのチェック
      int f_size = facilityList.size();
      if (!facilityCheckAclPermission(
        f_size,
        msgList,
        rundata,
        ALAccessControlConstants.VALUE_ACL_INSERT)) {
        return false;
      }

      // TODO: DBトランザクション
      // Validate のときに SELECT していることに注意する

      if (is_span) {
        Date startdate = start_date.getValue();
        startdate.setHours(0);
        startdate.setMinutes(0);
        start_date.setValue(startdate);
        Date enddate = end_date.getValue();
        enddate.setHours(0);
        enddate.setMinutes(0);
        end_date.setValue(enddate);
      } else {
        Calendar startcal = new GregorianCalendar();
        startcal.setTime(start_date.getValue());
        Calendar endcal = Calendar.getInstance();
        endcal.setTime(end_date.getValue());
        endcal.set(Calendar.YEAR, startcal.get(Calendar.YEAR));
        endcal.set(Calendar.MONTH, startcal.get(Calendar.MONTH));
        endcal.set(Calendar.DATE, startcal.get(Calendar.DATE));
        end_date.setValue(endcal.getTime());
      }

      int ownerid = ALEipUtils.getUserId(rundata);
      // 新規オブジェクトモデル
      schedule = Database.create(EipTSchedule.class);
      // 親スケジュール ID
      schedule.setParentId(Integer.valueOf(0));
      // 予定
      schedule.setName(name.getValue());
      // 場所
      schedule.setPlace(place.getValue());
      // 内容
      schedule.setNote(note.getValue());
      // 公開フラグ
      schedule.setPublicFlag(public_flag.getValue());
      // 共有メンバーによる編集／削除フラグ
      if (memberList.size() >= 1) {
        schedule.setEditFlag(edit_flag.getValue());
      } else {
        schedule.setEditFlag("F");
      }
      // オーナーID
      schedule.setOwnerId(Integer.valueOf(ownerid));
      // 作成日
      Date now = new Date();
      schedule.setCreateDate(now);
      schedule.setCreateUserId(Integer.valueOf(ownerid));
      // 更新日
      schedule.setUpdateDate(now);
      schedule.setUpdateUserId(Integer.valueOf(ownerid));

      if (is_span) {
        // 期間スケジュール設定の場合
        schedule.setEndDate(end_date.getValue());
        schedule.setRepeatPattern("S");

        schedule.setStartDate(start_date.getValue());
      } else if (!is_repeat) {
        // 終了日時
        schedule.setEndDate(end_date.getValue());
        schedule.setRepeatPattern("N");

        schedule.setStartDate(start_date.getValue());
      } else {
        // 繰り返しスケジュール設定の場合
        char lim = 'N';
        Calendar cal = Calendar.getInstance();
        cal.setTime(end_date.getValue());
        if ("ON".equals(limit_flag.getValue())) {
          lim = 'L';
          cal.set(limit_end_date.getValue().getYear(), limit_end_date
            .getValue()
            .getMonth() - 1, limit_end_date.getValue().getDay());

          ALDateContainer container = limit_start_date.getValue();
          Calendar limitStartCal = Calendar.getInstance();
          limitStartCal.setTime(start_date.getValue());
          limitStartCal.set(Calendar.YEAR, container.getYear());
          limitStartCal.set(Calendar.MONTH, container.getMonth() - 1);
          limitStartCal.set(Calendar.DATE, container.getDay());
          schedule.setStartDate(limitStartCal.getTime());
        } else {
          schedule.setStartDate(start_date.getValue());
        }

        schedule.setEndDate(cal.getTime());
        if ("D".equals(repeat_type.getValue())) {
          schedule.setRepeatPattern(new StringBuffer()
            .append('D')
            .append(lim)
            .toString());
        } else if ("W".equals(repeat_type.getValue())) {
          schedule.setRepeatPattern(new StringBuffer().append('W').append(
            week_0.getValue() != null ? 1 : 0).append(
            week_1.getValue() != null ? 1 : 0).append(
            week_2.getValue() != null ? 1 : 0).append(
            week_3.getValue() != null ? 1 : 0).append(
            week_4.getValue() != null ? 1 : 0).append(
            week_5.getValue() != null ? 1 : 0).append(
            week_6.getValue() != null ? 1 : 0).append(lim).toString());
        } else {
          DecimalFormat format = new DecimalFormat("00");
          schedule.setRepeatPattern(new StringBuffer().append('M').append(
            format.format(month_day.getValue())).append(lim).toString());
        }
      }

      EipTCommonCategory category1 =
        CommonCategoryUtils.getEipTCommonCategory(Long.valueOf(1));

      // 2007.3.28 ToDo連携
      // // スケジュールを登録
      // orm.doInsert(schedule);
      int size = memberList.size();
      for (int i = 0; i < size; i++) {
        EipTScheduleMap map = Database.create(EipTScheduleMap.class);
        ALEipUser user = memberList.get(i);
        int userid = (int) user.getUserId().getValue();
        // map.setPrimaryKey(schedule.getScheduleId(), userid);
        map.setEipTSchedule(schedule);
        map.setUserId(Integer.valueOf(userid));
        // O: 自スケジュール T: 仮スケジュール C: 確定スケジュール
        if (userid == ALEipUtils.getUserId(rundata)) {
          map.setStatus("O");
        } else {
          map.setStatus("T");
        }
        EipTCommonCategory category =
          CommonCategoryUtils.getEipTCommonCategory(common_category_id
            .getValue());
        if (category == null) {
          map.setCommonCategoryId(Integer.valueOf(1));
          map.setEipTSchedule(schedule);
          map.setEipTCommonCategory(category1);
        } else {
          map.setCommonCategoryId(Integer.valueOf((int) (common_category_id
            .getValue())));
          map.setEipTSchedule(schedule);
          map.setEipTCommonCategory(category);
        }
        map.setType(ScheduleUtils.SCHEDULEMAP_TYPE_USER);
      }

      // グループに施設を追加する．（公開スケジュールのみ）

      if ("O".equals(public_flag.toString()) && !(is_span)) {
        for (int i = 0; i < f_size; i++) {
          EipTScheduleMap map = Database.create(EipTScheduleMap.class);
          FacilityResultData frd = (FacilityResultData) facilityList.get(i);
          int facilityid = (int) frd.getFacilityId().getValue();
          // map.setPrimaryKey(schedule.getScheduleId(), facilityid);
          map.setEipTSchedule(schedule);
          map.setUserId(Integer.valueOf(facilityid));
          // O: 自スケジュール T: 仮スケジュール C: 確定スケジュール
          map.setStatus("O");
          map.setType(ScheduleUtils.SCHEDULEMAP_TYPE_FACILITY);
          map.setEipTCommonCategory(category1);
          map.setCommonCategoryId(Integer.valueOf(1));
        }
      }

      /* 施設重複判定 */
      if (!ignore_duplicate_facility) {
        if (facilityList.size() > 0) {
          ArrayList<Integer> fids = new ArrayList<Integer>();
          FacilityResultData facility = null;
          int fsize = facilityList.size();
          for (int i = 0; i < fsize; i++) {
            facility = (FacilityResultData) facilityList.get(i);
            fids
              .add(Integer.valueOf((int) facility.getFacilityId().getValue()));
          }
          if (ScheduleUtils.isDuplicateFacilitySchedule(
            schedule,
            fids,
            null,
            null)) {
            if (msgList.size() == 0) {
              msgList.add("duplicate_facility");
            }
            Database.rollback();
            return false;
          }
        }
      }

      // スケジュールを登録
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        schedule.getScheduleId(),
        ALEventlogConstants.PORTLET_TYPE_SCHEDULE,
        schedule.getName());

      /* メンバー全員に新着ポートレット登録 */
      ALAccessControlFactoryService aclservice =
        (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
          .getInstance())
          .getService(ALAccessControlFactoryService.SERVICE_NAME);
      ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();
      List<Integer> userIds =
        aclhandler.getAcceptUserIdsInListExceptLoginUser(
          (int) loginUser.getUserId().getValue(),
          ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_SELF,
          ALAccessControlConstants.VALUE_ACL_DETAIL,
          memberList);

      int u_size = userIds.size();
      for (int i = 0; i < u_size; i++) {
        Integer _id = userIds.get(i);
        WhatsNewUtils.insertWhatsNew(
          WhatsNewUtils.WHATS_NEW_TYPE_SCHEDULE,
          schedule.getScheduleId().intValue(),
          _id.intValue());
      }

    } catch (Exception e) {
      Database.rollback();
      logger.error("[ScheduleFormData]", e);
      throw new ALDBErrorException();
    }

    try {
      // メール送信
      int msgType =
        ALMailUtils.getSendDestType(ALMailUtils.KEY_MSGTYPE_SCHEDULE);
      if (msgType > 0) {
        // パソコンへメールを送信
        List<ALEipUserAddr> destMemberList =
          ALMailUtils.getALEipUserAddrs(memberList, ALEipUtils
            .getUserId(rundata), false);
        String subject =
          "[" + DatabaseOrmService.getInstance().getAlias() + "]スケジュール";
        String org_id = DatabaseOrmService.getInstance().getOrgId(rundata);
        for (int i = 0; i < destMemberList.size(); i++) {
          List<ALEipUserAddr> destMember = new ArrayList<ALEipUserAddr>();
          destMember.add(destMemberList.get(i));
          ALMailUtils.sendMailDelegate(
            org_id,
            ALEipUtils.getUserId(rundata),
            destMember,
            subject,
            subject,
            ScheduleUtils.createMsgForPc(rundata, schedule, memberList),
            ScheduleUtils.createMsgForCellPhone(
              rundata,
              schedule,
              memberList,
              destMember.get(0).getUserId()),
            ALMailUtils.getSendDestType(ALMailUtils.KEY_MSGTYPE_SCHEDULE),
            new ArrayList<String>());
        }
      }
    } catch (Exception ex) {
      msgList.add("メールを送信できませんでした。");
      logger.error("Exception", ex);
      return false;
    }

    return true;
  }

  /*
   * @see
   * com.aimluck.eip.common.ALAbstractFormData#updateFormData(org.apache.turbine
   * .util.RunData, org.apache.velocity.context.Context, java.util.ArrayList)
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    ArrayList<ALEipUser> newmemberList = new ArrayList<ALEipUser>();
    EipTSchedule schedule = null;
    EipTSchedule newSchedule = null;
    try {
      int entity_id;
      int f_size = facilityList.size();
      // 施設のアクセスコントロールのチェック
      if (!facilityCheckAclPermission(
        f_size,
        msgList,
        rundata,
        ALAccessControlConstants.VALUE_ACL_UPDATE)) {
        return false;
      }

      // TODO: DBトランザクション
      // Validate のときに SELECT していることに注意する

      if (is_span) {
        Date startdate = start_date.getValue();
        startdate.setHours(0);
        startdate.setMinutes(0);
        start_date.setValue(startdate);
        Date enddate = end_date.getValue();
        enddate.setHours(0);
        enddate.setMinutes(0);
        end_date.setValue(enddate);
      } else {
        Calendar startcal = new GregorianCalendar();
        startcal.setTime(start_date.getValue());
        Calendar endcal = Calendar.getInstance();
        endcal.setTime(end_date.getValue());
        endcal.set(Calendar.YEAR, startcal.get(Calendar.YEAR));
        endcal.set(Calendar.MONTH, startcal.get(Calendar.MONTH));
        endcal.set(Calendar.DATE, startcal.get(Calendar.DATE));
        end_date.setValue(endcal.getTime());
      }

      // オブジェクトモデルを取得
      schedule = ScheduleUtils.getEipTSchedule(rundata, context, false);
      if (schedule == null) {
        return false;
      }
      entity_id = schedule.getScheduleId().intValue();
      // int ownerid = ALEipUtils.getUserId(rundata);
      int ownerid = schedule.getOwnerId();

      // スケジュールのアップデート権限を検証する．
      /*
       * if (ownerid != schedule.getOwnerId().intValue() &&
       * "F".equals(schedule.getEditFlag())) { // アップデート失敗時は、スケジュールの一覧を表示させる．
       * return true; }
       */

      // 新たに追加されたメンバーのリストを取得
      List<?> scheduleMapList = schedule.getEipTScheduleMaps();
      int scheduleMapListSize = scheduleMapList.size();
      List<Integer> oldmemberIdList = new ArrayList<Integer>();
      for (int i = 0; i < scheduleMapListSize; i++) {
        EipTScheduleMap map = (EipTScheduleMap) scheduleMapList.get(i);
        if (ScheduleUtils.SCHEDULEMAP_TYPE_USER.equals(map.getType())) {
          oldmemberIdList.add(map.getUserId());
        }
      }
      int allmemberListSize = memberList.size();
      for (int i = 0; i < allmemberListSize; i++) {
        int memberId = (int) memberList.get(i).getUserId().getValue();
        if (!isContains(oldmemberIdList, memberId)) {
          newmemberList.add(ALEipUtils.getALEipUser(memberId));
        }
      }

      EipTCommonCategory category1 =
        CommonCategoryUtils.getEipTCommonCategory(Long.valueOf(1));

      // if (is_repeat && edit_repeat_flag.getValue() ==
      // FLAG_EDIT_REPEAT_ONE) {
      if (edit_repeat_flag.getValue() == FLAG_EDIT_REPEAT_ONE) {
        // 繰り返しスケジュールの個別日程を変更する．
        // 新規オブジェクトモデル
        newSchedule = Database.create(EipTSchedule.class);
        // 繰り返しの親スケジュール ID
        newSchedule.setParentId(schedule.getScheduleId());
        // 予定
        newSchedule.setName(name.getValue());
        // 場所
        newSchedule.setPlace(place.getValue());
        // 内容
        newSchedule.setNote(note.getValue());
        // 公開フラグ
        newSchedule.setPublicFlag(public_flag.getValue());
        // 共有メンバーによる編集／削除フラグ
        newSchedule.setEditFlag("F");
        newSchedule.setEditFlag(schedule.getEditFlag());
        // オーナーID
        newSchedule.setOwnerId(Integer.valueOf(ownerid));
        // 作成日
        Date now = new Date();
        newSchedule.setCreateDate(now);
        newSchedule.setCreateUserId(Integer.valueOf(ownerid));
        // 更新日
        newSchedule.setUpdateDate(now);
        newSchedule.setUpdateUserId(Integer.valueOf(ALEipUtils
          .getUserId(rundata)));
        // 終了日時
        newSchedule.setEndDate(end_date.getValue());
        newSchedule.setRepeatPattern("N");
        newSchedule.setStartDate(start_date.getValue());

        // 2007.3.28 ToDo連携

        int allsize = memberList.size() + facilityList.size();
        if (allsize > 0) {
          List<?> scheduleMaps = schedule.getEipTScheduleMaps();
          int size = memberList.size();
          for (int i = 0; i < size; i++) {
            EipTScheduleMap map = Database.create(EipTScheduleMap.class);
            ALEipUser user = memberList.get(i);
            int userid = (int) user.getUserId().getValue();
            // map.setPrimaryKey(newSchedule.getScheduleId(),
            // userid);
            map.setEipTSchedule(newSchedule);
            map.setUserId(Integer.valueOf(userid));
            // O: 自スケジュール T: 仮スケジュール C: 確定スケジュール
            // if (userid == ALEipUtils.getUserId(rundata)) {
            if (userid == ownerid) {
              map.setStatus("O");
            } else {
              if ("T".equals(change_tmpreserve_flag.getValue())) {
                map.setStatus("T");
              } else {
                EipTScheduleMap tmpMap =
                  getScheduleMap(
                    scheduleMaps,
                    userid,
                    ScheduleUtils.SCHEDULEMAP_TYPE_USER);
                if (tmpMap != null) {
                  map.setStatus(tmpMap.getStatus());
                } else {
                  map.setStatus("T");
                }
              }
            }
            EipTCommonCategory category =
              CommonCategoryUtils.getEipTCommonCategory(common_category_id
                .getValue());
            if (category == null) {
              map.setCommonCategoryId(Integer.valueOf(1));
              map.setEipTCommonCategory(category1);
            } else {
              map.setCommonCategoryId(Integer.valueOf((int) (common_category_id
                .getValue())));
              map.setEipTCommonCategory(category);
            }
            map.setType(ScheduleUtils.SCHEDULEMAP_TYPE_USER);
          }

          // グループに施設を追加する．（公開スケジュールのみ）
          if ("O".equals(public_flag.toString()) && !(is_span)) {
            for (int i = 0; i < f_size; i++) {
              EipTScheduleMap map = Database.create(EipTScheduleMap.class);
              FacilityResultData frd = (FacilityResultData) facilityList.get(i);
              int facilityid = (int) frd.getFacilityId().getValue();
              // map.setPrimaryKey(newSchedule.getScheduleId(),
              // facilityid);
              map.setEipTSchedule(newSchedule);
              map.setUserId(Integer.valueOf(facilityid));
              // O: 自スケジュール T: 仮スケジュール C: 確定スケジュール
              map.setStatus("O");
              map.setType(ScheduleUtils.SCHEDULEMAP_TYPE_FACILITY);
              map.setEipTCommonCategory(category1);
              map.setCommonCategoryId(Integer.valueOf(1));
            }
          }
        }

        // 登録されていたメンバーと今回追加されたメンバーのユーザー ID を取得する．
        List<?> scheduleMaps = schedule.getEipTScheduleMaps();
        int scheduleMapsSize = scheduleMaps.size();
        List<Integer> memberIdList = new ArrayList<Integer>();
        List<Integer> facilityIdList = new ArrayList<Integer>();
        for (int i = 0; i < scheduleMapsSize; i++) {
          EipTScheduleMap map = (EipTScheduleMap) scheduleMaps.get(i);
          if (ScheduleUtils.SCHEDULEMAP_TYPE_USER.equals(map.getType())) {
            memberIdList.add(map.getUserId());
          } else {
            facilityIdList.add(map.getUserId());
          }
        }
        int memberListSize = memberList.size();
        for (int i = 0; i < memberListSize; i++) {
          int memberId = (int) memberList.get(i).getUserId().getValue();
          if (!isContains(memberIdList, memberId)) {
            memberIdList.add(Integer.valueOf(memberId));
          }
        }
        int memberIdListSize = memberIdList.size();
        int[] memberIds = new int[memberIdListSize];
        for (int i = 0; i < memberIdListSize; i++) {
          memberIds[i] = memberIdList.get(i).intValue();
        }

        int facilityListSize = facilityList.size();
        for (int i = 0; i < facilityListSize; i++) {
          int facilityId =
            (int) ((FacilityResultData) facilityList.get(i))
              .getFacilityId()
              .getValue();
          if (!isContains(facilityIdList, facilityId)) {
            facilityList.add(Integer.valueOf(facilityId));
          }
        }

        int facilityIdListSize = facilityIdList.size();
        int[] facilityIds = new int[facilityIdListSize];
        for (int i = 0; i < facilityIdListSize; i++) {
          facilityIds[i] = facilityIdList.get(i).intValue();
        }

        /* 施設重複判定 */
        if (!ignore_duplicate_facility) {
          if (facilityIdList.size() > 0) {
            if (ScheduleUtils.isDuplicateFacilitySchedule(
              newSchedule,
              facilityIdList,
              schedule.getScheduleId(),
              view_date.getValue())) {
              if (msgList.size() == 0) {
                msgList.add("duplicate_facility");
              }
              Database.rollback();
              return false;
            }
          }
        }

        // ダミーのスケジュールを登録する．
        ScheduleUtils.insertDummySchedule(schedule, ownerid, view_date
          .getValue(), view_date.getValue(), memberIds, facilityIds);
        entity_id = newSchedule.getScheduleId().intValue();
      } else {
        // 予定
        schedule.setName(name.getValue());
        // 場所
        schedule.setPlace(place.getValue());
        // 内容
        schedule.setNote(note.getValue());
        // 公開フラグ
        schedule.setPublicFlag(public_flag.getValue());
        // 共有メンバーによる編集／削除フラグ
        if (schedule.getOwnerId().intValue() == ALEipUtils.getUserId(rundata)
          || schedule.getOwnerId().intValue() == 0) {
          schedule.setEditFlag(edit_flag.getValue());
        }

        // スケジュールの所有ユーザがすでにメンバーから抜けているかを検証する．
        int ownerUserId = schedule.getOwnerId().intValue();
        boolean rejectOwnerUser = false;
        List<?> tmpScheduleMaps = schedule.getEipTScheduleMaps();
        for (int i = 0; i < tmpScheduleMaps.size(); i++) {
          EipTScheduleMap map = (EipTScheduleMap) tmpScheduleMaps.get(i);
          if (ownerUserId == map.getUserId().intValue()
            && "R".equals(map.getStatus())) {
            rejectOwnerUser = true;
            break;
          }
        }
        if (rejectOwnerUser) {
          // if (rejectOwnerUser || !includeOwnerUser) {
          // スケジュールの登録ユーザがすでにメンバーから抜けている場合、
          // 最後に更新した人のユーザ ID をオーナ ID に設定する．
          schedule.setOwnerId(Integer.valueOf(ALEipUtils.getUserId(rundata)));
        }

        // 更新日
        schedule.setUpdateDate(new Date());
        schedule
          .setUpdateUserId(Integer.valueOf(ALEipUtils.getUserId(rundata)));

        if (is_span) {
          schedule.setEndDate(end_date.getValue());
          schedule.setRepeatPattern("S");

          schedule.setStartDate(start_date.getValue());
        } else if (!is_repeat) {
          // 終了日時
          schedule.setEndDate(end_date.getValue());
          schedule.setRepeatPattern("N");

          schedule.setStartDate(start_date.getValue());
        } else {
          char lim = 'N';
          Calendar cal = Calendar.getInstance();
          cal.setTime(end_date.getValue());
          if ("ON".equals(limit_flag.getValue())) {
            lim = 'L';
            cal.set(limit_end_date.getValue().getYear(), limit_end_date
              .getValue()
              .getMonth() - 1, limit_end_date.getValue().getDay());

            ALDateContainer container = limit_start_date.getValue();
            Calendar limitStartCal = Calendar.getInstance();
            limitStartCal.setTime(start_date.getValue());
            limitStartCal.set(Calendar.YEAR, container.getYear());
            limitStartCal.set(Calendar.MONTH, container.getMonth() - 1);
            limitStartCal.set(Calendar.DATE, container.getDay());

            schedule.setStartDate(limitStartCal.getTime());
          } else {
            schedule.setStartDate(start_date.getValue());
          }

          schedule.setEndDate(cal.getTime());
          if ("D".equals(repeat_type.getValue())) {
            String tmpPattern =
              new StringBuffer().append('D').append(lim).toString();
            schedule.setRepeatPattern(tmpPattern);
          } else if ("W".equals(repeat_type.getValue())) {
            String tmpPattern =
              new StringBuffer().append('W').append(
                week_0.getValue() != null ? 1 : 0).append(
                week_1.getValue() != null ? 1 : 0).append(
                week_2.getValue() != null ? 1 : 0).append(
                week_3.getValue() != null ? 1 : 0).append(
                week_4.getValue() != null ? 1 : 0).append(
                week_5.getValue() != null ? 1 : 0).append(
                week_6.getValue() != null ? 1 : 0).append(lim).toString();
            schedule.setRepeatPattern(tmpPattern);

          } else {
            DecimalFormat format = new DecimalFormat("00");
            schedule.setRepeatPattern(new StringBuffer().append('M').append(
              format.format(month_day.getValue())).append(lim).toString());
          }
        }

        List<?> scheduleMaps = schedule.getEipTScheduleMaps();

        Database.deleteAll(scheduleMaps);
        // スケジュールを登録
        // Database.commit();

        int size = memberList.size();
        for (int i = 0; i < size; i++) {
          EipTScheduleMap map = Database.create(EipTScheduleMap.class);
          ALEipUser user = memberList.get(i);
          int userid = (int) user.getUserId().getValue();
          map.setScheduleId(schedule.getScheduleId());
          map.setEipTSchedule(schedule);
          map.setUserId(Integer.valueOf(userid));

          // O: 自スケジュール T: 仮スケジュール C: 確定スケジュール
          if (userid == schedule.getOwnerId().intValue()) {
            map.setStatus("O");
          } else {
            if ("T".equals(change_tmpreserve_flag.getValue())) {
              map.setStatus("T");
            } else {
              EipTScheduleMap tmpMap =
                getScheduleMap(
                  scheduleMaps,
                  userid,
                  ScheduleUtils.SCHEDULEMAP_TYPE_USER);
              if (tmpMap != null) {
                map.setStatus(tmpMap.getStatus());
              } else {
                map.setStatus("T");
              }
            }
          }

          EipTCommonCategory category =
            CommonCategoryUtils.getEipTCommonCategory(common_category_id
              .getValue());
          if (category == null) {
            map.setCommonCategoryId(Integer.valueOf(1));
            map.setEipTSchedule(schedule);
            map.setEipTCommonCategory(category1);
          } else {
            map.setCommonCategoryId(Integer.valueOf((int) (common_category_id
              .getValue())));
            map.setEipTSchedule(schedule);
            map.setEipTCommonCategory(category);
          }

          map.setType(ScheduleUtils.SCHEDULEMAP_TYPE_USER);
          schedule.addToEipTScheduleMaps(map);
        }

        // グループに施設を追加する．（公開スケジュールのみ）
        if ("O".equals(public_flag.toString()) && !(is_span)) {
          for (int i = 0; i < f_size; i++) {
            EipTScheduleMap map = Database.create(EipTScheduleMap.class);
            FacilityResultData frd = (FacilityResultData) facilityList.get(i);
            int facilityid = (int) frd.getFacilityId().getValue();
            map.setEipTSchedule(schedule);
            map.setUserId(Integer.valueOf(facilityid));
            // O: 自スケジュール T: 仮スケジュール C: 確定スケジュール
            map.setStatus("O");
            map.setType(ScheduleUtils.SCHEDULEMAP_TYPE_FACILITY);
            map.setEipTCommonCategory(category1);
            map.setCommonCategoryId(Integer.valueOf(1));
          }
        }
      }

      /* 施設重複判定 */
      if (!ignore_duplicate_facility) {
        if (facilityList.size() > 0) {
          ArrayList<Integer> fids = new ArrayList<Integer>();
          FacilityResultData facility = null;
          int fsize = facilityList.size();
          for (int i = 0; i < fsize; i++) {
            facility = (FacilityResultData) facilityList.get(i);
            fids
              .add(Integer.valueOf((int) facility.getFacilityId().getValue()));
          }
          if (ScheduleUtils.isDuplicateFacilitySchedule(
            schedule,
            fids,
            null,
            null)) {
            if (msgList.size() == 0) {
              msgList.add("duplicate_facility");
            }
            Database.rollback();
            return false;
          }
        }
      }

      // スケジュールを登録
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        schedule.getScheduleId(),
        ALEventlogConstants.PORTLET_TYPE_SCHEDULE,
        schedule.getName());

      /* メンバー全員に新着ポートレット登録 */
      ALAccessControlFactoryService aclservice =
        (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
          .getInstance())
          .getService(ALAccessControlFactoryService.SERVICE_NAME);
      ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();
      List<Integer> userIds =
        aclhandler.getAcceptUserIdsInListExceptLoginUser(
          (int) loginUser.getUserId().getValue(),
          ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_SELF,
          ALAccessControlConstants.VALUE_ACL_DETAIL,
          memberList);

      int size = userIds.size();
      for (int i = 0; i < size; i++) {
        Integer _id = userIds.get(i);
        WhatsNewUtils.insertWhatsNew(
          WhatsNewUtils.WHATS_NEW_TYPE_SCHEDULE,
          entity_id,
          _id.intValue());
      }

    } catch (Exception e) {
      Database.rollback();
      logger.error("[ScheduleFormData]", e);
      throw new ALDBErrorException();
    }

    try {
      // メール送信
      int msgType =
        ALMailUtils.getSendDestType(ALMailUtils.KEY_MSGTYPE_SCHEDULE);
      if (msgType > 0) {
        // パソコンへメールを送信
        List<ALEipUserAddr> destMemberList =
          ALMailUtils.getALEipUserAddrs(memberList, ALEipUtils
            .getUserId(rundata), false);
        String subject =
          "[" + DatabaseOrmService.getInstance().getAlias() + "]スケジュール";

        if (edit_repeat_flag.getValue() == FLAG_EDIT_REPEAT_ONE) {
          for (int i = 0; i < destMemberList.size(); i++) {
            List<ALEipUserAddr> destMember = new ArrayList<ALEipUserAddr>();
            destMember.add(destMemberList.get(i));
            ALMailUtils.sendMailDelegate(
              org_id,
              ALEipUtils.getUserId(rundata),
              destMember,
              subject,
              subject,
              ScheduleUtils.createMsgForPc(rundata, newSchedule, memberList),
              ScheduleUtils.createMsgForCellPhone(
                rundata,
                newSchedule,
                memberList,
                destMember.get(0).getUserId()),
              ALMailUtils.getSendDestType(ALMailUtils.KEY_MSGTYPE_SCHEDULE),
              new ArrayList<String>());
          }
        } else {
          for (int i = 0; i < destMemberList.size(); i++) {
            List<ALEipUserAddr> destMember = new ArrayList<ALEipUserAddr>();
            destMember.add(destMemberList.get(i));
            ALMailUtils.sendMailDelegate(
              org_id,
              ALEipUtils.getUserId(rundata),
              destMember,
              subject,
              subject,
              ScheduleUtils.createMsgForPc(rundata, schedule, memberList),
              ScheduleUtils.createMsgForCellPhone(
                rundata,
                schedule,
                memberList,
                destMember.get(0).getUserId()),
              ALMailUtils.getSendDestType(ALMailUtils.KEY_MSGTYPE_SCHEDULE),
              new ArrayList<String>());
          }
        }
      }
    } catch (Exception ex) {
      msgList.add("メールを送信できませんでした。");
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  private EipTScheduleMap getScheduleMap(List<?> scheduleMaps, int userid,
      String type) {
    EipTScheduleMap map = null;
    int size = scheduleMaps.size();
    for (int i = 0; i < size; i++) {
      map = (EipTScheduleMap) scheduleMaps.get(i);
      if (map.getUserId().intValue() == userid && type.equals(map.getType())) {
        return map;
      }
    }
    return null;
  }

  /*
   * @see
   * com.aimluck.eip.common.ALAbstractFormData#deleteFormData(org.apache.turbine
   * .util.RunData, org.apache.velocity.context.Context, java.util.ArrayList)
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    try {
      // パラメータを取得する．
      if (rundata.getParameters().containsKey("del_member_flag")) {
        del_member_flag.setValue(rundata.getParameters().getString(
          "del_member_flag"));
      }
      if (rundata.getParameters().containsKey("del_range_flag")) {
        del_range_flag.setValue(rundata.getParameters().getString(
          "del_range_flag"));
      }
      int userid = 0; // 削除対象のユーザID
      if (rundata.getParameters().containsKey("userid")) {
        userid = rundata.getParameters().getInt("userid");
      }
      boolean is_facility = false; // 削除のユーザIDが施設かどうか
      if (rundata.getParameters().containsKey("type")) {
        is_facility =
          ScheduleUtils.SCHEDULEMAP_TYPE_FACILITY.equals(rundata
            .getParameters()
            .getString("type"));
      }

      // オブジェクトモデルを取得
      EipTSchedule schedule =
        ScheduleUtils.getEipTSchedule(rundata, context, false);
      if (schedule == null) {
        return false;
      }

      // 共有メンバーを取得する．
      List<ALEipUser> members = ScheduleUtils.getUsers(rundata, context, true);
      if (members != null && members.size() > 0) {
        memberList.addAll(members);
      }
      // 削除権限を検証する．
      boolean acl_delete_other = false;
      ALAccessControlFactoryService aclservice =
        (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
          .getInstance())
          .getService(ALAccessControlFactoryService.SERVICE_NAME);
      ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();
      if (aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_OTHER,
        ALAccessControlConstants.VALUE_ACL_DELETE)) {
        acl_delete_other = true;
      }

      boolean isMember = false;
      int loginuserId = (int) login_user.getUserId().getValue();
      int membersSize = memberList.size();
      ALEipUser eipUser = null;
      for (int i = 0; i < membersSize; i++) {
        eipUser = memberList.get(i);
        if (loginuserId == eipUser.getUserId().getValue()) {
          isMember = true;
          break;
        }
      }
      if (!acl_delete_other
        && !isMember
        && (schedule.getCreateUserId().intValue() != loginuserId)) {
        // TODO: エラー処理
        logger
          .error("[ScheduleFormData] ALPageNotFoundException: The user does not have the auth to delete the schedule.");
        throw new ALPermissionException();
      }

      if (loginuserId != schedule.getOwnerId().intValue()
        && "F".equals(schedule.getEditFlag())
        && FLAG_DEL_MEMBER_ONE != del_member_flag.getValue()) {
        // del_member_flag.setValue(FLAG_DEL_MEMBER_ONE);
        return true;
      }

      int delFlag = -1;
      if (del_member_flag.getValue() == FLAG_DEL_MEMBER_ALL) {
        if (del_range_flag.getValue() == FLAG_DEL_RANGE_ALL) {
          /** 全員の全期間予定削除 */
          delFlag = 0;
        } else {
          /** 全員の一日分予定削除 */
          delFlag = 1;
        }
      } else {
        if (del_range_flag.getValue() == FLAG_DEL_RANGE_ALL) {
          /** 1ユーザの全期間予定削除 */
          // EIP_M_SCHEDULE_MAP の STATUS のみ変更する．
          delFlag = 2;
        } else {
          /** 1ユーザの一日分予定削除 */
          delFlag = 3;
        }
      }
      if (delFlag == 0) {
        deleteSchedule(schedule);
      } else if (delFlag == 1) {
        if (!"N".equals(schedule.getRepeatPattern())) {
          int ownerid = ALEipUtils.getUserId(rundata);
          // ダミーのスケジュールを登録する．
          int memberIdListSize = memberList.size();
          int[] memberIdList = new int[memberIdListSize];
          for (int i = 0; i < memberIdListSize; i++) {
            memberIdList[i] = (int) memberList.get(i).getUserId().getValue();
          }

          // 同時に削除する施設ID一覧を取得する。
          int[] facilityIdList = ScheduleUtils.getFacilityIds(schedule);

          ScheduleUtils.insertDummySchedule(schedule, ownerid, view_date
            .getValue(), view_date.getValue(), memberIdList, facilityIdList);
        }
      } else if (delFlag == 2) {
        List<?> scheduleMaps = ScheduleUtils.getEipTScheduleMaps(schedule);
        if (scheduleMaps != null && scheduleMaps.size() > 0) {
          if (is_facility) {
            // 施設を削除する場合
            for (int i = 0; i < scheduleMaps.size(); i++) {
              EipTScheduleMap scheduleMap =
                (EipTScheduleMap) scheduleMaps.get(i);
              if (ScheduleUtils.SCHEDULEMAP_TYPE_FACILITY.equals(scheduleMap
                .getType())) {
                if (scheduleMap.getUserId().intValue() == userid) {
                  Database.delete(scheduleMap);
                }
              }
            }
          } else {
            // ユーザを削除する場合
            List<EipTScheduleMap> tmpScheduleMaps =
              new ArrayList<EipTScheduleMap>();
            int countRejectSchedule = 0;
            for (int i = 0; i < scheduleMaps.size(); i++) {
              EipTScheduleMap scheduleMap =
                (EipTScheduleMap) scheduleMaps.get(i);
              if (ScheduleUtils.SCHEDULEMAP_TYPE_USER.equals(scheduleMap
                .getType())) {
                tmpScheduleMaps.add(scheduleMap);
                if ("R".equals(scheduleMap.getStatus())) {
                  countRejectSchedule += 1;
                }
              }
            }
            int scheduleMapsSize = tmpScheduleMaps.size();

            if (countRejectSchedule >= scheduleMapsSize - 1) {
              // この schedule ID に関係するスケジュールがすべて reject されたため，
              // すべて削除する．
              deleteSchedule(schedule);
            } else {
              for (int i = 0; i < scheduleMapsSize; i++) {
                EipTScheduleMap scheduleMap = tmpScheduleMaps.get(i);
                if (scheduleMap.getUserId().intValue() == userid) {
                  if ((scheduleMap.getUserId().intValue() == login_user
                    .getUserId()
                    .getValue())
                    || (schedule.getCreateUserId().intValue() == login_user
                      .getUserId()
                      .getValue())) {
                    if ("O".equals(scheduleMap.getStatus())) {
                      schedule.setOwnerId(Integer.valueOf(0));
                      if ("F".equals(schedule.getEditFlag())) {
                        // 削除するユーザーが，スケジュールの登録者であり，
                        // かつ，そのスケジュールの編集権限が他の共有メンバーに与えられていないときには，
                        // そのスケジュールの編集権限を 'T' に設定する．
                        schedule.setEditFlag("T");
                      }
                    }
                    scheduleMap.setStatus("R");
                  }
                }
              }
            }
          }
        }
      } else if (delFlag == 3) {
        if (!"N".equals(schedule.getRepeatPattern())) {
          int ownerid = ALEipUtils.getUserId(rundata);
          if (is_facility) {
            // 施設を削除する場合
            memberList = new ArrayList<ALEipUser>();
            this.loadFormData(rundata, context, msgList);
            for (int i = 0; facilityList.size() > i; i++) {
              if (((FacilityResultData) facilityList.get(i))
                .getFacilityId()
                .getValue() == userid) {
                facilityList.remove(i);
                break;
              }
            }
            edit_repeat_flag.setValue(ScheduleFormData.FLAG_EDIT_REPEAT_ONE);
            this.updateFormData(rundata, context, msgList);
          } else {
            // ユーザを削除する場合
            int[] memberIdList = new int[1];
            memberIdList[0] = userid;
            // ひとりでも他の人の予定が残っていれば、施設予約を削除しない。
            // 参加ユーザー数を調べる
            EipTScheduleMap map;
            int user_count = 0;
            List<?> maps = schedule.getEipTScheduleMaps();
            for (int i = 0; i < maps.size(); i++) {
              map = (EipTScheduleMap) maps.get(i);
              if (map.getType().equals(ScheduleUtils.SCHEDULEMAP_TYPE_USER)) {
                user_count++;
              }
            }
            /** 既に登録されているダミースケジュールを検索する */
            SelectQuery<EipTSchedule> dummy_query =
              new SelectQuery<EipTSchedule>(EipTSchedule.class);
            Expression exp1 =
              ExpressionFactory.matchExp(
                EipTSchedule.PARENT_ID_PROPERTY,
                schedule.getScheduleId());
            Expression exp2 =
              ExpressionFactory.matchExp(
                EipTSchedule.START_DATE_PROPERTY,
                view_date.getValue());
            Expression exp3 =
              ExpressionFactory.matchExp(
                EipTSchedule.END_DATE_PROPERTY,
                view_date.getValue());
            dummy_query.setQualifier(exp1);
            dummy_query.andQualifier(exp2);
            dummy_query.andQualifier(exp3);
            List<EipTSchedule> list = dummy_query.fetchList();
            EipTSchedule dummy;
            EipTScheduleMap dummymap;
            /** ダミー登録されている人数をカウントする */
            int dummy_count = 0;
            for (int i = 0; i < list.size(); i++) {
              dummy = list.get(i);
              List<?> dummymaps = dummy.getEipTScheduleMaps();
              for (int j = 0; j < dummymaps.size(); j++) {
                dummymap = (EipTScheduleMap) dummymaps.get(j);
                if (dummymap.getType().equals(
                  ScheduleUtils.SCHEDULEMAP_TYPE_USER)) {
                  dummy_count++;
                }
              }
            }
            int[] facilityIdList = null;
            if (dummy_count >= user_count - 1) {
              // if( 登録済みダミースケジュール数合計 >= スケジュール参加者数 - 今回予定を消す人 )
              // 全員の予定が消されているので、同時に削除する施設ID一覧を取得する。
              facilityIdList = ScheduleUtils.getFacilityIds(schedule);
            }
            ScheduleUtils.insertDummySchedule(schedule, ownerid, view_date
              .getValue(), view_date.getValue(), memberIdList, facilityIdList);
          }
        }
      } else {
        // orm.doDelete(schedule);
        Database.delete(schedule);
      }
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        schedule.getScheduleId(),
        ALEventlogConstants.PORTLET_TYPE_SCHEDULE,
        schedule.getName());

    } catch (Exception e) {
      Database.rollback();
      // TODO: エラー処理
      logger.error("[ScheduleFormData]", e);
      throw new ALDBErrorException();

    }
    return true;
  }

  /*
   * @see
   * com.aimluck.eip.common.ALAbstractFormData#doViewForm(com.aimluck.eip.modules
   * .actions.common.ALAction, org.apache.turbine.util.RunData,
   * org.apache.velocity.context.Context)
   */
  @Override
  public boolean doViewForm(ALAction action, RunData rundata, Context context) {
    boolean res = super.doViewForm(action, rundata, context);
    // post(action, rundata, context);
    return res;
  }

  /*
   * @see
   * com.aimluck.eip.common.ALAbstractFormData#doInsert(com.aimluck.eip.modules
   * .actions.common.ALAction, org.apache.turbine.util.RunData,
   * org.apache.velocity.context.Context)
   */
  @Override
  public boolean doInsert(ALAction action, RunData rundata, Context context) {
    boolean res = super.doInsert(action, rundata, context);
    // post(action, rundata, context);
    return res;
  }

  /*
   * @see
   * com.aimluck.eip.common.ALAbstractFormData#doUpdate(com.aimluck.eip.modules
   * .actions.common.ALAction, org.apache.turbine.util.RunData,
   * org.apache.velocity.context.Context)
   */
  @Override
  public boolean doUpdate(ALAction action, RunData rundata, Context context) {
    boolean res = super.doUpdate(action, rundata, context);
    // post(action, rundata, context);
    return res;
  }

  private boolean facilityCheckAclPermission(int f_size, List<String> msgList,
      RunData rundata, int aclType) {
    if (f_size > 0) {
      ALAccessControlFactoryService aclservice =
        (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
          .getInstance())
          .getService(ALAccessControlFactoryService.SERVICE_NAME);
      ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

      hasAuthority =
        aclhandler.hasAuthority(
          ALEipUtils.getUserId(rundata),
          ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_FACILITY,
          aclType);

      if (!hasAuthority) {
        msgList.add(" 施設を予約する権限がありません ");
        return false;
      }
    }
    return true;
  }

  @SuppressWarnings("unused")
  private void post(ALAction action, RunData rundata, Context context) {
    selectData = new ScheduleOnedayGroupSelectData();
    selectData.initField();

    if (tmpView != null && !tmpView.equals("")) {
      selectData.setTmpViewDate(tmpView);
    } else {
      start_date.setNotNull(true);
      if (start_date.validate(new ArrayList<String>())) {
        selectData.setTmpViewDate(start_date.toString());
      } else if (tmpStart == null || tmpStart.equals("")) {
        ALDateTimeField now = new ALDateTimeField("yyyy-MM-dd-HH-mm");
        now.setValue(new Date());
        selectData.setTmpViewDate(now.toString());
      } else {
        selectData.setTmpViewDate(tmpStart);
      }
    }

    selectData.doSelectList(action, rundata, context);

    // 期限の調節：終了日を開始日より後の日時にする．
    // try {
    // if (limit_end_date.getValue().getDate().before(
    // limit_start_date.getValue().getDate())) {
    // limit_end_date.setValue(limit_start_date.getValue().getDate());
    // }
    // } catch (Exception ex) {
    // }
  }

  /**
   * 指定した曜日が，選択範囲に入っているかを検証する．
   * 
   * @param selectedWeek
   *          指定曜日
   * @param startWeek
   *          期間開始曜日
   * @param endWeek
   *          期間終了曜日
   * @return 選択範囲に入っている場合，true．
   */
  private boolean includeWeek(int selectedWeek, int startWeek, int endWeek) {
    if (startWeek <= endWeek) {
      if (startWeek <= selectedWeek && selectedWeek <= endWeek) {
        return true;
      } else {
        return false;
      }
    } else {
      if (selectedWeek < startWeek && endWeek < selectedWeek) {
        return false;
      } else {
        return true;
      }
    }
  }

  /**
   * 指定したスケジュールを削除する．
   * 
   * @param schedule
   */
  private void deleteSchedule(EipTSchedule schedule) {
    Integer scheduleId = schedule.getScheduleId();
    Database.delete(schedule);
    // ダミースケジュールの取得
    SelectQuery<EipTSchedule> query =
      new SelectQuery<EipTSchedule>(EipTSchedule.class);
    Expression exp1 =
      ExpressionFactory.matchExp(EipTSchedule.PARENT_ID_PROPERTY, scheduleId);
    query.setQualifier(exp1);
    Expression exp2 =
      ExpressionFactory.matchExp(EipTSchedule.EIP_TSCHEDULE_MAPS_PROPERTY
        + "."
        + EipTScheduleMap.STATUS_PROPERTY, "D");
    query.andQualifier(exp2);
    List<EipTSchedule> dellist = query.fetchList();
    // ダミースケジュールの削除
    Database.deleteAll(dellist);

    // 2007.3.28 ToDo連携
  }

  /**
   * 第一引数のリストに，第二引数で指定したユーザ ID が含まれているかを検証する．
   * 
   * @param memberIdList
   * @param memberId
   * @return
   */
  private boolean isContains(List<Integer> memberIdList, int userId) {
    int size = memberIdList.size();
    Integer tmpInt = null;
    for (int i = 0; i < size; i++) {
      tmpInt = memberIdList.get(i);
      if (userId == tmpInt.intValue()) {
        return true;
      }
    }
    return false;
  }

  /**
   * 開始日時を取得します。
   * 
   * @return
   */
  public ALDateTimeField getStartDate() {
    return start_date;
  }

  /**
   * 開始日時を取得します。
   * 
   * @return
   */
  public ALDateTimeField getStartDateSub() {
    ALDateTimeField tmpDate = new ALDateTimeField("yyyy/MM/dd");
    tmpDate.setValue(start_date.getValue());
    return tmpDate;
  }

  /**
   * 開始日時を取得します。
   * 
   * @return
   */
  public ALDateTimeField getStartDateTime() {
    ALDateTimeField tmpDate = new ALDateTimeField("HH:mm");
    tmpDate.setValue(start_date.getValue());
    return tmpDate;
  }

  /**
   * 終了日時を取得します。
   * 
   * @return
   */
  public ALDateTimeField getEndDate() {
    return end_date;
  }

  /**
   * 終了日時を取得します。
   * 
   * @return
   */
  public ALDateTimeField getEndDateSub() {
    ALDateTimeField tmpDate = new ALDateTimeField("yyyy/MM/dd");
    tmpDate.setValue(end_date.getValue());
    return tmpDate;
  }

  /**
   * 終了日時を取得します。
   * 
   * @return
   */
  public ALDateTimeField getEndDateTime() {
    ALDateTimeField tmpDate = new ALDateTimeField("HH:mm");
    tmpDate.setValue(end_date.getValue());
    return tmpDate;
  }

  /**
   * グループメンバーを取得します。
   * 
   * @return
   */
  public List<ALEipUser> getMemberList() {
    return memberList;
  }

  /**
   * 指定したグループ名のユーザーを取得します。
   * 
   * @param groupname
   * @return
   */
  public List<ALEipUser> getUsers(String groupname) {
    return ALEipUtils.getUsers(groupname);
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
   * 予定を取得します。
   * 
   * @return
   */
  public ALStringField getName() {
    return name;
  }

  /**
   * 内容を取得します。
   * 
   * @return
   */
  public ALStringField getNote() {
    return note;
  }

  /**
   * 場所を取得します。
   * 
   * @return
   */
  public ALStringField getPlace() {
    return place;
  }

  /**
   * 終了日時を取得します。
   * 
   * @return
   */
  public int getCurrentYear() {
    return currentYear;
  }

  /**
   * 
   * @return
   */
  public boolean isMember() {
    return (is_member || memberList.size() > 1);
  }

  /**
   * 
   * @return
   */
  public boolean isOwner() {
    return is_owner;
  }

  /**
   * ログインユーザを取得します。
   * 
   * @return
   */
  public ALEipUser getLoginUser() {
    return login_user;
  }

  /**
   * 編集するスケジュールの1日の情報を取得します。
   * 
   * @return
   */
  public ScheduleOnedayGroupSelectData getSelectData() {
    return selectData;
  }

  /**
   * 公開/非公開フラグを取得します。
   * 
   * @return
   */
  public ALStringField getPublicFlag() {
    return public_flag;
  }

  /**
   * 繰り返すかどうか。
   * 
   * @return
   */
  public boolean isRepeat() {
    return is_repeat;
  }

  /**
   * 期間スケジュールかどうか。
   * 
   * @return
   */
  public boolean isSpan() {
    return is_span;
  }

  /**
   * 期限を取得します。
   * 
   * @return
   */
  public ALDateField getLimitStartDate() {
    return limit_start_date;
  }

  /**
   * 期限を取得します。
   * 
   * @return
   */
  public ALDateTimeField getLimitStartDateSub() {
    ALDateTimeField tmpDate = new ALDateTimeField("yyyy/MM/dd");
    try {
      tmpDate.setValue(limit_start_date.getValue().getDate());
    } catch (Exception e) {
      logger.error("[ScheduleFormData]", e);

    }
    return tmpDate;
  }

  /**
   * 期限を取得します。
   * 
   * @return
   */
  public ALDateField getLimitEndDate() {
    return limit_end_date;
  }

  /**
   * 期限を取得します。
   * 
   * @return
   */
  public ALDateTimeField getLimitEndDateSub() {
    ALDateTimeField tmpDate = new ALDateTimeField("yyyy/MM/dd");
    try {
      tmpDate.setValue(limit_end_date.getValue().getDate());
    } catch (Exception e) {
      logger.error("[ScheduleFormData]", e);

    }
    return tmpDate;
  }

  /**
   * 期限フラグを取得します。
   * 
   * @return
   */
  public ALStringField getLimitFlag() {
    return limit_flag;
  }

  /**
   * 毎月繰り返す日を取得します。
   * 
   * @return
   */
  public ALNumberField getMonthDay() {
    return month_day;
  }

  /**
   * 繰り返しタイプを取得します。
   * 
   * @return
   */
  public ALStringField getRepeatType() {
    return repeat_type;
  }

  /**
   * 繰り返し曜日を取得します。
   * 
   * @return
   */
  public ALStringField getWeek0() {
    return week_0;
  }

  /**
   * 繰り返し曜日を取得します。
   * 
   * @return
   */
  public ALStringField getWeek1() {
    return week_1;
  }

  /**
   * 繰り返し曜日を取得します。
   * 
   * @return
   */
  public ALStringField getWeek2() {
    return week_2;
  }

  /**
   * 繰り返し曜日を取得します。
   * 
   * @return
   */
  public ALStringField getWeek3() {
    return week_3;
  }

  /**
   * 繰り返し曜日を取得します。
   * 
   * @return
   */
  public ALStringField getWeek4() {
    return week_4;
  }

  /**
   * 繰り返し曜日を取得します。
   * 
   * @return
   */
  public ALStringField getWeek5() {
    return week_5;
  }

  /**
   * 繰り返し曜日を取得します。
   * 
   * @return
   */
  public ALStringField getWeek6() {
    return week_6;
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
   * 繰り返しスケジュールの編集フラグ
   * 
   * @return
   */
  public ALNumberField getEditRepeatFlag() {
    return edit_repeat_flag;
  }

  /**
   * 共有メンバーによる編集／削除権限フラグ
   * 
   * @return
   */
  public ALStringField getEditFlag() {
    return edit_flag;
  }

  public ALStringField getChangeTmpreserveFlag() {
    return change_tmpreserve_flag;
  }

  public ALDateTimeField getViewDate() {
    return view_date;
  }

  public int getInt(long num) {
    return (int) num;
  }

  /**
   * 
   * @return
   */
  public boolean isFacility() {
    return (is_facility || facilityList.size() > 0);
  }

  public List<Object> getFacilityList() {
    return facilityList;
  }

  public List<FacilityResultData> getFacilityAllList() {
    return facilityAllList;
  }

  /**
   * 共有カテゴリ ID
   * 
   * @return
   */
  public ALNumberField getCommonCategoryId() {
    return common_category_id;
  }

  public int getMsgType() {
    return msg_type;
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

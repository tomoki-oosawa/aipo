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

import java.lang.reflect.Field;
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

import com.aimluck.commons.field.ALCellDateField;
import com.aimluck.commons.field.ALCellDateTimeField;
import com.aimluck.commons.field.ALCellNumberField;
import com.aimluck.commons.field.ALCellStringField;
import com.aimluck.commons.field.ALDateContainer;
import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.category.util.CommonCategoryUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTCommonCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.facilities.FacilityResultData;
import com.aimluck.eip.mail.util.ALEipUserAddr;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.schedule.util.CellScheduleUtils;
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
public class CellScheduleFormNoteData extends ALAbstractFormData {

  /** <code>logger</code> logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CellScheduleFormNoteData.class.getName());

  /** <code>FLAG_EDIT_REPEAT_DEF</code> デフォルト値（繰り返し編集範囲） */
  private static final int FLAG_EDIT_REPEAT_DEF = -1;

  /** <code>FLAG_EDIT_REPEAT_ALL</code> 個別日程を編集（繰り返し編集範囲） */
  // private static final int FLAG_EDIT_REPEAT_ALL = 0;
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
  private ALCellDateTimeField start_date;

  /** <code>end_date</code> 終了日時 */
  private ALCellDateTimeField end_date;

  /** <code>name</code> 予定 */
  private ALCellStringField name;

  /** <code>place</code> 場所 */
  private ALCellStringField place;

  /** <code>note</code> 内容 */
  private ALCellStringField note;

  /** <code>public_flag</code> 公開/非公開フラグ */
  private ALCellStringField public_flag;

  /** <code>repeat_type</code> 繰り返し種別 */
  private ALCellStringField repeat_type;

  /** <code>week_0</code> 繰り返し曜日 */
  private ALCellStringField week_0;

  /** <code>week_1</code> 繰り返し曜日 */
  private ALCellStringField week_1;

  /** <code>week_2</code> 繰り返し曜日 */
  private ALCellStringField week_2;

  /** <code>week_3</code> 繰り返し曜日 */
  private ALCellStringField week_3;

  /** <code>week_4</code> 繰り返し曜日 */
  private ALCellStringField week_4;

  /** <code>week_5</code> 繰り返し曜日 */
  private ALCellStringField week_5;

  /** <code>week_6</code> 繰り返し曜日 */
  private ALCellStringField week_6;

  /** <code>limit_flag</code> 期限ありなし */
  private ALCellStringField limit_flag;

  /** <code>limit_date</code> 繰り返し期限（開始日） */
  private ALCellDateField limit_start_date;

  /** <code>limit_date</code> 繰り返し期限（終了日） */
  private ALCellDateField limit_end_date;

  /** <code>month_day</code> 繰り返す日 */
  private ALCellNumberField month_day;

  /** <code>memberList</code> メンバーリスト */
  private List<ALEipUser> memberList;

  /** <code>memberList</code> 施設メンバーリスト */
  private List<FacilityResultData> facilityMemberList;

  /** <code>todo_id</code> ToDo ID */
  private ALCellNumberField common_category_id;

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
  private ALCellNumberField edit_repeat_flag;

  /** <code>tmpView</code> 表示する日 */
  private String tmpView;

  /** <code>end_date</code> 終了日時 */
  private ALCellDateTimeField view_date;

  /** <code>edit_control_flag</code> 共有メンバーによる編集／削除権限フラグ */
  private ALCellStringField edit_flag;

  /** <code>enable_change_tmpreserve</code> 仮スケジュールに戻すかどうか */
  private ALCellStringField change_tmpreserve_flag;

  /** <code>del_member_flag</code> [削除フラグ] 共有メンバーを削除するフラグ */
  private ALCellNumberField del_member_flag;

  /** <code>del_range_flag</code> [削除フラグ] 削除範囲のフラグ */
  private ALCellNumberField del_range_flag;

  /** <code>isOwner</code> 所有者かどうか */
  private boolean is_owner;

  protected String entityid;

  private ALCellNumberField del_flag;

  private String aclPortletFeature;

  /**
   * フォームを表示します。
   * 
   * @param action
   * @param rundata
   * @param context
   * @return TRUE 成功 FALSE 失敗
   */
  public boolean doViewForm(ALAction action, RunData rundata, Context context,
      boolean enable_entityid) {
    try {
      if (enable_entityid) {
        init(action, rundata, context);
        boolean isedit =
          (ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID) != null);
        action.setMode(isedit
          ? ALEipConstants.MODE_EDIT_FORM
          : ALEipConstants.MODE_NEW_FORM);
        setMode(action.getMode());
        List<String> msgList = new ArrayList<String>();

        EipTSchedule schedule =
          ScheduleUtils.getEipTSchedule(rundata, context, true);
        List<FacilityResultData> facilityList =
          CellScheduleUtils.getShareFacilityMemberList(rundata);
        context.put("isDuplicateFacility", "false");
        if (facilityList.size() > 0) {
          List<Integer> fids = new ArrayList<Integer>();
          FacilityResultData facility = null;
          int fsize = facilityList.size();
          for (int i = 0; i < fsize; i++) {
            facility = facilityList.get(i);
            fids
              .add(Integer.valueOf((int) facility.getFacilityId().getValue()));
          }
          if (ScheduleUtils.isDuplicateFacilitySchedule(
            schedule,
            fids,
            null,
            null)) {
            context.put("isDuplicateFacility", "true");
          }
        }

        boolean res =
          (setFormData(rundata, context, msgList)
            && ScheduleUtils.validateDelegate(
              getStartDate(),
              getEndDate(),
              getRepeatType(),
              isRepeat(),
              isSpan(),
              getWeek0(),
              getWeek1(),
              getWeek2(),
              getWeek3(),
              getWeek4(),
              getWeek5(),
              getWeek6(),
              getLimitFlag(),
              getLimitStartDate(),
              getLimitEndDate(),
              getMonthDay(),
              getLoginUser(),
              entityid,
              msgList) && loadFormData(rundata, context, msgList));
        action.setResultData(this);
        action.addErrorMessages(msgList);
        action.putData(rundata, context);

        return res;
      } else {

        init(action, rundata, context);
        action.setMode(ALEipConstants.MODE_NEW_FORM);
        List<String> msgList = new ArrayList<String>();

        setFormData(rundata, context, msgList);

        EipTSchedule schedule = Database.create(EipTSchedule.class);

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
          // 繰り返しの期間が設定されている場合
          if ("ON".equals(limit_flag.getValue())) {
            lim = 'L';

            int year = Integer.parseInt(limit_end_date.getYear());
            int month = Integer.parseInt(limit_end_date.getMonth()) - 1;
            int day = Integer.parseInt(limit_end_date.getDay());
            cal.set(year, month, day);

            Calendar limitStartCal = Calendar.getInstance();
            limitStartCal.setTime(start_date.getValue());
            limitStartCal.set(Calendar.YEAR, Integer.parseInt(limit_start_date
              .getYear()));
            limitStartCal.set(Calendar.MONTH, Integer.parseInt(limit_start_date
              .getMonth()) - 1);
            limitStartCal.set(Calendar.DATE, Integer.parseInt(limit_start_date
              .getDay()));
            schedule.setStartDate(limitStartCal.getTime());
            schedule.setEndDate(cal.getTime());
          } else {
            schedule.setStartDate(start_date.getValue());
            schedule.setEndDate(end_date.getValue());
          }
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

        context.put("isDuplicateFacility", "false");
        List<FacilityResultData> facilityList =
          CellScheduleUtils.getShareFacilityMemberList(rundata);
        if (facilityList.size() > 0) {
          List<Integer> fids = new ArrayList<Integer>();
          FacilityResultData facility = null;
          int fsize = facilityList.size();
          for (int i = 0; i < fsize; i++) {
            facility = facilityList.get(i);
            fids
              .add(Integer.valueOf((int) facility.getFacilityId().getValue()));
          }
          if (ScheduleUtils.isDuplicateFacilitySchedule(
            schedule,
            fids,
            null,
            null)) {
            context.put("isDuplicateFacility", "true");
          }
        }

        boolean res =
          (setFormData(rundata, context, msgList) && ScheduleUtils
            .validateDelegate(
              getStartDate(),
              getEndDate(),
              getRepeatType(),
              isRepeat(),
              isSpan(),
              getWeek0(),
              getWeek1(),
              getWeek2(),
              getWeek3(),
              getWeek4(),
              getWeek5(),
              getWeek6(),
              getLimitFlag(),
              getLimitStartDate(),
              getLimitEndDate(),
              getMonthDay(),
              getLoginUser(),
              entityid,
              msgList));
        action.setResultData(this);
        action.addErrorMessages(msgList);
        action.putData(rundata, context);
        return res;
      }
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
   * @param action
   * @param rundata
   * @param context
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    is_repeat = rundata.getParameters().getBoolean("is_repeat");
    is_span = rundata.getParameters().getBoolean("is_span");
    login_user = ALEipUtils.getALEipUser(rundata);

    is_owner = true;

    // スーパークラスのメソッドを呼び出す。
    super.init(action, rundata, context);

    entityid = ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);

    aclPortletFeature = ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_SELF;
  }

  /**
   * パラメータを読み込みます。
   * 
   * @param rundata
   * @param context
   */
  public void setTempView(RunData rundata, Context context) {
    if (rundata.getParameters().getString("view_date") != null) {
      tmpView = rundata.getParameters().getString("view_date");
    }
  }

  /**
   * パラメータを読み込みます。
   * 
   * @param rundata
   * @param context
   */
  public void loadParameters(RunData rundata, Context context,
      ALCellDateTimeField start_date, ALCellDateTimeField end_date) {
    String date =
      ScheduleUtils.translateDate(start_date.getValue(), "yyyy-MM-dd-HH-mm");
    ALEipUtils.setTemp(rundata, context, "tmpStart", date);
    date = ScheduleUtils.translateDate(end_date.getValue(), "yyyy-MM-dd-HH-mm");
    ALEipUtils.setTemp(rundata, context, "tmpEnd", date);
    tmpStart = ALEipUtils.getTemp(rundata, context, "tmpStart");
    tmpEnd = ALEipUtils.getTemp(rundata, context, "tmpEnd");

    ScheduleUtils.loadParametersViewDate(rundata, context, tmpStart, tmpView);
  }

  /**
   * パラメータを読み込みます。
   * 
   * @param rundata
   * @param context
   */
  public void loadParameters(RunData rundata, Context context) {
    ScheduleUtils.loadParametersDelegate(
      rundata,
      context,
      tmpStart,
      tmpEnd,
      tmpView);
  }

  /*
   *
   */
  public void initField() {
    // Date now = new Date();
    Calendar cal = Calendar.getInstance();
    int min = cal.get(Calendar.MINUTE);
    if (min <= 15) {
      cal.set(Calendar.MINUTE, 15);
    } else if (min <= 30) {
      cal.set(Calendar.MINUTE, 30);
    } else if (min <= 45) {
      cal.set(Calendar.MINUTE, 45);
    } else {
      cal.set(Calendar.MINUTE, 60);
    }
    Date now = cal.getTime();

    // 開始日時
    start_date = new ALCellDateTimeField("yyyy-MM-dd-HH-mm");
    if (tmpStart == null || tmpStart.equals("")) {
      start_date.setValue(now);
    } else {
      start_date.setValue(tmpStart);
    }
    start_date.setFieldName("開始日時");
    // 終了日時
    end_date = new ALCellDateTimeField("yyyy-MM-dd-HH-mm");
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
    repeat_type = new ALCellStringField();
    repeat_type.setFieldName("繰り返しタイプ");
    repeat_type.setValue("D");
    repeat_type.setTrim(true);
    // 日
    week_0 = new ALCellStringField();
    week_0.setFieldName("日");
    week_0.setTrim(true);
    // 月
    week_1 = new ALCellStringField();
    week_1.setFieldName("月");
    week_1.setTrim(true);
    // 火
    week_2 = new ALCellStringField();
    week_2.setFieldName("火");
    week_2.setTrim(true);
    // 水
    week_3 = new ALCellStringField();
    week_3.setFieldName("水");
    week_3.setTrim(true);
    // 木
    week_4 = new ALCellStringField();
    week_4.setFieldName("木");
    week_4.setTrim(true);
    // 金
    week_5 = new ALCellStringField();
    week_5.setFieldName("金");
    week_5.setTrim(true);
    // 土
    week_6 = new ALCellStringField();
    week_6.setFieldName("土");
    week_6.setTrim(true);
    // 繰り返し日
    month_day = new ALCellNumberField();
    month_day.setFieldName("毎月の日");
    month_day.limitValue(1, 31);
    // 繰り返しフラグ
    limit_flag = new ALCellStringField();
    limit_flag.setFieldName("繰り返し");
    limit_flag.setValue("OFF");
    limit_flag.setTrim(true);
    // 繰り返し期限
    limit_start_date = new ALCellDateField();
    limit_start_date.setFieldName("繰り返し期限");
    limit_start_date.setValue(start_date.getValue());
    limit_end_date = new ALCellDateField();
    limit_end_date.setFieldName("繰り返し期限");
    ALCellDateTimeField tmp_date = new ALCellDateTimeField("yyyy-MM-dd-HH-mm");
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
    name = new ALCellStringField();
    name.setFieldName("予定");
    name.setTrim(true);
    // 場所
    place = new ALCellStringField();
    place.setFieldName("場所");
    place.setTrim(true);
    // 内容
    note = new ALCellStringField();
    note.setFieldName("内容");
    note.setTrim(false);
    // 公開区分
    public_flag = new ALCellStringField();
    public_flag.setFieldName("公開区分");
    public_flag.setTrim(true);
    public_flag.setValue("O");
    // 繰り返しスケジュールの編集フラグ
    edit_repeat_flag = new ALCellNumberField();
    edit_repeat_flag.setFieldName("繰り返し編集範囲");
    edit_repeat_flag.setValue(FLAG_EDIT_REPEAT_DEF);

    // 指定日時
    view_date = new ALCellDateTimeField("yyyy-MM-dd");
    if (tmpView == null || tmpView.equals("")) {
      view_date.setValue(now);
    } else {
      view_date.setValue(tmpView);
    }
    view_date.setFieldName("指定日時");

    // 共有メンバーによる編集／削除権限フラグ
    edit_flag = new ALCellStringField();
    edit_flag.setFieldName("編集／削除権限");
    edit_flag.setTrim(true);
    edit_flag.setValue("T");

    // 仮スケジュールに戻すかどうか
    change_tmpreserve_flag = new ALCellStringField();
    change_tmpreserve_flag.setFieldName("仮スケジュール");
    change_tmpreserve_flag.setTrim(true);
    change_tmpreserve_flag.setValue("F");

    // メンバーリスト
    memberList = new ArrayList<ALEipUser>();
    // 現在の年（年を選択するリストボックスに利用）
    currentYear = Calendar.getInstance().get(Calendar.YEAR);

    // [削除フラグ] 共有メンバーを削除するフラグ
    del_member_flag = new ALCellNumberField();
    del_member_flag.setValue(FLAG_DEL_MEMBER_ALL);
    // [削除フラグ] 削除範囲のフラグ
    del_range_flag = new ALCellNumberField();
    del_range_flag.setValue(FLAG_DEL_RANGE_ALL);

    del_flag = new ALCellNumberField();
    del_flag.setValue(0);

    facilityMemberList = new ArrayList<FacilityResultData>();

    // 2007.3.28 ToDo連携
    common_category_id = new ALCellNumberField();
    common_category_id.setFieldName("カテゴリ");
    common_category_id.setValue(1);
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    Field[] fields = this.getClass().getDeclaredFields();
    boolean res =
      ScheduleUtils
        .setFormDataDelegate(rundata, context, this, fields, msgList);

    if (!res) {
      return res;
    }
    memberList = CellScheduleUtils.getShareUserMemberList(rundata);
    if (!is_span) {
      Calendar startDate = Calendar.getInstance();
      startDate.setTime(start_date.getValue());

      Calendar endDate = Calendar.getInstance();
      endDate.setTime(end_date.getValue());
      endDate.set(Calendar.YEAR, startDate.get(Calendar.YEAR));
      endDate.set(Calendar.MONTH, startDate.get(Calendar.MONTH));
      endDate.set(Calendar.DATE, startDate.get(Calendar.DATE));
      end_date.setValue(endDate.getTime());

      facilityMemberList =
        CellScheduleUtils.getShareFacilityMemberList(rundata);
    }
    return res;
  }

  /*
   *
   */
  @Override
  protected void setValidator() {
    getMonthDay().setNotNull(true);
    getLimitStartDate().setNotNull(true);
    getLimitEndDate().setNotNull(true);

    // 予定
    getName().setNotNull(true);
    getName().limitMaxLength(50);
    // 場所
    getPlace().limitMaxLength(50);
    // 内容
    getNote().limitMaxLength(1000);
  }

  /**
   * 
   * @param msgList
   * @return
   * @throws ALDBErrorException
   * @throws ALPageNotFoundException
   */
  @Override
  protected boolean validate(List<String> msgList) throws ALDBErrorException,
      ALPageNotFoundException {
    ScheduleUtils.validateDelegate(
      getStartDate(),
      getEndDate(),
      getRepeatType(),
      isRepeat(),
      isSpan(),
      getWeek0(),
      getWeek1(),
      getWeek2(),
      getWeek3(),
      getWeek4(),
      getWeek5(),
      getWeek6(),
      getLimitFlag(),
      getLimitStartDate(),
      getLimitEndDate(),
      getMonthDay(),
      getLoginUser(),
      entityid,
      msgList);

    // 予定
    getName().validate(msgList);
    // 場所
    getPlace().validate(msgList);
    // 内容
    getNote().validate(msgList);
    return (msgList.size() == 0);
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
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
        (record.getOwnerId().longValue() == login_user.getUserId().getValue())
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

    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return true;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALDBErrorException
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) throws ALDBErrorException {
    EipTSchedule schedule = null;
    try {

      // Validate のときに SELECT していることに注意する

      if (isSpan()) {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(start_date.getValue());
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);

        Calendar endDate = Calendar.getInstance();
        endDate.setTime(end_date.getValue());
        endDate.set(Calendar.HOUR_OF_DAY, 0);
        endDate.set(Calendar.MINUTE, 0);

        start_date.setValue(startDate.getTime());
        end_date.setValue(endDate.getTime());
      } else {
        Calendar startcal = new GregorianCalendar();
        startcal.setTime(getStartDate().getValue());
        Calendar endcal = Calendar.getInstance();
        endcal.setTime(getEndDate().getValue());
        endcal.set(Calendar.YEAR, startcal.get(Calendar.YEAR));
        endcal.set(Calendar.MONTH, startcal.get(Calendar.MONTH));
        endcal.set(Calendar.DATE, startcal.get(Calendar.DATE));
        getEndDate().setValue(endcal.getTime());
      }

      int ownerid = ALEipUtils.getUserId(rundata);
      // 新規オブジェクトモデル
      schedule = Database.create(EipTSchedule.class);
      // 親スケジュール ID
      schedule.setParentId(Integer.valueOf(0));
      // 予定
      schedule.setName(getName().getValue());
      // 場所
      schedule.setPlace(getPlace().getValue());
      // 内容
      schedule.setNote(getNote().getValue());
      // 公開フラグ
      schedule.setPublicFlag(getPublicFlag().getValue());
      // 共有メンバーによる編集／削除フラグ
      if (getMemberList().size() > 1) {
        schedule.setEditFlag(getEditFlag().getValue());
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

      if (isSpan()) {
        // 期間スケジュール設定の場合
        schedule.setEndDate(getEndDate().getValue());
        schedule.setRepeatPattern("S");

        schedule.setStartDate(getStartDate().getValue());
      } else if (!isRepeat()) {
        // 終了日時
        schedule.setEndDate(getEndDate().getValue());
        schedule.setRepeatPattern("N");

        schedule.setStartDate(getStartDate().getValue());
      } else {
        // 繰り返しスケジュール設定の場合
        char lim = 'N';
        Calendar cal = Calendar.getInstance();
        cal.setTime(getEndDate().getValue());
        if ("ON".equals(getLimitFlag().getValue())) {
          lim = 'L';
          cal.set(getLimitEndDate().getValue().getYear(), getLimitEndDate()
            .getValue()
            .getMonth() - 1, getLimitEndDate().getValue().getDay());

          ALDateContainer container = getLimitStartDate().getValue();
          Calendar limitStartCal = Calendar.getInstance();
          limitStartCal.setTime(getStartDate().getValue());
          limitStartCal.set(Calendar.YEAR, container.getYear());
          limitStartCal.set(Calendar.MONTH, container.getMonth() - 1);
          limitStartCal.set(Calendar.DATE, container.getDay());
          schedule.setStartDate(limitStartCal.getTime());
        } else {
          schedule.setStartDate(getStartDate().getValue());
        }

        schedule.setEndDate(cal.getTime());
        if ("D".equals(getRepeatType().getValue())) {
          schedule.setRepeatPattern(new StringBuffer()
            .append('D')
            .append(lim)
            .toString());
        } else if ("W".equals(getRepeatType().getValue())) {
          schedule.setRepeatPattern(new StringBuffer().append('W').append(
            getWeek0().getValue() != null ? 1 : 0).append(
            getWeek1().getValue() != null ? 1 : 0).append(
            getWeek2().getValue() != null ? 1 : 0).append(
            getWeek3().getValue() != null ? 1 : 0).append(
            getWeek4().getValue() != null ? 1 : 0).append(
            getWeek5().getValue() != null ? 1 : 0).append(
            getWeek6().getValue() != null ? 1 : 0).append(lim).toString());
        } else {
          DecimalFormat format = new DecimalFormat("00");
          schedule.setRepeatPattern(new StringBuffer().append('M').append(
            format.format(month_day.getValue())).append(lim).toString());
        }
      }

      EipTCommonCategory category1 =
        CommonCategoryUtils.getEipTCommonCategory(Long.valueOf(1));
      // スケジュールを登録
      // orm_schedule.doInsert(schedule);
      int size = getMemberList().size();
      for (int i = 0; i < size; i++) {
        EipTScheduleMap map = Database.create(EipTScheduleMap.class);
        ALEipUser user = getMemberList().get(i);
        int userid = (int) user.getUserId().getValue();
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

      // グループに施設を追加する．
      int f_size = facilityMemberList.size();
      for (int i = 0; i < f_size; i++) {
        EipTScheduleMap map = Database.create(EipTScheduleMap.class);
        FacilityResultData frd = facilityMemberList.get(i);
        int facilityid = (int) frd.getFacilityId().getValue();
        map.setEipTSchedule(schedule);
        map.setUserId(Integer.valueOf(facilityid));
        // O: 自スケジュール T: 仮スケジュール C: 確定スケジュール
        map.setStatus("O");
        map.setType(ScheduleUtils.SCHEDULEMAP_TYPE_FACILITY);
        map.setEipTCommonCategory(category1);
        map.setCommonCategoryId(Integer.valueOf(1));
      }

      Database.commit();

      // イベントログに保存
      ALEipUtils.setTemp(
        rundata,
        context,
        ALEipConstants.MODE,
        ALEipConstants.MODE_INSERT);
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
          (int) login_user.getUserId().getValue(),
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
      logger.error("[CellScheduleFormData]", e);
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

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    EipTSchedule schedule = null;
    try {

      // Validate のときに SELECT していることに注意する

      if (isSpan()) {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(start_date.getValue());
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);

        Calendar endDate = Calendar.getInstance();
        endDate.setTime(end_date.getValue());
        endDate.set(Calendar.HOUR_OF_DAY, 0);
        endDate.set(Calendar.MINUTE, 0);

        start_date.setValue(startDate.getTime());
        end_date.setValue(endDate.getTime());
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

      int ownerid = ALEipUtils.getUserId(rundata);

      // スケジュールのアップデート権限を検証する．
      if (ownerid != schedule.getOwnerId().intValue()
        && "F".equals(schedule.getEditFlag())) {
        // アップデート失敗時は、スケジュールの一覧を表示させる．
        return true;
      }

      // このスケジュールの共有カテゴリの取得
      SelectQuery<EipTScheduleMap> mapquery =
        Database.query(EipTScheduleMap.class);
      Expression mapexp =
        ExpressionFactory.matchExp(
          EipTScheduleMap.SCHEDULE_ID_PROPERTY,
          schedule.getScheduleId());
      mapquery.setQualifier(mapexp);
      List<EipTScheduleMap> list = mapquery.fetchList();
      if (list != null && list.size() > 0) {
        EipTScheduleMap map = list.get(0);
        EipTCommonCategory category = map.getEipTCommonCategory();
        if (category == null) {
          common_category_id.setValue(1);
        } else {
          common_category_id.setValue(category
            .getCommonCategoryId()
            .longValue());
        }
      }

      EipTCommonCategory category1 =
        CommonCategoryUtils.getEipTCommonCategory(Long.valueOf(1));

      // if (is_repeat && edit_repeat_flag.getValue() ==
      // FLAG_EDIT_REPEAT_ONE) {
      if (edit_repeat_flag.getValue() == FLAG_EDIT_REPEAT_ONE) {
        // 繰り返しスケジュールの個別日程を変更する．
        // 新規オブジェクトモデル
        EipTSchedule newSchedule = Database.create(EipTSchedule.class);
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
        // オーナーID
        newSchedule.setOwnerId(Integer.valueOf(ownerid));
        // 作成日
        Date now = new Date();
        newSchedule.setCreateDate(now);
        newSchedule.setCreateUserId(Integer.valueOf(ownerid));
        // 更新日
        newSchedule.setUpdateDate(now);
        newSchedule.setUpdateUserId(Integer.valueOf(ownerid));
        // 終了日時
        newSchedule.setEndDate(end_date.getValue());
        newSchedule.setRepeatPattern("N");
        newSchedule.setStartDate(start_date.getValue());

        // スケジュールを登録
        // orm_schedule.doInsert(newSchedule);

        int allsize = memberList.size() + facilityMemberList.size();
        if (allsize > 0) {
          int size = memberList.size();
          @SuppressWarnings("unchecked")
          List<EipTScheduleMap> scheduleMaps = schedule.getEipTScheduleMaps();
          for (int i = 0; i < size; i++) {
            EipTScheduleMap map = Database.create(EipTScheduleMap.class);
            ALEipUser user = memberList.get(i);
            int userid = (int) user.getUserId().getValue();
            // map.setPrimaryKey(newSchedule.getScheduleId(),
            // userid);
            map.setEipTSchedule(newSchedule);
            map.setUserId(Integer.valueOf(userid));
            // O: 自スケジュール T: 仮スケジュール C: 確定スケジュール
            if (userid == ALEipUtils.getUserId(rundata)) {
              map.setStatus("O");
            } else {
              if ("T".equals(change_tmpreserve_flag.getValue())) {
                map.setStatus("T");
              } else {
                EipTScheduleMap tmpMap = getScheduleMap(scheduleMaps, userid);
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

          // グループに施設を追加する．
          int f_size = facilityMemberList.size();
          for (int i = 0; i < f_size; i++) {
            EipTScheduleMap map = Database.create(EipTScheduleMap.class);
            FacilityResultData frd = facilityMemberList.get(i);
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

        // 登録されていたメンバーと今回追加されたメンバーのユーザー ID を取得する．
        List<?> scheduleMaps = schedule.getEipTScheduleMaps();
        int scheduleMapsSize = scheduleMaps.size();
        List<Integer> memberIdList = new ArrayList<Integer>();
        for (int i = 0; i < scheduleMapsSize; i++) {
          memberIdList.add(((EipTScheduleMap) scheduleMaps.get(i)).getUserId());
        }
        int memberListSize = memberList.size();
        for (int i = 0; i < memberListSize; i++) {
          int memberId = (int) memberList.get(i).getUserId().getValue();
          if (!ScheduleUtils.isContains(memberIdList, memberId)) {
            memberIdList.add(Integer.valueOf(memberId));
          }
        }
        int memberIdListSize = memberIdList.size();
        int[] memberIds = new int[memberIdListSize];
        for (int i = 0; i < memberIdListSize; i++) {
          memberIds[i] = memberIdList.get(i).intValue();
        }
        // ダミーのスケジュールを登録する．
        ScheduleUtils.insertDummySchedule(schedule, ownerid, view_date
          .getValue(), view_date.getValue(), memberIds);
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
        // 今回のアップデートでスケジュールの所有者がメンバーから抜けているかを検証する．
        boolean includeOwnerUser = false;
        int membersSize = memberList.size();
        ALEipUser eipUser = null;
        for (int i = 0; i < membersSize; i++) {
          eipUser = memberList.get(i);
          if (ownerUserId == eipUser.getUserId().getValue()) {
            includeOwnerUser = true;
            break;
          }
        }
        if (rejectOwnerUser || !includeOwnerUser) {
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

        SelectQuery<EipTScheduleMap> query =
          Database.query(EipTScheduleMap.class);
        Expression exp =
          ExpressionFactory.matchExp(
            EipTScheduleMap.SCHEDULE_ID_PROPERTY,
            schedule.getScheduleId());
        query.setQualifier(exp);
        List<EipTScheduleMap> schedulemaps = query.fetchList();
        Database.deleteAll(schedulemaps);

        int size = memberList.size();
        for (int i = 0; i < size; i++) {
          EipTScheduleMap map = Database.create(EipTScheduleMap.class);
          ALEipUser user = memberList.get(i);
          int userid = (int) user.getUserId().getValue();
          map.setEipTSchedule(schedule);
          map.setUserId(Integer.valueOf(userid));
          // O: 自スケジュール T: 仮スケジュール C: 確定スケジュール
          if (userid == schedule.getOwnerId().intValue()) {
            map.setStatus("O");
          } else {
            if ("T".equals(change_tmpreserve_flag.getValue())) {
              map.setStatus("T");
            } else {
              EipTScheduleMap tmpMap = getScheduleMap(schedulemaps, userid);
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
        }

        // グループに施設を追加する．
        int f_size = facilityMemberList.size();
        for (int i = 0; i < f_size; i++) {
          EipTScheduleMap map = Database.create(EipTScheduleMap.class);
          FacilityResultData frd = facilityMemberList.get(i);
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

      Database.commit();

      // イベントログに保存
      ALEipUtils.setTemp(
        rundata,
        context,
        ALEipConstants.MODE,
        ALEipConstants.MODE_UPDATE);
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
          (int) login_user.getUserId().getValue(),
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
            destMemberList,
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

  private EipTScheduleMap getScheduleMap(List<EipTScheduleMap> scheduleMaps,
      int userid) {
    EipTScheduleMap map = null;
    int size = scheduleMaps.size();
    for (int i = 0; i < size; i++) {
      map = scheduleMaps.get(i);
      if (map.getUserId().intValue() == userid) {
        return map;
      }
    }
    return null;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    try {
      // パラメータを取得する．
      if (rundata.getParameters().containsKey("del_flag")) {
        del_flag.setValue(rundata.getParameters().getString("del_flag"));
      }
      // パラメータの設定
      if (del_flag.getValue() == 1) {
        del_member_flag.setValue(0);
        del_range_flag.setValue(1);
      } else if (del_flag.getValue() == 2) {
        del_member_flag.setValue(1);
        del_range_flag.setValue(0);
      } else if (del_flag.getValue() == 3) {
        del_member_flag.setValue(1);
        del_range_flag.setValue(1);
      } else {
        // del_flag.getValue() == 0 の場合
        del_member_flag.setValue(0);
        del_range_flag.setValue(0);
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
      if (!isMember) {
        logger
          .error("[ScheduleFormData] ALPageNotFoundException: The user does not have the auth to delete the schedule.");
        throw new ALPageNotFoundException();
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
          delFlag = 0;
        } else {
          // del_range_flag.getValue() == FLAG_DEL_RANGE_ONE
          delFlag = 1;
        }
      } else {
        // del_member_flag.getValue() == FLAG_DEL_MEMBER_ONE
        // EIP_M_SCHEDULE_MAP の STATUS のみ変更する．
        if (del_range_flag.getValue() == FLAG_DEL_RANGE_ALL) {
          delFlag = 2;
        } else {
          // del_range_flag.getValue() == FLAG_DEL_RANGE_ONE
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
          ScheduleUtils.insertDummySchedule(schedule, ownerid, view_date
            .getValue(), view_date.getValue(), memberIdList);
        }
      } else if (delFlag == 2) {
        List<EipTScheduleMap> scheduleMaps =
          ScheduleUtils.getEipTScheduleMaps(schedule);
        schedule.getScheduleId();

        if (scheduleMaps != null && scheduleMaps.size() > 0) {
          int countRejectSchedule = 0;
          List<EipTScheduleMap> tmpScheduleMap =
            new ArrayList<EipTScheduleMap>();
          for (int i = 0; i < scheduleMaps.size(); i++) {
            EipTScheduleMap scheduleMap = scheduleMaps.get(i);
            if (ScheduleUtils.SCHEDULEMAP_TYPE_USER.equals(scheduleMap
              .getType())) {
              tmpScheduleMap.add(scheduleMap);
              if ("R".equals(scheduleMap.getStatus())) {
                countRejectSchedule += 1;
              }
            }
          }
          int scheduleMapsSize = tmpScheduleMap.size();

          if (countRejectSchedule >= scheduleMapsSize - 1) {
            // この schedule ID に関係するスケジュールがすべて reject されたため，
            // すべて削除する．
            deleteSchedule(schedule);
          } else {
            for (int i = 0; i < scheduleMapsSize; i++) {
              EipTScheduleMap scheduleMap = scheduleMaps.get(i);

              if (scheduleMap.getUserId().intValue() == login_user
                .getUserId()
                .getValue()) {
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

      } else if (delFlag == 3) {
        if (!"N".equals(schedule.getRepeatPattern())) {
          int ownerid = ALEipUtils.getUserId(rundata);
          // 共有メンバーとしてログインユーザのみ設定する．
          memberList.clear();
          memberList.add(login_user);
          // ダミーのスケジュールを登録する．
          int memberIdListSize = memberList.size();
          int[] memberIdList = new int[memberIdListSize];
          for (int i = 0; i < memberIdListSize; i++) {
            memberIdList[i] = (int) memberList.get(i).getUserId().getValue();
          }
          ScheduleUtils.insertDummySchedule(schedule, ownerid, view_date
            .getValue(), view_date.getValue(), memberIdList);
        }
      } else {
        // orm_schedule.doDelete(schedule);
        Database.delete(schedule);
      }

      Database.commit();

      // イベントログに保存
      ALEipUtils.setTemp(
        rundata,
        context,
        ALEipConstants.MODE,
        ALEipConstants.MODE_DELETE);
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        schedule.getScheduleId(),
        ALEventlogConstants.PORTLET_TYPE_SCHEDULE,
        schedule.getName());

    } catch (Exception e) {
      Database.rollback();
      logger.error("[ScheduleFormData]", e);
      throw new ALDBErrorException();

    }
    return true;
  }

  /**
   * 指定したスケジュールを削除する．
   * 
   * @param schedule
   */
  private void deleteSchedule(EipTSchedule schedule) {
    int scheduleId = schedule.getScheduleId().intValue();
    // orm_schedule.doDelete(schedule);
    Database.delete(schedule);

    // ダミースケジュールの取得
    SelectQuery<EipTSchedule> query = Database.query(EipTSchedule.class);
    Expression exp1 =
      ExpressionFactory.matchExp(EipTSchedule.PARENT_ID_PROPERTY, Integer
        .valueOf(scheduleId));
    query.setQualifier(exp1);
    Expression exp2 =
      ExpressionFactory.matchExp(EipTSchedule.EIP_TSCHEDULE_MAPS_PROPERTY
        + "."
        + EipTScheduleMap.STATUS_PROPERTY, "D");
    query.andQualifier(exp2);
    List<EipTSchedule> dellist = query.fetchList();
    // ダミースケジュールの削除
    if (dellist != null && dellist.size() > 0) {
      Database.deleteAll(dellist);
    }
  }

  public void setScheduleType(boolean is_repeat, boolean is_span) {
    this.is_repeat = is_repeat;
    this.is_span = is_span;
  }

  /**
   * 開始日時を取得します。
   * 
   * @return
   */
  public ALCellDateTimeField getStartDate() {
    return start_date;
  }

  public String getStartDateDate() {
    try {
      return ScheduleUtils.translateDate(start_date.getValue(), "yyyyMMdd");
    } catch (Exception e) {
      return "";
    }
  }

  public String getStartDateTime() {
    try {
      return ScheduleUtils.translateDate(start_date.getValue(), "HHmm");
    } catch (Exception e) {
      return "";
    }
  }

  public String getStartDateDate2() {
    try {
      Calendar cal = Calendar.getInstance();
      cal.setTime(start_date.getValue());
      return ScheduleUtils.translateDate(start_date.getValue(), "yyyy/MM/dd")
        + ALDateUtil.getDayOfWeek(cal);
    } catch (Exception e) {
      return "";
    }
  }

  public String getStartDateTime2() {
    try {
      return ScheduleUtils.translateDate(start_date.getValue(), "HH:mm");
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * 終了日時を取得します。
   * 
   * @return
   */
  public ALCellDateTimeField getEndDate() {
    return end_date;
  }

  public String getEndDateDate() {
    try {
      return ScheduleUtils.translateDate(end_date.getValue(), "yyyyMMdd");
    } catch (Exception e) {
      return "";
    }
  }

  public String getEndDateTime() {
    try {
      return ScheduleUtils.translateDate(end_date.getValue(), "HHmm");
    } catch (Exception e) {
      return "";
    }
  }

  public String getEndDateDate2() {
    try {
      Calendar cal = Calendar.getInstance();
      cal.setTime(end_date.getValue());
      return ScheduleUtils.translateDate(end_date.getValue(), "yyyy/MM/dd")
        + ALDateUtil.getDayOfWeek(cal);
    } catch (Exception e) {
      return "";
    }
  }

  public String getEndDateTime2() {
    try {
      return ScheduleUtils.translateDate(end_date.getValue(), "HH:mm");
    } catch (Exception e) {
      return "";
    }
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
  public ALCellStringField getName() {
    return name;
  }

  /**
   * 内容を取得します。
   * 
   * @return
   */
  public ALCellStringField getNote() {
    return note;
  }

  /**
   * 場所を取得します。
   * 
   * @return
   */
  public ALCellStringField getPlace() {
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
  public ALCellStringField getPublicFlag() {
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
  public ALCellDateField getLimitStartDate() {
    return limit_start_date;
  }

  public String getLimitStartDateDate() {
    try {
      return ScheduleUtils.translateDate(
        limit_start_date.getValue().getDate(),
        "yyyyMMdd");
    } catch (Exception e) {
      return "";
    }
  }

  public String getLimitStartDateDate2() {
    try {
      return ScheduleUtils.translateDate(
        limit_start_date.getValue().getDate(),
        "yyyy/MM/dd");
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * 期限を取得します。
   * 
   * @return
   */
  public ALCellDateField getLimitEndDate() {
    return limit_end_date;
  }

  public String getLimitEndDateDate() {
    try {
      return ScheduleUtils.translateDate(
        limit_end_date.getValue().getDate(),
        "yyyyMMdd");
    } catch (Exception e) {
      return "";
    }
  }

  public String getLimitEndDateDate2() {
    try {
      return ScheduleUtils.translateDate(
        limit_end_date.getValue().getDate(),
        "yyyy/MM/dd");
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * 期限フラグを取得します。
   * 
   * @return
   */
  public ALCellStringField getLimitFlag() {
    return limit_flag;
  }

  /**
   * 毎月繰り返す日を取得します。
   * 
   * @return
   */
  public ALCellNumberField getMonthDay() {
    return month_day;
  }

  /**
   * 繰り返しタイプを取得します。
   * 
   * @return
   */
  public ALCellStringField getRepeatType() {
    return repeat_type;
  }

  /**
   * 繰り返し曜日を取得します。
   * 
   * @return
   */
  public ALCellStringField getWeek0() {
    return week_0;
  }

  /**
   * 繰り返し曜日を取得します。
   * 
   * @return
   */
  public ALCellStringField getWeek1() {
    return week_1;
  }

  /**
   * 繰り返し曜日を取得します。
   * 
   * @return
   */
  public ALCellStringField getWeek2() {
    return week_2;
  }

  /**
   * 繰り返し曜日を取得します。
   * 
   * @return
   */
  public ALCellStringField getWeek3() {
    return week_3;
  }

  /**
   * 繰り返し曜日を取得します。
   * 
   * @return
   */
  public ALCellStringField getWeek4() {
    return week_4;
  }

  /**
   * 繰り返し曜日を取得します。
   * 
   * @return
   */
  public ALCellStringField getWeek5() {
    return week_5;
  }

  /**
   * 繰り返し曜日を取得します。
   * 
   * @return
   */
  public ALCellStringField getWeek6() {
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
  public ALCellNumberField getEditRepeatFlag() {
    return edit_repeat_flag;
  }

  /**
   * 共有メンバーによる編集／削除権限フラグ
   * 
   * @return
   */
  public ALCellStringField getEditFlag() {
    return edit_flag;
  }

  public ALCellStringField getChangeTmpreserveFlag() {
    return change_tmpreserve_flag;
  }

  public ALCellDateTimeField getViewDate() {
    return view_date;
  }

  public int getInt(long num) {
    return (int) num;
  }

  public void setScheduleType(RunData rundata) {
    setScheduleType(rundata.getParameters().getBoolean("is_repeat"), rundata
      .getParameters()
      .getBoolean("is_span"));
  }

  public String getEntityId() {
    return entityid;
  }

  public List<FacilityResultData> getFacilityMemberList() {
    return facilityMemberList;
  }

  @Override
  public String getAclPortletFeature() {
    return aclPortletFeature;
  }

}

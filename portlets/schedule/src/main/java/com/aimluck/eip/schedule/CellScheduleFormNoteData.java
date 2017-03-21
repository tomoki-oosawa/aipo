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

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALCellNumberField;
import com.aimluck.commons.field.ALDateContainer;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALDeleteFileUtil;
import com.aimluck.eip.category.util.CommonCategoryUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTCommonCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleFile;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALFileNotRemovedException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.facilities.FacilityResultData;
import com.aimluck.eip.mail.ALAdminMailContext;
import com.aimluck.eip.mail.ALAdminMailMessage;
import com.aimluck.eip.mail.ALMailService;
import com.aimluck.eip.mail.util.ALEipUserAddr;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.schedule.util.CellScheduleUtils;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.services.reminder.ALReminderHandler.ReminderCategory;
import com.aimluck.eip.services.reminder.ALReminderService;
import com.aimluck.eip.services.reminder.model.ALReminderDefaultItem;
import com.aimluck.eip.services.reminder.model.ALReminderItem;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * スケジュールのフォームデータを管理するクラスです。
 *
 */
public class CellScheduleFormNoteData extends AbstractCellScheduleFormData {

  /** <code>logger</code> logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CellScheduleFormNoteData.class.getName());

  /** <code>name</code> タイトル */
  private ALStringField name;

  /** <code>place</code> 場所 */
  private ALStringField place;

  /** <code>note</code> 内容 */
  private ALStringField note;

  /** <code>public_flag</code> 公開/非公開フラグ */
  private ALStringField public_flag;

  /** <code>todo_id</code> ToDo ID */
  private ALCellNumberField common_category_id;

  /** <code>groups</code> グループ */
  private List<ALEipGroup> groups;

  /** <code>del_member_flag</code> [削除フラグ] 共有メンバーを削除するフラグ */
  private ALCellNumberField del_member_flag;

  /** <code>del_range_flag</code> [削除フラグ] 削除範囲のフラグ */
  private ALCellNumberField del_range_flag;

  private ALCellNumberField del_flag;

  private String orgId;

  /**
   * フォームを表示します。
   *
   * @param action
   * @param rundata
   * @param context
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  public boolean doViewForm(ALAction action, RunData rundata, Context context) {
    try {
      init(action, rundata, context);
      boolean isedit =
        (rundata.getParameters().containsKey(ALEipConstants.ENTITY_ID) && !"new"
          .equals(rundata.getParameters().getString(ALEipConstants.ENTITY_ID)));
      if (is_copy) {
        isedit = false;
      }
      action.setMode(isedit
        ? ALEipConstants.MODE_EDIT_FORM
        : ALEipConstants.MODE_NEW_FORM);
      setMode(action.getMode());
      List<String> msgList = new ArrayList<String>();

      EipTSchedule schedule;
      boolean res = true;
      if (isedit || is_copy) {
        schedule = ScheduleUtils.getEipTSchedule(rundata, context, false);
        res = loadFormData(rundata, context, msgList);
      } else {
        setFormData(rundata, context, msgList);
        schedule = Database.create(EipTSchedule.class);
      }

      String schedule_type = getScheduleType().getValue();
      if (CellScheduleUtils.SCHEDULE_TYPE_SPAN.equals(schedule_type)) {
        schedule.setStartDate(form_data.getStartDate().getValue());
        schedule.setEndDate(form_data.getEndDate().getValue());
        schedule.setRepeatPattern("S");
      } else if (CellScheduleUtils.SCHEDULE_TYPE_ONEDAY.equals(schedule_type)) {
        schedule.setStartDate(form_data.getStartDate().getValue());
        schedule.setEndDate(form_data.getEndDate().getValue());
        schedule.setRepeatPattern("N");
      } else {
        // 繰り返しスケジュール設定の場合
        char lim = 'N';
        Calendar cal = Calendar.getInstance();
        // 繰り返しの期間が設定されている場合
        if ("ON".equals(form_data.getLimitFlag().getValue())) {
          lim = 'L';

          int year = Integer.parseInt(form_data.getLimitEndDate().getYear());
          int month =
            Integer.parseInt(form_data.getLimitEndDate().getMonth()) - 1;
          int day = Integer.parseInt(form_data.getLimitEndDate().getDay());
          cal.set(year, month, day);

          Calendar limitStartCal = Calendar.getInstance();
          limitStartCal.setTime(form_data.getStartDate().getValue());
          limitStartCal.set(Calendar.YEAR, Integer.parseInt(form_data
            .getLimitStartDate()
            .getYear()));
          limitStartCal.set(Calendar.MONTH, Integer.parseInt(form_data
            .getLimitStartDate()
            .getMonth()) - 1);
          limitStartCal.set(Calendar.DATE, Integer.parseInt(form_data
            .getLimitStartDate()
            .getDay()));
          schedule.setStartDate(limitStartCal.getTime());
          schedule.setEndDate(cal.getTime());
        } else {
          schedule.setStartDate(form_data.getStartDate().getValue());
          schedule.setEndDate(form_data.getEndDate().getValue());
        }
        if ("D".equals(form_data.getRepeatType().getValue())) {
          schedule.setRepeatPattern(new StringBuffer()
            .append('D')
            .append(lim)
            .toString());
        } else if ("W".equals(form_data.getRepeatType().getValue())) {
          if (form_data.getRepeatWeek().getValue().equals("0")) {
            schedule.setRepeatPattern(new StringBuffer()
              .append('W')
              .append(form_data.getWeek0().getValue() != null ? 1 : 0)
              .append(form_data.getWeek1().getValue() != null ? 1 : 0)
              .append(form_data.getWeek2().getValue() != null ? 1 : 0)
              .append(form_data.getWeek3().getValue() != null ? 1 : 0)
              .append(form_data.getWeek4().getValue() != null ? 1 : 0)
              .append(form_data.getWeek5().getValue() != null ? 1 : 0)
              .append(form_data.getWeek6().getValue() != null ? 1 : 0)
              .append(lim)
              .toString());
          } else {
            schedule.setRepeatPattern(new StringBuffer().append('W').append(
              form_data.getWeek0().getValue() != null ? 1 : 0).append(
              form_data.getWeek1().getValue() != null ? 1 : 0).append(
              form_data.getWeek2().getValue() != null ? 1 : 0).append(
              form_data.getWeek3().getValue() != null ? 1 : 0).append(
              form_data.getWeek4().getValue() != null ? 1 : 0).append(
              form_data.getWeek5().getValue() != null ? 1 : 0).append(
              form_data.getWeek6().getValue() != null ? 1 : 0).append(
              form_data.getRepeatWeek().getValue()).append(lim).toString());
          }

        } else if ("M".equals(form_data.getRepeatType().getValue())) {
          DecimalFormat format = new DecimalFormat("00");
          String month_day = format.format(form_data.getMonthDay().getValue());
          if ("32".equals(month_day)) {
            schedule.setRepeatPattern(new StringBuffer().append('M').append(
              "XX").append(lim).toString());
          } else {
            schedule.setRepeatPattern(new StringBuffer().append('M').append(
              month_day).append(lim).toString());
          }

        } else {
          DecimalFormat format = new DecimalFormat("00");
          schedule.setRepeatPattern(new StringBuffer()
            .append('Y')
            .append(format.format(form_data.getYearMonth().getValue()))
            .append(format.format(form_data.getYearDay().getValue()))
            .append(lim)
            .toString());
        }
      }

      context.put("isDuplicateFacility", "false");
      List<FacilityResultData> facilityList =
        CellScheduleUtils.getShareFacilityMemberList(rundata);
      if (facilityList.size() > 0) {
        List<Integer> fids = new ArrayList<Integer>();
        for (FacilityResultData facility : facilityList) {
          fids.add(Integer.valueOf((int) facility.getFacilityId().getValue()));
        }
        if (ScheduleUtils.isDuplicateFacilitySchedule(
          schedule,
          fids,
          null,
          null)) {
          context.put("isDuplicateFacility", "true");
        }
      }
      // 設備のアクセスコントロールのチェック
      int acltype =
        (ALEipConstants.MODE_NEW_FORM.equals(getMode()))
          ? ALAccessControlConstants.VALUE_ACL_INSERT
          : ALAccessControlConstants.VALUE_ACL_UPDATE;
      if (!facilityCheckAclPermission(rundata, acltype)) {
        if (acltype == ALAccessControlConstants.VALUE_ACL_UPDATE) {
          int[] old_ids = ScheduleUtils.getFacilityIds(schedule);
          boolean check = false;
          if (old_ids.length != facilityList.size()) {
            msgList.add(ALLocalizationUtils
              .getl10n("SCHEDULE_NO_PERMISSION_TO_MAKE_A_RESERVATION"));
            res = false;
          } else {
            for (int old_id : old_ids) {
              for (Object record : facilityList) {
                FacilityResultData frd = (FacilityResultData) record;
                int facilityid = (int) frd.getFacilityId().getValue();
                if (old_id == facilityid) {
                  check = true;
                  break;
                }
              }
              if (!check) {
                msgList.add(ALLocalizationUtils
                  .getl10n("SCHEDULE_NO_PERMISSION_TO_MAKE_A_RESERVATION"));
                res = false;
              }
              check = false;
            }
          }
        } else {
          if (facilityList.size() > 0) {
            msgList.add(ALLocalizationUtils
              .getl10n("SCHEDULE_NO_PERMISSION_TO_MAKE_A_RESERVATION"));
            res = false;
          }
        }
      }

      res =
        res
          && (form_data.validateDelegate(
            msgList,
            getLoginUser(),
            getEntityId(),
            getScheduleType().getValue()));
      action.setResultData(this);
      action.addErrorMessages(msgList);
      action.putData(rundata, context);
      return res;
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
    super.init(action, rundata, context);

    orgId = Database.getDomainName();

  }

  /*
   *
   */
  @Override
  public void initField() {
    // タイトル
    name = new ALStringField();
    name.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_TITLE"));
    name.setTrim(true);
    // 場所
    place = new ALStringField();
    place.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_PLACE"));
    place.setTrim(true);
    // 内容
    note = new ALStringField();
    note
      .setFieldName(ALLocalizationUtils.getl10n("SCHEDULE_SETFIELDNAME_NOTE"));
    note.setTrim(false);
    // 公開区分
    public_flag = new ALStringField();
    public_flag.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_PUBLIC"));
    public_flag.setTrim(true);
    public_flag.setValue("O");

    // [削除フラグ] 共有メンバーを削除するフラグ
    del_member_flag = new ALCellNumberField();
    del_member_flag.setValue(CellScheduleUtils.FLAG_DEL_MEMBER_ALL);
    // [削除フラグ] 削除範囲のフラグ
    del_range_flag = new ALCellNumberField();
    del_range_flag.setValue(CellScheduleUtils.FLAG_DEL_RANGE_ALL);

    del_flag = new ALCellNumberField();
    del_flag.setValue(0);

    // 2007.3.28 ToDo連携
    common_category_id = new ALCellNumberField();
    common_category_id.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_SETFIELDNAME_CATEGORY"));
    common_category_id.setValue(1);

    super.initField();
  }

  /*
   *
   */
  @Override
  protected void setValidator() {

    // タイトル
    getName().setNotNull(true);
    getName().limitMaxLength(50);

    // 場所
    getPlace().limitMaxLength(50);

    // 内容
    getNote().limitMaxLength(1000);

    super.setValidator();
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
    // タイトル
    getName().validate(msgList);

    // 場所
    getPlace().validate(msgList);

    // 内容
    getNote().validate(msgList);

    // 共有設備を選択している場合、公開区分で完全に隠すを選択できないようにする
    if ("P".equals(public_flag.getValue())
      && form_data.getFacilityMemberList().size() > 0) {
      msgList.add(ALLocalizationUtils.getl10n("SCHEDULE_HIDE"));
    }

    return super.validate(msgList);
  }

  @Override
  protected void loadCustomFormData(EipTSchedule record) {
    // タイトル
    name.setValue(record.getName());

    // 場所
    place.setValue(record.getPlace());

    // 内容
    note.setValue(record.getNote());

    // 公開フラグ
    public_flag.setValue(record.getPublicFlag());

    // 週をsetFormDataで再設定させる
    form_data.getWeek0().setValue("");
    form_data.getWeek1().setValue("");
    form_data.getWeek2().setValue("");
    form_data.getWeek3().setValue("");
    form_data.getWeek4().setValue("");
    form_data.getWeek5().setValue("");
    form_data.getWeek6().setValue("");
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

    return super.setFormData(rundata, context, msgList);
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
        startDate.setTime(form_data.getStartDate().getValue());
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);

        Calendar endDate = Calendar.getInstance();
        endDate.setTime(form_data.getEndDate().getValue());
        endDate.set(Calendar.HOUR_OF_DAY, 0);
        endDate.set(Calendar.MINUTE, 0);

        form_data.getStartDate().setValue(startDate.getTime());
        form_data.getEndDate().setValue(endDate.getTime());
      } else {
        Calendar startcal = new GregorianCalendar();
        startcal.setTime(form_data.getStartDate().getValue());
        Calendar endcal = Calendar.getInstance();
        endcal.setTime(form_data.getEndDate().getValue());
        endcal.set(Calendar.YEAR, startcal.get(Calendar.YEAR));
        endcal.set(Calendar.MONTH, startcal.get(Calendar.MONTH));
        endcal.set(Calendar.DATE, startcal.get(Calendar.DATE));
        form_data.getEndDate().setValue(endcal.getTime());
      }

      int ownerid = ALEipUtils.getUserId(rundata);
      // 新規オブジェクトモデル
      schedule = Database.create(EipTSchedule.class);
      // 親スケジュール ID
      schedule.setParentId(Integer.valueOf(0));
      // タイトル
      schedule.setName(getName().getValue());
      // 場所
      schedule.setPlace(getPlace().getValue());
      // 内容
      schedule.setNote(getNote().getValue());
      // 公開フラグ
      schedule.setPublicFlag(getPublicFlag().getValue());
      // 共有メンバーによる編集／削除フラグ
      if (form_data.getMemberList().size() > 1) {
        schedule.setEditFlag(form_data.getEditFlag().getValue());
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
        schedule.setEndDate(form_data.getEndDate().getValue());
        schedule.setRepeatPattern("S");

        schedule.setStartDate(form_data.getStartDate().getValue());
      } else if (!isRepeat()) {
        // 終了日時
        schedule.setEndDate(form_data.getEndDate().getValue());
        schedule.setRepeatPattern("N");

        schedule.setStartDate(form_data.getStartDate().getValue());
      } else {
        // 繰り返しスケジュール設定の場合
        char lim = 'N';
        Calendar cal = Calendar.getInstance();
        cal.setTime(form_data.getEndDate().getValue());
        if ("ON".equals(form_data.getLimitFlag().getValue())) {
          lim = 'L';
          cal.set(form_data.getLimitEndDate().getValue().getYear(), form_data
            .getLimitEndDate()
            .getValue()
            .getMonth() - 1, form_data.getLimitEndDate().getValue().getDay());

          ALDateContainer container = form_data.getLimitStartDate().getValue();
          Calendar limitStartCal = Calendar.getInstance();
          limitStartCal.setTime(form_data.getStartDate().getValue());
          limitStartCal.set(Calendar.YEAR, container.getYear());
          limitStartCal.set(Calendar.MONTH, container.getMonth() - 1);
          limitStartCal.set(Calendar.DATE, container.getDay());
          schedule.setStartDate(limitStartCal.getTime());
        } else {
          schedule.setStartDate(form_data.getStartDate().getValue());
        }

        schedule.setEndDate(cal.getTime());
        if ("D".equals(form_data.getRepeatType().getValue())) {
          schedule.setRepeatPattern(new StringBuffer()
            .append('D')
            .append(lim)
            .toString());
        } else if ("W".equals(form_data.getRepeatType().getValue())) {
          if ("0".equals(form_data.getRepeatWeek().getValue())) {
            schedule.setRepeatPattern(new StringBuffer()
              .append('W')
              .append(form_data.getWeek0().getValue() != null ? 1 : 0)
              .append(form_data.getWeek1().getValue() != null ? 1 : 0)
              .append(form_data.getWeek2().getValue() != null ? 1 : 0)
              .append(form_data.getWeek3().getValue() != null ? 1 : 0)
              .append(form_data.getWeek4().getValue() != null ? 1 : 0)
              .append(form_data.getWeek5().getValue() != null ? 1 : 0)
              .append(form_data.getWeek6().getValue() != null ? 1 : 0)
              .append(lim)
              .toString());
          } else {
            schedule.setRepeatPattern(new StringBuffer()
              .append('W')
              .append(form_data.getWeek0().getValue() != null ? 1 : 0)
              .append(form_data.getWeek1().getValue() != null ? 1 : 0)
              .append(form_data.getWeek2().getValue() != null ? 1 : 0)
              .append(form_data.getWeek3().getValue() != null ? 1 : 0)
              .append(form_data.getWeek4().getValue() != null ? 1 : 0)
              .append(form_data.getWeek5().getValue() != null ? 1 : 0)
              .append(form_data.getWeek6().getValue() != null ? 1 : 0)
              .append(form_data.getRepeatWeek().getValue().charAt(0))
              .append(lim)
              .toString());
          }
        } else if ("M".equals(form_data.getRepeatType().getValue())) {
          DecimalFormat format = new DecimalFormat("00");
          String month_day = format.format(form_data.getMonthDay().getValue());
          if ("32".equals(month_day)) {
            schedule.setRepeatPattern(new StringBuffer().append('M').append(
              "XX").append(lim).toString());
          } else {
            schedule.setRepeatPattern(new StringBuffer().append('M').append(
              month_day).append(lim).toString());
          }

        } else {
          DecimalFormat format = new DecimalFormat("00");
          schedule.setRepeatPattern(new StringBuffer()
            .append('Y')
            .append(format.format(form_data.getYearMonth().getValue()))
            .append(format.format(form_data.getYearDay().getValue()))
            .append(lim)
            .toString());
        }
      }

      EipTCommonCategory category1 =
        CommonCategoryUtils.getEipTCommonCategory(Long.valueOf(1));

      // ScheduleMapに参加ユーザーを追加する
      for (ALEipUser user : form_data.getMemberList()) {
        int userid = (int) user.getUserId().getValue();

        EipTScheduleMap map = Database.create(EipTScheduleMap.class);
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

      // 完全に隠すスケジュール以外の場合は、グループに設備を追加する
      boolean isFacility = false;
      if ("O".equals(public_flag.getValue())
        || "C".equals(public_flag.getValue())) {
        for (FacilityResultData frd : form_data.getFacilityMemberList()) {
          isFacility = true;
          int facilityid = (int) frd.getFacilityId().getValue();

          EipTScheduleMap map = Database.create(EipTScheduleMap.class);
          map.setEipTSchedule(schedule);
          map.setUserId(Integer.valueOf(facilityid));
          // O: 自スケジュール T: 仮スケジュール C: 確定スケジュール
          map.setStatus("O");
          map.setType(ScheduleUtils.SCHEDULEMAP_TYPE_FACILITY);
          map.setEipTCommonCategory(category1);
          map.setCommonCategoryId(Integer.valueOf(1));
        }
      }
      // 設備のアクセスコントロールのチェック
      if (isFacility
        && !facilityCheckAclPermission(
          rundata,
          ALAccessControlConstants.VALUE_ACL_INSERT)) {
        msgList.add(ALLocalizationUtils
          .getl10n("SCHEDULE_NO_PERMISSION_TO_MAKE_A_RESERVATION"));
        return false;
      }

      Database.commit();

      // アラーム
      if (ALReminderService.isEnabled()) {
        boolean isLimit = false;
        if ("ON".equals(form_data.getLimitFlag().getValue())) {
          isLimit = true;
        }

        // メンバーに対して それぞれのDefaultJob をもとにリマインドを作成
        for (ALEipUser user : form_data.getMemberList()) {
          ALReminderDefaultItem defaultItem =
            ALReminderService.getDefault(orgId, user
              .getUserId()
              .getValueAsString(), ReminderCategory.SCHEDULE);
          // DefaultItemがない場合は通知しない
          if (defaultItem != null && defaultItem.isEnabled()) {
            ScheduleUtils.setupReminderJob(
              Database.getDomainName(),
              user.getUserId().toString(),
              schedule,
              defaultItem.getNotifyTiming(),
              defaultItem.hasNotifyTypeMail(),
              defaultItem.hasNotifyTypeMessage(),
              isRepeat(),
              isLimit,
              isSpan());
          }
        }

      }
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

      // アクティビティ
      ALEipUser loginUser = getLoginUser();
      List<ALEipUser> memberList = form_data.getMemberList();
      if (loginUser != null) {
        String loginName = loginUser.getName().getValue();
        List<String> recipients = new ArrayList<String>();
        for (ALEipUser user : memberList) {
          if (loginUser.getUserId().getValue() != user.getUserId().getValue()) {
            recipients.add(user.getName().getValue());
          }
        }
        ScheduleUtils.createShareScheduleActivity(
          schedule,
          loginName,
          recipients,
          "new",
          ownerid); // createShareScheduleActivity関数に引数1個追加

        // アクティビティが公開スケジュールである場合、「更新情報」に表示させる。
        if ("O".equals(public_flag.toString())) {
          ScheduleUtils.createNewScheduleActivity(
            schedule,
            loginName,
            "new",
            ownerid);
        }
      }
    } catch (RuntimeException e) {
      Database.rollback();
      logger.error("[CellScheduleFormData]", e);
      throw new ALDBErrorException();
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
          ALMailUtils.getALEipUserAddrs(form_data.getMemberList(), ALEipUtils
            .getUserId(rundata), false);
        String subject =
          ALLocalizationUtils.getl10nFormat(
            "SCHEDULE_SUB_SCHEDULE",
            ALOrgUtilsService.getAlias());
        String org_id = ALOrgUtilsService.getAlias();

        // メール送信
        List<ALAdminMailMessage> messageList =
          new ArrayList<ALAdminMailMessage>();
        for (ALEipUserAddr destMember : destMemberList) {
          ALAdminMailMessage message = new ALAdminMailMessage(destMember);
          message.setPcSubject(subject);
          message.setCellularSubject(subject);
          message.setPcBody(ScheduleUtils.createMsg(
            rundata,
            schedule,
            form_data.getMemberList(),
            null,
            "new"));
          message.setCellularBody(ScheduleUtils.createMsg(
            rundata,
            schedule,
            form_data.getMemberList(),
            destMember.getUserId(),
            "new"));
          messageList.add(message);
        }

        ALMailService.sendAdminMailAsync(new ALAdminMailContext(
          org_id,
          ALEipUtils.getUserId(rundata),
          messageList,
          ALMailUtils.getSendDestType(ALMailUtils.KEY_MSGTYPE_SCHEDULE)));
        // msgList.addAll(errors);

      }
    } catch (Exception ex) {
      msgList.add(ALLocalizationUtils.getl10n("SCHEDULE_DONOT_SEND_MAIL"));
      logger.error("schedule", ex);
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
  @SuppressWarnings("unchecked")
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    EipTSchedule schedule = null;
    List<Integer> newmemberList = new ArrayList<Integer>();
    List<ALEipUser> removememberList = new ArrayList<ALEipUser>();
    List<Integer> memberIdsList = new ArrayList<Integer>();
    EipTSchedule newSchedule = null;
    try {

      // Validate のときに SELECT していることに注意する

      if (isSpan()) {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(form_data.getStartDate().getValue());
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);

        Calendar endDate = Calendar.getInstance();
        endDate.setTime(form_data.getEndDate().getValue());
        endDate.set(Calendar.HOUR_OF_DAY, 0);
        endDate.set(Calendar.MINUTE, 0);

        form_data.getStartDate().setValue(startDate.getTime());
        form_data.getEndDate().setValue(endDate.getTime());
      } else {
        Calendar startcal = new GregorianCalendar();
        startcal.setTime(form_data.getStartDate().getValue());
        Calendar endcal = Calendar.getInstance();
        endcal.setTime(form_data.getEndDate().getValue());
        endcal.set(Calendar.YEAR, startcal.get(Calendar.YEAR));
        endcal.set(Calendar.MONTH, startcal.get(Calendar.MONTH));
        endcal.set(Calendar.DATE, startcal.get(Calendar.DATE));
        form_data.getEndDate().setValue(endcal.getTime());
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
      // 設備のアクセスコントロールのチェック
      List<FacilityResultData> facilityList =
        CellScheduleUtils.getShareFacilityMemberList(rundata);
      if (!facilityCheckAclPermission(
        rundata,
        ALAccessControlConstants.VALUE_ACL_UPDATE)) {
        int[] old_ids = ScheduleUtils.getFacilityIds(schedule);
        if (old_ids.length != facilityList.size()) {
          msgList.add(ALLocalizationUtils
            .getl10n("SCHEDULE_NO_PERMISSION_TO_MAKE_A_RESERVATION"));
          return false;
        }
        boolean check = false;
        for (int old_id : old_ids) {
          for (Object record : facilityList) {
            FacilityResultData frd = (FacilityResultData) record;
            int facilityid = (int) frd.getFacilityId().getValue();
            if (old_id == facilityid) {
              check = true;
              break;
            }
          }
          if (!check) {
            msgList.add(ALLocalizationUtils
              .getl10n("SCHEDULE_NO_PERMISSION_TO_MAKE_A_RESERVATION"));
            return false;
          }
          check = false;
        }
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

      // 新たに追加されたメンバーのリストを取得
      List<?> scheduleMapList = schedule.getEipTScheduleMaps();
      List<Integer> oldmemberIdList = new ArrayList<Integer>();
      for (Object record : scheduleMapList) {
        EipTScheduleMap map = (EipTScheduleMap) record;
        if (ScheduleUtils.SCHEDULEMAP_TYPE_USER.equals(map.getType())) {
          oldmemberIdList.add(map.getUserId());
        }
      }
      for (ALEipUser user : form_data.getMemberList()) {
        int memberId = (int) user.getUserId().getValue();
        if (!isContains(oldmemberIdList, memberId)) {
          newmemberList.add(memberId);
        }
        memberIdsList.add(memberId);
      }

      for (Integer memberId : oldmemberIdList) {
        if (!isContains(memberIdsList, memberId)) {
          removememberList.add(ALEipUtils.getALEipUser(memberId));
        }
      }

      if (form_data.getEditRepeatFlag().getValue() == CellScheduleUtils.FLAG_EDIT_REPEAT_ONE) {
        // 繰り返しスケジュールの個別日程を変更する．
        // 新規オブジェクトモデル
        newSchedule = Database.create(EipTSchedule.class);
        // 繰り返しの親スケジュール ID
        newSchedule.setParentId(schedule.getScheduleId());
        // タイトル
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
        newSchedule.setEndDate(form_data.getEndDate().getValue());
        newSchedule.setRepeatPattern("N");
        newSchedule.setStartDate(form_data.getStartDate().getValue());

        int allsize =
          form_data.getMemberList().size()
            + form_data.getFacilityMemberList().size();
        if (allsize > 0) {
          List<EipTScheduleMap> scheduleMaps = schedule.getEipTScheduleMaps();
          for (ALEipUser user : form_data.getMemberList()) {
            int userid = (int) user.getUserId().getValue();

            EipTScheduleMap map = Database.create(EipTScheduleMap.class);
            map.setEipTSchedule(newSchedule);
            map.setUserId(Integer.valueOf(userid));

            // O: 自スケジュール T: 仮スケジュール C: 確定スケジュール
            if (userid == ALEipUtils.getUserId(rundata)) {
              map.setStatus("O");
            } else {
              if ("T".equals(form_data.getChangeTmpreserveFlag().getValue())) {
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

          // グループに設備を追加する．
          if ("O".equals(public_flag.getValue())
            || "C".equals(public_flag.getValue())) {
            for (FacilityResultData frd : form_data.getFacilityMemberList()) {
              int facilityid = (int) frd.getFacilityId().getValue();

              EipTScheduleMap map = Database.create(EipTScheduleMap.class);
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
        List<EipTScheduleMap> scheduleMaps = schedule.getEipTScheduleMaps();
        List<Integer> memberIdList = new ArrayList<Integer>();
        for (EipTScheduleMap map : scheduleMaps) {
          memberIdList.add(map.getUserId());
        }
        int memberListSize = form_data.getMemberList().size();
        for (int i = 0; i < memberListSize; i++) {
          int memberId =
            (int) form_data.getMemberList().get(i).getUserId().getValue();
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
        ScheduleUtils.insertDummySchedule(schedule, ownerid, form_data
          .getViewDate()
          .getValue(), form_data.getViewDate().getValue(), memberIds);
      } else {
        // タイトル
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
          schedule.setEditFlag(form_data.getEditFlag().getValue());
        }

        // スケジュールの所有ユーザがすでにメンバーから抜けているかを検証する．
        int ownerUserId = schedule.getOwnerId().intValue();
        boolean rejectOwnerUser = false;
        List<EipTScheduleMap> tmpScheduleMaps = schedule.getEipTScheduleMaps();
        for (EipTScheduleMap map : tmpScheduleMaps) {
          if (ownerUserId == map.getUserId().intValue()
            && "R".equals(map.getStatus())) {
            rejectOwnerUser = true;
            break;
          }
        }

        // 今回のアップデートでスケジュールの所有者がメンバーから抜けているかを検証する．
        boolean includeOwnerUser = false;
        for (ALEipUser eipUser : form_data.getMemberList()) {
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

        String schedule_type = getScheduleType().getValue();
        if (CellScheduleUtils.SCHEDULE_TYPE_SPAN.equals(schedule_type)) {
          schedule.setStartDate(form_data.getStartDate().getValue());
          schedule.setEndDate(form_data.getEndDate().getValue());
          schedule.setRepeatPattern("S");
        } else if (CellScheduleUtils.SCHEDULE_TYPE_ONEDAY.equals(schedule_type)) {
          schedule.setStartDate(form_data.getStartDate().getValue());
          schedule.setEndDate(form_data.getEndDate().getValue());
          schedule.setRepeatPattern("N");
        } else {
          char lim = 'N';
          Calendar cal = Calendar.getInstance();
          cal.setTime(form_data.getEndDate().getValue());
          if ("ON".equals(form_data.getLimitFlag().getValue())) {
            lim = 'L';
            cal.set(form_data.getLimitEndDate().getValue().getYear(), form_data
              .getLimitEndDate()
              .getValue()
              .getMonth() - 1, form_data.getLimitEndDate().getValue().getDay());

            ALDateContainer container =
              form_data.getLimitStartDate().getValue();
            Calendar limitStartCal = Calendar.getInstance();
            limitStartCal.setTime(form_data.getStartDate().getValue());
            limitStartCal.set(Calendar.YEAR, container.getYear());
            limitStartCal.set(Calendar.MONTH, container.getMonth() - 1);
            limitStartCal.set(Calendar.DATE, container.getDay());

            schedule.setStartDate(limitStartCal.getTime());
          } else {
            schedule.setStartDate(form_data.getStartDate().getValue());
          }

          schedule.setEndDate(cal.getTime());
          if ("D".equals(form_data.getRepeatType().getValue())) {
            String tmpPattern =
              new StringBuffer().append('D').append(lim).toString();
            schedule.setRepeatPattern(tmpPattern);
          } else if ("W".equals(form_data.getRepeatType().getValue())) {
            if ("0".equals(form_data.getRepeatWeek().getValue())) {
              String tmpPattern =
                new StringBuffer()
                  .append('W')
                  .append(form_data.getWeek0().getValue() != null ? 1 : 0)
                  .append(form_data.getWeek1().getValue() != null ? 1 : 0)
                  .append(form_data.getWeek2().getValue() != null ? 1 : 0)
                  .append(form_data.getWeek3().getValue() != null ? 1 : 0)
                  .append(form_data.getWeek4().getValue() != null ? 1 : 0)
                  .append(form_data.getWeek5().getValue() != null ? 1 : 0)
                  .append(form_data.getWeek6().getValue() != null ? 1 : 0)
                  .append(lim)
                  .toString();
              schedule.setRepeatPattern(tmpPattern);
            } else {
              String tmpPattern =
                new StringBuffer()
                  .append('W')
                  .append(form_data.getWeek0().getValue() != null ? 1 : 0)
                  .append(form_data.getWeek1().getValue() != null ? 1 : 0)
                  .append(form_data.getWeek2().getValue() != null ? 1 : 0)
                  .append(form_data.getWeek3().getValue() != null ? 1 : 0)
                  .append(form_data.getWeek4().getValue() != null ? 1 : 0)
                  .append(form_data.getWeek5().getValue() != null ? 1 : 0)
                  .append(form_data.getWeek6().getValue() != null ? 1 : 0)
                  .append(form_data.getRepeatWeek().getValue().charAt(0))
                  .append(lim)
                  .toString();
              schedule.setRepeatPattern(tmpPattern);
            }
          } else if ("M".equals(form_data.getRepeatType().getValue())) {
            DecimalFormat format = new DecimalFormat("00");
            String month_day =
              format.format(form_data.getMonthDay().getValue());
            if ("32".equals(month_day)) {
              schedule.setRepeatPattern(new StringBuffer().append('M').append(
                "XX").append(lim).toString());
            } else {
              schedule.setRepeatPattern(new StringBuffer().append('M').append(
                month_day).append(lim).toString());
            }
          } else {
            DecimalFormat format = new DecimalFormat("00");
            schedule.setRepeatPattern(new StringBuffer()
              .append('Y')
              .append(format.format(form_data.getYearMonth().getValue()))
              .append(format.format(form_data.getYearDay().getValue()))
              .append(lim)
              .toString());
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

        for (ALEipUser user : form_data.getMemberList()) {
          int userid = (int) user.getUserId().getValue();

          EipTScheduleMap map = Database.create(EipTScheduleMap.class);
          map.setEipTSchedule(schedule);
          map.setUserId(Integer.valueOf(userid));
          // O: 自スケジュール T: 仮スケジュール C: 確定スケジュール
          if (userid == schedule.getOwnerId().intValue()) {
            map.setStatus("O");
          } else {
            if ("T".equals(form_data.getChangeTmpreserveFlag().getValue())) {
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

        if ("O".equals(public_flag.getValue())
          || "C".equals(public_flag.getValue())) {
          for (FacilityResultData frd : form_data.getFacilityMemberList()) {
            int facilityid = (int) frd.getFacilityId().getValue();

            EipTScheduleMap map = Database.create(EipTScheduleMap.class);
            map.setEipTSchedule(schedule);
            map.setUserId(Integer.valueOf(facilityid));
            map.setStatus("O");
            map.setType(ScheduleUtils.SCHEDULEMAP_TYPE_FACILITY);
            map.setEipTCommonCategory(category1);
            map.setCommonCategoryId(Integer.valueOf(1));
          }
        }
      }

      Database.commit();

      // アラーム
      if (ALReminderService.isEnabled() || ALReminderService.isPastEnabled()) {
        boolean isLimit = false;
        if ("ON".equals(form_data.getLimitFlag().getValue())) {
          isLimit = true;
        }
        EipTSchedule targetSchedule = null;
        if (form_data.getEditRepeatFlag().getValue() == CellScheduleUtils.FLAG_EDIT_REPEAT_ONE) {
          targetSchedule = newSchedule;
        } else {
          targetSchedule = schedule;
        }
        // メンバーからはずれた人のJobを削除する
        for (ALEipUser user : removememberList) {
          if (schedule.getScheduleId().intValue() == targetSchedule
            .getScheduleId()
            .intValue()) {
            ALReminderItem item = new ALReminderItem();
            item.setOrgId(Database.getDomainName());
            item.setUserId(user.getUserId().toString());
            item.setItemId(schedule.getScheduleId().intValue());
            item.setCategory(ReminderCategory.SCHEDULE);
            ALReminderService.removeJob(item);
          }
        }
        // メンバーに対しては それぞれのJob をもとにリマインドを作成
        for (ALEipUser user : form_data.getMemberList()) {
          int memberId = (int) user.getUserId().getValue();
          if (isContains(newmemberList, memberId)) {
            // 追加されたメンバー
            ALReminderDefaultItem defaultItem =
              ALReminderService.getDefault(orgId, user
                .getUserId()
                .getValueAsString(), ReminderCategory.SCHEDULE);
            // DefaultItemがない場合は通知しない
            if (defaultItem != null && defaultItem.isEnabled()) {
              ScheduleUtils.setupReminderJob(
                Database.getDomainName(),
                user.getUserId().toString(),
                targetSchedule,
                defaultItem.getNotifyTiming(),
                defaultItem.hasNotifyTypeMail(),
                defaultItem.hasNotifyTypeMessage(),
                isRepeat(),
                isLimit,
                isSpan());
            }
          } else {
            // もともといたメンバー
            ALReminderItem item =
              ALReminderService.getJob(orgId, user
                .getUserId()
                .getValueAsString(), ReminderCategory.SCHEDULE, schedule
                .getScheduleId()
                .intValue());
            if (item != null) {
              if (!isSpan()) {
                ScheduleUtils.setupReminderJob(
                  Database.getDomainName(),
                  user.getUserId().toString(),
                  targetSchedule,
                  item.getNotifyTiming(),
                  item.hasNotifyTypeMail(),
                  item.hasNotifyTypeMessage(),
                  isRepeat(),
                  isLimit,
                  isSpan());
              } else {
                if (schedule.getScheduleId().intValue() == targetSchedule
                  .getScheduleId()
                  .intValue()) {
                  ALReminderService.removeJob(item);
                }
              }
            }
          }
        }
      }
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

      // アクティビティ
      ALEipUser loginUser = getLoginUser();
      List<ALEipUser> memberList = form_data.getMemberList();
      if (loginUser != null) {
        String loginName = loginUser.getName().getValue();
        List<String> recipients = new ArrayList<String>();
        for (ALEipUser user : memberList) {
          if (loginUser.getUserId().getValue() != user.getUserId().getValue()) {
            recipients.add(user.getName().getValue());
          }
        }
        ScheduleUtils.createShareScheduleActivity(
          schedule,
          loginName,
          recipients,
          "edit",
          ownerid);
        // アクティビティが公開スケジュールである場合、「更新情報」に表示させる。
        if ("O".equals(public_flag.toString())) {
          ScheduleUtils.createNewScheduleActivity(
            schedule,
            loginName,
            "edit",
            ownerid);
        }
      }

    } catch (RuntimeException e) {
      // RuntimeException
      Database.rollback();
      logger.error("[ScheduleFormData]", e);
      throw new ALDBErrorException();
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
          ALMailUtils.getALEipUserAddrs(form_data.getMemberList(), ALEipUtils
            .getUserId(rundata), false);
        String subject =
          ALLocalizationUtils.getl10nFormat(
            "SCHEDULE_SUB_SCHEDULE",
            ALOrgUtilsService.getAlias());
        String org_id = ALOrgUtilsService.getAlias();

        List<ALAdminMailMessage> messageList =
          new ArrayList<ALAdminMailMessage>();
        for (ALEipUserAddr destMember : destMemberList) {
          ALAdminMailMessage message = new ALAdminMailMessage(destMember);
          message.setPcSubject(subject);
          message.setCellularSubject(subject);
          message.setPcBody(ScheduleUtils.createMsg(
            rundata,
            schedule,
            form_data.getMemberList(),
            null,
            "edit"));
          message.setCellularBody(ScheduleUtils.createMsg(
            rundata,
            schedule,
            form_data.getMemberList(),
            destMember.getUserId(),
            "edit"));
          messageList.add(message);
        }

        ALMailService.sendAdminMailAsync(new ALAdminMailContext(
          org_id,
          ALEipUtils.getUserId(rundata),
          messageList,
          ALMailUtils.getSendDestType(ALMailUtils.KEY_MSGTYPE_SCHEDULE)));
        // msgList.addAll(errors);

      }
    } catch (Exception ex) {
      msgList.add(ALLocalizationUtils.getl10n("SCHEDULE_DONOT_SEND_MAIL"));
      logger.error("schedule", ex);
      return false;
    }
    return true;

  }

  private EipTScheduleMap getScheduleMap(List<EipTScheduleMap> scheduleMaps,
      int userid) {
    for (EipTScheduleMap map : scheduleMaps) {
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
        form_data.getMemberList().addAll(members);
      }
      // 削除権限を検証する．
      boolean isMember = false;
      int loginuserId = (int) getLoginUser().getUserId().getValue();
      int membersSize = form_data.getMemberList().size();
      ALEipUser eipUser = null;
      for (int i = 0; i < membersSize; i++) {
        eipUser = form_data.getMemberList().get(i);
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
        && CellScheduleUtils.FLAG_DEL_MEMBER_ONE != del_member_flag.getValue()) {
        // del_member_flag.setValue(FLAG_DEL_MEMBER_ONE);
        return true;
      }

      int delFlag = -1;
      if (del_member_flag.getValue() == CellScheduleUtils.FLAG_DEL_MEMBER_ALL) {
        if (del_range_flag.getValue() == CellScheduleUtils.FLAG_DEL_RANGE_ALL) {
          delFlag = 0;
        } else {
          // del_range_flag.getValue() == FLAG_DEL_RANGE_ONE
          delFlag = 1;
        }
      } else {
        // del_member_flag.getValue() == FLAG_DEL_MEMBER_ONE
        // EIP_M_SCHEDULE_MAP の STATUS のみ変更する．
        if (del_range_flag.getValue() == CellScheduleUtils.FLAG_DEL_RANGE_ALL) {
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
          int memberIdListSize = form_data.getMemberList().size();
          int[] memberIdList = new int[memberIdListSize];
          for (int i = 0; i < memberIdListSize; i++) {
            memberIdList[i] =
              (int) form_data.getMemberList().get(i).getUserId().getValue();
          }
          ScheduleUtils.insertDummySchedule(schedule, ownerid, form_data
            .getViewDate()
            .getValue(), form_data.getViewDate().getValue(), memberIdList);
        }
      } else if (delFlag == 2) {
        List<EipTScheduleMap> scheduleMaps =
          ScheduleUtils.getEipTScheduleMaps(schedule);
        if (scheduleMaps != null && scheduleMaps.size() > 0) {
          int rejectedScheduleCount = 0;
          List<EipTScheduleMap> userScheduleMap =
            new ArrayList<EipTScheduleMap>();
          for (EipTScheduleMap scheduleMap : scheduleMaps) {
            if (ScheduleUtils.SCHEDULEMAP_TYPE_USER.equals(scheduleMap
              .getType())) {
              userScheduleMap.add(scheduleMap);
              if ("R".equals(scheduleMap.getStatus())) {
                rejectedScheduleCount++;
              }
            }
          }
          int scheduleUserCount = userScheduleMap.size();

          if (rejectedScheduleCount >= scheduleUserCount - 1) {
            // この schedule ID のスケジュールを, ユーザが全員予定を削除したため，
            // 親スケジュールごと削除する．
            deleteSchedule(schedule);
          } else {
            for (EipTScheduleMap scheduleMap : userScheduleMap) {
              if (scheduleMap.getUserId().intValue() == getLoginUser()
                .getUserId()
                .getValue()) {
                if ((scheduleMap.getUserId().intValue() == getLoginUser()
                  .getUserId()
                  .getValue())
                  || (schedule.getCreateUserId().intValue() == getLoginUser()
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
          form_data.getMemberList().clear();
          form_data.getMemberList().add(getLoginUser());
          // ダミーのスケジュールを登録する．
          int memberIdListSize = form_data.getMemberList().size();
          int[] memberIdList = new int[memberIdListSize];
          for (int i = 0; i < memberIdListSize; i++) {
            memberIdList[i] =
              (int) form_data.getMemberList().get(i).getUserId().getValue();
          }
          ScheduleUtils.insertDummySchedule(schedule, ownerid, form_data
            .getViewDate()
            .getValue(), form_data.getViewDate().getValue(), memberIdList);
        }
      } else {
        Database.delete(schedule);
      }

      Database.commit();

      // アラーム
      if (ALReminderService.isEnabled() || ALReminderService.isPastEnabled()) {
        if (del_member_flag.getValue() == CellScheduleUtils.FLAG_DEL_MEMBER_ALL) {
          if (del_range_flag.getValue() == CellScheduleUtils.FLAG_DEL_RANGE_ALL) {
            // 完全に削除する（元のスケジュール、関連するダミースケジュール）
            for (ALEipUser member : members) {
              ALReminderItem item =
                ALReminderService.getJob(Database.getDomainName(), member
                  .getUserId()
                  .getValueAsString(), ReminderCategory.SCHEDULE, schedule
                  .getScheduleId()
                  .intValue());
              if (item != null) {
                ALReminderService.removeJob(item);
              }
            }
          }
        } else {
          if (del_range_flag.getValue() == CellScheduleUtils.FLAG_DEL_RANGE_ALL) {
            // 自分の全てのスケジュールを削除する、最後のメンバーの場合は完全に削除する

            SelectQuery<EipTSchedule> query =
              Database.query(EipTSchedule.class);

            // スケジュールID
            Expression exp1 =
              ExpressionFactory.matchDbExp(
                EipTSchedule.SCHEDULE_ID_PK_COLUMN,
                schedule.getScheduleId());
            query.setQualifier(exp1);

            List<EipTSchedule> schedules = query.fetchList();

            // 指定したSchedule IDのレコードが見つからない場合
            if (schedules == null || schedules.size() == 0) {
              // 最後のメンバーの場合は完全に削除する
              for (ALEipUser member : members) {
                ALReminderItem item =
                  ALReminderService.getJob(Database.getDomainName(), member
                    .getUserId()
                    .getValueAsString(), ReminderCategory.SCHEDULE, schedule
                    .getScheduleId()
                    .intValue());
                if (item != null) {
                  ALReminderService.removeJob(item);
                }
              }
            }
            if (schedules != null && schedules.size() > 0) {
              // 削除対象ユーザー(ガラケーの場合は自分)のみ削除
              ALReminderItem item =
                ALReminderService.getJob(
                  Database.getDomainName(),
                  String.valueOf(getLoginUser().getUserId().getValue()),
                  ReminderCategory.SCHEDULE,
                  schedule.getScheduleId().intValue());
              if (item != null) {
                ALReminderService.removeJob(item);
              }
            }
          }
        }
      }
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

    } catch (RuntimeException e) {
      // RuntimeException
      Database.rollback();
      logger.error("[ScheduleFormData]", e);
      throw new ALDBErrorException();
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

    // 添付ファイルの削除
    SelectQuery<EipTScheduleFile> dbquery =
      Database.query(EipTScheduleFile.class);
    dbquery.andQualifier(ExpressionFactory.matchDbExp(
      EipTScheduleFile.EIP_TSCHEDULE_PROPERTY,
      schedule.getScheduleId()));
    List<EipTScheduleFile> existsFiles = dbquery.fetchList();

    if (existsFiles.size() > 0) {
      try {
        ALDeleteFileUtil.deleteFiles(
          ScheduleUtils.FOLDER_FILEDIR_SCHEDULE,
          ScheduleUtils.CATEGORY_KEY,
          existsFiles);
      } catch (ALFileNotRemovedException e) {
        Database.rollback();
        logger.error("schedule", e);
      }
    }
  }

  /**
   * Facilityのアクセス権限をチェック
   *
   * @param rundata
   * @param aclType
   * @return
   */
  private boolean facilityCheckAclPermission(RunData rundata, int aclType) {
    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

    hasAuthority =
      aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_FACILITY,
        aclType);
    return hasAuthority;
  }

  /**
   * タイトルを取得します。
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
   * 公開/非公開フラグを取得します。
   *
   * @return
   */
  public ALStringField getPublicFlag() {
    return public_flag;
  }

  /**
   * グループリストを取得します。
   *
   * @return
   */
  @Override
  public List<ALEipGroup> getGroupList() {
    return groups;
  }

  public boolean isReminderEnabled() {
    return ALReminderService.isEnabled();
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
}

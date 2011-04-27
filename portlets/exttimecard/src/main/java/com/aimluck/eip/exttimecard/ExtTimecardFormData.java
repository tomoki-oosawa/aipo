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

package com.aimluck.eip.exttimecard;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateField;
import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTExtTimecard;
import com.aimluck.eip.cayenne.om.portlet.EipTExtTimecardSystem;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.exttimecard.util.ExtTimecardUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タイムカードのフォームデータを管理するクラスです。 <BR>
 * 
 */
public class ExtTimecardFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger =
    JetspeedLogFactoryService.getLogger(ExtTimecardFormData.class.getName());

  /** ToDo名 */

  private ALNumberField timecard_id;

  private ALNumberField user_id;

  private ALDateTimeField punch_date;

  private ALStringField type;

  private ALDateTimeField clock_in_time;

  private ALDateTimeField clock_out_time;

  private ALStringField reason;

  private ALDateTimeField outgoing_time1;

  private ALDateTimeField outgoing_time2;

  private ALDateTimeField outgoing_time3;

  private ALDateTimeField outgoing_time4;

  private ALDateTimeField outgoing_time5;

  private ALDateTimeField comeback_time1;

  private ALDateTimeField comeback_time2;

  private ALDateTimeField comeback_time3;

  private ALDateTimeField comeback_time4;

  private ALDateTimeField comeback_time5;

  private ALDateTimeField outgoing_comeback;

  private ALStringField remarks;

  private ALDateField create_date;

  private ALDateField update_date;

  private int entity_id;

  private int login_uid;

  private String edit_mode;

  private String alt_mode;

  private String old_clock_in_time_hour;

  private String old_clock_in_time_minute;

  private String old_clock_out_time_hour;

  private String old_clock_out_time_minute;

  private ALStringField rest_num;

  private ALStringField delete_flag;

  /** タイムカードの設定 */
  private EipTExtTimecardSystem timecard_system;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * 
   * 
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    login_uid = ALEipUtils.getUserId(rundata);

    // 出勤・退勤時間
    old_clock_in_time_hour =
      rundata.getParameters().get("old_clock_in_time_hour");
    old_clock_in_time_minute =
      rundata.getParameters().get("old_clock_in_time_minute");
    old_clock_out_time_hour =
      rundata.getParameters().get("old_clock_out_time_hour");
    old_clock_out_time_minute =
      rundata.getParameters().get("old_clock_out_time_minute");

    timecard_system =
      ExtTimecardUtils.getEipTExtTimecardSystemCurrentUserId(rundata, context);
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  public void initField() {
    timecard_id = new ALNumberField();
    user_id = new ALNumberField();

    type = new ALStringField();
    type.setFieldName("種類");
    type.setValue("");

    punch_date = new ALDateTimeField();
    punch_date.setFieldName("日付");

    clock_in_time = new ALDateTimeField();
    clock_in_time.setFieldName("勤怠時間");
    clock_out_time = new ALDateTimeField();

    outgoing_comeback = new ALDateTimeField();
    outgoing_comeback.setFieldName("外出時間");

    reason = new ALStringField();
    reason.setFieldName("修正理由");
    reason.setValue("");

    remarks = new ALStringField();
    remarks.setFieldName("備考");
    remarks.setValue("");

    create_date = new ALDateField();
    create_date.setValue(new Date());

    update_date = new ALDateField();
    update_date.setValue(new Date());

    edit_mode = "";

    alt_mode = "";

    rest_num = new ALStringField();
    rest_num.setValue("0");

    delete_flag = new ALStringField();
    delete_flag.setFieldName("削除");
    delete_flag.setValue("");

    try {
      Field field;
      for (int i = 1; i <= EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY; i++) {
        field = this.getClass().getDeclaredField("outgoing_time" + i);
        field.set(this, new ALDateTimeField());
        field = this.getClass().getDeclaredField("comeback_time" + i);
        field.set(this, new ALDateTimeField());
      }
    } catch (Exception e) {
      return;
    }
  }

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
        (ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID) != null);

      action.setMode(isedit
        ? ALEipConstants.MODE_EDIT_FORM
        : ALEipConstants.MODE_NEW_FORM);
      setMode(action.getMode());

      List<String> msgList = new ArrayList<String>();
      boolean res =
        (isedit) ? loadFormData(rundata, context, msgList) : setFormData(
          rundata,
          context,
          msgList);

      int aclType = ALAccessControlConstants.VALUE_ACL_INSERT;
      if (isedit || (getIsPast() || getIsToday())) {
        aclType = ALAccessControlConstants.VALUE_ACL_UPDATE;
      }
      doCheckAclPermission(rundata, context, aclType);

      action.setResultData(this);
      if (!msgList.isEmpty()) {
        action.addErrorMessages(msgList);
      }
      action.putData(rundata, context);
      return res;
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
   * タイムカードの各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // reason.setNotNull(true);
    // reason.limitMaxLength(1000);

    // work_date.setNotNull(true);
  }

  /**
   * タイムカードのフォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {
    try {
      if (type.getValue().equals("")) {
        msgList.add("『 <span class='em'>種類</span> 』を選択してください。");
      }

      if (!"punchin".equals(edit_mode)
        && !"punchout".equals(edit_mode)
        && !"outgoing".equals(edit_mode)
        && !"comeback".equals(edit_mode)) {
        if ((old_clock_in_time_hour != clock_in_time.getHour())
          || (old_clock_in_time_minute != clock_in_time.getMinute())
          || (old_clock_out_time_hour != clock_out_time.getHour())
          || (old_clock_out_time_minute != clock_out_time.getMinute())) {
          if (Calendar.getInstance().getTime().after(punch_date.getValue())) {
            reason.setNotNull(true);
            reason.validate(msgList);
          }
          remarks.validate(msgList);
        }
      }

      if (getMode() == ALEipConstants.MODE_INSERT) {
        SelectQuery<EipTExtTimecard> workflg_query =
          Database.query(EipTExtTimecard.class);
        Expression workflg_exp =
          ExpressionFactory.matchExp(EipTExtTimecard.USER_ID_PROPERTY, Integer
            .valueOf(login_uid));
        workflg_query.setQualifier(workflg_exp);
        workflg_query.orderDesending(EipTExtTimecard.PUNCH_DATE_PROPERTY);
        List<EipTExtTimecard> workflg_list = workflg_query.fetchList();
        if (workflg_list != null && workflg_list.size() > 0) {
        } else {
        }
      }

      /** 更新・挿入時 */
      if (edit_mode.equals("") && !"on".equals(delete_flag.getValue())) {
        /** 日付を punch_date に合わせる */
        if (ajustDate(clock_in_time, punch_date)
          && ajustDate(clock_out_time, punch_date)) {
          if (clock_in_time.getValue().getTime() > clock_out_time
            .getValue()
            .getTime()) {
            msgList
              .add("『 <span class='em'>退勤時刻</span> 』は『 <span class='em'>出勤時刻</span> 』以降の時刻を指定してください。");
          }
        }

        /** 勤怠時間は必須項目 */
        if (getIsPast() && "P".equals(type.getValue())) {
          if (!clock_in_time.isNotNullValue()
            || !clock_out_time.isNotNullValue()) {
            msgList.add("『 <span class='em'>"
              + clock_in_time.getFieldName()
              + "</span> 』を入力してください。");
          }
          /** 外出復帰時間が適切に入力されているかチェック */
          Field field_out, field_come;
          for (int i = 1; i <= Integer.parseInt(rest_num.getValue()); i++) {
            field_out = this.getClass().getDeclaredField("outgoing_time" + i);
            field_come = this.getClass().getDeclaredField("comeback_time" + i);
            ALDateTimeField outgoing = (ALDateTimeField) field_out.get(this);
            ALDateTimeField comeback = (ALDateTimeField) field_come.get(this);
            if (!outgoing.isNotNullValue() || !comeback.isNotNullValue()) {
              msgList.add("『 <span class='em'>"
                + outgoing_comeback.getFieldName()
                + "</span> 』を入力してください。"
                + "（"
                + i
                + "行目）");
            }
          }

          /** 外出／復帰時間をリストに代入 */
          List<Map<String, Long>> list_from_to =
            new ArrayList<Map<String, Long>>();
          for (int i = 1; i <= EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY; i++) {
            field_out = this.getClass().getDeclaredField("outgoing_time" + i);
            field_come = this.getClass().getDeclaredField("comeback_time" + i);
            ALDateTimeField outgoing = (ALDateTimeField) field_out.get(this);
            ALDateTimeField comeback = (ALDateTimeField) field_come.get(this);
            if (ajustDate(outgoing, punch_date)
              && ajustDate(comeback, punch_date)) {
              long from = outgoing.getValue().getTime();
              long to = comeback.getValue().getTime();
              if (from <= to) {
                if (clock_in_time.isNotNullValue()
                  && from < clock_in_time.getValue().getTime()) {
                  msgList
                    .add("『 <span class='em'>外出時刻</span> 』は『 <span class='em'>出勤時刻</span> 』以降の時刻を指定してください。（"
                      + i
                      + "行目）");
                }
                if (clock_out_time.isNotNullValue()
                  && to > clock_out_time.getValue().getTime()) {
                  msgList
                    .add("『 <span class='em'>復帰時刻</span> 』は『 <span class='em'>退勤時刻</span> 』以前の時刻を指定してください。（"
                      + i
                      + "行目）");
                }
                HashMap<String, Long> from_to = new HashMap<String, Long>();
                from_to.put("from", outgoing.getValue().getTime());
                from_to.put("to", comeback.getValue().getTime());
                list_from_to.add(from_to);
              } else {
                msgList
                  .add("『 <span class='em'>復帰時刻</span> 』は『 <span class='em'>外出時刻</span> 』以降の時刻を指定してください。（"
                    + i
                    + "行目）");
              }
            } else if (ajustDate(outgoing, punch_date)
              && !ajustDate(comeback, punch_date)
              && i == 1) {
              HashMap<String, Long> from_to = new HashMap<String, Long>();
              from_to.put("from", outgoing.getValue().getTime());
              // from_to.put("to", comeback.getValue().getTime());
              list_from_to.add(from_to);
              return (msgList.size() == 0);
            }
          }

          /** 外出時間の重複をチェックする */
          int i = 1;
          if (list_from_to.size() > 0) {
            List<Map<String, Long>> empty_from_to =
              new ArrayList<Map<String, Long>>();
            long min_from = list_from_to.get(0).get("from");
            long max_to = list_from_to.get(0).get("to");
            list_from_to.remove(0);
            for (Map<String, Long> map : list_from_to) {
              long new_from = map.get("from");
              long new_to = map.get("to");
              if (new_to <= min_from) {
                Map<String, Long> empty = new HashMap<String, Long>();
                empty.put("from", new_to);
                empty.put("to", min_from);
                empty_from_to.add(empty);
                min_from = new_from;
              } else if (new_from >= max_to) {
                Map<String, Long> empty = new HashMap<String, Long>();
                empty.put("from", max_to);
                empty.put("to", new_from);
                empty_from_to.add(empty);
                max_to = new_to;
              } else {
                /** empty_from_toのリストから入れる場所を探す */
                boolean duplicate_flag = true;
                for (Map<String, Long> empty_map : empty_from_to) {
                  if (empty_map.get("from") <= new_from
                    && empty_map.get("to") >= new_to) {
                    /** 区間を分割し、もとあった空白を削除 */
                    HashMap<String, Long> empty_left =
                      new HashMap<String, Long>();
                    empty_left.put("from", empty_map.get("from"));
                    empty_left.put("to", new_from);
                    empty_from_to.add(empty_left);

                    HashMap<String, Long> empty_right =
                      new HashMap<String, Long>();
                    empty_right.put("from", new_to);
                    empty_right.put("to", empty_map.get("to"));
                    empty_from_to.add(empty_right);

                    empty_from_to.remove(empty_map);

                    duplicate_flag = false;
                    break;
                  }
                }
                if (duplicate_flag) {
                  msgList.add("外出時間が重複しています。");
                  return false;
                }
              }
            }

            /** 並べ替える */
            Collections.sort(
              empty_from_to,
              new Comparator<Map<String, Long>>() {
                public int compare(Map<String, Long> o1, Map<String, Long> o2) {
                  Map<String, Long> hash1 = o1;
                  Map<String, Long> hash2 = o2;
                  long from1 = hash1.get("from");
                  long from2 = hash2.get("from");
                  if (from1 == from2) {
                    long to1 = hash1.get("to");
                    long to2 = hash2.get("to");
                    return (int) (to1 - to2);
                  } else {
                    return (int) (from1 - from2);
                  }
                }

                @Override
                public boolean equals(Object obj) {
                  return super.equals(obj);
                }
              });

            long from = min_from;
            long to;
            for (Map<String, Long> empty : empty_from_to) {
              to = empty.get("from");
              field_out = this.getClass().getDeclaredField("outgoing_time" + i);
              field_come =
                this.getClass().getDeclaredField("comeback_time" + i);
              ALDateTimeField outgoing = (ALDateTimeField) field_out.get(this);
              ALDateTimeField comeback = (ALDateTimeField) field_come.get(this);
              outgoing.setValue(new Date(from));
              comeback.setValue(new Date(to));
              i++;
              from = empty.get("to");
            }
            to = max_to;
            field_out = this.getClass().getDeclaredField("outgoing_time" + i);
            field_come = this.getClass().getDeclaredField("comeback_time" + i);
            ALDateTimeField outgoing = (ALDateTimeField) field_out.get(this);
            ALDateTimeField comeback = (ALDateTimeField) field_come.get(this);
            outgoing.setValue(new Date(from));
            comeback.setValue(new Date(to));
            i++;
          }

          /** 余った場所には空のALDateTimeFieldを追加する */
          for (; i <= EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY; i++) {
            field_out = this.getClass().getDeclaredField("outgoing_time" + i);
            field_come = this.getClass().getDeclaredField("comeback_time" + i);
            field_out.set(this, new ALDateTimeField());
            field_come.set(this, new ALDateTimeField());
          }
        }
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return (msgList.size() == 0);
  }

  @SuppressWarnings("unused")
  private EipTExtTimecard getNearlyAboveRecord(List<EipTExtTimecard> list,
      int timecard_id) {
    EipTExtTimecard result = null;
    EipTExtTimecard record = null;
    int size = list.size();
    for (int i = 0; i < size; i++) {
      record = list.get(i);
      if (record.getExtTimecardId().intValue() >= timecard_id) {
        return result;
      } else {
        result = record;
      }
    }

    return null;
  }

  @SuppressWarnings("unused")
  private EipTExtTimecard getNearlyBelowRecord(List<EipTExtTimecard> list,
      int timecard_id) {
    EipTExtTimecard record = null;
    int size = list.size();
    for (int i = 0; i < size; i++) {
      record = list.get(i);
      if (record.getExtTimecardId().intValue() > timecard_id) {
        return record;
      }
    }

    return null;
  }

  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean res = super.setFormData(rundata, context, msgList);

    if (res) {
      if (ALEipConstants.MODE_UPDATE.equals(this.getMode())) {
        try {
          if (!(this.entity_id > 0)) {
            entity_id =
              Integer.parseInt(ALEipUtils.getTemp(
                rundata,
                context,
                ALEipConstants.ENTITY_ID));
          }
          if ("".equals(this.type.getValue())) {
            String type = rundata.getParameters().get("type");
            this.type.setValue(type);
          }

          String punch_date_year =
            rundata.getParameters().get("punch_date_year");
          String punch_date_month =
            rundata.getParameters().get("punch_date_month");
          String punch_date_day = rundata.getParameters().get("punch_date_day");
          StringBuffer buffer = new StringBuffer(8);
          buffer
            .append(punch_date_year)
            .append('/')
            .append(punch_date_month)
            .append('/')
            .append(punch_date_day);

          this.punch_date.setValue(buffer.toString());

        } catch (Exception e) {
          logger.error("Exception", e);
        }
      } else if (ALEipConstants.MODE_NEW_FORM.equals(this.getMode())) {
        String session_date = rundata.getParameters().get("date");
        if (session_date != null && !"".equals(session_date)) {
          this.punch_date.setValue(session_date);
          this.type.setValue("P");
        }
      } else if ("alt_insert".equals(this.alt_mode)) {

        StringBuffer buffer = new StringBuffer(8);
        String type = rundata.getParameters().get("type");
        String punch_date_year = rundata.getParameters().get("punch_date_year");
        String punch_date_month =
          rundata.getParameters().get("punch_date_month");
        String punch_date_day = rundata.getParameters().get("punch_date_day");

        buffer
          .append(punch_date_year)
          .append('/')
          .append(punch_date_month)
          .append('/')
          .append(punch_date_day);

        String reason = rundata.getParameters().get("reason");
        String remarks = rundata.getParameters().get("remarks");

        this.type.setValue(type);
        this.punch_date.setValue(buffer.toString());
        this.reason.setValue(reason);
        this.remarks.setValue(remarks);

      } else {
        ALDateTimeField current_date = new ALDateTimeField();
        current_date.setValue(new Date());

        // 種類＝出勤を入れる
        this.type.setValue("P");
        this.punch_date.setValue(current_date.toString());
      }

      // 日時をセッションに保存
      ALEipUtils.setTemp(rundata, context, "punch_date", punch_date.toString());

    }

    return res;
  }

  /**
   * タイムカードをデータベースから読み出します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTExtTimecard timecard =
        ExtTimecardUtils.getEipTExtTimecard(rundata, context);
      if (timecard == null) {
        return false;
      }
      //
      timecard_id.setValue(timecard.getExtTimecardId().longValue());
      user_id.setValue(timecard.getUserId().intValue());
      punch_date.setValue(timecard.getPunchDate());
      type.setValue(timecard.getType());

      clock_in_time.setValue(timecard.getClockInTime());
      clock_out_time.setValue(timecard.getClockOutTime());

      // 外出・復帰時間
      outgoing_time1.setValue(timecard.getOutgoingTime1());
      outgoing_time2.setValue(timecard.getOutgoingTime2());
      outgoing_time3.setValue(timecard.getOutgoingTime3());
      outgoing_time4.setValue(timecard.getOutgoingTime4());
      outgoing_time5.setValue(timecard.getOutgoingTime5());

      comeback_time1.setValue(timecard.getComebackTime1());
      comeback_time2.setValue(timecard.getComebackTime2());
      comeback_time3.setValue(timecard.getComebackTime3());
      comeback_time4.setValue(timecard.getComebackTime4());
      comeback_time5.setValue(timecard.getComebackTime5());

      reason.setValue(timecard.getReason());
      remarks.setValue(timecard.getRemarks());
      create_date.setValue(timecard.getCreateDate());
      update_date.setValue(timecard.getUpdateDate());

      int rest_num_tmp = 0;
      for (int i = 1; i <= 5; i++) {
        if (this.getOutgoingTime(i).isNotNullValue()) {
          rest_num_tmp++;
        } else if (this.getComebackTime(i).isNotNullValue()) {
          rest_num_tmp++;
        }
      }
      rest_num.setValue(Integer.toString(rest_num_tmp));

      // 日時をセッションに保存
      ALEipUtils.setTemp(rundata, context, "punch_date", punch_date
        .getValue()
        .toString());

    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * タイムカードをデータベースから削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {

    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * タイムカードをデータベースに格納します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // 新規オブジェクトモデル
      EipTExtTimecard timecard = Database.create(EipTExtTimecard.class);

      // ユーザーID
      timecard.setUserId(Integer.valueOf(ALEipUtils.getUserId(rundata)));

      Calendar cal = Calendar.getInstance();
      if (timecard_system.getChangeHour() > cal.get(Calendar.HOUR_OF_DAY)) {
        cal.add(Calendar.DATE, -1);
      }

      // 日付
      timecard.setPunchDate(cal.getTime());

      // タイプ
      timecard.setType(type.getValue());

      if (edit_mode.equals("punchin")) {
        // 出勤
        timecard.setClockInTime(cal.getTime());
      } else if (edit_mode.equals("punchout")) {
        // 退勤
        timecard.setClockOutTime(cal.getTime());
      } else if (edit_mode.equals("outgoing")) {
        // 外出
        timecard.setOutgoingTime1(cal.getTime());
      } else if (edit_mode.equals("comeback")) {
        // 復帰
        timecard.setComebackTime1(cal.getTime());
      } else {
        // 修正時

        timecard.setPunchDate(punch_date.getValue());

        /** 未来時刻への打刻は不可 */

        if (cal.getTime().after(punch_date.getValue())) {
          // 削除する
          if ("on".equals(delete_flag.getValue())) {
            // タイプ
            timecard.setType("P");
            // 出退勤時間
            timecard.setClockInTime(null);
            timecard.setClockOutTime(null);
            // 外出・復帰時間
            for (int i = 1; i <= EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY; i++) {
              timecard.setOutgoingTime(null, i);
              timecard.setComebackTime(null, i);
            }
          } else if (!"P".equals(type.getValue())) {
            // 出退勤時間
            timecard.setClockInTime(null);
            timecard.setClockOutTime(null);
            // 外出・復帰時間
            for (int i = 1; i <= EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY; i++) {
              timecard.setOutgoingTime(null, i);
              timecard.setComebackTime(null, i);
            }
          } else {
            // 出退勤時間
            timecard.setClockInTime(clock_in_time.getValue());
            timecard.setClockOutTime(clock_out_time.getValue());

            // 外出・復帰時間
            Field field_out, field_come;
            for (int i = 1; i <= EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY; i++) {
              field_out = this.getClass().getDeclaredField("outgoing_time" + i);
              field_come =
                this.getClass().getDeclaredField("comeback_time" + i);
              ALDateTimeField outgoing = (ALDateTimeField) field_out.get(this);
              ALDateTimeField comeback = (ALDateTimeField) field_come.get(this);
              if (!outgoing.isNullHour() && !outgoing.isNullMinute()) {
                timecard.setOutgoingTime(outgoing.getValue(), i);
              }
              if (!comeback.isNullHour() && !comeback.isNullMinute()) {
                timecard.setComebackTime(comeback.getValue(), i);
              }
            }
          }

          // 修正理由
          timecard.setReason(reason.getValue());
        }

        // 備考
        timecard.setRemarks(remarks.getValue());
      }

      // 作成日
      timecard.setCreateDate(Calendar.getInstance().getTime());

      // 更新日
      timecard.setUpdateDate(Calendar.getInstance().getTime());

      // タイムカードを登録
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        timecard.getExtTimecardId(),
        ALEventlogConstants.PORTLET_TYPE_TIMECARD,
        null);

    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * データベースに格納されているタイムカードを更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      if (!edit_mode.equals("")) {
        ALEipUtils.setTemp(rundata, context, ALEipConstants.ENTITY_ID, String
          .valueOf(entity_id));
      }
      EipTExtTimecard timecard =
        ExtTimecardUtils.getEipTExtTimecard(rundata, context);
      if (timecard == null) {
        return false;
      }

      Calendar cal = Calendar.getInstance();
      if (edit_mode.equals("punchin")) {
        // 出勤
        timecard.setClockInTime(cal.getTime());
      } else if (edit_mode.equals("punchout")) {
        // 退勤
        timecard.setClockOutTime(cal.getTime());
      } else if (edit_mode.equals("outgoing")) {
        // 外出
        timecard.setNewOutgoingTime(cal.getTime());
      } else if (edit_mode.equals("comeback")) {
        // 復帰
        timecard.setNewComebackTime(cal.getTime());
      } else {
        // 修正時

        // タイプ
        timecard.setType(type.getValue());
        // 修正理由
        timecard.setReason(reason.getValue());
        // 備考
        timecard.setRemarks(remarks.getValue());

        /** 未来時刻への打刻は不可 */
        // Calendar cal = Calendar.getInstance();
        if (cal.getTime().after(punch_date.getValue())) {
          // 削除する
          if ("on".equals(delete_flag.getValue())) {
            // タイプ
            timecard.setType("P");
            // 出退勤時間
            timecard.setClockInTime(null);
            timecard.setClockOutTime(null);
            // 外出・復帰時間
            for (int i = 1; i <= EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY; i++) {
              timecard.setOutgoingTime(null, i);
              timecard.setComebackTime(null, i);
            }
          } else if (!"P".equals(type.getValue())) {
            // 出退勤時間
            timecard.setClockInTime(null);
            timecard.setClockOutTime(null);
            // 外出・復帰時間
            for (int i = 1; i <= EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY; i++) {
              timecard.setOutgoingTime(null, i);
              timecard.setComebackTime(null, i);
            }
          } else {
            // 出退勤時間
            timecard.setClockInTime(clock_in_time.getValue());
            timecard.setClockOutTime(clock_out_time.getValue());

            // 外出・復帰時間
            Field field_out, field_come;
            for (int i = 1; i <= EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY; i++) {
              field_out = this.getClass().getDeclaredField("outgoing_time" + i);
              field_come =
                this.getClass().getDeclaredField("comeback_time" + i);
              timecard.setOutgoingTime(null, i);
              timecard.setComebackTime(null, i);

              ALDateTimeField outgoing = (ALDateTimeField) field_out.get(this);
              ALDateTimeField comeback = (ALDateTimeField) field_come.get(this);
              if (!outgoing.isNullHour() && !outgoing.isNullMinute()) {
                timecard.setOutgoingTime(outgoing.getValue(), i);
              }
              if (!comeback.isNullHour() && !comeback.isNullMinute()) {
                timecard.setComebackTime(comeback.getValue(), i);
              }
            }
          }
        }
      }

      // 更新日
      timecard.setUpdateDate(Calendar.getInstance().getTime());

      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        timecard.getExtTimecardId(),
        ALEventlogConstants.PORTLET_TYPE_TIMECARD,
        null);

    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }

    return true;
  }

  /**
   * 各ボタンを押したときの動作 <BR>
   * 
   * @param action
   * @param rundata
   * @param context
   * @return TRUE 成功 FALSE 失敗
   */
  public boolean doPunch(ALAction action, RunData rundata, Context context,
      String mode) {
    try {
      edit_mode = mode;
      EipTExtTimecard timecard =
        ExtTimecardUtils.getUpdateEipTExtTimecard(rundata, context);
      this.type.setValue("P");

      if (timecard != null) {
        // 更新すべきidを記憶
        entity_id = timecard.getExtTimecardId();

        // 当日内の更新なのでupdate文を発行
        super.doUpdate(action, rundata, context);
      } else {
        // 日をまたいでの更新なのでinsert文を発行
        super.doInsert(action, rundata, context);
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 日付
   * 
   * @return
   */
  public ALDateTimeField getPunchDate() {
    return punch_date;
  }

  /**
   * 日付を取得します。
   * 
   * @return
   */
  public String getDateStr() {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日（EE）");
      return sdf.format(punch_date.getValue());
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * 日付が過去かどうか
   * 
   * @return
   */
  public boolean getIsPast() {
    Date now = Calendar.getInstance().getTime();
    Date date = punch_date.getValue();
    return date.before(now);
  }

  /**
   * 日付が現在かどうか
   * 
   * @return
   */
  public boolean getIsToday() {
    int change_hour =
      ExtTimecardUtils
        .getEipTExtTimecardSystemByUserId(login_uid)
        .getChangeHour();
    Calendar cal = Calendar.getInstance();
    Date date = punch_date.getValue();
    boolean is_today = false;
    if ((Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) < change_hour) {
      Calendar tmp_cal = Calendar.getInstance();
      tmp_cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal
        .get(Calendar.DATE));
      is_today = ExtTimecardUtils.sameDay(date, tmp_cal.getTime());
    } else {
      is_today = ExtTimecardUtils.sameDay(date, cal.getTime());
    }
    return is_today;
  }

  /**
   * 種類
   * 
   * @return
   */
  public ALStringField getType() {
    return type;
  }

  /**
   * 出勤時間
   * 
   * @return
   */
  public ALDateTimeField getClockInTime() {
    return clock_in_time;
  }

  /**
   * 退勤時間
   * 
   * @return
   */
  public ALDateTimeField getClockOutTime() {
    return clock_out_time;
  }

  /**
   * 外出時間
   * 
   * @return
   */
  public ALDateTimeField getOutgoingTime(int n) {
    if (n > EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY) {
      return null;
    }
    switch (n) {
      case 1:
        return outgoing_time1;
      case 2:
        return outgoing_time2;
      case 3:
        return outgoing_time3;
      case 4:
        return outgoing_time4;
      case 5:
        return outgoing_time5;
    }
    return null;
  }

  /**
   * 復帰時間
   * 
   * @return
   */
  public ALDateTimeField getComebackTime(int n) {
    if (n > EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY) {
      return null;
    }
    switch (n) {
      case 1:
        return comeback_time1;
      case 2:
        return comeback_time2;
      case 3:
        return comeback_time3;
      case 4:
        return comeback_time4;
      case 5:
        return comeback_time5;
    }
    return null;
  }

  public ALDateTimeField getOutgoingTime2() {
    return outgoing_time2;
  }

  /**
   * 修正理由
   * 
   * @return
   */
  public ALStringField getReason() {
    return reason;
  }

  /**
   * 備考
   * 
   * @return
   */
  public ALStringField getRemarks() {
    return remarks;
  }

  /**
   * タイムカード設定
   * 
   * @return
   */
  public EipTExtTimecardSystem getTimecardSystem() {
    return timecard_system;
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
  @SuppressWarnings("unused")
  private int compareToDate(Date date1, Date date2) {
    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();
    cal1.setTime(date1);
    cal2.setTime(date2);

    int date1Year = cal1.get(Calendar.YEAR);
    int date1Month = cal1.get(Calendar.MONTH) + 1;
    int date1Day = cal1.get(Calendar.DATE);
    int date1Hour = cal1.get(Calendar.HOUR);
    int date1Minute = cal1.get(Calendar.MINUTE);
    int date1Second = cal1.get(Calendar.SECOND);
    int date2Year = cal2.get(Calendar.YEAR);
    int date2Month = cal2.get(Calendar.MONTH) + 1;
    int date2Day = cal2.get(Calendar.DATE);
    int date2Hour = cal2.get(Calendar.HOUR);
    int date2Minute = cal2.get(Calendar.MINUTE);
    int date2Second = cal2.get(Calendar.SECOND);

    if (date1Year == date2Year
      && date1Month == date2Month
      && date1Day == date2Day
      && date1Hour == date2Hour
      && date1Minute == date2Minute
      && date1Second == date2Second) {
      return 0;
    }
    if (cal1.after(cal2)) {
      return 2;
    } else {
      return 1;
    }
  }

  /**
   * 日付がずれていたら、強制的に直します。
   * 
   * @param datetime
   * @param ajustto
   * @return
   */
  private boolean ajustDate(ALDateTimeField datetime, ALDateTimeField ajustto) {
    if (datetime != null && !datetime.isNullHour() && !datetime.isNullMinute()) {
      Date punch = ajustto.getValue();
      Calendar cal = Calendar.getInstance();
      cal.setTime(punch);
      cal.set(Calendar.HOUR, Integer.parseInt(datetime.getHour()));
      cal.set(Calendar.MINUTE, Integer.parseInt(datetime.getMinute()));
      cal.set(Calendar.SECOND, 0);

      /** CHANGE_HOUR以下だったら、次の日とみなす */
      if (Integer.parseInt(datetime.getHour()) < timecard_system
        .getChangeHour()) {
        cal.add(Calendar.DAY_OF_MONTH, 1);
      }
      datetime.setValue(cal.getTime());
      return true;
    }
    return false;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_TIMECARD_TIMECARD_SELF;
  }

  /**
   * alt_modeをセットする
   */
  public void setAltMode(String alt_mode) {
    this.alt_mode = alt_mode;
  }

  /**
   * alt_modeをゲットする
   */
  public String getAltMode() {
    return this.alt_mode;
  }

  /**
   * 外出数をゲットする
   */
  public int getRestNum() {
    return Integer.parseInt(rest_num.getValue());
    // if (this.rest_num == 0) {
    // return 1;
    // } else {
    // return this.rest_num;
    // }

  }
}

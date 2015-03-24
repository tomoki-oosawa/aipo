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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import com.aimluck.eip.cayenne.om.portlet.EipTTimecard;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.timecard.util.TimecardUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タイムカードのフォームデータを管理するクラスです。
 * 
 */
public class TimecardFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(TimecardFormData.class.getName());

  private ALNumberField timecard_id;

  private ALNumberField user_id;

  private ALStringField work_flag;

  private ALDateTimeField work_date;

  private ALStringField reason;

  private ALDateField create_date;

  private ALDateField update_date;

  private int entity_id;

  private int login_uid;

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

    login_uid = ALEipUtils.getUserId(rundata);
  }

  /**
   * 各フィールドを初期化します。
   * 
   */
  @Override
  public void initField() {
    timecard_id = new ALNumberField();

    user_id = new ALNumberField();

    work_flag = new ALStringField();
    work_flag.setValue("0");

    work_date = new ALDateTimeField();
    work_date.setValue(new Date());
    work_date.setFieldName("勤怠時間");

    reason = new ALStringField();
    reason.setFieldName("修正理由");

    create_date = new ALDateField();
    create_date.setValue(new Date());

    update_date = new ALDateField();
    update_date.setValue(new Date());
  }

  /**
   * タイムカードの各フィールドに対する制約条件を設定します。
   * 
   */
  @Override
  protected void setValidator() {
    reason.setNotNull(true);
    reason.limitMaxLength(1000);

    work_date.setNotNull(true);
  }

  /**
   * タイムカードのフォームに入力されたデータの妥当性検証を行います。
   * 
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean validate(List<String> msgList) {
    try {
      if (getMode() == ALEipConstants.MODE_INSERT) {
        SelectQuery<EipTTimecard> workflg_query =
          Database.query(EipTTimecard.class);
        Expression workflg_exp =
          ExpressionFactory.matchExp(EipTTimecard.USER_ID_PROPERTY, Integer
            .valueOf(login_uid));
        workflg_query.setQualifier(workflg_exp);
        workflg_query.orderDesending(EipTTimecard.WORK_DATE_PROPERTY);

        List<EipTTimecard> workflg_list = workflg_query.fetchList();
        if (workflg_list != null && workflg_list.size() > 0) {
          EipTTimecard record = workflg_list.get(0);

          if (record.getWorkFlag().equals(work_flag.getValue())) {
            return false;
          } else {
            return true;
          }
        } else {
          return true;
        }
      }

      Calendar cal = Calendar.getInstance();

      if (cal.getTime().before(work_date.getValue())) {
        // 未来時刻へは変更不可とする
        msgList
          .add("『 <span class='em'>勤怠時間</span> 』は『 <span class='em'>現在の時刻</span> 』以前で指定してください。");

        return false;
      }

      cal.setTime(work_date.getValue());
      cal.set(Calendar.DAY_OF_MONTH, 1);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.MILLISECOND, 0);

      SelectQuery<EipTTimecard> query = Database.query(EipTTimecard.class);
      Expression exp11 =
        ExpressionFactory.greaterOrEqualExp(
          EipTTimecard.WORK_DATE_PROPERTY,
          cal.getTime());
      cal.add(Calendar.MONTH, +1);
      Expression exp12 =
        ExpressionFactory.lessExp(EipTTimecard.WORK_DATE_PROPERTY, cal
          .getTime());
      query.setQualifier(exp11.andExp(exp12));
      Expression exp21 =
        ExpressionFactory.matchExp(EipTTimecard.USER_ID_PROPERTY, Integer
          .valueOf(login_uid));
      query.andQualifier(exp21);
      query.orderAscending(EipTTimecard.WORK_DATE_PROPERTY);

      List<EipTTimecard> list = query.fetchList();

      if (list == null || list.size() <= 0) {

      } else {
        EipTTimecard timecard0 = getNearlyAboveRecord(list, entity_id);
        EipTTimecard timecard1 = getNearlyBelowRecord(list, entity_id);

        // int id0 = 0;
        // int id1 = 0;
        // if (timecard0 != null) {
        // id0 = timecard0.getTimecardId().intValue();
        // }
        // if (timecard1 != null) {
        // id1 = timecard1.getTimecardId().intValue();
        // }

        if (timecard1 != null) {
          if (compareToDate(timecard1.getWorkDate(), work_date.getValue()) == 2) {
            if (timecard0 != null) {
              if (compareToDate(timecard0.getWorkDate(), work_date.getValue()) == 1) {

              } else {
                msgList
                  .add("『 <span class='em'>勤怠時間</span> 』は『 <span class='em'>前の勤怠時間</span> 』以降で指定してください。");
              }
            }
          } else {
            msgList
              .add("『 <span class='em'>勤怠時間</span> 』は『 <span class='em'>後の勤怠時間</span> 』以前で指定してください。");
          }
        } else {
          if (timecard0 != null) {
            if (compareToDate(timecard0.getWorkDate(), work_date.getValue()) == 1) {

            } else {
              msgList
                .add("『 <span class='em'>勤怠時間</span> 』は『 <span class='em'>前の勤怠時間</span> 』以降で指定してください。");
            }
          }
        }
      }
    } catch (Exception ex) {
      logger.error("timecard", ex);
      return false;
    }

    reason.validate(msgList);
    work_date.validate(msgList);

    return (msgList.size() == 0);
  }

  private EipTTimecard getNearlyAboveRecord(List<EipTTimecard> list,
      int timecard_id) {
    EipTTimecard result = null;
    EipTTimecard record = null;
    int size = list.size();
    for (int i = 0; i < size; i++) {
      record = list.get(i);
      if (record.getTimecardId().intValue() >= timecard_id) {
        return result;
      } else {
        result = record;
      }
    }

    return null;
  }

  private EipTTimecard getNearlyBelowRecord(List<EipTTimecard> list,
      int timecard_id) {
    EipTTimecard record = null;
    int size = list.size();
    for (int i = 0; i < size; i++) {
      record = list.get(i);
      if (record.getTimecardId().intValue() > timecard_id) {
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
      if (getMode() == ALEipConstants.MODE_UPDATE) {
        try {
          entity_id =
            Integer.parseInt(ALEipUtils.getTemp(
              rundata,
              context,
              ALEipConstants.ENTITY_ID));
        } catch (Exception e) {

        }
      }
    }

    return res;
  }

  /**
   * タイムカードをデータベースから読み出します。
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
      EipTTimecard timecard = TimecardUtils.getEipTTimecard(rundata, context);
      if (timecard == null) {
        return false;
      }

      timecard_id.setValue(timecard.getTimecardId().longValue());
      user_id.setValue(timecard.getUserId().intValue());
      work_flag.setValue(timecard.getWorkFlag());
      work_date.setValue(timecard.getWorkDate());

      reason.setValue(timecard.getReason());
      create_date.setValue(timecard.getCreateDate());
      update_date.setValue(timecard.getUpdateDate());

    } catch (Exception ex) {
      logger.error("timecard", ex);
      return false;
    }
    return true;
  }

  /**
   * タイムカードをデータベースから削除します。
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
      // オブジェクトモデルを取得
      EipTTimecard timecard = TimecardUtils.getEipTTimecard(rundata, context);
      if (timecard == null) {
        return false;
      }
      // 打刻情報を削除
      Database.delete(timecard);
      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        timecard.getTimecardId(),
        ALEventlogConstants.PORTLET_TYPE_TIMECARD,
        reason.getValue());

    } catch (Exception ex) {
      Database.rollback();
      logger.error("timecard", ex);
      return false;
    }
    return true;
  }

  /**
   * タイムカードをデータベースに格納します。
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
      EipTTimecard timecard = Database.create(EipTTimecard.class);
      // ユーザーID
      timecard.setUserId(Integer.valueOf(ALEipUtils.getUserId(rundata)));

      timecard.setWorkFlag(work_flag.getValue());
      timecard.setWorkDate(Calendar.getInstance().getTime());
      timecard.setReason(reason.getValue());

      // 作成日
      timecard.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      timecard.setUpdateDate(Calendar.getInstance().getTime());
      // タイムカードを登録
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        timecard.getTimecardId(),
        ALEventlogConstants.PORTLET_TYPE_TIMECARD,
        null);

    } catch (Exception ex) {
      Database.rollback();
      logger.error("timecard", ex);
      return false;
    }
    return true;
  }

  /**
   * データベースに格納されているタイムカードを更新します。
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
      // オブジェクトモデルを取得
      EipTTimecard timecard = TimecardUtils.getEipTTimecard(rundata, context);
      if (timecard == null) {
        return false;
      }

      timecard.setWorkDate(work_date.getValue());

      timecard.setReason(reason.getValue());

      // 更新日
      timecard.setUpdateDate(Calendar.getInstance().getTime());
      // タイムカードを更新
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        timecard.getTimecardId(),
        ALEventlogConstants.PORTLET_TYPE_TIMECARD,
        reason.getValue());

    } catch (Exception ex) {
      Database.rollback();
      logger.error("timecard", ex);
      return false;
    }
    return true;
  }

  /**
   * 
   * @return
   */
  public void setWorkFlag(String str) {
    work_flag.setValue(str);
  }

  public ALDateTimeField getWorkDate() {
    return work_date;
  }

  public ALStringField getReason() {
    return reason;
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
   * アクセス権限チェック用メソッド。 アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_TIMECARD_TIMECARD_SELF;
  }
}

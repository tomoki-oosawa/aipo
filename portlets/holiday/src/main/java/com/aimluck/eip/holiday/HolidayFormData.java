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

package com.aimluck.eip.holiday;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipMHoliday;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.holiday.util.HolidayUtils;
import com.aimluck.eip.orm.Database;

/**
 * 祝日のフォームデータを管理するクラスです。 <BR>
 * 
 */
public class HolidayFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(HolidayFormData.class.getName());

  /** 祝日の日付 */
  private ALDateField holidayDate;

  /** 祝日の名前 */
  private ALStringField holidayName;

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   */
  @Override
  public void initField() {
    // 祝日の日付
    holidayDate = new ALDateField();
    holidayDate.setFieldName("祝日の日付");
    holidayDate.setValue(HolidayUtils.getCurrentDate());
    // 祝日の名前
    holidayName = new ALStringField();
    holidayName.setFieldName("祝日の名前");
    holidayName.setTrim(true);
  }

  /**
   * 各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // 祝日の日付
    holidayDate.setNotNull(true);
    // 祝日の名前
    holidayName.setNotNull(true);
    holidayName.limitMaxLength(20);
  }

  /**
   * フォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {
    holidayDate.validate(msgList);
    holidayName.validate(msgList);
    return (msgList.size() == 0);
  }

  /**
   * フォームに入力された日付データが <BR>
   * すでに祝日登録されているかどうか検証します。
   * 
   * @param msgList
   * @return
   * 
   */
  private boolean holidayDateOrigialValidate(List<String> msgList) {
    if (msgList == null) {
      msgList = new ArrayList<String>();
    }

    try {
      Expression exp =
        ExpressionFactory.matchExp(
          EipMHoliday.HOLIDAY_DATE_PROPERTY,
          holidayDate.getValue().getDate());

      List<EipMHoliday> holidayList =
        Database.query(EipMHoliday.class, exp).fetchList();
      if (holidayList.size() > 0) {
        msgList.add("指定した日付はすでに祝日登録されています。");
        return false;
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 祝日を読み込みます。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    String date1 = null;
    try {
      // オブジェクトモデルを取得
      EipMHoliday holiday =
        HolidayUtils.getEipMHoliday(rundata, context, false);
      if (holiday == null) {
        return false;
      }
      // 祝日の日付
      if (HolidayUtils.isEmptyDate(holiday.getHolidayDate())) {
        holidayDate.setValue(date1);
      } else {
        holidayDate.setValue(holiday.getHolidayDate());
      }
      // 祝日の名前
      holidayName.setValue(holiday.getHolidayName());
      // 編集時の祝日の日付
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 祝日を追加します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // 新規オブジェクトモデル
      EipMHoliday holiday = Database.create(EipMHoliday.class);

      // 指定した日付がすでに登録されているかどうか確認
      if (!holidayDateOrigialValidate(msgList)) {
        return false;
      }

      // 祝日の日付
      holiday.setHolidayDate(holidayDate.getValue().getDate());
      // 祝日の名前
      holiday.setHolidayName(holidayName.getValue());
      // デフォルトフラグ
      // ユーザー追加分は全て"F"(false）
      holiday.setDefaultFlag("F");
      // Holidayを登録
      Database.commit();
      // 祝日を保持するManagerインスタンスを更新
      HolidayUtils.updateHolidays();

    } catch (Throwable t) {
      Database.rollback();
      logger.error(t);
      return false;
    }
    return true;
  }

  /**
   * 祝日を更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipMHoliday holiday =
        HolidayUtils.getEipMHoliday(rundata, context, false);
      if (holiday == null) {
        return false;
      }

      // 指定した日付がすでに登録されているかどうか確認
      // （ただし、自分自身の日付は除く）
      Date previousHolidayDate = holiday.getHolidayDate();
      Date modifiedHolidayDate = holidayDate.getValue().getDate();
      if (!holidayDateOrigialValidate(msgList)
        && !previousHolidayDate.equals(modifiedHolidayDate)) {
        return false;
      }

      // 祝日の日付
      holiday.setHolidayDate(holidayDate.getValue().getDate());
      // 祝日の名前
      holiday.setHolidayName(holidayName.getValue());
      // Holiday を更新
      Database.commit();
      // 祝日を保持するManagerインスタンスを更新
      HolidayUtils.updateHolidays();

    } catch (Throwable t) {
      Database.rollback();
      logger.error(t);
      return false;
    }
    return true;
  }

  /**
   * 祝日を削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipMHoliday holiday =
        HolidayUtils.getEipMHoliday(rundata, context, false);
      if (holiday == null) {
        return false;
      }

      // Holidayを削除
      Database.delete(holiday);
      Database.commit();
      // 祝日を保持するManagerインスタンスを更新
      HolidayUtils.updateHolidays();

    } catch (Throwable t) {
      Database.rollback();
      logger.error(t);
      return false;
    }
    return true;
  }

  /**
   * 祝日の名前を取得します。 <BR>
   * 
   * @return
   */
  public ALDateField getHolidayDate() {
    return holidayDate;
  }

  /**
   * 祝日の名前を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getHolidayName() {
    return holidayName;
  }

}

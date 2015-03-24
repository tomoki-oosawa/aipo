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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.aimluck.commons.field.ALDateField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTTimecardSettings;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.timecard.util.TimecardUtils;

/**
 * 一日分のタイムカード(出勤・退勤の履歴)を保持する。 一日ごとの勤務時間・残業時間などを計算し、その結果を保持する。
 * 
 * 
 */
public class TimecardSummaryResultData implements ALData {

  private ALDateField date = null;

  private List<TimecardResultData> list = null;

  private ALNumberField shugyo = null;

  private ALNumberField jikannai = null;

  private ALNumberField jikannai1 = null;

  private ALNumberField jikannai2 = null;

  private ALNumberField zangyo = null;

  private ALNumberField zangyo1 = null;

  private ALNumberField zangyo2 = null;

  private ALNumberField kyushutsu = null;

  private ALStringField chikoku = null;

  private ALStringField sotai = null;

  /**
   *
   */
  @Override
  public void initField() {
    date = new ALDateField();
    date.setValue(new Date());
    list = new ArrayList<TimecardResultData>();

    shugyo = new ALNumberField(0);

    jikannai = new ALNumberField(0);
    jikannai1 = new ALNumberField(0);
    jikannai2 = new ALNumberField(0);

    zangyo = new ALNumberField(0);
    zangyo1 = new ALNumberField(0);
    zangyo2 = new ALNumberField(0);

    kyushutsu = new ALNumberField(0);
    chikoku = new ALStringField();
    sotai = new ALStringField();
  }

  /**
   * 
   * @param date
   */
  public void setDate(Date date) {
    this.date.setValue(date);
  }

  /**
   * @return date
   */
  public ALDateField getDate() {
    return date;
  }

  /**
   * 
   * @return
   */
  public ALNumberField getKyushutsu() {
    return kyushutsu;
  }

  /**
   * 
   * @param kyushutsu
   */
  public void setKyushutsu(ALNumberField kyushutsu) {
    this.kyushutsu = kyushutsu;
  }

  /**
   * 
   * @return
   */
  public ALNumberField getShugyo() {
    return shugyo;
  }

  /**
   * 
   * @return
   */
  public ALStringField getShugyoStr() {
    return new ALStringField(minuteToHour(shugyo.getValue()));
  }

  /**
   * 
   * @param shugyo
   */
  public void setShugyo(ALNumberField shugyo) {
    this.shugyo = shugyo;
  }

  /**
   * 
   * @return
   */
  public ALNumberField getJikannai() {
    return jikannai;
  }

  /**
   * 
   * @return
   */
  public ALStringField getJikannaiStr() {
    return new ALStringField(minuteToHour(jikannai.getValue()));
  }

  /**
   * 
   * @return
   */
  public ALNumberField getJikannai1() {
    return jikannai1;
  }

  /**
   * 
   * @return
   */
  public ALStringField getJikannai1Str() {
    return new ALStringField(minuteToHour(jikannai1.getValue()));
  }

  /**
   * 
   * @return
   */
  public ALNumberField getJikannai2() {
    return jikannai2;
  }

  /**
   * 
   * @return
   */
  public ALStringField getJikannai2Str() {
    return new ALStringField(minuteToHour(jikannai2.getValue()));
  }

  /**
   * 
   * @return
   */
  public ALNumberField getZangyo() {
    return zangyo;
  }

  /**
   * 
   * @return
   */
  public ALStringField getZangyoStr() {
    return new ALStringField(minuteToHour(zangyo.getValue()));
  }

  /**
   * 
   * @param zangyo
   */
  public void setZangyo(ALNumberField zangyo) {
    this.zangyo = zangyo;
  }

  /**
   * 
   * @return
   */
  public ALNumberField getZangyo1() {
    return zangyo1;
  }

  /**
   * 
   * @return
   */
  public ALStringField getZangyo1Str() {
    return new ALStringField(minuteToHour(zangyo1.getValue()));
  }

  /**
   * 
   * @return
   */
  public ALNumberField getZangyo2() {
    return zangyo2;
  }

  /**
   * 
   * @return
   */
  public ALStringField getZangyo2Str() {
    return new ALStringField(minuteToHour(zangyo2.getValue()));
  }

  /**
   * 
   * @return
   */
  public ALStringField getChikoku() {
    return chikoku;
  }

  /**
   * 
   * @return
   */
  public ALStringField getSotai() {
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
   * 
   * @return
   */
  public String getDateStr() {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日(EE)");
      return sdf.format(date.getValue().getDate());
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * 
   * @return
   */
  public List<TimecardResultData> getList() {
    return list;
  }

  /**
   * 
   * @return
   */
  public List<TimecardResultData> getViewList() {
    List<TimecardResultData> viewlist = new ArrayList<TimecardResultData>();

    TimecardResultData rd = null;

    int size = list.size();
    for (int i = 0; i < size; i++) {
      rd = list.get(i);
      if (!TimecardUtils.WORK_FLG_DUMMY.equals(rd.getWorkFlag().getValue())) {
        viewlist.add(rd);
      }
    }
    return viewlist;
  }

  /**
   * 
   * @param rd
   */
  public void addTimecardResultData(TimecardResultData rd) {
    list.add(rd);
  }

  /**
   * 就業時間、残業時間などを計算する。
   * 
   */
  public void calc() {
    try {

      // 勤務時間設定データを取得
      EipTTimecardSettings settings = loadEipTTimecardSettings();

      // カレンダーオブジェクトを初期化
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.YEAR, Integer.parseInt(date.getYear()));
      cal.set(Calendar.MONTH, Integer.parseInt(date.getMonth()) - 1);
      cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.getDay()));
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      Date startDate = cal.getTime();
      Date endDate = null;

      cal.set(Calendar.HOUR_OF_DAY, settings.getStartHour());
      cal.set(Calendar.MINUTE, settings.getStartMinute());
      Date kinmuStartDate = cal.getTime();
      cal.set(Calendar.HOUR_OF_DAY, settings.getEndHour());
      cal.set(Calendar.MINUTE, settings.getEndMinute());
      Date kinmuEndDate = cal.getTime();

      List<TimecardResultData> tempList = new ArrayList<TimecardResultData>();

      boolean startflg = false;
      for (TimecardResultData rd : this.list) {
        if (TimecardUtils.WORK_FLG_ON.equals(rd.getWorkFlag().getValue())) {
          startDate = rd.getWorkDate().getValue();
          // 出勤データが連続するときは、退勤データを挿入
          if (startflg == true) {
            tempList.add(createTimecardResultData(
              startDate,
              TimecardUtils.WORK_FLG_OFF));
          }

          tempList.add(rd);
          startflg = true;

        } else if (TimecardUtils.WORK_FLG_OFF.equals(rd
          .getWorkFlag()
          .getValue())) {
          // 日の最初が出勤ではないときは、0:00のデータを追加する
          // 退勤データが連続するときは、出勤データを挿入
          if (startflg == false) {
            tempList.add(createTimecardResultData(
              startDate,
              TimecardUtils.WORK_FLG_ON));
          }

          endDate = rd.getWorkDate().getValue();

          // 勤務時間内外の区切るを挿入
          if (kinmuStartDate.after(startDate) && kinmuStartDate.before(endDate)) {
            tempList.add(createTimecardResultData(
              kinmuStartDate,
              TimecardUtils.WORK_FLG_OFF));
            tempList.add(createTimecardResultData(
              kinmuStartDate,
              TimecardUtils.WORK_FLG_ON));
          }
          if (kinmuEndDate.after(startDate) && kinmuEndDate.before(endDate)) {
            tempList.add(createTimecardResultData(
              kinmuEndDate,
              TimecardUtils.WORK_FLG_OFF));
            tempList.add(createTimecardResultData(
              kinmuEndDate,
              TimecardUtils.WORK_FLG_ON));
          }

          tempList.add(rd);

          startDate = endDate;
          startflg = false;
        }
      }
      // 最後に退勤していない場合は、0:00退勤のデータを追加する。
      if (startflg) {

        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        endDate = cal.getTime();

        // 勤務時間内外の区別をつける
        if (kinmuStartDate.after(startDate) && kinmuStartDate.before(endDate)) {
          tempList.add(createTimecardResultData(
            kinmuStartDate,
            TimecardUtils.WORK_FLG_OFF));
          tempList.add(createTimecardResultData(
            kinmuStartDate,
            TimecardUtils.WORK_FLG_ON));
        }
        if (kinmuEndDate.after(startDate) && kinmuEndDate.before(endDate)) {
          tempList.add(createTimecardResultData(
            kinmuEndDate,
            TimecardUtils.WORK_FLG_OFF));
          tempList.add(createTimecardResultData(
            kinmuEndDate,
            TimecardUtils.WORK_FLG_ON));
        }

        TimecardResultData rd = new TimecardResultData();
        rd.initField();
        rd.setWorkDate(endDate);
        rd.setWorkFlag(TimecardUtils.WORK_FLG_OFF);
        tempList.add(rd);
      }

      // 整列されたデータを順に見て、就業時間などを数える
      long shugyo_temp = 0;
      long jikannai_temp = 0;
      long zangyo_temp = 0;
      boolean iszangyo = false;
      for (TimecardResultData rd : tempList) {
        if (TimecardUtils.WORK_FLG_ON.equals(rd.getWorkFlag().getValue())) {
          startDate = rd.getWorkDate().getValue();
          if (kinmuStartDate.equals(startDate)
            || (kinmuStartDate.before(startDate) && kinmuEndDate
              .after(startDate))) {
            iszangyo = false;
          } else {
            iszangyo = true;
          }
        } else if (TimecardUtils.WORK_FLG_OFF.equals(rd
          .getWorkFlag()
          .getValue())) {
          endDate = rd.getWorkDate().getValue();
          long millisecond = endDate.getTime() - startDate.getTime();
          shugyo_temp += millisecond / 1000 / 60;
          if (iszangyo) {
            zangyo_temp += millisecond / 1000 / 60;
          } else {
            jikannai_temp += millisecond / 1000 / 60;
          }
        }
      }

      // 集計した結果を各変数にセットする。
      // 勤務時間内
      this.jikannai1.setValue(jikannai_temp);
      // if (settings != null && jikannai_temp >= settings.getWorktimeIn()) {
      if (jikannai_temp >= settings.getWorktimeIn()) {
        this.jikannai.setValue(jikannai_temp - settings.getResttimeIn());
        this.jikannai2.setValue(settings.getResttimeIn());
      } else {
        this.jikannai.setValue(jikannai_temp);
        this.jikannai2.setValue(0);
      }

      // 勤務時間外
      long nnn = zangyo_temp / settings.getWorktimeOut().intValue();
      this.zangyo.setValue(zangyo_temp - settings.getResttimeOut() * nnn);
      this.zangyo1.setValue(zangyo_temp);
      this.zangyo2.setValue(settings.getResttimeOut() * nnn);

      // 勤務時間内・外
      this.shugyo.setValue(this.jikannai.getValue() + this.zangyo.getValue());

      // 遅刻
      if (tempList.size() > 0) {
        if (kinmuStartDate.before(tempList.get(0).getWorkDate().getValue())) {
          this.chikoku.setValue("○");
        }
      }
      // 相対
      if (tempList.size() > 0) {
        if (kinmuEndDate.after(tempList
          .get(tempList.size() - 1)
          .getWorkDate()
          .getValue())) {
          this.sotai.setValue("○");
        }
      }

    } catch (RuntimeException ex) {
      ex.printStackTrace();
      throw ex;
    }
  }

  /**
   * TimecardResultDataオブジェクトのインスタンスを作る
   * 
   * @param date
   * @param workflag
   * @return
   */
  private TimecardResultData createTimecardResultData(Date date, String workflag) {
    TimecardResultData rd = new TimecardResultData();
    rd.initField();
    rd.setWorkDate(date);
    rd.setWorkFlag(workflag);
    return rd;
  }

  /**
   * 勤務時間設定をDBから取得する
   * 
   * @return
   */
  private EipTTimecardSettings loadEipTTimecardSettings() {

    SelectQuery<EipTTimecardSettings> query =
      Database.query(EipTTimecardSettings.class);

    return query.fetchSingle();
  }

}

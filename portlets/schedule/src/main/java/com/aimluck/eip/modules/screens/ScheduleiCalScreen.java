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
package com.aimluck.eip.modules.screens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.property.XProperty;
import net.fortuna.ical4j.util.UidGenerator;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.modules.screens.RawScreen;
import org.apache.turbine.util.RunData;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.schedule.ScheduleSearchResultData;
import com.aimluck.eip.schedule.ScheduleiCalSelectData;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class ScheduleiCalScreen extends RawScreen implements ALAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleiCalScreen.class.getName());

  /**
   * @param rundata
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @Override
  protected void doOutput(RunData rundata) throws Exception {

    ScheduleiCalSelectData selectData = new ScheduleiCalSelectData();
    selectData.initField();

    ALEipUser user = ALEipUtils.getALEipUser(rundata);

    VelocityContext context = new VelocityContext();
    ALEipUtils.setupContext(rundata, context);

    // 前後3ヶ月の予定を取得
    Calendar date = Calendar.getInstance();
    date.add(Calendar.MONTH, -3);
    date.set(Calendar.HOUR_OF_DAY, 0);
    date.set(Calendar.MINUTE, 0);
    date.set(Calendar.SECOND, 0);
    date.set(Calendar.MILLISECOND, 0);
    java.util.Date startDate = date.getTime();
    date.add(Calendar.MONTH, 6);
    java.util.Date endDate = date.getTime();

    selectData.doViewList(this, rundata, context);

    List<Object> resultList = selectData.getList();
    Map<Integer, List<ScheduleSearchResultData>> dummyMaps =
      selectData.getDummyMaps();

    net.fortuna.ical4j.model.Calendar cal =
      new net.fortuna.ical4j.model.Calendar();
    cal.getProperties().add(new ProdId("-//TOWN, Inc. //Aipo //JP"));
    cal.getProperties().add(Version.VERSION_2_0);
    cal.getProperties().add(CalScale.GREGORIAN);
    cal.getProperties().add(Method.PUBLISH);
    cal.getProperties().add(
      new XProperty("X-WR-CALNAME", user.getAliasName().getValue()));

    TimeZoneRegistry registry =
      TimeZoneRegistryFactory.getInstance().createRegistry();
    TimeZone timezone = registry.getTimeZone("Asia/Tokyo");
    VTimeZone tz = timezone.getVTimeZone();

    cal.getComponents().add(tz);

    for (Object result : resultList) {
      ScheduleSearchResultData rd = (ScheduleSearchResultData) result;

      java.util.Calendar cStart = Calendar.getInstance();
      cStart.setTime(rd.getStartDate().getValue());
      java.util.Calendar cEnd = Calendar.getInstance();
      cEnd.setTime(rd.getEndDate().getValue());

      Date dStart = null;
      Date dEnd = null;
      String ptn = rd.getPattern();

      if ("S".equals(rd.getPattern())) {
        cStart.add(Calendar.DATE, 1);
        dStart = new Date(cStart.getTime());
        if (cEnd.get(Calendar.HOUR_OF_DAY) == 0
          && cEnd.get(Calendar.MINUTE) == 0
          && cEnd.get(Calendar.SECOND) == 0) {
          cEnd.add(Calendar.DATE, 2);
        } else {
          cEnd.add(Calendar.DATE, 1);
        }
        dEnd = new Date(cEnd.getTime());
      } else {
        dStart = new DateTime(cStart.getTime());
        dEnd = new DateTime(cEnd.getTime());
      }

      java.util.Date currentStartDate = getRepeatStartDate(startDate, ptn);

      Recur recur = null;
      int count = 0;
      // 毎日
      if (ptn.charAt(0) == 'D') {
        recur = new Recur(Recur.DAILY, null);
        count = 1;
        // 毎週
      } else if (ptn.charAt(0) == 'W') {
        recur = new Recur(Recur.WEEKLY, null);

        boolean isEveryWeek;
        int a = Character.getNumericValue(ptn.charAt(8)); // アルファベットは10以上の数字に、その他の記号、日本語等は-1に変換される
        if (a >= 0 && a <= 9) {
          count = 9;
          isEveryWeek = false;
        } else {
          count = 8;
          isEveryWeek = true;
        }
        if (isEveryWeek) {
          if (ptn.charAt(1) != '0') {
            recur.getDayList().add(WeekDay.SU);
          }
          if (ptn.charAt(2) != '0') {
            recur.getDayList().add(WeekDay.MO);
          }
          if (ptn.charAt(3) != '0') {
            recur.getDayList().add(WeekDay.TU);
          }
          if (ptn.charAt(4) != '0') {
            recur.getDayList().add(WeekDay.WE);
          }
          if (ptn.charAt(5) != '0') {
            recur.getDayList().add(WeekDay.TH);
          }
          if (ptn.charAt(6) != '0') {
            recur.getDayList().add(WeekDay.FR);
          }
          if (ptn.charAt(7) != '0') {
            recur.getDayList().add(WeekDay.SA);
          }
        } else {
          if (ptn.charAt(1) != '0') {
            recur.getDayList().add(new WeekDay(WeekDay.SU, a));
          }
          if (ptn.charAt(2) != '0') {
            recur.getDayList().add(new WeekDay(WeekDay.MO, a));
          }
          if (ptn.charAt(3) != '0') {
            recur.getDayList().add(new WeekDay(WeekDay.TU, a));
          }
          if (ptn.charAt(4) != '0') {
            recur.getDayList().add(new WeekDay(WeekDay.WE, a));
          }
          if (ptn.charAt(5) != '0') {
            recur.getDayList().add(new WeekDay(WeekDay.TH, a));
          }
          if (ptn.charAt(6) != '0') {
            recur.getDayList().add(new WeekDay(WeekDay.FR, a));
          }
          if (ptn.charAt(7) != '0') {
            recur.getDayList().add(new WeekDay(WeekDay.SA, a));
          }
        }
      } else if (ptn.charAt(0) == 'M') {
        recur = new Recur(Recur.MONTHLY, null);
        int mday;
        if (ptn.substring(1, 3).equals("XX")) {
          mday = -1;
        } else {
          mday = Integer.parseInt(ptn.substring(1, 3));
        }
        recur.getMonthList().addAll(
          Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
        recur.getMonthDayList().add(mday);
        count = 3;
      } else if (ptn.charAt(0) == 'Y') {
        recur = new Recur(Recur.YEARLY, null);
        int ymonth = Integer.parseInt(ptn.substring(1, 3));
        int yday = Integer.parseInt(ptn.substring(3, 5));
        recur.getMonthList().add(ymonth);
        recur.getMonthDayList().add(yday);
        count = 5;
      }

      if (count > 0) {
        if (ptn.charAt(count) == 'L') {
          if (endDate.compareTo(cStart.getTime()) < 0
            || cEnd.getTime().compareTo(startDate) < 0) {
            // 期間指定の繰り返しスケジュールで、前後３ヶ月の範囲外のスケジュールは表示しない
            continue;
          }
          if (endDate.compareTo(cEnd.getTime()) < 0) {
            recur.setUntil(new DateTime(endDate));
          } else {
            recur.setUntil(new DateTime(cEnd.getTime()));
          }
          if (cStart.getTime().compareTo(startDate) < 0) {
            int hour = cStart.get(Calendar.HOUR_OF_DAY);
            int min = cStart.get(Calendar.MINUTE);
            cStart.setTime(currentStartDate);
            cStart.set(Calendar.HOUR_OF_DAY, hour);
            cStart.set(Calendar.MINUTE, min);
            hour = cEnd.get(Calendar.HOUR_OF_DAY);
            min = cEnd.get(Calendar.MINUTE);
            cEnd.setTime(currentStartDate);
            cEnd.set(Calendar.HOUR_OF_DAY, hour);
            cEnd.set(Calendar.MINUTE, min);
            dStart = new DateTime(cStart.getTime());
            dEnd = new DateTime(cEnd.getTime());
          } else {
            java.util.Date RepeatStartDate = getRepeatStartDate(dStart, ptn);
            int hour = cStart.get(Calendar.HOUR_OF_DAY);
            int min = cStart.get(Calendar.MINUTE);
            cStart.setTime(RepeatStartDate);
            cStart.set(Calendar.HOUR_OF_DAY, hour);
            cStart.set(Calendar.MINUTE, min);
            hour = cEnd.get(Calendar.HOUR_OF_DAY);
            min = cEnd.get(Calendar.MINUTE);
            cEnd.setTime(RepeatStartDate);
            cEnd.set(Calendar.HOUR_OF_DAY, hour);
            cEnd.set(Calendar.MINUTE, min);
            dStart = new DateTime(cStart.getTime());
            dEnd = new DateTime(cEnd.getTime());
          }
        } else {
          recur.setUntil(new DateTime(endDate.getTime()));
          int hour = cStart.get(Calendar.HOUR_OF_DAY);
          int min = cStart.get(Calendar.MINUTE);
          cStart.setTime(currentStartDate);
          cStart.set(Calendar.HOUR_OF_DAY, hour);
          cStart.set(Calendar.MINUTE, min);
          hour = cEnd.get(Calendar.HOUR_OF_DAY);
          min = cEnd.get(Calendar.MINUTE);
          cEnd.setTime(currentStartDate);
          cEnd.set(Calendar.HOUR_OF_DAY, hour);
          cEnd.set(Calendar.MINUTE, min);
          dStart = new DateTime(cStart.getTime());
          dEnd = new DateTime(cEnd.getTime());
          if (ptn.charAt(0) == 'Y') {
            if (endDate.compareTo(cStart.getTime()) < 0
              || startDate.compareTo(cEnd.getTime()) > 0) {
              recur = null;
              dStart = null;
              dEnd = null;
            }
          }
        }
      }

      VEvent event = new VEvent(dStart, dEnd, rd.getName().getValue());

      String place = rd.getPlace().getValue();
      if (place != null && place.length() > 0) {
        event.getProperties().add(new Location(place));
      }

      String description = rd.getDescription().getValue();
      if (description != null && description.length() > 0) {
        event.getProperties().add(new Description(description));
      }

      event.getProperties().add(
        new UidGenerator(rd.getScheduleId().getValueAsString()).generateUid());

      if (recur != null) {
        event.getProperties().add(new RRule(recur));
        if (dummyMaps.containsKey((int) rd.getScheduleId().getValue())) {
          List<ScheduleSearchResultData> list =
            dummyMaps.get((int) rd.getScheduleId().getValue());
          DateList dateList = new DateList();
          for (ScheduleSearchResultData dummy : list) {
            java.util.Calendar dummyStart = Calendar.getInstance();
            dummyStart.setTime(dummy.getStartDate().getValue());
            dummyStart.set(Calendar.HOUR_OF_DAY, cStart
              .get((Calendar.HOUR_OF_DAY)));
            dummyStart.set(Calendar.MINUTE, cStart.get((Calendar.MINUTE)));
            dateList.add(new DateTime(dummyStart.getTime()));
          }
          event.getProperties().add(new ExDate(dateList));
        }
        Character ptnFirst = ptn.charAt(0);
        if (ptnFirst == 'D'
          || ptnFirst == 'W'
          || ptnFirst == 'M'
          || ptnFirst == 'Y') {
          Character ptnLast = ptn.charAt(ptn.length() - 1);
          if (ptnLast == 'D' || ptnLast == 'A' || ptnLast == 'B') {
            // 候補日のリストを作成
            List<ALDateTimeField> candidateList =
              new ArrayList<ALDateTimeField>();
            ALDateTimeField candidate = new ALDateTimeField("yyyy-MM-dd");
            // candidate.setValue(dStart);
            Calendar cal2 = Calendar.getInstance();
            cal2 = (Calendar) cStart.clone();
            // cal2.set(Integer.parseInt(candidate.getYear()),
            // Integer.parseInt(candidate.getMonth()),
            // Integer.parseInt(candidate.getDay()));
            // cal2.set(Calendar.HOUR_OF_DAY,
            // Integer.parseInt(candidate.getHour()));
            // cal2.set(Calendar.MINUTE,
            // Integer.parseInt(candidate.getMinute()));

            // candidateListを完成させる
            cEnd.setTime(rd.getEndDate().getValue());
            dEnd = new DateTime(cEnd.getTime());
            Calendar eEnd = Calendar.getInstance();
            eEnd.setTime(dEnd);
            switch (ptnFirst) {
              case 'D':
                while (cal2.compareTo(eEnd) <= 0) {
                  candidate.setValue(cal2.getTime());
                  candidateList.add(candidate);
                  candidate = new ALDateTimeField("yyyy-MM-dd");
                  cal2.add(Calendar.DATE, 1);
                }
                break;
              case 'W':
                boolean isEveryWeek;
                int a = Character.getNumericValue(ptn.charAt(8)); // アルファベットは10以上の数字に、その他の記号、日本語等は-1に変換される
                if (a >= 0 && a <= 9) {
                  isEveryWeek = false;
                } else {
                  isEveryWeek = true;
                }
                List<Integer> dayOfWeekList = new ArrayList<Integer>();
                for (int i = 0; i < 7; i++) {
                  if (ptn.charAt(i + 1) == '1') {
                    dayOfWeekList.add(i);
                  }
                }
                int startDayOfWeek = cal2.get(Calendar.DAY_OF_WEEK);
                int currentIdx = dayOfWeekList.indexOf(startDayOfWeek);
                int num = dayOfWeekList.size();

                while (cal2.compareTo(eEnd) <= 0) {
                  candidate.setValue(cal2.getTime());
                  candidateList.add(candidate);
                  candidate = new ALDateTimeField("yyyy-MM-dd");
                  cal2.add(Calendar.DATE, (dayOfWeekList.get((currentIdx + 1)
                    % num) - dayOfWeekList.get((currentIdx) % num + 7) % 7));
                  if (currentIdx == num - 1 && !isEveryWeek) {
                    cal2.add(Calendar.MONTH, 1);
                  }
                  currentIdx++;
                  currentIdx = (currentIdx) % num;
                }
                break;
              case 'M':
                while (cal2.compareTo(eEnd) <= 0) {
                  candidate.setValue(cal2.getTime());
                  candidateList.add(candidate);
                  candidate = new ALDateTimeField("yyyy-MM-dd");
                  cal2.add(Calendar.MONTH, 1);
                }
                break;
              case 'Y':
                while (cal2.compareTo(eEnd) <= 0) {
                  candidate.setValue(cal2.getTime());
                  candidateList.add(candidate);
                  candidate = new ALDateTimeField("yyyy-MM-dd");
                  cal2.add(Calendar.YEAR, 1);
                }
                break;
            }
            DateList deleteList = new DateList();
            DateList addList = new DateList();
            switch (ptn.charAt(ptn.length() - 1)) {
              case 'D':
                for (int i = 0; i < candidateList.size(); i++) {
                  if (!ScheduleUtils.isView(
                    candidateList.get(i),
                    ptn,
                    dStart,
                    dEnd)) {
                    deleteList
                      .add(new DateTime(candidateList.get(i).getValue()));
                  }
                }
                break;
              case 'A':
                for (int i = 0; i < candidateList.size(); i++) {
                  if (!ScheduleUtils.isView(
                    candidateList.get(i),
                    ptn,
                    dStart,
                    dEnd)) {
                    deleteList
                      .add(new DateTime(candidateList.get(i).getValue()));
                    Calendar cal3 = Calendar.getInstance();
                    cal3.setTime(candidateList.get(i).getValue());
                    int day_count = cal3.get(Calendar.DAY_OF_WEEK);
                    while (ScheduleUtils.isUserHoliday(day_count - 1)) {
                      cal3.add(Calendar.DATE, -1);
                      day_count = cal3.get(Calendar.DAY_OF_WEEK);
                    }
                    addList.add(new DateTime(cal3.getTime()));
                  }
                }
                break;
              case 'B':
                for (int i = 0; i < candidateList.size(); i++) {
                  if (!ScheduleUtils.isView(
                    candidateList.get(i),
                    ptn,
                    dStart,
                    dEnd)) {
                    deleteList
                      .add(new DateTime(candidateList.get(i).getValue()));
                    Calendar cal3 = Calendar.getInstance();
                    cal3.setTime(candidateList.get(i).getValue());
                    int day_count = cal3.get(Calendar.DAY_OF_WEEK);
                    while (ScheduleUtils.isUserHoliday(day_count - 1)) {
                      cal3.add(Calendar.DATE, -1);
                      day_count = cal3.get(Calendar.DAY_OF_WEEK);
                    }
                    addList.add(new DateTime(cal3.getTime()));
                  }
                }
                break;
            }
            event.getProperties().add(new ExDate(deleteList));
            event.getProperties().add(new RDate(addList));
          }
        }
      }
      cal.getComponents().add(event);

    }

    ServletOutputStream out = null;
    try {

      out = rundata.getResponse().getOutputStream();
      out.write(cal.toString().getBytes(ALEipConstants.DEF_CONTENT_ENCODING));

    } catch (Throwable t) {
      logger.error("[ScheduleiCalScreen]", t);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (Throwable ignore) {

        }
      }
    }
  }

  // 休日か如何を考慮せずに、候補日のうち最も早いものを返す
  private java.util.Date getRepeatStartDate(java.util.Date startDate, String ptn) {
    try {
      Calendar cal = Calendar.getInstance();
      logger.error(startDate);
      cal.setTime(startDate);

      switch (ptn.charAt(0)) {
        case 'D':
          break;
        case 'W':
          while (ptn.charAt(cal.get(Calendar.DAY_OF_WEEK)) != '1') {
            cal.add(Calendar.DATE, 1);
          }
          break;
        case 'M':
          while (!(Integer.parseInt(ptn.substring(1, 3)) == cal
            .get(Calendar.DATE))) {
            cal.add(Calendar.DATE, 1);
          }
          break;
        case 'Y':
          while (Integer.parseInt(ptn.substring(1, 3)) == cal
            .get(Calendar.MONTH)
            && Integer.parseInt(ptn.substring(3, 5)) == cal.get(Calendar.DATE)) {
            cal.add(Calendar.DATE, 1);
          }
          break;
      }
      startDate = new DateTime(cal.getTime());
      return startDate;
    } catch (Exception e) {
      logger.error(e);
    }
    return new Date();
  }

  private boolean isView(Calendar cal, String ptn) {
    boolean result = false;

    Calendar cal_dummy = Calendar.getInstance();
    cal_dummy.setTime(cal.getTime());

    int mday_dummy = cal.get(Calendar.DATE);
    int yday_dummy = cal.get(Calendar.DATE);
    int ymonth_dummy = cal.get(Calendar.MONTH) + 1;
    int day_count = cal.get(Calendar.DAY_OF_WEEK);
    int day_count_dummy = day_count;

    int shift = 5;
    if (ptn.charAt(ptn.length() - 1) != 'N' && ptn.length() > 2) {
      if (ptn.charAt(ptn.length() - 1) == 'A') {
        shift = 1;
      } else if (ptn.charAt(ptn.length() - 1) == 'B') {
        shift = -1;
      } else if (ptn.charAt(ptn.length() - 1) == 'D') {
        shift = 0;
      }
    }

    // 祝日判定
    ScheduleUtils.setDate(cal.getTime());

    if ((ScheduleUtils.isHoliday() || ScheduleUtils
      .isUserHoliday(day_count - 1))
      && shift != 5) {
      result = false;
      return result;
    }

    // 毎日
    if (ptn.charAt(0) == 'D') {
      result = true;
      // 毎週
    } else if (ptn.charAt(0) == 'W') {

      int dow = cal.get(Calendar.DAY_OF_WEEK);
      int dowim = cal.get(Calendar.DAY_OF_WEEK_IN_MONTH);
      if (ptn.charAt(8) == 'N'
        || ptn.charAt(8) == 'L'
        || dowim == Character.getNumericValue(ptn.charAt(8))) {
        switch (dow) {
        // 日
          case Calendar.SUNDAY:
            result = ptn.charAt(1) != '0';
            break;
          // 月
          case Calendar.MONDAY:
            result = ptn.charAt(2) != '0';
            break;
          // 火
          case Calendar.TUESDAY:
            result = ptn.charAt(3) != '0';
            break;
          // 水
          case Calendar.WEDNESDAY:
            result = ptn.charAt(4) != '0';
            break;
          // 木
          case Calendar.THURSDAY:
            result = ptn.charAt(5) != '0';
            break;
          // 金
          case Calendar.FRIDAY:
            result = ptn.charAt(6) != '0';
            break;
          // 土
          case Calendar.SATURDAY:
            result = ptn.charAt(7) != '0';
            break;
          default:
            result = false;
            break;
        }

        // 今日が第何週目か
        int week_count_today = cal.get(Calendar.DAY_OF_WEEK_IN_MONTH);
        // 予定は第何週目か(毎週の予定ならば予定と同じ値にする)
        int week_count_schedule = cal.get(Calendar.DAY_OF_WEEK_IN_MONTH);
        if (ptn.length() == 11) {
          week_count_schedule = Character.getNumericValue(ptn.charAt(8));
        }

        switch (shift) {
          case -1:
            cal_dummy.add(Calendar.DATE, 1);
            ScheduleUtils.setDate(cal_dummy.getTime());
            day_count_dummy++;// 日を1日進める
            while (ScheduleUtils.isHoliday()
              || ScheduleUtils.isUserHoliday((day_count_dummy - 1) % 7)) { // 休日である限り繰り返す
              if (ptn.charAt((day_count_dummy - 1) % 7 + 1) == '1') { // 進んだ先に予定がある
                if (week_count_today == week_count_schedule) { // 今日の予定と予定の週が同じ
                  // 今日の予定に組み込む(予定ありとしてtrueを返す)
                  result = true;
                  // return result;
                }
              }
              // 日を1日進める
              cal_dummy.add(Calendar.DATE, 1);
              ScheduleUtils.setDate(cal_dummy.getTime());
              day_count_dummy++;
            }
            break;
          case 1:
            // 日を1日戻す
            cal_dummy.add(Calendar.DATE, -1);
            ScheduleUtils.setDate(cal_dummy.getTime());
            day_count_dummy += 6;
            while (ScheduleUtils.isHoliday()
              || ScheduleUtils.isUserHoliday((day_count_dummy - 1) % 7)) { // 休日である限り繰り返す
              if (ptn.charAt((day_count_dummy - 1) % 7 + 1) == '1') { // 戻った先に予定がある
                // 今日の予定に組み込む(予定ありとしてtrueを返す)
                if (week_count_today == week_count_schedule) {
                  result = true;
                  // return result;
                }
              }
              // 日を1日戻す
              cal_dummy.add(Calendar.DATE, -1);
              ScheduleUtils.setDate(cal_dummy.getTime());
              day_count_dummy += 6;
            }
            break;
          default:
            break;
        }
        ScheduleUtils.setDate(cal.getTime()); // setDateを戻す(念のため)
      }
      // 毎月
    } else if (ptn.charAt(0) == 'M') {
      int mday;
      if (ptn.substring(1, 3).equals("XX")) {
        mday = cal.getMaximum(Calendar.DATE);
      } else {
        mday = Integer.parseInt(ptn.substring(1, 3));
      }

      result = cal.get(Calendar.DATE) == mday;

      // 予定をずらす
      switch (shift) {
        case -1:
          // 日を１日進める
          cal_dummy.add(Calendar.DATE, 1);
          ScheduleUtils.setDate(cal_dummy.getTime());
          mday_dummy = cal_dummy.get(Calendar.DATE);
          day_count_dummy++;
          while (ScheduleUtils.isHoliday()
            || ScheduleUtils.isUserHoliday((day_count_dummy - 1) % 7)) { // 休日である限り
            if (mday == mday_dummy) { // 進んだ先に予定がある
              result = true;
            }
            cal_dummy.add(Calendar.DATE, 1);
            ScheduleUtils.setDate(cal_dummy.getTime());
            mday_dummy = cal_dummy.get(Calendar.DATE);
            day_count_dummy++;
          }
          break;
        case 1:
          // 日を１日遡る
          // 月末処理用
          if (ptn.substring(1, 3).equals("XX")) {
            Calendar cal_dummy_2 = Calendar.getInstance();
            cal_dummy_2.setTime(cal.getTime());
            cal_dummy_2.add(Calendar.MONTH, -1);
            mday = cal_dummy_2.getActualMaximum(Calendar.DATE);
          }

          cal_dummy.add(Calendar.DATE, -1);
          ScheduleUtils.setDate(cal_dummy.getTime());
          mday_dummy = cal_dummy.get(Calendar.DATE);
          day_count_dummy += 6;
          while (ScheduleUtils.isHoliday()
            || ScheduleUtils.isUserHoliday((day_count_dummy - 1) % 7)) { // 休日である限り
            if (mday == mday_dummy) { // 遡った先に予定がある
              result = true;
            }
            cal_dummy.add(Calendar.DATE, -1);
            ScheduleUtils.setDate(cal_dummy.getTime());
            mday_dummy = cal_dummy.get(Calendar.DATE);
            day_count_dummy += 6;
          }
          break;
        default:
          break;
      }
      ScheduleUtils.setDate(cal.getTime());

    } else if (ptn.charAt(0) == 'Y') {
      int ymonth = Integer.parseInt(ptn.substring(1, 3));
      int yday = Integer.parseInt(ptn.substring(3, 5));

      result =
        cal.get(Calendar.MONTH) == ymonth - 1 && cal.get(Calendar.DATE) == yday;

      // 予定をずらす
      switch (shift) {
        case -1:
          // 日を１日進める
          cal_dummy.add(Calendar.DATE, 1);
          ScheduleUtils.setDate(cal_dummy.getTime());
          yday_dummy = cal_dummy.get(Calendar.DATE);
          ymonth_dummy = cal_dummy.get(Calendar.MONTH) + 1;
          day_count_dummy++;
          while (ScheduleUtils.isHoliday()
            || ScheduleUtils.isUserHoliday((day_count_dummy - 1) % 7)) { // 休日である限り
            if ((yday == yday_dummy) && (ymonth == ymonth_dummy)) { // 進んだ先に予定がある
              result = true;
            }
            cal_dummy.add(Calendar.DATE, 1);
            ScheduleUtils.setDate(cal_dummy.getTime());
            yday_dummy = cal_dummy.get(Calendar.DATE);
            ymonth_dummy = cal_dummy.get(Calendar.MONTH) + 1;
            day_count_dummy++;
          }
          break;
        case 1:
          // 日を１日遡る
          cal_dummy.add(Calendar.DATE, -1);
          ScheduleUtils.setDate(cal_dummy.getTime());
          yday_dummy = cal_dummy.get(Calendar.DATE);
          ymonth_dummy = cal_dummy.get(Calendar.MONTH) + 1;
          day_count_dummy += 6;
          while (ScheduleUtils.isHoliday()
            || ScheduleUtils.isUserHoliday((day_count_dummy - 1) % 7)) { // 休日である限り
            if ((yday == yday_dummy) && (ymonth == ymonth_dummy)) { // 遡った先に予定がある
              result = true;
            }
            cal_dummy.add(Calendar.DATE, -1);
            ScheduleUtils.setDate(cal_dummy.getTime());
            yday_dummy = cal_dummy.get(Calendar.DATE);
            ymonth_dummy = cal_dummy.get(Calendar.MONTH) + 1;
            day_count_dummy += 6;
          }
          break;
        default:
          break;
      }
      ScheduleUtils.setDate(cal.getTime());

    } else {
      return true;
    }

    return result;
  }

  /**
   * @param rundata
   * @return
   */
  @Override
  protected String getContentType(RunData rundata) {
    return "text/calendar";
  }

  /**
   * @param obj
   */
  @Override
  public void setResultData(Object obj) {
  }

  /**
   * @param obj
   */
  @Override
  public void addResultData(Object obj) {
  }

  /**
   * @param objList
   */
  @Override
  public void setResultDataList(List<Object> objList) {
  }

  /**
   * @param msg
   */
  @Override
  public void addErrorMessage(String msg) {
  }

  /**
   * @param msgs
   */
  @Override
  public void addErrorMessages(List<String> msgs) {
  }

  /**
   * @param msgs
   */
  @Override
  public void setErrorMessages(List<String> msgs) {
  }

  /**
   * @param mode
   */
  @Override
  public void setMode(String mode) {
  }

  /**
   * @return
   */
  @Override
  public String getMode() {
    return null;
  }

  /**
   * @param rundata
   * @param context
   */
  @Override
  public void putData(RunData rundata, Context context) {
  }

}

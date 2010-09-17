/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2010 Aimluck,Inc.
 * http://aipostyle.com/
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.schedule.beans.CellAppScheduleBean;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.util.ALEipUtils;

public class CellAppScheduleSelectData {

  /** <code>logger</code> logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CellAppScheduleSelectData.class.getName());

  // 固有文字
  private final String START_DATE = "f";

  private final String END_DATE = "t";

  private final String SERVICE_TYPE = "s";

  // private final String LOGIN_ID = "i";

  // private final String PASS = "p";

  private final String TYPE_AIPO = "i";

  // error message

  private final String missing_startDate = "start date not be set";

  private final String missing_endDate = "end date not be set";

  private final String notForAipo = "request not for Aipo";

  private final String dateillegal = "date setting is illegal";

  private final String illegal_date = "illegal date error";

  // private final String loginError = "fail to login ,please do again";

  private String target_user_id;

  private ALDateTimeField startDate;

  private ALDateTimeField endDate;

  private List<String> msgList;

  private List<ScheduleDetailResultData> list;

  private List<CellAppScheduleBean> csvlist;

  private List<ScheduleDayContainer> allContainer;

  // ACL
  private String aclPortletFeature;

  public void init(RunData rundata) {
    target_user_id = String.valueOf(ALEipUtils.getUserId(rundata));
    startDate = new ALDateTimeField("yyyyMMddHHmmss") {

      private static final long serialVersionUID = -7569484329141837634L;

      @Override
      protected String translateDate(Date date, String dateFormat) {
        if (date == null) {
          return "Unknown";
        }
        // 日付を表示形式に変換
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(true);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
      }
    };
    endDate = new ALDateTimeField("yyyyMMddHHmmss") {

      private static final long serialVersionUID = -755157400664490157L;

      @Override
      protected String translateDate(Date date, String dateFormat) {
        if (date == null) {
          return "Unknown";
        }
        // 日付を表示形式に変換
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(true);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
      }
    };
    msgList = new ArrayList<String>();
    allContainer = new ArrayList<ScheduleDayContainer>();
    csvlist = new ArrayList<CellAppScheduleBean>();
    aclPortletFeature = ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_SELF;

    if (rundata.getParameters().containsKey(START_DATE)) {
      StringBuffer start_date =
        new StringBuffer(rundata.getParameters().getString(START_DATE));

      if (start_date.length() != 8) {
        msgList.add(illegal_date);
      }
      start_date.append("000000");
      startDate.setValue(start_date.toString());
    } else {
      msgList.add(missing_startDate);
    }

    if (rundata.getParameters().containsKey(END_DATE)) {
      StringBuffer end_date =
        new StringBuffer(rundata.getParameters().getString(END_DATE));
      // ALDateTimeFieldのsetValue時のvalidateを変えるべきか？
      if (end_date.length() != 8) {
        msgList.add(illegal_date);
      }
      end_date.append("235959");
      endDate.setValue(end_date.toString());
    } else {
      msgList.add(missing_endDate);
    }

    if (rundata.getParameters().containsKey(SERVICE_TYPE)) {
      if (!rundata.getParameters().getString(SERVICE_TYPE).equals(TYPE_AIPO)) {
        msgList.add(notForAipo);
      }
    }

  }

  public boolean validate() {
    startDate.validate(msgList);
    endDate.validate(msgList);
    if (msgList.size() != 0) {
      if (!(startDate.getValue().getTime() <= endDate.getValue().getTime())) {
        msgList.add(dateillegal);
      }
      return false;
    } else {
      return true;
    }
  }

  public boolean getViewList(RunData rundata) {
    try {
      // ACL
      doCheckAclPermission(rundata, ALAccessControlConstants.VALUE_ACL_LIST);

      List<EipTScheduleMap> aList = selectList(rundata);
      if (aList != null) {
        list = new ArrayList<ScheduleDetailResultData>();
        ScheduleDetailResultData obj = null;
        int size = aList.size();
        for (int i = 0; i < size; i++) {
          obj = getResultData(aList.get(i));
          if (obj != null) {
            list.add(obj);
          }
        }
      }

      return (list != null);
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

  private List<EipTScheduleMap> selectList(RunData rundata)
      throws ALPageNotFoundException, ALDBErrorException {
    try {
      SelectQuery<EipTScheduleMap> query = getSelectQuery(rundata);
      if (query == null) {
        return null;
      }
      List<EipTScheduleMap> list = query.fetchList();

      return ScheduleUtils.sortByDummySchedule(list);
    } catch (Exception e) {
      logger.error("[CellAppScheduleSelectData]", e);
      throw new ALDBErrorException();
    }

  }

  private SelectQuery<EipTScheduleMap> getSelectQuery(RunData rundata) {
    SelectQuery<EipTScheduleMap> query = Database.query(EipTScheduleMap.class);

    // 期間内のDayContainerの作成
    allContainer = getDayContainers(startDate.getValue(), endDate.getValue());
    // 終了日時
    Expression exp11 =
      ExpressionFactory.greaterOrEqualExp(
        EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
          + "."
          + EipTSchedule.END_DATE_PROPERTY,
        startDate.getValue());
    // 開始日時
    Expression exp12 =
      ExpressionFactory.lessOrEqualExp(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
        + "."
        + EipTSchedule.START_DATE_PROPERTY, endDate.getValue());
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

    query.setQualifier((exp11.andExp(exp12)).orExp(exp13.andExp(exp14)));

    if ((target_user_id != null) && (!target_user_id.equals(""))) {
      if (target_user_id.startsWith(ScheduleUtils.TARGET_FACILITY_ID)) {
        String fid =
          target_user_id.substring(
            ScheduleUtils.TARGET_FACILITY_ID.length(),
            target_user_id.length());
        // 指定ユーザをセットする．
        Expression exp1 =
          ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, fid);
        query.andQualifier(exp1);
        // 設備のスケジュール
        Expression exp2 =
          ExpressionFactory.matchExp(
            EipTScheduleMap.TYPE_PROPERTY,
            ScheduleUtils.SCHEDULEMAP_TYPE_FACILITY);
        query.andQualifier(exp2);
      } else {
        // 指定ユーザをセットする．
        Expression exp3 =
          ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, Integer
            .valueOf(target_user_id));
        query.andQualifier(exp3);
        // ユーザのスケジュール
        Expression exp4 =
          ExpressionFactory.matchExp(
            EipTScheduleMap.TYPE_PROPERTY,
            ScheduleUtils.SCHEDULEMAP_TYPE_USER);
        query.andQualifier(exp4);
      }
    } else {
      // 表示できるユーザがいない場合の処理
      return null;
    }

    // 開始日時でソート
    query.orderAscending(EipTScheduleMap.EIP_TSCHEDULE_PROPERTY
      + "."
      + EipTSchedule.START_DATE_PROPERTY);
    return query;
  }

  private ScheduleDetailResultData getResultData(Object obj)
      throws ALPageNotFoundException, ALDBErrorException {

    ScheduleDetailResultData rd = new ScheduleDetailResultData();
    rd.initField();
    try {
      EipTScheduleMap record = (EipTScheduleMap) obj;
      EipTSchedule schedule = record.getEipTSchedule();
      // スケジュールが棄却されている場合は表示しない
      if ("R".equals(record.getStatus())) {
        return rd;
      }
      int userid_int = Integer.parseInt(target_user_id);

      SelectQuery<EipTScheduleMap> mapquery =
        Database.query(EipTScheduleMap.class);
      Expression mapexp1 =
        ExpressionFactory.matchExp(
          EipTScheduleMap.SCHEDULE_ID_PROPERTY,
          schedule.getScheduleId());
      mapquery.setQualifier(mapexp1);
      Expression mapexp2 =
        ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, Integer
          .valueOf(target_user_id));
      mapquery.andQualifier(mapexp2);

      List<EipTScheduleMap> schedulemaps = mapquery.fetchList();
      boolean is_member =
        (schedulemaps != null && schedulemaps.size() > 0) ? true : false;

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

      //
      rd.setNote(schedule.getNote());
      rd.setPlace(schedule.getPlace());
      addResultData(rd);

    } catch (Exception e) {
      logger.error("Exception", e);

      return null;
    }
    return rd;

  }

  private List<ScheduleDayContainer> getDayContainers(Date start, Date end) {

    List<ScheduleDayContainer> list = new ArrayList<ScheduleDayContainer>();
    Calendar cals = Calendar.getInstance();
    Calendar cale = (Calendar) cals.clone();
    cals.setTime(start);
    cale.setTime(end);

    while (cals.before(cale)
      || cals.getTimeInMillis() == cale.getTimeInMillis()) {
      ScheduleDayContainer con = new ScheduleDayContainer();
      con.initField();
      con.setDate(cals.getTime());
      list.add(con);
      cals.add(Calendar.DATE, 1);
    }
    return list;
  }

  private void addResultData(ScheduleResultData rd) {
    int size = allContainer.size();

    // 期間用
    ALDateTimeField startDate = rd.getStartDate();
    ALDateTimeField endDate = rd.getEndDate();
    Calendar cals = Calendar.getInstance();
    Calendar cale = (Calendar) cals.clone();
    Calendar field_cal = (Calendar) cals.clone();
    cals.set(Integer.parseInt(startDate.getYear()), Integer.parseInt(startDate
      .getMonth()), Integer.parseInt(startDate.getDay()), 0, 0, 0);
    cale.set(Integer.parseInt(endDate.getYear()), Integer.parseInt(endDate
      .getMonth()), Integer.parseInt(endDate.getDay()), 0, 0, 0);

    for (int i = 0; i < size; i++) {
      ScheduleDayContainer con = allContainer.get(i);
      ALDateTimeField field = con.getDate();

      if (rd.getPattern().equals("S")) {
        if (cals.before(cale)) {
          // 期間スケジュールの期間
          field_cal.set(
            Integer.parseInt(field.getYear()),
            Integer.parseInt(field.getMonth()),
            Integer.parseInt(field.getDay()),
            0,
            0,
            0);
          if (cals.getTimeInMillis() <= field_cal.getTimeInMillis()
            && cale.getTimeInMillis() >= field_cal.getTimeInMillis()) {
            ScheduleDetailResultData rd3 = new ScheduleDetailResultData();
            rd3.initField();
            rd3.setScheduleId((int) rd.getScheduleId().getValue());
            rd3.setParentId((int) rd.getParentId().getValue());
            rd3.setName(rd.getName().getValue());
            rd3.setStartDate(field_cal.getTime());
            rd3.setEndDate(field_cal.getTime());
            rd3.setTmpreserve(rd.isTmpreserve());
            rd3.setPublic(rd.isPublic());
            rd3.setHidden(rd.isHidden());
            rd3.setDummy(rd.isDummy());
            rd3.setLoginuser(rd.isLoginuser());
            rd3.setOwner(rd.isOwner());
            rd3.setMember(rd.isMember());
            rd3.setType(rd.getType());
            rd3.setNote(((ScheduleDetailResultData) rd).getNoteStr());
            rd3.setPlace(((ScheduleDetailResultData) rd).getPlace().getValue());
            con.addResultData(rd3);
          }
        } else {
          // 期間スケジュール一日
          if (field.getYear().equals(rd.getStartDate().getYear())
            && field.getMonth().equals(rd.getStartDate().getMonth())
            && field.getDay().equals(rd.getStartDate().getDay())) {
            con.addResultData(rd);
            break;
          }
        }

        // 通常スケジュール
      } else if (!rd.getPattern().equals("N")) {
        // 繰り返しスケジュール
        if (ScheduleUtils.isView(con.getDate(), rd.getPattern(), rd
          .getStartDate()
          .getValue(), rd.getEndDate().getValue())) {
          Calendar temp = Calendar.getInstance();
          temp.setTime(field.getValue());
          temp
            .set(Calendar.HOUR, Integer.parseInt(rd.getStartDate().getHour()));
          temp.set(Calendar.MINUTE, Integer.parseInt(rd
            .getStartDate()
            .getMinute()));
          temp.set(Calendar.SECOND, 0);
          temp.set(Calendar.MILLISECOND, 0);
          Calendar temp2 = Calendar.getInstance();
          temp2.setTime(field.getValue());
          temp2.set(Calendar.HOUR, Integer.parseInt(rd.getEndDate().getHour()));
          temp2.set(Calendar.MINUTE, Integer.parseInt(rd
            .getEndDate()
            .getMinute()));
          temp2.set(Calendar.SECOND, 0);
          temp2.set(Calendar.MILLISECOND, 0);
          ScheduleDetailResultData rd3 = new ScheduleDetailResultData();
          rd3.initField();
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
          rd3.setType(rd.getType());
          // 繰り返しはON
          rd3.setRepeat(true);

          //
          rd3.setNote(((ScheduleDetailResultData) rd).getNoteStr());
          rd3.setPlace(((ScheduleDetailResultData) rd).getPlace().getValue());

          con.addResultData(rd3);
        }
      } else if (field.getYear().equals(rd.getStartDate().getYear())
        && field.getMonth().equals(rd.getStartDate().getMonth())
        && field.getDay().equals(rd.getStartDate().getDay())) {
        con.addResultData(rd);
        break;
      }
    }

  }

  private boolean doCheckAclPermission(RunData rundata, int defineAclType)
      throws ALPermissionException {
    if (defineAclType == 0) {
      return true;
    }

    String pfeature = getAclPortletFeature();
    if (pfeature == null || "".equals(pfeature)) {
      return true;
    }

    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

    boolean hasAuthority =
      aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        pfeature,
        defineAclType);

    if (!hasAuthority) {
      throw new ALPermissionException();
    }

    return true;
  }

  public void doFormatCsv() {
    int size = allContainer.size();
    for (int i = 0; i < size; i++) {
      ScheduleDayContainer con = allContainer.get(i);
      List<ScheduleResultData> alist = con.getScheduleList();
      int aSize = alist.size();
      for (int j = 0; j < aSize; j++) {
        ScheduleDetailResultData rd = (ScheduleDetailResultData) alist.get(j);
        CellAppScheduleBean bean = new CellAppScheduleBean();
        bean.initField();
        bean.setResultData(rd);
        csvlist.add(bean);
      }
    }
  }

  public String outPutCsv() {
    String LINE_SEPARATOR = System.getProperty("line.separator");
    StringBuffer sb = new StringBuffer();
    String delim = ",";
    List<CellAppScheduleBean> list = csvlist;
    int size = list.size();
    for (int i = 0; i < size; i++) {
      CellAppScheduleBean bean = list.get(i);
      String title = bean.getTitle();
      if (!title.equals("\"dummy\"")) {
        sb.append(bean.getStart_date()).append(delim);
        sb.append(bean.getEnd_date()).append(delim);
        sb.append(TYPE_AIPO).append(delim);
        sb.append(title).append(delim);
        sb.append(bean.getPlace()).append(delim);
        sb.append(bean.getText()).append(LINE_SEPARATOR);
      }
    }
    return sb.toString();
  }

  public List<String> getMsgList() {
    return msgList;
  }

  public String getAclPortletFeature() {
    return aclPortletFeature;
  }

}
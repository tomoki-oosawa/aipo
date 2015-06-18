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
package com.aimluck.eip.schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.cayenne.om.portlet.VEipTScheduleList;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 *
 */
public class ScheduleListSelectData extends ScheduleMonthlySelectData {

  /** <code>logger</code> logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleListSelectData.class.getName());

  /** <code>prevDate</code> 前の日 */
  private ALDateTimeField prevDate;

  /** <code>nextDate</code> 次の日 */
  private ALDateTimeField nextDate;

  /** <code>prevWeek</code> 前の週 */
  private ALDateTimeField prevWeek;

  /** <code>nextWeek</code> 次の週 */
  private ALDateTimeField nextWeek;

  /** <code>viewStart</code> 表示開始日時 */
  private ALDateTimeField viewStart;

  /** <code>viewEnd</code> 表示終了日時 */
  private ALDateTimeField viewEnd;

  /** 閲覧権限の有無 */
  private boolean hasAclviewOther;

  protected String listViewtype;

  private ScheduleListContainer con;

  private int userid;

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

    super.init(action, rundata, context);// 表示タイプの設定

    listViewtype = "list";
    // POST/GET から yyyy-MM-dd の形式で受け渡される。
    // 前の日
    prevDate = new ALDateTimeField("yyyy-MM-dd");
    // 次の日
    nextDate = new ALDateTimeField("yyyy-MM-dd");
    // 前の週
    prevWeek = new ALDateTimeField("yyyy-MM-dd");
    // 次の週
    nextWeek = new ALDateTimeField("yyyy-MM-dd");
    // 表示開始日時
    viewStart = new ALDateTimeField("yyyy-MM-dd");
    viewStart.setNotNull(true);
    // 表示終了日時
    viewEnd = new ALDateTimeField("yyyy-MM-dd");

    // 自ポートレットからのリクエストであれば、パラメータを展開しセッションに保存する。
    if (ALEipUtils.isMatch(rundata, context)) {
      // スケジュールの表示開始日時
      // e.g. 2004-3-14
      if (rundata.getParameters().containsKey("view_start")) {
        ALEipUtils.setTemp(rundata, context, "view_start", rundata
          .getParameters()
          .getString("view_start"));
      }
    }

    // 表示開始日時
    String tmpViewStart = ALEipUtils.getTemp(rundata, context, "view_start");
    if (tmpViewStart == null || tmpViewStart.equals("")) {
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      viewStart.setValue(cal.getTime());
    } else {
      viewStart.setValue(tmpViewStart);
      if (!viewStart.validate(new ArrayList<String>())) {
        ALEipUtils.removeTemp(rundata, context, "view_start");
        throw new ALPageNotFoundException();
      }
    }
    // MonthlyCalendarに表示する月を登録
    this.setMonthlyCalendarViewMonth(viewStart.getYear(), viewStart.getMonth());
    this.setMonthlyCalendar(rundata, context);

    Calendar cal2 = Calendar.getInstance();
    cal2.setTime(viewStart.getValue());
    cal2.add(Calendar.DATE, 1);
    nextDate.setValue(cal2.getTime());
    cal2.add(Calendar.DATE, 6);
    nextWeek.setValue(cal2.getTime());
    cal2.add(Calendar.DATE, -8);
    prevDate.setValue(cal2.getTime());
    cal2.add(Calendar.DATE, -6);
    prevWeek.setValue(cal2.getTime());
    cal2.add(Calendar.DATE, 7);

    // 表示終了日時
    cal2.add(Calendar.DATE, -1);
    viewEnd.setValue(cal2.getTime());

    ALEipUtils.setTemp(rundata, context, "tmpStart", viewStart.toString()
      + "-00-00");
    ALEipUtils.setTemp(rundata, context, "tmpEnd", viewStart.toString()
      + "-00-00");

    Calendar cal4 = Calendar.getInstance();
    cal4.setTime(viewStart.getValue());
    cal4.add(Calendar.DATE, 7);
    viewEnd.setValue(cal4.getTime());

    userid = ALEipUtils.getUserId(rundata);

    con = new ScheduleListContainer();
    con.initField();
    Calendar cal5 = Calendar.getInstance();
    cal5.setTime(viewStart.getValue());
    con.setViewStartDate(cal5);

    int loginUserId = ALEipUtils.getUserId(rundata);
    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();
    hasAclviewOther =
      aclhandler.hasAuthority(
        loginUserId,
        ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_OTHER,
        ALAccessControlConstants.VALUE_ACL_LIST);
  }

  @Override
  protected ResultList<VEipTScheduleList> selectList(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    try {
      setupLists(rundata, context);

      List<VEipTScheduleList> resultBaseList =
        getScheduleList(rundata, context);
      List<VEipTScheduleList> resultList =
        ScheduleUtils.sortByDummySchedule(resultBaseList);

      return new ResultList<VEipTScheduleList>(resultList);
    } catch (Exception e) {
      logger.error("[ScheduleListSelectData]", e);
      throw new ALDBErrorException();
    }
  }

  protected List<VEipTScheduleList> getScheduleList(RunData rundata,
      Context context) {

    Integer targetId = null;
    boolean isFacility = false;
    if ((target_user_id != null) && (!target_user_id.equals(""))) {
      if (!hasAclviewOther
        && !target_user_id.startsWith(ScheduleUtils.TARGET_FACILITY_ID)) {
        targetId = userid;
      } else {
        if (target_user_id.startsWith(ScheduleUtils.TARGET_FACILITY_ID)) {
          String fid =
            target_user_id.substring(
              ScheduleUtils.TARGET_FACILITY_ID.length(),
              target_user_id.length());
          targetId = Integer.valueOf(fid);
          isFacility = true;
        } else {
          targetId = Integer.valueOf(target_user_id);
        }
      }
    } else {
      // 表示できるユーザがいない場合の処理
      return new ArrayList<VEipTScheduleList>();
    }

    return ScheduleUtils.getScheduleList(Integer.valueOf(userid), viewStart
      .getValue(), viewEnd.getValue(), isFacility ? null : Arrays
      .asList(targetId), isFacility ? Arrays.asList(targetId) : null);
  }

  /**
   *
   * @param record
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultData(VEipTScheduleList record)
      throws ALPageNotFoundException, ALDBErrorException {
    ScheduleSearchResultData rd = new ScheduleSearchResultData();
    rd.initField();
    try {
      // スケジュールが棄却されている場合は表示しない
      if ("R".equals(record.getStatus())) {
        return rd;
      }

      boolean is_member = record.isMember();

      // Dummy スケジュールではない
      // 完全に隠す
      // 自ユーザー以外
      // 共有メンバーではない
      // オーナーではない
      if ((!"D".equals(record.getStatus()))
        && "P".equals(record.getPublicFlag())
        && (userid != record.getUserId().intValue())
        && (userid != record.getOwnerId().intValue())
        && !is_member) {
        return null;
      }
      if ("C".equals(record.getPublicFlag())
        && (userid != record.getUserId().intValue())
        && (userid != record.getOwnerId().intValue())
        && !is_member) {
        rd.setName(ALLocalizationUtils.getl10n("SCHEDULE_CLOSE_PUBLIC_WORD"));
        // 仮スケジュールかどうか
        rd.setTmpreserve(false);
      } else {
        rd.setName(record.getName());
        // 仮スケジュールかどうか
        rd.setTmpreserve("T".equals(record.getStatus()));
      }

      if (!hasAclviewOther && !is_member) {// 閲覧権限がなく、グループでもない
        return rd;
      }
      // ID
      rd.setScheduleId(record.getScheduleId().intValue());
      // 親スケジュール ID
      rd.setParentId(record.getParentId().intValue());
      // 開始日時
      rd.setStartDate(record.getStartDate());
      // 終了日時
      rd.setEndDate(record.getEndDate());
      // 公開するかどうか
      rd.setPublic("O".equals(record.getPublicFlag()));
      // 非表示にするかどうか
      rd.setHidden("P".equals(record.getPublicFlag()));
      // ダミーか
      rd.setDummy("D".equals(record.getStatus()));
      // ログインユーザかどうか
      rd.setLoginuser(record.getUserId().intValue() == userid);
      // オーナーかどうか
      rd.setOwner(record.getOwnerId().intValue() == userid);
      // 共有メンバーかどうか
      rd.setMember(is_member);
      // 繰り返しパターン
      rd.setPattern(record.getRepeatPattern());

      rd.setCreateUser(ALEipUtils.getALEipUser(record.getCreateUserId()));

      if (!rd.getPattern().equals("N")) {
        rd.setRepeat(true);
      }
      con.addResultData(rd);

    } catch (Exception e) {
      logger.error("[ScheduleListSelectData]", e);
      return null;
    }
    return rd;
  }

  /**
   * 表示開始日時を取得します。
   *
   * @return
   */
  @Override
  public ALDateTimeField getViewStart() {
    return viewStart;
  }

  /**
   * 表示終了日時を取得します。
   *
   * @return
   */
  @Override
  public ALDateTimeField getViewEnd() {
    return viewEnd;
  }

  /**
   * 表示タイプを取得します。
   *
   * @return
   */
  @Override
  public String getViewtype() {
    return listViewtype;
  }

  /**
   * 前の日を取得します。
   *
   * @return
   */
  public ALDateTimeField getPrevDate() {
    return prevDate;
  }

  /**
   * 前の週を取得します。
   *
   * @return
   */
  public ALDateTimeField getPrevWeek() {
    return prevWeek;
  }

  /**
   * 次の日を取得します。
   *
   * @return
   */
  public ALDateTimeField getNextDate() {
    return nextDate;
  }

  /**
   * 次の週を取得します。
   *
   * @return
   */
  public ALDateTimeField getNextWeek() {
    return nextWeek;
  }

  public List<ScheduleResultData> getScheduleList() {
    return con.getScheduleList();
  }

  public String getViewStartFormat() {
    return ALLocalizationUtils.getl10nFormat(
      "SCHEDULE_DATE_FORMAT_NOSPACE",
      getViewStart().getYear(),
      getViewStart().getMonth(),
      getViewStart().getDay());
  }

  public String getViewEndFormat() {
    return ALLocalizationUtils.getl10nFormat(
      "SCHEDULE_DATE_FORMAT_NOSPACE",
      getViewEnd().getYear(),
      getViewEnd().getMonth(),
      getViewEnd().getDay());
  }

  public boolean isLoginUserID(Long id) {
    return id == userid;
  }

}

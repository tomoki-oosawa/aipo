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
package com.aimluck.eip.modules.actions.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.cayenne.om.portlet.VEipTScheduleList;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.schedule.CellScheduleFormDateData;
import com.aimluck.eip.schedule.CellScheduleFormFacilityData;
import com.aimluck.eip.schedule.CellScheduleFormGroupData;
import com.aimluck.eip.schedule.CellScheduleFormGroupForSelectData;
import com.aimluck.eip.schedule.CellScheduleFormMemberData;
import com.aimluck.eip.schedule.CellScheduleFormMemberForSelectData;
import com.aimluck.eip.schedule.CellScheduleFormNoteData;
import com.aimluck.eip.schedule.CellScheduleOnedaySelectByMemberData;
import com.aimluck.eip.schedule.CellScheduleOnedaySelectData;
import com.aimluck.eip.schedule.CellScheduleSelectByMemberData;
import com.aimluck.eip.schedule.CellScheduleSelectData;
import com.aimluck.eip.schedule.CellScheduleSelectFormByMemberData;
import com.aimluck.eip.schedule.CellScheduleSelectFormData;
import com.aimluck.eip.schedule.CellScheduleWeekSelectByMemberData;
import com.aimluck.eip.schedule.CellScheduleWeekSelectData;
import com.aimluck.eip.schedule.ScheduleChangeStatusFormData;
import com.aimluck.eip.schedule.ScheduleOnedaySelectData;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * スケジュールのアクションクラスです。
 *
 */
public class CellScheduleAction extends ALBaseAction {

  /** <code>logger</code> logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CellScheduleAction.class.getName());

  private final String[] weekday_str = {
    "",
    ALLocalizationUtils.getl10n("SCHEDULE_SUNDAY_CELL"),
    ALLocalizationUtils.getl10n("SCHEDULE_MONDAY_CELL"),
    ALLocalizationUtils.getl10n("SCHEDULE_TUSEDAY_CELL"),
    ALLocalizationUtils.getl10n("SCHEDULE_WEDNESDAY_CELL"),
    ALLocalizationUtils.getl10n("SCHEDULE_THURSDAY_CELL"),
    ALLocalizationUtils.getl10n("SCHEDULE_FRIDAY_CELL"),
    ALLocalizationUtils.getl10n("SCHEDULE_SATURDAY_CELL") };

  /**
   *
   * @param portlet
   * @param context
   * @param rundata
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) {
    ALEipUtils.removeTemp(rundata, context, "view_date_top");
  }

  /**
   *
   * @param portlet
   * @param context
   * @param rundata
   */
  @Override
  protected void buildMaximizedContext(VelocityPortlet portlet,
      Context context, RunData rundata) {

    // MODEを取得
    String mode = rundata.getParameters().getString(ALEipConstants.MODE);
    try {
      if (ALEipConstants.MODE_FORM.equals(mode)) {
        doSchedule_form(rundata, context);
      } else if (ALEipConstants.MODE_DETAIL.equals(mode)) {
        doSchedule_detail(rundata, context);
      } else if (ALEipConstants.MODE_LIST.equals(mode)) {
        // modify motegi start
        // doSchedule_list(rundata, context);
        String uid =
          ALEipUtils.getTemp(rundata, context, "target_otheruser_id");
        if (uid != null && !"".equals(uid)) {
          // 他メンバーのスケジュール⇒ToDoの後でスケジュールに戻った場合
          doSchedule_list_select_member(rundata, context);
          // ALEipUtils.removeTemp(rundata, context, "target_otheruser_id");
        } else {
          doSchedule_list(rundata, context);
        }
        // modify motegi end
      }
      if (getMode() == null) {
        // セッション削除
        if (ALEipUtils.getTemp(rundata, context, "view_date_top") != null
          && !ALEipUtils.getTemp(rundata, context, "view_date_top").equals("")) {
          ALEipUtils.removeTemp(rundata, context, "view_date_top");
        }
        doSchedule_menu(rundata, context);
      }
    } catch (Exception ex) {
      logger.error("[ScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * スケジュールを一覧表示します。
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_list(RunData rundata, Context context) {
    try {
      // ポートレット ID を取得する．
      String portletId = ((JetspeedRunData) rundata).getJs_peid();

      // 自ポートレットからのリクエストであれば、パラメータを展開しセッションに保存する。
      if (ALEipUtils.isMatch(rundata, context)) {
        // 現在選択されているタブ
        // oneday : １日表示
        // weekly : 週間表示
        // monthly: 月間表示
        if (rundata.getParameters().containsKey("tab")) {
          ALEipUtils.setTemp(rundata, context, "tab", rundata
            .getParameters()
            .getString("tab"));
        }
      }

      String currentTab;
      ALAbstractSelectData<VEipTScheduleList, VEipTScheduleList> listData;
      String tmpCurrentTab = ALEipUtils.getTemp(rundata, context, "tab");
      if (tmpCurrentTab == null
        || !(tmpCurrentTab.equals("oneday")
          || tmpCurrentTab.equals("weekly")
          || tmpCurrentTab.equals("monthly")
          || tmpCurrentTab.equals("oneday-group") || tmpCurrentTab
            .equals("weekly-group"))) {
        currentTab = "oneday";
      } else {
        currentTab = tmpCurrentTab;
      }

      currentTab = ScheduleUtils.getCurrentTab(rundata, context);

      if ("oneday".equals(currentTab)) {
        listData = new CellScheduleOnedaySelectData();
        ((ScheduleOnedaySelectData) listData).setPortletId(portletId);
        listData.initField();
        listData.doViewList(this, rundata, context);
        setTemplate(rundata, "schedule-list");
      }

    } catch (Exception ex) {
      logger.error("[CellScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  public void doSchedule_form_selectday(RunData rundata, Context context) {
    try {
      CellScheduleSelectFormData formData = new CellScheduleSelectFormData();
      // formData.loadParameters(rundata, context);
      formData.initField();
      formData.doViewForm(this, rundata, context);

      setTemplate(rundata, "schedule-form-selectday");
    } catch (Exception ex) {
      logger.error("[CellScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * 日付指定用のフォームを表示する．
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_show_selectday(RunData rundata, Context context) {
    try {
      CellScheduleSelectFormData formData = new CellScheduleSelectFormData();
      formData.initField();
      setTemplate(rundata, "schedule-form-selectday");
      if (formData.doCheck(this, rundata, context)) {
        String viewdate = formData.getViewDateStr();
        JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
        rundata.setRedirectURI(jsLink.getPortletById(
          ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
          "eventSubmit_doSchedule_menu",
          "1").addQueryData("start_date", viewdate).toString());
        rundata.getResponse().sendRedirect(rundata.getRedirectURI());
        jsLink = null;
      }
    } catch (Exception ex) {
      logger.error("[CellScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   *
   * @param rundata
   * @param context
   * @param is_repeat
   * @param is_span
   * @param enable_entityid
   *          データベースから情報を取得する場合は，true．
   */
  public void doSchedule_form(RunData rundata, Context context) {
    try {
      CellScheduleFormDateData formData = new CellScheduleFormDateData();
      ALEipUtils.setTemp(rundata, context, "is_copy", "false");
      formData.loadParameters(rundata, context);
      formData.initField();
      formData.doViewForm(this, rundata, context);
      setTemplate(rundata, "schedule-form-date");
    } catch (Exception ex) {
      logger.error("[CellScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * スケジュールコピーのフォームを表示する．
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_form_copy(RunData rundata, Context context) {
    try {
      CellScheduleFormDateData formData = new CellScheduleFormDateData();
      ALEipUtils.setTemp(rundata, context, "is_copy", "true");
      formData.loadParameters(rundata, context);
      formData.initField();
      formData.doViewForm(this, rundata, context);
      setTemplate(rundata, "schedule-form-date");
    } catch (Exception ex) {
      logger.error("[CellScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * スケジュール登録のフォームを表示する．
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_form_group(RunData rundata, Context context) {
    try {
      CellScheduleFormGroupData formGroupData = new CellScheduleFormGroupData();
      formGroupData.loadParameters(rundata, context);
      formGroupData.initField();
      if (formGroupData.doViewForm(this, rundata, context)) {
        setTemplate(rundata, "schedule-form-group");
      } else {
        setTemplate(rundata, "schedule-form-date");
      }

    } catch (Exception ex) {
      logger.error("[CellScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * スケジュール登録のフォームを表示する．
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_form_member(RunData rundata, Context context) {
    try {
      CellScheduleFormMemberData formGroupData =
        new CellScheduleFormMemberData();
      formGroupData.loadParameters(rundata, context);
      formGroupData.initField();
      if (formGroupData.doViewForm(this, rundata, context)) {
        setTemplate(rundata, "schedule-form-member");
      } else {
        setTemplate(rundata, "schedule-form-group");
      }

    } catch (Exception ex) {
      logger.error("[CellScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * スケジュール登録のフォームを表示する．
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_form_member_prev(RunData rundata, Context context) {
    String prev = rundata.getParameters().getString("prev");
    rundata.getParameters().setString("start", prev);

    doSchedule_form_member(rundata, context);
  }

  /**
   * スケジュール登録のフォームを表示する．
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_form_member_next(RunData rundata, Context context) {
    String next = rundata.getParameters().getString("next");
    rundata.getParameters().setString("start", next);

    doSchedule_form_member(rundata, context);
  }

  /**
   * スケジュール登録のフォームを表示する．
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_form_facility_group(RunData rundata, Context context) {
    try {
      CellScheduleFormGroupData formGroupData = new CellScheduleFormGroupData();
      formGroupData.loadParameters(rundata, context);
      formGroupData.initField();
      if (formGroupData.doViewForm(this, rundata, context)) {
        setTemplate(rundata, "schedule-form-facility-group");
      } else {
        setTemplate(rundata, "schedule-form-date");
      }

    } catch (Exception ex) {
      logger.error("[CellScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * スケジュール登録のフォームを表示する．
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_form_facility(RunData rundata, Context context) {
    try {
      CellScheduleFormFacilityData formFacilityData =
        new CellScheduleFormFacilityData();
      formFacilityData.loadParameters(rundata, context);
      formFacilityData.initField();
      if (formFacilityData.doViewForm(this, rundata, context)) {
        setTemplate(rundata, "schedule-form-facility");
      } else {
        setTemplate(rundata, "schedule-form-facility-group");
      }

    } catch (Exception ex) {
      logger.error("[CellScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * スケジュール登録のフォームを表示する．
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_form_note(RunData rundata, Context context) {
    try {
      CellScheduleFormNoteData formNoteData = new CellScheduleFormNoteData();
      formNoteData.loadParameters(rundata, context);
      formNoteData.initField();
      if (formNoteData.doViewForm(this, rundata, context)) {
        setTemplate(rundata, "schedule-form-note");
      } else {
        setTemplate(rundata, "schedule-form-date");
      }
    } catch (Exception ex) {
      logger.error("[CellScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * スケジュールを登録します。
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_insert(RunData rundata, Context context) {
    try {
      CellScheduleFormNoteData formData = new CellScheduleFormNoteData();
      formData.initField();
      setTemplate(rundata, "schedule-form-note");
      if (formData.doInsert(this, rundata, context)) {
        JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
        rundata.setRedirectURI(jsLink.getPortletById(
          ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
          "eventSubmit_doSchedule_menu",
          "1").toString());
        rundata.getResponse().sendRedirect(rundata.getRedirectURI());
        jsLink = null;
      } else {
        setTemplate(rundata, "schedule-form-note");
      }
    } catch (Exception ex) {
      logger.error("[ScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * スケジュールを更新します。
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_update(RunData rundata, Context context) {
    try {
      CellScheduleFormNoteData formData = new CellScheduleFormNoteData();
      formData.initField();
      setTemplate(rundata, "schedule-form-date");
      if (formData.doUpdate(this, rundata, context)) {
        JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
        rundata.setRedirectURI(jsLink.getPortletById(
          ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
          "eventSubmit_doSchedule_menu",
          "1").toString());
        rundata.getResponse().sendRedirect(rundata.getRedirectURI());
        jsLink = null;
      } else {
        setTemplate(rundata, "schedule-form-note");
      }
    } catch (Exception ex) {
      logger.error("[ScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * スケジュール削除のフォームを表示する．
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_form_delete(RunData rundata, Context context) {
    try {
      CellScheduleSelectData detailData = new CellScheduleSelectData();
      detailData.init(this, rundata, context);
      detailData.initField();
      detailData.doViewDetail(this, rundata, context);
      setTemplate(rundata, "schedule-form-delete");
    } catch (Exception ex) {
      logger.error("[ScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * スケジュールを削除します。
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_delete(RunData rundata, Context context) {
    try {
      CellScheduleFormNoteData formData = new CellScheduleFormNoteData();
      formData.loadParameters(rundata, context);
      formData.initField();
      setTemplate(rundata, "schedule-menu");
      if (formData.doDelete(this, rundata, context)) {
        JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
        rundata.setRedirectURI(jsLink.getPortletById(
          ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
          "eventSubmit_doSchedule_menu",
          "1").toString());
        rundata.getResponse().sendRedirect(rundata.getRedirectURI());
        jsLink = null;
      }
    } catch (Exception ex) {
      logger.error("[ScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * スケジュールを詳細表示します。
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_detail(RunData rundata, Context context) {
    try {
      CellScheduleSelectData detailData = new CellScheduleSelectData();
      detailData.initField();
      detailData.doViewDetail(this, rundata, context);
      setTemplate(rundata, "schedule-detail");
    } catch (Exception ex) {
      logger.error("[ScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * スケジュールの状態を変更します。
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_change_status(RunData rundata, Context context) {
    try {
      ScheduleChangeStatusFormData formData =
        new ScheduleChangeStatusFormData();
      formData.loadParametersViewDate(rundata, context);
      formData.initField();
      if (formData.doUpdate(this, rundata, context)) {
        String viewDate = formData.getViewDate().toString();
        String entityid = rundata.getParameters().getString("entityid");
        setTemplate(rundata, "schedule-detail");
        JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
        if (viewDate == null || viewDate.equals("")) {
          logger
            .error("[ScheduleAction] ALPageNotFoundException: View Date is wrong.");
          throw new ALPageNotFoundException();
        }

        rundata.setRedirectURI(jsLink.getPortletById(
          ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
          "eventSubmit_doSchedule_detail",
          "1").addQueryData("view_date", viewDate).addQueryData(
          "entityid",
          entityid).toString());
        rundata.getResponse().sendRedirect(rundata.getRedirectURI());
        jsLink = null;
      }
    } catch (Exception ex) {
      logger.error("[ScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * スケジュールのメニュー(週間スケジュール)を表示する．
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_menu(RunData rundata, Context context) {

    // add by motegi start
    clearScheduleSessionForSelectMember(rundata, context);
    // add by motegi end

    Calendar cal = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();
    List<String> weekly = new ArrayList<String>();
    List<String> weekday = new ArrayList<String>();

    ALDateTimeField date = new ALDateTimeField("yyyy-MM-dd");

    if (rundata.getParameters().getString("start_date") != null) {
      String str;
      str = rundata.getParameters().getString("start_date");
      date.setValue(str);
      cal.setTime(date.getValue());
      cal2.setTime(date.getValue());
    } else if (ALEipUtils.getTemp(rundata, context, "view_date_top") != null
      && !ALEipUtils.getTemp(rundata, context, "view_date_top").equals("")) {
      String str;
      str = ALEipUtils.getTemp(rundata, context, "view_date_top");
      date.setValue(str);
      cal.setTime(date.getValue());
      cal2.setTime(date.getValue());

      rundata.getParameters().setString("start_date", str);
    }

    for (int k = 0; k < 7; k++) {
      StringBuffer day = new StringBuffer();
      day
        .append(cal.get(Calendar.YEAR))
        .append("-")
        .append(cal.get(Calendar.MONDAY) + 1)
        .append("-")
        .append(cal.get(Calendar.DATE));
      weekly.add(day.toString());
      weekday.add(weekday_str[cal.get(Calendar.DAY_OF_WEEK)]);

      cal.add(Calendar.DAY_OF_MONTH, 1);
    }
    cal2.add(Calendar.DAY_OF_MONTH, -7);
    ALEipUtils.setTemp(rundata, context, "view_date_top", weekly
      .get(0)
      .toString());
    context.put("daylist", weekly);
    context.put("weekdaylist", weekday);
    date.setValue(cal.getTime());
    context.put("nextweek", date.toString());
    date.setValue(cal2.getTime());
    context.put("prevweek", date.toString());

    CellScheduleWeekSelectData selectdata = new CellScheduleWeekSelectData();
    selectdata.initField();
    selectdata.doViewList(this, rundata, context);
    context.put("now", selectdata.getNow());

    setTemplate(rundata, "schedule-menu");
  }

  // private void clearScheduleSession(RunData rundata, Context context) {
  // List list = new ArrayList();
  // list.add("entityid");
  // list.add("target_user_id");
  // ALEipUtils.removeTemp(rundata, context, list);
  // }

  // ////////////////////////add by motegi start

  private void clearScheduleSessionForSelectMember(RunData rundata,
      Context context) {
    ALEipUtils.removeTemp(rundata, context, "target_otheruser_id");
    ALEipUtils.removeTemp(rundata, context, "target_othergroup_name");
  }

  /**
   * スケジュールのメニュー(週間スケジュール)を表示する．
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_form_selectgroup(RunData rundata, Context context) {
    try {
      CellScheduleFormGroupForSelectData formGroupData =
        new CellScheduleFormGroupForSelectData();
      formGroupData.loadParameters(rundata, context);
      formGroupData.initField();
      formGroupData.doViewForm(this, rundata, context);
      setTemplate(rundata, "schedule-form-select-group");
    } catch (Exception ex) {
      logger.error("[CellScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * スケジュール登録のフォームを表示する．
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_form_selectmember(RunData rundata, Context context) {
    try {
      CellScheduleFormMemberForSelectData formGroupData =
        new CellScheduleFormMemberForSelectData();
      formGroupData.loadParameters(rundata, context);
      formGroupData.initField();
      formGroupData.doViewForm(this, rundata, context);
      setTemplate(rundata, "schedule-form-select-member");
    } catch (Exception ex) {
      logger.error("[CellScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * スケジュールのメニュー(週間スケジュール)を表示する．
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_menu_select_member(RunData rundata, Context context) {
    Calendar cal = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();
    List<String> weekly = new ArrayList<String>();
    List<String> weekday = new ArrayList<String>();

    ALDateTimeField date = new ALDateTimeField("yyyy-MM-dd");

    if (rundata.getParameters().getString("start_date") != null) {
      String str;
      str = rundata.getParameters().getString("start_date");
      date.setValue(str);
      cal.setTime(date.getValue());
      cal2.setTime(date.getValue());
    } else if (ALEipUtils.getTemp(rundata, context, "view_date_top") != null
      && !ALEipUtils.getTemp(rundata, context, "view_date_top").equals("")) {
      String str;
      str = ALEipUtils.getTemp(rundata, context, "view_date_top");
      date.setValue(str);
      cal.setTime(date.getValue());
      cal2.setTime(date.getValue());

      rundata.getParameters().setString("start_date", str);
    }

    for (int k = 0; k < 7; k++) {
      StringBuffer day = new StringBuffer();
      day
        .append(cal.get(Calendar.YEAR))
        .append("-")
        .append(cal.get(Calendar.MONDAY) + 1)
        .append("-")
        .append(cal.get(Calendar.DATE));
      weekly.add(day.toString());
      weekday.add(weekday_str[cal.get(Calendar.DAY_OF_WEEK)]);

      cal.add(Calendar.DAY_OF_MONTH, 1);
    }
    cal2.add(Calendar.DAY_OF_MONTH, -7);
    ALEipUtils.setTemp(rundata, context, "view_date_top", weekly
      .get(0)
      .toString());
    context.put("daylist", weekly);
    context.put("weekdaylist", weekday);
    date.setValue(cal.getTime());
    context.put("nextweek", date.toString());
    date.setValue(cal2.getTime());
    context.put("prevweek", date.toString());

    CellScheduleWeekSelectByMemberData selectdata =
      new CellScheduleWeekSelectByMemberData();
    selectdata.initField();
    selectdata.doViewList(this, rundata, context);
    context.put("now", selectdata.getNow());
    // 対象ユーザーをセッションに設定
    ALEipUtils.setTemp(rundata, context, "target_otheruser_id", selectdata
      .getTargerUser()
      .getUserId()
      .getValueAsString());
    setTemplate(rundata, "schedule-menu-select-member");
  }

  /**
   * スケジュールを詳細表示します。
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_detail_select_member(RunData rundata, Context context) {
    try {
      CellScheduleSelectByMemberData detailData =
        new CellScheduleSelectByMemberData();
      detailData.initField();
      detailData.doViewDetail(this, rundata, context);
      // setMode(ALEipConstants.MODE_LIST);
      setTemplate(rundata, "schedule-detail-select-member");
    } catch (Exception ex) {
      logger.error("[ScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  public void doSchedule_form_selectday_select_member(RunData rundata,
      Context context) {
    try {
      CellScheduleSelectFormByMemberData formData =
        new CellScheduleSelectFormByMemberData();
      formData.initField();
      formData.doViewForm(this, rundata, context);

      setTemplate(rundata, "schedule-form-selectday-select-member");
    } catch (Exception ex) {
      logger.error("[CellScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * 日付指定用のフォームを表示する．
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_show_selectday_select_member(RunData rundata,
      Context context) {
    try {
      CellScheduleSelectFormByMemberData formData =
        new CellScheduleSelectFormByMemberData();
      formData.initField();
      setTemplate(rundata, "schedule-form-selectday-select-member");
      if (formData.doCheck(this, rundata, context)) {
        String viewdate = formData.getViewDateStr();
        JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
        rundata.setRedirectURI(jsLink
          .getPortletById(ALEipUtils.getPortlet(rundata, context).getID())
          .addQueryData("eventSubmit_doSchedule_menu_select_member", "1")
          .addQueryData(
            "selectedmember",
            formData.getTargerUser().getUserId().getValueAsString())
          .addQueryData("start_date", viewdate)
          .toString());
        rundata.getResponse().sendRedirect(rundata.getRedirectURI());
        jsLink = null;
      }
    } catch (Exception ex) {
      logger.error("[CellScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * スケジュールを一覧表示します。
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_list_select_member(RunData rundata, Context context) {
    try {
      // ポートレット ID を取得する．
      String portletId = ((JetspeedRunData) rundata).getJs_peid();

      // 自ポートレットからのリクエストであれば、パラメータを展開しセッションに保存する。
      if (ALEipUtils.isMatch(rundata, context)) {
        // 現在選択されているタブ
        // oneday : １日表示
        // weekly : 週間表示
        // monthly: 月間表示
        if (rundata.getParameters().containsKey("tab")) {
          ALEipUtils.setTemp(rundata, context, "tab", rundata
            .getParameters()
            .getString("tab"));
        }
      }

      String currentTab;
      ALAbstractSelectData<VEipTScheduleList, VEipTScheduleList> listData;
      String tmpCurrentTab = ALEipUtils.getTemp(rundata, context, "tab");
      if (tmpCurrentTab == null
        || !(tmpCurrentTab.equals("oneday")
          || tmpCurrentTab.equals("weekly")
          || tmpCurrentTab.equals("monthly")
          || tmpCurrentTab.equals("oneday-group") || tmpCurrentTab
            .equals("weekly-group"))) {
        currentTab = "oneday";
      } else {
        currentTab = tmpCurrentTab;
      }

      currentTab = ScheduleUtils.getCurrentTab(rundata, context);

      if (currentTab.equals("oneday")) {
        listData = new CellScheduleOnedaySelectByMemberData();
        ((CellScheduleOnedaySelectByMemberData) listData)
          .setPortletId(portletId);
        listData.initField();
        listData.doViewList(this, rundata, context);
        setTemplate(rundata, "schedule-list-select-member");
      }

    } catch (Exception ex) {
      logger.error("[CellScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * スケジュール削除のフォームを表示する．
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_form_delete_select_member(RunData rundata,
      Context context) {
    try {
      CellScheduleSelectByMemberData detailData =
        new CellScheduleSelectByMemberData();
      detailData.init(this, rundata, context);
      detailData.initField();
      detailData.doViewDetail(this, rundata, context);
      setTemplate(rundata, "schedule-form-delete-select-member");
    } catch (Exception ex) {
      logger.error("[ScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }
}

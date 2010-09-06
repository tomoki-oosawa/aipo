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
package com.aimluck.eip.modules.actions.schedule;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.schedule.ScheduleChangeStatusFormData;
import com.aimluck.eip.schedule.ScheduleFormData;
import com.aimluck.eip.schedule.ScheduleMonthlySelectData;
import com.aimluck.eip.schedule.ScheduleOnedayGroupSelectData;
import com.aimluck.eip.schedule.ScheduleOnedaySelectData;
import com.aimluck.eip.schedule.ScheduleSelectData;
import com.aimluck.eip.schedule.ScheduleWeeklyGroupSelectData;
import com.aimluck.eip.schedule.ScheduleWeeklySelectData;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * スケジュールのアクションクラスです。
 * 
 */
public class ScheduleAction extends ALBaseAction {

  /** <code>logger</code> logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleAction.class.getName());

  /** ノーマル画面からのスケジュール入力 */
  private static final String AFTER_BEHAVIOR = "afterbehavior";

  /**
   * 
   * @param portlet
   * @param context
   * @param rundata
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) {

    // セッション情報をクリアする
    clearScheduleSession(rundata, context);

    String tab;
    String portletId;
    ALAbstractSelectData<EipTScheduleMap, EipTScheduleMap> listData;

    try {
      // ポートレット ID を取得する．
      portletId = portlet.getID();

      // トップ画面からの操作後に，トップ画面に戻すかどうかを判定する．
      String afterBehavior =
        ALEipUtils
          .getPortlet(rundata, context)
          .getPortletConfig()
          .getInitParameter("p4b-behavior");
      if ("1".equals(afterBehavior)) {
        // ノーマル画面であることを指定．
        context.put(AFTER_BEHAVIOR, "1");
      }

      int tab_count = 0;

      // Velocity テンプレートを読み込む
      String template = "";
      String _template =
        portlet.getPortletConfig().getInitParameter("pba-template");
      boolean done = false;

      // 現在のユーザー名を取得する
      ALEipUser loginuser = ALEipUtils.getALEipUser(rundata);
      String current = loginuser.getAliasName().toString();
      context.put("current_user", current);
      context.put("current_user_ln", loginuser.getName());
      context.put("current_user_id", loginuser.getUserId());

      // アクセスコントロール
      String has_acl_self = ScheduleUtils.hasAuthSelf(rundata);
      String has_acl_other = ScheduleUtils.hasAuthOther(rundata);

      String tab_flg_oneday =
        ALEipUtils
          .getPortlet(rundata, context)
          .getPortletConfig()
          .getInitParameter("p6a-tab");
      if ("0".equals(tab_flg_oneday) && ("T".equals(has_acl_self))) {
        tab_count++;
        template = "schedule-oneday";
        if (template.equals(_template)) {
          done = true;
        }
      }
      String tab_flg_weekly =
        ALEipUtils
          .getPortlet(rundata, context)
          .getPortletConfig()
          .getInitParameter("p7a-tab");
      if ("0".equals(tab_flg_weekly) && ("T".equals(has_acl_self))) {
        tab_count++;
        if (("".equals(template)) || (!done)) {
          template = "schedule-weekly";
          if (template.equals(_template)) {
            done = true;
          }
        }
      }
      String tab_flg_monthly =
        ALEipUtils
          .getPortlet(rundata, context)
          .getPortletConfig()
          .getInitParameter("p8a-tab");
      if ("0".equals(tab_flg_monthly) && ("T".equals(has_acl_self))) {
        tab_count++;
        if (("".equals(template)) || (!done)) {
          template = "schedule-monthly";
          if (template.equals(_template)) {
            done = true;
          }
        }
      }
      String tab_flg_oneday_group =
        ALEipUtils
          .getPortlet(rundata, context)
          .getPortletConfig()
          .getInitParameter("p9a-tab");
      if ("0".equals(tab_flg_oneday_group) && ("T".equals(has_acl_other))) {
        tab_count++;
        if (("".equals(template)) || (!done)) {
          template = "schedule-oneday-group";
          if (template.equals(_template)) {
            done = true;
          }
        }
      }
      String tab_flg_weekly_group =
        ALEipUtils
          .getPortlet(rundata, context)
          .getPortletConfig()
          .getInitParameter("paa-tab");
      if ("0".equals(tab_flg_weekly_group) && ("T".equals(has_acl_other))) {
        tab_count++;
        if (("".equals(template)) || (!done)) {
          template = "schedule-weekly-group";
          if (template.equals(_template)) {
            done = true;
          }
        }
      }

      if ("".equals(template)) {
        template = _template;
      }

      if (template.equals("schedule-oneday")) {
        tab = "oneday";
        listData = new ScheduleOnedaySelectData();
        ((ScheduleOnedaySelectData) listData).setPortletId(portletId);
        // ブラウザ名を受け渡す．
        boolean isMsie = ScheduleUtils.isMsieBrowser(rundata);
        context.put("isMeie", Boolean.valueOf(isMsie));
        // setTemplate(rundata, "schedule-oneday");
        if ("T".equals(has_acl_self)) {
          if (tab_count == 0) {
            tab_flg_oneday = "0";
            tab_count++;
          }
        }
      } else if (template.equals("schedule-weekly")) {
        tab = "weekly";
        listData = new ScheduleWeeklySelectData();
        ((ScheduleWeeklySelectData) listData).setPortletId(portletId);
        // setTemplate(rundata, "schedule-weekly");
        if ("T".equals(has_acl_self)) {
          if (tab_count == 0) {
            tab_flg_weekly = "0";
            tab_count++;
          }
        }
      } else if (template.equals("schedule-monthly")) {
        tab = "monthly";
        listData = new ScheduleMonthlySelectData();
        ((ScheduleMonthlySelectData) listData).setPortletId(portletId);
        // setTemplate(rundata, "schedule-monthly");
        if ("T".equals(has_acl_self)) {
          if (tab_count == 0) {
            tab_flg_monthly = "0";
            tab_count++;
          }
        }
      } else if (template.equals("schedule-oneday-group")) {
        tab = "oneday-group";
        listData = new ScheduleOnedayGroupSelectData();
        ((ScheduleOnedayGroupSelectData) listData).setPortletId(portletId);
        // ブラウザ名を受け渡す．
        boolean isMsie = ScheduleUtils.isMsieBrowser(rundata);
        context.put("isMeie", Boolean.valueOf(isMsie));

        // setTemplate(rundata, "schedule-oneday-group");
        if ("T".equals(has_acl_other)) {
          if (tab_count == 0) {
            tab_flg_oneday_group = "0";
            tab_count++;
          }
        }
      } else {
        tab = "weekly-group";
        listData = new ScheduleWeeklyGroupSelectData();
        ((ScheduleWeeklyGroupSelectData) listData).setPortletId(portletId);
        // setTemplate(rundata, "schedule-weekly-group");
        if ("T".equals(has_acl_other)) {
          if (tab_count == 0) {
            tab_flg_weekly_group = "0";
            tab_count++;
          }
        }
      }

      if ("T".equals(has_acl_self)) {
        context.put("tab-oneday", tab_flg_oneday);
        context.put("tab-weekly", tab_flg_weekly);
        context.put("tab-monthly", tab_flg_monthly);
      }
      if ("T".equals(has_acl_other)) {
        context.put("tab-oneday-group", tab_flg_oneday_group);
        context.put("tab-weekly-group", tab_flg_weekly_group);
      }

      context.put("widthALL", Integer.toString(tab_count * 120 + 40) + "px");

      ALEipUtils.setTemp(rundata, context, "tab", tab);
      listData.initField();
      // 最低限表示するのに必要な権限のチェック
      if (!ScheduleUtils.hasMinimumAuthority(rundata)) {
        setTemplate(rundata, "schedule");
        context.put("hasMinimumAuthority", false);
      } else {
        if (listData.doViewList(this, rundata, context)) {
          setTemplate(rundata, "schedule");
          context.put("hasMinimumAuthority", true);
        }
      }
    } catch (Exception ex) {
      logger.error("[ScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
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

      // 現在のユーザー名を取得する
      ALEipUser loginuser = ALEipUtils.getALEipUser(rundata);
      String current = loginuser.getAliasName().toString();
      context.put("current_user", current);
      context.put("current_user_ln", loginuser.getName());
      context.put("current_user_id", loginuser.getUserId());

      if (ALEipConstants.MODE_FORM.equals(mode)) {
        doSchedule_form(rundata, context);
      } else if (ALEipConstants.MODE_DETAIL.equals(mode)) {
        doSchedule_detail(rundata, context);
      } else if (ALEipConstants.MODE_LIST.equals(mode)) {
        doSchedule_list(rundata, context);
      }

      if ("T".equals(ScheduleUtils.hasAuthSelf(rundata))) {
        context.put("tab-oneday", "0");
        context.put("tab-weekly", "0");
        context.put("tab-monthly", "0");
      }
      if ("T".equals(ScheduleUtils.hasAuthOther(rundata))) {
        context.put("tab-oneday-group", "0");
        context.put("tab-weekly-group", "0");
        context.put("widthALL", Integer.toString(5 * 120 + 40) + "px");
      }
      context.put("widthALL", Integer.toString(5 * 120 + 40) + "px");

      if (getMode() == null) {
        doSchedule_list(rundata, context);
      }
    } catch (Exception ex) {
      logger.error("[ScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }

  }

  /**
   * スケジュール登録のフォームを表示します。
   * 
   * @param rundata
   * @param context
   */
  public void doSchedule_form(RunData rundata, Context context) {
    try {
      ScheduleFormData formData = new ScheduleFormData();
      formData.loadParameters(rundata, context);
      formData.initField();
      formData.doViewForm(this, rundata, context);

      // ブラウザ名を受け渡す．
      boolean isMsie = ScheduleUtils.isMsieBrowser(rundata);
      context.put("isMeie", Boolean.valueOf(isMsie));

      // トップ画面からのスケジュール入力であるかを判定する．
      String afterBehavior = rundata.getRequest().getParameter(AFTER_BEHAVIOR);
      if (afterBehavior != null) {
        context.put(AFTER_BEHAVIOR, "1");
      }

      setTemplate(rundata, "schedule-form");
    } catch (Exception ex) {
      logger.error("[ScheduleAction] Exception.", ex);
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
      ScheduleFormData formData = new ScheduleFormData();
      formData.initField();

      // ブラウザ名を受け渡す．
      boolean isMsie = ScheduleUtils.isMsieBrowser(rundata);
      context.put("isMeie", Boolean.valueOf(isMsie));

      String afterBehavior = rundata.getRequest().getParameter(AFTER_BEHAVIOR);
      setTemplate(rundata, "schedule-form");
      if (formData.doInsert(this, rundata, context)) {
        if ("1".equals(afterBehavior)) {
          JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
          rundata.setRedirectURI(jsLink.getPortletById(
            ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
            "action",
            "controls.Restore").toString());
          rundata.getResponse().sendRedirect(rundata.getRedirectURI());
          jsLink = null;
        } else {
          doSchedule_list(rundata, context);
          // rundata.setRedirectURI(jsLink.getPortletById(
          // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
          // "eventSubmit_doSchedule_list", "1").toString());
        }

      } else {
        if ("1".equals(afterBehavior)) {
          // 追加処理後にノーマル画面に画面遷移することを指定．
          context.put(AFTER_BEHAVIOR, "1");
        }
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
      ScheduleFormData formData = new ScheduleFormData();
      formData.loadParametersViewDate(rundata, context);
      formData.initField();

      // ブラウザ名を受け渡す．
      boolean isMsie = ScheduleUtils.isMsieBrowser(rundata);
      context.put("isMeie", Boolean.valueOf(isMsie));

      String afterBehavior = rundata.getRequest().getParameter(AFTER_BEHAVIOR);
      setTemplate(rundata, "schedule-form");
      if (formData.doUpdate(this, rundata, context)) {
        if ("1".equals(afterBehavior)) {
          JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
          rundata.setRedirectURI(jsLink.getPortletById(
            ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
            "action",
            "controls.Restore").toString());
          rundata.getResponse().sendRedirect(rundata.getRedirectURI());
          jsLink = null;
        } else {
          doSchedule_list(rundata, context);
          // rundata.setRedirectURI(jsLink.getPortletById(
          // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
          // "eventSubmit_doSchedule_list", "1").toString());
        }

      } else {
        if ("1".equals(afterBehavior)) {
          // 変更処理後にノーマル画面に画面遷移することを指定．
          context.put(AFTER_BEHAVIOR, "1");
        }
      }
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
      String afterBehavior = rundata.getRequest().getParameter(AFTER_BEHAVIOR);
      ScheduleFormData formData = new ScheduleFormData();
      formData.loadParametersViewDate(rundata, context);
      formData.initField();
      if (formData.doDelete(this, rundata, context)) {
        setTemplate(rundata, "schedule-form");
        if ("1".equals(afterBehavior)) {
          JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
          rundata.setRedirectURI(jsLink.getPortletById(
            ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
            "action",
            "controls.Restore").toString());
          rundata.getResponse().sendRedirect(rundata.getRedirectURI());
          jsLink = null;
        } else {
          doSchedule_list(rundata, context);
          // rundata.setRedirectURI(jsLink.getPortletById(
          // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
          // "eventSubmit_doSchedule_list", "1").toString());
        }

      } else {
        if ("1".equals(afterBehavior)) {
          // 削除処理後にノーマル画面に画面遷移することを指定．
          context.put(AFTER_BEHAVIOR, "1");
        }
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
      ALAbstractSelectData<EipTScheduleMap, EipTScheduleMap> listData;
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
        listData = new ScheduleOnedaySelectData();
        ((ScheduleOnedaySelectData) listData).setPortletId(portletId);
        // ブラウザ名を受け渡す．
        boolean isMsie = ScheduleUtils.isMsieBrowser(rundata);
        context.put("isMeie", Boolean.valueOf(isMsie));
      } else if (currentTab.equals("weekly")) {
        listData = new ScheduleWeeklySelectData();
        ((ScheduleWeeklySelectData) listData).setPortletId(portletId);
      } else if (currentTab.equals("monthly")) {
        listData = new ScheduleMonthlySelectData();
        ((ScheduleMonthlySelectData) listData).setPortletId(portletId);
      } else if (currentTab.equals("oneday-group")) {
        listData = new ScheduleOnedayGroupSelectData();
        ((ScheduleOnedayGroupSelectData) listData).setPortletId(portletId);
        // ブラウザ名を受け渡す．
        boolean isMsie = ScheduleUtils.isMsieBrowser(rundata);
        context.put("isMeie", Boolean.valueOf(isMsie));
      } else {
        listData = new ScheduleWeeklyGroupSelectData();
        ((ScheduleWeeklyGroupSelectData) listData).setPortletId(portletId);
      }
      listData.initField();
      if (!ScheduleUtils.hasMinimumAuthority(rundata)) {
        setTemplate(rundata, "schedule-list");
      } else {
        listData.doViewList(this, rundata, context);
        setTemplate(rundata, "schedule-list");
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
      // トップ画面からのスケジュール入力であるかを判定する．
      String afterBehavior = rundata.getRequest().getParameter(AFTER_BEHAVIOR);
      if (afterBehavior != null) {
        context.put(AFTER_BEHAVIOR, "1");
      }

      ScheduleSelectData detailData = new ScheduleSelectData();
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
        setTemplate(rundata, "schedule-detail");

        if (viewDate == null || viewDate.equals("")) {
          logger
            .error("[ScheduleAction] ALPageNotFoundException: View Date is wrong.");
          throw new ALPageNotFoundException();
        }

        doSchedule_detail(rundata, context);
        // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
        // rundata.setRedirectURI(jsLink.getPortletById(
        // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        // "eventSubmit_doSchedule_detail", "1").addQueryData("view_date",
        // viewDate).toString());
        // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
        // jsLink = null;
      }
    } catch (Exception ex) {
      logger.error("[ScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * 編集画面でキャンセルを押したときの処理．
   * 
   * @param rundata
   * @param context
   */
  public void doSchedule_cancel(RunData rundata, Context context) {
    try {
      // トップ画面からのスケジュール入力であるかを判定する．
      String afterBehavior = rundata.getRequest().getParameter(AFTER_BEHAVIOR);
      if (afterBehavior != null && "1".equals(afterBehavior)) {
        JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
        rundata.setRedirectURI(jsLink.getPortletById(
          ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
          "action",
          "controls.Restore").toString());
        rundata.getResponse().sendRedirect(rundata.getRedirectURI());
        jsLink = null;
      } else {
        doSchedule_list(rundata, context);
      }
    } catch (Exception ex) {
      logger.error("[ScheduleAction] Exception.", ex);
    }
  }

  private void clearScheduleSession(RunData rundata, Context context) {
    List<String> list = new ArrayList<String>();
    list.add("entityid");
    list.add("target_user_id");
    ALEipUtils.removeTemp(rundata, context, list);
  }

}

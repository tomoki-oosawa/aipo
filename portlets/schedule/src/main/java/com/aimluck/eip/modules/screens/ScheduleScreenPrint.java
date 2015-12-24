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

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.VEipTScheduleList;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.schedule.ScheduleListSelectData;
import com.aimluck.eip.schedule.ScheduleMonthlySelectData;
import com.aimluck.eip.schedule.ScheduleOnedayGroupSelectData;
import com.aimluck.eip.schedule.ScheduleOnedaySelectData;
import com.aimluck.eip.schedule.ScheduleWeeklyGroupSelectData;
import com.aimluck.eip.schedule.ScheduleWeeklySelectData;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * スケジュールの一覧を処理するクラスです。 <br />
 * 
 */
public class ScheduleScreenPrint extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleScreen.class.getName());

  /** コンテントタイプ */
  private static final String CONTENT_TYPE = "text/html;charset="
    + ALEipConstants.DEF_CONTENT_ENCODING;

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
    // String tab = rundata.getParameters().getString("tab");
    try {
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

      ALAbstractSelectData<VEipTScheduleList, VEipTScheduleList> listData =
        null;
      // ポートレット ID を取得する．
      String portletId = portlet.getID();
      String currentTab;
      String tmpCurrentTab = ALEipUtils.getTemp(rundata, context, "tab");
      if (tmpCurrentTab == null
        || !(tmpCurrentTab.equals("oneday")
          || tmpCurrentTab.equals("weekly")
          || tmpCurrentTab.equals("monthly")
          || tmpCurrentTab.equals("list")
          || tmpCurrentTab.equals("oneday-group") || tmpCurrentTab
            .equals("weekly-group"))) {
        currentTab = "oneday";
      } else {
        currentTab = tmpCurrentTab;
      }

      int tab_count = 0;
      // Velocity テンプレートを読み込む
      String template = "";
      String _template =
        portlet.getPortletConfig().getInitParameter("pba-template");
      boolean done = false;

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

      String tab_flg_list =
        ALEipUtils
          .getPortlet(rundata, context)
          .getPortletConfig()
          .getInitParameter("pba-tab");
      if ("0".equals(tab_flg_list) && ("T".equals(has_acl_other))) {
        tab_count++;
        if (("".equals(template)) || (!done)) {
          template = "schedule-list";
          if (template.equals(_template)) {
            done = true;
          }
        }
      }

      if ("oneday".equals(currentTab)) {
        // tab = "oneday";
        if ("T".equals(has_acl_self)) {
          if (!"0".equals(tab_flg_oneday)) {
            tab_flg_oneday = "0";
            tab_count++;
          }
        }
        listData = new ScheduleOnedaySelectData();
        ((ScheduleOnedaySelectData) listData).setPortletId(portletId);
        // ブラウザ名を受け渡す．
        boolean isMsie = ALEipUtils.isMsieBrowser(rundata);
        context.put("isMeie", Boolean.valueOf(isMsie));
      } else if ("weekly".equals(currentTab)) {
        // tab = "weekly";
        if ("T".equals(has_acl_self)) {
          if (!"0".equals(tab_flg_weekly)) {
            tab_flg_weekly = "0";
            tab_count++;
          }
        }
        listData = new ScheduleWeeklySelectData();
        ((ScheduleWeeklySelectData) listData).setPortletId(portletId);
      } else if ("monthly".equals(currentTab)) {
        // tab = "monthly";
        if ("T".equals(has_acl_self)) {
          if (!"0".equals(tab_flg_monthly)) {
            tab_flg_monthly = "0";
            tab_count++;
          }
        }
        listData = new ScheduleMonthlySelectData();
        ((ScheduleMonthlySelectData) listData).setPortletId(portletId);
      } else if ("list".equals(currentTab)) {
        // tab = "list";
        if ("T".equals(has_acl_self)) {
          if (!"0".equals(tab_flg_list)) {
            tab_flg_list = "0";
            tab_count++;
          }
        }
        listData = new ScheduleListSelectData();
        ((ScheduleListSelectData) listData).setPortletId(portletId);
      } else if ("oneday-group".equals(currentTab)) {
        // tab = "oneday-group";
        if ("T".equals(has_acl_other)) {
          if (!"0".equals(tab_flg_oneday_group)) {
            tab_flg_oneday_group = "0";
            tab_count++;
          }
        }
        listData = new ScheduleOnedayGroupSelectData();
        ((ScheduleOnedayGroupSelectData) listData).setPortletId(portletId);
        // ブラウザ名を受け渡す．
        boolean isMsie = ALEipUtils.isMsieBrowser(rundata);
        context.put("isMeie", Boolean.valueOf(isMsie));
      } else if ("weekly-group".equals(currentTab)) {
        // tab = "weekly-group";
        if ("T".equals(has_acl_other)) {
          if (!"0".equals(tab_flg_weekly_group)) {
            tab_flg_weekly_group = "0";
            tab_count++;
          }
        }
        listData = new ScheduleWeeklyGroupSelectData();
        ((ScheduleWeeklyGroupSelectData) listData).setPortletId(portletId);
      } else {
        logger.info("unknown schedule type selected > listData is null");
        return;
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
      context.put("ajax_onloadimage", "true");

      listData.initField();
      listData.doViewList(this, rundata, context);

      String layout_template = "portlets/html/ajax-schedule-print.vm";

      setTemplate(rundata, context, layout_template);

    } catch (Exception ex) {
      logger.error("[ToDoScreen] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  @Override
  protected String getContentType(RunData rundata) {
    return CONTENT_TYPE;
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return ScheduleUtils.SCHEDULE_PORTLET_NAME;
  }

}

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

package com.aimluck.eip.modules.screens;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.om.registry.ClientEntry;
import org.apache.jetspeed.om.registry.ClientRegistry;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.VEipTScheduleList;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.schedule.AjaxScheduleWeeklyGroupEmptySelectData;
import com.aimluck.eip.schedule.ScheduleListSelectData;
import com.aimluck.eip.schedule.ScheduleMonthlySelectData;
import com.aimluck.eip.schedule.ScheduleOnedayGroupSelectData;
import com.aimluck.eip.schedule.ScheduleOnedaySelectData;
import com.aimluck.eip.schedule.ScheduleSearchSelectData;
import com.aimluck.eip.schedule.ScheduleWeeklyGroupSelectData;
import com.aimluck.eip.schedule.ScheduleWeeklySelectData;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.userfacility.beans.UserFacilityLiteBean;
import com.aimluck.eip.userfacility.util.UserFacilityUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * スケジュールの一覧を処理するクラスです。 <br />
 * 
 */
public class ScheduleScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleScreen.class.getName());

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

      String useragent = rundata.getUserAgent();
      ClientRegistry registry = (ClientRegistry) Registry.get(Registry.CLIENT);
      ClientEntry entry = registry.findEntry(useragent);

      if (tmpCurrentTab == null
        || !(tmpCurrentTab.equals("calendar")
          || tmpCurrentTab.equals("oneday")
          || tmpCurrentTab.equals("weekly")
          || tmpCurrentTab.equals("monthly")
          || tmpCurrentTab.equals("list")
          || tmpCurrentTab.equals("search")
          || tmpCurrentTab.equals("oneday-group") || tmpCurrentTab
          .equals("weekly-group"))) {
        currentTab = "calendar";
      } else {
        currentTab = tmpCurrentTab;
      }
      if ("IPHONE".equals(entry.getManufacturer())
        && "calender".equals(currentTab)) {
        currentTab = "oneday";
      }

      // Velocity テンプレートを読み込む
      String template = "";
      String _template =
        portlet.getPortletConfig().getInitParameter("pba-template");
      boolean done = false;

      // 初期共有メンバー表示フラグを取得する
      String showAll = portlet.getPortletConfig().getInitParameter("p7d-schk");
      if (!("t".equals(showAll))) {
        showAll = "f";
      }
      context.put("init_s_all", showAll);

      // アクセスコントロール
      String has_acl_other = ScheduleUtils.hasAuthOther(rundata);
      context.put("hasAcl", has_acl_other);

      if (!"IPHONE".equals(entry.getManufacturer())
        && ("".equals(template) || (!done))) {
        template = "schedule-calendar";
        if (template.equals(_template)) {
          done = true;
        }
      }
      if (("".equals(template)) || (!done)) {
        template = "schedule-oneday";
        if (template.equals(_template)) {
          template = "schedule-oneday-group";
          done = true;
        }
      }
      if (("".equals(template)) || (!done)) {
        template = "schedule-weekly";
        if (template.equals(_template)) {
          template = "schedule-weekly-group";
          done = true;
        }
      }
      if (("".equals(template)) || (!done)) {
        template = "schedule-monthly";
        if (template.equals(_template)) {
          done = true;
        }
      }
      if (("".equals(template)) || (!done)) {
        template = "schedule-oneday-group";
        if (template.equals(_template)) {
          done = true;
        }
      }
      if (("".equals(template)) || (!done)) {
        template = "schedule-weekly-group";
        if (template.equals(_template)) {
          done = true;
        }
      }
      if (("".equals(template)) || (!done)) {
        template = "schedule-search-list";
        if (template.equals(_template)) {
          done = true;
        }
      }
      if (("".equals(template)) || (!done)) {
        template = "schedule-search-result";
        if (template.equals(_template)) {
          done = true;
        }
      }
      if ("".equals(template)) {
        template = _template;
      }

      if ("calendar".equals(currentTab)) {
        // tab = "calendar"
        listData = new AjaxScheduleWeeklyGroupEmptySelectData();
        boolean isMsie = ScheduleUtils.isMsieBrowser(rundata);
        context.put("isMeie", Boolean.valueOf(isMsie));

        // 初期選択メンバーリストを取得する
        List<UserFacilityLiteBean> memberList =
          new ArrayList<UserFacilityLiteBean>();
        String selected_user =
          portlet.getPortletConfig().getInitParameter("p6a-uids");
        if (selected_user == null || "".equals(selected_user)) {
          UserFacilityLiteBean login_user =
            UserFacilityUtils.getUserFacilityLiteBean(rundata);
          memberList.add(login_user);
        } else {
          String selected_users[] = selected_user.split(",");
          List<UserFacilityLiteBean> ulist =
            ScheduleUtils.getALEipUserFacility(selected_users, rundata);
          if (ulist == null || ulist.size() == 0) {
            UserFacilityLiteBean login_user =
              UserFacilityUtils.getUserFacilityLiteBean(rundata);
            memberList.add(login_user);
          } else {
            memberList.addAll(ulist);
          }
        }

        context.put("member_list", memberList);

      } else if ("oneday".equals(currentTab)) {
        // tab = "oneday";
        listData = new ScheduleOnedaySelectData();
        ((ScheduleOnedaySelectData) listData).setPortletId(portletId);
        // ブラウザ名を受け渡す．
        boolean isMsie = ScheduleUtils.isMsieBrowser(rundata);
        context.put("isMeie", Boolean.valueOf(isMsie));
      } else if ("weekly".equals(currentTab)) {
        listData = new ScheduleWeeklySelectData();
        ((ScheduleWeeklySelectData) listData).setPortletId(portletId);
      } else if ("monthly".equals(currentTab)) {
        // tab = "monthly";
        listData = new ScheduleMonthlySelectData();
        ((ScheduleMonthlySelectData) listData).setPortletId(portletId);
      } else if ("oneday-group".equals(currentTab)) {
        // tab = "oneday-group";
        listData = new ScheduleOnedayGroupSelectData();
        ((ScheduleOnedayGroupSelectData) listData).setPortletId(portletId);
        // ブラウザ名を受け渡す．
        boolean isMsie = ScheduleUtils.isMsieBrowser(rundata);
        context.put("isMeie", Boolean.valueOf(isMsie));
      } else if ("list".equals(currentTab)) {
        // tab = "list";
        listData = new ScheduleListSelectData();
        ((ScheduleListSelectData) listData).setPortletId(portletId);
        // ブラウザ名を受け渡す．
        boolean isMsie = ScheduleUtils.isMsieBrowser(rundata);
        context.put("isMeie", Boolean.valueOf(isMsie));
      } else if ("search".equals(currentTab)) {
        // tab = "search";
        listData = new ScheduleSearchSelectData();
        ((ScheduleSearchSelectData) listData).setPortletId(portletId);
        // TODO: 表示カスタマイズ
        listData.setRowsNum(20);
        // ブラウザ名を受け渡す．
        boolean isMsie = ScheduleUtils.isMsieBrowser(rundata);
        context.put("isMeie", Boolean.valueOf(isMsie));
      } else {
        // tab = "weekly-group";
        listData = new ScheduleWeeklyGroupSelectData();
        ((ScheduleWeeklyGroupSelectData) listData).setPortletId(portletId);
      }

      context.put("ajax_onloadimage", "true");

      listData.initField();
      listData.doViewList(this, rundata, context);

      String layout_template = "portlets/html/ja/ajax-schedule.vm";

      setTemplate(rundata, context, layout_template);

    } catch (Exception ex) {
      logger.error("[ScheduleScreen] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return ScheduleUtils.SCHEDULE_PORTLET_NAME;
  }
}

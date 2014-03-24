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
 *
 * Project Management Portlet was developed by Advance,Inc.
 * http://www.a-dvance.co.jp/
 */

package com.aimluck.eip.modules.actions.project;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.project.ProjectSelectData;
import com.aimluck.eip.project.ProjectTaskSelectData;
import com.aimluck.eip.project.ProjectTaskSimpleSelectData;
import com.aimluck.eip.project.util.ProjectUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * プロジェクト管理アクションクラスです。
 * 
 */
public class ProjectAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ProjectAction.class.getName());

  /** ソート値のキー */
  private static final String LIST_SORT_STR = new StringBuffer()
    .append(ProjectTaskSelectData.class.getName())
    .append(ALEipConstants.LIST_SORT)
    .toString();

  /** ソート項目のキー */
  private static final String LIST_SORT_TYPE_STR = new StringBuffer()
    .append(ProjectTaskSelectData.class.getName())
    .append(ALEipConstants.LIST_SORT_TYPE)
    .toString();

  /** フィルタ値のキー */
  private static final String LIST_FILTER_STR = new StringBuffer()
    .append(ProjectTaskSelectData.class.getName())
    .append(ALEipConstants.LIST_FILTER)
    .toString();

  /** フィルタ項目のキー */
  private static final String LIST_FILTER_TYPE_STR = new StringBuffer().append(
    ProjectTaskSelectData.class.getName()).append(
    ALEipConstants.LIST_FILTER_TYPE).toString();

  /**
   * 通常表示の際の処理を記述します。
   * 
   * @param portlet
   *          VelocityPortlet
   * @param context
   *          Context
   * @param rundata
   *          RunData
   * @throws Exception
   *           例外
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {

    // セッション情報のクリア
    clearSession(rundata, context);

    ProjectTaskSimpleSelectData listData = new ProjectTaskSimpleSelectData();
    listData.setFiltersFromPSML(portlet, context, rundata);
    listData.initField();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "project");
  }

  /**
   * プロジェクト情報を一覧表示します。
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @throws Exception
   *           例外
   */
  public void doProject_list(RunData rundata, Context context) throws Exception {
    ProjectSelectData listData = new ProjectSelectData();
    listData.initField();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "project");
  }

  /**
   * 最大化表示の際の処理を記述します。
   * 
   * @param portlet
   *          VelocityPortlet
   * @param context
   *          Context
   * @param rundata
   *          RunData
   */
  @Override
  protected void buildMaximizedContext(VelocityPortlet portlet,
      Context context, RunData rundata) {
    try {
      if (getMode() == null) {
        String gantt = rundata.getParameters().getString("gantt");
        if (ProjectUtils.FLG_ON.equals(gantt)) {
          doGantt_chart(rundata, context);
        } else {
          doTask_list(rundata, context);
        }
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
    }
  }

  /**
   * タスク情報を一覧表示します。 <BR>
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @throws Exception
   *           例外
   */
  public void doTask_list(RunData rundata, Context context) throws Exception {
    ProjectTaskSelectData listData = new ProjectTaskSelectData();
    listData.initField();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1b-rows")));
    listData.doViewList(this, rundata, context);

    setTemplate(rundata, "project-task-list");
  }

  /**
   * ガントチャートを表示します。 <BR>
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @throws Exception
   *           例外
   */
  public void doGantt_chart(RunData rundata, Context context) throws Exception {
    ProjectTaskSelectData listData = new ProjectTaskSelectData();
    listData.initField();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1b-rows")));
    listData.doViewList(this, rundata, context);

    setTemplate(rundata, "project-gantt-chart");
  }

  /**
   * セッション情報をクリアする
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   */
  private void clearSession(RunData rundata, Context context) {
    List<String> list = new ArrayList<String>();
    // エンティティIDの初期化
    list.add("entityid");
    list.add(LIST_SORT_STR);
    list.add(LIST_SORT_TYPE_STR);
    list.add(LIST_FILTER_STR);
    list.add(LIST_FILTER_TYPE_STR);
    // 表示条件
    list.add("target_keyword"); // キーワード
    list.add("target_user_id"); // ユーザー
    list.add("target_tracker"); // 分類
    list.add("target_priority"); // 優先度
    list.add("target_status"); // ステータス
    list.add("target_progress_rate_from"); // ガントチャート表示From
    list.add("target_progress_rate_to"); // ガントチャート表示To
    list.add("target_delay"); // 進捗遅れ
    list.add("progress_line_checked"); // イナズマ線表示
    ALEipUtils.removeTemp(rundata, context, list);
  }

}

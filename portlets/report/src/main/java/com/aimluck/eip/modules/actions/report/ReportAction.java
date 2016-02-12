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
package com.aimluck.eip.modules.actions.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.report.ReportReplyFormData;
import com.aimluck.eip.report.ReportSelectData;
import com.aimluck.eip.report.util.ReportUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 報告書のアクションクラスです。 <BR>
 *
 */
public class ReportAction extends ALBaseAction {

  /** 返信用キー */
  private final String RESULT_ON_REPORT_DETAIL = "resultOnReportDetail";

  /** 返信用エラーメッセージキー */
  private final String ERROR_MESSAGE_LIST_ON_REPORT_DETAIL =
    "errmsgsOnReportDetail";

  /** 返信用 result */
  private Object resultOnReportDetail;

  /** 返信用異常系のメッセージを格納するリスト */
  private List<String> errmsgListOnReportDetail;

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ReportAction.class.getName());

  /**
   * 通常表示の際の処理を記述します。 <BR>
   *
   * @param portlet
   * @param context
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {

    // セッション情報をクリア
    ReportUtils.clearReportSession(rundata, context);
    ALEipUtils.setTemp(rundata, context, "Report_Maximize", "false");

    ReportSelectData listData = new ReportSelectData();
    listData.initField();
    listData.setRowsNum(Integer.parseInt(portlet
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    if (listData.doViewList(this, rundata, context)) {
      setTemplate(rundata, "report");
    }

  }

  /**
   * 最大化表示の際の処理を記述します。 <BR>
   *
   * @param portlet
   * @param context
   * @param rundata
   */
  @Override
  protected void buildMaximizedContext(VelocityPortlet portlet,
      Context context, RunData rundata) {

    // セッション情報をクリア
    ReportUtils.clearReportSession(rundata, context);
    ALEipUtils.setTemp(rundata, context, "Report_Maximize", "true");

    ReportSelectData listData = new ReportSelectData();
    listData.initField();
    // PSMLからパラメータをロードする
    // 最大表示件数（最大化時）
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1b-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "report-list");
  }

  /**
   *
   * @param obj
   */
  public void setResultDataOnReportDetail(Object obj) {
    resultOnReportDetail = obj;
  }

  /**
   *
   * @param msg
   */
  public void addErrorMessagesOnReportDetail(List<String> msgs) {
    if (errmsgListOnReportDetail == null) {
      errmsgListOnReportDetail = new ArrayList<String>();
    }
    errmsgListOnReportDetail.addAll(msgs);
  }

  /**
   * トピックに返信します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doReport_reply(RunData rundata, Context context) throws Exception {
    ReportReplyFormData formData = new ReportReplyFormData();
    formData.initField();
    if (formData.doInsert(this, rundata, context)) {
      // データ登録が成功したとき
      doReport_detail(rundata, context);
    } else {
      // トピック詳細表示用の情報を再取得
      ReportSelectData detailData = new ReportSelectData();
      detailData.initField();
      if (detailData.doViewDetail(this, rundata, context)) {
        setTemplate(rundata, "report-detail");
      } else {
        doReport_list(rundata, context);
      }
    }
  }

  /**
   * トピックを一覧表示します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doReport_list(RunData rundata, Context context) throws Exception {
    ReportSelectData listData = new ReportSelectData();
    listData.initField();
    // PSMLからパラメータをロードする
    // 最大表示件数（最大化時）
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1b-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "report-list");
  }

  /**
   * トピックを詳細表示します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doReport_detail(RunData rundata, Context context)
      throws Exception {
    ReportSelectData detailData = new ReportSelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      if (detailData.showReplyForm()) {
        ReportReplyFormData formData = new ReportReplyFormData();
        formData.initField();
        formData.doViewForm(this, rundata, context);
      }
      setTemplate(rundata, "report-detail");
    } else {
      doReport_list(rundata, context);
    }
  }

  /**
   *
   * @param context
   */
  public void putDataOnReportDetail(RunData rundata, Context context) {
    context.put(RESULT_ON_REPORT_DETAIL, resultOnReportDetail);
    context.put(ERROR_MESSAGE_LIST_ON_REPORT_DETAIL, errmsgListOnReportDetail);

    // For security
    context.put(ALEipConstants.SECURE_ID, rundata.getUser().getTemp(
      ALEipConstants.SECURE_ID));
  }

}

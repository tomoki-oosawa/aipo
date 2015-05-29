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
package com.aimluck.eip.modules.screens;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.report.ReportReplyFormData;
import com.aimluck.eip.report.ReportSelectData;
import com.aimluck.eip.report.util.ReportUtils;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 報告書の詳細画面を処理するクラスです。 <br />
 * 
 */
public class ReportDetailScreen extends ALVelocityScreen {

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
    .getLogger(ReportDetailScreen.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {
    try {
      ReportSelectData detailData = new ReportSelectData();
      detailData.initField();
      detailData.doViewDetail(this, rundata, context);

      if (detailData.showReplyForm()) {
        ReportReplyFormData formData = new ReportReplyFormData();
        formData
          .setAclPortletFeature(ALAccessControlConstants.POERTLET_FEATURE_REPORT_REPLY);
        formData.initField();
        formData.doViewForm(this, rundata, context);
      }
      String layout_template = "portlets/html/ajax-report-detail.vm";
      setTemplate(rundata, context, layout_template);
    } catch (Exception ex) {
      logger.error("[ReportDetailScreen] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
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
   * 
   * @param context
   */
  public void putDataOnReportDetail(RunData rundata, Context context) {
    context.put(RESULT_ON_REPORT_DETAIL, resultOnReportDetail);
    context.put(ERROR_MESSAGE_LIST_ON_REPORT_DETAIL, errmsgListOnReportDetail);
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return ReportUtils.REPORT_PORTLET_NAME;
  }

  /**
   * 
   * @param obj
   */
  public void setResultDataOnReportDetail(Object obj) {
    resultOnReportDetail = obj;
  }

}

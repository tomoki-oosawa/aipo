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
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.msgboard.MsgboardTopicReplyFormData;
import com.aimluck.eip.msgboard.MsgboardTopicSelectData;
import com.aimluck.eip.msgboard.util.MsgboardUtils;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 掲示板トピックの詳細画面を処理するクラスです。
 *
 */

public class MsgboardTopicDetailScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MsgboardTopicDetailScreen.class.getName());

  /** 返信用キー */
  private final String RESULT_ON_TOPIC_DETAIL = "resultOnTopicDetail";

  /** 返信用エラーメッセージキー */
  private final String ERROR_MESSAGE_LIST_ON_TOPIC_DETAIL =
    "errmsgsOnTopicDetail";

  /** 返信用 result */
  private Object resultOnTopicDetail;

  /** 返信用異常系のメッセージを格納するリスト */
  private List<String> errmsgListOnTopicDetail;

  /**
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    try {
      MsgboardTopicSelectData detailData = new MsgboardTopicSelectData();
      detailData.initField();
      detailData.loadCategoryList(rundata, context);
      detailData.doViewDetail(this, rundata, context);
      if (detailData.showReplyForm()) {
        MsgboardTopicReplyFormData formData = new MsgboardTopicReplyFormData();
        formData.setAclPortletFeature(ALAccessControlConstants.POERTLET_FEATURE_MSGBOARD_TOPIC_REPLY);
        formData.initField();
        formData.doViewForm(this, rundata, context);
      }

      String layout_template = "portlets/html/ajax-msgboard-topic-detail.vm";

      setTemplate(rundata, context, layout_template);
    } catch (Exception ex) {
      logger.error("[MsgboardTopicDetailScreen] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   *
   * @param obj
   */
  public void setResultDataOnTopicDetail(Object obj) {
    resultOnTopicDetail = obj;
  }

  /**
   *
   * @param msg
   */
  public void addErrorMessagesOnTopicDetail(List<String> msgs) {
    if (errmsgListOnTopicDetail == null) {
      errmsgListOnTopicDetail = new ArrayList<String>();
    }
    errmsgListOnTopicDetail.addAll(msgs);
  }

  /**
   *
   * @param context
   */
  public void putDataOnTopicDetail(RunData rundata, Context context) {
    context.put(RESULT_ON_TOPIC_DETAIL, resultOnTopicDetail);
    context.put(ERROR_MESSAGE_LIST_ON_TOPIC_DETAIL, errmsgListOnTopicDetail);
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return MsgboardUtils.MSGBOARD_PORTLET_NAME;
  }

}

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
package com.aimluck.eip.modules.actions.msgboard;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.msgboard.MsgboardTopicFormData;
import com.aimluck.eip.msgboard.MsgboardTopicMultiDelete;
import com.aimluck.eip.msgboard.MsgboardTopicReplyFormData;
import com.aimluck.eip.msgboard.MsgboardTopicSelectData;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 掲示板のアクションクラス
 * 
 */
public class CellMsgboardAction extends MsgboardAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MsgboardAction.class.getName());

  /** 返信用キー */
  private static final String RESULT_ON_TOPIC_DETAIL = "resultOnTopicDetail";

  /** 返信用エラーメッセージキー */
  private static final String ERROR_MESSAGE_LIST_ON_TOPIC_DETAIL =
    "errmsgsOnTopicDetail";

  /** 返信用 result */
  private Object resultOnTopicDetail;

  /** 返信用異常系のメッセージを格納するリスト */
  private List<String> errmsgListOnTopicDetail;

  /**
   * 通常表示の際の処理を記述します。
   * 
   * @param portlet
   * @param context
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {

  }

  /**
   * 最大化表示の際の処理を記述します。
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
        doMsgboard_topic_form(rundata, context);
      } else if (ALEipConstants.MODE_DETAIL.equals(mode)) {
        doMsgboard_topic_detail(rundata, context);
      } else if (ALEipConstants.MODE_LIST.equals(mode)) {
        doMsgboard_topic_list(rundata, context);
      } else if (getMode() == null) {
        doMsgboard_topic_list(rundata, context);
      }
    } catch (Exception ex) {
      logger.error("msgboard", ex);
    }

  }

  /**
   * トピック登録のフォームを表示する.
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  public void doMsgboard_topic_form(RunData rundata, Context context)
      throws Exception {
    MsgboardTopicFormData formData = new MsgboardTopicFormData();
    formData.initField();
    formData.loadCategoryList(rundata, context);
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "msgboard-topic-form");
  }

  /**
   * トピックを登録します。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  public void doMsgboard_topic_insert(RunData rundata, Context context)
      throws Exception {
    MsgboardTopicFormData formData = new MsgboardTopicFormData();
    formData.initField();
    formData.loadCategoryList(rundata, context);
    if (formData.doInsert(this, rundata, context)) {
      // データ登録が成功したとき
      doMsgboard_topic_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doMsgboard_topic_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    } else {
      setTemplate(rundata, "msgboard-topic-form");
    }

  }

  /**
   * トピックを削除します。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  public void doMsgboard_topic_delete(RunData rundata, Context context)
      throws Exception {
    MsgboardTopicFormData formData = new MsgboardTopicFormData();
    formData.initField();
    if (formData.doDelete(this, rundata, context)) {
      // データ削除が成功したとき
      doMsgboard_topic_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doMsgboard_topic_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    }

  }

  /**
   * 返信記事を削除します。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  public void doMsgboard_topic_reply_delete(RunData rundata, Context context)
      throws Exception {
    MsgboardTopicReplyFormData formData = new MsgboardTopicReplyFormData();
    formData.initField();
    if (formData.doDelete(this, rundata, context)) {
      // データ削除が成功したとき
      doMsgboard_topic_detail(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doMsgboard_topic_detail", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    }
  }

  /**
   * トピックを削除します。（複数）
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  public void doMsgboard_topic_multi_delete(RunData rundata, Context context)
      throws Exception {
    MsgboardTopicMultiDelete delete = new MsgboardTopicMultiDelete();
    delete.doMultiAction(this, rundata, context);
    doMsgboard_topic_list(rundata, context);
    // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
    // rundata.setRedirectURI(jsLink.getPortletById(
    // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
    // "eventSubmit_doMsgboard_topic_list", "1").toString());
    // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
    // jsLink = null;
  }

  /**
   * トピックを一覧表示します。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  public void doMsgboard_topic_list(RunData rundata, Context context)
      throws Exception {
    MsgboardTopicSelectData listData = new MsgboardTopicSelectData();
    listData.initField();
    listData.loadCategoryList(rundata, context);
    // PSMLからパラメータをロードする
    // 最大表示件数（最大化時）
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1b-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "msgboard-topic-list");
  }

  /**
   * トピックを詳細表示します。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  public void doMsgboard_topic_detail(RunData rundata, Context context)
      throws Exception {
    MsgboardTopicSelectData detailData = new MsgboardTopicSelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      if (detailData.showReplyForm()) {
        MsgboardTopicReplyFormData formData = new MsgboardTopicReplyFormData();
        formData.initField();
        formData.doViewForm(this, rundata, context);
      }
      setTemplate(rundata, "msgboard-topic-detail");
    } else {
      doMsgboard_topic_list(rundata, context);
    }
  }

  /**
   * 掲示板で使用したセッション情報を消去する．
   * 
   */
  @Override
  public void clearMsgboardSession(RunData rundata, Context context) {
    List<String> list = new ArrayList<String>();
    list.add("entityid");
    list.add("com.aimluck.eip.msgboard.MsgboardTopicSelectDatasort");
    list.add("com.aimluck.eip.msgboard.MsgboardTopicSelectDatasorttype");
    list.add("com.aimluck.eip.msgboard.MsgboardTopicSelectDatasorttype");
    list.add("com.aimluck.eip.msgboard.MsgboardTopicSelectDatafilter");
    list.add("com.aimluck.eip.msgboard.MsgboardTopicSelectDatafiltertype");
    list.add("com.aimluck.eip.msgboard.MsgboardTopicSelectDatasort");
    list.add("com.aimluck.eip.msgboard.MsgboardTopicSelectDatasorttype");
    ALEipUtils.removeTemp(rundata, context, list);

  }

  /**
   * 返信フォームを表示する.
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doMsgboard_reply_form(RunData rundata, Context context)
      throws Exception {
    MsgboardTopicReplyFormData formData = new MsgboardTopicReplyFormData();
    formData.initField();
    putData(rundata, context);
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "msgboard-reply-form");
  }

  /**
   * トピックに返信します。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  public void doMsgboard_topic_reply(RunData rundata, Context context)
      throws Exception {
    MsgboardTopicReplyFormData formData = new MsgboardTopicReplyFormData();
    formData.initField();
    if (formData.doInsert(this, rundata, context)) {
      // データ登録が成功したとき
      doMsgboard_topic_detail(rundata, context);
    } else {
      setTemplate(rundata, "msgboard-reply-form");
    }
  }

  /**
   * 
   * @param obj
   */
  @Override
  public void setResultDataOnTopicDetail(Object obj) {
    resultOnTopicDetail = obj;
  }

  /**
   * 
   * @param msg
   */
  @Override
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
  @Override
  public void putDataOnTopicDetail(RunData rundata, Context context) {
    context.put(RESULT_ON_TOPIC_DETAIL, resultOnTopicDetail);
    context.put(ERROR_MESSAGE_LIST_ON_TOPIC_DETAIL, errmsgListOnTopicDetail);

    // For security
    context.put(ALEipConstants.SECURE_ID, rundata.getUser().getTemp(
      ALEipConstants.SECURE_ID));
  }
}

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
package com.aimluck.eip.modules.actions.blog;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.blog.BlogCommonThemaSelectData;
import com.aimluck.eip.blog.BlogEntryCommentFormData;
import com.aimluck.eip.blog.BlogEntryFormData;
import com.aimluck.eip.blog.BlogEntryLatestSelectData;
import com.aimluck.eip.blog.BlogEntrySelectData;
import com.aimluck.eip.blog.BlogThemaFormData;
import com.aimluck.eip.blog.BlogThemaSelectData;
import com.aimluck.eip.blog.BlogUserSelectData;
import com.aimluck.eip.blog.BlogWordSelectData;
import com.aimluck.eip.blog.util.BlogUtils;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ブログのアクションクラスです。 <BR>
 * 
 */
public class BlogAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(BlogAction.class.getName());

  /** 返信用キー */
  private final String RESULT_ON_COMMENT_DETAIL = "resultOnCommentDetail";

  /** 返信用エラーメッセージキー */
  private final String ERROR_MESSAGE_LIST_ON_COMMENT_DETAIL =
    "errmsgsOnCommentDetail";

  /** 返信用 result */
  private Object resultOnCommentDetail;

  /** 返信用異常系のメッセージを格納するリスト */
  private ArrayList errmsgListOnCommentDetail;

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

    // セッション情報をクリアする．
    clearBlogSession(rundata, context);

    BlogEntryLatestSelectData listData = new BlogEntryLatestSelectData();
    listData.initField();
    // PSMLからパラメータをロードする
    // 最大表示件数（最大化時）
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.setStrLength(Integer.parseInt(ALEipUtils.getPortlet(
      rundata,
      context).getPortletConfig().getInitParameter("p3a-strlen")));

    // 最低限表示するのに必要な権限のチェック
    if (!BlogUtils.hasMinimumAuthority(rundata)) {
      setTemplate(rundata, "blog");
      context.put("hasMinimumAuthority", false);
    } else {
      listData.doViewList(this, rundata, context);
      setTemplate(rundata, "blog");
      context.put("hasMinimumAuthority", true);
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

    // MODEを取得
    String mode = rundata.getParameters().getString(ALEipConstants.MODE);
    try {
      if (ALEipConstants.MODE_FORM.equals(mode)) {
        doBlog_entry_form(rundata, context);
      } else if (ALEipConstants.MODE_DETAIL.equals(mode)) {
        doBlog_entry_detail(rundata, context);
      } else if (ALEipConstants.MODE_LIST.equals(mode)) {
        doBlog_entry_list(rundata, context);
      } else if ("thema_detail".equals(mode)) {
        doBlog_thema_detail(rundata, context);
      }
      if (getMode() == null) {
        doBlog_entry_list(rundata, context);
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
    }

  }

  /**
   * エントリー登録のフォームを表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doBlog_entry_form(RunData rundata, Context context)
      throws Exception {
    BlogEntryFormData formData = new BlogEntryFormData();
    formData.initField();
    formData.loadThemaList(rundata, context);
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "blog-entry-form");
  }

  /**
   * エントリーを登録します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doBlog_entry_insert(RunData rundata, Context context)
      throws Exception {
    BlogEntryFormData formData = new BlogEntryFormData();
    formData.initField();
    formData.loadThemaList(rundata, context);
    if (formData.doInsert(this, rundata, context)) {
      // データ登録が成功したとき
      doBlog_entry_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doBlog_entry_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    } else {
      setTemplate(rundata, "blog-entry-form");
    }
  }

  /**
   * エントリーを更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doBlog_entry_update(RunData rundata, Context context)
      throws Exception {
    BlogEntryFormData formData = new BlogEntryFormData();
    formData.initField();
    formData.loadThemaList(rundata, context);
    if (formData.doUpdate(this, rundata, context)) {
      // データ更新が成功したとき
      doBlog_entry_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doBlog_entry_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    } else {
      setTemplate(rundata, "blog-entry-form");
    }
  }

  /**
   * エントリーを削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doBlog_entry_delete(RunData rundata, Context context)
      throws Exception {
    BlogEntryFormData formData = new BlogEntryFormData();
    formData.initField();
    if (formData.doDelete(this, rundata, context)) {
      // データ削除が成功したとき
      doBlog_entry_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doBlog_entry_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    }
  }

  /**
   * エントリーを一覧表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doBlog_entry_list(RunData rundata, Context context)
      throws Exception {
    ALEipUtils.removeTemp(rundata, context, "view_month");
    ALEipUtils.removeTemp(rundata, context, "view_uid");
    doBlog_entry_list_user(rundata, context);
    // BlogEntrySelectData listData = new BlogEntrySelectData();
    // listData.initField();
    // listData.loadThemaList(rundata, context);
    // // PSMLからパラメータをロードする
    // // 最大表示件数（最大化時）
    // listData.setRowsNum(Integer.parseInt(ALEipUtils
    // .getPortlet(rundata, context).getPortletConfig().getInitParameter(
    // "p1b-rows")));
    // listData.doViewList(this, rundata, context);
    // setTemplate(rundata, "blog-entry-list");
  }

  /**
   * エントリーを一覧表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doBlog_entry_list_user(RunData rundata, Context context)
      throws Exception {
    BlogEntrySelectData listData = new BlogEntrySelectData();
    listData.initField();
    listData.loadThemaList(rundata, context);
    // PSMLからパラメータをロードする
    // 最大表示件数（最大化時）
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1b-rows")));
    listData.setStrLength(Integer.parseInt(ALEipUtils.getPortlet(
      rundata,
      context).getPortletConfig().getInitParameter("p3a-strlen")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "blog-entry-list");
  }

  /**
   * 最新のエントリーを一覧表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doBlog_entry_list_latest(RunData rundata, Context context)
      throws Exception {
    BlogEntryLatestSelectData listData = new BlogEntryLatestSelectData();
    listData.initField();
    // PSMLからパラメータをロードする
    // 最大表示件数（最大化時）
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1b-rows")));
    listData.setStrLength(Integer.parseInt(ALEipUtils.getPortlet(
      rundata,
      context).getPortletConfig().getInitParameter("p3a-strlen")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "blog-entry-list-latest");
  }

  /**
   * エントリーの詳細を表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doBlog_entry_detail(RunData rundata, Context context)
      throws Exception {
    BlogEntrySelectData detailData = new BlogEntrySelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      BlogEntryCommentFormData formData = new BlogEntryCommentFormData();
      formData.initField();
      formData.doViewForm(this, rundata, context);

      setTemplate(rundata, "blog-entry-detail");
    } else {
      doBlog_entry_list(rundata, context);
    }
  }

  /**
   * エントリーにコメントします。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doBlog_entry_reply(RunData rundata, Context context)
      throws Exception {
    BlogEntryCommentFormData formData = new BlogEntryCommentFormData();
    formData.initField();
    if (formData.doInsert(this, rundata, context)) {
      // データ登録が成功したとき
      doBlog_entry_detail(rundata, context);

      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doBlog_entry_detail", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    } else {
      // トピック詳細表示用の情報を再取得
      BlogEntrySelectData detailData = new BlogEntrySelectData();
      detailData.initField();
      if (detailData.doViewDetail(this, rundata, context)) {
        setTemplate(rundata, "blog-entry-detail");
      } else {
        doBlog_entry_list(rundata, context);
      }
    }
  }

  /**
   * コメントを削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doBlog_entry_comment_delete(RunData rundata, Context context)
      throws Exception {
    BlogEntryCommentFormData formData = new BlogEntryCommentFormData();
    formData.initField();
    if (formData.doDelete(this, rundata, context)) {
      // データ削除が成功したとき
      doBlog_entry_detail(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doBlog_entry_detail", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    }
  }

  // /**
  // * 添付ファイルの入力フォームを開く．
  // *
  // * @param rundata
  // * @param context
  // * @throws Exception
  // */
  // public void doBlog_entry_open_attachment(RunData rundata, Context context)
  // throws Exception {
  // BlogEntryAttachmentFormData formData = new BlogEntryAttachmentFormData();
  // formData.initField();
  // formData.doInitViewForm(this, rundata, context);
  // setTemplate(rundata, "blog-entry-attachment");
  // }
  //
  // /**
  // * 添付ファイルのアップロードを受け付ける．
  // *
  // * @param rundata
  // * @param context
  // * @throws Exception
  // */
  // public void doBlog_entry_upload_attachment(RunData rundata, Context
  // context)
  // throws Exception {
  // try {
  // BlogEntryAttachmentFormData formData = new BlogEntryAttachmentFormData();
  // formData.initField();
  // if (formData.doUpdate(this, rundata, context)) {
  // context.put("receiveFile", "true");
  // }
  // setTemplate(rundata, "blog-entry-attachment");
  // } catch (Exception ex) {
  // logger.error("Exception", ex);
  // }
  // }
  //
  // /**
  // * 添付ファイルを削除する．
  // *
  // * @param rundata
  // * @param context
  // * @throws Exception
  // */
  // public void doBlog_entry_del_attachments(RunData rundata, Context context)
  // throws Exception {
  // BlogEntryFormData formData = new BlogEntryFormData();
  // //formData.initOrms(rundata);
  // formData.initField();
  // formData.loadThemaList(rundata, context);
  // formData.doDeleteAttachments(this, rundata, context);
  // setTemplate(rundata, "blog-entry-form");
  // }

  /**
   * グループの一覧を表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doBlog_group_list(RunData rundata, Context context)
      throws Exception {
    BlogUserSelectData listData = new BlogUserSelectData();
    listData.initField();
    // // PSMLからパラメータをロードする
    // // 最大表示件数（最大化時）
    // listData.setRowsNum(Integer.parseInt(ALEipUtils
    // .getPortlet(rundata, context).getPortletConfig().getInitParameter(
    // "p1b-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "blog-group-list");
  }

  /**
   * 共通テーマの一覧を表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doBlog_common_thema_list(RunData rundata, Context context)
      throws Exception {
    BlogCommonThemaSelectData listData = new BlogCommonThemaSelectData();
    listData.initField();
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "blog-common-thema-list");
  }

  /**
   * 共通テーマを詳細表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doBlog_common_thema_detail(RunData rundata, Context context)
      throws Exception {
    BlogCommonThemaSelectData detailData = new BlogCommonThemaSelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      setTemplate(rundata, "blog-common-thema-detail");
    } else {
      doBlog_common_thema_list(rundata, context);
    }
  }

  /**
   * テーマ登録のフォームを表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doBlog_thema_form(RunData rundata, Context context)
      throws Exception {
    BlogThemaFormData formData = new BlogThemaFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "blog-thema-form");
  }

  /**
   * テーマを登録します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doBlog_thema_insert(RunData rundata, Context context)
      throws Exception {
    BlogThemaFormData formData = new BlogThemaFormData();
    formData.initField();
    if (formData.doInsert(this, rundata, context)) {
      // データ登録に成功したとき
      doBlog_thema_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doBlog_thema_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    } else {
      setTemplate(rundata, "blog-thema-form");
    }

  }

  /**
   * テーマを更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doBlog_thema_update(RunData rundata, Context context)
      throws Exception {
    BlogThemaFormData formData = new BlogThemaFormData();
    formData.initField();
    if (formData.doUpdate(this, rundata, context)) {
      // データ更新に成功したとき
      doBlog_thema_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doBlog_thema_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    } else {
      setTemplate(rundata, "blog-thema-form");
    }
  }

  /**
   * テーマを削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doBlog_thema_delete(RunData rundata, Context context)
      throws Exception {
    BlogThemaFormData formData = new BlogThemaFormData();
    formData.initField();
    if (formData.doDelete(this, rundata, context)) {
      // データ削除に成功したとき
      doBlog_thema_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doBlog_thema_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    }
  }

  // /**
  // * カテゴリを削除します。（複数） <BR>
  // * @param rundata
  // * @param context
  // * @throws Exception
  // */
  // public void doTodo_Thema_multi_delete(RunData rundata, Context context)
  // throws Exception {
  // ToDoThemaMultiDelete delete = new ToDoThemaMultiDelete();
  // delete.doMultiAction(this, rundata, context);
  // // doTodo_Thema_list(rundata, context);
  // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
  // rundata.setRedirectURI(
  // jsLink
  // .getPortletById(ALEipUtils.getPortlet(rundata, context).getID())
  // .addQueryData("eventSubmit_doTodo_Thema_list", "1")
  // .toString());
  // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
  // jsLink = null;
  // }

  /**
   * テーマを一覧表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doBlog_thema_list(RunData rundata, Context context)
      throws Exception {
    VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
    BlogThemaSelectData listData = new BlogThemaSelectData();
    listData.initField();
    // PSMLからパラメータをロードする
    // 最大表示件数（通常時）
    listData.setRowsNum(Integer.parseInt(portlet
      .getPortletConfig()
      .getInitParameter("p1c-rows")));
    listData.setStrLength(Integer.parseInt(portlet
      .getPortletConfig()
      .getInitParameter("p4a-strlen")));

    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "blog-thema-list");
  }

  /**
   * テーマを詳細表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doBlog_thema_detail(RunData rundata, Context context)
      throws Exception {
    BlogThemaSelectData detailData = new BlogThemaSelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      setTemplate(rundata, "blog-thema-detail");
    } else {
      doBlog_thema_list(rundata, context);
    }
    setTemplate(rundata, "blog-thema-detail");
  }

  /**
   * 検索一覧表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doBlog_search_list(RunData rundata, Context context)
      throws Exception {
    BlogWordSelectData listData = new BlogWordSelectData();
    // listData.setRowsNum(Integer.parseInt(ALEipUtils
    // .getPortlet(rundata, context).getPortletConfig().getInitParameter(
    // "p1a-rows")));
    listData.setStrLength(Integer.parseInt(ALEipUtils.getPortlet(
      rundata,
      context).getPortletConfig().getInitParameter("p3a-strlen")));
    listData.setRowsNum(20);
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "blog-search-list");

  }

  /**
   * 
   * @param obj
   */
  public void setResultDataOnCommentDetail(Object obj) {
    resultOnCommentDetail = obj;
  }

  /**
   * 
   * @param msg
   */
  public void addErrorMessagesOnCommentDetail(ArrayList msgs) {
    if (errmsgListOnCommentDetail == null) {
      errmsgListOnCommentDetail = new ArrayList();
    }
    errmsgListOnCommentDetail.addAll(msgs);
  }

  /**
   * 
   * @param context
   */
  public void putDataOnCommentDetail(RunData rundata, Context context) {
    context.put(RESULT_ON_COMMENT_DETAIL, resultOnCommentDetail);
    context
      .put(ERROR_MESSAGE_LIST_ON_COMMENT_DETAIL, errmsgListOnCommentDetail);
  }

  private void clearBlogSession(RunData rundata, Context context) {
    List list = new ArrayList();
    list.add("entityid");
    list.add("view_uid");
    list.add("target_group_name");
    list.add("Blogsword");
    list.add("com.aimluck.eip.blog.BlogWordSelectDatasort");
    list.add("com.aimluck.eip.blog.BlogEntrySelectDatafilter");
    list.add("com.aimluck.eip.blog.BlogEntrySelectDatafiltertype");
    list.add("com.aimluck.eip.blog.BlogThemaSelectDatasort");
    ALEipUtils.removeTemp(rundata, context, list);
  }
}

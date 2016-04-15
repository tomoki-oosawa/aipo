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
package com.aimluck.eip.modules.actions.blog;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.blog.BlogEntryCommentFormData;
import com.aimluck.eip.blog.BlogEntryFormData;
import com.aimluck.eip.blog.BlogEntryLatestSelectData;
import com.aimluck.eip.blog.BlogEntrySelectData;
import com.aimluck.eip.blog.BlogThemaFormData;
import com.aimluck.eip.blog.BlogThemaSelectData;
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
  private List<String> errmsgListOnCommentDetail;

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
    listData.loadThemaList(rundata, context);
    // PSMLからパラメータをロードする
    // 最大表示件数（最大化時）
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.setStrLength(100);

    listData.setFiltersPSML(portlet, context, rundata);

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
      logger.error("blog", ex);
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
    BlogEntrySelectData listData = new BlogEntrySelectData();
    listData.initField();
    listData.loadThemaList(rundata, context);
    // PSMLからパラメータをロードする
    // 最大表示件数（最大化時）
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1b-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "blog-entry-list");
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
    listData.setStrLength(100);
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
    listData.setStrLength(100);
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
    }
  }

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
    listData.setStrLength(100);

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
  public void addErrorMessagesOnCommentDetail(List<String> msgs) {
    if (errmsgListOnCommentDetail == null) {
      errmsgListOnCommentDetail = new ArrayList<String>();
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
    List<String> list = new ArrayList<String>();
    list.add("entityid");
    list.add("view_uid");
    list.add("target_group_name");
    list.add("Blogsword");
    list.add("BlogWordSelectDatasort");
    list.add("BlogEntrySelectDatafilter");
    list.add("BlogEntrySelectDatafiltertype");
    list.add("BlogThemaSelectDatasort");
    list.add(BlogUtils.OWNER_ID);
    list.add(BlogUtils.SEARCH_WORD);
    list.add(BlogUtils.GROUP_ID);
    list.add(BlogUtils.THEME_ID);
    ALEipUtils.removeTemp(rundata, context, list);
  }
}

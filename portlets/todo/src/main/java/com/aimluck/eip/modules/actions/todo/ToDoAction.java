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
package com.aimluck.eip.modules.actions.todo;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTTodo;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.todo.ToDoCategoryFormData;
import com.aimluck.eip.todo.ToDoCategoryMultiDelete;
import com.aimluck.eip.todo.ToDoCategorySelectData;
import com.aimluck.eip.todo.ToDoFormData;
import com.aimluck.eip.todo.ToDoMultiDelete;
import com.aimluck.eip.todo.ToDoMultiStateUpdate;
import com.aimluck.eip.todo.ToDoSelectData;
import com.aimluck.eip.todo.ToDoStateUpdateData;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ToDoのアクションクラスです。 <BR>
 *
 */
public class ToDoAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ToDoAction.class.getName());

  static final String LIST_FILTER_STR = new StringBuffer()
    .append(ToDoSelectData.class.getSimpleName())
    .append(ALEipConstants.LIST_FILTER)
    .toString();

  static final String LIST_FILTER_TYPE_STR = new StringBuffer().append(
    ToDoSelectData.class.getSimpleName()).append(
    ALEipConstants.LIST_FILTER_TYPE).toString();

  static final String LIST_SORT_STR = new StringBuffer()
    .append(ToDoSelectData.class.getSimpleName())
    .append(ALEipConstants.LIST_SORT)
    .toString();

  static final String LIST_SORT_TYPE_STR = new StringBuffer()
    .append(ToDoSelectData.class.getSimpleName())
    .append(ALEipConstants.LIST_SORT_TYPE)
    .toString();

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

    // セッション情報のクリア
    clearToDoSession(rundata, context);

    ToDoSelectData listData = new ToDoSelectData();
    listData.initField();
    listData.setRowsNum(Integer.parseInt(portlet
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.setTableColumNum(Integer.parseInt(portlet
      .getPortletConfig()
      .getInitParameter("p0e-rows")));

    listData.setFiltersPSML(portlet, context, rundata);

    if (listData.getTableColumNum() == 4) {
      String p12f_filters =
        portlet.getPortletConfig().getInitParameter("p12f-filters");
      if (null != p12f_filters) {
        ALEipUtils.setTemp(rundata, context, LIST_FILTER_STR, p12f_filters
          .trim());
      }
    }
    ALEipUtils.setTemp(
      rundata,
      context,
      LIST_SORT_STR,
      EipTTodo.UPDATE_DATE_PROPERTY);
    ALEipUtils.setTemp(
      rundata,
      context,
      LIST_SORT_TYPE_STR,
      ALEipConstants.LIST_SORT_TYPE_DESC);

    /*
     * ALEipUtils.setTemp( rundata, context, LIST_SORT_STR,
     * EipTTodo.UPDATE_DATE_PROPERTY);
     * 
     * ALEipUtils.setTemp( rundata, context, LIST_SORT_TYPE_STR,
     * ALEipConstants.LIST_SORT_TYPE_DESC);
     */
    /*
     * ToDoSelectData listData = new ToDoSelectData(); listData.initField();
     * listData.setRowsNum(Integer.parseInt(portlet .getPortletConfig()
     * .getInitParameter("p1a-rows")));
     * listData.setTableColumNum(Integer.parseInt(portlet .getPortletConfig()
     * .getInitParameter("p0e-rows")));
     */
    listData.setStrLength(0);
    listData.loadCategoryList(rundata);
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "todo");

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
        doTodo_form(rundata, context);
      } else if (ALEipConstants.MODE_DETAIL.equals(mode)) {
        doTodo_detail(rundata, context);
      } else if (ALEipConstants.MODE_LIST.equals(mode)) {
        doTodo_list(rundata, context);
      } else if ("category_detail".equals(mode)) {
        doTodo_category_detail(rundata, context);
      }
      if (getMode() == null) {
        doTodo_list(rundata, context);
      }
    } catch (Exception ex) {
      logger.error("todo", ex);
    }
  }

  /**
   * ToDo登録のフォームを表示します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTodo_form(RunData rundata, Context context) throws Exception {
    ToDoFormData formData = new ToDoFormData();
    formData.initField();
    formData.loadCategoryList(rundata);
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "todo-form");

  }

  /**
   * ToDoを登録します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTodo_insert(RunData rundata, Context context) throws Exception {
    ToDoFormData formData = new ToDoFormData();
    formData.initField();
    formData.loadCategoryList(rundata);
    if (formData.doInsert(this, rundata, context)) {
      // データ登録が成功したとき
      doTodo_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(
      // jsLink
      // .getPortletById(ALEipUtils.getPortlet(rundata, context).getID())
      // .addQueryData("eventSubmit_doTodo_list", "1")
      // .toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    } else {
      setTemplate(rundata, "todo-form");
    }
  }

  /**
   * ToDoを更新します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTodo_update(RunData rundata, Context context) throws Exception {
    ToDoFormData formData = new ToDoFormData();
    formData.initField();
    formData.loadCategoryList(rundata);
    if (formData.doUpdate(this, rundata, context)) {
      // データ更新が成功したとき
      doTodo_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doTodo_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    } else {
      setTemplate(rundata, "todo-form");
    }
  }

  /**
   * ToDoを削除します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTodo_delete(RunData rundata, Context context) throws Exception {
    ToDoFormData formData = new ToDoFormData();
    formData.initField();
    if (formData.doDelete(this, rundata, context)) {
      // データ削除が成功したとき
      doTodo_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doTodo_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    }
  }

  /**
   * ToDoを削除します。（複数） <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTodo_multi_delete(RunData rundata, Context context)
      throws Exception {
    ToDoMultiDelete delete = new ToDoMultiDelete();
    delete.doMultiAction(this, rundata, context);
    doTodo_list(rundata, context);
    // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
    // rundata.setRedirectURI(jsLink.getPortletById(
    // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
    // "eventSubmit_doTodo_list", "1").toString());
    // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
    // jsLink = null;
  }

  /**
   * ToDoを一覧表示します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTodo_list(RunData rundata, Context context) throws Exception {
    ToDoSelectData listData = new ToDoSelectData();
    listData.initField();
    listData.loadCategoryList(rundata);

    // デフォルトのソートカラムを設定
    ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "end_date");
    ALEipUtils.setTemp(
      rundata,
      context,
      LIST_SORT_TYPE_STR,
      ALEipConstants.LIST_SORT_TYPE_ASC);

    // PSMLからパラメータをロードする
    // 最大表示件数（最大化時）
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1b-rows")));
    listData.setStrLength(0);
    listData.loadCategoryList(rundata);
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "todo-list");
  }

  /**
   * ToDoを詳細表示します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTodo_detail(RunData rundata, Context context) throws Exception {
    ToDoSelectData detailData = new ToDoSelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      setTemplate(rundata, "todo-detail");
    } else {
      doTodo_list(rundata, context);
    }
  }

  /**
   * ToDoの状態を完了にします。（複数） <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTodo_multi_complete(RunData rundata, Context context)
      throws Exception {
    ToDoMultiStateUpdate data = new ToDoMultiStateUpdate();
    data.doMultiAction(this, rundata, context);
    doTodo_list(rundata, context);
  }

  /**
   * カテゴリ登録のフォームを表示します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTodo_category_form(RunData rundata, Context context)
      throws Exception {
    ToDoCategoryFormData formData = new ToDoCategoryFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "todo-category-form");
  }

  /**
   * カテゴリを登録します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTodo_category_insert(RunData rundata, Context context)
      throws Exception {
    ToDoCategoryFormData formData = new ToDoCategoryFormData();
    formData.initField();
    if (formData.doInsert(this, rundata, context)) {
      // データ登録に成功したとき
      doTodo_category_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doTodo_category_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    } else {
      setTemplate(rundata, "todo-category-form");
    }

  }

  /**
   * カテゴリを更新します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTodo_category_update(RunData rundata, Context context)
      throws Exception {
    ToDoCategoryFormData formData = new ToDoCategoryFormData();
    formData.initField();
    if (formData.doUpdate(this, rundata, context)) {
      // データ更新に成功したとき
      doTodo_category_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doTodo_category_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    } else {
      setTemplate(rundata, "todo-category-form");
    }
  }

  /**
   * カテゴリを削除します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTodo_category_delete(RunData rundata, Context context)
      throws Exception {
    ToDoCategoryFormData formData = new ToDoCategoryFormData();
    formData.initField();
    if (formData.doDelete(this, rundata, context)) {
      // データ削除に成功したとき
      doTodo_category_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doTodo_category_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    }
  }

  /**
   * カテゴリを削除します。（複数） <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTodo_category_multi_delete(RunData rundata, Context context)
      throws Exception {
    ToDoCategoryMultiDelete delete = new ToDoCategoryMultiDelete();
    delete.doMultiAction(this, rundata, context);
    doTodo_category_list(rundata, context);
    // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
    // rundata.setRedirectURI(jsLink.getPortletById(
    // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
    // "eventSubmit_doTodo_category_list", "1").toString());
    // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
    // jsLink = null;
  }

  /**
   * カテゴリを一覧表示します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTodo_category_list(RunData rundata, Context context)
      throws Exception {
    try {
      VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
      ToDoCategorySelectData listData = new ToDoCategorySelectData();
      listData.initField();
      // PSMLからパラメータをロードする
      // 最大表示件数（通常時）
      listData.setRowsNum(Integer.parseInt(portlet
        .getPortletConfig()
        .getInitParameter("p1c-rows")));
      listData.loadCategoryList(rundata);
      listData.doViewList(this, rundata, context);
      setTemplate(rundata, "todo-category-list");
    } catch (Exception e) {
      logger.error("[ToDoAction]", e);
    }
  }

  /**
   * カテゴリを詳細表示します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTodo_category_detail(RunData rundata, Context context)
      throws Exception {
    try {
      ToDoCategorySelectData detailData = new ToDoCategorySelectData();
      detailData.initField();
      if (detailData.doViewDetail(this, rundata, context)) {
        setTemplate(rundata, "todo-category-detail");
      } else {
        doTodo_category_list(rundata, context);
      }
      setTemplate(rundata, "todo-category-detail");
    } catch (Exception e) {
      logger.error("todo", e);
    }
  }

  /**
   * ToDoの状態を更新します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTodo_state_update(RunData rundata, Context context)
      throws Exception {
    ToDoStateUpdateData data = new ToDoStateUpdateData();
    data.initField();
    data.doUpdate(this, rundata, context);
    doTodo_list(rundata, context);
    // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
    // rundata.setRedirectURI(jsLink.getPortletById(
    // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
    // "eventSubmit_doTodo_list", "1").toString());
    // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
    // jsLink = null;
  }

  private void clearToDoSession(RunData rundata, Context context) {
    List<String> list = new ArrayList<String>();
    // エンティティIDの初期化
    list.add("entityid");
    // 選択しているタブ情報の削除
    list.add("tab");
    list.add("publictab");
    list.add("keyword");
    list.add("target_user_id");
    list.add("target_group_name");
    list.add("com.aimluck.eip.todo.ToDoSelectDatafilter");
    list.add("com.aimluck.eip.todo.ToDoSelectDatafiltertype");
    list.add("com.aimluck.eip.todo.ToDoCategorySelectDatasort");
    list.add("com.aimluck.eip.todo.ToDoCategorySelectDatasorttype");
    list.add("com.aimluck.eip.todo.ToDoPublicSelectDatasort");
    list.add("com.aimluck.eip.todo.ToDoPublicSelectDatasorttype");
    ALEipUtils.removeTemp(rundata, context, list);
  }

}
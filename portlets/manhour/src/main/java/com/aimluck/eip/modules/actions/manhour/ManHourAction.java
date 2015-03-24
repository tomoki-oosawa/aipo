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
package com.aimluck.eip.modules.actions.manhour;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.category.CommonCategoryFormData;
import com.aimluck.eip.category.CommonCategoryMultiDelete;
import com.aimluck.eip.category.CommonCategorySelectData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.manhour.ManHourSelectData;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.util.ALEipUtils;

/**
 * プロジェクト管理のアクションクラスです。 <BR>
 * 
 */
public class ManHourAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ManHourAction.class.getName());

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
    clearManHourSession(rundata, context);

    ManHourSelectData listData = new ManHourSelectData();
    listData.initField();
    listData.setNormal(true);
    if (listData.doViewList(this, rundata, context)) {
      setTemplate(rundata, "manhour");
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
      if (ALEipConstants.MODE_LIST.equals(mode)) {
        doManhour_list(rundata, context);
      } else if ("category_list".equals(mode)) {
        doManhour_category_list(rundata, context);
      } else if ("category_form".equals(mode)) {
        doManhour_category_form(rundata, context);
      } else if ("category_detail".equals(mode)) {
        doManhour_category_detail(rundata, context);
      }
      if (getMode() == null) {
        doManhour_list(rundata, context);
      }
    } catch (Exception ex) {
      logger.error("manhour", ex);
    }

  }

  /**
   * 工数を集計します。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doManhour_list(RunData rundata, Context context) throws Exception {
    ManHourSelectData listData = new ManHourSelectData();
    listData.initField();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "manhour-list");
  }

  /**
   * 共有カテゴリ登録のフォームを表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doManhour_category_form(RunData rundata, Context context)
      throws Exception {
    CommonCategoryFormData formData = new CommonCategoryFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "manhour-category-form");
  }

  /**
   * 共有カテゴリを登録します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doManhour_category_insert(RunData rundata, Context context)
      throws Exception {
    CommonCategoryFormData formData = new CommonCategoryFormData();
    formData.initField();
    if (formData.doInsert(this, rundata, context)) {
      // データ登録に成功したとき
      doManhour_category_list(rundata, context);
      // jsLink = null;
    } else {
      setTemplate(rundata, "manhour-category-form");
    }

  }

  /**
   * 共有カテゴリを更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doManhour_category_update(RunData rundata, Context context)
      throws Exception {
    CommonCategoryFormData formData = new CommonCategoryFormData();
    formData.initField();
    if (formData.doUpdate(this, rundata, context)) {
      // データ更新に成功したとき
      doManhour_category_list(rundata, context);
    } else {
      setTemplate(rundata, "manhour-category-form");
    }
  }

  /**
   * 共有カテゴリを削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doManhour_category_delete(RunData rundata, Context context)
      throws Exception {
    CommonCategoryFormData formData = new CommonCategoryFormData();
    formData.initField();
    if (formData.doDelete(this, rundata, context)) {
      // データ削除に成功したとき
      doManhour_category_list(rundata, context);
    }
  }

  /**
   * 共有カテゴリを削除します。（複数） <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doManhour_category_multi_delete(RunData rundata, Context context)
      throws Exception {
    CommonCategoryMultiDelete delete = new CommonCategoryMultiDelete();
    delete.doMultiAction(this, rundata, context);
    doManhour_category_list(rundata, context);
  }

  /**
   * 共有カテゴリを一覧表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doManhour_category_list(RunData rundata, Context context)
      throws Exception {
    CommonCategorySelectData listData = new CommonCategorySelectData();
    listData.initField();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p2a-rows")));
    listData.setStrLength(Integer.parseInt(ALEipUtils.getPortlet(
      rundata,
      context).getPortletConfig().getInitParameter("p3a-strlen")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "manhour-category-list");

  }

  /**
   * カテゴリを詳細表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doManhour_category_detail(RunData rundata, Context context)
      throws Exception {

    CommonCategorySelectData detailData = new CommonCategorySelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      setTemplate(rundata, "manhour-category-detail");
    } else {
      doManhour_category_list(rundata, context);
    }
    setTemplate(rundata, "manhour-category-detail");
  }

  private void clearManHourSession(RunData rundata, Context context) {
    List<String> list = new ArrayList<String>();
    list.add("entityid");
    list.add("category_id");
    list.add("target_group_name");
    list.add("com.aimluck.eip.category.CommonCategorySelectDatasort");
    list.add("com.aimluck.eip.category.CommonCategorySelectDatasorttype");
    ALEipUtils.removeTemp(rundata, context, list);
  }
}

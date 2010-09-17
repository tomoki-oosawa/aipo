/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2010 Aimluck,Inc.
 * http://aipostyle.com/
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

package com.aimluck.eip.modules.actions.facilities;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.facilities.FacilityFormData;
import com.aimluck.eip.facilities.FacilitySelectData;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 施設予約のアクションクラスです。 <BR>
 * 
 */
public class FacilitiesAction extends ALBaseAction {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FacilitiesAction.class.getName());

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

    if (getMode() == null) {
      doFacility_list(rundata, context);
    }
  }

  /**
   * 施設登録のフォームを表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doFacility_form(RunData rundata, Context context)
      throws Exception {
    FacilityFormData formData = new FacilityFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "facility-form");
  }

  /**
   * 施設を登録します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doFacility_insert(RunData rundata, Context context)
      throws Exception {
    FacilityFormData formData = new FacilityFormData();
    formData.initField();
    if (formData.doInsert(this, rundata, context)) {
      // データ登録が成功したとき
      // doTodo_list(rundata, context);
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doFacility_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      jsLink = null;
    } else {
      setTemplate(rundata, "facility-form");
    }
  }

  /**
   * 施設を更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doFacility_update(RunData rundata, Context context)
      throws Exception {
    FacilityFormData formData = new FacilityFormData();
    formData.initField();
    if (formData.doUpdate(this, rundata, context)) {
      // データ更新が成功したとき
      // doTodo_list(rundata, context);
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doFacility_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      jsLink = null;
    } else {
      setTemplate(rundata, "facility-form");
    }
  }

  /**
   * 施設を削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doFacility_delete(RunData rundata, Context context)
      throws Exception {
    FacilityFormData formData = new FacilityFormData();
    formData.initField();
    if (formData.doDelete(this, rundata, context)) {
      // データ削除が成功したとき
      // doTodo_list(rundata, context);
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doFacility_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      jsLink = null;
    }
  }

  /**
   * 施設を一覧表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doFacility_list(RunData rundata, Context context)
      throws Exception {
    FacilitySelectData listData = new FacilitySelectData();
    listData.initField();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "facility");
  }

  /**
   * 施設を詳細表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doFacility_detail(RunData rundata, Context context)
      throws Exception {
    FacilitySelectData detailData = new FacilitySelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      setTemplate(rundata, "facility-detail");
    } else {
      doFacility_list(rundata, context);
    }
  }

}

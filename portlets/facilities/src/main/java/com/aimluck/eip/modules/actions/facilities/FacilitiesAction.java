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
package com.aimluck.eip.modules.actions.facilities;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.facilities.FacilityChangeTurnFormData;
import com.aimluck.eip.facilities.FacilityFormData;
import com.aimluck.eip.facilities.FacilityGroupFormData;
import com.aimluck.eip.facilities.FacilityGroupSelectData;
import com.aimluck.eip.facilities.FacilitySelectData;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 設備予約のアクションクラスです。 <BR>
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
   * 設備登録のフォームを表示します。 <BR>
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
   * 設備を登録します。 <BR>
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
   * 設備を更新します。 <BR>
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
   * 設備を削除します。 <BR>
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
   * 設備を一覧表示します。 <BR>
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
   * 設備を詳細表示します。 <BR>
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

  /**
   * 設備グループ登録のフォームを表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doFacilitygroup_form(RunData rundata, Context context)
      throws Exception {
    FacilityGroupFormData formData = new FacilityGroupFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "facility-group-form");
  }

  /**
   * 設備グループを登録します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doFacilitygroup_insert(RunData rundata, Context context)
      throws Exception {
    FacilityGroupFormData formData = new FacilityGroupFormData();
    formData.initField();
    if (formData.doInsert(this, rundata, context)) {
      // データ登録が成功したとき
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doFacilitygroup_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      jsLink = null;
    } else {
      setTemplate(rundata, "facility-group-form");
    }
  }

  /**
   * 設備グループを更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doFacilitygroup_update(RunData rundata, Context context)
      throws Exception {
    FacilityGroupFormData formData = new FacilityGroupFormData();
    formData.initField();
    if (formData.doUpdate(this, rundata, context)) {
      // データ更新が成功したとき
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doFacilitygroup_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      jsLink = null;
    } else {
      setTemplate(rundata, "facility-group-form");
    }
  }

  /**
   * 設備グループを削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doFacilitygroup_delete(RunData rundata, Context context)
      throws Exception {
    FacilityGroupFormData formData = new FacilityGroupFormData();
    formData.initField();
    if (formData.doDelete(this, rundata, context)) {
      // データ削除が成功したとき
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doFacilitygroup_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      jsLink = null;
    }
  }

  /**
   * 設備グループを一覧表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doFacilitygroup_list(RunData rundata, Context context)
      throws Exception {
    FacilityGroupSelectData listData = new FacilityGroupSelectData();
    listData.initField();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "facility-group");
  }

  /**
   * 設備グループを詳細表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doFacilitygroup_detail(RunData rundata, Context context)
      throws Exception {
    FacilityGroupSelectData detailData = new FacilityGroupSelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      setTemplate(rundata, "facility-group-detail");
    } else {
      doFacility_list(rundata, context);
    }
  }

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doFacility_change_turn_form(RunData rundata, Context context)
      throws Exception {
    // 設備情報の詳細画面や編集画面からの遷移時に，
    // セッションに残る ENTITY_ID を削除する．
    ALEipUtils.removeTemp(rundata, context, ALEipConstants.ENTITY_ID);

    FacilityChangeTurnFormData formData = new FacilityChangeTurnFormData();
    formData.initField();

    if (formData.doViewForm(this, rundata, context)) {
      setTemplate(rundata, "facility-change-turn");
    } else {
      doFacility_list(rundata, context);
    }
  }

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doFacility_change_turn_update(RunData rundata, Context context)
      throws Exception {
    FacilityChangeTurnFormData formData = new FacilityChangeTurnFormData();
    formData.initField();

    if (formData.doUpdate(this, rundata, context)) {
      // データの更新に成功したとき
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doFacility_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      JetspeedLinkFactory.putInstance(jsLink);
      jsLink = null;
    } else {
      setTemplate(rundata, "facility-change-turn");
    }
  }
}

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
package com.aimluck.eip.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.cayenne.om.portlet.VEipTScheduleList;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.facilities.FacilityResultData;
import com.aimluck.eip.facilities.util.FacilitiesUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class AjaxScheduleWeeklyGroupEmptySelectData extends
    AjaxScheduleMonthlySelectData {

  /** <code>logger</code> logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AjaxScheduleWeeklyGroupEmptySelectData.class.getName());

  private String acl_feat;

  /** <code>groups</code> グループ */
  private List<ALEipGroup> groups;

  /** <code>facilityGroups</code> グループリスト */
  private List<ALEipGroup> facilityGroups;

  /** <code>selectedGroup</code> 選択されるグループ */
  private String selectedGroup;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    acl_feat = ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_SELF;
    initFacilityList(rundata);

    groups = ALEipUtils.getMyGroups(rundata);
    facilityGroups = ALEipUtils.getALEipGroups();

    selectedGroup =
      ALEipUtils
        .getPortlet(rundata, context)
        .getPortletConfig()
        .getInitParameter("p8h-cgrp");

    // 自ポートレットからのリクエストであれば、パラメータを展開しセッションに保存する。
    if (ALEipUtils.isMatch(rundata, context)) {
      if (rundata.getParameters().containsKey("view_start")) {
        ALEipUtils.setTemp(rundata, context, "view_start", rundata
          .getParameters()
          .getString("view_start"));
      }
    }

    // スーパークラスのメソッドを呼び出す。
    super.init(action, rundata, context);
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected ResultList<VEipTScheduleList> selectList(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    return new ResultList<VEipTScheduleList>();
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected VEipTScheduleList selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   * @param obj
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultData(VEipTScheduleList obj)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   * @param obj
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultDataDetail(VEipTScheduleList obj)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   * @return
   */
  @Override
  protected Attributes getColumnMap() {
    return null;
  }

  /**
   * 表示タイプを取得します。
   * 
   * @return
   */
  public String getViewtype() {
    return "calendar";
  }

  @Override
  public String getAclPortletFeature() {
    return acl_feat;
  }

  private boolean initFacilityList(RunData rundata) {
    List<Long> facilityList = null;
    String str[] = rundata.getParameters().getStrings("m_id");
    String s_item;

    List<Integer> f_list = new ArrayList<Integer>();

    int len = 0;
    if (str == null || str.length == 0) {
      return false;
    }
    len = str.length;

    for (int i = 0; i < len; i++) {
      s_item = str[i];
      if (s_item.startsWith("f")) {
        f_list.add(Integer.parseInt(s_item.substring(1)));
      }
    }

    if (f_list.size() == 0) {
      return false;
    }
    List<FacilityResultData> temp_list = new ArrayList<FacilityResultData>();
    facilityList = new ArrayList<Long>();

    SelectQuery<EipMFacility> facility_query =
      Database.query(EipMFacility.class);
    Expression exp =
      ExpressionFactory.inDbExp(EipMFacility.FACILITY_ID_PK_COLUMN, f_list);
    facility_query.setQualifier(exp);
    temp_list.addAll(FacilitiesUtils
      .getFacilitiesFromSelectQuery(facility_query));
    int tmpsize = temp_list.size();
    for (int i = 0; i < tmpsize; i++) {
      FacilityResultData facility = temp_list.get(i);
      facilityList.add(facility.getFacilityId().getValue());
    }

    if (facilityList.size() == 0 || facilityList == null) {
      return false;
    } else {
      /**
       * 設備が入っている場合は、他人のスケジュールを見る権限があるかをチェックする
       */
      acl_feat = ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_OTHER;
    }

    return true;
  }

  /**
   * グループリストを取得します。
   * 
   * @return
   */
  public List<ALEipGroup> getGroupList() {
    return groups;
  }

  /**
   * 部署マップを取得します。
   * 
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  /**
   * 部署に所属する人を取得します。
   * 
   * @param groupname
   * @return
   */
  @Deprecated
  public List<ALEipUser> getMemberListByPost(String groupname) {
    return ALEipUtils.getUsers(groupname);
  }

  /**
   * 施設のグループリストを取得します。
   * 
   * @return
   */
  public List<ALEipGroup> getFacilityGroupList() {
    return facilityGroups;
  }

  public String getFacilityName(String fname) {
    return "f;" + fname;
  }

  /**
   * @return selectedGroup
   */
  public String getSelectedGroup() {
    return selectedGroup;
  }

  /**
   * @param selectedGroup
   *          セットする selectedGroup
   */
  public void setSelectedGroup(String selectedGroup) {
    this.selectedGroup = selectedGroup;
  }
}

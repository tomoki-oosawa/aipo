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
package com.aimluck.eip.schedule;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALCellStringField;
import com.aimluck.eip.cayenne.om.portlet.EipMFacilityGroup;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.facilities.FacilityResultData;
import com.aimluck.eip.facilities.util.FacilitiesUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.util.ALEipUtils;

/**
 * スケジュールのフォームデータを管理するクラスです。
 * 
 */
public class CellScheduleFormFacilityData extends AbstractCellScheduleFormData {

  /** <code>logger</code> logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CellScheduleFormFacilityData.class.getName());

  /** <code>groups</code> グループ */
  private List<EipMFacilityGroup> facilities;

  /** <code>group</code> 選択グループ */
  private ALCellStringField selected_group;

  /** 表示行数 */
  private int rows_num = 10;

  /** 開始位置 */
  private int start;

  /** 総件数 */
  private int count;

  /** 現在のページ */
  private int current_page = 1;

  /** 総ページ数 */
  private int pages_num = 1;

  /** <code>facilityList</code> 全ての設備一覧 */
  private List<FacilityResultData> facilityAllList;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    facilityAllList = new ArrayList<FacilityResultData>();
    facilityAllList.addAll(FacilitiesUtils.getFacilityAllList());

    if (ALEipUtils.isMatch(rundata, context)) {
      // ページャ用
      if (rundata.getParameters().containsKey(ALEipConstants.LIST_START)) {
        current_page =
          rundata.getParameters().getInt(ALEipConstants.LIST_START);
      }
    }

    // 選択グループ
    selected_group.setValue(rundata.getParameters().get("selectedgroup"));

    super.init(action, rundata, context);
  }

  @Override
  public void initField() {
    selected_group = new ALCellStringField();

    super.initField();
  }

  @Override
  protected void loadCustomFormData(EipTSchedule record) {
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALDBErrorException
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) throws ALDBErrorException {
    return true;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  /**
   * 
   * @param cnt
   */
  protected void setPageParam(int cnt) {
    // 開始
    start = rows_num * (current_page - 1);
    // 総件数
    count = cnt;
    // 総ページ数
    pages_num = ((int) (Math.ceil(count / (double) rows_num)));
  }

  /**
   * 指定したグループIDのユーザーを取得します。
   * 
   * @param groupname
   * @return
   */
  public List<FacilityResultData> getFacilities(String groupId) {
    List<FacilityResultData> facilityAllList =
      new ArrayList<FacilityResultData>();
    facilityAllList.addAll(FacilitiesUtils.getFacilityAllList());

    List<FacilityResultData> facilityList = new ArrayList<FacilityResultData>();
    if ("all".equals(groupId)) {
      facilityList = facilityAllList;
    } else {
      facilityList = FacilitiesUtils.getFacilityListByGroupId(groupId);
    }

    int size = facilityList.size();
    setPageParam(size);
    int start = getStart();
    int rowsNum = this.getRowsNum();

    int count = -1;
    List<FacilityResultData> list = new ArrayList<FacilityResultData>();
    for (int i = 0; i < rowsNum; i++) {
      count = i + start;
      if (count >= size) {
        break;
      }
      list.add(facilityList.get(count));
    }

    return list;
  }

  /**
   * グループリストを取得します
   * 
   * @return
   */
  @Override
  public List<EipMFacilityGroup> getFacilityGroupList() {
    return facilities;
  }

  /**
   * 選択したグループを取得します
   * 
   * @return
   */
  public ALCellStringField getSelectedGroup() {
    return selected_group;
  }

  /**
   * 総ページ数を取得します。
   * 
   * @return
   */
  public int getPagesNum() {
    return pages_num;
  }

  /**
   * 表示する項目数を設定します。
   * 
   * @param num
   */
  public void setRowsNum(int num) {
    if (num >= 1) {
      rows_num = num;
    }
  }

  /**
   * 表示する項目数を取得します。
   * 
   * @return
   */
  public int getRowsNum() {
    return rows_num;
  }

  /**
   * 現在表示されているページを取得します。
   * 
   * @return
   */
  public int getCurrentPage() {
    return current_page;
  }

  /**
   * @return
   */
  public int getStart() {
    return start;
  }

  /**
   * 全ての設備メンバーを取得します。
   * 
   * @return
   */
  public List<FacilityResultData> getFacilityAllList() {
    return facilityAllList;
  }

  public boolean isContains(List<FacilityResultData> facilityList,
      FacilityResultData rd) {
    return FacilitiesUtils.isContains(facilityList, rd);
  }

}

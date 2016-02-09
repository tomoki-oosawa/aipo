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
package com.aimluck.eip.facilities;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.cayenne.om.portlet.EipMFacilityGroup;
import com.aimluck.eip.cayenne.om.portlet.EipMFacilityGroupMap;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.facilities.util.FacilitiesUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 設備のフォームデータを管理するクラスです。 <BR>
 *
 */
public class FacilityGroupFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FacilityGroupFormData.class.getName());

  /** 設備グループ名 */
  private ALStringField facility_group_name;

  private String facilitygroupid;

  /** 設備リスト */
  private List<Object> facilityList;

  /** 全設備リスト */
  private List<FacilityResultData> facilityAllList;

  /**
   *
   * @param action
   * @param rundata
   * @param context
   *
   *
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    facilityAllList = new ArrayList<FacilityResultData>();
    facilityAllList.addAll(FacilitiesUtils.getFacilityAllList());
    facilitygroupid =
      rundata.getParameters().getString(ALEipConstants.ENTITY_ID, "");
  }

  /**
   * 各フィールドを初期化します。 <BR>
   *
   *
   */
  @Override
  public void initField() {
    // 設備名
    facility_group_name = new ALStringField();
    facility_group_name.getFieldName();
    facility_group_name.setFieldName(ALLocalizationUtils
      .getl10n("FACILITIES_FACILITYGROUP_NAME"));
    facility_group_name.setTrim(true);

    // 設備リスト
    facilityList = new ArrayList<Object>();
  }

  /**
   * 設備の各フィールドに対する制約条件を設定します。 <BR>
   *
   *
   */
  @Override
  protected void setValidator() {
    // 設備グループ名必須項目
    facility_group_name.setNotNull(true);
    // 設備グループ名の文字数制限
    facility_group_name.limitMaxLength(50);
  }

  /**
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean res = super.setFormData(rundata, context, msgList);
    try {
      if (res) {
        String facilityIds[] =
          rundata.getParameters().getStrings("facility_to");
        if (facilityIds != null && facilityIds.length > 0) {
          SelectQuery<EipMFacility> fquery = Database.query(EipMFacility.class);
          Expression fexp =
            ExpressionFactory.inDbExp(
              EipMFacility.FACILITY_ID_PK_COLUMN,
              facilityIds);
          fquery.setQualifier(fexp);
          List<EipMFacility> facilities = fquery.fetchList();
          for (EipMFacility facility : facilities) {
            FacilityResultData rd = new FacilityResultData();
            rd.initField();
            rd.setFacilityId(facility.getFacilityId().longValue());
            rd.setFacilityName(facility.getFacilityName());
            facilityList.add(rd);
          }
        }
      }
    } catch (Exception ex) {
      logger.error("facilities", ex);
      res = false;
    }
    return res;
  }

  /**
   * 設備のフォームに入力されたデータの妥当性検証を行います。 <BR>
   *
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   *
   */
  @Override
  protected boolean validate(List<String> msgList) {
    try {
      SelectQuery<EipMFacilityGroup> query =
        Database.query(EipMFacilityGroup.class);
      if (ALEipConstants.MODE_INSERT.equals(getMode())) {
        Expression exp =
          ExpressionFactory.matchExp(
            EipMFacilityGroup.GROUP_NAME_PROPERTY,
            facility_group_name.getValue());
        query.setQualifier(exp);
      } else if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
        Expression exp1 =
          ExpressionFactory.matchExp(
            EipMFacilityGroup.GROUP_NAME_PROPERTY,
            facility_group_name.getValue());
        query.setQualifier(exp1);
        Expression exp2 =
          ExpressionFactory.noMatchDbExp(
            EipMFacilityGroup.GROUP_ID_PK_COLUMN,
            Integer.valueOf(facilitygroupid));
        query.andQualifier(exp2);
      }

      if (query.fetchList().size() != 0) {
        msgList.add(ALLocalizationUtils.getl10nFormat(
          "FACILITIES_FACILITYGROUP_IS_ALREADY_ADDED",
          facility_group_name.toString()));

      }
    } catch (Exception ex) {
      logger.error("facilities", ex);
      return false;
    }

    // 設備グループ名
    facility_group_name.validate(msgList);
    return (msgList.size() == 0);

  }

  /**
   * 設備グループをデータベースから読み出します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipMFacilityGroup facility =
        FacilitiesUtils.getEipMFacilityGroup(rundata, context);
      if (facility == null) {
        return false;
      }
      // 設備グループ名
      facility_group_name.setValue(facility.getGroupName());

      // facilitygroupのマップからその設備グループの設備リスト取得
      SelectQuery<EipMFacilityGroupMap> mapquery =
        Database.query(EipMFacilityGroupMap.class);
      Expression mapexp =
        ExpressionFactory.matchExp(
          EipMFacilityGroupMap.GROUP_ID_PROPERTY,
          facility.getGroupId());
      mapquery.setQualifier(mapexp);
      List<EipMFacilityGroupMap> FacilityMaps = mapquery.fetchList();
      List<Integer> facilityIds = new ArrayList<Integer>();
      for (EipMFacilityGroupMap map : FacilityMaps) {
        facilityIds.add(map.getEipMFacilityFacilityId().getFacilityId());
      }
      if (facilityIds.size() > 0) {
        SelectQuery<EipMFacility> fquery = Database.query(EipMFacility.class);
        Expression fexp =
          ExpressionFactory.inDbExp(
            EipMFacility.FACILITY_ID_PK_COLUMN,
            facilityIds);
        fquery.setQualifier(fexp);
        facilityList.addAll(FacilitiesUtils
          .getFacilitiesFromSelectQuery(fquery));
      }
    } catch (Exception ex) {
      logger.error("facilities", ex);
      return false;
    }
    return true;
  }

  /**
   * 設備グループをデータベースから削除します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipMFacilityGroup facility =
        FacilitiesUtils.getEipMFacilityGroup(rundata, context);
      if (facility == null) {
        return false;
      }
      SelectQuery<EipMFacilityGroupMap> fmaps =
        Database.query(EipMFacilityGroupMap.class);
      Expression fexp =
        ExpressionFactory.matchExp(
          EipMFacilityGroupMap.GROUP_ID_PROPERTY,
          facility.getGroupId());
      fmaps.setQualifier(fexp);
      fmaps.deleteAll();
      // 設備グループを削除
      Database.delete(facility);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        facility.getGroupId(),
        ALEventlogConstants.PORTLET_TYPE_FACILITY,
        ALLocalizationUtils.getl10nFormat(
          "FACILITIES_DELETED_FACILITYGROUP_WITH_NAME",
          facility.getGroupName()));

      // orm.doDelete(facility);
    } catch (Exception ex) {
      Database.rollback();
      logger.error("facilities", ex);
      return false;
    }
    return true;
  }

  /**
   * 設備グループをデータベースに格納します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // 設備グループの順番を調整
      int lastnum = 0;
      StringBuffer statement = new StringBuffer();
      statement
        .append("SELECT MAX(sort) as max_sort FROM eip_m_facility_group");
      String querydata = statement.toString();
      List<DataRow> maxnum =
        Database.sql(EipMFacilityGroup.class, querydata).fetchListAsDataRow();
      if (maxnum != null && maxnum.size() > 0) {
        Integer maxnum2 = (Integer) maxnum.get(0).get("max_sort");
        if (maxnum2 != null) {
          lastnum = maxnum2;
        }
      }
      // 最大のソートナンバーの後ろに振られていないデータを追加
      Expression exp2 =
        ExpressionFactory.matchExp(EipMFacilityGroup.SORT_PROPERTY, null);
      SelectQuery<EipMFacilityGroup> querynotsort =
        Database.query(EipMFacilityGroup.class);
      querynotsort.orderAscending(EipMFacilityGroup.SORT_PROPERTY);
      querynotsort.setQualifier(exp2);
      List<EipMFacilityGroup> group_notsort_list = querynotsort.fetchList();
      for (EipMFacilityGroup group_data2 : group_notsort_list) {
        group_data2.setSort(++lastnum);
      }

      // 新規オブジェクトモデル
      EipMFacilityGroup facilitygroup =
        Database.create(EipMFacilityGroup.class);
      rundata.getParameters().setProperties(facilitygroup);
      // 設備グループ名
      facilitygroup.setGroupName(facility_group_name.getValue());
      facilitygroup.setSort(++lastnum);

      for (Object record : facilityList) {
        FacilityResultData frd = (FacilityResultData) record;

        EipMFacilityGroupMap map = Database.create(EipMFacilityGroupMap.class);
        map.setFacilityId((int) frd.getFacilityId().getValue());
        map.setEipMFacilityGroupId(facilitygroup);
      }
      // 設備を登録
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        facilitygroup.getGroupId(),
        ALEventlogConstants.PORTLET_TYPE_FACILITY,
        ALLocalizationUtils.getl10nFormat(
          "FACILITIES_ADDED_FACILITYGROUP_WITH_NAME",
          facilitygroup.getGroupName()));
    } catch (Exception ex) {
      Database.rollback();
      logger.error("facilities", ex);
      return false;
    }
    return true;
  }

  /**
   * データベースに格納されている設備を更新します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipMFacilityGroup facilityGroup =
        FacilitiesUtils.getEipMFacilityGroup(rundata, context);
      if (facilityGroup == null) {
        return false;
      }
      SelectQuery<EipMFacilityGroupMap> fmaps =
        Database.query(EipMFacilityGroupMap.class);
      Expression fexp =
        ExpressionFactory.matchExp(
          EipMFacilityGroupMap.GROUP_ID_PROPERTY,
          facilityGroup.getGroupId());
      fmaps.setQualifier(fexp);
      List<EipMFacilityGroupMap> oldMapList = fmaps.fetchList();
      List<EipMFacilityGroupMap> newMapList =
        new ArrayList<EipMFacilityGroupMap>();
      for (Object facility : facilityList) {
        FacilityResultData frd = (FacilityResultData) facility;
        EipMFacilityGroupMap map = new EipMFacilityGroupMap();
        map.setFacilityId((int) frd.getFacilityId().getValue());
        map.setGroupId(facilityGroup.getGroupId());
        newMapList.add(map);
      }

      // oldlistのdelete
      List<Integer> oldMapIdList = new ArrayList<Integer>();
      if (oldMapList.size() > 0) {
        for (EipMFacilityGroupMap map : oldMapList) {
          oldMapIdList.add(map.getId());
        }
        SelectQuery<EipMFacilityGroupMap> remove =
          Database.query(EipMFacilityGroupMap.class);
        remove.where(Operations.and(Operations.eq(
          EipMFacilityGroupMap.GROUP_ID_PROPERTY,
          facilityGroup.getGroupId()), Operations.in(
          EipMFacilityGroupMap.FACILITY_ID_PROPERTY,
          oldMapIdList)));
        remove.deleteAll();
      }
      // newlistのinsert
      for (EipMFacilityGroupMap map : newMapList) {
        EipMFacilityGroupMap insert =
          Database.create(EipMFacilityGroupMap.class);
        insert.setFacilityId(map.getFacilityId());
        insert.setGroupId(facilityGroup.getGroupId());
      }
      // 設備グループ名
      facilityGroup.setGroupName(facility_group_name.getValue());
      // 設備を更新
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        facilityGroup.getGroupId(),
        ALEventlogConstants.PORTLET_TYPE_FACILITY,
        ALLocalizationUtils.getl10nFormat(
          "FACILITIES_UPDATED_FACILITYGROUP_WITH_NAME",
          facilityGroup.getGroupName()));
    } catch (Exception ex) {
      Database.rollback();
      logger.error("facilities", ex);
      return false;
    }
    return true;
  }

  /**
   * 設備グループ名を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getFacilityGroupName() {
    return facility_group_name;
  }

  /**
   * 設備グループIdを取得します。 <BR>
   *
   * @return
   */
  public String getFacilityGroupId() {
    return facilitygroupid;
  }

  /** 設備リストを取得します。 */
  public List<FacilityResultData> getFacilityList() {
    List<FacilityResultData> _facilityList =
      new ArrayList<FacilityResultData>();
    for (Object facility : facilityList) {
      _facilityList.add((FacilityResultData) facility);
    }
    return _facilityList;
  }
}

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
import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipFacilityGroup;
import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.cayenne.om.portlet.EipMFacilityGroup;
import com.aimluck.eip.cayenne.om.portlet.EipMFacilityGroupMap;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
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
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 設備のフォームデータを管理するクラスです。 <BR>
 * 
 */
public class FacilityFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FacilityFormData.class.getName());

  /** 設備名 */
  private ALStringField facility_name;

  /** メモ */
  private ALStringField note;

  private String facilityid;

  private int userId;

  private List<EipMFacilityGroup> facility_group_list;

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

    userId = ALEipUtils.getUserId(rundata);
    facility_group_list = new ArrayList<EipMFacilityGroup>();
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  @Override
  public void initField() {
    // 設備名
    facility_name = new ALStringField();
    facility_name.setFieldName(ALLocalizationUtils
      .getl10n("FACILITIES_FACILITY_NAME"));
    facility_name.setTrim(true);
    // メモ
    note = new ALStringField();
    note.setFieldName(ALLocalizationUtils.getl10n("FACILITIES_MEMO"));
    note.setTrim(false);
  }

  /**
   * 設備の各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // 設備名必須項目
    facility_name.setNotNull(true);
    // 設備名の文字数制限
    facility_name.limitMaxLength(50);
    // メモの文字数制限
    note.limitMaxLength(1000);
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
        if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
          facilityid =
            ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
        }
        String groupIds[] = rundata.getParameters().getStrings("group_to");
        if (groupIds != null && groupIds.length > 0) {
          SelectQuery<EipMFacilityGroup> fquery =
            Database.query(EipMFacilityGroup.class);
          Expression fexp =
            ExpressionFactory.inDbExp(
              EipMFacilityGroup.GROUP_ID_PK_COLUMN,
              groupIds);
          fquery.setQualifier(fexp);
          facility_group_list = fquery.fetchList();
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
      SelectQuery<EipMFacility> query = Database.query(EipMFacility.class);
      if (ALEipConstants.MODE_INSERT.equals(getMode())) {
        Expression exp =
          ExpressionFactory.matchExp(
            EipMFacility.FACILITY_NAME_PROPERTY,
            facility_name.getValue());
        query.setQualifier(exp);
      } else if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
        Expression exp1 =
          ExpressionFactory.matchExp(
            EipMFacility.FACILITY_NAME_PROPERTY,
            facility_name.getValue());
        query.setQualifier(exp1);
        Expression exp2 =
          ExpressionFactory.noMatchDbExp(
            EipMFacility.FACILITY_ID_PK_COLUMN,
            Integer.valueOf(facilityid));
        query.andQualifier(exp2);
      }

      if (query.fetchList().size() != 0) {
        msgList.add(ALLocalizationUtils.getl10nFormat(
          "FACILITIES_FACILITY_IS_ALREADY_ADDED",
          facility_name.toString()));
      }
    } catch (Exception ex) {
      logger.error("facilities", ex);
      return false;
    }

    // 設備名
    facility_name.validate(msgList);
    // メモ
    note.validate(msgList);
    return (msgList.size() == 0);

  }

  /**
   * 設備をデータベースから読み出します。 <BR>
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
      EipMFacility facility = FacilitiesUtils.getEipMFacility(rundata, context);
      if (facility == null) {
        return false;
      }
      // 設備名
      facility_name.setValue(facility.getFacilityName());
      // メモ
      note.setValue(facility.getNote());

      // 設備グループリスト
      SelectQuery<EipMFacilityGroupMap> query =
        Database.query(EipMFacilityGroupMap.class);
      query.where(Operations.eq(
        EipMFacilityGroupMap.FACILITY_ID_PROPERTY,
        facility.getFacilityId()));
      List<EipMFacilityGroupMap> maps = query.fetchList();
      List<Integer> faclityGroupIdList = new ArrayList<Integer>();
      for (EipMFacilityGroupMap map : maps) {
        faclityGroupIdList.add(map.getGroupId());
      }

      if (faclityGroupIdList.isEmpty()) {
        // for empty
        facility_group_list = new ArrayList<EipMFacilityGroup>(0);
      } else {
        SelectQuery<EipMFacilityGroup> fquery =
          Database.query(EipMFacilityGroup.class);
        Expression exp =
          ExpressionFactory.inDbExp(
            EipMFacilityGroup.GROUP_ID_PK_COLUMN,
            faclityGroupIdList);
        fquery.setQualifier(exp);
        facility_group_list = fquery.fetchList();
      }

    } catch (Exception ex) {
      logger.error("facilities", ex);
      return false;
    }
    return true;
  }

  /**
   * 設備をデータベースから削除します。 <BR>
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
      EipMFacility facility = FacilitiesUtils.getEipMFacility(rundata, context);
      if (facility == null) {
        return false;
      }

      SelectQuery<EipTScheduleMap> query1 =
        Database.query(EipTScheduleMap.class);
      Expression exp1 =
        ExpressionFactory.matchExp(EipTScheduleMap.USER_ID_PROPERTY, facility
          .getFacilityId());
      Expression exp2 =
        ExpressionFactory.matchExp(EipTScheduleMap.TYPE_PROPERTY, "F");
      query1.setQualifier(exp1.andExp(exp2));

      List<EipTScheduleMap> slist = query1.fetchList();
      if (slist != null && slist.size() > 0) {
        // 設備のスケジュールを削除
        Database.deleteAll(slist);
      }

      SelectQuery<EipMFacilityGroupMap> fmaps =
        Database.query(EipMFacilityGroupMap.class);
      Expression fexp =
        ExpressionFactory.matchExp(
          EipMFacilityGroupMap.FACILITY_ID_PROPERTY,
          facility.getFacilityId());
      fmaps.setQualifier(fexp);
      // マップ削除
      fmaps.deleteAll();

      // 設備を削除
      Database.delete(facility);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        facility.getFacilityId(),
        ALEventlogConstants.PORTLET_TYPE_FACILITY,
        ALLocalizationUtils.getl10nFormat(
          "FACILITIES_DELETED_FACILITY_WITH_NAME",
          facility.getFacilityName()));

      // orm.doDelete(facility);
    } catch (Exception ex) {
      Database.rollback();
      logger.error("facilities", ex);
      return false;
    }
    return true;
  }

  /**
   * 設備をデータベースに格納します。 <BR>
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
      // 設備の順番を調整
      int lastnum = 0;
      StringBuffer statement = new StringBuffer();
      statement.append("SELECT MAX(sort) as max_sort FROM eip_m_facility");
      String querydata = statement.toString();
      List<DataRow> maxnum =
        Database.sql(EipMFacility.class, querydata).fetchListAsDataRow();
      if (maxnum != null && maxnum.size() > 0) {
        Integer maxnum2 = (Integer) maxnum.get(0).get("max_sort");
        if (maxnum2 != null) {
          lastnum = maxnum2;
        }
      }
      // 最大のソートナンバーの後ろに振られていないデータを追加
      Expression exp2 =
        ExpressionFactory.matchExp(EipMFacility.SORT_PROPERTY, null);
      SelectQuery<EipMFacility> querynotsort =
        Database.query(EipMFacility.class);
      querynotsort.orderAscending(EipMFacility.UPDATE_DATE_PROPERTY);
      querynotsort.setQualifier(exp2);
      List<EipMFacility> facility_notsort_list = querynotsort.fetchList();
      for (EipMFacility facilitydata2 : facility_notsort_list) {
        facilitydata2.setSort(++lastnum);
      }

      // 新規オブジェクトモデル
      EipMFacility facility = Database.create(EipMFacility.class);
      // ユーザID
      facility.setUserId(Integer.valueOf(userId));
      // ソートnum
      facility.setSort(++lastnum);
      // 設備名
      facility.setFacilityName(facility_name.getValue());
      // メモ
      facility.setNote(note.getValue());
      // 作成日
      facility.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      facility.setUpdateDate(Calendar.getInstance().getTime());
      Group facility_group = JetspeedSecurity.getGroup("Facility");
      EipFacilityGroup fg = Database.create(EipFacilityGroup.class);
      fg.setEipMFacility(facility);
      fg.setTurbineGroup((TurbineGroup) facility_group);

      // マップにセット
      for (EipMFacilityGroup group : facility_group_list) {
        EipMFacilityGroupMap map = Database.create(EipMFacilityGroupMap.class);
        map.setEipMFacilityFacilityId(facility);
        map.setEipMFacilityGroupId(group);
      }

      // 設備を登録
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        facility.getFacilityId(),
        ALEventlogConstants.PORTLET_TYPE_FACILITY,
        ALLocalizationUtils.getl10nFormat(
          "FACILITIES_ADDED_FACILITY_WITH_NAME",
          facility.getFacilityName()));

      // ACL
      // EipTAclMap scheduleAcl = Database.create(EipTAclMap.class);
      // scheduleAcl.setFeature("schedule");
      // scheduleAcl.setTargetId(facility.getFacilityId());
      // scheduleAcl.setTargetType("f");
      // scheduleAcl.setId(2);
      // scheduleAcl.setType("ug");
      // scheduleAcl.setLevel(2);

      // Database.commit();

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
      EipMFacility facility = FacilitiesUtils.getEipMFacility(rundata, context);
      if (facility == null) {
        return false;
      }

      // 設備名
      facility.setFacilityName(facility_name.getValue());
      // メモ
      facility.setNote(note.getValue());
      // 更新日
      facility.setUpdateDate(Calendar.getInstance().getTime());

      // マップ
      SelectQuery<EipMFacilityGroupMap> fmaps =
        Database.query(EipMFacilityGroupMap.class);
      Expression fexp =
        ExpressionFactory.matchExp(
          EipMFacilityGroupMap.FACILITY_ID_PROPERTY,
          facility.getFacilityId());
      fmaps.setQualifier(fexp);
      List<EipMFacilityGroupMap> oldMapList = fmaps.fetchList();
      List<EipMFacilityGroupMap> newMapList =
        new ArrayList<EipMFacilityGroupMap>();
      if (facility_group_list != null) {
        for (EipMFacilityGroup group : facility_group_list) {
          EipMFacilityGroupMap map = new EipMFacilityGroupMap();
          map.setFacilityId(facility.getFacilityId());
          map.setGroupId(group.getGroupId());
          newMapList.add(map);
        }
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
          EipMFacilityGroupMap.FACILITY_ID_PROPERTY,
          facility.getFacilityId()), Operations.in(
          EipMFacilityGroupMap.GROUP_ID_PROPERTY,
          oldMapIdList)));
        remove.deleteAll();
      }
      // newlistのinsert
      for (EipMFacilityGroupMap map : newMapList) {
        EipMFacilityGroupMap insert =
          Database.create(EipMFacilityGroupMap.class);
        insert.setFacilityId(map.getFacilityId());
        insert.setGroupId(map.getGroupId());
      }

      // 設備を更新
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        facility.getFacilityId(),
        ALEventlogConstants.PORTLET_TYPE_FACILITY,
        ALLocalizationUtils.getl10nFormat(
          "FACILITIES_UPDATED_FACILITY_WITH_NAME",
          facility.getFacilityName()));
    } catch (Exception ex) {
      Database.rollback();
      logger.error("facilities", ex);
      return false;
    }
    return true;
  }

  /**
   * メモを取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getNote() {
    return note;
  }

  /**
   * 設備名を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getFacilityName() {
    return facility_name;
  }

  public List<EipMFacilityGroup> getFacilityGroupList() {
    return facility_group_list;
  }

}

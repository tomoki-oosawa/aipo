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
package com.aimluck.eip.facilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipFacilityGroup;
import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.facilities.util.FacilitiesUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 施設のフォームデータを管理するクラスです。 <BR>
 * 
 */
public class FacilityFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(FacilityFormData.class.getName());

  /** 施設名 */
  private ALStringField facility_name;

  /** メモ */
  private ALStringField note;

  private String facilityid;

  private int userId;

  private DataContext dataContext;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * @see com.aimluck.eip.common.ALAbstractFormData#init(com.aimluck.eip.modules.actions.common.ALAction,
   *      org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
   */
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    dataContext = DatabaseOrmService.getInstance().getDataContext();

    userId = ALEipUtils.getUserId(rundata);
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * @see com.aimluck.eip.common.ALData#initField()
   */
  public void initField() {
    // 施設名
    facility_name = new ALStringField();
    facility_name.setFieldName("施設名");
    facility_name.setTrim(true);
    // メモ
    note = new ALStringField();
    note.setFieldName("メモ");
    note.setTrim(false);
  }

  /**
   * 施設の各フィールドに対する制約条件を設定します。 <BR>
   * 
   * @see com.aimluck.eip.common.ALAbstractFormData#setValidator()
   */
  protected void setValidator() {
    // 施設名必須項目
    facility_name.setNotNull(true);
    // 施設名の文字数制限
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
   * @see com.aimluck.eip.common.ALAbstractFormData#setFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean res = super.setFormData(rundata, context, msgList);
    try {
      if (res) {
        if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
          facilityid = ALEipUtils.getTemp(rundata, context,
              ALEipConstants.ENTITY_ID);
        }
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      res = false;
    }
    return res;
  }

  /**
   * 施設のフォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * @see com.aimluck.eip.common.ALAbstractFormData#validate(java.util.ArrayList)
   */
  protected boolean validate(List<String> msgList) {
    try {
      SelectQuery query = new SelectQuery(EipMFacility.class);
      if (ALEipConstants.MODE_INSERT.equals(getMode())) {
        Expression exp = ExpressionFactory.matchExp(
            EipMFacility.FACILITY_NAME_PROPERTY, facility_name.getValue());
        query.setQualifier(exp);
      } else if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
        Expression exp1 = ExpressionFactory.matchExp(
            EipMFacility.FACILITY_NAME_PROPERTY, facility_name.getValue());
        query.setQualifier(exp1);
        Expression exp2 = ExpressionFactory.noMatchDbExp(
            EipMFacility.FACILITY_ID_PK_COLUMN, Integer.valueOf(facilityid));
        query.andQualifier(exp2);
      }

      if (dataContext.performQuery(query).size() != 0) {
        msgList.add("施設名『 <span class='em'>" + facility_name.toString()
            + "</span> 』は既に登録されています。");
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }

    // 施設名
    facility_name.validate(msgList);
    // メモ
    note.validate(msgList);
    return (msgList.size() == 0);

  }

  /**
   * 施設をデータベースから読み出します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * @see com.aimluck.eip.common.ALAbstractFormData#loadFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipMFacility facility = FacilitiesUtils.getEipMFacility(rundata, context);
      if (facility == null)
        return false;
      // 施設名
      facility_name.setValue(facility.getFacilityName());
      // メモ
      note.setValue(facility.getNote());
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 施設をデータベースから削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * @see com.aimluck.eip.common.ALAbstractFormData#deleteFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipMFacility facility = FacilitiesUtils.getEipMFacility(rundata, context);
      if (facility == null)
        return false;

      SelectQuery query1 = new SelectQuery(EipTScheduleMap.class);
      Expression exp1 = ExpressionFactory.matchExp(
          EipTScheduleMap.USER_ID_PROPERTY, facility.getFacilityId());
      Expression exp2 = ExpressionFactory.matchExp(
          EipTScheduleMap.TYPE_PROPERTY, "F");
      query1.setQualifier(exp1.andExp(exp2));

      List slist = dataContext.performQuery(query1);
      if (slist != null && slist.size() > 0) {
        // 施設のスケジュールを削除
        dataContext.deleteObjects(slist);
      }
      // 施設を削除
      dataContext.deleteObject(facility);
      dataContext.commitChanges();
      // orm.doDelete(facility);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 施設をデータベースに格納します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * @see com.aimluck.eip.common.ALAbstractFormData#insertFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // 新規オブジェクトモデル
      EipMFacility facility = (EipMFacility) dataContext
          .createAndRegisterNewObject(EipMFacility.class);
      // ユーザID
      facility.setUserId(Integer.valueOf(userId));
      // 施設名
      facility.setFacilityName(facility_name.getValue());
      // メモ
      facility.setNote(note.getValue());
      // 作成日
      facility.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      facility.setUpdateDate(Calendar.getInstance().getTime());

      Group facility_group = JetspeedSecurity.getGroup("Facility");
      EipFacilityGroup fg = (EipFacilityGroup) dataContext
          .createAndRegisterNewObject(EipFacilityGroup.class);
      fg.setEipMFacility(facility);
      fg.setTurbineGroup((TurbineGroup) facility_group);

      // 施設を登録
      dataContext.commitChanges();
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * データベースに格納されている施設を更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * @see com.aimluck.eip.common.ALAbstractFormData#updateFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipMFacility facility = FacilitiesUtils.getEipMFacility(rundata, context);
      if (facility == null)
        return false;

      // 施設名
      facility.setFacilityName(facility_name.getValue());
      // メモ
      facility.setNote(note.getValue());
      // 更新日
      facility.setUpdateDate(Calendar.getInstance().getTime());

      // 施設を更新
      dataContext.commitChanges();
    } catch (Exception ex) {
      logger.error("Exception", ex);
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
   * 施設名を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getFacilityName() {
    return facility_name;
  }

}

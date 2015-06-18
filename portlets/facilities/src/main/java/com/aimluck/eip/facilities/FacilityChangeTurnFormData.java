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
package com.aimluck.eip.facilities;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.facilities.util.FacilitiesUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 設備の順番情報のフォームデータを管理するためのクラスです。 <br />
 */
public class FacilityChangeTurnFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FacilityChangeTurnFormData.class.getName());

  // 設備名のリスト　ソート後
  private ALStringField positions;

  private String[] facilityIds = null;

  /** 設備情報のリスト */
  private List<FacilityResultData> facilityList = null;

  private List<EipMFacility> rawFacilityList = null;

  /**
   * 初期化します。
   * 
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    facilityList = new ArrayList<FacilityResultData>();
    rawFacilityList = new ArrayList<EipMFacility>();
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  @Override
  public void initField() {
    // ユーザ名のリスト
    positions = new ALStringField();
    positions.setFieldName(ALLocalizationUtils
      .getl10n("FACILITIES_FACILITY_NAME_LIST"));
    positions.setTrim(true);
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
    boolean res = true;
    try {
      res = super.setFormData(rundata, context, msgList);
      if (res) {
        if (positions.getValue() == null || positions.getValue().equals("")) {// 初期
          SelectQuery<EipMFacility> query = Database.query(EipMFacility.class);
          query.orderAscending(EipMFacility.SORT_PROPERTY);
          facilityList =
            FacilitiesUtils.getFacilityResultList(query.fetchList());
        } else {// データ送信時
          StringTokenizer st = new StringTokenizer(positions.getValue(), ",");
          facilityIds = new String[st.countTokens()];
          int count = 0;
          while (st.hasMoreTokens()) {
            facilityIds[count] = st.nextToken();
            count++;
          }
          SelectQuery<EipMFacility> query = Database.query(EipMFacility.class);
          List<EipMFacility> list = query.fetchList();

          for (int i = 0; i < facilityIds.length; i++) {
            EipMFacility facility =
              getEipMFacilityFromFacilityId(list, facilityIds[i]);
            facilityList.add(FacilitiesUtils.getFacilityResultData(facility));
            rawFacilityList.add(facility);
          }
        }
      }
    } catch (Exception ex) {
      logger.error("facilities", ex);
      return false;
    }
    return res;
  }

  /**
   * 各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
  }

  /**
   * フォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return
   */
  @Override
  protected boolean validate(List<String> msgList) {
    return (msgList.size() == 0);
  }

  /**
   * 『設備』を読み込みます。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      return true;
    } catch (Exception e) {
      logger.error("facilities", e);
      return false;
    }
  }

  /**
   * 『設備』を追加します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 『設備』を更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    boolean res = true;
    try {
      int newPosition = 1;
      for (EipMFacility facility : rawFacilityList) {
        facility.setSort(newPosition);
        newPosition++;
      }
      Database.commit();
    } catch (Exception e) {
      Database.rollback();
      logger.error("facilities", e);
      res = false;
    }
    return res;
  }

  /**
   * 『設備』を削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 指定したchar型文字が記号であるかを判断します。
   * 
   * @param ch
   * @return
   */
  protected boolean isSymbol(char ch) {
    byte[] chars;

    try {
      chars = (Character.valueOf(ch).toString()).getBytes("shift_jis");
    } catch (UnsupportedEncodingException ex) {
      return false;
    }

    if (chars == null
      || chars.length == 2
      || Character.isDigit(ch)
      || Character.isLetter(ch)) {
      return false;
    } else {
      return true;
    }

  }

  /**
   * 指定した設備IDのオブジェクトを取得する．
   * 
   * @param userList
   * @param userName
   * @return
   */
  private EipMFacility getEipMFacilityFromFacilityId(
      List<EipMFacility> facilityList, String facilityId) {
    for (int i = 0; i < facilityList.size(); i++) {
      EipMFacility facility = facilityList.get(i);
      if (facility.getFacilityId().toString().equals(facilityId)) {
        return facility;
      }
    }
    return null;
  }

  /**
   * ユーザ情報のリストを取得する．
   * 
   * @return
   */
  public List<FacilityResultData> getFacilityList() {
    return facilityList;
  }

}

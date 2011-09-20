/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;

/**
 * 施設の順番情報のフォームデータを管理するためのクラスです。 <br />
 */
public class FacilityChangeTurnFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FacilityChangeTurnFormData.class.getName());

  // 施設名のリスト　ソート後
  private ALStringField positions;

  private String[] facilityIds = null;

  /** 施設情報のリスト */
  private List<EipMFacility> facilityList = null;

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

    facilityList = new ArrayList<EipMFacility>();
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
    positions.setFieldName("施設名リスト");
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
          facilityList = query.fetchList();
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
            facilityList.add(facility);
          }
        }
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
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
    // if (positions.getValue() != null && (!positions.getValue().equals(""))) {
    // 受信したユーザ ID の検証
    // StringTokenizer st = new StringTokenizer(positions.getValue(), ",");
    // ALStringField field = null;
    // while (st.hasMoreTokens()) {
    // field = new ALStringField();
    // field.setTrim(true);
    // field.setValue(st.nextToken());
    // field.limitMaxLength(16); // ユーザ名の最大文字数が 16 文字．クラス AccountFormData を参照．
    // field.setCharacterType(ALStringField.TYPE_ASCII);
    // field.validate(msgList);
    // String unameValue = field.getValue();
    // int length = unameValue.length();
    // for (int i1 = 0; i1 < length; i1++) {
    // if (isSymbol(unameValue.charAt(i1))) {
    // // 使用されているのが妥当な記号であるかの確認
    // if (!(unameValue.charAt(i1) == "_".charAt(0)
    // || unameValue.charAt(i1) == "-".charAt(0) || unameValue
    // .charAt(i1) == ".".charAt(0))) {
    // msgList
    // .add("『 <span class='em'>ログイン名</span> 』に使用できる記号は「-」「.」「_」のみです。");
    // break;
    // }
    // }
    // }
    // }
    // }
    return (msgList.size() == 0);
  }

  /**
   * 『施設』を読み込みます。 <BR>
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
      logger.error("Exception", e);
      return false;
    }
  }

  /**
   * 『施設』を追加します。 <BR>
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
   * 『施設』を更新します。 <BR>
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
      for (EipMFacility facility : facilityList) {
        facility.setSort(newPosition);
        newPosition++;
      }
      Database.commit();
    } catch (Exception e) {
      Database.rollback();
      logger.error("Exception", e);
      res = false;
    }
    return res;
  }

  /**
   * 『施設』を削除します。 <BR>
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
   * 指定した施設IDのオブジェクトを取得する．
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
  public List<EipMFacility> getFacilityList() {
    return facilityList;
  }

}

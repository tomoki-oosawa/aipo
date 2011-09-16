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

package com.aimluck.eip.fileio;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.cayenne.om.portlet.EipMFacilityGroup;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;

/**
 * 『施設』のフォームデータを管理するクラスです。 <BR>
 * 
 */
public class FileIOFacilityCsvFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileIOFacilityCsvFormData.class.getName());

  /** ブラウザに表示するデフォルトのパスワード（ダミーパスワード） */
  // public static final String DEFAULT_VIEW_PASSWORD = "*";

  /** 施設名 */
  private ALStringField facility_name;

  /** 施設メモ */
  private ALStringField note;

  /** 施設グループ */
  private ALStringField facility_group;

  /** 施設グループがデータベースに存在するか否か */
  private boolean facility_group_exist;

  private final boolean isSkipfacility_nameValidation = false;

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  @Override
  public void initField() {
    // 施設名
    facility_name = new ALStringField();
    facility_name.setFieldName("施設名");
    facility_name.setTrim(true);
    // メモ
    note = new ALStringField();
    note.setFieldName("メモ");
    note.setTrim(true);
    // 施設グループ
    facility_group = new ALStringField();
    facility_group.setFieldName("施設グループ");
    facility_group.setTrim(true);
  }

  /**
   * 各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // 施設名
    facility_name.setNotNull(true);
    // facility_name.setCharacterType(ALStringField.TYPE_ASCII);
    facility_name.limitMaxLength(50);

    note.limitMaxLength(1000);

    facility_group.limitMaxLength(200);

  }

  /**
   * フォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {
    if (!isSkipfacility_nameValidation) {
      String facilitynamestr = facility_name.getValue(); // 施設名にバリデートは必要か todo
    }
    return (msgList.size() == 0);
  }

  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return super.setFormData(rundata, context, msgList);
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
    return false;
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
    boolean res = false;
    try {
      // todo
      /*
       * List<ALEipFacility> facilityList = new ArrayList<ALEipFacility>();
       * facilityList
       * .add(ALEipUtils.getALEipFacility(facility_name.getValue()));
       * 
       * // 新規オブジェクトモデル EipTSchedule schedule =
       * Database.create(EipTSchedule.class); // 親スケジュール ID
       * schedule.setParentId(Integer.valueOf(1)); // 予定
       * schedule.setName(schedulename.getValue()); // 場所
       * schedule.setPlace(place.getValue()); // 内容
       * schedule.setNote(note.getValue());
       */
      res = true;
    } catch (Exception e) {
      Database.rollback();
      logger.error("Exception", e);
    }

    return res;
  }

  // end point of insertFormData

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
    return false;
  }

  /**
   * 『ユーザー』を削除します。 <BR>
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
   * 施設名を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getFacilityName() {
    return facility_name;
  }

  /**
   * 施設グループ名を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getFacilityGroup() {
    return facility_group;
  }

  /**
   * 施設名を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getNote() {
    return note;
  }

  /**
   * 施設名を入力します <BR>
   * 
   * @param str
   */
  public void setFacilityName(String str) {
    facility_name.setValue(str);
  }

  /**
   * メモを入力します（部署） <BR>
   * 
   * @param str
   */
  public void setNote(String str) {
    note.setValue(str);
  }

  /**
   * 施設グループ名を入力します <BR>
   * 
   * @param str
   */
  public void setFacilityGroup(String str) {
    facility_group.setValue(str);
  }

  /**
   * 施設から施設IDを取得 <BR>
   * 
   * @return
   */
  private EipMFacility getEipMFacility(ALStringField facility_name) {
    SelectQuery<EipMFacility> query = Database.query(EipMFacility.class);
    Expression exp =
      ExpressionFactory.matchExp(
        EipMFacility.FACILITY_NAME_PROPERTY,
        facility_name);
    query.setQualifier(exp);
    List<EipMFacility> list = query.fetchList();
    if (list == null || list.size() == 0) {
      return null;
    }
    EipMFacility facility = list.get(0);
    return facility;
  }

  /**
   * 施設グループ名から施設グループIDを取得 <BR>
   * 
   * @return
   */
  private EipMFacilityGroup getEipMFacilityGroup(
      ALStringField facility_group_name) {
    SelectQuery<EipMFacilityGroup> query =
      Database.query(EipMFacilityGroup.class);
    Expression exp =
      ExpressionFactory.matchExp(
        EipMFacilityGroup.GROUP_NAME_PROPERTY,
        facility_group_name);
    query.setQualifier(exp);
    List<EipMFacilityGroup> list = query.fetchList();
    if (list == null || list.size() == 0) {
      return null;
    }
    EipMFacilityGroup facility_group = list.get(0);
    return facility_group;
  }

  /**
   * 読み取った単語を指定されたフィールドに格納します。 <BR>
   * 
   * @param token
   * @param i
   */

  public void addItemToken(String token, int i) {
    switch (i) {
      case -1:
        break;
      case 0:
        try {
          setFacilityName(token);
        } catch (Exception e) {
          logger.error(e);
        }
        break;
      case 1:
        setNote(token);
        break;
      case 2:
        setFacilityGroup(token);
        break;
      default:
        break;
    }
  }

  /**
   * 
   * @return
   */
  private Map<String, TurbineUser> getAllFacilityGroupsFromDB() { // どうやって施設グループを取得できるか。
    Map<String, TurbineUser> map = null;
    try {
      SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
      List<TurbineUser> list = query.fetchList();

      map = new LinkedHashMap<String, TurbineUser>();
      TurbineUser user = null;
      int size = list.size();
      for (int i = 0; i < size; i++) {
        user = list.get(i);
        map.put(user.getLoginName(), user);
      }
    } catch (Exception ex) {
      logger.error("[ALEipUtils]", ex);
      // throw new ALDBErrorException();
    }
    return map;
  }

  public boolean isFacilityGroupExist() {
    return facility_group_exist;
  }
}

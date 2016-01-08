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
package com.aimluck.eip.account;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.account.util.AccountUtils;
import com.aimluck.eip.cayenne.om.account.EipMPosition;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 　役職を管理するフォームデータを管理するクラスです。 <BR>
 *
 */
public class AccountPositionFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountPositionFormData.class.getName());

  /** 役職名 */
  private ALStringField position_name;

  /** 役職ID */
  private int position_id;

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
  }

  /**
   * 各フィールドを初期化します。 <BR>
   *
   *
   */
  @Override
  public void initField() {

    // 役職名
    position_name = new ALStringField();
    position_name.setFieldName("役職名");
    position_name.setTrim(true);
  }

  /**
   * 各フィールドに対する制約条件を設定します。 <BR>
   *
   *
   */
  @Override
  protected void setValidator() {
    position_name.setNotNull(true);
    position_name.limitMaxLength(50);
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
    if (res) {
      try {
        if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
          position_id =
            Integer.parseInt(ALEipUtils.getTemp(
              rundata,
              context,
              ALEipConstants.ENTITY_ID));
        }
      } catch (Exception ex) {
        logger.error("AccountPositionFormData.setFormData", ex);
      }
    }
    return res;
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
    position_name.validate(msgList);

    try {
      SelectQuery<EipMPosition> query = Database.query(EipMPosition.class);

      if (ALEipConstants.MODE_INSERT.equals(getMode())) {
        Expression exp =
          ExpressionFactory.matchExp(
            EipMPosition.POSITION_NAME_PROPERTY,
            position_name.getValue());
        query.setQualifier(exp);
      } else if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
        Expression exp1 =
          ExpressionFactory.matchExp(
            EipMPosition.POSITION_NAME_PROPERTY,
            position_name.getValue());
        query.setQualifier(exp1);
        Expression exp2 =
          ExpressionFactory.noMatchDbExp(
            EipMPosition.POSITION_ID_PK_COLUMN,
            Integer.valueOf(position_id));
        query.andQualifier(exp2);
      }

      if (query.fetchList().size() != 0) {
        msgList.add(ALLocalizationUtils.getl10nFormat(
          "ACCOUNT_VALIDATE_POSITION",
          position_name));
      }
    } catch (Exception ex) {
      logger.error("AccountPositionFormData.validate", ex);
      return false;
    }

    return (msgList.size() == 0);
  }

  /**
   * 『役職』を読み込みます。 <BR>
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
      // オブジェクトモデルを取得
      EipMPosition record = AccountUtils.getEipMPosition(rundata, context);
      if (record == null) {
        return false;
      }
      position_name.setValue(record.getPositionName());
    } catch (Exception ex) {
      logger.error("AccountPositionFormData.loadFormData", ex);
      return false;
    }
    return true;
  }

  /**
   * 『役職』を追加します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {

      // 役職の順番を調整
      int lastnum = 0;
      StringBuffer statement = new StringBuffer();
      statement.append("SELECT MAX(sort) as max_sort FROM eip_m_position");
      String querydata = statement.toString();
      List<DataRow> maxnum =
        Database.sql(EipMPosition.class, querydata).fetchListAsDataRow();
      if (maxnum != null && maxnum.size() > 0) {
        Integer maxnum2 = (Integer) maxnum.get(0).get("max_sort");
        if (maxnum2 != null) {
          lastnum = maxnum2;
        }
      }
      // 最大のソートナンバーの後ろに振られていないデータを追加
      Expression exp2 =
        ExpressionFactory.matchExp(EipMPosition.SORT_PROPERTY, null);
      SelectQuery<EipMPosition> querynotsort =
        Database.query(EipMPosition.class);
      querynotsort.orderAscending(EipMPosition.UPDATE_DATE_PROPERTY);
      querynotsort.setQualifier(exp2);
      List<EipMPosition> position_notsort_list = querynotsort.fetchList();
      for (EipMPosition positiondata2 : position_notsort_list) {
        positiondata2.setSort(++lastnum);
      }

      EipMPosition position = Database.create(EipMPosition.class);
      position.setPositionName(position_name.getValue());
      position.setSort(++lastnum);
      Date now = new Date();
      position.setCreateDate(now);
      position.setUpdateDate(now);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        position.getPositionId(),
        ALEventlogConstants.PORTLET_TYPE_ACCOUNT,
        "役職「" + position.getPositionName() + "」を追加");

      position_id = position.getPositionId().intValue();

      ALEipManager.getInstance().reloadPosition();
    } catch (Exception ex) {
      Database.rollback();
      logger.error("AccountPositionFormData.insertFormData", ex);
      return false;
    }
    return true;
  }

  /**
   * 『役職』を更新します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipMPosition record = AccountUtils.getEipMPosition(rundata, context);
      if (record == null) {
        return false;
      }
      record.setPositionName(position_name.getValue());
      record.setUpdateDate(new Date());
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        record.getPositionId(),
        ALEventlogConstants.PORTLET_TYPE_ACCOUNT,
        "役職「" + record.getPositionName() + "」を更新");

      ALEipManager.getInstance().reloadPosition();
    } catch (Exception ex) {
      Database.rollback();
      logger.error("AccountPositionFormData.updateFormData", ex);
      return false;
    }
    return true;
  }

  /**
   * 『役職』を削除します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipMPosition record = AccountUtils.getEipMPosition(rundata, context);
      if (record == null) {
        return false;
      }

      // 役職IDを取得
      int positionId = record.getPositionId();

      // 役職を削除
      Database.delete(record);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        record.getPositionId(),
        ALEventlogConstants.PORTLET_TYPE_ACCOUNT,
        "役職「" + record.getPositionName() + "」を削除");

      // この役職に設定されているユーザーの役職IDを0とする
      String sql =
        "UPDATE turbine_user set POSITION_ID = 0 where POSITION_ID = "
          + positionId;
      Database.sql(TurbineUser.class, sql).execute();

      ALEipManager.getInstance().reloadPosition();
    } catch (Exception ex) {
      Database.rollback();
      logger.error("AccountPositionFormData.deleteFormData", ex);
      return false;
    }
    return true;
  }

  /**
   * 『役職名』を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getPositionName() {
    return position_name;
  }

  /**
   *
   * @return
   */
  public int getPositionId() {
    return position_id;
  }
}

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
package com.aimluck.eip.account;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.account.EipMPosition;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;

/**
 * 役職を複数削除するためのクラスです．
 * 
 */
public class AccountPositionMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountPositionMultiDelete.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @param values
   * @param msgList
   * @return
   */
  @Override
  protected boolean action(RunData rundata, Context context,
      List<String> values, List<String> msgList) {
    try {
      // オブジェクトモデル群を取得
      List<EipMPosition> list = getEipMPositions(rundata, context, values);
      if (list == null || list.size() == 0) {
        return false;
      }

      EipMPosition position = null;
      List<Integer> ids = new ArrayList<Integer>();
      int size = list.size();
      for (int i = 0; i < size; i++) {
        position = list.get(i);
        ids.add(position.getPositionId());
      }

      // 役職を削除
      Expression exp =
        ExpressionFactory.inDbExp(EipMPosition.POSITION_ID_PK_COLUMN, ids);
      SelectQuery<EipMPosition> query = Database.query(EipMPosition.class, exp);
      List<EipMPosition> postisions = query.fetchList();

      // 役職を削除
      int psize = postisions.size();
      for (int i = 0; i < psize; i++) {
        Database.delete(list.get(i));
        // イベントログに保存
        ALEventlogFactoryService.getInstance().getEventlogHandler().log(
          list.get(i).getPositionId(),
          ALEventlogConstants.PORTLET_TYPE_ACCOUNT,
          "役職「" + list.get(i).getPositionName() + "」を削除");
      }

      // この役職に設定されているユーザーの役職IDを0とする
      int idssize = ids.size();
      for (int i = 0; i < idssize; i++) {
        String sql =
          "UPDATE turbine_user set POSITION_ID = 0 where POSITION_ID = "
            + (ids.get(i)).intValue();
        Database.sql(TurbineUser.class, sql).execute();
      }
      Database.commit();

      ALEipManager.getInstance().reloadPosition();

    } catch (Exception ex) {
      Database.rollback();
      logger.error("AccountPositionMultiDelete.action", ex);
      return false;
    }
    return true;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  private List<EipMPosition> getEipMPositions(RunData rundata, Context context,
      List<String> values) {
    List<EipMPosition> list = null;

    try {
      if (values == null || values.size() == 0) {
        logger.debug("values are empty...");
        return null;
      }

      Expression exp =
        ExpressionFactory.inDbExp(EipMPosition.POSITION_ID_PK_COLUMN, values);
      SelectQuery<EipMPosition> query = Database.query(EipMPosition.class, exp);
      list = query.fetchList();
      if (list == null || list.size() == 0) {
        logger.debug("Not found ID...");
        return null;
      }
    } catch (Exception ex) {
      logger.error("AccountPositionMultiDelete.getEipMPositions", ex);
      list = null;
    }
    return list;
  }
}

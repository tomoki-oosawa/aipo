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
package com.aimluck.eip.account;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.account.EipMPosition;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.orm.DatabaseOrmService;

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
   * @see com.aimluck.eip.common.ALAbstractCheckList#action(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList,
   *      java.util.ArrayList)
   */
  protected boolean action(RunData rundata, Context context,
      List<String> values, List<String> msgList) {
    DataContext dataContext = DatabaseOrmService.getInstance().getDataContext();
    try {
      // オブジェクトモデル群を取得
      List<?> list = getEipMPositions(rundata, context, dataContext, values);
      if (list == null || list.size() == 0)
        return false;

      EipMPosition position = null;
      List<Integer> ids = new ArrayList<Integer>();
      int size = list.size();
      for (int i = 0; i < size; i++) {
        position = (EipMPosition) list.get(i);
        ids.add(position.getPositionId());
      }

      // 役職を削除
      Expression exp = ExpressionFactory.inDbExp(
          EipMPosition.POSITION_ID_PK_COLUMN, ids);
      SelectQuery query = new SelectQuery(EipMPosition.class, exp);
      List<?> postisions = dataContext.performQuery(query);

      // 役職を削除
      int psize = postisions.size();
      for (int i = 0; i < psize; i++) {
        dataContext.deleteObject((EipMPosition) list.get(i));
      }

      // この役職に設定されているユーザーの役職IDを0とする
      int idssize = ids.size();
      for (int i = 0; i < idssize; i++) {
        String sql = "UPDATE TURBINE_USER set POSITION_ID = 0 where POSITION_ID = "
            + ((Integer) ids.get(i)).intValue();
        @SuppressWarnings("deprecation")
        SQLTemplate rawSelect = new SQLTemplate(TurbineUser.class, sql, false);
        dataContext.performQuery(rawSelect);
      }
      dataContext.commitChanges();

      ALEipManager.getInstance().reloadPosition();

    } catch (Exception ex) {
      dataContext.rollbackChanges();
      logger.error("Exception", ex);
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
  private List<?> getEipMPositions(RunData rundata, Context context,
      DataContext dataContext, List<String> values) {
    List<?> list = null;

    try {
      if (values == null || values.size() == 0) {
        logger.debug("values are empty...");
        return null;
      }

      Expression exp = ExpressionFactory.inDbExp(
          EipMPosition.POSITION_ID_PK_COLUMN, values);
      SelectQuery query = new SelectQuery(EipMPosition.class, exp);
      list = dataContext.performQuery(query);
      if (list == null || list.size() == 0) {
        logger.debug("Not found ID...");
        return null;
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      list = null;
    }
    return list;
  }
}

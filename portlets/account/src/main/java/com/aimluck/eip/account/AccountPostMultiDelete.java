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

import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.account.EipMPost;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 部署を複数削除するためのクラス． <BR>
 * このとき部署に関連づけられているグループも削除する．
 *
 */
public class AccountPostMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(AccountPostMultiDelete.class.getName());

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
    try {
      // オブジェクトモデルを取得
      List<?> list = getEipMPosts(rundata, context, values);
      if (list == null || list.size() == 0)
        return false;

      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();

      int listsize = list.size();
      for (int i = 0; i < listsize; i++) {
        EipMPost record = (EipMPost) list.get(i);
        // グループからユーザーを削除
        List<ALEipUser> users = ALEipUtils.getUsers(record.getGroupName());
        int size = users.size();
        for (int j = 0; j < size; j++) {
          JetspeedSecurity.unjoinGroup(users.get(j).getName().getValue(),
              record.getGroupName());
        }

        // グループを削除
        JetspeedSecurity.removeGroup(record.getGroupName());

        // 部署を削除
        dataContext.deleteObject(record);
      }

      dataContext.commitChanges();

      // singletonオブジェクトのリフレッシュ
      ALEipManager.getInstance().reloadPost();

    } catch (Exception ex) {
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
  private List<?> getEipMPosts(RunData rundata, Context context,
      List<String> values) {
    List<?> list = null;

    try {
      if (values == null || values.size() == 0) {
        logger.debug("values are empty...");
        return null;
      }

      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      Expression exp = ExpressionFactory.inDbExp(EipMPost.POST_ID_PK_COLUMN,
          values);
      SelectQuery query = new SelectQuery(EipMPost.class, exp);
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

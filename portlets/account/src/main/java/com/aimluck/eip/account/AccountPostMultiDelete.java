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

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.account.EipMPost;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
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
   */
  @Override
  protected boolean action(RunData rundata, Context context,
      List<String> values, List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      List<EipMPost> list = getEipMPosts(rundata, context, values);
      if (list == null || list.size() == 0) {
        return false;
      }

      int listsize = list.size();
      for (int i = 0; i < listsize; i++) {
        EipMPost record = list.get(i);
        // グループからユーザーを削除
        List<ALEipUser> users =
          ALEipUtils.getUsersIncludingN(record.getGroupName());
        int size = users.size();
        for (int j = 0; j < size; j++) {
          JetspeedSecurity.unjoinGroup(
            users.get(j).getName().getValue(),
            record.getGroupName());
        }

        // グループを削除
        JetspeedSecurity.removeGroup(record.getGroupName());

        // 部署を削除
        Database.deleteAll(record);

        // イベントログに保存
        ALEventlogFactoryService.getInstance().getEventlogHandler().log(
          record.getPostId(),
          ALEventlogConstants.PORTLET_TYPE_ACCOUNT,
          "部署「" + record.getPostName() + "」を削除");
      }

      Database.commit();

      // singletonオブジェクトのリフレッシュ
      ALEipManager.getInstance().reloadPost();

    } catch (Exception ex) {
      Database.rollback();
      logger.error("AccountPostMultiDelete.action", ex);
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
  private List<EipMPost> getEipMPosts(RunData rundata, Context context,
      List<String> values) {
    List<EipMPost> list = null;

    try {
      if (values == null || values.size() == 0) {
        logger.debug("values are empty...");
        return null;
      }

      Expression exp =
        ExpressionFactory.inDbExp(EipMPost.POST_ID_PK_COLUMN, values);
      SelectQuery<EipMPost> query = Database.query(EipMPost.class, exp);
      list = query.fetchList();
      if (list == null || list.size() == 0) {
        logger.debug("Not found ID...");
        return null;
      }
    } catch (Exception ex) {
      logger.error("AccountPostMultiDelete.getEipMPosts", ex);
      list = null;
    }
    return list;
  }
}

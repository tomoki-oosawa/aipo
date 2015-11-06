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
package com.aimluck.eip.webmail;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.cayenne.om.portlet.EipTMailFilter;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.webmail.util.WebMailUtils;

/**
 * 複数のWebメールフィルタを削除するクラスです。 <br />
 */
public class WebMailFilterMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WebMailFilterMultiDelete.class.getName());

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
      int accountId = -1;
      try {
        accountId =
          Integer.parseInt(ALEipUtils.getTemp(
            rundata,
            context,
            WebMailUtils.ACCOUNT_ID));
        if (accountId < 0) {
          return false;
        }
      } catch (Exception e) {
        return false;
      }

      // 現在操作中のメールアカウントを取得する
      int userId = ALEipUtils.getUserId(rundata);
      EipMMailAccount mailAccount =
        ALMailUtils.getMailAccount( userId, accountId);

      // フィルタを削除
      SelectQuery<EipTMailFilter> query = Database.query(EipTMailFilter.class);

      Expression exp =
        ExpressionFactory.inDbExp(EipTMailFilter.FILTER_ID_PK_COLUMN, values);
      Expression exp2 =
        ExpressionFactory.matchExp(
          EipTMailFilter.EIP_MMAIL_ACCOUNT_PROPERTY,
          mailAccount);
      query.setQualifier(exp.andExp(exp2)).orderAscending(
        EipTMailFilter.SORT_ORDER_PROPERTY);

      List<EipTMailFilter> deleteFilterList = query.fetchList();

      // 削除対象のフィルタが見つかったら
      if (deleteFilterList != null && deleteFilterList.size() != 0) {
        int minSortOrder = (deleteFilterList.get(0)).getSortOrder();

        // ソート番号のずれを直す
        SelectQuery<EipTMailFilter> query2 =
          Database.query(EipTMailFilter.class);
        Expression exp3 =
          ExpressionFactory.matchExp(
            EipTMailFilter.EIP_MMAIL_ACCOUNT_PROPERTY,
            mailAccount);
        Expression exp4 =
          ExpressionFactory.greaterOrEqualExp(
            EipTMailFilter.SORT_ORDER_PROPERTY,
            minSortOrder + 1);
        Expression exp5 =
          ExpressionFactory.inDbExp(EipTMailFilter.FILTER_ID_PK_COLUMN, values);
        query2.setQualifier(exp3.andExp(exp4.andExp(exp5.notExp())));

        List<EipTMailFilter> correctFilterList = query2.fetchList();

        // 最小のソート番号以降で、削除されていないフィルタのリストを取り出し、
        // 連番を振りなおす
        for (EipTMailFilter correctFilter : correctFilterList) {
          correctFilter.setSortOrder(minSortOrder);
          minSortOrder++;
        }

        Database.deleteAll(deleteFilterList);
        Database.commit();
      }
      return true;
    } catch (Throwable t) {
      Database.rollback();
      logger.error(t);
      return false;
    }
  }

}

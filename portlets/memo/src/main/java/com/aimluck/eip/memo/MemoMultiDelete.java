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
package com.aimluck.eip.memo;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTMemo;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * メモ帳の複数削除を行うためのクラスです。 <BR>
 *
 */
public class MemoMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(MemoMultiDelete.class.getName());

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
  protected boolean action(RunData rundata, Context context, List<String> values,
      List<String> msgList) {
    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();

      List<Integer> ids = new ArrayList<Integer>();
      int size = values.size();
      for (int i = 0; i < size; i++) {
        ids.add(Integer.valueOf(values.get(i)));
      }

      SelectQuery query = new SelectQuery(EipTMemo.class);
      Expression exp1 = ExpressionFactory.matchExp(EipTMemo.OWNER_ID_PROPERTY,
          Integer.valueOf(ALEipUtils.getUserId(rundata)));
      query.setQualifier(exp1);
      Expression exp2 = ExpressionFactory.inDbExp(EipTMemo.MEMO_ID_PK_COLUMN,
          ids);

      query.andQualifier(exp2);

      List<?> memos = dataContext.performQuery(query);
      if (memos == null || memos.size() == 0)
        return false;

      int lsize = memos.size();
      for (int i = 0; i < lsize; i++) {
        EipTMemo memo = (EipTMemo) memos.get(i);
        // メモを削除
        dataContext.deleteObject(memo);
        dataContext.commitChanges();
        // イベントログに保存
        ALEventlogFactoryService.getInstance().getEventlogHandler().log(
            memo.getMemoId(), ALEventlogConstants.PORTLET_TYPE_MEMO,
            memo.getMemoName());
      }

    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

}

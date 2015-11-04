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
package com.aimluck.eip.gpdb;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTGpdb;
import com.aimluck.eip.cayenne.om.portlet.EipTGpdbRecord;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.gpdb.util.GpdbUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;

/**
 * Webデータベースの複数削除を行うためのクラスです。 <BR>
 * 
 */
public class GpdbMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GpdbMultiDelete.class.getName());

  /**
   * 処理を実行します。
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @param values
   *          List<String>
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean action(RunData rundata, Context context,
      List<String> values, List<String> msgList) {
    try {

      // delete database
      SelectQuery<EipTGpdb> query = Database.query(EipTGpdb.class);
      Expression exp =
        ExpressionFactory.inDbExp(EipTGpdb.GPDB_ID_PK_COLUMN, values);
      query.setQualifier(exp);

      List<EipTGpdb> list = query.fetchList();
      if (list == null || list.isEmpty()) {
        return false;
      }

      for (EipTGpdb gpdb : list) {
        List<EipTGpdbRecord> listRecord =
          GpdbUtils.getEipTGpdbRecord("" + gpdb.getGpdbId());

        if (listRecord != null && !listRecord.isEmpty()) {
          msgList.add(GpdbUtils.ERRMSG_DB_DELETE);
          return false;
        }
      }

      if (msgList.isEmpty()) {

        for (EipTGpdb gpdb : list) {
          // 項目定義を削除
          GpdbUtils.removeGpdbItem(gpdb);

          // Webデータベース削除
          Database.delete(gpdb);
          Database.commit();

          // イベントログに保存
          ALEventlogFactoryService.getInstance().getEventlogHandler().log(
            gpdb.getGpdbId(),
            ALEventlogConstants.PORTLET_TYPE_GPDB,
            "Webデータベース 「" + gpdb.getGpdbName() + "」 削除");
        }
      }

    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

}

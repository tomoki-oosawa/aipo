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

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTGpdb;
import com.aimluck.eip.cayenne.om.portlet.EipTGpdbRecord;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.gpdb.util.GpdbUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * Webデータベースレコードの複数削除を行うためのクラスです。 <BR>
 *
 */
public class GpdbRecordMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GpdbRecordMultiDelete.class.getName());

  /** フィルタ値のキー */
  private static final String LIST_FILTER_STR = new StringBuffer().append(
    GpdbRecordSelectData.class.getSimpleName()).append(
    ALEipConstants.LIST_FILTER).toString();

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
      String gpdbId = ALEipUtils.getTemp(rundata, context, LIST_FILTER_STR);

      // Webデータベースオブジェクト
      EipTGpdb gpdb = GpdbUtils.getEipTGpdb(gpdbId);

      // delete database
      SelectQuery<EipTGpdbRecord> query = Database.query(EipTGpdbRecord.class);
      query.setQualifier(ExpressionFactory.matchDbExp(
        EipTGpdbRecord.GPDB_PROPERTY,
        gpdbId));
      query.andQualifier(ExpressionFactory.inExp(
        EipTGpdbRecord.RECORD_NO_PROPERTY,
        values));
      query.orderAscending(EipTGpdbRecord.RECORD_NO_PROPERTY);

      List<EipTGpdbRecord> gpdbRecordList = query.fetchList();
      if (gpdbRecordList == null || gpdbRecordList.isEmpty()) {
        return false;
      }

      int recordNo = Integer.MIN_VALUE;
      for (EipTGpdbRecord record : gpdbRecordList) {

        // レコード削除
        Database.delete(record);

        // 添付ファイル削除
        GpdbUtils.removeGpdbRecordFile(rundata, gpdbRecordList);

        Database.commit();

        if (recordNo != record.getRecordNo()) {

          // イベントログに保存
          ALEventlogFactoryService.getInstance().getEventlogHandler().log(
            record.getGpdbItemId(),
            ALEventlogConstants.PORTLET_TYPE_GPDB,
            "Webデータベース 「" + gpdb.getGpdbName() + "」 データ削除");

          recordNo = record.getRecordNo();
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

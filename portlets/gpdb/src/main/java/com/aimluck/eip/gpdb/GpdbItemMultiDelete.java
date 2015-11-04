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
import com.aimluck.eip.cayenne.om.portlet.EipTGpdbItem;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.gpdb.util.GpdbUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;

/**
 * 区分値の複数削除を行うためのクラスです。 <BR>
 * 
 */
public class GpdbItemMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GpdbItemMultiDelete.class.getName());

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
      SelectQuery<EipTGpdbItem> query = Database.query(EipTGpdbItem.class);
      query.setQualifier(ExpressionFactory.inDbExp(
        EipTGpdbItem.GPDB_ITEM_ID_PK_COLUMN,
        values));

      List<EipTGpdbItem> gpdbItemList = query.fetchList();
      if (gpdbItemList == null || gpdbItemList.isEmpty()) {
        return false;
      }

      for (EipTGpdbItem gpdbItem : gpdbItemList) {
        if (GpdbUtils.FLG_ON.equals(gpdbItem.getTitleFlg())) {
          msgList.add(GpdbUtils.ERRMSG_TITLE_ITEM_DELETE);
          return false;
        }
      }

      if (msgList.isEmpty()) {

        // Webデータベースオブジェクト
        EipTGpdb gpdb =
          GpdbUtils.getEipTGpdb(rundata.getParameters().getString("gpdb_id"));

        for (EipTGpdbItem gpdbItem : gpdbItemList) {
          // 項目に紐づくレコード情報を削除
          GpdbUtils.removeGpdbRecord(rundata, gpdbItem);
          // 項目定義を削除
          Database.delete(gpdbItem);
          Database.commit();

          // イベントログに保存
          ALEventlogFactoryService.getInstance().getEventlogHandler().log(
            gpdbItem.getGpdbItemId(),
            ALEventlogConstants.PORTLET_TYPE_GPDB,
            "Webデータベース 「"
              + gpdb.getGpdbName()
              + "」 項目定義 「"
              + gpdbItem.getGpdbItemName()
              + "」 削除");
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

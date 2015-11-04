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

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipMGpdbKubun;
import com.aimluck.eip.cayenne.om.portlet.EipMGpdbKubunValue;
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
public class GpdbKubunMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GpdbKubunMultiDelete.class.getName());

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

      // 削除対象の区分値を取得
      SelectQuery<EipMGpdbKubunValue> query =
        Database.query(EipMGpdbKubunValue.class);
      query.setQualifier(ExpressionFactory.inDbExp(
        EipMGpdbKubunValue.GPDB_KUBUN_VALUE_ID_PK_COLUMN,
        values));

      List<EipMGpdbKubunValue> flist = query.fetchList();
      if (flist == null || flist.isEmpty()) {
        return false;
      }

      // チェック
      List<Integer> checkedKubunList = new ArrayList<Integer>(); // チェック済み区分
      for (EipMGpdbKubunValue value : flist) {

        EipMGpdbKubun Gpdbkubun =
          GpdbUtils.getEipMGpdbKubun(value.getGpdbKubunId());

        int kubunCount =
          GpdbUtils.getRecordCountKubunUsed("" + value.getGpdbKubunValueId());
        if (kubunCount > 0) {
          // Webデータベースレコードが登録されている場合
          msgList.add("選択項目 『"
            + Gpdbkubun.getGpdbKubunName()
            + "』 の値 『"
            + value.getGpdbKubunValue()
            + "』 は使用されているため削除できません。");

        } else {

          int kubunId = value.getGpdbKubunId();

          if (!checkedKubunList.contains(kubunId)) {

            SelectQuery<EipTGpdbItem> valQuery =
              Database.query(EipTGpdbItem.class);
            valQuery.setQualifier(ExpressionFactory.matchExp(
              EipTGpdbItem.GPDB_KUBUN_ID_PROPERTY,
              kubunId));

            if (!valQuery.fetchList().isEmpty()) {

              // 区分値リストを取得
              List<GpdbKubunValueResultData> valList =
                GpdbUtils.getGpdbKubunValueList("" + kubunId);
              if (valList != null && !valList.isEmpty()) {
                msgList.add("選択項目 『"
                  + Gpdbkubun.getGpdbKubunName()
                  + "』 は項目定義されているため、全ての値は削除できません。");
              }
            }
            checkedKubunList.add(kubunId);
          }
        }
      }

      if (!msgList.isEmpty()) {
        return false;
      }

      // 削除処理
      for (EipMGpdbKubunValue value : flist) {

        Database.delete(value);
        Database.commit();

        EipMGpdbKubun Gpdbkubun =
          GpdbUtils.getEipMGpdbKubun(value.getGpdbKubunId());

        // イベントログに保存
        ALEventlogFactoryService.getInstance().getEventlogHandler().log(
          value.getGpdbKubunValueId(),
          ALEventlogConstants.PORTLET_TYPE_GPDB,
          "Webデータベース 選択項目「"
            + Gpdbkubun.getGpdbKubunName()
            + "」 値 「"
            + value.getGpdbKubunValue()
            + "」 削除");
      }

      // 区分値を持たない区分を削除
      if (GpdbUtils.removeGpdbKubunNoValue()) {
        Database.commit();
      }

    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

}

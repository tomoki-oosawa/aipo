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
package com.aimluck.eip.webmail;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.cayenne.om.portlet.EipTMail;
import com.aimluck.eip.cayenne.om.portlet.EipTMailFolder;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.webmail.util.WebMailUtils;

/**
 * 複数のWebメールをフォルダ移動するクラスです。 <br />
 */
public class WebMailMultiMove extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WebMailMultiMove.class.getName());

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
    int accountId = -1, toFolderId = -1;
    int userId = ALEipUtils.getUserId(rundata);
    EipMMailAccount account;

    // フォルダ移動は現在受信トレイ間でのみ。
    String currentTab = ALEipUtils.getTemp(rundata, context, "tab");
    if (currentTab == null
      || "".equals(currentTab)
      || WebMailUtils.TAB_SENT.equals(currentTab)) {
      return false;
    }

    try {
      // アカウントIDと移動先フォルダIDが指定されていなければ終了
      accountId =
        Integer.parseInt(ALEipUtils.getTemp(
          rundata,
          context,
          WebMailUtils.ACCOUNT_ID));
      toFolderId = Integer.valueOf(rundata.getParameters().get("move_folder"));
      account = ALMailUtils.getMailAccount(userId, accountId);

      if (accountId < 0 || toFolderId < 0 || account == null) {
        return false;
      }

      // 移動先フォルダIDがアカウントのものかどうか確かめる。
      EipTMailFolder folder =
        WebMailUtils.getEipTMailFolder(account, String.valueOf(toFolderId));
      if (folder == null) {
        return false;
      }

      // フォルダを移動
      SelectQuery<EipTMail> query = Database.query(EipTMail.class);
      Expression exp =
        ExpressionFactory.inDbExp(EipTMail.MAIL_ID_PK_COLUMN, values);

      List<EipTMail> mailList = query.setQualifier(exp).fetchList();
      for (EipTMail mail : mailList) {
        mail.setFolderId(toFolderId);
      }

      Database.commit();

      return true;
    } catch (Throwable t) {
      Database.rollback();
      logger.error("[WebMailMultiMove]", t);
      return false;
    }
  }

}

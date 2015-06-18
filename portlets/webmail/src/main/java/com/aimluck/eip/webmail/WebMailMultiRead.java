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

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.mail.ALFolder;
import com.aimluck.eip.mail.ALMailFactoryService;
import com.aimluck.eip.mail.ALMailHandler;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.webmail.util.WebMailUtils;

/**
 * 複数のWebメールを既読にするクラスです。 <br />
 */
public class WebMailMultiRead extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WebMailMultiRead.class.getName());

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

      String currentTab = ALEipUtils.getTemp(rundata, context, "tab");
      if (currentTab == null || "".equals(currentTab)) {
        return false;
      }

      int type_mail =
        (WebMailUtils.TAB_RECEIVE.equals(currentTab))
          ? ALFolder.TYPE_RECEIVE
          : ALFolder.TYPE_SEND;
      int userId = ALEipUtils.getUserId(rundata);

      String orgId = Database.getDomainName();

      ALMailHandler handler =
        ALMailFactoryService.getInstance().getMailHandler();
      ALFolder folder =
        handler.getALFolder(type_mail, orgId, userId, Integer
          .valueOf(accountId));
      folder.readMails(values);
    } catch (Exception ex) {
      logger.error("webmail", ex);
      return false;
    }
    return true;
  }

}

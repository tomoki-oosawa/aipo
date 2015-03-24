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
package com.aimluck.eip.mail;

import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.orm.Database;

/**
 *
 */
public class ALMailService {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALMailService.class.getName());

  public static ALMailHandler getService() {
    return ALMailFactoryService.getInstance().getMailHandler();
  }

  /**
   * 管理者メールを送信します。
   * 
   * @param adminMailContext
   * @return
   * @throws Exception
   */
  public static List<String> sendAdminMail(ALAdminMailContext adminMailContext)
      throws Exception {
    return getService().sendAdminMail(adminMailContext);
  }

  public static void sendAdminMailAsync(
      final ALAdminMailContext adminMailContext) throws Exception {

    Runnable sender =
      new ALMailSendThread(Database.getDomainName(), adminMailContext);

    Thread mailthread = new Thread(sender);
    mailthread.start();
  }
}

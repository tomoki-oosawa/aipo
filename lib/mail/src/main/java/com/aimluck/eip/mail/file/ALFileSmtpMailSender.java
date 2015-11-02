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
package com.aimluck.eip.mail.file;

import com.aimluck.eip.mail.ALFolder;
import com.aimluck.eip.mail.ALMailSenderContext;
import com.aimluck.eip.mail.ALSmtpMailSender;

/**
 * ローカルのファイルシステムを利用し、メール送信（SMTP）を操作するクラスです。 <br />
 * 
 */
public class ALFileSmtpMailSender extends ALSmtpMailSender {

  /**
   * コンストラクタ
   * 
   * @param userRootFolderName
   * @param accountName
   * @param smtpHost
   * @param smtpPort
   */
  public ALFileSmtpMailSender(ALMailSenderContext scontext) {
    super(scontext);
  }

  @Override
  protected ALFolder getALFolder() {
    return new ALFileLocalFolder(
      ALFolder.TYPE_SEND,
      scontext.getOrgId(),
      scontext.getUserId(),
      scontext.getAccountId());
  }

}

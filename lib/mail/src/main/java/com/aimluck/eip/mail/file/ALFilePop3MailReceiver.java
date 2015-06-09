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
package com.aimluck.eip.mail.file;

import com.aimluck.eip.mail.ALFolder;
import com.aimluck.eip.mail.ALMailReceiverContext;
import com.aimluck.eip.mail.ALPop3MailReceiver;

/**
 * ローカルのファイルシステムを利用し、メール受信（POP3）を操作するクラスです。 <br />
 * 
 */
public class ALFilePop3MailReceiver extends ALPop3MailReceiver {

  public ALFilePop3MailReceiver(ALMailReceiverContext rcontext) {
    super(rcontext);
  }

  @Override
  protected ALFolder getALFolder() {
    return new ALFileLocalFolder(
      ALFolder.TYPE_RECEIVE,
      rcontext.getOrgId(),
      rcontext.getUserId(),
      rcontext.getAccountId());
  }

}

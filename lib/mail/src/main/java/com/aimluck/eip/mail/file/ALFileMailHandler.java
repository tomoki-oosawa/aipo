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

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.mail.ALFolder;
import com.aimluck.eip.mail.ALMailHandler;
import com.aimluck.eip.mail.ALMailReceiver;
import com.aimluck.eip.mail.ALMailReceiverContext;
import com.aimluck.eip.mail.ALMailSender;
import com.aimluck.eip.mail.ALMailSenderContext;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.services.storage.ALStorageService;

/**
 * ローカルのファイルシステムを利用し、メールの送受信を操作するクラスです。 <br />
 * 
 */
public class ALFileMailHandler extends ALMailHandler {

  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALFileMailHandler.class.getName());

  /** カテゴリキー（mail） */
  protected String categoryKey = null;

  public static ALMailHandler getInstance() {
    return new ALFileMailHandler();
  }

  @Override
  protected ALMailReceiver getALMailReceiver(ALMailReceiverContext rcontext) {
    return new ALFilePop3MailReceiver(rcontext);
  }

  @Override
  public ALMailSender getALMailSender(ALMailSenderContext scontext) {
    return new ALFileSmtpMailSender(scontext);
  }

  @Override
  public ALMailSender getALAdminMailSender(ALMailSenderContext scontext) {
    return new ALFileSmtpMailSender(scontext);
  }

  @Override
  public ALFolder getALFolder(int type_mail, String org_id, int user_id,
      int account_id) {
    return new ALFileLocalFolder(type_mail, org_id, user_id, account_id);
  }

  protected String getCategoryKey() {
    return (categoryKey != null && !"".equals(categoryKey))
      ? categoryKey
      : ALMailUtils.categoryKey;
  }

  /**
   * アカウントフォルダを削除する．
   * 
   * @param userRootFolderName
   * @param accountName
   * @return
   */
  @Override
  public boolean removeAccount(String org_id, int user_id, int account_id) {
    StringBuilder key = new StringBuilder();
    String categoryKeytmp = getCategoryKey();

    if (categoryKeytmp != null && !"".equals(categoryKeytmp)) {
      key.append(categoryKeytmp);
    }
    key.append(ALStorageService.separator());
    key.append(user_id);
    key.append(ALStorageService.separator());
    key.append(account_id);

    return ALStorageService.deleteFolder(ALMailUtils.rootFolderPath, key
      .toString());
  }

  /**
   * アカウントフォルダ容量計算
   * 
   * @param userRootFolderName
   * @param accountName
   * @return
   */
  @Override
  public long getFolderSize(String org_id, int user_id, int account_id) {
    StringBuilder key = new StringBuilder();
    String categoryKeytmp = getCategoryKey();

    if (categoryKeytmp != null && !"".equals(categoryKeytmp)) {
      key.append(categoryKeytmp);
    }
    key.append(ALStorageService.separator());
    key.append(user_id);
    key.append(ALStorageService.separator());
    key.append(account_id);

    return ALStorageService.getFolderSize(ALMailUtils.rootFolderPath, key
      .toString());
  }

}

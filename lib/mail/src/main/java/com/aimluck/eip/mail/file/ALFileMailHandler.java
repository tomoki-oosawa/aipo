/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.mail.file;

import java.io.File;
import java.io.IOException;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.mail.ALFolder;
import com.aimluck.eip.mail.ALMailHandler;
import com.aimluck.eip.mail.ALMailReceiver;
import com.aimluck.eip.mail.ALMailReceiverContext;
import com.aimluck.eip.mail.ALMailSender;
import com.aimluck.eip.mail.ALMailSenderContext;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.util.orgutils.ALOrgUtilsFactoryService;
import com.aimluck.eip.util.orgutils.ALOrgUtilsHandler;

/**
 * ローカルのファイルシステムを利用し、メールの送受信を操作するクラスです。 <br />
 * 
 */
public class ALFileMailHandler extends ALMailHandler {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALFileMailHandler.class.getName());

  /** カテゴリキー（mail） */
  protected String categoryKey = null;

  /**
   * コンストラクタ
   */
  private ALFileMailHandler() {
  }

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
  public ALFolder getALFolder(int type_mail, String org_id, int user_id,
      int account_id) {
    return new ALFileLocalFolder(type_mail, org_id, user_id, account_id);
  }

  protected String getCategoryKey() {
    return (categoryKey != null && !"".equals(categoryKey)) ? categoryKey
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
    StringBuffer fullName = null;
    String categoryKeytmp = getCategoryKey();

    if (categoryKeytmp != null && !"".equals(categoryKeytmp)) {
      ALOrgUtilsHandler handler = ALOrgUtilsFactoryService.getInstance()
        .getOrgUtilsHandler();
      File docPath = handler.getDocumentPath(ALMailUtils.rootFolderPath,
        org_id, categoryKeytmp);
      String pathStr = null;
      try {
        pathStr = docPath.getCanonicalPath();
      } catch (IOException e) {
        logger.error("ALFileMailHandler: unable to resolve file path for "
          + pathStr);
      }
      fullName = new StringBuffer(pathStr);
      fullName.append(File.separator).append(user_id).append(File.separator)
        .append(account_id);
    } else {
      fullName = new StringBuffer(ALMailUtils.rootFolderPath);
      fullName.append(File.separator).append(org_id).append(File.separator)
        .append(user_id).append(File.separator).append(account_id);
    }

    String path = fullName.toString();
    File folder = new File(path);
    return ALMailUtils.deleteFolder(folder);
  }

}

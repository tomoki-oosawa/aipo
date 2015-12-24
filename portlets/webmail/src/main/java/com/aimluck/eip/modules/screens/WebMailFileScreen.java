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
package com.aimluck.eip.modules.screens;

import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.modules.screens.RawScreen;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.mail.ALFolder;
import com.aimluck.eip.mail.ALLocalMailMessage;
import com.aimluck.eip.mail.ALMailFactoryService;
import com.aimluck.eip.mail.ALMailHandler;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.webmail.util.WebMailUtils;

/**
 * Webメールのファイルを処理するクラスです。 <br />
 */
public class WebMailFileScreen extends RawScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WebMailFileScreen.class.getName());

  /**
   *
   * @param rundata
   * @return
   */
  @Override
  protected String getContentType(RunData rundata) {
    return "application/octet-stream";
  }

  /**
   *
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata) throws Exception {
    ServletOutputStream out = null;

    try {
      String orgId = Database.getDomainName();
      int uid = ALEipUtils.getUserId(rundata);
      int accountid =
        Integer.parseInt(rundata.getParameters().getString(
          WebMailUtils.ACCOUNT_ID));
      int mailindex = rundata.getParameters().getInt(ALEipConstants.ENTITY_ID);
      int attachmentIndex = rundata.getParameters().getInt("attachmentIndex");
      if (mailindex < 0 || attachmentIndex < 0) {
        return;
      }

      String currentTab = rundata.getParameters().getString("tab");
      int type_mail =
        (WebMailUtils.TAB_RECEIVE.equals(currentTab))
          ? ALFolder.TYPE_RECEIVE
          : ALFolder.TYPE_SEND;
      ALMailHandler handler =
        ALMailFactoryService.getInstance().getMailHandler();
      ALFolder folder = handler.getALFolder(type_mail, orgId, uid, accountid);
      ALLocalMailMessage msg = (ALLocalMailMessage) folder.getMail(mailindex);
      if (msg == null) {
        return;
      }

      String fileName;
      boolean isMsie = ALEipUtils.isMsieBrowser(rundata);
      boolean isEdge = ALEipUtils.isEdgeBrowser(rundata);
      if (isMsie || isEdge) {
        fileName =
          new String(
            msg.getFileName(attachmentIndex).getBytes("Windows-31J"),
            "8859_1");
      } else {
        fileName =
          new String(
            msg.getFileName(attachmentIndex).getBytes("UTF-8"),
            "8859_1");
      }

      InputStream in = msg.getInputStream(attachmentIndex);
      HttpServletResponse response = rundata.getResponse();
      // ファイル名の送信(attachment部分をinlineに変更すればインライン表示)
      response.setHeader("Content-disposition", "attachment; filename=\""
        + fileName
        + "\"");
      response.setHeader("Cache-Control", "aipo");
      response.setHeader("Pragma", "aipo");

      // ファイル内容の出力
      out = response.getOutputStream();
      byte[] b = new byte[1024];
      int len = -1;
      while ((len = in.read(b)) != -1) {
        out.write(b, 0, len);
        out.flush();
      }
      in.close();
      out.flush();
      out.close();
    } catch (Exception e) {
      logger.error("WebMailFileScreen.doOutput", e);
    }

  }
}

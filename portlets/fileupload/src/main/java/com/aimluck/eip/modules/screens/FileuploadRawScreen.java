/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.modules.screens.RawScreen;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFile;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFolder;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFolderMap;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardCategoryMap;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardFile;
import com.aimluck.eip.cayenne.om.portlet.EipTMsgboardTopic;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALEipUtils;

public class FileuploadRawScreen extends RawScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileuploadRawScreen.class.getName());

  /** ファイル名 */
  private String fileName = null;

  /** ローカルディスクに保存されているファイルへのフルパス */
  private String filepath = null;

  private int userid;

  private EipTMsgboardTopic msgboardtopic;

  private EipTMsgboardCategory msgboardcategory;

  private EipTCabinetFolder cabinetfolder;

  private EipTCabinetFolderMap cabinetfoldermap;

  private List cabinetfiles;

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
   * ファイル名
   * 
   * @return
   */
  protected String getFileName() {
    return fileName;
  }

  /**
   * ファイル名
   * 
   * @return
   */
  protected String getFilePath() {
    return filepath;
  }

  protected void setFileName(String fileName) {
    this.fileName = fileName;
  }

  protected void setFilePath(String filepath) {
    this.filepath = filepath;
  }

  /**
   * 
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata) throws Exception {
    InputStream in = null;
    OutputStream out = null;
    try {
      String attachmentRealName = null;

      boolean isMsie = FileuploadUtils.isMsieBrowser(rundata);
      if (isMsie) {
        attachmentRealName =
          new String(getFileName().getBytes("Windows-31J"), "8859_1");
      } else {
        attachmentRealName =
          new String(getFileName().getBytes("UTF-8"), "8859_1");
      }

      HttpServletResponse response = rundata.getResponse();
      // ファイル名の送信(attachment部分をinlineに変更すればインライン表示)
      response.setHeader("Content-disposition", "attachment; filename=\""
        + attachmentRealName
        + "\"");
      response.setHeader("Cache-Control", "aipo");
      response.setHeader("Pragma", "aipo");

      // ファイル内容の出力
      out = response.getOutputStream();

      String path = getFilePath();
      if (path == null || "".equals(path)) {
        throw new FileNotFoundException();
      }
      in = ALStorageService.getFile(filepath);
      out = new BufferedOutputStream(response.getOutputStream());

      byte[] buf = new byte[1024];
      int length;
      while ((length = in.read(buf)) > 0) {
        out.write(buf, 0, length);
      }
    } catch (Exception e) {
      logger.error("[ERROR]" + e);
    } finally {
      try {
        if (out != null) {
          out.flush();
          out.close();
        }
      } catch (IOException ex) {
        logger.error("[ERROR]" + ex);
      }
    }
  }

  protected boolean doCheckAclPermission(RunData rundata, String pfeature,
      int defineAclType) throws ALPermissionException {
    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();
    if (!aclhandler.hasAuthority(
      ALEipUtils.getUserId(rundata),
      pfeature,
      defineAclType)) {
      throw new ALPermissionException();
    }
    return true;
  }

  protected boolean doFileCheckView(RunData rundata,
      EipTMsgboardFile msgboardfile) throws ALPermissionException {
    userid = ALEipUtils.getUserId(rundata);
    msgboardtopic = msgboardfile.getEipTMsgboardTopic();
    msgboardcategory = msgboardtopic.getEipTMsgboardCategory();

    List<?> categoryMap = msgboardcategory.getEipTMsgboardCategoryMaps();
    int mapsize = categoryMap.size();
    if (mapsize > 0) {
      for (int i = 0; i < mapsize; i++) {
        EipTMsgboardCategoryMap map =
          (EipTMsgboardCategoryMap) categoryMap.get(i);

        // 全員が閲覧可能
        if ("A".equals(map.getStatus())) {
          return true;
        } else {
          // ログインユーザが所属メンバの場合
          if (map.getUserId().intValue() == userid) {
            return true;
          }
        }
      }
      throw new ALPermissionException();
    } else {
      return true;
    }
  }

  protected boolean doFileCheckView(RunData rundata, EipTCabinetFile cabinetfile)
      throws ALPermissionException {
    userid = ALEipUtils.getUserId(rundata);
    cabinetfolder = cabinetfile.getEipTCabinetFolder();
    cabinetfiles = cabinetfolder.getEipTCabinetFiles();

    List<?> folderMap = cabinetfolder.getEipTCabinetFolderMap();
    int mapsize = folderMap.size();
    if (mapsize > 0) {
      for (int i = 0; i < mapsize; i++) {
        EipTCabinetFolderMap map = (EipTCabinetFolderMap) folderMap.get(i);

        // 全員が閲覧可能
        if ("A".equals(map.getStatus())) {
          return true;
        } else {
          // ログインユーザが所属メンバの場合
          if (map.getUserId().intValue() == userid) {
            return true;
          }
        }
      }
      throw new ALPermissionException();
    } else {
      return true;
    }
  }
}

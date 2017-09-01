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

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;

import org.apache.ecs.ConcreteElement;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.modules.screens.RawScreen;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALEipUtils;

public abstract class FileuploadRawScreen extends RawScreen {

  /** logger */
  private static final JetspeedLogger logger =
    JetspeedLogFactoryService.getLogger(FileuploadRawScreen.class.getName());

  /** ファイル名 */
  private String fileName = null;

  /** ローカルディスクに保存されているファイルへのフルパス */
  private String filepath = null;

  @Override
  protected ConcreteElement build(RunData data) throws Exception {
    init(data);
    return doBuild(data);
  }

  protected abstract void init(RunData rundata) throws Exception;

  /**
   *
   * @param rundata
   * @return
   */
  @Override
  protected String getContentType(RunData rundata) {
    String inline = rundata.getParameters().get("inline");
    String contentType = FileuploadUtils.getInlineContentType(getFileName());

    return (contentType != null
      && (FileuploadUtils.isAcceptInline(getFileName()) || "1".equals(inline)))
        ? contentType
        : "application/octet-stream";
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
      doCheckAttachmentAclPermission(
        rundata,
        ALAccessControlConstants.VALUE_ACL_EXPORT);
      String attachmentRealName = null;
      boolean isAndroid = ALEipUtils.isAndroidBrowser(rundata);
      boolean isIOS = ALEipUtils.isIOSBrowser(rundata);

      if (isAndroid || isIOS) {// androidだと日本語タイトルが変換されるので一律でfileに変更
        attachmentRealName = "file";
        if (getFileName().lastIndexOf(".") > -1) {
          attachmentRealName +=
            getFileName().substring(getFileName().lastIndexOf("."));
        }
      } else {
        boolean isMsie = ALEipUtils.isMsieBrowser(rundata);
        boolean isEdge = ALEipUtils.isEdgeBrowser(rundata);
        if (isMsie || isEdge) {
          attachmentRealName =
            new String(getFileName().getBytes("Windows-31J"), "8859_1");
        } else {
          attachmentRealName =
            new String(getFileName().getBytes("UTF-8"), "8859_1");
        }
      }

      String inline = rundata.getParameters().get("inline");
      String type =
        FileuploadUtils.isAcceptInline(getFileName()) || "1".equals(inline)
          ? "inline"
          : "attachment";

      HttpServletResponse response = rundata.getResponse();
      // ファイル名の送信(attachment部分をinlineに変更すればインライン表示)
      response.setHeader(
        "Content-disposition",
        type + "; filename=\"" + attachmentRealName + "\"");
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
      long fileSize = ALStorageService.getFileSize(filepath);
      String httpRange = rundata.getRequest().getHeader("range");
      if (httpRange != null) {
        String[] httpRangeBytes = httpRange.split("=");
        String[] httpRangeValue = httpRangeBytes[1].split("-");
        int startRange = Integer.parseInt(httpRangeValue[0]);
        int endRange =
          httpRangeValue.length > 1
            ? Integer.parseInt(httpRangeValue[1])
            : (int) fileSize - 1;
        Integer rangeLength = endRange - startRange + 1;
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        response.setContentType(
          new MimetypesFileTypeMap().getContentType(attachmentRealName));
        response.setContentLength(rangeLength);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Connection", "keep-alive");
        response.setHeader(
          "Content-Range",
          "bytes " + startRange + "-" + endRange + "/" + fileSize);

        byte[] buf = new byte[1024];
        int length;
        skip(in, startRange);
        int toRead = rangeLength;

        while ((length = in.read(buf)) > 0) {
          if ((toRead -= length) > 0) {
            out.write(buf, 0, length);
          } else {
            out.write(buf, 0, toRead + length);
            break;
          }
        }

      } else {
        response.setContentLength((int) fileSize);
        response.setContentType(
          new MimetypesFileTypeMap().getContentType(attachmentRealName));
        byte[] buf = new byte[1024];

        int length;
        while ((length = in.read(buf)) > 0) {
          out.write(buf, 0, length);
        }
      }
    } catch (RuntimeException e) {
      logger.error("FileuploadRawScreen.doOutput RuntimeException", e);
      throw e;
    } catch (Exception e) {
      logger.error("FileuploadRawScreen.doOutput Exception", e);
    } finally {
      try {
        if (out != null) {
          out.flush();
          out.close();
        }
      } catch (IOException ex) {
        logger.error("FileuploadRawScreen.doOutput", ex);
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

  /**
   * ファイルのアクセス権限をチェックします。
   *
   * @return
   */
  protected boolean doCheckAttachmentAclPermission(RunData rundata,
      int defineAclType) throws ALPermissionException {

    if (defineAclType == 0) {
      return true;
    }

    // アクセス権限のチェックをしない場合
    boolean checkAttachmentAuthority = isCheckAttachmentAuthority();
    if (!checkAttachmentAuthority) {
      return true;
    }

    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();
    if (!aclhandler.hasAuthority(
      ALEipUtils.getUserId(rundata),
      ALAccessControlConstants.POERTLET_FEATURE_ATTACHMENT,
      defineAclType)) {
      throw new ALPermissionException();
    }
    return true;
  }

  /**
   * ファイルアクセス権限チェック用メソッド。<br />
   * ファイルのアクセス権限をチェックするかどうかを判定します。
   *
   * @return
   */
  public boolean isCheckAttachmentAuthority() {
    return true;
  }

  private static void skip(InputStream is, long n) {
    try {
      while (n > 0) {
        long n1 = is.skip(n);
        if (n1 > 0) {
          n -= n1;
        } else if (n1 == 0) {
          if (is.read() == -1) {
            break;
          } else {
            n--;
          }
        }
      }
    } catch (Exception e) {
      logger.error("FileuploadRawScreen.skip", e);
    }
  }
}

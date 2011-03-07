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

package com.aimluck.eip.fileupload;

import java.io.File;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.upload.TurbineUpload;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.upload.FileItem;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.fileupload.beans.FileuploadBean;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ファイルアップロードのフォームデータを管理するクラスです。 <br />
 * 
 */
public class FileuploadFormData extends ALAbstractFormData {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileuploadFormData.class.getName());

  public static final String ATTACHMENT_INIT = "attachmentinit";

  public static final String KEY_ATTACHMENT_ID = "attachment";

  /** 添付ファイル名の最大文字数 */
  private final int FIELD_ATTACHMENT_MAX_LEN = 128;

  /** 添付フォルダ名 */
  private String folderName = null;

  /** 新規追加の添付ファイル */
  private FileuploadBean filebean = null;

  /** 受信した添付ファイル */
  private FileItem attachmentItem = null;

  /** 添付ファイル名 */
  private ALStringField attachmentName = null;

  /** 現在の添付ファイル数 */
  private int nowSize = 0;

  /** 添付可能数 */
  private int maxSize = 0;

  /** ログインユーザ ID */
  private int userId = -1;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    userId = ALEipUtils.getUserId(rundata);

    folderName =
      rundata.getParameters().getString(
        FileuploadUtils.KEY_FILEUPLOAD_FODLER_NAME);
    if (folderName == null || "".equals(folderName)) {
      folderName = "0_" + String.valueOf(System.nanoTime());
    }
  }

  /**
   * データに値をセットする． <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   *          エラーメッセージのリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {

    // Itemの取得
    ParameterParser parser = rundata.getParameters();
    attachmentItem = parser.getFileItem(KEY_ATTACHMENT_ID);

    if (attachmentItem != null) {
      File file = new File(attachmentItem.getName());
      attachmentName.setValue(file.getName());
    }

    // 現在の添付ファイル数
    nowSize = parser.getInt(FileuploadUtils.KEY_NOW_SIZE, 0);
    // 添付可能数
    maxSize = parser.getInt(FileuploadUtils.KEY_MAX_SIZE, 0);

    return true;
  }

  /**
   * 
   */
  @Override
  protected void setValidator() {
    attachmentName.setNotNull(true);
    attachmentName.limitMaxLength(FIELD_ATTACHMENT_MAX_LEN);
    attachmentName.setCharacterType(ALStringField.TYPE_ALL);
  }

  /**
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {

    if (attachmentItem != null) {
      if (attachmentItem.getSize() > 0) {
        long fileSizeSum =
          ALStorageService.getTmpFolderSize(userId, folderName);
        fileSizeSum += attachmentItem.getSize();
        if (fileSizeSum > TurbineUpload.getSizeMax()) {
          msgList.add("追加したファイルの全容量が 7MB よりも大きくなりました。これ以上、ファイルを追加することはできません。");
        } else {
          attachmentName.validate(msgList);
        }
      } else {
        msgList.add("サイズが 0KB のファイルを追加することはできません。");
      }
    } else {
      msgList.add("追加したファイルの全容量が 7MB よりも大きくなりました。これ以上、ファイルを追加することはできません。");
    }

    // 添付可能数のチェック
    if (maxSize > 0) {
      if (nowSize >= maxSize) {
        msgList.add("これ以上、ファイルを追加することはできません。");
      }
    }

    return (msgList.size() == 0);
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    return true;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {

    try {
      int fileId = Long.valueOf(System.nanoTime()).intValue();
      String newAttachmentFileName = String.valueOf(fileId);

      String realfilename =
        FileuploadUtils.getRealFileName(attachmentItem.getName());

      ALStorageService.createNewTmpFile(
        attachmentItem.getInputStream(),
        userId,
        folderName,
        newAttachmentFileName,
        realfilename);

      filebean = new FileuploadBean();
      filebean.setFolderName(folderName);
      filebean.setFileId(fileId);
      filebean.setFileName(realfilename);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 
   */
  @Override
  public void initField() {
    attachmentName = new ALStringField();
    attachmentName.setFieldName("添付ファイル名");
    attachmentName.setTrim(true);
  }

  @SuppressWarnings("unused")
  private String getNewAttachmentFolderName(File folder) {
    int maxNum = 1;
    String[] filenames = folder.list();
    File file = null;
    int tmpInt = 1;
    if (filenames != null) {
      int length = filenames.length;
      for (int i = 0; i < length; i++) {
        file =
          new File(folder.getAbsolutePath() + File.separator + filenames[i]);
        if (file.isDirectory()) {
          try {
            tmpInt = Integer.parseInt(file.getName());
            if (maxNum <= tmpInt) {
              maxNum = tmpInt + 1;
            }
          } catch (NumberFormatException e) {
          }
        }
      }
    }
    return Integer.toString(maxNum);
  }

  @SuppressWarnings("unused")
  private String getNewAttachmentFileName(File folder) {
    int maxNum = 1;
    String[] filenames = folder.list();
    File file = null;
    int tmpInt = 1;
    int length = filenames.length;
    for (int i = 0; i < length; i++) {
      file = new File(folder.getAbsolutePath() + File.separator + filenames[i]);
      if (file.isFile()
        && !file.getName().endsWith(FileuploadUtils.EXT_FILENAME)) {
        try {
          tmpInt = Integer.parseInt(file.getName());
          if (maxNum <= tmpInt) {
            maxNum = tmpInt + 1;
          }
        } catch (NumberFormatException e) {
        }
      }
    }
    return Integer.toString(maxNum);
  }

  public FileuploadBean getNewAttachmentFile() {
    return filebean;
  }

  public String getFolderName() {
    return folderName;
  }

}

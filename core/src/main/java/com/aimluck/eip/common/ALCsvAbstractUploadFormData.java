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
package com.aimluck.eip.common;

import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.upload.FileItem;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * CSVデータを管理するための抽象クラスです。 <br />
 * 
 */
public abstract class ALCsvAbstractUploadFormData extends ALAbstractFormData {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALCsvAbstractUploadFormData.class.getName());

  /** 添付ファイル名の最大文字数 */
  private final int FIELD_ATTACHMENT_MAX_LEN = 128;

  /** 受信した添付ファイル */
  private FileItem attachmentItem = null;

  /** 添付ファイル名 */
  private ALStringField attachmentName = null;

  /** データを分割表示する際の分割数 */
  protected int page_count;

  /** CSVファイルの行数 */
  protected int line_count;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
    page_count = 0;
    line_count = 0;
  }

  /**
   * 一時フォルダを指定します。 <br />
   * 
   * @return
   */
  public abstract String getTempFilePath();

  /**
   * データに値を設定します。 <br />
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
    attachmentItem = parser.getFileItem("attachment");
    if (attachmentItem != null) {
      if (attachmentItem.getSize() > 0) {
        attachmentName.setValue(attachmentItem.getName());
        return true;
      } else {
        msgList.add(ALLocalizationUtils.getl10n("COMMON_NOT_ADD_FILE_ZERO_KB"));
        return false;
      }
    } else {
      msgList.add(ALLocalizationUtils.getl10n("COMMON_NO_FILE"));
      return false;
    }

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
   * @param msgList
   * @return
   */
  @Override
  protected boolean validate(List<String> msgList) {
    attachmentName.validate(msgList);

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
      String filepath = getTempFilePath();
      ALStorageService.createNewFile(attachmentItem.getInputStream(), filepath);
      ALCsvTokenizer reader = new ALCsvTokenizer();
      if (!reader.init(filepath)) {
        return false;
      }

      try {
        page_count = 0;
        line_count = 0;
        while (reader.eof != -1) {
          for (int i = 0; i < ALCsvTokenizer.CSV_SHOW_SIZE; i++) {
            while (reader.eof != -1) {
              reader.nextToken();
              if (reader.eof == -1) {
                break;
              }
              if (reader.line) {
                break;
              }
            }
            if (reader.eof == -1) {
              break;
            }
            line_count++;
          }
          page_count++;
        }
      } catch (Exception e) {

      }

    } catch (Exception ex) {
      logger.error("ALCsvAbstractUploadFormData.updateFormData", ex);
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
    try {
      ALStorageService.deleteFile(getTempFilePath());
    } catch (Exception ex) {
      logger.error("ALCsvAbstractUploadFormData.deleteFormData", ex);
      return false;
    }

    return true;
  }

  /**
   *
   */
  @Override
  public void initField() {
    attachmentName = new ALStringField();
    attachmentName.setFieldName(ALLocalizationUtils
      .getl10n("COMMON_CSV_FILE_NAME"));
    attachmentName.setTrim(true);
  }

  public int getPageCount() {
    return page_count;
  }

  public int getLineCount() {
    return line_count;
  }
}

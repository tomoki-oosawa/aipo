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

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.turbine.modules.screens.RawScreen;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ブラウザにXLSファイルを返すクラスです。 <br />
 * 
 */
public abstract class ALXlsScreen extends RawScreen implements ALAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALXlsScreen.class.getName());

  /** アクセス権限の有無 */
  protected boolean hasAuthority;

  @Override
  protected void doOutput(RunData rundata) throws Exception {
    VelocityContext context = new VelocityContext();

    try {
      init(rundata, context);

      doCheckAclPermission(rundata, context, getDefineAclType());

      // xlsファイルの作成
      HSSFWorkbook wb = createXLSFile(rundata, context);

      HttpServletResponse response = rundata.getResponse();
      // ファイル名の送信
      response.setHeader("Content-disposition", "attachment; filename=\""
        + getFileName()
        + "\"");
      response.setHeader("Cache-Control", "aipo");
      response.setHeader("Pragma", "aipo");

      if (wb != null) {
        wb.write(response.getOutputStream());
      }
    } catch (ALPermissionException e) {
      ALEipUtils.redirectPermissionError(rundata);
    } catch (Exception e) {
      logger.error("ALXlsScreen.doOutput", e);
    }
  }

  /**
   * 初期化処理を行います。
   * 
   * @param action
   * @param rundata
   * @param context
   */
  public void init(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

  }

  /**
   * xlsファイルを」作成します。
   * 
   * @param rundata
   */
  protected HSSFWorkbook createXLSFile(RunData rundata, Context context) {
    try {
      HSSFWorkbook wb = new HSSFWorkbook();

      createHSSFWorkbook(rundata, context, wb);

      return wb;
    } catch (Exception e) {
      logger.error("ALXlsScreen.createXLSFile", e);
      return null;
    }
  }

  /**
   * xlsファイルの中身（シート）を生成します。
   * 
   * @param rundata
   * @param context
   * @param wb
   * @return
   */
  protected abstract boolean createHSSFWorkbook(RunData rundata,
      Context context, HSSFWorkbook wb);

  /**
   * xlsファイル名を取得します。
   * 
   * @return
   */
  protected abstract String getFileName();

  /**
   * 
   * @param wb
   * @param sheet_name
   * @param headers
   *          ヘッダ部作成
   * @param cell_enc_types
   *          セルの表示タイプ
   * @return
   */
  protected HSSFSheet createHSSFSheet(HSSFWorkbook wb, String sheet_name,
      String[] headers, short[] cell_enc_types) {

    HSSFSheet sheet = wb.createSheet(sheet_name);
    wb.setSheetName(0, sheet_name, HSSFWorkbook.ENCODING_UTF_16);

    HSSFRow row1 = sheet.createRow(0);
    int len = headers.length;
    for (int i = 0; i < len; i++) {
      HSSFCell cell_row1 = row1.createCell((short) i);
      cell_row1.setEncoding(HSSFCell.ENCODING_UTF_16);
      cell_row1.setCellValue(headers[i]);
    }

    return sheet;
  }

  /**
   * xlsシートに行を追加する。
   * 
   * @param newrow
   * @param cell_enc_types
   * @param rows
   */
  protected void addRow(HSSFRow newrow, short[] cell_enc_types, String[] rows) {
    int len = rows.length;
    for (int j = 0; j < len; j++) {
      HSSFCell cell_newrow = newrow.createCell((short) j);
      if (cell_enc_types[j] == HSSFCell.CELL_TYPE_NUMERIC) {
        try {
          cell_newrow.setCellValue(Double.parseDouble(rows[j]));
        } catch (Exception e) {
          cell_newrow.setCellValue("");
        }
      } else if (cell_enc_types[j] == HSSFCell.ENCODING_UTF_16) {
        cell_newrow.setEncoding(cell_enc_types[j]);
        cell_newrow.setCellValue(rows[j]);
      } else {
        cell_newrow.setCellValue(rows[j]);
      }
    }
  }

  protected void addFooter(HSSFRow newrow, short[] cell_enc_types, String[] rows) {
    int len = rows.length;
    for (int j = 0; j < len; j++) {
      HSSFCell cell_newrow = newrow.createCell((short) j);
      if (cell_enc_types[j] == HSSFCell.CELL_TYPE_NUMERIC) {
        cell_enc_types[j] = HSSFCell.CELL_TYPE_FORMULA;
        cell_newrow.setCellValue(rows[j]);
      } else if (cell_enc_types[j] == HSSFCell.ENCODING_UTF_16) {
        cell_newrow.setEncoding(cell_enc_types[j]);
        cell_newrow.setCellValue(rows[j]);
      } else {
        cell_newrow.setCellValue(rows[j]);
      }
    }
  }

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
   * @param obj
   */
  @Override
  public void setResultData(Object obj) {

  }

  /**
   * 
   * @param obj
   */
  @Override
  public void addResultData(Object obj) {

  }

  /**
   * 
   * @param objList
   */
  @Override
  public void setResultDataList(List<Object> objList) {

  }

  /**
   * 
   * @param msg
   */
  @Override
  public void addErrorMessage(String msg) {

  }

  /**
   * 
   * @param msg
   */
  @Override
  public void addErrorMessages(List<String> msgs) {

  }

  /**
   * 
   * @param msgs
   */
  @Override
  public void setErrorMessages(List<String> msgs) {

  }

  /**
   * 
   * @param mode
   */
  @Override
  public void setMode(String mode) {

  }

  /**
   * 
   * @return
   */
  @Override
  public String getMode() {
    return null;
  }

  /**
   * 
   * @param context
   */
  @Override
  public void putData(RunData rundata, Context context) {

  }

  /**
   * アクセス権限をチェックします。
   * 
   * @return
   */
  protected boolean doCheckAclPermission(RunData rundata, Context context,
      int defineAclType) throws ALPermissionException {
    if (defineAclType == 0) {
      return true;
    }

    String pfeature = getAclPortletFeature();
    if (pfeature == null || "".equals(pfeature)) {
      return true;
    }

    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

    hasAuthority =
      aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        pfeature,
        defineAclType);

    if (!hasAuthority) {
      throw new ALPermissionException();
    }

    return true;
  }

  /**
   * アクセス権限用メソッド。<br />
   * アクセス権限の有無を返します。
   * 
   * @return
   */
  public boolean hasAuthority() {
    return hasAuthority;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限を返します。
   * 
   * @return
   */
  protected int getDefineAclType() {
    return ALAccessControlConstants.VALUE_ACL_EXPORT;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   * 
   * @return
   */
  public String getAclPortletFeature() {
    return null;
  }
}

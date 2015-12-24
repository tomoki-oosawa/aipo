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

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.timecard.TimecardListResultData;
import com.aimluck.eip.timecard.TimecardResultData;
import com.aimluck.eip.timecard.TimecardSelectData;
import com.aimluck.eip.timecard.util.TimecardUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タイムカードのファイル出力を取り扱うクラスです
 */
public class TimecardXlsExportScreen extends ALXlsScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(TimecardXlsExportScreen.class.getName());

  public static final String FILE_NAME = "timecard.xls";

  /** ログインユーザーID */
  private String userid;

  /** アクセス権限の機能名 */
  private String aclPortletFeature = null;

  /**
   * 初期化処理を行います。
   * 
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    String target_user_id =
      rundata.getParameters().getString(TimecardUtils.TARGET_USER_ID);
    userid = Integer.toString(ALEipUtils.getUserId(rundata));

    // アクセス権
    if (target_user_id == null
      || "".equals(target_user_id)
      || userid.equals(target_user_id)) {
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_TIMECARD_TIMECARD_SELF;
    } else {
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_TIMECARD_TIMECARD_OTHER;
    }

    super.init(rundata, context);
  }

  @Override
  protected boolean createHSSFWorkbook(RunData rundata, Context context,
      HSSFWorkbook wb) {
    try {
      setupTimecardSheet(rundata, context, wb);
    } catch (Exception e) {
      logger.error("TimecardCsvExportScreen", e);
      return false;
    }
    return true;
  }

  private void setupTimecardSheet(RunData rundata, Context context,
      HSSFWorkbook wb) throws Exception {

    TimecardSelectData listData = new TimecardSelectData();
    listData.initField();
    listData.setRowsNum(1000);
    listData.doViewList(this, rundata, context);

    String sheet_name = "タイムカード";
    // ヘッダ部作成
    String[] headers = { "日付", "合計", "状態", "勤怠時間", "修正理由" };
    // 0：日本語，1：英数字
    short[] cell_enc_types =
      {
        HSSFCell.ENCODING_UTF_16,
        HSSFCell.CELL_TYPE_NUMERIC,
        HSSFCell.ENCODING_UTF_16,
        HSSFCell.ENCODING_UTF_16,
        HSSFCell.ENCODING_UTF_16 };
    HSSFSheet sheet = createHSSFSheet(wb, sheet_name, headers, cell_enc_types);

    int rowcount = 0;

    // スタイルの設定
    HSSFCellStyle style_col = wb.createCellStyle();
    style_col.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
    style_col.setAlignment(HSSFCellStyle.ALIGN_JUSTIFY);

    TimecardListResultData tclistrd = null;
    List<String> daykeys = listData.getDateListKeys();
    int daykeysize = daykeys.size();
    for (int i = 0; i < daykeysize; i++) {
      tclistrd = listData.getDateListValue(daykeys.get(i));
      List<TimecardResultData> viewlist = tclistrd.getViewList();
      int viewlistsize = viewlist.size();

      for (int j = 0; j < viewlistsize; j++) {
        TimecardResultData rd = viewlist.get(j);
        String workStr = null;
        if ("0".equals(rd.getWorkFlag().toString())) {
          workStr = "退勤";
        } else {
          workStr = "出勤";
        }

        String[] rows =
          {
            tclistrd.getDateStr(),
            tclistrd.getSummayTimes(),
            workStr,
            rd.getWorkDateStr(),
            rd.getReason().toString() };

        rowcount = rowcount + 1;
        addRow(sheet.createRow(rowcount), cell_enc_types, rows);
      }
      sheet.addMergedRegion(new Region(
        rowcount - viewlistsize + 1,
        (short) 0,
        rowcount,
        (short) 0));
      HSSFRow row = sheet.getRow(rowcount - viewlistsize + 1);
      HSSFCell cell1 = row.getCell((short) 0);
      cell1.setCellStyle(style_col);

      sheet.addMergedRegion(new Region(
        rowcount - viewlistsize + 1,
        (short) 1,
        rowcount,
        (short) 1));
      HSSFCell cell2 = row.getCell((short) 1);
      cell2.setCellStyle(style_col);
    }

    int uid = ALEipUtils.getUserId(rundata);
    ALEventlogFactoryService.getInstance().getEventlogHandler().logXlsScreen(
      uid,
      "タイムカード出力",
      ALEventlogConstants.PORTLET_TYPE_TIMECARD_XLS_SCREEN);
  }

  @Override
  protected String getFileName() {
    return FILE_NAME;
  }

  /**
   * アクセス権限チェック用メソッド。 アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return aclPortletFeature;
  }

}

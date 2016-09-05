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
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.exttimecard.ExtTimecardSummaryListSelectData;
import com.aimluck.eip.exttimecard.ExtTimecardSummaryResultData;
import com.aimluck.eip.exttimecard.util.ExtTimecardUtils;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タイムカードのファイル出力を取り扱うクラスです
 */
public class ExtTimecardSummaryNewXlsExportScreen extends ALXlsScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ExtTimecardSummaryNewXlsExportScreen.class.getName());

  public static final String FILE_NAME = "timecard_monthly.xls";

  /** ログインユーザーID */
  private String userid;

  /** <code>target_group_name</code> 表示対象の部署名 */
  private String target_group_name;

  private String view_month;

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
      rundata.getParameters().getString(ExtTimecardUtils.TARGET_USER_ID);

    target_group_name =
      rundata.getParameters().getString(ExtTimecardUtils.TARGET_GROUP_NAME);

    view_month = rundata.getParameters().getString("view_month");

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

    ExtTimecardSummaryListSelectData listData =
      new ExtTimecardSummaryListSelectData();
    listData.init(this, rundata, context);
    listData.setRowsNum(1000);
    listData.doViewList(this, rundata, context);
    listData.setuserList(target_group_name);

    String sheet_name = "タイムカード";
    // ヘッダ部作成
    String[] headers =
      {
        "氏名",
        "年",
        "月",
        "勤務形態",
        "出勤日数",
        "所定休日出勤日数",
        "法定休日出勤日数",
        "総労働時間",
        "所定内労働時間",
        "法定内残業時間",
        "残業時間",
        "所定休日労働時間",
        "法定休日労働時間",
        "深夜労働時間",
        "休憩時間",
        "遅刻日数",
        "早退日数",
        "欠勤日数",
        "有休日数",
        "代休日数",
        "その他日数",
        "未入力" };
    // 0：日本語，1：英数字
    short[] cell_enc_types =
      {
        HSSFCell.ENCODING_UTF_16,
        HSSFCell.CELL_TYPE_NUMERIC,
        HSSFCell.CELL_TYPE_NUMERIC,
        HSSFCell.ENCODING_UTF_16,
        HSSFCell.CELL_TYPE_NUMERIC,
        HSSFCell.CELL_TYPE_NUMERIC,
        HSSFCell.CELL_TYPE_NUMERIC,
        HSSFCell.CELL_TYPE_NUMERIC,
        HSSFCell.CELL_TYPE_NUMERIC,
        HSSFCell.CELL_TYPE_NUMERIC,
        HSSFCell.CELL_TYPE_NUMERIC,
        HSSFCell.CELL_TYPE_NUMERIC,
        HSSFCell.CELL_TYPE_NUMERIC,
        HSSFCell.CELL_TYPE_NUMERIC,
        HSSFCell.CELL_TYPE_NUMERIC,
        HSSFCell.CELL_TYPE_NUMERIC,
        HSSFCell.CELL_TYPE_NUMERIC,
        HSSFCell.CELL_TYPE_NUMERIC,
        HSSFCell.CELL_TYPE_NUMERIC,
        HSSFCell.CELL_TYPE_NUMERIC,
        HSSFCell.CELL_TYPE_NUMERIC,
        HSSFCell.CELL_TYPE_NUMERIC };
    HSSFSheet sheet = createHSSFSheet(wb, sheet_name, headers, cell_enc_types);

    int rowcount = 0;

    // スタイルの設定
    HSSFCellStyle style_col = wb.createCellStyle();
    style_col.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
    style_col.setAlignment(HSSFCellStyle.ALIGN_JUSTIFY);

    ExtTimecardSummaryResultData tclistrd = null;
    List<ExtTimecardSummaryResultData> daykeys =
      listData.getGroupExtTimecards();
    int daykeysize = daykeys.size();
    for (int i = 0; i < daykeysize; i++) {
      tclistrd = daykeys.get(i);

      String user_name = tclistrd.getUserName();// 氏名
      String year = view_month.substring(0, 4); // 年
      String month = view_month.substring(5); // 月
      String service_form = tclistrd.getSystemName();// 勤務形態
      // 出勤日数
      String total_work_day = tclistrd.getTotalWorkDay().getValueAsString();
      // 所定休日出勤日数
      String official_off_day = tclistrd.getOfficialOffDay().getValueAsString();
      // 法定休日出勤日数
      String statutory_off_day =
        tclistrd.getStatutoryOffDay().getValueAsString();
      // 総労働時間
      String total_work_hour = tclistrd.getTotalWorkHour().getValueAsString();
      // 所定内労働時間
      String work_hour = tclistrd.getWorkHour().getValueAsString();
      // 法定内残業時間
      String overtime_statutory_work_hour =
        tclistrd.getOvertimeWithinStatutoryWorkingHour().getValueAsString();
      // 残業時間
      // String overtime_day = tclistrd.getOvertimeDay().getValueAsString();
      String overtime_hour = tclistrd.getOvertimeHour().getValueAsString();
      // 所定休日労働時間
      String total_official_off_hour =
        tclistrd.getTotalOfficialOffHour().getValueAsString();
      // 法定休日労働時間
      String total_statutory_off_hour =
        tclistrd.getTotalStatutoryOffHour().getValueAsString();
      // 深夜労働時間
      String midnight_work_hour =
        tclistrd.getTotalMidnightWorkHour().getValueAsString();
      // 休憩時間
      String rest_hour = tclistrd.getRestHour().getValueAsString();
      // String off_day = tclistrd.getOffDay().getValueAsString();// 休出日数
      // String off_hour = tclistrd.getOffHour().getValueAsString();// 休出時間
      // 遅刻日数
      String late_coming_day = tclistrd.getLateComingDay().getValueAsString();
      // 早退日数
      String early_leaving_day =
        tclistrd.getEarlyLeavingDay().getValueAsString();
      // 欠勤日数
      String absent_day = tclistrd.getAbsentDay().getValueAsString();
      // 有休日数
      String paid_holiday = tclistrd.getPaidHoliday().getValueAsString();
      // 代休日数
      String compensatory_holiday =
        tclistrd.getCompensatoryHoliday().getValueAsString();
      // その他日数
      String other_day = tclistrd.getOtherDay().getValueAsString();
      // 未入力
      String noinput = tclistrd.getNoInput().getValueAsString();

      String[] rows =
        {
          user_name,
          year,
          month,
          service_form,
          total_work_day,
          official_off_day,
          statutory_off_day,
          total_work_hour,
          work_hour,
          overtime_statutory_work_hour,
          overtime_hour,
          total_official_off_hour,
          total_statutory_off_hour,
          midnight_work_hour,
          rest_hour,
          late_coming_day,
          early_leaving_day,
          absent_day,
          paid_holiday,
          compensatory_holiday,
          other_day,
          noinput };
      rowcount = rowcount + 1;
      addRow(sheet.createRow(rowcount), cell_enc_types, rows);
    }

    // イベントログ
    int uid = ALEipUtils.getUserId(rundata);
    ALEventlogFactoryService.getInstance().getEventlogHandler().logXlsScreen(
      uid,
      "タイムカード出力",
      ALEventlogConstants.PORTLET_TYPE_EXTTIMECARD);
  }

  @Override
  protected String getFileName() {
    return FILE_NAME;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   *
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return aclPortletFeature;
  }

}
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
import java.util.Map;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTExtTimecard;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.exttimecard.ExtTimecardListResultData;
import com.aimluck.eip.exttimecard.ExtTimecardResultData;
import com.aimluck.eip.exttimecard.ExtTimecardSelectData;
import com.aimluck.eip.exttimecard.util.ExtTimecardUtils;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タイムカードのファイル出力を取り扱うクラスです
 */
public class ExtTimecardXlsExportScreen extends ALXlsScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ExtTimecardXlsExportScreen.class.getName());

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
      rundata.getParameters().getString(ExtTimecardUtils.TARGET_USER_ID);
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

    ExtTimecardSelectData listData = new ExtTimecardSelectData();
    listData.initField();
    listData.setRowsNum(1000);
    listData.doViewList(this, rundata, context);

    String sheet_name = "タイムカード";
    // ヘッダ部作成
    String[] headers =
      {
        "氏名",
        "日付",
        "曜日",
        "勤務形態",
        "出勤時間",
        "退勤時間",
        "出勤日数",
        "就業時間",
        "残業日数",
        "残業時間",
        "休出日数",
        "休出時間",
        "遅刻日数",
        "早退日数",
        "欠勤日数",
        "有休日数",
        "代休日数",
        "その他日数",
        "修正理由、備考",
        "外出１",
        "復帰１",
        "外出２",
        "復帰２",
        "外出３",
        "復帰３",
        "外出４",
        "復帰４",
        "外出５",
        "復帰５" };
    // 0：日本語，1：英数字
    short[] cell_enc_types =
      {
        HSSFCell.ENCODING_UTF_16,
        HSSFCell.ENCODING_UTF_16,
        HSSFCell.ENCODING_UTF_16,
        HSSFCell.ENCODING_UTF_16,
        HSSFCell.ENCODING_UTF_16,
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
        HSSFCell.ENCODING_UTF_16,
        HSSFCell.ENCODING_UTF_16,
        HSSFCell.ENCODING_UTF_16,
        HSSFCell.ENCODING_UTF_16,
        HSSFCell.ENCODING_UTF_16,
        HSSFCell.ENCODING_UTF_16,
        HSSFCell.ENCODING_UTF_16,
        HSSFCell.ENCODING_UTF_16,
        HSSFCell.ENCODING_UTF_16,
        HSSFCell.ENCODING_UTF_16,
        HSSFCell.ENCODING_UTF_16 };
    HSSFSheet sheet = createHSSFSheet(wb, sheet_name, headers, cell_enc_types);

    int rowcount = 0;

    // スタイルの設定
    HSSFCellStyle style_col = wb.createCellStyle();
    style_col.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
    style_col.setAlignment(HSSFCellStyle.ALIGN_JUSTIFY);

    String user_name =
      ALEipUtils.getUserFullName(Integer.parseInt(listData.getTargetUserId()));// 氏名
    String system_name =
      ExtTimecardUtils.getEipTExtTimecardSystemByUserId(
        Integer.parseInt(listData.getTargetUserId())).getSystemName();

    ExtTimecardListResultData tclistrd = null;
    List<ExtTimecardListResultData> daykeys = listData.getDateListKeys();
    int daykeysize = daykeys.size();
    for (int i = 0; i < daykeysize; i++) {
      tclistrd = daykeys.get(i);

      String date = "";// 日付
      String day = ""; // 曜日
      String service_form = system_name; // 勤務形態
      String clock_in_time = "";// 出勤時間
      String clock_out_time = "";// 退勤時間
      String work_day = "0"; // 出勤日数 0 or 1
      String work_hour = "0";// 就業時間
      String overtime_day = "0";// 残業日数
      String overtime_hour = "0";// 残業時間
      String off_day = "0";// 休出日数
      String off_hour = "0";// 休出時間
      String late_coming_day = "0";// 遅刻日数
      String early_leaving_day = "0";// 早退日数
      String absent_day = "0";// 欠勤日数
      String paid_holiday = "0";// 有休日数
      String compensatory_holiday = "0";// 代休日数
      String other_day = "0";// その他日数
      String remark = "";// 修正理由、備考
      String[] out_going =
        new String[EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY];// 外出
      String[] come_back =
        new String[EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY];// 復帰

      if (tclistrd.getRd() == null) {

        // ExtTimecardResultData rd = tclistrd.getRd();

        date = tclistrd.getDateStr("yyyy/MM/dd");
        day = tclistrd.getDateStr("EE");
      } else {

        ExtTimecardResultData rd = tclistrd.getRd();

        // ExtTimecardSummaryResultData srd = tclistrd.get

        date = tclistrd.getDateStr("yyyy/MM/dd");
        day = tclistrd.getDateStr("EE");
        String type = rd.getType().toString();
        if (!rd.getIsNullClockInTime()) {
          clock_in_time = rd.getClockInTime("HH:mm");
          clock_out_time = rd.getClockOutTime("HH:mm");
          if (tclistrd.getWorkHour() > 0.0) {
            work_day = "1";
            work_hour = Float.toString(tclistrd.getWorkHour());
          } else {
            work_hour = "0";
          }
          if (tclistrd.getOvertimeHourWithoutRestHour() > 0.0) {
            overtime_day = "1";
            overtime_hour =
              Float.toString(tclistrd.getOvertimeHourWithoutRestHour());
          } else {
            overtime_hour = "0";
          }
          if (tclistrd.getOffHour() > 0.0) {
            off_day = "1";
            off_hour = Float.toString(tclistrd.getOffHour());
          } else {
            off_hour = "0";
          }
        }
        if (rd.getRefixFlag().getValue() == "1") {
          remark = rd.getReason().getValue();
          if (rd.getReasonFlg() && rd.getRemarksFlg()) {
            remark += "、";
          }
        }
        if (rd.getRemarksFlg()) {
          remark += rd.getRemarks().getValue();
        }
        late_coming_day = tclistrd.isLateComing() ? "1" : "0";
        early_leaving_day = tclistrd.isEarlyLeaving() ? "1" : "0";
        absent_day = type.equals(EipTExtTimecard.TYPE_ABSENT) ? "1" : "0";
        paid_holiday = type.equals(EipTExtTimecard.TYPE_HOLIDAY) ? "1" : "0";
        compensatory_holiday =
          type.equals(EipTExtTimecard.TYPE_COMPENSATORY) ? "1" : "0";
        other_day = type.equals(EipTExtTimecard.TYPE_ETC) ? "1" : "0";

        List<Map<String, String>> list = tclistrd.getOutgoingComeback_xls();
        for (int j = 0; j < EipTExtTimecard.OUTGOING_COMEBACK_PER_DAY; j++) {
          Map<String, String> map = list.get(j);
          out_going[j] = map.get("outgoing");
          come_back[j] = map.get("comeback");
        }
      }

      String[] rows =
        {
          user_name,
          date,
          day,
          service_form,
          clock_in_time,
          clock_out_time,
          work_day,
          work_hour,
          overtime_day,
          overtime_hour,
          off_day,
          off_hour,
          late_coming_day,
          early_leaving_day,
          absent_day,
          paid_holiday,
          compensatory_holiday,
          other_day,
          remark,
          out_going[0],
          come_back[0],
          out_going[1],
          come_back[1],
          out_going[2],
          come_back[2],
          out_going[3],
          come_back[3],
          out_going[4],
          come_back[4] };
      rowcount = rowcount + 1;
      addRow(sheet.createRow(rowcount), cell_enc_types, rows);
    }
    /*
     * rowcount = rowcount + 1; String NOTHING = null; String work_day =
     * "=SUM(G2:G" + rowcount + ")"; String work_hour = "=SUM(H2:H" + rowcount +
     * ")"; String overtime_day = "=SUM(I2:I" + rowcount + ")"; String
     * overtime_hour = "=SUM(J2:J" + rowcount + ")"; String off_day =
     * "=SUM(K2:K" + rowcount + ")"; String off_hour = "=SUM(L2:L" + rowcount +
     * ")"; String late_coming_day = "=SUM(M2:M" + rowcount + ")"; String
     * early_leaving_day = "=SUM(N2:N" + rowcount + ")"; String absent_day =
     * "=SUM(O2:O" + rowcount + ")"; String paid_holiday = "=SUM(P2:P" +
     * rowcount + ")"; String compensatory_holiday = "=SUM(Q2:Q" + rowcount +
     * ")"; String other_day = "=SUM(R2:R" + rowcount + ")"; String[] rows = {
     * NOTHING, NOTHING, NOTHING, NOTHING, NOTHING, NOTHING, work_day,
     * work_hour, overtime_day, overtime_hour, off_day, off_hour,
     * late_coming_day, early_leaving_day, absent_day, paid_holiday,
     * compensatory_holiday, other_day, };
     * 
     * addFooter(sheet.createRow(rowcount), cell_enc_types, rows);
     */

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

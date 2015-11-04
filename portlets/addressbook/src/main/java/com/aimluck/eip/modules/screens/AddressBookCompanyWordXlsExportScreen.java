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

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.addressbook.AddressBookCompanyResultData;
import com.aimluck.eip.addressbook.AddressBookCompanyWordSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アドレスブック・会社情報のファイル出力を取り扱うクラスです
 */
public class AddressBookCompanyWordXlsExportScreen extends ALXlsScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AddressBookWordXlsExportScreen.class.getName());

  public static final String FILE_NAME = "addressbookcompany.xls";

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
      rundata.getParameters().getString("target_user_id"/*
                                                         * AddressBookUtils.
                                                         * TARGET_USER_ID
                                                         */);
    userid = Integer.toString(ALEipUtils.getUserId(rundata));

    // アクセス権
    if (
    // target_user_id == null || "".equals(target_user_id)
    // ||
    userid.equals(target_user_id)) {
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_ADDRESSBOOK_ADDRESS_OUTSIDE;
    }
    // else {
    // aclPortletFeature =
    // ALAccessControlConstants.POERTLET_FEATURE_TIMECARD_TIMECARD_OTHER;
    // }

    super.init(rundata, context);
  }

  @Override
  protected boolean createHSSFWorkbook(RunData rundata, Context context,
      HSSFWorkbook wb) {
    try {
      setupAddressBookSheet(rundata, context, wb);
    } catch (Exception e) {
      logger.error(
        "AddressBookCompanyWordXlsExportScreen.createHSSFWorkbook",
        e);
      return false;
    }
    return true;
  }

  private void setupAddressBookSheet(RunData rundata, Context context,
      HSSFWorkbook wb) throws Exception {

    String sheet_name = "アドレスブック(会社情報)";
    // ヘッダ部作成
    String[] headers =
      { "会社名", "会社名（フリガナ）", "部署名", "郵便番号", "住所", "電話番号", "FAX", "URL" };
    // 0：日本語，1：英数字
    short[] cell_enc_types =
      {
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

    AddressBookCompanyWordSelectData listData =
      new AddressBookCompanyWordSelectData();

    listData.initField();
    listData.setRowsNum(1000);
    listData.doSelectList(this, rundata, context);
    int page_num = listData.getPagesNum();
    int current_page = 1;
    while (true) {
      int listsize = listData.getList().size();
      AddressBookCompanyResultData rd;
      for (int j = 0; j < listsize; j++) {
        rd = (AddressBookCompanyResultData) listData.getList().get(j);
        String[] rows =
          {
            rd.getCompanyName().getValue(),
            rd.getCompanyNameKana().getValue(),
            rd.getPostName().getValue(),
            rd.getZipcode().getValue(),
            rd.getAddress().getValue(),
            rd.getTelephone().getValue(),
            rd.getFaxNumber().getValue(),
            rd.getUrl().getValue() };

        rowcount = rowcount + 1;
        addRow(sheet.createRow(rowcount), cell_enc_types, rows);
      }

      current_page++;
      if (current_page > page_num) {
        break;
      }
      listData.setCurrentPage(current_page);
      listData.doSelectList(this, rundata, context);
    }

    int uid = ALEipUtils.getUserId(rundata);
    ALEventlogFactoryService.getInstance().getEventlogHandler().logXlsScreen(
      uid,
      "アドレスブック(会社情報)出力",
      163/* ALEventlogConstants.PORTLET_TYPE_ADDRESSBOOK_XLS_SCREEN */);
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

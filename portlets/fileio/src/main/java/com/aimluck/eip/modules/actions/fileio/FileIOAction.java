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
package com.aimluck.eip.modules.actions.fileio;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALCsvTokenizer;
import com.aimluck.eip.fileio.FileIOAccountCsvFormData;
import com.aimluck.eip.fileio.FileIOAccountCsvSelectData;
import com.aimluck.eip.fileio.FileIOAccountCsvUploadFormData;
import com.aimluck.eip.fileio.FileIOAccountPostCsvFormData;
import com.aimluck.eip.fileio.FileIOAccountPostCsvSelectData;
import com.aimluck.eip.fileio.FileIOAccountPostCsvUploadFormData;
import com.aimluck.eip.fileio.FileIOAddressBookCsvFormData;
import com.aimluck.eip.fileio.FileIOAddressBookCsvSelectData;
import com.aimluck.eip.fileio.FileIOAddressBookCsvUploadFormData;
import com.aimluck.eip.fileio.FileIOScheduleCsvFormData;
import com.aimluck.eip.fileio.FileIOScheduleCsvSelectData;
import com.aimluck.eip.fileio.FileIOScheduleCsvUploadFormData;
import com.aimluck.eip.fileio.util.FileIOAccountCsvUtils;
import com.aimluck.eip.fileio.util.FileIOAddressBookCsvUtils;
import com.aimluck.eip.fileio.util.FileIOScheduleCsvUtils;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALCSVUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * FileIOのアクションクラスです <BR>
 *
 *
 */
public class FileIOAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileIOAction.class.getName());

  /**
   * 通常表示の際の処理を記述します。 <BR>
   *
   * @param portlet
   * @param context
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {
    if (getMode() == null) {
      doAccount_form_csv(rundata, context);
    }
  }

  /**
   * アドレス帳の一括入力 <BR>
   *
   * @param rundata
   * @param context
   */
  public void doAddressbook_form(RunData rundata, Context context) {
    try {
      FileIOAddressBookCsvFormData formData =
        new FileIOAddressBookCsvFormData();
      formData.initField();
      formData.doViewForm(this, rundata, context);

      setTemplate(rundata, "fileio-addressbook-csv");
    } catch (Exception ex) {
      logger.error("[AccountAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * アドレス帳の一括入力する際のファイルアップロード <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_upload_csv(RunData rundata, Context context)
      throws Exception {
    FileIOAddressBookCsvUploadFormData formData =
      new FileIOAddressBookCsvUploadFormData();
    formData.initField();
    /* ファイルのアップロード */
    ALCSVUtils.csvUpload(rundata, context, this, formData);
    context.put("temp_folder", formData.getTempFolderIndex());
    // 読み込み順序の設定
    List<String> sequency = new ArrayList<String>();
    sequency.add("15");
    sequency.add("16");
    sequency.add("17");
    sequency.add("18");
    sequency.add("13");
    sequency.add("2");
    sequency.add("3");
    sequency.add("4");
    sequency.add("5");
    sequency.add("6");
    sequency.add("12");
    sequency.add("7");
    sequency.add("8");
    sequency.add("9");
    sequency.add("10");
    sequency.add("11");
    sequency.add("19");
    sequency.add("20");
    ALCSVUtils.setSequency(rundata, context, sequency);

    setTemplate(rundata, "fileio-addressbook-csv");
    doAddressbook_list_csv(rundata, context, formData.getTempFolderIndex());
  }

  /**
   * 読み込んだ内容をリスト表示 <BR>
   *
   * @param rundata
   * @param context
   * @param folderIndex
   * @throws Exception
   */
  public void doAddressbook_list_csv(RunData rundata, Context context,
      String folderIndex) throws Exception {
    FileIOAddressBookCsvSelectData listData =
      new FileIOAddressBookCsvSelectData();
    listData.initField();
    listData.setTempFolderIndex(folderIndex);
    /* リストの作成 */
    ALCSVUtils.makeList(rundata, context, this, listData);
    setTemplate(rundata, "fileio-addressbook-csv");
  }

  /**
   * 読み込んだ内容からエラーが発生した件のみリスト表示 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_list_csv_error(RunData rundata, Context context)
      throws Exception {
    FileIOAddressBookCsvSelectData listData =
      new FileIOAddressBookCsvSelectData();
    listData.initField();
    listData.setTempFolderIndex(rundata
      .getParameters()
      .getString("temp_folder"));
    ALCSVUtils.makeErrorList(rundata, context, this, listData);
    listData.setNotErrorCount(Integer.parseInt(ALEipUtils.getTemp(
      rundata,
      context,
      "not_error_count")));
    context.put("validateError", true);
    setTemplate(rundata, "fileio-addressbook-csv");
  }

  /**
   * データが多数に及んだ際における分割表示 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_list_csv_page(RunData rundata, Context context)
      throws Exception {
    FileIOAddressBookCsvSelectData listData =
      new FileIOAddressBookCsvSelectData();
    listData.initField();
    listData.setTempFolderIndex(rundata
      .getParameters()
      .getString("temp_folder"));
    context
      .put("temp_folder", rundata.getParameters().getString("temp_folder"));
    ALCSVUtils.makeListPage(rundata, context, this, listData);
    setTemplate(rundata, "fileio-addressbook-csv");
  }

  /**
   * CSVファイルからデータベースへの登録 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_insert_csv(RunData rundata, Context context)
      throws Exception {
    int not_error = 0;
    int line = 0;

    String temp_folder_index = rundata.getParameters().getString("temp_folder");
    String filepath =
      FileIOAddressBookCsvUtils.getAddressBookCsvFolderName(temp_folder_index)
        + ALStorageService.separator()
        + FileIOAddressBookCsvUtils.CSV_ADDRESSBOOK_TEMP_FILENAME;

    ALCsvTokenizer reader = new ALCsvTokenizer();
    if (!reader.init(filepath)) {
      return;
    }
    List<String> sequency = ALCSVUtils.getSequency(rundata, context);
    String token;
    int i, j;
    while (reader.eof != -1) {
      line++;
      FileIOAddressBookCsvFormData formData =
        new FileIOAddressBookCsvFormData();
      formData.initField();
      for (j = 0; j < sequency.size(); j++) {
        token = reader.nextToken();
        i = Integer.parseInt(sequency.get(j));
        formData.addItemToken(token, i);
        if (reader.eof == -1) {
          break;
        }
        if (reader.line) {
          break;
        }
      }
      while ((!reader.line) && (reader.eof != -1)) {
        reader.nextToken();
      }
      if (reader.eof == -1 && j == 0) {
        break;
      }
      // カンマ不足対策
      for (j++; j < sequency.size(); j++) {
        i = Integer.parseInt(sequency.get(j));
        formData.addItemToken("", i);
      }
      /** データベースから読み取る場合 */
      if ((!formData.getFirstName().toString().equals("名前（名）"))
        && (!formData.getCompanyName().toString().equals("会社名"))) {
        if (formData.doInsert(this, rundata, context)) {
          not_error++;
        }
      } else {
        if (not_error > 0) {
          not_error--;
        }
      }
    }

    ALEipUtils.setTemp(rundata, context, "not_error_count", Integer
      .toString(not_error));
    int error_count =
      Integer.parseInt(ALEipUtils.getTemp(rundata, context, "error_count"));
    if (error_count > 0) {
      doAddressbook_list_csv_error(rundata, context);
    } else {
      doAddressbook_form(rundata, context);
    }
  }

  /**
   * アドレス帳（会社情報）の一括入力 <BR>
   *
   * @param rundata
   * @param context
   */
  public void doAddressbook_company_form(RunData rundata, Context context) {
    try {
      FileIOAddressBookCsvFormData formData =
        new FileIOAddressBookCsvFormData();
      formData.initField();
      formData.setIsCompanyOnly(true);
      formData.doViewForm(this, rundata, context);

      setTemplate(rundata, "fileio-addressbook-company-csv");
    } catch (Exception ex) {
      logger.error("[AccountAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * アドレス帳(会社情報)の一括入力する際のファイルアップロード <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_company_upload_csv(RunData rundata, Context context)
      throws Exception {
    FileIOAddressBookCsvUploadFormData formData =
      new FileIOAddressBookCsvUploadFormData();
    formData.initField();
    /* ファイルのアップロード */
    ALCSVUtils.csvUpload(rundata, context, this, formData);
    context.put("temp_folder", formData.getTempFolderIndex());
    // 読み込み順序の設定
    List<String> sequency = new ArrayList<String>();
    sequency.add("6");
    sequency.add("12");
    sequency.add("7");
    sequency.add("8");
    sequency.add("9");
    sequency.add("10");
    sequency.add("11");
    sequency.add("19");
    ALCSVUtils.setSequency(rundata, context, sequency);

    setTemplate(rundata, "fileio-addressbook-company-csv");
    doAddressbook_company_list_csv(rundata, context, formData
      .getTempFolderIndex());
  }

  /**
   * 読み込んだ内容をリスト表示 <BR>
   *
   * @param rundata
   * @param context
   * @param folderIndex
   * @throws Exception
   */
  public void doAddressbook_company_list_csv(RunData rundata, Context context,
      String folderIndex)// 最初に呼び出されたとき
      throws Exception {
    FileIOAddressBookCsvSelectData listData =
      new FileIOAddressBookCsvSelectData();
    listData.initField();
    listData.setIsCompanyOnly(true);
    listData.setTempFolderIndex(folderIndex);
    /* リストの作成 */
    ALCSVUtils.makeList(rundata, context, this, listData);
    setTemplate(rundata, "fileio-addressbook-company-csv");
  }

  /**
   * 読み込んだ内容からエラーが発生した件のみリスト表示 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_company_list_csv_error(RunData rundata,
      Context context) throws Exception {
    FileIOAddressBookCsvSelectData listData =
      new FileIOAddressBookCsvSelectData();
    listData.initField();
    listData.setIsCompanyOnly(true);
    listData.setTempFolderIndex(rundata
      .getParameters()
      .getString("temp_folder"));
    ALCSVUtils.makeErrorList(rundata, context, this, listData);
    listData.setNotErrorCount(Integer.parseInt(ALEipUtils.getTemp(
      rundata,
      context,
      "not_error_count")));
    context.put("validateError", true);
    setTemplate(rundata, "fileio-addressbook-company-csv");
  }

  /**
   * データが多数に及んだ際における分割表示 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_company_list_csv_page(RunData rundata,
      Context context) throws Exception {
    FileIOAddressBookCsvSelectData listData =
      new FileIOAddressBookCsvSelectData();
    listData.initField();
    listData.setIsCompanyOnly(true);
    listData.setTempFolderIndex(rundata
      .getParameters()
      .getString("temp_folder"));
    context
      .put("temp_folder", rundata.getParameters().getString("temp_folder"));
    ALCSVUtils.makeListPage(rundata, context, this, listData);
    setTemplate(rundata, "fileio-addressbook-company-csv");
  }

  /**
   * CSVファイルからデータベースへの登録 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAddressbook_company_insert_csv(RunData rundata, Context context)
      throws Exception {
    int not_error = 0;
    String temp_folder_index = rundata.getParameters().getString("temp_folder");
    String filepath =
      FileIOAddressBookCsvUtils.getAddressBookCsvFolderName(temp_folder_index)
        + ALStorageService.separator()
        + FileIOAddressBookCsvUtils.CSV_ADDRESSBOOK_TEMP_FILENAME;
    ALCsvTokenizer reader = new ALCsvTokenizer();
    if (!reader.init(filepath)) {
      return;
    }
    List<String> sequency = ALCSVUtils.getSequency(rundata, context);
    String token;
    int i, j;
    while (reader.eof != -1) {
      FileIOAddressBookCsvFormData formData =
        new FileIOAddressBookCsvFormData();
      formData.initField();
      formData.setIsCompanyOnly(true);
      for (j = 0; j < sequency.size(); j++) {
        token = reader.nextToken();
        i = Integer.parseInt(sequency.get(j));
        formData.addItemToken(token, i);
        if (reader.eof == -1) {
          break;
        }
        if (reader.line) {
          break;
        }
      }
      while ((!reader.line) && (reader.eof != -1)) {
        reader.nextToken();
      }
      if (reader.eof == -1 && j == 0) {
        break;
      }
      // カンマ不足対策
      for (j++; j < sequency.size(); j++) {
        i = Integer.parseInt(sequency.get(j));
        formData.addItemToken("", i);
      }

      if ((!formData.getFirstName().toString().equals("名前（名）"))
        && (!formData.getCompanyName().toString().equals("会社名"))) {
        if ((formData.doInsert(this, rundata, context))
          && (!formData.getSameCompany())) {
          not_error++;
        }
      } else {
        if (not_error > 0) {
          not_error--;
        }
      }
    }
    logger.warn("[addressbookCompanyImport]addressbook_Company CSV imported");
    ALEipUtils.setTemp(rundata, context, "not_error_count", Integer
      .toString(not_error));
    int error_count =
      Integer.parseInt(ALEipUtils.getTemp(rundata, context, "error_count"));
    if (error_count > 0) {
      doAddressbook_company_list_csv_error(rundata, context);
    } else {
      doAddressbook_company_form(rundata, context);
    }
  }

  /**
   * ユーザーの一括登録 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_form_csv(RunData rundata, Context context)
      throws Exception {
    FileIOAccountCsvFormData formData = new FileIOAccountCsvFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "fileio-account-read-csv");
  }

  /**
   * CSVファイルからデータベースへの登録 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_insert_csv(RunData rundata, Context context)
      throws Exception {
    int not_error = 0;
    String temp_folder_index = rundata.getParameters().getString("temp_folder");
    String filepath =
      FileIOAccountCsvUtils.getAccountCsvFolderName(temp_folder_index)
        + ALStorageService.separator()
        + FileIOAccountCsvUtils.CSV_ACCOUNT_TEMP_FILENAME;

    ALCsvTokenizer reader = new ALCsvTokenizer();
    if (!reader.init(filepath)) {
      return;
    }

    List<String> sequency = ALCSVUtils.getSequency(rundata, context);
    List<String> usernameList = new ArrayList<String>();

    int i, j;
    String token;
    int line = 0;

    // 最終行まで読み込む
    while (reader.eof != -1) {
      line++;
      FileIOAccountCsvFormData formData = new FileIOAccountCsvFormData();
      formData.initField();
      for (j = 0; j < sequency.size(); j++) {
        token = reader.nextToken();
        i = Integer.parseInt(sequency.get(j));
        formData.addItemToken(token, i);
        if (reader.eof == -1) {
          break;
        }
        if (reader.line) {
          break;
        }
      }
      while ((!reader.line) && (reader.eof != -1)) {
        reader.nextToken();
      }
      if (reader.eof == -1 && j == 0) {
        break;
      }
      // カンマ不足対策
      for (j++; j < sequency.size(); j++) {
        i = Integer.parseInt(sequency.get(j));
        formData.addItemToken("", i);
      }

      /** データベースから読み取る場合 */
      try {
        if (formData.getUserName().toString().equals("")) {
          continue;
        }
      } catch (Exception e) {
        continue;
      }
      String username = formData.getUserName().toString();
      if (!username.equals("ユーザー名")) {
        if (!usernameList.contains(username)) {
          usernameList.add(username);
          if (formData.doInsert(this, rundata, context)) {
            not_error++;
          }
        } else {

        }
      } else {
        if (not_error > 0) {
          not_error--;
        }
      }
    }

    /*
     * JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
     * rundata.setRedirectURI(jsLink.getPortletById(
     * ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
     * "eventSubmit_doAccount_list", "1").toString());
     * rundata.getResponse().sendRedirect(rundata.getRedirectURI()); jsLink =
     * null;
     */

    ALEipUtils.setTemp(rundata, context, "not_error_count", Integer
      .toString(not_error));
    int error_count =
      Integer.parseInt(ALEipUtils.getTemp(rundata, context, "error_count"));
    if (error_count > 0) {
      doAccount_csv_list_error(rundata, context);
    } else {
      doAccount_form_csv(rundata, context);
    }
  }

  /**
   * ユーザー一括入力する際のファイルアップロード <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_upload_csv(RunData rundata, Context context)
      throws Exception {
    FileIOAccountCsvUploadFormData formData =
      new FileIOAccountCsvUploadFormData();
    formData.initField();

    /* ファイルのアップロード */
    ALCSVUtils.csvUpload(rundata, context, this, formData);
    context.put("temp_folder", formData.getTempFolderIndex());
    // 読み込み順序の設定
    List<String> sequency = new ArrayList<String>();
    sequency.add("0");
    sequency.add("1");
    sequency.add("2");
    sequency.add("3");
    sequency.add("4");
    sequency.add("5");
    sequency.add("6");
    sequency.add("7");
    sequency.add("8");
    sequency.add("9");
    sequency.add("10");
    sequency.add("11");
    sequency.add("12");
    sequency.add("13");
    ALCSVUtils.setSequency(rundata, context, sequency);

    setTemplate(rundata, "fileio-account-read-csv");
    doAccount_list_csv(rundata, context, formData.getTempFolderIndex());
  }

  /**
   * 読み込んだ内容をリスト表示 <BR>
   *
   * @param rundata
   * @param context
   * @param folderIndex
   * @throws Exception
   */
  public void doAccount_list_csv(RunData rundata, Context context,
      String folderIndex) throws Exception {
    FileIOAccountCsvSelectData listData = new FileIOAccountCsvSelectData();
    listData.initField();
    listData.setTempFolderIndex(folderIndex);
    /* リストの作成 */
    ALCSVUtils.makeList(rundata, context, this, listData);
    setTemplate(rundata, "fileio-account-read-csv");
  }

  /**
   * 読み込んだ内容からエラーが発生した件のみリスト表示 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_csv_list_error(RunData rundata, Context context)
      throws Exception {
    FileIOAccountCsvSelectData listData = new FileIOAccountCsvSelectData();
    listData.initField();
    listData.setTempFolderIndex(rundata
      .getParameters()
      .getString("temp_folder"));
    ALCSVUtils.makeErrorList(rundata, context, this, listData);
    listData.setNotErrorCount(Integer.parseInt(ALEipUtils.getTemp(
      rundata,
      context,
      "not_error_count")));
    context.put("validateError", true);
    setTemplate(rundata, "fileio-account-read-csv");
  }

  /**
   * データが多数に及んだ際における分割表示 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_csv_list_page(RunData rundata, Context context)
      throws Exception {
    FileIOAccountCsvSelectData listData = new FileIOAccountCsvSelectData();
    listData.initField();
    listData.setTempFolderIndex(rundata
      .getParameters()
      .getString("temp_folder"));
    context
      .put("temp_folder", rundata.getParameters().getString("temp_folder"));
    ALCSVUtils.makeListPage(rundata, context, this, listData);
    setTemplate(rundata, "fileio-account-read-csv");
  }

  /**
   * 部署の一括登録
   *
   * @param rundata
   * @param context
   */
  public void doAccount_postcsv_form(RunData rundata, Context context) {
    try {
      FileIOAccountPostCsvFormData formData =
        new FileIOAccountPostCsvFormData();
      // formData.loadParameters(rundata, context);
      formData.initField();
      formData.doViewForm(this, rundata, context);

      // トップ画面からのスケジュール入力であるかを判定する．
      String afterBehavior =
        rundata.getRequest().getParameter(ALCSVUtils.AFTER_BEHAVIOR);
      if (afterBehavior != null) {
        context.put(ALCSVUtils.AFTER_BEHAVIOR, "1");
      }
      setTemplate(rundata, "fileio-account-post-csv");
    } catch (Exception ex) {
      logger.error("[AccountAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * アドレス帳(会社情報)の一括入力する際のファイルアップロード <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_postcsv_upload(RunData rundata, Context context)
      throws Exception {
    FileIOAccountPostCsvUploadFormData formData =
      new FileIOAccountPostCsvUploadFormData();
    formData.initField();
    /* ファイルのアップロード */
    ALCSVUtils.csvUpload(rundata, context, this, formData);
    context.put("temp_folder", formData.getTempFolderIndex());
    // 読み込み順序の設定
    List<String> sequency = new ArrayList<String>();
    sequency.add("0");
    sequency.add("1");
    sequency.add("2");
    sequency.add("3");
    sequency.add("4");
    sequency.add("5");
    ALCSVUtils.setSequency(rundata, context, sequency);

    setTemplate(rundata, "fileio-account-post-csv");
    doAccount_postcsv_list(rundata, context, formData.getTempFolderIndex());
  }

  /**
   * 読み込んだ内容をリスト表示 <BR>
   *
   * @param rundata
   * @param context
   * @param folderIndex
   * @throws Exception
   */
  private void doAccount_postcsv_list(RunData rundata, Context context,
      String folderIndex)// 最初に呼び出されたとき
      throws Exception {
    FileIOAccountPostCsvSelectData listData =
      new FileIOAccountPostCsvSelectData();
    listData.initField();
    listData.setTempFolderIndex(folderIndex);
    /* リストの作成 */
    ALCSVUtils.makeList(rundata, context, this, listData);
    setTemplate(rundata, "fileio-account-post-csv");
  }

  /**
   * 読み込んだ内容からエラーが発生した件のみリスト表示 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_postcsv_list_error(RunData rundata, Context context)
      throws Exception {
    FileIOAccountPostCsvSelectData listData =
      new FileIOAccountPostCsvSelectData();
    listData.initField();
    listData.setTempFolderIndex(rundata
      .getParameters()
      .getString("temp_folder"));
    ALCSVUtils.makeErrorList(rundata, context, this, listData);
    listData.setNotErrorCount(Integer.parseInt(ALEipUtils.getTemp(
      rundata,
      context,
      "not_error_count")));
    context.put("validateError", true);
    setTemplate(rundata, "fileio-account-post-csv");
  }

  /**
   * データが多数に及んだ際における分割表示 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_postcsv_list_page(RunData rundata, Context context)
      throws Exception {
    FileIOAccountPostCsvSelectData listData =
      new FileIOAccountPostCsvSelectData();
    listData.initField();
    listData.setTempFolderIndex(rundata
      .getParameters()
      .getString("temp_folder"));
    context
      .put("temp_folder", rundata.getParameters().getString("temp_folder"));
    ALCSVUtils.makeListPage(rundata, context, this, listData);
    setTemplate(rundata, "fileio-account-post-csv");
  }

  /**
   * CSVファイルからデータベースへの登録 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doAccount_postcsv_insert(RunData rundata, Context context)
      throws Exception {
    int not_error = 0;
    String temp_folder_index = rundata.getParameters().getString("temp_folder");
    String filepath =
      FileIOAccountCsvUtils.getAccountPostCsvFolderName(temp_folder_index)
        + ALStorageService.separator()
        + FileIOAccountCsvUtils.CSV_ACCOUNT_POST_TEMP_FILENAME;

    ALCsvTokenizer reader = new ALCsvTokenizer();
    if (!reader.init(filepath)) {
      return;
    }
    List<String> sequency = ALCSVUtils.getSequency(rundata, context);
    String token;
    int i, j;
    while (reader.eof != -1) {
      FileIOAccountPostCsvFormData formData =
        new FileIOAccountPostCsvFormData();
      formData.initField();
      for (j = 0; j < sequency.size(); j++) {
        token = reader.nextToken();
        i = Integer.parseInt(sequency.get(j));
        formData.addItemToken(token, i);
        if (reader.eof == -1) {
          break;
        }
        if (reader.line) {
          break;
        }
      }
      while ((!reader.line) && (reader.eof != -1)) {
        reader.nextToken();
      }
      if (reader.eof == -1 && j == 0) {
        break;
      }
      // カンマ不足対策
      for (j++; j < sequency.size(); j++) {
        i = Integer.parseInt(sequency.get(j));
        formData.addItemToken("", i);
      }
      /** データベースから読み取る場合 */
      try {
        if (formData.getPostName().toString().equals("")) {
          continue;
        }
      } catch (Exception e) {
        continue;
      }
      if (!formData.getPostName().toString().equals("部署名")) {
        if (formData.doInsert(this, rundata, context)) {
          not_error++;
        }
      } else {
        if (not_error > 0) {
          not_error--;
        }
      }
    }
    logger.warn("[postImport]post CSV imported");
    ALEipUtils.setTemp(rundata, context, "not_error_count", Integer
      .toString(not_error));
    int error_count =
      Integer.parseInt(ALEipUtils.getTemp(rundata, context, "error_count"));
    if (error_count > 0) {
      doAccount_postcsv_list_error(rundata, context);
    } else {
      doAccount_postcsv_form(rundata, context);
    }
  }

  /**
   * 単体スケジュールの一括入力 <BR>
   *
   * @param rundata
   * @param context
   */
  public void doSchedule_form(RunData rundata, Context context) {
    try {
      FileIOScheduleCsvFormData formData = new FileIOScheduleCsvFormData();
      formData.initField();
      formData.doViewForm(this, rundata, context);
      setTemplate(rundata, "fileio-schedule-csv");
    } catch (Exception ex) {
      logger.error("[ScheduleAction] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * スケジュールの一括入力する際のファイルアップロード
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doSchedule_upload_csv(RunData rundata, Context context)
      throws Exception {
    FileIOScheduleCsvUploadFormData formData =
      new FileIOScheduleCsvUploadFormData();
    formData.initField();
    /* ファイルのアップロード */
    ALCSVUtils.csvUpload(rundata, context, this, formData);
    context.put("temp_folder", formData.getTempFolderIndex());
    List<String> sequency = new ArrayList<String>();
    sequency.add("4");
    sequency.add("6");
    sequency.add("5");
    sequency.add("7");
    sequency.add("2");
    sequency.add("1");
    sequency.add("3");
    sequency.add("0");
    sequency.add("8");
    ALCSVUtils.setSequency(rundata, context, sequency);
    ALEipUtils.setTemp(rundata, context, "is_autotime", rundata
      .getParameters()
      .getString("autotime_flg", "0"));
    doSchedule_list_csv(rundata, context, formData.getTempFolderIndex());
  }

  /**
   * 読み込んだ内容をリスト表示 <BR>
   *
   * @param rundata
   * @param context
   * @param folderIndex
   * @throws Exception
   */
  public void doSchedule_list_csv(RunData rundata, Context context,
      String folderIndex) throws Exception {
    FileIOScheduleCsvSelectData listData = new FileIOScheduleCsvSelectData();
    listData.initField();
    listData.setTempFolderIndex(folderIndex);
    listData.setIsAutoTime(ALEipUtils.getTemp(rundata, context, "is_autotime"));
    /* リストの作成 */
    ALCSVUtils.makeList(rundata, context, this, listData);
    setTemplate(rundata, "fileio-schedule-csv");
  }

  /**
   * 読み込んだ内容からエラーが発生した件のみリスト表示 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doSchedule_list_csv_error(RunData rundata, Context context)
      throws Exception {
    FileIOScheduleCsvSelectData listData = new FileIOScheduleCsvSelectData();
    listData.initField();
    listData.setTempFolderIndex(rundata
      .getParameters()
      .getString("temp_folder"));
    listData.setIsAutoTime(ALEipUtils.getTemp(rundata, context, "is_autotime"));
    ALCSVUtils.makeErrorList(rundata, context, this, listData);
    listData.setNotErrorCount(Integer.parseInt(ALEipUtils.getTemp(
      rundata,
      context,
      "not_error_count")));
    context.put("validateError", true);
    setTemplate(rundata, "fileio-schedule-csv");
  }

  /**
   * 読み込んだ内容をリスト表示 <BR>
   *
   * @param rundata
   * @param context
   * @param folderIndex
   * @throws Exception
   */
  public void doSchedule_list_csv_page(RunData rundata, Context context)
      throws Exception {
    FileIOScheduleCsvSelectData listData = new FileIOScheduleCsvSelectData();
    listData.initField();
    listData.setTempFolderIndex(rundata
      .getParameters()
      .getString("temp_folder"));
    context
      .put("temp_folder", rundata.getParameters().getString("temp_folder"));
    listData.setIsAutoTime(ALEipUtils.getTemp(rundata, context, "is_autotime"));
    ALCSVUtils.makeListPage(rundata, context, this, listData);
    setTemplate(rundata, "fileio-schedule-csv");
  }

  /**
   * CSVファイルからデータベースへの登録 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doSchedule_insert_csv(RunData rundata, Context context)
      throws Exception {
    int not_error = 0;
    String temp_folder_index = rundata.getParameters().getString("temp_folder");
    String filepath =
      FileIOScheduleCsvUtils.getScheduleCsvFolderName(temp_folder_index)
        + ALStorageService.separator()
        + FileIOScheduleCsvUtils.FOLDER_TMP_FOR_USERINFO_CSV_FILENAME;
    ALCsvTokenizer reader = new ALCsvTokenizer();
    if (!reader.init(filepath)) {
      return;
    }
    List<String> sequency = ALCSVUtils.getSequency(rundata, context);
    String token;
    int i, j;
    while (reader.eof != -1) {
      FileIOScheduleCsvFormData formData = new FileIOScheduleCsvFormData();
      formData.initField();
      formData.setIsAutoTime(ALEipUtils
        .getTemp(rundata, context, "is_autotime"));
      for (j = 0; j < sequency.size(); j++) {
        token = reader.nextToken();
        i = Integer.parseInt(sequency.get(j));
        formData.addItemToken(token, i);
        if (reader.eof == -1) {
          break;
        }
        if (reader.line) {
          break;
        }
      }
      while ((!reader.line) && (reader.eof != -1)) {
        reader.nextToken();
      }
      if (reader.eof == -1 && j == 0) {
        break;
      }
      // カンマ不足対策
      for (j++; j < sequency.size(); j++) {
        i = Integer.parseInt(sequency.get(j));
        formData.addItemToken("", i);
      }

      List<String> errmsg = new ArrayList<String>();
      formData.adjust();
      formData.adjustUser(errmsg);

      if (errmsg.size() > 0) {
        continue;
      }

      /** データベースから読み取る場合 */
      try {
        if (formData.getUserNameString().toString().equals("")) {
          continue;
        }
        if (formData.getScheduleName().toString().equals("")) {
          continue;
        }
      } catch (Exception e) {
        continue;
      }
      if (!formData.getUserFullName().toString().equals("名前")) {
        if (formData.doInsert(this, rundata, context)) {
          not_error++;
        }
      } else {
        if (not_error > 0) {
          not_error--;
        }
      }
    }
    logger.warn("[scheduleImport]1 schedule CSV imported");
    ALEipUtils.setTemp(rundata, context, "not_error_count", Integer
      .toString(not_error));
    int error_count =
      Integer.parseInt(ALEipUtils.getTemp(rundata, context, "error_count"));
    if (error_count > 0) {
      doSchedule_list_csv_error(rundata, context);
    } else {
      doSchedule_form(rundata, context);
    }

  }
}

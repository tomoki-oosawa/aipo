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
package com.aimluck.eip.fileio;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALCsvAbstractSelectData;
import com.aimluck.eip.common.ALCsvTokenizer;
import com.aimluck.eip.fileio.util.FileIOAddressBookCsvUtils;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * CSV ファイルから読み込んだアドレス帳情報を表示するクラス．
 * 
 * 
 */
public class FileIOAddressBookCsvSelectData extends
    ALCsvAbstractSelectData<FileIOAddressBookCsvData, FileIOAddressBookCsvData> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileIOAddressBookCsvSelectData.class.getName());

  /** 会社情報のみ入力する場合はtrue */
  private boolean is_company_only;

  /**
   * フィールドの初期化
   */
  @Override
  public void initField() {
    super.initField();
    setIsCompanyOnly(false);
  }

  /**
   * CSVファイルからアドレス帳情報を取得します <BR>
   */
  @Override
  protected ResultList<FileIOAddressBookCsvData> selectList(RunData rundata,
      Context context) {
    String filepath;
    try {
      if (stats == ALCsvTokenizer.CSV_LIST_MODE_READ) {
        return new ResultList<FileIOAddressBookCsvData>(
          readAddressBookInfoFromCsv(rundata));
      } else if (stats == ALCsvTokenizer.CSV_LIST_MODE_NO_ERROR) {
        filepath =
          FileIOAddressBookCsvUtils
            .getAddressBookCsvFolderName(getTempFolderIndex())
            + ALStorageService.separator()
            + FileIOAddressBookCsvUtils.CSV_ADDRESSBOOK_TEMP_FILENAME;
        return new ResultList<FileIOAddressBookCsvData>(
          readAddressBookInfoFromCsvPage(rundata, filepath, (rundata
            .getParameters()
            .getInteger("csvpage") - 1), ALCsvTokenizer.CSV_SHOW_SIZE));
      } else if (stats == ALCsvTokenizer.CSV_LIST_MODE_ERROR) {
        if (this.error_count > 0) {
          filepath =
            FileIOAddressBookCsvUtils
              .getAddressBookCsvFolderName(getTempFolderIndex())
              + ALStorageService.separator()
              + FileIOAddressBookCsvUtils.CSV_ADDRESSBOOK_TEMP_ERROR_FILENAME;
        } else {
          return null;
        }
        return new ResultList<FileIOAddressBookCsvData>(
          readAddressBookInfoFromCsvPage(
            rundata,
            filepath,
            0,
            ALCsvTokenizer.CSV_SHOW_ERROR_SIZE));
      } else {
        return null;
      }
    } catch (Exception ex) {
      logger.error("fileio", ex);
      return null;
    }
  }

  /**
   * CSVファイルを読み込んで表示用リストを作成します <BR>
   * 
   * @param rundata
   * @return
   * @throws Exception
   */
  private List<FileIOAddressBookCsvData> readAddressBookInfoFromCsv(
      RunData rundata) throws Exception {
    String filepath =
      FileIOAddressBookCsvUtils
        .getAddressBookCsvFolderName(getTempFolderIndex())
        + ALStorageService.separator()
        + FileIOAddressBookCsvUtils.CSV_ADDRESSBOOK_TEMP_FILENAME;

    String filepath_err =
      FileIOAddressBookCsvUtils
        .getAddressBookCsvFolderName(getTempFolderIndex())
        + ALStorageService.separator()
        + FileIOAddressBookCsvUtils.CSV_ADDRESSBOOK_TEMP_ERROR_FILENAME;

    ALCsvTokenizer reader = new ALCsvTokenizer();
    if (!reader.init(filepath)) {
      return null;
    }

    List<FileIOAddressBookCsvData> list =
      new ArrayList<FileIOAddressBookCsvData>();
    int ErrCount = 0;// エラーが発生した回数

    String token;
    int i, j, k;
    int line = 0;
    String ErrorCode = "";

    List<FileIOAddressBookCsvData> collectList =
      new ArrayList<FileIOAddressBookCsvData>();

    while (reader.eof != -1) {
      line++;
      StringBuffer e_line = new StringBuffer();
      boolean b_err = false;
      List<String> errmsg = new ArrayList<String>();
      FileIOAddressBookCsvFormData formData =
        new FileIOAddressBookCsvFormData();
      formData.initField();
      formData.setIsCompanyOnly(is_company_only);
      e_line.append("");
      for (j = 0; j < sequency.size(); j++) {
        token = reader.nextToken();

        if (j > 0) {
          e_line.append(",");
        }
        e_line.append("\"");
        e_line.append(makeOutputItem(token));
        e_line.append("\"");

        i = Integer.parseInt((String) sequency.get(j));
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
        i = Integer.parseInt((String) sequency.get(j));
        formData.addItemToken("", i);
        e_line.append(",\"\"");
      }

      formData.setValidator();
      if (!formData.validate(errmsg)) {
        b_err = true;
      }

      try {
        FileIOAddressBookCsvData data = new FileIOAddressBookCsvData();
        data.initField();
        data.setLineCount(line);

        StringBuffer sb = new StringBuffer();
        sb.append(formData.getLastName().toString()
          + " "
          + formData.getFirstName().toString());

        data.setName(formData.getLastName().toString()
          + " "
          + formData.getFirstName().toString());
        data.setNameKana(formData.getLastNameKana().toString()
          + " "
          + formData.getFirstNameKana().toString());
        data.setCreatedUser(formData.getUserName().toString());

        data.setTelephone(formData.getTelephone());
        data.setCellularPhone(formData.getCellularPhone());
        data.setEmail(formData.getEmail());
        data.setCellularMail(formData.getCellularMail());

        data.setCompanyName(formData.getCompanyName().getValue());
        data.setCompanyNameKana(formData.getCompanyNameKana().getValue());
        data.setZipcode(formData.getCompZipcode());
        data.setCompanyAddress(formData.getCompAddress());
        data.setCompanyTelephone(formData.getCompTelephone());
        data.setCompanyFaxNumber(formData.getCompFaxNumber());
        data.setPostName(formData.getPostName().getValue());
        data.setPositionName(formData.getPositionName().getValue());
        data.setCompanyUrl(formData.getCompUrl());
        data.setNote(formData.getNote().getValue());

        FileIOAddressBookCsvData pdata; // 同じデータがないか判別
        for (k = 0; k < collectList.size(); k++) {
          pdata = collectList.get(k);
          if ((data.getCompanyName().toString().equals(pdata
            .getCompanyName()
            .toString()))
            && (data.getPostName().toString().equals(pdata
              .getPostName()
              .toString()))) {

            if (is_company_only) {
              data.setSameCompany(true);
              b_err = true;
            }
          }
        }

        if (formData.getSameCompany()) {
          if (is_company_only) {
            b_err = true;
          }
          data.setSameCompany(true);
        }
        if ((data.getName().toString().equals(""))
          && (data.getCompanyNameKana().toString().equals(""))) {
          b_err = true;
        }
        if ((!data.getName().toString().equals(""))
          && (data.getNameKana().toString().equals(""))) {
          b_err = true;
        }
        if ((!data.getCompanyName().toString().equals(""))
          && (data.getCompanyNameKana().toString().equals(""))) {
          b_err = true;
        }
        data.setIsError(b_err);

        if (b_err) {
          ErrorCode += e_line.toString();
          ErrorCode += "," + Integer.toString(line);
          ErrorCode += "\n";
        } else {
          collectList.add(data);
        }

        if ((!formData.getFirstName().toString().equals(
          ALLocalizationUtils.getl10n("FILEIO_LAST_NAME")))
          && (!formData.getCompanyName().toString().equals(
            ALLocalizationUtils.getl10n("FILEIO_COMPANY_NAME")))) {
          if (ErrCount == 0) {
            if (!b_err) {
              if (list.size() < ALCsvTokenizer.CSV_SHOW_SIZE) {
                list.add(data);
              }
            } else {
              // list.clear();// エラーが初めて発生した場合。
              list.add(data);
              ErrCount++;
            }
          } else {
            if (b_err) {
              ErrCount++;
            }
            list.add(data);
          }
        } else {
          if (ErrCount > 0) {
            ErrCount--;
          }
          int lc = getLineCount();
          setLineCount(lc - 1);
        }
        if (ErrCount >= ALCsvTokenizer.CSV_SHOW_ERROR_SIZE) {
          break;
        }
      } catch (Exception e) {
        logger.error("readError", e);
      }
      if (reader.eof == -1) {
        break;
      }
    }
    collectList.clear();
    collectList = null;
    setErrorCount(ErrCount);
    if (ErrCount > 0) {
      outputErrorData(rundata, ErrorCode, filepath_err);
    }
    return list;
  }

  /**
   * CSVファイルを読み込んでページ毎に表示用リストを作成します <BR>
   * 
   * @param rundata
   * @param filepath
   * @param StartLine
   * @param LineLimit
   * @return
   * @throws Exception
   */
  private List<FileIOAddressBookCsvData> readAddressBookInfoFromCsvPage(
      RunData rundata, String filepath, int StartLine, int LineLimit)
      throws Exception {

    int line_index = StartLine * ALCsvTokenizer.CSV_SHOW_SIZE;

    ALCsvTokenizer reader = new ALCsvTokenizer();
    if (!reader.setStartLine(filepath, line_index)) {
      return null;
    }

    List<FileIOAddressBookCsvData> list =
      new ArrayList<FileIOAddressBookCsvData>();

    String token;
    int i, j;
    int line = 0;
    while (reader.eof != -1) {
      line++;
      if (line > LineLimit) {
        break;
      }

      List<String> errmsg = new ArrayList<String>();
      FileIOAddressBookCsvFormData formData =
        new FileIOAddressBookCsvFormData();
      formData.initField();
      formData.setIsCompanyOnly(is_company_only);
      for (j = 0; j < sequency.size(); j++) {
        token = reader.nextToken();
        i = Integer.parseInt((String) sequency.get(j));
        formData.addItemToken(token, i);
        if (reader.eof == -1) {
          break;
        }
        if (reader.line) {
          break;
        }
        if (j == sequency.size() - 1) {
          if (stats == ALCsvTokenizer.CSV_LIST_MODE_ERROR) {
            token = reader.nextToken();
            line = Integer.parseInt(token);
          }
        }
      }
      while ((!reader.line) && (reader.eof != -1)) {
        reader.nextToken();
      }

      formData.setValidator();
      boolean iserror = !(formData.validate(errmsg));
      if (reader.eof == -1) {
        break;
      }
      try {
        FileIOAddressBookCsvData data = new FileIOAddressBookCsvData();
        data.initField();
        data.setLineCount(line + line_index);
        data.setName(formData.getLastName().toString()
          + " "
          + formData.getFirstName().toString());
        data.setNameKana(formData.getLastNameKana().toString()
          + " "
          + formData.getFirstNameKana().toString());
        data.setCreatedUser(formData.getUserName().toString());

        data.setTelephone(formData.getTelephone());
        data.setCellularPhone(formData.getCellularPhone());
        data.setEmail(formData.getEmail());
        data.setCellularMail(formData.getCellularMail());

        data.setCompanyName(formData.getCompanyName().getValue());
        data.setCompanyNameKana(formData.getCompanyNameKana().getValue());
        data.setZipcode(formData.getCompZipcode());
        data.setCompanyAddress(formData.getCompAddress());
        data.setCompanyTelephone(formData.getCompTelephone());
        data.setCompanyFaxNumber(formData.getCompFaxNumber());
        data.setPostName(formData.getPostName().getValue());
        data.setPositionName(formData.getPositionName().getValue());
        data.setCompanyUrl(formData.getCompUrl());
        data.setSameCompany(formData.getSameCompany());
        data.setNote(formData.getNote().getValue());

        if (is_company_only && formData.getSameCompany()) {
          data.setIsError(true);
        } else {
          data.setIsError(iserror);
        }
        if ((!formData.getFirstName().toString().equals(
          ALLocalizationUtils.getl10n("FILEIO_LAST_NAME")))
          && (!formData.getCompanyName().toString().equals(
            ALLocalizationUtils.getl10n("FILEIO_COMPANY_NAME")))) {
          list.add(data);
        }
      } catch (Exception e) {
        logger.error("readError", e);
      }
      if (reader.eof == -1) {
        break;
      }
    }

    return list;
  }

  /**
   * 会社情報のみ入力する場合はtrueを設定します <BR>
   * 
   * @param flag
   */
  public void setIsCompanyOnly(boolean flag) {
    is_company_only = flag;
  }

  public String getLineCount2() {
    return ALLocalizationUtils.getl10nFormat(
      "FILEIO_ERROR_NUMBER",
      getLineCount(),
      getErrorCount());
  }

  public String getLineCount3() {
    return ALLocalizationUtils.getl10nFormat(
      "FILEIO_REGISTER_NUMBER",
      getLineCount(),
      getNotErrorCount());
  }
}

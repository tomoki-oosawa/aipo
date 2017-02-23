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
package com.aimluck.eip.fileio;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALCsvAbstractSelectData;
import com.aimluck.eip.common.ALCsvTokenizer;
import com.aimluck.eip.fileio.util.FileIOAccountCsvUtils;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * CSV ファイルから読み込んだ部署情報を表示するクラス．
 *
 */
public class FileIOAccountPostCsvSelectData extends
    ALCsvAbstractSelectData<FileIOAccountPostCsvData, FileIOAccountPostCsvData> {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileIOAccountPostCsvSelectData.class.getName());

  /**
   * CSVファイルから部署情報を取得します <BR>
   */
  @Override
  protected ResultList<FileIOAccountPostCsvData> selectList(RunData rundata,
      Context context) {
    String filepath;
    try {
      if (stats == ALCsvTokenizer.CSV_LIST_MODE_READ) {
        return new ResultList<FileIOAccountPostCsvData>(
          readAccountInfoFromCsv(rundata));
      } else if (stats == ALCsvTokenizer.CSV_LIST_MODE_NO_ERROR) {
        filepath =
          FileIOAccountCsvUtils
            .getAccountPostCsvFolderName(getTempFolderIndex())
            + ALStorageService.separator()
            + FileIOAccountCsvUtils.CSV_ACCOUNT_POST_TEMP_FILENAME;
        return new ResultList<FileIOAccountPostCsvData>(
          readAccountInfoFromCsvPage(rundata, filepath, (rundata
            .getParameters()
            .getInteger("csvpage") - 1), ALCsvTokenizer.CSV_SHOW_SIZE));
      } else if (stats == ALCsvTokenizer.CSV_LIST_MODE_ERROR) {
        if (this.error_count > 0) {
          filepath =
            FileIOAccountCsvUtils
              .getAccountPostCsvFolderName(getTempFolderIndex())
              + ALStorageService.separator()
              + FileIOAccountCsvUtils.CSV_ACCOUNT_POST_TEMP_ERROR_FILENAME;
        } else {
          return null;
        }
        return new ResultList<FileIOAccountPostCsvData>(
          readAccountInfoFromCsvPage(
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
  private List<FileIOAccountPostCsvData> readAccountInfoFromCsv(RunData rundata)
      throws Exception {
    String filepath =
      FileIOAccountCsvUtils.getAccountPostCsvFolderName(getTempFolderIndex())
        + ALStorageService.separator()
        + FileIOAccountCsvUtils.CSV_ACCOUNT_POST_TEMP_FILENAME;

    String filepath_err =
      FileIOAccountCsvUtils.getAccountPostCsvFolderName(getTempFolderIndex())
        + ALStorageService.separator()
        + FileIOAccountCsvUtils.CSV_ACCOUNT_POST_TEMP_ERROR_FILENAME;
    ALCsvTokenizer reader = new ALCsvTokenizer();
    if (!reader.init(filepath)) {
      return null;
    }

    List<FileIOAccountPostCsvData> list =
      new ArrayList<FileIOAccountPostCsvData>();
    int ErrCount = 0;// エラーが発生した回数

    String token;
    int i, j, k;
    int line = 0;
    int count = 0;

    String ErrorCode = "";

    List<FileIOAccountPostCsvData> collectList =
      new ArrayList<FileIOAccountPostCsvData>();

    while (reader.eof != -1) {
      line++;
      StringBuffer e_line = new StringBuffer();
      boolean b_err = false;
      List<String> errmsg = new ArrayList<String>();
      FileIOAccountPostCsvFormData formData =
        new FileIOAccountPostCsvFormData();
      formData.initField();
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
        FileIOAccountPostCsvData data = new FileIOAccountPostCsvData();
        data.initField();
        data.setLineCount(line);
        data.setPostName(formData.getPostName().getValue());
        data.setSamePost(formData.getSamePost());
        data.setZipcode(formData.getZipcode().getValue());
        data.setAddress(formData.getAddress().getValue());
        data.setOutTelephone(formData.getOutTelephone().getValue());
        data.setInTelephone(formData.getInTelephone().getValue());
        data.setFaxNumber(formData.getFaxNumber().getValue());

        FileIOAccountPostCsvData pdata; // 同じデータがないか判別
        for (k = 0; k < collectList.size(); k++) {
          pdata = collectList.get(k);
          if (data.getPostName().getValue().equals(
            pdata.getPostName().getValue())) {
            data.setSamePost(true);
            b_err = true;
          }
        }

        /** 部署名が空白の場合はエラー */
        if (data.getPostName().toString().equals("")) {
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
        if (!formData.getPostName().toString().equals(
          ALLocalizationUtils.getl10n("FILIIO_UNIT_NAME"))) {
          count++;
          setLineCount(count);

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
        }
        if (ErrCount >= ALCsvTokenizer.CSV_SHOW_ERROR_SIZE) {
          break;
        }
      } catch (RuntimeException e) {
        logger.error("readError");
      } catch (Exception e) {
        logger.error("readError");
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
   * CSVファイルを読み込んでページ毎の表示用リストを作成します <BR>
   *
   * @param rundata
   * @param filepath
   * @param StartLine
   * @param LineLimit
   * @return
   * @throws Exception
   */
  private List<FileIOAccountPostCsvData> readAccountInfoFromCsvPage(
      RunData rundata, String filepath, int StartLine, int LineLimit)
      throws Exception {

    int line_index = StartLine * ALCsvTokenizer.CSV_SHOW_SIZE;

    ALCsvTokenizer reader = new ALCsvTokenizer();
    if (!reader.setStartLine(filepath, line_index)) {
      return null;
    }

    List<FileIOAccountPostCsvData> list =
      new ArrayList<FileIOAccountPostCsvData>();

    String token;
    int i, j;
    int line = 0;
    while (reader.eof != -1) {
      line++;
      if (line > LineLimit) {
        break;
      }
      List<String> errmsg = new ArrayList<String>();
      FileIOAccountPostCsvFormData formData =
        new FileIOAccountPostCsvFormData();
      formData.initField();
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
        FileIOAccountPostCsvData data = new FileIOAccountPostCsvData();
        data.initField();
        data.setLineCount(line + line_index);
        data.setPostName(formData.getPostName().getValue());
        data.setSamePost(formData.getSamePost());
        data.setZipcode(formData.getZipcode().getValue());
        data.setAddress(formData.getAddress().getValue());
        data.setOutTelephone(formData.getOutTelephone().getValue());
        data.setInTelephone(formData.getInTelephone().getValue());
        data.setFaxNumber(formData.getFaxNumber().getValue());
        data.setIsError(iserror);
        if (!formData.getPostName().toString().equals(
          ALLocalizationUtils.getl10n("FILIIO_UNIT_NAME"))) {
          list.add(data);
        }
      } catch (Exception e) {
        logger.error("readError");
      }
      if (reader.eof == -1) {
        break;
      }
    }

    return list;
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

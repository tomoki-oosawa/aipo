/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.fileio;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.common.ALCsvAbstractSelectData;
import com.aimluck.eip.common.ALCsvTokenizer;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.fileio.util.FileIOScheduleCsvUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.util.ALEipUtils;

/**
 * CSV ファイルから読み込んだスケジュール情報を表示するクラス．
 * 
 * 
 */
public class FileIOScheduleCsvSelectData extends
    ALCsvAbstractSelectData<FileIOScheduleCsvData, FileIOScheduleCsvData> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileIOScheduleCsvSelectData.class.getName());

  /** 時間を表すフィールドを自動補完するか否か */
  private String autotime_flg;

  /**
   * 初期化 <BR>
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
  }

  /**
   * 各フィールドの初期化
   */
  @Override
  public void initField() {
    autotime_flg = "";
    super.initField();
  }

  /**
   * スケジュール一覧を取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<FileIOScheduleCsvData> selectList(RunData rundata,
      Context context) {
    String filepath;
    try {
      if (stats == ALCsvTokenizer.CSV_LIST_MODE_READ) {
        return new ResultList<FileIOScheduleCsvData>(
          readScheduleInfoFromCsv(rundata));
      } else if (stats == ALCsvTokenizer.CSV_LIST_MODE_NO_ERROR) {
        filepath =
          FileIOScheduleCsvUtils.getScheduleCsvFolderName(getTempFolderIndex())
            + File.separator
            + FileIOScheduleCsvUtils.FOLDER_TMP_FOR_USERINFO_CSV_FILENAME;
        return new ResultList<FileIOScheduleCsvData>(
          readScheduleInfoFromCsvPage(rundata, filepath, (rundata
            .getParameters()
            .getInteger("csvpage") - 1), ALCsvTokenizer.CSV_SHOW_SIZE));
      } else if (stats == ALCsvTokenizer.CSV_LIST_MODE_ERROR) {
        filepath =
          FileIOScheduleCsvUtils.getScheduleCsvFolderName(getTempFolderIndex())
            + File.separator
            + FileIOScheduleCsvUtils.FOLDER_TMP_FOR_USERINFO_CSV_TEMP_FILENAME;
        return new ResultList<FileIOScheduleCsvData>(
          readScheduleInfoFromCsvPage(
            rundata,
            filepath,
            0,
            ALCsvTokenizer.CSV_SHOW_ERROR_SIZE));
      } else {
        return null;
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected FileIOScheduleCsvData selectDetail(RunData rundata, Context context) {
    return null;
  }

  /**
   * CSVファイルを読み込んで表示用リストを作成します <BR>
   * 
   * @param rundata
   * @return
   * @throws Exception
   */
  private List<FileIOScheduleCsvData> readScheduleInfoFromCsv(RunData rundata)
      throws Exception {

    String filepath =
      FileIOScheduleCsvUtils.getScheduleCsvFolderName(getTempFolderIndex())
        + File.separator
        + FileIOScheduleCsvUtils.FOLDER_TMP_FOR_USERINFO_CSV_FILENAME;

    String filepath_err =
      FileIOScheduleCsvUtils.getScheduleCsvFolderName(getTempFolderIndex())
        + File.separator
        + FileIOScheduleCsvUtils.FOLDER_TMP_FOR_USERINFO_CSV_TEMP_FILENAME;

    ALCsvTokenizer reader = new ALCsvTokenizer();
    if (!reader.init(filepath)) {
      return null;
    }

    List<FileIOScheduleCsvData> list = new ArrayList<FileIOScheduleCsvData>();
    int ErrCount = 0;// エラーが発生した回数
    Date now = new Date();

    String token;
    int i, j;
    int line = 0;
    String ErrorCode = "";
    StringBuffer e_line = new StringBuffer();

    while (reader.eof != -1) {
      line++;
      boolean b_err = false;
      List<String> errmsg = new ArrayList<String>();
      FileIOScheduleCsvFormData formData = new FileIOScheduleCsvFormData();
      formData.initField();
      formData.setIsAutoTime(autotime_flg);
      e_line.append("");
      for (j = 0; j < sequency.size(); j++) {
        token = reader.nextToken();
        /** エラー出力用の文字列 */
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
        ALDateTimeField date =
          new ALDateTimeField(ALDateTimeField.DEFAULT_DATE_TIME_FORMAT);

        ALEipUser user =
          ALEipUtils.getALEipUser(formData.getUserName().getValue());
        if (user == null) {
          user = new ALEipUser();
          user.initField();
          user.setAliasName(formData.getUserFirstName().toString(), formData
            .getUserLastName()
            .toString());
          b_err = true;
        }

        try {
          formData.getUserName();
        } catch (Exception e) {
          b_err = true;
        }

        FileIOScheduleCsvData data = new FileIOScheduleCsvData();
        data.setUser(user);

        data.initField();
        data.setLineCount(line);

        data.setName(formData.getScheduleName().getValue());
        if (data.getName().toString().equals("")) {
          b_err = true;
        }

        data.setPlace(formData.getPlace().getValue());
        data.setNote(formData.getNote().getValue());

        try {
          date.setValue(formData.getCreateDate().toString());
          data.setUpdateDate(date.getValue());
        } catch (Exception e) {
          data.setUpdateDate(now);
        }

        try {
          date.setValue(formData.getStartDateTime().toString());
          data.setStartDate(date.getValue());
        } catch (Exception e) {
          data.setStartDate(null);
          b_err = true;
        }

        try {
          date.setValue(formData.getEndDateTime().toString());
          data.setEndDate(date.getValue());
        } catch (Exception e) {
          data.setEndDate(null);
          b_err = true;
        }

        data.setIsError(b_err);
        if (b_err) {
          ErrorCode += e_line.toString();
          ErrorCode += "," + Integer.toString(line);
          ErrorCode += "\n";
        }
        if (!formData.getUserFullName().toString().equals("名前")) {
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
   * @return
   * @throws Exception
   */
  private List<FileIOScheduleCsvData> readScheduleInfoFromCsvPage(
      RunData rundata, String filepath, int StartLine, int LineLimit)
      throws Exception {

    int line_index = StartLine * (ALCsvTokenizer.CSV_SHOW_SIZE);

    ALCsvTokenizer reader = new ALCsvTokenizer();
    if (!reader.setStartLine(filepath, line_index)) {
      return null;
    }

    List<FileIOScheduleCsvData> list = new ArrayList<FileIOScheduleCsvData>();

    Date now = new Date();

    String token;
    int i, j;
    int line = 0;
    while (reader.eof != -1) {
      line++;
      boolean b_err = false;
      if (line > LineLimit) {
        break;
      }
      List<String> errmsg = new ArrayList<String>();
      FileIOScheduleCsvFormData formData = new FileIOScheduleCsvFormData();
      formData.initField();
      formData.setIsAutoTime(autotime_flg);
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
      formData.validate(errmsg);

      if (reader.eof == -1) {
        break;
      }
      try {
        ALDateTimeField date =
          new ALDateTimeField(ALDateTimeField.DEFAULT_DATE_TIME_FORMAT);

        ALEipUser user =
          ALEipUtils.getALEipUser(formData.getUserName().getValue());
        if (user == null) {
          user = new ALEipUser();
          user.initField();
          user.setAliasName(formData.getUserFirstName().toString(), formData
            .getUserLastName()
            .toString());
          b_err = true;
        }

        FileIOScheduleCsvData data = new FileIOScheduleCsvData();
        data.setUser(user);

        data.initField();
        data.setLineCount(line + line_index);
        data.setName(formData.getScheduleName().getValue());
        if (data.getName().toString().equals("")) {
          b_err = true;
        }

        data.setPlace(formData.getPlace().getValue());
        data.setNote(formData.getNote().getValue());

        try {
          date.setValue(formData.getCreateDate().toString());
          data.setUpdateDate(date.getValue());
        } catch (Exception e) {
          b_err = true;
          data.setUpdateDate(now);
        }

        try {
          date.setValue(formData.getStartDateTime().toString());
          data.setStartDate(date.getValue());
        } catch (Exception e) {
          b_err = true;
          data.setStartDate(null);
        }

        try {
          date.setValue(formData.getEndDateTime().toString());
          data.setEndDate(date.getValue());
        } catch (Exception e) {
          b_err = true;
          data.setEndDate(null);
        }

        data.setIsError(b_err);
        if (!formData.getUserFullName().toString().equals("名前")) {
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

  /**
   * 時刻を自動入力する場合はここで"1"を入力します <BR>
   * 
   * @param flag
   */
  public void setIsAutoTime(String flag) {
    autotime_flg = flag;
  }

}

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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALCsvAbstractSelectData;
import com.aimluck.eip.common.ALCsvTokenizer;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.fileio.util.FileIOAccountCsvUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * CSV ファイルから読み込んだアカウント情報を表示するクラス．
 * 
 */
public class FileIOAccountCsvSelectData
    extends
    ALCsvAbstractSelectData<FileIOAccountCsvResultData, FileIOAccountCsvResultData> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileIOAccountCsvSelectData.class.getName());

  /** 最大登録可能数を超えているかのフラグ */
  private boolean overMaxUser = false;

  /**
   * 初期化
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
  }

  /**
   * アカウント一覧を取得します。 ただし、論理削除されているアカウントは取得しません。
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<FileIOAccountCsvResultData> selectList(RunData rundata,
      Context context) {
    String filepath;
    try {
      if (stats == ALCsvTokenizer.CSV_LIST_MODE_READ) {
        return new ResultList<FileIOAccountCsvResultData>(
          readAccountInfoFromCsv(rundata));
      } else if (stats == ALCsvTokenizer.CSV_LIST_MODE_NO_ERROR) {
        filepath =
          FileIOAccountCsvUtils.getAccountCsvFolderName(getTempFolderIndex())
            + ALStorageService.separator()
            + FileIOAccountCsvUtils.CSV_ACCOUNT_TEMP_FILENAME;
        return new ResultList<FileIOAccountCsvResultData>(
          readAccountInfoFromCsvPage(rundata, filepath, (rundata
            .getParameters()
            .getInteger("csvpage") - 1), ALCsvTokenizer.CSV_SHOW_SIZE));
      } else if (stats == ALCsvTokenizer.CSV_LIST_MODE_ERROR) {
        if (this.error_count > 0) {
          filepath =
            FileIOAccountCsvUtils.getAccountCsvFolderName(getTempFolderIndex())
              + ALStorageService.separator()
              + FileIOAccountCsvUtils.CSV_ACCOUNT_TEMP_ERROR_FILENAME;
        } else {
          return null;
        }
        return new ResultList<FileIOAccountCsvResultData>(
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
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected FileIOAccountCsvResultData selectDetail(RunData rundata,
      Context context) {
    return null;
  }

  /**
   * CSVファイルを読み込んで表示用リストを作成します <BR>
   * 
   * @param rundata
   * @return
   * @throws Exception
   */
  private List<FileIOAccountCsvResultData> readAccountInfoFromCsv(
      RunData rundata) throws Exception {
    String filepath =
      FileIOAccountCsvUtils.getAccountCsvFolderName(getTempFolderIndex())
        + ALStorageService.separator()
        + FileIOAccountCsvUtils.CSV_ACCOUNT_TEMP_FILENAME;

    String filepath_err =
      FileIOAccountCsvUtils.getAccountCsvFolderName(getTempFolderIndex())
        + ALStorageService.separator()
        + FileIOAccountCsvUtils.CSV_ACCOUNT_TEMP_ERROR_FILENAME;

    ALCsvTokenizer reader = new ALCsvTokenizer();
    if (!reader.init(filepath)) {
      return null;
    }

    List<FileIOAccountCsvResultData> list =
      new ArrayList<FileIOAccountCsvResultData>();
    Map<String, TurbineUser> existedUserMap = getAllUsersFromDB();
    if (existedUserMap == null) {
      existedUserMap = new LinkedHashMap<String, TurbineUser>();
    }
    int ErrCount = 0;

    // 同一ユーザの存在を確認するために，ユーザ名のリストを保持する．
    List<String> usernameList = new ArrayList<String>();
    int i, j;
    String token;
    int line = 0;

    String ErrorCode = "";

    // 最終行まで読み込む
    while (reader.eof != -1) {
      line++;
      StringBuilder e_line = new StringBuilder();
      boolean same_user = false;
      boolean b_err = false;
      List<String> errmsg = new ArrayList<String>();

      FileIOAccountCsvFormData formData = new FileIOAccountCsvFormData();
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

      if (formData.getUserName().toString().equals(
        ALLocalizationUtils.getl10n("FILEIO_USER_NAME"))) {
        setLineCount(getLineCount() - 1);

        ErrorCode += e_line.toString();
        ErrorCode += "," + Integer.toString(line) + ",false";
        ErrorCode += "\n";
        continue;
      }

      if (usernameList.contains(formData.getUserName().getValue())) {
        // repeatUsers = true;
        same_user = true;
        b_err = true;
      } else {
        usernameList.add(formData.getUserName().getValue());
      }

      formData.setValidator();
      if (!formData.validate(errmsg)) {
        b_err = true;
      }

      try {
        String username = formData.getUserName().getValue();
        FileIOAccountCsvResultData data = new FileIOAccountCsvResultData();
        TurbineUser user = new TurbineUser();

        if (existedUserMap.containsKey(username)) {
          TurbineUser tmpuser2 = existedUserMap.get(username);
          // same_user = true;
          if ("F".equals(tmpuser2.getDisabled())) {
            user.setLoginName(username);
          } else {
            user.setLoginName(null);
            b_err = true;
          }
        } else {
          user.setLoginName(username);
          TurbineUser newuser = new TurbineUser();
          newuser.setLoginName(username);
          newuser.setDisabled("F");
          existedUserMap.put(username, newuser);
        }

        user.setPasswordValue(formData.getPassword().getValue());
        user.setFirstName(formData.getFirstName().getValue());
        user.setLastName(formData.getLastName().getValue());
        user.setFirstNameKana(formData.getFirstNameKana().getValue());
        user.setLastNameKana(formData.getLastNameKana().getValue());
        user.setEmail(formData.getEmail().getValue());
        user.setOutTelephone(formData.getOutTelephone().getValue());
        user.setInTelephone(formData.getInTelephone().getValue());
        user.setCellularPhone(formData.getCellularPhone().getValue());
        user.setCellularMail(formData.getCellularMail().getValue());

        data.initField();
        data.setLineCount(line);
        data.setUser(user);
        data.setPostNotFound(formData.getPostNotFound());
        List<String> postnames = new ArrayList<String>();
        String[] st = formData.getPostName().toString().split("/");
        for (int k = 0; k < st.length; k++) {
          postnames.add((st[k]));
        }
        data.setPostName(formData.getPostName().getValue());
        data.setPostNameList(postnames);

        data.setPositionNotFound(formData.getPositionNotFound());
        data.setPositionName(formData.getPositionName().getValue());
        data.setSameUser(same_user);
        data.setIsError(b_err);

        if (b_err) {
          ErrorCode += e_line.toString();
          ErrorCode +=
            "," + Integer.toString(line) + "," + Boolean.toString(same_user);
          ErrorCode += "\n";
        }
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

        if (ErrCount >= ALCsvTokenizer.CSV_SHOW_ERROR_SIZE) {
          break;
        }

      } catch (RuntimeException ex) {
        throw ex;
      } catch (Exception e) {
        logger.error(e);
      }
    }

    TurbineUser tmpuser2 = null;
    int count_legitimate_user = 0;
    Collection<TurbineUser> coll = existedUserMap.values();
    Iterator<TurbineUser> iter = coll.iterator();
    while (iter.hasNext()) {
      tmpuser2 = iter.next();
      if (!"T".equals(tmpuser2.getDisabled())) {
        count_legitimate_user++;
      }
    }

    int max_user = ALEipUtils.getLimitUsers();
    if ((max_user > 0) && (count_legitimate_user > max_user + 2)) {
      overMaxUser = true;
    }

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
  private List<FileIOAccountCsvResultData> readAccountInfoFromCsvPage(
      RunData rundata, String filepath, int StartLine, int LineLimit)
      throws Exception {

    int line_index = StartLine * ALCsvTokenizer.CSV_SHOW_SIZE;

    ALCsvTokenizer reader = new ALCsvTokenizer();
    if (!reader.setStartLine(filepath, line_index)) {
      return null;
    }

    Map<String, TurbineUser> existedUserMap = getAllUsersFromDB();
    if (existedUserMap == null) {
      existedUserMap = new LinkedHashMap<String, TurbineUser>();
    }

    List<FileIOAccountCsvResultData> list =
      new ArrayList<FileIOAccountCsvResultData>();

    String token;
    int i, j;
    int line = 0;
    while (reader.eof != -1) {
      boolean iserror = false;
      boolean same_user = false;
      line++;
      if (line > LineLimit) {
        break;
      }
      List<String> errmsg = new ArrayList<String>();
      FileIOAccountCsvFormData formData = new FileIOAccountCsvFormData();
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
      if (stats == ALCsvTokenizer.CSV_LIST_MODE_ERROR) {
        token = reader.nextToken();
        same_user = Boolean.parseBoolean(token);
      }

      while ((!reader.line) && (reader.eof != -1)) {
        reader.nextToken();
      }

      if (formData.getUserName().toString().equals(
        ALLocalizationUtils.getl10n("FILEIO_USER_NAME"))) {
        continue;
      }

      if (reader.eof == -1) {
        break;
      }

      formData.setValidator();
      if (!formData.validate(errmsg)) {
        iserror = true;
      }

      if (stats == ALCsvTokenizer.CSV_LIST_MODE_ERROR) {
        iserror = true;
      }

      try {
        String username = formData.getUserName().getValue();
        FileIOAccountCsvResultData data = new FileIOAccountCsvResultData();
        data.initField();
        TurbineUser user = new TurbineUser();

        if (existedUserMap.containsKey(username)) {
          TurbineUser tmpuser2 = existedUserMap.get(username);
          // same_user = true;
          if ("F".equals(tmpuser2.getDisabled())) {
            user.setLoginName(username);
          } else {
            user.setLoginName(null);
            iserror = true;
          }
        } else {
          user.setLoginName(username);
          TurbineUser newuser = new TurbineUser();
          newuser.setLoginName(username);
          newuser.setDisabled("F");
          existedUserMap.put(username, newuser);
        }

        user.setPasswordValue(formData.getPassword().getValue());
        user.setFirstName(formData.getFirstName().getValue());
        user.setLastName(formData.getLastName().getValue());
        user.setFirstNameKana(formData.getFirstNameKana().getValue());
        user.setLastNameKana(formData.getLastNameKana().getValue());
        user.setEmail(formData.getEmail().getValue());
        user.setOutTelephone(formData.getOutTelephone().getValue());
        user.setInTelephone(formData.getInTelephone().getValue());
        user.setCellularPhone(formData.getCellularPhone().getValue());
        user.setCellularMail(formData.getCellularMail().getValue());

        data.setUser(user);
        data.setLineCount(line + line_index);

        List<String> postnames = new ArrayList<String>();
        String[] st = formData.getPostName().toString().split("/");
        for (int k = 0; k < st.length; k++) {
          postnames.add((st[k]));
        }
        data.setPostName(formData.getPostName().getValue());
        data.setPostNameList(postnames);
        data.setPostNotFound(formData.getPostNotFound());

        data.setPositionNotFound(formData.getPositionNotFound());
        data.setPositionName(formData.getPositionName().getValue());
        data.setSameUser(same_user);
        data.setIsError(iserror);

        list.add(data);
      } catch (RuntimeException e) {
        logger.error("readError");
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
   * 
   * @return
   */
  private Map<String, TurbineUser> getAllUsersFromDB() {
    Map<String, TurbineUser> map = null;
    try {
      SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
      List<TurbineUser> list = query.fetchList();

      map = new LinkedHashMap<String, TurbineUser>();
      TurbineUser user = null;
      int size = list.size();
      for (int i = 0; i < size; i++) {
        user = list.get(i);
        map.put(user.getLoginName(), user);
      }
    } catch (Exception ex) {
      logger.error("[ALEipUtils]", ex);
      // throw new ALDBErrorException();
    }
    return map;
  }

  /**
   * @param obj
   * @return
   * 
   */
  @Override
  protected Object getResultData(FileIOAccountCsvResultData obj) {
    return obj;
  }

  /**
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(FileIOAccountCsvResultData obj) {
    return null;
  }

  /**
   * @return
   * 
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    return map;
  }

  /**
   * ユーザー数が最大値を上回っているかどうかを示す <BR>
   */
  public boolean overMaxUser() {
    return overMaxUser;
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

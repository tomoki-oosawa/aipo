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
package com.aimluck.eip.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALCsvAbstractSelectData;
import com.aimluck.eip.common.ALCsvAbstractUploadFormData;
import com.aimluck.eip.common.ALCsvTokenizer;
import com.aimluck.eip.modules.actions.common.ALAction;

/**
 *
 */
public class ALCSVUtils {

  public static final String AFTER_BEHAVIOR = "afterbehavior";

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALEipUtils.class.getName());

  public static String DB_NAME_POSTGRESQL = "postgresql";

  /** CSVファイルのアップロード* */
  public static void csvUpload(RunData rundata, Context context,
      ALAction action, ALCsvAbstractUploadFormData formData) throws Exception {

    // formData.initField();
    formData.doUpdate(action, rundata, context);

    ALEipUtils.setTemp(rundata, context, "page_count", Integer
      .toString(formData.getPageCount()));
    ALEipUtils.setTemp(rundata, context, "line_count", Integer
      .toString(formData.getLineCount()));

  }

  /** テンプレートに出力する際のリスト作成* */
  public static void makeList(RunData rundata, Context context,
      ALAction action, ALCsvAbstractSelectData<?, ?> listData)// 最初に呼び出されたとき
      throws Exception {
    listData.setState(ALCsvTokenizer.CSV_LIST_MODE_READ);
    List<String> sequency =
      stringToArray(ALEipUtils.getTemp(rundata, context, "sequency"));
    int page_count =
      Integer.parseInt(ALEipUtils.getTemp(rundata, context, "page_count"));
    int line_count =
      Integer.parseInt(ALEipUtils.getTemp(rundata, context, "line_count"));

    listData.setSequency(sequency);
    listData.setPageCount(page_count);
    listData.setLineCount(line_count);
    listData.doViewList(action, rundata, context);

    ALEipUtils.setTemp(rundata, context, "line_count", Integer
      .toString(listData.getLineCount()));
    ALEipUtils.setTemp(rundata, context, "error_count", Integer
      .toString(listData.getErrorCount()));
  }

  /** テンプレートに出力する際のエラーリスト作成* */
  public static void makeErrorList(RunData rundata, Context context,
      ALAction action, ALCsvAbstractSelectData<?, ?> listData) throws Exception {
    listData.setState(ALCsvTokenizer.CSV_LIST_MODE_ERROR);

    List<String> sequency =
      stringToArray(ALEipUtils.getTemp(rundata, context, "sequency"));
    int page_count =
      Integer.parseInt(ALEipUtils.getTemp(rundata, context, "page_count"));
    int line_count =
      Integer.parseInt(ALEipUtils.getTemp(rundata, context, "line_count"));

    listData.setSequency(sequency);
    listData.setPageCount(page_count);
    listData.setLineCount(line_count);

    int error_count =
      Integer.parseInt(ALEipUtils.getTemp(rundata, context, "error_count"));

    listData.setErrorCount(error_count);
    listData.doViewList(action, rundata, context);

  }

  /** テンプレートに出力する際のリスト作成（複数ページに分割される場合）* */
  public static void makeListPage(RunData rundata, Context context,
      ALAction action, ALCsvAbstractSelectData<?, ?> listData) throws Exception {

    List<String> sequency =
      stringToArray(ALEipUtils.getTemp(rundata, context, "sequency"));
    int page_count =
      Integer.parseInt(ALEipUtils.getTemp(rundata, context, "page_count"));
    int line_count =
      Integer.parseInt(ALEipUtils.getTemp(rundata, context, "line_count"));

    listData.setSequency(sequency);
    listData.setPageCount(page_count);
    listData.setLineCount(line_count);

    int error_count =
      Integer.parseInt(ALEipUtils.getTemp(rundata, context, "error_count"));

    listData.setErrorCount(error_count);

    listData.setState(ALCsvTokenizer.CSV_LIST_MODE_NO_ERROR);

    listData.doViewList(action, rundata, context);
  }

  /** 読み込む順序 */
  public static void setSequency(RunData rundata, Context context,
      List<String> arg) {
    ALEipUtils.setTemp(rundata, context, "sequency", arrayToString(arg));
  }

  public static List<String> getSequency(RunData rundata, Context context) {
    return stringToArray(ALEipUtils.getTemp(rundata, context, "sequency"));
  }

  private static String arrayToString(List<String> arg) {
    int i;
    String str = "";
    StringBuffer buf = new StringBuffer();
    for (i = 0; i < arg.size(); i++) {
      if (i != 0) {
        buf.append(",");
      }
      buf.append(arg.get(i));
    }
    str = buf.toString();
    return str;
  }

  private static List<String> stringToArray(String str) {
    try {
      List<String> arg = new ArrayList<String>();
      StringTokenizer st = new StringTokenizer(str, ",");
      while (st.hasMoreTokens()) {
        arg.add(st.nextToken());
      }
      return arg;
    } catch (Exception e) {
      return null;
    }
  }

}

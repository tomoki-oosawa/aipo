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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.portlet.EipTGpdb;
import com.aimluck.eip.cayenne.om.portlet.EipTGpdbItem;
import com.aimluck.eip.cayenne.om.portlet.EipTGpdbRecord;
import com.aimluck.eip.gpdb.util.GpdbUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class GpdbCsvFileScreen extends ALCSVScreen {

  /**
   *
   * @param rundata
   * @return
   */
  @Override
  protected String getContentType(RunData rundata) {
    return "application/octet-stream";
  }

  /**
   * @param rundata
   * @return
   * @throws Exception
   */
  @Override
  @SuppressWarnings("unchecked")
  protected String getCSVString(RunData rundata) throws Exception {
    String LINE_SEPARATOR = System.getProperty("line.separator");
    String gpdbId = rundata.getParameters().getString("gpdb_id");
    EipTGpdb gpdb = GpdbUtils.getEipTGpdb(gpdbId);
    boolean isFirst;

    StringBuffer sb = new StringBuffer();

    isFirst = true;
    List<EipTGpdbItem> items = gpdb.getEipTGpdbItem();
    Collections.sort(items, getEipTGpdbItemComparator());
    for (EipTGpdbItem item : items) {
      if (isFirst) {
        isFirst = false;
      } else {
        sb.append(",");
      }
      sb.append(item.getGpdbItemName());
    }

    int lastRecordNo = -1;
    List<EipTGpdbRecord> records = gpdb.getEipTGpdbRecord();
    Collections.sort(records, getEipTGpdbRecordComparator1());
    Collections.sort(records, getEipTGpdbRecordComparator2());
    for (EipTGpdbRecord record : records) {
      String type = record.getGpdbItem().getType();

      int recordNo = record.getRecordNo();
      if (lastRecordNo != recordNo) {
        sb.append(LINE_SEPARATOR);
        lastRecordNo = recordNo;
      } else {
        sb.append(",");
      }

      Integer kubun = record.getGpdbItem().getGpdbKubunId();
      String value = record.getValue();
      if (value != null) {
        String[] separateValue = null;
        separateValue = value.split("\\|"); // 複数の項目への対応で分割処理
        if (kubun != null && !"".equals(value)) {
          for (int i = 0; i < separateValue.length; i++) {
            separateValue[i] =
              GpdbUtils
                .getEipMGpdbKubunValue(separateValue[i])
                .getGpdbKubunValue();
            sb.append(separateValue[i]);
            if (i < separateValue.length - 1) {
              sb.append("|");
            }
          }
        } else { // タイトル名などの、上の条件式に当てはまらないものを出力
          if (type.equals(GpdbUtils.ITEM_TYPE_TEXTAREA)) {
            // テキストエリアの場合、ダブルクオーテーションで囲む
            value = "\"" + value + "\"";
          }
          if (type.equals(GpdbUtils.ITEM_TYPE_CREATE_USER)
            || type.equals(GpdbUtils.ITEM_TYPE_UPDATE_USER)) {
            // 登録者、更新者の場合、名称・ユーザーIDをセットする
            if (!"".equals(value.trim())) {
              Integer userid = Integer.valueOf(value);
              value = ALEipUtils.getALEipUser(userid).getAliasName().getValue();
            }

          }
          sb.append(value);
        }
      }

    }

    return sb.toString();
  }

  /**
   * @return
   */
  @Override
  protected String getFileName() {
    // String result = Encoder.encodeUrl(fileName);
    // return result + ".csv";
    return "webdb.csv";
  }

  private static Comparator<EipTGpdbItem> getEipTGpdbItemComparator() {
    Comparator<EipTGpdbItem> com = null;
    com = new Comparator<EipTGpdbItem>() {
      @Override
      public int compare(EipTGpdbItem s, EipTGpdbItem t) {
        return (s.getOrderNo() - t.getOrderNo());
      }
    };
    return com;
  }

  private static Comparator<EipTGpdbRecord> getEipTGpdbRecordComparator1() {
    Comparator<EipTGpdbRecord> com = null;
    com = new Comparator<EipTGpdbRecord>() {
      @Override
      public int compare(EipTGpdbRecord s, EipTGpdbRecord t) {
        return (s.getGpdbItem().getOrderNo() - t.getGpdbItem().getOrderNo());
      }
    };
    return com;
  }

  private static Comparator<EipTGpdbRecord> getEipTGpdbRecordComparator2() {
    Comparator<EipTGpdbRecord> com = null;
    com = new Comparator<EipTGpdbRecord>() {
      @Override
      public int compare(EipTGpdbRecord s, EipTGpdbRecord t) {
        return (s.getRecordNo() - t.getRecordNo());
      }
    };
    return com;
  }

}

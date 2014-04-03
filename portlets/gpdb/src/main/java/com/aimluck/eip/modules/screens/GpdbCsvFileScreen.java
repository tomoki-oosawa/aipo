/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2014 Aimluck,Inc.
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

import info.bliki.wiki.filter.Encoder;

import java.util.Collections;
import java.util.List;

import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.portlet.EipTGpdb;
import com.aimluck.eip.cayenne.om.portlet.EipTGpdbItem;
import com.aimluck.eip.cayenne.om.portlet.EipTGpdbRecord;
import com.aimluck.eip.gpdb.util.GpdbUtils;

/**
 *
 */
public class GpdbCsvFileScreen extends ALCSVScreen {

  private String fileName = null;

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
    fileName = gpdb.getGpdbName();
    boolean isFirst;

    StringBuffer sb = new StringBuffer();

    isFirst = true;
    List<EipTGpdbItem> items = gpdb.getEipTGpdbItem();
    Collections.sort(items, new EipTGpdbItemComparator());
    for (EipTGpdbItem item : items) {
      String type = item.getType();
      if (type.equals(GpdbUtils.ITEM_TYPE_FILE)
        || type.equals(GpdbUtils.ITEM_TYPE_IMAGE)) {
        continue;
      }
      if (isFirst) {
        isFirst = false;
      } else {
        sb.append(",");
      }
      sb.append(item.getGpdbItemName());
    }

    int lastRecordNo = -1;
    List<EipTGpdbRecord> records = gpdb.getEipTGpdbRecord();
    Collections.sort(records, new EipTGpdbRecordComparator1());
    Collections.sort(records, new EipTGpdbRecordComparator2());
    for (EipTGpdbRecord record : records) {
      String type = record.getGpdbItem().getType();
      if (type.equals(GpdbUtils.ITEM_TYPE_FILE)
        || type.equals(GpdbUtils.ITEM_TYPE_IMAGE)) {
        continue;
      }

      int recordNo = record.getRecordNo();
      if (lastRecordNo != recordNo) {
        sb.append(LINE_SEPARATOR);
        lastRecordNo = recordNo;
      } else {
        sb.append(",");
      }

      String value;
      Integer kubun = record.getGpdbItem().getGpdbKubunId();
      value = record.getValue();
      if (kubun != null && (value != null && !"".equals(value))) {
        value = GpdbUtils.getEipMGpdbKubunValue(value).getGpdbKubunValue();
      }

      sb.append(value);
    }

    return sb.toString();
  }

  /**
   * @return
   */
  @Override
  protected String getFileName() {
    String result = Encoder.encodeUrl(fileName);
    return result + ".csv";
  }

  @SuppressWarnings("rawtypes")
  private class EipTGpdbItemComparator implements java.util.Comparator {
    @Override
    public int compare(Object s, Object t) {
      return ((EipTGpdbItem) s).getOrderNo() - ((EipTGpdbItem) t).getOrderNo();
    }
  }

  @SuppressWarnings("rawtypes")
  private class EipTGpdbRecordComparator1 implements java.util.Comparator {
    @Override
    public int compare(Object s, Object t) {
      return ((EipTGpdbRecord) s).getGpdbItem().getOrderNo()
        - ((EipTGpdbRecord) t).getGpdbItem().getOrderNo();
    }
  }

  @SuppressWarnings("rawtypes")
  private class EipTGpdbRecordComparator2 implements java.util.Comparator {
    @Override
    public int compare(Object s, Object t) {
      return ((EipTGpdbRecord) s).getRecordNo()
        - ((EipTGpdbRecord) t).getRecordNo();
    }
  }
}

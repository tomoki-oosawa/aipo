/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import jp.sf.orangesignal.csv.Csv;
import jp.sf.orangesignal.csv.CsvConfig;
import jp.sf.orangesignal.csv.handlers.ResultSetHandler;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.modules.screens.RawScreen;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.services.storage.ALStorageService;

/**
 *
 *
 */
public class FileIOExportScreen extends RawScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileuploadRawScreen.class.getName());

  /**
   * 
   * @param rundata
   * @return
   */
  @Override
  protected String getContentType(RunData rundata) {
    return "text/plain; charset=utf8";
  }

  /**
   * 
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata) throws Exception {
    PrintWriter writer = null;
    InputStream resourceAsStream =
      rundata.getServletContext().getResourceAsStream(
        "WEB-INF/datasource/dbcp-org001.properties");
    Connection conn = null;

    File tmpDir =
      new File(JetspeedResources.getString("aipo.tmp.directory", "")
        + ALStorageService.separator()
        + "csv");

    if (!tmpDir.isDirectory()) {
      tmpDir.mkdirs();
    }

    try {
      writer = rundata.getResponse().getWriter();
      {
        Properties dbcpProp = new Properties();
        dbcpProp.load(resourceAsStream);
        String driverClassName =
          dbcpProp.get("cayenne.dbcp.driverClassName").toString();
        String url = dbcpProp.get("cayenne.dbcp.url").toString();
        String username = dbcpProp.get("cayenne.dbcp.username").toString();
        String password = dbcpProp.get("cayenne.dbcp.password").toString();
        Class.forName(driverClassName);
        conn = DriverManager.getConnection(url, username, password);

        DatabaseMetaData meta = conn.getMetaData();
        ResultSet tables =
          meta.getTables(null, null, null, new String[] { "TABLE" });
        CsvConfig csvConfig = new CsvConfig();
        csvConfig.setQuote('"');
        csvConfig.setQuoteDisabled(false);
        csvConfig.setEscape('"');
        csvConfig.setEscapeDisabled(false);
        ResultSetHandler resultSetHandler = new ResultSetHandler();
        while (tables.next()) {
          String tableName = tables.getString("table_name");
          PreparedStatement statement =
            conn.prepareStatement("SELECT * FROM " + tableName + ";");
          ResultSet resultSet = statement.executeQuery();
          Csv.save(
            resultSet,
            new File(tmpDir, tableName + ".csv"),
            csvConfig,
            resultSetHandler);
          resultSet.close();
          statement.close();
        }
      }

    } catch (Exception e) {
      logger.error("[ERROR]" + e);
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }
}

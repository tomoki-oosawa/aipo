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

package com.aimluck.eip.filter;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 *
 */
public class ALMigrateFilter implements javax.servlet.Filter {
  public FilterConfig filterConfig;

  @Override
  public void doFilter(final ServletRequest request,
      final ServletResponse response, FilterChain chain)
      throws java.io.IOException, javax.servlet.ServletException {
    chain.doFilter(request, response);
  }

  @Override
  public void init(final FilterConfig filterConfig) {
    this.filterConfig = filterConfig;
    ServletContext servletContext = this.filterConfig.getServletContext();
    InputStream resourceAsStream =
      servletContext
        .getResourceAsStream("WEB-INF/datasource/dbcp-org001.properties");
    Connection conn = null;
    Statement stmt = null;
    try {
      Properties dbcpProp = new Properties();
      dbcpProp.load(resourceAsStream);
      String driverClassName =
        dbcpProp.get("cayenne.dbcp.driverClassName").toString();
      String url = dbcpProp.get("cayenne.dbcp.url").toString();
      String username = dbcpProp.get("cayenne.dbcp.username").toString();
      String password = dbcpProp.get("cayenne.dbcp.password").toString();

      if (url.startsWith("jdbc:postgresql")) {
        // JDBCドライバのロード
        Class.forName(driverClassName);
        // データベース接続
        System.out.println("Connecting to " + url);
        conn = DriverManager.getConnection(url, username, password);
        stmt = conn.createStatement();

        // テーブル構造の確認
        List<String> columnsTurbineUser = new ArrayList<String>();
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet columns = meta.getColumns(null, null, "turbine_user", null);
        while (columns.next()) {
          columnsTurbineUser
            .add(columns.getString("COLUMN_NAME").toUpperCase());
        }
        columns.close();

        boolean hasBatch = false;
        /**
         * 7000to7010
         */
        if (!columnsTurbineUser.contains("TUTORIAL_FORBID")) {
          stmt
            .addBatch("ALTER TABLE TURBINE_USER ADD COLUMN TUTORIAL_FORBID VARCHAR (1) DEFAULT 'F';");
          stmt.addBatch("UPDATE TURBINE_USER SET TUTORIAL_FORBID = 'T' ;");
          hasBatch = true;
        }

        /**
         * 7010to7020
         */
        if (!columnsTurbineUser.contains("PHOTO_MODIFIED_SMARTPHONE")) {
          stmt
            .addBatch("UPDATE EIP_T_ACL_PORTLET_FEATURE SET ACL_TYPE = 41 WHERE FEATURE_ID = 172 AND FEATURE_NAME = 'timecard_timecard_other';");
          stmt
            .addBatch("UPDATE EIP_T_ACL_PORTLET_FEATURE SET ACL_TYPE = 45 WHERE FEATURE_ID = 172 AND FEATURE_NAME = 'timecard_timecard_other';");
          stmt
            .addBatch("ALTER TABLE activity ALTER COLUMN title TYPE character varying(255);");
          stmt
            .addBatch("ALTER TABLE activity ALTER COLUMN title SET NOT NULL;");

          if (!columnsTurbineUser.contains("PHOTO_SMARTPHONE")) {
            stmt
              .addBatch("ALTER TABLE turbine_user  ADD PHOTO_SMARTPHONE bytea;");
          }
          if (!columnsTurbineUser.contains("HAS_PHOTO_SMARTPHONE")) {
            stmt
              .addBatch("ALTER TABLE turbine_user  ADD HAS_PHOTO_SMARTPHONE  VARCHAR (1) DEFAULT 'F';");
          }

          stmt
            .addBatch("ALTER TABLE turbine_user  ADD PHOTO_MODIFIED_SMARTPHONE TIMESTAMP;");
          hasBatch = true;
        }
        if (hasBatch) {
          stmt.executeBatch();
        }
      }

    } catch (SQLException e) {
      while (e != null) {
        System.err.println(e.getMessage());
        System.err.println(e.getSQLState());
        System.err.println(e.getErrorCode());
        System.out.println("");

        e = e.getNextException();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        stmt.close();
      } catch (Exception i2) {
      }
      try {
        conn.close();
      } catch (Exception i3) {
      }
    }
  }

  @Override
  public void destroy() {
  }
}
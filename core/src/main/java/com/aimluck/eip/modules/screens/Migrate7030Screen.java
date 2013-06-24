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
import java.io.FileReader;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletConfig;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Layout;
import org.apache.jetspeed.om.profile.Parameter;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.psml.PsmlEntry;
import org.apache.jetspeed.om.profile.psml.PsmlLayout;
import org.apache.jetspeed.om.profile.psml.PsmlParameter;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.psmlmanager.db.DBUtils;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.modules.screens.RawScreen;
import org.apache.turbine.util.RunData;
import org.exolab.castor.mapping.Mapping;
import org.xml.sax.InputSource;

import com.aimluck.eip.cayenne.om.account.JetspeedUserProfile;

/**
 * ブラウザにXLSファイルを返すクラスです。 <br />
 * 
 */
public class Migrate7030Screen extends RawScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(Migrate7030Screen.class.getName());

  /** アクセス権限の有無 */
  protected boolean hasAuthority;

  @Override
  protected void doOutput(RunData rundata) throws Exception {
    try {

      InputStream resourceAsStream =
        rundata.getServletContext().getResourceAsStream(
          "WEB-INF/datasource/dbcp-org001.properties");
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
            columnsTurbineUser.add(columns
              .getString("COLUMN_NAME")
              .toUpperCase());
          }
          columns.close();

          boolean hasBatch = false;
          /**
           * 7000to7010
           */
          if (!columnsTurbineUser.contains("TUTORIAL_FORBID")) {
            stmt
              .addBatch("ALTER TABLE TURBINE_USER ADD COLUMN TUTORIAL_FORBID VARCHAR (1) DE   AULT 'F';");
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

          /*
           * 7020to7030
           */

          SelectQuery query = new SelectQuery(JetspeedUserProfile.class);
          Expression exp =
            ExpressionFactory.matchExp(
              JetspeedUserProfile.MEDIA_TYPE_PROPERTY,
              "html");
          query.setQualifier(exp);
          String orgId = "org001";
          DataContext dataContext = DataContext.getThreadDataContext();

          @SuppressWarnings("unchecked")
          List<JetspeedUserProfile> list = dataContext.performQuery(query);

          Mapping mapping = getMapping(rundata);

          if (list != null) {
            for (JetspeedUserProfile prof : list) {
              Portlets portlets =
                DBUtils.bytesToPortlets(prof.getProfile(), mapping);
              Iterator<Portlets> iterator = portlets.getPortletsIterator();
              while (iterator.hasNext()) {
                Portlets childPortlets = iterator.next();
                if ("101".equals(childPortlets.getId())) {
                  Boolean exists = false;
                  for (Entry entry : childPortlets.getEntriesArray()) {
                    if ("Export".equals(entry.getParent())) {
                      exists = true;
                    }
                  }
                  if (!exists) {
                    long afterPos = 0;
                    for (Entry entry : childPortlets.getEntriesArray()) {
                      if ("FileIO".equals(entry.getParent())) {
                        Layout layout = entry.getLayout();
                        afterPos = layout.getPosition();
                        break;
                      }
                    }

                    for (Entry entry : childPortlets.getEntriesArray()) {
                      Layout layout = entry.getLayout();
                      if ("FileIO".equals(entry.getParent())) {
                        PsmlEntry entryPsml = new PsmlEntry();
                        entryPsml.setId("130");
                        entryPsml.setParent("Export");
                        Layout newLayout = new PsmlLayout();
                        Parameter columnParam = new PsmlParameter();
                        columnParam.setName("column");
                        columnParam.setValue("0");
                        Parameter rowParam = new PsmlParameter();
                        rowParam.setName("row");
                        rowParam.setValue("0");
                        newLayout.addParameter(columnParam);
                        newLayout.addParameter(rowParam);
                        newLayout.setPosition(layout.getPosition() + 1);
                        entryPsml.setLayout(newLayout);
                        childPortlets.addEntry(entryPsml);
                      } else if (layout.getPosition() > afterPos) {
                        layout.setPosition(layout.getPosition() + 1);
                      }
                    }
                  }
                  byte[] newProfBytes =
                    DBUtils.portletsToBytes(portlets, mapping);
                  prof.setProfile(newProfBytes);
                  dataContext.commitChanges();
                }
              }
            }
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
    } catch (Exception e) {
      logger.error("ALXlsScreen.doOutput", e);
    }
  }

  /**
   * @param paramRunData
   * @return
   */
  @Override
  protected String getContentType(RunData paramRunData) {
    return "text/plain";
  }

  public static Mapping getMapping(RunData runData) {

    ServletConfig conf = runData.getServletConfig();

    String mapFilePath =
      conf.getServletContext().getRealPath(
        JetspeedResources.getString("services.PsmlManager.mapping"));

    File map = new File(mapFilePath);

    if (!map.exists()) {
      return null;
    }

    Mapping mapping = null;

    try {
      mapping = new Mapping();
      InputSource is = new InputSource(new FileReader(map));
      is.setSystemId(map.getCanonicalPath());
      mapping.loadMapping(is);
    } catch (RuntimeException e) {
      logger.error("PSMLManager: Error in psml mapping creation", e);
    } catch (Exception e) {
      logger.error("PSMLManager: Error in psml mapping creation", e);
    }

    return mapping;
  }
}

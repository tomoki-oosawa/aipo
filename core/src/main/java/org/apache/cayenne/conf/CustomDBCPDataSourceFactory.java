/*
 * Aipo is a groupware program developed by TOWN, Inc.
 * Copyright (C) 2004-2015 TOWN, Inc.
 * http://aipostyle.com/
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

package org.apache.cayenne.conf;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.cayenne.access.CustomDataSourceUtil;
import org.apache.commons.dbcp.ThreadPoolingDataSource;
import org.apache.commons.pool.ObjectPool;

/**
 *
 */
public class CustomDBCPDataSourceFactory extends DBCPDataSourceFactory
    implements DataSourceFactoryDelegate {

  @Override
  public DataSource getDataSource(String location) throws Exception {
    DBCPDataSourceProperties properties = null;
    if (System.getProperty("catalina.home") != null) {
      String baseLocation =
        new StringBuilder(System.getProperty("catalina.home")).append(
          File.separator).append(location).toString();
      FileInputStream in = null;
      try {
        in = new FileInputStream(baseLocation);
        Properties p = new Properties();
        p.load(in);
        properties = new DBCPDataSourceProperties(p);
      } catch (Throwable ignore) {
        // ignore
      } finally {
        if (in != null) {
          try {
            in.close();
          } catch (Throwable ignore) {
            // ignore
          }
        }
      }
    }
    if (properties == null) {
      properties =
        new DBCPDataSourceProperties(
          parentConfiguration.getResourceLocator(),
          location);
    }

    CustomDBCPDataSourceBuilder builder =
      new CustomDBCPDataSourceBuilder(properties);
    return builder.createDataSource();
  }

  /**
   *
   */
  @Override
  public void tearDown() {
    try {
      DataSource dataSource = CustomDataSourceUtil.getThreadDataSource();
      if (dataSource instanceof ThreadPoolingDataSource) {
        ThreadPoolingDataSource poolingDataSource =
          (ThreadPoolingDataSource) dataSource;
        if (poolingDataSource != null) {
          ObjectPool pool = poolingDataSource.getPool();
          if (pool != null) {
            try {
              pool.close();
            } catch (Throwable t) {
              //
            }
          }
        }
      }
    } catch (Throwable t) {
    }
  }
}

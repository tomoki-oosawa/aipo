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
package com.aimluck.eip.orm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.conf.Configuration;
import org.apache.cayenne.conf.ServletUtil;
import org.apache.cayenne.map.DataMap;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.orm.DatabaseOrmService;

/**
 * データベースマッピングクラスです。 <br />
 * 
 */
public class PkgDatabaseOrmService extends DatabaseOrmService {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(PkgDatabaseOrmService.class.getName());

  public static final String ORG_PKG = "org001";

  @Override
  protected void initOrm(ServletContext context) throws InitializationException {
    logger.debug("initOrm");

    ServletUtil.initializeSharedConfiguration(context);

    // ORG_PKG にユーザテーブルをマッピングする
    DataDomain dataDomain =
      Configuration.getSharedConfiguration().getDomain(SHARED_DOMAIN);
    Collection<?> dataMaps = dataDomain.getDataMaps();

    Iterator<?> iter = dataMaps.iterator();
    while (iter.hasNext()) {
      DataMap map = (DataMap) iter.next();
      logger.debug("map:" + map.getName());
      copyDataMap(SHARED_DOMAIN, map.getName(), ORG_PKG);
    }
  }

  @Override
  public String getDefaultOrgId() {
    return ORG_PKG;
  }

  @Override
  public String getOrgId(String company_id) {
    return null;
  }

  @Override
  public String getCompanyId(String org_id) {
    return null;
  }

  @Override
  public String getOrgId(RunData rundata) {
    return ORG_PKG;
  }

  @Override
  public String getOrgId(HttpSession session) {
    return ORG_PKG;
  }

  @Override
  public DataContext getDataContext() {
    RunData rundata = getRunData();
    try {
      if (rundata == null) {
        return DataContext.createDataContext(ORG_PKG);
      }
      return getDefaultContext(rundata.getSession());
    } catch (IllegalStateException e) {
      rundata.setScreenTemplate("Timeout");
      return DataContext.createDataContext(ORG_PKG);
    }
  }

  @Override
  public List<String> getOrgKeys() {
    List<String> orglist = new ArrayList<String>();
    orglist.add(ORG_PKG);
    return orglist;
  }

  @Override
  public String getAlias() {
    return JetspeedResources.getString("aipo.alias");
  }

  @Override
  public String getAliasjp() {
    return JetspeedResources.getString("aipo.aliasjp");
  }

  @Override
  public String getCopyright() {
    return JetspeedResources.getString("aipo.copyright");
  }

  @Override
  public String getCopyrightShort() {
    return JetspeedResources.getString("aipo.copyright_short");
  }

  @Override
  public String getTheme() {
    return JetspeedResources.getString("aipo.theme");
  }

}

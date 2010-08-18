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
package com.aimluck.eip.orm;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.conf.Configuration;
import org.apache.cayenne.conf.ServletUtil;
import org.apache.cayenne.dba.AutoAdapter;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.rundata.JetspeedRunDataService;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.common.ALDBErrorException;

/**
 * データベースマッピングクラスです。 <br />
 * 
 */
abstract public class DatabaseOrmService extends TurbineBaseService {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(DatabaseOrmService.class.getName());

  public static final String SERVICE_NAME = "DatabaseOrmService";

  public static final String ORG_DEFAULT = "org000";

  public static final String ORG_PRE = "org";

  protected static final String SHARED_DOMAIN = "SharedDomain";

  protected JetspeedRunDataService runDataService = null;

  abstract protected void initOrm(ServletContext context)
      throws InitializationException;

  /**
   * 
   * @param srcDataDomainName
   * @param srcDataMapName
   * @param destDataDomainName
   */
  protected void copyDataMap(String srcDataDomainName, String srcDataMapName,
      String destDataDomainName) {

    DataDomain destDataDomain = Configuration.getSharedConfiguration()
      .getDomain(destDataDomainName);

    if (destDataDomain == null) {
      return;
    }

    DataDomain sharedDataDomain = Configuration.getSharedConfiguration()
      .getDomain(srcDataDomainName);
    destDataDomain.addMap(sharedDataDomain.getMap(srcDataMapName));

    Collection<?> nodes = destDataDomain.getDataNodes();

    if (nodes.size() != 1) {
      return;
    }

    Iterator<?> iter = nodes.iterator();

    while (iter.hasNext()) {
      DataNode node = (DataNode) iter.next();
      node.addDataMap(sharedDataDomain.getMap(srcDataMapName));
    }
  }

  public static DatabaseOrmService getInstance() {
    return (DatabaseOrmService) TurbineServices.getInstance().getService(
      DatabaseOrmService.SERVICE_NAME);
  }

  abstract public String getOrgId(String company_id);

  abstract public String getCompanyId(String org_id);

  abstract public String getOrgId(RunData rundata);

  abstract public String getOrgId(HttpSession session);

  abstract public List<String> getOrgKeys();

  abstract public String getDefaultOrgId();

  /**
   * 
   * @param session
   * @return
   */
  public static DataContext getDefaultContext(HttpSession session) {
    synchronized (session) {
      try {
        DataContext ctxt = (DataContext) session
          .getAttribute(ServletUtil.DATA_CONTEXT_KEY);

        if (ctxt == null) {
          JetspeedRunData rundata = getInstance().getRunData();
          if (rundata != null
            && rundata.getParameters().containsKey(DatabaseOrmService.ORG_PRE)
            && !"".equals(rundata.getParameters().getString(
              DatabaseOrmService.ORG_PRE))) {
            ctxt = DataContext.createDataContext(rundata.getParameters()
              .getString(DatabaseOrmService.ORG_PRE));
          } else {
            String org_id = DatabaseOrmService.getInstance().getOrgId(session);
            ctxt = DataContext.createDataContext(org_id);
          }
          session.setAttribute(ServletUtil.DATA_CONTEXT_KEY, ctxt);
        }
        return ctxt;
      } catch (IllegalStateException e) {
        DataContext ctxt = null;
        JetspeedRunData rundata = getInstance().getRunData();
        if (rundata != null
          && rundata.getParameters().containsKey(DatabaseOrmService.ORG_PRE)
          && !"".equals(rundata.getParameters().getString(
            DatabaseOrmService.ORG_PRE))) {
          ctxt = DataContext.createDataContext(rundata.getParameters()
            .getString(DatabaseOrmService.ORG_PRE));
        } else {
          ctxt = DataContext.createDataContext(getInstance().getDefaultOrgId());
        }

        return ctxt;
      }
    }
  }

  public DataContext getDataContext() {
    RunData rundata = getRunData();
    if (rundata == null) {
      return null;
    }
    return getDefaultContext(rundata.getSession());
  }

  protected JetspeedRunData getRunData() {
    JetspeedRunData rundata = null;
    if (this.runDataService != null) {
      rundata = this.runDataService.getCurrentRunData();
    }
    return rundata;
  }

  public void addDomain(String jdbcDriver, String dataSourceUrl, int minCons,
      int maxCons, String userName, String password, String domainName,
      String dataNodeName) {
    try {
      DataSource ds = new org.apache.cayenne.conn.PoolManager(jdbcDriver,
        dataSourceUrl, minCons, maxCons, userName, password);

      DataDomain domain = new DataDomain(domainName);

      DataNode node = new DataNode(dataNodeName);
      node.setAdapter(new AutoAdapter(ds));
      node.setDataSource(ds);
      node.setDataSourceLocation(dataNodeName + ".driver.xml");

      domain.addNode(node);

      Configuration.getSharedConfiguration().addDomain(domain);
      copyDataMap(SHARED_DOMAIN, "AccountSharedDomainMap", domainName);
      copyDataMap(SHARED_DOMAIN, "SecuritySharedDomainMap", domainName);
      copyDataMap(SHARED_DOMAIN, "SharedDomainMap", domainName);
    } catch (Exception e) {
      logger.error("[ERROR]", e);
    }
  }

  public void remomveDomain(String domainName) {
    try {
      DataDomain domain = Configuration.getSharedConfiguration().getDomain(
        domainName);
      if (domain == null) {
        throw new ALDBErrorException();
      }

      domain.shutdown();

      Configuration.getSharedConfiguration().removeDomain(domainName);
    } catch (Exception e) {
      logger.error("[ERROR]", e);
    }
  }

  /**
   *
   */
  @Override
  public synchronized void init(ServletConfig conf)
      throws InitializationException {
    if (getInit()) {
      return;
    }

    super.init(conf);

    this.runDataService = (JetspeedRunDataService) TurbineServices
      .getInstance().getService(RunDataService.SERVICE_NAME);

    initOrm(conf.getServletContext());
    setInit(true);
  }

  abstract public String getAlias();

  abstract public String getAliasjp();

  abstract public String getCopyright();

  abstract public String getCopyrightShort();

  abstract public String getTheme();
}

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
package com.aimluck.eip.modules.actions.project;

import static com.aimluck.eip.modules.actions.project.util.ProjectTestUtil.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.conf.Configuration;
import org.apache.cayenne.conf.DefaultConfiguration;
import org.apache.cayenne.util.WebApplicationResourceLocator;
import org.apache.jetspeed.services.resources.JetspeedResourceService;
import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineServices;
import org.easymock.EasyMock;

import com.aimluck.eip.orm.Database;

public class ProjectTestInitializer {

  public static void initialize() {
    System.setProperty("catalina.home", System.getProperty("user.dir"));

    ServletContext servletContext = EasyMock.createMock(ServletContext.class);

    WebApplicationResourceLocator locator = new WebApplicationResourceLocator();
    locator.setSkipAbsolutePath(true);
    locator.setSkipClasspath(true);
    locator.setSkipCurrentDirectory(true);
    locator.setSkipHomeDirectory(true);
    locator.addFilesystemPath(System.getProperty("user.dir")
      + FS
      + fileSeparatorJoin("war", "target", "aipo", "WEB-INF"));

    DefaultConfiguration conf =
      new DefaultConfiguration(Configuration.DEFAULT_DOMAIN_FILE, locator);
    Configuration.initializeSharedConfiguration(conf);
    Database.initialize(servletContext);

    ServletConfig config = EasyMock.createNiceMock(ServletConfig.class);
    ServletContext context = EasyMock.createNiceMock(ServletContext.class);

    HttpServletRequest request =
      EasyMock.createNiceMock(HttpServletRequest.class);
    HttpServletResponse response =
      EasyMock.createNiceMock(HttpServletResponse.class);

    EasyMock.expect(config.getServletName()).andStubReturn("Aipo");
    EasyMock.expect(config.getServletContext()).andStubReturn(context);
    EasyMock
      .expect(config.getInitParameter(TurbineConstants.APPLICATION_ROOT))
      .andStubReturn(
        System.getProperty("user.dir")
          + FS
          + fileSeparatorJoin("war", "target", "aipo"));
    EasyMock
      .expect(config.getInitParameter(TurbineServices.PROPERTIES_PATH_KEY))
      .andStubReturn(
        FS
          + fileSeparatorJoin(
            "WEB-INF",
            "conf",
            "JetspeedJResources.properties"));
    EasyMock.expect(request.getMethod()).andStubReturn("POST");
    EasyMock.expect(request.getContextPath()).andStubReturn("/context");
    EasyMock.expect(request.getRequestURI()).andStubReturn("");
    EasyMock.expect(context.getRealPath("/")).andStubReturn(getRoot());

    EasyMock.replay(config);
    EasyMock.replay(context);
    EasyMock.replay(request);
    EasyMock.replay(response);

    Turbine turbine = new Turbine();
    JetspeedResourceService service = new JetspeedResourceService();
    try {
      turbine.init(config);
      service.init(config);
    } catch (ServletException e) {
      e.printStackTrace();
    } catch (InitializationException e) {
      e.printStackTrace();
    }

    DataContext datacontext = null;
    try {
      datacontext = Database.createDataContext(Database.DEFAULT_ORG);
    } catch (Exception e) {
      e.printStackTrace();
    }

    DataContext.bindThreadDataContext(datacontext);
  }

  public static void finalizeTest() {

  }

  private static String getRoot() {
    return System.getProperty("user.dir")
      + FS
      + fileSeparatorJoin("war", "target", "aipo");
  }
}

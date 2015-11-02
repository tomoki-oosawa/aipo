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
package com.aimluck.eip.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.conf.Configuration;
import org.apache.cayenne.conf.ServletUtil;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.http.HttpServletRequestLocator;
import com.aimluck.eip.http.HttpServletResponseLocator;
import com.aimluck.eip.http.ServletContextLocator;
import com.aimluck.eip.orm.Database;

/**
 *
 */
public class ALBaseFilter implements Filter {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALBaseFilter.class.getName());

  private FilterConfig filterConfig;

  /**
   *
   */
  @Override
  public void destroy() {
  }

  /**
   * @param request
   * @param response
   * @param filterChain
   * @throws IOException
   * @throws ServletException
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain filterChain) throws IOException, ServletException {
    ServletContext prevServletContext = ServletContextLocator.get();
    HttpServletRequest prevHttpServletRequest = HttpServletRequestLocator.get();
    HttpServletResponse prevHttpServletResponse =
      HttpServletResponseLocator.get();
    if (request instanceof HttpServletRequest) {
      HttpServletRequest hreq = (HttpServletRequest) request;
      if (isInvalid(hreq.getQueryString()) || isInvalid(hreq.getRequestURI())) {
        ((HttpServletResponse) response)
          .sendError(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }
    }
    try {
      DataContext dataContext = null;
      try {
        dataContext =
          Database.createDataContext(getCurrentOrgId(
            (HttpServletRequest) request,
            (HttpServletResponse) response));
      } catch (Exception e) {
        logger.error("ALBaseFilter.doFilter", e);
      }
      ServletContextLocator.set(filterConfig.getServletContext());
      HttpServletRequestLocator.set((HttpServletRequest) request);
      HttpServletResponseLocator.set((HttpServletResponse) response);
      DataContext.bindThreadDataContext(dataContext);
      filterChain.doFilter(request, response);
    } finally {
      ServletContextLocator.set(prevServletContext);
      HttpServletRequestLocator.set(prevHttpServletRequest);
      HttpServletResponseLocator.set(prevHttpServletResponse);
      Database.tearDown();
    }
  }

  /**
   * @param filterConfig
   * @throws ServletException
   */
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    this.filterConfig = filterConfig;
    Configuration.configureCommonLogging(null);
    ServletUtil.initializeSharedConfiguration(filterConfig.getServletContext());
    Database.initialize(filterConfig.getServletContext());
  }

  protected String getCurrentOrgId(HttpServletRequest request,
      HttpServletResponse response) {
    return Database.DEFAULT_ORG;
  }

  /**
   * XXS url attack protection blocking access
   */
  private boolean isInvalid(String value) {
    return (value != null && (value.indexOf('<') != -1
      || value.indexOf('>') != -1
      || value.indexOf("%3C") != -1
      || value.indexOf("%3c") != -1
      || value.indexOf("%3E") != -1 || value.indexOf("%3e") != -1));
  }
}

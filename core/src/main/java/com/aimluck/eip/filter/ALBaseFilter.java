/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2010 Aimluck,Inc.
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

package com.aimluck.eip.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.cayenne.access.DataContext;

import com.aimluck.eip.orm.DataContextLocator;

/**
 * 
 */
public class ALBaseFilter implements Filter {

  /**
   * 
   */
  public void destroy() {
  }

  /**
   * @param request
   * @param response
   * @param filterChain
   * @throws IOException
   * @throws ServletException
   */
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain filterChain) throws IOException, ServletException {
    DataContext previousDataContext = null;
    try {
      previousDataContext = DataContextLocator.get();
      filterChain.doFilter(request, response);
    } finally {
      DataContextLocator.set(previousDataContext);
    }
  }

  /**
   * @param filterConfig
   * @throws ServletException
   */
  public void init(FilterConfig filterConfig) throws ServletException {
  }

}

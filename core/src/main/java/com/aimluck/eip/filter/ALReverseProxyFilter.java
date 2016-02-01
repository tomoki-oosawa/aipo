/*
 * This file is part of the com.aipo.aws package.
 * Copyright (C) 2004-2011 Aimluck,Inc.
 * http://www.aipo.com
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.aimluck.eip.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 */
public class ALReverseProxyFilter implements Filter {

  @SuppressWarnings("unused")
  private static final Logger logger = Logger
    .getLogger(ALReverseProxyFilter.class.getName());

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

    filterChain.doFilter(
      getHttpServletRequestWrapper((HttpServletRequest) request),
      response);
  }

  /**
   * @param filterConfig
   * @throws ServletException
   */
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  /**
   *
   * @param request
   * @return
   */
  protected HttpServletRequestWrapper getHttpServletRequestWrapper(
      HttpServletRequest request) {
    return new ALHttpServletRequestWrapper(request);
  }
}

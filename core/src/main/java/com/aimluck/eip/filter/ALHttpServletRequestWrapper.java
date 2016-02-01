/*
 * This file is part of the com.aipo.aws package.
 * Copyright (C) 2004-2011 Aimluck,Inc.
 * http://www.aipo.com
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.aimluck.eip.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 */
public class ALHttpServletRequestWrapper extends HttpServletRequestWrapper {

  private final String protocol;

  private final int port;

  private String remoteAddr;

  /**
   * @param request
   */
  public ALHttpServletRequestWrapper(HttpServletRequest request,
      String protocol, int port) {
    super(request);

    this.protocol = protocol;
    this.port = port;

    String header = getHeader("X-FORWARDED-FOR");

    if (header != null && header != "") {
      String[] split = header.split(",");
      remoteAddr = split[0];
    } else {
      remoteAddr = null;
    }
  }

  public ALHttpServletRequestWrapper(HttpServletRequest request) {
    super(request);

    String hfor = getHeader("X-FORWARDED-FOR");

    if (hfor != null && hfor != "") {
      String[] split = hfor.split(",");
      remoteAddr = split[0];
    } else {
      remoteAddr = null;
    }
    String hport = getHeader("X-FORWARDED-PORT");
    if (hport != null && hport != "") {
      port = Integer.valueOf(hport);
    } else {
      port = -1;
    }
    String hhttps = getHeader("X-FORWARDED-PROTO");
    if (hhttps != null && hhttps != "") {
      protocol = hhttps;
    } else {
      protocol = null;
    }
  }

  /**
   *
   * @return
   */
  @Override
  public int getServerPort() {
    return port != -1 ? port : super.getServerPort();
  }

  /**
   *
   * @return
   */
  @Override
  public String getScheme() {
    return protocol != null ? protocol : super.getScheme();
  }

  /**
   *
   * @return
   */
  @Override
  public String getRemoteAddr() {
    return isProxyRequest() ? remoteAddr : super.getRemoteAddr();
  }

  @Override
  public StringBuffer getRequestURL() {
    int port = getServerPort();
    String protocol = getScheme();
    return isProxyRequest() ? new StringBuffer(protocol).append("://").append(
      getServerName()).append(
      (port == 443 || port == 80) ? "" : ":" + String.valueOf(port)).append(
      getRequestURI()) : super.getRequestURL();
  }

  @Override
  public boolean isSecure() {
    return protocol != null ? "https".equals(protocol) : super.isSecure();
  }

  /**
   *
   * @return
   */
  protected boolean isProxyRequest() {
    return remoteAddr != null;
  }

}

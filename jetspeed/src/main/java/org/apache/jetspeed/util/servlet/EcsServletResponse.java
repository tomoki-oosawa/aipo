/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jetspeed.util.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * EcsServletResponse is an HttpServletResponse implementation that wraps an
 * existing HttpServletResponse and redirects its output to an alternate
 * OutputStream.
 * 
 * @author <a href="raphael@apache.org">Rapha�l Luta</a>
 */
public class EcsServletResponse implements HttpServletResponse {
  /** The wrapped ServletResponse */
  private final HttpServletResponse rsp;

  /** The new OutputStream to use */
  private final EcsServletOutputStream sout;

  /** The PrintWriter to use if defined */
  private PrintWriter writer;

  /** state flag: has getOutputStream() already been called ? */
  private boolean outUsed = false;

  /** state flag: has getWriter() already been called ? */
  private boolean writerUsed = false;

  /**
   * Build a Response
   * 
   * @param rsp
   *          the base HttpServletResponse to wrap
   * @param out
   *          the OutputStream to be used for writing data
   */
  public EcsServletResponse(HttpServletResponse rsp, OutputStream out) {
    this.rsp = rsp;
    this.sout = new EcsServletOutputStream(out);
  }

  /**
   * @see javax.servlet.ServletResponse#getCharacterEncoding
   */
  public String getCharacterEncoding() {
    return rsp.getCharacterEncoding();
  }

  /**
   * @see javax.servlet.ServletResponse#getOutputStream
   */
  public ServletOutputStream getOutputStream() throws IOException {
    if (writerUsed) {
      throw new IllegalStateException("Writer already used");
    }

    this.outUsed = true;

    return this.sout;
  }

  /**
   * @see javax.servlet.ServletResponse#getWriter
   */
  public PrintWriter getWriter() throws IOException {
    if (outUsed) {
      throw new IllegalStateException("OutputStream already in use");
    }

    if (writer == null) {
      this.writer = new PrintWriter(this.sout);
    }

    this.writerUsed = true;

    return writer;
  }

  /**
   * @see javax.servlet.ServletResponse#setContentLength
   */
  public void setContentLength(int len) {
    // silently fail
  }

  /**
   * @see javax.servlet.ServletResponse#setContentType
   */
  public void setContentType(String type) {
    // silently fail
  }

  /**
   * @see javax.servlet.ServletResponse#setBufferSize
   */
  public void setBufferSize(int size) {
    // silently fail
  }

  /**
   * @see javax.servlet.ServletResponse#getBufferSize
   */
  public int getBufferSize() {
    return rsp.getBufferSize();
  }

  /**
   * @see javax.servlet.ServletResponse#flushBuffer
   */
  public void flushBuffer() throws IOException {
    this.sout.flush();
  }

  /**
   * @see javax.servlet.ServletResponse#isCommitted
   */
  public boolean isCommitted() {
    // we don't implement reset() so always claim
    // the data is committed
    return true;
  }

  /**
   * @see javax.servlet.ServletResponse#reset
   */
  public void reset() {
    // silently fail
  }

  /**
   * @see javax.servlet.ServletResponse#setLocale
   */
  public void setLocale(Locale loc) {
    // we can safely ignore this
  }

  /**
   * @see javax.servlet.ServletResponse#getLocale
   */
  public Locale getLocale() {
    return rsp.getLocale();
  }

  /**
   * @see javax.servlet.http.HttpServletResponse#addCookie
   */
  public void addCookie(Cookie cookie) {
    rsp.addCookie(cookie);
  }

  /**
   * @see javax.servlet.http.HttpServletResponse#containsHeader
   */
  public boolean containsHeader(String name) {
    return rsp.containsHeader(name);
  }

  /**
   * @see javax.servlet.http.HttpServletResponse#encodeURL
   */
  public String encodeURL(String url) {
    return rsp.encodeURL(url);
  }

  /**
   * @see javax.servlet.http.HttpServletResponse#encodeRedirectURL
   */
  public String encodeRedirectURL(String url) {
    return rsp.encodeRedirectURL(url);
  }

  /**
   * @see javax.servlet.http.HttpServletResponse#encodeUrl
   */
  public String encodeUrl(String url) {
    return rsp.encodeUrl(url);
  }

  /**
   * @see javax.servlet.http.HttpServletResponse#encodeRedirectUrl
   */
  public String encodeRedirectUrl(String url) {
    return rsp.encodeRedirectUrl(url);
  }

  /**
   * @see javax.servlet.http.HttpServletResponse#sendError
   */
  public void sendError(int sc, String msg) throws IOException {
    // silently fail
  }

  /**
   * @see javax.servlet.http.HttpServletResponse#sendError
   */
  public void sendError(int sc) throws IOException {
    // silently fail
  }

  /**
   * @see javax.servlet.http.HttpServletResponse#sendRedirect
   */
  public void sendRedirect(String location) throws IOException {
    // silently fail
  }

  /**
   * @see javax.servlet.http.HttpServletResponse#setDateHeader
   */
  public void setDateHeader(String name, long date) {
    // silently fail
  }

  /**
   * @see javax.servlet.http.HttpServletResponse#addDateHeader
   */
  public void addDateHeader(String name, long date) {
    // silently fail
  }

  /**
   * @see javax.servlet.http.HttpServletResponse#setHeader
   */
  public void setHeader(String name, String value) {
    // silently fail
  }

  /**
   * @see javax.servlet.http.HttpServletResponse#addHeader
   */
  public void addHeader(String name, String value) {
    // silently fail
  }

  /**
   * @see javax.servlet.http.HttpServletResponse#setIntHeader
   */
  public void setIntHeader(String name, int value) {
    // silently fail
  }

  /**
   * @see javax.servlet.http.HttpServletResponse#addIntHeader
   */
  public void addIntHeader(String name, int value) {
    // silently fail
  }

  /**
   * @see javax.servlet.http.HttpServletResponse#setStatus
   */
  public void setStatus(int sc) {
    // silently fail
  }

  /**
   * @see javax.servlet.http.HttpServletResponse#setStatus
   */
  public void setStatus(int sc, String sm) {
    // silently fail
  }

  public void resetBuffer() {
    // silently fail
    // added by Ben Woodward
  }

  /**
   * @return
   */
  public String getContentType() {
    return null;
  }

  /**
   * @param charset
   */
  public void setCharacterEncoding(String charset) {
    // silently fail
  }

}

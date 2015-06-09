/*
 * Copyright 2000-2004 The Apache Software Foundation.
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

package org.apache.jetspeed.services.webpage;

// java.io
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/*
 * Abstract Base class for web page sessions. Implements the primary dispatcher
 * logic for getting and posting resources.
 * 
 */

public abstract class AbstractSiteSession implements SiteSession {

  // the base url of the host being proxied
  protected String targetBase;

  // the base url the wps
  protected String proxyBase;

  // the cookies collection
  protected HashMap cookies = new HashMap();

  // counters
  protected int hitCount = 0;

  protected int cacheCount = 0;

  // the log file singleton instance
  static Logger log = Logger.getLogger(AbstractSiteSession.class);

  /**
   * Create a NetElementSession, which maintains sessions with one network
   * element.
   * 
   * @param targetBase
   *            the target host's base URL
   * @param proxyBase
   *            the proxy server host URL base address.
   */
  public AbstractSiteSession(String targetBase, String proxyBase) {
    this.proxyBase = proxyBase;
    this.targetBase = targetBase;
  }

  /**
   * Given a URL, returns the content from that URL in a string. All HTTP
   * hyperlinks(HREFs) are rewritten as proxied-referenced hyperlinks. All
   * relative references to web resources (images, stylesheets, ...) are
   * rewritten as absolute references, but are not proxied. Determines if we are
   * logged on to the target site. If not, calls logon(), which is a
   * implementation specific 'POST' exchange
   * 
   * @see logon(String, HttpServletRequest, HttpServletResponse)
   * 
   * @param url
   *            the proxied resource address.
   * @param data
   *            the rundata
   * 
   * @exception IOException
   *                a servlet exception.
   */

  public void dispatch(String url, ProxyRunData data) throws IOException {
    try {
      Configuration config = Configuration.getInstance();

      log.debug("=== Dispatching =" + url);

      // open a pooled HTTP connection
      // URL u = new URL(url);
      URL u = new URL(null, url);
      HttpURLConnection con = (HttpURLConnection) u.openConnection();

      // if (con instanceof HttpURLConnection)
      con.setDoInput(true);
      con.setDoOutput(true);
      con.setAllowUserInteraction(false);
      con.setFollowRedirects(false);

      if (data.getPosting()) {
        con.setRequestMethod("POST");
      }

      // are there any cookies in our current session?
      if (cookies.isEmpty()) {
        // there are no cookies, must be a new session, so lets logon
        log.debug("... no session id provided. Logging on...");

        if (false == logon(data)) {
          return;
        }

      }

      // send the cookies (session ids) back to the NE
      Iterator it = cookies.values().iterator();
      Cookie cookie;
      while (it.hasNext()) {
        cookie = (Cookie) it.next();
        String sessionID = WebPageHelper.buildCookieString(cookie);
        con.setRequestProperty("Cookie", sessionID);
        log.debug("... Sending Session ID: " + sessionID);
      }

      // we have to get the post parameters from the servlet container,
      // and then re-encode them. i wish i could find out how to keep
      // the servlet container from reading them, because now i have to
      // parse them out, and then re-encode
      if (data.getPosting()) {

        // get the post params
        StringBuffer postParams = new StringBuffer();
        int count = 0;
        Enumeration e = data.getRequest().getParameterNames();
        while (e.hasMoreElements()) {

          String name = (String) e.nextElement();
          if (name.equals(config.getSID()) || name.equals(config.getURL())) {
            continue;
          }

          String values[] = data.getRequest().getParameterValues(name);
          if (values != null) {
            for (int i = 0; i < values.length; i++) {
              if (count > 0) {
                postParams.append("&");
              }
              postParams.append(name);
              postParams.append("=");
              postParams.append(URLEncoder.encode(values[i]));
              count++;
            }
          }
        }
        String postString = postParams.toString();
        con.setRequestProperty("Content-length", String.valueOf(postString
          .length()));
        // write directly to the output stream
        DataOutputStream dos = new DataOutputStream(con.getOutputStream());

        log.debug("... POST: " + postString);
        dos.writeBytes(postString);
        dos.close();
      }

      int rc = con.getResponseCode();

      // Get the Session Information from Headers
      int contentType =
        WebPageHelper.getContentType(con.getHeaderField("content-type"), u
          .toString());
      String location = con.getHeaderField("Location");

      if ((rc == con.HTTP_MOVED_PERM || rc == con.HTTP_MOVED_TEMP)
        && null != location) {
        log.debug("+++ REDIRECT = " + location);
        location = WebPageHelper.concatURLs(targetBase, location);
        dispatch(location, data);
        return;
      }

      // get cookies set from server
      String cookieString = con.getHeaderField("Set-Cookie");
      if (null != cookieString) {
        log.debug("... new SessionID found: " + cookieString);
        WebPageHelper.parseCookies(cookieString, this);
      }

      if (contentType == WebPageHelper.CT_IMAGE
        || contentType == WebPageHelper.CT_BINARY
        || contentType == WebPageHelper.CT_APPLICATION) {
        // wasn't in the cache, get it from host
        getBinaryContent(con, data.getResponse());
        return;
      }

      rewriteContent(data, con, contentType, url);

    } catch (IOException ex) {
      log.error("*** PROXY DISPATCH EXCEPTION = " + ex);
      throw ex;
    }

  }

  /**
   * Gets the HTML content from the URL Connection stream and returns it in a
   * string
   * 
   * @param con
   *            The URLConnection to read from.
   * @param resource
   *            The full URL of the resource.
   * @return The HTML Content from the stream.
   * 
   * @exception IOException
   *                a servlet exception.
   */
  public String getHTMLContent(URLConnection con, ProxyRunData data,
      String resource) throws IOException {

    int CAPACITY = 4096;

    InputStream is = con.getInputStream();
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    Configuration config = Configuration.getInstance();
    FileOutputStream fos = null;
    boolean logging = config.getEnableContentLog();
    if (logging) {
      if (data != null) {
        String fileName =
          data.getServlet().getServletContext().getRealPath(
            config.getLogLocation());
        fos = new FileOutputStream(fileName, true);
        WebPageHelper.writeHeader(fos, resource);
      }
    }

    // now process the InputStream...

    byte[] bytes = new byte[CAPACITY];

    int readCount = 0;
    int total = 0;

    while ((readCount = is.read(bytes)) > 0) {
      buffer.write(bytes, 0, readCount);
      if (logging) {
        fos.write(bytes, 0, readCount);
      }
      total += readCount;
    }
    if (logging) {
      fos.close();
    }
    is.close();

    return buffer.toString();
  }

  /**
   * Gets the HTML content from the URL Connection stream and writes it to
   * respones
   * 
   * @param con
   *            The URLConnection to read from.
   * 
   * @exception IOException
   *                a servlet exception.
   */
  public void getBinaryContent(URLConnection con, HttpServletResponse response)
      throws IOException {

    int CAPACITY = 4096;

    InputStream is = con.getInputStream();

    // FileOutputStream fos = new FileOutputStream("/test.fw", true);

    // now process the InputStream...

    byte[] bytes = new byte[CAPACITY];

    int readCount = 0;
    while ((readCount = is.read(bytes)) > 0) {

      response.getOutputStream().write(bytes, 0, readCount);
      // fos.write( bytes, 0, readCount);
    }

    // fos.close();
    is.close();

  }

  /**
   * Given a cookie, it first checks to see if that cookie is already managed in
   * this session. If it is, it means that the session has timed out and that
   * the network element has now created a new session. In that case, replace
   * the cookie, and re-establish the session (logon) If its a new cookie, we
   * will still need to logon, and and the cookie to the managed cookies
   * collection for this session.
   * 
   * @param cookie
   *            new cookie returned from target server.
   * @return true when a new cookie added, false when updated.
   * 
   */
  public boolean addCookieToSession(Cookie cookie) {
    boolean added = (null == cookies.get(cookie.getName()));
    cookies.put(cookie.getName(), cookie); // adds or updates
    return added;
  }

  /*
   * Gets the hitcount for this session.
   * 
   * @return the hitcount for this session.
   */
  public int getHitCount() {
    return hitCount;
  }

  /*
   * Increments the hitcount for this session.
   * 
   */
  public void incHitCount() {
    hitCount++;
  }

  /*
   * Gets the cache count for this session.
   * 
   * @return the cache count for this session.
   */
  public int getCacheCount() {
    return cacheCount;
  }

  /*
   * Increments the hitcount for this session.
   * 
   */
  public void incCacheCount() {
    cacheCount++;
  }

}

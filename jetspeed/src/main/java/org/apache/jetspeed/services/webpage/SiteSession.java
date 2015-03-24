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
import java.io.IOException;

// javax.servlet
import javax.servlet.http.*;

// java.net
import java.net.URLConnection;

/**
 * Standard interface for all proxied sessions.
 * Handles the communication and session state between the webpage service and a single site
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: SiteSession.java,v 1.3 2004/02/23 03:46:26 jford Exp $ 
 */
public interface SiteSession
{    

    /**
     * Given a site URL, proxies the content of that site.  
     * The actual rules on rewriting the proxied resource are dependent on implementation 
     * and configuration parameters. For example, all HTTP hyperlinks(HREFs) could be
     * rewritten as proxied hyperlinks back to this Proxy.
     * Or all relative references to web resources (images, stylesheets, ...) could be
     * rewritten as absolute references, but are not proxied.
     *
     * @param site the proxied resource address.
     * @param data the request specific rundata.
     *
     * @exception IOException a servlet exception.
     */    
    public void proxy(String site, ProxyRunData data)
                    throws IOException;

    /**
     * Gets the HTML content from the URL Connection stream and returns it as a Stream
     *
     * @param con The URLConnection to read from.
     * @param data the request specific rundata.
     * @return The HTML Content from the stream.
     *
     * @deprecate
     * @exception IOException a servlet exception.
     */
    public String getContentAsString(URLConnection con,
                                     ProxyRunData data,
                                     String url) 
                    throws IOException;

    
    /**
     * Retrieves the content from the URL Connection stream and writes it to servlet response
     *
     * @param con The URLConnection to read from.
     *
     * @exception IOException a servlet exception.
     */
    public void drainContent(URLConnection con,
                             HttpServletResponse response) throws IOException;

    /**
      * Given a cookie, it first checks to see if that cookie is already
      * managed in this session. If it is, it means that the session has
      * timed out and that the network element has now created a new session.
      * In that case, replace the cookie, and re-establish the session (logon)
      * If its a new cookie, we will still need to logon, and and the cookie to
      * the managed cookies collection for this session.
      *
      * @param cookie new cookie returned from target server.
      * @return true when a new cookie added, false when updated.
      *
      */
    public boolean addCookieToSession(Cookie cookie);

    /**
     * Logs on to the target host
     *
     * @param data the request specific rundata.     
     *
     * @exception IOException a servlet exception.
     */    
    public boolean logon(ProxyRunData data)
                      throws IOException;


    /**
     * Logs out of the target host
     *
     * @param data the request specific rundata.     
     *
     * @exception IOException a servlet exception.
     */    
    public boolean logout(ProxyRunData data)
                      throws IOException;


    /**
     * Reads stream for proxied host, runs a rewriter against that stream,
     * rewriting relevant links, and writes the parsed stream back to the client.
     *
     * @param request Servlet request.
     * @param con the URLConnection with proxied host.
     * @param contentType the contentType of the request.
     *
     * @exception IOException a servlet exception.
     */    
    public void rewriteContent(ProxyRunData data,
                               URLConnection con,
                               int contentType,
                               String url) throws IOException;

    /**
     * Gets the hitcount for this session.
     *
     * @return the hitcount for this session.
     */
    public int getHitCount();

    /**
     * Increments the hitcount for this session.
     *
     */
    public void incHitCount();

    /**
     * Gets the cache count for this session.
     *
     * @return the cache count for this session.
     */
    public int getCacheCount();

    /**
     * Increments the hitcount for this session.
     *
     */
    public void incCacheCount();

}


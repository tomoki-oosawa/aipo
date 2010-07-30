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

import javax.servlet.http.*;

/**
 * State information per request, easily passed within proxy service.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: ProxyRunData.java,v 1.2 2004/02/23 03:46:26 jford Exp $ 
 */

public class ProxyRunData
{
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpServlet servlet;
    private boolean posting = false;

    /*
     * Construct a rundata object. Only lives per one request.
     *
     * @param servlet The HTTP Servlet object that contains the proxy server.
     * @param request The HTTP Request.
     * @param response The HTTP Response.
     * @param posting If true it means we are in a HTTP POST, otherwise HTTP GET.
     */
    ProxyRunData( HttpServlet servlet,
                  HttpServletRequest request,
                  HttpServletResponse response,
                  boolean posting)
    {
        this.servlet = servlet;
        this.request = request;
        this.response = response;
        this.posting = posting;
    }

    /*
     * Gets the HttpServlet for this request.
     *
     * @return The HttpServlet object for this request.
     *
     */
    public HttpServlet getServlet()
    {
        return servlet;
    }

    /*
     * Gets the HttpServletRequest for this request.
     *
     * @return The HttpServletRequest object for this request.
     *
     */
    public HttpServletRequest getRequest()
    {
        return request;
    }

    /*
     * Gets the HttpServletResponse for this request.
     *
     * @return The HttpServletResponse object for this request.
     *
     */
    public HttpServletResponse getResponse()
    {
        return response;
    }

    /*
     * Gets the state of this request, either a HTTP post or get.
     *
     * @return The state of the request, true for post, false for get.
     *
     */
    public boolean getPosting()
    {
        return posting;
    }

}


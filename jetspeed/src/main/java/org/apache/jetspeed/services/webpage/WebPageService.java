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

// javax.servlet
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;

// java.io
import java.io.IOException;

// java.util
import java.util.Collection;


/**
 * The WebPage Service service interface definition.
 * All implementations of an WebPage Service must implement this interface.
 *
 * Interface defines methods to control the WebPage service, 
 * to manage 0..n sessions amongst 0..n sites.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: WebPageService.java,v 1.2 2004/02/23 03:46:26 jford Exp $
 */

interface WebPageService 
{

    /** The name of this service */
    public final static String SERVICE_NAME      = "WebPageService";

    /**
     * The primary method invoked when the a proxied GET is executed.
     *
     * @param servlet the Servlet.          
     * @param req Servlet request.
     * @param res Servlet response.
     * @exception IOException a servlet exception.
     * @exception ServletException a servlet exception.
     */
    
    public void get(HttpServlet servlet,
                    HttpServletRequest request, 
                    HttpServletResponse response)
        throws ServletException, IOException;


    /**
     * The primary method invoked when the a proxied POST is executed.
     *
     * @param servlet the Servlet.     
     * @param req Servlet request.
     * @param res Servlet response.
     * @exception IOException a servlet exception.
     * @exception ServletException a servlet exception.
     */
    public void post(HttpServlet servlet,
                     HttpServletRequest request, 
                     HttpServletResponse response)
        throws ServletException, IOException;

    /**
     * One time initialization of the WebPage service
     *
     * @param config the servlet configuration.     
     * @exception IOException a servlet exception.
     * @exception ServletException a servlet exception.
     */
    public boolean init(ServletConfig config)
            throws ServletException, IOException;

    /**
     * One time de-initialization of the proxy service
     *
     */
    public void destroy();


    /**
     * Returns true if the service was initialized successfully.
     *
     * @return true if the service was initialized successfully.
     */
    public boolean isInit();

    /**
     * Returns a snapshot of all the active and inactive sessions.
     *
     * @return the collection of sessions.
     */
    public Collection getSessions();

    /**
     * Returns a Session, give a string id key identifying that session.
     *
     * @param id The ID of the session.
     * @return The corresponding sessions.
     */
    public SessionMap getSession(String id);

    /**
     * Returns a snapshot collection of all the sites managed by this Proxy service.
     *
     * @return the collection of managed sites.
     */
    public Collection getSites();


    /**
     * Returns the error string from failed initialized.
     *
     * @return the error string from last error.
     */
    public String getErrorString();
}


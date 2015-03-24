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
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

// java.io
import java.io.IOException;

// java.util
import java.util.Collection;

/**
 * <P>This is a commodity static accessor class around the
 * <code>WebPageService</code> interface</P>
 *
 * @see org.apache.jetspeed.services.webpage.WebPageService
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: WebPageManager.java,v 1.2 2004/02/23 03:46:26 jford Exp $
 */

public class WebPageManager
{

    // the singleton service reference
    private static WebPageService service = null;
   

    /** 
     * Commodity method for getting a reference to the service
     * singleton
     */
    private static WebPageService getService()
    {        
        if (service == null)
        {
            // TODO: load from configuration
            service = new JetspeedWebPageService();
        }
        return service;
    }
    
    /**
     * @see WebPageService#isInit
     */
    public static boolean isInit()
    {
       return getService().isInit();
    }

    /**
     * @see WebPageService#get
     */
    public static void get(HttpServlet servlet, 
                           HttpServletRequest request, 
                           HttpServletResponse response)
                           throws ServletException, IOException    
    {
       getService().get(servlet, request, response);
    }

    /**
     * @see WebPageService#post
     */
    public static void post(HttpServlet servlet, 
                            HttpServletRequest request, 
                            HttpServletResponse response)
        throws ServletException, IOException
    {
       getService().post(servlet, request, response);
    }


    /**
     * @see WebPageService#init
     */
    public static void init(ServletConfig config)
            throws ServletException, IOException
    {
        getService().init(config);
    }

    /**
     * @see WebPageService#destroy
     */
    public static void destroy()
    {
        getService().destroy();
    }

    /**
     * @see WebPageService#getSessions
     */
    public static Collection getSessions()
    {
        return getService().getSessions();
    }

    /**
     * @see WebPageService#getSession
     */
    public static SessionMap getSession(String id)
    {
        return getService().getSession(id);
    }

    /**
     * @see WebPageService#getNetworkElements
     */
    public static Collection getSites()
    {
        return getService().getSites();
    }

    /**
     * @see WebPageService#getErrorString
     */
    public static String getErrorString()
    {
        return getService().getErrorString();
    }

}

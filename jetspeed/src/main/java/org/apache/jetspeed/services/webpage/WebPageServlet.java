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
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;

// java.util
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.Logger;

/**
  *
  * WebPageServlet is the main servlet entry point for the WebPage Service server. 
  *
  */

public class WebPageServlet extends HttpServlet
{
    public static final String WPS_KILLSESSION = "kill";
    public static final String WPS_KILLPARAM   = "9";

    static Logger log = Logger.getLogger(WebPageServlet.class);
    
    /**
     * 
     * handles an HTTP GET request.
     *
     */
    public void doGet (HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException
    {        
        if (!WebPageManager.isInit())    
        {
            displayInfoPage(response);
            log.error(WebPageManager.getErrorString());
            return;
        }
     
        boolean error = false;
        try
        {        
            if (dispatch(request, response, true))
                return;  // request was handled by the WPS server
        }
        catch(Exception e)
        {
            //e.printStackTrace();
            log.error(e);           
            displayErrorPage(response, e.getMessage() );
            error = true;
        }

        // request wasn't handled by the WPS server (no parameters)
        // lets just display the info page

        if (false == error)
            displayInfoPage(response);
        
    }

    /**
     * 
     * handles an HTTP POST request.
     *
     */
    public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        if (!WebPageManager.isInit())    
        {
            displayInfoPage(response);
            log.error(WebPageManager.getErrorString());
            return;
        }
        
        boolean error = false;
        try
        {        
            if (dispatch(request, response, false))
                return;  // request was handled by the WebPageManager server
        }
        catch(Exception e)
        {
            //e.printStackTrace();
            log.error(e);           
            displayErrorPage(response, e.getMessage());
            error = true;
        }

        // request wasn't handled by the WebPageManager server (no parameters)
        // lets just display the info page

        if (false == error)
            displayInfoPage(response);
    }
        
    /**
     * 
     * Dispatches the HTTP GET or POST action to the WebPageManagerService.
     * Requests to the WebPageManager server are indicated in the query parameters.
     *
     * @return true if the request was intercepted by the WebPageManager server, otherwise false
     */
    private boolean dispatch(HttpServletRequest request, 
                                      HttpServletResponse response,
                                      boolean isGet)
                        throws ServletException, IOException
    {        
        Configuration config = Configuration.getInstance();

        String nesid  = request.getParameter(config.getSID());
        String pxurl  = request.getParameter(config.getURL());

        if (nesid == null && null == pxurl) 
            return false; // no interception specified

        // request is to the WebPageManager server...

        if (isGet) {
            WebPageManager.get(this, request, response);
        }
        else {
            WebPageManager.post(this, request, response);
        }


        return true; // did not find a WebPageManager action 
    }

    /**
     *  Display WebPageManager default info page.
     */
    private void displayInfoPage(HttpServletResponse response)                 
    {
        try
        {
            PrintWriter pw = response.getWriter ();
            String infoFile = Configuration.getInstance()
                          .getProperty(Configuration.KEY_CONTENT_INFO);
            String indexFile = getServletContext().getRealPath(infoFile);
            BufferedReader br = new BufferedReader(new FileReader(indexFile));
            
            String line;
            while ((line = br.readLine()) != null) 
            {
                pw.println(line);
            }
    
            if (WebPageManager.isInit())
                pw.println("<br><font color='green'>:: Status :: Online ::</font><br>" );
            else
            {
                pw.println("<br><font color='red'>:: Status :: Offline ::</font><br>" );             
                pw.println(":: Reason :: " + WebPageManager.getErrorString());
            }
            br.close();
        }
        catch (Exception ex)
        {
            log.error("Failed to read servlet info page");
            displayInfo(response);            
        }
    }

    /**
     *  Display WebPageManager default error page.
     */
    private void displayErrorPage(HttpServletResponse response, String msg)                 
    {
        try
        {
            PrintWriter pw = response.getWriter ();
    
            String fileName = Configuration.getInstance()
                          .getProperty(Configuration.KEY_CONTENT_ERROR);
            String template = getServletContext().getRealPath(fileName);
    
            BufferedReader br = new BufferedReader(new FileReader(template));

            String line;

            while ((line = br.readLine()) != null) 
            {
                int index = line.indexOf("$msg");
                if (index > -1) {
                    StringBuffer buffer = new StringBuffer(line);
                    WebPageHelper.replaceAll(buffer, "$msg", msg);
                    pw.println(buffer.toString());
                }
                else
                    pw.println(line);
            }
            br.close();                         
        }
        catch (Exception ex)
        {
            log.error("Failed to read servlet info page");
            displayError(response, msg);        
        }
    }

    /**
     * In certain situations the init() method is called more than once,
     * somtimes even concurrently. This causes bad things to happen,
     * so we use this flag to prevent it.
     */
    private static boolean firstInit = true;    

    /**
     * This init method will load the default resources from a
     * properties file.
     *
     * @param config typical Servlet initialization parameter.
     * @exception ServletException a servlet exception.
     */
    public final void init(ServletConfig config)
        throws ServletException
    {
        super.init(config);

        org.apache.log4j.PropertyConfigurator.configure("WebPageManagerLog4j.properties");

        synchronized ( this.getClass() )
        {
            if (!firstInit)
            {
                return;
            }
            firstInit = false;

            try 
            {
                WebPageManager.init(config);            
            }
            catch (IOException ex) 
            {
                throw new ServletException(ex.toString());
            }
        }
    }

    /**
     * Called by the servlet container to indicate to a servlet that the servlet 
     * is being taken out of service. The WebPageManager server cleans up all connections,
     * logging out of sessions.
     *
     */
    public final void destroy()
    {
        WebPageManager.destroy();
    }


    /*
     * Used as a failsafe in case the default info page can't be found.
     *
     */
    private void displayInfo(HttpServletResponse response)
    {
        try
        {
            PrintWriter pw = response.getWriter();
            pw.println("<HTML><HEAD><title>Jetspeed Web Page Servlet</title></HEAD><BODY><H1>Jetspeed Web Page Servlet</H1>");
                
                if (WebPageManager.isInit())
                    pw.println("<br><font color='green'>:: Status :: Online ::</font><br>" );
                else
                {
                    pw.println("<br><font color='red'>:: Status :: Offline ::</font><br>" );             
                    pw.println(":: Reason :: " + WebPageManager.getErrorString());
                }
            pw.println("<br><br>Warning. The Jetspeed HTTP Web Page Servlet was not correctly installed.<br>");
            pw.println("<br>Please contact Al Gore for support (he invented the internet).<br>");
            pw.println("</BODY></HTML>");
        }
        catch (IOException ex)
        {
            log.error("Failed to get a PrintWriter on response.");
        }
    }

    /*
     * Used as a failsafe in case the default error page can't be found.
     *
     */
    private void displayError(HttpServletResponse response, String msg)
    {
        try
        {
            PrintWriter pw = response.getWriter();
            pw.println("<HTML><HEAD><title>Web Page Servlet General Exception</title></HEAD><BODY><H1>Web Page Servlet General Exception</H1>");
                
                if (WebPageManager.isInit())
                    pw.println("<br><font color='green'>:: Status :: Online ::</font><br>" );
                else
                {
                    pw.println("<br><font color='red'>:: Status :: Offline ::</font><br>" );             
                    pw.println(":: Reason :: " + msg);
                }
            pw.println("<br><br>Warning. The Jetspeed Web Page Servlet was not correctly installed.<br>");
            pw.println("<br>Please contact Al Gore for support (he invented the internet).<br>");
            pw.println("</BODY></HTML>");
        }
        catch (IOException ex)
        {
            log.error("Failed to get a PrintWriter on response.");
        }
    }

}


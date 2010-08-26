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

// java.util
import java.util.Properties;
import java.util.Collection;

// java.io
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;

// javax.servlet
import javax.servlet.http.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;

// velocity
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.servlet.VelocityServlet;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.ParseErrorException;


/**
  *
  * WebPageConsoleServlet is the main servlet entry point for the WebPage console. 
  *
  */

public class WebPageConsoleServlet extends VelocityServlet
{

    private String servletPath = null;
    private String proxyRoot = null;
    /*
     * Request Handler for a Velocity servlet. Includes the context.
     *
     * @param request Servlet request.
     * @param response Servlet response.
     * @context the velocity request context.
     * @exception IOException a servlet exception.
     * @exception ServletException a servlet exception.
     *     
     * @return The newly merged template.
     */
    public Template handleRequest( HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   Context ctx )
    {
        if (servletPath == null)
        {
            servletPath = request.getServletPath();
            proxyRoot = getProxyHost(request);
        }
        
        boolean online = WebPageManager.isInit();
        if (online == false)
        {
            bootStrapProxy(request, response); // forward
        }

        String action = request.getParameter("action");
        String template;
        
        if (action == null)
            template = sessions(ctx);
        else if (action.equals("details"))
            template = elementSessions(ctx, request);
        else if (action.equals("test"))
            template = test(ctx, request);
        else if (action.equals("kill"))
            template = kill(ctx, request);
        else if (action.equals("killne"))
            template = killne(ctx);
        else if (action.equals("clear"))
            template = clear(ctx);
        else
            template = sessions(ctx);
                
        
        Template outty = null;
        
        try
        {
            outty =  getTemplate(template);
        }
        catch( ParseErrorException pee )
        {
            System.out.println("WebPageConsoleServlet : parse error for template " + pee);
        }
        catch( ResourceNotFoundException rnfe )
        {
            System.out.println("WebPageConsoleServlet : template not found " + rnfe);
        }
        catch( Exception e )
        {
            System.out.println("Error " + e);
        }
        return outty;

    }

    /**  
     *  Set the log file to be off of the webapp root, and
     *  will do the same with the file loader paths
     *
     * @param config The Servlet configuration.
     * @return The Properties collection of Velocity properties.
     * @throws exceptions when failed to read the properties or log files
     */        
    protected Properties loadConfiguration(ServletConfig config)
    throws IOException, FileNotFoundException
    {
        /*
         *  get our properties file and load it
         */

        String propsFile = config.getInitParameter(INIT_PROPS_KEY);

        Properties p = new Properties();

        if ( propsFile != null )
        {
            String realPath = getServletContext().getRealPath(propsFile);

            if ( realPath != null )
            {
                propsFile = realPath;
            }

            p.load( new FileInputStream(propsFile) );
        }

        /*
         *  first, normalize our velocity log file to be in the 
         *  webapp
         */

        String log = p.getProperty( Velocity.RUNTIME_LOG);

        if (log != null )
        {
            log = getServletContext().getRealPath( log );

            if (log != null)
            {
                p.setProperty( Velocity.RUNTIME_LOG, log );
            }
        }


        /*
         *  now, if there is a file loader resource path, treat it the
         *  same way.
         */

        String path = p.getProperty( Velocity.FILE_RESOURCE_LOADER_PATH );

        if ( path != null)
        {
            path = getServletContext().getRealPath(  path );

            if ( path != null)
            {
                p.setProperty( Velocity.FILE_RESOURCE_LOADER_PATH, path );
            }
        }

        return p;
    }  

    //////////////////////////////////////////////////////////////////////

    private String sessions(Context ctx)
    {
        // default Sessions screen

        Collection sessions = WebPageManager.getSessions();
        Collection targets = WebPageManager.getSites();
        boolean online = WebPageManager.isInit();

        ctx.put("sessions", sessions);
        ctx.put("targets", targets);  
        ctx.put("online", new Boolean(online));
        ctx.put("proxyError", WebPageManager.getErrorString());
        ctx.put("cmd", this);
        return "proxyConsole.vm";
    }

    //////////////////////////////////////////////////////////////////////

    private String elementSessions(Context ctx, HttpServletRequest request)
    {
        String sid = request.getParameter("id");

        if (sid == null)
        {
            return sessions(ctx);
        }
        // long id = Long.valueOf(sid).longValue();
        SessionMap map = WebPageManager.getSession(sid);
        if (map == null)
        {
            return sessions(ctx);
        }

        // default Sessions screen
        boolean online = WebPageManager.isInit();
    
        //Collection sessions = WebPageManager.getSessions();    
        //ctx.put("xxx_sessions", sessions );

        ctx.put("online", new Boolean(online));
        ctx.put("proxyError", WebPageManager.getErrorString());
        ctx.put("cmd", this);
        ctx.put("ne_sessions", map);

        return "neConsole.vm";  
    }
            
    //////////////////////////////////////////////////////////////////////

    private String kill(Context ctx, HttpServletRequest request)
    {
        // default Sessions screen
        boolean online = WebPageManager.isInit();
    
        Collection sessions = WebPageManager.getSessions();
        Collection elements = WebPageManager.getSites();
    
        ctx.put("xxx_sessions", sessions);
        ctx.put("elements", elements);  
        ctx.put("online", new Boolean(online));
        ctx.put("proxyError", WebPageManager.getErrorString());
        ctx.put("cmd", this);
        
        return "proxyConsole.vm";
    }

    //////////////////////////////////////////////////////////////////////

    private String killne(Context ctx)
    {
        return "proxyConsole.vm";
    }

    //////////////////////////////////////////////////////////////////////

    private String clear(Context ctx)
    {
        return "proxyConsole.vm";
    }

    //////////////////////////////////////////////////////////////////////
            
    private String test(Context ctx, HttpServletRequest request)
    {
        String id = request.getParameter("id");
        if (id == null)
        {
            return sessions(ctx);
        }
        SessionMap map = WebPageManager.getSession(id);
        if (map == null)
        {
            return sessions(ctx);
        }
        String ipa = request.getParameter("ipa");
        if (ipa == null)
        {
            return sessions(ctx);
        }
        SiteSession session = (SiteSession)map.get(ipa);
        if (session == null)
        {
            return sessions(ctx);
        }

        // default Sessions screen
        boolean online = WebPageManager.isInit();
    
        //Collection sessions = WebPageManager.getSessions();    
        //ctx.put("xxx_sessions", sessions );

        ctx.put("online", new Boolean(online));
        ctx.put("proxyError", WebPageManager.getErrorString());
        ctx.put("cmd", this);
        ctx.put("ne_sessions", map);
        //ctx.put("element", session.;
        ctx.put("nes", session);

        return "testConsole.vm";
    }

    //////////////////////////////////////////////////////////////////////
                  
    public String getRefresh()
    {
        return servletPath + "?action=refresh";
    }

    public String getClear()
    {
        return servletPath + "?action=clear";
    }

    public String getDetails()
    {
        return servletPath + "?action=details";
    }

    public String getTest()
    {
        return servletPath + "?action=test";
    }

    public String getKill()
    {
        return servletPath + "?action=kill";
    }

    public String getKillne()
    {
        return servletPath + "?action=killne";
    }

    public String getLogon()
    {
        String proxy = proxyRoot;
        Configuration config = Configuration.getInstance();
        proxy = proxy.concat("?logon-test=true&" + config.getSID() + "=");
        return proxy;
    }

    public String getProxyRoot()
    {
        return proxyRoot;
    }

    public String getWebapp()
    {
        Configuration config = Configuration.getInstance();
        return config.getWebapp();
        //return ProxyUtil.concatURLs(proxyRoot, config.getWebapp());
    }

    ////////////////////////////////////////////////////////////////////

    private String getProxyHost(HttpServletRequest request)
    {    
        StringBuffer root = new StringBuffer();
        String scheme = request.getScheme();
        root.append(scheme);
        root.append( "://");
        int port = request.getServerPort();
        root.append(request.getServerName());  
    
        if ( (port > 0) &&
             ((scheme.equals("http") && port != 80) ||
              (scheme.equals("https") && port != 443)
             )
           )
        {
            root.append(":");
            root.append(port);
        }
        root.append( Configuration.WPS_SERVLET );
        return root.toString();
    }

    //////////////////////////////////////////////////////////////

    void bootStrapProxy(HttpServletRequest request, 
                        HttpServletResponse response)
    {

        ServletContext ctx = getServletContext();

        try
        {
            RequestDispatcher dispatcher 
                                = ctx.getRequestDispatcher(Configuration.WPS_SERVLET);
            
            dispatcher.forward(request, response);
        } 
        catch (Exception e) 
        {
        }
    }
}



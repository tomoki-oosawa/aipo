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

// java.util
import java.util.Collection;
import java.util.Iterator;

// javax.servlet
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// java.net
import java.net.URL;
import java.util.HashMap;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;

/**
 *
 * <p>This is the default implementation of the <code>WebPageService</code> interface.</p>
 *
 * <p>
 * It is a service that provides Web Page facade and delegation
 * services for clients to transparently access resources existing on web
 * pages from requests originating from the portal server.</p>
 *
 * <p>Since the WebPage service is giving the appearance of a single session to 
 * the client, the service needs to manage the synchronization of sessions,
 * including single-sign-on, and security authorization permissions between the
 * the portal server and one or more sites.</p> 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: JetspeedWebPageService.java,v 1.4 2004/02/23 03:46:26 jford Exp $ 
 */

public class JetspeedWebPageService
    implements WebPageService
{
    // the session keys used to store network element proxy sessions
    public final static String SESSION_MAP = "wps.SessionMap";
    public final static String URL_SESSION_MAP = "wps.URLSessionMap";

    public final static String INIT_PROPERTIES_PARAM = "properties";

    /**
     * service state
     *
     */

    // the name of the host that this server is running on
    private String host = null;       

    // cache of sites cached and managed by the Proxy.
    // objects are of type org.apache.jetspeed.services.httpProxy.Site
    private HashMap sites = new HashMap();

    // active sessions that the Proxy is working with
    // the objects are of type org.apache.jetspeed.services.httpProxy.SessionMap
    // this cache is updated on servlet unbound events
    private HashMap sessions = new HashMap();

    // has this service been initialized yet
    private boolean init = false;

    // the log file singleton instance
    static Logger log = Logger.getLogger(JetspeedWebPageService.class);

    // last error
    private String lastError = "Jetspeed WebPage Service has not been initialized.";

    /**
     * The primary method invoked when the a Jetspeed GET is executed.
     *
     * @param servlet the Servlet.     
     * @param request Servlet request.
     * @param response Servlet response.
     * @exception IOException a servlet exception.
     * @exception ServletException a servlet exception.
     */
    public void get(HttpServlet servlet,
                    HttpServletRequest request, 
                    HttpServletResponse response)                         
    throws ServletException, IOException
    {
        ProxyRunData rundata = new ProxyRunData(servlet, request, response, false);
        dispatch(rundata);
    }

    /**
     * The primary method invoked when the a Jetspeed POST is executed.
     *
     * @param servlet the Servlet.          
     * @param request Servlet request.
     * @param response Servlet response.
     * @exception IOException a servlet exception.
     * @exception ServletException a servlet exception.
     */    
    public void post(HttpServlet servlet,
                     HttpServletRequest request, 
                     HttpServletResponse response)
    throws ServletException, IOException
    {        
        ProxyRunData rundata = new ProxyRunData(servlet, request, response, true);
        dispatch(rundata);
    }


    /**
     * The common dispatcher for both GETs and PUTs
     *
     * @param data the request specific state.
     * @exception IOException a servlet exception.
     * @exception ServletException a servlet exception.
     */    
    private void dispatch(ProxyRunData data)
    throws ServletException, IOException
    {        
        // Turn this on for Debugging
        //HttpProxyDebug.snoopParams(data.getRequest(), System.err);
        //HttpProxyDebug.snoopHeaders(data.getRequest(), System.err);        

        // get the proxy host for this server
        getHost(data.getRequest());

        //
        // get the full Network Element IP and Resource parameters from request
        //
        Configuration config = Configuration.getInstance();
        String sid = data.getRequest().getParameter(config.getSID());
        String url = data.getRequest().getParameter(config.getURL());

        if (null == sid)
        {
            if (null == url)
            {
                throw new ServletException("Bad Request. No proxy-parameters passed in request.");
            }
            proxyByURL(url, data);
            return;
        }

        // 
        // found the proxy query parameter denoting proxy-by-network-element-id
        //

        // maps a Site unique id to a Site object
        Site site = getSite(sid);
        if (null == site)
        {
            // logon failed, return error screen here:
            throw new ServletException("The Requested Site ID is currently not configured on this system: " + sid);
        }

        // 
        // check the status of the site
        // if it isn't online, exit out with exception
        //        
        if (site.getStatus() != Configuration.STATUS_ONLINE)
        {
            throw new ServletException("The Requested Site (" 
                                       + site.getURL()
                                       + ") is not available. Status = "
                                       + WebPageHelper.getAvailabilityStatus(site.getStatus()) );            
        }

        // 
        // get the path to the requested resource
        //
        String resource = getResourcePath(site.getURL(), data.getRequest());

        boolean loggedOn = true;


        // get the ession Map for this Portal Session
        // we get the session with 'false' since we don't want to create a new
        // session. The session should be already created 
        HttpSession session = data.getRequest().getSession(false);
        if (null == session)
        {
            session = data.getRequest().getSession(true);
        }

        // 
        // look up the session map from the current servlet session
        //
        String sessionID = session.getId();
        SessionMap smap = (SessionMap)sessions.get(sessionID);
        SiteSession jss = null;
        if (null == smap)
        {
            // get the user from the session
            /*
            User user = (User)session.getAttribute(config.getUserSessionKey());
            String username;
            if (user != null)
            {
                username = user.getUserName();
            }
            */
            // it wasn't found, create a new map 
            String username = "";
            smap = new SessionMap(sessionID, username);


            // add the map to the servlet session for callbacks on unbound
            session.setAttribute(SESSION_MAP, smap);
            // add the map to my collection of sessions
            sessions.put(sessionID, smap);

            // and create the network element session
            jss = new JetspeedSiteSession(site, this.host, username);

            // and then put the session into the network element map
            smap.put(site.getURL(), jss);

            // always logon when creating a new session
            loggedOn = jss.logon(data);

        } else
        {
            // found the session map, lets get the session
            jss = (JetspeedSiteSession)smap.get(site.getURL());
            if (null == jss)
            {
                // get the user from the session
                /*
                User user = (User)session.getAttribute(config.getUserSessionKey());
                String username;
                if (user != null) 
                {
                    username = user.getUserName();
                }
                */

                // no session exists, so create one
                String username = "";
                jss = new JetspeedSiteSession(site, this.host, username);
                smap.put(site.getURL(), jss);        

                // and then always logon when creating a new ne session 
                loggedOn = jss.logon(data);
            }
        }        
        if (loggedOn)
        {
            // debug TODO: remove this eventually
            if (data.getRequest().getParameter("logon-test") != null)
                return;

            if (WebPageCache.isCacheableResource(resource))
            {                
                if (WebPageCache.getResourceFromCache(resource, site.getID(), site.getURL(), this.host, data))
                {
                    smap.incCacheCount();
                    jss.incCacheCount();
                }
                else
                {
                    smap.incHitCount();
                    jss.incHitCount();
                }
                return;
            }

            smap.incHitCount();
            jss.incHitCount();
            jss.proxy(resource, data);        
        }
    }

    /**
     * Builds the proxy url which is used when rewriting other URLs
     * 
     *
     * @param req Servlet request.
     */
    private void getHost(HttpServletRequest request)
    {
        // TODO: try to get this to work!        
        // URL.setURLStreamHandlerFactory(new sun.net.www.protocol.http.handler( );

        if (null != this.host)
            return;

        StringBuffer root = new StringBuffer();
        String scheme = request.getScheme();
        root.append(scheme);
        root.append( "://");
        int port = request.getServerPort();

        String hostname = request.getServerName();
        String ip = WebPageHelper.getIP(hostname);
        if (null == ip)
            root.append(hostname);
        else
            root.append(ip);

        if ( (port > 0) &&
             ((scheme.equals("http") && port != 80) ||
              (scheme.equals("https") && port != 443)
             )
           )
        {
            root.append(":");
            root.append(port);
        }
        root.append( request.getServletPath() );
        this.host = root.toString();
    }

    /**
     * Given a Site id, maps to base URL for that site and returns the Site object
     * 
     *
     * @param sid the string Site ID
     * @return the Site object.
     */
    public Site getSite(String sid) throws ServletException
    {
        return(Site)sites.get(sid);
    }

    /**
     * Creates the full path the requested resource on the site from a relative path in request.
     *
     * @param url the base url for the site.
     * @param request the Servlet request.
     * @return the full path to the resource.
     */
    public String getResourcePath(String url, HttpServletRequest request)
    {
        String path = request.getParameter(Configuration.getInstance().getPath());           
        if (path == null)
            return "";

        String fullPath = WebPageHelper.concatURLs(url, path);

        return fullPath.replace('@', '&');
    }


    /**
     *  Given a URL, begin a Jetspeed session with that host
     *  It is here for future use.
     *
     * @param url the URL of the host to proxy.
     * @param data the runData
     * @return a new session
     */
    private SiteSession proxyByURL(String url,
                                      ProxyRunData data)
    throws ServletException, IOException
    {
        String newURL = url.replace('@', '&');
        String base = getTargetBase(newURL);

        // get the Session Map for this session
        // we get the session with 'false' since we don't want to create a new
        // session. The session should be already created 
        HttpSession session = data.getRequest().getSession(false);
        if (null == session)
        {
            session = data.getRequest().getSession(true);
        }

        String sessionID = session.getId();
        SessionMap smap = (SessionMap)sessions.get(sessionID);

        SiteSession pxSession = null;
        if (null == smap)
        {
            // create the map 
            smap = new SessionMap(sessionID, "NA"); // username not relevant....

            session.setAttribute(URL_SESSION_MAP, smap);
            sessions.put(sessionID, smap);

            Site site = new SecuredSite(base, base);
            pxSession = new JetspeedSiteSession(site, base, this.host);
            smap.put(base, pxSession);  // map(targetBaseHostName, Session)
        } 
        else
        {
            pxSession = (JetspeedSiteSession)smap.get(base);
            if (null == pxSession)
            {
                Site site = new SecuredSite(base, base);
                pxSession = new JetspeedSiteSession(site, base, this.host);
                smap.put(base, pxSession);  // map(targetBaseHostName, Session)
            }
        }

        if (WebPageCache.isCacheableResource(newURL))
        {

            if (WebPageCache.getResourceFromCache(newURL, -1, base, this.host, data))
            {
                smap.incCacheCount();
                pxSession.incCacheCount();
            }
            else
            {
                smap.incHitCount();
                pxSession.incHitCount();
            }
            return (JetspeedSiteSession)pxSession;
        }

        smap.incHitCount();
        pxSession.incHitCount();
        pxSession.proxy(newURL, data);
        return(JetspeedSiteSession)pxSession;       
    }

    /**
     * Maps a full URL path to a resource to a base path
     *   given: http://localhost:8080/jetspeed/search/index.html
     *   returns: http://localhost:8080/jetspeed/
     *
     * @param url the full URL of the resource.
     * @return the base host application string
     */
    public String getTargetBase(String url) throws ServletException
    {
        try
        {
            URL u = new URL(url);
            StringBuffer base = new StringBuffer();
            String protocol = u.getProtocol();
            base.append(protocol);
            base.append( "://");
            int port = u.getPort();
            base.append(u.getHost());  
            if ( (port > 0) &&
                 ((protocol.equals("http") && port != 80) ||
                  (protocol.equals("https") && port != 443)
                 )
               )
            {
                base.append(":");
                base.append(port);
            }

            // we need to separate the filename from the resource, since
            // URL.getPath() and .getFile() return the same string
            String path = u.getFile();

            if (null != path)
            {

                int dot = path.lastIndexOf('.');
                int slash = path.lastIndexOf('/');
                if (dot > slash && slash != -1)
                { // its a file
                    path = path.substring(0, slash);
                }
                // 

                base.append(path);

                if ('/' != base.charAt(base.length()-1))
                    base.append('/');
            } else
                base.append("/");

            return base.toString();
        } catch (MalformedURLException e)
        {
            throw new ServletException(e.toString());
        }
    }


    /**
     * One time initialization of the proxy service
     *
     * @param config the servlet configuration.     
     * @exception IOException a servlet exception.
     * @exception ServletException a servlet exception.
     */
    public boolean init(ServletConfig config)
    throws ServletException, IOException
    {

        String paramFile = config.getInitParameter(INIT_PROPERTIES_PARAM);
        if (null == paramFile)
        {
            lastError = "Jetspeed HTTP Proxy Init Property Not Found:" + INIT_PROPERTIES_PARAM;
            log.error(lastError);                                
            return false;
        }

        String fullPath = config.getServletContext().getRealPath(paramFile);

        Configuration pc = Configuration.getInitialInstance(fullPath);  
        if (null == pc)
        {
            return false;
        }

        lastError = "";
        init = true;
        return true;
    }

    /**
     * Returns true if the service was initialized successfully.
     *
     * @retun true if the service was initialized successfully.
     */
    public boolean isInit()
    {
        return init;
    }

    /**
     * One time de-initialization of the proxy service
     *
     */
    public void destroy()
    {
        try
        {
            //
            // first logout of all Network Element Sessions
            //
            Iterator it = sessions.values().iterator();
            while (it.hasNext())
            {
                SessionMap map = (SessionMap)it.next();
                Iterator itElements = map.values().iterator();
                while (itElements.hasNext())
                {
                    SiteSession has = (SiteSession)itElements.next();
                    try 
                    {
                        has.logout(null);
                    }
                    catch (Exception e)
                    {
                        // continue logging out even if one fails
                        log.error("Shutdown-Logout of Session: " + e);                                
                    }
                }
            }

        } catch ( Exception ex )
        {
            log.error( ex );
        }
    }

    /**
     * Returns a snapshot collection of all the active and inactive sessions.
     *
     * @return the collection of sessions.
     */
    public Collection getSessions()
    {
        return sessions.values();
    }

    /**
     * Returns a session, give a string id key identifying that session.
     *
     * @param id The ID of the session.
     * @return The corresponding session.
     */
    public SessionMap getSession(String id)
    {
        return (SessionMap)sessions.get(id);
    }

    /**
     * Returns a snapshot collection of all the managed sites in the system.
     *
     * @return the collection of sites.
     */
    public Collection getSites()
    {
        return sites.values();
    }


    /**
     * Returns the error string from failed initialized.
     *
     * @return the error string from last error.
     */
    public String getErrorString()
    {
        return lastError;
    }


}




    

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

import org.apache.log4j.Logger;

import org.apache.jetspeed.util.rewriter.HTMLRewriter;

/**
 * <p>Represents a session with a site</p>
 *  
 * <p>This class extends AbstractSiteSession, implementing
 *    the specific code login and logout to a site. The content rewriter
 *    is also specialized here to handle the specific needs </p>

 *
 * <p>Sessions are stored in the SessionMap per Jetspeed Session.
 *    (The SessionMap is stored in the Servlet Session)</p>
 *
 */

public class JetspeedSiteSession extends AbstractSiteSession
{    

    Site site;

    // the User Name owning this session
    String         userName;

    // the log file singleton instance
    static Logger log = Logger.getLogger(JetspeedSiteSession.class);

    /**
     * Create a session, which maintains sessions with one website.
     * 
     * @param site the site to manage.
     * @param targetBase the target host's base URL
     * @param proxyBase the proxy server host URL base address.
     */                
    public JetspeedSiteSession(Site site, 
                               String proxyBase,
                               String userName)                 
    {
        super(site.getURL(), proxyBase);
        this.site = site;        
        this.userName = userName;
    }

    /**
     * Logs on to the Secured site 'automatically', using a predefined 
     * exchange based on a logon-screen POST to the site,
     * sending the logon credentials and security permissions.
     *
     * @param data the request specific rundata.          
     *
     * @exception IOException a servlet exception.
     */    
    public boolean logon(ProxyRunData data)
                       throws IOException
    {
        return true;
    }


    /**
     * Reads stream from proxied host, runs HTML parser against that stream,
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
                               String url) throws IOException
    {   
        // Read the HTML Content 
        String content = getHTMLContent(con, data, url);   
        if (WebPageHelper.CT_HTML == contentType) 
        {
            // TODO: Deprecate this and use stream-based rewriting
            HTMLRewriter rewriter = new HTMLRewriter (); // site.getID(), url);
            // TODO: use Reader String result = rewriter.rewrite(content, proxyBase, targetBase);
            //data.getResponse().getWriter().write(result);
        }
        else
            data.getResponse().getWriter().write(content);    

    }

    /**
     * Retrieves the content from the URL Connection stream and writes it to servlet response
     *
     * @param con The URLConnection to read from.
     *
     * @exception IOException a servlet exception.
     */
    public void drainContent(URLConnection con,
                             HttpServletResponse response) throws IOException
    {
        // TODO: rewrite this, and deprecate all String based rewriting
    }

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
                    throws IOException
    {
        return ""; // todo: deprecate this
    }



    /**
     * Gets the network element object associated with this session.
     * 
     * @return A network element object for this session.
     */                
    public Site getSite()
    {
        return site;
    }

    /**
     * Gets the user name who owns this session.
     * 
     * @return The string value of the user name.
     */                
    public String getUserName()
    {
        return userName;
    }

    /**
     * Logs out to the Network Element  by sending the session id cookie
     * to a pre-defined logout-URL in the Network Element.
     * The logout-URL is defined in the proxy configuration.
     * We communicate with a specific MNE logout resource form via HTTP GET.
     *
     * @param data the request specific rundata.          
     *
     * @exception IOException a servlet exception.
     */    
    public boolean logout(ProxyRunData data)
                       throws IOException
    {
        return true; // LOGOUT
    }

    public void proxy(String site, ProxyRunData data)
                    throws IOException
    {
    }

}


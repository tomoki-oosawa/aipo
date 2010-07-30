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

package org.apache.jetspeed.portal.portlets;

//Element Construction Set
import org.apache.jetspeed.util.JetspeedClearElement;
import org.apache.ecs.ConcreteElement;

//Jetspeed stuff
import org.apache.jetspeed.portal.PortletConfig;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.util.HTMLRewriter;
import org.apache.jetspeed.util.Base64;

//turbine
import org.apache.turbine.util.RunData;

//standard java stuff
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

/**
 * A class that loads a web page and filters it to have certain features
 * deleted.
 *
 *@author <a href="mailto:rammer@sycom.at">Ingo Rammer</a>
 *@author <a href="mailto:sgala@apache.org">Santiago Gala</a>
 *@author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 *@author <a href="mailto:david@pssp.com">David G. Powers</a>
 */
public class WebPagePortlet extends AbstractInstancePortlet 
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(WebPagePortlet.class.getName());
    
    protected HTMLRewriter rewriter = null;
    protected boolean initDone = false;
    protected boolean contentStale = true;
    protected boolean cacheContent = false;
    protected String  username = null;
    protected String  password = null;
    
    /**
     * Initialize this portlet by defining a HTML rewriter.
     * @throws PortletException Initialization failed
     */    
    public void init() throws PortletException {
  
        if (initDone) // Why is init called more than once per portlet?
            return;

        PortletConfig config = this.getPortletConfig();
        
        try 
        {
            //FIXME: HTMLRewriter should take a Reader, and work
            rewriter = new HTMLRewriter(
                    ! config.getInitParameter("dont_remove_script","no")
                        .equalsIgnoreCase("yes"),
                    ! config.getInitParameter("dont_remove_style","no")
                        .equalsIgnoreCase("yes"),
                    ! config.getInitParameter("dont_remove_noscript","no")
                        .equalsIgnoreCase("yes"),
                    ! config.getInitParameter("dont_remove_meta","no")
                        .equalsIgnoreCase("yes"),
                    ! config.getInitParameter("dont_remove_applet","no")
                        .equalsIgnoreCase("yes"),
                    ! config.getInitParameter("dont_remove_object","no")
                        .equalsIgnoreCase("yes"),
                    ! config.getInitParameter("dont_remove_head","no")
                        .equalsIgnoreCase("yes"),
                    ! config.getInitParameter("dont_remove_onsomething","no")
                        .equalsIgnoreCase("yes"),
                    config.getInitParameter("open_in_popup","no")
                        .equalsIgnoreCase("yes")
                        );

            // fetch username and password for HTTP Basic Autentication
            username = config.getInitParameter("username");
            password = config.getInitParameter("password");
            
            contentStale = true;
            initDone = true;
        } catch (Exception e) {
            logger.info("Exception occurred:" + e.toString());
            e.printStackTrace();
            throw new PortletException( e.toString() );
        }
    }
    
    /**
     * took this from FileServerPortlet as it was private 
     *
    */
    // FIXME: Currently only the expiration the HTTP Reponse header is honored. 
    //        Expiration information in <meta> tags are not honored 
    protected Reader getReader(String url) throws IOException 
    {
        URL            pageUrl = new URL(url);

        URLConnection  pageConn = pageUrl.openConnection();
        try
        {
            // set HTTP Basic Authetication header if username and password are set
            if (username != null && password !=null)
            {
                pageConn.setRequestProperty("Authorization", "Basic " +
                                        Base64.encodeAsString(username + ":" + password));
            }
                
        }
        catch (Exception e)
        {
            logger.info("Exception occurred:" + e.toString(), e);
        }
        
        long           pageExpiration = pageConn.getExpiration();
        String         encoding = "iso-8859-1";
        String         contentType = pageConn.getContentType();
        String         tempString = null;
        String         noCache = "no-cache";
        
        if (contentType != null)
        {
            StringTokenizer st = new StringTokenizer(contentType, "; =");
            while (st.hasMoreTokens())
            {
                if (st.nextToken().equalsIgnoreCase("charset"))
                {
                    try
                    {
                        encoding = st.nextToken();
                        break;
                    }
                    catch (Exception e)
                    {
                        break;
                    }
                }
            }
        }

        /*
         * Determing if content should be cached.
         */
        cacheContent = true; // Assume content is cached
        if (pageExpiration == 0) {
            cacheContent = false;
        }
        // Check header field CacheControl
        tempString = pageConn.getHeaderField( "Cache-Control");
        if (tempString != null) {
            if (tempString.toLowerCase().indexOf(noCache) >= 0) {
                cacheContent = false;
            }
        }
        // Check header field Pragma
        tempString = pageConn.getHeaderField( "Pragma");
        if (tempString != null) {
            if (tempString.toLowerCase().indexOf(noCache) >= 0) {
                cacheContent = false;
            }
        }
            
        // Assign a reader
        Reader rdr = new InputStreamReader(pageConn.getInputStream(),
                                           encoding );

        // Only set the page expiration it the page has not expired
        if (pageExpiration > System.currentTimeMillis() && (cacheContent == true))
        {
            contentStale = false;
            logger.debug( "WebPagePortlet caching URL: " + 
                       url + 
                       " Expiration: " + 
                       pageExpiration +
                       ", " +
                       (pageExpiration - System.currentTimeMillis() ) +
                       " milliseconds into the future" );
            setExpirationMillis(pageExpiration);
        } else {
            contentStale = true;
        }

        return rdr;
    }


    /**
    This methods outputs the content of the portlet for a given 
    request.

    @param data the RunData object for the request
    @return the content to be displayed to the user-agent
    */
    public ConcreteElement getContent( RunData data ) 
    {
        PortletConfig config = this.getPortletConfig();
        
        if (contentStale == true)
            return getWebPageContent(data, config);
        
        if (null == getExpirationMillis())
            return getContent( data, null, true);
        
        if (getExpirationMillis().longValue() <= System.currentTimeMillis())
            return getWebPageContent(data, config);

        return getContent( data, null , true );
    }

    private ConcreteElement getWebPageContent( RunData data, PortletConfig config )
    {    
        
        String convertedString = null;  // parsed and re-written HTML
        JetspeedClearElement element = null;

        String url = selectUrl( data, config );

        try 
        {
            Reader htmlReader = getReader( url );
            //FIXME: HTMLRewriter should take a Reader, and work
            convertedString = rewriter.convertURLs(htmlReader, url);
            element = new JetspeedClearElement(convertedString);

            //FIXME: We should do a clearContent() for the media type, not ALL media types
            this.clearContent();  // doing this because setContent() is not overwriting current content.
            this.setContent(element);

            htmlReader.close();

        } catch (Exception e) {
            logger.info("Exception occurred:" + e.toString(), e);
        }        

        return element;
    }
    
    /**
     * Usually called by caching system when portlet is marked as expired, but
     * has not be idle longer then TimeToLive.
     *
     * Any cached content that is expired need to be refreshed.
     */
    public void refresh() {
        if (cacheContent == true) {
          getWebPageContent(null, this.getPortletConfig());
        }
    }

    /**
    * Select the URL to use for this portlet.
    * @return The URL to use for this portlet
    */
    protected String selectUrl( RunData data, PortletConfig config )
    {
        String url = config.getURL();

        return url;

    }   // selectUrl

}

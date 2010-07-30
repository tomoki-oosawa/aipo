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

package org.apache.jetspeed.portal.portlets;

//Element Construction Set
import org.apache.ecs.ConcreteElement;

//Jetspeed stuff
import org.apache.jetspeed.portal.PortletConfig;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.util.JetspeedClearElement;
import org.apache.jetspeed.util.SimpleTransform;
import org.apache.jetspeed.cache.disk.JetspeedDiskCache;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;


//XML stuff
import org.xml.sax.SAXException;

//java stuff
import java.io.IOException;


/**
Provides a content publication system (like Slashdot).

@author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
@version $Id: JetspeedContent.java,v 1.26 2004/02/23 04:03:34 jford Exp $ 
*/
public class JetspeedContent extends FileWatchPortlet 
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JetspeedContent.class.getName());
    
    public static final String PROVIDER_NAME_KEY = "provider-name";

    /**
    The name of the JPC provider.
    */
    private String provider = "";

    /**
    The stylesheet for using with this provider
    */
    private String stylesheet = "";

    /**
    The url that was specified by the provider
    */
    private String url = null;
    
    /**
    Return the last time the provider's URL has been changed.
    */
    private long lastModified;
    
    
    /**
    Init this Portlet, set it's title, description, etc.
    */
    public void init() throws PortletException {

        PortletConfig config = this.getPortletConfig();
        
        provider = config.getInitParameter( PROVIDER_NAME_KEY );
        
        if ( provider == null ) {
            throw new PortletException( "You need to specify " + PROVIDER_NAME_KEY );
        }

        //NOTE:  There are no HARD keys here in JetspeedResources.  If you change
        //this format of this it will break at runtime.  CAREFULL!
        
        this.url = JetspeedResources.getString( "content.provider." + provider + ".url" );
        
        this.stylesheet = JetspeedResources.getString( "content.provider." + provider + ".stylesheet.url" );
        
        String title = JetspeedResources.getString( "content.provider." + provider + ".title" );

        String description = JetspeedResources.getString( "content.provider." + provider + ".description" );

        this.setTitle( title );
        this.setDescription( description );
       
        this.setContent( this.parse( url ) );
            
        //now make sure all the above 4 values are define within JetspeedResources
        if ( url == null ||
             stylesheet == null ||
             title == null ||
             description == null ) {
            throw new PortletException( "Not all properties defined in JetspeedResources.  See JetspeedResources.properties notes." );
        }
        
        this.getPortletConfig().setURL( url );
        
        //set the last modification date for this file so that if it is 
        //modified this portlet can be expired from the cache.
        
        try {
            this.lastModified = JetspeedDiskCache.getInstance()
                .getEntry( this.url ).getLastModified();
        } catch ( IOException e ) {
            logger.error("Exception",  e);
        }
        
    }
    
    /**
    Parse out the JCP URL and return it as a concrete element
    */
    private ConcreteElement parse( String url ) throws PortletException {
 
            
        //now get the url and stylesheet from the JetspeedDiskCache store...

        try {
        
        url = JetspeedDiskCache.getInstance()
            .getEntry( url ).getURL();

        this.stylesheet = JetspeedDiskCache.getInstance()
            .getEntry( this.stylesheet ).getURL();
        } catch (IOException e) {
            logger.error( "Couldn't transform content.", e );
            throw new PortletException( "Couldn't transform content.  Please see error log" );
        }

        
        try {
            
            return new JetspeedClearElement( SimpleTransform.transform( url, stylesheet ) );
            
        } catch (SAXException e) {
            logger.error( "Couldn't transform content.", e );
            throw new PortletException( "Couldn't transform content.  Please see error log" );
        }

       
    }
    
}

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


//standard java stuff
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;
import java.text.DateFormat;

//Element Construction Set
import org.apache.ecs.html.A;
import org.apache.ecs.html.B;
import org.apache.ecs.html.Table;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TR;
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.StringElement;

//Jetspeed stuff
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.services.PortletFactory;
import org.apache.jetspeed.cache.disk.JetspeedDiskCache;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;

//Turbine
import org.apache.turbine.util.RunData;

/**
<p>
A Portlet which displays info about other Portlets.  This really isn't mean't
to be used in any other place except the PortletServlet as it is just displays
information about another servlet.  If in the future this should be used
elsewhere it can as it is a 1st class Servlet.
</p>

<p>
Note:  I decided to leave this Portlet within the cache because it would be
expired if it isn't needed.
</p>

@author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
@version $Id: PortletInfoPortlet.java,v 1.46 2004/02/23 04:03:33 jford Exp $
*/
public class PortletInfoPortlet extends AbstractPortlet 
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(PortletInfoPortlet.class.getName());
    
    public static final String THIRDPARTY_PORTLETRENDERER_URL_KEY = "thirdparty.portletrenderer.url";
    public static final String THIRDPARTY_PORTLETRENDERER_CAPTION_KEY = "thirdparty.portletrenderer.caption";

    /**
    */
    public boolean getAllowEdit( RunData rundata ) {
        //NOTE:  it is important that this ALWAYS return false.  The
        //PortletInfoPortlet will try to work with itself and get confused.
        return false;
    }

    /**
    */
    public boolean getAllowMaximize( RunData rundata ) {
        //NOTE:  it is important that this ALWAYS return false.  The
        //PortletInfoPortlet will try to work with itself and get confused.
        return false;
    }


    /**
    */
    public ConcreteElement getContent( RunData rundata ) {

        String portletName = ((JetspeedRunData)rundata).getPortlet();

        DateFormat df = DateFormat.getDateTimeInstance();
        
        if ( portletName == null ) {
            String message = "Could not find given entry ";
            logger.error( message );
            return new StringElement( message );
        }

        Portlet portlet = null;
        try {
            portlet = PortletFactory.getPortlet( portletName, "0" );
        } catch (PortletException e) {
            logger.error("Exception",  e);
            return new StringElement( e.getMessage() );
        }

        Table t = new Table();
        
        t.addElement( this.getRow(  "Portlet name: " + portlet.getName() ) );

        String url = portlet.getPortletConfig().getURL();
        if ( url != null ) {
            t.addElement( this.getRow( "From URL: " + url ) );

            try { 
                long urlUpdated = JetspeedDiskCache.getInstance().getEntry( url ).getLastModified();
                t.addElement( this.getRow( "URL last updated:  " + df.format( new Date(urlUpdated) ) ) );
                long urlExpires = JetspeedDiskCache.getInstance().getEntry( url ).getExpirationTime();
                t.addElement( this.getRow( "URL expires:  " + df.format( new Date(urlExpires) ) ) );
            } catch ( IOException e ) {
                logger.error("Exception",  e);
            }
        } 

        t.addElement( this.getRow( "Portlet last updated:  " + df.format( new Date(portlet.getCreationTime()) ) ) );



        //BEGIN 3RD PARTY REPL


        t.addElement( new TR().addElement( new TD()
            .addElement( new B().addElement( "Actions:" ) ) ) );
            
        String internal = null;
        JetspeedLink jsLink = null;

        try
        {
            jsLink = JetspeedLinkFactory.getInstance(rundata);
            String mtype = rundata.getParameters().getString("mtype");
            if (mtype != null)
            {
                jsLink.setMediaType(mtype);
                jsLink.addQueryData("mtype", mtype);
            }
            String js_peid = rundata.getParameters().getString("js_peid");
            // FIX ME: If the portlet is viewed in Avantgo and then portlet info is restored, the portlet will
            // be maximized (similar to customizing certain portlet types. The desired effect would be to 
            // set the portlet's mode to normal.
            internal = jsLink.addPathInfo("js_peid", js_peid).setAction("controls.Maximize").toString();
        }
        catch (Exception e)
        {
            logger.error("Exception",  e);
        }
        JetspeedLinkFactory.putInstance(jsLink);

        StringBuffer external = new StringBuffer( getPortletConfig().getInitParameter( THIRDPARTY_PORTLETRENDERER_URL_KEY ) );

        //this is the parameters of what so specify to the 3rd party provider
        external.append("&title=" + URLEncoder.encode( portlet.getTitle() ) );
        external.append("&url=" + URLEncoder.encode(internal));

        String message = getPortletConfig().getInitParameter( THIRDPARTY_PORTLETRENDERER_CAPTION_KEY );

        t.addElement( new TR()
            .addElement( new TD()
                .addElement( new A( external.toString() ).setTarget("_blank").addElement( message ) ) ) );

        //END 3RD PARTY REPL

            
        // BEGIN MIME TYPE SUPPORT
        /* RL: Temporarily disable mime support while changing registry
         t.addElement( new TR().addElement( new TD()
            .addElement( new B().addElement( "Mime Types:" ) ) ) );

        MimeType[] mts = portlet.getPortletConfig().getCapabilityMap().getMimeTypes();
            
        for ( int i = 0; i < mts.length; ++i ) {
                
            t.addElement( new TR()
                .addElement( new TD( mts[i].toString() ) ) );
                
        }
         */      

        //END MIME TYPE SUPPORT
            
        //BEGIN PROPERTIES SECTION
                                
        Iterator names= portlet.getPortletConfig().getInitParameterNames();

        if ( names.hasNext() ) {
            //OK... add the Properties from the Portet to this info set...
            t.addElement( new TR().addElement( new TD()
                .addElement( new B().addElement( "Properties:" ) ) ) );

        }
            
        while ( names.hasNext() ) {
                
            String name = (String)names.next();
            String value = (String)portlet.getPortletConfig().getInitParameter( name );

            t.addElement( new TR()
                .addElement( new TD( name + ":  " + value ) ) );
                
        }

        //END PROPERTIES SECTION
        return t;

    }

    /**
    Get a row for the output table
    */
    private ConcreteElement getRow( String message ) {
 
        return new TR()
                      .addElement( new TD()
                      .setNoWrap( true )
                      .addElement( message ) );
       
    }

}

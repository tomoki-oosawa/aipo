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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

//Element Construction Set
import org.apache.ecs.html.Comment;
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.ElementContainer;

//standard Jetspeed stuff
import org.apache.jetspeed.portal.PortletConfig;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

//turbine stuff
import org.apache.turbine.util.RunData;
import org.apache.turbine.modules.ScreenLoader;

/**
 This portlet will attempt to render the Turbine Screen within a portlet.
 Parameters are passed to the screen via portlet config information:
 <PRE>

 <entry type="abstract" name="TurbineScreen">
     <classname>portlets.TurbineScreenPortlet</classname>
 </entry>

 <entry type="ref" parent="TurbineScreen" name="TestPortal">
     <parameter name="display.screen" value="TurbineScreenName" />
     <parameter name="TurbineScreenName.param.MYPARAM" value="MYVALUE" />
     <metainfo>
          <title>Place title here</title>
          <description>Place description here</description>
     </metainfo>
 </entry>

 </PRE>

 In the above example, the parameter MYPARAM=MYVALUE will be set in the rundata that calls the screen.

@author <a href="mailto:ekkerbj@netscape.net">Jeff Breckke</a>
@version $Id: TurbineScreenPortlet.java,v 1.13 2004/02/23 04:03:34 jford Exp $
*/
public class TurbineScreenPortlet extends AbstractPortlet
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(TurbineScreenPortlet.class.getName());
    
    private String screen = null;
    private String bgcolor = null;
    private String classes = null;
    private Hashtable paramSet = null;

    static final public String BGCOLOR = "bgcolor";
    static final public String CLASSES = "classes";
    static final public String SCREEN = "display.screen";

    /**
    */
    public ConcreteElement getContent( RunData rundata )
    {
        ElementContainer content = new ElementContainer();
        try
        {
            rundata = setParams( rundata );
            content.addElement( new Comment( "BEGIN TurbineScreenPortlet" ) );
            content.addElement( ScreenLoader.getInstance().eval( rundata, screen ) );
            content.addElement( new Comment( "END TurbineScreenPortlet" ) );
        }
        catch ( Exception e )
        {
            String message = "TurbineScreenPortlet: " + e.getMessage();
            logger.error( message, e );
            content.addElement( message );
        }
        return ( content );
    }

    /**
    */
    public void init() throws PortletException
    {
        PortletConfig pc = getPortletConfig();
        ConcreteElement myContent = null;
        try
        {
            screen = ( String ) pc.getInitParameter( SCREEN );
            //if it is null here it should be in the parameters
            if ( screen == null )
            {
                throw new IllegalArgumentException("Missing screen parameter");
            }

            //if it is still null something is wrong
            if ( screen == null )
            {
                throw new PortletException( "You need to specify a " + SCREEN + " parameter for this portlet" );
            }

            /* Save the parameters if any */
            String param = null;
            String value = null;
            java.util.Map dict = pc.getInitParameters();
            Iterator en = dict.keySet().iterator();
            int index = -1;
            String tParam = screen + ".param";
            String newParam = null;
            paramSet = new Hashtable();
            while ( en.hasNext() )
            {
                param = ( String ) en.next();
                index = param.indexOf( tParam );
                if ( index != -1 )
                {
                    value = ( String ) dict.get( param );
                    if ( value == null )
                    {
                        throw new PortletException( "Could not retrieve value for " + param );
                    }
                    newParam = param.substring( index + tParam.length() + 1 );
                    paramSet.put( newParam, value );
                }
            }

            bgcolor = this.getPortletConfig().getPortletSkin().getBackgroundColor();
            classes = "WEB-INF/classes";
        }
        catch ( Exception e )
        {
            String message = "TurbineScreenPortlet: " + e.getMessage();
            logger.error( message, e );
        }
    }

    /**
    */
    public boolean getAllowEdit( RunData rundata )
    {
        return false;
    }

    /**
    */
    public boolean getAllowMaximize( RunData rundata )
    {
        return true;
    }

    /**
    */
    private RunData setParams( RunData data )
    {
        data.getParameters().add( BGCOLOR, bgcolor );
        data.getParameters().add( CLASSES, classes );

        Enumeration en = paramSet.keys();
        String param = null;
        while ( en.hasMoreElements() )
        {
            param = ( String ) en.nextElement();
            data.getParameters().add( param, ( String ) paramSet.get( param ) );
        }
        return ( data );
    }
}

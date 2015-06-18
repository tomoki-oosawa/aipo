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

package org.apache.jetspeed.modules.layouts;

//jetspeed stuff
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.util.MimeType;

// Turbine
import org.apache.turbine.modules.Layout;
import org.apache.turbine.modules.NavigationLoader;
import org.apache.turbine.modules.ScreenLoader;
import org.apache.turbine.util.RunData;

// ECS Classes
import org.apache.ecs.*;
import org.apache.ecs.wml.*;
import org.apache.ecs.xml.*;

/**
 * Legacy jetspeed Layout used when handling ECS-only content.
 * You should not use it if you want to use Velocity or JSPs.
 *
 * @deprecated This layout is not used anymore in Jetspeed
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Lute</a>
 * @version $Id: JetspeedLayout.java,v 1.13 2004/02/23 02:59:30 jford Exp $
 */
public class JetspeedLayout extends Layout
{

    public static final String TOP =
        JetspeedResources.getString( JetspeedResources.NAVIGATIONS_TOP_KEY );

    public static final String BOTTOM =
        JetspeedResources.getString( JetspeedResources.NAVIGATIONS_BOTTOM_KEY );

    /**
        The Doctype of WML
    */
    public static final Doctype WML_DOCTYPE =
        new Doctype( "wml",
                     "PUBLIC",
                     "\"-//WAPFORUM//DTD WML 1.1//EN\"",
                     "\"http://www.wapforum.org/DTD/wml_1.1.xml\"" );


    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JetspeedLayout.class.getName());     
    
    /**
     * 
     */        
    public void doBuild( RunData data ) throws Exception
    {

        CapabilityMap cm = ((JetspeedRunData)data).getCapability();

        MimeType mt = cm.getPreferredType();
        data.setContentType( mt.getContentType() );
        data.setCharSet( mt.getCharSet() );

        if ( logger.isDebugEnabled() )
        {
            logger.debug( "Layout: Setting type to: " + mt );
        }

        if ( mt.equals( MimeType.WML ) ) {
            // we should output WML code, use raw output
            // don"t use navigations for WML

            ElementContainer ec = new ElementContainer();
            WML wml = new WML();
            ec.addElement( new PI().setVersion( 1.0 ) );
            ec.addElement( WML_DOCTYPE );
            ec.addElement( wml );
            wml.addElement( new Template().addElement( new Do( DoType.ACCEPT, "Back" ).addElement( new Prev() ) ) );

            // Now execute the Screen portion of the page
            ConcreteElement screen = ScreenLoader.getInstance().eval ( data, data.getScreen() );
            if (screen != null)
                wml.addElement( screen );

            // output everything on stdout
            try {
                ec.output( data.getOut() );
            } catch ( java.io.IOException e ) {
                logger.error("Exception",  e);
            } finally {
                ec = null;
            }

        } else {

            // Execute the Top Navigation portion for this Layout
            ConcreteElement topNav =
                NavigationLoader.getInstance().eval ( data, TOP );
            if ( topNav != null)
                data.getPage().getBody().addElement( topNav );

            // Now execute the Screen portion of the page
            ConcreteElement screen = ScreenLoader.getInstance().eval ( data, data.getScreen() );
            if (screen != null)
                data.getPage().getBody().addElement( screen );

            // The screen should have attempted to set a Title
            // for itself, otherwise, a default title is set
            data.getPage().getTitle()
                .addElement( data.getTitle() );

            // The screen should have attempted to set a Body bgcolor
            // for itself, otherwise, a default body bgcolor is set
            data.getPage().getBody()
                .setBgColor(HtmlColor.white);

            // Execute the Bottom Navigation portion for this Layout
            ConcreteElement bottomNav =
            NavigationLoader.getInstance().eval ( data, BOTTOM );
            if ( bottomNav != null)
                data.getPage().getBody().addElement( bottomNav );
        }
    }

}

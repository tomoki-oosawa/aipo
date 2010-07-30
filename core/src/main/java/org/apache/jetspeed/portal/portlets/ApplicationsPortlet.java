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
import org.apache.ecs.html.A;
import org.apache.ecs.html.Table;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TR;
import org.apache.ecs.ConcreteElement;

//Jetspeed stuff
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletConfig;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.portal.PortletURIManager;
import org.apache.jetspeed.services.PortletFactory;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.om.registry.PortletEntry;

//turbine
import org.apache.turbine.util.RunData;

//standard java stuff
import java.util.*;

/**
 * Handles enumerating Portlets that are also applications
 *
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @author <a href="mailto:sgala@hisitech.com">Santiago Gala</a>
 * @version $Id: ApplicationsPortlet.java,v 1.39 2004/02/23 04:03:34 jford Exp $
 */
public class ApplicationsPortlet extends AbstractPortlet 
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(ApplicationsPortlet.class.getName());    
    
    /* SGP: here we cache Applications */
    private Vector applications = new Vector();

    /**
    */
    public void init() throws PortletException
    {

        PortletConfig config = this.getPortletConfig();

        this.setTitle( "Applications" );
        this.setDescription( "A list of Applications that are installed within Jetspeed" );

        logger.info( "Jetspeed: initializing the ApplicationsPortlet: BEGIN " );

        Enumeration portlets = Registry.get( Registry.PORTLET ).getEntries();

        while ( portlets.hasMoreElements() ) {

            PortletEntry entry = (PortletEntry)portlets.nextElement();

            if ( entry.isApplication() )
            {
                applications.addElement(entry);
            }
        }

        logger.info( "Jetspeed: initializing the ApplicationsPortlet: END " );

    }

    /**
    */
    public ConcreteElement getContent( RunData data ) {

        Table table = new Table();
        Enumeration portlets = applications.elements();

        while ( portlets.hasMoreElements() ) {

            PortletEntry entry = (PortletEntry)portlets.nextElement();
            
            String url = PortletURIManager.getPortletMaxURI( entry, data ).toString();
            Portlet portlet = null;
            try {
                portlet = PortletFactory.getPortlet(entry.getName(), "0");
            } catch (PortletException e) {
                continue;
            }
            A anchor = new A( url ).addElement( portlet.getTitle() );
            table.addElement( new TR().addElement( new TD().addElement( anchor ) ) );
            table.addElement( new TR().addElement( new TD().addElement( portlet.getDescription() ) ) );
        }

        return table;

    }


    public boolean getAllowEdit( RunData rundata ) {
        return false;
    }

    public boolean getAllowMaximize( RunData rundata ) {
        return true;
    }


}

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

package org.apache.jetspeed.portal.controllers;

//jetspeed support
import org.apache.jetspeed.portal.BasePortletSetConstraints;
import org.apache.jetspeed.portal.PortletConfig;
import org.apache.jetspeed.portal.PortletController;
import org.apache.jetspeed.portal.PortletControllerConfig;
import org.apache.jetspeed.portal.PortletSet;

import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.om.registry.PortletControllerEntry;
import org.apache.jetspeed.om.registry.MediaTypeEntry;
import org.apache.jetspeed.util.MimeType;

//turbine support
import org.apache.turbine.util.RunData;

//ecs stuff
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.ElementContainer;

import java.util.Map;
import java.util.Iterator;

/**
 @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 @version $Id: AbstractPortletController.java,v 1.26 2004/02/23 03:25:06 jford Exp $
*/
public abstract class AbstractPortletController implements PortletController
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(AbstractPortletController.class.getName());    
    
    /**
     *  Default padding to be displayed between portlets
     */
    public int DEFAULT_PADDING = 3;

    private String width="100%";
    private PortletSet portlets = null;
    private PortletControllerConfig conf = null;


    /**
    Allows the user to override the default set of portlets...
    */
    public final void setConfig(PortletControllerConfig conf)
    {
        this.conf = conf;
    }


    /**
    */
    public final PortletControllerConfig getConfig()
    {
        return this.conf;
    }

    /**
    Allows the user to override the default set of portlets...
    */
    public final void setPortlets(PortletSet portlets)
    {
      this.portlets = portlets;
    }


    /**
    */
    public final PortletSet getPortlets()
    {
        return this.portlets;
    }

    /**
    */
    public String getWidth() {
        return this.width;
    }

    /**
    */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
    Returns the padding value between the displayed portlets
    */
    public int getPadding() {
        int padding = 0;

        try {
            PortletConfig conf = getPortlets().getPortletConfig();
            padding =  Integer.parseInt( conf.getSkin( "padding" , String.valueOf( DEFAULT_PADDING ) ) );
        } catch ( RuntimeException e ) {
            logger.error("Exception getting padding value", e);
            padding = DEFAULT_PADDING;
        }

        return padding;
    }

    /**
    Sets the padding space to be put between portlets
    */
    public void setPadding(int padding) {
        try {
            PortletConfig conf = getPortlets().getPortletConfig();
            conf.setSkin( "padding" , String.valueOf( padding ) );
        } catch ( RuntimeException e ) {
            logger.error("Exception setting padding value", e);
            // FIXME: What should we do if there's no portlets, config or skin defined ?
        }

    }

    /**
    Sets the padding space to be put between portlets
    */
    public void setPadding(String padding) {
        try {
            PortletConfig conf = getPortlets().getPortletConfig();
            conf.setSkin( "padding" , padding );
        } catch ( RuntimeException e ) {
            logger.error("Exception setting padding value", e);
            // FIXME: What should we do if there's no portlets, config or skin defined ?
        }
    }

    /**
    */
    public void init()
    {
        // no specific init
    }


    /**
    @see Portlet#supportsType
    */
    public boolean supportsType( MimeType mimeType )
    {
        // we now need to check that the control also supports the type...
        PortletControllerEntry entry =
                (PortletControllerEntry)Registry.getEntry(Registry.PORTLET_CONTROLLER,
                                                   getConfig().getName() );
        String baseType = mimeType.toString();

        if (entry!=null)
        {
            Iterator i = entry.listMediaTypes();

            while(i.hasNext())
            {
                String name = (String)i.next();
                MediaTypeEntry media = (MediaTypeEntry)Registry.getEntry(Registry.MEDIA_TYPE, name);

                if (media != null)
                {
                    if (baseType.equals(media.getMimeType()))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
    */
    public ConcreteElement getContent( RunData rundata )
    {

        CapabilityMap map = ((JetspeedRunData)rundata).getCapability();
        ConcreteElement content = null;

        if ( MimeType.WML.equals( map.getPreferredType() ) )
        {
            content = getWMLContent( portlets, rundata );
        }
        else if ( MimeType.HTML.equals( map.getPreferredType() ) )
        {
            content = getHTMLContent( portlets, rundata );
        }
        else
        {
            // we don't know how to handle this type, maybe a subclass knows
            content = getContent( portlets, rundata );
        }

        return content;
    }

    /**
    */
    protected ConcreteElement getContent( PortletSet set, RunData data )
    {
        return new ElementContainer();
    }

    /**
    */
    protected ConcreteElement getWMLContent( PortletSet set, RunData data )
    {
        return new ElementContainer();
    }

    /**
    */
    protected ConcreteElement getHTMLContent( PortletSet set, RunData data )
    {
        return new ElementContainer();
    }

    /**
     * Creates a constraint object based on an original map source.
     *
     * @param original the source for this constraint object
     * @return a new Constraints object appropriate for this controller
     */
    public PortletSet.Constraints getConstraints( Map original )
    {
        PortletSet.Constraints constraints = new BasePortletSetConstraints();
        if (original != null) constraints.putAll(original);
        return constraints;
    }

}

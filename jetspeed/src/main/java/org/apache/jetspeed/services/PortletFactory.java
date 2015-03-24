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

package org.apache.jetspeed.services;

//jetspeed stuff
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.services.portletfactory.PortletFactoryService;
import org.apache.turbine.services.TurbineServices;
 
/**
 * Static wrapper around the PortletFactoryService
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: PortletFactory.java,v 1.4 2004/02/23 04:00:57 jford Exp $
 */
public class PortletFactory
{

    /** 
     * Commodity method for getting a reference to the service
     * singleton
     */
    private static PortletFactoryService getService()
    {
        return (PortletFactoryService)TurbineServices
                .getInstance()
                .getService(PortletFactoryService.SERVICE_NAME);     
    }

    /**
     * Given a PSML Entry return an instanciated Portlet.
     *
     * @param entry a PSML Entry describing a portlet
     * @return an instanciated portlet corresponding to this entry
     */
    public static Portlet getPortlet( Entry entry ) throws PortletException
    {
        return getService().getPortlet( entry );
    }

    /**
     * Given a Portlet registry entry name, instanciate it
     *
     * @param name the name of a portlet in the registry
     * @return an instanciated portlet corresponding to this entry
     */
    public static Portlet getPortlet( String name, String id ) throws PortletException
    {
        return getService().getPortlet( name, id );
    }
}


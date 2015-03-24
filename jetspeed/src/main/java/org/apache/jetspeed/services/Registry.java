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

import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.om.registry.RegistryException;
import org.apache.jetspeed.services.registry.RegistryService;
import org.apache.turbine.services.TurbineServices;
import java.util.Enumeration;

/**
 * <P>This is a commodity static accessor class around the
 * <code>RegistryService</code></P>
 *
 * @see org.apache.jetspeed.services.registry.RegistryService
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: Registry.java,v 1.8 2004/02/23 04:00:57 jford Exp $
 */
public class Registry
{

    /** Default Portlet Registry name */
    public static String PORTLET = "Portlet";

    /** Default PortletControl Registry name */
    public static String PORTLET_CONTROL = "PortletControl";

    /** Default PortletController Registry name */
    public static String PORTLET_CONTROLLER = "PortletController";

    /** Default MediaType Registry name */
    public static String MEDIA_TYPE = "MediaType";

    /** Default Client Registry name */
    public static String CLIENT = "Client";

    /** Default Security Registry name */
    public static String SECURITY = "Security";

    /** Default Skin Registry name */
    public static String SKIN = "Skin";

    /**
     * Commodity method for getting a reference to the service
     * singleton
     */
    private static RegistryService getService()
    {
        return (RegistryService)TurbineServices
                .getInstance()
                .getService(RegistryService.SERVICE_NAME);
    }

    /**
     * @see RegistryService#getNames
     */
    public static Enumeration getNames()
    {
        return getService().getNames();
    }

    /**
     * @see RegistryService#get
     */
    public static org.apache.jetspeed.om.registry.Registry get( String regName )
    {
        return getService().get( regName );
    }

    /**
     * @see RegistryService#createEntry
     */
    public static RegistryEntry createEntry( String regName )
    {
        return getService().createEntry( regName );
	}

    /**
     * @see RegistryService#getEntry
     */
    public static RegistryEntry getEntry( String regName, String entryName )
    {
        return getService().getEntry( regName, entryName );
    }

    /**
     * @see RegistryService#addEntry
     */
    public static void addEntry( String regName, RegistryEntry value )
        throws RegistryException
    {
        getService().addEntry( regName, value );
    }

    /**
     * @see RegistryService#removeEntry
     */
    public static void removeEntry( String regName, String entryName )
    {
        getService().removeEntry( regName, entryName );
    }
}

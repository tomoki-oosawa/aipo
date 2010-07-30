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

package org.apache.jetspeed.services.persistence;

import java.util.List;

import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.portal.*;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletException;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;

/**
 * Static accessor to the Portal Persistence Service for storing and
 * retrieving portlet instances.
 *
 * @author <a href="mailto:sweaver@rippe.com">Scott Weaver</a>
 * @version $Id: PersistenceManager.java,v 1.3 2004/02/23 03:33:52 jford Exp $
 */
public abstract class PersistenceManager
{
    /**
     * Retrieves a List of portlet instances for the given profile.
     *
     * @param profile Retrieve instances from this profile.
     * @return List The list of all instances in current profile.
     */
    public static List getInstances(Profile profile) throws PortletException
    {
        return getService().getInstances(profile);
    }
        
    /**
     * Retrieves a List of portlet instances for the current profile.
     *
     * @param data Request rundata.
     * @return List The list of all instances in current profile.
     */
    public static List getInstances(RunData data) throws PortletException
    {
        return getService().getInstances(data);
    }

    /**
     * Retrieves a portlet instance from persistent storage for the given portlet.
     *
     * @param portlet The portlet to retrieve an instance for
     * @param data Request rundata.
     * @return PortletInstance The instance for the given portlet.
     */
    public static PortletInstance getInstance(Portlet portlet, Profile profile)
    {
        return getService().getInstance(portlet, profile);
    }

    /**
     * Retrieves a portlet instance from persistent storage for the given portlet.
     *
     * @param portlet The portlet to retrieve an instance for
     * @param data Request rundata.
     * @return PortletInstance The instance for the given portlet.
     */
    public static PortletInstance getInstance(Portlet portlet, RunData data)
    {
        return getService().getInstance(portlet, data);
    }

    /**
     * Store a portlet instance to permenant storage.
     *
     * @param instance The portlet instance to be stored.
     * @exception PortalPersistenceException if there were problems
     * storing the PSMLDocument to persistent storage.
     * 
     */
    public static void store(PortletInstance pPortlet) throws PortalPersistenceException
    {
        getService().store(pPortlet);
    }
        
    
    protected static PortalPersistenceService getService()
    {
        return (PortalPersistenceService) TurbineServices.getInstance().getService(
            "PortalPersistenceService");
    }

}
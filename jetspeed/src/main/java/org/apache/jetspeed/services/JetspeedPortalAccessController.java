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

// Jetspeed imports
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.services.security.PortalAccessController;
import org.apache.jetspeed.services.security.PortalResource;

// Turbine
import org.apache.turbine.services.TurbineServices;

/**
 * Static accessor for the PortalAccessController service
 *
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id: JetspeedPortalAccessController.java,v 1.3 2004/02/23 04:00:57 jford Exp $
 */
public abstract class JetspeedPortalAccessController
{
    
    /** Creates new JetspeedIdGenerator */
    public JetspeedPortalAccessController()
    {
    }
    
    /*
     * Utility method for accessing the service
     * implementation
     *
     * @return a UniqueIdService implementation instance
     */
    protected static PortalAccessController getService()
    {
        return (PortalAccessController)TurbineServices
        .getInstance().getService(PortalAccessController.SERVICE_NAME);
    }
    
    public static boolean checkPermission(JetspeedUser user, Entry entry, String action)
    {
        return getService().checkPermission(user, entry, action);
    }

    public static boolean checkPermission(JetspeedUser user, Portlet portlet, String action)
    {
        return getService().checkPermission(user, portlet, action);
    }

    public static boolean checkPermission(JetspeedUser user, PortalResource resource, String action)
    {
        return getService().checkPermission(user, resource, action);
    }
    
}

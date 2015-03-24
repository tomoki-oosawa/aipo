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

package org.apache.jetspeed.util;

import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Reference;
import org.apache.jetspeed.services.idgenerator.JetspeedIdGenerator;

// Jetspeed
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.services.security.PortalResource;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.JetspeedSecurity;

// Turbine stuff
import org.apache.turbine.util.RunData;

/**
 * This class provides static util methods for portlet manipulation that 
 * aren't part of the default services.
 *
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a> 
 * @version $Id: PortletUtils.java,v 1.4 2004/02/23 03:23:42 jford Exp $
 */
public class PortletUtils 
{
    /**
     * Regenerates jspeid for all portlets, entries and references
     *
     * @param profile Profile to process
     * @return Profile with portlet ids regenerated
     */
    public static void regenerateIds(Portlets topPortlets)
    throws Exception
    {
        // Display some portlets
        Portlets[] portlets = topPortlets.getPortletsArray();
        for (int i = 0; i < portlets.length; i++)
        {
            portlets[i].setId(JetspeedIdGenerator.getNextPeid());
            
            Entry[] entries = portlets[i].getEntriesArray();
            for (int j = 0; j < entries.length; j++)
            {
                entries[j].setId(JetspeedIdGenerator.getNextPeid());
            }

            Reference[] refs = portlets[i].getReferenceArray();
            for (int k = 0; k < refs.length; k++)
            {
                refs[k].setId(JetspeedIdGenerator.getNextPeid());
            }

            regenerateIds(portlets[i]);
        }
    }

    /**
     * Returns true if specific profile is accessible by the current user
     * 
     * @param data
     * @param profile
     * @return 
     */
    public static boolean canAccessProfile(RunData rundata, Profile profile)
    {
        boolean result = true;

        if (profile != null && profile.getRootSet() != null)
        {
            PortalResource portalResource = new PortalResource(profile.getRootSet());
            String owner = null;
            if (profile.getUserName() != null)
            {
                owner = profile.getUserName();
            }
            portalResource.setOwner(owner);

            result = JetspeedSecurity.checkPermission((JetspeedUser) rundata.getUser(), 
                                                      portalResource, 
                                                      JetspeedSecurity.PERMISSION_CUSTOMIZE);
        }

        return result;

    }

}

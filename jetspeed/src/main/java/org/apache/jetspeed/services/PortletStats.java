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

// jetspeed stuff
import org.apache.jetspeed.services.portletstats.PortletStatsService;
import org.apache.jetspeed.portal.Portlet;

// turbine stuff
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;


/**
 * <P>This is a commodity static accessor class around the 
 * <code>PortletStatsService</code></P>
 * 
 * @see org.apache.jetspeed.services.portletstats.PortletStatsService
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: PortletStats.java,v 1.4 2004/02/23 04:00:57 jford Exp $
 */
public class PortletStats {
 
    /**
     * Return code when access to portlet was successful
     */
    public static final String ACCESS_OK        = "200";

    /**
     * Return code when access to portlet was denied by the security manager
     */
    public static final String ACCESS_DENIED    = "401";

    /** 
     * Commodity method for getting a reference to the service
     * singleton
     */
    private static PortletStatsService getService() 
    {
        return (PortletStatsService)TurbineServices
                .getInstance()
                .getService(PortletStatsService.SERVICE_NAME);     
    }
    
    /**
     * @see org.apache.jetspeed.services.portletstats.PortletStatsService#isEnabled
     */
    public static boolean isEnabled() 
    {
        return getService().isEnabled();
    }

    /**
     * @see org.apache.jetspeed.services.portletstats.PortletStatsService#setEnabled
     */
    public static boolean setEnabled(boolean state) 
    {
        return getService().setEnabled(state);
    }

    /**
     * @see org.apache.jetspeed.services.portletstats.PortletStatsService#logAccess
     */
    public static void logAccess(RunData data, Portlet portlet, String statusCode) 
    {
        getService().logAccess(data, portlet, statusCode);
    }
    
    /**
     * @see org.apache.jetspeed.services.portletstats.PortletStatsService#logAccess
     */
    public static void logAccess(RunData data, Portlet portlet, String statusCode, long time) 
    {
        getService().logAccess(data, portlet, statusCode, time);
    }
}

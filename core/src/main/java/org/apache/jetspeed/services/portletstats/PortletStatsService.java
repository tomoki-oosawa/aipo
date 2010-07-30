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

package org.apache.jetspeed.services.portletstats;

// turbine stuff
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.Service;

//jetspeed stuff
import org.apache.jetspeed.portal.Portlet;
 
/**
 * This service is responsible for logging access to portlets.
 * 
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: PortletStatsService.java,v 1.3 2004/02/23 03:36:27 jford Exp $
 */
public interface PortletStatsService extends Service
{

    /** The default control to use when none is specified */
    public String SERVICE_NAME = "PortletStats";
            
    /**
     * Returns sevice enabled state
     * 
     * @return true if service is enabled
     */
    public boolean isEnabled();

    /**
     * Sets service enabled state
     * 
     * @param state  new state
     * @return original service enabled state
     */
    public boolean setEnabled(boolean state);

    /**
     * Logs portlet access using default load time.
     * 
     * @param data       Current request info object
     * @param portlet    Portlet being logged
     * @param statusCode HTTP status code. For now, either 200 (successfull) or 401 (unauthorized)
     */
    public void logAccess(RunData data, Portlet portlet, String statusCode);

    /**
     * Logs portlet access.
     * 
     * @param data       Current request info object
     * @param portlet    Portlet being logged
     * @param statusCode HTTP status code. For now, either 200 (successfull) or 401 (unauthorized)
     */
    public void logAccess(RunData data, Portlet portlet, String statusCode, long time);

}


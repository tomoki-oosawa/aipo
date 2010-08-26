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

package org.apache.jetspeed.util;

import org.apache.turbine.util.RunData;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.persistence.PersistenceManager;
import org.apache.jetspeed.portal.PortletInstance;

/**
 * Defines standard utility functions on session attributes
 *
 * @author <a href="mailto:david@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a> 
 * @version $Id: PortletSessionState.java,v 1.9 2004/02/23 03:23:42 jford Exp $
 */
public class PortletSessionState
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(PortletSessionState.class.getName());    

    /**
     * Portlet attribute to save config state.
     */
    public static final String CONFIG_CHANGED = "config_changed";

    /**
    Returns the parameter for this name from the Temp - session object
    */
    public static Object getAttribute(RunData rundata, String attrName)
    {
        return rundata.getUser().getTemp(attrName);
    }

    public static Object getAttribute(RunData rundata, String attrName, Object defValue)
    {
        Object o = rundata.getUser().getTemp(attrName, defValue);
        if (o instanceof java.lang.String && !(defValue instanceof java.lang.String))
        {
            return defValue;
        }
        return o;
    }

    /**
     * Sets the parameter for this name in Temp
     * Uses the portlet parameter to generate a unique key.
     */
    public static void setAttribute(RunData rundata, String attrName, Object attrValue)
    {
        rundata.getUser().setTemp(attrName, attrValue);
    }

    /**
     * Clears the parameter for this name from Temp
     * Uses the portlet parameter to generate a unique key.
     */
    public static void clearAttribute(RunData rundata, String attrName)
    {
        rundata.getUser().removeTemp(attrName);
    }
    
    /**
     * Returns the parameter for this name from the Temp - session object
     * Uses the portlet parameter to generate a unique key.
     */
    public static Object getAttribute(Portlet portlet, RunData rundata, String attrName)
    {
        return rundata.getUser().getTemp(generateKey(portlet, attrName));
    }

    /**
     * Returns the attribute for this name using the following search path:
     * <ul>
     * <li>request parameter</li>
     * <li>session attribute</li>
     * <li>instance attribute</li>     
     * <li>config parameter</li>
     * <ul>
     * Uses the portlet parameter to generate a unique key.
     * 
     * @param portlet
     * @param rundata
     * @param attrName
     * @return attribute value
     */
    public static Object getAttributeWithFallback(Portlet portlet, RunData rundata, String attrName)
    {
        Object result = null;

        // Look in the request first
        if(isMyRequest(rundata, portlet)) 
        {
            result =  rundata.getParameters().getString(attrName);
            if (result != null) 
            {
                if (result.toString().trim().equalsIgnoreCase("")) 
                {
                    clearAttribute(portlet, rundata, attrName);
                    result = null;
                } 
                else 
                {
                    setAttribute(portlet, rundata, attrName, result);
                }
            }
        }

        // Look in the session attributes
        if (result == null) 
        {
            result = getAttribute(portlet, rundata, attrName);
        }

        // Look in the instance attributes
        if (result == null)
        {
            result = portlet.getAttribute(attrName, null, rundata);
        }
        
        // Finally, look in the config
        if (result == null) 
        {
            result = portlet.getPortletConfig().getInitParameter(attrName);
        }

        return result;

    }

    /**
     * Returns true if the request pertains to current portlet instance. It assumes that the portlet interested in
     * recognizing its own requests, has a hidden input "js_peid". For backwards compatibility, if "js_peid" was
     * not set, this method will return TRUE.
     * 
     * @param rundata
     * @return boolean
     */
    public static boolean isMyRequest(RunData rundata, Portlet portlet) {

        // If the request does not contain "js_peid", assume that the portlet is not interested 
        // in isMyRequest functionality and return TRUE.
        String requestPeid = rundata.getParameters().getString("js_peid");
        if (requestPeid == null || requestPeid.equalsIgnoreCase(""))
        {
            return true;
        }

        // If the portlet does not have its id set, assume that the portlet is not interested
        // in isMyRequest functionality and return TRUE.
        if (portlet == null || portlet.getID() == null)
        {
            return true;
        }

        // Retrieve portlet instance
        String peId = null;
        PortletInstance instance = PersistenceManager.getInstance(portlet, rundata);
        if (instance != null)
        {
            peId = instance.getPortlet().getID();
        }

        // Compare the ids
        if (peId != null && peId.equals(requestPeid))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Sets the parameter for this name in Temp
     * Uses the portlet parameter to generate a unique key.
     */
    public static void setAttribute(Portlet portlet,
                             RunData rundata,
                             String attrName,
                             Object attrValue)
    {
        rundata.getUser().setTemp(generateKey(portlet, attrName), attrValue);
    }

    /**
     * Clears the parameter for this name from Temp
     * Uses the portlet parameter to generate a unique key.
     */
    public static void clearAttribute(Portlet portlet, RunData rundata, String attrName)
    {
        rundata.getUser().removeTemp(generateKey(portlet, attrName));
    }

    /**
     * Uses the portlet parameter to generate a unique key, using the portlet.getId.
     */
    protected static String generateKey(Portlet portlet, String name)
    {
        if (portlet != null)
        {
            return (portlet.getID()+"."+name);
        }
        else
        {
            logger.error("PortletSessionState: Passed null Velocity Portlet for name: " + name);
            return name;
        }
    }

    /**
     * Returns the parameter for this name from the Temp - session object
     * Uses the portlet parameter to generate a unique key.
     */
    public static Object getAttribute(VelocityPortlet portlet, RunData rundata, String attrName)
    {
        return getAttribute((Portlet) portlet, rundata, attrName);
    }

    /**
     * Sets the parameter for this name in Temp
     * Uses the portlet parameter to generate a unique key.
     */
    public static void setAttribute(VelocityPortlet portlet,
                             RunData rundata,
                             String attrName,
                             Object attrValue)
    {
        setAttribute((Portlet) portlet, rundata, attrName, attrValue);
    }

    /**
     * Clears the parameter for this name from Temp
     * Uses the portlet parameter to generate a unique key.
     */
    public static void clearAttribute(VelocityPortlet portlet, RunData rundata, String attrName)
    {
        clearAttribute((Portlet) portlet, rundata, attrName);
    }

    /**
     * The portlet config has changed. Calling getPortletConfigChanged returns the current state
     * and resets it.
     * 
     * @param portlet
     * @param rundata
     */
    public static void setPortletConfigChanged(Portlet portlet, RunData rundata)
    {
        setAttribute(portlet, rundata, CONFIG_CHANGED, "true");
    }

    /**
     * Returns current state of portlet config and resets it if set.
     * 
     * @param portlet
     * @param rundata
     * @return TRUE if portlet config has changed
     */
    public static boolean getPortletConfigChanged(Portlet portlet, RunData rundata)
    {
        String state = (String) getAttribute(portlet, rundata, CONFIG_CHANGED);
        if (state != null)
        {
            clearAttribute(portlet, rundata, CONFIG_CHANGED);
        }

        return state != null;
    }

}

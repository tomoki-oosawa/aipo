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
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.om.registry.Parameter;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletConfig;

/**
 * Defines standard utility functions for config parameters
 *
 * @author <a href="mailto:david@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a> 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: PortletConfigState.java,v 1.4 2004/02/23 03:23:42 jford Exp $
 */
public class PortletConfigState
{

    /**
    Returns the parameter for this name from the xreg
    */
    public static String getConfigParameter(Portlet portlet,
                                            String attrName,
                                            String attrDefaultValue)
    {
        PortletConfig pc = portlet.getPortletConfig();
        return pc.getInitParameter(attrName, attrDefaultValue);
    }

    /**
    Returns the parameter for this name from the psml
    */
    public static String getInstanceParameter(Portlet portlet,
                                              RunData rundata,
                                              String attrName)
    {
        return portlet.getAttribute( attrName, null, rundata);
    }

    /**
     * Sets the parameter in the psml
     */
    public static void setInstanceParameter(Portlet portlet,
                                     RunData rundata,
                                     String attrName,
                                     String attrValue)
    {
        portlet.setAttribute(attrName, attrValue, rundata);
    }

    public static void clearInstanceParameter(Portlet portlet,
                                       RunData rundata,
                                       String attrName)
    {
        if( portlet.getAttribute(attrName, null, rundata) != null )
            portlet.setAttribute(attrName, null, rundata);
    }

    /*
     * Gets the parameter using the fallback routine - first checks PSML,
     * in case it doesn't find it then it looks up the registry
     */
    public static String getParameter(Portlet portlet,
                               RunData rundata,
                               String attrName,
                               String attrDefValue)
    {
        String str = getInstanceParameter( portlet, rundata, attrName);
        if (str == null)
        {
            str = getConfigParameter(portlet, attrName, attrDefValue);
        }
        return str;
    }

    /**
    Returns the parameter for this name from the xreg
    */
    public static String getConfigParameter(VelocityPortlet portlet,
                                            String attrName,
                                            String attrDefaultValue)
    {
        return getConfigParameter((Portlet) portlet, attrName, attrDefaultValue);
    }

    /**
    Returns the parameter for this name from the psml
    */
    public static String getInstanceParameter(VelocityPortlet portlet,
                                              RunData rundata,
                                              String attrName)
    {
        return getInstanceParameter((Portlet) portlet, rundata, attrName);
    }

    /**
     * Sets the parameter in the psml
     */
    public static void setInstanceParameter(VelocityPortlet portlet,
                                     RunData rundata,
                                     String attrName,
                                     String attrValue)
    {
        setInstanceParameter((Portlet) portlet, rundata, attrName, attrValue);
    }

    public static void clearInstanceParameter(VelocityPortlet portlet,
                                       RunData rundata,
                                       String attrName)
    {
        clearInstanceParameter((Portlet) portlet,rundata,attrName);
    }

    /*
     * Gets the parameter using the fallback routine - first checks PSML,
     * in case it doesn't find it then it looks up the registry
     */
    public static String getParameter(VelocityPortlet portlet,
                               RunData rundata,
                               String attrName,
                               String attrDefValue)
    {
        return getParameter((Portlet) portlet, rundata, attrName, attrDefValue);
    }
    
    /**
     * Sets the registry (.xreg) value of this portlet.  Use this method because
     * PortletConfig.setInitParameter() is all but useless in this case.  The portlet
     * config availble in the Portlet is never saved back to the registry.
     */
    public static void setPortletConfigParameter(Portlet portlet, String name, String value)
    {
        PortletEntry pEntry = (PortletEntry) Registry.getEntry(Registry.PORTLET, portlet.getName());

        if (pEntry != null)
        {
            Parameter param = pEntry.getParameter(name);
            portlet.getPortletConfig().setInitParameter(name, value);
            if (param != null)
            {
                param.setValue(value);
            }
            else
            {
                pEntry.addParameter(name, value);
            }
        }
    }
}

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

package org.apache.jetspeed.modules.actions.portlets.security;

import java.util.Iterator;

// velocity
import org.apache.velocity.context.Context;

// turbine util
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.StringUtils;

// jetspeed services
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.jetspeed.services.resources.JetspeedResources;

// jetspeed velocity
import org.apache.jetspeed.modules.actions.portlets.VelocityPortletAction;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;


/**
 * This action sets up the template context for browsing of permissions in the Turbine database.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: PermissionBrowserAction.java,v 1.7 2004/02/23 02:53:08 jford Exp $
 */
public class PermissionBrowserAction extends VelocityPortletAction
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(PermissionBrowserAction.class.getName());     
    
    /**
     * Build the maximized state content for this portlet. (Same as normal state).
     *
     * @param portlet The velocity-based portlet that is being built.
     * @param context The velocity context for this request.
     * @param rundata The turbine rundata context for this request.
     */
    protected void buildMaximizedContext( VelocityPortlet portlet,
                                          Context context,
                                          RunData rundata )
    {
        buildNormalContext( portlet, context, rundata);
    }

    /**
     * Build the configure state content for this portlet.
     * TODO: we could configure this portlet with configurable skins, etc..
     *
     * @param portlet The velocity-based portlet that is being built.
     * @param context The velocity context for this request.
     * @param rundata The turbine rundata context for this request.
     */
    protected void buildConfigureContext( VelocityPortlet portlet,
                                          Context context,
                                          RunData rundata )
    {

        buildNormalContext( portlet, context, rundata);
    }

    /**
     * Build the normal state content for this portlet.
     *
     * @param portlet The velocity-based portlet that is being built.
     * @param context The velocity context for this request.
     * @param rundata The turbine rundata context for this request.
     */
    protected void buildNormalContext( VelocityPortlet portlet,
                                       Context context,
                                       RunData rundata )
    {
        try
        {
            Iterator permissions = JetspeedSecurity.getPermissions();
            context.put(SecurityConstants.CONTEXT_PERMISSIONS, permissions);
        }
        catch (JetspeedSecurityException e)
        {
           // log the error msg
            logger.error("Exception", e);

            rundata.setMessage("Error in Jetspeed Permission Security: " + e.toString());
            rundata.setStackTrace(StringUtils.stackTrace(e), e);
            rundata.setScreenTemplate(JetspeedResources.getString("template.error","Error"));
        }
    }

}
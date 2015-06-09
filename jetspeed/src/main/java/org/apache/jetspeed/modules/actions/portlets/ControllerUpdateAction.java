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
package org.apache.jetspeed.modules.actions.portlets;

import org.apache.jetspeed.modules.actions.portlets.security.SecurityConstants;
import org.apache.jetspeed.om.registry.MediaTypeRegistry;
import org.apache.jetspeed.om.registry.PortletControllerEntry;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import org.apache.jetspeed.services.Registry;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * This action enables to update the controller entries
 *
 * @author <a href="mailto:caius1440@hotmail.com">Jeremy Ford</a>
 * @version $Id: ControllerUpdateAction.java,v 1.4 2004/02/23 02:56:58 jford Exp $
 */
public class ControllerUpdateAction extends RegistryUpdateAction
{
    private static final String CONTROLLER_UPDATE_PANE = "ControllerForm";
    
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(ControllerUpdateAction.class.getName());

    public ControllerUpdateAction()
    {
        registryEntryName = "controller_name";
        registry = Registry.PORTLET_CONTROLLER;
        pane = CONTROLLER_UPDATE_PANE;
    }

    /**
     * @see org.apache.jetspeed.modules.actions.portlets.VelocityPortletAction#buildNormalContext(org.apache.jetspeed.portal.portlets.VelocityPortlet, org.apache.velocity.context.Context, org.apache.turbine.util.RunData)
     */
    protected void buildNormalContext(
        VelocityPortlet portlet,
        Context context,
        RunData rundata)
        throws Exception
    {
        super.buildNormalContext(portlet, context, rundata);

        String mode =
            rundata.getParameters().getString(SecurityConstants.PARAM_MODE);

        if (mode != null)
        {
            if(mode.equals(SecurityConstants.PARAM_MODE_DELETE)
                || mode.equals(SecurityConstants.PARAM_MODE_UPDATE))
            {
                String controllerName =
                    rundata.getParameters().getString(registryEntryName);
                PortletControllerEntry controllerEntry =
                    (PortletControllerEntry) Registry.getEntry(
                        registry,
                        controllerName);
                context.put("entry", controllerEntry);
            }
            
            if(mode.equals(SecurityConstants.PARAM_MODE_UPDATE))
            {
                MediaTypeRegistry mediaTypeReg = (MediaTypeRegistry)Registry.get(Registry.MEDIA_TYPE);
                context.put("media_types", iteratorToCollection(mediaTypeReg.listEntryNames()));
            }
        }
    }

    protected void updateRegistryEntry(
        RunData rundata,
        RegistryEntry registryEntry)
        throws Exception
    {
        super.updateRegistryEntry(rundata, registryEntry);

        PortletControllerEntry controllerEntry =
            (PortletControllerEntry) registryEntry;
        String className = rundata.getParameters().getString("class_name");
        if (hasChanged(controllerEntry.getClassname(), className))
        {
            controllerEntry.setClassname(className);
        }
    }

    /**
     * @param rundata
     */
    protected void clearUserData(RunData rundata)
    {
        try
        {
            super.clearUserData(rundata);

            rundata.getUser().removeTemp("media_type_ref");
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("ControllerUpdateAction: Failed to clear user data");
            }
        }

    }

    /**
     * @param rundata
     */
    protected void resetForm(RunData rundata)
    {
        super.resetForm(rundata);
        String mediaTypeRef =
            rundata.getParameters().getString("media_type_ref");

        rundata.getUser().setTemp("media_type_ref", mediaTypeRef);
    }
}

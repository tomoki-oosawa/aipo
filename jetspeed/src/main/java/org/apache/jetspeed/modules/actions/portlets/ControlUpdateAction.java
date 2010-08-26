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
import org.apache.jetspeed.om.registry.PortletControlEntry;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.Registry;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * This action enables to update the control entries
 *
 * @author <a href="mailto:caius1440@hotmail.com">Jeremy Ford</a>
 * @version $Id: ControlUpdateAction.java,v 1.2 2004/02/23 02:56:58 jford Exp $
 */
public class ControlUpdateAction extends RegistryUpdateAction
{
    private static final String CONTROL_UPDATE_PANE = "ControlForm";

    public ControlUpdateAction()
    {
        registryEntryName = "control_name";
        registry = Registry.PORTLET_CONTROL;
        pane = CONTROL_UPDATE_PANE;
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

        if (mode != null
            && (mode.equals(SecurityConstants.PARAM_MODE_DELETE)
                || mode.equals(SecurityConstants.PARAM_MODE_UPDATE)))
        {
            String controllerName =
                rundata.getParameters().getString(registryEntryName);
            PortletControlEntry controlEntry =
                (PortletControlEntry) Registry.getEntry(
                    registry,
                    controllerName);
            context.put("entry", controlEntry);

            MediaTypeRegistry mediaTypeReg =
                (MediaTypeRegistry) Registry.get(Registry.MEDIA_TYPE);
            context.put(
                "media_types",
                iteratorToCollection(mediaTypeReg.listEntryNames()));
        }
    }

    protected void updateRegistryEntry(
        RunData rundata,
        RegistryEntry registryEntry)
        throws Exception
    {
        super.updateRegistryEntry(rundata, registryEntry);

        PortletControlEntry controllerEntry =
            (PortletControlEntry) registryEntry;
        String className = rundata.getParameters().getString("class_name");
        if (hasChanged(controllerEntry.getClassname(), className))
        {
            controllerEntry.setClassname(className);
        }
    }

    /**
     * @see org.apache.jetspeed.modules.actions.portlets.RegistryUpdateAction#resetForm(org.apache.turbine.util.RunData)
     */
    protected void resetForm(RunData rundata)
    {
        super.resetForm(rundata);
    }

    /**
     * @see org.apache.jetspeed.modules.actions.portlets.RegistryUpdateAction#clearUserData(org.apache.turbine.util.RunData)
     */
    protected void clearUserData(RunData rundata)
    {
        super.clearUserData(rundata);
    }

}

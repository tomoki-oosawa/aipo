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

import java.util.Iterator;
import java.util.Map;

import org.apache.jetspeed.modules.actions.portlets.security.SecurityConstants;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.om.registry.SkinEntry;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.Registry;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * This action enables to update the skin entries
 *
 * @author <a href="mailto:caius1440@hotmail.com">Jeremy Ford</a>
 * @version $Id: SkinUpdateAction.java,v 1.6 2004/02/23 02:56:58 jford Exp $
 */
public class SkinUpdateAction extends RegistryUpdateAction
{

    private static final String PARAMETER = "parameter.";
    private static final String SKIN_UPDATE_PANE = "SkinForm";
    
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(SkinUpdateAction.class.getName());     
    
    public SkinUpdateAction()
    {
        registryEntryName = "skinname";
        registry = Registry.SKIN;
        pane = SKIN_UPDATE_PANE;
    }

    /**
     * Subclasses must override this method to provide default behavior
     * for the portlet action
     */
    /**
     * Build the normal state content for this portlet.
     *
     * @param portlet The velocity-based portlet that is being built.
     * @param context The velocity context for this request.
     * @param rundata The turbine rundata context for this request.
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
            String skinName = rundata.getParameters().getString("skinname");
            SkinEntry skinEntry =
                (SkinEntry) Registry.getEntry(Registry.SKIN, skinName);
            context.put("entry", skinEntry);
        }
    }

    /**
     * @see org.apache.jetspeed.modules.actions.portlets.RegistryUpdateAction#updateRegistryEntry(org.apache.turbine.util.RunData, org.apache.jetspeed.om.registry.RegistryEntry)
     */
    protected void updateRegistryEntry(
        RunData rundata,
        RegistryEntry registryEntry) throws Exception
    {
        super.updateRegistryEntry(rundata, registryEntry);
        updateParameters(rundata, (SkinEntry) registryEntry);

    }

    /**
     * Populates the user's temp storage with form data
     * @param rundata The turbine rundata context for this request.
     */
    protected void resetForm(RunData rundata)
    {
        super.resetForm(rundata);

        Object[] keys = rundata.getParameters().getKeys();
        if (keys != null)
        {
            for (int i = 0; i < keys.length; i++)
            {
                String key = (String) keys[i];

                if (key.startsWith(PARAMETER))
                {
                    String parameterValue =
                        rundata.getParameters().getString(key);

                    if (parameterValue != null && parameterValue.length() > 0)
                    {
                        rundata.getUser().setTemp(key, parameterValue);
                    }
                }
            }
        }
    }

    /**
     * Adds parameters to a skin entry
     * @param rundata The turbine rundata context for this request.
     * @param skinEntry
     */
    private void updateParameters(RunData rundata, SkinEntry skinEntry)
    {
        Object[] keys = rundata.getParameters().getKeys();
        if (keys != null)
        {
            for (int i = 0; i < keys.length; i++)
            {
                String key = (String) keys[i];

                if (key.startsWith(PARAMETER))
                {
                    String parameterValue =
                        rundata.getParameters().getString(key);

                    if (parameterValue != null && parameterValue.length() > 0)
                    {
                        String parameterName =
                            key.substring(PARAMETER.length());
                        skinEntry.removeParameter(parameterName);
                        skinEntry.addParameter(parameterName, parameterValue);
                    }
                }
            }
        }
    }

    /**
     * Clears the temporary storage of any data that was used
     * @param rundata
     */
    protected void clearUserData(RunData rundata)
    {
        try
        {
            super.clearUserData(rundata);

            Map tempStorage = rundata.getUser().getTempStorage();
            if (tempStorage != null)
            {
                Iterator keyIter = tempStorage.keySet().iterator();
                while (keyIter.hasNext())
                {
                    Object keyObj = keyIter.next();
                    if (keyObj instanceof String)
                    {
                        String key = (String) keyObj;
                        if (key.startsWith(PARAMETER))
                        {
                            keyIter.remove();
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("SkinUpdateAction: Failed to clear user data");
            }
        }
    }
}

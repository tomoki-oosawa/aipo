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
import org.apache.jetspeed.om.registry.MediaTypeEntry;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.Registry;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * This action enables to update the media entries
 *
 * @author <a href="mailto:caius1440@hotmail.com">Jeremy Ford</a>
 * @version $Id: MediaUpdateAction.java,v 1.2 2004/02/23 02:56:58 jford Exp $
 */
public class MediaUpdateAction extends RegistryUpdateAction
{
    private static final String MEDIA_UPDATE_PANE = "MediaForm";
    
    public MediaUpdateAction()
    {
        registryEntryName = "media_type_name";
        registry = Registry.MEDIA_TYPE;
        pane = MEDIA_UPDATE_PANE;
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
            String mediaTypeName =
                rundata.getParameters().getString(registryEntryName);
            MediaTypeEntry mediaEntry =
                (MediaTypeEntry) Registry.getEntry(
                    registry,mediaTypeName);
            context.put("entry", mediaEntry);
        }
    }
    
    /** 
     * @see org.apache.jetspeed.modules.actions.portlets.RegistryUpdateAction#updateRegistryEntry(org.apache.turbine.util.RunData, org.apache.jetspeed.om.registry.RegistryEntry)
     */
    protected void updateRegistryEntry(RunData rundata, RegistryEntry registryEntry) throws Exception
    {
        super.updateRegistryEntry(rundata, registryEntry);
        
        updateMediaTypeEntry(rundata, (MediaTypeEntry) registryEntry);
    }

    /**
     * @param rundata
     * @param mediaTypeName
     */
    protected void updateMediaTypeEntry(
        RunData rundata,
        MediaTypeEntry mediaTypeEntry)
    {
        String charSet = rundata.getParameters().getString("charset");
        String mimeType = rundata.getParameters().getString("mime_type");

        if(hasChanged(mediaTypeEntry.getCharacterSet(), charSet))
        {
            mediaTypeEntry.setCharacterSet(charSet);
        }
        if(hasChanged(mediaTypeEntry.getMimeType(), mimeType))
        {
            mediaTypeEntry.setMimeType(mimeType);
        }
    }

    /**
      * Populates the user's temp storage with form data
      * @param rundata The turbine rundata context for this request.
      */
    protected void resetForm(RunData rundata)
    {
        super.resetForm(rundata);
        String charSet = rundata.getParameters().getString("charset");
        String mimeType = rundata.getParameters().getString("mime_type");
        
        rundata.getUser().setTemp("charset", charSet);
        rundata.getUser().setTemp("mime_type", mimeType);
    }
    
    /**
     * Clears the temporary storage of any data that was used
     * @param rundata The turbine rundata context for this request.
     */
    protected void clearUserData(RunData rundata)
    {
        super.clearUserData(rundata);
        rundata.getUser().removeTemp("charset");
        rundata.getUser().removeTemp("mime_type");
    }
}

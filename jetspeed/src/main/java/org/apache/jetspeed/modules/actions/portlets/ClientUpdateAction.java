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
package org.apache.jetspeed.modules.actions.portlets;

// Jetspeed classes
import org.apache.jetspeed.modules.actions.portlets.security.SecurityConstants;
import org.apache.jetspeed.om.registry.ClientEntry;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.Registry;

// Regexp classes
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

// Trubine classes
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * This action enables to update the client entries
 *
 * @author <a href="mailto:caius1440@hotmail.com">Jeremy Ford</a>
 * @version $Id: ClientUpdateAction.java,v 1.4 2004/02/23 02:56:58 jford Exp $
 */
public class ClientUpdateAction extends RegistryUpdateAction
{
    private static final String whoAmI = "ClientUpdateAction: ";
    private static final String CLIENT_UPDATE_PANE = "ClientForm";
    
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(ClientUpdateAction.class.getName());
    
    public ClientUpdateAction()
    {
        registryEntryName = "client_name";
        registry = Registry.CLIENT;
        pane = CLIENT_UPDATE_PANE;
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
			String clientName =
				rundata.getParameters().getString("client_name");
			ClientEntry clientEntry =
				(ClientEntry) Registry.getEntry(Registry.CLIENT, clientName);
			context.put("entry", clientEntry);
		}
	}

	/**
	 * Add a mimetype to a client entry
	 * @param rundata The turbine rundata context for this request.
	 * @param context The velocity context for this request.
	 * @throws Exception
	 */
	public void doAddmimetype(RunData rundata, Context context)
		throws Exception
	{
		try
		{
			String clientName =
				rundata.getParameters().getString("client_name");
			ClientEntry clientEntry =
				(ClientEntry) Registry.getEntry(Registry.CLIENT, clientName);
			if (clientEntry != null)
			{
                String mimeType = rundata.getParameters().getString("mime_type");
				clientEntry.getMimetypeMap().addMimetype(mimeType);

				Registry.addEntry(Registry.CLIENT, clientEntry);
				clearUserData(rundata);
			}
			else
			{
                DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_INVALID_ENTITY_NAME);
				rundata.setRedirectURI(duri.toString());
				resetForm(rundata);
                
                logger.error(this.getClass().getName() + ": Failed to find registry entry while trying to add mime type");
			}
		}
		catch (Exception e)
		{
            DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_UPDATE_FAILED);
			rundata.setRedirectURI(duri.toString());
			resetForm(rundata);
            
            logger.error("Error addin mime type", e);
		}
	}
    

	/**
	 * Remove mime types from a client entry
	 * @param rundata The turbine rundata context for this request.
	 * @param context The velocity context for this request.
	 * @throws Exception
	 */
    
	public void doRemovemimetype(RunData rundata, Context context)
		throws Exception
	{
		try
		{
			String clientName =
				rundata.getParameters().getString("client_name");
			ClientEntry clientEntry =
				(ClientEntry) Registry.getEntry(Registry.CLIENT, clientName);
			if (clientEntry != null)
			{
				String[] mimeTypes =
                    rundata.getParameters().getStrings("mime_type");
				if (mimeTypes != null && mimeTypes.length > 0)
				{
					for (int i = 0; i < mimeTypes.length; i++)
					{
						String mimeType = mimeTypes[i];

						clientEntry.getMimetypeMap().removeMimetype(mimeType);
					}

					Registry.addEntry(Registry.CLIENT, clientEntry);
					clearUserData(rundata);
				}
			}
			else
			{
                DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_INVALID_ENTITY_NAME);
				rundata.setRedirectURI(duri.toString());
				resetForm(rundata);
                
                logger.error(this.getClass().getName() + ": Failed to find registry entry while trying to remove mime types");
			}
		}
		catch (Exception e)
		{
            DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_UPDATE_FAILED);
			rundata.setRedirectURI(duri.toString());
			resetForm(rundata);
            
            logger.error("Error removing mime types", e);
		}
	}
    

	/**
     * @see org.apache.jetspeed.modules.actions.portlets.RegistryUpdateAction#updateRegistryEntry(org.apache.turbine.util.RunData, org.apache.jetspeed.om.registry.RegistryEntry)
     */
    protected void updateRegistryEntry(RunData rundata, RegistryEntry registryEntry) throws Exception
	{
        super.updateRegistryEntry(rundata, registryEntry);
        
        updateClientEntry(rundata, (ClientEntry)registryEntry);
	}

	/**
	 * Set the client entry parameters from the input parameters
	 * @param rundata The turbine rundata context for this request.
	 * @param context The velocity context for this request.
	 */
	private void updateClientEntry(
		RunData rundata,
        ClientEntry clientEntry) throws Exception
	{
		String userAgentPattern =
			rundata.getParameters().getString("user_agent_pattern");
		String manufacturer = rundata.getParameters().getString("manufacturer");
		String model = rundata.getParameters().getString("model");
		String version = rundata.getParameters().getString("version");

        if(hasChanged(clientEntry.getUseragentpattern(), userAgentPattern))
		{
            try
			{
                RE re = new RE(userAgentPattern);
                clientEntry.setUseragentpattern(userAgentPattern);
			}
            catch(RESyntaxException e)
			{
                logger.error(whoAmI + "Illegal regular expression syntax " + userAgentPattern + " for user agent");
                logger.debug(whoAmI + "Illegal regular expression syntax for user agent", e);
                
                throw new IllegalArgumentException("Illegal regular expression syntax for user agent");
			}
		}
        if(hasChanged(clientEntry.getManufacturer(), manufacturer))
		{
            clientEntry.setManufacturer(manufacturer);
        }
        if(hasChanged(clientEntry.getModel(), model))
        {
            clientEntry.setModel(model);
        }
        if(hasChanged(clientEntry.getVersion(), version))
        {
            clientEntry.setVersion(version);
		}
	}

	/**
	 * Populates the user's temp storage with form data
	 * @param rundata The turbine rundata context for this request.
	 */
    protected void resetForm(RunData rundata)
	{
        super.resetForm(rundata);
        
		String userAgentPattern =
			rundata.getParameters().getString("user_agent_pattern");
		String manufacturer = rundata.getParameters().getString("manufacturer");
		String model = rundata.getParameters().getString("model");
		String version = rundata.getParameters().getString("version");

		String capability = rundata.getParameters().getString("capability");
        String mimeType = rundata.getParameters().getString("mime_type");

		rundata.getUser().setTemp("user_agent_pattern", userAgentPattern);
		rundata.getUser().setTemp("manufacturer", manufacturer);
		rundata.getUser().setTemp("model", model);
		rundata.getUser().setTemp("version", version);

		rundata.getUser().setTemp("capability", capability);
		rundata.getUser().setTemp("mimetype", mimeType);
	}

	/**
	 * Clears the temporary storage of any data that was used
	 * @param rundata The turbine rundata context for this request.
	 */
    protected void clearUserData(RunData rundata)
    {
	try
	{
            super.clearUserData(rundata);
            
            rundata.getUser().removeTemp("user_agent_pattern");
            rundata.getUser().removeTemp("manufacturer");
            rundata.getUser().removeTemp("model");
            rundata.getUser().removeTemp("version");

            rundata.getUser().removeTemp("capability");
            rundata.getUser().removeTemp("mime_type");
        }
        catch (Exception e)
        {
                   
            if (logger.isDebugEnabled())
            {
                logger.debug("ClientUpdateAction: Failed to clear user data");
            }
        }
    }
}

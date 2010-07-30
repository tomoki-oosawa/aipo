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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.apache.jetspeed.modules.actions.portlets.security.SecurityConstants;
import org.apache.jetspeed.om.registry.SecurityAccess;
import org.apache.jetspeed.om.registry.SecurityAllow;
import org.apache.jetspeed.om.registry.base.BaseSecurityAccess;
import org.apache.jetspeed.om.registry.base.BaseSecurityAllow;
import org.apache.jetspeed.om.registry.base.BaseSecurityAllowOwner;
import org.apache.jetspeed.om.registry.base.BaseSecurityEntry;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.Registry;
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * This action sets up the template context for managing of security entries in the Turbine database.
 *
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: $
 */
public class SecurityUpdateAction extends RegistryUpdateAction
{
    private static final String SECURITY_UPDATE_PANE = "security-form";

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(SecurityUpdateAction.class.getName());     
    
    public SecurityUpdateAction()
    {
        registryEntryName = "security_name";
        registry = Registry.SECURITY;
        pane = SECURITY_UPDATE_PANE;
    }

    /**
     * @see org.apache.jetspeed.modules.actions.portlets.VelocityPortletAction#buildNormalContext(VelocityPortlet, Context, RunData)
     */
    protected void buildNormalContext(
        VelocityPortlet portlet,
        Context context,
        RunData rundata)
        throws Exception
    {
        String mode =
            rundata.getParameters().getString(SecurityConstants.PARAM_MODE);
        context.put(SecurityConstants.PARAM_MODE, mode);

        String msgid =
            rundata.getParameters().getString(SecurityConstants.PARAM_MSGID);
        if (msgid != null)
        {
            int id = Integer.parseInt(msgid);
            if (id < SecurityConstants.MESSAGES.length)
            {
                context.put(
                    SecurityConstants.PARAM_MSG,
                    SecurityConstants.MESSAGES[id]);
            }
        }

        if (mode != null
            && (mode.equals(SecurityConstants.PARAM_MODE_DELETE)
                || mode.equals(SecurityConstants.PARAM_MODE_UPDATE)))
        {
            String securityName =
                rundata.getParameters().getString("security_name");
            BaseSecurityEntry securityEntry =
                (BaseSecurityEntry) Registry.getEntry(
                    Registry.SECURITY,
                    securityName);

            String subMode = rundata.getParameters().getString("subMode");
            if (subMode != null)
            {
                context.put("subMode", subMode);
                int accessIndex =
                    rundata.getParameters().getInt("access_index", -1);
                if (accessIndex != -1)
                {
                    context.put("accessIndex", new Integer(accessIndex));
                    accessIndex--;
                    BaseSecurityAccess securityAccess =
                        (BaseSecurityAccess) securityEntry.getAccesses().get(
                            accessIndex);
                    context.put("securityAccess", securityAccess);
                }
            }

            Iterator permissionIter = JetspeedSecurity.getPermissions();
            Iterator userIter = JetspeedSecurity.getUsers();
            Iterator roleIter = JetspeedSecurity.getRoles();
            Iterator groupIter = JetspeedSecurity.getGroups();

            context.put("permissions", iteratorToCollection(permissionIter));
            context.put("users", iteratorToCollection(userIter));
            context.put("roles", iteratorToCollection(roleIter));
            context.put("groups", iteratorToCollection(groupIter));

            context.put("entry", securityEntry);
        }

        if (mode != null && (mode.equals(SecurityConstants.PARAM_MODE_INSERT)))
        {
            Iterator permissionIter = JetspeedSecurity.getPermissions();
            context.put("permissions", permissionIter);
        }
    }

    /**
     * Update a security entry in the registry
     * @param rundata The turbine rundata context for this request.
     * @param context The velocity context for this request.
     * @throws Exception
     */
    public void doAddaccess(RunData rundata, Context context) throws Exception
    {
        try
        {
            String securityName =
                rundata.getParameters().getString("security_name");
            BaseSecurityEntry securityEntry =
                (BaseSecurityEntry) Registry.getEntry(
                    Registry.SECURITY,
                    securityName);

            if (securityEntry != null)
            {
                String action =
                    rundata.getParameters().getString("access_action");

                if (action != null && action.length() > 0)
                {
                    BaseSecurityAccess securityAccess =
                        new BaseSecurityAccess();
                    securityAccess.setAction(action);

                    addAllow(rundata, securityAccess);

                    Vector accesses = securityEntry.getAccesses();
                    accesses.add(securityAccess);
                    securityEntry.setAccesses(accesses);

                    Registry.addEntry(Registry.SECURITY, securityEntry);
                    clearUserData(rundata);
                }
                else
                {
                    
                    DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_MISSING_PARAMETER);
                    rundata.setRedirectURI(duri.toString());
                    resetForm(rundata);
                }
            }
            else
            {
                DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_INVALID_ENTITY_NAME);
                rundata.setRedirectURI(duri.toString());
                resetForm(rundata);
                
                logger.error("Failed to find registry entry while trying to add accesses");
            }
        }
        catch (Exception e)
        {
            DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_UPDATE_FAILED);
            rundata.setRedirectURI(duri.toString());
            resetForm(rundata);
            
            logger.error("Exception", e);
        }
    }

    /**
     * Update a security entry in the registry
     * @param rundata The turbine rundata context for this request.
     * @param context The velocity context for this request.
     * @throws Exception
     */
    public void doUpdateaccess(RunData rundata, Context context)
        throws Exception
    {
        try
        {
            String securityName =
                rundata.getParameters().getString("security_name");
            BaseSecurityEntry securityEntry =
                (BaseSecurityEntry) Registry.getEntry(
                    Registry.SECURITY,
                    securityName);
            if (securityEntry != null)
            {
                int accessIndex =
                    rundata.getParameters().getInt("access_index", -1);
                accessIndex--;
                String action =
                    rundata.getParameters().getString("access_action");

                if (accessIndex >= 0
                    && accessIndex < securityEntry.getAccesses().size())
                {
                    BaseSecurityAccess securityAccess =
                        (BaseSecurityAccess) securityEntry.getAccesses().get(
                            accessIndex);
                    securityAccess.setAction(action);

                    Registry.addEntry(Registry.SECURITY, securityEntry);
                    clearUserData(rundata);
                }
            }
            else
            {
                DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_INVALID_ENTITY_NAME);
                rundata.setRedirectURI(duri.toString());
                resetForm(rundata);

                logger.error("Failed to find registry entry while trying to update accesses");
            }
        }
        catch (Exception e)
        {
            DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_UPDATE_FAILED);
            rundata.setRedirectURI(duri.toString());
            resetForm(rundata);

            logger.error("Exception", e);
        }
    }

    /**
     * Remove a access entry from a security entry in the registry
     * @param rundata The turbine rundata context for this request.
     * @param context The velocity context for this request.
     * @throws Exception
     */
    public void doRemoveaccess(RunData rundata, Context context)
        throws Exception
    {
        try
        {
            String securityName =
                rundata.getParameters().getString("security_name");
            BaseSecurityEntry securityEntry =
                (BaseSecurityEntry) Registry.getEntry(
                    Registry.SECURITY,
                    securityName);
            if (securityEntry != null)
            {
                int[] accessIndexes =
                    rundata.getParameters().getInts("access_index");

                if (accessIndexes != null && accessIndexes.length > 0)
                {
                    ArrayList deleteList = new ArrayList();

                    for (int i = 0; i < accessIndexes.length; i++)
                    {
                        int accessIndex = accessIndexes[i];
                        accessIndex--;

                        if (accessIndex >= 0
                            && accessIndex < securityEntry.getAccesses().size())
                        {
                            deleteList.add(
                                securityEntry.getAccesses().get(accessIndex));
                        }
                        else
                        {
                            logger.error(
                                "Access Index: " + i + " is out of range");
                        }
                    }


                    Vector accesses = securityEntry.getAccesses();
                    Iterator deleteIter = deleteList.iterator();
                    while (deleteIter.hasNext())
                    {
                        SecurityAccess sa = (SecurityAccess) deleteIter.next();
                        accesses.remove(sa);
                    }
                    
                    securityEntry.setAccesses(accesses);

                    Registry.addEntry(Registry.SECURITY, securityEntry);
                    clearUserData(rundata);
                }
                else
                {
                    DynamicURI duri =
                        redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_MISSING_PARAMETER);
                    rundata.setRedirectURI(duri.toString());
                    resetForm(rundata);
                }
            }
            else
            {
                DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_INVALID_ENTITY_NAME);
                rundata.setRedirectURI(duri.toString());
                resetForm(rundata);
                
                logger.error("Failed to find registry entry while trying to remove accesses");
            }
        }
        catch (Exception e)
        {
            DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_DELETE_FAILED);
            rundata.setRedirectURI(duri.toString());
            resetForm(rundata);
            
            logger.error("Exception", e);
        }
    }

    /**
     * Update a security entry in the registry
     * @param rundata The turbine rundata context for this request.
     * @param context The velocity context for this request.
     * @throws Exception
     */
    public void doAddallow(RunData rundata, Context context) throws Exception
    {
        try
        {
            String securityName =
                rundata.getParameters().getString("security_name");
            BaseSecurityEntry securityEntry =
                (BaseSecurityEntry) Registry.getEntry(
                    Registry.SECURITY,
                    securityName);
            if (securityEntry != null)
            {
                int accessIndex =
                    rundata.getParameters().getInt("access_index", -1);
                accessIndex--;

                if (accessIndex >= 0
                    && accessIndex < securityEntry.getAccesses().size())
                {
                    BaseSecurityAccess securityAccess =
                        (BaseSecurityAccess) securityEntry.getAccesses().get(
                            accessIndex);
                    addAllow(rundata, securityAccess);
                    Registry.addEntry(Registry.SECURITY, securityEntry);
                    clearUserData(rundata);
                }
                else
                {
                    DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_MISSING_PARAMETER);
                    rundata.setRedirectURI(duri.toString());
                    resetForm(rundata);
                }
            }
            else
            {
                DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_INVALID_ENTITY_NAME);
                rundata.setRedirectURI(duri.toString());
                resetForm(rundata);
                
                logger.error("Failed to find registry entry while trying to add allow");
            }
        }
        catch (Exception e)
        {
            DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_UPDATE_FAILED);
            rundata.setRedirectURI(duri.toString());
            resetForm(rundata);
            
            logger.error("Exception", e);
        }
    }

    /**
     * Update a security entry in the registry
     * @param rundata The turbine rundata context for this request.
     * @param context The velocity context for this request.
     * @throws Exception
     */
    public void doRemoveallow(RunData rundata, Context context)
        throws Exception
    {
        try
        {
            String securityName =
                rundata.getParameters().getString("security_name");
            BaseSecurityEntry securityEntry =
                (BaseSecurityEntry) Registry.getEntry(
                    Registry.SECURITY,
                    securityName);
            if (securityEntry != null)
            {
                int accessIndex =
                    rundata.getParameters().getInt("access_index", -1);
                accessIndex--;

                if (accessIndex >= 0
                    && accessIndex < securityEntry.getAccesses().size())
                {
                    BaseSecurityAccess securityAccess =
                        (BaseSecurityAccess) securityEntry.getAccesses().get(
                            accessIndex);

                    String allowType =
                        rundata.getParameters().getString(
                            "allow_type",
                            "allows");
                    int[] allowIndexes =
                        rundata.getParameters().getInts("allow_index");

                    if (allowIndexes != null && allowIndexes.length > 0)
                    {
                        for (int i = 0; i < allowIndexes.length; i++)
                        {
                            int allowIndex = allowIndexes[i];
                            allowIndex--;

                            if (allowIndex >= 0)
                            {
                                //TODO: more validation
                                if (allowType.equals("owner"))
                                {
                                    securityAccess.getOwnerAllows().remove(
                                        allowIndex);
                                }
                                else
                                {
                                    securityAccess.getAllows().remove(
                                        allowIndex);
                                }

                                Registry.addEntry(
                                    Registry.SECURITY,
                                    securityEntry);
                                clearUserData(rundata);
                            }
                            else
                            {
                                logger.error(
                                    "Allow Index: "
                                        + allowIndex
                                        + " is out of range.");
                            }
                        }
                    }
                    else
                    {
                        DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_MISSING_PARAMETER);
                        rundata.setRedirectURI(duri.toString());
                        resetForm(rundata);
                    }
                }
                else
                {
                    DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_MISSING_PARAMETER);
                    rundata.setRedirectURI(duri.toString());
                    resetForm(rundata);
                }
            }
            else
            {
                DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_INVALID_ENTITY_NAME);
                rundata.setRedirectURI(duri.toString());
                resetForm(rundata);
                
                logger.error("Failed to find registry entry while trying to remove allow");
            }
        }
        catch (Exception e)
        {
            DynamicURI duri = redirect(rundata, SecurityConstants.PARAM_MODE_UPDATE, SecurityConstants.MID_DELETE_FAILED);
            rundata.setRedirectURI(duri.toString());
            resetForm(rundata);
            
            logger.error("Exception", e);
        }
    }

    private void addAllow(RunData rundata, BaseSecurityAccess securityAccess)
    {
        String allowType = rundata.getParameters().getString("allow_type");
        String allowValue = rundata.getParameters().getString("allow_value");
		String allowValue2 = rundata.getParameters().getString("allow_value2");

        SecurityAllow allow = null;
        if (allowType.equals("user"))
        {
            allow = new BaseSecurityAllow();
            allow.setUser(allowValue);
            securityAccess.getAllows().add(allow);
        }
        else if (allowType.equals("role"))
        {
            allow = new BaseSecurityAllow();
            allow.setRole(allowValue);

            securityAccess.getAllows().add(allow);
        }
        else if (allowType.equals("group"))
        {
            allow = new BaseSecurityAllow();
            allow.setGroup(allowValue);

            securityAccess.getAllows().add(allow);
        }
		else if (allowType.equals("groupRole"))
		{
			allow = new BaseSecurityAllow();
			allow.setGroup(allowValue);
			allow.setRole(allowValue2);

			securityAccess.getAllows().add(allow);
		}
        else if (allowType.equals("owner"))
        {
            allow = new BaseSecurityAllowOwner();
            allow.setOwner(true);

            securityAccess.getOwnerAllows().add(allow);
        }
        else
        {
            //throw exception?
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

            rundata.getUser().removeTemp("security_name");
            rundata.getUser().removeTemp("allow_type");
            rundata.getUser().removeTemp("allow_value");
            rundata.getUser().removeTemp("access_index");
            rundata.getUser().removeTemp("access_action");
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("SecurityUpdateAction: Failed to clear user data");
            }
        }
    }

    /**
     * Populates the user's temp storage with form data
     * @param rundata The turbine rundata context for this request.
     */
    protected void resetForm(RunData rundata)
    {
        String securityName =
            rundata.getParameters().getString("security_name");
        String allowType = rundata.getParameters().getString("allow_type");
        String allowValue = rundata.getParameters().getString("allow_value");
        String accessIndex = rundata.getParameters().getString("access_index");
        String accessAction =
            rundata.getParameters().getString("access_action");

        rundata.getUser().setTemp("security_name", securityName);
        rundata.getUser().setTemp("allow_type", allowType);
        rundata.getUser().setTemp("allow_value", allowValue);
        rundata.getUser().setTemp("access_index", accessIndex);
        rundata.getUser().setTemp("access_action", accessAction);
    }
}

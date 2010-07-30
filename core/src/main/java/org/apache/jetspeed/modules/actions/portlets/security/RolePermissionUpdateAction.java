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

// java util
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.jetspeed.modules.actions.portlets.SecureVelocityPortletAction;
import org.apache.jetspeed.om.security.Permission;
import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.StringUtils;
import org.apache.velocity.context.Context;

/**
 * This action sets up the template context for editing security permissions in the Turbine database
 * for a given role.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: RolePermissionUpdateAction.java,v 1.9 2004/03/31 04:49:10 morciuch Exp $
 */
public class RolePermissionUpdateAction extends SecureVelocityPortletAction
{
    private static final String TEMP_ROLE = "tempRole";

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(RolePermissionUpdateAction.class.getName());     
    
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
            Role role = null;
            /*
             * Grab the mode for the user form.
             */
            String mode = rundata.getParameters().getString(SecurityConstants.PARAM_MODE);

            //
            // check to see if we are adding a role for a single user
            //
            String entityid = rundata.getParameters().getString(SecurityConstants.PARAM_ENTITY_ID);
            if (entityid == null || entityid.trim().length() == 0)
            {
                return;
            }

            buildRolePermissionContext(portlet, context, rundata, entityid);

            //
            // if there was an error, display the message
            //
            String msgid = rundata.getParameters().getString(SecurityConstants.PARAM_MSGID);
            if (msgid != null)
            {
                int id = Integer.parseInt(msgid);
                if (id < SecurityConstants.MESSAGES.length)
                    context.put(SecurityConstants.PARAM_MSG, SecurityConstants.MESSAGES[id]);
            }

        }
        catch (Exception e)
        {
            logger.error("Error in Jetspeed Role Permission Security", e);
            rundata.setMessage("Error in Jetspeed Role Permission Security: " + e.toString());
            rundata.setStackTrace(StringUtils.stackTrace(e), e);
            rundata.setScreenTemplate(JetspeedResources.getString("template.error","Error"));
        }
    }


    /**
     * Database Update Action for Security Role Permissions. Performs updates into security database.
     *
     * @param rundata The turbine rundata context for this request.
     * @param context The velocity context for this request.
     */
    public void doUpdate(RunData rundata, Context context)
        throws Exception
    {
        String entityid = rundata.getParameters().getString(SecurityConstants.PARAM_ENTITY_ID);
        if (entityid == null || entityid.trim().length() == 0)
        {
            logger.error("RolePermissionBrowser: Failed to get entity: " + entityid );
            DynamicURI duri = new DynamicURI (rundata);
            duri.addPathInfo(SecurityConstants.PANE_NAME, SecurityConstants.PANEID_ROLEPERMISSION_UPDATE);
            duri.addPathInfo(SecurityConstants.PARAM_MSGID, SecurityConstants.MID_MISSING_PARAMETER);
            rundata.setRedirectURI(duri.toString());
            return;
        }
        Role role = JetspeedSecurity.getRole(entityid);
        if (null == role)
        {
            logger.error("RolePermissionBrowser: Failed to get role: " + entityid );
            DynamicURI duri = new DynamicURI (rundata);
            duri.addPathInfo(SecurityConstants.PANE_NAME, SecurityConstants.PANEID_ROLEPERMISSION_UPDATE);
            duri.addPathInfo(SecurityConstants.PARAM_MSGID, SecurityConstants.MID_MISSING_PARAMETER);
            rundata.setRedirectURI(duri.toString());
            return;
        }


        try
        {
            List permissions = (List)rundata.getUser().getTemp(SecurityConstants.CONTEXT_PERMISSIONS);
            List selected = (List)rundata.getUser().getTemp(SecurityConstants.CONTEXT_SELECTED);

            if (permissions == null || selected == null)
            {
                DynamicURI duri = new DynamicURI (rundata);
                duri.addPathInfo(SecurityConstants.PANE_NAME, SecurityConstants.PANEID_ROLEPERMISSION_UPDATE);
                duri.addPathInfo(SecurityConstants.PARAM_MSGID, SecurityConstants.MID_MISSING_PARAMETER);
                rundata.setRedirectURI(duri.toString());
                return;
            }

            //
            // walk thru all the permissions, see if anything changed
            // if changed, update the database
            //
            for (int ix = 0; ix < permissions.size(); ix++)
            {
                String permissionName = ((Permission)permissions.get(ix)).getName();
                boolean newValue = rundata.getParameters().getBoolean("box_" + permissionName, false);
                boolean oldValue = ((Boolean)selected.get(ix + 1)).booleanValue();
                if (newValue != oldValue)
                {
                    if (newValue == true)
                    {
                        // grant a permission to a role
                        JetspeedSecurity.grantPermission(role.getName(), permissionName);
                    }
                    else
                    {
                        // revoke a permission from a role
                        JetspeedSecurity.revokePermission(role.getName(), permissionName);
                    }
                }
            }

            // clear the temp values
            rundata.getUser().setTemp(SecurityConstants.CONTEXT_PERMISSIONS, null);
            rundata.getUser().setTemp(SecurityConstants.CONTEXT_SELECTED, null);


        }
        catch (Exception e)
        {
           // log the error msg
            logger.error("Failed update role+permission", e);

            //
            // error on update - display error message
            //
            DynamicURI duri = new DynamicURI (rundata);
            duri.addPathInfo(SecurityConstants.PANE_NAME, SecurityConstants.PANEID_ROLEPERMISSION_UPDATE);
            duri.addPathInfo(SecurityConstants.PARAM_MSGID, SecurityConstants.MID_UPDATE_FAILED);
            if (role != null)
                duri.addPathInfo(SecurityConstants.PARAM_ENTITY_ID, role.getName());
            rundata.setRedirectURI(duri.toString());

        }
    }

    /**
     * Build the context for a role browser for a specific user.
     *
     * @param portlet The velocity-based portlet that is being built.
     * @param context The velocity context for this request.
     * @param rundata The turbine rundata context for this request.
     * @param roleName The roleName of the role that we are building a role context for.
     */
    private void buildRolePermissionContext(VelocityPortlet portlet,
                                            Context context,
                                            RunData rundata,
                                            String roleName)
        throws JetspeedSecurityException
    {
        // get master list of permissions
        Iterator master = JetspeedSecurity.getPermissions();

        // get the user object
        Role role = JetspeedSecurity.getRole(roleName);
        if (null == role)
        {
            // no ACL found
            logger.error("RolePermissionBrowser: Failed to get role: " + roleName);
            return;
        }

        // get the permissions for this particular role
        Iterator subset = JetspeedSecurity.getPermissions(roleName);

        Vector permissions = new Vector();
        Vector selected = new Vector();
        boolean sel = false;
        int ix = 0;
        selected.add(0, new Boolean(sel));
        while(master.hasNext())
        {
            Permission permission = (Permission) master.next();
            permissions.add(permission);
            sel = JetspeedSecurity.hasPermission(roleName, permission.getName());
            ix = ix + 1;
            selected.add(ix, new Boolean(sel));
        }
        selected.trimToSize();
        permissions.trimToSize();

        rundata.getUser().setTemp(SecurityConstants.CONTEXT_PERMISSIONS, permissions);
        rundata.getUser().setTemp(SecurityConstants.CONTEXT_SELECTED, selected);
        context.put(SecurityConstants.CONTEXT_PERMISSIONS, permissions);
        context.put(SecurityConstants.CONTEXT_SELECTED, selected);
        context.put(SecurityConstants.CONTEXT_ROLE, role);
    }


}
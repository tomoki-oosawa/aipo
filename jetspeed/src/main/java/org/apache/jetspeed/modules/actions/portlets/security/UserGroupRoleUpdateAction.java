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

package org.apache.jetspeed.modules.actions.portlets.security;

// java util
import java.util.Iterator;
import java.util.Vector;

import org.apache.jetspeed.modules.actions.portlets.SecureVelocityPortletAction;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.GroupRole;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.PsmlManager;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.util.PortletUtils;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.StringUtils;
import org.apache.velocity.context.Context;


/**
 * This action sets up the template context for editing security group roles
 * for a given user.
 *
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: UserGroupRoleUpdateAction.java,v 1.3 2004/03/31 04:49:10 morciuch Exp $
 */
public class UserGroupRoleUpdateAction extends SecureVelocityPortletAction
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(UserRoleUpdateAction.class.getName());     

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

            //
            // check to see if we are adding a role for a single user
            //
            String entityid = rundata.getParameters().getString(SecurityConstants.PARAM_ENTITY_ID);
            if (entityid == null || entityid.trim().length() == 0)
            {
                return;
            }

            buildUserGroupRoleContext(portlet, context, rundata, entityid);

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
            logger.error("Error in Jetspeed User Group Role Security", e);
            rundata.setMessage("Error in Jetspeed User Group Role Security: " + e.toString());
            rundata.setStackTrace(StringUtils.stackTrace(e), e);
            rundata.setScreenTemplate(JetspeedResources.getString("template.error","Error"));
        }
    }


    /**
     * Appends profile for specified role to the end of profile for specified user
     * 
     * @param user   User to append to
     * @param role   Role to append from
     * @exception Exception
     */
    private void appendNewRoleProfile(JetspeedRunData jdata, JetspeedUser user, Role role)
    throws Exception
    {
        // Retrieve the role profile
        ProfileLocator roleLocator = Profiler.createLocator();
        roleLocator.setRole(role);
        roleLocator.setMediaType(jdata.getCapability().getPreferredMediaType());
        roleLocator.setName("default.psml");
        Profile roleProfile = Profiler.getProfile(roleLocator);
        if (roleProfile != null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("UserGroupRoleUpdateAction: retrieved profile for role: " + roleProfile.getPath());
            }
        }

        // Retrieve the user profile
        ProfileLocator userLocator = Profiler.createLocator();
        userLocator.setUser(user);
        userLocator.setMediaType(jdata.getCapability().getPreferredMediaType());
        userLocator.setName("default.psml");
        Profile userProfile = Profiler.getProfile(userLocator);
        if (userProfile != null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("UserGroupRoleUpdateAction: retrieved profile for user: " + userProfile.getPath());
            }
        }

        // Append role profile to user profile
        if (roleProfile != null && 
            roleProfile.getDocument() != null && 
            userProfile != null && 
            userProfile.getDocument() != null)
        {
            Profile tmpProfile = (Profile) roleProfile.clone();
            Portlets rolePortlets = tmpProfile.getDocument().getPortlets();
            Portlets userPortlets = userProfile.getDocument().getPortlets();

            // Handle pane based profile
            if (rolePortlets.getPortletsCount() > 0)
            {
                for (int i = 0; i < rolePortlets.getPortletsCount(); i++)
                {
                    Portlets pane = rolePortlets.getPortlets(i);
                    pane.setLayout(null);                            
                    userPortlets.addPortlets(pane);
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("UserRoleUpdateAction: appended pane: " + pane.getId() + " to user: " + user.getUserName());
                    }
                }
            }
            // Handle profile with no panes
            else
            {
                if (rolePortlets.getTitle() == null)
                {
                    String title = org.apache.turbine.util.StringUtils.firstLetterCaps(roleProfile.getRoleName());
                    rolePortlets.setTitle(title + " Home");
                }
                rolePortlets.setLayout(null);
                userPortlets.addPortlets(rolePortlets);
            }

            // Regenerate ids
            PortletUtils.regenerateIds(userPortlets);

            // Save the user profile
            PsmlManager.store(userProfile);
        }
    }

    /**
     * Build the context for a role browser for a specific user.
     *
     * @param portlet The velocity-based portlet that is being built.
     * @param context The velocity context for this request.
     * @param rundata The turbine rundata context for this request.
     * @param userid The userid of the user that we are building a role context for.
     */
    private void buildUserGroupRoleContext(VelocityPortlet portlet,
                                           Context context,
                                           RunData rundata,
                                           String userid)
    throws Exception
    {
        // get the user object
        JetspeedUser user = JetspeedSecurity.getUser(userid);
        if (null == user)
        {
            // no User found
            logger.error("UserGroupRoleBrowser: Failed to get user: " + userid );
            return;
        }
        // get master list of roles
        Iterator roles = JetspeedSecurity.getRoles();
		Vector masterRoles = new Vector();
		while (roles.hasNext())
		{
			Role role = (Role) roles.next();
			masterRoles.add(role);
		}

        Iterator groups = JetspeedSecurity.getGroups();
        Vector masterGroups = new Vector();
		while (groups.hasNext())
		{
			Group group = (Group) groups.next();
			masterGroups.add(group);
		}
                
        Vector selected = new Vector();
        Iterator groupRoles = JetspeedSecurity.getRoles(userid);
		while (groupRoles.hasNext())
		{
			GroupRole gr = (GroupRole) groupRoles.next();
			selected.add(gr.getGroup().getName() + gr.getRole().getName());
		}

        rundata.getUser().setTemp(SecurityConstants.CONTEXT_ROLES, masterRoles);
		rundata.getUser().setTemp(SecurityConstants.CONTEXT_GROUPS, masterGroups);        
        rundata.getUser().setTemp(SecurityConstants.CONTEXT_SELECTED, selected);
        
        context.put(SecurityConstants.CONTEXT_USER, user);
        context.put(SecurityConstants.CONTEXT_ROLES, masterRoles);
		context.put(SecurityConstants.CONTEXT_GROUPS, masterGroups);
        context.put(SecurityConstants.CONTEXT_SELECTED, selected);

    }

    /**
     * Update the roles that are to assigned to a user
     * for a project.
     */
    public void doRoles(RunData data, Context context)
    throws Exception
    {
        /*
         * Get the user we are trying to update. The username
         * has been hidden in the form so we will grab the
         * hidden username and use that to retrieve the
         * user.
         */
        String username = data.getParameters().getString("username");
        JetspeedUser user = JetspeedSecurity.getUser(username);

        /*
         * Grab all the Groups and Roles in the system.
         */
        for (Iterator groups = JetspeedSecurity.getGroups(); groups.hasNext();)
        {
            String groupName = ((Group) groups.next()).getName();

            for (Iterator roles = JetspeedSecurity.getRoles(); roles.hasNext();)
            {
                /*
                 * In the UserRoleForm.vm we made a checkbox
                 * for every possible Group/Role combination
                 * so we will compare every possible combination
                 * with the values that were checked off in
                 * the form. If we have a match then we will
                 * grant the user the role in the group.
                 */
                Role role = (Role) roles.next();
                String roleName = role.getName();
                String groupRole = groupName + roleName;

                String formGroupRole = data.getParameters().getString(groupRole);

                if (formGroupRole != null && JetspeedSecurity.hasRole(username, roleName, groupName) == false)
                {
                    JetspeedSecurity.grantRole(username, roleName, groupName);
                    
					// If role profile merging is active, append profile for the new role
					if (Profiler.useRoleProfileMerging())
					{
						appendNewRoleProfile((JetspeedRunData) data, user, role);
					}                    
                }
                else if (formGroupRole == null && JetspeedSecurity.hasRole(username, roleName, groupName))
                {
                    JetspeedSecurity.revokeRole(username, roleName, groupName);
                }
            }
        }
    }


}

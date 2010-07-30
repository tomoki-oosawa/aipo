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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.modules.actions.portlets.SecureVelocityPortletAction;
import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.StringUtils;
import org.apache.velocity.context.Context;


/**
 * This action sets up the template context for editing security roles in the Turbine database
 * for a given user.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: UserGroupUpdateAction.java,v 1.5 2004/03/31 04:49:10 morciuch Exp $
 */
public class UserGroupUpdateAction extends SecureVelocityPortletAction
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(UserGroupUpdateAction.class.getName());     
    
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
            Group group = null;
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

            buildUserGroupsContext(portlet, context, rundata, entityid);

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
            logger.error("Error in Jetspeed User Group Security", e);
            rundata.setMessage("Error in Jetspeed User Group Security: " + e.toString());
            rundata.setStackTrace(StringUtils.stackTrace(e), e);
            rundata.setScreenTemplate(JetspeedResources.getString("template.error","Error"));
        }
    }


    /**
     * Database Update Action for Security Roles. Performs updates into security database.
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
            logger.error("UserGroupBrowser: Failed to get entity: " + entityid );
            DynamicURI duri = new DynamicURI (rundata);
            duri.addPathInfo(SecurityConstants.PANE_NAME, "UserGroupForm");
            duri.addPathInfo(SecurityConstants.PARAM_MSGID, SecurityConstants.MID_MISSING_PARAMETER);
            rundata.setRedirectURI(duri.toString());
            return;
        }

        JetspeedUser user = JetspeedSecurity.getUser(entityid);
        if (null == user)
        {
            logger.error("UserGroupBrowser: Failed to get user: " + entityid );
            DynamicURI duri = new DynamicURI (rundata);
            duri.addPathInfo(SecurityConstants.PANE_NAME, "UserGroupForm");
            duri.addPathInfo(SecurityConstants.PARAM_MSGID, SecurityConstants.MID_MISSING_PARAMETER);
            rundata.setRedirectURI(duri.toString());
            return;
        }


        try
        {
            List groups = (List)rundata.getUser().getTemp(SecurityConstants.CONTEXT_GROUPS);
            List selected = (List)rundata.getUser().getTemp(SecurityConstants.CONTEXT_SELECTED);

            if (groups == null || selected == null)
            {
                DynamicURI duri = new DynamicURI (rundata);
                duri.addPathInfo(SecurityConstants.PANE_NAME, "UserGroupForm");
                duri.addPathInfo(SecurityConstants.PARAM_MSGID, SecurityConstants.MID_MISSING_PARAMETER);
                rundata.setRedirectURI(duri.toString());
                return;
            }

            //
            // walk thru all the roles, see if anything changed
            // if changed, update the database
            //
            for (int ix = 0; ix < groups.size(); ix++)
            {
                boolean newValue = rundata.getParameters().getBoolean("box_" + ((Group)groups.get(ix)).getName(), false);
                boolean oldValue = ((Boolean)selected.get(ix + 1)).booleanValue();
                if (newValue != oldValue)
                {
                    if (newValue == true)
                    {
                        // grant a role to a user
                        JetspeedSecurity.joinGroup( user.getUserName(),
                                                ((Group)groups.get(ix)).getName() );
                    }
                    else
                    {
                        // revoke a role from a user
                        JetspeedSecurity.unjoinGroup( user.getUserName(),
                                                    ((Group)groups.get(ix)).getName() );
                    }
                }
            }

            // clear the temp values
            rundata.getUser().setTemp(SecurityConstants.CONTEXT_GROUPS, null);
            rundata.getUser().setTemp(SecurityConstants.CONTEXT_SELECTED, null);

        }
        catch (Exception e)
        {
           // log the error msg
            logger.error("Failed update role+permission: ", e);

            //
            // error on update - display error message
            //
            DynamicURI duri = new DynamicURI (rundata);
            duri.addPathInfo(SecurityConstants.PANE_NAME, "UserGroupForm");
            duri.addPathInfo(SecurityConstants.PARAM_MSGID, SecurityConstants.MID_UPDATE_FAILED);
            if (user != null)
                duri.addPathInfo(SecurityConstants.PARAM_ENTITY_ID, user.getUserName());
            rundata.setRedirectURI(duri.toString());

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
    private void buildUserGroupsContext(VelocityPortlet portlet,
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
            logger.error("UserGroupBrowser: Failed to get user: " + userid );
            return;
        }
        // get master list of roles
        Iterator groups = JetspeedSecurity.getGroups();
        ArrayList masterGroups = new ArrayList();
        ArrayList selected = new ArrayList();
        int ix = 0;
        boolean sel = false;
        selected.add(ix, new Boolean(sel));
        while(groups.hasNext())
        {
            Group group = (Group)groups.next();
            masterGroups.add(group);
            sel = JetspeedSecurity.inGroup(user.getUserName(), group.getName());
            ix = ix + 1;
            selected.add(ix, new Boolean(sel));
        }
        masterGroups.trimToSize();
        selected.trimToSize();

        rundata.getUser().setTemp(SecurityConstants.CONTEXT_GROUPS, masterGroups);
        rundata.getUser().setTemp(SecurityConstants.CONTEXT_SELECTED, selected);
        context.put(SecurityConstants.CONTEXT_USER, user);
        context.put(SecurityConstants.CONTEXT_GROUPS, masterGroups);
        context.put(SecurityConstants.CONTEXT_SELECTED, selected);

    }


}
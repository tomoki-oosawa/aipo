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

// velocity
import org.apache.jetspeed.modules.actions.portlets.SecureVelocityPortletAction;
import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.JetspeedGroupFactory;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.security.GroupException;
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.StringUtils;
import org.apache.velocity.context.Context;


/**
 * This action sets up the template context for editing security groups in the Turbine database.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: GroupUpdateAction.java,v 1.10 2004/03/31 04:49:10 morciuch Exp $
 */

public class GroupUpdateAction extends SecureVelocityPortletAction
{
    private static final String TEMP_GROUP = "tempGroup";
    
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(GroupUpdateAction.class.getName());     

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
             * Grab the mode for the form.
             */
            String mode = rundata.getParameters().getString(SecurityConstants.PARAM_MODE);

            // if we are updating or deleting - put the name in the context
            //
            if (mode != null && (mode.equals(SecurityConstants.PARAM_MODE_UPDATE) ||
                                 mode.equals(SecurityConstants.PARAM_MODE_DELETE)))
            {
                // get the primary key and put the object in the context
                String groupname = rundata.getParameters().getString(SecurityConstants.PARAM_ENTITY_ID);
                group = JetspeedSecurity.getGroup(groupname);
                context.put(SecurityConstants.CONTEXT_GROUP, group);
            }

            //
            // if there was an error, display the message
            //
            String msgid = rundata.getParameters().getString(SecurityConstants.PARAM_MSGID);
            if (msgid != null)
            {
                int id = Integer.parseInt(msgid);
                if (id < SecurityConstants.MESSAGES.length)
                    context.put(SecurityConstants.PARAM_MSG, SecurityConstants.MESSAGES[id]);

                // get the bad entered data and put it back for convenient update
                Group tempGroup = (Group)rundata.getUser().getTemp(TEMP_GROUP);
                if (tempGroup != null)
                    context.put(SecurityConstants.CONTEXT_GROUP, tempGroup);
            }
            context.put(SecurityConstants.PARAM_MODE, mode);

        }
        catch (Exception e)
        {
            logger.error("Error in Jetspeed User Security", e);
            rundata.setMessage("Error in Jetspeed User Security: " + e.toString());
            rundata.setStackTrace(StringUtils.stackTrace(e), e);
            rundata.setScreenTemplate(JetspeedResources.getString("template.error","Error"));
        }
    }

    /**
     * Database Insert Action for Security Groups. Performs inserts into security database.
     *
     * @param rundata The turbine rundata context for this request.
     * @param context The velocity context for this request.
     */
    public void doInsert(RunData rundata, Context context)
        throws Exception
    {
        Group group = null;
        try
        {
            //
            // validate that its not an 'blank' groupname -- not allowed
            //
            String name = rundata.getParameters().getString("name");
            if (name == null || name.trim().length() == 0)
            {
                DynamicURI duri = new DynamicURI (rundata);
                duri.addPathInfo(SecurityConstants.PANE_NAME, SecurityConstants.PANEID_GROUP_UPDATE);
                duri.addPathInfo(SecurityConstants.PARAM_MSGID, SecurityConstants.MID_INVALID_ENTITY_NAME);
                rundata.setRedirectURI(duri.toString());
                rundata.getUser().setTemp(TEMP_GROUP, null);
                return;
            }

            //
            // generate a new group
            //
            group = JetspeedGroupFactory.getInstance();
            group.setName(name);

            //
            // add the group
            ///
            JetspeedSecurity.addGroup(group);

        }
        catch (GroupException e)
        {
            // log the error msg
            logger.error("Exception", e);

            //
            // dup key found - display error message - bring back to same screen
            //
            DynamicURI duri = new DynamicURI (rundata);
            duri.addPathInfo(SecurityConstants.PANE_NAME, SecurityConstants.PANEID_GROUP_UPDATE);
            duri.addPathInfo(SecurityConstants.PARAM_MSGID, SecurityConstants.MID_ENTITY_ALREADY_EXISTS);
            rundata.setRedirectURI(duri.toString());

            // save values that user just entered so they don't have to re-enter
           if (group != null)
               rundata.getUser().setTemp(TEMP_GROUP, group);
        }
    }

    /**
     * Database Update Action for Security Groups. Performs updates into security database.
     *
     * @param rundata The turbine rundata context for this request.
     * @param context The velocity context for this request.
     */
    public void doUpdate(RunData rundata, Context context)
        throws Exception
    {
        Group group = null;
        try
        {
            //
            // get the group object from the selected group entry in the browser
            //
            group = JetspeedSecurity.getGroup(
                     rundata.getParameters().getString(SecurityConstants.PARAM_ENTITY_ID));

            //
            // update the group in the database
            //
            JetspeedSecurity.saveGroup(group);

        }
        catch (Exception e)
        {
           //  Log the error msg
            logger.error("Exception", e);

            //
            // error on update - display error message
            //
            DynamicURI duri = new DynamicURI (rundata);
            duri.addPathInfo(SecurityConstants.PANE_NAME, SecurityConstants.PANEID_GROUP_UPDATE);
            duri.addPathInfo(SecurityConstants.PARAM_MSGID, SecurityConstants.MID_UPDATE_FAILED);
            if (group != null)
                duri.addPathInfo(SecurityConstants.PARAM_ENTITY_ID, group.getName());
            duri.addQueryData(SecurityConstants.PARAM_MODE, SecurityConstants.PARAM_MODE_UPDATE);
            rundata.setRedirectURI(duri.toString());

           // save values that user just entered so they don't have to re-enter
           if (group != null)
               rundata.getUser().setTemp(TEMP_GROUP, group);

        }
    }

    /**
     * Database Delete Action for Security Groups. Performs deletes into security database.
     *
     * @param rundata The turbine rundata context for this request.
     * @param context The velocity context for this request.
     */
    public void doDelete(RunData rundata, Context context)
        throws Exception
    {
        Group group = null;

        try
        {
            //
            // get the group object from the selected group entry in the browser
            //
            group = JetspeedSecurity.getGroup(
                        rundata.getParameters().getString( SecurityConstants.PARAM_ENTITY_ID) );

            //
            // remove the group
            //
            JetspeedSecurity.removeGroup(group.getName());

        }
        catch (Exception e)
        {
           // log the error msg
            logger.error("Exception", e);

            //
            // error on delete - display error message
            //
            DynamicURI duri = new DynamicURI (rundata);
            duri.addPathInfo(SecurityConstants.PANE_NAME, SecurityConstants.PANEID_GROUP_UPDATE);
            duri.addPathInfo(SecurityConstants.PARAM_MSGID, SecurityConstants.MID_DELETE_FAILED);
            if (group != null)
                duri.addPathInfo(SecurityConstants.PARAM_ENTITY_ID, group.getName());
            duri.addQueryData(SecurityConstants.PARAM_MODE, SecurityConstants.PARAM_MODE_DELETE);
            rundata.setRedirectURI(duri.toString());

            // save values that user just entered so they don't have to re-enter
           if (group != null)
               rundata.getUser().setTemp(TEMP_GROUP, group);

        }

    }



}

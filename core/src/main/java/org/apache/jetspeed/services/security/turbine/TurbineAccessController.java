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

package org.apache.jetspeed.services.security.turbine;

// Java imports
import java.util.Iterator;

import javax.servlet.ServletConfig;

import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.om.registry.Security;
import org.apache.jetspeed.om.security.GroupRole;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletController;
import org.apache.jetspeed.portal.PortletSet;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.security.PortalAccessController;
import org.apache.jetspeed.services.security.PortalResource;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;

/**
 * TurbineAccessController
 *
 * @author <a href="paulsp@apache.org">Paul Spencer</a>
 * @version $Id: TurbineAccessController.java,v 1.8 2004/02/23 03:54:49 jford Exp $
 */
public class TurbineAccessController extends TurbineBaseService
implements PortalAccessController
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(TurbineAccessController.class.getName());
    
    private final static String CONFIG_DEFAULT_PERMISSION_LOGGEDIN     = "services.JetspeedSecurity.permission.default.loggedin";
    private final static String CONFIG_DEFAULT_PERMISSION_ANONYMOUS     = "services.JetspeedSecurity.permission.default.anonymous";
    
    /**
     * Given a <code>JetspeedUser</code>, authorize that user to perform the secured action on
     * the given <code>Portlet</code> resource. If the user does not have
     * sufficient privilege to perform the action on the resource, the check returns false,
     * otherwise when sufficient privilege is present, checkPermission returns true.
     *
     * @param user the user to be checked.
     * @param portlet the portlet resource.
     * @param action the secured action to be performed on the resource by the user.
     * @return boolean true if the user has sufficient privilege.
     */
    public boolean checkPermission(JetspeedUser user, Portlet portlet, String action)
    {
        return checkPermission(user, portlet, action, null);
    }
    
    /**
     * Given a <code>JetspeedUser</code>, authorize that user to perform the secured action on
     * the given <code>Portlet</code> resource. If the user does not have
     * sufficient privilege to perform the action on the resource, the check returns false,
     * otherwise when sufficient privilege is present, checkPermission returns true.
     *
     * @param user the user to be checked.
     * @param portlet the portlet resource.
     * @param action the secured action to be performed on the resource by the user.
     * @param owner of the entry, i.e. the username
     * @return boolean true if the user has sufficient privilege.
     */
    public boolean checkPermission(JetspeedUser user, Portlet portlet, String action, String owner)
    {
        String portletName = portlet.getName();
        RegistryEntry regEntry = (RegistryEntry)Registry.getEntry(Registry.PORTLET, portletName);
        //portlet is not a portlet - probably a controller or control
        if (regEntry==null)
        {
            PortletSet ps  = portlet.getPortletConfig().getPortletSet();
            if (ps != null)
            {
                PortletController pc = ps.getController();
                if (pc != null)
                {
                    portletName = pc.getConfig().getName();
                    regEntry = (RegistryEntry)Registry.getEntry(Registry.PORTLET_CONTROLLER, portletName);
                }
            }
        }
        if (regEntry==null)
        {
            return checkDefaultPermission(user, action);
        }
        return checkPermission(user, regEntry, action);
    }
    
    /**
     * Given a <code>JetspeedUser</code>, authorize that user to perform the secured action on
     * the given Portlet Instance (<code>Entry</code>) resource. If the user does not have
     * sufficient privilege to perform the action on the resource, the check returns false,
     * otherwise when sufficient privilege is present, checkPermission returns true.
     *
     * @param user the user to be checked.
     * @param entry the portlet instance resource.
     * @param action the secured action to be performed on the resource by the user.
     * @return boolean true if the user has sufficient privilege.
     */
    public boolean checkPermission(JetspeedUser user, Entry entry, String action)
    {
        return checkPermission(user, entry, action, null);
    }
    
    /**
     * Given a <code>JetspeedUser</code>, authorize that user to perform the secured action on
     * the given Portlet Instance (<code>Entry</code>) resource. If the user does not have
     * sufficient privilege to perform the action on the resource, the check returns false,
     * otherwise when sufficient privilege is present, checkPermission returns true.
     *
     * @param user the user to be checked.
     * @param entry the portlet instance resource.
     * @param action the secured action to be performed on the resource by the user.
     * @param owner of the entry, i.e. the username
     * @return boolean true if the user has sufficient privilege.
     */
    public boolean checkPermission(JetspeedUser user, Entry entry, String action, String owner)
    {
        String portletName = entry.getParent();
        RegistryEntry regEntry = (RegistryEntry)Registry.getEntry(Registry.PORTLET, portletName);
        if (regEntry==null)
        {
            return checkDefaultPermission(user, action);
        }
        return checkPermission(user, regEntry, action);
    }
    
    
    /**
     * Given a <code>JetspeedUser</code>, authorize that user to perform the secured action on
     * the given resource. If the user does not have
     * sufficient privilege to perform the action on the resource, the check returns false,
     * otherwise when sufficient privilege is present, checkPermission returns true.
     *
     * @param user the user to be checked.
     * @param resource requesting an action
     * @param action the secured action to be performed on the resource by the user.
     * @return boolean true if the user has sufficient privilege.
     */
    public boolean checkPermission(JetspeedUser user, PortalResource resource, String action)
    {
        switch (resource.getResourceType())
        {
            case PortalResource.TYPE_ENTRY:
                return checkPermission(user, resource.getEntry(), action);
            case PortalResource.TYPE_REGISTRY:
                return checkPermission(user, resource.getRegistryEntry(), action);
            case PortalResource.TYPE_REGISTRY_PARAMETER:
                return checkPermission(user, resource.getRegistryParameter(), action);
            case PortalResource.TYPE_PORTLET:
                return checkPermission(user, resource.getPortlet(), action);
            case PortalResource.TYPE_ENTRY_PARAMETER:
                return checkPermission(user, (RegistryEntry) resource.getEntryParameter(), action);
        }
        return false;
    }
    
    /**
     * Checks if the user has access to a given portlet for the given action
     *
     * @param user the requesting user.
     * @param regEntry the registry entry from the registry.
     * @param action the jetspeed-action (view, edit, customize, delete...) for which permission is being checked.
     * @exception Sends a RegistryException if the manager can't add
     *            the provided entry
     */
    private boolean checkPermission(JetspeedUser user,  RegistryEntry regEntry, String action)
    {
        Security security = regEntry.getSecurity();
        if (null == security)
            return checkDefaultPermission( user, action);
        String securityRole = security.getRole();
        if (null == securityRole)
            return checkDefaultPermission( user, action);

        
        // determine if Portlet has specified role
        try
        {

            if (false == JetspeedSecurity.hasRole(user.getUserName(), securityRole))
            {
                return false;
            }

        } catch (Exception e)
        {
            logger.error("Exception",  e);
            return false;
        }
        
        return checkPermission(user, action);
    }
    
    /**
     * Checks if the currently logged on user has access for the given action
     *
     * @param user the requesting user.
     * @param action the jetspeed-action (view, edit, customize, delete...) for which permission is being checked.
     * @exception Sends a RegistryException if the manager can't add
     *            the provided entry
     */
    /**
     * given the rundata, checks if the currently logged on user has access for the given action
     *
     * @param rundata the request rundata.
     * @param permission the jetspeed-action (view, edit, customize, delete...) for which permission is being checked.
     * @param entry the registry entry from the registry.
     * @exception Sends a RegistryException if the manager can't add
     *            the provided entry
     */
    private boolean checkPermission(JetspeedUser user, String action)
    {
        if (action == null)
        {
            return true; 
        }

        // determine if user has specified role
        try
        {
            Iterator roles = JetspeedSecurity.getRoles(user.getUserName());
            while (roles.hasNext())
            {
                GroupRole gr = (GroupRole) roles.next();
                Role role = gr.getRole();
                return JetspeedSecurity.hasPermission(role.getName(), action);
            }
        
        } catch (Exception e)
        {
            logger.error("Exception",  e);
            return false;
        }
        
        return true;
    }
    
    private boolean checkDefaultPermission(JetspeedUser user, String action)
    {
        String defaultPermissions[] = null;
        try
        {
            if ( (user == null) || !user.hasLoggedIn() )
            {
                defaultPermissions = JetspeedResources.getStringArray(CONFIG_DEFAULT_PERMISSION_ANONYMOUS);
            } else
            {
                defaultPermissions = JetspeedResources.getStringArray(CONFIG_DEFAULT_PERMISSION_LOGGEDIN);
            }
        } 
        catch (Exception e)
        {
            logger.error( "Error checking permissions for " + user + " on " + action, e);
        }
        for (int i = 0; i < defaultPermissions.length; i++)
        {
            if (defaultPermissions[i].equals("*"))
                return true;
            if (defaultPermissions[i].equals(action))
                return true;
        }
        return false;
    }
    
    /*
     * Turbine Services Interface
     */
    
    /**
     * This is the early initialization method called by the
     * Turbine <code>Service</code> framework
     * @param conf The <code>ServletConfig</code>
     * @exception throws a <code>InitializationException</code> if the service
     * fails to initialize
     */
    public synchronized void init(ServletConfig conf)
    throws InitializationException
    {
        if (getInit()) return;
        
        super.init(conf);
        
        setInit(true);
    }
    
}

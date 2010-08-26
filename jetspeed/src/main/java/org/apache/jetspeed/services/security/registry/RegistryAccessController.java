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

package org.apache.jetspeed.services.security.registry;

// Java imports
import java.util.Iterator;

import javax.servlet.ServletConfig;

import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.om.registry.SecurityEntry;
import org.apache.jetspeed.om.security.GroupRole;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletController;
import org.apache.jetspeed.portal.PortletSet;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.security.JetspeedRoleManagement;
import org.apache.jetspeed.services.security.PortalAccessController;
import org.apache.jetspeed.services.security.PortalResource;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;

/**
 * TurbineAccessController
 *
 * @author <a href="paulsp@apache.org">Paul Spencer</a>
 * @version $Id: RegistryAccessController.java,v 1.10 2004/02/23 03:54:03 jford Exp $
 */
public class RegistryAccessController extends TurbineBaseService implements PortalAccessController
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(RegistryAccessController.class.getName());
    
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
        SecurityReference securityRef = portlet.getPortletConfig().getSecurityRef();
        if (securityRef != null)
        {
            return checkPermission( user, securityRef, action, owner);
        }

        String portletName = portlet.getName();
        RegistryEntry registryEntry = null;
        // Don't query registry if portlet is a set
        if (!(portlet instanceof PortletSet))
        {
            registryEntry = (RegistryEntry) Registry.getEntry(Registry.PORTLET, portletName);
        }            
        //portlet is not a portlet - probably a controller or control
        if (registryEntry==null) {
            PortletSet ps  = portlet.getPortletConfig().getPortletSet();
            if (ps != null) {
                PortletController pc = ps.getController();
                if (pc != null) {
                    portletName = pc.getConfig().getName();
                    registryEntry = (RegistryEntry)Registry.getEntry(Registry.PORTLET_CONTROLLER, portletName);
                }
            }
        }
        if (registryEntry==null) {
            return true; // Since their is no entry, their no security to test.  Per spec. all is allowed
        }

        return checkPermission(user, registryEntry, action, owner);
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
        return checkPermission( user, entry, action, null);
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
        SecurityReference securityRef = entry.getSecurityRef();
        if (securityRef == null)
        {
            return checkPermission( user, Registry.getEntry( Registry.PORTLET, entry.getParent()), action, owner);
        }
        return checkPermission( user, securityRef, action, owner);
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
                return checkPermission(user, resource.getEntry(), action, resource.getOwner());
            case PortalResource.TYPE_PORTLET:
                return checkPermission(user, resource.getPortlet(), action, resource.getOwner());
            case PortalResource.TYPE_REGISTRY:
                return checkPermission(user, resource.getRegistryEntry(), action, resource.getOwner());
            case PortalResource.TYPE_REGISTRY_PARAMETER:
                return checkPermission(user, resource.getRegistryParameter(), action, resource.getOwner());
        }

        // We should never get here
        logger.error( "In " + this.getClass().getName() + ".checkPermission(user, resource, action) - Unkown resource = " + resource.getResourceType());
        return false;
    }
    
    /**
     * Checks if the user has access to a given registry entry for the given action
     *
     * @param user the requesting user.
     * @param regEntry the registry entry from the registry.
     * @param owner of the entry, i.e. the username
     * @param action the jetspeed-action (view, edit, customize, delete...) for which permission is being checked.
     */
    private boolean checkPermission(JetspeedUser user,  RegistryEntry regEntry, String action, String owner)
    {
        SecurityReference securityRef = regEntry.getSecurityRef();
        if (securityRef == null)
            return true;  // No security defined on Registry entry
        return checkPermission( user, securityRef, action, owner);
    }

    /**
     * Checks if the user has access for the given action using a security reference 
     *
     * @param user the requesting user.
     * @param securityRef the security reference to check
     * @param action the jetspeed-action (view, edit, customize, delete...) for which permission is being checked.
     */
    private boolean checkPermission(JetspeedUser user, SecurityReference securityRef, String action, String owner)
    {
        SecurityEntry securityEntry = (SecurityEntry) Registry.getEntry( Registry.SECURITY, securityRef.getParent());
        if (securityEntry == null)
        {
            logger.warn("Security id " + securityRef.getParent() + " does not exist.  This was requested by the user " + user.getUserName());
            return false;
        }

        if (securityEntry.allowsUser(user.getUserName(), action, owner))
        {
            return true;
        }

        try
        {
			for( Iterator roles = JetspeedRoleManagement.getRoles(user.getUserName()); roles.hasNext();)
			{
				GroupRole grouprole = (GroupRole) roles.next();
				String groupname = grouprole.getGroup().getName();
				String rolename = grouprole.getRole().getName();
				if (securityEntry.allowsGroupRole(groupname, rolename, action))
					return true;					
			}

//            for( Iterator roles = JetspeedRoleManagement.getRoles(user.getUserName()); roles.hasNext();)
//            {
//                Role role = (Role) roles.next();
//                if (securityEntry.allowsRole((String) role.getName(), action))
//                    return true;
//            }
//            
//			for( Iterator groups = JetspeedGroupManagement.getGroups(user.getUserName()); groups.hasNext();)
//			{
//				Group group = (Group) groups.next();
//				if (securityEntry.allowsGroup((String) group.getName(), action))
//					return true;
//			}
            
        }
        catch (Exception e)
        {
            logger.error("Exception", e);
            return false;
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

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

import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;

import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileException;
import org.apache.jetspeed.om.security.BaseJetspeedGroupRole;
import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.GroupRole;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.om.security.UserNamePrincipal;
import org.apache.jetspeed.om.security.turbine.TurbineRole;
import org.apache.jetspeed.om.security.turbine.TurbineRolePeer;
import org.apache.jetspeed.om.security.turbine.TurbineRolePermissionPeer;
import org.apache.jetspeed.om.security.turbine.TurbineUserGroupRole;
import org.apache.jetspeed.om.security.turbine.TurbineUserGroupRolePeer;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.PsmlManager;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.rundata.JetspeedRunDataService;
import org.apache.jetspeed.services.security.CachedAcl;
import org.apache.jetspeed.services.security.GroupManagement;
import org.apache.jetspeed.services.security.JetspeedSecurityCache;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.jetspeed.services.security.JetspeedSecurityService;
import org.apache.jetspeed.services.security.RoleException;
import org.apache.jetspeed.services.security.RoleManagement;
import org.apache.torque.Torque;
import org.apache.torque.om.NumberKey;
import org.apache.torque.util.Criteria;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.turbine.util.Log;

/**
 * Default Jetspeed-Turbine Role Management implementation
 *
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: TurbineRoleManagement.java,v 1.14 2004/02/23 03:54:49 jford Exp $
 */

public class TurbineRoleManagement extends TurbineBaseService
                                   implements RoleManagement
{
    private JetspeedRunDataService runDataService = null;
    private final static String CASCADE_DELETE = "programmatic.cascade.delete";
    private final static boolean DEFAULT_CASCADE_DELETE = true;
    private boolean cascadeDelete;
    private final static String CACHING_ENABLE = "caching.enable";
    private boolean cachingEnable = true;


    ///////////////////////////////////////////////////////////////////////////
    // Role Management Interfaces
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Retrieves all <code>Role</code>s for a given username principal.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param username a user principal identity to be retrieved.
     * @return Iterator over all roles associated to the user principal (iterator of GroupRole objects keyed on group+role).
     * @exception RoleException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public Iterator getRoles(String username)
        throws JetspeedSecurityException
    {
        JetspeedUser user = null;
        try
        {
            if (cachingEnable)
            {
                Iterator result = JetspeedSecurityCache.getRoles(username);
                if (null != result)
                {
                    return result;
                }
            }
            user = JetspeedSecurity.getUser(new UserNamePrincipal(username));
        }
        catch(JetspeedSecurityException e)
        {
            throw new RoleException("Failed to Retrieve User: ", e);
        }
        Criteria criteria = new Criteria();
        criteria.add(TurbineUserGroupRolePeer.USER_ID, user.getUserId());
        List rels;
        HashMap roles;

        try
        {
            rels = TurbineUserGroupRolePeer.doSelect(criteria);
            if (rels.size() > 0)
            {
                roles = new HashMap(rels.size());
            }
            else
            {
				roles = new HashMap();
            }

            for (int ix = 0; ix < rels.size(); ix++)
            {
				TurbineUserGroupRole rel = (TurbineUserGroupRole) rels.get(ix);
				Role role = rel.getTurbineRole();
				Group group = rel.getTurbineGroup();
				GroupRole groupRole = new BaseJetspeedGroupRole();
				groupRole.setGroup(group);
				groupRole.setRole(role);
				roles.put(group.getName() + role.getName(), groupRole);
            }
        }
        catch(Exception e)
        {
            throw new RoleException("Failed to retrieve roles ", e);
        }
        return roles.values().iterator();
    }

    /**
     * Retrieves all <code>Role</code>s.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @return Iterator over all roles.
     * @exception RoleException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public Iterator getRoles()
        throws JetspeedSecurityException
    {
        Criteria criteria = new Criteria();
        List roles;
        try
        {
            roles = TurbineRolePeer.doSelect(criteria);
        }
        catch(Exception e)
        {
            throw new RoleException("Failed to retrieve roles ", e);
        }
        return roles.iterator();
    }

    /**
     * Adds a <code>Role</code> into permanent storage.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception RoleException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public void addRole(Role role)
        throws JetspeedSecurityException
    {
        if(roleExists(role.getName()))
        {
            throw new RoleException("The role '" +
                role.getName() + "' already exists");
        }

        try
        {
            TurbineRole trole = new TurbineRole();
            trole.setRoleName(role.getName());
            Criteria criteria = TurbineRolePeer.buildCriteria(trole);
            NumberKey key = (NumberKey)TurbineRolePeer.doInsert(criteria);
            role.setId(key.toString());
        }
        catch(Exception e)
        {
            throw new RoleException("Failed to create role '" +
                role.getName() + "'", e);
        }

        if (cachingEnable)
        {
            JetspeedSecurityCache.addRole(role);
        }

        try
        {
            addDefaultRolePSML(role);
        }
        catch (Exception e)
        {
            try
            {
                removeRole(role.getName());
            }
            catch (Exception e2)
            {
            }
            throw new RoleException("failed to add default PSML for Role resource", e);
        }

    }

    protected void addDefaultRolePSML(Role role)
        throws RoleException
    {
        try
        {
            JetspeedRunDataService runDataService =
               (JetspeedRunDataService)TurbineServices.getInstance()
                   .getService(RunDataService.SERVICE_NAME);
            JetspeedRunData rundata = runDataService.getCurrentRunData();
            Profile profile = Profiler.createProfile();
            profile.setRole(role);
            profile.setMediaType("html");
            Profiler.createProfile(rundata, profile);
        }
        catch (ProfileException e)
        {
            try
            {
                removeRole(role.getName());
            }
            catch(Exception e2)
            {
            }
            throw new RoleException("Failed to create Role PSML", e);
        }
    }

    /**
     * Saves a <code>Role</code> into permanent storage.
     *
     * The security service can throw a <code>NotUniqueEntityException</code> when the public
     * credentials fail to meet the security provider-specific unique constraints.
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception RoleException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public void saveRole(Role role)
        throws JetspeedSecurityException
    {
        if(!roleExists(role.getName()))
        {
            throw new RoleException("The role '" +
                role.getName() + "' doesn't exists");
        }

        try
        {
            if (role instanceof TurbineRole)
            {
                TurbineRolePeer.doUpdate((TurbineRole)role);
            }
            else
            {
                throw new RoleException("TurbineRoleManagment: Role is not a Turbine role, cannot update");
            }

        }
        catch(Exception e)
        {
            throw new RoleException("Failed to create role '" +
                role.getName() + "'", e);
        }

    }

    /**
     * Removes a <code>Role</code> from the permanent store.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param rolename the principal identity of the role to be retrieved.
     * @exception RoleException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public void removeRole(String rolename)
        throws JetspeedSecurityException
    {
        Connection conn = null;
        try
        {
            conn = Torque.getConnection();
            Role role = this.getRole(rolename);

            Criteria criteria = new Criteria();
            criteria.add(TurbineRolePeer.ROLE_NAME, rolename);

            if(cascadeDelete)
            {
                // CASCADE TURBINE_USER_GROUP_ROLE, TURBINE_ROLE_PERMISSION
                Criteria criteria1 = new Criteria();
                criteria1.add(TurbineUserGroupRolePeer.ROLE_ID, role.getId());
                TurbineUserGroupRolePeer.doDelete(criteria1, conn);

                Criteria criteria2 = new Criteria();
                criteria2.add(TurbineRolePermissionPeer.ROLE_ID, role.getId());
                TurbineRolePermissionPeer.doDelete(criteria2, conn);
            }

            TurbineRolePeer.doDelete(criteria, conn);
            PsmlManager.removeRoleDocuments(role);

            conn.commit();

            if (cachingEnable)
            {
                JetspeedSecurityCache.removeAllRoles(rolename);
            }
        }
        catch(Exception e)
        {
            try
            {
                conn.rollback();
            }
            catch (java.sql.SQLException sqle)
            {
                Log.error(sqle);
            }
            throw new RoleException("Failed to remove role '" +
                rolename + "'", e);
        }
        finally
        {
            try
            {
                Torque.closeConnection(conn);
            }
            catch (Exception e){}
        }

    }

    /**
     * Grants a role to a user.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception RoleException when the security provider has a general failure retrieving users.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
	public void grantRole(String username, String rolename)
		throws JetspeedSecurityException
	{
		grantRole(username, rolename, GroupManagement.DEFAULT_GROUP_NAME);
	}
    public void grantRole(String username, String rolename, String groupname)
        throws JetspeedSecurityException
    {
        try
        {
            JetspeedUser user = JetspeedSecurity.getUser(username);
            Role role = this.getRole(rolename);
            Group group = JetspeedSecurity.getGroup(groupname);

            Criteria criteria = new Criteria();
            criteria.add(TurbineUserGroupRolePeer.USER_ID, user.getUserId());
            criteria.add(TurbineUserGroupRolePeer.GROUP_ID, group.getId());
            criteria.add(TurbineUserGroupRolePeer.ROLE_ID, role.getId());
            TurbineUserGroupRolePeer.doInsert(criteria);

            if (cachingEnable)
            {
                JetspeedSecurityCache.addRole(username, role, group);
            }
        }
        catch(Exception e)
        {
            throw new RoleException("Grant role '" + rolename + "' to user '" + username + "' failed: ", e);
        }
    }

    /**
     * Revokes a role from a user.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception RoleException when the security provider has a general failure retrieving users.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
	public void revokeRole(String username, String rolename)
		throws JetspeedSecurityException
	{
		revokeRole(username, rolename, GroupManagement.DEFAULT_GROUP_NAME);
	}

    public void revokeRole(String username, String rolename, String groupname)
        throws JetspeedSecurityException
    {
        try
        {
            JetspeedUser user = JetspeedSecurity.getUser(username);
            Role role = this.getRole(rolename);
            Group group = JetspeedSecurity.getGroup(groupname);

            Criteria criteria = new Criteria();
            criteria.add(TurbineUserGroupRolePeer.USER_ID, user.getUserId());
            criteria.add(TurbineUserGroupRolePeer.GROUP_ID, group.getId());
            criteria.add(TurbineUserGroupRolePeer.ROLE_ID, role.getId());
            TurbineUserGroupRolePeer.doDelete(criteria);

            if (cachingEnable)
            {
                JetspeedSecurityCache.removeRole(username, rolename, groupname);
            }

        }
        catch(Exception e)
        {
            throw new RoleException("Revoke role '" + rolename + "' to user '" + username + "' failed: ", e);
        }

    }

    /**
     * Checks for the relationship of user has a role. Returns true when the user has the given role.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception RoleException when the security provider has a general failure retrieving users.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
	public boolean hasRole(String username, String rolename)
		throws JetspeedSecurityException
	{
		return hasRole(username, rolename, GroupManagement.DEFAULT_GROUP_NAME);
	}
	
    public boolean hasRole(String username, String rolename, String groupname)
        throws JetspeedSecurityException
    {
        List roles;

        try
        {
            if (cachingEnable)
            {
                CachedAcl acl = JetspeedSecurityCache.getAcl(username);
                if (null != acl)
                {
                    return acl.hasRole(rolename, groupname);
                }
            }
            JetspeedUser user = JetspeedSecurity.getUser(username);
            Role role = this.getRole(rolename);
            Group group = JetspeedSecurity.getGroup(groupname);

            Criteria criteria = new Criteria();
            criteria.add(TurbineUserGroupRolePeer.USER_ID, user.getUserId());
            criteria.add(TurbineUserGroupRolePeer.GROUP_ID, group.getId());
            criteria.add(TurbineUserGroupRolePeer.ROLE_ID, role.getId());
            roles = TurbineUserGroupRolePeer.doSelect(criteria);

        }
        catch(Exception e)
        {
            throw new RoleException("Failed to check role '" +
                rolename + "'", e);
        }
        return ( roles.size() > 0 );
    }


    /**
     * Retrieves a single <code>Role</code> for a given rolename principal.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param rolename a role principal identity to be retrieved.
     * @return Role the role record retrieved.
     * @exception RoleException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public Role getRole(String rolename)
        throws JetspeedSecurityException
    {
        List roles;
        try
        {
            Criteria criteria = new Criteria();
            criteria.add(TurbineRolePeer.ROLE_NAME, rolename);
            roles = TurbineRolePeer.doSelect(criteria);
        }
        catch(Exception e)
        {
            throw new RoleException("Failed to retrieve role '" +
                rolename + "'", e);
        }
        if ( roles.size() > 1 )
        {
            throw new RoleException(
                "Multiple Roles with same rolename '" + rolename + "'");
        }
        if ( roles.size() == 1 )
        {
            TurbineRole role = (TurbineRole)roles.get(0);
            return role;
        }
        throw new RoleException("Unknown role '" + rolename + "'");

    }


    ///////////////////////////////////////////////////////////////////////////
    // Internal
    ///////////////////////////////////////////////////////////////////////////

    protected JetspeedRunData getRunData()
     {
         JetspeedRunData rundata = null;
         if (this.runDataService != null)
         {
             rundata = this.runDataService.getCurrentRunData();
         }
         return rundata;
     }

    /**
     * Check whether a specified role exists.
     *
     * The login name is used for looking up the account.
     *
     * @param roleName the name of the role to check for existence.
     * @return true if the specified account exists
     * @throws RoleException if there was a general db access error
     *
     */
    protected boolean roleExists(String roleName)
        throws RoleException
    {
        Criteria criteria = new Criteria();
        criteria.add(TurbineRolePeer.ROLE_NAME, roleName);
        List roles;
        try
        {
            roles = TurbineRolePeer.doSelect(criteria);
        }
        catch(Exception e)
        {
            throw new RoleException(
                "Failed to check account's presence", e);
        }
        if (roles.size() < 1)
        {
            return false;
        }
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Service Init
    ///////////////////////////////////////////////////////////////////////////


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

        // get configuration parameters from Jetspeed Resources
        ResourceService serviceConf = ((TurbineServices)TurbineServices.getInstance())
                                                     .getResources(JetspeedSecurityService.SERVICE_NAME);

        this.runDataService =
           (JetspeedRunDataService)TurbineServices.getInstance()
               .getService(RunDataService.SERVICE_NAME);

        cascadeDelete = serviceConf.getBoolean( CASCADE_DELETE, DEFAULT_CASCADE_DELETE );
        cachingEnable = serviceConf.getBoolean( CACHING_ENABLE, cachingEnable );

        setInit(true);
     }



}



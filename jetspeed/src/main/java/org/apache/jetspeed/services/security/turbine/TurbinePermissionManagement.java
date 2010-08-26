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
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.ServletConfig;


// Jetspeed Security
import org.apache.jetspeed.services.security.PermissionManagement;
import org.apache.jetspeed.services.security.JetspeedSecurityCache;

import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.om.security.Permission;

import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.security.JetspeedSecurityService;

// Jetspeed Security Exceptions
import org.apache.jetspeed.services.security.PermissionException;
import org.apache.jetspeed.services.security.JetspeedSecurityException;

// Jetspeed Database OM
import org.apache.jetspeed.om.security.turbine.TurbinePermission;
import org.apache.jetspeed.om.security.turbine.TurbinePermissionPeer;
import org.apache.jetspeed.om.security.turbine.TurbineRolePermission;
import org.apache.jetspeed.om.security.turbine.TurbineRolePermissionPeer;

// Jetspeed logging 
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

// Torque
import org.apache.torque.util.Criteria;
import org.apache.torque.om.NumberKey;
import org.apache.torque.Torque;

// Rundata
import org.apache.jetspeed.services.rundata.JetspeedRunDataService;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.services.rundata.RunDataService;

// Turbine
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.resources.ResourceService;

/**
 * Default Jetspeed-Turbine Permission Management implementation
 *
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id: TurbinePermissionManagement.java,v 1.10 2004/02/23 03:54:49 jford Exp $
 */

public class TurbinePermissionManagement extends TurbineBaseService
                                   implements PermissionManagement
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(TurbinePermissionManagement.class.getName());
    
    private JetspeedRunDataService runDataService = null;
    private final static String CASCADE_DELETE = "programmatic.cascade.delete";
    private final static boolean DEFAULT_CASCADE_DELETE = true;
    private final static String CONFIG_SYSTEM_PERMISSIONS = "system.permissions";
    private boolean cascadeDelete;
    private final static String CACHING_ENABLE = "caching.enable";
    private boolean cachingEnable = true;
    private Vector systemPermissions = null;

    ///////////////////////////////////////////////////////////////////////////
    // Permission Management Interfaces
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Retrieves all <code>Permission</code>s for a given rolename principal.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param rolename a role name identity to be retrieved.
     * @return Iterator over all permissions associated to the role principal.
     * @exception PermissionException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public Iterator getPermissions(String rolename)
        throws JetspeedSecurityException
    {
        Role role = null;
        try
        {
            if (cachingEnable)
            {
                Iterator iterator = JetspeedSecurityCache.getPermissions(rolename);
                if (iterator != null)
                {
                    return iterator;
                }
            }
            role = JetspeedSecurity.getRole(rolename);
        }
        catch(JetspeedSecurityException e)
        {
            logger.error( "Failed to Retrieve Role: ", e );
            throw new PermissionException("Failed to Retrieve Role: ", e);
        }
        Criteria criteria = new Criteria();
        criteria.add(TurbineRolePermissionPeer.ROLE_ID, role.getId());
        List rels;
        HashMap perms;

        try
        {
            rels = TurbineRolePermissionPeer.doSelect(criteria);
            if (rels.size() > 0)
            {
                perms = new HashMap(rels.size());
            }
            else
                perms = new HashMap();

            for (int ix = 0; ix < rels.size(); ix++)
            {
                TurbineRolePermission rel = (TurbineRolePermission)rels.get(ix);
                Permission perm = rel.getTurbinePermission();
                perms.put(perm.getName(), perm);
            }
        }
        catch(Exception e)
        {
            logger.error( "Failed to retrieve permissions ", e );
            throw new PermissionException("Failed to retrieve permissions ", e);
        }
        return perms.values().iterator();
    }

    /**
     * Retrieves all <code>Permission</code>s.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @return Iterator over all permissions.
     * @exception PermissionException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public Iterator getPermissions()
        throws JetspeedSecurityException
    {
        Criteria criteria = new Criteria();
        List permissions;
        try
        {
            permissions = TurbinePermissionPeer.doSelect(criteria);
        }
        catch(Exception e)
        {
            logger.error( "Failed to retrieve permissions ", e);
            throw new PermissionException("Failed to retrieve permissions ", e);
        }
        return permissions.iterator();
    }

    /**
     * Adds a <code>Permission</code> into permanent storage.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception PermissionException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public void addPermission(Permission permission)
        throws JetspeedSecurityException
    {
        if(permissionExists(permission.getName()))
        {
            throw new PermissionException("The permission '" +
                permission.getName() + "' already exists");
        }

        try
        {
            TurbinePermission tpermission = new TurbinePermission();
            tpermission.setPermissionName(permission.getName());
            Criteria criteria = TurbinePermissionPeer.buildCriteria(tpermission);
            NumberKey key = (NumberKey)TurbinePermissionPeer.doInsert(criteria);
            permission.setId(key.toString());
        }
        catch(Exception e)
        {
            String message = "Failed to create permission '" + permission.getName() + "'";
            logger.error( message, e );
            throw new PermissionException(message, e);
        }
    }


    /**
     * Saves a <code>Permission</code> into permanent storage.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception PermissionException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public void savePermission(Permission permission)
        throws JetspeedSecurityException
    {
        if(!permissionExists(permission.getName()))
        {
            throw new PermissionException("The permission '" +
                permission.getName() + "' doesn't exists");
        }

        try
        {
            if (permission instanceof TurbinePermission)
            {
                TurbinePermissionPeer.doUpdate((TurbinePermission)permission);
            }
            else
            {
                throw new PermissionException("TurbinePermissionManagment: Permission is not a Turbine permission, cannot update");
            }

        }
        catch(Exception e)
        {
            String message = "Failed to create permission '" + permission.getName() + "'";
            logger.error( message, e );
            throw new PermissionException( message, e );
        }

    }

    /**
     * Removes a <code>Permission</code> from the permanent store.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param permissionName the principal identity of the permission to be retrieved.
     * @exception PermissionException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public void removePermission(String permissionName)
        throws JetspeedSecurityException
    {
        Connection conn = null;
        try
        {

            if (systemPermissions.contains(permissionName))
            {
                throw new PermissionException("[" + permissionName + "] is a system permission and cannot be removed");
            }

            conn = Torque.getConnection();
            Permission permission = this.getPermission(permissionName);

            Criteria criteria = new Criteria();
            criteria.add(TurbinePermissionPeer.PERMISSION_NAME, permissionName);

            if(cascadeDelete)
            {
                // CASCADE to TURBINE_ROLE_PERMISSION
                Criteria critRolePerm = new Criteria();
                critRolePerm.add(TurbineRolePermissionPeer.PERMISSION_ID, permission.getId());
                TurbineRolePermissionPeer.doDelete(critRolePerm, conn);
            }
            TurbinePermissionPeer.doDelete(criteria, conn);
                
            conn.commit();
            
            if (cachingEnable)
            {
                JetspeedSecurityCache.removeAllPermissions(permissionName);
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
                logger.error("SQLException", sqle);
            }
            String message = "Failed to remove permission '" + permissionName + "'";
            logger.error( message, e );
            throw new PermissionException( message, e );
        }
        finally
        {
            try
            {
                Torque.closeConnection(conn);
            }
            catch (Throwable e)
            {
                logger.error( "Error closing Torque connection", e );
            }
        }

    }

    /**
     * Grants a permission to a role.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param roleName grant a permission to this role.
     * @param permissionName the permission to grant to the role.
     * @exception PermissionException when the security provider has a general failure retrieving permissions.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public void grantPermission(String roleName, String permissionName)
        throws JetspeedSecurityException
    {
        try
        {
            Role role = JetspeedSecurity.getRole(roleName);
            Permission permission = this.getPermission(permissionName);

            Criteria criteria = new Criteria();
            criteria.add(TurbineRolePermissionPeer.ROLE_ID, role.getId());
            criteria.add(TurbineRolePermissionPeer.PERMISSION_ID, permission.getId());
            TurbineRolePermissionPeer.doInsert(criteria);
            if (cachingEnable)
            {
                JetspeedSecurityCache.addPermission(roleName,permission);
            }
        }
        catch(Exception e)
        {
            String message = "Grant permission '" + permissionName + "' to role '" + roleName + "' failed: ";
            logger.error( message, e );
            throw new PermissionException( message, e );
        }
    }

    /**
     * Revokes a permission from a role.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param roleName grant a permission to this role.
     * @param permissionName the permission to grant to the role.
     * @exception PermissionException when the security provider has a general failure retrieving permissions.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public void revokePermission(String roleName, String permissionName)
        throws JetspeedSecurityException
    {
        try
        {
            Role role = JetspeedSecurity.getRole(roleName);
            Permission permission = this.getPermission(permissionName);

            Criteria criteria = new Criteria();
            criteria.add(TurbineRolePermissionPeer.ROLE_ID, role.getId());
            criteria.add(TurbineRolePermissionPeer.PERMISSION_ID, permission.getId());
            TurbineRolePermissionPeer.doDelete(criteria);
            if (cachingEnable)
            {
                JetspeedSecurityCache.removePermission(roleName, permissionName);
            }
        }
        catch(Exception e)
        {
            String message = "Revoke permission '" + permissionName + "' to role '" + roleName + "' failed: ";
            logger.error( message, e );
            throw new PermissionException( message, e);
        }

    }

    /**
     * Checks for the relationship of role has a permission. Returns true when the role has the given permission.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param roleName grant a permission to this role.
     * @param permissionName the permission to grant to the role.
     * @exception PermissionException when the security provider has a general failure retrieving permissions.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public boolean hasPermission(String roleName, String permissionName)
        throws JetspeedSecurityException
    {
        List permissions;

        try
        {
            if (cachingEnable)
            {
                return JetspeedSecurityCache.hasPermission(roleName, permissionName);
            }

            Role role = JetspeedSecurity.getRole(roleName);
            Permission permission = this.getPermission(permissionName);

            Criteria criteria = new Criteria();
            criteria.add(TurbineRolePermissionPeer.ROLE_ID, role.getId());
            criteria.add(TurbineRolePermissionPeer.PERMISSION_ID, permission.getId());
            permissions = TurbineRolePermissionPeer.doSelect(criteria);

        }
        catch(Exception e)
        {
            String message = "Failed to check permission '" + permissionName + "'";
            logger.error( message , e );
            throw new PermissionException( message, e );
        }
        return ( permissions.size() > 0 );
    }


    /**
     * Retrieves a single <code>Permission</code> for a given permissionName principal.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param permissionName a permission principal identity to be retrieved.
     * @return Permission the permission record retrieved.
     * @exception PermissionException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public Permission getPermission(String permissionName)
        throws JetspeedSecurityException
    {
        List permissions;

        try
        {
            Criteria criteria = new Criteria();
            criteria.add(TurbinePermissionPeer.PERMISSION_NAME, permissionName);
            permissions = TurbinePermissionPeer.doSelect(criteria);
        }
        catch(Exception e)
        {
            String message = "Failed to retrieve permission '" + permissionName + "'";
            logger.error( message, e );
            throw new PermissionException( message, e );
        }
        if ( permissions.size() > 1 )
        {
            throw new PermissionException(
                "Multiple Permissions with same permissionname '" + permissionName + "'");
        }
        if ( permissions.size() == 1 )
        {
            TurbinePermission permission = (TurbinePermission)permissions.get(0);
            return permission;
        }
        throw new PermissionException("Unknown permission '" + permissionName + "'");

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
     * Check whether a specified permission exists.
     *
     * The login name is used for looking up the account.
     *
     * @param permissionName the name of the permission to check for existence.
     * @return true if the specified account exists
     * @throws PermissionException if there was a general db access error
     *
     */
    protected boolean permissionExists(String permissionName)
        throws PermissionException
    {
        Criteria criteria = new Criteria();
        criteria.add(TurbinePermissionPeer.PERMISSION_NAME, permissionName);
        List permissions;
        try
        {
            permissions = TurbinePermissionPeer.doSelect(criteria);
        }
        catch(Exception e)
        {
            logger.error( "Failed to check account's presence", e );
            throw new PermissionException(
                "Failed to check account's presence", e);
        }
        if (permissions.size() < 1)
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
        systemPermissions = serviceConf.getVector( CONFIG_SYSTEM_PERMISSIONS, new Vector() );
        setInit(true);
     }



}




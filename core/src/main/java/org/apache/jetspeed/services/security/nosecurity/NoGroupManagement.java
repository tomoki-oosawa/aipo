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

package org.apache.jetspeed.services.security.nosecurity;

import java.util.Iterator;
import java.util.Vector;
import java.util.HashMap;

// Jetspeed Security
import org.apache.jetspeed.services.security.GroupManagement;

import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.BaseJetspeedGroup;

// Jetspeed Security Exceptions
import org.apache.jetspeed.services.security.JetspeedSecurityException;

// Turbine
import org.apache.turbine.services.TurbineBaseService;

/**
 * <p> The <code>NoGroupManagement</code> class is a Jetspeed
 * security provider, implementing the <code>GroupManagement</code> interface.
 * It provides no group management - no groups are listed, no groups are saved,
 * no users are in any groups, any request for a group is satisfied with a temporary Group object.
 *
 * @author <a href="mailto:ggolden@apache.org">Glenn R. Golden</a>
 * @version $Id: NoGroupManagement.java,v 1.3 2004/02/23 03:53:24 jford Exp $
 */
public class NoGroupManagement
    extends TurbineBaseService
    implements GroupManagement
{
    /**
     * Retrieves all <code>Group</code>s for a given username principal.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param username a user principal identity to be retrieved.
     * @return Iterator over all groups associated to the user principal.
     * @exception GroupException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public Iterator getGroups(String username)
        throws JetspeedSecurityException
    {
        return new Vector().iterator();
    }

    /**
     * Retrieves all <code>Group</code>s.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @return Iterator over all groups.
     * @exception GroupException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public Iterator getGroups()
        throws JetspeedSecurityException
    {
        return new Vector().iterator();
    }

    /**
     * Adds a <code>Group</code> into permanent storage.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception GroupException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public void addGroup(Group group)
        throws JetspeedSecurityException
    {
    }

    /**
     * Saves a <code>Group</code> into permanent storage.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception GroupException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public void saveGroup(Group group)
        throws JetspeedSecurityException
    {
    }

    /**
     * Removes a <code>Group</code> from the permanent store.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param groupname the principal identity of the group to be retrieved.
     * @exception GroupException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public void removeGroup(String groupname)
        throws JetspeedSecurityException
    {
    }

    /**
     * Joins a user to a group.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception GroupException when the security provider has a general failure retrieving groups.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public void joinGroup(String username, String groupname)
        throws JetspeedSecurityException
    {
    }

    /**
     * Join a user to a group - specific role.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception GroupException when the security provider has a general failure retrieving groups.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public void joinGroup(String username, String groupname, String rolename)
        throws JetspeedSecurityException
    {
    }

    /**
     * Unjoins a user from a group.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception GroupException when the security provider has a general failure retrieving groups.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public void unjoinGroup(String username, String groupname)
        throws JetspeedSecurityException
    {
    }

    /**
     * Unjoin a user from a group in which the user has a specific role instead of <Code>JetspeedSecurity.getRole(defaultRole)</Code>
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception GroupException when the security provider has a general failure retrieving groups.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */

    public void unjoinGroup(String username, String groupname, String rolename)
        throws JetspeedSecurityException
    {
    }


    /**
     * Checks for the relationship of user in a group. Returns true when the user is in the given group.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception GroupException when the security provider has a general failure retrieving groups.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public boolean inGroup(String username, String groupname)
        throws JetspeedSecurityException
    {
        return false;
    }

    /**
     * Retrieves a single <code>Group</code> for a given groupname principal.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param groupname a group principal identity to be retrieved.
     * @return Group the group record retrieved.
     * @exception GroupException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public Group getGroup(String groupname)
        throws JetspeedSecurityException
    {
        BaseJetspeedGroup r = new BaseJetspeedGroup();
        //r.setNew(false);
        r.setName(groupname);
        r.setId(groupname);
        return r;
    }

    /**
     * Retrieves a hashtable which associates Groups with Roles for a given username principal.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param username a user principal identity to be retrieved.
     * @return Hashtable record retrieved.
     * @exception GroupException when the security provider has a general failure.
     */
    public HashMap getTurbineGroupRole(String username)
        throws JetspeedSecurityException
    {
      HashMap h = new HashMap();
      return h;
    }
}


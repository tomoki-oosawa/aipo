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

package org.apache.jetspeed.services.security;

import java.util.Iterator;

import org.apache.jetspeed.om.security.Group;
import org.apache.turbine.services.TurbineServices;

/**
 * <p> The <code>GroupManagement</code> interface describes contract between
 * the portal and security provider required for Jetspeed Group Management.
 * This interface enables an application to be independent of the underlying
 * group management technology.
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id: JetspeedGroupManagement.java,v 1.5 2004/02/23 03:58:11 jford Exp $
 */

public abstract class JetspeedGroupManagement
{
    public String SERVICE_NAME = "GroupManagement";

    /*
     * Utility method for accessing the service
     * implementation
     *
     * @return a GroupManagement implementation instance
     */
    protected static GroupManagement getService()
    {
        return (GroupManagement)TurbineServices
        .getInstance().getService(GroupManagement.SERVICE_NAME);
    }

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
    public static Iterator getGroups(String username)
        throws JetspeedSecurityException
    {
        return getService().getGroups(username);
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
    public static Iterator getGroups()
        throws JetspeedSecurityException
    {
        return getService().getGroups();
    }

    /**
     * Adds a <code>Group</code> into permanent storage.
     *
     *
     * @exception GroupException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public static void addGroup(Group group)
        throws JetspeedSecurityException
    {
        getService().addGroup(group);
    }

    /**
     * Saves a <code>Group</code> into permanent storage.
     *
     *
     * @exception GroupException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public static void saveGroup(Group group)
        throws JetspeedSecurityException
    {
        getService().saveGroup(group);
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
    public static void removeGroup(String groupname)
        throws JetspeedSecurityException
    {
        getService().removeGroup(groupname);
    }

    /**
     * Join a user to a group.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception GroupException when the security provider has a general failure retrieving users.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public static void joinGroup(String username, String groupname)
        throws JetspeedSecurityException
    {
        getService().joinGroup(username,groupname);
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
    public static void joinGroup(String username, String groupname, String rolename)
        throws JetspeedSecurityException
    {
        getService().joinGroup(username,groupname, rolename);
    }


    /**
     * Unjoin a user from a group.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception GroupException when the security provider has a general failure retrieving users.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public static void unjoinGroup(String username, String groupname)
        throws JetspeedSecurityException
    {
        getService().unjoinGroup(username,groupname);
    }

    /**
     * Unjoin a user from a group - specific role.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception GroupException when the security provider has a general failure retrieving users.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public static void unjoinGroup(String username, String groupname, String rolename)
        throws JetspeedSecurityException
    {
        getService().unjoinGroup(username,groupname,rolename);
    }


    /**
     * Checks for the relationship of user has a group. Returns true when the user has the given group.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception GroupException when the security provider has a general failure retrieving users.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public static boolean inGroup(String username, String groupname)
        throws JetspeedSecurityException
    {
        return getService().inGroup(username,groupname);
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
    public static Group getGroup(String groupname)
        throws JetspeedSecurityException
    {
        return getService().getGroup(groupname);
    }
}












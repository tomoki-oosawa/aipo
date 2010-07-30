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

package org.apache.jetspeed.services.security.ldap;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletConfig;

import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileException;
import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.UserNamePrincipal;
import org.apache.jetspeed.om.security.ldap.LDAPGroup;
import org.apache.jetspeed.om.security.ldap.LDAPRole;
import org.apache.jetspeed.om.security.ldap.LDAPUser;
import org.apache.jetspeed.services.JetspeedLDAP;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.PsmlManager;
import org.apache.jetspeed.services.ldap.LDAPURL;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.rundata.JetspeedRunDataService;
import org.apache.jetspeed.services.security.GroupException;
import org.apache.jetspeed.services.security.GroupManagement;
import org.apache.jetspeed.services.security.InsufficientPrivilegeException;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.jetspeed.services.security.JetspeedSecurityService;
import org.apache.jetspeed.services.security.UnknownUserException;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.rundata.RunDataService;

/**
 * 
 * @author <a href="mailto:ender@kilicoglu.nom.tr">Ender KILICOGLU</a>
 * @author <a href="mailto:sami.leino@netorek.fi">Sami Leino</a>
 * 
 */
public class LDAPGroupManagement extends TurbineBaseService implements
    GroupManagement {
  // Constants
  private final static String CONFIG_DEFAULT_ROLE = "role.default";

  private final static String CASCADE_DELETE = "programmatic.cascade.delete";

  private final static String DEFAULT_DEFAULT_ROLE = "user";

  private final static boolean DEFAULT_CASCADE_DELETE = true;

  private final static String[] ATTRS = { "ou", "uid", "groupname" };

  // Instance variables
  private JetspeedRunDataService runDataService = null;

  private boolean cascadeDelete = false;

  private String defaultRole = null;

  // /////////////////////////////////////////////////////////////////////////
  // Group Management Interfaces
  // /////////////////////////////////////////////////////////////////////////

  /**
   * Retrieves all <code>Group</code>s for a given username principal.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @param username
   *            a user principal identity to be retrieved.
   * @return Iterator over all groups associated to the user principal.
   * @exception GroupException
   *                when the security provider has a general failure.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public Iterator getGroups(String username) throws JetspeedSecurityException {
    Vector groups = new Vector();
    StringTokenizer st;
    LDAPUser user;

    try {
      user = (LDAPUser) JetspeedSecurity
          .getUser(new UserNamePrincipal(username));
    } catch (JetspeedSecurityException e) {
      throw new GroupException("Failed to Retrieve User: ", e);
    }

    try {
      for (Enumeration enu = user.getGroupRoles().elements(); enu
          .hasMoreElements();) {
        st = new StringTokenizer((String) enu.nextElement(), ",");
        groups.add(new LDAPGroup(st.nextToken(), false));
      }
    } catch (Exception e) {
      throw new GroupException("Failed to retrieve groups ", e);
    }

    return groups.iterator();
  }

  /**
   * Retrieves all <code>Group</code>s.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @return Iterator over all groups.
   * @exception GroupException
   *                when the security provider has a general failure.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public Iterator getGroups() throws JetspeedSecurityException {
    // BasicAttributes attr = new BasicAttributes();
    Vector groups = new Vector();
    Vector groupurls;

    try {
      groupurls = JetspeedLDAP.search(JetspeedLDAP.buildURL("ou=groups"),
          "(objectclass=jetspeedgroup)", ATTRS, true);
      if (groupurls.size() > 0) {
        for (Enumeration enu = groupurls.elements(); enu.hasMoreElements();) {
          groups.add(new LDAPGroup((LDAPURL) (((Vector) enu.nextElement())
              .firstElement())));
        }
      } else {
        throw new UnknownUserException("No groups");
      }
    } catch (Exception e) {
      throw new GroupException("Failed to retrieve groups ", e);
    }

    return groups.iterator();
  }

  /**
   * Adds a <code>Group</code> into permanent storage.
   * 
   * The security service can throw a <code>NotUniqueEntityException</code>
   * when the public credentials fail to meet the security provider-specific
   * unique constraints. The security service may optionally check the current
   * user context to determine if the requestor has permission to perform this
   * action.
   * 
   * @exception GroupException
   *                when the security provider has a general failure.
   * @exception NotUniqueEntityException
   *                when the public credentials fail to meet the security
   *                provider-specific unique constraints.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   * 
   */
  public void addGroup(Group group) throws JetspeedSecurityException {
    LDAPGroup ldapGroup = null;

    if (groupExists(group.getName())) {
      throw new GroupException("The group '" + group.getName()
          + "' already exists");
    }
    try {
      ldapGroup = new LDAPGroup(group.getName(), true);
      ldapGroup.update(true);
    } catch (Exception e) {
      throw new GroupException("Failed to create group '" + group.getName()
          + "'", e);
    }

    try {
      addDefaultGroupPSML(ldapGroup);
    } catch (Exception e) {
      try {
        removeGroup(ldapGroup.getName());
      } catch (Exception e2) {
      }
      throw new GroupException("failed to add default PSML for Group resource",
          e);
    }

  }

  protected void addDefaultGroupPSML(Group group) throws GroupException {
    try {
      JetspeedRunDataService runDataService = (JetspeedRunDataService) TurbineServices
          .getInstance().getService(RunDataService.SERVICE_NAME);

      JetspeedRunData rundata = runDataService.getCurrentRunData();
      Profile profile = Profiler.createProfile();
      profile.setGroup(group);
      profile.setMediaType("html");
      Profiler.createProfile(rundata, profile);
    } catch (ProfileException e) {
      try {
        removeGroup(group.getName());
      } catch (Exception e2) {
      }
      throw new GroupException("Failed to create Group PSML", e);
    }
  }

  /**
   * Saves a <code>Group</code> into permanent storage.
   * 
   * The security service can throw a <code>NotUniqueEntityException</code>
   * when the public credentials fail to meet the security provider-specific
   * unique constraints. The security service may optionally check the current
   * user context to determine if the requestor has permission to perform this
   * action.
   * 
   * @exception GroupException
   *                when the security provider has a general failure.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void saveGroup(Group group) throws JetspeedSecurityException {
    if (!groupExists(group.getName())) {
      throw new GroupException("The group '" + group.getName()
          + "' doesn't exists");
    }

    try {
    } catch (Exception e) {
      throw new GroupException("Failed to create group '" + group.getName()
          + "'", e);
    }
  }

  /**
   * Removes a <code>Group</code> from the permanent store.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @param groupname
   *            the principal identity of the group to be retrieved.
   * @exception GroupException
   *                when the security provider has a general failure.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void removeGroup(String groupname) throws JetspeedSecurityException {
    try {
      LDAPGroup group = new LDAPGroup(groupname, false);
      JetspeedLDAP.deleteEntry(group.getldapurl());

      if (cascadeDelete) {
      }

      PsmlManager.removeGroupDocuments(group);
    } catch (Exception e) {
      throw new GroupException("Failed to remove group '" + groupname + "'", e);
    }
  }

  /**
   * Join a user to a group.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @exception GroupException
   *                when the security provider has a general failure retrieving
   *                users.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void joinGroup(String username, String groupname)
      throws JetspeedSecurityException {
    LDAPUser user;
    try {
      user = (LDAPUser) JetspeedSecurity
          .getUser(new UserNamePrincipal(username));
    } catch (JetspeedSecurityException e) {
      throw new GroupException("Failed to Retrieve User: ", e);
    }
    try {
      user.addGroupRole(groupname, defaultRole);
      user.update(false);
    } catch (Exception e) {
      throw new GroupException("Failed to add group info ", e);
    }
  }

  /**
   * Join a user to a group - specific role.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @exception GroupException
   *                when the security provider has a general failure retrieving
   *                groups.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void joinGroup(String username, String groupname, String rolename)
      throws JetspeedSecurityException {
    LDAPUser user;
    LDAPRole role;

    try {
      user = (LDAPUser) JetspeedSecurity
          .getUser(new UserNamePrincipal(username));
      role = (LDAPRole) JetspeedSecurity.getRole(rolename);
    } catch (JetspeedSecurityException e) {
      throw new GroupException("Failed to Retrieve User: ", e);
    }
    try {
      user.addGroupRole(groupname, rolename);
      user.update(false);
    } catch (Exception e) {
      throw new GroupException("Failed to add group info ", e);
    }
  }

  /**
   * Unjoin a user from a group.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @exception GroupException
   *                when the security provider has a general failure retrieving
   *                users.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void unjoinGroup(String username, String groupName)
      throws JetspeedSecurityException {
    LDAPUser user;

    try {
      user = (LDAPUser) JetspeedSecurity
          .getUser(new UserNamePrincipal(username));
    } catch (JetspeedSecurityException e) {
      throw new GroupException("Failed to Retrieve User: ", e);
    }

    try {
      user.removeGroup(groupName);
      user.update(false);
    } catch (Exception e) {
      throw new GroupException("Failed to add group info ", e);
    }
  }

  /**
   * Unjoin a user from a group in which the user has a specific role.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @exception GroupException
   *                when the security provider has a general failure retrieving
   *                users.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void unjoinGroup(String username, String groupName, String rolename)
      throws JetspeedSecurityException {
    LDAPUser user;

    try {
      user = (LDAPUser) JetspeedSecurity
          .getUser(new UserNamePrincipal(username));
    } catch (JetspeedSecurityException e) {
      throw new GroupException("Failed to Retrieve User: ", e);
    }

    try {
      user.removeGroup(groupName);
      user.update(false);
    } catch (Exception e) {
      throw new GroupException("Failed to add group info ", e);
    }
  }

  /**
   * Checks for the relationship of user in a group. Returns true when the user
   * is in the given group.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @exception GroupException
   *                when the security provider has a general failure retrieving
   *                users.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public boolean inGroup(String username, String groupname)
      throws JetspeedSecurityException {
    // Vector groups = new Vector();
    StringTokenizer st;
    LDAPUser user;

    try {
      user = (LDAPUser) JetspeedSecurity
          .getUser(new UserNamePrincipal(username));
    } catch (JetspeedSecurityException e) {
      throw new GroupException("Failed to Retrieve User: ", e);
    }

    try {
      for (Enumeration enu = user.getGroupRoles().elements(); enu
          .hasMoreElements();) {
        st = new StringTokenizer((String) enu.nextElement(), ",");

        if (st.nextToken().equalsIgnoreCase(groupname)) {
          return true;
        }
      }
    } catch (Exception e) {
      throw new GroupException("Failed to retrieve groups ", e);
    }

    return false;
  }

  /**
   * Retrieves a single <code>Group</code> for a given groupname principal.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @param groupname
   *            a group principal identity to be retrieved.
   * 
   * @return Group the group record retrieved.
   * 
   * @exception GroupException
   *                when the security provider has a general failure.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   * 
   */
  public Group getGroup(String groupname) throws JetspeedSecurityException {
    // BasicAttributes attr = new BasicAttributes();
    LDAPGroup group;
    Vector groupurls;

    try {
      groupurls = JetspeedLDAP.search(JetspeedLDAP.buildURL("ou=groups"),
          "(&(uid=" + groupname + ")(objectclass=jetspeedgroup))", ATTRS, true);
      if (groupurls.size() == 1) {
        return new LDAPGroup((LDAPURL) ((Vector) groupurls.elementAt(0))
            .firstElement());
      } else if (groupurls.size() > 1) {
        throw new GroupException("Multiple groups with same name");
      } else {
        throw new GroupException("Unknown group '" + groupname + "'");
      }
    } catch (Exception e) {
      throw new GroupException("Failed to retrieve groups ", e);
    }
  }

  /**
   * Retrieves a hashtable which associates Groups with Roles for a given
   * username principal.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @param username
   *            a user principal identity to be retrieved.
   * @return Hashtable record retrieved.
   * @exception GroupException
   *                when the security provider has a general failure.
   */
  public HashMap getTurbineGroupRole(String username)
      throws JetspeedSecurityException {
    LDAPGroup group;
    Vector groupurls;
    HashMap h = new HashMap();
    return h;
  }

  // /////////////////////////////////////////////////////////////////////////
  // Internal
  // /////////////////////////////////////////////////////////////////////////

  protected JetspeedRunData getRunData() {
    JetspeedRunData rundata = null;

    if (this.runDataService != null) {
      rundata = this.runDataService.getCurrentRunData();
    }

    return rundata;
  }

  /**
   * Check whether a specified group exists.
   * 
   * The login name is used for looking up the account.
   * 
   * @param groupName
   *            the name of the group to check for existence.
   * @return true if the specified account exists
   * @throws GroupException
   *             if there was a general db access error
   * 
   */
  protected boolean groupExists(String groupName) throws GroupException {
    // BasicAttributes attr = new BasicAttributes();
    Vector groupurls;

    try {
      groupurls = JetspeedLDAP.search(JetspeedLDAP.buildURL("ou=groups"),
          "(&(uid=" + groupName + ")(objectclass=jetspeedgroup))", ATTRS, true);
      if (groupurls.size() > 0) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      throw new GroupException("Failed to retrieve groups ", e);
    }
  }

  // /////////////////////////////////////////////////////////////////////////
  // Service Init
  // /////////////////////////////////////////////////////////////////////////

  /**
   * This is the early initialization method called by the Turbine
   * <code>Service</code> framework
   * 
   * @param conf
   *            The <code>ServletConfig</code>
   * @exception throws
   *                a <code>InitializationException</code> if the service
   *                fails to initialize
   */
  public synchronized void init(ServletConfig conf)
      throws InitializationException {
    if (getInit())
      return;
    super.init(conf);

    // get configuration parameters from Jetspeed Resources
    ResourceService serviceConf = ((TurbineServices) TurbineServices
        .getInstance()).getResources(JetspeedSecurityService.SERVICE_NAME);

    this.runDataService = (JetspeedRunDataService) TurbineServices
        .getInstance().getService(RunDataService.SERVICE_NAME);

    defaultRole = serviceConf.getString(CONFIG_DEFAULT_ROLE,
        DEFAULT_DEFAULT_ROLE);
    cascadeDelete = serviceConf.getBoolean(CASCADE_DELETE,
        DEFAULT_CASCADE_DELETE);

    setInit(true);
  }

}

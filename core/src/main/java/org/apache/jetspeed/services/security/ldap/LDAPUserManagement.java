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

package org.apache.jetspeed.services.security.ldap;

import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;
import javax.servlet.ServletConfig;

import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.security.UserNamePrincipal;
import org.apache.jetspeed.om.security.ldap.LDAPUser;
import org.apache.jetspeed.services.JetspeedLDAP;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.PsmlManager;
import org.apache.jetspeed.services.ldap.LDAPURL;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.rundata.JetspeedRunDataService;
import org.apache.jetspeed.services.security.CredentialsManagement;
import org.apache.jetspeed.services.security.InsufficientPrivilegeException;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.jetspeed.services.security.JetspeedSecurityService;
import org.apache.jetspeed.services.security.NotUniqueUserException;
import org.apache.jetspeed.services.security.UnknownUserException;
import org.apache.jetspeed.services.security.UserException;
import org.apache.jetspeed.services.security.UserManagement;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.turbine.util.RunData;

/**
 * 
 * @author <a href="mailto:ender@kilicoglu.nom.tr">Ender KILICOGLU </a>
 * @author <a href="mailto:sami.leino@netorek.fi">Sami Leino </a>
 * 
 */
public class LDAPUserManagement extends TurbineBaseService implements
    UserManagement, CredentialsManagement {
  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(LDAPUserManagement.class.getName());

  // Constants
  private final static String CONFIG_SECURE_PASSWORDS_KEY = "secure.passwords";

  private final static String CONFIG_SECURE_PASSWORDS_ALGORITHM = "secure.passwords.algorithm";

  private final static String CONFIG_SECURE_PASSWORDS_SUFFIX = "secure.passwords.suffix";

  private final static String CONFIG_NEWUSER_ROLES = "newuser.roles";

  private final static String[] DEFAULT_CONFIG_NEWUSER_ROLES = { "user" };

  private final static String[] ATTRS = { "ou", "userPassword", "uid", "mail",
      "sn", "givenName", "uidNumber", "name", "objectdata", "objectClass",
      "usergrouprole", "lastlogindate", "lastmodifieddate", "creationdate",
      "confirm", "disabled" };

  // Class variables
  protected static boolean securePasswords = false;

  protected static String passwordsAlgorithm = "crypt";

  protected static String passwordsSuffix = "{crypt}";

  // Instance variables
  protected JetspeedRunDataService runDataService = null;

  protected String roles[] = null;

  // /////////////////////////////////////////////////////////////////////////
  // User Management Interfaces

  // /////////////////////////////////////////////////////////////////////////

  /**
   * Retrieves a <code>JetspeedUser</code> given the primary principle. The
   * principal can be any valid Jetspeed Security Principal:
   * <code>org.apache.jetspeed.om.security.UserNamePrincipal</code>
   *   <code>org.apache.jetspeed.om.security.UserIdPrincipal</code>
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @param principal
   *            a principal identity to be retrieved.
   * 
   * @return a <code>JetspeedUser</code> associated to the principal identity.
   * @exception UserException
   *                when the security provider has a general failure retrieving
   *                a user.
   * @exception UnknownUserException
   *                when the security provider cannot match the principal
   *                identity to a user.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public JetspeedUser getUser(Principal principal)
      throws JetspeedSecurityException {
    // BasicAttributes attr = new BasicAttributes();
    Vector userurls;
    LDAPUser user = null;

    try {
      userurls = JetspeedLDAP.search(JetspeedLDAP.buildURL("ou=users"),
          "(&(uid=" + principal.getName() + ")(objectclass=jetspeeduser))",
          ATTRS, true);
    } catch (Exception e) {
      logger.error("Failed to retrieve user '" + principal.getName() + "'", e);
      throw new UserException("Failed to retrieve user '" + principal.getName()
          + "'", e);
    }
    if (userurls != null) {
      if (userurls.size() == 1) {
        user = new LDAPUser((LDAPURL) ((Vector) userurls.elementAt(0))
            .firstElement());
        return user;
      } else if (userurls.size() > 1) {
        throw new UserException("Multiple Users with same username '"
            + principal.getName() + "'");
      } else {
        throw new UnknownUserException("Unknown user '" + principal.getName()
            + "'");
      }
    } else {
      throw new UnknownUserException("Unknown user '" + principal.getName()
          + "'");
    }
  }

  public JetspeedUser getUser(RunData rundata, Principal principal)
      throws JetspeedSecurityException {
    return null;
  }

  /**
   * Retrieves a collection of all <code>JetspeedUser</code>s. The security
   * service may optionally check the current user context to determine if the
   * requestor has permission to perform this action.
   * 
   * @return a collection of <code>JetspeedUser</code> entities.
   * @exception UserException
   *                when the security provider has a general failure retrieving
   *                users.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public Iterator getUsers() throws JetspeedSecurityException {
    String filter = "(objectclass=jetspeeduser)";
    return getUsersUsingLDAPSpecificFilter(filter, null);
  }

  /**
   * Retrieves a collection of <code>JetspeedUser</code> s filtered by a
   * security provider-specific query string. For example SQL, OQL, JDOQL. The
   * security service may optionally check the current user context to determine
   * if the requestor has permission to perform this action.
   * 
   * @return a collection of <code>JetspeedUser</code> entities.
   * @exception UserException
   *                when the security provider has a general failure retrieving
   *                users.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   * 
   */
  public Iterator getUsers(String filter) throws JetspeedSecurityException {
    // String ldapFilter = convert(filter);
    return getUsersUsingLDAPSpecificFilter(filter, null);
  }

  /**
   * Retrieves a collection of <code>JetspeedUser</code> s filtered by a
   * security provider-specific query string. For example SQL, OQL, JDOQL. The
   * security service may optionally check the current user context to determine
   * if the requestor has permission to perform this action.
   * 
   * @return a collection of <code>JetspeedUser</code> entities.
   * @exception UserException
   *                when the security provider has a general failure retrieving
   *                users.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  protected Iterator getUsersUsingLDAPSpecificFilter(String filter,
      String[] attributesToFetch) throws JetspeedSecurityException {
    String baseDN = "ou=users";
    NamingEnumeration userEnum = null;
    List resultList = new Vector(1024);

    try {
      LDAPURL url = JetspeedLDAP.buildURL(baseDN);
      DirContext ctx = JetspeedLDAP.getService().connect(url);
      userEnum = JetspeedLDAP.search(ctx, url.getDN(), filter,
          attributesToFetch, JetspeedLDAP.getService().SUB);

      while (userEnum.hasMoreElements()) {
        LDAPUser user = buildUser(((SearchResult) userEnum.nextElement())
            .getAttributes());
        resultList.add(user);
      }

      JetspeedLDAP.getService().checkAndCloseContext(ctx);
    } catch (Exception e) {
      logger.error("Failed to retrieve user with filter:" + filter, e);
      throw new UserException("Failed to retrieve user with filter:" + filter,
          e);
    }

    return (resultList.iterator());
  }

  protected LDAPUser buildUser(Attributes attributes) {
    return new LDAPUser(attributes);
  }

  /**
   * Saves a <code>JetspeedUser</code>'s attributes into permanent storage.
   * The user's account is required to exist in the storage. The security
   * service may optionally check the current user context to determine if the
   * requestor has permission to perform this action.
   * 
   * @exception UserException
   *                when the security provider has a general failure retrieving
   *                users.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void saveUser(JetspeedUser user) throws JetspeedSecurityException {
    if (!accountExists(user, true)) {
      throw new UnknownUserException("Cannot save user '" + user.getUserName()
          + "', User doesn't exist");
    }
    try {
      ((LDAPUser) user).update(false);
    } catch (Exception e) {
      logger.error("Failed to save user object ", e);
      throw new UserException("Failed to save user object ", e);
    }
  }

  /**
   * Adds a <code>JetspeedUser</code> into permanent storage. The security
   * service can throw a <code>NotUniqueUserException</code> when the public
   * credentials fail to meet the security provider-specific unique constraints.
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @exception UserException
   *                when the security provider has a general failure retrieving
   *                users.
   * @exception NotUniqueUserException
   *                when the public credentials fail to meet the security
   *                provider-specific unique constraints.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void addUser(JetspeedUser user) throws JetspeedSecurityException {
    if (accountExists(user)) {
      throw new NotUniqueUserException("The account '" + user.getUserName()
          + "' already exists");
    }

    String initialPassword = user.getPassword();
    String encrypted = JetspeedSecurity.encryptPassword(initialPassword);
    user.setPassword(encrypted);
    ((LDAPUser) user).update(true);

    addDefaultPSML(user);
  }

  /*
   * A default PSML page is added for the user, and the Jetspeed default roles
   * are assigned to the new user.
   * 
   * @param user The new user. @throws
   */
  protected void addDefaultPSML(JetspeedUser user)
      throws JetspeedSecurityException {
    for (int ix = 0; ix < roles.length; ix++) {
      try {

        JetspeedSecurity.grantRole(user.getUserName(), JetspeedSecurity
            .getRole(roles[ix]).getName());
      } catch (Exception e) {
        logger.error("Could not grant role: " + roles[ix] + " to user "
            + user.getUserName(), e);
      }
    }
    try {
      JetspeedRunData rundata = getRunData();
      if (rundata != null) {
        Profile profile = Profiler.createProfile();
        profile.setUser(user);
        profile.setMediaType("html");
        Profiler.createProfile(getRunData(), profile);
      }
    } catch (Exception e) {
      logger.error("Failed to create profile for new user ", e);
      removeUser(new UserNamePrincipal(user.getUserName()));
      throw new UserException("Failed to create profile for new user ", e);
    }
  }

  /**
   * Removes a <code>JetspeedUser</code> from the permanent store. The
   * security service may optionally check the current user context to determine
   * if the requestor has permission to perform this action.
   * 
   * @param principal
   *            the principal identity to be retrieved.
   * @exception UserException
   *                when the security provider has a general failure retrieving
   *                a user.
   * @exception UnknownUserException
   *                when the security provider cannot match the principal
   *                identity to a user.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void removeUser(Principal principal) throws JetspeedSecurityException {
    // BasicAttributes attr = new BasicAttributes();
    // Vector userurls = new Vector();
    LDAPUser user = (LDAPUser) getUser(principal);

    try {
      JetspeedLDAP.deleteEntry(user.getldapurl());
      PsmlManager.removeUserDocuments(user);
    } catch (Exception e) {
      logger.error("Failed to remove account '" + user.getUserName() + "'", e);
      throw new UserException("Failed to remove account '" + user.getUserName()
          + "'", e);
    }

  }

  // /////////////////////////////////////////////////////////////////////////
  // Credentials Management
  // /////////////////////////////////////////////////////////////////////////

  /**
   * Allows for a user to change their own password.
   * 
   * @param user
   *            the JetspeedUser to change password
   * @param oldPassword
   *            the current password supplied by the user.
   * @param newPassword
   *            the current password requested by the user.
   * @exception UserException
   *                when the security provider has a general failure retrieving
   *                a user.
   * @exception UnknownUserException
   *                when the security provider cannot match the principal
   *                identity to a user.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void changePassword(JetspeedUser user, String oldPassword,
      String newPassword) throws JetspeedSecurityException {
    oldPassword = JetspeedSecurity.convertPassword(oldPassword);
    newPassword = JetspeedSecurity.convertPassword(newPassword);

    if (!accountExists(user)) {
      throw new UnknownUserException("The account '" + user.getUserName()
          + "' does not exist");
    } else if (!passwordsMatch(user, oldPassword)) {
      throw new UserException("The supplied old password for '"
          + user.getUserName() + "' was incorrect");
    }

    String encrypted = JetspeedSecurity.encryptPassword(newPassword);
    user.setPassword(encrypted);

    // save the changes in the database immediately, to prevent the password
    // being 'reverted' to the old value if the user data is lost somehow
    // before it is saved at session's expiry.
    saveUser(user);
  }

  /**
   * Forcibly sets new password for a User.
   * 
   * Provides an administrator the ability to change the forgotten or
   * compromised passwords. Certain implementatations of this feature would
   * require administrative level access to the authenticating server / program.
   * 
   * @param user
   *            the user to change the password for.
   * @param password
   *            the new password.
   * 
   * @exception UserException
   *                when the security provider has a general failure retrieving
   *                a user.
   * @exception UnknownUserException
   *                when the security provider cannot match the principal
   *                identity to a user.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void forcePassword(JetspeedUser user, String password)
      throws JetspeedSecurityException {
    if (!accountExists(user)) {
      throw new UnknownUserException("The account '" + user.getUserName()
          + "' does not exist");
    }

    String encrypted = JetspeedSecurity.encryptPassword(password);
    user.setPassword(encrypted);

    // save the changes in the database immediately, to prevent the
    // password being 'reverted' to the old value if the user data
    // is lost somehow before it is saved at session's expiry.
    saveUser(user);
  }

  /**
   * This method provides client-side encryption of passwords.
   * 
   * If <code>secure.passwords</code> are enabled in
   * JetspeedSecurity.properties, the password will be encrypted, if not, it
   * will be returned unchanged. The <code>secure.passwords.algorithm</code>
   * property can be used to chose which digest algorithm should be used for
   * performing the encryption. <code>SHA</code> is used by default.
   * 
   * @param password
   *            the password to process
   * 
   * @return processed password
   * 
   */
  public String encryptPassword(String password)
      throws JetspeedSecurityException {
    if (securePasswords == false) {
      return password;
    } else if (password == null) {
      return null;
    } else if (password.startsWith(passwordsSuffix)) {
      // A kludge
      return password;
    }

    return passwordsSuffix + UnixCrypt.crypt(password);
  }

  /**
   * <p>
   * Check's if user's current password matches with the supplied password.
   * </p>
   * 
   * @param user
   *            User whose password will be checked
   * @param suppliedPassword
   *            Password to match
   * 
   * @return True if passwords match.
   * 
   */
  public static boolean passwordsMatch(JetspeedUser user,
      String suppliedPassword) {
    if (securePasswords == false) {
      return user.getPassword().equals(suppliedPassword);
    } else {
      return UnixCrypt.matches(user.getPassword().substring(
          passwordsSuffix.length()), suppliedPassword);
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

    securePasswords = serviceConf.getBoolean(CONFIG_SECURE_PASSWORDS_KEY,
        securePasswords);
    passwordsAlgorithm = serviceConf.getString(
        CONFIG_SECURE_PASSWORDS_ALGORITHM, passwordsAlgorithm);
    passwordsSuffix = serviceConf.getString(CONFIG_SECURE_PASSWORDS_SUFFIX,
        passwordsSuffix);

    try {
      roles = serviceConf.getStringArray(CONFIG_NEWUSER_ROLES);
    } catch (Exception e) {
    }

    if (null == roles || roles.length == 0) {
      roles = DEFAULT_CONFIG_NEWUSER_ROLES;
    }

    this.runDataService = (JetspeedRunDataService) TurbineServices
        .getInstance().getService(RunDataService.SERVICE_NAME);

    setInit(true);
  }

  // /////////////////////////////////////////////////////////////////////////
  // Internal
  // /////////////////////////////////////////////////////////////////////////

  /**
   * Check whether a specified user's account exists.
   * 
   * The login name is used for looking up the account.
   * 
   * @param user
   *            The user to be checked.
   * @param checkUniqueId
   *            Make sure that we aren't overwriting another user with different
   *            id.
   * 
   * @return true If the specified account exists
   * 
   * @throws UserException
   *             If there was a general db access error
   * 
   */
  protected boolean accountExists(JetspeedUser user) throws UserException {
    return accountExists(user, false);
  }

  protected boolean accountExists(JetspeedUser user, boolean checkUniqueId)
      throws UserException {
    UserNamePrincipal principal = new UserNamePrincipal(user.getUserName());

    try {
      getUser(principal);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  protected JetspeedRunData getRunData() {
    JetspeedRunData rundata = null;

    if (this.runDataService != null) {
      rundata = this.runDataService.getCurrentRunData();
    }

    return rundata;
  }

}

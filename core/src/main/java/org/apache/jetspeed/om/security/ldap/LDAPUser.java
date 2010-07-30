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

package org.apache.jetspeed.om.security.ldap;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.JetspeedAuthentication;
import org.apache.jetspeed.services.JetspeedLDAP;
import org.apache.jetspeed.services.JetspeedUserManagement;
import org.apache.jetspeed.services.ldap.LDAPURL;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.jetspeed.services.security.UserException;
import org.apache.turbine.om.security.User;
import org.apache.turbine.util.ObjectUtils;

/**
 * 
 * @author <a href="mailto:ender@kilicoglu.nom.tr">Ender KILICOGLU</a>
 * @author <a href="mailto:sami.leino@netorek.fi">Sami Leino</a>
 * 
 * 
 */
public class LDAPUser extends BaseLDAPObject implements JetspeedUser,
    HttpSessionBindingListener {

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(LDAPUser.class.getName());

  // ---------------------------- Constants ----------------------------

  protected static final String OBJECT_CLASS = "jetspeeduser";

  protected static final String ORGANIZATIONAL_UNIT = "ou=users";

  protected static final String ATTR_UID = "uid";

  protected static final String ATTR_UID_NUMBER = "uidNumber";

  protected static final String ATTR_USER_PASSWORD = "userPassword";

  protected static final String ATTR_NAME = "name";

  protected static final String ATTR_GIVEN_NAME = "givenName";

  protected static final String ATTR_SN = "sn";

  protected static final String ATTR_MAIL = "mail";

  protected static final String ATTR_OBJECT_DATA = "objectdata";

  protected static final String ATTR_OBJECT_CLASS = "objectClass";

  protected static final String ATTR_USER_GROUP_ROLE = "usergrouprole";

  protected static final String ATTR_LAST_LOGIN_DATE = "lastlogindate";

  protected static final String ATTR_LAST_MODIFIED_DATE = "lastmodifieddate";

  protected static final String ATTR_CREATION_DATE = "creationdate";

  protected static final String ATTR_CONFIRMED = "confirm";

  protected static final String ATTR_DISABLED = "disabled";

  // ------------------------- Member variables ------------------------

  /** The date on which the user account was created. */
  private Date createDate = null;

  /** The date on which the user last accessed the application. */
  private Date lastAccessDate = null;

  /** The date on which the user last changed his password. */
  private Date passwordChanged = null;

  /** This is data that will survive a servlet engine restart. */
  private Hashtable permStorage = null;

  /** This is data that will not survive a servlet engine restart. */
  private Hashtable tempStorage = null;

  /** Name of the user */
  protected String name = "";

  /** User's roles. */
  protected Vector groupRoles = null;

  // --------------------------- Constructors --------------------------

  public LDAPUser() {
    this("TempUser", true);
  }

  /**
   * Constructor. Create a new User and set the createDate.
   */
  public LDAPUser(String username, boolean isNew) {
    super.ldapurl = JetspeedLDAP.buildURL(ATTR_UID + "=" + username + ","
        + ORGANIZATIONAL_UNIT);
    this.isNew = isNew;

    createDate = new Date();
    lastAccessDate = createDate;
    tempStorage = new Hashtable(20);
    permStorage = new Hashtable(50);
    groupRoles = new Vector();
    setHasLoggedIn(Boolean.FALSE);

    if (isNew) {
      setDisabled("F");
      setUserName(username);
      String uidNumber = Long.valueOf(System.currentTimeMillis()).toString();
      setUserId(uidNumber);
      myAttrs = new BasicAttributes();
      myAttrs.put(ATTR_UID, username);
      myAttrs.put(ATTR_UID_NUMBER, uidNumber);
      Attribute oc = new BasicAttribute(ATTR_OBJECT_CLASS);
      oc.add("jetspeeduser");
      myAttrs.put(oc);
    } else {
      myAttrs = JetspeedLDAP.read(ldapurl);
      fillObject(myAttrs);
    }
  }

  public LDAPUser(LDAPURL ldapurl) {
    fillObject(JetspeedLDAP.read(ldapurl));
  }

  public LDAPUser(Attributes attributes) {
    fillObject(attributes);
  }

  private void fillObject(Attributes attributes) {
    tempStorage = new Hashtable(20);
    permStorage = new Hashtable(50);
    setHasLoggedIn(Boolean.FALSE);

    myAttrs = attributes;

    try {
      setPermStorage(deserializePerm(getutil(ATTR_OBJECT_DATA, attributes)));
    } catch (Exception e) {
      logger.error("fillobject()", e);
    }

    setUserName(getutil(ATTR_UID, attributes));
    setUserId(getutil(ATTR_UID_NUMBER, attributes));
    setEmail(getutil(ATTR_MAIL, attributes));
    setFirstName(getutil(ATTR_GIVEN_NAME, attributes));
    setLastName(getutil(ATTR_SN, attributes));
    // setName(getutil(ATTR_NAME, attributes));
    setConfirmed(getutil(ATTR_CONFIRMED, attributes));

    setLastLogin(parseDate(getutil(ATTR_LAST_LOGIN_DATE, attributes)));
    lastAccessDate = parseDate(getutil(ATTR_LAST_MODIFIED_DATE, attributes));
    setCreateDate(parseDate(getutil(ATTR_CREATION_DATE, attributes)));
    String temp = getutil(ATTR_DISABLED, attributes);
    if (temp != null && temp.equals("TRUE"))
      setDisabled("T");
    else
      setDisabled("F");

    try {
      setPassword(new String((byte[]) attributes.get(ATTR_USER_PASSWORD)
          .getAll().nextElement()));
    } catch (Exception e) {
      logger.error("fillobject() could not set password", e);
    }

    this.groupRoles = getutil(ATTR_USER_GROUP_ROLE, attributes, true);
    ldapurl = JetspeedLDAP.buildURL(ATTR_UID + "=" + getUserName() + ","
        + ORGANIZATIONAL_UNIT);
  }

  // --------------------- Persistence operations ----------------------

  public void update(boolean create) throws JetspeedSecurityException {
    removeutil("createTimeStamp", false);
    removeutil("modifyTimeStamp", false);

    setutil(ATTR_USER_PASSWORD, (String) getPerm(User.PASSWORD));
    setutil(ATTR_MAIL, (String) getPerm(User.EMAIL));
    setutil(ATTR_CONFIRMED, (String) getPerm(User.CONFIRM_VALUE));
    setutil(ATTR_SN, (String) getPerm(User.LAST_NAME));
    setutil(ATTR_GIVEN_NAME, (String) getPerm(User.FIRST_NAME));
    setutil(ATTR_USER_GROUP_ROLE, this.getGroupRoles(), create);
    // setutilMulti(ATTR_USER_GROUP_ROLE, groupRoles);
    setutil(ATTR_LAST_LOGIN_DATE, formatDate(getLastLogin()));
    setutil(ATTR_LAST_MODIFIED_DATE, formatDate(getLastAccessDate()));
    setutil(ATTR_CREATION_DATE, formatDate(getCreateDate()));
    if ("T".equals(getDisabled()))
      setutil(ATTR_DISABLED, "TRUE");
    else
      setutil(ATTR_DISABLED, "FALSE");

    try {
      setutil(ATTR_OBJECT_DATA, serializePerm(permStorage));
    } catch (Exception e) {
      logger.warn("Could not serialize object data!", e);
    }

    if (create) {
      ldapurl = JetspeedLDAP.buildURL("uid=" + (String) getPerm(User.USERNAME)
          + ",ou=users");
      setutil(ATTR_UID, (String) getPerm(User.USERNAME));
      if (JetspeedLDAP.addEntry(ldapurl, myAttrs) == false)
        throw new UserException("Could not insert user data to LDAP!");
    } else if (JetspeedLDAP.exists(ldapurl)) {
      JetspeedLDAP.deleteAttrs(ldapurl, rmAttrs);
      if (JetspeedLDAP.updateEntry(ldapurl, myAttrs) == false)
        throw new UserException("Could not update user data to LDAP!");
    }
  }

  // ------------------------ Accessor methods -------------------------

  public Vector getGroupRoles() {
    return this.groupRoles;
  }

  public void addGroupRole(String groupName, String roleName) {
    groupRoles.add(groupName + "," + roleName);
  }

  public void removeGroup(String groupName) {
    for (Enumeration enu = groupRoles.elements(); enu.hasMoreElements();) {
      String groupRoleStr = (String) enu.nextElement();
      if (groupRoleStr.startsWith(groupName + ",")) {
        groupRoles.remove(groupRoleStr);
      }
    }
  }

  public void removeGroupRole(String groupName, String roleName) {
    for (Enumeration enu = groupRoles.elements(); enu.hasMoreElements();) {
      String groupRoleStr = (String) enu.nextElement();
      if (groupRoleStr.equalsIgnoreCase(groupName + "," + roleName)) {
        groupRoles.remove(groupRoleStr);
      }
    }
  }

  /**
   * Returns the primary principle for this User, the user id.
   * 
   * 
   * @return the user id.
   */
  public String getUserId() {
    String tmp = null;

    try {
      tmp = (String) getPerm(JetspeedUser.USER_ID);
      if (tmp.length() == 0) {
        tmp = null;
      }
    } catch (Exception e) {
      logger.error("getUserId():", e);
    }
    return tmp;
  }

  public void setUserId(String id) {
    if (getUserId() == null) {
      setPerm(JetspeedUser.USER_ID, id);
    }
  }

  /**
   * Gets the access counter for a user during a session.
   * 
   * @return The access counter for the user for the session.
   */
  public int getAccessCounterForSession() {
    try {
      return ((Integer) getTemp(User.SESSION_ACCESS_COUNTER)).intValue();
    } catch (Exception e) {
      logger.error("getAccessCounterForSession():", e);
      return 0;
    }
  }

  /**
   * Gets the access counter for a user from perm storage.
   * 
   * @return The access counter for the user.
   */
  public int getAccessCounter() {
    try {
      return ((Integer) getPerm(User.ACCESS_COUNTER)).intValue();
    } catch (Exception e) {
      logger.error("getAccessCounter():", e);
      return 0;

    }
  }

  /**
   * Gets the create date for this User. This is the time at which the user
   * object was created.
   * 
   * @return A Java Date with the date of creation for the user.
   */
  public java.util.Date getCreateDate() {
    return createDate;
  }

  /**
   * Gets the last access date for this User. This is the last time that the
   * user object was referenced.
   * 
   * @return A Java Date with the last access date for the user.
   */
  public java.util.Date getLastAccessDate() {
    if (lastAccessDate == null) {
      setLastAccessDate();
    }
    return lastAccessDate;
  }

  /**
   * 
   * Get last login date/time for this user.
   * 
   * @return A Java Date with the last login date for the user.
   */
  public java.util.Date getLastLogin() {
    return (java.util.Date) getPerm(User.LAST_LOGIN);
  }

  /**
   * Get password for this user.
   * 
   * @return A String with the password for the user.
   */
  public String getPassword() {
    return (String) getPerm(User.PASSWORD);
  }

  /**
   * Get an object from permanent storage.
   * 
   * @param name
   *            The object's name.
   * @return An Object with the given name.
   */
  public Object getPerm(String name) {
    return permStorage.get(name);
  }

  /**
   * Get an object from permanent storage; return default if value is null.
   * 
   * @param name
   *            The object's name.
   * @param def
   *            A default value to return.
   * @return An Object with the given name.
   */
  public Object getPerm(String name, Object def) {
    try {
      Object val = permStorage.get(name);

      return (val == null ? def : val);
    } catch (Exception e) {
      logger.error("getPerm():", e);
      return def;
    }
  }

  /**
   * This should only be used in the case where we want to save the data to the
   * database.
   * 
   * @return A Hashtable.
   */
  public Hashtable getPermStorage() {
    if (this.permStorage == null) {
      this.permStorage = new Hashtable(50);
    }
    return this.permStorage;
  }

  /**
   * Get an object from temporary storage.
   * 
   * @param name
   *            The object's name.
   * @return An Object with the given name.
   */
  public Object getTemp(String name) {
    return tempStorage.get(name);
  }

  /**
   * Get an object from temporary storage; return default if value is null.
   * 
   * @param name
   *            The object's name.
   * @param def
   *            A default value to return.
   * @return An Object with the given name.
   */
  public Object getTemp(String name, Object def) {
    Object val;
    try {
      val = tempStorage.get(name);
      if (val == null) {
        val = def;
      }
    } catch (Exception e) {
      logger.error("getTemp():", e);
      val = def;
    }
    return val;

  }

  /**
   * Returns the username for this user. If this is defined, then the user is
   * considered logged in.
   * 
   * @return A String with the username.
   */
  public String getUserName() {
    String tmp = null;
    try {
      tmp = (String) getPerm(User.USERNAME);
      if (tmp.length() == 0) {
        tmp = null;
      }
    } catch (Exception e) {
      logger.error("getUserName():", e);
    }

    return tmp;
  }

  /**
   * Returns the first name for this user. If this is defined, then the user is
   * considered logged in.
   * 
   * @return A String with the user's first name.
   */
  public String getFirstName() {
    String tmp = null;

    try {
      tmp = (String) getPerm(User.FIRST_NAME);
      if (tmp.length() == 0) {
        tmp = null;
      }
    } catch (Exception e) {
      logger.error("getFirstName():", e);
    }
    return tmp;
  }

  /**
   * Returns the last name for this user. If this is defined, then the user is
   * considered logged in.
   * 
   * @return A String with the user's last name.
   */
  public String getLastName() {
    String tmp = null;

    try {
      tmp = (String) getPerm(User.LAST_NAME);
      if (tmp.length() == 0)
        tmp = null;
    } catch (Exception e) {
      logger.error("getLastName():", e);
    }

    return tmp;
  }

  /**
   * The user is considered logged in if they have not timed out.
   * 
   * 
   * @return Whether the user has logged in.
   */
  public boolean hasLoggedIn() {

    Boolean loggedIn = getHasLoggedIn();
    return (loggedIn != null && loggedIn.booleanValue());
  }

  /**
   * Returns the email address for this user.
   * 
   * 
   * @return A String with the user's email address.
   */
  public String getEmail() {
    return (String) getPerm(User.EMAIL);
  }

  /**
   * Increments the permanent hit counter for the user.
   */
  public void incrementAccessCounter() {
    setAccessCounter(getAccessCounter() + 1);
  }

  /**
   * Increments the session hit counter for the user.
   */
  public void incrementAccessCounterForSession() {
    setAccessCounterForSession(getAccessCounterForSession() + 1);
  }

  /**
   * Remove an object from temporary storage and return the object.
   * 
   * @param name
   *            The name of the object to remove.
   * @return An Object.
   */
  public Object removeTemp(String name) {
    return tempStorage.remove(name);
  }

  /**
   * Sets the access counter for a user, saved in perm storage.
   * 
   * @param cnt
   *            The new count.
   */
  public void setAccessCounter(int cnt) {
    setPerm(User.ACCESS_COUNTER, Integer.valueOf(cnt));
  }

  /**
   * Sets the session access counter for a user, saved in temp storage.
   * 
   * @param cnt
   *            The new count.
   */
  public void setAccessCounterForSession(int cnt) {
    setTemp(User.SESSION_ACCESS_COUNTER, Integer.valueOf(cnt));
  }

  /**
   * Sets the last access date for this User. This is the last time that the
   * user object was referenced.
   */
  public void setLastAccessDate() {
    lastAccessDate = new java.util.Date();
  }

  /**
   * Sets the create date for this User. This is the time at which the user
   * object was created.
   * 
   * @param date
   *            The create date.
   */
  public void setCreateDate(java.util.Date date) {
    createDate = date;
  }

  /**
   * Set last login date/time.
   * 
   * @param date
   *            The last login date.
   */
  public void setLastLogin(java.util.Date date) {
    setPerm(User.LAST_LOGIN, date);
  }

  /**
   * Set password.
   * 
   * @param password
   *            The new password.
   */
  public void setPassword(String password) {
    setPerm(User.PASSWORD, password);
  }

  /**
   * Put an object into permanent storage. If the value is null, it will convert
   * that to a "" because the underlying storage mechanism within TurbineUser is
   * currently a Hashtable and null is not a valid value.
   * 
   * @param name
   *            The object's name.
   * @param value
   *            The object.
   */
  public void setPerm(String name, Object value) {
    ObjectUtils.safeAddToHashtable(getPermStorage(), name, value);
  }

  /**
   * This should only be used in the case where we want to save the data to the
   * database.
   * 
   * @param stuff
   *            A Hashtable.
   */
  public void setPermStorage(Hashtable stuff) {
    this.permStorage = stuff;
  }

  /**
   * This should only be used in the case where we want to save the data to the
   * database.
   * 
   * @return A Hashtable.
   */
  public Hashtable getTempStorage() {
    if (this.tempStorage == null) {
      this.tempStorage = new Hashtable(20);
    }
    return this.tempStorage;
  }

  /**
   * This should only be used in the case where we want to save the data to the
   * database.
   * 
   * @param storage
   *            A Hashtable.
   */
  public void setTempStorage(Hashtable storage) {
    this.tempStorage = storage;
  }

  /**
   * This gets whether or not someone has logged in. hasLoggedIn() returns this
   * value as a boolean. This is private because you should use hasLoggedIn()
   * instead.
   * 
   * @return True if someone has logged in.
   * 
   */
  private Boolean getHasLoggedIn() {
    return (Boolean) getTemp(User.HAS_LOGGED_IN);
  }

  /**
   * This sets whether or not someone has logged in. hasLoggedIn() returns this
   * value.
   * 
   * @param value
   *            Whether someone has logged in or not.
   */
  public void setHasLoggedIn(Boolean value) {
    setTemp(User.HAS_LOGGED_IN, value);
  }

  /**
   * Put an object into temporary storage. If the value is null, it will convert
   * that to a "" because the underlying storage mechanism within TurbineUser is
   * currently a Hashtable and null is not a valid value.
   * 
   * @param name
   *            The object's name.
   * @param value
   *            The object.
   */
  public void setTemp(String name, Object value) {
    ObjectUtils.safeAddToHashtable(tempStorage, name, value);
  }

  /**
   * Sets the username for this user.
   * 
   * @param username
   *            The user's username.
   */
  public void setUserName(String username) {
    setPerm(User.USERNAME, username);
  }

  /**
   * Sets the first name for this user.
   * 
   * @param firstName
   *            User's first name.
   */
  public void setFirstName(String firstName) {
    setPerm(User.FIRST_NAME, firstName);
  }

  /**
   * Sets the last name for this user.
   * 
   * @param lastName
   *            User's last name.
   */
  public void setLastName(String lastName) {
    setPerm(User.LAST_NAME, lastName);
  }

  /**
   * Sets the email address.
   * 
   * @param address
   *            The email address.
   */
  public void setEmail(String address) {
    setPerm(User.EMAIL, address);
  }

  /**
   * This method reports whether or not the user has been confirmed in the
   * system by checking the User.CONFIRM_VALUE column in the users record to see
   * if it is equal to User.CONFIRM_DATA.
   * 
   * @return True if the user has been confirmed.
   */
  public boolean isConfirmed() {
    String value = getConfirmed();
    return (value != null && value.equals(User.CONFIRM_DATA));
  }

  /**
   * Sets the confirmation value. The value should be either a random string or
   * User.CONFIRM_DATA
   * 
   * @param value
   *            The confirmation key value.
   */
  public void setConfirmed(String value) {
    String val = "";
    if (value != null) {
      val = value;
    }
    setPerm(User.CONFIRM_VALUE, val);
  }

  /**
   * Gets the confirmation value.
   * 
   * @return status The confirmation value for this User
   */
  public String getConfirmed() {
    return (String) getPerm(User.CONFIRM_VALUE);
  }

  /**
   * Updates the last login date in the database.
   * 
   * @exception Exception,
   *                a generic exception.
   */
  public void updateLastLogin() throws Exception {
    setPerm(User.LAST_LOGIN, new java.util.Date());
  }

  /**
   * Implement this method if you wish to be notified when the User has been
   * Bound to the session.
   * 
   * @param hsbe
   *            The HttpSessionBindingEvent.
   */
  public void valueBound(HttpSessionBindingEvent hsbe) {
  }

  /**
   * Implement this method if you wish to be notified when the User has been
   * Unbound from the session.
   * 
   * @param hsbe
   *            The HttpSessionBindingEvent.
   */
  public void valueUnbound(HttpSessionBindingEvent hsbe) {
    try {
      if (this.hasLoggedIn()) {
        if (JetspeedResources.getBoolean("automatic.logout.save", false)) {
          JetspeedUserManagement.saveUser(this);
        }

        JetspeedAuthentication.logout();
      }
    } catch (Exception e) {
      logger.error("LDAPUser.valueUnbound(): " + e.getMessage(), e);

      // To prevent messages being lost in case the logging system
      // goes away before sessions get unbound on servlet container
      // shutdown, print the stcktrace to the container's console.
      ByteArrayOutputStream ostr = new ByteArrayOutputStream();
      e.printStackTrace(new PrintWriter(ostr, true));
      String stackTrace = ostr.toString();
      System.out.println(stackTrace);
    }
  }

  /**
   * Saves this object to the data store.
   */
  public void save() throws Exception {
    if (this.isNew()) {
      JetspeedUserManagement.saveUser(this);
    } else {
      JetspeedUserManagement.addUser(this);
    }
  }

  /**
   * Returns the disabled status for the user
   * 
   * @return True when the account is disabled
   */
  public String getDisabled() {
    String disabled = null;

    try {
      String tmp = (String) getPerm(JetspeedUser.DISABLED);
      if (tmp != null && tmp.length() > 0) {
        disabled = tmp;
      }
    } catch (Exception e) {
      logger.error("getDisabled():", e);
    }

    return disabled;
  }

  public void setDisabled(String disabled) {
    setPerm(JetspeedUser.DISABLED, disabled);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isNew() {
    return isNew;
  }

  void setNew(boolean isNew) {
    this.isNew = isNew;
  }

  /**
   * Returns the date of last password change
   * 
   * @return date
   * 
   */
  public Date getPasswordChanged() {
    return this.passwordChanged;
  }

  /**
   * Sets the date of last password change
   * 
   * @param value
   *            Date
   */
  public void setPasswordChanged(Date value) {
    this.passwordChanged = value;
  }

}

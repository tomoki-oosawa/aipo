/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.om.security;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Hashtable;

import javax.servlet.http.HttpSessionBindingEvent;

import org.apache.jetspeed.services.JetspeedAuthentication;
import org.apache.jetspeed.services.JetspeedUserManagement;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.om.security.User;
import org.apache.turbine.util.ObjectUtils;

/**
 * The default Jetspeed implementation of User interface.
 * 
 * This basic implementation contains the functionality that is expected to be
 * common among all User implementations. You are welcome to extend this class
 * if you wish to have custom functionality in your user objects (like accessor
 * methods for custom attributes).
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: BaseJetspeedUser.java,v 1.11 2004/02/23 03:14:12 jford Exp $
 */
public class BaseJetspeedUser /* extends SecurityObject */implements
    JetspeedUser {
  /** The date on which the user account was created. */
  private Date createDate = null;

  /** The date on which the user last accessed the application. */
  private Date lastAccessDate = null;

  /** This is data that will survive a servlet engine restart. */
  private Hashtable permStorage = null;

  /** This is data that will not survive a servlet engine restart. */
  private Hashtable tempStorage = null;

  protected String name = "";

  protected boolean isNew = true;

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(BaseJetspeedUser.class.getName());

  /**
   * Constructor. Create a new User and set the createDate.
   */
  public BaseJetspeedUser() {
    createDate = new Date();
    tempStorage = new Hashtable(10);
    permStorage = new Hashtable(10);
    setHasLoggedIn(Boolean.FALSE);
    setDisabled("F");
    isNew = true;
  }

  /**
   * Returns the primary principle for this User, the user id.
   * 
   * @return the user id.
   */
  public String getUserId() {
    String tmp = null;
    try {
      tmp = (String) getPerm(JetspeedUser.USER_ID);
      if (tmp != null && tmp.length() == 0) {
        tmp = null;
      }
    } catch (Exception e) {
      logger.error("getUserId(): " + e.getMessage(), e);
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
    int accessCounter = 0;
    try {
      Integer temp = (Integer) getTemp(User.SESSION_ACCESS_COUNTER);
      if (temp != null) {
        accessCounter = temp.intValue();
      }
    } catch (Exception e) {
      logger.debug("getAccessCounterForSession(): " + e.getMessage(), e);
    }

    return accessCounter;
  }

  /**
   * Gets the access counter for a user from perm storage.
   * 
   * @return The access counter for the user.
   */
  public int getAccessCounter() {
    int accessCounter = 0;
    try {
      Integer temp = (Integer) getPerm(User.ACCESS_COUNTER);
      if (temp != null) {
        accessCounter = temp.intValue();
      }
    } catch (Exception e) {
      logger.debug("getAccessCounter(): " + e.getMessage(), e);
    }
    return accessCounter;
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
      logger.error("getPerm(" + name + "): " + e.getMessage(), e);
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
      this.permStorage = new Hashtable();
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
      logger.error("getTemp(" + name + "): " + e.getMessage(), e);
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
      logger.error("getUserName(): " + e.getMessage(), e);
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
      logger.error("getFirstName(): " + e.getMessage(), e);
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
      logger.error("getLastName(): " + e.getMessage(), e);
    }
    return tmp;
  }

  /**
   * The user is considered logged in if they have not timed out.
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
    setPerm(User.ACCESS_COUNTER, new Integer(cnt));
  }

  /**
   * Sets the session access counter for a user, saved in temp storage.
   * 
   * @param cnt
   *            The new count.
   */
  public void setAccessCounterForSession(int cnt) {
    setTemp(User.SESSION_ACCESS_COUNTER, new Integer(cnt));
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
      this.tempStorage = new Hashtable();
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
    // Currently we have no need for this method.
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
      java.util.Date now = new java.util.Date();
      // System.out.println("*********** value unbound ********************: " +
      // now.toString());
      if (this.hasLoggedIn()) {
        if (JetspeedResources.getBoolean("automatic.logout.save", false)) {
          JetspeedUserManagement.saveUser(this);
        }
        JetspeedAuthentication.logout();
      }

    } catch (Exception e) {
      logger.error("TurbineUser.valueUnbound(): " + e.getMessage(), e);

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
      JetspeedUserManagement.addUser(this);
    } else {
      JetspeedUserManagement.saveUser(this);
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
      logger.error("getDisabled(): " + e.getMessage(), e);
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

  protected void setNew(boolean isNew) {
    this.isNew = isNew;
  }

  /**
   * @see org.apache.jetspeed.om.security.JetspeedUser#getPasswordChanged
   */
  public Date getPasswordChanged() {
    return (Date) getPerm(JetspeedUser.PASSWORD_CHANGED);
  }

  /**
   * @see org.apache.jetspeed.om.security.JetspeedUser#setPasswordChanged
   */
  public void setPasswordChanged(Date value) {
    setPerm(JetspeedUser.PASSWORD_CHANGED, value);
  }

}
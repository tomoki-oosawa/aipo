/*
 * Copyright 2000-2002,2004 The Apache Software Foundation.
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

// package
package org.apache.jetspeed.services.statemanager;

// imports
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

/**
 * <p>
 * JetspeedHttpStateManagerService is an implementation of the
 * BaseStateManagerService which manages the states stored in the "current"
 * HttpSession.
 * </p>
 * <p>
 * Note: This implementation of the StateManagerService takes advantage of the
 * Servlet container's management of the HttpSession. When the session is
 * invalidated, the states we manage will be automatically cleaned up. When this
 * happens, the objects placed into our states will have their
 * SessionStateBindingListener mechanism invoked.
 * </p>
 * <p>
 * Note: This implementation segments the states by session. States created in
 * one session will NOT BE AVAILABLE from other sessions.
 * </p>
 *
 * @version $Revision: 1.4 $
 * @see org.apache.jetspeed.services.statemanager.BaseStateManagerService
 * @see org.apache.jetspeed.services.statemanager.StateManagerService
 * @see org.apache.jetspeed.services.statemanager.SessionState
 * @author <a href="mailto:ggolden@apache.org">Glenn R. Golden</a>
 */
public class JetspeedHttpStateManagerService extends BaseStateManagerService
    implements Serializable {

  private static final long serialVersionUID = 6138392597252459984L;

  /**
   * Initialize the states storage.
   */
  @Override
  protected void initStates() {
  } // initStates

  /**
   * Cleanup the states storage.
   */
  @Override
  protected void shutdownStates() {
  } // shutdownStates

  /**
   * Access the current HttpSession.
   */
  private HttpSession getSession() {
    // get the current session that was installed for this thread
    HttpSession session =
      (HttpSession) m_httpSessions.get(Thread.currentThread());
    if (session == null) {
      return null;
    }

    // call isNew just to see if the session has been invalidated already
    try {
      session.isNew();
    } catch (IllegalStateException e) {
      return null;
    }

    return session;

  } // getSession

  /**
   * Convert the key to a name safe to store directly in the session.
   *
   * @param key
   *          The state key.
   * @return a name safe to store directly in the session based on key.
   */
  private String getSessionKey(String key) {
    // we want our keys not to conflict with any other session usage...
    return JetspeedHttpStateManagerService.class.getName() + "." + key;

  } // getSessionKey

  /**
   * Access the Map which is the set of attributes for a state.
   *
   * @param key
   *          The state key.
   * @return The Map which is the set of attributes for a state.
   */
  @Override
  protected Map getState(String key) {
    // get the session
    HttpSession session = getSession();
    if (session == null) {
      return null;
    }

    // get this state from our entry in the session
    StateEntry stateEntry =
      (StateEntry) session.getAttribute(getSessionKey(key));
    if (stateEntry == null) {
      return null;
    }

    return stateEntry.getMap();

  } // getState

  /**
   * Add a new state to the states we are managing.
   *
   * @param key
   *          The state key.
   * @param state
   *          The Map which is the set of attributes for the state.
   */
  @Override
  protected void addState(String key, Map state) {
    // get the session
    HttpSession session = getSession();
    if (session == null) {
      return;
    }

    // create a stateEntry to hold our state Map
    StateEntry stateEntry = new StateEntry(key, state);

    // put it in the session
    session.setAttribute(getSessionKey(key), stateEntry);

  } // addState

  /**
   * Remove a state from the states we are managing.
   *
   * @param key
   *          The state key.
   */
  @Override
  protected void removeState(String key) {
    // get the session
    HttpSession session = getSession();
    if (session == null) {
      return;
    }

    // remove the key from the session - the StateEntry will be notified
    session.removeAttribute(getSessionKey(key));

  } // removeState

  /**
   * Access an array of the keys of all states managed, those that start with
   * the parameter.
   *
   * @param start
   *          The starting string used to select the keys.
   * @return an array of the keys of all states managed.
   */
  @Override
  protected String[] getStateKeys(String start) {
    // get the session
    HttpSession session = getSession();
    if (session == null) {
      return null;
    }

    // use this as the test pattern
    String pattern = getSessionKey(start);

    // for those that match, this starts the session key
    int subStart = getSessionKey("").length();

    // collect for return
    Vector rv = new Vector();

    // get the session names
    Enumeration names = session.getAttributeNames();
    while (names.hasMoreElements()) {
      String sessionName = (String) names.nextElement();

      // pick our states, and those whose key starts with the pattern
      if (sessionName.startsWith(pattern)) {
        rv.add(sessionName.substring(subStart));
      }
    }

    if (rv.size() == 0) {
      return null;
    }

    return (String[]) rv.toArray(new String[rv.size()]);

  } // getStateKeys

  /**
   * Store the Map for the state, and listen for HttpSessionBinding events
   */
  private class StateEntry implements HttpSessionBindingListener, Serializable {

    private static final long serialVersionUID = 8630600178442354718L;

    /** Store the map. */
    private Map m_map = null;

    /** The state key. */
    private String m_key = null;

    /**
     * Construct. s
     *
     * @param key
     *          The state key.
     * @param map
     *          The map to hold.
     */
    public StateEntry(String key, Map map) {
      m_key = key;
      m_map = map;

    } // StateEntry

    /**
     * Access the map we are holding.
     *
     * @return the Map we are holding.
     */
    public Map getMap() {
      return m_map;

    } // getMap

    /**
     * We don't care about when we are bound...
     */
    @Override
    public void valueBound(HttpSessionBindingEvent event) {
    }

    /**
     * When we are unbound, unbind our state's (map's) attributes
     */
    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
      // notify all attribute and clear the state
      retireAttributes(m_key, m_map);
      m_map = null;
      m_key = null;
    }

  } // class StateEntry

} // JetspeedHttpStateManagerService

/**********************************************************************************
 *
 * $Header:
 * /home/cvspublic/jakarta-jetspeed/src/java/org/apache/jetspeed/services
 * /statemanager/JetspeedHttpStateManagerService.java,v 1.4 2004/02/23 03:38:28
 * jford Exp $
 *
 **********************************************************************************/


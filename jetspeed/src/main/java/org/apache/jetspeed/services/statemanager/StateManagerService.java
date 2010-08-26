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
import javax.servlet.http.HttpSession;

/**
* <p>The StateManagerService is a service that manages SessionState information.
* Each SessionState is identified by a unique key in this service.  The SessionState
* is composed of name - value sets of attributes, stored under the key by the service.</p>
* <p>See the proposal: jakarta-jetspeed/proposals/StateManager.txt for more details.</p>
* <p>Attribute values placed into SessionStates may be automatically removed, for example when
* a SessionState expires in some way, or by means of some other automatic service.  These value
* objects can be notified of their placement into and out of the SessionState.  Objects that wish
* to receive this notification implement the SessionStateBindingListener interface.  This is based
* on and similar to the HttpSessionBindingListener mechanism.</p>
* <p>To support the "current" routines; the ability to get a session state based
* on the current http session, this service must be installed properly into the
* procssing of each http request.  At the start of the request, the HttpSession
* of the request must be passed into the setCurrentContext() method.  At the end
* of the request, clearCurrentContext() is called.  For Jetspeed, this is done in
* the JetspeedRunDataService, which is always going to be called by Turbine in this
* way.</p>
* @version $Revision: 1.5 $
* @see org.apache.jetspeed.services.statemanager.SessionState
* @see org.apache.jetspeed.services.statemanager.SessionStateBindingListener
* @author <a href="mailto:ggolden@apache.org">Glenn R. Golden</a>
*/
public interface StateManagerService
{
    /** The name used to find the service in the service manager. */
    public String SERVICE_NAME = "StateManagerService";

    /**
    * Access the named attribute of the keyed state.
    * @param key The state key.
    * @param name The attribute name.
    * @return The named attribute value of the keyed state.
    */
    public Object getAttribute ( String key, String name );

    /**
    * Set the named state attribute of the keyed state with the provided object.
    * @param key The state key.
    * @param name The attribute name.
    * @param value The new value of the attribute (any object type).
    */
    public void setAttribute( String key, String name, Object value );

    /**
    * Remove the named state attribute of the keyed state, if it exists.
    * @param key The state key.
    * @param name The attribute name.
    */
    public void removeAttribute( String key, String name );

    /**
    * Remove all state attribute of the keyed state.
    * @param key The state key.
    */
    public void clear( String key );

    /**
    * Access an array of all names of attributes stored in the keyed state.
    * @param key The state key.
    * @return An array of all names of attributes stored in the keyed state.
    */
    public String[] getAttributeNames( String key );

    /**
    * Access an SessionState object with the given key.
    * @param key The SessionState key.
    * @return an SessionState object with the given key.
    */
    public SessionState getSessionState( String key );

    /**
    * Access the SessionState object associated with the current request's http session.
	* The session id is used as the key.
    * @return an SessionState object associated with the current request's http session.
    */
    public SessionState getCurrentSessionState();

    /**
    * Access the SessionState object associated with the current request's http session with the given key.
	* @param key The string to add to the session id to form the SessionState key.
    * @return an SessionState object associated with the current request's http session with the given key.
    */
    public SessionState getCurrentSessionState( String key );

    /**
    * Retire, forget about and clean up all states that start with the given key.
    * @param keyStart The beginning of the key of the states to clean up.
    */
    public void retireState( String keyStart );

    /**
    * Set the "current" context for this thread -
    * Call this at the start of each request, and call %%% at the end.
    * getCurrentSession() uses this for the session state key.
    * @param session the HttpSession of the current request.
    */
    public void setCurrentContext( HttpSession session );

    /**
    * Clear the "current context for this thread -
    * Call at the end of each request, balanced with calls to setCurrentContext()
    */
    public void clearCurrentContext();

}   // interface StateManagerService

/**********************************************************************************
*
* $Header: /home/cvspublic/jakarta-jetspeed/src/java/org/apache/jetspeed/services/statemanager/StateManagerService.java,v 1.5 2004/02/23 03:38:28 jford Exp $
*
**********************************************************************************/


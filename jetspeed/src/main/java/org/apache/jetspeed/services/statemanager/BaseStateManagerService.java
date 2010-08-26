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
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpSession;

import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.util.RunData;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.statemanager.StateManagerService;
import org.apache.jetspeed.services.statemanager.SessionStateBindingListener;

/**
* <p>BaseStateManagerService is a Turbine Service implementation of the
* StateManagerService.</p>
* <p>Each SessionState is stored in a Map, storing the names and values
* of the state attributes.</p>
* <p>The set of states managed is stored in some specific way by extension classes.</p>
* <p>See the proposal: jakarta-jetspeed/proposals/StateManager.txt for more details.</p>
* @version $Revision: 1.5 $
* @see org.apache.jetspeed.services.statemanager.StateManagerService
* @see org.apache.jetspeed.services.statemanager.SessionState
* @author <a href="mailto:ggolden@apache.org">Glenn R. Golden</a>
*/
public abstract class BaseStateManagerService
    extends TurbineBaseService
    implements StateManagerService
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(BaseStateManagerService.class.getName());
    
    /** map of thread to http session for that thread. */
    protected Map m_httpSessions = null;

    /*******************************************************************************
    * Abstract methods
    *******************************************************************************/

    /**
    * Initialize the states storage.
    */
    protected abstract void initStates();

    /**
    * Cleanup the states storage.
    */
    protected abstract void shutdownStates();

    /**
    * Access the Map which is the set of attributes for a state.
    * @param key The state key.
    * @return The Map which is the set of attributes for a state.
    */
    protected abstract Map getState( String key );

    /**
    * Add a new state to the states we are managing.
    * @param key The state key.
    * @param state The Map which is the set of attributes for the state.
    */
    protected abstract void addState( String key, Map state );

    /**
    * Remove a state from the states we are managing.
    * @param key The state key.
    */
    protected abstract void removeState( String key );

    /**
    * Access an array of the keys of all states managed, those that start with the parameter.
    * @param start The starting string used to select the keys.
    * @return an array of the keys of all states managed.
    */
    protected abstract String[] getStateKeys( String start );

    /**
    * retire the attributes of the state.
    * @param key The state key.
    * @param state The Map of attributes to retire.
    */
    protected void retireAttributes( String key, Map state )
    {
        if (state == null) return;

        Set attributes = state.entrySet();
        synchronized (state)
        {
            Iterator i = attributes.iterator();
            while (i.hasNext())
            {
                Map.Entry attribute = (Map.Entry) i.next();
                unBindAttributeValue(key, (String)attribute.getKey(), attribute.getValue());
            }
        }

        // remove all attributes
        state.clear();

    }   // retireAttributes

    /**
    * If the object is a SessionStateBindingListener, unbind it
    * @param stateKey The state key.
    * @param attributeName The attribute name.
    * @param attribute The attribute object
    */
    protected void unBindAttributeValue( String stateKey, String attributeName, Object attribute )
    {
        // if this object wants session binding notification
        if ((attribute != null) && (attribute instanceof SessionStateBindingListener))
        {
            try
            {
                ((SessionStateBindingListener)attribute)
                    .valueUnbound(stateKey, attributeName);
            }
            catch (Throwable e) 
            {
                logger.warn("JetspeedStateManagerService.unBindAttributeValue: unbinding exception: ", e);
            }
        }

    }   // unBindAttributeValue

    /**
    * If the object is a SessionStateBindingListener, bind it
    * @param stateKey The state key.
    * @param attributeName The attribute name.
    * @param attribute The attribute object
    */
    protected void bindAttributeValue( String stateKey, String attributeName, Object attribute )
    {
        // if this object wants session binding notification
        if ((attribute != null) && (attribute instanceof SessionStateBindingListener))
        {
            try
            {
                ((SessionStateBindingListener)attribute)
                    .valueBound(stateKey, attributeName);
            }
            catch (Throwable e) 
            {
                logger.warn("JetspeedStateManagerService.bindAttributeValue: unbinding exception: ", e);
            }
        }

    }   // bindAttributeValue

    /*******************************************************************************
    * Service implementation
    *******************************************************************************/

    /**
    * Performs early initialization.
    *
    * @param config A ServletConfing to use for initialization
    * activities.
    * @exception InitializationException, if initialization of this
    * class was not successful.
    */
    public void init( ServletConfig config )
        throws InitializationException
    {
        super.init(config);

    }   // init

    /**
    * Performs early initialization.
    *
    * @param data An RunData to use for initialization activities.
    * @exception InitializationException, if initialization of this
    * class was not successful.
    */
    public void init( RunData data )
        throws InitializationException
    {
        super.init(data);

    }   // init

    /**
    * Performs late initialization.
    *
    * If your class relies on early initialization, and the object it
    * expects was not received, you can use late initialization to
    * throw an exception and complain.
    *
    * @exception InitializationException, if initialization of this
    * class was not successful.
    */
    public void init()
        throws InitializationException
    {
        super.init();

        // allocate a thread-safe map to store the "current" http session for each thread
        m_httpSessions = Collections.synchronizedMap(new HashMap());

        // create our states storage
        initStates();

    }   // init

    /**
    * Returns to uninitialized state.
    *
    * You can use this method to release resources thet your Service
    * allocated when Turbine shuts down.
    */
    public void shutdown()
    {
        m_httpSessions.clear();
        m_httpSessions = null;
        shutdownStates();
        super.shutdown();

    }   // shutdown

    /*******************************************************************************
    * StateManagerService implementation
    *******************************************************************************/

    /**
    * Access the named attribute of the keyed state.
    * @param key The state key.
    * @param name The attribute name.
    * @return The named attribute value of the keyed state.
    */
    public Object getAttribute ( String key, String name )
    {
        Map state = getState(key);
        if (state == null) return null;
        return state.get(name);

    }   // getAttribute

    /**
    * Set the named state attribute of the keyed state with the provided object.
    * @param key The state key.
    * @param name The attribute name.
    * @param value The new value of the attribute (any object type).
    */
    public void setAttribute( String key, String name, Object value )
    {
        Map state = getState(key);
        if (state == null)
        {
            // create a synchronized map to store the state attributes
            state = Collections.synchronizedMap(new HashMap());
            addState(key, state);
        }

        // get the old, if any
        Object old = getAttribute(key, name);
        
        // store the new
        state.put(name, value);

        // if there was an old value, unbind it
        if (old != null)
        {
            unBindAttributeValue(key, name, old);
        }

        // bind the new
        bindAttributeValue(key, name, value);

    }   // setAttribute

    /**
    * Remove the named state attribute of the keyed state, if it exists.
    * @param key The state key.
    * @param name The attribute name.
    */
    public void removeAttribute( String key, String name )
    {
        Map state = getState(key);
        if (state == null) return;

        // get the old, if any
        Object old = getAttribute(key, name);

        // remove
        state.remove(name);
        
        // if the state is now empty, remove it
        if (state.isEmpty())
        {
            removeState(key);
        }

        // if there was an old value, unbind it
        if (old != null)
        {
            unBindAttributeValue(key, name, old);
        }

    }   // removeAttribute

    /**
    * Remove all state attribute of the keyed state.
    * @param key The state key.
    */
    public void clear( String key )
    {
        Map state = getState(key);
        if (state == null) return;

        // notify all attribute and clear the state
        retireAttributes(key, state);
        
        // and forget about it
        removeState(key);

    }   // clear

    /**
    * Access an array of all names of attributes stored in the keyed state.
    * @param key The state key.
    * @return An array of all names of attributes stored in the keyed state.
    */
    public String[] getAttributeNames( String key )
    {
        Map state = (Map) getState(key);
        if (state == null) return null;
        if (state.size() == 0) return null;

        // put the names into an array for return
        return (String[]) state.keySet().toArray(new String[state.size()]);

    }   // getAttributeNames

    /**
    * Access an SessionState object with the given key.
    * @param key The SessionState key.
    * @return an SessionState object with the given key.
    */
    public SessionState getSessionState( String key )
    {
        return new MySessionState(key, this);

    }   // getSessionState

    /**
    * Access the SessionState object associated with the current request's http session.
	* The session id is used as the key.
    * @return an SessionState object associated with the current request's http session.
    */
    public SessionState getCurrentSessionState()
    {
        HttpSession session = (HttpSession) m_httpSessions.get(Thread.currentThread());
        if (session == null) return null;

        return getSessionState(session.getId());

    }   // getCurrentSessionState

    /**
    * Access the SessionState object associated with the current request's http session with the given key.
	* @param key The string to add to the session id to form the SessionState key.
    * @return an SessionState object associated with the current request's http session with the given key.
    */
    public SessionState getCurrentSessionState( String key )
    {
        HttpSession session = (HttpSession) m_httpSessions.get(Thread.currentThread());
        if (session == null) return null;

        return getSessionState(session.getId() + key);

    }   // getCurrentSessionState

    /**
    * Retire, forget about and clean up all states that start with the given key.
    * @param keyStart The beginning of the key of the states to clean up.
    */
    public synchronized void retireState( String keyStart )
    {
        // get the current state keys into an array
        String keys[] = getStateKeys(keyStart);
        if (keys == null) return;
        
        // clear them
        for (int i = 0; i < keys.length; i++)
        {
            clear(keys[i]);
        }

    }   // retireState

    /**
    * Set the "current" context for this thread -
    * Call this at the start of each request, and call %%% at the end.
    * getCurrentSession() uses this for the session state key.
    * @param session the HttpSession of the current request.
    */
    public void setCurrentContext( HttpSession session )
    {
        // store the session associated with this thread
        m_httpSessions.put(Thread.currentThread(), session);

    }   // setCurrentContext

    /**
    * Clear the "current context for this thread -
    * Call at the end of each request, balanced with calls to setCurrentContext()
    */
    public void clearCurrentContext()
    {
        // clear the session associated with this thread
        m_httpSessions.remove(Thread.currentThread());

    }    // clearCurrentContext

    /*******************************************************************************
    * SessionState implementation
    *******************************************************************************/

    /**
    * A SessionState implementation, as covers to this service, storing the key.
    */
    private class MySessionState
        implements SessionState
    {
        /** The state key. */
        private String m_key = null;

        /** The StateManagerService object. */
        private BaseStateManagerService m_service = null;

        /**
        * Construct.
        * @param key The state key.
        * @param service The JetspeedStateManagerService instance.
        */
        public MySessionState( String key,
                                BaseStateManagerService service)
        {
            m_key = key;
            m_service = service;

        }   // MySessionState

        /**
        * Access the named attribute.
        * @param name The attribute name.
        * @return The named attribute value.
        */
        public Object getAttribute( String name )
        {
            return m_service.getAttribute(m_key, name);

        }   // getAttribute

        /**
        * Set the named attribute value to the provided object.
        * @param name The attribute name.
        * @param value The value of the attribute (any object type).
        */
        public void setAttribute( String name, Object value )
        {
            m_service.setAttribute(m_key, name, value);

        }   // setAttribute

        /**
        * Remove the named attribute, if it exists.
        * @param name The attribute name.
        */
        public void removeAttribute( String name )
        {
            m_service.removeAttribute(m_key, name);

        }   // removeAttribute

        /**
        * Remove all attributes.
        */
        public void clear()
        {
            m_service.clear(m_key);

        }   // clear

        /**
        * Access an array of all names of attributes stored in the SessionState.
        * @return An array of all names of attribute stored in the SessionState.
        */
        public String[] getAttributeNames()
        {
            return m_service.getAttributeNames(m_key);

        }   // getAttributeNames

        /**
        * Access the full unique StateManager key for the SessionState.
        * @return the full unique StateManager key for the SessionState.
        */
        public String getKey()
        {
            return m_key;

        }   // getKey

        /**
        * Retire, forget about and clean up this state.
        */
        public void retire()
        {
            m_service.retireState(m_key);

        }   // retire

    }   // class MySessionState

}   // BaseStateManagerService

/**********************************************************************************
*
* $Header: /home/cvspublic/jakarta-jetspeed/src/java/org/apache/jetspeed/services/statemanager/BaseStateManagerService.java,v 1.5 2004/02/23 03:38:28 jford Exp $
*
**********************************************************************************/


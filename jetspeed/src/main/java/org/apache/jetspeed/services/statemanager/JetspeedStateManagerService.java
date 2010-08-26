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
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import java.util.Set;

import org.apache.jetspeed.services.statemanager.BaseStateManagerService;

/**
* <p>JetspeedStateManagerService is an implementation of the BaseStateManagerService
* which manages the states stored in a local Map (synchronized HashMap).</p>
* <p>Note: This implementation of the StateManagerService is independent of all other
* services; but it has no automatic way to retire no longer used state.  If the
* application does not explicitly retire the states created, they will hang around
* forever. (see clear() and retireState() of the StateManagerService).</p>
* @version $Revision: 1.6 $
* @see org.apache.jetspeed.services.statemanager.BaseStateManagerService
* @see org.apache.jetspeed.services.statemanager.StateManagerService
* @see org.apache.jetspeed.services.statemanager.SessionState
* @author <a href="mailto:ggolden@apache.org">Glenn R. Golden</a>
*/
public class JetspeedStateManagerService
    extends BaseStateManagerService
{
    /** Store each set of state parameters by state key
        (each is a HashMap keyed by parameter name) */
    private Map m_states = null;

    /**
    * Initialize the states storage.
    */
    protected void initStates()
    {
        // create our states map synchronized
        m_states = Collections.synchronizedMap(new HashMap());

    }   // initStates

    /**
    * Cleanup the states storage.
    */
    protected void shutdownStates()
    {
        m_states.clear();
        m_states = null;

    }   // shutdownStates

    /**
    * Access the Map which is the set of attributes for a state.
    * @param key The state key.
    * @return The Map which is the set of attributes for a state.
    */
    protected Map getState( String key )
    {
        return (Map) m_states.get(key);

    }   // getState

    /**
    * Add a new state to the states we are managing.
    * @param key The state key.
    * @param state The Map which is the set of attributes for the state.
    */
    protected void addState( String key, Map state )
    {
        m_states.put(key, state);

    }   // addState

    /**
    * Remove a state from the states we are managing.
    * @param key The state key.
    */
    protected void removeState( String key )
    {
        m_states.remove(key);

    }   // removeState

    /**
    * Access an array of the keys of all states managed, those that start with the parameter.
    * @param start The starting string used to select the keys.
    * @return an array of the keys of all states managed.
    */
    protected String[] getStateKeys( String start )
    {
        // collect for return
        Vector rv = new Vector();

        // get the entire set of keys to iterate over
        Set allStateKeys = m_states.keySet();
        synchronized (m_states)
        {
            Iterator i = allStateKeys.iterator();
            while (i.hasNext())
            {
                String key = (String) i.next();
                
                // if this matches our pattern
                if (key.startsWith(start))
                {
                    rv.add(key);
                }
            }
        }

        if (rv.size() == 0) return null;

        return (String[]) rv.toArray(new String[rv.size()]);

    }   // getStateKeys

}   // JetspeedStateManagerService

/**********************************************************************************
*
* $Header: /home/cvspublic/jakarta-jetspeed/src/java/org/apache/jetspeed/services/statemanager/JetspeedStateManagerService.java,v 1.6 2004/02/23 03:38:28 jford Exp $
*
**********************************************************************************/


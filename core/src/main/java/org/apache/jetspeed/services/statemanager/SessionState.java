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

/**
* <p>SessionState is an interface for objects that provide name - value information sets
* with a unique key that can be used in the StateManager service</p>
* <p>See the proposal: jakarta-jetspeed/proposals/StateManager.txt for more details.</p>
* @version $Revision: 1.5 $
* @see org.apache.jetspeed.services.statemanager.StateManagerService
* @author <a href="mailto:ggolden@apache.org">Glenn R. Golden</a>
*/
public interface SessionState
{
    /**
    * Access the named attribute.
    * @param name The attribute name.
    * @return The named attribute value.
    */
    public Object getAttribute( String name );

    /**
    * Set the named attribute value to the provided object.
    * @param name The attribute name.
    * @param value The value of the attribute (any object type).
    */
    public void setAttribute( String name, Object value );

    /**
    * Remove the named attribute, if it exists.
    * @param name The attribute name.
    */
    public void removeAttribute( String name );

    /**
    * Remove all attributes.
    */
    public void clear();

    /**
    * Access an array of all names of attributes stored in the SessionState.
    * @return An array of all names of attribute stored in the SessionState.
    */
    public String[] getAttributeNames();

    /**
    * Access the unique StateManager key for the SessionState.
    * @return the unique StateManager key for the SessionState.
    */
    public String getKey();

    /**
    * Retire, forget about and clean up this state.
    */
    public void retire();

}   // interface SessionState

/**********************************************************************************
*
* $Header: /home/cvspublic/jakarta-jetspeed/src/java/org/apache/jetspeed/services/statemanager/SessionState.java,v 1.5 2004/02/23 03:38:28 jford Exp $
*
**********************************************************************************/


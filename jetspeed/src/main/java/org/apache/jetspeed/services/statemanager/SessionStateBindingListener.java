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


/**
* <p>SessionStateBindingListener is an interface for objects that wish to be
* notified when they are bound to and unbound from a SessionState managed by the
* Jetspeed SessionManagerService.</p>
* <p>This is loosely modeled on the HttpSessionBindingListener.</p>
* @version $Revision: 1.2 $
* @see org.apache.jetspeed.services.statemanager.StateManagerService
* @author <a href="mailto:ggolden@apache.org">Glenn R. Golden</a>
*/
public interface SessionStateBindingListener
{
    /**
    * Accept notification that this object has been bound as a SessionState attribute.
    * @param sessionStateKey The id of the session state which holds the attribute.
    * @param attributeName The id of the attribute to which this object is now the value.
    */
    public void valueBound(String sessionStateKey, String attributeName);

    /**
    * Accept notification that this object has been removed from a SessionState attribute.
    * @param sessionStateKey The id of the session state which held the attribute.
    * @param attributeName The id of the attribute to which this object was the value.
    */
    public void valueUnbound(String sessionStateKey, String attributeName);

}   // interface SessionStateBindingListener

/**********************************************************************************
*
* $Header: /home/cvspublic/jakarta-jetspeed/src/java/org/apache/jetspeed/services/statemanager/SessionStateBindingListener.java,v 1.2 2004/02/23 03:38:28 jford Exp $
*
**********************************************************************************/


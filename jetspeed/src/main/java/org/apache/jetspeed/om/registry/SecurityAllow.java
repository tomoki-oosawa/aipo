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

package org.apache.jetspeed.om.registry;

/**
 * Interface for manipulatin the security entries on the registry entries
 *
 * 
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a> 
 * @version $Id: SecurityAllow.java,v 1.5 2004/02/23 03:11:39 jford Exp $
 */
public interface SecurityAllow {

    /** Getter for property role.
     * @return Value of property role.
     */
    public String getRole();
    
    /** Setter for property role.
     * @param role New value of property role.
     */
    public void setRole(String role);

	/** Getter for property group.
	 * @return Value of property group.
	 */
	public String getGroup();
    
	/** Setter for property group.
	 * @param role New value of property group.
	 */
	public void setGroup(String group);
    
    /** Getter for property user.
     * @return Value of property user.
     */
    public String getUser();
    
    /** Setter for property user.
     * @param user New value of property user.
     */
    public void setUser(String user);
    
    /** Getter for property owner.
     * @return Value of property owner.
     */
    public boolean isOwner();
    
    /** Setter for property owner.
     * @param owner New value of property owner.
     */
    public void setOwner(boolean owner);
    
}

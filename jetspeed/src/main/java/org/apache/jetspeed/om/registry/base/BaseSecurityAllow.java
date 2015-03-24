/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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


package org.apache.jetspeed.om.registry.base;

// Jetspeed imports
import org.apache.jetspeed.om.registry.SecurityAllow;

/**
 * Interface for manipulatin the Security Allow on the registry entries
 * 
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id: BaseSecurityAllow.java,v 1.5 2004/02/23 03:08:26 jford Exp $
 */
public class BaseSecurityAllow implements SecurityAllow, java.io.Serializable
{
    /** Holds value of property role. */
    private String role;

	/** Holds value of property group. */
	private String group;
    
    /** Holds value of property user. */
    private String user;
    
    /** Holds value of property owner. */
    private boolean owner = false;
    
    /** Creates new BaseSecurityAllow */
    public BaseSecurityAllow()
    {
    }

    /**
     * Create a new BaseSecurityAllow that sets the owner property
     *
     * @param owner Set the owner property
     */
    public BaseSecurityAllow(boolean owner)
    {
        this.owner = owner;
    }
    
    /** Getter for property role.
     * @return Value of property role.
     */
    public String getRole()
    {
        return role;
    }
    
    /** Setter for property role.
     * @param role New value of property role.
     */
    public void setRole(String role)
    {
        this.role = role;
    }

	/** Getter for property group.
	 * @return Value of property group.
	 */
	public String getGroup()
	{
		return group;
	}
    
	/** Setter for property group.
	 * @param role New value of property group.
	 */
	public void setGroup(String group)
	{
		this.group = group;
	}
    
    /** Getter for property user.
     * @return Value of property user.
     */
    public String getUser()
    {
        return user;
    }
    
    /** Setter for property user.
     * @param user New value of property user.
     */
    public void setUser(String user)
    {
        this.user = user;
    }
    
    /** Getter for property owner.
     * @return Value of property owner.
     */
    public boolean isOwner()
    {
        return this.owner;
    }
    
    /** Setter for property owner.
     * @param owner New value of property owner.
     */
    public void setOwner(boolean owner)
    {
        this.owner = owner;
    }
    
}

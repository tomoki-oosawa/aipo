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

// Java imports
import java.util.Vector;
import java.util.Iterator;

// Jetspeed imports
import org.apache.jetspeed.om.registry.SecurityAccess;
import org.apache.jetspeed.om.registry.SecurityAllow;

/**
 * Interface for manipulatin the Security Access on the registry entries
 *
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id: BaseSecurityAccess.java,v 1.11 2004/02/23 03:08:26 jford Exp $
 */
public class BaseSecurityAccess implements SecurityAccess, java.io.Serializable
{

    /** Holds value of property action. */
    private String action;

    /** Holds value of property allows. */
    private Vector allows = new Vector();

    /** Holds value of property ownerAllows. */
    private Vector ownerAllows = new Vector();

    /** Holds the combination of allows and ownerAllows. */
    private transient Vector allAllows = new Vector();

    /** Creates new BaseSecurityAccess */
    public BaseSecurityAccess()
    {
    }

    /**
     * Implements the equals operation so that 2 elements are equal if
     * all their member values are equal.
     */
    public boolean equals(Object object)
    {
        if (object == null || !(object instanceof SecurityAccess))
        {
            return false;
        }

        SecurityAccess obj = (SecurityAccess) object;

        if (action != null)
        {
            if (!action.equals(obj.getAction()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getAction() != null)
            {
                return false;
            }
        }

        Iterator i = allows.iterator();
        Iterator i2 = obj.getAllows().iterator();
        while (i.hasNext())
        {
            SecurityAllow c1 = (SecurityAllow) i.next();
            SecurityAllow c2 = null;

            if (i2.hasNext())
            {
                c2 = (SecurityAllow) i2.next();
            }
            else
            {
                return false;
            }

            if (!c1.equals(c2))
            {
                return false;
            }
        }

        if (i2.hasNext())
        {
            return false;
        }

        i = ownerAllows.iterator();
        i2 = obj.getOwnerAllows().iterator();
        while (i.hasNext())
        {
            BaseSecurityAllowOwner c1 = (BaseSecurityAllowOwner) i.next();
            BaseSecurityAllowOwner c2 = null;

            if (i2.hasNext())
            {
                c2 = (BaseSecurityAllowOwner) i2.next();
            }
            else
            {
                return false;
            }

            if (!c1.equals(c2))
            {
                return false;
            }
        }

        if (i2.hasNext())
        {
            return false;
        }

        return true;
    }

    /** Getter for property action.
     * @return Value of property action.
     */
    public String getAction()
    {
        return action;
    }

    /** Setter for property action.
     * @param action New value of property action.
     */
    public void setAction(String action)
    {
        this.action = action;
    }

    /** Getter for property allows.
     * @return Value of property allows.
     */
    public Vector getAllows()
    {
        if (allows == null)
        {
            allows = new Vector();
        }
        return allows;
    }

    /** Setter for property allows.
     * @param allows New value of property allows.
     */
    public void setAllows(Vector allows)
    {
        this.allows = allows;
        if (this.allAllows != null)
        {
            allAllows.removeAllElements();
        }
    }

    /** Getter for property ownerAllows.
     * @return Value of property ownerAllows.
     */
    public Vector getOwnerAllows()
    {
        if (ownerAllows == null)
        {
            ownerAllows = new Vector();
        }
        return this.ownerAllows;
    }

    /** Setter for property ownerAllows.
     * @param ownerAllows New value of property ownerAllows.
     */
    public void setOwnerAllows(Vector ownerAllows)
    {
        this.ownerAllows = ownerAllows;
        if (this.allAllows != null)
        {
            allAllows.removeAllElements();
        }
    }

    /**
     * Return a vector contain all allows elements.  If the vector is null
     * or empty, then create and populate it with elements from the allows
     * and ownerAllows vectors.
     *
     * @return vector containing all allows
     */
    public Vector getAllAllows()
    {
        int elementCount = 0;
        if (this.allAllows == null)
        {
            allAllows = new Vector();
        }

        if (allAllows.isEmpty() == true)
        {
            if (this.allows != null)
            {
                elementCount += this.allows.size();
                allAllows.ensureCapacity(elementCount);
                allAllows.addAll(this.allows);
            }

            if (this.ownerAllows != null)
            {
                elementCount += this.ownerAllows.size();
                allAllows.ensureCapacity(elementCount);
                allAllows.addAll(this.ownerAllows);
            }
        }
        return this.allAllows;
    }
}

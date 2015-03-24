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

package org.apache.jetspeed.om;

// Jetspeed imports
import org.apache.jetspeed.om.SecurityReference;

/**
 * BaseSecurityReference
 *
 * @author <a href="paulsp@apache.org">Paul Spencer</a>
 * @version $Id: BaseSecurityReference.java,v 1.4 2004/02/23 03:14:32 jford Exp $
 */
public class BaseSecurityReference implements SecurityReference
{
    
    /** Holds value of property parent. */
    private String parent;
    
    /** Creates new BaseSecurityReference */
    public BaseSecurityReference()
    {
    }
    
    /** Getter for property parent.
     * @return Value of property parent.
     */
    public String getParent()
    {
        return parent;
    }
    
    /** Setter for property parent.
     * @param parent New value of property parent.
     */
    public void setParent(String parent)
    {
        this.parent = parent;
    }
    
    /**
     * Create a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException
    {
        BaseSecurityReference cloned = new BaseSecurityReference();
        cloned.parent = this.parent;
        
        return cloned;

    }   // clone
}

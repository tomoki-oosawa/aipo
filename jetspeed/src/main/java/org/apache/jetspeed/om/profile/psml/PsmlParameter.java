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

package org.apache.jetspeed.om.profile.psml;

// Jetspeed imports
import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.om.profile.Parameter;

/**
 * Bean like implementation of the Parameter interface suitable for 
 * Castor serialization.
 * 
 * @see org.apache.jetspeed.om.registry.PsmlParameter
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: PsmlParameter.java,v 1.6 2004/02/23 03:02:54 jford Exp $
 */
public class PsmlParameter implements Parameter, java.io.Serializable
{

    private String name;
    private String value;

    /** Holds value of property securityRef. */
    private SecurityReference securityRef;
    
    public PsmlParameter()
    {}
    
    /** @return the name of the parameter */
    public String getName()
    {
        return this.name;
    }
                                
    /** Sets the parameter name
     * @param name the parmeter name
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /** @return the value of the parameter */
    public String getValue()
    {
        return this.value;
    }
                                
    /** 
     * @param value the parameter value
     */
    public void setValue( String value )
    {
        this.value = value;
    }

    /** Getter for property securityRef.
     * @return Value of property securityRef.
     */
    public SecurityReference getSecurityRef()
    {
        return securityRef;
    }
    
    /** Setter for property securityRef.
     * @param securityRef New value of property securityRef.
     */
    public void setSecurityRef(SecurityReference securityRef)
    {
        this.securityRef = securityRef;
    }
    
    /**
     * Create a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException
    {
        Object cloned = super.clone();
        
        // clone the security ref
        ((PsmlParameter)cloned).securityRef = ((this.securityRef == null) ? null : (SecurityReference) this.securityRef.clone());
        
        return cloned;

    }   // clone

}
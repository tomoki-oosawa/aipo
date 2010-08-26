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

package org.apache.jetspeed.om.profile;

// Jetspeed imports
import org.apache.jetspeed.om.SecurityReference;

/**
 * Interface for parameters in psml
 *
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: Parameter.java,v 1.5 2004/02/23 03:05:01 jford Exp $
 */
public interface Parameter extends Cloneable
{
    /** @return name the name of the parameter */
    public String getName();

    /** Sets the name of this parameter
     * @param name the parameter name
     */
    public void setName( String name );

    /** @return the value of the parameter */
    public String getValue();

    /** Sets the value of the param
     * @param value the value of the param
     */
    public void setValue( String value );

    /** Getter for property securityRef.
     * @return Value of property securityRef.
     */
    public SecurityReference getSecurityRef();
    
    /** Setter for property securityRef.
     * @param securityRef New value of property securityRef.
     */
    public void setSecurityRef(SecurityReference securityRef);

    /**
     * Create a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException;
}
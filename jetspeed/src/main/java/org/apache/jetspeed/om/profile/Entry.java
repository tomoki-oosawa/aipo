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
 * An Entry is a representation of a Portlet within a configuration
 * Document.
 * An Entry is always associated to a parent Registry entry as well as
 * some layout constraints and local parameters if required.
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: Entry.java,v 1.4 2004/02/23 03:05:01 jford Exp $
 */
public interface Entry extends IdentityElement
{

    /** @return the entry name from which this one is derived */
    public String getParent();
                                
    /**
     * Sets the ancestor for this Entry.
     * @param parent the new ancestor entry name. This name should
     * be defined in the Registry
     */
    public void setParent( String parent );
        
    /** Getter for property securityRef.
     * @return Value of property securityRef.
     */
    public SecurityReference getSecurityRef();
    
    /** Setter for property securityRef.
     * @param securityRef New value of property securityRef.
     */
    public void setSecurityRef(SecurityReference securityRef);
    
}
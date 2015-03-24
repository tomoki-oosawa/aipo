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
import org.apache.jetspeed.om.profile.*;

/**
 * Default bean like implementation of the Entry interface
 * suitable for serialization with Castor
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: PsmlEntry.java,v 1.6 2004/02/23 03:02:54 jford Exp $
 */
public class PsmlEntry extends PsmlIdentityElement implements Entry
{

    private String parent = null;

    /** Holds value of property securityRef. */
    private SecurityReference securityRef;    
    
    /** @return the entry name from which this one is derived */
    public String getParent()
    {
        return this.parent;
    }
                                
    /**
     * Sets the ancestor for this Entry.
     * @param parent the new ancestor entry name. This name should
     * be defined in the system registry
     */
    public void setParent( String parent )
    {
        this.parent = parent;
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
    
    /** This method recreates the paramter name index for quick retrieval
     *  of parameters by name. Shoule be called whenever a complete index
     *  of parameter should be rebuilt (eg removing a parameter or setting 
     *  a parameters vector)
    private void buildNameIndex()
    {
        Hashtable idx = new Hashtable();
        
        Iterator i = parameter.iterator();
        int count = 0;
        while( i.hasNext() )
        {
            Parameter p = (Parameter)i.next();
            idx.put( p.getName(), new Integer(count) );
            count++;
        }
        
        this.nameIdx = idx;
    }            
     */

    /**
     * Create a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException
    {
        Object cloned = super.clone();

        // clone the securityRef
        ((PsmlEntry)cloned).securityRef = ((this.securityRef == null) ? null : (SecurityReference) this.securityRef.clone());

        return cloned;

    }   // clone
}


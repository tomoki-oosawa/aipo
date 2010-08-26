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

// Java imports
import java.util.Iterator;

// Jetspeed imports
import org.apache.jetspeed.om.SecurityReference;

/**
 * Portlets is collection of portlet entries and other portlet sets
 * within a configuration Document.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: Portlets.java,v 1.7 2004/02/23 03:05:01 jford Exp $
 */
public interface Portlets extends IdentityElement
{
    public Controller getController();

    public void setController(Controller controller);        
    
    public void setSecurity(Security security);
 
    public Security getSecurity();

    public int getEntryCount();

    public int getPortletsCount();

    public int getReferenceCount();

    public Portlets getPortlets(int index)
        throws java.lang.IndexOutOfBoundsException;

    public Entry getEntry(int index)
        throws java.lang.IndexOutOfBoundsException;

    public Reference getReference(int index)
        throws java.lang.IndexOutOfBoundsException;

    public Entry removeEntry(int index);

    public Portlets removePortlets(int index);

    public Reference removeReference(int index);

    public Iterator getEntriesIterator();
  
    public Iterator getPortletsIterator();

    public Iterator getReferenceIterator();

    public void addEntry(Entry entry)
        throws java.lang.IndexOutOfBoundsException;

    public void addPortlets(Portlets portlets)
        throws java.lang.IndexOutOfBoundsException;

    public void addReference(Reference ref)
        throws java.lang.IndexOutOfBoundsException;

    public Entry[] getEntriesArray();

    public Portlets[] getPortletsArray();

    public Reference[] getReferenceArray();

    /** Getter for property securityRef.
     * @return Value of property securityRef.
     */
    public SecurityReference getSecurityRef();    
  
    /** Setter for property securityRef.
     * @param securityRef New value of property securityRef.
     */
    public void setSecurityRef(SecurityReference securityRef);

    
    /**
     * @return Portlets parent <code>Portlets</code> object for this Portlets collection
     * <code>null</code> if it is the root.
     */    
    public Portlets getParentPortlets();
    
    /**
     * @param Portlets Sets the parent <code>Portlets</code> collection for this Portlets collection
     * 
     */
    public void setParentPortlets(Portlets parent);
}
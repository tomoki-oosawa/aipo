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

import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Entry;

import java.io.Serializable;
/**
 * This interface represents a loaded PSML document in memory, providing
 * all facilities for finding and updating specific parts of the 
 * document.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: PSMLDocument.java,v 1.8 2004/02/23 03:05:01 jford Exp $
 */
public interface PSMLDocument extends Serializable, Cloneable
{
    /**
     * Return the name of this document
     */
    public String getName();
        
    /**
     * Sets a new name for this document
     * 
     * @param name the new document name
     */
    public void setName(String name);

    /**
     * Return the portlet set PSML description of this document
     *
     * @return a PSML object model hierarchy, or null if none is 
     * defined for this document
     */
    public Portlets getPortlets();

    /**
     * Sets a new PSML object model for this document
     * 
     * @param portlets the PSML object model
     */
    public void setPortlets(Portlets portlets);

    /** Returns the first entry in the current PSML resource corresponding 
     *  to the given portlet name
     * 
     *  @param name the portlet name to seek
     *  @return the found entry description or null
     */
    public Entry getEntry(String name);

    /** Returns the first entry in the current PSML resource corresponding 
     *  to the given entry id
     * 
     *  @param entryId the portlet's entry id to seek
     *  @return the found entry description or null
     */
    public Entry getEntryById(String entryId);

    /** Returns the first portlets element in the current PSML resource corresponding 
     *  to the given name
     * 
     *  @param name the portlets name to seek
     *  @return the found portlets description or null
     */
    public Portlets getPortlets(String name);

    /** Returns the first portlets element in the current PSML resource corresponding 
     *  to the given name
     * 
     *  @param portletId the portlet's entry id to seek
     *  @return the found portlets description or null
     */
    public Portlets getPortletsById(String portletId);

    /** Returns the first portlets element in the current PSML resource 
     *  found at the specified position. The position is computed using
     *  a left-most tree traversal algorithm of the existing portlets (thus
     *  not counting other entry objects)
     * 
     *  @param position the sought position
     *  @return the found portlets object or null if we did not find such an
     *  object
     */
    public Portlets getPortlets(int position);

    /** 
     * Removes the first entry in the current PSML resource corresponding 
     * to the given entry id
     * 
     * @param entryId the portlet's entry id to remove
     * @return true if the entry was removed
     */
    public boolean removeEntryById(String entryId);
    
    /**
     * Create a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException;

}


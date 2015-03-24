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

package org.apache.jetspeed.om.registry;

// Jetspeed imports
import org.apache.jetspeed.om.SecurityReference;

/**
 * RegistryEntry is the base interface that objects must implement in order
 * to be used with the Registry service.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @version $Id: RegistryEntry.java,v 1.11 2004/02/23 03:11:39 jford Exp $
 */
public interface RegistryEntry
{
    /**
     * @return the id of this entry. This value should be unique within its
     * registry class.
     */
    public long getId();

    /**
     * @return the name of this entry. This value should be unique within its
     * registry class.
     */
    public String getName();

    /**
     * Changes the name of this entry
     * @param name the new name for this entry
     */
    public void setName(String name);

    /**
     * @return the entry title in the default locale for this entry, if set
     */
    public String getTitle();

    /**
     * Sets the title of the portlet entry
     * @param title the new title for the entry
     */
    public void setTitle(String title);

    /**
     * @return the entry description in the default locale for this entry, if set
     */
    public String getDescription();

    /**
     * Sets the description for the portlet entry
     * @param description the new description for the entry
     */
    public void setDescription(String description);

    /**
     * @return the security properties for this entry
     */
    public Security getSecurity();

    /**
     * Set the security properties for this entry
     * @param security the new security properties
     */
    public void setSecurity(Security security);

    /**
     * @return the metainfo properties for this entry
     */
    public MetaInfo getMetaInfo();

    /**
     * Set the metainfo properties for this entry
     * @param metainfo the new metainfo properties
     */
    public void setMetaInfo(MetaInfo metainfo);


    /**
     * Test if this entry should be visible in a list of the registry contents
     * @return true if the entry should be hidden
     */
    public boolean isHidden();

    /** Modify the visibility status of this entry
     * @param hidden the new status. If true, the entry will not be displayed in
     * a registry list
     */
    public void setHidden(boolean hidden);

    /** Getter for property securityRef.
     * @return Value of property securityRef.
     */
    public SecurityReference getSecurityRef();

    /** Setter for property securityRef.
     * @param securityRef New value of property securityRef.
     */
    public void setSecurityRef(SecurityReference securityRef);

}

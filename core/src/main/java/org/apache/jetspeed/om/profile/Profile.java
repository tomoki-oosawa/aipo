/*
 * Copyright 2000-2004 The Apache Software Foundation.
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

import org.apache.jetspeed.portal.PortletSet;

/**
Represents a profile, the interface to a PSML resource.

@author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
@version $Id: Profile.java,v 1.11 2004/02/23 03:05:01 jford Exp $
*/

public interface Profile extends ProfileLocator
{
    /**
     *  Gets the root set of portlets for this profile object.
     *
     *  @deprecated Will be removed with refactoring of profiler service.
     *  @return The root portlet set for this profile.
     */
    public PortletSet getRootSet();

    /**
     * Gets the psml document attached to this profile
     *
     * @return The PSML document for this profile.
     */
    public PSMLDocument getDocument();

    /**
     * Sets the psml document attached to this profile
     *
     * @param The PSML document for this profile.
     */
    public void setDocument(PSMLDocument document);

    /**
     * stores the resource by merging and rewriting the psml file
     *
     * @throws ProfileException if an error occurs storing the profile
     */
    public void store() throws ProfileException;
    
    /**
     * initialize a profile from a locator
     * 
     * @param locator
     */
    public void init(ProfileLocator locator);
}

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

import org.apache.jetspeed.services.psmlmanager.PsmlManagerService;

/**
    Represents a Profile object specially for importing PSML from one service to another (ex. file -> db or db -> file)
    For getDocument, the provider service provides the PSML document.
    For saveDocument, the consumer service writes the PSML document.

    @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
    @version $Id: ImportProfile.java,v 1.4 2004/02/23 03:05:01 jford Exp $
*/

public class ImportProfile  extends BaseProfile implements Profile
{
    private final PsmlManagerService provider;
    private final PsmlManagerService consumer;

    public ImportProfile(PsmlManagerService provider, PsmlManagerService consumer)
    {
        super();
        this.provider = provider;
        this.consumer = consumer;
    }

    public ImportProfile(PsmlManagerService provider, PsmlManagerService consumer, ProfileLocator locator)
    {
        super(locator);
        this.provider = provider;
        this.consumer = consumer;
    }

   /** 
     * @see Object#clone
     * @return an instance copy of this object
     */    
    public Object clone() throws java.lang.CloneNotSupportedException
    {
        return super.clone();
    }

    /**
       Gets the root set of portlets for this profile object.

       @return The root portlet set for this profile.
     */
    public PSMLDocument getDocument()
    {
        synchronized (this)
        {
            if ((this.document == null) || (this.document.getPortlets() == null))
            {
                this.document = provider.getDocument(this);
            }
        }        
        return this.document;
    }

    /**
       stores the resource by merging and rewriting the psml file

       @throws ProfileException if an error occurs storing the profile
    */
    public void store() throws ProfileException
    {
        if (document != null)
        {
            consumer.store(this);
        }
    }

}
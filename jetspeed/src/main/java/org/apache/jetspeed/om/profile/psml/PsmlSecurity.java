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

import org.apache.jetspeed.om.profile.Security;

/**
 * Bean like implementation of the Security interface suitable for 
 * Castor serialization.
 * 
 * @see org.apache.jetspeed.om.registry.Security
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @version $Id: PsmlSecurity.java,v 1.5 2004/02/23 03:02:54 jford Exp $
 */
public class PsmlSecurity implements Security, java.io.Serializable
{

    private String id;

    public PsmlSecurity()
    {}

    /**
     * Gets the security entry unique id.
     * This value is guaranteed to be unique in the security provider namespace.
     *
     * @return the id of this entry. 
     */
    public String getId()
    {
        return id;
    }
    
    /**
     * Sets the security entry unique id.
     * This value is guaranteed to be unique in the security provider namespace.
     *
     * @param id the unique id of the security entry.
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Create a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException
    {
        return super.clone();

    }   // clone

}
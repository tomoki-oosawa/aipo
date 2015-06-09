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

package org.apache.jetspeed.services.security;

// Jetspeed imports
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.registry.RegistryEntry;

import java.io.Serializable;
/**
 * PortalResource
 *
 * @author <a href="paulsp@apache.org">Paul Spencer</a>
 * @version $Id: PortalResource.java,v 1.5 2004/02/23 03:58:11 jford Exp $
 */
public class PortalResource implements Serializable
{
    public static final int TYPE_PORTLET = 100;
    public static final int TYPE_ENTRY = 200;
    public static final int TYPE_ENTRY_PARAMETER = 201;
    public static final int TYPE_REGISTRY = 300;
    public static final int TYPE_REGISTRY_PARAMETER = 301;

    private int resourceType = 0;

    private Entry entry = null;
    private org.apache.jetspeed.om.profile.Parameter entryParameter = null;
    private Portlet portlet = null;
    private org.apache.jetspeed.om.registry.Parameter registryParameter = null;
    private RegistryEntry registryEntry = null;
    
    /** Holds value of property owner. */
    private String owner;    

    /**
     * Resource is a PSML entry, i.e. a Portlet Instance
     * @param entry PSML Entry, i.e Portlet Instance
     */
    public PortalResource(Entry entry)
    {
        resourceType = TYPE_ENTRY;
        this.entry = entry;
    }

    /**
     * Resource is a parameter associated with a PSML entry, i.e. a Portlet Instance
     * @param entry PSML Entry, i.e Portlet Instance
     * @param parameter Requesting parameter
     */
    public PortalResource(Entry entry, org.apache.jetspeed.om.profile.Parameter parameter)
    {
        resourceType = TYPE_ENTRY_PARAMETER;
        this.entry = entry;
        this.entryParameter = parameter;
    }

    /**
     * Resource is a Portlet, i.e. a Portlet
     * @param portlet Portlet registry entry
     */
    public PortalResource(Portlet portlet)
    {
        resourceType = TYPE_PORTLET;
        this.portlet = portlet;
    }

    /**
     * Resource is a registry entry
     *
     * @param registryEntry of portal resource
     */
    public PortalResource(RegistryEntry registryEntry)
    {
        resourceType = TYPE_REGISTRY;
        this.registryEntry = registryEntry;
    }

    /**
     * Resource is a parameter associated with a registry entry
     * @param registryEntry of portal resource
     * @param parameter Requesting parameter
     */
    public PortalResource(RegistryEntry registryEntry, org.apache.jetspeed.om.registry.Parameter parameter)
    {
        resourceType = TYPE_REGISTRY_PARAMETER;
        this.registryEntry = registryEntry;
        this.registryParameter = parameter;
    }

    /**
     * Getter for property resourceType.
     * @return Value of property resourceType.
     */
    public int getResourceType()
    {
        return resourceType;
    }

    /**
     * Getter for property entry.
     * @return Value of property entry.
     */
    public Entry getEntry()
    {
        return entry;
    }

    /**
     * Getter for property parameter
     * @return Value of property parameter.
     */
    public org.apache.jetspeed.om.profile.Parameter getEntryParameter()
    {
        return entryParameter;
    }

    /**
     * Getter for property portlet.
     * @return Value of property portlet.
     */
    public Portlet getPortlet()
    {
        return portlet;
    }

    /**
     * Getter for property registryEntry.
     * @return Value of property registryEntry.
     */
    public RegistryEntry getRegistryEntry()
    {
        return registryEntry;
    }

    /**
     * Getter for property registryParameter.
     * @return Value of property registryParameter.
     */
    public org.apache.jetspeed.om.registry.Parameter getRegistryParameter()
    {
        return registryParameter;
    }

    /** Getter for property owner.
     * @return Value of property owner.
     */
    public String getOwner()
    {
        return this.owner;
    }
    
    /** Setter for property owner.
     * @param owner New value of property owner.
     */
    public void setOwner(String owner)
    {
        this.owner = owner;
    }
    
}

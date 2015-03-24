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

package org.apache.jetspeed.services.registry;

import org.apache.jetspeed.om.registry.Registry;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.om.registry.RegistryException;
import org.apache.turbine.services.Service;
import java.util.Enumeration;

/**
 * <P>This service is a facade for all registry related operations</P>
 *
 * @see org.apache.jetspeed.om.registry.Registry
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: RegistryService.java,v 1.4 2004/02/23 03:31:50 jford Exp $
 */
public interface RegistryService extends Service
{

    /** The name of this service */
    public String SERVICE_NAME = "Registry";

    /**
     * Returns a Registry object for further manipulation
     *
     * @param regName the name of the registry to fetch
     * @return a Registry object if found by the manager or null
     */
    public Registry get( String regName );

    /**
     * Creates a new RegistryEntry instance compatible with the current
     * Registry instance implementation
     *
     * @param regName the name of the registry to use
     * @return the newly created RegistryEntry
     */
    public RegistryEntry createEntry( String regName );

    /**
     * Returns a RegistryEntry from the named Registry.
     * This is a convenience wrapper around {@link
     * org.apache.jetspeed.om.registry.Registry#getEntry }
     *
     * @param regName the name of the registry
     * @param entryName the name of the entry to retrieve from the
     *                  registry
     * @return a RegistryEntry object if the key is found or null
     */
    public RegistryEntry getEntry( String regName, String entryName );

    /**
     * Add a new RegistryEntry in the named Registry.
     * This is a convenience wrapper around {@link
     * org.apache.jetspeed.om.registry.Registry#addEntry }
     *
     * @param regName the name of the registry
     * @param entry the Registry entry to add
     * @exception Sends a RegistryException if the manager can't add
     *            the provided entry
     */
    public void addEntry( String regName, RegistryEntry entry )
        throws RegistryException;

    /**
     * Deletes a RegistryEntry from the named Registry
     * This is a convenience wrapper around {@link
     * org.apache.jetspeed.om.registry.Registry#removeEntry }
     *
     * @param regName the name of the registry
     * @param entryName the name of the entry to remove
     */
    public void removeEntry( String regName, String entryName );

    /**
     *  List all the registry currently available to this service
     *
     * @return an Enumeration of registry names.
     */
    public Enumeration getNames();

}

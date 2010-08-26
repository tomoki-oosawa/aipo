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

package org.apache.jetspeed.om.registry.base;

import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.om.registry.Registry;
import org.apache.jetspeed.om.registry.InvalidEntryException;

/**
 * Provides base functionality within a Database Registry.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: DatabaseRegistry.java,v 1.3 2004/02/23 03:08:26 jford Exp $
 */
public class DatabaseRegistry extends BaseRegistry implements Registry
{

    /**
    @see Registry#setEntry
    */
    public void setEntry( RegistryEntry entry ) throws InvalidEntryException
    {
        // TODO: save it to the database here

        super.setEntry(entry);

        // TODO: notify other servers via jcs
    }

    /**
    @see Registry#addEntry
    */
    public void addEntry( RegistryEntry entry ) throws InvalidEntryException
    {
        // TODO: add it to the database here

        super.addEntry(entry);

        // TODO: notify other servers via jcs
    }

    /**
    @see Registry#removeEntry
    */
    public void removeEntry( String name )
    {
        // TODO: add it to the database here

        super.removeEntry(name);

        // TODO: notify other servers via jcs
    }

    /**
    @see Registry#removeEntry
    */

    public void removeEntry( RegistryEntry entry )
    {
        // TODO: add it to the database here

        super.removeEntry(entry);

        // TODO: notify other servers via jcs
    }

}
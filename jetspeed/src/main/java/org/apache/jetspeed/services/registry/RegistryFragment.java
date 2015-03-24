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

import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.om.registry.RegistryEntry;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Bean like implementation of a multi-object registry usable
 * by Castor XML serialization
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: RegistryFragment.java,v 1.10 2004/02/23 03:31:50 jford Exp $
 */
public class RegistryFragment extends Hashtable implements java.io.Serializable
{

    /** this flag is used to mark this fragment has some changes that are
     * not yet persisted to disk
     */
    private transient boolean dirty = false;

    /** this flag is used to mark that this fragment needs to updated to
     * incorporated changes from its disk state
     */
    private transient boolean changed = false;

    /** @return true if this fragment has some unpersisted changes
     */
    public boolean isDirty()
    {
        return this.dirty;
    }

    /** Sets the dirty flag indicating wether this fragment has some
     * uncommitted changes
     *
     * @param value the new dirty state for this fragment
     */
    public void setDirty(boolean value)
    {
        this.dirty = value;
    }

    /** @return true if this fragment has some persisted changes that need loading
     */
    public boolean hasChanged()
    {
        return this.changed;
    }

    /** Sets the changed flag indicating wether this fragment has some
     * changes to load
     *
     * @param value the new dirty state for this fragment
     */
    public void setChanged(boolean value)
    {
        this.changed = value;
    }

    /** @return the entries stored in this Fragment that are suitable
     *  for the requested registry
     *
     *  @param name a valid Registry name.
     */
    public Vector getEntries(String name)
    {

        if (name != null)
        {
            Vector registry = (Vector)get(name);

            if (registry != null)
            {
                return registry;
            }
        }

        return new Vector();
    }

    /** Add a new entry in the fragment. It does not check for name
     *  duplication
     *  @param name a valid Registry name.
     *  @param entry the entry to add
     */
    public void addEntry(String name, RegistryEntry entry)
    {
        if ( (name != null) && (entry != null) )
        {
            Vector registry = (Vector)get(name);

            if (registry != null)
            {
                registry.add(entry);
            }
        }
    }

    /** Remove an existing entry in the fragment.
     *  @param name a valid Registry name.
     *  @param entryName the name of the entry to remove
     */
    public void removeEntry(String name, String entryName)
    {
        if ( (name != null) && (entryName != null) )
        {
            Vector registry = (Vector)get(name);
            if (registry != null)
            {
                Iterator i = registry.iterator();
                while(i.hasNext())
                {
                    RegistryEntry regEntry = (RegistryEntry)i.next();
                    if (entryName.equals(regEntry.getName()))
                    {
                        i.remove();
                    }
                }
            }
        }
    }

    /** Modify an existing entry in the fragment.
     *  @param name a valid Registry name.
     *  @param entry the entry to add
     */
    public void setEntry(String name, RegistryEntry entry)
    {
        if (entry!=null)
        {
            removeEntry(name,entry.getName());
            addEntry(name,entry);
        }
    }

    // Castor serialization support methods

    public Vector getPortlets()
    {
        return (Vector)get(Registry.PORTLET);
    }

    public void setPortlets(Vector portlets)
    {
        if (portlets!=null)
        {
            put(Registry.PORTLET,portlets);
        }
    }

    public Vector getControls()
    {
        return (Vector)get(Registry.PORTLET_CONTROL);
    }

    public void setControls(Vector controls)
    {
        if (controls!=null)
        {
            put(Registry.PORTLET_CONTROL,controls);
        }
    }

    public Vector getControllers()
    {
        return (Vector)get(Registry.PORTLET_CONTROLLER);
    }

    public void setControllers(Vector controllers)
    {
        if (controllers!=null)
        {
            put(Registry.PORTLET_CONTROLLER,controllers);
        }
    }

    public Vector getMedias()
    {
        return (Vector)get(Registry.MEDIA_TYPE);
    }

    public void setMedias(Vector medias)
    {
        if (medias!=null)
        {
            put(Registry.MEDIA_TYPE,medias);
        }
    }

    public Vector getSkins()
    {
        return (Vector)get(Registry.SKIN);
    }

    public void setSkins(Vector skins)
    {
        if (skins!=null)
        {
            put(Registry.SKIN,skins);
        }
    }

    public Vector getSecurityEntries()
    {
        return (Vector)get(Registry.SECURITY);
    }

    public void setSecurityEntries(Vector securityEntries)
    {
        if (securityEntries!=null)
        {
            put(Registry.SECURITY, securityEntries);
        }
    }

    public Vector getClients()
    {
        return (Vector)get(Registry.CLIENT);
    }

    public void setClients(Vector clients)
    {
        if (clients!=null)
        {
            put(Registry.CLIENT, clients);
        }
    }
}

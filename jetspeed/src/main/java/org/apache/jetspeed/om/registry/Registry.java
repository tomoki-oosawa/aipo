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

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Represents all items within Jetspeed that hold configuration information.
 *
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @author <a href="raphael@apache.org">Rapha�l Luta</a>
 * @version $Id: Registry.java,v 1.7 2004/02/23 03:11:39 jford Exp $
 */
public interface Registry {

  /**
   * Get the number of entries within the Registry.
   *
   * @return the number of elements in this Registry instance
   */
  public int getEntryCount();

  /**
   * Creates a new RegistryEntry instance compatible with the current Registry
   * instance implementation
   *
   * @return the newly created RegistryEntry
   */
  public RegistryEntry createEntry();

  /**
   * Get the entry in the registry with the specified name
   *
   * @throws RegistryException
   *           if the given 'name' does not exist within the Registry
   */
  public RegistryEntry getEntry(String name) throws RegistryException;

  /**
   * Set the entry in the registry with the specified name and Entry
   *
   * @throws RegistryException
   *           if the given 'name' does not exist within the Registry
   */
  public void setEntry(RegistryEntry entry) throws RegistryException;

  /**
   * Add the given entry to the registry with the given name.
   *
   * @throws RegistryException
   *           if the given 'name' already exists within the Registry
   */
  public void addEntry(RegistryEntry entry) throws RegistryException;

  /**
   * Tests if an entry with the specified name exists within the Registry
   *
   * @param name
   *          the name of the entry that we are looking for
   * @return true if an entry with this name exists in the Registry
   */
  public boolean hasEntry(String name);

  /**
   * Removes the given entry from the Registry
   *
   * @param entry
   *          the RegistryEntry to remove
   */
  public void removeEntry(RegistryEntry entry);

  /**
   * Removes the given entry from the Registry
   *
   * @param name
   *          the name of the entry to remove from the Registry
   */
  public void removeEntry(String name);

  /**
   * Get all entries within this Registry
   *
   * @return an Enumeration of all unordered current entries
   */
  public Enumeration getEntries();

  /**
   * List all the entry names within this Registry
   *
   * @return an Iterator over an unordered list of current entry names
   */
  public Iterator listEntryNames();

  /**
   * Get all entries within this Registry as an array
   *
   * @return an unordered array of current registry entries
   */
  public RegistryEntry[] toArray();

}

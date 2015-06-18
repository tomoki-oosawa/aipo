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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.jetspeed.om.registry.InvalidEntryException;
import org.apache.jetspeed.om.registry.Registry;
import org.apache.jetspeed.om.registry.RegistryEntry;

/**
 * Provides base functionality within a Registry.
 * 
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 */
public class BaseRegistry implements LocalRegistry {
  protected static final boolean DEBUG = false;

  protected Map entries = new TreeMap();

  /** @see Registry#getEntryCount */
  public int getEntryCount() {
    return this.entries.size();
  }

  /** @see Registry#getEntry */
  public RegistryEntry getEntry(String name) throws InvalidEntryException {

    RegistryEntry entry = null;

    if (name != null) {
      entry = (RegistryEntry) this.entries.get(name);
    }

    if (entry == null) {
      throw new InvalidEntryException(
          InvalidEntryException.ENTRY_DOES_NOT_EXIST + " " + name);
    }

    return entry;
  }

  /**
   * @see Registry#setEntry
   */
  public void setEntry(RegistryEntry entry) throws InvalidEntryException {
    synchronized (this) {

      if (this.hasEntry(entry.getName()) == false) {
        throw new InvalidEntryException(
            InvalidEntryException.ENTRY_DOES_NOT_EXIST + " " + entry.getName());
      }

      this.entries.put(entry.getName(), entry);
    }
  }

  /**
   * @see Registry#addEntry
   */
  public void addEntry(RegistryEntry entry) throws InvalidEntryException {

    synchronized (this) {
      if (this.hasEntry(entry.getName())) {
        throw new InvalidEntryException(
            InvalidEntryException.ENTRY_ALREADY_PRESENT);
      }

      this.entries.put(entry.getName(), entry);
    }
  }

  /**
   * @see Registry#hasEntry
   */
  public boolean hasEntry(String name) {
    return this.entries.containsKey(name);
  }

  /**
   * @see Registry#removeEntry
   */
  public void removeEntry(String name) {
    synchronized (this) {
      this.entries.remove(name);
    }
  }

  /**
   * @see Registry#removeEntry
   */

  public void removeEntry(RegistryEntry entry) {
    synchronized (this) {
      this.entries.remove(entry.getName());
    }
  }

  /**
   * @see Registry#getEntries
   */
  public Enumeration getEntries() {
    Vector v = null;

    synchronized (this) {
      // this is ne
      v = new Vector(this.entries.values());
    }

    return v.elements();
  }

  /**
   * @see Registry#listEntryNames
   */
  public Iterator listEntryNames() {
    return entries.keySet().iterator();
  }

  /**
   * @see Registry#toArray
   */
  public RegistryEntry[] toArray() {

    Enumeration enu = getEntries();
    Vector v = new Vector();

    while (enu.hasMoreElements()) {
      v.addElement(enu.nextElement());
    }

    RegistryEntry[] entries = new RegistryEntry[v.size()];
    v.copyInto(entries);
    return entries;

  }

  /**
   * Creates a new RegistryEntry instance compatible with the current Registry
   * instance implementation
   * 
   * @return the newly created RegistryEntry
   */
  public RegistryEntry createEntry() {
    return new BaseRegistryEntry();
  }

  // RegistryService specific methods

  /**
   * This method is used to only set the entry in the local memory cache of the
   * registry without any coherency check with persistent storage
   * 
   * @param entry
   *            the RegistryEntry to store
   */
  public void setLocalEntry(RegistryEntry entry) throws InvalidEntryException {
    synchronized (this) {

      if (this.hasEntry(entry.getName()) == false) {
        throw new InvalidEntryException(
            InvalidEntryException.ENTRY_DOES_NOT_EXIST + " " + entry.getName());
      }

      this.entries.put(entry.getName(), entry);
    }
  }

  /**
   * This method is used to only add the entry in the local memory cache of the
   * registry without any coherency check with persistent storage
   * 
   * @param entry
   *            the RegistryEntry to store
   */
  public void addLocalEntry(RegistryEntry entry) throws InvalidEntryException {

    synchronized (this) {
      if (this.hasEntry(entry.getName())) {
        throw new InvalidEntryException(
            InvalidEntryException.ENTRY_ALREADY_PRESENT);
      }

      this.entries.put(entry.getName(), entry);
    }
  }

  /**
   * This method is used to only remove the entry from the local memory cache of
   * the registry without any coherency check with persistent storage
   * 
   * @param name
   *            the name of the RegistryEntry to remove
   */
  public void removeLocalEntry(String name) {
    synchronized (this) {
      this.entries.remove(name);
    }
  }

  /**
   * This method is used to only remove the entry from the local memory cache of
   * the registry without any coherency check with persistent storage
   * 
   * @param entry
   *            the RegistryEntry to remove
   */
  public void removeLocalEntry(RegistryEntry entry) {
    synchronized (this) {
      this.entries.remove(entry.getName());
    }
  }

}

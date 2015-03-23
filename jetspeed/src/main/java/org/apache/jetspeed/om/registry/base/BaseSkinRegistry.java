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

import org.apache.jetspeed.om.registry.InvalidEntryException;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.om.registry.RegistryException;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 * Extends BaseRegistry implementation to override object creation method and
 * ensure Registry object is synchronized with its persistence backend by
 * delegating actual addition/deletion of objects to the registry service.
 * <p>
 * To avoid loops, a RegistryService implementation using this class nees to
 * call the addLocalEntry/removeLocalEntry methods to modify the in memory state
 * of this Registry
 * </p>
 *
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @version $Id: BaseSkinRegistry.java,v 1.4 2004/02/23 03:08:26 jford Exp $
 */
public class BaseSkinRegistry extends BaseRegistry {

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(BaseSkinRegistry.class.getName());

  /**
   * @see Registry#setEntry
   */
  @Override
  public void setEntry(RegistryEntry entry) throws InvalidEntryException {
    // Delegate to the RegistryService to ensure correct handling of
    // persistence if using file fragments

    try {
      Registry.addEntry(Registry.SKIN, entry);
    } catch (RegistryException e) {
      logger.error("Exception", e);
    }
  }

  /**
   * @see Registry#addEntry
   */
  @Override
  public void addEntry(RegistryEntry entry) throws InvalidEntryException {
    // Delegate to the RegistryService to ensure correct handling of
    // persistence if using file fragments

    try {
      Registry.addEntry(Registry.SKIN, entry);
    } catch (RegistryException e) {
      logger.error("Exception", e);
    }
  }

  /**
   * @see Registry#removeEntry
   */
  @Override
  public void removeEntry(String name) {
    // Delegate to the RegistryService to ensure correct handling of
    // persistence if using file fragments

    Registry.removeEntry(Registry.SKIN, name);
  }

  /**
   * @see Registry#removeEntry
   */
  @Override
  public void removeEntry(RegistryEntry entry) {
    // Delegate to the RegistryService to ensure correct handling of
    // persistence if using file fragments

    if (entry != null) {
      Registry.removeEntry(Registry.SKIN, entry.getName());
    }
  }

  /**
   * Creates a new RegistryEntry instance compatible with the current Registry
   * instance implementation
   *
   * @return the newly created RegistryEntry
   */
  @Override
  public RegistryEntry createEntry() {
    return new BaseSkinEntry();
  }
}
